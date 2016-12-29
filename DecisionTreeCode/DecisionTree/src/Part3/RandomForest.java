package Part3;
import java.util.ArrayList;

public class RandomForest {
	
	public ArrayList<String> getLabelsForRandomForest(String[][] trainData, String [][]testData){
		ArrayList<ArrayList<String>> labelsForEachTree = new ArrayList<>();
		ArrayList<String[][]> allTrainingSets = new ArrayList<>();
		int numOfTrees = (int)Math.sqrt(trainData.length);
		getAllTrainingSets(allTrainingSets, numOfTrees, trainData);
		System.out.println("Started Random Forest");
		for(String [][] mat: allTrainingSets){
			ExecuteDecisionTree obj = new ExecuteDecisionTree();
			Node root = obj.constructTree(mat);
			labelsForEachTree.add((ArrayList<String>)obj.predictLabels(testData, root).clone());
		}
		System.out.println("Completed Random Forest");
		ArrayList<String> predictedLabels = new ArrayList<>();
		System.out.println("Started Bagging");
		predictedLabels = (ArrayList<String>)getBaggedResult(labelsForEachTree).clone();
		System.out.println("Completed Bagging");
		return predictedLabels;
	}
	
	
	private ArrayList<String> getBaggedResult(ArrayList<ArrayList<String>> labels){
		ArrayList<String> result = new ArrayList<>();
		//For each attribute
		for(int attNum=0;attNum<labels.get(0).size();attNum++){
			int count1 = 0;
			int count0 = 0;
			for(int eachList = 0; eachList<labels.size(); eachList++){
				if(labels.get(eachList).get(attNum).equals("1")){
					++count1;
				}
				else if(labels.get(eachList).get(attNum).equals("0")){
					++count0;
				}
			}
			if(count1 > count0){result.add(""+1);}
			else if(count1<count0){result.add(""+0);}
			else{result.add(""+1);}
		}
		return result;
	}
	
	private void getAllTrainingSets( ArrayList<String[][]>trainingSets, int numTrees, String [][] trainMat ){
		int rowsPerTrainSet = (trainMat.length/numTrees);
		int remainingRows = (trainMat.length%numTrees);
		String[][] firstMat = new String[rowsPerTrainSet+remainingRows][trainMat[0].length];
		int nextStart = 0;
		for(nextStart=0;nextStart<(rowsPerTrainSet+remainingRows); nextStart++){
			for(int col=0;col<trainMat[0].length;col++){
				firstMat[nextStart][col] = trainMat[nextStart][col];
			}
		}
		trainingSets.add(firstMat);
		--numTrees;
		System.out.println("Got the first Training Set");
		for(int i=numTrees;i>0;i--){
			String [][] newSet = new String[rowsPerTrainSet+remainingRows][trainMat[0].length];
			int newSetRow = 0;
			//Filling the rows of new Matrix leaving the last rows empty i.e remaining Rows
			for(int row=nextStart;row<(nextStart+rowsPerTrainSet);row++){
				for(int col=0;col<trainMat[0].length;col++){
					newSet[newSetRow][col] = trainMat[row][col];
				}
				++newSetRow;
			}
			//Filling the last rows by repeating data from top
			for(int k=0;k<remainingRows;k++){
				for(int col=0;col<trainMat[0].length;col++){
					newSet[newSet.length-k-1][col] = newSet[k][col];
				}
			}
			trainingSets.add(newSet);
			nextStart = nextStart+rowsPerTrainSet;
			if(nextStart >= trainMat.length){
				break;
			}	
		}
		System.out.println("Got all training partitions");
	}
	
}
