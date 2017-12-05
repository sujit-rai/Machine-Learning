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
matrix = np.concatenate((matrix,[np.exp(matrix[0])],[np.exp(matrix[1])],[matrix[3]**0.5],[matrix[4]**0.5],[matrix[5]**0.5],[matrix[6]**0.5]),axis=0)
matrix = np.concatenate(([matrix[3]],[matrix[4]],[matrix[5]],[matrix[7]],[matrix[9]],[matrix[10]],[matrix[12]]),axis=0)
stdmatrix = [np.array((matrix[i] - np.mean(matrix[i]))/np.std(matrix[i])) for i in range(len(matrix))]
matrix = np.transpose(np.concatenate(([sex],stdmatrix,[target]),axis = 0))
dataset = [",".join(i) for i in matrix]
f1.close()
l = 0
f = 0.3
def mylinridgereg(x,y,l):
	return np.matmul(np.matmul(np.linalg.pinv(np.matmul(x.T,x) + (l * np.identity(x.shape[1]))),x.T),y)
def mylinridgeregeval(x,t):
	return np.matmul(x,t)
def meansquarrederr(t,td):
	return np.mean(np.square(t-td))


lm = []
train_m = []
test_m = []
fs = []

for s in range(9):
	fs.append(f)
	random.shuffle(dataset)
	train_data_size = int(f * len(dataset))
	train_data = dataset[:train_data_size]
	test_data = dataset[train_data_size:]
	f = f + 0.05
	llist = []
	train_means = []
	test_means = []
	
	for u in range(10):
		l = random.uniform(0,1)
		llist.append(l)
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
		ta = mylinridgereg(w,target,l)
		train_pred = mylinridgeregeval(w,ta)
		train_means.append(meansquarrederr(target,train_pred))
		prediction = mylinridgeregeval(wt,ta)
		test_means.append(meansquarrederr(test_target,prediction))
		print "training set fraction : "+str(f)+" lambda : " + str(l) + " train error : " + str(meansquarrederr(target,train_pred)) + " test error : " + str(meansquarrederr(test_target,prediction))
		
	lm.append(llist)
	train_m.append(train_means)
	test_m.append(test_means)
	l = 0.1
print np.array(llist)
print np.array(train_m)
print np.array(test_m)


min_test = [np.min(test_m[i]) for i in range(len(fs))]
min_test_l = [ test_m[i].index(min_test[i]) for i in range(len(min_test))]
min_test_l = [ lm[i][min_test_l[i]] for i in range(len(min_test))]
print min_test_l


# plt.ion()
# plt.scatter(fs,min_test)
# plt.show()
# input()

fig, ax = plt.subplots(nrows=1, ncols=2)

ax[0].scatter(fs,min_test)
ax[0].set_ylabel('Minimum Mean Squarred Test Error')
ax[0].set_xlabel('Training Fraction Value')
ax[1].scatter(fs,min_test_l)
ax[1].set_ylabel('Lambda for corresponding Mean Squarred Test Error')
ax[1].set_xlabel('Training Fraction Value')
plt.show(block=False)
plt.pause(5)
plt.close()