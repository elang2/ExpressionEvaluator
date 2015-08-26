package expeval;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Operators {

	private Set<String> mathOperators;
	private Set<String> assignmentOperators;
	private Map<String, Integer> operatorPrecedence;

	public static final String SELF_ADD = "+=";
	public static final String ADD = "+";
	public static final String MULTIPLY = "*";
	public static final String INCREMENT = "++";
	public static final String EQUALS = "=";

	public Operators() {

		mathOperators = new HashSet<>();
		assignmentOperators = new HashSet<>();
		operatorPrecedence = new HashMap<>();

		mathOperators.add(ADD);
		mathOperators.add(MULTIPLY);

		assignmentOperators.add(EQUALS);
		assignmentOperators.add(SELF_ADD);

		operatorPrecedence.put(ADD, 0);
		operatorPrecedence.put(MULTIPLY, 1);

	}

	public boolean isMathOpertor(String input) {
		return mathOperators.contains(input);
	}

	public boolean isAssignmentOperator(String input) {
		return assignmentOperators.contains(input);
	}

	public boolean isHigherPrecedence(String operatorOne, String operatorTwo) {
		return operatorPrecedence.get(operatorOne) > operatorPrecedence
				.get(operatorTwo);
	}

}
