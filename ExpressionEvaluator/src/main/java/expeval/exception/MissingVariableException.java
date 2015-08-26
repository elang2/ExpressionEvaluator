package expeval.exception;

public class MissingVariableException extends Exception {

	public MissingVariableException(String variableName) {
		super("Missing definition for variable : " + variableName);
	}

}
