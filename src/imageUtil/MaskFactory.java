package imageUtil;

import magick.DrawInfo;
import magick.ImageInfo;
import magick.MagickImage;
import magick.MagickException;
import magick.PixelPacket;
import java.awt.Dimension;
import java.util.*;
import java.io.File;

public class MaskFactory {
	public static int[] getSobelFilter3x() {
		int[] filter = {-1, -2, -1,
				         0,  0,  0,
				         1,  2,  1};
		return filter;
	}
	
    public static int[] getSobelFilter3y() {
    	int[] filter = {-1,  0,  1,
		                -2,  0,  2,
		                -1,  0,  1};
    	return filter;
	}

	public static int[] getPrewittFilter3x() {
		int[] filter = {-1, -1, -1,
		                 0,  0,  0,
		                 1,  1,  1};
		return filter;
	}
	
	public static int[] getPrewittFilter3y() {
		int[] filter = {-1,  0,  1,
                        -1,  0,  1,
                        -1,  0,  1};
        return filter;
	}
	
	public static int[] getGaussianFilter3() {
		int[] filter = {1, 2, 1,
				        2, 4, 2,
				        1, 2, 1};
		return filter;
	}
	
	public static int[] getMeanFilter(int span) {
		int size = span * 2 + 1;
		int[] filter = new int[size * size];
		for (int i = 0; i < size * size; i++)
			filter[i] = 1;
		return filter;
	}
}

