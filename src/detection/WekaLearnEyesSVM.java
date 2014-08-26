package detection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import myUtil.FileUtil;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.core.Instances;

public class WekaLearnEyesSVM extends WekaLearning {
	
	public WekaLearnEyesSVM(Classifier model, WekaFormat dataCreator) {
		super(model, dataCreator);
	}

	public List<String> classify(String filename, double[] result) {
		// TODO Auto-generated method stub
		List<String> testData = FileUtil.readCSV(filename);
		testData.remove(0);
		return classify(testData, result);
	}
	
	public static List<String> run(List<String> data, int ratioTrain, int ratioTest, int weight, double[] result) {
		List<String> newdata = new ArrayList<String>();
		int total = ratioTrain + ratioTest;
		int trainsize = ratioTrain * data.size() / total;
		//long seed = System.nanoTime();
		//Collections.shuffle(data, new Random(seed));
		SMO scheme = new SMO();
		try {
			scheme.setOptions(weka.core.Utils.splitOptions("-C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\""));
			//scheme.setOptions(weka.core.Utils.splitOptions("-C 4.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.RBFKernel -C 250007 -G 1.0\""));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		WekaLearning weka = new WekaLearnEyesSVM(scheme, new WekaDataEyesCombFeat());
		weka.buildClassifier(data.subList(0, trainsize));
		if (ratioTrain < total) {
			newdata = weka.classify(data.subList(trainsize, data.size() - 1), result);
			for (int i = 0; i < weight; i++)
			    newdata.addAll(data.subList(0, trainsize)); //weighting
		}
		System.out.println("trainsize=" + trainsize + ", total=" + data.size());
		return newdata;
	}
	
	@Override
	boolean isUpdateLabelOn() {
		return false;
	}
	
	public static void main(String[] args) {
		List<String> data = FileUtil.readCSV("/Users/toshihirokuboi/Workspace/eDetection_v2_1/src/eyeCandidatesInfo1.txt");
		List<String> newdata = null;
		String header = data.remove(0);
		int trials = 10;
		double[] result1 = {0,0,0,0};
		double[] result2 = {0,0,0,0};
		//Boolean semi = false;
		int learning = 2; //0:supervised, 1:semi, 2:ensemble
		if (learning == 0) {
			List<String> data2 = FileUtil.readCSV("/Users/toshihirokuboi/Workspace/eDetection_v2_1/src/eyeCandidatesInfo2.txt");
			data2.remove(0);
			data.addAll(data2);
		}
		
		long seed = System.nanoTime();
		Random random = new Random(seed);
		if (learning != 2) {
			for (int i=0; i < trials; i++) {
				Collections.shuffle(data, random);
				newdata = WekaLearnEyesSVM.run(data, 2, 1, 1, result1);
				if (learning == 1) {
					//FileUtil.writeCSV(newdata, "SemiSVTrainingSet.csv");
					System.out.println("Testing Semi Supervised Learning");
					SMO scheme = new SMO();
					try {
						scheme.setOptions(weka.core.Utils.splitOptions("-C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\""));
						//scheme.setOptions(weka.core.Utils.splitOptions("-C 4.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.RBFKernel -C 250007 -G 1.0\""));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					WekaLearnEyesSVM model = new WekaLearnEyesSVM(scheme, new WekaDataEyesCombFeat());

					model.buildClassifier(newdata);
					model.classify("/Users/toshihirokuboi/Workspace/eDetection_v2_1/src/eyeCandidatesInfo2.txt", result2);
				}
			}
		}
		else {
			System.out.println("Testing Semi Supervised Learning");
			SMO scheme = new SMO();
			try {
				scheme.setOptions(weka.core.Utils.splitOptions("-C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\""));
				//scheme.setOptions(weka.core.Utils.splitOptions("-C 4.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.RBFKernel -C 250007 -G 1.0\""));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			WekaLearnEyesSVM model = new WekaLearnEyesSVM(scheme, new WekaDataEyesCombFeat());

			model.buildClassifier(data);
			List<String> labels = new ArrayList<String>();
			labels.add(header);
			labels.addAll(model.classify("/Users/toshihirokuboi/Workspace/eDetection_v2_1/src/eyeCandidatesInfo2.txt", result2));
			FileUtil.writeCSV(labels, "eyelabels_SVM.csv");
		}
		if (learning == 0)
		    System.out.println("Acc=" + result1[0]/(double)trials + ", Prec=" + result1[1]/(double)trials + ", Recall=" + result1[2]/(double)trials + ", F=" + result1[3]/(double)trials);
		else if (learning == 1)
		    System.out.println("Acc=" + result2[0]/(double)trials + ", Prec=" + result2[1]/(double)trials + ", Recall=" + result2[2]/(double)trials + ", F=" + result2[3]/(double)trials);
		else if (learning == 2)
		    System.out.println("Acc=" + result2[0] + ", Prec=" + result2[1] + ", Recall=" + result2[2] + ", F=" + result2[3]);
	}

}
