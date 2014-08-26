package detection;

import imageUtil.CannyEdgeDetector;
import imageUtil.Convolution;
import imageUtil.GradInfo;
import model.Histogram;

public class EyePair extends RegionGroup
{
	public double area;
	public double diffArea;
	public double diffX1;
	public double diffX2;
	public double diffY1;
	public double diffY2;
	public double distX;
	public double distY;
	public double distCenters;
	public double distCorners;
	public boolean isInsideRegion;
	public boolean isEyes;
	public Histogram sumHistH;
	public Histogram sumHistV;
	public Histogram sumHistR;
	public Histogram diffHistH;
	public Histogram diffHistV;
	public Histogram diffHistR;
	public double diffDistBtwCenters;
	public double diffPercentArea;
	public double diffBpRatio;
	public double avPercentArea;
	public double avBpRatio;
	public int enumEnclosed;  // whether or not the region is enclosed
	public int topLeft; //0, 1, or 2
	public int topRight;
	public int bottomLeft;
	public int bottomRight;
	public int top;
	public int bottom;
	public int left;
	public int right;
	public int xOverlap; //0:no, 1:less than 50%, 2:more
	public int yOverlap;
	public Histogram edgeMap;
	public Histogram atanHistH45;
	public Histogram atanHistH90;
	public Histogram atanHistH135;
	public Histogram atanHistH0;
	public Histogram atanHistV45;
	public Histogram atanHistV90;
	public Histogram atanHistV135;
	public Histogram atanHistV0;
	public int atan0;
	public int atan45;
	public int atan90;
	public int atan135;
	
	
	public EyePair(int id) {
		super(id);
	}

	public EyePair(int id, RegionInfo region1, RegionInfo region2) {
		super(id);
		
		isInsideRegion = false;
		isEyes = false;

		this.addMember(region1);
		this.addMember(region2);
        //computeCombinedFeatures();
	}
	
	public void setCombinedFeatures(byte[] pixels, int width, int height) {
		if (this.getMembers().size() < 2) {
			System.out.println("not enough members registered!");
			return;
		}
		RegionInfo region1 = this.getMembers().get(0);
		RegionInfo region2 = this.getMembers().get(1);
		double area1 = Math.abs((double)(region1.maxX - region1.minX) * (double)(region1.maxY - region1.minY));
		double area2 = Math.abs((double)(region2.maxX - region2.minX) * (double)(region2.maxY - region2.minY));
		diffArea = area1 > 0 && area2 > 0 ? Math.min(area1,area2) / Math.max(area1,area2) : 0;
		diffX1 = Math.abs(((double)(region1.minX - region2.minX)) / (double)width);
		diffX1 = discretize(diffX1, 0.001);
		diffX2 = Math.abs(((double)(region1.maxX - region2.maxX)) / (double)width);
		diffX2 = discretize(diffX2, 0.001);
		diffY1 = Math.abs(((double)(region1.minY - region2.minY)) / (double)height);
		diffY1 = discretize(diffY1, 0.001);
		diffY2 = Math.abs(((double)(region1.maxY - region2.maxY)) / (double)height);
		diffY2 = discretize(diffY2, 0.001);
		distX = Math.abs((region1.maxX + region1.minX) / 2 - (region2.maxX + region2.minX) / 2) / (double)width;
		distX = discretize(distX, 0.001);
		distY = Math.abs((region1.maxY + region1.minY) / 2 - (region2.maxY + region2.minY) / 2) / (double)height;
		distY = discretize(distY, 0.001);
		distCenters = (double)(distX * distX + distY * distY) / (double)(width * width + height * height);
		//distCenters = discretize(distCenters, 0.00000001);
		minX = (region1.minX <= region2.minX ? region1.minX : region2.minX);
		minY = (region1.minY <= region2.minY ? region1.minY : region2.minY);
		maxX = (region1.maxX >= region2.maxX ? region1.maxX : region2.maxX);
		maxY = (region1.maxY >= region2.maxY ? region1.maxY : region2.maxY);
		area = (double)(maxX- minX) * (maxY - minY) / (double)(width * height);
		area = discretize(area, 0.0001);
		if (region1.maxX < region2.minX || region2.maxX < region1.minX)
			xOverlap = 0;
		else if (maxX - minX > (region1.xRange + region2.xRange) * 0.5)
			xOverlap = 1;
		else
			xOverlap = 2;

		if (region1.maxY < region2.minY || region2.maxY < region1.minY)
			yOverlap = 0;
		else if (maxY - minY > (region1.yRange + region2.yRange) * 0.5)
			yOverlap = 1;
		else
			yOverlap = 2;

		diffDistBtwCenters = (region1.distBtwCenters + region2.distBtwCenters > 0 ? (float)(region1.distBtwCenters - region2.distBtwCenters) / ((float)(region1.distBtwCenters + region2.distBtwCenters) / 2.0f) : 0 );
		diffPercentArea = region1.percentArea > 0 && region2.percentArea > 0 ? Math.min(region1.percentArea, region2.percentArea) / Math.max(region1.percentArea, region2.percentArea) : 0;
		diffBpRatio = region1.bpRatio > 0 && region2.bpRatio > 0 ? Math.min(region1.bpRatio, region2.bpRatio) / Math.max(region1.bpRatio, region2.bpRatio) : 0;
		avPercentArea = (region1.percentArea + region2.percentArea) / 2.0;
		avBpRatio = (region1.bpRatio + region2.bpRatio) / 2.0;
		
		if (region1.enclosed && region2.enclosed)
			enumEnclosed = 2;
		else if (region1.enclosed || region2.enclosed)
		    enumEnclosed = 1;
		else
			enumEnclosed = 0;
		    	
		
		distCorners = 0;
		if (Math.abs(region1.minX - region2.minX) + Math.abs(region1.minY - region2.minY)
				<= Math.abs(region1.maxX - region2.maxX) + Math.abs(region1.maxY - region2.maxY))
			distCorners = Math.abs(region1.minX - region2.minX) + Math.abs(region1.minY - region2.minY);
		else
			distCorners = Math.abs(region1.maxX - region2.maxX) + Math.abs(region1.maxY - region2.maxY);
		//distCorners = discretize(distCorners, 1.0);
		
		setEdgeFeatures(pixels, width, height, region1, region2);

	}
	
