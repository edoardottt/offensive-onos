These files are intended to help/build/use data mining in ONOS in order to detect CAP attacks.

Results obtained with gendata2 & predict2 (K-nearest neighbours, K = 3):
```
 ----------- Training data -----------
Read 1000/1000 lines
Model score: 0.975
Model confusion matrix: 
[[520  23]
 [  2 455]]
 ----------- Test data -----------
Read 200/200 lines
Model score: 0.98
Model confusion matrix: 
[[105   4]
 [  0  91]]
Time difference is 0.0 seconds
```
```
 ----------- Training data -----------
Read 200000/200000 lines
Model score: 0.987855
Model confusion matrix: 
[[105765   2121]
 [   308  91806]]
 ----------- Test data -----------
Read 40000/40000 lines
Model score: 0.975075
Model confusion matrix: 
[[20758   816]
 [  181 18245]]
The test ran for 1199.0 seconds
```
