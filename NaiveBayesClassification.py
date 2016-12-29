import numpy as np
import random
import math

def loadData(dataFile):
    dataset = np.loadtxt(dataFile, dtype=bytes, delimiter='\t').astype(str)
    if dataset[0][4] == 'Present' or dataset[0][4] == 'Absent':
        for i in range(len(dataset)):
            if dataset[i][4] == 'Present':
                dataset[i][4] = 1
            else:
                dataset[i][4] = 0
    return dataset

def splitData(dataset):
    select = int(len(dataset) * ((K-1)/K))
    # print(dataset[0].astype(float))
    train_idx = random.sample(range(len(dataset)), select)
    training_data = dataset[np.array(train_idx)]

    # print(training_data.shape)
    # print(train_idx)
    # print(training_data[0])
    # print(float(training_data[0][0]))
    # print("Rows TRAIN",len(training_data))
    # print("Colms ", len(training_data[0]))

    test_idx = list(set(range(len(dataset))) - set(train_idx))
    test_data = dataset[np.array(test_idx)]

    # print(test_idx)
    # print(test_data[0])
    # print(float(test_data[0][0]))
    # print("Rows TEST",len(test_data))
    # print("Colms", len(test_data[0]))
    return training_data, test_data

def createLabelMap(training_data):
    # print(training_data.shape)
    map = {}
    last_col = len(training_data[0]) - 1
    for row in training_data:
        if row[last_col] == 1.0:
            if 1 in map.keys():
                map[1] = np.vstack((map[1], row))
            else:
                map[1] = row
        else:
            if 0 in map.keys():
                map[0] = np.vstack((map[0], row))
            else:
                map[0] = row

    # print("No of Zeros", len(map[0]))
    # print("No of Ones", len(map[1]))
    return map

def calculateMeanSD(map):
    # print(training_data[:, range(len(training_data[0])- 1)].shape)
    # print("No of zero class", len(map[1]))
    meanSDMap = {}
    meanSDMap[0] = []
    meanSDMap[1] = []
    for key, value in map.items():
        sum = value[:, range(len(value[0]))].sum(axis=0)
        avg = sum / len(value)
        #
        # print("Length of Avg", len(avg))
        # np.round(avg, 6)
        # print(avg)
        # np.round(sum, 6)
        # print(sum)
        # print(float(sum[len(sum) - 1]))

        stanDev = []
        j = 0
        for col in value.T:
            # print(len(value.T))
            sumCol = 0.0
            for i in range(len(value)):
                sumCol += pow(col[i] - avg[j], 2)
            j += 1
            stanDev.append(math.sqrt(sumCol / (len(value) - 1)))

        # np.round(variance, 6)
        avg = np.delete(avg, len(avg) - 1)
        stanDev = np.delete(stanDev, len(stanDev) - 1)
        # print("Length of Variance", len(variance))
        # print(variance)
        # print(avg, " ", variance)
        meanSDMap[key].append(avg)
        meanSDMap[key].append(stanDev)
    # print("Len of VALUE", meanSDMap)
    return meanSDMap

def predictLabel(test_data, meanSDMap):
    predLabels = []
    actualLabels = []
    for sample in test_data:
        initProb = 0.0
        actualLabels.append(int(sample[len(sample) - 1]))
        sample = np.delete(sample, len(sample) - 1)
        for label in meanSDMap:
            prob = calcProb(label, sample, meanSDMap)
            if prob > initProb:
                initProb = prob
                predictedLabel = label
        predLabels.append(predictedLabel)
    # print("size of Predicted Label", len(predLabels))
    # print("Actual Label", actualLabels)
    # print("Predic Label", predLabels)
    return predLabels, actualLabels

def calcProb(label, sample, meanSDMap):
    # print("sample", sample)
    # print("MAP", meanSDMap[0][1])
    prob = 1.0
    for i in range(len(sample)):
        # print("Mean", meanSDMap[label][0][i], "Var", meanSDMap[label][1][i])
        exponent = math.exp(-(math.pow(sample[i] - meanSDMap[label][0][i], 2) / (2 * math.pow(meanSDMap[label][1][i], 2))))
        # print("Exp shape", exponent.shape)
        mult = 1 / (math.sqrt(2 * math.pi) * meanSDMap[label][1][i])
        calProb = mult * exponent
        # print(calProb)
        prob *= calProb
    # print("Prob returned", prob, "Label", label)
    # print("lebel Size", len(labelMap[label]))
    # print("train_size", train_size)
    return prob * len(labelMap[label]) / train_size

