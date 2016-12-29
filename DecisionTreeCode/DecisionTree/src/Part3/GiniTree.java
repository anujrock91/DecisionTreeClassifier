package Part3;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;

public class GiniTree {
	
	private String [] categoricalSplits;
	public Node constructTree(String[][] trainData){
		GINISplit obj = new GINISplit();
		double[]attSplit = obj.getGINISplit(trainData);
		return createTree(attSplit, trainData);
	}
	
	public Node createTree(double [] attributeSplit, String[][] trainData){
		Node node = new Node();
		categoricalSplits = new String[attributeSplit.length];
		int index = getAttribute(attributeSplit, trainData);
		if(index == -1){
			node.leftClassPredict = ""+trainData[0][trainData[0].length-1];
			node.rightClassPredict = ""+trainData[0][trainData[0].length-1];
			node.splitValue = null;
		}
		else{
			node.attributeIndex = index;
			if(attributeSplit[index] == -9999.0){
				node.splitValue = ""+categoricalSplits[index];
			}
			else{
				node.splitValue = ""+attributeSplit[index];
			}
			
			String [][] left = getDividedTrainingData(trainData, index, node.splitValue, "<");
			String [][] right = getDividedTrainingData(trainData, index, node.splitValue, ">=");
			if(isSingleClass(left)){
				node.leftClassPredict = left[0][left[0].length-1];
				node.leftChild = null;
			}
			else{
				GINISplit newObj = new GINISplit();
				double[] leftSplits = newObj.getGINISplit(left);
				node.leftChild = createTree(leftSplits, left);
			}
			if(isSingleClass(right)){
				node.rightClassPredict = right[0][right[0].length-1];
				node.rightChild = null;
			}
			else{
				GINISplit newObj = new GINISplit();
				double [] rightSplits = newObj.getGINISplit(right);
				node.rightChild = createTree(rightSplits, right);
			}
		}
		return node;
	}
	
	
	//if all records belong to same class
	private boolean isSingleClass(String [][] mat){
		int classLabelIndex = (mat[0].length-1);
		int count1 = 0;
		int count0 = 0;
		for(int i=0;i<mat.length;i++){
			if(mat[i][classLabelIndex].equals("1")){
				++count1;
			}
			else if(mat[i][classLabelIndex].equals("0")){
				++count0;
			}
		}
		if(count0 != 0 && count1 != 0){
			return false;
		}
		else{
			return true;
		}
	}
	
	
	//Dividing the training data into twoParts
	private String[][] getDividedTrainingData(String [][] trainData, int attNum, String splitVal, String sign){
		//if categorical
		if(isCategrical(splitVal)){
			ArrayList<Integer> indexes = new ArrayList<>();
			if(sign.trim().equals("<")){
				for(int i=0;i<trainData.length;i++){
					if(!trainData[i][attNum].equalsIgnoreCase(splitVal)){
						indexes.add(i);
					}
				}
			}
			else if(sign.trim().equals(">=")){
				for(int i=0;i<trainData.length;i++){
					if(trainData[i][attNum].equalsIgnoreCase(splitVal)){
						indexes.add(i);
					}
				}
			}
			String [][] newTrainMat = new String[indexes.size()][trainData[0].length];
			int row = 0;
			for(Integer i: indexes){
				for(int col=0;col<trainData[0].length;col++){
					newTrainMat[row][col] = trainData[i][col];
				}
				++row;
			}
			return newTrainMat;
		}
		//if Not categorical
		else{
			ArrayList<Integer> indexes = new ArrayList<>();
			if(sign.trim().equals("<")){
				for(int i=0;i<trainData.length;i++){
					if(Double.parseDouble(trainData[i][attNum]) < Double.parseDouble(splitVal)){
						indexes.add(i);
					}
				}
			}
			else if(sign.trim().equals(">=")){
				for(int i=0;i<trainData.length;i++){
					if(Double.parseDouble(trainData[i][attNum]) >= Double.parseDouble(splitVal)){
						indexes.add(i);
					}
				}
			}
			String [][] newTrainMat = new String[indexes.size()][trainData[0].length];
			int row = 0;
			for(Integer i: indexes){
				for(int col=0;col<trainData[0].length;col++){
					newTrainMat[row][col] = trainData[i][col];
				}
				++row;
			}
			return newTrainMat;
		}
	}
	
	
	//Getting the attribute with the maximum information Gain
	private int getAttribute(double [] attributeSplitArr, String [][] trainSet){
		double maxInfoGain = Double.MIN_VALUE;
		int attributeToConsider = -1;
		for(int eachAtt = 0; eachAtt < attributeSplitArr.length; eachAtt++){
			if(attributeSplitArr[eachAtt] == -99.0){
				continue;
			}
			else{
				double infoGain = getInfoGain(attributeSplitArr, eachAtt, trainSet.clone());
				if((infoGain > maxInfoGain) &&(infoGain != -99.0)){
					maxInfoGain = infoGain;
					attributeToConsider = eachAtt;
				}
				//if infoGain is -99 means that there is no use of this attribute. Mark this as visited so that it is no more used
				if(infoGain == -99.0){
					attributeSplitArr[eachAtt] = -99;
				}
			}
		}
		return attributeToConsider;
	}
	
