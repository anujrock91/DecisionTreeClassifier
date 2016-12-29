package KNNAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KNearestNeighbours extends RunKNNAlgorithm {

	private static Map<Integer, List<DistanceLabel>> neighboursMap = new LinkedHashMap<Integer, List<DistanceLabel>>();

	public void findKNearestNeighbours() {

		populateNeighboursForTestData();
		evaluateMeasures();

	}

	private void populateNeighboursForTestData() {
		for (int i = 0; i < testData.size(); i++) {
			populateNeighboursMap(testData.get(i));
		}
	}

	private void populateNeighboursMap(Sample sample) {
		List<DistanceLabel> distanceLabels = new ArrayList<DistanceLabel>();
		Integer testSampleId = sample.getSampleId();

		for (int i = 0; i < trainingData.size(); i++) {
			DistanceLabel distanceLabel = new DistanceLabel();
			Double totalDistance = null;
			Double numericalDistance = null;
			Double categoricalDistance = null;
			Integer trainDataLabel = trainingData.get(i).getClassLabel();

			numericalDistance = calculateEuclideanDistance(trainingData.get(i).getNumValues(), sample.getNumValues());
			categoricalDistance = calculateCategoricalDistance(trainingData.get(i).getCatValues(),
					sample.getCatValues());
			totalDistance = numericalDistance + categoricalDistance;

			distanceLabel.setDistanceFromPoint(totalDistance);
			distanceLabel.setLabel(trainDataLabel);
			distanceLabels.add(distanceLabel);
		}

		neighboursMap.put(testSampleId, distanceLabels);
	}

	private Double calculateEuclideanDistance(List<Double> trainDataDimensions, List<Double> testDimensions) {
		Double euclideanDistance = 0.0;
		Double power = 2.0;

		for (int i = 0; i < trainDataDimensions.size(); i++) {
			Double trainFeatureValue = trainDataDimensions.get(i);
			Double testFeatureValue = testDimensions.get(i);
			Double attributeDiff = Math.abs(trainFeatureValue - testFeatureValue);
			euclideanDistance = euclideanDistance + Math.pow(attributeDiff, power);
		}

		return Math.sqrt(euclideanDistance);
	}

	private Double calculateCategoricalDistance(List<String> trainDataDimensions, List<String> testDimensions) {
		Double categoricalDistance = 0.0;

		for (int i = 0; i < trainDataDimensions.size(); i++) {
			String trainFetaureValue = trainDataDimensions.get(i).trim();
			String testFeatureValue = testDimensions.get(i).trim();

			if (!trainFetaureValue.equalsIgnoreCase(testFeatureValue)) {
				categoricalDistance = categoricalDistance + 1;
			}
		}

		return categoricalDistance;
	}

	private void evaluateMeasures() {
		Map<Integer, Integer> predictedLabelsMap = new LinkedHashMap<Integer, Integer>();
		double accuracy = 0.0;
		double precision = 0.0;
		double recall = 0.0;
		double fMeasure = 0.0;
		double a = 0.0;
		double b = 0.0;
		double c = 0.0;
		double d = 0.0;

		// build predicted labels map for all training datasets one by one
		for (Integer sampleId : neighboursMap.keySet()) {
			Integer majorityLabel = null;

			if (votingScheme.equals("weightedVoting")) {
				majorityLabel = getMajorityLabelWithWeightedVoting(sampleId);
			} else {
				majorityLabel = getMajorityLabelWithMajorityVoting(sampleId);
			}
			/*
			 * System.out.println("SampleID: " + sampleId + ", predictedLabel: "
			 * + majorityLabel + ", actualLabel: " +
			 * actualLabelDictionary.get(sampleId));
			 */
			predictedLabelsMap.put(sampleId, majorityLabel);
		}

		// initialize neighbours map to new linkedhashmap for next iteration of
		// cross fold validation test data set
		neighboursMap = new LinkedHashMap<Integer, List<DistanceLabel>>();

		for (Integer sampleId : predictedLabelsMap.keySet()) {
			Integer actualLabel = actualLabelDictionary.get(sampleId);
			Integer predictedLabel = predictedLabelsMap.get(sampleId);

			if ((actualLabel == 0) && (predictedLabel == 0)) {
				d = d + 1;
			} else if ((actualLabel == 0) && (predictedLabel == 1)) {
				c = c + 1;
			} else if ((actualLabel == 1) && (predictedLabel == 0)) {
				b = b + 1;
			} else if ((actualLabel == 1) && (predictedLabel == 1)) {
				a = a + 1;
			}
		}

		accuracy = ((a + d) / (a + b + c + d));
		precision = ((a) / (a + c));
		recall = ((a) / (a + b));
		double temp = (2 * a);
		fMeasure = ((temp) / (temp + b + c));

		System.out.println("Accuracy: " + accuracy + ", Iteration: " + iterationNum);
		System.out.println("Precision:, " + precision + ", Iteration: " + iterationNum);
		System.out.println("Recall:, " + recall + ", Iteration: " + iterationNum);
		System.out.println("F measure:, " + fMeasure + ", Iteration: " + iterationNum);

		accuracyMap.put(iterationNum, accuracy);
		precisionMap.put(iterationNum, precision);
		recallMap.put(iterationNum, recall);
		fMeasureMap.put(iterationNum, fMeasure);
	}

	private Integer getMajorityLabelWithMajorityVoting(Integer sampleId) {
		List<DistanceLabel> kNearestNeighbours = new ArrayList<DistanceLabel>();
		Integer majorityLabel = null;

		List<DistanceLabel> distanceLabels = neighboursMap.get(sampleId);
		Collections.sort(distanceLabels, new DistanceLabel.DistanceComparator());

		for (int i = 0; i < noOfNearestNeighbours; i++) {
			kNearestNeighbours.add(distanceLabels.get(i));
		}

		// get majority label by majority voting
		Map<Integer, Integer> voteMap = new HashMap<Integer, Integer>();

		for (DistanceLabel label : kNearestNeighbours) {
			Integer value = null;
			Integer key = label.getLabel();

			if (voteMap.containsKey(key)) {
				value = voteMap.get(key);
				value = value + 1;
				voteMap.put(key, value);
			} else {
				value = 1;
				voteMap.put(key, value);
			}

		}

		Integer highestVoteCount = 0;

		for (Integer label : voteMap.keySet()) {
			Integer tempVal = voteMap.get(label);

			if (tempVal > highestVoteCount) {
				highestVoteCount = tempVal;
				majorityLabel = label;
			}
		}

		return majorityLabel;
	}

	private Integer getMajorityLabelWithWeightedVoting(Integer sampleId) {
		List<DistanceLabel> kNearestNeighbours = new ArrayList<DistanceLabel>();
		Integer majorityLabel = null;

		List<DistanceLabel> distanceLabels = neighboursMap.get(sampleId);
		Collections.sort(distanceLabels, new DistanceLabel.DistanceComparator());

		for (int i = 0; i < noOfNearestNeighbours; i++) {
			kNearestNeighbours.add(distanceLabels.get(i));
		}

		// get majority label by majority voting
		Map<Integer, Double> voteMap = new HashMap<Integer, Double>();

		for (DistanceLabel label : kNearestNeighbours) {
			Double value = null;
			Integer key = label.getLabel();
			Double distance = label.getDistanceFromPoint();
			Double inverseSquaredDistance = 1.0 / (Math.pow(distance, 2.0));

			if (voteMap.containsKey(key)) {
				value = voteMap.get(key);
				value = value + inverseSquaredDistance;
				voteMap.put(key, value);
			} else {
				value = inverseSquaredDistance;
				voteMap.put(key, value);
			}

		}

		Double highestVoteCount = 0.0;

		for (Integer label : voteMap.keySet()) {
			Double tempVal = voteMap.get(label);

			if (tempVal > highestVoteCount) {
				highestVoteCount = tempVal;
				majorityLabel = label;
			}
		}

		return majorityLabel;
	}

}
