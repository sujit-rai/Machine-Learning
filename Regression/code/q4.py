import numpy as np
import random
import matplotlib.pyplot as plt
import time
import scipy.stats as sc


f1 = open('linregdata','rU')

matrix = [([line.split(',')[0]]+[float(item) for item in line.split(',')[1:len(line.split(','))-1]]+[int(line.split(',')[-1])]) for line in f1]
matrix = np.array(matrix)
matrix = np.transpose(matrix)
sex = matrix[0]
target = matrix[-1]
matrix = (matrix[1:-1]).astype(float)
stdmatrix = [np.array((matrix[i] - np.mean(matrix[i]))/np.std(matrix[i])) for i in range(len(matrix))]
matrix = np.transpose(np.concatenate(([sex],stdmatrix,[target]),axis = 0))
dataset = [",".join(i) for i in matrix]
f1.close()
f1 = open('train.txt','w')
f2 = open('test.txt','w')
random.shuffle(dataset)
train_data_size = int(0.2 * len(dataset))
train_data = dataset[:train_data_size]
test_data = dataset[train_data_size:]
print >> f1, "\n".join(train_data)
print "train data is present in train.txt"
print >> f2, "\n".join(test_data)
print "test data is present in test.txt"
f1.close()
f2.close()
