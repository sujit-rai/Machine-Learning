import numpy as np
import matplotlib.pyplot as plt
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
fig,ax=plt.subplots()
ax.plot(d1[:,0],d1[:,1],'r.',label='Not issued')
ax.plot(d2[:,0],d2[:,1],'g.',label='Issued')
legend = ax.legend(loc='upper right')
plt.xlabel('Feature x1----->')
plt.ylabel('Feature x2----->')
plt.savefig('q1output.png')
plt.show(block=False)
plt.pause(5)
plt.close()