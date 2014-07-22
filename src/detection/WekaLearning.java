package detection;

import java.util.List;
import java.util.Random;

import myUtil.FileUtil;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public abstract class WekaLearning {
	protected Classifier model;
	WekaFormat dataCreator;

	public WekaLearning(Classifier model, WekaFormat dataCreator) {
		this.model = model;
		this.dataCreator = dataCreator;
	}
	
	public void setDataCreator(WekaFormat dataCreator) {
		this.dataCreator = dataCreator;
	}
	
	public Classifier exportModel() {
		return model;
	}

	public Instances createData(int size, List<String> strArray) {
		return dataCreator.createInstances(dataCreator.createVector(), strArray);
	}
	
	private void buildKernel(List<String> data) {
		Instances trainningSet = createData(data.size(), data);

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
		Instances instances = createData(testData.size(), testData);
		try {
			Evaluation eval = new Evaluation(instances);
			eval.evaluateModel(model, instances);
			System.out.println(eval.toSummaryString("\nResults\n======\n", false));
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	public void buildClassifier(List<String> trainingData) {
		buildKernel(trainingData);
	}

	public void classify(List<String> testData, double[] result) {
		String actual;
		String predicted;
		String imgfile;
		String[] tokens;
		int match = 0;
		int mismatch = 0;
		int tp = 0, fp = 0, tn = 0, fn = 0;
		Instances instances = createData(testData.size(), testData);
		for (int i = 0; i < testData.size(); i++) {
			tokens = testData.get(i).split(",");
			imgfile = tokens[0].replace('"', ' ').trim();
			System.out.println(imgfile+":"+tokens[1]+":"+tokens[2]+":"+tokens[3]+","+tokens[4]+","+tokens[5]+","+tokens[6]);
			try {
				double pred = this.model.classifyInstance(instances.instance(i));
				actual = instances.classAttribute().value((int) instances.instance(i).classValue());
				predicted = instances.classAttribute().value((int) pred);
				if (isUpdateLabelOn())
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
		double pre = (tp+fp != 0 ? ((double)tp/(double)(tp + fp)) : 0);
		double rec = (tp+fn != 0 ? ((double)tp/(double)(tp + fn)) : 0);
		double f = (pre+rec != 0 ? 2*pre*rec/(pre+rec) : 0);
		result[0] += acc;
		result[1] += pre;
		result[2] += rec;
		result[3] += f;
		System.out.println("Agree=" + match + ", disagree=" + mismatch + ", %Agree=" + ((double)match/(double)(match + mismatch)) + ", total instances=" + (match + mismatch));
		System.out.println("TP=" + tp + ", FP=" + fp + ", TN=" + tn + ", FN=" + fn + ", Precision=" + pre + ", Recall=" + rec);
	}
	
	static void updateLabel(List<String> data, int i, String label) {
		StringBuilder sb = new StringBuilder();
		String tokens[] = data.get(i).split(",");
		tokens[1] = label;
		for (String token : tokens) {
			sb.append(token.trim());
			sb.append(",");
		}
		data.set(i, sb.substring(0, sb.length() - 1));
	}

	boolean isUpdateLabelOn() {
		return false;
	}
}