	//Getting the information Gain for an attribute
	private double getInfoGain(double [] attributeSplit, int attrbuteNum, String[][] trainSet){
		double split = attributeSplit[attrbuteNum];
		//if Categorical Variable
		if(split == -9999.0){
			return getInfoGain(attrbuteNum, trainSet);
		}
		else{
			ArrayList<String> leftSide = new ArrayList<>();
			ArrayList<String> leftLabels = new ArrayList<>();
			ArrayList<String> rightSide = new ArrayList<>();
			ArrayList<String> rightLabels = new ArrayList<>();
			fillDataAccordingToSplit(leftSide, leftLabels, attrbuteNum, split, "<", trainSet);
			fillDataAccordingToSplit(rightSide, rightLabels, attrbuteNum, split, ">=", trainSet);
			return getInfoGain(leftLabels, rightLabels, trainSet);
		}
	}
	
	
	//Method Overloading for InfoGain
	private double getInfoGain(ArrayList<String> leftLabels, ArrayList<String> rightlabels, String [][] trainSet){
		double countleft1s = 0;
		double countleft0s = 0;
		double countright1s = 0;
		double countright0s = 0;
		double recordLeft = leftLabels.size();
		double recordRight = rightlabels.size();
		double sizeOfTrain = trainSet.length;
		for(int i=0;i<leftLabels.size();i++){
			if(Integer.parseInt(leftLabels.get(i)) == 1){
				++countleft1s;
			}else if(Integer.parseInt(leftLabels.get(i)) == 0){
				++countleft0s;
			}
		}
		for(int i=0;i<rightlabels.size();i++){
			if(Integer.parseInt(rightlabels.get(i)) == 1){
				++countright1s;
			}else if(Integer.parseInt(rightlabels.get(i)) == 0){
				++countright0s;
			}
		}
		if(leftLabels.size() != 0 && rightlabels.size() != 0){
			double giniLeft = (1-Math.pow((countleft0s/recordLeft), 2)-Math.pow((countleft1s/recordLeft), 2));
			double giniRight = (1-Math.pow((countright0s/recordRight), 2)-Math.pow((countright1s/recordRight), 2));
			double giniChildren = (((recordLeft/sizeOfTrain)*(giniLeft)) + ((recordRight/sizeOfTrain)*(giniRight)));
			double giniParent = (1-Math.pow((countleft1s+countright1s)/sizeOfTrain, 2) - Math.pow((countright0s+countleft0s)/sizeOfTrain, 2));
			return (giniParent-giniChildren);
		}
		else{
			//signifies there is no use of this attribute because if does not partition at all 
			return -99;
		}
	}
	
	
	
