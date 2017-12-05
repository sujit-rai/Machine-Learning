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
matrix = np.concatenate((matrix,[matrix[0]**2],[matrix[1]**2],[matrix[3]**2],[matrix[0]**3],[matrix[1]**3],[matrix[3]**3],[matrix[0]**4],[matrix[1]**4],[matrix[3]**4]),axis=0)
# matrix = np.concatenate(([matrix[3]],[matrix[4]],[matrix[5]],[matrix[7]],[matrix[9]],[matrix[10]],[matrix[12]]),axis=0)
stdmatrix = [np.array((matrix[i] - np.mean(matrix[i]))/np.std(matrix[i])) for i in range(len(matrix))]
matrix = np.transpose(np.concatenate(([sex],stdmatrix,[target]),axis = 0))
dataset = [",".join(i) for i in matrix]
f1.close()
random.shuffle(dataset)
train_data_size = int(0.8 * len(dataset))
train_data = dataset[:train_data_size]
test_data = dataset[train_data_size:]
def mylinridgereg(x,y,l):
	return np.matmul(np.matmul(np.linalg.pinv(np.matmul(x.T,x) + (l * np.identity(x.shape[1]))),x.T),y)
def mylinridgeregeval(x,t):
	return np.matmul(x,t)
def meansquarrederr(t,td):
	return np.mean(np.square(t-td))
w = np.array([i.split(',')[1:-1] for i in train_data]).astype(float)
wt = np.array([i.split(',')[1:-1] for i in test_data]).astype(float)
o = np.ones((len(w),1),dtype=int)
ot = np.ones((len(wt),1),dtype=int)
sex = np.array([[0 if i.split(',')[0]=='F' else (1 if i.split(',')[0] == 'M' else -1) for i in train_data]]).T
sext = np.array([[0 if i.split(',')[0]=='F' else (1 if i.split(',')[0] == 'M' else -1) for i in test_data]]).T
target = np.array([[int(i.split(',')[-1]) for i in train_data]]).T
test_target = np.array([[int(i.split(',')[-1]) for i in test_data]]).T
w = np.concatenate((o,w),axis=1)
wt = np.concatenate((ot,wt),axis = 1)
ta = mylinridgereg(w,target,0.01)
prediction = mylinridgeregeval(wt,ta)
test_err = meansquarrederr(test_target,prediction)
fig, ax = plt.subplots(nrows=1, ncols=2)

ax[0].scatter(test_target,prediction)
ax[0].set_xlabel('Actual Value')
ax[0].set_ylabel('Predicted Value')
ax[0].set_title('Test Data')
ax[1].scatter(target,mylinridgeregeval(w,ta))
ax[1].set_xlabel('Actual Value')
ax[1].set_ylabel('Predicted Value')
ax[1].set_title('Train Data')
plt.show(block=False)
plt.pause(5)
plt.close()