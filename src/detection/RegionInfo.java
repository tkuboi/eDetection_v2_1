package detection;

import java.util.*;
import java.awt.Point;

import model.Histogram;
import model.Histogram2D;

public class RegionInfo {
   public int marker;
   public int pixCount;
   public int minX;
   public int maxX;
   public int minY;
   public int maxY;
   public int xRange;
   public int yRange;
   public int type; // 0:unknown, 1:text, 2:line
   public int textGroup;
   public double blkPixPercent;
   public double sumPix;
   public double sdHoriz;
   public double sdVert;
   public double sdPix;
   public double percentEnclosed; // percentage enclosed by bubble (black pixels)
   public double distBtwCenters;
   public boolean isBubble;
   public boolean enclosed;  // whether or not the region is enclosed
   public RegionGroup rg;
   public ArrayList<Integer> pixels;
   public Histogram histV;
   public Histogram histH;
   public Histogram2D histR;

   public RegionInfo(int m, int n, int x1, int x2, int y1, int y2) {
         this.marker = m;
         this.pixCount = n;
         this.minX = x1;
         this.maxX = x2;
         this.minY = y1;
         this.maxY = y2;
         this.xRange = x2 - x1 + 1;
         this.yRange = y2 - y1 + 1;
         this.blkPixPercent = 0.0;
         this.sumPix = 0.0;
         this.sdHoriz = 0.0;
         this.sdVert = 0.0;
         this.sdPix = 0.0;
         this.isBubble = false;
         this.type = 0;
         this.textGroup = -1;
         this.enclosed = false;
         this.percentEnclosed = 0.0;
         this.distBtwCenters = 0.0;
         this.rg = null;
         this.pixels = new ArrayList<Integer>();
         this.histH = null;
         this.histV = null;
         this.histR = null;
   }

   public RegionInfo(int m, int n, int x1, int x2, int y1, int y2, boolean closed, double ptClosed) {
         this.marker = m;
         this.pixCount = n;
         this.minX = x1;
         this.maxX = x2;
         this.minY = y1;
         this.maxY = y2;
         this.xRange = x2 - x1 + 1;
         this.yRange = y2 - y1 + 1;
         this.blkPixPercent = 0.0;
         this.sumPix = 0.0;
         this.sdHoriz = 0.0;
         this.sdVert = 0.0;
         this.sdPix = 0.0;
         this.isBubble = false;
         this.type = 0;
         this.textGroup = -1;
         this.enclosed = closed;
         this.percentEnclosed = ptClosed;
         this.distBtwCenters = 0.0;
         this.rg = null;
         this.pixels = new ArrayList<Integer>();
         this.histH = null;
         this.histV = null;
         this.histR = null;
   }

   public RegionInfo(int m, int n, int x1, int x2, int y1, int y2, boolean closed, double ptClosed, RegionGroup g) {
         this.marker = m;
         this.pixCount = n;
         this.minX = x1;
         this.maxX = x2;
         this.minY = y1;
         this.maxY = y2;
         this.xRange = x2 - x1 + 1;
         this.yRange = y2 - y1 + 1;
         this.blkPixPercent = 0.0;
         this.sumPix = 0.0;
         this.sdHoriz = 0.0;
         this.sdVert = 0.0;
         this.sdPix = 0.0;
         this.isBubble = false;
         this.type = 0;
         this.textGroup = -1;
         this.enclosed = closed;
         this.percentEnclosed = ptClosed;
         this.rg = g;
         this.distBtwCenters = calcDist();
         this.pixels = new ArrayList<Integer>();
         this.histH = null;
         this.histV = null;
         this.histR = null;
   }

   public RegionInfo(int m, int n, int x1, int x2, int y1, int y2, ArrayList<Integer> pixels) {
       this.marker = m;
       this.pixCount = n;
       this.minX = x1;
       this.maxX = x2;
       this.minY = y1;
       this.maxY = y2;
       this.xRange = x2 - x1 + 1;
       this.yRange = y2 - y1 + 1;
       this.blkPixPercent = 0.0;
       this.sumPix = 0.0;
       this.sdHoriz = 0.0;
       this.sdVert = 0.0;
       this.sdPix = 0.0;
       this.isBubble = false;
       this.type = 0;
       this.textGroup = -1;
       this.enclosed = false;
       this.percentEnclosed = 0.0;
       this.rg = null;
       this.distBtwCenters = 0.0;
       this.pixels = pixels;
       this.histH = null;
       this.histV = null;
       this.histR = null;
 }

   public double calcDist() {
      double dist = 0.0;
      if (this.rg == null)
         return dist;
      double cX1 = (double)(this.minX + this.maxX) / 2;
      double cY1 = (double)(this.minY + this.maxY) / 2;
      double cX2 = (double)(this.rg.minX + this.rg.maxX) / 2;
      double cY2 = (double)(this.rg.minY + this.rg.maxY) / 2;
      dist = (cX1 - cX2) * (cX1 - cX2) + (cY1 - cY2) * (cY1 - cY2);
      System.out.println("dist=" + dist);
      return Math.sqrt(dist);
   }

   public String toString() {
      String str = "";
      str = this.marker + ": ";
      str = str + "pixCount=" + this.pixCount + ", ";
      str = str + "xRange=" + this.xRange + ", ";
      str = str + "yRange=" + this.yRange + ", ";
      str = str + "distBtwCenters=" + this.distBtwCenters + ",";
      str = str + "type=" + this.type;
      return str;
   }
   
   public String toJsonString() {
	   String str = "{";
	   str = str + "\"marker\":" + this.marker + ",";
       str = str + "\"isBubble\":" + this.isBubble + ", ";
	   str = str + "\"pixCount\":" + this.pixCount + ", ";
	   str = str + "\"minX\":" + this.minX + ", ";
	   str = str + "\"minY\":" + this.minY + ", ";
	   str = str + "\"maxX\":" + this.maxX + ", ";
	   str = str + "\"maxY\":" + this.maxY + ", ";
	   str = str + "\"xRange\":" + this.xRange + ", ";
	   str = str + "\"yRange\":" + this.yRange + ", ";
	   str = str + "\"distBtwCenters\":" + this.distBtwCenters + ", ";
       str = str + "\"blkPixPercent\":" + this.blkPixPercent + ", ";
       str = str + "\"sumPix\":" + this.sumPix + ", ";
       str = str + "\"sdHoriz\":" + this.sdHoriz + ", ";
       str = str + "\"sdVert\":" + this.sdVert + ", ";
       str = str + "\"sdPix\":" + this.sdPix + ", ";
       str = str + "\"enclosed\":" + this.enclosed + ", ";
       str = str + "\"percentEnclosed\":" + this.percentEnclosed + ", ";
       str = str + "\"HistH\":" + this.histH.toJsonString() + ", ";
       str = str + "\"HistV\":" + this.histV.toJsonString();
	   str += "}";
	   return str;
   }
}

