package KNNAlgorithm;

import java.util.List;

public class Sample {

	private Integer sampleId;
	private List<Double> numValues;
	private List<String> catValues;
	private Integer classLabel;
	private Integer predictedLabel;

	public Integer getSampleId() {
		return sampleId;
	}

	public void setSampleId(Integer sampleId) {
		this.sampleId = sampleId;
	}

	public List<Double> getNumValues() {
		return numValues;
	}

	public void setNumValues(List<Double> numValues) {
		this.numValues = numValues;
	}

	public List<String> getCatValues() {
		return catValues;
	}

	public void setCatValues(List<String> catValues) {
		this.catValues = catValues;
	}

	public Integer getClassLabel() {
		return classLabel;
	}

	public void setClassLabel(Integer classLabel) {
		this.classLabel = classLabel;
	}

	public Integer getPredictedLabel() {
		return predictedLabel;
	}

	public void setPredictedLabel(Integer predictedLabel) {
		this.predictedLabel = predictedLabel;
	}

}
