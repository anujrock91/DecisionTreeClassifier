package Part3;
import java.util.ArrayList;



public class GINISplit {
	
	//Returns an array of Split Values for continuous variables. If Categorical then it returns -9999
	public double[] getGINISplit(String [][] trainingDataSet){
		double [] attributeSplitValue = new double[trainingDataSet[0].length-1];
		if(trainingDataSet.length == 0){
			return attributeSplitValue;
		}
		getGINISplits(trainingDataSet.clone(), attributeSplitValue);
		return attributeSplitValue;
	}
	
	//Fills the array of attributeSplits 
	private void getGINISplits(String [][] dataSet, double [] splits){
		int classLabelCol = (dataSet[0].length-1);
		//For each attribute
		for(int attribute = 0; attribute < (dataSet[0].length-1) ; attribute++){
			ArrayList<String> attributeValues = new ArrayList<>();
			ArrayList<String> classLabels = new ArrayList<>();
			for(int row=0;row<dataSet.length;row++){
				attributeValues.add(dataSet[row][attribute]);
				classLabels.add(dataSet[row][classLabelCol]);				
			}
			//if the attribute is categorical This function should return -9999
			splits[attribute] = getGINIAttributeSplit(attributeValues, classLabels);
		}
	}
	
	//Getting the boundary of attribute -9999 if categorical variable
	private double getGINIAttributeSplit(ArrayList<String> attributeValues, ArrayList<String> classLabel){
		ArrayList<String> tempAttributeValues = (ArrayList<String>)attributeValues.clone();
		ArrayList<String> tempClassLabel = (ArrayList<String>)classLabel.clone();
		ArrayList<ArrayList<String>> tempAttributeLabel = new ArrayList<>();
		if(IsCategoricalAttribute(tempAttributeValues)){
			return -9999;
		}
		else{
			double splitValue = 0;
			CombineAndSort(tempAttributeValues, tempClassLabel, tempAttributeLabel);
			double minGini = Double.MAX_VALUE;
			if(tempAttributeLabel.size() == 1){
				splitValue = Double.parseDouble(tempAttributeLabel.get(0).get(0));
			}
			for(int i=0;i<(tempAttributeLabel.size()-1);i++){
				double giniChildIndex = getGiniOfSplit(tempAttributeLabel, i+1, i+1);
				if(minGini > giniChildIndex){
					minGini = giniChildIndex;
					splitValue = ((Double.parseDouble(tempAttributeLabel.get(i).get(0))+Double.parseDouble(tempAttributeLabel.get(i+1).get(0)))/2);
				}
			}
			return splitValue;
		}
	}
	
	
	
	private void CombineAndSort(ArrayList<String> tempAttributeValues, ArrayList<String> tempClassLabel, ArrayList<ArrayList<String>> tempAttributeLabel) {
		for(int i=0;i<tempAttributeValues.size();i++){
			ArrayList<String> combine = new ArrayList<>();
			combine.add(tempAttributeValues.get(i));
			combine.add(tempClassLabel.get(i));
			tempAttributeLabel.add(combine);
		}
		
		for(int i=0;i<tempAttributeLabel.size()-1;i++){
			for(int j=i+1;j<tempAttributeLabel.size();j++){
				if(Double.parseDouble(tempAttributeLabel.get(j).get(0)) <= Double.parseDouble(tempAttributeLabel.get(i).get(0))){
					ArrayList<String> min = tempAttributeLabel.remove(j);
					tempAttributeLabel.add(i, min);
				}
			}
		}
	}
	
	//Calculating the GINI Index of split
	private double getGiniOfSplit(ArrayList<ArrayList<String>> attributeLabel, int left, int right){
		ArrayList<ArrayList<String>> leftRecords = new ArrayList<>();
		ArrayList<ArrayList<String>> rightRecords = new ArrayList<>();
		for(int i=0;i<left;i++){//Copying the contents that have to go on left
			ArrayList<String> record = new ArrayList<>();
			record.add(attributeLabel.get(i).get(0));
			record.add(attributeLabel.get(i).get(1));
			leftRecords.add(record);
		}
		for(int i=right;i<attributeLabel.size();i++){//Copying the contents that have to go on right
			ArrayList<String> record = new ArrayList<>();
			record.add(attributeLabel.get(i).get(0));
			record.add(attributeLabel.get(i).get(1));
			rightRecords.add(record);
		}
		//Counting the number of 0 and 1 on left
		double count1left = 0;
		double count0left = 0;
		double numLeftRecords = leftRecords.size();
		for(int i=0;i<leftRecords.size();i++){
			if(leftRecords.get(i).get(1).equals("1")){
				++count1left;
			}
			else if(leftRecords.get(i).get(1).equals("0")){
				++count0left;
			}
		}
		double giniLeft = (1-((count1left/numLeftRecords)*(count1left/numLeftRecords))-((count0left/numLeftRecords)*(count0left/numLeftRecords)));
		//Counting the number of 0 and 1 on right
		double count1right = 0;
		double count0right = 0;
		double numRightRecords = rightRecords.size();
		for(int i=0;i<rightRecords.size();i++){
			if(rightRecords.get(i).get(1).equals("1")){
				++count1right;
			}
			else if(rightRecords.get(i).get(1).equals("0")){
				++count0right;
			}
		}
		double giniRight = (1-Math.pow((count1right/numRightRecords), 2) - Math.pow((count0right/numRightRecords), 2));
		double giniChildren = ((numLeftRecords/attributeLabel.size())*giniLeft)+((numRightRecords/attributeLabel.size())*giniRight);
		return giniChildren;
	}
	
	
	private boolean IsCategoricalAttribute(ArrayList<String> list){
		ArrayList<String> temp = (ArrayList<String>)list.clone();
		try{
			Double.parseDouble(temp.get(0));
			return false;
		}
		catch(Exception e){
			return true;
		}
	}
	
}
