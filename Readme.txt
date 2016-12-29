
**********RUN FOR NAIVE BAYES****************
Run the python file "NaiveBayesClassification.py" using python3 compiler.
Place the dataset in the same directory as python file.
Input the dataset name and K Fold Value.
Run the .py file.

**********RUN FOR DECISION TREE****************
Load the project DecisionTree inside DecisionTreeCode.
Open it on ecplise.
In TestCode file, comment the code from ine 8 to 12, 25-32
Inside ReadFile construct specify the file Location, and fraction of training data to take.
Run the code, we will get all the measures, tree display, and predicted labels.

**********RUN FOR FOREST **************************
Load the project DecisionTree inside DecisionTreeCode.
Open it on ecplise.
In TestCode file, comment the code from ine 8 - 23
In rfObj, specify the path of the file to consider and specify teh fraction of training data.
Run the code.
It will print labels and measure for the data set.
NOTE: DO NOT SPECIFY FRACTION AS 1 inside rfObj.

*********RUN K FOLD FOR RANDOM FOREST*************
Load the project DecisionTree inside DecisionTreeCode.
Open it on ecplise.
In TestCode file, comment the code from ine 11 - 32
In obj1, specify the dataset that has to be considered for K Fold on random forest.
Run the code.

********RUN K FOLD FOR DECISION TREE**************
Load the project DecisionTree inside DecisionTreeCode.
Open it on ecplise.
In TestCode file, comment the code from ine 14 - 32 and 8-9
In obj2, specify the dataset that has to be considered for K Fold on decision tree.
Run the code.

**********RUN FOR BOOSTING****************
Place the dataset in the same directory as Boosting.java file.
Run the Boosting.java file.
Input the numberof iterations.
Check the results in the console.
NOTE: DO NOT SPECIFY FRACTION AS 1 inside rfObj.

**********RUN FOR K-Nearest Neighbours****************
Create a New Java Project -> Create a new Java Package with name KNNAlgorithm -> Copy all the .java files inside the package -> 
copy config.properties file inside the src folder of project -> Open RunKNNAlgorithm.java which is the main class file -> Right click and Run as Java Application
Regarding configuration -> You can specify the values of several parameters for the algorithm inside and config.properties file such as inputFileName(if it contains both training and sample data), put entries in trainDataFile and testDataFile only when training and test data sets are present in different files,
choose number of nearest neighbours, choose the number for folds for cross fold validation, choose partition = KFold if you want to run with K-Fold cross validation technique else it will run with Random Shuffling Approach, choose votingScheme=weightedVoting if you want to vote based on inverse squared distances else it will run based on majority voting scheme