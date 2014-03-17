package model;

import java.awt.Dimension;
import java.awt.Point;
import java.util.*;

/*
 * This class is for putting data into bins.
 * This class shall be used to efficiently search data which has small number of discrete values.
 */
public class NaiveBin<T> {
	private int numBins;
	private class bin<T> {
		public ArrayList<T> elements;

		public bin() {
			this.elements = new ArrayList<T>();
		}
		
		public void add(T element) {
			this.elements.add(element);
		}

		public Object[] toArray() {
			return this.elements.toArray();
		}
	}
	private bin[] bins;
	
	private void initialize() {
		for (int i = 0; i < this.numBins; i++)
			this.bins[i] = new bin();
	}
	public NaiveBin(int num) {
		this.numBins = num;
		this.bins = new bin[this.numBins];
		initialize();
	}
	
	@SuppressWarnings("unchecked")
	public void put(T element, int val) {
		this.bins[val].add(element);
	}
	
	public Object[] get(int val) {
		return this.bins[val].toArray();
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<T> get(int min, int max) {
		ArrayList<T> list = new ArrayList<T>();
		for (int i = min; i <= max && i < this.numBins; i++) {
			list.addAll(this.bins[i].elements);
		}
		return list;
	}
	
	public static void main(String[] args) {
		NaiveBin<Integer> bin = new NaiveBin<Integer>(256);
		for (int i = 0; i < 256; i++) {
			bin.put(i, i);
		}
		System.out.println(bin.get(255)[0]);
	}
}
