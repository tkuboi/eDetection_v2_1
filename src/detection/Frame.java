package detection;

import java.util.*;
   
   public class Frame {
	   public String filename;
	   public ArrayList<Bubble> bubbles;
	   public Frame(String name) {
		   filename = name;
		   bubbles = new ArrayList<Bubble>();
	   }
   }