	//Method Overloading for categorical Info Gain
	private double getInfoGain(int attNum, String [][] trainData){
		String bestSplit = getBestSplitForCategorical(trainData, attNum);
		categoricalSplits[attNum] = bestSplit;
		if(bestSplit == null){ // means that there is no split possible that can partition the data.So this attribute is futile
			return -99.0;
		}
		ArrayList<String> sLabel = new ArrayList<>();
		ArrayList<String> restLabel = new ArrayList<>();
		ArrayList<String> totalLabels = new ArrayList<>();
		for(int row=0; row<trainData.length;row++){
			totalLabels.add(trainData[row][trainData[0].length-1]);
			if(trainData[row][attNum].equals(bestSplit)){
				sLabel.add(trainData[row][trainData[0].length-1]);
			}
			else{
				restLabel.add(trainData[row][trainData[0].length-1]);
			}
		}
		double giniS = getGiniCategorical(sLabel);
		double giniRest = getGiniCategorical(restLabel);
		double parentGini = getGiniCategorical(totalLabels);
		double childGini = getChildGini(giniS, giniRest, sLabel.size(), restLabel.size());
		return parentGini-childGini;
	} 
	
	
	//getting the best Split for categorical variable
	private String getBestSplitForCategorical(String [][] trainData, int attNum){
		HashSet<String> values= new HashSet<>();
		for(int row = 0;row<trainData.length;row++){
			values.add(trainData[row][attNum]);
		}
		double minGini = Double.MAX_VALUE;
		String splitString = null;
		if(values.size() == 1 || trainData.length == 0){
			splitString = null;
		}
		for(String s: values){
			ArrayList<String> sLabels = new ArrayList<>();
			ArrayList<String> restLabels = new ArrayList<>();
			for(int row=0;row<trainData.length;row++){
				if(trainData[row][attNum].equals(s)){
					sLabels.add(trainData[row][trainData[0].length-1]);
				}
				else{
					restLabels.add(trainData[row][trainData[0].length-1]);
				}
			}
			double sSize = sLabels.size();
			double restSize = restLabels.size();
			double gini1 = getGiniCategorical(sLabels); //returns -99.0 if all records go to one side, so that case is absurd
			double gini2 = getGiniCategorical(restLabels);
			if(gini1 == -99.0 || gini2 == -99.0){
				continue;
			}
			else{
				double childGini = getChildGini(gini1, gini2, sSize, restSize);
				if(minGini > childGini){
					minGini = childGini;
					splitString = s;
				}
			}
		}
		return splitString;
	}
	
	
	//get Child Gini Index
	private double getChildGini(double gini1, double gini2, double sSize, double restSize){
		double childGini = 0;
		double totalRecord = sSize+restSize;
		childGini = (((sSize/totalRecord)*(gini1)) + ((restSize/totalRecord)*(gini2)) );
		return childGini;
	}
	
	
	//get Gini for categorical variables
	private double getGiniCategorical(ArrayList<String> list){
		double count1s = 0;
		double count0s = 0;
		for(int i=0 ;i<list.size();i++){
			if(list.get(i).equals("1")){
				++count1s;
			}
			else if(list.get(i).equals("0")){
				++count0s;
			}
		}
		if(list.size() == 0){
			return -99.0;
		}
		else{
			double gini = (1- Math.pow(count1s/list.size(), 2) - Math.pow(count0s/list.size(), 2) );
			return gini;
		}
		
	}
	
	
	
	//Filling the data as per the split
	private void fillDataAccordingToSplit(ArrayList<String> value, ArrayList<String> label, int numAtt, double split, String sign ,String [][] trainSet){
		if(sign.trim().equals("<")){
			for(int row=0; row<trainSet.length; row++){
				if(Double.parseDouble(trainSet[row][numAtt]) < split){
					value.add(trainSet[row][numAtt]);
					label.add(trainSet[row][trainSet[0].length-1]);
				}
			}
		}
		else if(sign.trim().equals(">=")){
			for(int row=0; row<trainSet.length; row++){
				if(Double.parseDouble(trainSet[row][numAtt]) >= split){
					value.add(trainSet[row][numAtt]);
					label.add(trainSet[row][trainSet[0].length-1]);
				}
			}
		}
	}
	
	
	//is a categorical Variable
	private boolean isCategrical(String val){
		try{
			Double.parseDouble(val);
			return false;
		}
		catch(Exception e){
			return true;
		}
	}
}
