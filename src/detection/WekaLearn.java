package detection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.FastVector;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
//import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.SMO;

import myUtil.FileUtil;

public class WekaLearn {
	private SMO model;
	
	//private int numTrainingSet;
	
	public WekaLearn() {
		//model = new LibSVM();
		model = new SMO();
	}

	public Classifier exportModel() {
		return model;
	}
	
	private static FastVector createVector() {
		 // Declare two numeric attributes
		//filename, marker, isBubble, minX, minY, maxX, maxY, pixCount, 
		//xRange, yRange, isAreaLessThanMax, isAreaMoreThanMin, isXLessThanMax, 
		//isYLessThanMax, isRegionEnclosed, isPixCountMoreThanMin, isDistBtwCentersLessThanMax,
		//distBtwCenters, blkPixPercent, sumPix, sdHoriz, sdVert, sdPix, enclosed, percentEnclosed,
		//percentArea, histH_1,histH_2,histH_3,histH_4,histH_5,histH_6,histH_7,histH_8,histH_9,histH_10,
		//histV_1,histV_2,histV_3,histV_4,histV_5,histV_6,histV_7,histV_8,histV_9,histV_10,
		//histR_1,histR_2,histR_3,histR_4,histR_5,histR_6,histR_7,histR_8,histR_9,histR_10,
		//histR_11,histR_12,histR_13,histR_14,histR_15,histR_16,histR_17,histR_18,histR_19,histR_20,
		//histR_21,histR_22,histR_23,histR_24
		FastVector fvClassVal = new FastVector(2);
		fvClassVal.addElement("true");
		fvClassVal.addElement("false");
		Attribute ClassAttribute = new Attribute("theClass", fvClassVal);

		FastVector fvBoolVal = new FastVector(2);
		fvBoolVal.addElement("true");
		fvBoolVal.addElement("false");

		Attribute Attribute4 = new Attribute("maxY");
		Attribute Attribute5 = new Attribute("pixCount");
		Attribute Attribute6 = new Attribute("xRange");
		Attribute Attribute7 = new Attribute("yRange");
		Attribute Attribute8 = new Attribute("isAreaLessThanMax");
		Attribute Attribute9 = new Attribute("isAreaMoreThanMin");
		Attribute Attribute10 = new Attribute("isXLessThanMax");
		Attribute Attribute11 = new Attribute("isYLessThanMax");
		Attribute Attribute12 = new Attribute("isRegionEnclosed");
		Attribute Attribute13 = new Attribute("isPixCountMoreThanMin");
		Attribute Attribute14 = new Attribute("isDistBtwCentersLessThanMax");
		Attribute Attribute15 = new Attribute("distBtwCenters");
		Attribute Attribute16 = new Attribute("blkPixPercent");
		Attribute Attribute17 = new Attribute("sumPix");
		Attribute Attribute18 = new Attribute("sdHoriz");
		Attribute Attribute19 = new Attribute("sdVert");
		Attribute Attribute20 = new Attribute("sdPix");
		Attribute Attribute21 = new Attribute("enclosed", fvBoolVal);
		Attribute Attribute22 = new Attribute("percentEnclosed");
		Attribute Attribute23 = new Attribute("percentArea");
		Attribute Attribute24 = new Attribute("histH_1");
		Attribute Attribute25 = new Attribute("histH_2");
		Attribute Attribute26 = new Attribute("histH_3");
		Attribute Attribute27 = new Attribute("histH_4");
		Attribute Attribute28 = new Attribute("histH_5");
		Attribute Attribute29 = new Attribute("histH_6");
		Attribute Attribute30 = new Attribute("histH_7");
		Attribute Attribute31 = new Attribute("histH_8");
		Attribute Attribute32 = new Attribute("histH_9");
		Attribute Attribute33 = new Attribute("histH_10");
		Attribute Attribute34 = new Attribute("histV_1");
		Attribute Attribute35 = new Attribute("histV_2");
		Attribute Attribute36 = new Attribute("histV_3");
		Attribute Attribute37 = new Attribute("histV_4");
		Attribute Attribute38 = new Attribute("histV_5");
		Attribute Attribute39 = new Attribute("histV_6");
		Attribute Attribute40 = new Attribute("histV_7");
		Attribute Attribute41 = new Attribute("histV_8");
		Attribute Attribute42 = new Attribute("histV_9");
		Attribute Attribute43 = new Attribute("histV_10");
		Attribute Attribute44 = new Attribute("histR_1");
		Attribute Attribute45 = new Attribute("histR_2");
		Attribute Attribute46 = new Attribute("histR_3");
		Attribute Attribute47 = new Attribute("histR_4");
		Attribute Attribute48 = new Attribute("histR_5");
		Attribute Attribute49 = new Attribute("histR_6");
		Attribute Attribute50 = new Attribute("histR_7");
		Attribute Attribute51 = new Attribute("histR_8");
		Attribute Attribute52 = new Attribute("histR_9");
		Attribute Attribute53 = new Attribute("histR_10");
		Attribute Attribute54 = new Attribute("histR_11");
		Attribute Attribute55 = new Attribute("histR_12");
		Attribute Attribute56 = new Attribute("histR_13");
		Attribute Attribute57 = new Attribute("histR_14");
		Attribute Attribute58 = new Attribute("histR_15");
		Attribute Attribute59 = new Attribute("histR_16");
		Attribute Attribute60 = new Attribute("histR_17");
		Attribute Attribute61 = new Attribute("histR_18");
		Attribute Attribute62 = new Attribute("histR_19");
		Attribute Attribute63 = new Attribute("histR_20");
		Attribute Attribute64 = new Attribute("histR_21");
		Attribute Attribute65 = new Attribute("histR_22");
		Attribute Attribute66 = new Attribute("histR_23");
		Attribute Attribute67 = new Attribute("histR_24");
		

		// Declare the feature vector
		FastVector fvWekaAttributes = new FastVector(65);
		fvWekaAttributes.addElement(Attribute4);    
		fvWekaAttributes.addElement(Attribute5);    
		fvWekaAttributes.addElement(Attribute6);    
		fvWekaAttributes.addElement(Attribute7);    
		fvWekaAttributes.addElement(Attribute8);    
		fvWekaAttributes.addElement(Attribute9);    
		fvWekaAttributes.addElement(Attribute10);    
		fvWekaAttributes.addElement(Attribute11);    
		fvWekaAttributes.addElement(Attribute12);    
		fvWekaAttributes.addElement(Attribute13);    
		fvWekaAttributes.addElement(Attribute14);    
		fvWekaAttributes.addElement(Attribute15);    
		fvWekaAttributes.addElement(Attribute16);    
		fvWekaAttributes.addElement(Attribute17);    
		fvWekaAttributes.addElement(Attribute18);    
		fvWekaAttributes.addElement(Attribute19);    
		fvWekaAttributes.addElement(Attribute20);    
		fvWekaAttributes.addElement(Attribute21);    
		fvWekaAttributes.addElement(Attribute22);    
		fvWekaAttributes.addElement(Attribute23);    
		fvWekaAttributes.addElement(Attribute24);    
		fvWekaAttributes.addElement(Attribute25);    
		fvWekaAttributes.addElement(Attribute26);    
		fvWekaAttributes.addElement(Attribute27);    
		fvWekaAttributes.addElement(Attribute28);    
		fvWekaAttributes.addElement(Attribute29);    
		fvWekaAttributes.addElement(Attribute30);
		fvWekaAttributes.addElement(Attribute31);    
		fvWekaAttributes.addElement(Attribute32);    
		fvWekaAttributes.addElement(Attribute33);    
		fvWekaAttributes.addElement(Attribute34);    
		fvWekaAttributes.addElement(Attribute35);    
		fvWekaAttributes.addElement(Attribute36);    
		fvWekaAttributes.addElement(Attribute37);    
		fvWekaAttributes.addElement(Attribute38);    
		fvWekaAttributes.addElement(Attribute39);    
		fvWekaAttributes.addElement(Attribute40);    
		fvWekaAttributes.addElement(Attribute41);    
		fvWekaAttributes.addElement(Attribute42);    
		fvWekaAttributes.addElement(Attribute43);    
		fvWekaAttributes.addElement(Attribute44);    
		fvWekaAttributes.addElement(Attribute45);    
		fvWekaAttributes.addElement(Attribute46);    
		fvWekaAttributes.addElement(Attribute47);    
		fvWekaAttributes.addElement(Attribute48);    
		fvWekaAttributes.addElement(Attribute49);    
		fvWekaAttributes.addElement(Attribute50);    
		fvWekaAttributes.addElement(Attribute51);    
		fvWekaAttributes.addElement(Attribute52);    
		fvWekaAttributes.addElement(Attribute53);    
		fvWekaAttributes.addElement(Attribute54);    
		fvWekaAttributes.addElement(Attribute55);    
		fvWekaAttributes.addElement(Attribute56);    
		fvWekaAttributes.addElement(Attribute57);    
		fvWekaAttributes.addElement(Attribute58);    
		fvWekaAttributes.addElement(Attribute59);    
		fvWekaAttributes.addElement(Attribute60);    
		fvWekaAttributes.addElement(Attribute61);    
		fvWekaAttributes.addElement(Attribute62);    
		fvWekaAttributes.addElement(Attribute63);    
		fvWekaAttributes.addElement(Attribute64);    
		fvWekaAttributes.addElement(Attribute65);    
		fvWekaAttributes.addElement(Attribute66);    
		fvWekaAttributes.addElement(Attribute67);    		
		fvWekaAttributes.addElement(ClassAttribute);


		return fvWekaAttributes;
	}

