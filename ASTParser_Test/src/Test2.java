import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ITrackedNodePosition;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;

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

	public static String getMethodName(int depth)
	{
		depth=0;
		final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		return ste[ste.length - 1 - depth].getMethodName(); //Thank you Tom Tresansky
	}
	/*
	 * Prints a List
	 */
	public static  void printList (List<?> list2) 
	{      
		List<?> list= list2;
		for (int i = 0; i < list.size();  i++) 
			System.out.print(list2.get(i)+"\n");
		System.out.println();
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

	/*
	 * Read a file and store as a string
	 * @ http://stackoverflow.com/questions/16027229/reading-from-a-text-file-and-storing-in-a-string
	 */

	public static String readFile(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		} finally {
			br.close();
		}
	}
	//	static String readFile(String path, Charset encoding) 
	//			throws IOException 
	//	{
	//		byte[] encoded = Files.readAllBytes(Paths.get(path));
	//		return new String(encoded, encoding);
	//	}

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
		System.out.println("convertClasstoPackage:: className " +className);
		final Package[] packages = Package.getPackages();
		for (final Package p : packages) {
			final String pack = p.getName();
			final String tentative = pack + "." + className;
			try {
				Class.forName(tentative);
			} catch (final ClassNotFoundException e) {
				continue;
			}
			System.out.println("convertClasstoPackage:: "+pack);
			return(pack);
		}
		System.out.println("convertClasstoPackage:: Package not found!!");
		return("convertClasstoPackage:: Package not found!!");
	}

	/*
	 * Given a class name find the package it is associated to.
	 */	
	static String convertClasstoPackage2(String className) {

		try {
			Class cls = Class.forName(className);

			// returns the name and package of the class
			// System.out.println("Class = " + cls.getName());
			// System.out.println("Package = " + cls.getPackage().getName());
			return cls.getPackage().getName();
		}
		catch(ClassNotFoundException ex) {
			System.out.println(ex.toString());
			return null;
		}
	}


	static String getVariablesInScope(String source,String VariableName){
		// 		String source = "import java.util.ArrayList;\n"
		// 				+ "import java.util.List;\n"
		// 				+ "\n"
		// 				+ "class abc{\n"
		// 				+ "public void test(){\n"
		// 				+ "	RiWordnet wordnet = new RiWordnet(null);\n"
		// 				+ "\n"
		// 				+ "List<String> listStart = FindSubstring(wordnet,start);\n"
		// 				+ "	List<String> listEnd = FindSubstring(wordnet, end);\n "
		// 				+ "	List<Float> distance = new ArrayList<Float>();\n "
		// 				+ "	for(String s:listStart)\n"
		// 				+ "		for(String e:listEnd){\n"
		// 				+ "			pos = wordnet.getBestPos(s);\n"
		// 				+ "			dist = 1- wordnet.getDistance(s,e,pos);\n"
		// 				+ "			distance.add(dist);\n"
		// 				+ "		}\n"
		// 				+ "	return Collections.max(distance);"
		// 				+ "}\n"
		// 				+ "}\n";
		List<String> returnName = new ArrayList<String>();
		final CompilationUnit root = parseStatementsCompilationUnit(source);

		root.accept(new ASTVisitor() {

			public boolean visit(SimpleName node) {
				if(node.toString().equals(VariableName)){
					ASTNode temp =(ASTNode)node;
					while(temp.getNodeType() != ASTNode.METHOD_DECLARATION)
						temp=(ASTNode)temp.getParent();
					//System.out.println(((MethodDeclaration)temp).getName());
					if(returnName.contains(((MethodDeclaration)temp).getName().toString())==false)
						returnName.add(((MethodDeclaration)temp).getName().toString());
				}
				return true;
			}
		});
		if(returnName.size() >1){
			System.out.println("Something's wrong");
			return "";
		}
		else
			return returnName.get(0);
	}
	static boolean ifMethodExisit(CompilationUnit cu, String methodName){
		List<String> methodNames = new ArrayList<String>();
		cu.accept(new ASTVisitor() {
			public boolean visit(MethodDeclaration node){
				methodNames.add(node.getName().toString());
				return false;
			}
		});
		if(methodNames.contains(methodName)==true)
			return true;
		else
			return false;
	}


	/*
	 * Print all the declared variables in the scope of the method
	 */

	static List<Variables> getVariablesAndImport(String source,String methodName){

		//		String source = "import java.util.ArrayList;\n"
		//				+ "import java.util.List;\n"
		//				+ "\n"
		//				+ "class abc{\n"
		//				+ "public void test(){\n"
		//				+ "	RiWordnet wordnet = new RiWordnet(null);\n"
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
		//				+ "	return Collections.max(distance);"
		//				+ "}\n"
		//				+ "public void test2(){\n"
		//				+ "int a,b,c,d;\n"
		//				+ "}\n"
		//				+ "}\n";

		List<Variables> returnDeclared = new ArrayList<Variables>();
		//source = enclosedClasses(source);

		final CompilationUnit root = parseStatementsCompilationUnit(source);
		if(ifMethodExisit(root, methodName)==false){
			System.out.println("Method doesn't exist!");
			return null;
		}

		root.accept(new ASTVisitor() {
			public boolean visit(ImportDeclaration node){
				//System.out.println(node.getName().getFullyQualifiedName());
				return true;
			}
			public boolean visit(VariableDeclarationFragment node) {
				if(node.resolveBinding() != null){
					ASTNode temp = (ASTNode)node;
					while(temp.getNodeType() != ASTNode.VARIABLE_DECLARATION_STATEMENT)
						temp=temp.getParent();
					returnDeclared.add(new Variables(node.getName().toString(),"variable",((VariableDeclarationStatement) temp).getType().toString(),"","NA",root.getLineNumber(temp.getStartPosition())));
				}
				return false;
			}
			public boolean visit(SingleVariableDeclaration node) {

				System.out.println("2" +node.toString());
				if(node.resolveBinding() != null){
					System.out.println("!!!"+node.getType().toString());
					returnDeclared.add(new Variables(node.getName().toString(),"variable",node.getType().toString(),"","NA",root.getLineNumber(node.getStartPosition())));
				}
				return false;
			}
			public boolean visit(VariableDeclaration node) {
				if(node.resolveBinding() != null){if(node.resolveBinding() != null){
					ASTNode temp = (ASTNode)node;
					while(temp.getNodeType() != ASTNode.VARIABLE_DECLARATION_STATEMENT)
						temp=temp.getParent();
					returnDeclared.add(new Variables(node.getName().toString(),"variable",((VariableDeclarationStatement) temp).getType().toString(),"","NA",root.getLineNumber(temp.getStartPosition())));}
				}
				return false;     
			}
			public boolean visit(MethodDeclaration node){
				if(node.getName().toString().equals(methodName)){
					node.accept(new ASTVisitor() {		            
						public boolean visit(VariableDeclarationFragment node) {
							if(node.resolveBinding() != null){
								ASTNode temp = (ASTNode)node;
								while(temp.getNodeType() != ASTNode.VARIABLE_DECLARATION_STATEMENT)
									temp=temp.getParent();
								returnDeclared.add(new Variables(node.getName().toString(),"variable",((VariableDeclarationStatement) temp).getType().resolveBinding().getQualifiedName(),"","NA",root.getLineNumber(temp.getStartPosition())));
							}
							return false;
						}
						public boolean visit(SingleVariableDeclaration node) {
							if(node.resolveBinding() != null){
								returnDeclared.add(new Variables(node.getName().toString(),"variable",node.getType().resolveBinding().getQualifiedName(),"","NA",root.getLineNumber(node.getStartPosition())));
							}
							return false;
						}
						public boolean visit(VariableDeclaration node) {
							if(node.resolveBinding() != null){
								if(node.resolveBinding() != null){
									ASTNode temp = (ASTNode)node;
									while(temp.getNodeType() != ASTNode.VARIABLE_DECLARATION_STATEMENT)
										temp=temp.getParent();
									returnDeclared.add(new Variables(node.getName().toString(),"variable",((VariableDeclarationStatement) temp).getType().resolveBinding().getQualifiedName(),"","NA",root.getLineNumber(temp.getStartPosition())));
								}
							}
							return false;
						}
					});
				}
				return false;
			}
		});
		//				for(Variables element:returnDeclared){
		//					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>------------------------------------");
		//					System.out.println(element.name);
		//		//			System.out.println(element.type);
		//					System.out.println(element.variableType);
		//		//			System.out.println(element.packageImport);
		//		//			System.out.println(element.returnType);	
		//		//			System.out.println(element.lineNumber);
		//					System.out.println(element.lineNumber);
		//		//			System.out.println("------------------------------------");
		//				}
		return returnDeclared;
	}

	/*
	 * Prints all the undeclared variables
	 */
	static List<Variables> checkVariableDeclaration(String source){
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

		List<Variables> returnUndeclared = new ArrayList<Variables>();

		final CompilationUnit root = parseStatementsCompilationUnit(source);
		IProblem[] problems = root.getProblems();
		if (problems != null && problems.length > 0) {
			for (IProblem problem : problems) {
				System.out.println(problem.getMessage());
				System.out.println(problem.getID());

				System.out.println(IProblem.UnresolvedVariable);
				System.out.println(IProblem.UndefinedType);
				System.out.println(IProblem.UndefinedName);

				if(problem.getID() == IProblem.UnresolvedVariable){
					if(returnUndeclared.contains(new Variables(problem.getArguments()[0],"","",""))==false)
						returnUndeclared.add(new Variables(problem.getArguments()[0],"variable","","NA"));
				}
				else if(problem.getID() == IProblem.UndefinedType){
					if(returnUndeclared.contains(new Variables(problem.getArguments()[0],"","",""))==false)
						returnUndeclared.add(new Variables(problem.getArguments()[0],"type","NA",convertClasstoPackage(problem.getArguments()[0])));
				}
				else if(problem.getID() == IProblem.UndefinedName){
					System.out.println(problem.getArguments()[0]);
					//					if(returnUndeclared.contains(new Variables(problem.getArguments()[0],"","",""))==false)
					//						returnUndeclared.add(new Variables(problem.getArguments()[0],"variable","","NA"));
				}
				else{
					;
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

	static List<String> findElementsInfixExpression(InfixExpression node){
		List<String> returnValue = new ArrayList<String>();
		System.out.println("findElementsInfixExpression >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		if(node!=null){
			returnValue.add(node.getLeftOperand().toString());
			returnValue.add(node.getRightOperand().toString());
			List<Expression> extendedOperands = node.extendedOperands();
			for (Expression element : extendedOperands) {
				returnValue.add(element.toString());
			}
		}
		return returnValue;
	}
	/*
	 * Resolve types of objects which are not resolved
	 * */
	static List<ExpressionCollector> findExpressionStatement(DataCollector data,String tempString){
		List <ExpressionCollector> returnValue = new ArrayList<ExpressionCollector>();
		List <MethodInvocation> expressionStatements = new ArrayList<MethodInvocation>();
		List<String> returnBakerType = new ArrayList<String>();
		List<String> returnArgumentBakerType = new ArrayList<String>();
		final CompilationUnit root = parseStatementsCompilationUnit(tempString);
		//Find all the ExpressionStatement node and then look for method invocation which doesn't have an assignment sign
		root.accept(new ASTVisitor() {
			public boolean visit(ExpressionStatement node) {
				node.accept(new ASTVisitor(){
					public boolean visit(MethodInvocation node){
						ASTNode temp=node;
						while(temp.getParent() != null){
							if(temp.getNodeType() == ASTNode.ASSIGNMENT)
								return false;
							temp=temp.getParent();
						}
						System.out.println("1");
						System.out.println(node.getExpression().toString());
						if(node.getExpression().resolveTypeBinding()==null){
							int noArguments= node.arguments().size();
							List<Expression> actualArguments = node.arguments();
							System.out.println(node.getExpression().toString() + " is not declared");
							String className = node.getName().toString();
							System.out.println("ClassName :: "+ className);
							List<String> elementBaker= elementsMatchFromBaker(data,className);
							printList(elementBaker);
							System.out.println("Number of matching elements from Baker : " + elementBaker.size());
							//extra checking: checking if the arguments match (between baker elements and code)
							String tempElements = null;
							for(String e:elementBaker){
								System.out.println(noArguments);
								System.out.println(StringUtils.countMatches(e, ",") +1);
								System.out.println(e);
								if((StringUtils.countMatches(e, ",") +1) != noArguments){
									System.out.println("Somethings wrong!");
									elementBaker.remove(e);
								}
								else{
									tempElements=e.split("\\."+className)[0];
									if(returnBakerType.contains(tempElements)==false){
										returnBakerType.add(tempElements);
										System.out.println(tempElements);
									}
									String tempElementsA = null;
									Pattern pattern = Pattern.compile("\\(([^\"]*)\\)");
									Matcher m = pattern.matcher(e);
									if (m.find()){ 
										tempElementsA = m.group(1);
									}
									if(returnArgumentBakerType.contains(tempElementsA)==false){
										returnArgumentBakerType.add(tempElementsA);
										System.out.println("RBT " + tempElementsA);
									}
								}
							}
							System.out.println("returnBakerType.size() : " + returnBakerType.size());
							System.out.println("returnArgumentBakerType.size() : " + returnArgumentBakerType.size());
							returnValue.add(new ExpressionCollector(node.toString(),node.getExpression().toString(),"confused","",returnBakerType,null));
							if(returnArgumentBakerType.size() ==1){
								List<String> tempArg = Arrays.asList(returnArgumentBakerType.get(0).split("\\,"));
								for(int i=0;i<tempArg.size();i++){
									System.out.println("returnArgumentBakerType :: >>>>>>>>>>>>." + actualArguments.get(i).toString());
									returnValue.add(new ExpressionCollector(node.toString(),actualArguments.get(i).toString(),tempArg.get(i),"NA"));
								}
							}


						}
						return false;
					}					
				});
				return false;
			}
		});

		return returnValue;

	}


	/*
	 * Trying to resolve parameters from a ExpressionStatement + MethodInvocation
	 */
	static List<ExpressionCollector> findTypeParameter(DataCollector data,String tempString){
		System.out.println("findTypeParameter :: ");
		List <ExpressionCollector> returnValue = new ArrayList<ExpressionCollector>();
		List <MethodInvocation> expressionStatements = new ArrayList<MethodInvocation>();
		List<String> returnBakerReturnType = new ArrayList<String>();
		List<String> returnBakerArguements = new ArrayList<String>();
		final CompilationUnit root = parseStatementsCompilationUnit(tempString);
		//Find all the ExpressionStatement node and then look for method invocation which doesn't have an assignment sign
		root.accept(new ASTVisitor() {
			public boolean visit(ExpressionStatement node) {
				node.accept(new ASTVisitor(){
					public boolean visit(MethodInvocation node){
						ASTNode temp=node;
						while(temp.getParent() != null){
							if(temp.getNodeType() == ASTNode.ASSIGNMENT)
								return false;
							temp=temp.getParent();
						}
						System.out.println("1 " +node.toString());
						int noArguments=-1;
						expressionStatements.add(node);
						if(node.getExpression().resolveTypeBinding() == null){
							System.out.println("Return");
							return false;
						}
						String className = node.getExpression().toString() + "." + node.getName().toString();
												System.out.println("2"+node.getExpression().resolveTypeBinding());
						System.out.println("1" + className);
						//						if(node.resolveMethodBinding() != null)
						//							System.out.println("1" + node.resolveMethodBinding().toString());
						noArguments = node.arguments().size();
						//Find from baker what is the api this corresponds to
						List<String> elementBaker= elementsMatchFromBaker(data,className);
						printList(elementBaker);
						System.out.println("Number of arguments: " + noArguments);
						System.out.println("Number of matching elements from Baker : " + elementBaker.size());

						//extra checking: checking if the arguments match (between baker elements and code)
						String tempElements = null;
						for(String e:elementBaker){
							if((StringUtils.countMatches(e, ",") +1) != noArguments){
								System.out.println("Somethings wrong!");
								elementBaker.remove(e);
							}
							else{
								Pattern pattern = Pattern.compile("\\(([^\"]*)\\)");
								Matcher m = pattern.matcher(e);
								if (m.find()){ 
									tempElements = m.group(1);
								}
								if(returnBakerArguements.contains(tempElements)==false){
									returnBakerArguements.add(tempElements);
									System.out.println(tempElements);
								}

							}
						}
						System.out.println("returnBakerArguements.size() : " + returnBakerArguements.size());
						if(returnBakerArguements.size() == 1){
							System.out.println("Precision is 1");
							System.out.println(returnBakerArguements);
							String[] arg = returnBakerArguements.get(0).split("\\,");
							int count=0;
							for(Expression e2:(List<Expression>)node.arguments()){
								System.out.println("Test: " + e2.toString());
								returnValue.add(new ExpressionCollector(node.toString(),e2.toString(),arg[count++].replace(" ", ""),""));
							}
						}
						else{
							//TODO: Need to make a field in ExpressionCollector which would store all the possible types for a variable.
							System.out.println("Precision is >1");
							int count=0;
							for(int i=0;i<noArguments;i++){
								List<String> tempList = new ArrayList<String>();
								for(String bakerelement:returnBakerArguements){
									tempList.add(bakerelement.split("\\,")[count]);
								}
								returnValue.add(new ExpressionCollector(node.toString(),node.arguments().get(count).toString(),"confused","",tempList,null));
							}
						}
						printList(returnBakerArguements);	


						return false;
					}
				});

				return true;	
			}
		});
		//returnValue.stream().forEach(p->System.out.println(p.getVariableName()+ " , "+p.getReturnType()));



		return returnValue;
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
		List<String> returnBakerArguements = new ArrayList<String>();
		List <ExpressionCollector> returnValue = new ArrayList<ExpressionCollector>();
		List <Expression> expressionStatement = new ArrayList<Expression>();
		//String tempString = enclosedClasses(source);
		final CompilationUnit root = parseStatementsCompilationUnit(tempString);
		if(root == null)
			System.out.println("Something is wrong!!");
		root.accept(new ASTVisitor() {
			public boolean visit(Assignment node) {
				System.out.println("findLeftNodeType >>>>>>>>>> :: " + node.toString());
				expressionStatement.add(node.getRightHandSide());
				return true;	
			}
		});
		for(Expression e: expressionStatement){
			Expression node = e;
			System.out.println("findLeftNodeType expression :: " + node.toString());
			node.accept(new ASTVisitor(){
				public boolean visit(MethodInvocation node){
					int noArguments=-1;
					String className =null;
					if(node.getExpression().resolveTypeBinding()!=null)
						className = node.getExpression().toString() + "." + node.getName().toString();
					else
						className = node.getName().toString();
					//					System.out.println(node.getName().toString());
					//					System.out.println("findLeftNodeType arguments :: "+node.arguments());
					//					System.out.println("findLeftNodeType arguments size :: "+node.arguments().size());
					noArguments = node.arguments().size();
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
							System.out.println("Inside :: " +element2);
							if(returnBakerReturnType.contains(element2.split("\\-")[0])==false){
								returnBakerReturnType.add(element2.split("\\-")[0]);
							}
							if(returnBakerArguements.contains(element2.split("\\-")[1]) ==false 
									&& (StringUtils.countMatches(element2.split("\\-")[1],",") == (noArguments -1))){
								returnBakerArguements.add(element2.split("\\-")[1]);
							}
						}
					}
					System.out.println("4-Iamhere"+returnBakerReturnType.size());
					//					System.out.println("5-Iamhere"+returnBakerArguements.size());
					if(returnBakerReturnType.size() > 1 && returnBakerArguements.size() == 1){
						System.out.println("findLeftNodeType:: Different API but same parameter");
						returnValue.add(new ExpressionCollector(node.toString(),((Assignment)node.getParent()).getLeftHandSide().toString(), "confused",returnBakerArguements.get(0), returnBakerReturnType,returnBakerArguements));
					}
					else if(returnBakerReturnType.size() > 1 && returnBakerArguements.size() > 1){
						System.out.println("findLeftNodeType:: Different API and different parameter");
						returnValue.add(new ExpressionCollector(node.toString(),((Assignment)node.getParent()).getLeftHandSide().toString(), "confused","confused", returnBakerReturnType,returnBakerArguements));
					}
					else if(returnBakerReturnType.size() == 1 && returnBakerArguements.size() == 1){
						System.out.println("findLeftNodeType::Perfect");
						returnValue.add(new ExpressionCollector(node.toString(),((Assignment)node.getParent()).getLeftHandSide().toString(), returnBakerReturnType.get(0),returnBakerArguements.get(0)));
						List<Expression> tempArguments = node.arguments();
						printList(tempArguments);
						List<String> tempReturnArguments = Arrays.asList(returnBakerArguements.get(0).replace(" ", "").split("\\,"));
						printList(tempReturnArguments);
						if(tempArguments.size() == tempReturnArguments.size())
							for(int index=0;index<tempArguments.size();index++)
								returnValue.add(new ExpressionCollector(node.toString(),tempArguments.get(index).toString(),tempReturnArguments.get(index),"NA"));
						else
							System.out.println("Something is wrong!");
					}
					else if(returnBakerReturnType.size() == 1 && returnBakerArguements.size() == 0){
						System.out.println("findLeftNodeType::Perfect");
						returnValue.add(new ExpressionCollector(node.toString(),((Assignment)node.getParent()).getLeftHandSide().toString(), returnBakerReturnType.get(0),"NA"));
					}
					else if(returnBakerReturnType.size() == 1 && returnBakerArguements.size() > 1){
						System.out.println("findLeftNodeType::Perfect");
						returnValue.add(new ExpressionCollector(node.toString(),((Assignment)node.getParent()).getLeftHandSide().toString(), returnBakerReturnType.get(0),"confused", returnBakerReturnType,returnBakerArguements));
					}
					else if(returnBakerReturnType.size() == 0){
						returnValue.add(new ExpressionCollector(node.toString(),((Assignment)node.getParent()).getLeftHandSide().toString(), "unresolved","NA"));
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
						tempList.add(node.getLeftOperand().resolveTypeBinding().getTypeDeclaration().getQualifiedName());
					}
					else;
					//System.out.println(node.getLeftOperand().resolveTypeBinding());
					//System.out.println("================================== "+ node.getRightOperand().toString());
					if(node.getRightOperand().resolveTypeBinding() != null){
						//System.out.println(node.getRightOperand().resolveTypeBinding().getTypeDeclaration().getName());
						tempList.add(node.getRightOperand().resolveTypeBinding().getTypeDeclaration().getQualifiedName());
					}
					else;
					//System.out.println(node.getRightOperand().resolveTypeBinding());
					//System.out.println(node.getRightOperand().resolveTypeBinding());
					List<Expression> extendedOperands = node.extendedOperands();
					for (Expression element : extendedOperands) {
						//System.out.println(element.toString());
						if(element.resolveTypeBinding()!=null){
							//System.out.println(element.resolveTypeBinding().getTypeDeclaration().getName());
							tempList.add(element.resolveTypeBinding().getTypeDeclaration().getQualifiedName());
						}
						else;
						//System.out.println(element.resolveTypeBinding());
					}
					if(tempList.size()>1){
						returnValue.add(new ExpressionCollector(node.toString(),((Assignment)node.getParent()).getLeftHandSide().toString(), "confused","NA"));
					}
					else if(tempList.size() == 0){
						returnValue.add(new ExpressionCollector(node.toString(),((Assignment)node.getParent()).getLeftHandSide().toString(), "unresolved","NA"));
					}
					else{//tempList.size() ==1 : I am assuming that InfixExpression would have all elements of same type
						List<String> elementInfix = findElementsInfixExpression(node);
						returnValue.add(new ExpressionCollector(node.toString(),((Assignment)node.getParent()).getLeftHandSide().toString(), tempList.get(0),"NA"));
						for(String element:elementInfix) //No need to check if the element is a part of undeclared variable. Would be checked in mergeExpressionCollector()
							returnValue.add(new ExpressionCollector(node.toString(),element, tempList.get(0),"NA"));

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
		System.out.println("processQualifiedNames :: " +className);


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
			//			if(temp.type.equals("api_method"))
			{
				for(String tempElement:temp.elements){
					if(tempElement.contains(className) == true){
						//System.out.println("ElementsMatchFromBaker:: " + tempElement);
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
				Class[] paramVal = methVal.getParameterTypes();
				StringBuffer params = new StringBuffer();
				for (int j = 0; j < paramVal.length; j++) {
					if (j > 0)
						params.append(", ");
					params.append(paramVal[j].getName());
				}
				if(returnValues.contains(returnVal.getName())!=true){
					returnValues.add(returnVal.getName()+" - " + params);
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
			//System.out.println(getMethodName(0) + "I am confused!");
			;
		return returnValues;
	}

	static List<Variables> fillLineNumber(List<Variables> undeclaredVariables, String source){
		final CompilationUnit root = parseStatementsCompilationUnit(source);
		for(Variables element:undeclaredVariables){
			root.accept(new ASTVisitor() {
				public boolean visit(SimpleName node) {
					if(node.toString().equals(element.name)){
						if(element.lineNumber > root.getLineNumber(node.getStartPosition()) || element.lineNumber == -1 )
							element.lineNumber = root.getLineNumber(node.getStartPosition());
						return true;
					}
					else
						return true;
				}
			});
		}
		return undeclaredVariables;
	}

	static List<Variables> mergeExpressionCollector(List<ExpressionCollector>ec, List<Variables>undeclaredVariables, String source){
		//System.out.println("mergeExpressionCollector :: >>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		ec.stream().forEach(p->p.printData());
		System.out.println("=============================>>>>>>>>>>>>>>>>>>>=======================");
		for(Variables element: undeclaredVariables){
			for(ExpressionCollector e:ec){
				//				System.out.println("mergeExpressionCollector:" + element.name + " , " + e.getVariableName());
				//				System.out.println("mergeExpressionCollector12:" + e.getReturnType());
				if(element.name.equals("string1") && e.getVariableName().equals("string1"))
					System.out.println("It's here");
				if(element.name.equals(e.getVariableName())==true && (e.getReturnType().equals("confused")==false) && (e.getReturnType().equals("unresolved") ==false)){
					element.variableType.add(e.getReturnType());
					element.packageImport = convertClasstoPackage2(((element.variableType).get(0)).replace(" ",""));	
					//System.out.println(element.name + " , " + element.variableType + " , " + element.packageImport);
				}
				else if(element.name.equals(e.getVariableName())==true && (e.getReturnType().equals("confused")==true) && (e.getReturnType().equals("unresolved") ==false)){
					element.variableType=e.getReturnTypeList();
					//element.packageImport = convertClasstoPackage2(((element.variableType).get(0)).replace(" ",""));	
					//System.out.println(element.name + " , " + element.variableType + " , " + element.packageImport);
				}
			}
		}
		return undeclaredVariables;
	}

	public static Integer countList(List<Integer> list, Integer element){
		Integer count=0;
		for(Integer e:list)
			if(e==element)
				count++;
		return count;
	}


	static List<Variables> getInformationFromParameter(List<ExpressionCollector>ec, List<Variables>undeclaredVariables, String source){
		for(Variables element: undeclaredVariables){
			for(ExpressionCollector e:ec){
				//System.out.println("getInformationFromParameter >>>>>>>>>>>>> : " + e.getVariableName() + " " + element.name);
				//The type of element which was a undeclared variable before and the return type is not confused (can be more than one type) or unresolved
				if(element.name.equals(e.getVariableName())==true && (e.getReturnType().equals("confused")==false) && (e.getReturnType().equals("unresolved") ==false)){
					//TODO: try to find variable types from API signature returned by Baker
					if(e.getArgumentList()!= null && e.getArgumentList().size()>=1){ //Argument has more than or equal to one option
						//System.out.println("getInformationFromParameter Expression :: " +e.getExpression());
						//find all the variables in the expression
						final CompilationUnit root = parseStatementsCompilationUnit(source);
						root.accept(new ASTVisitor() {
							public boolean visit(MethodInvocation node) { //it has to be a method invocation
							//	System.out.println("getInformationFromParameter : " + node.toString());
								if(e.getExpression().contains(node.getName().toString())==true){ //find the method invocation node which contains the expression
									//System.out.println("getInformationFromParameter Found Expression :: " + node.arguments()); //argument of the method invocation
									int count =0;
									List<String> codeArguments = new ArrayList<String>();
									for(SimpleName name: (List<SimpleName>)node.arguments()){
										count++;
										//System.out.println("getInformationFromParameter Test 1 "+ name.toString());
										for(Variables tempElement: undeclaredVariables){ 
											if(tempElement.name.equals(name.toString())==true){//checking if the arguments are undeclared variables
												if(tempElement.variableType.size() == 1) //variable type of the undeclared Variable was resolved using fillUndeclaredVariablesFromBaker()
													codeArguments.add(tempElement.variableType.get(0));
												else
													codeArguments.add("X"); //if it has been not resolved add 'X' to the array
												//System.out.println("getInformationFromParameter : tempElement.name : " + tempElement.name + ", tempElement.variableType : "+ tempElement.variableType + ", argument number : " + count) ;
											}
										}
									}
									//System.out.println("Code Arguments >>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
									//printList(codeArguments);
									//System.out.println("Expression Collector Arguments >>>>>>>>>>>>>>>>>>>>>>>>>>>>");
									//Trying to see if some already resolved parameters can be used to find other parameters using resolved API
									List <Integer> similarity = new ArrayList<Integer>();
									for(String argument: e.getArgumentList()){
										List <String> expressionCollectorArg = Arrays.asList(argument.replace(" ", "").split("\\,"));
										//printList(expressionCollectorArg);
										if(codeArguments.size() != expressionCollectorArg.size()){
											//System.out.println("getInformationFromParameter: Something is wrong!!");
											return false;
										}
										else{
											int matches=0;
											for(int counter = 0; counter < codeArguments.size(); counter++) {
												//System.out.println("getInformationFromParameter:codeArgument "+codeArguments.get(counter));
												if(codeArguments.get(counter).equals(expressionCollectorArg.get(counter)) == true){
													//System.out.println("getInformationFromParameter: For " +e.getExpression() + "argument number " + counter + " matches");
													matches++;
												}
											}
											similarity.add(matches); //Find out how many signature matches (matches <= codeArguments.size() and matches <= expressionCollectorArg.size())
										}
									}
									//System.out.println("Maximum similarity : " +(float)Collections.max(similarity)/codeArguments.size());
									//System.out.println("Collisions: "+ countList(similarity, Collections.max(similarity)));
									if(countList(similarity, Collections.max(similarity)) == 1){
										int counter = 0;
										for(SimpleName name: (List<SimpleName>)node.arguments()){
											counter++;
											for(Variables tempElement: undeclaredVariables){
												if(tempElement.name.equals(name.toString())==true){
													String temp = (e.getArgumentList().get(similarity.indexOf(Collections.max(similarity))));
													tempElement.variableType.add(temp.replace(" ", "").split("\\,")[counter-1]);
												}
											}
										}
									}
								}
								return true;
							}
						});
					}
				}
			}
		}
		return undeclaredVariables;
	}

	static List<Variables> fillUndeclaredVariablesFromBaker(DataCollector data,List<Variables> undeclaredVariables){
		//Find elements with api_type
		for(Variables element:undeclaredVariables){
			for(DataCollectorSchema temp:data.listClasses){
				if(temp.type.equals("api_type"))
				{
					for(String tempElement:temp.elements){
						if(tempElement.contains(element.name) == true && temp.elements.size() == 1){
							element.packageImport = tempElement;
						}						
					}
				}
			}
		}
		return undeclaredVariables;
	}

	private static void AddStatements(String source)throws MalformedTreeException, BadLocationException, CoreException {

		final CompilationUnit root = parseStatementsCompilationUnit(source);

		// create a ASTRewrite
		AST ast = root.getAST();
		ASTRewrite rewriter = ASTRewrite.create(ast);

		// for getting insertion position
		TypeDeclaration typeDecl = (TypeDeclaration) root.types().get(0);
		MethodDeclaration methodDecl = typeDecl.getMethods()[0];
		Block block = methodDecl.getBody();

		// create new statements for insertion
		MethodInvocation newInvocation = ast.newMethodInvocation();
		newInvocation.setName(ast.newSimpleName("add"));
		Statement newStatement = ast.newExpressionStatement(newInvocation);

		//create ListRewrite
		ListRewrite listRewrite = rewriter.getListRewrite(block, Block.STATEMENTS_PROPERTY);
		listRewrite.insertFirst(newStatement, null);

		TextEdit edits = rewriter.rewriteAST();
		IDocument doc = new Document(source);

		edits.apply(doc);
		System.out.println(doc.get());
	}


	static void rewriteSource(String source){
		Document document = new org.eclipse.jface.text.Document(source);
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(document.get().toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		ASTRewrite rewriter = ASTRewrite.create(cu.getAST());

		AST ast = cu.getAST();

		// for getting insertion position
		TypeDeclaration typeDecl = (TypeDeclaration) cu.types().get(0);
		MethodDeclaration methodDecl = typeDecl.getMethods()[0];
		Block block = methodDecl.getBody();

		// create new statements for insertion
		MethodInvocation newInvocation = (MethodInvocation) ast.newMethodInvocation();
		newInvocation.setName(ast.newSimpleName("add"));
		Statement newStatement = ast.newExpressionStatement(newInvocation);

		//create ListRewrite
		ListRewrite listRewrite = rewriter.getListRewrite(block, Block.STATEMENTS_PROPERTY);
		listRewrite.insertFirst(newStatement, null);

		TextEdit edits = null;
		try {
			edits = rewriter.rewriteAST();
		} catch (JavaModelException | IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IDocument doc = new Document(source);

		try {
			edits.apply(doc);
		} catch (MalformedTreeException | BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(doc.get());
	}

	static boolean checkReturnStatement(String source, String methodName){
		final CompilationUnit root = parseStatementsCompilationUnit(source);
		List<String> returnList = new ArrayList<String>();
		if(root == null)
			System.out.println("It's null");
		root.accept(new ASTVisitor() {
			public boolean visit(MethodDeclaration node) {
				if(node.isConstructor() != true && node.getName().toString().equals(methodName) == true){
					//System.out.println("Method Name: " + node.getName().toString());
					//System.out.println("Return Type of the MethodDeclaration is :: " +node.getReturnType2().toString());
					returnList.add(node.getName().toString());
					node.accept(new ASTVisitor(){
						public boolean visit(ReturnStatement node1){
							//System.out.println("Return Type in MethodBody ::  " +node1.getExpression().toString() );
							returnList.clear();
							return true;
						}
					});
					return true;
				}
				else
					return false;
			}
		});
		if(returnList.size() == 0)
			return true;
		else
			return false;
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
		//checkVariableDeclaration();
	}

	static void Test3(){
		List<Variables> test = new ArrayList<Variables>();
		test.add(new Variables("a","","",""));
		test.add(new Variables("b","","",""));
		test.add(new Variables("c","","",""));
		Assert.isTrue(test.contains(new Variables("a","","","")) == true);
	}

	static void Test4(){
		String source = "RiWordnet wordnet = new RiWordnet(null);\n"
				+ "	String pos;\n"
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
		List<Variables> undeclaredVariables = checkVariableDeclaration(source);
		undeclaredVariables = fillLineNumber(undeclaredVariables, source);
		for(Variables element:undeclaredVariables){
			System.out.println("------------------------------------");
			System.out.println(element.name);
			System.out.println(element.type);
			System.out.println(element.packageImport);
			System.out.println(element.returnType);	
			System.out.println(element.lineNumber);
			System.out.println("------------------------------------");
		}

	}

	public static void Test5() throws IOException{
		String source = readFile("Snippet.txt");
		DataCollector data = null;
		source = addReturnStatements(source);
		List<Variables> undeclaredVariables = checkVariableDeclaration(source);

		try {
			data =  getTypesFromBaker(source);
		} catch (FileNotFoundException | UnsupportedEncodingException| InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Done!");
		List<String> importStatements = getImportStatement(source);
		List<ExpressionCollector>hints = extractHints(source);
		List<ExpressionCollector>returnValue4 = findReturnStatements(source); //Return Values
		System.out.println("=============================findReturnStatements=====================================================");
		returnValue4.stream().forEach(p->p.printData());
		System.out.println("===================================================================================");
		List<ExpressionCollector>returnValue3 = findExpressionStatement(data,source);
		System.out.println("===============================findExpressionStatement====================================================");
		returnValue3.stream().forEach(p->p.printData());
		System.out.println("===================================================================================");
		List<ExpressionCollector>returnValue2 = findTypeParameter(data,source); //MethodInvocation as expression
		System.out.println("=================================findTypeParameter==================================================");
		returnValue2.stream().forEach(p->p.printData());
		System.out.println("===================================================================================");
		List<ExpressionCollector>returnValue =  findLeftNodeType(data,source); //Assignment
		System.out.println("=====================================findLeftNodeType==============================================");
		returnValue.stream().forEach(p->p.printData());
		System.out.println("===================================================================================");

		//TODO: Need to check if addALL actually works for us!

		returnValue.addAll(returnValue4);
		returnValue.addAll(returnValue2);
		returnValue.addAll(returnValue3);
		System.out.println("===================================================================================");
		returnValue.stream().forEach(p->p.printData());
		System.out.println("===================================================================================");
		/*
		for(Variables element:undeclaredVariables){
			System.out.println("------------------------------------");
			System.out.println(element.name);
			//			System.out.println(element.type);
			System.out.println(element.variableType);
			//			System.out.println(element.packageImport);
			//			System.out.println(element.returnType);	
			//			System.out.println(element.lineNumber);
			System.out.println(element.lineNumber);
			//			System.out.println("------------------------------------");
		}*/
		undeclaredVariables = fillUndeclaredVariablesFromBaker(data,undeclaredVariables);
		undeclaredVariables = mergeExpressionCollector(returnValue,undeclaredVariables,source);
		undeclaredVariables = getInformationFromParameter(returnValue,undeclaredVariables,source);
		undeclaredVariables = fillLineNumber(undeclaredVariables, source);


		//		for(Variables element:undeclaredVariables){
		//			System.out.println("-----------------###----------------");
		//			System.out.println(element.name);
		//			System.out.println(element.type);
		//			System.out.println(element.variableType);
		//			System.out.println(element.packageImport);
		//			//			System.out.println(element.returnType);	
		//			//			System.out.println(element.lineNumber);
		//			System.out.println(element.lineNumber);
		//			//			System.out.println("------------------------------------");
		//		}
		for(Variables element:undeclaredVariables){

			if(element.type == "variable"){
				int count=0;
				System.out.println("#####################################");
				System.out.println("Undeclared Variables: " +element.name + " , " + element.variableType);
				List<Variables> declaredVariables =new ArrayList<Variables>(); 
				String methodName = getVariablesInScope(source, element.name);
				declaredVariables = getVariablesAndImport(source,methodName );
				declaredVariables = fillLineNumber(declaredVariables, source);
				if(element.variableType.size()==1){
					for(Variables e:declaredVariables)
						if((e.variableType.get(0).replace(" ", "")).equals((element.variableType.get(0)).replace(" ", "")) == true){
							System.out.println("Possible Options : (Which variables can be substituted)");
							System.out.println("------------------------------------");
							System.out.println(e.name);
							//					System.out.println(element.type);
							System.out.println(e.variableType);
							//					System.out.println(element.packageImport);
							//					System.out.println(element.returnType);	
							//					System.out.println(element.lineNumber);
							System.out.println(e.lineNumber);
							//					System.out.println("------------------------------------");
							count++;
						}
					if(count==0)
						System.out.println("Variable " + element.name +" needs declaration of type " + element.variableType );
				}
				else
					System.out.println("Variable " + element.name +" could not be resolved" );
			}
			else if(element.type == "type" && element.packageImport != ""){
				System.out.println("#####################################");
				if(importStatements.contains(element.packageImport) == false)
					System.out.println("Add Imports : " +element.packageImport + " "+element.name);
			}
		}
		System.out.println("#####################################");
		System.out.println("Consider the following hints");
		for(ExpressionCollector element:hints){
			System.out.println(element.getConstantExpression() + " might need to be changed in the expression " + element.getExpression());
		}
	}
	private static List<ExpressionCollector> findReturnStatements(String tempString) {
		final CompilationUnit root = parseStatementsCompilationUnit(tempString);
		List <ExpressionCollector> returnValue = new ArrayList<ExpressionCollector>();
		System.out.println(ASTNode.BLOCK);
		if(root == null)
			System.out.println("Something is wrong!");
		//find all method declaration
		//find all return Statements
		//if resolvebinding is null
		//get the type from the method signature

		root.accept(new ASTVisitor() {
			public boolean visit(MethodDeclaration node) {
				node.accept(new ASTVisitor(){
					public boolean visit(ReturnStatement node1){
						System.out.println(node1.getExpression().toString());
						if(node1.getExpression().resolveTypeBinding() == null){
							System.out.println("Variable " + node1.getExpression().toString() +  " is not declared");
							ASTNode temp = node1;
							while(temp.getNodeType() != ASTNode.METHOD_DECLARATION){
								System.out.println(temp.getNodeType());
								temp=temp.getParent();
							}
							if(((MethodDeclaration)temp).getReturnType2().resolveBinding() != null){
								System.out.println(((MethodDeclaration)temp).getReturnType2().resolveBinding().getQualifiedName());
								returnValue.add(new ExpressionCollector(node1.toString(),node1.getExpression().toString(),((MethodDeclaration)temp).getReturnType2().resolveBinding().getQualifiedName(),""));
							}
							else
								returnValue.add(new ExpressionCollector(node1.toString(),node1.getExpression().toString(),((MethodDeclaration)temp).getReturnType2().toString(),""));
						}
						return false;
					}
				});
				return false;
			}
		});

		return returnValue;
	}


	public static void Test6(){
		DataCollector data =  null;
		String source = "if(cn == null){\n"
				+ "String driver = \"com.mysql.jdbc.Driver\"; \n"
				+ "Class.forName(driver); \n"
				+ "dbHost = \"jdbc:mysql://\"+dbHost;\n"
				+ "cn = DriverManager.getConnection(dbHost,dbUser,dbPassword);\n"
				+ "System.out.println(\"test\");\n";

		String tempString = enclosedClasses(source);
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

	static void Test7(){
		String source=null;
		try {
			source = readFile("Snippet.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		DataCollector data=null;

		try {
			data =  getTypesFromBaker(source);
		} catch (FileNotFoundException | UnsupportedEncodingException| InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Done!");

		System.out.println("Baker Data =======================");
		data.printData();

		List<ExpressionCollector>returnValue3 = findExpressionStatement(data,source);
		returnValue3.stream().forEach(a->a.printData());
	}


	static void Test8() throws MalformedTreeException, BadLocationException, CoreException{
		String source=null;
		try {
			source = readFile("Snippet.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//checkReturnStatement(source);
		//rewriteSource(source);
		//
		//		   Document document= new Document(source);
		//
		//		   // creation of DOM/AST from a ICompilationUnit
		//		   CompilationUnit astRoot = parseStatementsCompilationUnit(source);
		//
		//		   // start record of the modifications
		//		   astRoot.recordModifications();
		//
		//		   // modify the AST
		//		   TypeDeclaration typeDeclaration = (TypeDeclaration)astRoot.types().get(0);
		//		   SimpleName newName = astRoot.getAST().newSimpleName("Y");
		//		   typeDeclaration.setName(newName);
		//
		//		   // computation of the text edits
		//		  // TextEdit edits = astRoot.rewrite(document, astRoot.getJavaProject().getOptions(true));
		//
		//		   // computation of the new source code
		//		  // edits.apply(document);
		//		   String newSource = document.get();

		CompilationUnit sourceCu = parseStatementsCompilationUnit(source);
		CompilationUnit targetCu = parseStatementsCompilationUnit(source);

		ASTRewrite rewriter = ASTRewrite.create(targetCu.getAST());            
		IDocument targetDoc = new Document(new String(source.toCharArray()));


	}

	static void Test9(){

		String code = "public class TestFormatter{public static void main(String[] args){System.out.println(\"Hello World\");}}";
		CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(null);

		TextEdit textEdit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, code, 0, code.length(), 0, null);
		IDocument doc = new Document(code);
		try {
			textEdit.apply(doc);
			System.out.println(doc.get());
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	static void Test10(){
		String doc=null;
		try {
			doc = readFile("Snippet.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(addReturnStatements(doc));
	}

	public static String addReturnStatements(String doc){

		Document document = new Document(doc);
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(document.get().toCharArray());
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		AST ast = cu.getAST();
		ASTRewrite rewriter = ASTRewrite.create(ast);

		TypeDeclaration typeDecl = (TypeDeclaration) cu.types().get(0);
		MethodDeclaration methodDecl;


		System.out.println("# of methods: "+typeDecl.getMethods().length );
		for(int i=0;i<(typeDecl.getMethods().length);i++){
			methodDecl = typeDecl.getMethods()[i];
			System.out.print("Method Name: "+methodDecl.getName().toString());
			if(checkReturnStatement(doc,methodDecl.getName().toString()) == true)
				System.out.println(" :: It's fine");
			else{
				System.out.println(" :: Return statement missing!");
				Block block = methodDecl.getBody();

				System.out.println("Method Return Type :: " + methodDecl.getReturnType2().toString());
				if(methodDecl.getReturnType2().toString().equals("void") == true)
					return doc;

				String returnTypeVariable = "return" + methodDecl.getReturnType2().toString();


				/*
				VariableDeclarationFragment fragment = ast.newVariableDeclarationFragment();
				fragment.setName(ast.newSimpleName(returnTypeVariable));
				// fragment.setType(ast.newSimpleType(ast.newSimpleName(methodDecl.getReturnType2().toString())));
				fragment.setInitializer(ast.newNullLiteral());
				VariableDeclarationStatement result = ast.newVariableDeclarationStatement(fragment);
				result.setType(ast.newSimpleType(ast.newSimpleName(methodDecl.getReturnType2().toString())));



				ListRewrite listRewriteSVD = rewriter.getListRewrite(block, Block.STATEMENTS_PROPERTY);
				listRewriteSVD.insertFirst(result, null);

				TextEdit edits = rewriter.rewriteAST(document, null);
				 */
				ReturnStatement newReturnStatement = ast.newReturnStatement();
				newReturnStatement.setExpression(ast.newSimpleName(returnTypeVariable));

				ListRewrite listRewrite = rewriter.getListRewrite(block, Block.STATEMENTS_PROPERTY);
				listRewrite.insertLast(newReturnStatement, null);

				TextEdit edits = rewriter.rewriteAST(document, null);

				try {
					UndoEdit undo = edits.apply(document);
				} catch (MalformedTreeException | BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}


		//	System.out.println(document.get().toCharArray());
		return document.get().toString();
	}

	public static void Test11(){
		String source=null;
		try {
			source = readFile("Snippet.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		source= addReturnStatements(source);
		findReturnStatements(source);
	}
	static void Test12(){
		String tempString=null;
		try {
			tempString = readFile("Snippet.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<ExpressionCollector> returnValue = extractHints(tempString);
		returnValue.stream().forEach(a->a.printData());
	} 

	//To find constant expression
	static List<ExpressionCollector> extractHints(String tempString){

		final CompilationUnit root = parseStatementsCompilationUnit(tempString);
		List <ExpressionCollector> hintValue = new ArrayList<ExpressionCollector>();
		if(root == null)
			System.out.println("Something is wrong!");

		root.accept(new ASTVisitor() {
			public boolean visit(MethodDeclaration root1){
				//System.out.println("new MethodDeclaration " +root1.getName());
				root1.accept(new ASTVisitor(){
					public boolean visit( VariableDeclarationStatement node) {
						node.accept(new ASTVisitor(){
							public boolean visit(VariableDeclarationFragment node1){
								if(node1.getInitializer()!= null){
									System.out.println("################# VariableDeclarationFragment :: " + node1.getInitializer().toString());//.resolveConstantExpressionValue());
									if(node1.getInitializer().resolveConstantExpressionValue() != null){
										hintValue.add(new ExpressionCollector(node1.toString(),node1.getInitializer().resolveConstantExpressionValue().toString()));
										System.out.println("RIGHT!");
									}
									node1.accept(new ASTVisitor(){
										public boolean visit(MethodInvocation node2){
											System.out.println("MI>>>>>>>>>>>>>" +node2.toString());
											List<Expression> temp = node2.arguments();
											for(int i=0;i<temp.size();i++){
												//System.out.println("MethodInvocation inside VariableDeclarationStatement:: ################" +temp.get(i).resolveConstantExpressionValue());
												if(temp.get(i).resolveConstantExpressionValue() != null){
													hintValue.add(new ExpressionCollector(node1.toString(),temp.get(i).resolveConstantExpressionValue().toString()));
													//	System.out.println("RIGHT!");
												}
											}
											System.out.println("MethodInvocation inside VariableDeclarationStatement:: " + node2.arguments()); 
											return true;

										}
										public boolean visit(ClassInstanceCreation node2){
											System.out.println("ClassInstanceCreate :: " +node2.toString());
											System.out.println("ClassInstanceCreate :: " +node2.arguments());
											for(int i=0;i<node2.arguments().size();i++){
												if(((Expression)node2.arguments().get(i)).resolveConstantExpressionValue()!= null){
													hintValue.add(new ExpressionCollector(node1.toString(),((Expression)node2.arguments().get(i)).resolveConstantExpressionValue().toString()));
													//	System.out.println(((Expression)node2.arguments().get(i)).resolveConstantExpressionValue().toString());
												}
											}
											return true;
										}
										public boolean visit(TypeLiteral node2){
											System.out.println("TypeLiteral : "+node2.toString());
											hintValue.add(new ExpressionCollector(node1.toString(),node2.toString()));
											return true;
										}
									});			
								}
								return false;
							}
						});

						return false;
					}

					public boolean visit(MethodInvocation node){
						System.out.println("OUTTAMI >>>>>>>>>>>>>>>>>>" + node.toString());
						System.out.println(node.arguments());
						List<Expression> temp = node.arguments();
						for(int i=0;i<temp.size();i++){
							//System.out.println(" MethodInvocation:: ################ " +temp.get(i).resolveConstantExpressionValue());
							if(temp.get(i).resolveConstantExpressionValue() != null){
								//System.out.println("RIGHT!");
								hintValue.add(new ExpressionCollector(node.toString(),temp.get(i).resolveConstantExpressionValue().toString()));
							}
						}
						return false;

					}
				});
				return false;
			}
		});

		return hintValue;
	}

	static void Test14(){
		String source=null;
		try {
			source = readFile("Snippet.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		source = addReturnStatements(source);
		List<Variables> undeclaredVariables = checkVariableDeclaration(source);

		for(Variables element:undeclaredVariables){
			if(element.type.equals("type") != true){
				System.out.println("------------------------------------");
				System.out.println(element.name);
				//			System.out.println(element.type);
				System.out.println(element.variableType);
				//			System.out.println(element.packageImport);
				//			System.out.println(element.returnType);	
				//			System.out.println(element.lineNumber);
				System.out.println(element.lineNumber);
				//			System.out.println("------------------------------------");
			}
		}
	}
	public static void Test15(){
		String source=null;
		try {
			source = readFile("Snippet.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		source = addReturnStatements(source);
		List<String> importStatements = getImportStatement(source);
		printList(importStatements);
	}
	public static void Test16(){
		String source=null;
		DataCollector data = null;
		try {
			source = readFile("Snippet.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			data =  getTypesFromBaker(source);
		} catch (FileNotFoundException | UnsupportedEncodingException| InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("-------------------------------------------");
		List<ExpressionCollector>returnValue3 = findLeftNodeType(data,source);
		returnValue3.stream().forEach(a->a.printData());
	}
	public static List<String> getImportStatement(String source){
		List<String> ImportStatements = new ArrayList<String>();
		final CompilationUnit root = parseStatementsCompilationUnit(source);
		if(root == null)
			System.out.println("Something is wrong!");

		root.accept(new ASTVisitor() {		
			public boolean visit(ImportDeclaration node){
				ImportStatements.add(node.getName().getFullyQualifiedName());
				return true;
			}
		});
		return ImportStatements;
	}

	public static void main(String args[]) throws InterruptedException, IOException{

		//checkVariableDeclaration();
		//		getVariableTypeFromDeclaration();
		//Test1();
		//findLeftNodeType();
		//findMethodDeclaration();
		//getVariables();
		//getTypesFromBaker();
		//printList(getMethodTest("java.sql.DriverManager","getConnection"));
		//processQualifiedNames("java.lang.Class.forName(java.lang.String,java.lang.String)");
		//findLeftNodeType();
		//Test5();
		//getVariablesAndImport();
		//		getVariablesInScope();
		//getVariablesAndImport("tesx");
		//		try {
		//			Test8();
		//		} catch (MalformedTreeException | BadLocationException | CoreException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//Test16();
		Test5();
		//		convertClasstoPackage2("java.sql.Connection");
	}

}