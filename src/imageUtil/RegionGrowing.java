package imageUtil;

import java.util.ArrayList;
import java.util.Hashtable;

import detection.RegionInfo;
import detection.Seed;
import detection.BDetection.Callable1;
import detection.BDetection.Callable2;

public class RegionGrowing {
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

	private static boolean isLegal(int idx, int width, int height) {
		boolean result = false;
		if (0 <= idx && idx < width * height) {
			result = true;
		}
		return result;
	}

	private static boolean isWhiteSpace(byte[] pixels, int idx, int radius, int width, int height) { 
		boolean result = false;
		double score = 0.0;
		if (isLegal(idx, width, height) && pixels[3*idx] == (byte)1) {
			int x = idx % width;
			int y = idx / width;
			int idx2 = 0;
			for (int i = -radius; i <= radius; i++) {
				for (int j = -radius; j <= radius; j++) {
					idx2 = width * (y + j) + x + i;
					if (isLegal(idx2, width, height) && pixels[3*idx2] >= (byte)1) { //1
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

	public static boolean isWhiteSpace(byte[] pixels, byte[] blackPixels, int width, int height, ArrayList<RegionInfo> blackRegions, int idx, int radius, float gScore) { 
		boolean result = false;
		float score = 0.0f;
		int x = idx % width;
		int y = idx / width;
		int idx2 = 0;
		int val = 0;

		if (isLegal(idx, width, height) && pixels[3*idx] == (byte)1) {
			for (int i = -radius; i <= radius; i++) {
				for (int j = -radius; j <= radius; j++) {
					idx2 = width * (y + j) + x + i;
					if (isLegal(idx2, width, height)) { 
						if (pixels[3*idx2] >= (byte)1 ) { //1
							score++;
						}
						else {
							val = blackPixels[idx2] & 0xff;
							if (val >=2 && blackRegions.get(val - 2).type == 1)
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

	public static boolean isWhiteSpace(byte[] pixels, byte[] blackPixels, int width, int height, ArrayList<RegionInfo> blackRegions, int idx, int radius, float gScore, Seed seed) { 
		boolean result = false;
		float score = 0.0f;
		int x = idx % width;
		int y = idx / width;
		int idx2 = 0;
		int val = 0;

		if (isLegal(idx, width, height) && pixels[3*idx] == (byte)1) {
			for (int i = -radius; i <= radius; i++) {
				for (int j = -radius; j <= radius; j++) {
					idx2 = width * (y + j) + x + i;
					if (isLegal(idx2, width, height) &&
							(seed.closed || (x + i > seed.minX - 45 && x + i < seed.maxX + 45 &&
									y + j > seed.minY - 45 && y + j < seed.maxY + 45))) { 
						if (pixels[3*idx2] >= (byte)1 ) { //1
							score++;
						}
						else {
							val = blackPixels[idx2] & 0xff;
							if (val >=2 && blackRegions.get(val - 2).type == 1)
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

	public static boolean isWhiteSpace2(byte[] pixels, byte[] blackPixels, int width, int height, ArrayList<RegionInfo> blackRegions, int idx, int radius, float gScore, Seed seed) { 
		boolean result = false;
		float score = 0.0f;
		int x = idx % width;
		int y = idx / width;
		int idx2 = 0;
		int val = 0;

		if (isLegal(idx, width, height) && pixels[3*idx] == (byte)1) {
			for (int i = -radius; i <= radius; i++) {
				for (int j = -radius; j <= radius; j++) {
					idx2 = width * (y + j) + x + i;
					if (isLegal(idx2, width, height)) { 
						if (pixels[3*idx2] >= (byte)1 ) { //1
							score++;
						}
						else {
							val = blackPixels[idx2] & 0xff;
							if (val >=2 && blackRegions.get(val - 2).type == 1)
								score++;
						}
					}
				}
			}
			if (score >= gScore) { //(1+(radius*2)) * (1+(radius*2))
				if (seed.closed || (x > seed.minX - 0 && x < seed.maxX + 0 &&
						y > seed.minY - 0 && y < seed.maxY + 0))
					result = true;
				else if(!isChokePoint(pixels, blackPixels, width, height, blackRegions, idx, seed))
					result = true;
			}
		}
		return result;
	}

	public static boolean isWhiteSpace3(byte[] pixels, byte[] blackPixels, int width, int height, ArrayList<RegionInfo> blackRegions, int idx, int radius, float gScore) { 
		boolean result = false;
		float score = 0.0f;
		int x = idx % width;
		int y = idx / width;
		int idx2 = 0;
		int val = 0;

		if (isLegal(idx, width, height) && pixels[3*idx] == (byte)1) {
			for (int i = -radius; i <= radius; i++) {
				for (int j = -radius; j <= radius; j++) {
					idx2 = width * (y + j) + x + i;
					if (isLegal(idx2, width, height)) { 
						if (pixels[3*idx2] >= (byte)1 ) { //1
							score++;
						}
						else {
							val = blackPixels[idx2] & 0xff;
							if (val >=2 && blackRegions.get(val - 2).type == 1)
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

	public static boolean isChokePoint(byte[] pixels, byte[] blackPixels, int width, int height, ArrayList<RegionInfo> blackRegions, int idx, Seed seed) {
		boolean chokepoint = false;
		int x = idx % width;
		int y = idx / width;
		int i = 0;
		int index = (y + i) * width + x;
		while(y + i < height &&
				pixels[3 * index] != (byte)0 &&
				!((blackPixels[index] & 0xff) >= 2 &&
				blackRegions.get((blackPixels[index] & 0xff) - 2).type != 1)) { // up
			i++;
				index = (y + i) * width + x;
		}
		int maxY = y + i;
		i = 0;
		index = (y + i) * width + x;
		while(y + i >= 0 &&
				pixels[3 * index] != (byte)0 &&
				!((blackPixels[index] & 0xff) >= 2 &&
				blackRegions.get((blackPixels[index] & 0xff) - 2).type != 1)) { // down
			i--;
				index = (y + i) * width + x;
		}
		int minY = y + i;
		i = 0;
		index = y * width + x + i;
		while(x + i < width &&
				pixels[3 * index] != (byte)0 &&
				!((blackPixels[index] & 0xff) >= 2 &&
				blackRegions.get((blackPixels[index] & 0xff) - 2).type != 1)) { // right
			i++;
				index = y * width + x + i;
		}
		int maxX = x + i;
		i = 0;
		index = y * width + x + i;
		while(x + i >= 0 &&
				pixels[3 * index] != (byte)0 &&
				!((blackPixels[index] & 0xff) >= 2 &&
				blackRegions.get((blackPixels[index] & 0xff) - 2).type != 1)) { // left
			i--;
				index = y * width + x + i;
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

	public static RegionInfo seedGrowth(byte[] pixels, byte[] blackPixels, int width, int height, ArrayList<RegionInfo> blackRegions, Seed seed, int marker, int radius, float gScore) {
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
		count += addQueue(idx, marker, queue, pixels);
		table.put(idx, 1);

		while(!queue.isEmpty()) {  
			pix = queue.remove(0);
			x = pix % width;
			y = pix / width;
			// check all 8 neighbors
			// if the neighbor is 1, mark the pixel with the marker and add the pixel to the queue
			if (y - 1 >= 0 && x - 1 >= 0 && x - 1 < width) {
				idx = width * (y - 1) + x - 1;
				if (table.get(idx) == null && isWhiteSpace2(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore, seed)) { // check right 1
					count += addQueue(idx, marker, queue, pixels);
					maxX = (x > maxX ? x : maxX);
					minX = (x < minX ? x : minX);
					maxY = (y > maxY ? y : maxY);
					minY = (y < minY ? y : minY);
				}
				table.put(idx, 1);
			}
			if (y >= 0 && x - 1 >= 0 && x - 1 < width) {
				idx = width * (y) + x - 1;
				if (table.get(idx) == null && isWhiteSpace2(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore, seed)) { // check right 2
					count += addQueue(idx, marker, queue, pixels);
					maxX = (x > maxX ? x : maxX);
					minX = (x < minX ? x : minX);
					maxY = (y > maxY ? y : maxY);
					minY = (y < minY ? y : minY);
				}
				table.put(idx, 1);
			}
			if (y + 1 >= 0 && x - 1 >= 0 && x - 1 < width) {
				idx = width * (y + 1) + x - 1;
				if (table.get(idx) == null && isWhiteSpace2(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore, seed)) { // check right 3
					count += addQueue(idx, marker, queue, pixels);
					maxX = (x > maxX ? x : maxX);
					minX = (x < minX ? x : minX);
					maxY = (y > maxY ? y : maxY);
					minY = (y < minY ? y : minY);
				}
				table.put(idx, 1);
			}
			if (y - 1 >= 0 && x >= 0 && x < width) {
				idx = width * (y - 1) + x;
				if (table.get(idx) == null && isWhiteSpace2(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore, seed)) { // check right 4
					count += addQueue(idx, marker, queue, pixels);
					maxX = (x > maxX ? x : maxX);
					minX = (x < minX ? x : minX);
					maxY = (y > maxY ? y : maxY);
					minY = (y < minY ? y : minY);
				}
				table.put(idx, 1);
			}
			if (y + 1 >= 0 && x >= 0 && x < width) {
				idx = width * (y + 1) + x;
				if (table.get(idx) == null && isWhiteSpace2(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore, seed)) { // check right 5
					count += addQueue(idx, marker, queue, pixels);
					maxX = (x > maxX ? x : maxX);
					minX = (x < minX ? x : minX);
					maxY = (y > maxY ? y : maxY);
					minY = (y < minY ? y : minY);
				}
				table.put(idx, 1);
			}
			if (y - 1 >= 0 && x + 1 >= 0 && x + 1 < width) {
				idx = width * (y - 1) + x + 1;
				if (table.get(idx) == null && isWhiteSpace2(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore, seed)) { // check right 6
					count += addQueue(idx, marker, queue, pixels);
					maxX = (x > maxX ? x : maxX);
					minX = (x < minX ? x : minX);
					maxY = (y > maxY ? y : maxY);
					minY = (y < minY ? y : minY);
				}
				table.put(idx, 1);
			}
			if (y >= 0 && x + 1 >= 0 && x + 1 < width) {
				idx = width * y + x + 1;
				if (table.get(idx) == null && isWhiteSpace2(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore, seed)) { // check right 7
					count += addQueue(idx, marker, queue, pixels);
					maxX = (x > maxX ? x : maxX);
					minX = (x < minX ? x : minX);
					maxY = (y > maxY ? y : maxY);
					minY = (y < minY ? y : minY);
				}
				table.put(idx, 1);
			}
			if (y + 1 >= 0 && x + 1 >= 0 && x + 1 < width) {
				idx = width * (y + 1) + x + 1;
				if (table.get(idx) == null && isWhiteSpace2(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore, seed)) { // check right 8
					count += addQueue(idx, marker, queue, pixels);
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

	public static RegionInfo seedGrowth(byte[] pixels, byte[] blackPixels, int width, int height, ArrayList<RegionInfo> blackRegions, Seed seed, int radius, float gScore) {
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
				if (table.get(idx) == null && isWhiteSpace(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 1
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
				if (table.get(idx) == null && isWhiteSpace(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 2
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
				if (table.get(idx) == null && isWhiteSpace(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 3
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
				if (table.get(idx) == null && isWhiteSpace(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 4
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
				if (table.get(idx) == null && isWhiteSpace(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 5
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
				if (table.get(idx) == null && isWhiteSpace(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 6
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
				if (table.get(idx) == null && isWhiteSpace(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 7
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
				if (table.get(idx) == null && isWhiteSpace(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 8
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
	public RegionInfo seedGrowth3(byte[] pixels, byte[] blackPixels, int width, int height, ArrayList<RegionInfo> blackRegions, Seed seed, int marker, int radius, float gScore) {
		int pix, x, y, x1, y1, idx, count, maxX, minX, maxY, minY;
		int edgePixels = 0;
		int closed = 0;
		pix = seed.index;
		int maxArea = (seed.maxX - seed.minX) * (seed.maxY - seed.minY) * 3;
		x = y = x1 = y1 = idx = count = 0;
		ArrayList<Integer> queue = new ArrayList<Integer>();
		Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
		x = pix % width;
		y = pix / width;
		maxX = minX = x;
		maxY = minY = y;

		idx = width * y + x;
		count += addQueue(idx, marker, queue, pixels);
		table.put(idx, 1);

		while(!queue.isEmpty()) {  
			pix = queue.remove(0);
			x = pix % width;
			y = pix / width;
			// check all 8 neighbors
			// if the neighbor is 1, mark the pixel with the marker and add the pixel to the queue
			if (y - 1 >= 0 && x - 1 >= 0 && x - 1 < width) {
				idx = width * (y - 1) + x - 1;
				if (table.get(idx) == null && isWhiteSpace3(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 1
					x1 = idx % width;
					y1 = idx / width;
					if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
							y1 > seed.minY - 0 && y1 < seed.maxY + 0)
							|| !isChokePoint(pixels, blackPixels, width, height, blackRegions, idx, seed)) { // boundary reached
						count += addQueue(idx, marker, queue, pixels);
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
				if (table.get(idx) == null && isWhiteSpace3(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 1
					x1 = idx % width;
					y1 = idx / width;
					if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
							y1 > seed.minY - 0 && y1 < seed.maxY + 0)
							|| !isChokePoint(pixels, blackPixels, width, height, blackRegions, idx, seed)) { // boundary reached
						count += addQueue(idx, marker, queue, pixels);
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
				if (table.get(idx) == null && isWhiteSpace3(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 1
					x1 = idx % width;
					y1 = idx / width;
					if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
							y1 > seed.minY - 0 && y1 < seed.maxY + 0)
							|| !isChokePoint(pixels, blackPixels, width, height, blackRegions, idx, seed)) { // boundary reached
						count += addQueue(idx, marker, queue, pixels);
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
				if (table.get(idx) == null && isWhiteSpace3(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 1
					x1 = idx % width;
					y1 = idx / width;
					if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
							y1 > seed.minY - 0 && y1 < seed.maxY + 0)
							|| !isChokePoint(pixels, blackPixels, width, height, blackRegions, idx, seed)) { // boundary reached
						count += addQueue(idx, marker, queue, pixels);
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
				if (table.get(idx) == null && isWhiteSpace3(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 1
					x1 = idx % width;
					y1 = idx / width;
					if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
							y1 > seed.minY - 0 && y1 < seed.maxY + 0)
							|| !isChokePoint(pixels, blackPixels, width, height, blackRegions, idx, seed)) { // boundary reached
						count += addQueue(idx, marker, queue, pixels);
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
				if (table.get(idx) == null && isWhiteSpace3(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 1
					x1 = idx % width;
					y1 = idx / width;
					if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
							y1 > seed.minY - 0 && y1 < seed.maxY + 0)
							|| !isChokePoint(pixels, blackPixels, width, height, blackRegions, idx, seed)) { // boundary reached
						count += addQueue(idx, marker, queue, pixels);
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
				if (table.get(idx) == null && isWhiteSpace3(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 1
					x1 = idx % width;
					y1 = idx / width;
					if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
							y1 > seed.minY - 0 && y1 < seed.maxY + 0)
							|| !isChokePoint(pixels, blackPixels, width, height, blackRegions, idx, seed)) { // boundary reached
						count += addQueue(idx, marker, queue, pixels);
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
				if (table.get(idx) == null && isWhiteSpace3(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 1
					x1 = idx % width;
					y1 = idx / width;
					if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
							y1 > seed.minY - 0 && y1 < seed.maxY + 0)
							|| !isChokePoint(pixels, blackPixels, width, height, blackRegions, idx, seed)) { // boundary reached
						count += addQueue(idx, marker, queue, pixels);
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
	public static RegionInfo seedGrowth4(byte[] pixels, byte[] blackPixels, int width, int height, ArrayList<RegionInfo> blackRegions, Seed seed, int radius, float gScore) {
		int pix, x, y, x1, y1, idx, count, maxX, minX, maxY, minY;
		int edgePixels = 0;
		int closed = 0;
		pix = seed.index;
		int maxArea = (seed.maxX - seed.minX) * (seed.maxY - seed.minY) * 3;
		x = y = x1 = y1 = idx = count = 0;
		ArrayList<Integer> queue = new ArrayList<Integer>();
		Hashtable<Integer, Integer> table = new Hashtable<Integer, Integer>();
		x = pix % width;
		y = pix / width;
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
				if (table.get(idx) == null && isWhiteSpace3(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 1
					x1 = idx % width;
					y1 = idx / width;
					if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
							y1 > seed.minY - 0 && y1 < seed.maxY + 0)
							|| !isChokePoint(pixels, blackPixels, width, height, blackRegions, idx, seed)) { // boundary reached
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
				if (table.get(idx) == null && isWhiteSpace3(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 1
					x1 = idx % width;
					y1 = idx / width;
					if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
							y1 > seed.minY - 0 && y1 < seed.maxY + 0)
							|| !isChokePoint(pixels, blackPixels, width, height, blackRegions, idx, seed)) { // boundary reached
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
				if (table.get(idx) == null && isWhiteSpace3(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 1
					x1 = idx % width;
					y1 = idx / width;
					if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
							y1 > seed.minY - 0 && y1 < seed.maxY + 0)
							|| !isChokePoint(pixels, blackPixels, width, height, blackRegions, idx, seed)) { // boundary reached
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
				if (table.get(idx) == null && isWhiteSpace3(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 1
					x1 = idx % width;
					y1 = idx / width;
					if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
							y1 > seed.minY - 0 && y1 < seed.maxY + 0)
							|| !isChokePoint(pixels, blackPixels, width, height, blackRegions, idx, seed)) { // boundary reached
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
				if (table.get(idx) == null && isWhiteSpace3(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 1
					x1 = idx % width;
					y1 = idx / width;
					if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
							y1 > seed.minY - 0 && y1 < seed.maxY + 0)
							|| !isChokePoint(pixels, blackPixels, width, height, blackRegions, idx, seed)) { // boundary reached
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
				if (table.get(idx) == null && isWhiteSpace3(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 1
					x1 = idx % width;
					y1 = idx / width;
					if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
							y1 > seed.minY - 0 && y1 < seed.maxY + 0)
							|| !isChokePoint(pixels, blackPixels, width, height, blackRegions, idx, seed)) { // boundary reached
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
				if (table.get(idx) == null && isWhiteSpace3(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 1
					x1 = idx % width;
					y1 = idx / width;
					if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
							y1 > seed.minY - 0 && y1 < seed.maxY + 0)
							|| !isChokePoint(pixels, blackPixels, width, height, blackRegions, idx, seed)) { // boundary reached
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
				if (table.get(idx) == null && isWhiteSpace3(pixels, blackPixels, width, height, blackRegions, idx, radius, gScore)) { // check right 1
					x1 = idx % width;
					y1 = idx / width;
					if (seed.closed || (x1 > seed.minX - 0 && x1 < seed.maxX + 0 &&
							y1 > seed.minY - 0 && y1 < seed.maxY + 0)
							|| !isChokePoint(pixels, blackPixels, width, height, blackRegions, idx, seed)) { // boundary reached
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

}
