package imageUtil;

import magick.DrawInfo;
import magick.ImageInfo;
import magick.MagickImage;
import magick.MagickException;
import magick.PixelPacket;
import java.awt.Dimension;
import java.util.*;
import java.io.File;

public class Convolution {
   public static byte[] filterGray(byte[] grayimg, int width, int height, int[] kernel, int x, int y) {
	   byte[] result = new byte[width * height];
	   int score = 0;
	   int span = x / 2;
	   int idx = 0;
	   int idx2 = 0;
	   int count = 0;
	   for (int row = 0; row < height; row++) {
		   for (int col = 0; col < width; col++) {
			   score = 0;
			   count = 0;
			   for (int m = 0; m < y; m++) {
				   for (int n = 0; n < x; n++) {
					   idx = (row - span + m) * width + col - span + n;
					   if (row - span + m >= 0 && row - span + m < height
							   && col - span + n >= 0 && col - span + n < width) 
					   {
						   idx2 = m * x + n;
						   score += (0xFF & grayimg[idx]) * kernel[idx2];
						   count += (0xFF & kernel[idx2]);
					   }
				   }
			   }
			   result[row * width + col] = (byte)(score / count);
		   }
	   }
	   return result;
   }
   
   public static byte[] filterRGB(byte[] img, int width, int height, int[] filter, int x, int y) {
	   byte[] result = new byte[width * height * 3];
	   return result;
   }
   
   public static byte[] gradientMask(byte[] grayimg, int width, int height, int[] kernel, int x, int y, int t) {
	   int max = 0;
	   int idx = 0;
	   int score = 0;
	   int[] response = new int[width * height];
	   int span = x / 2;
	   for (int row = 0; row < height; row++) {
		   for (int col = 0; col < width; col++) {
			   score = 0;
			   for (int m = 0; m < y; m++) {
				   for (int n = 0; n < x; n++) {
					   idx = (row - span + m) * width + col - span + n;
					   if (row - span + m >= 0 && row - span + m < height
							   && col - span + n >= 0 && col - span + n < width) {
						   score += (0xFF & grayimg[idx]) * kernel[m * x + n];
					   }
				   }
			   }
			   if (score >= t) {
				   response[row * width + col] = score;
				   if (score > max)
					   max = score;
			   }
			   else {
				   response[row * width + col] = 0;
			   }
		   }
	   }
	   return normalize2Byte(response, width, height, max);
   }
   
   public static byte[] gradientMask(byte[] grayimg, int width, int height, int[] kernel1, int[] kernel2, int x, int y, int t) {
	   int max = 0;
	   int idx = 0;
	   int idx2 = 0;
	   int score = 0;
	   int score1 = 0;
	   int score2 = 0;
	   int[] response = new int[width * height];
	   int span = x / 2;
	   for (int row = 0; row < height; row++) {
		   for (int col = 0; col < width; col++) {
			   score = 0;
			   score1 = 0;
			   score2 = 0;
			   for (int m = 0; m < y; m++) {
				   for (int n = 0; n < x; n++) {
					   idx = (row - span + m) * width + col - span + n;
					   if (row - span + m >= 0 && row - span + m < height
							   && col - span + n >= 0 && col - span + n < width) {
						   idx2 = m * x + n;
						   score1 += (0xFF & grayimg[idx]) * kernel1[idx2];
						   score2 += (0xFF & grayimg[idx]) * kernel2[idx2];
					   }
				   }
			   }
			   score = (score1 < 0 ? score1 * (-1) : score1) +
					   (score2 < 0 ? score2 * (-1) : score2);
			   if (score >= t) {
				   response[row * width + col] = score;
				   if (score > max)
					   max = score;
			   }
			   else {
				   response[row * width + col] = 0;
			   }
		   }
	   }
	   return normalize2Byte(response, width, height, max);  
   }
   
