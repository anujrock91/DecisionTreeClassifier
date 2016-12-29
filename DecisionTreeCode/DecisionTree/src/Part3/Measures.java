package Part3;
import java.util.ArrayList;

public class Measures {
		
	private ArrayList<String> machineLabels = new ArrayList<>();
	private String [][] testSet;
	
	public Measures(ArrayList<String> predictedLabels, String [][] testData){
		this.machineLabels = (ArrayList<String>)predictedLabels.clone();
		this.testSet = testData;
	}
	
	
	//get accuracy
	public double getAccuracy(){
		double a = 0;
		double b= 0;
		double c = 0;
		double d = 0;
		for(int i =0;i<testSet.length;i++){
			if(testSet[i][testSet[0].length-1].equals("1") && machineLabels.get(i).equals("1")){
				++a;
			}
		}
		for(int i =0;i<testSet.length;i++){
			if(testSet[i][testSet[0].length-1].equals("1") && machineLabels.get(i).equals("0")){
				++b;
			}
		}
		for(int i =0;i<testSet.length;i++){
			if(testSet[i][testSet[0].length-1].equals("0") && machineLabels.get(i).equals("1")){
				++c;
			}
		}
		for(int i =0;i<testSet.length;i++){
			if(testSet[i][testSet[0].length-1].equals("0") && machineLabels.get(i).equals("0")){
				++d;
			}
		}
		double accuracy = ((a+d)/(a+b+c+d));
		return accuracy;
	}
	
	//Precision
	public double getPrecision(){
		double precision;
		double a = 0;
		double b= 0;
		double c = 0;
		double d = 0;
		for(int i =0;i<testSet.length;i++){
			if(testSet[i][testSet[0].length-1].equals("1") && machineLabels.get(i).equals("1")){
				++a;
			}
		}
		for(int i =0;i<testSet.length;i++){
			if(testSet[i][testSet[0].length-1].equals("1") && machineLabels.get(i).equals("0")){
				++b;
			}
		}
		for(int i =0;i<testSet.length;i++){
			if(testSet[i][testSet[0].length-1].equals("0") && machineLabels.get(i).equals("1")){
				++c;
			}
		}
		for(int i =0;i<testSet.length;i++){
			if(testSet[i][testSet[0].length-1].equals("0") && machineLabels.get(i).equals("0")){
				++d;
			}
		}
		precision = ((a)/(a+c));
		return precision;
	}
	
	public double getRecall(){
		double recall;
		double a = 0;
		double b= 0;
		double c = 0;
		double d = 0;
		for(int i =0;i<testSet.length;i++){
			if(testSet[i][testSet[0].length-1].equals("1") && machineLabels.get(i).equals("1")){
				++a;
			}
		}
		for(int i =0;i<testSet.length;i++){
			if(testSet[i][testSet[0].length-1].equals("1") && machineLabels.get(i).equals("0")){
				++b;
			}
		}
		for(int i =0;i<testSet.length;i++){
			if(testSet[i][testSet[0].length-1].equals("0") && machineLabels.get(i).equals("1")){
				++c;
			}
		}
		for(int i =0;i<testSet.length;i++){
			if(testSet[i][testSet[0].length-1].equals("0") && machineLabels.get(i).equals("0")){
				++d;
			}
		}
		recall = ((a)/(a+b));
		return recall;
	}
	
	
	public double getFMeasure(){
		double fMeasure;
		double a = 0;
		double b= 0;
		double c = 0;
		double d = 0;
		for(int i =0;i<testSet.length;i++){
			if(testSet[i][testSet[0].length-1].equals("1") && machineLabels.get(i).equals("1")){
				++a;
			}
		}
		for(int i =0;i<testSet.length;i++){
			if(testSet[i][testSet[0].length-1].equals("1") && machineLabels.get(i).equals("0")){
				++b;
			}
		}
		for(int i =0;i<testSet.length;i++){
			if(testSet[i][testSet[0].length-1].equals("0") && machineLabels.get(i).equals("1")){
				++c;
			}
		}
		for(int i =0;i<testSet.length;i++){
			if(testSet[i][testSet[0].length-1].equals("0") && machineLabels.get(i).equals("0")){
				++d;
			}
		}
		fMeasure = ((2*a)/((2*a)+b+c));
		return fMeasure;
	}
	
	
	public void displayMeasures(){
		System.out.println("A : " + getAccuracy() + " | R : " +getRecall() + " | F : " +getFMeasure() + " | P : " + getPrecision());
	}
	
}