	private static Instances createInstances(FastVector fvWekaAttributes, int size, List<String> strArray) {
		Instances trainningSet = new Instances("Rel", fvWekaAttributes, size);
		String[] tokens;
		for (String str : strArray) {
			//System.out.println(str);
			tokens = str.split(",");
			Instance vector = new Instance(65);
			vector.setValue((Attribute)fvWekaAttributes.elementAt(0), Double.parseDouble(tokens[6].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(1), Double.parseDouble(tokens[7].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(2), Double.parseDouble(tokens[8].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(3), Double.parseDouble(tokens[9].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(4), Double.parseDouble(tokens[10].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(5), Double.parseDouble(tokens[11].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(6), Double.parseDouble(tokens[12].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(7), Double.parseDouble(tokens[13].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(8), Double.parseDouble(tokens[14].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(9), Double.parseDouble(tokens[15].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(10), Double.parseDouble(tokens[16].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(11), Double.parseDouble(tokens[17].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(12), Double.parseDouble(tokens[18].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(13), Double.parseDouble(tokens[19].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(14), Double.parseDouble(tokens[20].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(15), Double.parseDouble(tokens[21].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(16), Double.parseDouble(tokens[22].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(17), tokens[23].trim());
			vector.setValue((Attribute)fvWekaAttributes.elementAt(18), Double.parseDouble(tokens[24].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(19), Double.parseDouble(tokens[25].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(20), Double.parseDouble(tokens[26].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(21), Double.parseDouble(tokens[27].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(22), Double.parseDouble(tokens[28].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(23), Double.parseDouble(tokens[29].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(24), Double.parseDouble(tokens[30].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(25), Double.parseDouble(tokens[31].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(26), Double.parseDouble(tokens[32].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(27), Double.parseDouble(tokens[33].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(28), Double.parseDouble(tokens[34].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(29), Double.parseDouble(tokens[35].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(30), Double.parseDouble(tokens[36].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(31), Double.parseDouble(tokens[37].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(32), Double.parseDouble(tokens[38].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(33), Double.parseDouble(tokens[39].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(34), Double.parseDouble(tokens[40].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(35), Double.parseDouble(tokens[41].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(36), Double.parseDouble(tokens[42].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(37), Double.parseDouble(tokens[43].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(38), Double.parseDouble(tokens[44].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(39), Double.parseDouble(tokens[45].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(40), Double.parseDouble(tokens[46].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(41), Double.parseDouble(tokens[47].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(42), Double.parseDouble(tokens[48].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(43), Double.parseDouble(tokens[49].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(44), Double.parseDouble(tokens[50].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(45), Double.parseDouble(tokens[51].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(46), Double.parseDouble(tokens[52].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(47), Double.parseDouble(tokens[53].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(48), Double.parseDouble(tokens[54].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(49), Double.parseDouble(tokens[55].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(50), Double.parseDouble(tokens[56].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(51), Double.parseDouble(tokens[57].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(52), Double.parseDouble(tokens[58].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(53), Double.parseDouble(tokens[59].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(54), Double.parseDouble(tokens[60].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(55), Double.parseDouble(tokens[61].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(56), Double.parseDouble(tokens[62].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(57), Double.parseDouble(tokens[63].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(58), Double.parseDouble(tokens[64].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(59), Double.parseDouble(tokens[65].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(60), Double.parseDouble(tokens[66].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(61), Double.parseDouble(tokens[67].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(62), Double.parseDouble(tokens[68].trim()));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(63), Double.parseDouble(tokens[69].trim()));
			//vector.setValue((Attribute)fvWekaAttributes.elementAt(67), Double.parseDouble(tokens[70]));
			vector.setValue((Attribute)fvWekaAttributes.elementAt(64), tokens[2].trim());
			trainningSet.add(vector);
		}
		int cIdx=trainningSet.numAttributes()-1;
		trainningSet.setClassIndex(cIdx);

        return trainningSet;
	}
	
	private void buildKernel(List<String> data) {
		//data.remove(0);
		FastVector vector = createVector();
		Instances trainningSet = createInstances(vector, data.size(), data);

		//RBFKernel svm = new RBFKernel();
		try {
			model.buildClassifier(trainningSet);
			Evaluation eval = new Evaluation(trainningSet);
			eval.crossValidateModel(model, trainningSet, 3, new Random(1));
			System.out.println(eval.toSummaryString("\nResults\n======\n", false));
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	public void evaluate(String filename) {
		List<String> testData = FileUtil.readCSV(filename);
		testData.remove(0);
		FastVector vector = createVector();
		Instances instances = createInstances(vector, testData.size(), testData);
		try {
			Evaluation eval = new Evaluation(instances);
			eval.evaluateModel(model, instances);
			System.out.println(eval.toSummaryString("\nResults\n======\n", false));
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	public List<String> classify(String filename, double[] result) {
		/*String actual;
		String predicted;
		String imgfile;
		String[] tokens;
		int match = 0;
		int mismatch = 0;*/
		List<String> testData = FileUtil.readCSV(filename);
		testData.remove(0);
		return classify(testData, result);
		/*FastVector vector = createVector();
		Instances instances = createInstances(vector, testData.size(), testData);
		for (int i = 0; i < testData.size(); i++) {
			tokens = testData.get(i).split(",");
			imgfile = tokens[0].replace('"', ' ').trim();
			System.out.println(imgfile+":"+tokens[1]+":"+tokens[2]+":"+tokens[3]+","+tokens[4]+","+tokens[5]+","+tokens[6]);
			try {
				double pred = this.model.classifyInstance(instances.instance(i));
				actual = instances.classAttribute().value((int) instances.instance(i).classValue());
				predicted = instances.classAttribute().value((int) pred);
				System.out.print("actual: " + actual);
				System.out.println(", predicted: " + predicted);
				if (actual.equals(predicted))
					match++;
				else
					mismatch++;
			}
			catch (Exception ex) {
				System.err.println(ex.getMessage());
			}
		}
		System.out.println("Agree=" + match + ", disagree=" + mismatch + ", %Agree=" + ((double)match/(double)(match + mismatch)));
		return null;*/
	}
	
	public List<String> classify(List<String> testData, double[] result) {
		String actual;
		String predicted;
		String imgfile;
		String[] tokens;
		int match = 0;
		int mismatch = 0;
		int tp = 0, fp = 0, tn = 0, fn = 0;
		//ArrayList<String> testData = readCSV(filename);
		//testData.remove(0);
		FastVector vector = createVector();
		Instances instances = createInstances(vector, testData.size(), testData);
		for (int i = 0; i < testData.size(); i++) {
			tokens = testData.get(i).split(",");
			imgfile = tokens[0].replace('"', ' ').trim();
			System.out.println(imgfile+":"+tokens[1]+":"+tokens[2]+":"+tokens[3]+","+tokens[4]+","+tokens[5]+","+tokens[6]);
			try {
				double pred = this.model.classifyInstance(instances.instance(i));
				actual = instances.classAttribute().value((int) instances.instance(i).classValue());
				predicted = instances.classAttribute().value((int) pred);
				//instances.instance(i).setClassValue(predicted);
				updateLabel(testData, i, predicted);
				System.out.print("actual: " + actual);
				System.out.println(", predicted: " + predicted);
				if (actual.equals(predicted)) {
					match++;
					if (predicted.equals("true"))
						tp++;
					else
						tn++;
				}
				else {
					mismatch++;
					if (predicted.equals("true"))
						fp++;
					else
						fn++;
				}
			}
			catch (Exception ex) {
				System.err.println(ex.getMessage());
			}
		}
		double acc = ((double)match/(double)(match + mismatch));
		double pre = ((double)tp/(double)(tp + fp));
		double rec = ((double)tp/(double)(tp + fn));
		double f = 2*pre*rec/(pre+rec);
		result[0] += acc;
		result[1] += pre;
		result[2] += rec;
		result[3] += f;
		System.out.println("Agree=" + match + ", disagree=" + mismatch + ", %Agree=" + ((double)match/(double)(match + mismatch)) + ", total instances=" + (match + mismatch));
		System.out.println("TP=" + tp + ", FP=" + fp + ", TN=" + tn + ", FN=" + fn + ", Precision=" + ((double)tp/(double)(tp + fp)) + ", Recall=" + ((double)tp/(double)(tp + fn)));
		return testData;
	}
	
	private static void updateLabel(List<String> data, int i, String label) {
		StringBuilder sb = new StringBuilder();
		String tokens[] = data.get(i).split(",");
		tokens[2] = label;
		for (String token : tokens) {
			sb.append(token.trim());
			sb.append(",");
		}
		data.set(i, sb.substring(0, sb.length() - 1));
	}
	
	public static List<String> convert2csv(Instances instances) {
		List<String> csv = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < instances.numInstances(); i++) {
			for (int j = 0; j < instances.instance(i).numAttributes(); j++) {
			    sb.append(instances.instance(i).toString(j));
			    sb.append(",");
			}
			sb.deleteCharAt(sb.length()-1);
			csv.add(sb.toString());
			sb.delete(0, sb.length());
		}
		return csv;
	}
		
	public void buildClassifier(String filename) {
		List<String> trainningData = FileUtil.readCSV(filename);
		buildKernel(trainningData);
	}
	
	public void buildClassifier(List<String> trainingData) {
		buildKernel(trainingData);
	}
	
	public static List<String> run(List<String> data, int ratioTrain, int ratioTest, int weight, double[] result) {
		List<String> newdata = null;
		int ratio = ratioTrain + ratioTest;
		int trainsize = ratioTrain * data.size() / ratio;
		long seed = System.nanoTime();
		Collections.shuffle(data, new Random(seed));
		WekaLearn weka = new WekaLearn();
		weka.buildClassifier(data.subList(0, trainsize));
		if (ratioTrain < ratio) {
			newdata = weka.classify(data.subList(trainsize, data.size() - 1), result);
			for (int i = 0; i < weight; i++)
			    newdata.addAll(data.subList(0, trainsize)); //weighting*/
		}
		return newdata;
	}
	
	public static void main(String[] args) {
		List<String> data = FileUtil.readCSV("/Users/toshihirokuboi/Workspace/eDetection_v2_1/src/featureSet1.csv");
		data.remove(0);
		int trials = 5;
		double[] result1 = {0,0,0,0};
		double[] result2 = {0,0,0,0};
		Boolean semi = true;
		if (!semi) {
			List<String> data2 = FileUtil.readCSV("/Users/toshihirokuboi/Workspace/eDetection_v2_1/src/featureSet3.csv");
			data2.remove(0);
			data.addAll(data2);
		}
		
		for (int i=0; i < trials; i++) {
			List<String> newdata = WekaLearn.run(data, 1, 1, 1, result1);
			if (semi) {
				FileUtil.writeCSV(newdata, "SemiSVTrainingSet.csv");
				System.out.println("Testing Semi Supervised Learning");
				WekaLearn model = new WekaLearn();
				model.buildClassifier(newdata);
				model.classify("/Users/toshihirokuboi/Workspace/eDetection_v2_1/src/featureSet3.csv", result2);
			}
		}
		if (!semi)
		    System.out.println("Acc=" + result1[0]/(double)trials + ", Prec=" + result1[1]/(double)trials + ", Recall=" + result1[2]/(double)trials + ", F=" + result1[3]/(double)trials);
		else
		    System.out.println("Acc=" + result2[0]/(double)trials + ", Prec=" + result2[1]/(double)trials + ", Recall=" + result2[2]/(double)trials + ", F=" + result2[3]/(double)trials);
		//int ratio = 3;
		//int trainsize = 2 * data.size() / ratio;
		//long seed = System.nanoTime();
		//Collections.shuffle(data, new Random(seed));
		//WekaLearn weka = new WekaLearn();
		//weka.buildClassifier(data.subList(0, trainsize));
		//weka.classify(data.subList(trainsize, data.size() - 1));
		//weka.buildClassifier("/Users/toshihirokuboi/Workspace/eDetection_v2_1/src/featureSet1.csv");
		//weka.evaluate("/Users/toshihirokuboi/Workspace/eDetection_v2_1/src/featureSet2.csv");
		//weka.classify("/Users/toshihirokuboi/Workspace/eDetection_v2_1/src/featureSet3.csv");
	}
}
