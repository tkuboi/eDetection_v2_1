package detection;

import java.util.ArrayList;
import java.util.List;

public abstract class WekaData {
	private List<String> fields;
	private int numAttributes;
	private final int numFields = 0;
	
	public WekaData() {
		fields = new ArrayList<String>();
	}

	public WekaData(String str) {
		this();
		setFields(str);
	}
	
	public abstract void setFields(String str);
	
	public int getNumAttributes() {
		return numAttributes;
	}
	
	public List<String> getFields() {
		return fields;
	}
}
