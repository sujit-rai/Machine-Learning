Decision Trees & Forests
Sujit Rai

aclimdb folder contains the dataset.

1. In Order to reduce the number of attributes in the sample training data, imdbEr file was used. The features were reduced to 33000 by selecting words having Expected rating greater and 0.5 and less than -0.5. Then random 1000 reviews were selected from labelled.feat file containing equal number of positive and negative reviews. Now from these 33000 features those features were deleted which had frequency 0 in all the 1000 reviews. The number of features now reduced to around 6000. This 6000 reviews were used for training the decision tree.
2. Statistics of the Decision tree :
   1. Number of Terminal Nodes : 162
   2. Accuracy on training data : 0.68844
   3. Accuracy on test data : 0.68264
   4. Effect of early stopping (stopping criteria : probability of positive or negative review is greater than 0.75)
      1. Number of Terminal Nodes : 86
      2. Accuracy on training data : 0.7118
      3. Accuracy on test data : 0.71256
   1. Effect of early stopping (stopping criteria : Depth greater than 10)
      1. Number of Terminal Nodes : 55
      2. Accuracy on training data : 0.68276
      3. Accuracy on test data : 0.67536
   1. Effect of early stopping (stopping criteria : number of instances less than 10)
      1. Number of Terminal Nodes : 143
      2. Accuracy on training data : 0.68448
      3. Accuracy on test data : 0.68204
   1. Effect of early stopping (stopping criteria : number of instances less than 20)
      1. Number of Terminal Nodes : 132
      2. Accuracy on training data : 0.68052
      3. Accuracy on test data : 0.67868
   1. Attributes most frequently used as a split function :
      1. Also 
      2. Even
      3. None
      4. Enjoyed
      5. Guy
1. Effect of noise
   1. When 10% noise is added
      1. Accuracy on training data : 0.64152
      2. Accuracy on test data : 0.63248
      3. Number of nodes in the tree : 327
   1. When 5% noise is added
      1. Accuracy on training data : 0.67892
      2. Accuracy on test data : 0.67104
      3. Number of nodes in the tree : 329
   1. When 1% noise is added 
      1. Accuracy on training data : 0.69944
      2. Accuracy on test data : 0.69012
      3. Number of nodes in the tree : 339
   1. When 0.5% noise is added
      1. Accuracy on training data : 0.70764
      2. Accuracy on test data : 0.69372
      3. Number of nodes in the tree : 325
1. Effect of pruning
   1. Number of Nodes : 21
   2. Accuracy on validation data(500 positive/500 negative) used during pruning: 0.821
   3. Accuracy on test data (25000) : 0.69564
1. Decision Forests
   1. Accuracy on test data : 0.74388
   2. Accuracy on training data  : 0.75648



Commands : 


1. Decision tree without stopping criteria : 
   1. java Detree // this creates the decision tree
   2. java Testtree // this will test the tree on the test data and print its accuracy
1. Decision tree with Early Stopping :
   1. java Detree 2 1 0.75 // stopping criteria on probability of positive or negative > 0.75
   2. java Detree 2 2
   3. java Detree 2 3
   4. java Detree 2 4
1. Decision tree with noise :
   1. java Detree 3 0.10 // add 10% noise
   2. java Detree 3 0.05 // add 5% noise
   3. java Detree 3 0.01 // add 1 % noise 
1. Pruned tree : 
   1. java pruning // this will prune the tree and print the accuracy in terminal
1. Decision Forest :
   1. java Forest // this will create 20 tree
   2. java TestForest // this will test the forest on the test data


Approach :


The approach used for learning and testing of the reviews are as follows :
1. Preprocessing : The file “Preprocessing.java” contains code to read 500 positive and 500 negative reviews from the data set. It first extracts features from the imdbEr.txt file based on the following conditions :  (polarity <-0.5 && polarity > 0.5 && words is present in any of the 1000 reviews). It then forms a table from this and stores it a file named “tableindexes.txt”. The first character decribes the rating of the review. The remaining  word in the line is in the format “word index : expected rating : frequency”.
2. Tree creation : The file “Detree.java” reads this “tableindexes.txt” file and creates decision tree using this table. This Decision tree is then converted in rules and stored in “rules.txt” file.
3. Testing : The file “Testtree.java” creates decision tree by reading the “rules.txt” file and then tests the formed tree on the training or test data. The “rules.txt” file is used to store the decision tree so that it can be used to create the tree in a very short time.


The approach for creating Decision tree :
        Decision tree was created using feature bagging where only 300 random features were used for creating the decision trees on 1000 reviews.




The approach for pruning the tree : 
        First the complete Decision tree was traversed in post-order and the nodes were stored in an array. Then each element was selected and accuracy was calculated before and after pruning the node. Then at the end entire tree was used to calculate the accuracy.