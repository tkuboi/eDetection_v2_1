package detection;

import java.util.List;

import weka.core.FastVector;
import weka.core.Instances;

public interface WekaFormat {
	public FastVector createVector();
	public Instances createInstances(FastVector fvWekaAttributes, List<String> strArray);
}