	private void setEdgeFeatures(byte[] pixels, int width, int height, RegionInfo region1, RegionInfo region2) {
		this.topLeft = 0;
		if ((pixels[(region1.minY * width + region1.minX)*3] & 0xFF) == region1.marker)
			this.topLeft++;
		if ((pixels[(region2.minY * width + region2.minX)*3] & 0xFF) == region2.marker)
			this.topLeft++;
		
		this.topRight = 0;
		if ((pixels[(region1.minY * width + region1.maxX)*3] & 0xFF) == region1.marker)
			this.topRight++;
		if ((pixels[(region2.minY * width + region2.maxX)*3] & 0xFF) == region2.marker)
			this.topRight++;

		this.bottomLeft = 0;
		if ((pixels[(region1.maxY * width + region1.minX)*3] & 0xFF) == region1.marker)
			this.bottomLeft++;
		if ((pixels[(region2.maxY * width + region2.minX)*3] & 0xFF) == region2.marker)
			this.bottomLeft++;

		this.bottomRight = 0;
		if ((pixels[(region1.maxY * width + region1.maxX)*3] & 0xFF) == region1.marker)
			this.bottomRight++;
		if ((pixels[(region2.maxY * width + region2.maxX)*3] & 0xFF) == region2.marker)
			this.bottomRight++;

		this.top = 0;
		int median1 = (region1.maxX + region1.minX) / 2;
		if ((pixels[(region1.minY * width + median1)*3] & 0xFF) == region1.marker
				|| (pixels[(region1.minY * width + median1 - 1)*3] & 0xFF) == region1.marker
				|| (pixels[(region1.minY * width + median1 + 1)*3] & 0xFF) == region1.marker)
			this.top++;
		
		int median2 = (region2.maxX + region2.minX) / 2;
		if ((pixels[(region2.minY * width + median2)*3] & 0xFF) == region2.marker
				|| (pixels[(region2.minY * width + median2 - 1)*3] & 0xFF) == region2.marker
				|| (pixels[(region2.minY * width + median2 + 1)*3] & 0xFF) == region2.marker)
			this.top++;	
		
		this.bottom = 0;
		if ((pixels[(region1.maxY * width + median1)*3] & 0xFF) == region1.marker
				|| (pixels[(region1.maxY * width + median1 - 1)*3] & 0xFF) == region1.marker
				|| (pixels[(region1.maxY * width + median1 + 1)*3] & 0xFF) == region1.marker)
			this.bottom++;
		
		if ((pixels[(region2.maxY * width + median2)*3] & 0xFF) == region2.marker
				|| (pixels[(region2.maxY * width + median2 - 1)*3] & 0xFF) == region2.marker
				|| (pixels[(region2.maxY * width + median2 + 1)*3] & 0xFF) == region2.marker)
			this.bottom++;		

		this.left = 0;
		median1 = (region1.minY + region1.maxY) / 2;
		if ((pixels[(median1 * width + region1.minX)*3] & 0xFF) == region1.marker
				|| (pixels[(median1 * width + region1.minX - 1)*3] & 0xFF) == region1.marker
				|| (pixels[(median1 * width + region1.minX + 1)*3] & 0xFF) == region1.marker)
			this.left++;
		
		median2 = (region2.minY + region2.maxY) / 2;
		if ((pixels[(median2 * width + region2.minX)*3] & 0xFF) == region2.marker
				|| (pixels[(median2 * width + region2.minX - 1)*3] & 0xFF) == region2.marker
				|| (pixels[(median2 * width + region2.minX + 1)*3] & 0xFF) == region2.marker)
			this.left++;		

		this.right = 0;
		if ((pixels[(median1 * width + region1.maxX)*3] & 0xFF) == region1.marker
				|| (pixels[(median1 * width + region1.maxX - 1)*3] & 0xFF) == region1.marker
				|| (pixels[(median1 * width + region1.maxX + 1)*3] & 0xFF) == region1.marker)
			this.right++;
		
		if ((pixels[(median2 * width + region2.maxX)*3] & 0xFF) == region2.marker
				|| (pixels[(median2 * width + region2.maxX - 1)*3] & 0xFF) == region2.marker
				|| (pixels[(median2 * width + region2.maxX + 1)*3] & 0xFF) == region2.marker)
			this.right++;		
	}
	
