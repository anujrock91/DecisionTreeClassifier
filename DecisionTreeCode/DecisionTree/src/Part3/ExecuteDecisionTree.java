package Part3;
import java.io.IOException;
import java.util.ArrayList;

public class ExecuteDecisionTree {
	
	public Node constructTree(String [][] trainData){
		GiniTree obj = new GiniTree();
		return obj.constructTree(trainData.clone());
	}
	
	public ArrayList<String> predictLabels(String [][] testData, Node root){
		ArrayList<String> labels = new ArrayList<>();
		for(int i=0;i<testData.length;i++){
			labels.add(predictLabel(testData[i], root));
		}
		return labels;
	}	
	
	private String predictLabel(String [] record, Node temp){
		String label = null;
		if(isCategorical(temp.splitValue)){
			//Go on right
			if(record[temp.attributeIndex].equals(temp.splitValue)){
				if(temp.rightChild == null){ label = temp.rightClassPredict; }
				else{ label = predictLabel(record, temp.rightChild); }
			}
			//Go on left
			else{
				if(temp.leftChild == null){ label = temp.leftClassPredict; }
				else{ label = predictLabel(record, temp.leftChild);	}
			}
		} 
		//if not categorical Variable
		else{
			if(Double.parseDouble(record[temp.attributeIndex]) >= Double.parseDouble(temp.splitValue)){//go on right
				if(temp.rightChild == null){label = temp.rightClassPredict;}
				else{label = predictLabel(record, temp.rightChild);}
			}
			else{// go on left
				if(temp.leftChild == null){label = temp.leftClassPredict;}
				else{label = predictLabel(record, temp.leftChild);}
			}
		}
		return label;
	}
	
	private boolean isCategorical(String value){
		try{
			Double.parseDouble(value);
			return false;
		}
		catch(Exception e){
			return true;
		}
	}
	
	
	public void displayTree(Node root){
		ArrayList<ArrayList<Node>> levelOrder = new ArrayList<>();
		doLevelOrder(root, 0, levelOrder);
		System.out.println("If records >= split value goes on right else left");
		System.out.println();
		for(ArrayList<Node> eachLevel : levelOrder){
			for(int i=0;i<eachLevel.size();i++){
				if(eachLevel.get(i) != null){
					System.out.print("|| "+eachLevel.get(i).attributeIndex+":"+ eachLevel.get(i).splitValue+":");
					if(eachLevel.get(i).leftChild == null){System.out.print("leftPredict"+ eachLevel.get(i).leftClassPredict + ":");}
					if(eachLevel.get(i).rightChild == null){System.out.print("rightPredict"+ eachLevel.get(i).rightClassPredict +":");}
				}
				else{
					System.out.print("|| " + eachLevel.get(i));
				}
			}
			System.out.println();
		}
	}
	
	
	//DisplayTree
	private void doLevelOrder(Node temp, int level, ArrayList<ArrayList<Node>> levelArray){
		if(temp == null){}
		else{
			//if this level does not exists
			if((levelArray.size()-1) < level){
				ArrayList<Node> newList = new ArrayList<>();
				newList.add(temp);
				levelArray.add(level,newList);
			}
			//if this level exists
			else{
				ArrayList<Node> levelTemp = levelArray.get(level);
				int flag = 0;
				for(int i=0;i<levelTemp.size();i++){
					if(levelTemp.get(i) == temp){ flag = 1;}
				}
				if(flag == 1){}
				else{levelTemp.add(temp);}
			}
			//check if children level is not already added
			if((levelArray.size()-1) < (level+1)){
				ArrayList<Node> newList = new ArrayList<>();
				newList.add(temp.leftChild);
				newList.add(temp.rightChild);
				levelArray.add(level+1, newList);
			}
			else{
				levelArray.get(level+1).add(temp.leftChild);
				levelArray.get(level+1).add(temp.rightChild);
			}
			doLevelOrder(temp.leftChild, level+1, levelArray);
			doLevelOrder(temp.rightChild, level+1, levelArray);
		}
	}
	
	
}
