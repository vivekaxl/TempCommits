package test;

import java.util.List;

public class ExpressionCollector {
	public static  void printList (List<?> list2) 
	{      
		List<?> list= list2;
		for (int i = 0; i < list.size();  i++) 
			System.out.print(list2.get(i)+"\n");
	}
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
		this.returnTypeList = null;
		this.argumentList = null;
	}
	
	public ExpressionCollector(String expression,String variableName, String returnType,String arguments, List<String> returnTypeList, List<String> argumentList) {
		super();
		this.expression = expression;
		this.variableName = variableName;
		this.returnType = returnType;
		this.arguments = arguments;
		this.returnTypeList = returnTypeList;
		this.argumentList = argumentList;
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
	List<String> returnTypeList;
	List<String> argumentList;
	
	public void printData(){
		System.out.println("Expression: " + this.expression);
		System.out.println("Variable Name: " + this.variableName);
		System.out.println("Return Type: " + this.returnType);
		System.out.println("Arguments: " + this.arguments);
		if(this.returnTypeList!= null && this.returnTypeList.size() != 1){
			System.out.println("Return Type List: ");
			printList(this.returnTypeList);
		}
		if(this.argumentList != null && this.argumentList.size() != 1){
			System.out.println("Argument Type List: ");
			printList(this.argumentList);
		}
		System.out.println();
	}
	
}
