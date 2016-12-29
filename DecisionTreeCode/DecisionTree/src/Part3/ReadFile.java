package Part3;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ReadFile {
	private String filePathName = null;
	private double setFractionForTraining = 0;
	private ArrayList<String> inputList = new ArrayList<>();
	
	public ReadFile(String filePathName, double fraction) throws IOException{
		this.filePathName = filePathName;
		this.setFractionForTraining = fraction;
		if(this.setFractionForTraining == 0.0 || this.setFractionForTraining == 1){
			System.out.println("Cannot have 0 or 1. Specify Value between 0 and 1");
		}
		else{
			readFile();
		}
	}
	
	
	public String[][] getTrainingData(){
		double fraction = setFractionForTraining;
		int numRowsTraining = (int)Math.round((double)fraction*(double)inputList.size());
		String [][] trainingSet = new String[numRowsTraining][inputList.get(0).split("\t").length];
		for(int row=0; row<numRowsTraining; row++){
			String [] lineSplit = inputList.get(row).split("\t");
			for(int col=0; col<lineSplit.length ; col++){
				trainingSet[row][col] = lineSplit[col];
			}
		}
		return trainingSet;
	}
	
	
	public String[][] getTestData(){
		double fraction = setFractionForTraining;
		int numRowsTraining = (int)Math.round((double)fraction*(double)inputList.size());
		String [][] testSet = new String[inputList.size()-numRowsTraining][inputList.get(0).split("\t").length];
		int testRow = 0;
		for(int row = numRowsTraining; row < inputList.size(); row++){
			String lineSlit[] = inputList.get(row).split("\t");
			for(int col = 0;col<lineSlit.length;col++){
				testSet[testRow][col] = lineSlit[col];
			}
			++testRow;
		}
		return testSet;
	}
	
	
	private void readFile() throws IOException{
		File fName = new File(filePathName); 
		FileReader fRead = new FileReader(fName);
		BufferedReader readStream = new BufferedReader(fRead);
		String line = null;
		while((line = readStream.readLine()) != null){
			inputList.add(line);
		}
		readStream.close();
		fRead.close();
		Collections.shuffle(inputList);
	}
	
	
}	
