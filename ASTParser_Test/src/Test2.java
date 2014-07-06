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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
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
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;



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
        
//        //Prints all the Compilation Errors
//        IProblem[] problems = cu.getProblems();
//        if (problems != null && problems.length > 0) {
//           System.out.println("Got problems compiling the source file: "+ problems.length);
//            for (IProblem problem : problems) {
//                System.out.println(problem);
//            }
//        }
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
	
	public static void getTypesFromBaker() throws FileNotFoundException, UnsupportedEncodingException, InterruptedException{
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
		String source = "if(cn == null){"
				+ "String driver = \"com.mysql.jdbc.Driver\"; "
				+ "Class.forName(driver); "
				+ "dbHost = \"jdbc:mysql://\"+dbHost;"
				+ "cn = DriverManager.getConnection(dbHost,dbUser,dbPassword);";
		PrintWriter writer = new PrintWriter("temp.txt", "UTF-8");
		writer.println(source);
		writer.flush();
		writer.close();
		
	
		ProcessBuilder p=new ProcessBuilder("curl", "--data-urlencode","pastedcode@temp.txt", "http://gadget.cs.uwaterloo.ca:2145/snippet/getapijsonfromcode.php");
		try {
			final Process shell = p.start();
			InputStream shellIn = shell.getInputStream();
			int shellExitStatus = shell.waitFor();			 
			response = convertStreamToStr(shellIn);
			shellIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String temp = response.substring(25, response.length()-3);
		DataCollector test = new DataCollector();
		test.insertData(temp);
		test.printData();
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
 * Prints all the undeclared variables
 */
	static void checkVariableDeclaration(){
		String source ="package javaproject;" // package for all classes
	            + "class Dummy {"
	            + "int j;" //
	            + "   public void add(){"
	            + "int x=0,y=0;"
	            + "j=x+y;\n"
	            + "System.out.println(z);" //
	            + "   }" //
	            + "}"; 

		final CompilationUnit root = parseStatementsCompilationUnit(source);
		root.accept(new ASTVisitor() {
            public boolean visit(SimpleName node) {
            	if(node.resolveBinding() == null){
            		System.out.println(node.toString()+" is not declared");
            	}
//            	else{
//            		System.out.println(node.toString() + " is declared");
//            	}
            	return true;
            }
		});        
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

	static void enclosedClasses(){
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

		String source ="int x=0,y=0;\n"
        + "return(x+y)\n";

		final CompilationUnit root = parseStatementsCompilationUnit(source);
		List<MethodDeclaration> listMethodDeclaration = new ArrayList();
		root.accept(new ASTVisitor() {
            public boolean visit(MethodDeclaration node) {
            		listMethodDeclaration.add(node);
            		return true;
            	}
			});
		
		//Adding a class around the snippet
		if(root.getProblems() != null){
			System.out.println("Compilation Problems second");
			String source_second = "class Test{\n" + source + "}\n";
			final CompilationUnit root_second = parseStatementsCompilationUnit(source_second);
			root_second.accept(new ASTVisitor() {
	            public boolean visit(MethodDeclaration node) {
	            		listMethodDeclaration.add(node);
	            		return true;
	            	}
				});
		}
		
		//Adding Method and class around the snippet
		if(root.getProblems() != null){
			System.out.println("Compilation Problems third");
			String source_third = "class SmartCopyTestClass{\n" +"void SmartCopytestClass(){\n"+ source + "}\n" +"}\n";
			final CompilationUnit root_second = parseStatementsCompilationUnit(source_third);
			root_second.accept(new ASTVisitor() {
	            public boolean visit(MethodDeclaration node) {
	            		listMethodDeclaration.add(node);
	            		return true;
	            	}
				});
		}
		
		if(root.getProblems() != null){
			System.out.println("snippet has errors");
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
			
//		}
		



	}
	
	
	public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException, InterruptedException{
		checkVariableDeclaration();
//		getVariableTypeFromDeclaration();
	//enclosedClasses();
		//findMethodDeclaration();
		//getVariables();
	}
}
