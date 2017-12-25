package apps;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                
    
	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   
	
	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;
    
    /**
     * String containing all delimiters (characters other than variables and constants), 
     * to be used with StringTokenizer
     */
    public static final String delims = " \t*+-/()[]";
    
    /**
     * Initializes this Expression object with an input expression. Sets all other
     * fields to null.
     * 
     * @param expr Expression
     */
    public Expression(String expr) {
        this.expr = expr;
    }

    /**
     * Populates the scalars and arrays lists with symbols for scalar and array
     * variables in the expression. For every variable, a SINGLE symbol is created and stored,
     * even if it appears more than once in the expression.
     * At this time, values for all variables are set to
     * zero - they will be loaded from a file in the loadSymbolValues method.
     */
    public void buildSymbols() {
    	scalars = new ArrayList<ScalarSymbol>();
    	arrays = new ArrayList<ArraySymbol>();
    	
    	String noSpaces = "";
    	boolean isVar = false;
    	int start = -1;
    	
    	expr = expr.trim();
    	for (int i = 0; i < expr.length(); i++){
    		if (expr.charAt(i) != ' ') {
    			noSpaces = noSpaces + expr.charAt(i);
    		}
    	}
    	for (int i = 0; i < noSpaces.length(); i++) {
    		if (i == 0) {
    			if (Character.isLetter(noSpaces.charAt(i))) {
    				if (i == noSpaces.length() - 1) {
    					String name = noSpaces;
    					scalars.add(new ScalarSymbol(name));
    					break;
    				}
    				isVar = true;
    				start = 0;
    			}
    			continue;
    		}
    		else {
    			if (isVar) {
    				if (Character.isLetter(noSpaces.charAt(i))) {
    					if (i == noSpaces.length()-1) {
    						String name = noSpaces.substring(start, i+1);
    						boolean isThere = false;
    						for (int m = 0; m < scalars.size(); m++) {
    							if (scalars.get(m).name.equals(name)) {
    								isThere = true;
    								break;
    							}
    						}
    						if (!(isThere)) {
    							scalars.add(new ScalarSymbol(name));
    						}
    						
    					}
    					else {
    						isVar = true;
    						continue;
    					}
    				}
    				else if (noSpaces.charAt(i) == '[') {
    					String name = noSpaces.substring(start, i);
    					boolean isThere = false;
    					for (int m = 0; m < arrays.size(); m++) {
    						if (arrays.get(m).name.equals(name)) {
    							isThere = true;
    							break;
    						}
    					}
    					if (!(isThere)) {
    						arrays.add(new ArraySymbol(name));
    					}
    					isVar = false;
    					start = -1;
    				}
    				else {
    					String name = noSpaces.substring(start, i);
    					boolean isThere = false;
						for (int m = 0; m < scalars.size(); m++) {
							if (scalars.get(m).name.equals(name)) {
								isThere = true;
							}
						}
						if (!(isThere)) {
							scalars.add(new ScalarSymbol(name));
						}
    					isVar = false;
    					start = -1;
    				}
    			}
    			else {
    				if (Character.isLetter(noSpaces.charAt(i))) {
    					if (i == noSpaces.length() - 1) {
    						if (Character.isLetter(noSpaces.charAt(i))) {
    							String name = noSpaces.substring(i);
    							boolean isThere = false;
    							for (int m = 0; m < scalars.size(); m++) {
    								if (scalars.get(m).name.equals(name)) {
    									isThere = true;
    								}
    							}
    							if (!(isThere)) {
    								scalars.add(new ScalarSymbol(name));
    							}
    							
    						}
    					}
    					else {
    						isVar = true;
    						start = i;
    					}
    				}
    				else {
    					continue;
    				}
    			}
    		}
    			
    	}
    	
    }
    
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    
    
    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions.
     * 
     * @return Result of evaluation
     */
    public float evaluate() {
    	return eval(expr);
    }
    
    private float eval(String expr) {
    	expr = expr.trim();
    	String noSpaces = "";
    	String varName = "";
    	String num = "";
    	int varStart = -1;
    	int numStart = -1;
    	for (int i = 0; i < expr.length(); i++){
    		if (expr.charAt(i) != ' ') {
    			noSpaces = noSpaces + expr.charAt(i);
    		}
    	}
    	Stack<Character> operators = new Stack<Character>();
    	Stack<Float> operands = new Stack<Float>();
    	Stack<Character> reverseOperators = new Stack<Character>();
    	Stack<Float> reverseOperands = new Stack<Float>();
    	boolean isVar = false;
    	boolean isNum = false;
    	for (int i = 0; i < noSpaces.length(); i++) {
    		if (i == 0) {
    			if (Character.isLetter(noSpaces.charAt(i))) {
    				isVar = true;
    				varStart = i;
    				isNum = false;
    				continue;
    			}
    			else if (Character.isDigit(noSpaces.charAt(i))) {
    				isVar = false;
    				isNum = true;
    				numStart = i;
    				continue;
    			}
    			else if ((noSpaces.charAt(i) == '(')) {
    				//find closing parentheses/brackets and then recurse
    				Stack<Integer>parenbracket = new Stack<Integer>();
    				for (int j = i; j < noSpaces.length(); j++) {
    					if (noSpaces.charAt(j) == '(') {
    						parenbracket.push(j);
    					}
    					else if (noSpaces.charAt(j) == ')') {
    						if (parenbracket.size() > 1) {
    							parenbracket.pop();
    						}
    						else if (parenbracket.size() == 1) {
    							int first = parenbracket.pop();
    							String subexpr = noSpaces.substring(first+1, j);
    							operands.push(eval(subexpr));
    							i = j;
    							break;
    						}
    					}
    				}
    			}
    		}
    		else {
    			if (isVar) {
    				if (Character.isLetter(noSpaces.charAt(i))) {
    					continue;
    				}
    				else if ((noSpaces.charAt(i) == '[')) {
    					isVar = false;
    					varName = noSpaces.substring(varStart, i);
    					int index = -1;
    					Stack<Integer>parenbracket = new Stack<Integer>();
        				for (int j = i; j < noSpaces.length(); j++) {
        					if (noSpaces.charAt(j) == '[') {
        						parenbracket.push(j);
        					}
        					else if (noSpaces.charAt(j) == ']') {
        						if (parenbracket.size() > 1) {
        							parenbracket.pop();
        						}
        						else if (parenbracket.size() == 1) {
        							int first = parenbracket.pop();
        							String subexpr = noSpaces.substring(first+1, j);
        							index = (int)eval(subexpr);
        							i = j;
        							break;
        						}
        					}
        				}
        				for (int k = 0; k < arrays.size(); k++) {
        					if (varName.equals(arrays.get(k).name)) {
        						operands.push((float)arrays.get(k).values[index]);
        					}
        				}
    					//find closing brackets and recurse
    				}
    				else {
    					isVar = false;
    					varName = noSpaces.substring(varStart, i);
    					for (int j = 0; j < scalars.size(); j++) {
    						if (varName.equals(scalars.get(j).name)) {
    							operands.push((float)scalars.get(j).value);
    							break;
    						}
    					}
    					if ((noSpaces.charAt(i) == '+') || (noSpaces.charAt(i) == '-') || (noSpaces.charAt(i) == '*') || (noSpaces.charAt(i) == '/')) {
    						operators.push(noSpaces.charAt(i));
    					}
    				}
    			}
    			else if (isNum) {
    				if (Character.isDigit(noSpaces.charAt(i))) {
    					continue;
    				}
    				
    				else {
    					isNum = false;
    					num = noSpaces.substring(numStart, i);
    					operands.push(Float.parseFloat(num));
    					if ((noSpaces.charAt(i) == '+') || (noSpaces.charAt(i) == '-') || (noSpaces.charAt(i) == '*') || (noSpaces.charAt(i) == '/')) {
    						operators.push(noSpaces.charAt(i));
    					}
    				}
    			}
    			else {
    				if (Character.isLetter(noSpaces.charAt(i))) {
    					isVar = true;
    					isNum = false;
    					varStart = i;
    					continue;
    				}
    				else if (Character.isDigit(noSpaces.charAt(i))) {
    					isNum = true;
    					isVar = false;
    					numStart = i;
    					continue;
    				}
    				else if (noSpaces.charAt(i) == '(') {
    					//find the closing bracket/parentheses and then recurse on that expression
    					Stack<Integer>parenbracket = new Stack<Integer>();
        				for (int j = i; j < noSpaces.length(); j++) {
        					if (noSpaces.charAt(j) == '(') {
        						parenbracket.push(j);
        					}
        					else if (noSpaces.charAt(j) == ')') {
        						if (parenbracket.size() > 1) {
        							parenbracket.pop();
        						}
        						else if (parenbracket.size() == 1) {
        							int first = parenbracket.pop();
        							String subexpr = noSpaces.substring(first+1, j);
        							operands.push(eval(subexpr));
        							i = j;
        							break;
        						}
        					}
        				}
    				}
    				else if ((noSpaces.charAt(i) == '+') || (noSpaces.charAt(i) == '-') || (noSpaces.charAt(i) == '*') || (noSpaces.charAt(i) == '/')) {
    					operators.push(noSpaces.charAt(i));
    				}
    			}
    			
    		}
    	}
    	if (isNum) {
			num = noSpaces.substring(numStart);
			operands.push(Float.parseFloat(num));
		}
    	if (isVar) {
    		varName = noSpaces.substring(varStart);
    		for (int j = 0; j < scalars.size(); j++) {
				if (varName.equals(scalars.get(j).name)) {
					operands.push((float)scalars.get(j).value);
					break;
				}
				
			}
    	}
    	while (operators.size() > 0) {
    		reverseOperators.push(operators.pop());
    	}
    	while (operands.size() > 0) {
    		reverseOperands.push(operands.pop());
    	}
    	while ((reverseOperands.size() > 1) && (reverseOperators.size() > 0)) {
			if (reverseOperators.size() == 1) {
				float x = reverseOperands.pop();
				float y = reverseOperands.pop();
				char operator = reverseOperators.pop();
				if (operator == '+') {
					reverseOperands.push(x + y);
				}
				else if (operator == '-') {
					reverseOperands.push(x - y);
				}
				else if (operator == '/') {
					reverseOperands.push(x/y);
				}
				else if (operator == '*') {
					reverseOperands.push(x*y);
				}
			}
			else {
				char firstOp = reverseOperators.pop();
				char secondOp = reverseOperators.pop();
				float x = reverseOperands.pop();
				float y = reverseOperands.pop();
				if ((firstOp == '+') && (secondOp == '+')) {
					reverseOperands.push(x+y);
					reverseOperators.push('+');
				}
				else if ((firstOp == '-') && (secondOp == '+')) {
					reverseOperands.push(x-y);
					reverseOperators.push('+');
				}
				else if ((firstOp == '+') && (secondOp == '-')) {
					reverseOperands.push(x+y);
					reverseOperators.push('-');
				}
				else if ((firstOp == '-') && (secondOp == '-')) {
					reverseOperands.push(x-y);
					reverseOperators.push('-');
				}
				else if ((firstOp == '*') && (secondOp == '*')) {
					reverseOperands.push(x*y);
					reverseOperators.push('*');
				}
				else if ((firstOp == '/') && (secondOp == '/')) {
					reverseOperands.push(x/y);
					reverseOperators.push('/');
				}
				else if ((firstOp == '/') && (secondOp == '*')) {
					reverseOperands.push(x/y);
					reverseOperators.push('*');
				}
				else if ((firstOp == '*') && (secondOp == '/')) {
					reverseOperands.push(x*y);
					reverseOperators.push('/');
				}
				else if ((firstOp == '*') && (secondOp == '+')) {
					reverseOperands.push(x*y);
					reverseOperators.push('+');
				}
				else if ((firstOp == '/') && (secondOp == '+')) {
					reverseOperands.push(x/y);
					reverseOperators.push('+');
				}
				else if ((firstOp == '*') && (secondOp == '-')) {
					reverseOperands.push(x*y);
					reverseOperators.push('-');
				}
				else if ((firstOp == '/') && (secondOp == '-')) {
					reverseOperands.push(x/y);
					reverseOperators.push('-');
				}
				else if ((firstOp == '+') && (secondOp == '*')) {
					float z = reverseOperands.pop();
					reverseOperands.push(y*z);
					reverseOperands.push(x);
					reverseOperators.push('+');
				}
				else if ((firstOp == '+') && (secondOp == '/')) {
					float z = reverseOperands.pop();
					reverseOperands.push(y/z);
					reverseOperands.push(x);
					reverseOperators.push('+');
				}
				else if ((firstOp == '-') && (secondOp == '*')) {
					float z = reverseOperands.pop();
					reverseOperands.push(y*z);
					reverseOperands.push(x);
					reverseOperators.push('-');
				}
				else if ((firstOp == '-') && (secondOp == '/')) {
					float z = reverseOperands.pop();
					reverseOperands.push(y/z);
					reverseOperands.push(x);
					reverseOperators.push('-');
				}
			}
		}
    	
    	
    	if (reverseOperands.size() > 0) {
    		return (float)(reverseOperands.pop());
    	}
    	return -1;
    	
    }

    /**
     * Utility method, prints the symbols in the scalars list
     */
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    		for (ArraySymbol as: arrays) {
    			System.out.println(as);
    		}
    }

}