def attributeValProbMap(dataset):
    size = len(dataset)
    freq1, freq0 = calculateClassFreq(dataset)
    leng = len(dataset[0])
    probMap = {}
    for row in dataset:
        for i in range(len(row) - 1):
            count1 = 0
            count0 = 0
            if row[i] not in probMap:
                for j in range(size):
                    if row[i] == dataset[j][i] and dataset[j][leng - 1] == '1':
                        count1 += 1
                    elif row[i] == dataset[j][i] and dataset[j][leng - 1] == '0':
                        count0 += 1
                probMap[row[i]] = [count0/freq0, count1/freq1]
    prob1 = freq1/size
    prob0 = freq0/size
    return prob1, prob0, probMap

def calculateClassFreq(dataset):
    freq1 = 0
    freq0 = 0
    col = len(dataset[0]) - 1
    for i in range(len(dataset)):
        if(dataset[i][col] == '1'):
            freq1 += 1
        else:
            freq0 += 1
    return freq1, freq0

def calculatePostProb(prob1, prob0, probMap):
    test = []
    lookup = ['Outlook', 'Temperature', 'Humidity', 'Windy']
    for i in range(len(lookup)):
        test.append(input("Enter "+lookup[i]+" : "))
    print(test)
    PH0X = prob0
    for data in test:
        PH0X *= probMap[data][0]
    # print("P(H0/X) : ",PH0X)

    PH1X = prob1
    for data in test:
        PH1X *= probMap[data][1]
    sum = PH0X + PH1X
    print("P(H0/X) Likelihood", PH0X)
    print("P(H1/X) Likelihood", PH1X)
    print()
    print("P(H0/X) Posterior : ", PH0X / sum)
    print("P(H1/X) Posterior : ", PH1X / sum)

# ********************************Naive Bayes Script***********************************
datasets = ['project3_dataset1.txt', 'project3_dataset2.txt', 'project3_dataset4.txt']
loop_var = True
while loop_var:
    dataFile = input("Enter the dataset file name: ")
    if dataFile not in datasets:
        print("Incorrect dataset specified ")
    else:
        break

if dataFile == "project3_dataset4.txt":
    dataset = loadData(dataFile)
    prob1, prob0, probMap = attributeValProbMap(dataset)
    calculatePostProb(prob1, prob0, probMap)
else:
    while loop_var:
        K = input("Enter Integer value of K: ")
        if K.isdigit():
            K = int(K)
            break
        else:
            print("Incorrect K specified ")
    Accuracy = []
    Precision = []
    Recall = []
    FMeasure = []
    for i in range(K):
        dataset = loadData(dataFile)
        training_data, test_data = splitData(dataset)

        training_data = training_data.astype(float)
        test_data = test_data.astype(float)

        labelMap = createLabelMap(training_data)
        meanVarMap = calculateMeanSD(labelMap)

        train_size = len(training_data)
        predictedLabel, actualLabel = predictLabel(test_data, meanVarMap)

        # Measure Calculation
        a = 0
        b = 0
        c = 0
        d = 0
        for i in range(len(predictedLabel)):
            if predictedLabel[i] == actualLabel[i] == 1:
                a += 1
            elif predictedLabel[i] == actualLabel[i] == 0:
                d += 1
            elif predictedLabel[i] == 1 and actualLabel[i] == 0:
                c += 1
            else:
                b += 1
        #Accuracy calculation
        Accuracy.append(((a + d) / (a + b + c + d)) * 100.0)
        # Precision Calculation
        Precision.append((a / (a + c)) * 100.0)
        # Recall Calculation
        Recall.append((a / (a + b)) * 100.0)
        # F-Measure Calculation
        FMeasure.append(((2*a) / (2*a + b + c)) * 100.0)

    print("Average Accuracy",np.average(Accuracy))
    print("Average Precision", np.average(Precision))
    print("Average Recall", np.average(Recall))
    print("Average FMeasure", np.average(FMeasure))