import numpy as np
import math
import matplotlib.pyplot as plt
def predictVal(x1,x2,W):
	p=3
	sum=0.0
	for i in range(2,degree+1,1):
		for j in range(i,-1,-1):
			sum+=W[p]*(x1**j)*(x2**(i-j))
			p+=1
	sum+=W[0]+W[1]*x1+W[2]*x2
	return sigmoid(sum)

def featuretransform(X,degree):
	temp=[]
	for i in range(2,degree+1,1):
		for j in range(i,-1,-1):
			x1=X[:,0]
			x2=X[:,1]
			temp=np.zeros([X.shape[0],1])
			for k in range(x1.shape[0]):
				temp[k]=(x1[k]**j)*(x2[k]**(i-j))
			X=np.hstack((X,temp))
	return X

def MeanError(X,Y,W):
	meanerror=0
	for i in range(X.shape[1]):
		x1=X[:,i]
		meanerror=meanerror+(hypothesis(W,x1)-Y[i])**2
	meanerror=(1.0/(2.0*X.shape[1]))*meanerror
	return meanerror

def Misclassify(X,Y,W):
	correct=0;
	wrong=0;
	for i in range(X.shape[1]):
		if (hypothesis(W,X[:,i])==1 and Y[i]==1) or (hypothesis(W,X[:,i])==0 and Y[i]==0):
			correct=correct+1
		else:
			wrong=wrong+1
	return wrong

def sigmoid(x):
	z=1/(1+np.exp(-x))
	return z

def GradientDescent(X,Y,W,alpha,lamda,iter):
	for j in xrange(iter):
		W+=alpha*np.dot(X,Y-sigmoid(np.dot(X.T,W)))+float(lamda)/X.shape[1]*W
		if(j%10000==0):
			print 'Iteration number :',j,' Examples Misclassified :',Misclassify(X,Y,W)
			if Misclassify(X,Y,W)==0:
				break
	return Misclassify(X,Y,W),W

def NewtonRaphson(X,Y,W,lamda,iter):
	for j in range(iter):
		R= np.zeros([X.shape[1],X.shape[1]])
		for i in range(X.shape[1]):
			hyp=hypo(W,X[:,i])
			R[i][i]=float(hyp)*(1.0-hyp)
		H=np.dot(np.dot(X,R),X.T)
		Hinv=np.linalg.pinv(H)
		F=np.zeros([Y.shape[0],Y.shape[1]])
		for i in range(Y.shape[0]):
			F[i]=hypo(W,X[:,i])
		W=W-np.dot(np.dot(Hinv,X),(F-Y))+float(lamda)/X.shape[1]*W
		if Misclassify(X,Y,W)==0:
			break
		print 'Iteration number :',j,' Examples Misclassified:',Misclassify(X,Y,W)
	return Misclassify(X,Y,W),W

def hypo(W,X):
	return ((1.0)/(1+np.exp(-np.dot(W.T,X))))

def hypothesis(W,X):
	temp= ((1.0)/(1+np.exp(-np.dot(W.T,X))))
	if temp>=0.5:
		return 1
	else:
		return 0

f=open('credit.txt','rU')
row = []
for line in f :
	row.append([(float)(item) for item in line.split(',')])
x=[]
Y=[]
for j in row:
	Y.append([item for item in j[2:3]])
Y=np.matrix(Y)
for j in row:
	x.append([item for item in j[0:2]])
x=np.matrix(x)
degree=2
x=featuretransform(x,degree)
x1 = np.ones((len(x),1))
X = np.hstack((x1,x))
X=X.T
W=np.ones([X.shape[0],1])
lamda=0.001
for i in range(5):
	alpha=0.01
	W=np.matrix(W)
	#alpha =0.01 lamda=0.001 W=0.05 for 
	for i in range(W.shape[0]):
		W[i][0]=0.05#-0.0100005
	print '****************************Initial W*************************************'
	print W
	f.close()
	X=np.array(X)
	Y=np.array(Y)
	W=np.array(W)
	M,W=GradientDescent(X,Y,W,alpha,lamda,500000)
	print 'Number of examples misclassified :',M
	#plt.plot(i,MeanError(X,Y,W),'g.')f
	print '*******************W after Gradient Descend***************************'
	print W
	#Gradient descend degree 2,3 alpha=0.01 lamda=0.0001
	f=open('credit.txt','rU')
	r=[]
	for line in f:
			r.append([(float)(item) for item in line.split(',')])
	matrix = np.array(r)
	d1=[]
	d2=[]
	for row in matrix:
		if int(row[2])==0:
			d1.append(row)
		if int(row[2])==1:
			d2.append(row)
	d1=np.array(d1)
	d2=np.array(d2)
	print '****************************Initial W*************************************'
	print W
	fig,ax=plt.subplots()
	ax.plot(d1[:,0],d1[:,1],'r.',label='Not issued')
	ax.plot(d2[:,0],d2[:,1],'g.',label='Issued')
	plt.xlabel('Feature x1----->')
	plt.ylabel('Feature x2----->')
	legend = ax.legend(loc='upper right')
	titl='Gradient Descent lamda= '+str(lamda)
	plt.title(titl)
	x1vals = np.linspace(0,6,100)
	x2vals = np.linspace(0,6,100)
	X1,X2 = np.meshgrid(x1vals,x2vals)
	Z = np.zeros([x2vals.size,x1vals.size])
	for i in range(Z.shape[0]):
		for j in range(Z.shape[1]):
			Z[i][j] = predictVal(x1vals[j],x2vals[i],W);
	plt.contour(X1,X2,Z,levels=np.linspace(0.5,0.5000000000001,2))
	plt.savefig('q66output.png')
	plt.show(block=False)
	plt.pause(5)
	plt.close()
	f.close()
	lamda=lamda+0.001