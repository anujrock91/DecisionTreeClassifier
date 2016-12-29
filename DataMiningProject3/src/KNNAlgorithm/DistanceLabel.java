package KNNAlgorithm;

import java.util.Comparator;

public class DistanceLabel implements Comparable<DistanceLabel> {

	private Double distanceFromPoint;
	private Integer label;

	public Double getDistanceFromPoint() {
		return distanceFromPoint;
	}

	public void setDistanceFromPoint(Double distanceFromPoint) {
		this.distanceFromPoint = distanceFromPoint;
	}

	public Integer getLabel() {
		return label;
	}

	public void setLabel(Integer label) {
		this.label = label;
	}

	@Override
	public int compareTo(DistanceLabel o) {
		if (this.distanceFromPoint > o.getDistanceFromPoint())
			return 1;
		else if (this.distanceFromPoint < o.getDistanceFromPoint())
			return -1;
		return 0;
	}

	static class DistanceComparator implements Comparator<DistanceLabel> {
		public int compare(DistanceLabel dl1, DistanceLabel dl2) {
			return dl1.compareTo(dl2);
		}
	}

}
