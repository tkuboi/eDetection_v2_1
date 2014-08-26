package model;

public class Histogram {
    private int size;
    private int[] bins;
    private int min;
    private int max;
    
    public Histogram(int size, int min, int max) {
    	this.size = size;
    	this.min = min;
    	this.max = max;
    	this.bins = new int[this.size];
    }
    
    public void bin(int v) {
    	int i = (this.size) * (v - this.min) / (this.max - this.min);
    	this.bins[i]++;
    }

    public void bin(int i, int v) {
    	this.bins[i] = v;
    }

    public void binWithValue(int i, int v) {
    	int idx = (this.size) * (i - this.min) / (this.max - this.min);
    	this.bins[idx] = v;
    }

    public void binWithValueAdd(int i, int v) {
    	int idx = (this.size) * (i - this.min) / (this.max - this.min);
    	this.bins[idx] += v;
    }

    public int getVal(int i) {
    	return this.bins[i];
    }

    public int getSize() {
    	return this.size;
    }

    public int getMin() {
    	return this.min;
    }

    public int getMax() {
    	return this.max;
    }

    public int[] getBins() {
    	return this.bins;
    }
    
    public int getSum() {
    	int sum = 0;
    	for (int bin : this.bins) {
    		sum += bin;
    	}
    	return sum;
    }
    
    public int getMaxVal() {
    	int max = this.bins[0];
    	for (int bin : this.bins) {
    		if (bin > max)
    			max = bin;
    	}
    	return max;
    }

    public int getMinVal() {
    	int min = this.bins[0];
    	for (int bin : this.bins) {
    		if (bin < min)
    			min = bin;
    	}
    	return min;
    }

    public String toJsonString() {
    	String str = "[";
    	for (int i = 0; i < this.size - 1; i++) {
    		str += this.bins[i];
    		str += ",";
    	}
    	str += this.bins[this.size - 1];
    	str += "]";
    	return str;
    }
    
    public String toCsvString() {
    	String str = "";
    	for (int i = 0; i < this.size - 1; i++) {
    		str += this.bins[i];
    		str += ",";
    	}
    	str += this.bins[this.size - 1];
    	return str;
    }

    public String toCsvStringNormalized() {
    	String str = "";
    	for (int i = 0; i < this.size - 1; i++) {
    		str += (getSum() > 0 ? (float)this.bins[i] / (float)getSum() : 0);
    		str += ",";
    	}
    	str += (getSum() > 0 ? (float)this.bins[this.size - 1] / (float)getSum() : 0);
    	return str;
    }

    public String toCsvStringDiscretized(int num_cat) {
    	String str = "";
    	for (int i = 0; i < this.size - 1; i++) {
    		str += (getSum() > 0 ? discretize((float)this.bins[i], (float)getSum(), num_cat) : 0);
    		str += ",";
    	}
    	str += (getSum() > 0 ? discretize((float)this.bins[this.size - 1], (float)getSum(), num_cat) : 0);
    	return str;
    }

    int discretize(float v, float sum, int num_cat) {
    	float min = getMinVal();
    	float max = getMaxVal();
    	return (int)(((v == 0 ? v : v+1) - min) * num_cat / (max - min + 1));
    }

    public static String getLabels(String name, int size) {
    	StringBuilder sb = new StringBuilder();
    	for (int i = 1; i <= size; i++) {
    	    sb.append(name + "_" + i);
    	    sb.append(",");
    	}
    	sb.setLength(sb.length()-1);
    	return sb.toString();
    }
}