   public static GradInfo[] getGradAtan(byte[] grayimg, int width, int height, int[] kernel1, int[] kernel2, int x, int y, int t) {
	   int max = 0;
	   int idx = 0;
	   int idx2 = 0;
	   int score = 0;
	   int score1 = 0;
	   int score2 = 0;
	   byte[] pixels = copyPixels(grayimg, width, height); // repeat pixels on the edge
	   GradInfo[] response = new GradInfo[width * height];
	   int span = x / 2;
	   for (int row = 1; row < height + 1; row++) {
		   for (int col = 1; col < width + 1; col++) {
			   score = 0;
			   score1 = 0;
			   score2 = 0;
			   for (int m = 0; m < y; m++) {
				   for (int n = 0; n < x; n++) {
					   idx = (row - span + m) * (width+2) + col - span + n;
					   idx2 = m * x + n;
					   score1 += (0xFF & pixels[idx]) * kernel1[idx2];
					   score2 += (0xFF & pixels[idx]) * kernel2[idx2];
				   }
			   }
			   score = Math.abs(score1) + Math.abs(score2);
			   if (score >= t) {
				   response[(row - 1) * width + col - 1] = new GradInfo(score, Math.atan2(score1, score2));
				   if (score > max)
					   max = score;
			   }
			   else {
				   response[(row - 1) * width + col - 1] = new GradInfo(0, 0);
			   }
		   }
	   }
	   System.out.println("max=" + max);
	   return response;  
   }
   
   public static byte[] normalize2Byte(int[] org, int width, int height, int max) {
	   byte[] result = new byte[width * height];
	   for (int i = 0; i < width * height; i++) {
		   result[i] = (byte)(255 * (org[i] / max));
	   }
	   return result;
   }
   
   public static int templateMatching(byte[] img, int width, int height, byte[] template, int x, int y) {
      double[] result = new double[width * height];
      double max = 0;
      int idx = 0;
      int idx2 = 0;
      int idx3 = 0;
      int maxIdx = 0;
      double score = 0;
      double denom1 = 0;
      double denom2 = 0;
      int threshold = 1;
      double tav = average(template, x, y);
      double fav = 0;
      for (int row = 0; row < height - y; row++) {
         for (int col = 0; col < width - x; col++) {
            score = 0;
            denom1 = 0;
            denom2 = 0;
            idx = row * width + col;
            fav = average(img, row, col, width, height, x, y);
            for (int m = 0; m < y; m++) {
            	for (int n = 0; n < x; n++) {
            		idx2 = (row + m) * width + col + n;
            		idx3 = m * x + n;
            		score = score + (img[idx2] - fav) * (template[idx3] - tav);
            		denom1 = denom1 + (img[idx2] - fav) * (img[idx2] - fav);
            		denom2 = denom2 + (template[idx3] - tav) * (template[idx3] - tav);
            	}
            }
            score = score / Math.sqrt(denom1 * denom2);
            result[idx] = score;
            if (score > max) {
            	max = score;
            	maxIdx = idx;
            }
         }
      }
      return maxIdx;
   }

   public static double average(byte[] mat, int width, int height) {
	   double sum = 0;
	   double count = 0;
	   int idx = 0;
	   for (int row = 0; row < height; row++) {
		   for (int col = 0; col < width; col++) {
			   idx = row * width + col;
			   sum += mat[idx];
			   count ++;
		   }
	   }
	   return sum / count;
   }
   
   public static double average(byte[] mat, int r, int c, int width, int height, int x, int y) {
	   double sum = 0;
	   double count = 0;
	   int idx = 0;
	   for (int row = r; row < r + y; row++) {
		   for (int col = c; col < c + x; col++) {
			   idx = row * width + col;
			   sum += mat[idx];
			   count ++;
		   }
	   }
	   return sum / count;
   }
   
   private static byte[] copyPixels(byte[] img, int width, int height) {
	   byte[] pixels = new byte[(width + 2) * (height + 2)];
	   for (int row = 0; row < height; row++) {
		   for (int col = 0; col < width; col++) {
			   pixels[(row + 1) * (width + 2) + col + 1] = img[row * width + col];
		   }
	   }
	   for (int row = 0; row < height + 2; row++) {
		   pixels[row * (width+2)] = pixels[row * (width+2) + 1];
		   pixels[row * (width+2) + width + 1] = pixels[row * (width+2) + width];
	   }
	   for (int col = 0; col < width + 2; col++) {
		   pixels[col] = pixels[(width + 2) + col];
		   pixels[(height+1) * (width + 2) + col] = pixels[height * (width + 2) + col];
	   }
	   return pixels;
   }
}

