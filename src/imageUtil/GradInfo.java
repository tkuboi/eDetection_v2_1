package imageUtil;

import magick.DrawInfo;
import magick.ImageInfo;
import magick.MagickImage;
import magick.MagickException;
import magick.PixelPacket;
import java.awt.Dimension;
import java.util.*;
import java.io.File;

public class GradInfo {
	public int grad;
	public double atan;
	public GradInfo(int g, double a) {
		this.grad = g;
		this.atan = a;
	}
}

