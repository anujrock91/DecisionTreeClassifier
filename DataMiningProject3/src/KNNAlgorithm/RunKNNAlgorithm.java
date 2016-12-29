package KNNAlgorithm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class RunKNNAlgorithm {

	public static List<Sample> samples = new ArrayList<Sample>();
	public static String inputFileName = null;
	public static String trainDataFile = null;
	public static String testDataFile = null;
	public static Integer noOfNearestNeighbours = null;
	public static List<Sample> trainingData = new ArrayList<Sample>();
	public static List<Sample> testData = new ArrayList<Sample>();
	public static Integer demoTrainDataSize = null;
	public static Integer fold = null;
	public static Integer iterationNum = 1;
	public static Map<Integer, Double> accuracyMap = new LinkedHashMap<Integer, Double>();
	public static Map<Integer, Double> precisionMap = new LinkedHashMap<Integer, Double>();
	public static Map<Integer, Double> recallMap = new LinkedHashMap<Integer, Double>();
	public static Map<Integer, Double> fMeasureMap = new LinkedHashMap<Integer, Double>();
	public static Map<Integer, Integer> actualLabelDictionary = new LinkedHashMap<Integer, Integer>();
	public static String votingScheme = null;
	public static String partition = null;

	public static void main(String[] args) {
		loadKMeansInputParams();
		if ((trainDataFile != null) && (testDataFile != null)) {
			populateSampleDataForDemo();
			normalizeInputData();
			populateActualLabelDictionary();
			findKNearestNeighboursForDemoData();
		} else {
			populateSampleData();
			normalizeInputData();
			populateActualLabelDictionary();
			if(partition.equals("KFold")){
				findKNearestNeighboursWithKFold();
			} else {
				findKNearestNeighboursForRandomShuffling();
			}
		}
	}

	private static void populateActualLabelDictionary() {

		for (int i = 0; i < samples.size(); i++) {
			Integer sampleId = samples.get(i).getSampleId();
			Integer actualLabel = samples.get(i).getClassLabel();
			actualLabelDictionary.put(sampleId, actualLabel);
		}

	}

	private static void loadKMeansInputParams() {
		InputStream inputStream = null;

		try {
			Properties prop = new Properties();
			String propFileName = "config.properties";

			inputStream = RunKNNAlgorithm.class.getClassLoader().getResourceAsStream(propFileName);

			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("Property file '" + propFileName + "' not found in the classpath:");
			}

			// read and fetch input params here
			inputFileName = prop.getProperty("inputFileName");
			trainDataFile = prop.getProperty("trainDataFile");
			testDataFile = prop.getProperty("testDataFile");
			noOfNearestNeighbours = Integer.parseInt(prop.getProperty("noOfNearestNeighbours"));
			fold = Integer.parseInt(prop.getProperty("fold"));
			votingScheme = prop.getProperty("votingScheme");
			partition = prop.getProperty("partition");
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	// populating samples data from input files
	private static void populateSampleData() {
		BufferedReader buf = null;
		try {
			buf = new BufferedReader(new FileReader(inputFileName));
			String line;
			String[] lineTermsArr;
			Integer lineNum = 1;

			while ((line = buf.readLine()) != null) {
				Sample sample = new Sample();
				List<Double> numValues = new ArrayList<Double>();
				List<String> catValues = new ArrayList<String>();
				Integer column = 0;
				lineTermsArr = line.split("\t");
				Integer labelIndex = ((lineTermsArr.length) - 1);
				for (String term : lineTermsArr) {
					if (!"".equals(term)) {
						if (column == labelIndex) {
							sample.setClassLabel(Integer.parseInt(term));
						} else if (isNumeric(term)) {
							numValues.add(Double.parseDouble(term));
						} else {
							catValues.add(term);
						}
					}
					column++;
				}
				sample.setSampleId(lineNum);
				sample.setNumValues(numValues);
				sample.setCatValues(catValues);
				samples.add(sample);
				lineNum++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (buf != null) {
				try {
					buf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private static boolean isNumeric(String str) {
		try {
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	private static void findKNearestNeighboursForRandomShuffling() {
		List<Sample> sampleTrainData = new ArrayList<Sample>();
		List<Sample> sampleTestData = new ArrayList<Sample>();
		double temp = 1.0;
		double testDataPart = (temp / fold);
		double trainDataPart = 1 - testDataPart;

		Collections.shuffle(samples);
		Integer trainDataSize = (int) ((trainDataPart) * samples.size());

		for (int i = 0; i <= trainDataSize; i++) {
			sampleTrainData.add(samples.get(i));
		}

		for (int i = (trainDataSize + 1); i < samples.size(); i++) {
			sampleTestData.add(samples.get(i));
		}

		trainingData = sampleTrainData;
		testData = sampleTestData;
		System.out.println(trainingData.size());
		System.out.println(testData.size());

		KNearestNeighbours kNearestNeighbours = new KNearestNeighbours();
		kNearestNeighbours.findKNearestNeighbours();
	}

	private static void findKNearestNeighboursWithKFold() {
		double temp = 1.0;
		double testDataPart = (temp / fold);
		double trainDataPart = 1 - testDataPart;
		Integer trainDataSize = (int) ((trainDataPart) * samples.size());
		Integer testDataSize = samples.size() - trainDataSize;
		Integer testDataIndex = 0;

		for (int i = 0; i < fold; i++) {
			List<Sample> sampleInputData = new ArrayList<Sample>();
			List<Sample> sampleTrainData = new ArrayList<Sample>();
			List<Sample> sampleTestData = new ArrayList<Sample>();

			sampleInputData.addAll(samples);

			Integer temp1 = 0;
			while (temp1 < (testDataSize - 1)) {
				if (testDataIndex < sampleInputData.size()) {
					sampleTestData.add(sampleInputData.get(testDataIndex));
					testDataIndex++;
					temp1++;
				} else {
					break;
				}
			}

			if (sampleInputData.removeAll(sampleTestData)) {
				sampleTrainData.addAll(sampleInputData);
			}

			trainingData = sampleTrainData;
			testData = sampleTestData;

			KNearestNeighbours kNearestNeighbours = new KNearestNeighbours();
			kNearestNeighbours.findKNearestNeighbours();
			iterationNum++;
		}
	}

	private static void normalizeInputData() {
		Integer lenght1 = samples.get(0).getNumValues().size();
		Integer length2 = samples.size();
		double[] minValArr = new double[lenght1];
		double[] maxValArr = new double[lenght1];

		for (int i = 0; i < lenght1; i++) {
			Double minVal = Double.MAX_VALUE;
			Double maxVal = Double.MIN_VALUE;
			for (int j = 0; j < length2; j++) {
				Sample sample = samples.get(j);
				Double numVal = sample.getNumValues().get(i);
				if (numVal < minVal) {
					minVal = numVal;
				}

				if (numVal > maxVal) {
					maxVal = numVal;
				}

			}

			minValArr[i] = minVal;
			maxValArr[i] = maxVal;
		}

		for (int i = 0; i < lenght1; i++) {
			Double denominator = maxValArr[i] - minValArr[i];
			for (int j = 0; j < length2; j++) {
				Sample sample = samples.get(j);
				Double numVal = sample.getNumValues().get(i);
				Double numerator = (numVal - minValArr[i]);
				Double finalVal = (numerator / denominator);
				samples.get(j).getNumValues().set(i, finalVal);
			}
		}

	}

	private static void findKNearestNeighboursForDemoData() {
		List<Sample> demoTrainData = new ArrayList<Sample>();
		List<Sample> demoTestData = new ArrayList<Sample>();

		for (int i = 0; i < demoTrainDataSize; i++) {
			demoTrainData.add(samples.get(i));
		}

		for (int i = demoTrainDataSize; i < samples.size(); i++) {
			demoTestData.add(samples.get(i));
		}

		trainingData = demoTrainData;
		testData = demoTestData;

		KNearestNeighbours kNearestNeighbours = new KNearestNeighbours();
		kNearestNeighbours.findKNearestNeighbours();
	}

	// populating samples data from input files for demo purpose
	private static void populateSampleDataForDemo() {
		BufferedReader buf1 = null;
		BufferedReader buf2 = null;
		try {
			buf1 = new BufferedReader(new FileReader(trainDataFile));
			String line1;
			String[] lineTermsArr1;
			Integer lineNum1 = 1;

			while ((line1 = buf1.readLine()) != null) {
				Sample sample = new Sample();
				List<Double> numValues = new ArrayList<Double>();
				List<String> catValues = new ArrayList<String>();
				Integer column = 0;
				lineTermsArr1 = line1.split("\t");
				Integer labelIndex = ((lineTermsArr1.length) - 1);
				for (String term : lineTermsArr1) {
					if (!"".equals(term)) {
						if (column == labelIndex) {
							sample.setClassLabel(Integer.parseInt(term));
						} else if (isNumeric(term)) {
							numValues.add(Double.parseDouble(term));
						} else {
							catValues.add(term);
						}
					}
					column++;
				}
				sample.setSampleId(lineNum1);
				sample.setNumValues(numValues);
				sample.setCatValues(catValues);
				samples.add(sample);
				lineNum1++;
			}

			demoTrainDataSize = lineNum1 - 1;
			buf2 = new BufferedReader(new FileReader(testDataFile));
			String line2;
			String[] lineTermsArr2;
			while ((line2 = buf2.readLine()) != null) {
				Sample sample = new Sample();
				List<Double> numValues = new ArrayList<Double>();
				List<String> catValues = new ArrayList<String>();
				Integer column = 0;
				lineTermsArr2 = line2.split("\t");
				Integer labelIndex = ((lineTermsArr2.length) - 1);
				for (String term : lineTermsArr2) {
					if (!"".equals(term)) {
						if (column == labelIndex) {
							sample.setClassLabel(Integer.parseInt(term));
						} else if (isNumeric(term)) {
							numValues.add(Double.parseDouble(term));
						} else {
							catValues.add(term);
						}
					}
					column++;
				}
				sample.setSampleId(lineNum1);
				sample.setNumValues(numValues);
				sample.setCatValues(catValues);
				samples.add(sample);
				lineNum1++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (buf1 != null || buf2 != null) {
				try {
					buf1.close();
					buf2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
