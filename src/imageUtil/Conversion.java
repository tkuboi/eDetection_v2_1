package imageUtil;

import magick.DrawInfo;
import magick.ImageInfo;
import magick.MagickImage;
import magick.MagickException;
import magick.PixelPacket;
import java.awt.Dimension;
import java.util.*;
import java.io.File;

public class Conversion {
   // use luminosiry: 0.21 R + 0.71 G + 0.07 B
   public static byte[] rgb2gray(byte[] original, int width, int height) {
      byte[] result = new byte[width * height];
      int idx = 0;
      for (int row = 0; row < height; row++) {
         for (int col = 0; col < width; col++) {
            idx = row * width + col;
            result[idx] = (byte)(0.21 * (original[idx*3] & 0xff)
                          + 0.71 * (original[idx*3 + 1] & 0xff)
                          + 0.07 * (original[idx*3 + 2] & 0xff));
         }
      }
      return result;
   }
   
   public static void main(String[] args) {
      try {
         ImageInfo ii = new ImageInfo("./TrainingSet/images/001.jpg"); 
         MagickImage image = new MagickImage(ii);
         Dimension dimensions = image.getDimension();
         byte[] pixels = new byte[dimensions.width * dimensions.height * 3];
         image.dispatchImage(0, 0, dimensions.width, dimensions.height, "RGB", pixels);         
         byte[] grayscale = Conversion.rgb2gray(pixels, dimensions.width, dimensions.height);
         MagickImage blobImage = new MagickImage();
         blobImage.constituteImage(dimensions.width,
                                   dimensions.height,
                                   "I",
                                   grayscale);
         blobImage.setFileName("gray.jpg");
         blobImage.writeImage(new ImageInfo());
      } 
      catch(MagickException e) {
         e.printStackTrace();
      }
   }
}

