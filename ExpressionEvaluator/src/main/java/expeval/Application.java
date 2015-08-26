package expeval;

public class Application {
	
	public static void main(String args[])
	{
		ExpressionEvaluator evaluator = new ExpressionEvaluator();
		try {
			evaluator.evaluate("c:\\wars\\input.txt");
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
}
