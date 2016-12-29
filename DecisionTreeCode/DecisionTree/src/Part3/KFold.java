package Part3;
import java.io.IOException;
import java.util.ArrayList;

public class KFold {
	
	public void runKFoldDecisionTree(String filePath, double K) throws IOException{
		double avgAccuracy = 0;
		double avgPrecision = 0;
		double avgRecall = 0;
		double avgFMeasure = 0;
		double fraction = (1-(K/100.0));
		ReadFile rf = new ReadFile(filePath, fraction);
		String [][] training = rf.getTrainingData();
		String [][] testData = rf.getTestData();
		ExecuteDecisionTree obj = new ExecuteDecisionTree();
		Node root = obj.constructTree(training);
		ArrayList<String> labels = (ArrayList<String>) obj.predictLabels(testData, root).clone();
		Measures m = new Measures(labels, testData);
		m.displayMeasures();
		avgAccuracy = avgAccuracy + m.getAccuracy();
		avgPrecision = avgPrecision + m.getPrecision();
		avgRecall = avgRecall + m.getRecall();
		avgFMeasure = avgFMeasure + m.getFMeasure();
		String [][] combinedMat = new String[training.length+testData.length][training[0].length];
		for(int row=0;row<training.length;row++){
			for(int col=0;col<training[0].length;col++){
				combinedMat[row][col] = training[row][col];
			}
		}
		for(int row=0;row<testData.length;row++){
			for(int col=0;col<testData[0].length;col++){
				combinedMat[combinedMat.length-1-row][col] = testData[row][col];
			}
		}
		int contained = (training.length/testData.length);
		int rows = (training.length/contained);
		int countStart = 0;
		int loopCount= 0;
		for(int con=contained;con>0;con--){
			++loopCount;
			String [][] tempTraining = new String[combinedMat.length-rows][training[0].length];
			String [][] tempTest = new String[rows][training[0].length];
			int tempTestRows = 0;
			for(int row=countStart;row<(countStart+rows);row++){
				for(int col=0;col<combinedMat[0].length;col++){
					tempTest[tempTestRows][col] = combinedMat[row][col];
				}
				++tempTestRows;
			}
			int tempTrainingRows = 0;
			//Copy before part
			for(int row=0; row<countStart;row++){
				for(int col=0;col<training[0].length;col++){
					tempTraining[tempTrainingRows][col] = combinedMat[row][col];
				}
				++tempTrainingRows;
			}
			//Copy after part
			for(int row=(countStart+rows); row < combinedMat.length; row++){
				for(int col=0;col<combinedMat[0].length;col++){
					tempTraining[tempTrainingRows][col]  = combinedMat[row][col];
				}
				++tempTrainingRows;
			}
			countStart = countStart+rows;
			ExecuteDecisionTree tree = new ExecuteDecisionTree();
			Node rootTemp = tree.constructTree(tempTraining);
			ArrayList<String> tempLabels = tree.predictLabels(tempTest, rootTemp);
			Measures tempMeasure = new Measures(tempLabels, tempTest);
			tempMeasure.displayMeasures();
			avgAccuracy = avgAccuracy + tempMeasure.getAccuracy();
			avgPrecision = avgPrecision + tempMeasure.getPrecision();
			avgRecall = avgRecall + tempMeasure.getRecall();
			avgFMeasure = avgFMeasure + tempMeasure.getFMeasure();
			combinedMat = combineMatrix(tempTest, tempTraining);
			if(countStart >= training.length){
				break;
			}
		}
		
		System.out.println("Avg Values : ");
		System.out.println("A: " + (avgAccuracy/(loopCount+1)) );
		System.out.println("R: " + (avgRecall/(loopCount+1)) );
		System.out.println("P: " + (avgPrecision/(loopCount+1)) );
		System.out.println("F: " + (avgFMeasure/(loopCount+1)) );
	}
	
	
	public String[][] combineMatrix(String[][] test, String[][] train){
		String [][] result = new String[test.length+train.length][train[0].length];
		int start = 0;
		for(int row=0;row<test.length;row++){
			for(int col=0;col<test[0].length;col++){
				result[start][col] = test[row][col];
			}
			++start;
		}
		for(int row=0;row<train.length;row++){
			for(int col=0;col<train[0].length;col++){
				result[start][col] = train[row][col];
			}
			++start;
		}
		return result;
	}
	
	
	
