import numpy as np
f1 = open('linregdata','rU')
f2 = open('q2output.txt','w')
matrix = [([line.split(',')[0]]+[float(item) for item in line.split(',')[1:len(line.split(','))-1]]+[int(line.split(',')[-1])]) for line in f1]
matrix = np.array(matrix)
matrix = np.transpose(matrix)
sex = matrix[0]
target = matrix[-1]
matrix = (matrix[1:-1]).astype(float)
stdmatrix = [np.array((matrix[i] - np.mean(matrix[i]))/np.std(matrix[i])) for i in range(len(matrix))]
matrix = np.transpose(np.concatenate(([sex],stdmatrix,[target]),axis = 0))
dataset = [",".join(i) for i in matrix]
print >> f2, "\n".join([ ",".join(i) for i in matrix])
print "output printed in file q2output.txt"
f1.close()
f2.close()