	public void computeHists() {
		if (this.getMembers().size() < 2) {
			System.out.println("not enough members registered!");
			return;
		}
		RegionInfo region1 = this.getMembers().get(0);
		RegionInfo region2 = this.getMembers().get(1);

		sumHistH = sumHists(region1.histH, region2.histH);
		sumHistV = sumHists(region1.histV, region2.histV);
		sumHistR = sumHists(region1.histR, region2.histR);
		diffHistH = diffHists(region1.histH, region2.histH);
		diffHistV = diffHists(region1.histV, region2.histV);
		diffHistR = diffHists(region1.histR, region2.histR);
	}
	
	private static Histogram sumHists(Histogram hist1, Histogram hist2) {
		Histogram sumHist = new Histogram(hist1.getSize(),hist1.getMin(),hist1.getMax());
		int[] bins1 = hist1.getBins();
		int[] bins2 = hist2.getBins();
		for (int i = 0; i < hist1.getSize(); i++)
			sumHist.bin(i, (bins1[i] + bins2[i] > 0 ? 1 : 0));
		return sumHist;
	}

	private static Histogram diffHists(Histogram hist1, Histogram hist2) {
		Histogram diffHist = new Histogram(hist1.getSize(),hist1.getMin(),hist1.getMax());
		int[] bins1 = hist1.getBins();
		int[] bins2 = hist2.getBins();
		for (int i = 0; i < hist1.getSize(); i++)
			diffHist.bin(i, (bins1[i] > 0 && bins2[i] > 0 ? 2 : (bins1[i] + bins2[i] > 0 ? 1 : 0)));
		return diffHist;
	}
	
