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
   private double unit_t;
   private double unit_r;
   private int size_t;
   private int size_r;
   private int width;
   private int height;
   private int max;
   private class Vector {
	   public double t; // theta
	   public double r; // rho
	   public Vector(double t, double r) {
		   this.t = t;
		   this.r = r;
	   }
   }
   
   public HoughTransform(int x, int y, byte[] img, int width, int height) {
	   this.unit_t = Math.PI / (double)x;
	   this.unit_r = y;
	   this.size_t = x;
	   this.size_r = (int)Math.round(Math.sqrt(width*width + height*height) / this.unit_r);
	   //this.space = new int[size_t][size_p * 2];
	   this.space = new int[size_r * 2][size_t];
	   this.pixels = img;
	   this.width = width;
	   this.height = height;
	   this.max = 0;
   }
   
   public void findLines() {
	   initSpace();
	   for (int row = 0; row < height; row++) { // y
		   for (int col = 0; col < width; col++) { // x
			   if ((this.pixels[row * this.width + col] & 0xff) == 1)
			       populateSpace(col, row);
		   }
	   }
   }
   
   public byte[] pickAndDrwaLines(int threshold) {
	   ArrayList<Vector> list = selectCells(threshold);
	   return drawLines(list);
   }
   
   private void initSpace() {
	   for (int t = 0; t < size_t; t++) {
		   for (int r = 0; r < size_r; r++) {
			   this.space[r][t] = 0;
		   }
	   }
   }
   
   private void populateSpace(int x, int y) {
	   double _t = 0;
	   double rho;
	   int r = 0;
	   for (int t = 0; t < size_t; t++) {	
		   _t = t * this.unit_t;
	       rho = x * Math.cos(_t) + y * Math.sin(_t);
	       System.out.println("(" + x + "," + y + ") t=" + t + ", rho=" + rho);
	       r = (int)Math.round(rho / this.unit_r);
	       //p = (p >= 0 ? p : -1 * p);
	       if (r >= -1 * this.size_r && r < this.size_r) {
	           space[r + size_r][t] += 1;
	           System.out.println("r=" + r + ", +size_r=" + (r+size_r) + ", t=" + t);
	       }
	   }
   }
   
   private ArrayList<Vector> selectCells(int num) {
	   int max = 0;
	   int i = 0;
	   /*int[] order = new int[num];
	   for (int j=0; j < num; j++)
		   order[j] = 0;
	   for (int t = 0; t < size_t; t++) {
		   for (int r = 0; r < 2 * size_r; r++) {
			   i = 0;
			   while(i < num && this.space[r][t] <= order[i])
				   i++;
			   if (i < num) {
				   for (int j = num - 1; j > i; j--) {
					   order[j] = order[j-1];
				   }
				   order[i] = space[r][t];
			   }
		   }
	   }*/
	   //for (int j=0; j < num; j++)
	   //   System.out.println(order[j]);
	   int threshold = num;//order[num - 1];
	   System.out.println("threshold=" + threshold);
	   ArrayList<Vector> list = new ArrayList<Vector>();
	   for (int t = 0; t < size_t; t++) {
		   for (int r = 0; r < 2 * size_r; r++) {
			   if (max < this.space[r][t])
				   max = this.space[r][t];
			   if (this.space[r][t] >= threshold)
				   list.add(new Vector(t * this.unit_t, (r - size_r) * this.unit_r));
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
		   double a = -1 * Math.cos(v.t) / Math.sin(v.t), b = v.r/Math.sin(v.t);
		   double x0 = a*v.r, y0 = b*v.r;
		   /*int x1 = (int)Math.round(x0 + 100*(-b));
		   int y1 = (int)Math.round(y0 + 100*(a));
		   int x2 = (int)Math.round(x0 - 100*(-b));
		   int y2 = (int)Math.round(y0 - 100*(a));*/
		   for (int x = 0; x < this.width; x++) {
			   //y = r*x + c;
			   //y1 = r*x1 + c;
			   //y2 - r*x2 = y1 - r*x1;
			   //r = (y1 - y2) / (x1 - x2);
			   //c = y1 - x1 * (y1 - y2) / (x1 - x2);
			   //y = x * (y1 - y2) / (x1 - x2) + y1 - x1 * (y1 - y2) / (x1 - x2);
			   //y = (int)Math.round((v.p - x * Math.cos(v.theta)) / Math.sin(v.theta));
			   y = (int)(a*x + b);
			   if (x >= 0 && x < width && y < height && 0 <= y)
				   result[y * width + x] = (byte)2;
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
	   byte[] img = new byte[this.size_t * this.size_r * 2 * 3];
	   for (int row = 0; row < size_t; row++) {
		   for (int col = 0; col < size_r * 2; col++) {
			   idx = row * size_r * 2 + col;
			   img[idx*3] = (byte)(255 * this.space[col][row] / this.max);
			   img[idx*3 + 1] = (byte)img[idx*3];
			   img[idx*3 + 2] = (byte)img[idx*3];
			   //System.out.println(img[idx*3]);
		   }
	   }
	   return img;
   }
}

