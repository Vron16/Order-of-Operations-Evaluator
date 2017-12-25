# Order-of-Operations-Evaluator

Given an input expression with addition, subtraction, multiplication, and division, the evaluator will solve the expression using the order of operations rules. 

If parenthetical expressions are involved, the evaluator will evaluate these first, in accordance with order of operations. 

In addition to evaluating numerical expressions, the evaluator is capable of reading in scalar variables from a text file and evaluating expressions containing these variables. For instance, if a variable x is initialized to 2 in a text file, the evaluator will substitute 2 for every instance of x in the input expression.

Lastly, the evaluator is capable of reading in arrays from a text file and evaluating expressions within brackets in order to determine the index from which it has to read the value in. For instance, an array arr containing int values could be utilized in an expression as arr[2*3-2], which is simplified to arr[4]. The value stored at arr[4] is then returned and used to solve the remainder of the expression.
