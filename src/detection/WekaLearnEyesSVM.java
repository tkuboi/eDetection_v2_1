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

	public void classify(String filename, double[] result) {
		// TODO Auto-generated method stub
		List<String> testData = FileUtil.readCSV(filename);
		//testData.remove(0);
		classify(testData, result);
	}
	
	public static List<String> run(List<String> data, int ratioTrain, int ratioTest, int weight, double[] result) {
		List<String> newdata = new ArrayList<String>();
		int total = ratioTrain + ratioTest;
		int trainsize = ratioTrain * data.size() / total;
		//long seed = System.nanoTime();
		//Collections.shuffle(data, new Random(seed));
		SMO scheme = new SMO();
		try {
			//scheme.setOptions(weka.core.Utils.splitOptions("-C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\""));
			scheme.setOptions(weka.core.Utils.splitOptions("-C 2.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.RBFKernel -C 250007 -G 1.0\""));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		WekaLearning weka = new WekaLearnEyesSVM(scheme, new WekaDataEyesCombFeat());
		weka.buildClassifier(data.subList(0, trainsize));
		if (ratioTrain < total) {
			weka.classify(data.subList(trainsize, data.size() - 1), result);
			/*newdata.addAll(data.subList(trainsize, data.size() - 1));
			for (int i = 0; i < weight; i++)
			    newdata.addAll(data.subList(0, trainsize)); //weighting*/
		}
		System.out.println("trainsize=" + trainsize + ", total=" + data.size());
		return newdata;
	}
	
	public static void main(String[] args) {
		List<String> data = FileUtil.readCSV("/Users/toshihirokuboi/Workspace/eDetection_v2_1/src/eyeCandidatesInfo1.txt");
		data.remove(0);
		int trials = 10;
		double[] result1 = {0,0,0,0};
		double[] result2 = {0,0,0,0};
		Boolean semi = false;
		if (!semi) {
			List<String> data2 = FileUtil.readCSV("/Users/toshihirokuboi/Workspace/eDetection_v2_1/src/eyeCandidatesInfo2.txt");
			data2.remove(0);
			data.addAll(data2);
		}
		
		long seed = System.nanoTime();
		Random random = new Random(seed);
		for (int i=0; i < trials; i++) {
			Collections.shuffle(data, random);
			List<String> newdata = WekaLearnEyesSVM.run(data, 2, 1, 1, result1);
			if (semi) {
				FileUtil.writeCSV(newdata, "SemiSVTrainingSet.csv");
				System.out.println("Testing Semi Supervised Learning");
				SMO scheme = new SMO();
				try {
					scheme.setOptions(weka.core.Utils.splitOptions("-C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\""));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				WekaLearnEyesSVM model = new WekaLearnEyesSVM(scheme, new WekaDataEyesCombFeat());
				
				model.buildClassifier(newdata);
				model.classify("/Users/toshihirokuboi/Workspace/eDetection_v2_1/src/featureSet3.csv", result2);
			}
		}
		if (!semi)
		    System.out.println("Acc=" + result1[0]/(double)trials + ", Prec=" + result1[1]/(double)trials + ", Recall=" + result1[2]/(double)trials + ", F=" + result1[3]/(double)trials);
		else
		    System.out.println("Acc=" + result2[0]/(double)trials + ", Prec=" + result2[1]/(double)trials + ", Recall=" + result2[2]/(double)trials + ", F=" + result2[3]/(double)trials);
	}

}
