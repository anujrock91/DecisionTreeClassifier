package Part3;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Boosting {

	public static Map<Integer, List<Double>> weightsMap = new LinkedHashMap<Integer, List<Double>>();
	public static Map<Integer, Double> errorMap = new LinkedHashMap<Integer, Double>();
	public static Map<Integer, Double> alphaMap = new LinkedHashMap<Integer, Double>();
	public static Map<Integer, List<String>> predictedLabelsMap = new LinkedHashMap<Integer, List<String>>();
	public static ArrayList<Node> nodes = new ArrayList<Node>();
	
	public static void main(String[] args) throws IOException {
		// KFold obj = new KFold();
		// obj.runKFoldForest("B:/UB_CS/DataMining/Project3/Data/project3_dataset1.txt",
		// 10);
		ReadFile rf = new ReadFile("project3_dataset2.txt", 0.9);
		Integer numOfRounds = 10;
		Integer currentRound = 0;
		String[][] train = rf.getTrainingData();
		String[][] test = rf.getTestData();
		
		for (int i = 0; i < numOfRounds; i++) {
			currentRound++;
			if (currentRound == 1) {
				List<Double> initialWeights = new ArrayList<Double>();
				double temp = 1.0;
				double initialWeight = (temp / (train.length));
				for (int j = 0; j < train.length; j++) {
					initialWeights.add(initialWeight);
				}

				weightsMap.put(currentRound, initialWeights);
				ExecuteDecisionTree tree = new ExecuteDecisionTree();
				Node root = tree.constructTree(train);
				nodes.add(root);
				ArrayList<String> labels = tree.predictLabels(train, root);
				predictedLabelsMap.put(currentRound, labels);
				// update weights after first round
				populateErrorsMap(train, currentRound);
				populateAlphaMap(currentRound);
				updateWeightsMap(train, currentRound);
			} else {
				String[][] bootstrapSample = getBootstrapSample(train, currentRound);
				ExecuteDecisionTree tree = new ExecuteDecisionTree();
				Node root = tree.constructTree(bootstrapSample);
				nodes.add(root);
				ArrayList<String> labels = tree.predictLabels(train, root);
				predictedLabelsMap.put(currentRound, labels);
				populateErrorsMap(train, currentRound);
				populateAlphaMap(currentRound);
				updateWeightsMap(train, currentRound);
			}
		}

		printErrorMap();
		printAlphaMap();
		printWeightMap();
		predictOnTest(train);
	}
	
	private static void predictOnTest(String [][] testData){
		int max = Integer.MIN_VALUE;
		double al = Double.MIN_VALUE;
		for(Entry<Integer, Double> e : alphaMap.entrySet()){
			if(e.getValue() > al){
				al = e.getValue();
				max = e.getKey();
			}
		}
		Node bestTreeRoot = nodes.get(max);
		ExecuteDecisionTree exe = new ExecuteDecisionTree();
		ArrayList<String> labels = exe.predictLabels(testData, bestTreeRoot);
		System.out.println("Index of Max alpha" + max);
		System.out.println(labels);
		Measures m = new Measures(labels, testData);
		m.displayMeasures();
	}
	
	private static String[][] getBootstrapSample(String[][] train, Integer roundNumber) {
		List<Double> weights = weightsMap.get(roundNumber);
		double sum = 0;
		for (int i = 0; i < weights.size(); i++) {
			sum += weights.get(i);
		}

		double avgWt = sum / weights.size();
		ArrayList<Integer> maxWtIdx = new ArrayList<Integer>();
		for (int i = 0; i < weights.size(); i++) {
			if (weights.get(i) > avgWt)
				maxWtIdx.add(i);
		}
		String[][] bootSample = new String[maxWtIdx.size()][train[0].length];
		for (int i = 0; i < maxWtIdx.size(); i++) {
			bootSample[i] = train[maxWtIdx.get(i)];
		}
		return bootSample;
	}

	private static void populateErrorsMap(String[][] trainData, Integer roundNumber) {
		List<String> predictedLabels = predictedLabelsMap.get(roundNumber);
		List<Double> weights = weightsMap.get(roundNumber);
		Double numerator = 0.0;
		Double denominator = 0.0;
		Double totalErrorValue = 0.0;

		for (int i = 0; i < predictedLabels.size(); i++) {
			Integer delta = 0;
			String actualLabel = trainData[i][trainData[0].length - 1];
			String predictedLabel = predictedLabels.get(i);
			Double weight = weights.get(i);

			if (predictedLabel != actualLabel) {
				delta = delta + 1;
			}

			numerator = numerator + (weight * delta);
			denominator = denominator + weight;
		}

		totalErrorValue = (numerator / denominator);

		errorMap.put(roundNumber, totalErrorValue);
	}

	private static void populateAlphaMap(Integer roundNumber) {
		Double errorValue = errorMap.get(roundNumber);
		Double alpha = null;
		Double temp1 = 1.0;
		Double temp2 = temp1 / errorValue;
		Double temp3 = (temp2 - temp1);
		Double temp4 = 0.5;
		Double temp5 = Math.log(temp3);
		alpha = (temp4 * temp5);

		alphaMap.put(roundNumber, alpha);
	}

	private static void updateWeightsMap(String[][] trainData, Integer roundNumber) {
		List<String> predictedLabels = predictedLabelsMap.get(roundNumber);
		List<Double> weights = weightsMap.get(roundNumber);
		Double alpha = alphaMap.get(roundNumber);
		Double denominator = 0.0;
		List<Double> updatedWeights = new ArrayList<Double>();

		for (int i = 0; i < weights.size(); i++) {
			denominator = denominator + weights.get(i);
		}

		for (int i = 0; i < predictedLabels.size(); i++) {
			Double numerator = 0.0;
			Double delta = 0.0;
			Double updatedWeight = 0.0;
			String actualLabel = trainData[i][trainData[0].length - 1];
			String predictedLabel = predictedLabels.get(i);
			Double weight = weights.get(i);

			if (predictedLabel != actualLabel) {
				delta = -1.0;
			} else {
				delta = 1.0;
			}

			Double temp1 = -1.0;
			Double temp2 = temp1 * ((alpha * delta));
			Double temp3 = Math.exp(temp2);
			numerator = (weight * temp3);
			updatedWeight = (numerator / denominator);
			updatedWeights.add(updatedWeight);
		}

		weightsMap.put((roundNumber + 1), updatedWeights);
	}

	private static void printErrorMap() {
		for(Integer round: errorMap.keySet()){
			System.out.println("Round: " + round + ", Error: " + errorMap.get(round));
		}
	}
	
	private static void printAlphaMap() {
		for(Integer round: alphaMap.keySet()){
			System.out.println("Round: " + round + ", Aplha: " + alphaMap.get(round));
		}
	}
	
	private static void printWeightMap() {
		for(Integer round: weightsMap.keySet()){
			System.out.println("Round: " + round + ", Weight: " + weightsMap.get(round));
		}
	}

}
