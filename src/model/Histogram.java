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
    
    public int getSize() {
    	return this.size;
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
}
