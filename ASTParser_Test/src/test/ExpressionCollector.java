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
	public ExpressionCollector(String expression,String variableName, String returnType,String arguments) {
		super();
		this.expression = expression;
		this.variableName = variableName;
		this.returnType = returnType;
		this.arguments = arguments;
	}
	String expression;
	String variableName;
	public String getVariableName() {
		return variableName;
	}
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
	String returnType;
	String arguments;
	
	public void printData(){
		System.out.println("Expression: " + this.expression);
		System.out.println("Variable Name: " + this.variableName);
		System.out.println("Return Type: " + this.returnType);
		System.out.println("Arguments: " + this.arguments);
		System.out.println();
	}
	
}
