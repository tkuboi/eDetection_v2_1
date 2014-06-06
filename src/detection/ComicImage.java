package detection;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import detection.BDetection.Callable1;
import detection.BDetection.Callable2;
import magick.DrawInfo;
import magick.ImageInfo;
import magick.MagickImage;
import magick.MagickException;
import magick.PixelPacket;

public class ComicImage {
	private MagickImage image;
    private byte[] pixels;
    private byte[] blackPixels;
    private List<RegionInfo> pixCounts;
    private List<RegionInfo> blackRegions;
    private List<RegionGroup> pixGroup;
    private int width;
    private int height;
    private String filename;
    
    public ComicImage(String name, MagickImage image, Dimension dimensions) {
    	this.filename = name;
    	this.image = image;
    	this.width = dimensions.width;
    	this.height = dimensions.height;
    	this.pixels = new byte[dimensions.width * dimensions.height * 3];
    	this.pixCounts = new ArrayList<RegionInfo>();
    	this.blackRegions = new ArrayList<RegionInfo>();
    	this.pixGroup = new ArrayList<RegionGroup>();
    }
    
    public static ComicImage importImage(File file) {
    	ComicImage ci = null;
        try {
           ImageInfo ii = new ImageInfo(file.toString()); 
           MagickImage image = new MagickImage(ii);
           Dimension dimensions = image.getDimension();
           ci = new ComicImage(file.getName(), image, dimensions);
        } 
        catch(MagickException e) {
           e.printStackTrace();
        }
        return ci;
    }
    
    public String getFilename() {
 	   return this.filename;
    }
    
    public void createBWPix() {
       int threshold = BDetection.BW_THRESHOLD;
       try {
          this.image.dispatchImage(0, 0,
                           this.width, this.height,
                           "RGB",
                           this.pixels);         

          for (int row = 0; row < this.height; row++) {
             for (int col = 0; col < this.width; col++) {
                int index = (row * this.width + col) * 3;

                // The unsigned bytes need to be converted to ints.
                int value = ((0xFF & this.pixels[index + 0]) +
                            (0xFF & this.pixels[index + 1]) +
                            (0xFF & this.pixels[index + 2])) / 3;

                byte fill = 0;
                if (value > threshold) {
                   fill = (byte)1;
                }

                this.pixels[index + 0] = fill;
                this.pixels[index + 1] = fill;
                this.pixels[index + 2] = fill;
             }
             //System.out.println("in getBWPix row=" + row + " height=" + height + " width=" + width);
          }
       } catch (MagickException e) {
          e.printStackTrace();
       }
    }

    private boolean isLegal(int idx) {
        boolean result = false;
        if (0 <= idx && idx < this.width * this.height) {
           result = true;
        }
        return result;
     }

    public void createBWPixRankFilter() {
       int threshold = BDetection.BW_THRESHOLD;
       int value = 0;
       ArrayList<Integer> values = new ArrayList<Integer>();
       try {
          this.image.dispatchImage(0, 0,
                           this.width, this.height,
                           "RGB",
                           this.pixels);

          for (int row = 0; row < this.height; row++) {
             for (int col = 0; col < this.width; col++) {
                values.clear();
                int index = (row * this.width + col);
                int index2 = ((row - 1) * this.width + col);
                int index3 = ((row + 1) * this.width + col);
                int index4 = (row * this.width + col - 1);
                int index5 = (row * this.width + col + 1);
                int index6 = ((row - 1) * this.width + col - 1);
                int index7 = ((row - 1) * this.width + col + 1);
                int index8 = ((row + 1) * this.width + col - 1);
                int index9 = ((row + 1) * this.width + col + 1);

                // The unsigned bytes need to be converted to ints.
                values.add(((0xFF & this.pixels[index * 3 + 0]) +
                            (0xFF & this.pixels[index * 3 + 1]) +
                            (0xFF & this.pixels[index * 3 + 2])) / 3);

                if (isLegal(index2)) {
                   values.add(((0xFF & this.pixels[index2 * 3 + 0]) +
                            (0xFF & this.pixels[index2 * 3 + 1]) +
                            (0xFF & this.pixels[index2 * 3 + 2])) / 3);
                }

                if (isLegal(index3)) {
                   values.add(((0xFF & this.pixels[index3 * 3 + 0]) +
                            (0xFF & this.pixels[index3 * 3 + 1]) +
                            (0xFF & this.pixels[index3 * 3 + 2])) / 3);
                }

                if (isLegal(index4)) {
                   values.add(((0xFF & this.pixels[index4 * 3 + 0]) +
                            (0xFF & this.pixels[index4 * 3 + 1]) +
                            (0xFF & this.pixels[index4 * 3 + 2])) / 3);
                }

                if (isLegal(index5)) {
                   values.add(((0xFF & this.pixels[index5 * 3 + 0]) +
                            (0xFF & this.pixels[index5 * 3 + 1]) +
                            (0xFF & this.pixels[index5 * 3 + 2])) / 3);
                }

                if (isLegal(index6)) {
                   values.add(((0xFF & this.pixels[index6 * 3 + 0]) +
                            (0xFF & this.pixels[index6 * 3 + 1]) +
                            (0xFF & this.pixels[index6 * 3 + 2])) / 3);
                }

                if (isLegal(index7)) {
                   values.add(((0xFF & this.pixels[index7 * 3 + 0]) +
                            (0xFF & this.pixels[index7 * 3 + 1]) +
                            (0xFF & this.pixels[index7 * 3 + 2])) / 3);
                }

                if (isLegal(index8)) {
                   values.add(((0xFF & this.pixels[index8 * 3 + 0]) +
                            (0xFF & this.pixels[index8 * 3 + 1]) +
                            (0xFF & this.pixels[index8 * 3 + 2])) / 3);
                }

                if (isLegal(index9)) {
                   values.add(((0xFF & this.pixels[index9 * 3 + 0]) +
                            (0xFF & this.pixels[index9 * 3 + 1]) +
                            (0xFF & this.pixels[index9 * 3 + 2])) / 3);
                }

                value = getMedian(values.toArray(new Integer[values.size()]));

                byte fill = 0;
                if (value > threshold) {
                   fill = (byte)1;
                }

                this.pixels[index * 3 + 0] = fill;
                this.pixels[index * 3 + 1] = fill;
                this.pixels[index * 3 + 2] = fill;
             }
             //System.out.println("in getBWPix row=" + row + " height=" + height + " width=" + width);
          }
       } catch (MagickException e) {
          e.printStackTrace();
       }
    }

