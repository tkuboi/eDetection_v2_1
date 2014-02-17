package test;

import magick.DrawInfo;
import magick.ImageInfo;
import magick.MagickImage;
import magick.MagickException;
import magick.PixelPacket;

import java.awt.Dimension;
import java.util.*;
import java.io.File;
import java.io.IOException;
import imageUtil.*;
   
public class Test {
	public static void main(String[] args) {
		try {
	         ImageInfo ii = new ImageInfo("/Users/toshihirokuboi/Workspace/search.jpg"); 
	         MagickImage image = new MagickImage(ii);
	         Dimension dimensions = image.getDimension();
	         byte[] pixels = new byte[dimensions.width * dimensions.height * 3];
	         image.dispatchImage(0, 0,
                     dimensions.width, dimensions.height,
                     "RGB",
                     pixels);
	         byte[] grayimg = Conversion.rgb2gray(pixels, dimensions.width, dimensions.height);
	         ImageInfo ii2 = new ImageInfo("/Users/toshihirokuboi/Workspace/hand.jpg");
	         MagickImage template = new MagickImage(ii2);
	         Dimension dimensions2 = template.getDimension();
	         byte[] temps = new byte[dimensions2.width * dimensions2.height * 3];
	         template.dispatchImage(0, 0,
                     dimensions2.width, dimensions2.height,
                     "RGB",
                     temps);
	         byte[] temp = Conversion.rgb2gray(temps, dimensions2.width, dimensions2.height);
	         int idx = Convolution.templateMatching(grayimg, dimensions.width, dimensions.height,
	        		 temp, dimensions2.width, dimensions2.height);
	         int y = idx / dimensions.width;
	         int x = idx % dimensions.width;
	         System.out.println("Width=" + dimensions.width + " ,Height=" + dimensions.height);
	         System.out.println("idx=" + idx + ", x=" + x + ", y=" + y);
	     
	         int idx2 = 0;

	         for (int row = y; row < y + dimensions2.height; row++) {
	        	 idx2 = row * dimensions.width + x;
	        	 pixels[idx2 * 3] = (byte)255;
	        	 pixels[idx2 * 3 + 1] = (byte)0;
	        	 pixels[idx2 * 3 + 2] = (byte)0;
	        	 idx2 = row * dimensions.width + (x + dimensions2.width - 1);
	        	 pixels[idx2 * 3] = (byte)255;
	        	 pixels[idx2 * 3 + 1] = (byte)0;
	        	 pixels[idx2 * 3 + 2] = (byte)0;
	         }
	         for (int col = x; col < x + dimensions2.width; col++) {
	        	 idx2 = y * dimensions.width + col;
	        	 pixels[idx2 * 3] = (byte)255;
	        	 pixels[idx2 * 3 + 1] = (byte)0;
	        	 pixels[idx2 * 3 + 2] = (byte)0;
	        	 idx2 = (y + dimensions2.height - 1) * dimensions.width + col;
	        	 pixels[idx2 * 3] = (byte)255;
	        	 pixels[idx2 * 3 + 1] = (byte)0;
	        	 pixels[idx2 * 3 + 2] = (byte)0;
	         }
	         MagickImage blobImage = new MagickImage();
	         blobImage.constituteImage(dimensions.width,
	                                   dimensions.height,
	                                   "RGB",
	                                   pixels);
	         blobImage.setFileName("result.jpg");
	         blobImage.writeImage(new ImageInfo());
	      } 
	      catch(MagickException e) {
	         e.printStackTrace();
	      }
	}
}

