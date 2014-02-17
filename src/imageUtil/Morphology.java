package imageUtil;

import magick.DrawInfo;
import magick.ImageInfo;
import magick.MagickImage;
import magick.MagickException;
import magick.PixelPacket;
import java.awt.Dimension;
import java.util.*;
import java.io.File;

public class Morphology {
   public static byte[] dilation(byte[] original, int width, int height, int[][] element, int x, int y) {
      byte[] result = new byte[width * height];
      int idx = 0;
      int idx2 = 0;
      int score = 0;
      int threshold = 1;
      for (int row = 0; row < height; row++) {
         for (int col = 0; col < width; col++) {
            score = 0;
            idx = row * width + col;
            if (original[idx] == (byte)0) {
               for (int m = 0; m < y; m++) {
                  for (int n = 0; n < x; n++) {
                     idx2 = (row + m - y/2) * width + col + n - x/2;
                     if (element[n][m] == 1
                         && (row + m - y/2) >= 0
                         && (row + m - y/2) < height
                         && (col + n - x/2) >= 0
                         && (col + n - x/2) < width
                         && original[idx2] == (byte)1)
                        score++;
                  }
               }
               if (score >= threshold)
                  result[idx] = (byte)1;
               else
                  result[idx] = (byte)0;
            }
            else {
               result[idx] = (byte)1;
            }
         }
      }
      return result;
   }

   public static byte[] erosion(byte[] original, int width, int height, int[][] element, int x, int y) {
      byte[] result = new byte[width * height];
      int idx = 0;
      int idx2 = 0;
      int score = 0;
      int threshold = x * y;
      for (int row = 0; row < height; row++) {
         for (int col = 0; col < width; col++) {
            score = 0;
            idx = row * width + col;
            if (original[idx] == (byte)1) {
               for (int m = 0; m < y; m++) {
                  for (int n = 0; n < x; n++) {
                     idx2 = (row + m - y/2) * width + col + n - x/2;
                     if (element[n][m] == 1
                         && (row + m - y/2) >= 0
                         && (row + m - y/2) < height
                         && (col + n - x/2) >= 0
                         && (col + n - x/2) < width
                         && original[idx2] == (byte)1)
                        score++;
                  }
               }
               if (score < threshold)
                  result[idx] = (byte)0;
               else
                  result[idx] = (byte)1;
            }
            else {
               result[idx] = (byte)0;
            }
         }
      }
      return result;
   }

   public static byte[] opening(byte[] original, int width, int height, int[][] element, int x, int y) {
      byte[] result = erosion(original, width, height, element, x, y);
      return dilation(result, width, height, element, x, y);
   }

   public static byte[] closing(byte[] original, int width, int height, int[][] element, int x, int y) {
      byte[] result = dilation(original, width, height, element, x, y);
      return erosion(result, width, height, element, x, y);
   }

   public static byte[] hitAndMiss(byte[] original, int width, int height, int[][] element, int x, int y) {
      byte[] result = new byte[width * height];
      int idx = 0;
      int idx2 = 0;
      int score = 0;
      int threshold = 0;
      for (int m = 0; m < y; m++) {
         for (int n = 0; n < x; n++) {
            if (element[n][m] == 1 || element[n][m] == 0)
               threshold++;
         }
      }

      for (int row = 0; row < height; row++) {
         for (int col = 0; col < width; col++) {
            score = 0;
            idx = row * width + col;
            if (original[idx] == (byte)element[x/2][y/2]) {
               for (int m = 0; m < y; m++) {
                  for (int n = 0; n < x; n++) {
                     idx2 = (row + m - y/2) * width + col + n - x/2;
                     if ((row + m - y/2) >= 0
                         && (row + m - y/2) < height
                         && (col + n - x/2) >= 0
                         && (col + n - x/2) < width
                         && original[idx2] == (byte)element[n][m])
                        score++;
                  }
               }
               if (score == threshold)
                  result[idx] = (byte)1;
               else
                  result[idx] = (byte)0;
            }
            else {
               result[idx] = (byte)0;
            }
         }
      }
      return result;
   }

   public static byte[] thinning(byte[] original, int width, int height, int[][] element, int x, int y) {
      byte[] result = new byte[width * height];
      int idx = 0;
      int idx2 = 0;
      int score = 0;
      int threshold = 0;
      for (int m = 0; m < y; m++) {
         for (int n = 0; n < x; n++) {
            if (element[n][m] == 1 || element[n][m] == 0)
               threshold++;
         }
      }

      for (int row = 0; row < height; row++) {
         for (int col = 0; col < width; col++) {
            score = 0;
            idx = row * width + col;
            if (original[idx] == (byte)element[x/2][y/2]) {
               for (int m = 0; m < y; m++) {
                  for (int n = 0; n < x; n++) {
                     idx2 = (row + m - y/2) * width + col + n - x/2;
                     if ((row + m - y/2) >= 0
                         && (row + m - y/2) < height
                         && (col + n - x/2) >= 0
                         && (col + n - x/2) < width
                         && original[idx2] == (byte)element[n][m])
                        score++;
                  }
               }
               if (score == threshold)
                  result[idx] = (byte)0;
               else
                  result[idx] = original[idx];
            }
            else {
               result[idx] = original[idx];
            }
         }
      }
      return result;
   }

   public static byte[] thickning(byte[] original, int width, int height, int[][] element, int x, int y) {
      byte[] result = new byte[width * height];
      int idx = 0;
      int idx2 = 0;
      int score = 0;
      int threshold = 0;
      for (int m = 0; m < y; m++) {
         for (int n = 0; n < x; n++) {
            if (element[n][m] == 1 || element[n][m] == 0)
               threshold++;
         }
      }

      for (int row = 0; row < height; row++) {
         for (int col = 0; col < width; col++) {
            score = 0;
            idx = row * width + col;
            if (original[idx] == (byte)element[x/2][y/2]) {
               for (int m = 0; m < y; m++) {
                  for (int n = 0; n < x; n++) {
                     idx2 = (row + m - y/2) * width + col + n - x/2;
                     if ((row + m - y/2) >= 0
                         && (row + m - y/2) < height
                         && (col + n - x/2) >= 0
                         && (col + n - x/2) < width
                         && original[idx2] == (byte)element[n][m])
                        score++;
                  }
               }
               if (score == threshold)
                  result[idx] = (byte)1;
               else
                  result[idx] = original[idx];
            }
            else {
               result[idx] = original[idx];
            }
         }
      }
      return result;
   }

   public static byte[] skeletonization(byte[] original, int width, int height, int[][] element, int x, int y) {
      byte[] result = new byte[width * height];
      //todo
      return result;
   }
}

