package model;

public class Histogram2D {
    private int sizeX;
    private int sizeY;
    private int minX;
    private int maxX;
    private int minY;
    private int maxY;

    private int[] bins;
    
    public Histogram2D(int x, int y, int min1, int max1, int min2, int max2) {
    	this.sizeX = x;
    	this.sizeY = y;
    	this.minX = min1;
    	this.maxX = max1;
    	this.minY = min2;
    	this.maxY = max2;
    	this.bins = new int[this.sizeX * this.sizeY];
    }
    
    public void bin(int x, int y) {
    	int row = (this.sizeY - 1) * (y - this.minY) / (this.maxY - this.minY);
    	int col = (this.sizeX - 1) * (x - this.minX) / (this.maxX - this.minX);
    	this.bins[row * this.sizeX + col]++;
    }
    
    public int getSizeX() {
    	return this.sizeX;
    }

    public int getSizeY() {
    	return this.sizeY;
    }

    public int[] getBins() {
    	return this.bins;
    }
    
    public String toJsonString() {
    	String str = "[";
    	for (int i = 0; i < this.sizeX * this.sizeY - 1; i++) {
    		str += this.bins[i];
    		str += ",";
    	}
    	str += this.bins[this.sizeX * this.sizeY - 1];
    	str += "]";
    	return str;
    }
}