	public void setEdgeMap(byte[] pixels, int width, int height) {
		int x1 = minX - 3 >= 0 ? minX - 3 : 0;
		int y1 = minY - 3 >= 0 ? minY - 3 : 0;
		int x2 = maxX + 3 <= width ? maxX + 3 : width;
		int y2 = maxY + 3 <= height ? maxY + 3 :height;
		byte[] subRegion = new byte[(x2 - x1 + 1) * (y2 - y1 + 1)];
		subRegion = getSubBlob(reduceChannel(pixels, width, height),width,x1,y1,x2,y2);

		edgeMap = new Histogram(100, 0, (x2 - x1 + 1) * (y2 - y1 + 1));
		GradInfo[] gradInfo = CannyEdgeDetector.genGradInfo(subRegion, (x2 - x1 + 1), (y2 - y1 + 1), 1);
		byte[] edges = CannyEdgeDetector.suppressNonMaxima(gradInfo, (x2 - x1 + 1), (y2 - y1 + 1));
		for (int i = 0; i < edges.length; i++)
			edgeMap.binWithValueAdd(i, edges[i] & 0xff);
		
		atanHistH0 = new Histogram(10,0,(y2 - y1 + 1));
		atanHistH45 = new Histogram(10,0,(y2 - y1 + 1));
		atanHistH90 = new Histogram(10,0,(y2 - y1 + 1));
		atanHistH135 = new Histogram(10,0,(y2 - y1 + 1));
		atanHistV0 = new Histogram(10,0,(x2 - x1 + 1));
		atanHistV45 = new Histogram(10,0,(x2 - x1 + 1));
		atanHistV90 = new Histogram(10,0,(x2 - x1 + 1));
		atanHistV135 = new Histogram(10,0,(x2 - x1 + 1));
		
		atan0 = atan45 = atan90 = atan135 = 0;

		int idx = 0;
		for (int row = 0; row < (y2 - y1 + 1); row++) {
			for (int col = 0; col < (x2 - x1 + 1); col++) {
				idx = row * (x2 - x1 + 1) + col;
				if (gradInfo[idx].grad > 0) {
					double atan = gradInfo[idx].atan * 180 / Math.PI;
					//System.out.println(atan);
					if ((atan > -20  && atan < 20)) { // top - bot
						//check 2 neighbors
						atanHistH0.bin(row);
						atanHistV0.bin(col);
						atan0 += 1;
					}
					else if ((atan >= 20 && atan < 70)
							|| (atan <= -20 && atan > -70)) { //bot L - top R
						//check 2 neighbors
						atanHistH45.bin(row);
						atanHistV45.bin(col);
						atan45 += 1;
					}
					else if ((atan >= 70 && atan < 110)
							|| (atan <= -70 && atan > -110)) { //L - R
						//check 2 neighbors
						atanHistH90.bin(row);
						atanHistV90.bin(col);
						atan90 += 1;
					}
					else if ((atan >= 110 && atan < 160)
							|| (atan <= -110 && atan > -160)) { //top L - bot R
						//check 2 neighbors
						atanHistH135.bin(row);
						atanHistV135.bin(col);
						atan135 += 1;
					}
					else { //top - bot
						//check 2 neighbors
						atanHistH0.bin(row);
						atanHistV0.bin(col);
						atan0 += 1;
					}
				}
			}
		}

		atan0 = discretize(atan0, 10);
		atan45 = discretize(atan45, 10);
		atan90 = discretize(atan90, 10);
		atan135 = discretize(atan135, 10);
	}

	public static byte[] getSubBlob(byte[] pixels, int width, int x1, int y1, int x2, int y2) {
		byte[] subBlob = new byte[(x2 - x1 + 1) * (y2 - y1 + 1)];
		int idx = 0;
		int i = 0;
		for (int row = y1; row < y2; row++) {
			for (int col = x1; col < x2; col++) {
				idx = row*width + col;
				subBlob[i++] = pixels[idx];
			}
		}
		return subBlob;
	}
	
