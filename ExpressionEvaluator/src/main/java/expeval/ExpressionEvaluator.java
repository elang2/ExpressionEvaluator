package expeval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import expeval.exception.InvalidSyntaxException;
import expeval.exception.MissingVariableException;


/**
 * 
 * @author Elankumaran Srinivasan
 * 
 * @version 1.0
 * 
 * This program will read the expression from a file and print out the final values of the expressions.
 * 
 *
 */

public class ExpressionEvaluator {

	public Operators operators = new Operators();		
	public Map<String, Integer> variableMap = new HashMap<>();
	public Stack<String> operatorStack = new Stack();
	public Stack<Integer> operandStack = new Stack();

	
	
	
	/*
	 * Read the contents from a file and evaluate it.
	 * 
	 * @Param filePath location of the file containing the expressions
	 *    
	 */
	
	public void evaluate(String filePath) throws Exception {

		try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
			String line = br.readLine();
			while (line != null) {
				if(line.trim().length()>0){
				evaluateExpression(parseLine(line.trim()));
				}
			
				line = br.readLine();
			}
			printVariableValues();
		} catch (IOException e) {
			throw new Exception("Unable to open the file");
		}
	}

	
	private String parseLine(String input) {
		return input;
	}


	/*
	 * Print the values of the variables  
	 */
	private void printVariableValues() {
		Iterator<String> mapIterator = variableMap.keySet().iterator();
		while(mapIterator.hasNext())
		{
			String key = mapIterator.next(); 
			System.out.println(key + "=" + variableMap.get(key));
		}
	}

	
	/* Analyze and split the expression into left hand side and right hand side operations
	 * 
	 * @Param line A single line from the input file. Each line represents an expression
	 */
	
	private void evaluateExpression(String line) throws Exception {
		
		String[] inputArray = line.split(" ");  // Split the expression into an array to easily retrieve operands and operators

		if (inputArray.length < 3) {			 // There needs to be at least three elements to make an expression. Left side, right side and an assigment.
			throw new InvalidSyntaxException();
		}

		String variableName = inputArray[0];
		String probableOperator = inputArray[1];

		
		
		if (operators.isMathOpertor(variableName) || operators.isAssignmentOperator(variableName) || operators.isMathOpertor(probableOperator)	|| !operators.isAssignmentOperator(probableOperator)) {
			throw new InvalidSyntaxException();	 	// Checking syntax. Variable needs to be followed by an assigment operations.
		}
		else {
			Integer number = variableMap.get(variableName);
			
			if (probableOperator.equals(operators.SELF_ADD)) {

				if (number == null) {
					throw new MissingVariableException(variableName);
							
				} else {
					operandStack.push(number);
					operatorStack.push(operators.ADD);
				}
			}
			variableMap.put(variableName, number == null ? 0 : number);
		}
		evaluateRightHandSide(inputArray, variableName);
	}
	
	
	/*
	 * Evaluate the right hand side
	 * 
	 * @Param inputArray  single line expression split into an array using whitespace
	 * @Param variableName  Name of the variable currently being evaliable. This will be the name of the variable present on the left hand side of the expression.
	 *   
	 */

	private void evaluateRightHandSide(String[] inputArray, String variableName) throws Exception {

		for (int i = 2; i < inputArray.length; i++) {

			if (operators.isAssignmentOperator(inputArray[i])) {
				throw new InvalidSyntaxException();
			}

			if (operators.isMathOpertor(inputArray[i])) {   // If an operator

				if (operandStack.isEmpty()) {
					throw new InvalidSyntaxException();  // Operator was first encountered before the operand.
				}

				if (operatorStack.isEmpty()) {
					operatorStack.push(inputArray[i]);	  // First operand encountered.
				}

				else {

					if (!operators.isHigherPrecedence(operatorStack.peek(),	inputArray[i]))	{
						operatorStack.push(inputArray[i]);		// If the operator on top of the operator stack is of equal or lower precedence than the one currently being read, then add the newly read operator to the stack.
					} else {
						while (!operatorStack.isEmpty() && operators.isHigherPrecedence(operatorStack.peek(), inputArray[i])) { 
							popAndCompute();		// Evaluate the infix expression using the two stack approach. One stack to hold the operands and another to hold the operators.
						}
						operatorStack.push(inputArray[i]);	
					}
				}
			}else { // This will be an operand.
				if (inputArray[i].contains(operators.INCREMENT)) {
					evaluatePrePostIncrement(inputArray[i]);
				} else {
					Integer number = variableMap.get(inputArray[i]);
					if (number != null) {
						operandStack.push(number);
					} else {
						
						try{
						operandStack.push(Integer.parseInt(inputArray[i]));
						}
						catch(NumberFormatException e)
						{
						throw new MissingVariableException(inputArray[i]);
						}
						
					}
				}
			}
		}

		while (!operatorStack.isEmpty()) {  // Once all the characters or inputs in an expression is read and then distributed in the stacks. Evaluate them by popping two operands at a time and an operator.
			popAndCompute();
		}
		
		
		variableMap.put(variableName, operandStack.pop());
	}

	private void evaluatePrePostIncrement(String input) throws Exception {
		String[] splitOperand = input.split("\\+");
		
		if (splitOperand.length > 3 || splitOperand.length==0) {
			throw new InvalidSyntaxException();
		}
		String variable = input.replace("+", "");

		if (input.indexOf(operators.ADD) == 0) {
			// Pre increment
			Integer number = variableMap.get(variable);
			number = number + 1;
			variableMap.put(variable, number);
			operandStack.push(number);
		} else {
			// post increment
			Integer number = variableMap.get(variable);
			operandStack.push(number);
			number = number + 1;
			variableMap.put(variable, number);
		}
	}

	private void popAndCompute() throws InvalidSyntaxException{

		
		if(operandStack.size()<=operatorStack.size())
		{
			throw new InvalidSyntaxException();
		}
		
		String operator = operatorStack.pop();
		int operandOne = operandStack.pop();
		int operandTwo = operandStack.pop();
		int result = 0;

		switch (operator) {
			case Operators.MULTIPLY :
				result = operandOne * operandTwo;
				break;
			case Operators.ADD :
				result = operandOne + operandTwo;
				break;
		}
		operandStack.push(result);
	}

}

