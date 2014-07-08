package imageUtil;

public class DrawObject {
	
	public static void drawRect(byte[] pixels, int width,
			int x1, int y1, int x2, int y2, byte r, byte g, byte b) {
		int minX = (x1 <= x2 ? x1 : x2);
		int minY = (y1 <= y2 ? y1 : y2);
		int maxX = (x2 >= x1 ? x2 : x1);
		int maxY = (y2 >= y1 ? y2 : y1);
		
		int idx1 = 0, idx2 = 0;
		
		for (int y = minY; y <= maxY; y++) {
			idx1 = y * width + minX;
			idx2 = y * width + maxX;
			pixels[idx1*3] = pixels[idx2*3] = r;
			pixels[idx1*3+1] = pixels[idx2*3 + 1] = g;
			pixels[idx1*3+2] = pixels[idx2*3 + 2] = b;
		}
		
		for (int x = minX; x <= maxX; x++) {
			idx1 = minY * width + x;
			idx2 = maxY * width + x;
			pixels[idx1*3] = pixels[idx2*3] = r;
			pixels[idx1*3+1] = pixels[idx2*3 + 1] = g;
			pixels[idx1*3+2] = pixels[idx2*3 + 2] = b;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
