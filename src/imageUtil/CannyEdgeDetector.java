package imageUtil;

import magick.DrawInfo;
import magick.ImageInfo;
import magick.MagickImage;
import magick.MagickException;
import magick.PixelPacket;
import java.awt.Dimension;
import java.util.*;
import java.io.File;

public class CannyEdgeDetector {
	
	public static byte[] genEdgeMap(byte[] grayimg, int width, int height, int t) {
		int[] filter = MaskFactory.getGaussianFilter3();
		byte[] result = Convolution.filterGray(grayimg, width, height, filter, 3, 3);
		GradInfo[] edgemap  = Convolution.getGradAtan(result, width, height,
				MaskFactory.getSobelFilter3x(), MaskFactory.getSobelFilter3y(),
				3, 3, t);
		return suppressNonMaxima(edgemap, width, height);
	}
	
	private static byte[] suppressNonMaxima(GradInfo[] edgemap, int width, int height) {
		byte[] result = new byte[width * height];
		int idx = 0;
		int x = -1;
		int y = -1;
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				idx = row * width + col;
				if (edgemap[idx].grad > 0) {
					result[idx] = (byte)1;
					x = -1;
					y = -1;
					if (edgemap[idx].atan >= 0 && edgemap[idx].atan < 20) { // top - bot
						//check 2 neighbors
						if (row - 1 >= 0)
							x = (row - 1) * width + (col);
						if (row + 1 < height)
						    y = (row + 1) * width + (col);
						
					}
					else if (edgemap[idx].atan >= 20 && edgemap[idx].atan < 70) { //bot L - top R
						//check 2 neighbors
						if (row + 1 < height && col - 1 >= 0)
						    x = (row + 1) * width + (col - 1);
						if (row - 1 >= 0 && col + 1 < width)
						    y = (row - 1) * width + (col + 1);
					}
					else if (edgemap[idx].atan >= 70 && edgemap[idx].atan < 110) { //L - R
						//check 2 neighbors
						if (col - 1 >= 0)
							x = (row) * width + (col - 1);
						if (col + 1 < width)
						    y = (row) * width + (col + 1);
					}
					else if (edgemap[idx].atan >= 110 && edgemap[idx].atan < 160) { //top L - bot R
						//check 2 neighbors
						if (row - 1 >= 0 && col - 1 >= 0)
						    x = (row - 1) * width + (col - 1);
						if (row + 1 < height && col + 1 < width)
						    y = (row + 1) * width + (col + 1);
					}
					else { //top - bot
						//check 2 neighbors
						if (row - 1 >= 0)
						    x = (row - 1) * width + (col);
						if (row + 1 < height)
						    y = (row + 1) * width + (col);
					}
					if (x >= 0 && y >= 0
							&& edgemap[idx].grad <= Math.max(edgemap[x].grad, edgemap[y].grad))
						result[idx] = (byte)0;
					else if (x >=0 && y < 0 && edgemap[idx].grad <= edgemap[x].grad)
						result[idx] = (byte)0;
					else if (x < 0 && y >= 0 && edgemap[idx].grad <= edgemap[y].grad)
						result[idx] = (byte)0;
				}
				else {
					result[idx] = (byte)0;
				}
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		try {
			int t = Integer.parseInt(args[1]);
			int hough_t = Integer.parseInt(args[2]);
			ImageInfo ii = new ImageInfo(args[0]); 
	        MagickImage image = new MagickImage(ii);
	        Dimension dimensions = image.getDimension();
	        int width = dimensions.width;
	        int height = dimensions.height;
	        byte[] pixels = new byte[width * height * 3];
	        image.dispatchImage(0, 0,
                    width, height,
                    "RGB",
                    pixels);
	        byte[] gray = Conversion.rgb2gray(pixels, width, height);
			byte[] edgemap = CannyEdgeDetector.genEdgeMap(gray, width, height, t);
			HoughTransform transform = new HoughTransform(180, 10, edgemap, width, height);
			transform.findLines();
			edgemap = transform.pickAndDrwaLines(hough_t);
			byte[] rgbimg = new byte[width*height*3];
			for (int i = 0; i < width*height; i++) {
				if ((0xFF & edgemap[i]) == 2) {
					rgbimg[i*3] = (byte)255;
					rgbimg[i*3 + 1] = (byte)0;
					rgbimg[i*3 + 2] = (byte)0;
				}
				else {
				    rgbimg[i*3] = (byte)((0xFF & edgemap[i]) * 255);
				    rgbimg[i*3 + 1] = (byte)((0xFF & edgemap[i]) * 255);
				    rgbimg[i*3 + 2] = (byte)((0xFF & edgemap[i]) * 255);
				}
				//System.out.println("i=" + i + " val=" + edgemap[i]);
			}
			MagickImage blobImage = new MagickImage();
			blobImage.constituteImage(width,
					height,
					"RGB",
					rgbimg);
			blobImage.setFileName("result.jpg");
			blobImage.writeImage(new ImageInfo());
			
			MagickImage spaceImage = new MagickImage();
			spaceImage.constituteImage(2*(width/10),
					180,
					"RGB",
					transform.getSpaceImg());
			spaceImage.setFileName("space.jpg");
			spaceImage.writeImage(new ImageInfo());
		}
		catch(MagickException e) {
	         e.printStackTrace();
		}
	}
}

