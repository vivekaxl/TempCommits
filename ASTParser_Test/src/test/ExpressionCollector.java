package test;

public class ExpressionCollector {

	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public ExpressionCollector(String expression, String returnType) {
		super();
		this.expression = expression;
		this.returnType = returnType;
	}
	String expression;
	String returnType;
	
	public void printData(){
		System.out.println("Expression: " + this.expression);
		System.out.println("Return Type: " + this.returnType);
	}
	
}
