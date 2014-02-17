package imageUtil;

import magick.DrawInfo;
import magick.ImageInfo;
import magick.MagickImage;
import magick.MagickException;
import magick.PixelPacket;
import java.awt.Dimension;
import java.util.*;
import java.io.File;
import java.math.*;

public class HoughTransform {
   private int[][] space;
   private byte[] pixels;
   private double unit_theta;
   private double unit_p;
   private int size_t;
   private int size_p;
   private int width;
   private int height;
   private int max;
   private class Vector {
	   public double theta;
	   public double p;
	   public Vector(double t, double _p) {
		   theta = t;
		   p = _p;
	   }
   }
   
   public HoughTransform(int x, int y, byte[] img, int width, int height) {
	   this.unit_theta = Math.PI / x;
	   this.unit_p = y;
	   this.size_t = x;
	   this.size_p = (int)Math.round(Math.sqrt(width*width + height*height) / y);
	   this.space = new int[size_t][size_p * 2];
	   this.pixels = img;
	   this.width = width;
	   this.height = height;
	   this.max = 0;
   }
   
   public void findLines() {
	   initSpace();
	   for (int row = 0; row < height; row++) {
		   for (int col = 0; col < width; col++) {
			   if ((this.pixels[row * this.width + col] & 0xff) == 1)
			       populateSpace(row, col);
		   }
	   }
   }
   
   public byte[] pickAndDrwaLines(int threshold) {
	   ArrayList<Vector> list = selectCells(threshold);
	   return drawLines(list);
   }
   
   private void initSpace() {
	   for (int t = 0; t < size_t; t++) {
		   for (int p = 0; p < size_p; p++) {
			   this.space[t][p] = 0;
		   }
	   }
   }
   
   private void populateSpace(int x, int y) {
	   double _t = 0;
	   int p = 0;
	   for (int t = 0; t < size_t; t++) {	
		   _t = t * this.unit_theta;
	       p = (int)Math.round((x * Math.cos(_t) + y * Math.sin(_t)) / this.unit_p);
	       //p = (p >= 0 ? p : -1 * p);
	       if (p >= -1 * this.size_p && p < this.size_p)
	           space[t][p + size_p] += 1;
	   }
   }
   
   private ArrayList<Vector> selectCells(int num) {
	   int max = 0;
	   int i = 0;
	   int[] order = new int[num];
	   for (int j=0; j < num; j++)
		   order[j] = 0;
	   for (int t = 0; t < size_t; t++) {
		   for (int p = 0; p < 2 * size_p; p++) {
			   i = 0;
			   while(i < num && this.space[t][p] <= order[i])
				   i++;
			   if (i < num) {
				   for (int j = num - 1; j > i; j--) {
					   order[j] = order[j-1];
				   }
				   order[i] = space[t][p];
			   }
		   }
	   }
	   for (int j=0; j < num; j++)
		   System.out.println(order[j]);
	   int threshold = order[num - 1];
	   System.out.println("threshold=" + threshold);
	   ArrayList<Vector> list = new ArrayList<Vector>();
	   for (int t = 0; t < size_t; t++) {
		   for (int p = 0; p < 2 * size_p; p++) {
			   if (max < this.space[t][p])
				   max = this.space[t][p];
			   if (this.space[t][p] >= threshold)
				   list.add(new Vector(t * this.unit_theta, (p - size_p) * this.unit_p));
		   }
	   }
	   System.out.println("HoughTransform max=" + max);
	   this.max = max;
	   return list;
   }
   
   private byte[] drawLines(ArrayList<Vector> list) {
	   byte[] result = copyPixels();
	   int y = 0;
	   for (Vector v : list) {
		   double a = Math.cos(v.theta), b = Math.sin(v.theta);
		   double x0 = a*v.p, y0 = b*v.p;
		   int x1 = (int)Math.round(x0 + 100*(-b));
		   int y1 = (int)Math.round(y0 + 100*(a));
		   int x2 = (int)Math.round(x0 - 100*(-b));
		   int y2 = (int)Math.round(y0 - 100*(a));
		   for (int x = x1; x < x2; x++) {
			   //y = r*x + c;
			   //y1 = r*x1 + c;
			   //y2 - r*x2 = y1 - r*x1;
			   //r = (y1 - y2) / (x1 - x2);
			   //c = y1 - x1 * (y1 - y2) / (x1 - x2);
			   y = x * (y1 - y2) / (x1 - x2) + y1 - x1 * (y1 - y2) / (x1 - x2);
			   //y = (int)Math.round((v.p - x * Math.cos(v.theta)) / Math.sin(v.theta));
			   if (x >= 0 && x < height && y < width && 0 <= y)
				   result[x * width + y] = (byte)2;
		   }
	   }
	   return result;
   }
   
   private byte[] copyPixels() {
	   byte[] copy = new byte[this.width * this.height];
	   for (int i = 0; i < this.width * this.height; i++) {
		   copy[i] = this.pixels[i];
	   }
	   return copy;
   }
   
   public byte[] getSpaceImg() {
	   int idx = 0;
	   byte[] img = new byte[this.size_t * this.size_p * 2 * 3];
	   for (int row = 0; row < size_t; row++) {
		   for (int col = 0; col < size_p * 2; col++) {
			   idx = row * size_p * 2 + col;
			   img[idx*3] = (byte)(255 * this.space[row][col] / this.max);
			   img[idx*3 + 1] = (byte)img[idx*3];
			   img[idx*3 + 2] = (byte)img[idx*3];
			   //System.out.println(img[idx*3]);
		   }
	   }
	   return img;
   }
}

