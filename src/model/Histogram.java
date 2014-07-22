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
    	int i = (this.size - 1) * (v - this.min) / (this.max - this.min);
    	this.bins[i]++;
    }

    public void bin(int i, int v) {
    	this.bins[i] = v;
    }

    public void binWithValue(int i, int v) {
    	int idx = (this.size - 1) * (i - this.min) / (this.max - this.min);
    	this.bins[idx] = v;
    }

    public void binWithValueAdd(int i, int v) {
    	int idx = (this.size - 1) * (i - this.min) / (this.max - this.min);
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
    		str += ", ";
    	}
    	str += this.bins[this.size - 1];
    	return str;
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