    private int getMedian(Integer[] list) {
       int median = 0;
       Arrays.sort(list);
       median = list.length / 2;
       return list[median];
    }

    private void sort(Integer[] list) {
       int pivot = list.length / 2;
       int temp = 0;
       int left = 0;
       int right = list.length - 1;
       while(left < list.length && list[left] < list[pivot]) {
          left++;
       }
       while(right >= 0 && list[right] > list[pivot]) {
          right--;
       }
       if (right != left) {
          temp = list[right];
          list[right] = list[left];
          list[left] = temp;
       }
    }

    public void makeBlackPixels() {
       int idx = 0;
       this.blackPixels = new byte[this.width * this.height];
       for (int row = 0; row < this.height; row++) {
          for (int col = 0; col < this.width; col++) {
             idx = row * this.width + col;
             this.blackPixels[idx] = (this.pixels[idx * 3] == (byte)1 ? (byte)0 : (byte)1);
          }
       }      
    }

    public void rlsaSmoothing() {
       byte[] horz = connectHorizontal(BDetection.NUM_CON_H_NEIGHBOR);
       byte[] vert = connectVertical(BDetection.NUM_CON_V_NEIGHBOR);
       this.blackPixels = andOp(horz, vert);
    }

    private byte[] connectHorizontal(int c) {
       byte[] horz = new byte[this.width * this.height];
       for (int row = 0; row < this.height; row++) {
          for (int col = 0; col < this.width; col++) {
             int idx = row * this.width + col;
             if (this.pixels[idx * 3] == (byte)1 && adjacentHorzWhites(idx,c) < c) {
                horz[idx] = (byte)1; //set to black
             }
             else {
                horz[idx] = (this.pixels[idx * 3] == (byte)1 ? 0 : (byte)1);
             }
          }
       }
       return horz;
    }

    private byte[] connectVertical(int c) {
       byte[] vert = new byte[this.width * this.height];
       for (int col = 0; col < this.width; col++) {
          for (int row = 0; row < this.height; row++) {
             int idx = row * this.width + col;
             if (this.pixels[idx * 3] == (byte)1 && adjacentVertWhites(idx, c) < c)
                vert[idx] = (byte)1; //set to black
             else
                vert[idx] = (this.pixels[idx * 3] == (byte)1 ? 0 : (byte)1); //set to white
          }
       }
       return vert;
    }

    private int adjacentHorzWhites(int idx, int c) {
       int score = 0;
       int x = idx % this.width;
       int y = idx / this.width;
       for (int i = 1; i <= c && x + i < this.width; i++) {
          if (this.pixels[(y * this.width + x + i) * 3] == (byte)1)
 	    score++;
       }
       return score;
    }

    private int adjacentVertWhites(int idx, int c) {
       int score = 0;
       int x = idx % this.width;
       int y = idx / this.width;
       for (int i = 1; i <= c && y + i < this.height; i++) {
          if (this.pixels[((y + i) * this.width + x) * 3] == (byte)1)
 	    score++;
       }
       return score;
    }

    private byte[] andOp(byte[] h, byte[] v) {
       byte[] result = new byte[this.width * this.height];
       for (int i = 0; i < this.width * this.height; i++) {
          if (h[i] == (byte)1 && v[i] == (byte)1)
             result[i] = (byte)1;
          else
             result[i] = 0;
       }
       return result;
    }

    public byte[] getSubBlob(int orgX, int orgY, int x, int y) {
        System.out.println(" minX=" + orgX + " minY=" + orgY + " maxX=" + x + " maxY=" + y + " size=" + ((x - orgX + 1) * (y - orgY + 1) * 3));
        int width = x - orgX + 1;
        int height = y - orgY + 1;
        byte[] blob = new byte[width * height * 3];
        for (int row = 0; row < height; row++) {
           System.arraycopy(this.pixels, (orgX + (orgY + row) * this.width) * 3,
                               blob, row * width * 3, width * 3);
        }
        return blob;
     }

     public static byte[] copyBlob(int width, int height, byte[] blob) {
        byte[] copy = new byte[width * height * 3];
        for (int row = 0; row < height; row++) {
           System.arraycopy(blob, (row * width) * 3,
                               copy, row * width * 3, width * 3);
        }
        return copy;
     }

}
