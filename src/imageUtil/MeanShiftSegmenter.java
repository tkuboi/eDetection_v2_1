package imageUtil;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Hashtable;

import detection.BDetection;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import model.Color;
import model.NaiveBin;

public class MeanShiftSegmenter {
   private static final double THRESHOLD = 0.0001;
   private static final int MAX_ITERATIONS = 100;
   public static byte[] segment(byte[] pixels, int width, int height, double h) {
	   int max = 256 * 256 * 256;
	   int binSize = (int)h;
	   NaiveBin<Integer> bins = binPoints(pixels, width, height, max, binSize);
	   Hashtable<Integer, Integer> hashtable = new Hashtable<Integer, Integer>();
	   ArrayList<Integer> seeds = selectSeeds(bins, hashtable, max, binSize, 1);
	   Color[] centers = meanShift(pixels, seeds, h, bins, binSize);
	   byte[] map = clusterize2(centers, hashtable, bins, pixels, width, height, binSize);
	   return map;
   }
   
   private static Color[] meanShift(byte[] pixels, ArrayList<Integer> seeds, double h, NaiveBin<Integer> bins, int binSize) {
	   Color[] centers = new Color[seeds.size()];
	   double[] new_center = new double[3];
	   double[] old_center = new double[3];
	   int iterations = 0;
	   int r, g, b;
	   boolean converged = false;
	   int size = seeds.size();
	   int idx = 0;
	   for (int i = 0; i < size; i++) {
		   System.out.println("processing " + i + "/" + size + "th pixel..");
		   idx = seeds.get(i);
		   //System.out.println("seed=" + idx);
		   new_center[0] = idx % 256;
		   new_center[2] = (idx - new_center[0]) / (256*256);
		   new_center[1] = ((idx - new_center[0]) % (256 * 256)) / 256;
		   converged = false;
		   iterations = 0;
		   while(!converged) {
			   ArrayList<Color> points = getPoints(pixels, new_center, h, bins, binSize);
			   if (points.size() == 0) {
				   System.out.println("prints.size=0");
				   break;
			   }
			   old_center = new_center;
			   //distance = calcDistance(old_center, points, h);
			   new_center = flatKernelUpdate(old_center, points, h);
			   iterations++;
			   //System.out.println("iterations="+iterations);
			   converged = (isConverged(new_center, old_center, THRESHOLD) || iterations >= MAX_ITERATIONS);
		   }
		   r = (int) new_center[0];
		   g = (int) new_center[1];
		   b = (int) new_center[2];
		   centers[i] = new Color((byte)r, (byte)g, (byte)b);
		   System.out.println("iterations="+iterations);
	   }
	   return centers;
   }
   
   private static ArrayList<Color> getPoints(byte[] pixels, double[] new_center, double h,
		   NaiveBin<Integer> bins, int binSize) {
	   //System.out.println("Searching for points..");
	   //System.out.println("[0]=" + new_center[0] + ", [1]=" + new_center[1] + ", [2]=" + new_center[2]);
	   ArrayList<Color> points = new ArrayList<Color>();
	   int min_r = (int) (new_center[0] - h >= 0 ? new_center[0] - h : 0);
	   int max_r = (int) (new_center[0] + h <= 255 ? new_center[0] + h : 255);	
	   int min_g = (int) (new_center[1] - h >= 0 ? new_center[1] - h : 0);
	   int max_g = (int) (new_center[1] + h <= 255 ? new_center[1] + h : 255);	
	   int min_b = (int) (new_center[2] - h >= 0 ? new_center[2] - h : 0);
	   int max_b = (int) (new_center[2] + h <= 255 ? new_center[2] + h : 255);
	   //System.out.println("min(r,g,b)=" + min_r + ", " + min_g + ", " + min_b);
	   //System.out.println("max(r,g,b)=" + max_r + ", " + max_g + ", " + max_b);
	   int max = (max_r + max_g * 256 + max_b * 256 * 256) / binSize;
	   int min = (min_r + min_g * 256 + min_b * 256 * 256) / binSize;
	   ArrayList<Integer> list = bins.get(min, max);
	   //min = (int)((new_center[0] + new_center[1] * 256 + new_center[2] * 256 * 256) / binSize);
	   //System.out.println("min=" + min + ", max=" + max);
	   //ArrayList<Integer> list = bins.get(min,min+1);
	   for (Integer j : list) {
	       points.add(new Color(pixels[j*3],pixels[j*3+1],pixels[j*3+2]));
	   }
	   //System.out.println(points.size() + " points found!");
	   return points;
   }
   
   private static double[] calcDistance(Color center, ArrayList<Color> points, double h) {
	   double[] result = new double[points.size()];
	   int i = 0;
	   for (Color point : points) {
		   result[i++] = Math.sqrt((center.r - point.r)^2 + (center.g - point.g)^2 + (center.b - point.b)^2);
	   }
	   return result;
   }
  
   private static double calcDiff(double[] new_center, double[] old_center) {
	   return Math.abs(new_center[0] - old_center[0]) + Math.abs(new_center[1] - old_center[1])
			   + Math.abs(new_center[2] - old_center[2]);
   }

