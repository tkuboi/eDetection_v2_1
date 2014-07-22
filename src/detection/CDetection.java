package detection;

import imageUtil.DrawObject;
import imageUtil.WriteImage;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import myUtil.FileUtil;
import detection.BDetection.Callable1;
import detection.BDetection.Callable2;

public class CDetection {
	
	private byte[] pixels;
	private int width;
	private int height;
	private List<RegionInfo> whiteRegions;
	private List<EyePair> eyePairs;
	private BDetection bDetection;
	
    public CDetection(BDetection bd) {
    	this.bDetection = bd;
    	this.pixels = bd.getPixels();
    	this.width = bd.getWidth();
    	this.height = bd.getHeight();
    	this.whiteRegions = new ArrayList<RegionInfo>();
    	this.eyePairs = new ArrayList<EyePair>();
    }
	
    public String getFilename() {
    	return this.bDetection.getFilename();
    }
    
	public void processWhitePixels() {
		int marker = bDetection.getTextGroup().size() + 2; //mark pixel with number greater than 1
		final int width = this.width;
		final int height = this.height;
		int i = 0;
		ArrayList<Integer> seeds = new ArrayList<Integer>();
		RegionInfo rInfo = null;
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				i = row * width + col;
				if (this.pixels[i*3] == (byte)1) { // white pixel
					seeds.add(i);
				}
			}
		}
		for (Integer seed : seeds) {
			if (this.pixels[seed*3] != (byte)1)
				continue;
			int col = seed % width;
			int row = seed / width;
			//System.out.println("col=" + col + ", row=" + row + ", marker=" + marker);
			rInfo = BDetection.seedGrowth(seed, marker, width, this.pixels,
					new Callable1<Boolean>() {
				public Boolean call(int idx, int minX, int maxX, int minY, int maxY, byte[] blob) {
					return BDetection.isWhiteSpace(idx, 1, width, height, blob);
				}
			},
			new Callable2<Integer>() {
				public Integer call(int idx, int val, ArrayList<Integer> queue, byte[] blob) {
					return BDetection.addQueue(idx, val, queue, blob);
				}
			}
					);
			if (rInfo.pixCount > 0) {
				this.whiteRegions.add(rInfo);
				marker++;
			}
		}
	}

	public void pairWhiteRegions() {
		int id = 0;
		for (int i = 0; i < this.whiteRegions.size(); i++) {
			RegionInfo region1 = this.whiteRegions.get(i);
			
			/*RegionGroup eyePair = new RegionGroup(id++);
			eyePair.minX = region1.minX;
			eyePair.minY = region1.minY;
			eyePair.maxX = region1.maxX;
			eyePair.maxY = region1.maxY;
			this.eyePairs.add(eyePair);*/

			for (int j = i + 1; j < this.whiteRegions.size(); j++) {
				RegionInfo region2 = this.whiteRegions.get(j);
				//add as a pair of eyes
				EyePair eyePair = new EyePair(id++, region1, region2);
				
				eyePair.setCombinedFeatures(this.pixels, this.width, this.height);
				
				/*double area1 = Math.abs((double)(region1.maxX - region1.minX) * (double)(region1.maxY - region1.minY));
				double area2 = Math.abs((double)(region2.maxX - region2.minX) * (double)(region2.maxY - region2.minY));

				eyePair.diffArea = Math.abs(area1 - area2) / (double)(this.width * this.height);
				double distX = Math.abs((region1.maxX + region1.minX) / 2 - (region2.maxX + region2.minX) / 2);
				double distY = Math.abs((region1.maxY + region1.minY) / 2 - (region2.maxY + region2.minY) / 2);
				eyePair.distX = (double)distX / (double)(this.width);
				eyePair.distY = (double)distY / (double)(this.height);
				eyePair.distCenters = (double)(distX * distX + distY * distY) / (double)(this.width * this.width + this.height * this.height);*/
				eyePair.isInsideRegion = checkIsInsideRegion(eyePair);
				
				this.eyePairs.add(eyePair);
 			}
		}
	}
	
	public boolean checkIsInsideRegion(EyePair eyePair) {
		for (RegionInfo whiteRegion : this.whiteRegions) {
			if (eyePair.minX >= whiteRegion.minX && eyePair.minY >= whiteRegion.minY
					&& eyePair.maxX <= whiteRegion.maxX && eyePair.maxY <= whiteRegion.maxY)
				return true;
		}
		return false;
	}
	
	public List<String> exportEyePairs() {
		String header = this.bDetection.getFilename() + ",";
		List<String> lines = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		int negCount = 0;
		System.out.println("The number of eyePairs=" + this.eyePairs.size());
		//lines.add("filename,"+EyePair.labels());
		for(EyePair pair : this.eyePairs) {
			RegionInfo region1 = pair.getMembers().get(0);
			RegionInfo region2 = pair.getMembers().get(1);
			double area1 = Math.abs((double)(region1.maxX - region1.minX) * (double)(region1.maxY - region1.minY));
			double area2 = Math.abs((double)(region2.maxX - region2.minX) * (double)(region2.maxY - region2.minY));
			double diffArea = Math.abs(area1 - area2) / area1;
			if (pair.isEyes)
				System.out.println(pair.isEyes + ":diffArea=" + pair.diffArea + ", distCenters=" + pair.distCenters + ", distCorners=" + pair.distCorners + ", area1=" + area1 + ", area2=" + area2);
			if (pair.diffArea < 0.1 && pair.distCenters < 0.1 && pair.distCorners < 100 && area1 > 500 && area2 > 500) {
				pair.computeHists();
				pair.setEdgeMap(pixels, width, height);
				sb.append(header);
				sb.append(pair.toCSV());
				//sb.append("\n");
				lines.add(sb.toString());
				sb.setLength(0);
				if (!pair.isEyes)
					negCount++;
			}
			//System.out.println("pixels.size:" + region1.pixels.size() + ", pixels.size:" + region2.pixels.size());
		}
		System.out.println("the number of lines=" + lines.size());
		return lines;
	}
	
	public void drawRect() {
		System.out.println(this.eyePairs.size());
		//draw rectangle on the pixels
		for (RegionGroup eyes : this.eyePairs) {
			//draw rect (eyes.minX, eyes.minY) - (eyes.maxX, eyes.maxY)
			DrawObject.drawRect(this.pixels, this.width, eyes.minX, eyes.minY,
					eyes.maxX, eyes.maxY, (byte)255, (byte)0, (byte)0);
		}
	}
	
	public void writeImage() {
		//write an image
		String filename = "eyes_" + this.bDetection.getFilename();
		WriteImage.write(filename, this.pixels, this.width, this.height);
	}
	
	public void analyzeContent() {
		for (RegionInfo r : this.whiteRegions) {
			byte[] blob = this.bDetection.getSubBlob(r.minX, r.minY, r.maxX, r.maxY);
		    int width = r.maxX - r.minX + 1;
		    int height = r.maxY - r.minY + 1;
		    r.histH = BDetection.buildHistH(blob, width, height);
		    r.histV = BDetection.buildHistV(blob, width, height);
		    r.histR = this.bDetection.buildHistR(r);
		}
	}
	
	private static List<Frame> getTags(String filename) {
		List<Frame> frames = new ArrayList<Frame>();
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
							frame.elements.add(new Element(Integer.parseInt(tokens[i]),
									Integer.parseInt(tokens[i+1]),
									Integer.parseInt(tokens[i+2]),
									Integer.parseInt(tokens[i+3])));
						}
						frames.add(frame);
					}
				}
			}
			System.out.println("the number of frames=" + frames.size());

		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return frames;
	}

	private static Frame[] sortFrames(List<Frame> frames, List<CDetection> cds) {
		Frame[] arrframes = new Frame[cds.size()];
		for (int i=0; i < cds.size(); i++) {
			//String name = frames.get(i).filename;
			//name.equals(anObject)
			if (frames.get(i).filename.equals(cds.get(i).getFilename()))
				arrframes[i] = frames.get(i);
			else {
				int j = 0;
				while (j < frames.size() && !frames.get(j).filename.equals(cds.get(i).getFilename()))
					j++;
				if (j < frames.size())
					arrframes[i] = frames.get(j);
			}
		}
		return arrframes;
	}
	   
	public static void addLabel(String filename, List<CDetection> cds) {
		Frame[] frames = sortFrames(getTags(filename), cds);
		Frame frame = null;
		for (int i = 0; i < cds.size(); i++) {
			System.out.println("the number of cds=" + cds.size());
			if (frames[i].filename.equals(cds.get(i).getFilename())) {
				frame = frames[i];
			}
			else {
				for (Frame f : frames) {
					if (f.filename.equals(cds.get(i).getFilename())) {
						frame = f;
						break;
					}
				}
			}
			
			for (EyePair r : cds.get(i).eyePairs) {
				for (Element e : frame.elements) {
					if (r.minX >= e.x1
							&& r.minY >= e.y1
							&& r.maxX <= e.x2
							&& r.maxY <= e.y2
							&& Math.abs(r.maxX - r.minX - e.x2 + e.x1) < Math.abs(e.x2 - e.x1) * 0.2
							&& Math.abs(r.maxY - r.minY - e.y2 + e.y1) < Math.abs(e.y2 - e.y1) * 0.2
							&& e.hit == 0) { // match found in bubbles.txt
						e.hit = 1;
						r.isEyes = true;
						break;
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "./image1-2/";
		//String path = "./image_0670/";
		File dir = new File(path);
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File directory, String fileName) {
				return fileName.endsWith(".jpg");
			}
		});
		List<CDetection> cds = new ArrayList<CDetection>();
		List<String> eyeCandidatesInfo = new ArrayList<String>();
		BDetection bd = null;
		for (int i = 0; i < files.length; i++) {
			System.out.println(files[i].getName());
			bd = BDetection.factory(files[i]);
			bd.createBWPixRankFilter();
			bd.rlsaSmoothing();
			//bd.processBlackPixels();
			//bd.classifyBlackRegions();
			//bd.groupTexts(BDetection.GROUP_TEXT_THRESHOLD);
			//bd.pickRegions2();
			//bd.testRegions2();
			//bd.writeImage(files[i].getName()); //output
			//bds.add(bd);
			CDetection cd = new CDetection(bd);
			//do region growing
			cd.processWhitePixels();
			//classify
			cd.analyzeContent();
			cd.pairWhiteRegions();
			//draw rectangle around face or eyes
			//cd.drawRect();
			//cd.writeImage();
			//eyeCandidatesInfo.addAll(cd.exportEyePairs());
			cds.add(cd);
		}
		addLabel(path+"eyes.txt", cds);
		eyeCandidatesInfo.add("filename,"+EyePair.labels());
		for (CDetection cd : cds)
			eyeCandidatesInfo.addAll(cd.exportEyePairs());
		
		System.out.println("The number of eyeCandidatesInfo=" + eyeCandidatesInfo.size());
		FileUtil.writeCSV(eyeCandidatesInfo, "eyeCandidatesInfo.txt");
	}

}