	public void runKFoldForest(String filePath, int K) throws IOException{
		double avgAccuracy = 0;
		double avgPrecision = 0;
		double avgRecall = 0;
		double avgFMeasure = 0;
		double fraction = (1-(K/100.0));
		ReadFile rf = new ReadFile(filePath, fraction);
		String [][] training = rf.getTrainingData();
		String [][] testData = rf.getTestData();
		/*ExecuteDecisionTree obj = new ExecuteDecisionTree();
		Node root = obj.constructTree(training);
		ArrayList<String> labels = (ArrayList<String>) obj.predictLabels(testData, root).clone();*/
		RandomForest rfTree = new RandomForest();
		ArrayList<String> labels =rfTree.getLabelsForRandomForest(training, testData);
		Measures m = new Measures(labels, testData);
		m.displayMeasures();
		avgAccuracy = avgAccuracy + m.getAccuracy();
		avgPrecision = avgPrecision + m.getPrecision();
		avgRecall = avgRecall + m.getRecall();
		avgFMeasure = avgFMeasure + m.getFMeasure();
		String [][] combinedMat = new String[training.length+testData.length][training[0].length];
		for(int row=0;row<training.length;row++){
			for(int col=0;col<training[0].length;col++){
				combinedMat[row][col] = training[row][col];
			}
		}
		for(int row=0;row<testData.length;row++){
			for(int col=0;col<testData[0].length;col++){
				combinedMat[combinedMat.length-1-row][col] = testData[row][col];
			}
		}
		int contained = (training.length/testData.length);
		int rows = (training.length/contained);
		int countStart = 0;
		int loopCount= 0;
		for(int con=contained;con>0;con--){
			++loopCount;
			String [][] tempTraining = new String[combinedMat.length-rows][training[0].length];
			String [][] tempTest = new String[rows][training[0].length];
			int tempTestRows = 0;
			for(int row=countStart;row<(countStart+rows);row++){
				for(int col=0;col<combinedMat[0].length;col++){
					tempTest[tempTestRows][col] = combinedMat[row][col];
				}
				++tempTestRows;
			}
			int tempTrainingRows = 0;
			//Copy before part
			for(int row=0; row<countStart;row++){
				for(int col=0;col<training[0].length;col++){
					tempTraining[tempTrainingRows][col] = combinedMat[row][col];
				}
				++tempTrainingRows;
			}
			//Copy after part
			for(int row=(countStart+rows); row < combinedMat.length; row++){
				for(int col=0;col<combinedMat[0].length;col++){
					tempTraining[tempTrainingRows][col]  = combinedMat[row][col];
				}
				++tempTrainingRows;
			}
			countStart = countStart+rows;
			/*ExecuteDecisionTree tree = new ExecuteDecisionTree();
			Node rootTemp = tree.constructTree(tempTraining);
			ArrayList<String> tempLabels = tree.predictLabels(tempTest, rootTemp);*/
			RandomForest tempForest = new RandomForest();
			ArrayList<String> tempLabels = tempForest.getLabelsForRandomForest(tempTraining, tempTest);
			Measures tempMeasure = new Measures(tempLabels, tempTest);
			tempMeasure.displayMeasures();
			avgAccuracy = avgAccuracy + tempMeasure.getAccuracy();
			avgPrecision = avgPrecision + tempMeasure.getPrecision();
			avgRecall = avgRecall + tempMeasure.getRecall();
			avgFMeasure = avgFMeasure + tempMeasure.getFMeasure();
			combinedMat = combineMatrix(tempTest, tempTraining);
			if(countStart >= training.length){
				break;
			}
		}
		
		System.out.println("Avg Values : ");
		System.out.println("A: " + (avgAccuracy/(loopCount+1)) );
		System.out.println("R: " + (avgRecall/(loopCount+1)) );
		System.out.println("P: " + (avgPrecision/(loopCount+1)) );
		System.out.println("F: " + (avgFMeasure/(loopCount+1)) );
	}
}
