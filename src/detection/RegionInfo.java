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
   public int isAreaLessThanMax;
   public int isAreaMoreThanMin;
   public int isXLessThanMax;
   public int isYLessThanMax;
   public int isRegionEnclosed;
   public int isPixCountMoreThanMin;
   public int isDistBtwCentersLessThanMax;
   public double blkPixPercent;
   public double sumPix;
   public double sdHoriz;
   public double sdVert;
   public double sdPix;
   public double percentEnclosed; // percentage enclosed by bubble (black pixels)
   public double distBtwCenters;
   public double percentArea;
   public double bpRatio;
   public boolean isBubble;
   public boolean enclosed;  // whether or not the region is enclosed
   public RegionGroup rg;
   public ArrayList<Integer> pixels;
   public Histogram histV;
   public Histogram histH;
   public Histogram histR;

   public RegionInfo(int m, int n, int x1, int x2, int y1, int y2) {
         this.marker = m;
         this.pixCount = n;
         this.minX = x1;
         this.maxX = x2;
         this.minY = y1;
         this.maxY = y2;
         this.xRange = x2 - x1 + 1;
         this.yRange = y2 - y1 + 1;
         this.isAreaLessThanMax = 0;
         this.isAreaMoreThanMin = 0;
         this.isXLessThanMax = 0;
         this.isYLessThanMax = 0;
         this.isRegionEnclosed = 0;
         this.isPixCountMoreThanMin = 0;
         this.isDistBtwCentersLessThanMax = 0;
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
         this.percentArea = 0.0;
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
         this.isAreaLessThanMax = 0;
         this.isAreaMoreThanMin = 0;
         this.isXLessThanMax = 0;
         this.isYLessThanMax = 0;
         this.isRegionEnclosed = 0;
         this.isPixCountMoreThanMin = 0;
         this.isDistBtwCentersLessThanMax = 0;
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
         this.percentArea = 0.0;
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
         this.isAreaLessThanMax = 0;
         this.isAreaMoreThanMin = 0;
         this.isXLessThanMax = 0;
         this.isYLessThanMax = 0;
         this.isRegionEnclosed = 0;
         this.isPixCountMoreThanMin = 0;
         this.isDistBtwCentersLessThanMax = 0;
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
         this.percentArea = 0.0;
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
       this.isAreaLessThanMax = 0;
       this.isAreaMoreThanMin = 0;
       this.isXLessThanMax = 0;
       this.isYLessThanMax = 0;
       this.isRegionEnclosed = 0;
       this.isPixCountMoreThanMin = 0;
       this.isDistBtwCentersLessThanMax = 0;
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
       this.percentArea = 0.0;
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
       str = str + "\"features\":{";
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
       str = str + "\"percentArea\":" + this.percentArea;// + ", ";
       //str = str + "\"HistR\":" + this.histR.toJsonString() + ", ";
       //str = str + "\"HistH\":" + this.histH.toJsonString() + ", ";
       //str = str + "\"HistV\":" + this.histV.toJsonString();
	   str += "}";
	   str += "}";
	   return str;
   }
   
   public String toCSV() {
	   String str = "";
	   str = str + this.marker + ",";
       str = str + this.isBubble + ", ";
	   str = str + this.minX + ", ";
	   str = str + this.minY + ", ";
	   str = str + this.maxX + ", ";
	   str = str + this.maxY + ", ";
	   str = str + this.pixCount + ", ";
	   str = str + this.xRange + ", ";
	   str = str + this.yRange + ", ";
	   str = str + this.isAreaLessThanMax + ", ";
	   str = str + this.isAreaMoreThanMin + ", ";
	   str = str + this.isXLessThanMax + ", ";
	   str = str + this.isYLessThanMax + ", ";
	   str = str + this.isRegionEnclosed + ", ";
	   str = str + this.isPixCountMoreThanMin + ", ";
	   str = str + this.isDistBtwCentersLessThanMax + ", ";
	   str = str + this.distBtwCenters + ", ";
       str = str + this.blkPixPercent + ", ";
       str = str + this.sumPix + ", ";
       str = str + this.sdHoriz + ", ";
       str = str + this.sdVert + ", ";
       str = str + this.sdPix + ", ";
       str = str + this.enclosed + ", ";
       str = str + this.percentEnclosed + ", ";
       str = str + this.percentArea + ", ";
       str = str + this.histH.toCsvString() + ", ";
       str = str + this.histV.toCsvString() + ", ";
       str = str + this.histR.toCsvString();

	   return str;
   }
   
   public static String labels() {
	   String labels = "";
	   labels = labels + "marker, ";
	   labels = labels + "isBubble, ";
	   labels = labels + "minX, ";
	   labels = labels + "minY, ";
	   labels = labels + "maxX, ";
	   labels = labels + "maxY, ";
	   labels = labels + "pixCount, ";
	   labels = labels + "xRange, ";
	   labels = labels + "yRange, ";
	   labels = labels + "isAreaLessThanMax" + ", ";
	   labels = labels + "isAreaMoreThanMin" + ", ";
	   labels = labels + "isXLessThanMax" + ", ";
	   labels = labels + "isYLessThanMax" + ", ";
	   labels = labels + "isRegionEnclosed" + ", ";
	   labels = labels + "isPixCountMoreThanMin" + ", ";
	   labels = labels + "isDistBtwCentersLessThanMax" + ", ";
	   labels = labels + "distBtwCenters, ";
	   labels = labels + "blkPixPercent, ";
	   labels = labels + "sumPix, ";
	   labels = labels + "sdHoriz, ";
	   labels = labels + "sdVert, ";
	   labels = labels + "sdPix, ";
	   labels = labels + "enclosed, ";
	   labels = labels + "percentEnclosed, ";
	   labels = labels + "percentArea, ";
       //labels = labels + this.histR.toCsvString() + ", ";
	   labels = labels + "histH_1,";
	   labels = labels + "histH_2,";
	   labels = labels + "histH_3,";
	   labels = labels + "histH_4,";
	   labels = labels + "histH_5,";
	   labels = labels + "histH_6,";
	   labels = labels + "histH_7,";
	   labels = labels + "histH_8,";
	   labels = labels + "histH_9,";
	   labels = labels + "histH_10,";
	   labels = labels + "histV_1,";
	   labels = labels + "histV_2,";
	   labels = labels + "histV_3,";
	   labels = labels + "histV_4,";
	   labels = labels + "histV_5,";
	   labels = labels + "histV_6,";
	   labels = labels + "histV_7,";
	   labels = labels + "histV_8,";
	   labels = labels + "histV_9,";
	   labels = labels + "histV_10,";
	   labels = labels + "histR_1,";
	   labels = labels + "histR_2,";
	   labels = labels + "histR_3,";
	   labels = labels + "histR_4,";
	   labels = labels + "histR_5,";
	   labels = labels + "histR_6,";
	   labels = labels + "histR_7,";
	   labels = labels + "histR_8,";
	   labels = labels + "histR_9,";
	   labels = labels + "histR_10,";
	   labels = labels + "histR_11,";
	   labels = labels + "histR_12,";
	   labels = labels + "histR_13,";
	   labels = labels + "histR_14,";
	   labels = labels + "histR_15,";
	   labels = labels + "histR_16,";
	   labels = labels + "histR_17,";
	   labels = labels + "histR_18,";
	   labels = labels + "histR_19,";
	   labels = labels + "histR_20,";
	   labels = labels + "histR_21,";
	   labels = labels + "histR_22,";
	   labels = labels + "histR_23,";
	   labels = labels + "histR_24";

	   return labels;
   }
}

