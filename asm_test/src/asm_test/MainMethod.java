package asm_test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


/*
 * You need to use asm-5.0_BETA.jar and asm-commons-5.0_BETA.jar
 * @http://stackoverflow.com/questions/22806946/getting-error-scanning-file-when-running-jetty-9-on-java-8-using-the-maven-jetty
 */
public class MainMethod {

    /**
     * @param args
     * @throws IOException 
     */
	/*
	*return -1 if the classes are not related or the distance if related.
	*/
    public static int findDistance(String classA, String classB) throws IOException {
    	List<String> superClass = new ArrayList<String>();
        ClassVisitor cl=new ClassVisitor(Opcodes.ASM4) {
            /**
             * Called when a class is visited. This is the method called first
             */
            @Override
            public void visit(int version, int access, String name,
                    String signature, String superName, String[] interfaces) {
//                System.out.println("Visiting class: "+name);
//                System.out.println("Class Major Version: "+version);
//                System.out.println("Super class: "+superName);
                superClass.add(superName);
                super.visit(version, access, name, signature, superName, interfaces);
            }
            
//            /**
//             * Invoked only when the class being visited is an inner class
//             */
//            @Override
//            public void visitOuterClass(String owner, String name, String desc) {
//                System.out.println("Outer class: "+owner);
//                super.visitOuterClass(owner, name, desc);
//            }
//
//            /**
//             *Invoked when a class level annotation is encountered
//             */
//            @Override
//            public AnnotationVisitor visitAnnotation(String desc,
//                    boolean visible) {
//                System.out.println("Annotation: "+desc);
//                return super.visitAnnotation(desc, visible);
//            }
//
//            /**
//             * When a class attribute is encountered 
//             */
//            @Override
//            public void visitAttribute(Attribute attr) {
//                System.out.println("Class Attribute: "+attr.type);
//                super.visitAttribute(attr);
//            }
//
//            /**
//             *When an inner class is encountered 
//             */
//            @Override
//            public void visitInnerClass(String name, String outerName,
//                    String innerName, int access) {
//                System.out.println("Inner Class: "+ innerName+" defined in "+outerName);
//                super.visitInnerClass(name, outerName, innerName, access);
//            }
//            
//            /**
//             * When a field is encountered
//             */
//            @Override
//            public FieldVisitor visitField(int access, String name,
//                    String desc, String signature, Object value) {
//                System.out.println("Field: "+name+" "+desc+" value:"+value);
//                return super.visitField(access, name, desc, signature, value);
//            }
//
//            
//            @Override
//            public void visitEnd() {
//                System.out.println("Method ends here");
//                super.visitEnd();
//            }
//
//            /**
//             * When a method is encountered
//             */
//            @Override
//            public MethodVisitor visitMethod(int access, String name,
//                    String desc, String signature, String[] exceptions) {
//                //System.out.println("Method: "+name+" "+desc);
//                return super.visitMethod(access, name, desc, signature, exceptions);
//            }
//
//            /**
//             * When the optional source is encountered
//             */
//            @Override
//            public void visitSource(String source, String debug) {
//                System.out.println("Source: "+source);
//                super.visitSource(source, debug);
//            }
            
            
        };
        int count=1;
        String className = classA;
        while(className!= null){
	        InputStream in=MainMethod.class.getResourceAsStream(className);
	        ClassReader classReader=new ClassReader(in);
	        classReader.accept(cl, 0);
	       
	        if(superClass.get(0) == null)
	        	className=null;
	        else{
		        System.out.println("Super count " +count+" is " + superClass.get(0).replace("/","."));
	        	className = "/" + superClass.get(0) + ".class";
	        	if(className.equals(classB)==true)
	        		return count;
	        	count++;
	        }
	        superClass.clear();
        }
		return -1;

    }
	
    public static String changeToInnerClass(String className){
    	return "/" + className.replace(".", "/") + ".class";
    }
    
    public static void Test1() throws IOException{
    	String classA = "java.lang.String";
    	String classB = "java.util.ArrayList";
    	int distance = Math.max(findDistance(changeToInnerClass(classA),changeToInnerClass(classB)),findDistance(changeToInnerClass(classB),changeToInnerClass(classA)));
    	System.out.println("Distance between " + classA + " and " +classB +" is :: " + distance);
    }
    
    public static void Test2() throws IOException{
    	String classA = "java.util.AbstractCollection";
    	String classB = "java.util.ArrayList";
    	int distance = Math.max(findDistance(changeToInnerClass(classA),changeToInnerClass(classB)),findDistance(changeToInnerClass(classB),changeToInnerClass(classA)));
    	System.out.println("Distance between " + classA + " and " +classB +" is :: " + distance);
    }
    public static void main(String[] args) throws IOException{
    	Test1();
    	System.out.println();
    	Test2();
    }
	

}
