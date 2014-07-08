package detection;

import java.util.*;
   
   public class Frame {
	   public String filename;
	   public List<Element> elements;
	   public Frame(String name) {
		   filename = name;
		   elements = new ArrayList<Element>();
	   }
   }

