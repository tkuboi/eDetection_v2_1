package myUtil;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileUtil {
	
	public static List<String> readCSV(String filename) {
		List<String> lines = new ArrayList<String>();
		File file = null;
		Scanner sc = null;
		try {
			file = new File(filename);
			sc = new Scanner(file);
			String line = null;
			while (sc.hasNextLine()) {
				line = sc.nextLine();
				if (line.length() > 0) lines.add(line);
			}
		}
		catch (IOException ex) {
			System.err.println(ex.getMessage());
		}
		finally {
			if (sc != null)
				sc.close();
		}
		return lines;
	}

	public static void writeCSV(List<String> lines, String filename) {
		File file = null;
		PrintWriter writer = null;
		try {
			file = new File(filename);
			writer = new PrintWriter(file);
			for (String line : lines) {
				writer.println(line);
			}
		}
		catch (IOException ex) {
			System.err.println(ex.getMessage());
		}
		finally {
			if (writer != null)
				writer.close();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
