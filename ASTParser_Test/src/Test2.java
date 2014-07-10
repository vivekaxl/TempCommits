import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import test.ExpressionCollector;



public class Test2 {
	
	protected static ASTNode parseStatements(String source) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_STATEMENTS);
		parser.setSource(source.toCharArray());
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		ASTNode root = parser.createAST(null);
		return root;
	}
	/*
	 * Prints a List
	 */
	public static  void printList (List<?> list2) 
    {      
        List<?> list= list2;
        for (int i = 0; i < list.size();  i++) 
            System.out.print(list2.get(i)+"\n");
    }
	
	
	
	/*
	 * Summary: Prints all the compilation Errors
	 * Parameters: CompilationUnit
	 * Return: void 
	 */
	protected static void printCompilationErrors(CompilationUnit cu){
      //Prints all the Compilation Errors
      IProblem[] problems = cu.getProblems();
      if (problems != null && problems.length > 0) {
         System.out.println("Got problems compiling the source file: "+ problems.length);
          for (IProblem problem : problems) {
              System.out.println(problem);
          }
      }
	}
	
	protected static CompilationUnit parseStatementsCompilationUnit(String source) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
           parser.setSource(source.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        parser.setEnvironment( // apply classpath
                new String[] { "//home//vivek//Projects//SmartCopy//ASTParser_Test//bin" }, //
                null, null, true);
        parser.setUnitName("any_name");
        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		return cu;

	}
	protected static Stream<ASTNode> getDescendants(ASTNode root, Predicate<ASTNode> condition) {
		NodeCollectionVisitor visitor = new NodeCollectionVisitor(condition);
		root.accept(visitor);
		return visitor.nodes.stream();
	}
	public static void getVariables(){
		String source = "class abc{\n"
				+ " int a;\n "
				+ "int b;\n "
				+ "int c;\n"
				+ "int test;\n"
				+ "void add(int a,int b){\n "
				+ "int tex;\n "
				+ "float test;\n"
				+ "return(a+b);\n"
				+ "}\n "
				+ "public static void main(String args[]){\n"
				+ "int test;\n"
				+ " add(1,2);\n"
				+ "}\n"
				+ "}";
		final ASTNode root = parseStatements(source);
		System.out.println("----------------------------------");
		List<ASTNode> listVariableDeclaration = getDescendants(root,node -> node.getNodeType() == ASTNode.VARIABLE_DECLARATION_FRAGMENT ).collect(Collectors.toList());
		List<ASTNode> listMethodDeclaration = getDescendants(root,node -> node.getNodeType() == ASTNode.METHOD_DECLARATION ).collect(Collectors.toList());
		listMethodDeclaration.stream().map(node->node.toString()).forEach( e->System.out.println(e));
		Iterator itrM = listMethodDeclaration.iterator();
	    while(itrM.hasNext()) {
	    	ASTNode temp = (ASTNode) itrM.next();
	    	List<ASTNode> listScopeVariable = getDescendants(temp,node -> node.getNodeType() == ASTNode.VARIABLE_DECLARATION_FRAGMENT).collect(Collectors.toList());
	    	System.out.println("Declared inside the method");
			System.out.println(listScopeVariable);
			System.out.println("Declared inside the class");
			System.out.println(listVariableDeclaration.stream().filter(node->node.getParent().getNodeType() == 23).collect(Collectors.toList()));
			System.out.println();
	    }
		
	}
	public static String convertStreamToStr(InputStream is) throws IOException {
		 
			if (is != null) {
			Writer writer = new StringWriter();
			 
			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
					}
				} finally {
				is.close();
				}
			return writer.toString();
			}
		else {
			return "";
		}
	}
	
	static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}
	
	public static DataCollector getTypesFromBaker(String source) throws FileNotFoundException, UnsupportedEncodingException, InterruptedException{
		String response = null;
//		String source = "	RiWordnet wordnet = new RiWordnet(null);\n"
//				+ "\n"
//				+ "	String pos;\n"
//				+ "\n"
//				+ "List<String> listStart = FindSubstring(wordnet,start);\n"
//				+ "	List<String> listEnd = FindSubstring(wordnet, end);\n "
//				+ "	List<Float> distance = new ArrayList<Float>();\n "
//				+ "	for(String s:listStart)\n"
//				+ "		for(String e:listEnd){\n"
//				+ "			pos = wordnet.getBestPos(s);\n"
//				+ "			dist = 1- wordnet.getDistance(s,e,pos);\n"
//				+ "			distance.add(dist);\n"
//				+ "		}\n"
//				+ "	return Collections.max(distance);";
//		String source = "if(cn == null){\n"
//				+ "String driver = \"com.mysql.jdbc.Driver\"; \n"
//				+ "Class.forName(driver); \n"
//				+ "dbHost = \"jdbc:mysql://\"+dbHost;\n"
//				+ "cn = DriverManager.getConnection(dbHost,dbUser,dbPassword);\n"
//				+ "System.out.println(\"test\");\n";
		PrintWriter writer = new PrintWriter("temp.txt", "UTF-8");
		writer.println(source);
		writer.flush();
		writer.close();
		
	
		ProcessBuilder p=new ProcessBuilder("curl", "--data-urlencode","pastedcode@temp.txt", "http://gadget.cs.uwaterloo.ca:2145/snippet/getapijsonfromcode.php");
		try {
			System.out.println("Baker Start");
			final Process shell = p.start();
			InputStream shellIn = shell.getInputStream();
			int shellExitStatus = shell.waitFor();
			System.out.println(shellExitStatus);
			response = convertStreamToStr(shellIn);
			shellIn.close();
			System.out.println("Processed finished with status: " + shellExitStatus);
		} catch(Exception e){
			System.out.println(" getTypesFromBaker didn't work");
			e.printStackTrace();
		}
		String temp = response.substring(25, response.length()-3);
		DataCollector test = new DataCollector(source);
		test.insertData(temp);
		test.printData();
		return test;
	}
	
	static void getVariableTypeFromDeclaration(){
		String source = "	RiWordnet wordnet = new RiWordnet(null);\n"
		+ "\n"
		+ "	String pos;\n"
		+ "\n"
		+ "List<String> listStart = FindSubstring(wordnet,start);\n"
		+ "	List<String> listEnd = FindSubstring(wordnet, end);\n "
		+ "	List<Float> distance = new ArrayList<Float>();\n "
		+ "	for(String s:listStart)\n"
		+ "		for(String e:listEnd){\n"
		+ "			pos = wordnet.getBestPos(s);\n"
		+ "			dist = 1- wordnet.getDistance(s,e,pos);\n"
		+ "			distance.add(dist);\n"
		+ "		}\n"
		+ "	return Collections.max(distance);";
		final ASTNode root = parseStatements(source);
    	List<ASTNode> tempListDS = getDescendants(root,node -> node.getNodeType() == ASTNode.VARIABLE_DECLARATION_FRAGMENT).collect(Collectors.toList());
    	Iterator itrDF = tempListDS.iterator();
	    while(itrDF.hasNext()) {
	    	ASTNode temp = (ASTNode) itrDF.next();
	    	System.out.println(temp.toString());
	    	System.out.println(((VariableDeclarationStatement) (temp.getParent())).getType());
	    	System.out.println();
	    }
	}
	
	/*
	 * Given a class name find the package it is associated to.
	 * Caveat: The packages it looks into are the packages that are already loaded
	 * @http://stackoverflow.com/questions/8742965/how-to-find-the-package-name-given-a-class-name
	 */
	static String convertClasstoPackage(String className){
		    final Package[] packages = Package.getPackages();
		    for (final Package p : packages) {
		        final String pack = p.getName();
		        final String tentative = pack + "." + className;
		        try {
		            Class.forName(tentative);
		        } catch (final ClassNotFoundException e) {
		            continue;
		        }
		        System.out.println(pack);
		        return(pack);
		    }
		    return("Package not found!!");
	}
	
