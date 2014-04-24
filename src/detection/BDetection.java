package detection;

import magick.DrawInfo;
import magick.ImageInfo;
import magick.MagickImage;
import magick.MagickException;
import magick.PixelPacket;

import java.awt.Dimension;
import java.awt.Point;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import imageUtil.*;
import model.*;

public class BDetection {
   static final int NUM_SEEDS = 10;
   static final int BW_THRESHOLD = 155; //225
   static final int MAX_GAP = 5;
   static final int RADIUS = 3; //3
   static final float GAP_SCORE = 49.0f;
   static final float BP_SCORE = 40.0f;
   static final int BP_RADIUS = 3; //3
   static final int MAX_BLACKREGION_COUNT = 100;
   static final double MAX_AREA = 0.99;
   static final double MIN_AREA = 0.01; //0.05
   static final double MAX_EMPTINESS = 0.999999; //0.814
   static final double MIN_EMPTINESS = 0.25;//0.55
   static final double MAX_WIDTH = 0.75;
   static final double MAX_HEIGHT = 0.75;
   static final double SD_AREA = 5.0;
   static final double MAX_SUM_PIX = 33000.0;
   static final double MIN_SUM_PIX = 250.0; //330
   static final double MIN_AV_PIX = 69;//96
   static final double MIN_SD_PIX = 100.0;
   static final double MIN_SD_VERT = 10;
   static final double MIN_SD_HORIZ = 10;
   static final double MAX_SD_VERT = 200;
   static final double MAX_SD_HORIZ = 245;
   static final double MIN_BLKPIX_PERCENT = 0.05;
   static final double GROUP_TEXT_THRESHOLD = 30.0; //35
   static final int NUM_CON_H_NEIGHBOR = 5; //5
   static final int NUM_CON_V_NEIGHBOR = 30; //30
   static final int MIN_PIX_FOR_TEXT = 70;
   static final int MAX_PIX_FOR_TEXT = 7999;
   static final int MAX_X_RANGE_FOR_TEXT = 100;
   static final int MAX_XY_RATIO_FOR_TEXT = 10;
   static final double MIN_XY_RATIO_FOR_TEXT = 0.2;
   static final double MIN_PIX_AREA_RATIO_FOR_TEXT = 0.43;
   static final int MIN_PIX_FOR_TEXT2 = 3000;

   private byte[] pixels;
   private byte[] blackPixels;
   private MagickImage image;
   private final int width;
   private final int height;
   private String filename;
   private ArrayList<RegionInfo> pixCounts;
   private ArrayList<RegionInfo> blackRegions;
   private ArrayList<RegionGroup> textgroup;

   public interface Callable1<T> {
      public T call(int idx, int minX, int maxX, int minY, int maxY, byte[] blob);
   }

   public interface Callable2<T> {
      public T call(int idx, int val, ArrayList<Integer> queue, byte[] blob);
   }
   
   private BDetection(String name, MagickImage image, Dimension dimensions) {
	  this.filename = name;
      this.image = image;
      this.width = dimensions.width;
      this.height = dimensions.height;
      this.pixels = new byte[dimensions.width * dimensions.height * 3];
      //this.map = new byte[dimensions.width][dimensions.height];
      //this.seeds = new ArrayList<Integer>();
      this.pixCounts = new ArrayList<RegionInfo>();
      this.blackRegions = new ArrayList<RegionInfo>();
      this.textgroup = new ArrayList<RegionGroup>();
   }

