import numpy as np
import math
import matplotlib.pyplot as plt
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
			print 'Iteration number :',j,' Examples misclassified : ',Misclassify(X,Y,W)
			if Misclassify(X,Y,W)<35:
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
		F=np.dot(X.T,W)
		W=W-np.dot(np.dot(Hinv,X),(F-Y))+float(lamda)/X.shape[1]*W
		var=Misclassify(X,Y,W)
		if var==0:
			print 'Example Misclassified',var
			break
	return Misclassify(X,Y,W),W

def hypo(W,X):
	return ((1.0)/(1+np.exp(-np.dot(W.T,X))))

def hypothesis(W,X):
	temp= ((1.0)/(1+np.exp(-np.dot(W.T,X))))
	if temp>=0.5:
		return 1
	else:
		return 0

degree=1
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
x1 = np.ones((len(x),1))
X = np.hstack((x1,x))
X=X.T
print 'shape of x :',X.shape[0]
W=np.ones([X.shape[0],1])
W=np.matrix(W)
W[0]=-0.01
W[1]=0
W[2]=-0.01
#W[i][0]=-0.03 for newton raphson line
print '****************************Initial W*************************************'
print W
X=np.array(X)
Y=np.array(Y)
W=np.array(W)
alpha=0.05
lamda=0.1
M,W=GradientDescent(X,Y,W,alpha,lamda,500000)
print 'Number of examples misclassified :',M
print '***************************W Gradient Descent********************************'
print W
f.close()
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
fig,x1=plt.subplots()
plt.plot(d1[:,0],d1[:,1],'r.',label='Not issued')
plt.plot(d2[:,0],d2[:,1],'g.',label='Issued')
legend = x1.legend(loc='upper right')
plt.xlabel('Feature x1----->')
plt.ylabel('Feature x2----->')
titl='Gradient Descent'
plt.title(titl)
x1vals = np.linspace(0,7,1000)
for x1val in x1vals:
	x2val=(W[0]+W[1]*float(x1val))/W[2]
	if(x2val>=0 and x2val<=7):
		plt.plot(x1val,x2val,'b.')
plt.savefig('q21output.png')
plt.show(block=False)
plt.pause(3)
plt.close()
for i in range(W.shape[0]):
	W[i][0]=-0.03
M,W=NewtonRaphson(X,Y,W,lamda,500)
fig,x1=plt.subplots()
plt.plot(d1[:,0],d1[:,1],'r.',label='Not issued')
plt.plot(d2[:,0],d2[:,1],'g.',label='Issued')
legend = x1.legend(loc='upper right')
plt.xlabel('Feature x1----->')
plt.ylabel('Feature x2----->')
titl='Newton Raphson'
plt.title(titl)
x1vals = np.linspace(0,7,1000)
for x1val in x1vals:
	x2val=(W[0]+W[1]*float(x1val))/W[2]
	if(x2val>=0 and x2val<=7):
		plt.plot(x1val,x2val,'b.')
plt.savefig('q22output.png')
plt.show(block=False)
plt.pause(3)
plt.close()
# x2vals = np.linspace(1.5,6,100)
# X1,X2 = np.meshgrid(x1vals,x2vals)
# Z = np.zeros([x2vals.size,x1vals.size])
# for i in range(Z.shape[0]):
# 	for j in range(Z.shape[1]):
# 		Z[i][j] = predictVal(x1vals[j],x2vals[i],W);
# ax1.contour(X1,X2,Z,[0,0.5])
#ax1.colorbar()
# for i in range(W.shape[0]):
# 	W[i][0]=0.0004
# M,W=NewtonRaphson(X,Y,W,lamda,500)
# fig2=plt.figure()
# ax2=fig2.add_subplot(111)
# ax2.plot(d1[:,0],d1[:,1],'r.',label='Not issued')
# ax2.plot(d2[:,0],d2[:,1],'g.',label='Issued')
# # ax2.xlabel('Feature x1----->')
# # ax2.ylabel('Feature x2----->')
# # titl='Newton Raphson Fitting Degree '+str(degree)+' polynomial'
# # ax2.title(titl)
# x1vals = np.linspace(1.5,6,100)
# x2vals = np.linspace(1.5,6,100)
# X1,X2 = np.meshgrid(x1vals,x2vals)
# Z = np.zeros([x2vals.size,x1vals.size])
# for i in range(Z.shape[0]):
# 	for j in range(Z.shape[1]):
# 		Z[i][j] = predictVal(x1vals[j],x2vals[i],W);
# ax2.contour(X1,X2,Z,[0,0.5])
# #ax2.colorbar()
# f.close()
# ###############################################
# plt.savefig('q5output.png')
# plt.show()