/*
 * Prints all the undeclared variables
 */
	static List<Variables> checkVariableDeclaration(/*String source*/){
//		String source ="package javaproject;" // package for all classes
//	            + "class Dummy {"
//	            + "int j;" //
//	            + "   public void add(){"
//	            + "int x=0,y=0;"
//	            + "j=x+y;\n"
//	            + "System.out.println(z);" //
//	            + "   }" //
//	            + "}"; 
//		String source = "if(cn == null){\n"
//				+ "String driver = \"com.mysql.jdbc.Driver\"; \n"
//				+ "Class.forName(driver); \n"
//				+ "dbHost = \"jdbc:mysql://\"+dbHost;\n"
//				+ "cn = DriverManager.getConnection(dbHost,dbUser,dbPassword);\n"
//				+ "System.out.println(\"test\");\n";
		String source = "	RiWordnet wordnet = new RiWordnet(null);\n"
		+ "\n"
		+ "	String pos;\n"
		+ "\n"
		+ "List<String> listStart = FindSubstring(wordnet,start);\n"
		+ "	List<String> listEnd = FindSubstring(wordnet, end);\n "
		+ "	List<Float> distance = new ArrayList<Float>();\n "
		+ "	for(String s:listStart)\n"
		+ "		for(String e:listEnd){\n"
		+ "			pos = wordnet.getBestPos(s);\n"
		+ "			dist = 1- wordnet.getDistance(s,e,pos);\n"
		+ "			distance.add(dist);\n"
		+ "		}\n"
		+ "	return Collections.max(distance);";

		source = enclosedClasses(source);
		List<Variables> returnUndeclared = new ArrayList<Variables>();

		final CompilationUnit root = parseStatementsCompilationUnit(source);
		IProblem[] problems = root.getProblems();
	      if (problems != null && problems.length > 0) {
	          for (IProblem problem : problems) {
	        	  if(problem.getID() == IProblem.UnresolvedVariable){
	        		  if(returnUndeclared.contains(new Variables(problem.getArguments()[0],"","",""))==false)
	        			  returnUndeclared.add(new Variables(problem.getArguments()[0],"variable","","NA"));
	        	  }
	        	  else if(problem.getID() == IProblem.UndefinedType){
	        		  if(returnUndeclared.contains(new Variables(problem.getArguments()[0],"","",""))==false)
	        			  returnUndeclared.add(new Variables(problem.getArguments()[0],"type","NA",convertClasstoPackage(problem.getArguments()[0])));
	        	  }
	        	  else if(problem.getID() == IProblem.UndefinedName){
	        		  if(returnUndeclared.contains(new Variables(problem.getArguments()[0],"","",""))==false)
	        			  returnUndeclared.add(new Variables(problem.getArguments()[0],"name","NA",convertClasstoPackage(problem.getArguments()[0])));
	        	  }
	        	  else{
		        	  System.out.println("------------------------------------");
		        	  System.out.println(problem);
		        	  System.out.println(problem.getID());
		        	  System.out.println("------------------------------------");
	        	  }
	        	  
	          }
	      }
		//printList(undeclaredVariables);
		//System.out.println();
		//printList(unresolvedTypes);
		//getListOfImports(unresolvedTypes);
		//System.out.println();
		//printList(undefinedNames);
		//getListOfImports(undefinedNames);
	     for(Variables element:returnUndeclared){
	    	 System.out.println("------------------------------------");
	    	 System.out.println(element.name);
	    	 System.out.println(element.type);
	    	 System.out.println(element.packageImport);
	    	 System.out.println(element.returnType);	    	 
	    	 System.out.println("------------------------------------");
	     }
		return returnUndeclared;
	}
	
	/*
	 * Given a list of classNames the method returns all the packages that needs to be imported
	 */
	
	public static void getListOfImports(List<String> classNames){
		for(String className:classNames){
			System.out.println(className);
			convertClasstoPackage(className);
		}
		System.out.println();
	}
	
	public static void findMethodDeclaration(){
		String source = "class abc{\n"
				+ " int a;\n "
				+ "int b;\n "
				+ "void add(int a,int b){\n "
				+ "return(a+b);\n"
				+ "}\n "
				+ "public static void main(String args[]){\n"
				+ " add(1,2);\n"
				+ "}\n"
				+ "}";

		final ASTNode root = parseStatements(source);
		List<ASTNode> listMethodDeclaration = getDescendants(root,node -> node.getNodeType() == ASTNode.METHOD_DECLARATION ).collect(Collectors.toList());
		Iterator itr = listMethodDeclaration.iterator();
	    while(itr.hasNext()) {
	    	System.out.println();
	    	ASTNode temp = (ASTNode) itr.next();
	    	System.out.print("Name of the declared method : ");
	    	System.out.println(((MethodDeclaration)temp).getName());
//	    	System.out.println(temp.getStartPosition());
//	    	System.out.println(temp.getLength());
	    	System.out.println("Body of Method Declartion : ");
	    	System.out.println(source.substring(temp.getStartPosition(), temp.getStartPosition()+temp.getLength()));
	    }
	}

	static String enclosedClasses(String source){
//		String source ="package javaproject;\n" // package for all classes
//	            + "class Dummy {\n"
//	            + "int j;\n" //
//	            + "   public int add(){\n"
//	            + "int x=0,y=0;\n"
//	            + "return(x+y);\n"
//	            + "   }\n" //
//	            + "}\n"; 
		
//		String source ="   public int add(){\n"
//	            + "int x=0,y=0;\n"
//	            + "return(x+y);\n"
//	            + "   }\n";

//		String source ="int x=0,y=0;\n"
//        + "return(x+y);\n";

		
		/*
		 * Can use type declaration and methodDeclaration to find if the snippet is enclosed in class and a method. 
		 */

		final CompilationUnit root = parseStatementsCompilationUnit(source);
		List<MethodDeclaration> listMethodDeclaration = new ArrayList<MethodDeclaration>();
		root.accept(new ASTVisitor() {
            public boolean visit(MethodDeclaration node) {
            		listMethodDeclaration.add(node);
            		return true;
            	}
            public boolean visit(TypeDeclaration node) {
        		//System.out.println("TD1");
        		return true;
        	}
			});
		
		//Adding a class around the snippet
		if(root.getProblems() != null && root.getProblems().length >0){
			//System.out.println("Compilation Problems second");
			String source_second = "class Test{\n" + source + "}\n";
			final CompilationUnit root_second = parseStatementsCompilationUnit(source_second);
			root_second.accept(new ASTVisitor() {
	            public boolean visit(MethodDeclaration node) {
	            	//System.out.println("MD2");
	            		listMethodDeclaration.add(node);
	            		return true;
	            	}
	            public boolean visit(TypeDeclaration node) {
	        		//System.out.println("TD2");
	        		return true;
	        	}
				});
			
			
			//Adding Method and class around the snippet
			if(root_second.getProblems() != null  && root_second.getProblems().length >0){
				//System.out.println("Compilation Problems third");
				String source_third = "class SmartCopyTestClass{\n" +"void SmartCopytestClass(){\n"+ source + "}\n" +"}\n";
				final CompilationUnit root_third = parseStatementsCompilationUnit(source_third);
				root_third.accept(new ASTVisitor() {
		            public boolean visit(MethodDeclaration node) {
		            	//System.out.println("MD3");
		            		listMethodDeclaration.add(node);
		            		return true;
		            	}
		            public boolean visit(TypeDeclaration node) {
		        		//System.out.println("TD3");
		        		return true;
		        	}
					});
				if(root_third.getProblems() != null && root_third.getProblems().length>0){
					source = source_third;
				}
			}
			else{
				source = source_second;
			}
		}

		
			Iterator itr = listMethodDeclaration.iterator();
			while(itr.hasNext()){
				MethodDeclaration temp = (MethodDeclaration) itr.next();
		   		 IMethodBinding binding = temp.resolveBinding();
		          if (binding != null) {
	          		  System.out.println("Method declared is :: ( "+temp.getName()+" ) at line"+ root.getLineNumber(temp.getStartPosition()));
		              ITypeBinding type = binding.getDeclaringClass();
		              if (type != null) {
		                  System.out.println("Class in which method is declared is : " + type.getName());
		              }
		          }
			}
			
			return source;	
		}
		
	static List<ExpressionCollector> findLeftNodeType(DataCollector data, String tempString){

//		String source = "if(cn == null){\n"
//		+ "String driver = \"com.mysql.jdbc.Driver\"; \n"
//		+ "Class.forName(driver); \n"
//		+ "dbHost = \"jdbc:mysql://\"+dbHost;\n"
//		+ "cn = DriverManager.getConnection(dbHost,dbUser,dbPassword);\n"
//		+ "System.out.println(\"test\");\n"
//		+ "}\n";
		List<String> tempList = new ArrayList<String>();
		List<String> returnBakerReturnType = new ArrayList<String>();
		List <ExpressionCollector> returnValue = new ArrayList<ExpressionCollector>();
		List <Expression> expressionStatement = new ArrayList<Expression>();
		//String tempString = enclosedClasses(source);
		final CompilationUnit root = parseStatementsCompilationUnit(tempString);
		root.accept(new ASTVisitor() {
            public boolean visit(Assignment node) {
            	//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>" + node.toString());
            	expressionStatement.add(node.getRightHandSide());
            	return true;	
            }
            });
		for(Expression e: expressionStatement){
			Expression node = e;
			System.out.println(node.toString());
	        node.accept(new ASTVisitor(){
	        	public boolean visit(MethodInvocation node){
	        		String className = node.getExpression().toString() + "." + node.getName().toString();
	        		//System.out.println(node.getName().toString());
	        		//System.out.println(node.arguments());
	        		//Find from baker what is the api this corresponds to
	        		List<String> elementBakerReturnType = elementsMatchFromBaker(data,className);
	        		for(String element:elementBakerReturnType){
	        			//System.out.println(element);
	        			//parse the api returned by the baker and break it down to className, methodName and parameterList
	        			QualifiedNames tempQN = processQualifiedNames(element);
	        			//find the return type of api based on the className and methodName
	        			//TODO: can pass parameters as well then there would be only one returnValue rather than a list of returnValues
	        			List<String> tempReturnType = getReturnType(tempQN.className, tempQN.methodName);
	        			for(String element2:tempReturnType){
	        				if(returnBakerReturnType.contains(element2)==false)
	        					returnBakerReturnType.add(element2);
	        			}
	        		}
	        		if(returnBakerReturnType.size() > 1){
	        			System.out.println("I am confused");
	        			returnValue.add(new ExpressionCollector(node.toString(), "confused"));
	        		}
	        		else if(tempList.size() == 0){
	    				returnValue.add(new ExpressionCollector(node.toString(), "unresolved"));
	    			}
	        		else{
	        			System.out.println("-----------------------------");
	        			Assignment temp = (Assignment) node.getParent();
	        			System.out.println(temp.getLeftHandSide().toString());
	        			System.out.println(node.getParent().getNodeType());
	        			//TODO
	        			//Make sure I include this in a structure of undeclared variables.
	        			System.out.println("-----------------------------");
	        			returnValue.add(new ExpressionCollector(node.toString(), returnBakerReturnType.get(0)));
	        		}
	        		return false;
	        	}
	        	/*
	        	 * Would return all the types of the operands in a Infix Expression
	        	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.InfixExpression)
	        	 */
	    		public boolean visit(InfixExpression node){
	    			
	    			//System.out.println("=============================Infix Expression : " +node.toString());
	    			//System.out.println(node.hasExtendedOperands());
	    			//System.out.println("================================== "+ node.getLeftOperand().toString());
	    			if(node.getLeftOperand().resolveTypeBinding() != null){
	    				//System.out.println(node.getLeftOperand().resolveTypeBinding().getTypeDeclaration().getName());
	    				tempList.add(node.getLeftOperand().resolveTypeBinding().getTypeDeclaration().getName());
	    			}
	    			else;
	    				//System.out.println(node.getLeftOperand().resolveTypeBinding());
	    			//System.out.println("================================== "+ node.getRightOperand().toString());
	    			if(node.getRightOperand().resolveTypeBinding() != null){
	    				//System.out.println(node.getRightOperand().resolveTypeBinding().getTypeDeclaration().getName());
	    				tempList.add(node.getRightOperand().resolveTypeBinding().getTypeDeclaration().getName());
	    			}
	    			else;
	    				//System.out.println(node.getRightOperand().resolveTypeBinding());
	    			//System.out.println(node.getRightOperand().resolveTypeBinding());
	    			List<Expression> extendedOperands = node.extendedOperands();
	    			for (Expression element : extendedOperands) {
	    				//System.out.println(element.toString());
	    				if(element.resolveTypeBinding()!=null){
	    					//System.out.println(element.resolveTypeBinding().getTypeDeclaration().getName());
	    					tempList.add(element.resolveTypeBinding().getTypeDeclaration().getName());
	    				}
	    				else;
	    					//System.out.println(element.resolveTypeBinding());
	    			}
	    			if(tempList.size()>1){
	    				returnValue.add(new ExpressionCollector(node.toString(), "confused"));
	    			}
	    			else if(tempList.size() == 0){
	    				returnValue.add(new ExpressionCollector(node.toString(), "unresolved"));
	    			}
	    			else{
	    				returnValue.add(new ExpressionCollector(node.toString(), tempList.get(0)));
	    			}
	    			
					return false;
	    		}
	    	});
	        
	        
		}
		return returnValue;
	}
	
	static QualifiedNames processQualifiedNames(String name){
		QualifiedNames returnValue = new QualifiedNames();
		//Pattern pattern = Pattern.compile("\\(([A-Za-z0-9.,]+)\\)");
		Pattern pattern = Pattern.compile("\\(([^\"]*)\\)");
		Matcher m = pattern.matcher(name);
		if (m.find()){ 
			String[] tempElements = m.group(1).split(",");
			returnValue.setArguments(Arrays.asList(tempElements));
		}
		name = name.replaceAll("\\(.*\\)", "");
		String[] temp = name.split("\\.");
		returnValue.setMethodName(temp[temp.length-1]);
		
		String className = temp[0];
		for(int i=1;i<temp.length-1;i++)
			className = className + "." + temp[i];
		returnValue.setClassName(className);
		System.out.println(className);
		

//		printList(returnValue.getArguments());
//		System.out.println(returnValue.getMethodName());
//		System.out.println(returnValue.getClassName());
		
		return returnValue;
	}
	/*
	 * Given an incomplete className.methodName, it parses through the data returned by Baker 
	 * and returns the complete list of matching qualified names	 * 
	 */
	static List<String> elementsMatchFromBaker(DataCollector data, String className){
		//Find elements with api_method
		List<String> returnMethodName = new ArrayList<String>();
		for(DataCollectorSchema temp:data.listClasses){
			if(temp.type.equals("api_method")){
				for(String tempElement:temp.elements){
					if(tempElement.contains(className) == true){
						returnMethodName.add(tempElement);
					}						
				}
			}
		}
		return returnMethodName;
	}
	
	/*
	 * Given class name and method, the method would return the return values of the methods
	 * Input: ClassName and Method Name
	 * Return: List of ReturnTypes. Sometime methods are overloaded.
	 */
	public static List<String> getReturnType(String className, String methodName){
			
		Class inspect = null;
		try {
			inspect = Class.forName(className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      Method[] methods = inspect.getDeclaredMethods();
	      List<String> returnValues = new ArrayList<String>();
	      for (int i = 0; i < methods.length; i++) {
	    	  if(methods[i].getName().equals(methodName)){
	    		  System.out.println("Method Found");
	  	        Method methVal = methods[i];
		        Class returnVal = methVal.getReturnType();
//		        int mods = methVal.getModifiers();
//		        String modVal = Modifier.toString(mods);
//		        Class[] paramVal = methVal.getParameterTypes();
//		        StringBuffer params = new StringBuffer();
//		        for (int j = 0; j < paramVal.length; j++) {
//		          if (j > 0)
//		            params.append(", ");
//		          params.append(paramVal[j].getName());
//		        }
		        if(returnValues.contains(returnVal.getName())!=true){
		        	returnValues.add(returnVal.getName());
		        	System.out.println("Return Type: " +returnVal.getName());
		        }
//		        
//		        System.out.println("Method: " + methVal.getName() + "()");
//		        System.out.println("Modifiers: " + modVal);
//		        System.out.println("Return Type: " + returnVal.getName());
//		        System.out.println("Parameters: " + params + "\n");
	    	  }
	      }
	      if(returnValues.size()>1)
	    	  System.out.println("I am confused!");
		return returnValues;
	}
	static void Test1(){
		String source = "if(cn == null){\n"
		+ "String driver = \"com.mysql.jdbc.Driver\"; \n"
		+ "Class.forName(driver); \n"
		+ "dbHost = \"jdbc:mysql://\"+dbHost;\n"
		+ "cn = DriverManager.getConnection(dbHost,dbUser,dbPassword);\n"
		+ "System.out.println(\"test\");\n"
		+ "}\n";

		DataCollector data = null;
		//enclosing the code snippet into classes 
		String tempString = enclosedClasses(source); 
		//get list of undeclared variables
		//List<String> undeclaredVariables = checkVariableDeclaration(tempString);
		//get types from the Baker (http://gadget.cs.uwaterloo.ca:2145/snippet/onlineextractor.html)
		try {
			 data =  getTypesFromBaker(tempString);
		} catch (FileNotFoundException | UnsupportedEncodingException
				| InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done!");
		//printList(elementsMatchFromBaker(data,"Class.forName"));
		
		List<ExpressionCollector>returnValue =  findLeftNodeType(data,tempString);
		returnValue.stream().forEach(p->p.printData());
	}
	
	static void Test2(){
		String source = "if(cn == null){\n"
		+ "String driver = \"com.mysql.jdbc.Driver\"; \n"
		+ "Class.forName(driver); \n"
		+ "dbHost = \"jdbc:mysql://\"+dbHost;\n"
		+ "cn = DriverManager.getConnection(dbHost,dbUser,dbPassword);\n"
		+ "System.out.println(\"test\");\n"
		+ "}\n";
		
		String tempString = enclosedClasses(source);
		final CompilationUnit root = parseStatementsCompilationUnit(tempString);
		checkVariableDeclaration();
	}
	
	static void Test3(){
		List<Variables> test = new ArrayList<Variables>();
		test.add(new Variables("a","","",""));
		test.add(new Variables("b","","",""));
		test.add(new Variables("c","","",""));
		Assert.isTrue(test.contains(new Variables("a","","","")) == true);
	}
	public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException, InterruptedException{
		
		checkVariableDeclaration();
//		getVariableTypeFromDeclaration();
		//Test1();
		//findLeftNodeType();
		//findMethodDeclaration();
		//getVariables();
		//getTypesFromBaker();
		//printList(getMethodTest("java.sql.DriverManager","getConnection"));
		//processQualifiedNames("java.lang.Class.forName(java.lang.String,java.lang.String)");
		//findLeftNodeType();
		//Test3();
	}
}