   private static double[] flatKernelUpdate(double[] old_center, ArrayList<Color> points, double h) {
	   double[] mean = new double[3];
	   double size = points.size();
	   //System.out.println("size="+size);
	   double sum_r = 0;
	   double sum_g = 0;
	   double sum_b = 0;
	   for (Color point : points) {
		   sum_r = sum_r + (point.r&0xff);
		   sum_g = sum_g + (point.g&0xff);
		   sum_b = sum_b + (point.b&0xff);
		   //System.out.println("("+(point.r&0xff)+", "+(point.g&0xff)+", "+(point.b&0xff)+")");
	   }
	   mean[0] = (sum_r / size);
	   mean[1] = (sum_g / size);
	   mean[2] = (sum_b / size);
	   //System.out.println("("+sum_r+", "+sum_g+", "+sum_b+")");
	   return mean;
   }
   
   private static boolean isConverged(double[] new_center, double[] old_center, double threshold) {
	   boolean converged = false;
	   if (calcDiff(new_center, old_center) <= threshold)
		   converged = true;
	   return converged;
   }
   
   private static byte[] clusterize(Color[] centers, ArrayList<Integer> seeds, NaiveBin<Integer> bins, byte[] pixels, int width, int height, int binSize) {
	   byte[] map = new byte[width * height * 3];
	   int[] table = new int[width * height];
	   int idx = 0;
	   int bin = 0;
	   for (int i = 0; i < centers.length; i++) {
		   idx = seeds.get(i);
		   bin = ((pixels[idx*3]&0xff) + (pixels[idx*3+1]&0xff)*256 + (pixels[idx*3+2]&0xff)*256*256)/binSize;
		   for (Object o : bins.get(bin)) {
			   map[(Integer)o*3] = centers[i].r;
			   map[(Integer)o*3+1] = centers[i].g;
			   map[(Integer)o*3+2] = centers[i].b;
			   table[(Integer)o] = 1;
		   }
	   }
	   int count = 0;
	   for (int i = 0; i < width * height; i++) {
		   if (table[i] != 1) {
			   count++;
		   }
	   }
	   System.out.println("count of posssible empty data=" + count);
	   return map;
   }
   
   private static byte[] clusterize2(Color[] centers, Hashtable<Integer, Integer> hashtable, NaiveBin<Integer> bins, byte[] pixels, int width, int height, int binSize) {
	   byte[] map = new byte[width * height * 3];
	   int idx = 0;
	   int bin = 0;
	   for (int i = 0; i < width * height; i++) {
		   //to-do: map pixel -> centers -> map
		   bin = ((pixels[i*3]&0xff) + (pixels[i*3+1]&0xff)*256 + (pixels[i*3+2]&0xff)*256*256)/binSize;
		   idx = hashtable.get(bin);
		   map[i*3] = centers[idx].r;
		   map[i*3+1] = centers[idx].g;
		   map[i*3+2] = centers[idx].b;
	   }
	   return map;
   }
   
   private static NaiveBin<Integer> binPoints(byte[] points, int width, int height, int max, int binSize) {
	   int size = 1 + max / binSize;
	   System.out.println("max=" + max + ", binSize=" + binSize + ", size=" + size);
	   NaiveBin<Integer> bins = new NaiveBin<Integer>(size);
	   int val = 0;
	   for (int i = 0; i < width * height; i++) {
		   val = ((points[i*3] & 0xff) + (points[i*3+1] & 0xff) * 256 + (points[i*3+2] & 0xff) * 256 * 256) / binSize;
		   bins.put(i, val);
	   }
	   return bins;
   }
   
   private static ArrayList<Integer> selectSeeds(NaiveBin<Integer> bins, Hashtable<Integer, Integer> hashtable, int max, int binSize, int minFreq) {
	   ArrayList<Integer> seeds = new ArrayList<Integer>();
	   int count = 0;
	   for (int i = 0; i < bins.getNumBins(); i++) {
		   if (bins.get(i).length >= minFreq) {
			   seeds.add(i * binSize);
			   hashtable.put(i, count);
			   count++;
		   }
	   }
	   return seeds;
   }
   
   public static void main(String[] args) {
	   String filename = "COPACABANA_2009.jpg";
	   filename = args[0];
	   String output = "segmented.jpg";
	   double h = 10.0;
	   h = Double.parseDouble(args[1]);
	      try {
	          ImageInfo ii = new ImageInfo(filename); 
	          MagickImage image = new MagickImage(ii);
	          Dimension dimensions = image.getDimension();
	          byte[] pixels = new byte[dimensions.width*dimensions.height*3];
	          image.dispatchImage(0, 0,
                      dimensions.width, dimensions.height,
                      "RGB",
                      pixels);
	          byte[] result = MeanShiftSegmenter.segment(pixels, dimensions.width, dimensions.height, h);
	          MagickImage blobImage = new MagickImage();
	          blobImage.constituteImage(dimensions.width,
	                                    dimensions.height,
	                                    "RGB",
	                                    result);
	          blobImage.setFileName(output);
	          blobImage.writeImage(new ImageInfo());

	       } 
	       catch(MagickException e) {
	          e.printStackTrace();
	       }

   }
}
