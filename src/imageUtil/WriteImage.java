package imageUtil;

import detection.BDetection;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import model.Color;

public class WriteImage {
	
	public static void write(String filename, byte[] pixels, int width, int height) {
		byte[] blob = fillRegions(pixels, width, height);
		try {
			MagickImage blobImage = new MagickImage();
			blobImage.constituteImage(width,
					height,
					"RGB",
					blob);
			blobImage.setFileName(filename);
			blobImage.writeImage(new ImageInfo());
		} catch (MagickException e) {
			e.printStackTrace();
		}
	}
	
	public static byte[] fillRegions(byte[] pixels, int width, int height) {
		byte[] blob = new byte[height * width * 3];
		int val = 0;
		for(int i = 0; i < height * width; i++) {
			val = pixels[i*3] & 0xff;
			if (val >= 1 && val < 255) {
				blob[i*3] = (byte)255;
				blob[i*3+1] = (byte)255;
				blob[i*3+2] = (byte)255;
				//System.out.println("val=" + val + " and bubble="+this.pixCounts.get(val-2).isBubble);
			}
			else {
				blob[i*3] = pixels[i*3];
				blob[i*3+1] = pixels[i*3+1];
				blob[i*3+2] = pixels[i*3+2];				
			}
		}
		return blob;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
