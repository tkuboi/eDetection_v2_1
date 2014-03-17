package imageUtil;

import java.awt.Dimension;
import java.util.ArrayList;

import detection.BDetection;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import model.Color;
import model.NaiveBin;

public class MeanShiftSegmenter {
   private static final double THRESHOLD = 1.0;
   private static final int MAX_ITERATIONS = 20;
   public static byte[] segment(byte[] pixels, int width, int height, double h) {
	   NaiveBin<Integer> bins_r = new NaiveBin<Integer>(256);
	   NaiveBin<Integer> bins_g = new NaiveBin<Integer>(256);
	   NaiveBin<Integer> bins_b = new NaiveBin<Integer>(256);
	   for (int i = 0; i < width*height; i++) {
		   bins_r.put(i, (pixels[i*3]&0xff));
		   bins_g.put(i, (pixels[i*3+1]&0xff));
		   bins_b.put(i, (pixels[i*3+2]&0xff));
	   }
	   Color[] centers = meanShift(pixels, width, height, h, bins_r, bins_g, bins_b);
	   byte[] map = clusterize(centers, width, height);
	   return map;
   }
   
   private static Color[] meanShift(byte[] pixels, int width, int height, double h,
		   NaiveBin<Integer> bins_r, NaiveBin<Integer> bins_g, NaiveBin<Integer> bins_b) {
	   Color[] centers = new Color[width * height];
	   Color new_center;
	   Color old_center;
	   int iterations = 0;
	   double distance;
	   boolean converged = false;
	   for (int i = 0; i < width*height; i++) {
		   new_center = new Color(pixels[i*3], pixels[i*3+1], pixels[i*3+2]);
		   converged = false;
		   iterations = 0;
		   while(!converged) {
			   ArrayList<Color> points = getPoints(pixels, new_center, h, bins_r, bins_g, bins_b);
			   if (points.size() == 0)
				   break;
			   old_center = new_center;
			   //distance = calcDistance(old_center, points, h);
			   new_center = flatKernelUpdate(old_center, points, h);
			   iterations++;
			   //System.out.println("iterations="+iterations);
			   converged = (isConverged(new_center, old_center, THRESHOLD) || iterations >= MAX_ITERATIONS);
		   }
		   centers[i] = new_center;
		   System.out.println("iterations="+iterations);
	   }
	   return centers;
   }
   
   private static ArrayList<Color> getPoints(byte[] pixels, Color center, double h,
		   NaiveBin<Integer> bins_r, NaiveBin<Integer> bins_g, NaiveBin<Integer> bins_b) {
	   ArrayList<Color> points = new ArrayList<Color>();
	   int min_r = (int) (center.r - h >= 0 ? center.r - h : 0);
	   int max_r = (int) (center.r + h);	
	   ArrayList<Integer> list_r = bins_r.get(min_r, max_r);
	   int min_g = (int) (center.g - h >= 0 ? center.g - h : 0);
	   int max_g = (int) (center.g + h);	
	   ArrayList<Integer> list_g = bins_g.get(min_g, max_g);
	   int min_b = (int) (center.b - h >= 0 ? center.b - h : 0);
	   int max_b = (int) (center.b + h);	
	   ArrayList<Integer> list_b = bins_b.get(min_b, max_b);
	   
	   for (Integer j : list_r) {
		   if (list_g.contains(j) && list_b.contains(j))
			   points.add(new Color(pixels[j*3],pixels[j*3+1],pixels[j*3+2]));
	   }
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
  
   private static double calcDiff(Color new_center, Color old_center) {
	   double result = (new_center.r - old_center.r)^2 + (new_center.g - old_center.g)^2
			   + (new_center.b - old_center.b)^2;
	   return Math.sqrt(result);
   }

   private static Color flatKernelUpdate(Color center, ArrayList<Color> points, double h) {
	   Color mean = new Color();
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
	   mean.r = (byte)(sum_r / size);
	   mean.g = (byte)(sum_g / size);
	   mean.b = (byte)(sum_b / size);
	   //System.out.println("("+sum_r+", "+sum_g+", "+sum_b+")");
	   return mean;
   }
   
   private static boolean isConverged(Color new_center, Color old_center, double threshold) {
	   boolean converged = false;
	   if (calcDiff(new_center, old_center) <= threshold)
		   converged = true;
	   return converged;
   }
   
   private static byte[] clusterize(Color[] centers, int width, int height) {
	   byte[] map = new byte[width * height * 3];
	   for (int i = 0; i < width*height; i++) {
		   map[i*3] = centers[i].r;
		   map[i*3+1] = centers[i].g;
		   map[i*3+2] = centers[i].b;
	   }
	   return map;
   }
   
   public static void main(String[] args) {
	   String filename = "COPACABANA_2009.jpg";
	   String output = "segmented.jpg";
	   double h = 3.0;
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