	public static byte[] reduceChannel(byte[] img, int width, int height) {
		byte[] result = new byte[width * height];
		int idx = 0;
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				idx = row * width + col;
				result[idx] = ((img[idx*3] & 0xff) > 0 ? (byte)1 : (byte)0);
			}
		}
		return result;
	}
	
	public static int discretize(double val, double denom) {
		return (int)(val / denom);
	}

	@Override
	public String toCSV() {
		StringBuilder sb = new StringBuilder();
		sb.append((this.isEyes ? 1 : 0) + ",");
		sb.append(this.minX + ",");
		sb.append(this.minY + ",");
		sb.append(this.maxX + ",");
		sb.append(this.maxY + ",");
		sb.append(this.diffArea + ",");
		sb.append(this.diffX1 + ",");
		sb.append(this.diffX2 + ",");
		sb.append(this.diffY1 + ",");
		sb.append(this.diffY2 + ",");
		sb.append(this.distCenters + ",");
		sb.append(this.distCorners + ",");
		sb.append(this.distX + ",");
		sb.append(this.distY + ",");
		sb.append((this.isInsideRegion ? 1 : 0) + ",");

		sb.append(this.sumHistH.toCsvStringDiscretized(10) + ",");
		sb.append(this.sumHistV.toCsvStringDiscretized(10) + ",");
		sb.append(this.sumHistR.toCsvStringDiscretized(10) + ",");

		sb.append(this.diffHistH.toCsvString() + ",");
		sb.append(this.diffHistV.toCsvString() + ",");
		sb.append(this.diffHistR.toCsvString() + ",");
		
		sb.append(this.edgeMap.toCsvStringDiscretized(10) + ",");
		
		sb.append(this.diffDistBtwCenters + ",");
		sb.append(this.diffPercentArea + ",");
		sb.append(this.diffBpRatio + ",");
		sb.append(this.avPercentArea + ",");
		sb.append(this.avBpRatio + ",");
        sb.append(this.enumEnclosed + ",");		
        sb.append(this.topLeft + ",");		
        sb.append(this.topRight + ",");		
        sb.append(this.bottomLeft + ",");		
        sb.append(this.bottomRight + ",");		
        sb.append(this.left + ",");		
        sb.append(this.right + ",");		
        sb.append(this.top + ",");		
        sb.append(this.bottom + ",");		
        sb.append(this.xOverlap + ",");		
        sb.append(this.yOverlap + ",");		
        sb.append(this.atan0 + ",");		
        sb.append(this.atan45 + ",");		
        sb.append(this.atan90+ ",");		
        sb.append(this.atan135 + ",");		
        sb.append(this.atanHistH0.toCsvStringDiscretized(10) + ",");		
        sb.append(this.atanHistH45.toCsvStringDiscretized(10) + ",");		
        sb.append(this.atanHistH90.toCsvStringDiscretized(10) + ",");		
        sb.append(this.atanHistH135.toCsvStringDiscretized(10) + ",");	
        sb.append(this.atanHistV0.toCsvStringDiscretized(10) + ",");		
        sb.append(this.atanHistV45.toCsvStringDiscretized(10) + ",");		
        sb.append(this.atanHistV90.toCsvStringDiscretized(10) + ",");		
        sb.append(this.atanHistV135.toCsvStringDiscretized(10) + ",");
        sb.append(this.area);

		/*for (RegionInfo member : super.getMembers()) {
			sb.append(",");
			sb.append(member.toCSV());
		}*/
		return sb.toString();
	}
	
	public static String labels() {
		String labels = "isEyes,";
		labels += "minX,";
		labels += "minY,";
		labels += "maxX,";
		labels += "maxY,";
		labels += "diffArea,";
		labels += "diffX1,";
		labels += "diffX2,";
		labels += "diffY1,";
		labels += "diffY2,";
		labels += "distCenters,";
		labels += "distCorners,";
		labels += "distX,";
		labels += "distY,";
		labels += "isInsideRegion,";
		labels += Histogram.getLabels("sumHistH", 10);
		labels += ",";
		labels += Histogram.getLabels("sumHistV", 10);
		labels += ",";
		labels += Histogram.getLabels("sumHistR", 24);
		labels += ",";
		labels += Histogram.getLabels("diffHistH", 10);
		labels += ",";
		labels += Histogram.getLabels("diffHistV", 10);
		labels += ",";
		labels += Histogram.getLabels("diffHistR", 24);
		labels += ",";
		labels += Histogram.getLabels("edgeMap", 100);
		labels += ",";
		labels += "diffDistBtwCenters,";
		labels += "diffPercentArea,";
		labels += "diffBpRatio,";
		labels += "avPercentArea,";
		labels += "avBpRatio,";
		labels += "enumEnclosed,";
		labels += "topLeft,";
		labels += "topRight,";
		labels += "bottomLeft,";
		labels += "bottomRight,";
		labels += "left,";
		labels += "right,";
		labels += "top,";
		labels += "bottom,";
		labels += "xOverlap,";
		labels += "yOverlap,";
		labels += "atan0,";
		labels += "atan45,";
		labels += "atan90,";
		labels += "atan135,";
		labels += Histogram.getLabels("atanHistH0", 10);
		labels += ",";
		labels += Histogram.getLabels("atanHistH45", 10);
		labels += ",";
		labels += Histogram.getLabels("atanHistH90", 10);
		labels += ",";
		labels += Histogram.getLabels("atanHistH135", 10);
		labels += ",";
		labels += Histogram.getLabels("atanHistV0", 10);
		labels += ",";
		labels += Histogram.getLabels("atanHistV45", 10);
		labels += ",";
		labels += Histogram.getLabels("atanHistV90", 10);
		labels += ",";
		labels += Histogram.getLabels("atanHistV135", 10);
		labels += ",";
		labels += "area";
				
		/*labels += RegionInfo.labels(1);
		labels += ",";
		labels += RegionInfo.labels(2);*/
		return labels;
	}
	
}