   public static BDetection factory(File file) {
      BDetection bd = null;
      try {
         ImageInfo ii = new ImageInfo(file.toString()); 
         MagickImage image = new MagickImage(ii);
         //image = image.blurImage(1, 0.9);
         Dimension dimensions = image.getDimension();
         bd = new BDetection(file.getName(), image, dimensions);
      } 
      catch(MagickException e) {
         e.printStackTrace();
      }
      return bd;
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

   public void processBlackPixels() {
      int marker = 2; //mark pixel with number greater than 1
      final int width = this.width;
      final int height = this.height;
      int i = 0;
      ArrayList<Integer> seeds = new ArrayList<Integer>();
      RegionInfo rInfo = null;
      for (int row = 0; row < height; row++) {
         for (int col = 0; col < width; col++) {
            i = row * width + col;
            if (this.blackPixels[i] == (byte)1) { // black pixel
               seeds.add(i);
            }
         }
      }
      for (Integer seed : seeds) {
               int col = seed % width;
               int row = seed / width;
               //System.out.println("col=" + col + ", row=" + row + ", marker=" + marker);
               rInfo = seedGrowth(seed, marker, width, this.blackPixels,
                  new Callable1<Boolean>() {
                     public Boolean call(int idx, int minX, int maxX, int minY, int maxY, byte[] blob) {
                        return BDetection.isBlackPixel(idx, BDetection.BP_RADIUS, BDetection.BP_SCORE,
                                                          width, height, blob);
                     }
                  },
                  new Callable2<Integer>() {
                     public Integer call(int idx, int val, ArrayList<Integer> queue, byte[] blob) {
                        return BDetection.addQueueNoRGB(idx, val, queue, blob);
                     }
                  }
               );
               if (rInfo.pixCount > 0) {
                  this.blackRegions.add(rInfo);
                  marker++;
               }
      }
   }

   public void classifyBlackRegions() {
      double xyRatio = 0.0;
      double pixAreaRatio = 0.0;
      for (RegionInfo r : this.blackRegions) {
         if (r.yRange > 0)
            xyRatio = (double)r.xRange / (double)r.yRange;
         if (r.yRange > 0 && r.xRange > 0)
            pixAreaRatio = (double)r.pixCount / (double)(r.yRange * r.xRange);
         if (r.pixCount > MIN_PIX_FOR_TEXT && r.pixCount < MAX_PIX_FOR_TEXT && r.xRange < MAX_X_RANGE_FOR_TEXT && xyRatio < MAX_XY_RATIO_FOR_TEXT && xyRatio > MIN_XY_RATIO_FOR_TEXT
             && (pixAreaRatio > MIN_PIX_AREA_RATIO_FOR_TEXT || r.pixCount > MIN_PIX_FOR_TEXT2)) {
            r.type = 1;
         }
         else {
            r.type = 2;
         }
         String str = r.toString();
         str = str + ", xyRatio=" + xyRatio + ", pixAreaRatio=" + pixAreaRatio;
         System.out.println(str);
      }
   }

   public void groupTexts(double threshold) {
      //ArrayList<RegionGroup> textgroup = new ArrayList<RegionGroup>();
      int group = 0;
      double dist = 0;
      for (RegionInfo r1 : this.blackRegions) {
         if (r1.type == 1) {
            if (r1.textGroup == -1) {
               r1.textGroup = group;
               this.textgroup.add(new RegionGroup(group, r1));
               group++;
            }
       
            for (RegionInfo r2 : this.blackRegions) {
               if (r2.type == 1) {
                  if ((r1 != r2) && (distance(r1, r2) <= threshold)) {
                     join(r1, r2, this.textgroup);
                  }
               }
            }
         }
      }
      analyzeTextgroup(this.textgroup);
   }

   private void analyzeTextgroup(ArrayList<RegionGroup> textgroup) {
      int minX, minY, maxX, maxY;
      for (RegionGroup group : textgroup) {
         maxX = maxY = -1;
         minX = minY = 9999999;
         for (RegionInfo r : group.getMembers()) {
             minX = (r.minX < minX ? r.minX : minX);
             minY = (r.minY < minY ? r.minY : minY);
             maxX = (r.maxX > maxX ? r.maxX : maxX);
             maxY = (r.maxY > maxY ? r.maxY : maxY);
         }
         group.minX = minX;
         group.minY = minY;
         group.maxX = maxX;
         group.maxY = maxY;
         if (group.getMembers().size() > 0)
            group.setClosed(isClosed(minX, minY, maxX, maxY));
      }
   }

   private void join(RegionInfo r1, RegionInfo r2, ArrayList<RegionGroup> textgroup) {
      int oldgroup = 0;
      if (r2.textGroup == -1) {
         r2.textGroup = r1.textGroup;
         textgroup.get(r1.textGroup).addMember(r2);
      }
      else {
         if (r1.textGroup != r2.textGroup) {
         if (r1.textGroup <= r2.textGroup) {
            oldgroup = r2.textGroup;
            textgroup.get(r1.textGroup).addMembers(textgroup.get(oldgroup).getMembers());
            textgroup.get(oldgroup).clearMembers();
         }
         else {
            oldgroup = r1.textGroup;
            textgroup.get(r2.textGroup).addMembers(textgroup.get(oldgroup).getMembers());
            textgroup.get(oldgroup).clearMembers();
         }
         }
      }
   }

   private static double distance(RegionInfo r1, RegionInfo r2) {
      double dist = 10000.0;
      int maxX = (r1.maxX <= r2.maxX ? r1.maxX : r2.maxX);
      int minX = (r1.minX >= r2.minX ? r1.minX : r2.minX);
      int maxY = (r1.maxY <= r2.maxY ? r1.maxY : r2.maxY);
      int minY = (r1.minY >= r2.minY ? r1.minY : r2.minY);
      if (maxX - minX > 0) { // the two overlap horizontally
         if (r1.maxY < r2.minY) // r2 is above r1
            dist = r2.minY - r1.maxY;
         else                   // r2 is below r1
            dist = r1.minY - r2.maxY;
      }
      else if (maxY - minY > 0) { // the two overlap vertically
         if (r1.maxX < r2.minX) // r2 is right of r1
            dist = r2.minX - r1.maxX;
         else                   // r2 is left of r1
            dist = r1.minX - r2.maxX;
      }
      else { // no overlap at all
         dist = Math.sqrt(dist); // default distance is 100
      }

      return dist;
   }

   private boolean isClosed(int x1, int y1, int x2, int y2) {
      boolean closed = true;
      int x = (x1 + x2) / 2;
      int y = (y1 + y2) / 2;
      ArrayList<Seed> seeds = new ArrayList<Seed>();
      int i = 0;
      int idx = y * this.width + x;
      while(this.pixels[idx*3] != (byte)1 && i++ < 200) {
         x = BDetection.randomWithRange(x1, x2);
         y = BDetection.randomWithRange(y1, y2);
         idx = y * this.width + x;
      }
      Seed seed = new Seed(idx, x1, y1, x2, y2, true);
      RegionInfo r = seedGrowth(seed, BDetection.RADIUS, BDetection.GAP_SCORE);
      int area = (r.maxX - r.minX) * (r.maxY - r.minY);
      if ( r.maxX - x2 > 120 || r.maxY - y2 > 120 || x1 - r.minX > 120 || y1 - r.minY > 120) //130
         closed = false;
      System.out.println("closed=" + closed);
      return closed;
   }

   private static int randomWithRange(int min, int max)
   {
      int range = (max - min) + 1;     
      return (int)(Math.random() * range) + min;
   }

   public static boolean checkPixel(byte[] blob, int idx, int width, int height, int size) {
      int score = 0;
      int x = idx % width;
      int y = idx / width;
      int b = (size - 1) / 2;
      int index = 0;
      for (int i = y - b; i <= y + b; i++) {
         for (int j = x - b; j <= x + b; j++) {
            index = i * width + j;
            if (isLegal(index, width, height) && blob[index * 3] != (byte)1)
               score++;
         } 
      }
      if (score < size)
    	  return true;
      else
        return false;
   }
   

   public void pickRegions() {
      ArrayList<Seed> seeds = new ArrayList<Seed>();
      int idx = 0;
      int marker = 2; //mark pixel with number greater than 1
      int i = 0;
      final int width = this.width;
      final int height = this.height;
      int x = 0; int y = 0;
      for (RegionGroup group : this.textgroup) {
         if (group.getMembers().size() > 0) {
            System.out.println(group.getId() + ":" + group.isClosed());
            System.out.println(group.minX + "," + group.minY + "," + group.maxX + "," + group.maxY);
            System.out.println(((group.maxX + group.minX) / 2) + "," + ((group.maxY + group.minY) / 2));
            idx = ((group.maxY + group.minY) / 2) * width + (group.maxX + group.minX) / 2;
            while(!checkPixel(this.pixels, idx, width, height, 2) && i++ < 1000) { // 
               x = BDetection.randomWithRange(group.minX, group.maxX);
               y = BDetection.randomWithRange(group.minY, group.maxY);
               idx = y * this.width + x;
            }
            seeds.add(new Seed(idx, group.minX, group.minY, group.maxX, group.maxY, group.isClosed(), group));
            System.out.println((idx % this.width) + "," + (idx / this.width));
         }
      }
      for (Seed seed : seeds) {
         //grow region from the seed points
         //System.out.println("region=" + marker);
         RegionInfo rInfo = seedGrowth(seed, marker, BDetection.RADIUS, BDetection.GAP_SCORE);
         this.pixCounts.add(rInfo);
         marker++;
      }
   }

   public void pickRegions2() {
      ArrayList<Seed> seeds = new ArrayList<Seed>();
      int idx = 0;
      int marker = 2; //mark pixel with number greater than 1
      int i = 0, j = 0;
      //int numSeeds = 3;
      final int width = this.width;
      final int height = this.height;
      int x = 0; int y = 0;
      int maxVal = 0;
      int maxIdx = 0;
      Integer[] candidates = new Integer[NUM_SEEDS];
      for (RegionGroup group : this.textgroup) {
    	  i = 0; j = 0;
    	  maxVal = 0;
    	  maxIdx = 0;
         if (group.getMembers().size() > 0) {
            System.out.println(group.getId() + ":" + group.isClosed());
            System.out.println(group.minX + "," + group.minY + "," + group.maxX + "," + group.maxY);
            System.out.println(((group.maxX + group.minX) / 2) + "," + ((group.maxY + group.minY) / 2));
            //removeTexts(group.getMembers());
            x = (group.maxX + group.minX) / 2;
            y = ((group.maxY + group.minY) / 2);
            idx = y * width + x;
            while(i++ < NUM_SEEDS) {
            	while(!checkPixel(this.pixels, idx, width, height, 3) && j++ < 1000) { //  && i++ < 1000
            		x = BDetection.randomWithRange(group.minX, (group.maxX + group.minX) / 2);
            		y = BDetection.randomWithRange(group.minY, (group.maxY + group.minY) / 2);
            		//y++; 
            		idx = y * this.width + x;
            		//seeds.add(new Seed(idx, group.minX, group.minY, group.maxX, group.maxY, group.isClosed(), group));
            	}
            	RegionInfo rInfo = seedGrowth4(new Seed(idx, group.minX, group.minY, group.maxX, group.maxY, group.isClosed(), group),
            			BDetection.RADIUS, BDetection.GAP_SCORE);
            	candidates[i-1] = idx;
            	if (rInfo.pixCount > maxVal) {
            		maxVal = rInfo.pixCount;
            		maxIdx = i-1;
            	}
            }
            seeds.add(new Seed(candidates[maxIdx], group.minX, group.minY, group.maxX, group.maxY, group.isClosed(), group));
            System.out.println((idx % this.width) + "," + (idx / this.width));
         }
      }
      for (Seed seed : seeds) {
         //grow region from the seed points
         //System.out.println("region=" + marker);
         RegionInfo rInfo = seedGrowth3(seed, marker, BDetection.RADIUS, BDetection.GAP_SCORE);
         this.pixCounts.add(rInfo);
         marker++;
      }
   }

   private void removeTexts(ArrayList<RegionInfo> texts) {
	   for (RegionInfo text : texts) {
		   for (Integer idx : text.pixels) {
			   this.pixels[idx*3] = (byte)1;
		   }
	   }
   }
   
   private void restoreTexts(ArrayList<RegionInfo> texts) {
	   for (RegionInfo text : texts) {
		   for (Integer idx : text.pixels) {
			   this.pixels[idx*3] = (byte)0;
		   }
	   }
   }
   
   public static Color pickRandomColor() {
      Color color = new Color();
      color.r = (byte)randomWithRange(0, 255);
      color.g = (byte)randomWithRange(0, 255);
      color.b = (byte)randomWithRange(0, 255);
      return color;
   }

   public static void pickColor(int n, Color color) {
      switch(n) {
         case 1 : color.r = (byte)255;
                  color.g = (byte)255;
                  color.b = (byte)255;
             break;
         case 2 : color.r = (byte)255;
                  color.g = (byte)0;
                  color.b = (byte)0;
             break;
         case 3 : color.r = (byte)0;
                  color.g = (byte)255;
                  color.b = (byte)0;
             break;
         case 4 : color.r = (byte)0;
                  color.g = (byte)0;
                  color.b = (byte)255;
             break;
         case 5 : color.r = (byte)255;
                  color.g = (byte)0;
                  color.b = (byte)255;
             break;
         case 6 : color.r = (byte)0;
                  color.g = (byte)255;
                  color.b = (byte)255;
             break;
         case 7 : color.r = (byte)255;
                  color.g = (byte)255;
                  color.b = (byte)0;
             break;
         case 8 : color.r = (byte)100;
                  color.g = (byte)255;
                  color.b = (byte)0;
             break;
         case 9 : color.r = (byte)0;
                  color.g = (byte)255;
                  color.b = (byte)100;
             break;
         case 10 : color.r = (byte)255;
                  color.g = (byte)0;
                  color.b = (byte)100;
             break;
         case 11 : color.r = (byte)255;
                  color.g = (byte)100;
                  color.b = (byte)0;
             break;
         default: color.r = (byte)100;
                  color.g = (byte)100;
                  color.b = (byte)100;
      }
   }

   private boolean isLegal(int idx) {
      boolean result = false;
      if (0 <= idx && idx < this.width * this.height) {
         result = true;
      }
      return result;
   }

   private static boolean isLegal(int idx, int width, int height) {
      boolean result = false;
      if (0 <= idx && idx < width * height) {
         result = true;
      }
      return result;
   }

   private boolean isWhiteSpace(int idx, int radius, int width, int height) { 
      boolean result = false;
      double score = 0.0;
      if (isLegal(idx) && this.pixels[3*idx] == (byte)1) {
         int x = idx % this.width;
         int y = idx / this.width;
         int idx2 = 0;
         for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
               idx2 = this.width * (y + j) + x + i;
               if (isLegal(idx2) && this.pixels[3*idx2] >= (byte)1) { //1
                  score++;
               }
            }
         }
         if (score >= (1+(radius*2)) * (1+(radius*2))) { //
            result = true;
         }
      }
      return result;
   }

   public static boolean isWhiteSpace(int idx, int radius, int width, int height, byte[] blob) { 
      boolean result = false;
      double score = 0.0;
      if (isLegal(idx, width, height) && blob[3*idx] == (byte)1) {
         int x = idx % width;
         int y = idx / width;
         int idx2 = 0;
         for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
               idx2 = width * (y + j) + x + i;
               if (isLegal(idx2, width, height) && blob[3*idx2] >= (byte)1) { //1
                  score++;
               }
            }
         }
         if (score >= (1+(radius*2)) * (1+(radius*2))) { //(1+(radius*2)) * (1+(radius*2))
            result = true;
         }
      }
      return result;
   }

   public boolean isWhiteSpace(int idx, int radius, float gScore) { 
      boolean result = false;
      float score = 0.0f;
      int x = idx % this.width;
      int y = idx / this.width;
      int idx2 = 0;
      int val = 0;

      if (isLegal(idx, this.width, this.height) && this.pixels[3*idx] == (byte)1) {
         for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
               idx2 = width * (y + j) + x + i;
               if (isLegal(idx2, this.width, this.height)) { 
                  if (this.pixels[3*idx2] >= (byte)1 ) { //1
                     score++;
                  }
                  else {
                     val = this.blackPixels[idx2] & 0xff;
                     if (val >=2 && this.blackRegions.get(val - 2).type == 1)
                        score++;
                  }
               }
            }
         }
         if (score >= gScore) { //(1+(radius*2)) * (1+(radius*2))
            result = true;
         }
      }
      return result;
   }

   public boolean isWhiteSpace(int idx, int radius, float gScore, Seed seed) { 
      boolean result = false;
      float score = 0.0f;
      int x = idx % this.width;
      int y = idx / this.width;
      int idx2 = 0;
      int val = 0;

      if (isLegal(idx, this.width, this.height) && this.pixels[3*idx] == (byte)1) {
         for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
               idx2 = width * (y + j) + x + i;
               if (isLegal(idx2, this.width, this.height) &&
                   (seed.closed || (x + i > seed.minX - 45 && x + i < seed.maxX + 45 &&
                   y + j > seed.minY - 45 && y + j < seed.maxY + 45))) { 
                  if (this.pixels[3*idx2] >= (byte)1 ) { //1
                     score++;
                  }
                  else {
                     val = this.blackPixels[idx2] & 0xff;
                     if (val >=2 && this.blackRegions.get(val - 2).type == 1)
                        score++;
                  }
               }
            }
         }
         if (score >= gScore) { //(1+(radius*2)) * (1+(radius*2))
            result = true;
         }
      }
      return result;
   }

   public boolean isWhiteSpace2(int idx, int radius, float gScore, Seed seed) { 
      boolean result = false;
      float score = 0.0f;
      int x = idx % this.width;
      int y = idx / this.width;
      int idx2 = 0;
      int val = 0;

      if (isLegal(idx, this.width, this.height) && this.pixels[3*idx] == (byte)1) {
         for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
               idx2 = width * (y + j) + x + i;
               if (isLegal(idx2, this.width, this.height)) { 
                  if (this.pixels[3*idx2] >= (byte)1 ) { //1
                     score++;
                  }
                  else {
                     val = this.blackPixels[idx2] & 0xff;
                     if (val >=2 && this.blackRegions.get(val - 2).type == 1)
                        score++;
                  }
               }
            }
         }
         if (score >= gScore) { //(1+(radius*2)) * (1+(radius*2))
            if (seed.closed || (x > seed.minX - 0 && x < seed.maxX + 0 &&
                   y > seed.minY - 0 && y < seed.maxY + 0))
               result = true;
            else if(!isChokePoint(idx, seed))
               result = true;
         }
      }
      return result;
   }

   public boolean isWhiteSpace3(int idx, int radius, float gScore) { 
      boolean result = false;
      float score = 0.0f;
      int x = idx % this.width;
      int y = idx / this.width;
      int idx2 = 0;
      int val = 0;

      if (isLegal(idx, this.width, this.height) && this.pixels[3*idx] == (byte)1) {
         for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
               idx2 = width * (y + j) + x + i;
               if (isLegal(idx2, this.width, this.height)) { 
                  if (this.pixels[3*idx2] >= (byte)1 ) { //1
                     score++;
                  }
                  else {
                     val = this.blackPixels[idx2] & 0xff;
                     if (val >=2 && this.blackRegions.get(val - 2).type == 1)
                        score++;
                  }
               }
            }
         }
         if (score >= gScore) { //(1+(radius*2)) * (1+(radius*2))
            result = true;
         }
      }
      return result;
   }

   public boolean isChokePoint(int idx, Seed seed) {
      boolean chokepoint = false;
      int x = idx % this.width;
      int y = idx / this.width;
      int i = 0;
      int index = (y + i) * this.width + x;
      while(y + i < this.height &&
            this.pixels[3 * index] != (byte)0 &&
            !((this.blackPixels[index] & 0xff) >= 2 &&
            this.blackRegions.get((this.blackPixels[index] & 0xff) - 2).type != 1)) { // up
         i++;
         index = (y + i) * this.width + x;
      }
      int maxY = y + i;
      i = 0;
      index = (y + i) * this.width + x;
      while(y + i >= 0 &&
            this.pixels[3 * index] != (byte)0 &&
            !((this.blackPixels[index] & 0xff) >= 2 &&
            this.blackRegions.get((this.blackPixels[index] & 0xff) - 2).type != 1)) { // down
         i--;
         index = (y + i) * this.width + x;
      }
      int minY = y + i;
      i = 0;
      index = y * this.width + x + i;
      while(x + i < this.width &&
            this.pixels[3 * index] != (byte)0 &&
            !((this.blackPixels[index] & 0xff) >= 2 &&
            this.blackRegions.get((this.blackPixels[index] & 0xff) - 2).type != 1)) { // right
         i++;
         index = y * this.width + x + i;
      }
      int maxX = x + i;
      i = 0;
      index = y * this.width + x + i;
      while(x + i >= 0 &&
            this.pixels[3 * index] != (byte)0 &&
            !((this.blackPixels[index] & 0xff) >= 2 &&
            this.blackRegions.get((this.blackPixels[index] & 0xff) - 2).type != 1)) { // left
         i--;
         index = y * this.width + x + i;
      }
      int minX = x + i;
      if ((maxX > seed.maxX + 50 || minX < seed.minX - 50) &&
          (maxY - minY <= 30 || maxY > seed.maxY + 50 || minY < seed.minY - 50))
          chokepoint = true;
      else if ((maxY > seed.maxY + 50 || minY < seed.minY - 50) &&
          (maxX - minX <= 30 || maxX > seed.maxX + 50 || minX < seed.minX - 50))
          chokepoint = true;
      //System.out.println("(" + minX + ", " + minY + ")(" + maxX + ", " + maxY + ")");
      return chokepoint;
   }

   public static boolean isBlackSpace(int idx, int radius, int width, int height, byte[] blob) { 
      boolean result = false;
      double score = 0.0;
      if (isLegal(idx, width, height) && blob[3*idx] == (byte)0) {
         int x = idx % width;
         int y = idx / width;
         int idx2 = 0;
         for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
               idx2 = width * (y + j) + x + i;
               if (isLegal(idx2, width, height) && blob[3*idx2] <= (byte)0) { //1
                  score++;
               }
            }
         }
         if (score >= (1+(radius*2)) * (1+(radius*2))) {
            result = true;
         }
      }
      return result;
   }

   public static boolean isBlackSpace(int idx, int radius, float gScore, int width, int height, byte[] blob) { 
      boolean result = false;
      float score = 0.0f;
      if (isLegal(idx, width, height) && blob[3*idx] == (byte)0) {
         int x = idx % width;
         int y = idx / width;
         int idx2 = 0;
         for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
               idx2 = width * (y + j) + x + i;
               if (isLegal(idx2, width, height) && blob[3*idx2] == (byte)0) { //1
                  score++;
               }
               if (score >= gScore) {
                  result = true;
                  return result;
               }
            }
         }
      }
      return result;
   }

   public static boolean isBlackPixel(int idx, int radius, float gScore, int width, int height, byte[] blob) { 
      boolean result = false;
      double score = 0.0;
      if (isLegal(idx, width, height) && (blob[idx] == (byte)1 || blob[idx] == (byte)0)) { // blob[idx] == (byte)1
         int x = idx % width;
         int y = idx / width;
         int idx2 = 0;
         for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
               idx2 = width * (y + j) + x + i;
               if (isLegal(idx2, width, height) && blob[idx2] >= (byte)1) { //1
                  score++;
               }
            }
         }
         if (score >= gScore || blob[idx] == (byte)1) {
            result = true;
         }
      }
      return result;
   }

   public static int addQueue(int idx, int marker, ArrayList<Integer> queue, byte[] blob) {
      blob[3*idx] = (byte)marker;
      blob[3*idx + 1] = (byte)marker;
      blob[3*idx + 2] = (byte)marker;
      queue.add(idx);
      return 1;
   }

   public static int addQueue(int idx, ArrayList<Integer> queue) {
      queue.add(idx);
      return 1;
   }

   public static int addQueueNoRGB(int idx, int marker, ArrayList<Integer> queue, byte[] blob) {
      blob[idx] = (byte)marker;
      queue.add(idx);
      return 1;
   }

   public static int addQueue(int idx, ArrayList<Integer> queue, byte[] blob) {
      blob[3*idx] = (byte)-1;
      blob[3*idx + 1] = (byte)-1;
      blob[3*idx + 2] = (byte)-1;
      queue.add(idx);
      return 1;
   }

   public void testRegions() {
      double percentArea = 0;
      for (RegionInfo region : this.pixCounts) {
         //System.out.println("pixCount=" + region.pixCount);
         //percentArea = (double)region.pixCount / ((double)this.width * (double)this.height);
         percentArea = ((double)(region.xRange * region.yRange)) / ((double)this.width * (double)this.height);
         if (percentArea < BDetection.MAX_AREA && percentArea > BDetection.MIN_AREA
             && (region.xRange < this.width * BDetection.MAX_WIDTH
             || region.yRange < this.height * BDetection.MAX_HEIGHT)
             ) {
            //region.isBubble = calcAverageBW(region);
            //region.isBubble = containsText(region);
            region.isBubble = true;
            System.out.println("In testRegions: Region " + region.marker + " is bubble="
                               + region.isBubble + ". area=" + percentArea + " PT enclosed=" + region.percentEnclosed);
         }
      }
   }

   public void testRegions2() {
      double bpRatio = 0;
      for (RegionInfo region : this.pixCounts) {
         analyzeContent(region, false);
         region.histR = buildHistR(region);
         region.percentArea = ((double)(region.xRange * region.yRange)) / ((double)this.width * (double)this.height);
         if (region.percentArea < BDetection.MAX_AREA && region.percentArea > BDetection.MIN_AREA
             && (region.xRange < this.width * BDetection.MAX_WIDTH
             || region.yRange < this.height * BDetection.MAX_HEIGHT)
             && (region.enclosed || region.percentEnclosed > 0.83)
             && region.pixCount > 6000
             && region.distBtwCenters < 57.4) {
        	region.isAreaLessThanMax = 1;
        	region.isAreaMoreThanMin = 1;
        	region.isXLessThanMax = 1;
        	region.isYLessThanMax = 1;
        	region.isRegionEnclosed = 1;
        	region.isPixCountMoreThanMin = 1;
        	region.isDistBtwCentersLessThanMax = 1;
        	 
            bpRatio = calcAverageBW(region);
            region.bpRatio = bpRatio;
            if (bpRatio > 0.059) {
               region.isBubble = true;
            }
            
         }
         System.out.println("In testRegions: Region " + region.marker + " is bubble="
                               + region.isBubble + ". area=" + region.percentArea + " PT enclosed=" + region.percentEnclosed
                               + ", pixCount=" + region.pixCount + ", dist=" + region.distBtwCenters
                               + ", bpRatio=" + bpRatio);
      }
   }

   private Histogram buildHistR(RegionInfo region) {
	   int unitX = 15;
	   int sizeX = 360 / unitX; //360 degrees
	   int l = (int)(Math.sqrt(this.width * this.width + this.height * this.height)/2);
	   //int sizeY = 20;
	   //Histogram2D histR = new Histogram2D(sizeX, sizeY, 0, sizeX, 0, l);
	   Histogram histR = new Histogram(sizeX, 0, 360);
	   int orgX = (region.maxX - region.minX) / 2;
	   int orgY = (region.maxY - region.minY) / 2;
	   //rho = cos()x + sin()y;
	   //x = r*cos(), y = r*sin();
	   int x = 0;
	   int y = 0;
	   int idx = 0;
	   int val = 0;
	   for (int t = 0; t < sizeX; t++) {
		   for (int r = 1; r < l; r++) {
			   x = orgX + (int) Math.floor(r * Math.cos(t*unitX*Math.PI/180));
			   y = orgY + (int) Math.floor(r * Math.sin(t*unitX*Math.PI/180));
			   idx = y * this.width + x;
			   if (isLegal(idx) && this.pixels[idx*3] == (byte)0) {
				   val = this.blackPixels[idx] & 0xff;
                   if (val >=2 && this.blackRegions.get(val - 2).type != 1)
				       histR.bin(t, r);
                   break;
			   }
			   else if (!isLegal(idx)) {
				   histR.bin(t, r);
				   break;
			   }
		   }
	   }
	   return histR;
   }
   
   /*private double getDistCenters(RegionInfo r) {
      double cX1 = (r.minX + r.maxX) / 2;
      double cY1 = (r.minY + r.maxY) / 2;
      double cX2, cY2;
      boolean inRegion = false;
      int idx = 0;
      int val = 0;
      int bpMarker = 0;
      for(int row = r.minY; row <= r.maxY; row++) {
         inRegion = false;
         for(int col = r.minX; col <= r.maxX; col++) {
            idx = this.width * row + col;
            val = this.pixels[idx*3] & 0xff;
            if (val == r.marker)
               inRegion = true;
            if (inRegion && val == 0 && !checkAhead(col, row))
               inRegion = false;
            if (inRegion && val == 0) {
               bpMarker = this.blackPixels[idx];
               if (bpMarker >= 2 && this.blackRegions.get(bpMarker).type == 1) {
                  cX2 = 0;
                  cY2 = 0;
                  break;
               }
            }
         }
      }
      double dist = 0;
      return dist; 
   }*/

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

   private void extractText(RegionInfo region) {
	      ArrayList<Integer> seeds = new ArrayList<Integer>();
	      ArrayList<RegionInfo> list = new ArrayList<RegionInfo>();
	      int maxX = (region.maxX + 4 <= this.width ? region.maxX + 4 : this.width);
	      int minX = (region.minX - 4 >= 0 ? region.minX - 4 : 0);
	      int maxY = (region.maxY + 4 <= this.height ? region.maxY + 4 : this.height);
	      int minY = (region.minY - 4 >= 0 ? region.minY - 4 : 0);
	      /*int maxX = region.maxX;
	      int minX = region.minX;
	      int maxY = region.maxY;
	      int minY = region.minY;*/
	      final int width = maxX - minX + 1;
	      final int height = maxY - minY + 1;
	      boolean start = false;
	      int idx = 0;
	      int[] rangeCol = new int[2];
	      int[] rangeRow = new int[2];
	      byte[] blob = getSubBlob(minX, minY, maxX, maxY);
	      for (int row = 0; row < height; row++) {
	    	  for (int col = 0; col < width; col++) {
	    		  idx = row * width + col;
		    	  //if (row == 0)
		    	  //	  rangeRow = getStartEndIdxRow(blob, width, height, col);
	    		  if (col == 0)
	    			  rangeCol = getStartEndIdxCol(blob, width, row);
	    		  //System.out.println("min row=" + rangeRow[0] + "-max row=" + rangeRow[1] + "/" + height);
	    		  //System.out.println("val of col=" + col + " is " + (0xFF & blob[idx * 3]));
	    		  /*if ((blob[idx*3] & 0xff) <= 0
	    				  && (col < rangeCol[0] || col > rangeCol[1] || row == height - 1)) {
	    			  //System.out.println("val of idx=" + idx + " is " + (blob[idx*3] & 0xff));
	    			  //blob[idx*3] = (byte)1;
	    			  //blob[idx*3 + 1] = (byte)1;
	    			  //blob[idx*3 + 2] = (byte)1;
	    			  RegionInfo rInfo = seedGrowth(idx, 1, width, blob,
	    			            new Callable1<Boolean>() {
	    			               public Boolean call(int idx, int minX, int maxX, int minY, int maxY, byte[] blob) {
	    			                  return BDetection.isBlackSpace(idx, 0, width, height, blob);
	    			               }
	    			            },
	    			            new Callable2<Integer>() {
	    			               public Integer call(int idx, int val, ArrayList<Integer> queue, byte[] blob) {
	    			                  return BDetection.addQueue(idx, val, queue, blob);
	    			               }
	    			            }
	    			         );
	    		  }*/
	    	  }
	      }
	      writeRegionImage(region, blob, 0, width, height);
   }
   
   private void analyzeContent(RegionInfo region, Boolean write) {
	      int maxX = (region.maxX + 4 <= this.width ? region.maxX + 4 : this.width);
	      int minX = (region.minX - 4 >= 0 ? region.minX - 4 : 0);
	      int maxY = (region.maxY + 4 <= this.height ? region.maxY + 4 : this.height);
	      int minY = (region.minY - 4 >= 0 ? region.minY - 4 : 0);
	      final int width = maxX - minX + 1;
	      final int height = maxY - minY + 1;
	      int idx = 0;
	      int[] rangeCol = new int[2];
	      int[] rangeRow = new int[2];
	      byte[] blob = getSubBlob(minX, minY, maxX, maxY);
	      for (int row = 0; row < height; row++) {
	    	  for (int col = 0; col < width; col++) {
	    		  idx = row * width + col;
	    		  if (col == 0)
	    			  rangeCol = getStartEndIdxCol(blob, width, row);
	    		  if ((blob[idx*3] & 0xff) <= 0
	    				  && (col < rangeCol[0] || col > rangeCol[1] || row == height - 1)) {
	    			  RegionInfo rInfo = seedGrowth(idx, 1, width, blob,
	    			            new Callable1<Boolean>() {
	    			               public Boolean call(int idx, int minX, int maxX, int minY, int maxY, byte[] blob) {
	    			                  return BDetection.isBlackSpace(idx, 0, width, height, blob);
	    			               }
	    			            },
	    			            new Callable2<Integer>() {
	    			               public Integer call(int idx, int val, ArrayList<Integer> queue, byte[] blob) {
	    			                  return BDetection.addQueue(idx, val, queue, blob);
	    			               }
	    			            }
	    			         );
	    		  }
	    	  }
	      }
	      region.histH = buildHistH(blob, width, height);
	      region.histV = buildHistV(blob, width, height);
	      //return blob;
	      if (write)
	          writeRegionImage(region, blob, 0, width, height);
   }
   
   private static Histogram buildHistH(byte[] blob, int width, int height) {
	   Histogram hist = new Histogram(10, 0, width);
	   for (int row = 0; row < height; row++) {
		   for (int col = 0; col < width; col++) {
			   if (blob[row * width + col] == (byte)0)
				   hist.bin(col);
		   }
	   }
	   return hist;
   }
   
   private static Histogram buildHistV(byte[] blob, int width, int height) {
	   Histogram hist = new Histogram(10, 0, height);
	   for (int row = 0; row < height; row++) {
		   for (int col = 0; col < width; col++) {
			   if (blob[row * width + col] == (byte)0)
				   hist.bin(row);
		   }
	   }
	   return hist;
   }
   
   private static int[] getStartEndIdxRow(byte[] blob, int width, int height, int col) {
	   int[] range = new int[2];
	   int idx = 0;
	   int min = height;
	   int max = 0;
	   System.out.println("height=" + height);
	   for (int row = 0; row < height; row++) {
		   idx = row * width + col;
		   //System.out.println("val=" + (blob[idx * 3] & 0xFF));
		   if ((blob[idx * 3] & 0xff) >= 1) {
			   if (row < min)
				   min = row;
			   if (row > max)
				   max = row;
		   }
	   }
	   range[0] = min;
	   range[1] = max;
	   //System.out.println("start=" + range[0] + ", end=" + range[1]);
	   return range;
   }
   
   private static int[] getStartEndIdxCol(byte[] blob, int width, int row) {
	   int[] range = new int[2];
	   int idx = 0;
	   int min = width;
	   int max = 0;
	   for (int col = 0; col < width; col++) {
		   idx = row * width + col;
		   if ((blob[idx * 3] & 0xFF) >= 1) {
			   if (col < min)
				   min = col;
			   if (col > max)
				   max = col;
		   }
	   }
	   range[0] = min;
	   range[1] = max;
	   //System.out.println("start=" + range[0] + ", end=" + range[1]);
	   return range;
   }

   private boolean containsText(RegionInfo region) {
      ArrayList<Integer> seeds = new ArrayList<Integer>();
      ArrayList<RegionInfo> list = new ArrayList<RegionInfo>();
      int maxX = region.maxX - region.minX;
      final int width = maxX + 1;
      final int height = region.maxY - region.minY + 1;
      boolean start = false;
      byte[] blob = getSubBlob(region.minX, region.minY, region.maxX, region.maxY);
      writeRegionImage(region, blob, 0);
      for (int i = 0; i < width * height; i++) {
         if (blob[i * 3] == region.marker) {
            start = true;
         }
         else if (blob[i * 3] > 1) {
            start = false;
         }
         else if (start && blob[i * 3] == 0) {
            if (inRegion(blob, region, i)) {
               seeds.add(i);
            }
            else {
               start = false;
            }
         }
         if (i % width == 0) {
            start = false;
         }
      }
      System.out.println("seeds size=" + seeds.size());
      for (Integer seed : seeds) {
         //grow region from the seed points
         RegionInfo rInfo = seedGrowth(seed, 0, width, blob,
            new Callable1<Boolean>() {
               public Boolean call(int idx, int minX, int maxX, int minY, int maxY, byte[] blob) {
                  return BDetection.isBlackSpace(idx, 0, width, height, blob);
               }
            },
            new Callable2<Integer>() {
               public Integer call(int idx, int val, ArrayList<Integer> queue, byte[] blob) {
                  return BDetection.addQueue(idx, queue, blob);
               }
            }
         );
         if (rInfo.pixCount > 1) {
            list.add(rInfo);
         }
      }
      System.out.println("list size=" + list.size());
      writeRegionImage(region, blob, 1);
      return isText(region, list);
   }

   private boolean inRegion(byte[] blob, RegionInfo region, int idx) {
      boolean result = false;
      int i = idx;
      int width = region.maxX - region.minX + 1; 
      //System.out.println("in inRegion: idx=" + i + " width=" + width);
      while (i % width != 0 && !result) {
         if (blob[i * 3] == region.marker) {
            result = true;
         }
         //System.out.println(i + ":" + result);
         i++;
      }
      return result;
   }

   private void writeRegionImage(RegionInfo region, byte[] pixels, int idx) {
      String fname = this.filename + "_" + Integer.toString(region.marker)
    		  + "_" + Integer.toString(idx) + ".jpg";
      int width = region.xRange;
      int height = region.yRange;
      byte[] blob = fillRegion(pixels, width, height);
      try {
         MagickImage blobImage = new MagickImage();
         blobImage.constituteImage(width,
                                   height,
                                   "RGB",
                                   blob);
         blobImage.setFileName(fname);
         blobImage.writeImage(new ImageInfo());
      } catch (MagickException e) {
         e.printStackTrace();
      }
   }

   private void writeRegionImage(RegionInfo region, byte[] pixels, int idx, int width, int height) {
	      int i = this.filename.indexOf(".jpg");
	      String fname = this.filename.substring(0, i) + "_" + Integer.toString(region.marker)
	    		  + "_" + Integer.toString(idx) + ".jpg";
	      //int width = region.xRange;
	      //int height = region.yRange;
	      byte[] blob = fillRegion(pixels, width, height);
	      try {
	         MagickImage blobImage = new MagickImage();
	         blobImage.constituteImage(width,
	                                   height,
	                                   "RGB",
	                                   blob);
	         blobImage.setFileName(fname);
	         blobImage.writeImage(new ImageInfo());
	      } catch (MagickException e) {
	         e.printStackTrace();
	      }
	   }
   
   private void writeBlackImage(String fname) {
      byte[] blob = convertToRGB();
      try {
         MagickImage blobImage = new MagickImage();
         blobImage.constituteImage(this.width,
                                   this.height,
                                   "RGB",
                                   blob);
         blobImage.setFileName("BP_" + fname);
         blobImage.writeImage(new ImageInfo());
      } catch (MagickException e) {
         e.printStackTrace();
      }
   }

   private boolean isText(RegionInfo bubble, ArrayList<RegionInfo> list) {
      int sumArea = 0;
      int sumPix = 0;
      int sumVert = 0;
      int sumHoriz = 0;
      int size = list.size();
      double avArea = 0;
      double avPix = 0;
      double avVert = 0;
      double avHoriz = 0;
      double sdArea = 0;
      double sdPix = 0;
      double sdVert = 0;
      double sdHoriz = 0;
      double blkPixPercent = 0;
      for (RegionInfo region : list) {
         if (region.minX > 0 && region.minY > 0 && region.maxX < bubble.xRange && region.maxY < bubble.yRange) {
            sumPix += region.pixCount;
            sumVert += region.yRange;
            sumHoriz += region.xRange;
            sumArea = sumArea + region.yRange * region.xRange;
         }
      }
      System.out.println("sum for area=" + sumArea + " pix=" + sumPix + " vert=" + sumVert);
      avArea = (double)sumArea / (double)size;
      avPix = (double)sumPix / (double)size;
      avVert = (double)sumVert / (double)size;
      avHoriz = (double)sumHoriz / (double)size;
      blkPixPercent = (double)sumPix / (double)(bubble.pixCount + sumPix);
      System.out.println("av for area=" + avArea + " pix=" + avPix + " vert=" + avVert);
      for (RegionInfo region : list) {
         if (region.minX > 0 && region.minY > 0 && region.maxX < bubble.xRange && region.maxY < bubble.yRange) {
            sdArea = sdArea + (region.yRange * region.xRange - avArea)
                     * (region.yRange * region.xRange - avArea);
            sdPix = sdPix + (region.pixCount - avPix) * (region.pixCount - avPix);
            sdVert = sdVert + (region.yRange - avVert) * (region.yRange - avVert);
            sdHoriz = sdHoriz + (region.xRange - avHoriz) * (region.xRange - avHoriz);
         }
      }

      sdArea = sdArea / (double)size;
      sdPix = sdPix / (double)size;
      sdVert = sdVert / (double)size;
      sdHoriz = sdHoriz / (double)size;
      System.out.println("sd for area=" + sdArea + " pix=" + sdPix + " vert=" + sdVert + " horiz=" + sdHoriz + " blkPixPercent=" + blkPixPercent);
      if (size > 0 && avArea > 0 &&
          sumPix < BDetection.MAX_SUM_PIX && sumPix > BDetection.MIN_SUM_PIX &&
          sdVert > BDetection.MIN_SD_VERT  && sdHoriz > BDetection.MIN_SD_HORIZ &&
          sdVert < BDetection.MAX_SD_VERT  && sdHoriz < BDetection.MAX_SD_HORIZ &&
          avPix > BDetection.MIN_AV_PIX && blkPixPercent > BDetection.MIN_BLKPIX_PERCENT) {
         return true;
      }
      else {
         return false;
      }
   }

   public static RegionInfo seedGrowth(int seed, int val, int width, byte[] blob,
                                       Callable1<Boolean> checkFunc, Callable2<Integer> addFunc) {
      int pix, x, y, idx, count, maxX, minX, maxY, minY;
      pix = seed;
      x = y = idx = count = 0;
      ArrayList<Integer> queue = new ArrayList<Integer>();
      //ArrayList<Integer> blackPixels = new ArrayList<Integer>(); 
      Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
      x = pix % width;
      y = pix / width;
      maxX = minX = x;
      maxY = minY = y;
      //if (y - 1 >= 0 && x - 1 >= 0 && x - 1 < width) {
         idx = width * y + x;
         if (checkFunc.call(idx, minX, maxX, minY, maxY, blob)) { // check the pixel itself
            count += addFunc.call(idx, val, queue, blob);
            //blackPixels.add(new Integer(idx));
         }
         table.put(idx, 1);
      //}
      while(!queue.isEmpty()) {
         pix = queue.remove(0);
         x = pix % width;
         y = pix / width;
         // check all 8 neighbors
         // if the neighbor is 1, mark the pixel with the marker and add the pixel to the queue
         if (y - 1 >= 0 && x - 1 >= 0 && x - 1 < width) {
            idx = width * (y - 1) + x - 1;
            if (table.get(idx) == null && checkFunc.call(idx, minX, maxX, minY, maxY, blob)) { // check right 1
               count += addFunc.call(idx, val, queue, blob);
               maxX = (x > maxX ? x : maxX);
               minX = (x < minX ? x : minX);
               maxY = (y > maxY ? y : maxY);
               minY = (y < minY ? y : minY);
               //blackPixels.add(new Integer(idx));
            }
            table.put(idx, 1);
         }
         if (y >= 0 && x - 1 >= 0 && x - 1 < width) {
            idx = width * (y) + x - 1;
            if (table.get(idx) == null && checkFunc.call(idx, minX, maxX, minY, maxY, blob)) { // check right 2
               count += addFunc.call(idx, val, queue, blob);
               maxX = (x > maxX ? x : maxX);
               minX = (x < minX ? x : minX);
               maxY = (y > maxY ? y : maxY);
               minY = (y < minY ? y : minY);
               //blackPixels.add(new Integer(idx));
            }
            table.put(idx, 1);
         }
         if (y + 1 >= 0 && x - 1 >= 0 && x - 1 < width) {
            idx = width * (y + 1) + x - 1;
            if (table.get(idx) == null && checkFunc.call(idx, minX, maxX, minY, maxY, blob)) { // check right 3
               count += addFunc.call(idx, val, queue, blob);
               maxX = (x > maxX ? x : maxX);
               minX = (x < minX ? x : minX);
               maxY = (y > maxY ? y : maxY);
               minY = (y < minY ? y : minY);
               //blackPixels.add(new Integer(idx));
            }
            table.put(idx, 1);
         }
         if (y - 1 >= 0 && x >= 0 && x < width) {
            idx = width * (y - 1) + x;
            if (table.get(idx) == null && checkFunc.call(idx, minX, maxX, minY, maxY, blob)) { // check right 4
               count += addFunc.call(idx, val, queue, blob);
               maxX = (x > maxX ? x : maxX);
               minX = (x < minX ? x : minX);
               maxY = (y > maxY ? y : maxY);
               minY = (y < minY ? y : minY);
               //blackPixels.add(new Integer(idx));
            }
            table.put(idx, 1);
         }
         if (y + 1 >= 0 && x >= 0 && x < width) {
            idx = width * (y + 1) + x;
            if (table.get(idx) == null && checkFunc.call(idx, minX, maxX, minY, maxY, blob)) { // check right 5
               count += addFunc.call(idx, val, queue, blob);
               maxX = (x > maxX ? x : maxX);
               minX = (x < minX ? x : minX);
               maxY = (y > maxY ? y : maxY);
               minY = (y < minY ? y : minY);
               //blackPixels.add(new Integer(idx));
            }
            table.put(idx, 1);
         }
         if (y - 1 >= 0 && x + 1 >= 0 && x + 1 < width) {
            idx = width * (y - 1) + x + 1;
            if (table.get(idx) == null && checkFunc.call(idx, minX, maxX, minY, maxY, blob)) { // check right 6
               count += addFunc.call(idx, val, queue, blob);
               maxX = (x > maxX ? x : maxX);
               minX = (x < minX ? x : minX);
               maxY = (y > maxY ? y : maxY);
               minY = (y < minY ? y : minY);
               //blackPixels.add(new Integer(idx));
            }
            table.put(idx, 1);
         }
         if (y >= 0 && x + 1 >= 0 && x + 1 < width) {
            idx = width * y + x + 1;
            if (table.get(idx) == null && checkFunc.call(idx, minX, maxX, minY, maxY, blob)) { // check right 7
               count += addFunc.call(idx, val, queue, blob);
               maxX = (x > maxX ? x : maxX);
               minX = (x < minX ? x : minX);
               maxY = (y > maxY ? y : maxY);
               minY = (y < minY ? y : minY);
               //blackPixels.add(new Integer(idx));
            }
            table.put(idx, 1);
         }
         if (y + 1 >= 0 && x + 1 >= 0 && x + 1 < width) {
            idx = width * (y + 1) + x + 1;
            if (table.get(idx) == null && checkFunc.call(idx, minX, maxX, minY, maxY, blob)) { // check right 8
               count += addFunc.call(idx, val, queue, blob);
               maxX = (x > maxX ? x : maxX);
               minX = (x < minX ? x : minX);
               maxY = (y > maxY ? y : maxY);
               minY = (y < minY ? y : minY);
               //blackPixels.add(new Integer(idx));
            }
            table.put(idx, 1);
         }
      }
      //return new RegionInfo(val, count, minX, maxX, minY, maxY, blackPixels);
      return new RegionInfo(val, count, minX, maxX, minY, maxY);
   }

   public RegionInfo seedGrowth(Seed seed, int marker, int radius, float gScore) {
      int pix, x, y, idx, count, maxX, minX, maxY, minY;
      pix = seed.index;
      int maxArea = (seed.maxX - seed.minX) * (seed.maxY - seed.minY) * 3;
      x = y = idx = count = 0;
      ArrayList<Integer> queue = new ArrayList<Integer>();
      Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
      x = pix % width;
      y = pix / width;
      maxX = minX = x;
      maxY = minY = y;

      idx = width * y + x;
      count += addQueue(idx, marker, queue, this.pixels);
      table.put(idx, 1);

      while(!queue.isEmpty()) {  
         pix = queue.remove(0);
         x = pix % width;
         y = pix / width;
         // check all 8 neighbors
         // if the neighbor is 1, mark the pixel with the marker and add the pixel to the queue
         if (y - 1 >= 0 && x - 1 >= 0 && x - 1 < width) {
            idx = width * (y - 1) + x - 1;
            if (table.get(idx) == null && isWhiteSpace2(idx, radius, gScore, seed)) { // check right 1
               count += addQueue(idx, marker, queue, this.pixels);
         maxX = (x > maxX ? x : maxX);
         minX = (x < minX ? x : minX);
         maxY = (y > maxY ? y : maxY);
         minY = (y < minY ? y : minY);
            }
            table.put(idx, 1);
         }
         if (y >= 0 && x - 1 >= 0 && x - 1 < width) {
            idx = width * (y) + x - 1;
            if (table.get(idx) == null && isWhiteSpace2(idx, radius, gScore, seed)) { // check right 2
               count += addQueue(idx, marker, queue, this.pixels);
         maxX = (x > maxX ? x : maxX);
         minX = (x < minX ? x : minX);
         maxY = (y > maxY ? y : maxY);
         minY = (y < minY ? y : minY);
            }
            table.put(idx, 1);
         }
         if (y + 1 >= 0 && x - 1 >= 0 && x - 1 < width) {
            idx = width * (y + 1) + x - 1;
            if (table.get(idx) == null && isWhiteSpace2(idx, radius, gScore, seed)) { // check right 3
               count += addQueue(idx, marker, queue, this.pixels);
         maxX = (x > maxX ? x : maxX);
         minX = (x < minX ? x : minX);
         maxY = (y > maxY ? y : maxY);
         minY = (y < minY ? y : minY);
            }
            table.put(idx, 1);
         }
         if (y - 1 >= 0 && x >= 0 && x < width) {
            idx = width * (y - 1) + x;
            if (table.get(idx) == null && isWhiteSpace2(idx, radius, gScore, seed)) { // check right 4
               count += addQueue(idx, marker, queue, this.pixels);
         maxX = (x > maxX ? x : maxX);
         minX = (x < minX ? x : minX);
         maxY = (y > maxY ? y : maxY);
         minY = (y < minY ? y : minY);
            }
            table.put(idx, 1);
         }
         if (y + 1 >= 0 && x >= 0 && x < width) {
            idx = width * (y + 1) + x;
            if (table.get(idx) == null && isWhiteSpace2(idx, radius, gScore, seed)) { // check right 5
               count += addQueue(idx, marker, queue, this.pixels);
         maxX = (x > maxX ? x : maxX);
         minX = (x < minX ? x : minX);
         maxY = (y > maxY ? y : maxY);
         minY = (y < minY ? y : minY);
            }
            table.put(idx, 1);
         }
         if (y - 1 >= 0 && x + 1 >= 0 && x + 1 < width) {
            idx = width * (y - 1) + x + 1;
            if (table.get(idx) == null && isWhiteSpace2(idx, radius, gScore, seed)) { // check right 6
               count += addQueue(idx, marker, queue, this.pixels);
         maxX = (x > maxX ? x : maxX);
         minX = (x < minX ? x : minX);
         maxY = (y > maxY ? y : maxY);
         minY = (y < minY ? y : minY);
            }
            table.put(idx, 1);
         }
         if (y >= 0 && x + 1 >= 0 && x + 1 < width) {
            idx = width * y + x + 1;
            if (table.get(idx) == null && isWhiteSpace2(idx, radius, gScore, seed)) { // check right 7
               count += addQueue(idx, marker, queue, this.pixels);
         maxX = (x > maxX ? x : maxX);
         minX = (x < minX ? x : minX);
         maxY = (y > maxY ? y : maxY);
         minY = (y < minY ? y : minY);
            }
            table.put(idx, 1);
         }
         if (y + 1 >= 0 && x + 1 >= 0 && x + 1 < width) {
            idx = width * (y + 1) + x + 1;
            if (table.get(idx) == null && isWhiteSpace2(idx, radius, gScore, seed)) { // check right 8
               count += addQueue(idx, marker, queue, this.pixels);
         maxX = (x > maxX ? x : maxX);
         minX = (x < minX ? x : minX);
         maxY = (y > maxY ? y : maxY);
         minY = (y < minY ? y : minY);
            }
            table.put(idx, 1);
         }
      }
      return new RegionInfo(marker, count, minX, maxX, minY, maxY);
   }

   public RegionInfo seedGrowth(Seed seed, int radius, float gScore) {
      int pix, x, y, idx, count, maxX, minX, maxY, minY;
      pix = seed.index;
      int maxArea = (seed.maxX - seed.minX) * (seed.maxY - seed.minY) * 20;
      x = y = idx = count = 0;
      ArrayList<Integer> queue = new ArrayList<Integer>();
      Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
      x = pix % width;
      y = pix / width;
      maxX = minX = x;
      maxY = minY = y;

      idx = width * y + x;
      count += addQueue(idx, queue);
      table.put(idx, 1);

      while(!queue.isEmpty()) { // && (maxX - minX) * (maxY - minY) <= maxArea
         pix = queue.remove(0);
         x = pix % width;
         y = pix / width;
         // check all 8 neighbors
         // if the neighbor is 1, mark the pixel with the marker and add the pixel to the queue
         if (y - 1 >= 0 && x - 1 >= 0 && x - 1 < width) {
            idx = width * (y - 1) + x - 1;
            if (table.get(idx) == null && isWhiteSpace(idx, radius, gScore)) { // check right 1
               count += addQueue(idx, queue);
         maxX = (x > maxX ? x : maxX);
         minX = (x < minX ? x : minX);
         maxY = (y > maxY ? y : maxY);
         minY = (y < minY ? y : minY);
            }
            table.put(idx, 1);
         }
         if (y >= 0 && x - 1 >= 0 && x - 1 < width) {
            idx = width * (y) + x - 1;
            if (table.get(idx) == null && isWhiteSpace(idx, radius, gScore)) { // check right 2
               count += addQueue(idx, queue);
         maxX = (x > maxX ? x : maxX);
         minX = (x < minX ? x : minX);
         maxY = (y > maxY ? y : maxY);
         minY = (y < minY ? y : minY);
            }
            table.put(idx, 1);
         }
         if (y + 1 >= 0 && x - 1 >= 0 && x - 1 < width) {
            idx = width * (y + 1) + x - 1;
            if (table.get(idx) == null && isWhiteSpace(idx, radius, gScore)) { // check right 3
               count += addQueue(idx, queue);
         maxX = (x > maxX ? x : maxX);
         minX = (x < minX ? x : minX);
         maxY = (y > maxY ? y : maxY);
         minY = (y < minY ? y : minY);
            }
            table.put(idx, 1);
         }
         if (y - 1 >= 0 && x >= 0 && x < width) {
            idx = width * (y - 1) + x;
            if (table.get(idx) == null && isWhiteSpace(idx, radius, gScore)) { // check right 4
               count += addQueue(idx, queue);
         maxX = (x > maxX ? x : maxX);
         minX = (x < minX ? x : minX);
         maxY = (y > maxY ? y : maxY);
         minY = (y < minY ? y : minY);
            }
            table.put(idx, 1);
         }
         if (y + 1 >= 0 && x >= 0 && x < width) {
            idx = width * (y + 1) + x;
            if (table.get(idx) == null && isWhiteSpace(idx, radius, gScore)) { // check right 5
               count += addQueue(idx, queue);
         maxX = (x > maxX ? x : maxX);
         minX = (x < minX ? x : minX);
         maxY = (y > maxY ? y : maxY);
         minY = (y < minY ? y : minY);
            }
            table.put(idx, 1);
         }
         if (y - 1 >= 0 && x + 1 >= 0 && x + 1 < width) {
            idx = width * (y - 1) + x + 1;
            if (table.get(idx) == null && isWhiteSpace(idx, radius, gScore)) { // check right 6
               count += addQueue(idx, queue);
         maxX = (x > maxX ? x : maxX);
         minX = (x < minX ? x : minX);
         maxY = (y > maxY ? y : maxY);
         minY = (y < minY ? y : minY);
            }
            table.put(idx, 1);
         }
         if (y >= 0 && x + 1 >= 0 && x + 1 < width) {
            idx = width * y + x + 1;
            if (table.get(idx) == null && isWhiteSpace(idx, radius, gScore)) { // check right 7
               count += addQueue(idx, queue);
         maxX = (x > maxX ? x : maxX);
         minX = (x < minX ? x : minX);
         maxY = (y > maxY ? y : maxY);
         minY = (y < minY ? y : minY);
            }
            table.put(idx, 1);
         }
         if (y + 1 >= 0 && x + 1 >= 0 && x + 1 < width) {
            idx = width * (y + 1) + x + 1;
            if (table.get(idx) == null && isWhiteSpace(idx, radius, gScore)) { // check right 8
               count += addQueue(idx, queue);
         maxX = (x > maxX ? x : maxX);
         minX = (x < minX ? x : minX);
         maxY = (y > maxY ? y : maxY);
         minY = (y < minY ? y : minY);
            }
            table.put(idx, 1);
         }
      }
      return new RegionInfo(0, count, minX, maxX, minY, maxY);
   }

   // called from pickRegions2 to fill regions
   public RegionInfo seedGrowth3(Seed seed, int marker, int radius, float gScore) {
      int pix, x, y, x1, y1, idx, count, maxX, minX, maxY, minY;
      int edgePixels = 0;
      int closed = 0;
      pix = seed.index;
      int maxArea = (seed.maxX - seed.minX) * (seed.maxY - seed.minY) * 3;
      x = y = x1 = y1 = idx = count = 0;
      ArrayList<Integer> queue = new ArrayList<Integer>();
      Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
      x = pix % this.width;
      y = pix / this.width;
      maxX = minX = x;
      maxY = minY = y;

      idx = width * y + x;
      count += addQueue(idx, marker, queue, this.pixels);
      table.put(idx, 1);

      while(!queue.isEmpty()) {  
         pix = queue.remove(0);
         x = pix % width;
         y = pix / width;
         // check all 8 neighbors
         // if the neighbor is 1, mark the pixel with the marker and add the pixel to the queue
         if (y - 1 >= 0 && x - 1 >= 0 && x - 1 < width) {
            idx = width * (y - 1) + x - 1;
            if (table.get(idx) == null && isWhiteSpace3(idx, radius, gScore)) { // check right 1
               x1 = idx % this.width;
               y1 = idx / this.width;
               if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
                   y1 > seed.minY - 0 && y1 < seed.maxY + 0)
                                             || !isChokePoint(idx, seed)) { // boundary reached
                  count += addQueue(idx, marker, queue, this.pixels);
                  maxX = (x > maxX ? x : maxX);
                  minX = (x < minX ? x : minX);
                  maxY = (y > maxY ? y : maxY);
                  minY = (y < minY ? y : minY);
               }
               else { // boundary not reached
                  edgePixels++;
               }
            }
            else if (table.get(idx) == null) {
               closed++;
               edgePixels++;
            }
            table.put(idx, 1);
         }
         if (y >= 0 && x - 1 >= 0 && x - 1 < width) {
            idx = width * (y) + x - 1;
            if (table.get(idx) == null && isWhiteSpace3(idx, radius, gScore)) { // check right 1
               x1 = idx % this.width;
               y1 = idx / this.width;
               if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
                   y1 > seed.minY - 0 && y1 < seed.maxY + 0)
                                             || !isChokePoint(idx, seed)) { // boundary reached
                  count += addQueue(idx, marker, queue, this.pixels);
                  maxX = (x > maxX ? x : maxX);
                  minX = (x < minX ? x : minX);
                  maxY = (y > maxY ? y : maxY);
                  minY = (y < minY ? y : minY);
               }
               else { // boundary not reached
                  edgePixels++;
               }
            }
            else if (table.get(idx) == null) {
               closed++;
               edgePixels++;
            }
            table.put(idx, 1);
         }
         if (y + 1 >= 0 && x - 1 >= 0 && x - 1 < width) {
            idx = width * (y + 1) + x - 1;
            if (table.get(idx) == null && isWhiteSpace3(idx, radius, gScore)) { // check right 1
               x1 = idx % this.width;
               y1 = idx / this.width;
               if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
                   y1 > seed.minY - 0 && y1 < seed.maxY + 0)
                                             || !isChokePoint(idx, seed)) { // boundary reached
                  count += addQueue(idx, marker, queue, this.pixels);
                  maxX = (x > maxX ? x : maxX);
                  minX = (x < minX ? x : minX);
                  maxY = (y > maxY ? y : maxY);
                  minY = (y < minY ? y : minY);
               }
               else { // boundary not reached
                  edgePixels++;
               }
            }
            else if (table.get(idx) == null) {
               closed++;
               edgePixels++;
            }
            table.put(idx, 1);
         }
         if (y - 1 >= 0 && x >= 0 && x < width) {
            idx = width * (y - 1) + x;
            if (table.get(idx) == null && isWhiteSpace3(idx, radius, gScore)) { // check right 1
               x1 = idx % this.width;
               y1 = idx / this.width;
               if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
                   y1 > seed.minY - 0 && y1 < seed.maxY + 0)
                                             || !isChokePoint(idx, seed)) { // boundary reached
                  count += addQueue(idx, marker, queue, this.pixels);
                  maxX = (x > maxX ? x : maxX);
                  minX = (x < minX ? x : minX);
                  maxY = (y > maxY ? y : maxY);
                  minY = (y < minY ? y : minY);
               }
               else { // boundary not reached
                  edgePixels++;
               }
            }
            else if (table.get(idx) == null) {
               closed++;
               edgePixels++;
            }
            table.put(idx, 1);
         }
         if (y + 1 >= 0 && x >= 0 && x < width) {
            idx = width * (y + 1) + x;
            if (table.get(idx) == null && isWhiteSpace3(idx, radius, gScore)) { // check right 1
               x1 = idx % this.width;
               y1 = idx / this.width;
               if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
                   y1 > seed.minY - 0 && y1 < seed.maxY + 0)
                                             || !isChokePoint(idx, seed)) { // boundary reached
                  count += addQueue(idx, marker, queue, this.pixels);
                  maxX = (x > maxX ? x : maxX);
                  minX = (x < minX ? x : minX);
                  maxY = (y > maxY ? y : maxY);
                  minY = (y < minY ? y : minY);
               }
               else { // boundary not reached
                  edgePixels++;
               }
            }
            else if (table.get(idx) == null) {
               closed++;
               edgePixels++;
            }
            table.put(idx, 1);
         }
         if (y - 1 >= 0 && x + 1 >= 0 && x + 1 < width) {
            idx = width * (y - 1) + x + 1;
            if (table.get(idx) == null && isWhiteSpace3(idx, radius, gScore)) { // check right 1
               x1 = idx % this.width;
               y1 = idx / this.width;
               if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
                   y1 > seed.minY - 0 && y1 < seed.maxY + 0)
                                             || !isChokePoint(idx, seed)) { // boundary reached
                  count += addQueue(idx, marker, queue, this.pixels);
                  maxX = (x > maxX ? x : maxX);
                  minX = (x < minX ? x : minX);
                  maxY = (y > maxY ? y : maxY);
                  minY = (y < minY ? y : minY);
               }
               else { // boundary not reached
                  edgePixels++;
               }
            }
            else if (table.get(idx) == null) {
               closed++;
               edgePixels++;
            }
            table.put(idx, 1);
         }
         if (y >= 0 && x + 1 >= 0 && x + 1 < width) {
            idx = width * y + x + 1;
            if (table.get(idx) == null && isWhiteSpace3(idx, radius, gScore)) { // check right 1
               x1 = idx % this.width;
               y1 = idx / this.width;
               if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
                   y1 > seed.minY - 0 && y1 < seed.maxY + 0)
                                             || !isChokePoint(idx, seed)) { // boundary reached
                  count += addQueue(idx, marker, queue, this.pixels);
                  maxX = (x > maxX ? x : maxX);
                  minX = (x < minX ? x : minX);
                  maxY = (y > maxY ? y : maxY);
                  minY = (y < minY ? y : minY);
               }
               else { // boundary not reached
                  edgePixels++;
               }
            }
            else if (table.get(idx) == null) {
               closed++;
               edgePixels++;
            }
            table.put(idx, 1);
         }
         if (y + 1 >= 0 && x + 1 >= 0 && x + 1 < width) {
            idx = width * (y + 1) + x + 1;
            if (table.get(idx) == null && isWhiteSpace3(idx, radius, gScore)) { // check right 1
               x1 = idx % this.width;
               y1 = idx / this.width;
               if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
                   y1 > seed.minY - 0 && y1 < seed.maxY + 0)
                                             || !isChokePoint(idx, seed)) { // boundary reached
                  count += addQueue(idx, marker, queue, this.pixels);
                  maxX = (x > maxX ? x : maxX);
                  minX = (x < minX ? x : minX);
                  maxY = (y > maxY ? y : maxY);
                  minY = (y < minY ? y : minY);
               }
               else { // boundary not reached
                  edgePixels++;
               }
            }
            else if (table.get(idx) == null) {
               closed++;
               edgePixels++;
            }
            table.put(idx, 1);
         }
      }
      return new RegionInfo(marker, count, minX, maxX, minY, maxY,
                             seed.closed, ((double)closed / (double)edgePixels), seed.rg);
   }

   //called from pickRegions2 to do seed growth preliminarily.
   public RegionInfo seedGrowth4(Seed seed, int radius, float gScore) {
	      int pix, x, y, x1, y1, idx, count, maxX, minX, maxY, minY;
	      int edgePixels = 0;
	      int closed = 0;
	      pix = seed.index;
	      int maxArea = (seed.maxX - seed.minX) * (seed.maxY - seed.minY) * 3;
	      x = y = x1 = y1 = idx = count = 0;
	      ArrayList<Integer> queue = new ArrayList<Integer>();
	      Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
	      x = pix % this.width;
	      y = pix / this.width;
	      maxX = minX = x;
	      maxY = minY = y;

	      idx = width * y + x;
	      count += addQueue(idx, queue);
	      table.put(idx, 1);

	      while(!queue.isEmpty()) {  
	         pix = queue.remove(0);
	         x = pix % width;
	         y = pix / width;
	         // check all 8 neighbors
	         // if the neighbor is 1, mark the pixel with the marker and add the pixel to the queue
	         if (y - 1 >= 0 && x - 1 >= 0 && x - 1 < width) {
	            idx = width * (y - 1) + x - 1;
	            if (table.get(idx) == null && isWhiteSpace3(idx, radius, gScore)) { // check right 1
	               x1 = idx % this.width;
	               y1 = idx / this.width;
	               if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
	                   y1 > seed.minY - 0 && y1 < seed.maxY + 0)
	                                             || !isChokePoint(idx, seed)) { // boundary reached
	                  count += addQueue(idx, queue);
	                  maxX = (x > maxX ? x : maxX);
	                  minX = (x < minX ? x : minX);
	                  maxY = (y > maxY ? y : maxY);
	                  minY = (y < minY ? y : minY);
	               }
	               else { // boundary not reached
	                  edgePixels++;
	               }
	            }
	            else if (table.get(idx) == null) {
	               closed++;
	               edgePixels++;
	            }
	            table.put(idx, 1);
	         }
	         if (y >= 0 && x - 1 >= 0 && x - 1 < width) {
	            idx = width * (y) + x - 1;
	            if (table.get(idx) == null && isWhiteSpace3(idx, radius, gScore)) { // check right 1
	               x1 = idx % this.width;
	               y1 = idx / this.width;
	               if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
	                   y1 > seed.minY - 0 && y1 < seed.maxY + 0)
	                                             || !isChokePoint(idx, seed)) { // boundary reached
	                  count += addQueue(idx, queue);
	                  maxX = (x > maxX ? x : maxX);
	                  minX = (x < minX ? x : minX);
	                  maxY = (y > maxY ? y : maxY);
	                  minY = (y < minY ? y : minY);
	               }
	               else { // boundary not reached
	                  edgePixels++;
	               }
	            }
	            else if (table.get(idx) == null) {
	               closed++;
	               edgePixels++;
	            }
	            table.put(idx, 1);
	         }
	         if (y + 1 >= 0 && x - 1 >= 0 && x - 1 < width) {
	            idx = width * (y + 1) + x - 1;
	            if (table.get(idx) == null && isWhiteSpace3(idx, radius, gScore)) { // check right 1
	               x1 = idx % this.width;
	               y1 = idx / this.width;
	               if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
	                   y1 > seed.minY - 0 && y1 < seed.maxY + 0)
	                                             || !isChokePoint(idx, seed)) { // boundary reached
	                  count += addQueue(idx, queue);
	                  maxX = (x > maxX ? x : maxX);
	                  minX = (x < minX ? x : minX);
	                  maxY = (y > maxY ? y : maxY);
	                  minY = (y < minY ? y : minY);
	               }
	               else { // boundary not reached
	                  edgePixels++;
	               }
	            }
	            else if (table.get(idx) == null) {
	               closed++;
	               edgePixels++;
	            }
	            table.put(idx, 1);
	         }
	         if (y - 1 >= 0 && x >= 0 && x < width) {
	            idx = width * (y - 1) + x;
	            if (table.get(idx) == null && isWhiteSpace3(idx, radius, gScore)) { // check right 1
	               x1 = idx % this.width;
	               y1 = idx / this.width;
	               if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
	                   y1 > seed.minY - 0 && y1 < seed.maxY + 0)
	                                             || !isChokePoint(idx, seed)) { // boundary reached
	                  count += addQueue(idx, queue);
	                  maxX = (x > maxX ? x : maxX);
	                  minX = (x < minX ? x : minX);
	                  maxY = (y > maxY ? y : maxY);
	                  minY = (y < minY ? y : minY);
	               }
	               else { // boundary not reached
	                  edgePixels++;
	               }
	            }
	            else if (table.get(idx) == null) {
	               closed++;
	               edgePixels++;
	            }
	            table.put(idx, 1);
	         }
	         if (y + 1 >= 0 && x >= 0 && x < width) {
	            idx = width * (y + 1) + x;
	            if (table.get(idx) == null && isWhiteSpace3(idx, radius, gScore)) { // check right 1
	               x1 = idx % this.width;
	               y1 = idx / this.width;
	               if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
	                   y1 > seed.minY - 0 && y1 < seed.maxY + 0)
	                                             || !isChokePoint(idx, seed)) { // boundary reached
	                  count += addQueue(idx, queue);
	                  maxX = (x > maxX ? x : maxX);
	                  minX = (x < minX ? x : minX);
	                  maxY = (y > maxY ? y : maxY);
	                  minY = (y < minY ? y : minY);
	               }
	               else { // boundary not reached
	                  edgePixels++;
	               }
	            }
	            else if (table.get(idx) == null) {
	               closed++;
	               edgePixels++;
	            }
	            table.put(idx, 1);
	         }
	         if (y - 1 >= 0 && x + 1 >= 0 && x + 1 < width) {
	            idx = width * (y - 1) + x + 1;
	            if (table.get(idx) == null && isWhiteSpace3(idx, radius, gScore)) { // check right 1
	               x1 = idx % this.width;
	               y1 = idx / this.width;
	               if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
	                   y1 > seed.minY - 0 && y1 < seed.maxY + 0)
	                                             || !isChokePoint(idx, seed)) { // boundary reached
	                  count += addQueue(idx, queue);
	                  maxX = (x > maxX ? x : maxX);
	                  minX = (x < minX ? x : minX);
	                  maxY = (y > maxY ? y : maxY);
	                  minY = (y < minY ? y : minY);
	               }
	               else { // boundary not reached
	                  edgePixels++;
	               }
	            }
	            else if (table.get(idx) == null) {
	               closed++;
	               edgePixels++;
	            }
	            table.put(idx, 1);
	         }
	         if (y >= 0 && x + 1 >= 0 && x + 1 < width) {
	            idx = width * y + x + 1;
	            if (table.get(idx) == null && isWhiteSpace3(idx, radius, gScore)) { // check right 1
	               x1 = idx % this.width;
	               y1 = idx / this.width;
	               if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
	                   y1 > seed.minY - 0 && y1 < seed.maxY + 0)
	                                             || !isChokePoint(idx, seed)) { // boundary reached
	                  count += addQueue(idx, queue);
	                  maxX = (x > maxX ? x : maxX);
	                  minX = (x < minX ? x : minX);
	                  maxY = (y > maxY ? y : maxY);
	                  minY = (y < minY ? y : minY);
	               }
	               else { // boundary not reached
	                  edgePixels++;
	               }
	            }
	            else if (table.get(idx) == null) {
	               closed++;
	               edgePixels++;
	            }
	            table.put(idx, 1);
	         }
	         if (y + 1 >= 0 && x + 1 >= 0 && x + 1 < width) {
	            idx = width * (y + 1) + x + 1;
	            if (table.get(idx) == null && isWhiteSpace3(idx, radius, gScore)) { // check right 1
	               x1 = idx % this.width;
	               y1 = idx / this.width;
	               if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
	                   y1 > seed.minY - 0 && y1 < seed.maxY + 0)
	                                             || !isChokePoint(idx, seed)) { // boundary reached
	                  count += addQueue(idx, queue);
	                  maxX = (x > maxX ? x : maxX);
	                  minX = (x < minX ? x : minX);
	                  maxY = (y > maxY ? y : maxY);
	                  minY = (y < minY ? y : minY);
	               }
	               else { // boundary not reached
	                  edgePixels++;
	               }
	            }
	            else if (table.get(idx) == null) {
	               closed++;
	               edgePixels++;
	            }
	            table.put(idx, 1);
	         }
	      }
	      return new RegionInfo(0, count, minX, maxX, minY, maxY,
	                             seed.closed, ((double)closed / (double)edgePixels), seed.rg);
	   }

   /*public static RegionInfo seedGrowth(int seed, int marker, int width, byte[] blob) {
      int pix, x, y, idx, count, maxX, minX, maxY, minY;
      pix = seed;
      x = y = idx = count = 0;
      ArrayList<Integer> queue = new ArrayList<Integer>();
      x = pix % width;
      y = pix / width;
      maxX = minX = x;
      maxY = minY = y;
      if (y - 1 >= 0 && x - 1 >= 0 && x - 1 < width) {
         idx = width * y + x;
         if (checkFunc.call(idx, blob)) { // check the pixel itself
            //count += addFunc.call(idx, val, queue, blob);
            count += addQueue(idx, queue, marker);
         }
      }

      while(!queue.isEmpty()) {
         pix = queue.remove(0);
         x = pix % width;
         y = pix / width;
         maxX = (x > maxX ? x : maxX);
         minX = (x < minX ? x : minX);
         maxY = (y > maxY ? y : maxY);
         minY = (y < minY ? y : minY);
         // check all 8 neighbors
         // if the neighbor is 1, mark the pixel with the marker and add the pixel to the queue
         if (y - 1 >= 0 && x - 1 >= 0 && x - 1 < width) {
            idx = width * (y - 1) + x - 1;
            if (isWhiteSpace(idx, BDetection.RADIUS)) { // check right 1
               count += addQueue(idx, queue, marker);
            }
         }
         if (y >= 0 && x - 1 >= 0 && x - 1 < width) {
            idx = width * (y) + x - 1;
            if (isWhiteSpace(idx, BDetection.RADIUS)) { // check right 2
               count += addQueue(idx, queue, marker);
            }
         }
         if (y + 1 >= 0 && x - 1 >= 0 && x - 1 < width) {
            idx = width * (y + 1) + x - 1;
            if (isWhiteSpace(idx, BDetection.RADIUS)) { // check right 3
               count += addQueue(idx, queue, marker);
            }
         }
         if (y - 1 >= 0 && x >= 0 && x < width) {
            idx = width * (y - 1) + x;
            if (isWhiteSpace(idx, BDetection.RADIUS)) { // check right 4
               count += addQueue(idx, queue, marker);
            }
         }
         if (y + 1 >= 0 && x >= 0 && x < width) {
            idx = width * (y + 1) + x;
            if (isWhiteSpace(idx, BDetection.RADIUS)) { // check right 5
               count += addQueue(idx, queue, marker);
            }
         }
         if (y - 1 >= 0 && x + 1 >= 0 && x + 1 < width) {
            idx = width * (y - 1) + x + 1;
            if (isWhiteSpace(idx, BDetection.RADIUS)) { // check right 6
               count += addQueue(idx, queue, marker);
            }
         }
         if (y >= 0 && x + 1 >= 0 && x + 1 < width) {
            idx = width * y + x + 1;
            if (isWhiteSpace(idx, BDetection.RADIUS)) { // check right 7
               count += addQueue(idx, queue, marker);
            }
         }
         if (y + 1 >= 0 && x + 1 >= 0 && x + 1 < width) {
            idx = width * (y + 1) + x + 1;
            if (isWhiteSpace(idx, BDetection.RADIUS)) { // check right 8
               count += addQueue(idx, queue, marker);
            }
         }
      }
      return new RegionInfo(marker, count, minX, maxX, minY, maxY);
   }*/

   private double calcAverageBW(RegionInfo region) {
      double bp = 0.0;
      for (RegionInfo r : region.rg.getMembers()) {
         bp += r.pixCount;
      }
      double result = bp / region.pixCount;
      return result;
   }

   private boolean scanVertically(int marker) {
      boolean result = true;
      int whitePerCol = 0;
      for (int i = 0; i < this.width; i++) {
         for (int j = 0; j < this.height; j++) {
            if (this.pixels[3*(j*this.width+i)] == (byte)marker) {
               whitePerCol++;
            }
         }
         if (whitePerCol / this.height > BDetection.MAX_HEIGHT) {
            result = false;
            return result;
         }
         whitePerCol = 0;
      }
      return result;
   }

   private byte[] fillRegions() {
      byte[] blob = new byte[this.height * this.width * 3];
      Color color = new Color();
      int idx = 0;
      int val = 0;
      for(int i = 0; i < this.height * this.width; i++) {
         val = this.pixels[i*3] & 0xff;
         //if (val > 1)
         //   System.out.println("is bubble=" + this.pixCounts.get(val-2).isBubble);
         if (val > 1 && this.pixCounts.get(val-2).isBubble) {
            BDetection.pickColor(val, color);
            blob[i*3] = color.r;
            blob[i*3+1] = color.g;
            blob[i*3+2] = color.b;
            //System.out.println("val=" + val + " and bubble="+this.pixCounts.get(val-2).isBubble);
         }
         else if (val >= 1) {
            BDetection.pickColor(1, color);
            blob[i*3] = color.r;
            blob[i*3+1] = color.g;
            blob[i*3+2] = color.b;
            //System.out.println("val=" + val + " blob=" + (color.r&0xff));
         }
         else {
            blob[i*3] = (byte)0;
            blob[i*3 + 1] = (byte)0;
            blob[i*3 + 2] = (byte)0;
         }
      }
      return blob;
   }

   private byte[] fillRegion(byte[] pixels, int width, int height) {
      byte[] blob = new byte[height * width * 3];
      int idx = 0;
      int val = 0;
      for(int i = 0; i < height * width * 3; i+=3) {
         val = pixels[i] & 0xff;
         if (val >= 1 && val <= BDetection.NUM_SEEDS) {
            blob[i] = (byte)255;
            blob[i+1] = (byte)255;
            blob[i+2] = (byte)255;
         }
         else if (val == 0) {
            blob[i] = 0;
            blob[i+1] = 0;
            blob[i+2] = 0;
         }
         else {
            blob[i] = (byte)255;
            blob[i + 1] = 0;
            blob[i + 2] = 0;
         }
      }
      return blob;
   }

   private byte[] convertToRGB() {
      byte[] blob = new byte[this.height * this.width * 3];
      int idx = 0;
      int val = 0;
      Color color = null;
      Color[] colors = new Color[this.blackRegions.size() + 2];
      for (int i = 0; i < this.blackRegions.size() + 2; i++) {
         colors[i] = pickRandomColor();
      }

      for(int i = 0; i < this.height * this.width; i++) {
         if (this.blackPixels[i] == (byte)0) {
            blob[i*3] = (byte)255;
            blob[i*3+1] = (byte)255;
            blob[i*3+2] = (byte)255;
         }
         else {
            if ((this.blackPixels[i] & 0xff) < 2) {
               blob[i*3] = (byte)0;
               blob[i*3+1] = (byte)0;
               blob[i*3+2] = (byte)0;
            }
            else {
               val = this.blackPixels[i] & 0xff;
               if (this.blackRegions.get(val - 2).type == 1) {
                  //val = this.blackRegions.get(val - 2).textGroup;
                  blob[i*3] = (byte)255;
                  blob[i*3 + 1] = (byte)0;
                  blob[i*3 + 2] = (byte)0;
               }
               else {
                  color = colors[val];
                  blob[i*3] = color.r;
                  blob[i*3 + 1] = color.g;
                  blob[i*3 + 2] = color.b;
               }
            }
         }
      }
      return blob;
   }

   public void writeImage(String fname) {
      byte[] blob = fillRegions();
      try {
         MagickImage blobImage = new MagickImage();
         blobImage.constituteImage(this.width,
                                   this.height,
                                   "RGB",
                                   blob);
         blobImage.setFileName(fname);
         blobImage.writeImage(new ImageInfo());
      } catch (MagickException e) {
         e.printStackTrace();
      }
   }

   public void printBlackPixels() {
      String str = ""; int idx;
      for (int i = 0; i < this.height; i++) {
         str = "";
         for (int j = 0; j < this.width; j++) {
            idx = i * this.width + j;
            str += this.blackPixels[idx];
            str += "|";            
         }
         System.out.println(str);
      }
   }

   private static ArrayList<Frame> getTags(String filename) {
           ArrayList<Frame> frames = new ArrayList<Frame>();
	   try {
		   
		   String line = "";
		   String[] tokens;
		   File file = new File(filename);
		   Scanner sc = new Scanner(file);
		   while (sc.hasNextLine()) {
			   line = sc.nextLine();
			   if (line.length() > 0) {
				   Frame frame;
				   tokens = line.split(";");
				   if (tokens.length == 1) {
					   frame = new Frame(tokens[0]);
					   frames.add(frame);
				   }
				   else if (tokens.length > 1) {
					   frame = new Frame(tokens[0]);
					   for (int i = 1; i < tokens.length; i += 4) {
						   frame.bubbles.add(new Bubble(Integer.parseInt(tokens[i]),
								   Integer.parseInt(tokens[i+1]),
								   Integer.parseInt(tokens[i+2]),
								   Integer.parseInt(tokens[i+3])));
					   }
					   frames.add(frame);
				   }
			   }
		   }
		   
	   }
	   catch (IOException e) {
		   e.printStackTrace();
	   }
       return frames;
   }
   
   private static Frame[] sortFrames(ArrayList<Frame> frames, ArrayList<BDetection> bds) {
	   Frame[] arrframes = new Frame[bds.size()];
	   for (int i=0; i < bds.size(); i++) {
		   //String name = frames.get(i).filename;
		   //name.equals(anObject)
		   if (frames.get(i).filename.equals(bds.get(i).getFilename()))
			   arrframes[i] = frames.get(i);
		   else {
			   int j = 0;
			   while (j < frames.size() && !frames.get(j).filename.equals(bds.get(i).getFilename()))
				   j++;
			   if (j < frames.size())
				   arrframes[i] = frames.get(j);
		   }
	   }
	   return arrframes;
   }
   
   public static void evaluate(String filename, ArrayList<BDetection> bds, boolean textOutput) {
	   int tp = 0;
	   int tn = 0;
	   int fp = 0;
	   int fn = 0;
	   ArrayList<String> falseNegatives = new ArrayList<String>();
	   Frame[] frames = sortFrames(getTags(filename), bds);
	   for (int i=0; i < bds.size(); i++) {
		   
		   ArrayList<Bubble> bubbles = frames[i].bubbles;
		   for (RegionInfo r : bds.get(i).pixCounts) {
			   if (r.isBubble) {
				   int j = 0;
				   boolean cont = true;
				   while (j < bubbles.size() && cont) {
                                   System.out.println("minX=" + r.minX + ", x1=" + bubbles.get(j).x1);
                                   System.out.println("minY=" + r.minY + ", y1=" + bubbles.get(j).y1);
                                   System.out.println("maxX=" + r.maxX + ", x2=" + bubbles.get(j).x2);
                                   System.out.println("maxY=" + r.maxY + ", y2=" + bubbles.get(j).y2);
					   if (r.minX >= bubbles.get(j).x1
							   && r.minY >= bubbles.get(j).y1
							   && r.maxX <= bubbles.get(j).x2
							   && r.maxY <= bubbles.get(j).y2
							   && bubbles.get(j).hit == 0) {
						   bubbles.get(j).hit = 1;
						   tp++;
						  
						   cont = false;
						   if (textOutput)
						      bds.get(i).extractText(r);
					   }
					   else {
						   r.isBubble = false;
					   }
					   j++;
				   }
				   if (cont) {
					   fp++;
				   }
			   }
			   else {
				   tn++;
			   }
		   }
		   
		   for (Bubble b : bubbles) {
			   if (b.hit == 0) {
				   falseNegatives.add(frames[i].filename + ": " + b.x1 + ", " + b.y1 + " - " + b.x2 + ", " + b.y2);
			       fn++;
			   }
		   }
	   }
	   System.out.println("TP=" + tp + ", FP=" + fp + ", FN=" + fn + ", Precision=" + ((double)tp/(double)(tp+fp)));
	   if (falseNegatives.size() > 0) {
		   System.out.println("False Negatives:");
		   for (String str : falseNegatives) {
			   String[] tokens = str.split(": ");
			   String[] xys = tokens[1].split(", ");
			   System.out.println(str);
			   extractFeatureFromRect(bds, tokens[0], Integer.parseInt(xys[0]), Integer.parseInt(xys[1]), Integer.parseInt(xys[2]), Integer.parseInt(xys[3]));
		   }
	   }
   }
   
   public static void extractFeatureFromRect(ArrayList<BDetection> bds, String filename, int x1, int y1, int x2, int y2) {
	   
   }
   
   public static void exportFeatureSet(ArrayList<BDetection> bds, String filename) {
	   PrintWriter writer;
	   String str = "";
	   int idx = 0;
	   try {
		   writer = new PrintWriter(filename, "UTF-8");
		   writer.println("filename, " + RegionInfo.labels());
		   str = "";
		   for (int i=0; i < bds.size(); i++) {
			   for (RegionInfo r : bds.get(i).pixCounts) {
				   writer.println("\""+ bds.get(i).filename + "\", " + r.toCSV());
			   }
		   }
		   writer.close();
	   } catch (FileNotFoundException | UnsupportedEncodingException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
	   }
   }
   
   public static String toJsonString(RegionInfo r) {
	   String str = "{";
	   
	   str += "}";
	   return str;
   }
   
   public static void main(String[] args) {
      //String path = "./TrainingSet/images/";
	  String path = "./image_0670/";
      File dir = new File(path);
      File[] files = dir.listFiles(new FilenameFilter() {
    	    public boolean accept(File directory, String fileName) {
    	        return fileName.endsWith(".jpg");
    	    }
    	});
      ArrayList<BDetection> bds = new ArrayList<BDetection>();
      BDetection bd = null;
      for (int i = 0; i < files.length; i++) {
         System.out.println(files[i].getName());
         bd = BDetection.factory(files[i]);
         //bd.createBWPix();
         bd.createBWPixRankFilter();
         bd.rlsaSmoothing();
         //bd.makeBlackPixels();
         bd.processBlackPixels();
         bd.classifyBlackRegions();
         //bd.groupTexts();
         bd.groupTexts(BDetection.GROUP_TEXT_THRESHOLD);
         //bd.printBlackPixels();
         //bd.writeBlackImage(files[i].getName());
         //bd.pickRegions();
         bd.pickRegions2();
         //bd.testRegions();
         bd.testRegions2();
         bd.writeImage(files[i].getName()); //output
         bds.add(bd);
      }
      //BDetection.evaluate("./TrainingSet/bubbles.txt",bds, true);
      BDetection.evaluate(path+"bubbles.txt",bds, true);
      BDetection.exportFeatureSet(bds, "featureSet3.csv");
   }
}


