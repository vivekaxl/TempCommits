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
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.SimpleName;



public class Test {
	protected static ASTNode parseStatements(String source) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_STATEMENTS);
		parser.setSource(source.toCharArray());
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		ASTNode root = parser.createAST(null);
		return root;
	}
	protected static Stream<ASTNode> getDescendants(ASTNode root, Predicate<ASTNode> condition) {
		NodeCollectionVisitor visitor = new NodeCollectionVisitor(condition);
		root.accept(visitor);
		return visitor.nodes.stream();
	}
	static void checkVariableDeclaration(){
		String source = "	RiWordnet wordnet = new RiWordnet(null);\n"
				+ "\n"
				+ "	String pos;\n\nList<String> listStart = FindSubstring(wordnet,start);\n"
				+ "	List<String> listEnd = FindSubstring(wordnet, end);\n "
				+ "	List<Float> distance = new ArrayList<Float>();\n "
				+ "	for(String s:listStart)\n"
				+ "		for(String e:listEnd){\n"
				+ "			pos = wordnet.getBestPos(s);\n"
				+ "			float dist = 1- wordnet.getDistance(s,e,pos);\n"
				+ "			distance.add(dist);\n"
				+ "		}\n"
				+ "	return Collections.max(distance);";
	final ASTNode root = parseStatements(source);
	System.out.println("----------------------------------");
	List<String> listVariableName = new ArrayList<String>();;
	List<ASTNode> listExpression = getDescendants(root,node -> node.getNodeType() == ASTNode.EXPRESSION_STATEMENT  ).collect(Collectors.toList());
	//System.out.println(listExpression);
    Iterator itr = listExpression.iterator();
    while(itr.hasNext()) {
    	ASTNode temp = (ASTNode) itr.next();
    	List<String> tempListSimplename = getDescendants(temp,node -> node.getNodeType() == ASTNode.SIMPLE_NAME).map(s->s.toString()).collect(Collectors.toList());
    	//System.out.println(tempListSimplename);
    	listVariableName.addAll(tempListSimplename);

    }
    
    System.out.println("listVariableName");
    System.out.println(listVariableName);
    List<String> listVariableDec = getDescendants(root,node -> node.getNodeType() == ASTNode.VARIABLE_DECLARATION_FRAGMENT  ).map(s->s.toString()).collect(Collectors.toList());
    System.out.println("listVariableDec");
	System.out.println(listVariableDec);
	
	if(listVariableDec.containsAll(listVariableName))
		System.out.println("All variable declared");
	else
		System.out.println("Variables not declared");
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
		System.out.println("----------------------------------");
		List<ASTNode> listMethodDeclaration = getDescendants(root,node -> node.getNodeType() == ASTNode.METHOD_DECLARATION ).collect(Collectors.toList());
		Iterator itr = listMethodDeclaration.iterator();
	    while(itr.hasNext()) {
	    	ASTNode temp = (ASTNode) itr.next();
	    	System.out.println(temp.getStartPosition());
	    	System.out.println(temp.getLength());
	    	System.out.println(source.substring(temp.getStartPosition(), temp.getStartPosition()+temp.getLength()));
	    }
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
		PrintWriter writer = new PrintWriter("temp.txt", "UTF-8");
		writer.println(source);
		writer.flush();
		writer.close();
		
		String readBack = null ;
		try {
			readBack = readFile("temp.txt",StandardCharsets.UTF_8);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
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
	
	/*
	 * Doesn't work correctly. Changing it!
	 */
	static void newcheckVariableDeclaration(){
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
		String source = "int x=0,y=0;";
		final ASTNode root = parseStatements(source);
    	List<ASTNode> tempListSimplename = getDescendants(root,node -> node.getNodeType() == ASTNode.SIMPLE_NAME).collect(Collectors.toList());
    	//System.out.println(tempListSimplename);
    	Iterator itr = tempListSimplename.iterator();
	    while(itr.hasNext()) {
	    	ASTNode temp = (ASTNode) itr.next();
	    	//List<ASTNode> listScopeVariable = getDescendants(temp,node -> node.getNodeType() == ASTNode.VARIABLE_DECLARATION_FRAGMENT).collect(Collectors.toList());
			//System.out.println(listScopeVariable);
	    	System.out.println(temp.getNodeType());
	    }
		
		
	}
	public static void main(String args[]){
		getVariables();
	}
}
