package Part3;
import java.io.IOException;
import java.util.ArrayList;

public class TestCode {

	public static void main(String[] args) throws IOException {
		KFold obj1 = new KFold();
		obj1.runKFoldForest("B:/UB_CS/DataMining/Project3/Data/project3_dataset1.txt", 15);
		
		KFold obj2 = new KFold();
		obj2.runKFoldDecisionTree("B:/UB_CS/DataMining/Project3/Data/project3_dataset4.txt", 20);
		
		ReadFile rf = new ReadFile("B:/UB_CS/DataMining/Project3/Data/project3_dataset4.txt", 0.7);
		String[][] train = rf.getTrainingData();
		String [][] test = rf.getTestData();
		ExecuteDecisionTree tree = new  ExecuteDecisionTree();
		Node root = tree.constructTree(train);
		ArrayList<String> label = tree.predictLabels(test, root);
		System.out.println("Predicted LabelsList : " + label);
		tree.displayTree(root);
		Measures m = new Measures(label, test);
		m.displayMeasures();
		
		ReadFile rfObj = new ReadFile("B:/UB_CS/DataMining/Project3/Data/project3_dataset4.txt", 0.7);
		String [][] forestTrain = rfObj.getTrainingData();
		String [][] forestTest = rfObj.getTestData();
		RandomForest forest = new RandomForest();
		ArrayList<String> labelForest = forest.getLabelsForRandomForest(forestTrain, forestTest);
		System.out.println("Predicted labels : " + labelForest);
		Measures mForest = new Measures(labelForest, forestTest);
		mForest.displayMeasures();
		/*ReadFile rf = new ReadFile("B:/UB_CS/DataMining/Project3/Data/project3_dataset4.txt", 0.7);
		String[][] train = rf.getTrainingData();
		String [][] test = rf.getTestData();
		ExecuteDecisionTree tree = new  ExecuteDecisionTree();
		Node root = tree.constructTree(train);
		ArrayList<String> label = tree.predictLabels(test, root);
		System.out.println("Predicted LabelsList : " + label);
		tree.displayTree(root);
		Measures m = new Measures(label, test);
		m.displayMeasures();*/
	/*	ReadFile rf = new ReadFile("B:/UB_CS/DataMining/Project3/Data/project3_dataset4.txt", 0.7);
		String [][] train = rf.getTrainingData();
		String [][] test = rf.getTestData();
		RandomForest forest = new RandomForest();
		ArrayList<String> labels = forest.getLabelsForRandomForest(train, test);
		Measures m = new  Measures(labels, test);
		m.displayMeasures();*/
		/*KFold obj = new KFold();
		obj.runKFoldDecisionTree("B:/UB_CS/DataMining/Project3/Data/project3_dataset4.txt", 20);*/
		/*KFold obj = new KFold();
		obj.runKFoldForest("B:/UB_CS/DataMining/Project3/Data/project3_dataset1.txt", 10);*/
	}
	
}
