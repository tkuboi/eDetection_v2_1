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
   private Points[][] accumulator; // row=rho, col=theta
   private byte[] pixels;
   private double unit_t;
   private double unit_r;
   private int size_t;
   private int size_r;
   private int width;
   private int height;
   private int max;
   private class Vector {
	   public int t; // theta
	   public int r; // rho
	   public Vector(int t, int r) {
		   this.t = t;
		   this.r = r;
	   }
   }
   private class Points {
	   public ArrayList<Integer> points;
	   public Points() {
		   this.points = new ArrayList<Integer>();
	   }
   }
   
   public HoughTransform(int unit_t, int unit_r, byte[] img, int width, int height) {
	   this.unit_t = unit_t;
	   this.unit_r = unit_r;
	   this.size_t = (int)((180 + 1) / unit_t);
	   this.size_r = (int)(Math.sqrt(width*width + height*height) / this.unit_r);
	   this.accumulator = new Points[2 * size_r][size_t];
	   this.pixels = img;
	   this.width = width;
	   this.height = height;
	   this.max = 0;
   }
   
   public byte[] transform(int numLines) {
	   for (int row = 0; row < height; row++) { // y
		   for (int col = 0; col < width; col++) { // x
			   if ((this.pixels[row * this.width + col] & 0xff) == 1)
			       accumulate(col, row);
		   }
	   }
	   return drawLines(selectCells(numLines));
   }
         
   private void accumulate(int x, int y) {
	   double theta = 0;
	   double rho = 0;
	   for (int t = 0; t < this.size_t; t++) {
		   theta = Math.PI * t * this.unit_t / 180;
		   rho = (x * Math.cos(theta) + y * Math.sin(theta)) / this.unit_r;
           if (rho >= -1 * this.size_r && rho < this.size_r) {
        	   //System.out.println("rho=" + (rho + this.size_r) + "t=" + t);
        	   if (this.accumulator[(int)(rho + this.size_r)][t] == null)
        		   this.accumulator[(int)(rho + this.size_r)][t] = new Points();
		       this.accumulator[(int)(rho + this.size_r)][t].points.add(this.width * y + x);
           }
	   }
   }
   
   private ArrayList<Vector> selectCells(int num) {
	   int max = 0;
	   int i = 0;
	   int[] order = new int[num];
	   for (int j=0; j < num; j++)
		   order[j] = 0;
	   for (int t = 0; t < size_t; t++) {
		   for (int r = 0; r < 2 * size_r; r++) {
			   i = 0;
			   while(i < num && (this.accumulator[r][t] == null || this.accumulator[r][t].points.size() <= order[i]))
				   i++;
			   if (i < num) {
				   for (int j = num - 1; j > i; j--) {
					   order[j] = order[j-1];
				   }
				   order[i] = accumulator[r][t].points.size();
			   }
		   }
	   }
	   int threshold = order[num-1];
	   System.out.println("threshold=" + threshold);
	   ArrayList<Vector> list = new ArrayList<Vector>();
	   for (int r = 0; r < 2 * this.size_r; r++) {
		   for (int t = 0; t < this.size_t; t++) {
			   if (this.accumulator[r][t] != null && max < this.accumulator[r][t].points.size())
				   max = this.accumulator[r][t].points.size();
			   if (this.accumulator[r][t] != null && this.accumulator[r][t].points.size() >= threshold)
				   list.add(new Vector(t, r));
		   }
	   }
	   System.out.println("HoughTransform max=" + max);
	   this.max = max;
	   return list;
   }
   
   private byte[] drawLines(ArrayList<Vector> list) {
	   byte[] result = copyPixels();
	   Object[] points;
	   int y = 0;
	   int x1, x2, xt;
	   for (Vector v : list) {
		   points = this.accumulator[v.r][v.t].points.toArray();
		   Arrays.sort(points);
		   x1 = (int) points[0];
		   x1 = x1 % this.width;
		   x2 = (int) points[points.length - 1];
		   x2 = x2 % this.width;
		   if (x1 > x2) {
			   xt = x2;
			   x2 = x1;
			   x1 = xt;
		   }
		   System.out.println("(" + x1 + " < " + x2 + ")");
		   double theta = Math.PI * (v.t * this.unit_t) / 180;
		   double a = -1 * Math.cos(theta) / Math.sin(theta), b = ((v.r- size_r) * this.unit_r)/Math.sin(theta);
		   for (int x = x1; x <= x2; x++) {
			   y = (int)(a*x + b);
			   if (y < this.height && 0 <= y)
				   result[y * this.width + x] = (byte)2;
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
   
}

