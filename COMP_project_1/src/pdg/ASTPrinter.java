package pdg;
import java.io.FileInputStream;
import java.io.IOException;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.mxgraph.view.mxGraph;

public class ASTPrinter {
	private static FileInputStream in;
	
	public ASTPrinter() {	}
	
	public static void addFile(FileInputStream inArg, DirectedGraph<String, DefaultEdge> hrefGraph, String previousNode) throws ParseException, IOException {
		in = inArg;
		CompilationUnit cu;
		try {
		// parse the file
			cu = JavaParser.parse(in);
		} finally {
		    in.close();
		}    
		new CodeVisitor().processNode(cu, hrefGraph, previousNode);
	}
	
}


    /**
     * Simple visitor implementation for visiting nodes. 
     */
    class CodeVisitor extends VoidVisitorAdapter<Object> {
    	
    	void processNode(Node child2, DirectedGraph<String, DefaultEdge> hrefGraph, String previousNode){ 
    		String nextNode = previousNode;
    		
    		if(relevant(child2)) {
    			if(child2.getClass().equals(com.github.javaparser.ast.body.MethodDeclaration.class)){
    				printMethodModifiers(child2);
    				MethodType(child2);
    				MethodName(child2);
    			}
    			else if(child2.getClass().equals(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration.class)){
    				printClassIntModifiers(child2);
    				ClassName(child2);
    				ClassExtension(child2);
    			}    		
    			
    			else if (child2.getClass().equals(com.github.javaparser.ast.expr.AssignExpr.class)) {
    				hrefGraph.addVertex(child2.toString());
    				hrefGraph.addEdge(previousNode, child2.toString());
    				nextNode = child2.toString();
    			}
    			
    			else{
    				System.out.println("------------------------------------------------------------");
    				System.out.println(child2.getClass());
    				System.out.println(child2.toString());
    			}
    		}	
    		
    		
    		for(Node child: child2.getChildrenNodes()){
    			
    			processNode(child, hrefGraph, nextNode);
    		}
    	}

		private boolean relevant(Node child2) {
			if(child2.getClass().equals(com.github.javaparser.ast.body.VariableDeclarator.class))
				return false;
			if(child2.getClass().equals(com.github.javaparser.ast.CompilationUnit.class))
				return false;
			if(child2.getClass().equals(com.github.javaparser.ast.stmt.ExpressionStmt.class))
				return false;
			if(child2.getClass().equals(com.github.javaparser.ast.expr.FieldAccessExpr.class))
				return false;
			if(child2.getClass().equals(com.github.javaparser.ast.stmt.BlockStmt.class))
				return false;
			if(child2.getClass().equals(com.github.javaparser.ast.type.VoidType.class))
				return false;
			if(child2.getClass().equals(com.github.javaparser.ast.type.ClassOrInterfaceType.class))
				return false;
			return true;
		}
		
		private void MethodType(Node child2){
			if(child2.getClass().equals(com.github.javaparser.ast.body.MethodDeclaration.class)) {
					System.out.println("------------------------------------------------------------");
					System.out.print("Method.Type\n"+((MethodDeclaration)child2).getType().toString()+"\n");
				
			}
		}
		
		private void MethodName(Node child2){
			if(child2.getClass().equals(com.github.javaparser.ast.body.MethodDeclaration.class)) {
					System.out.println("------------------------------------------------------------");
					System.out.print("Method.Name\n"+((MethodDeclaration)child2).getNameExpr()+"\n");
				
			}
		}
			
		private void ClassName(Node child2){ 
			if(child2.getClass().equals(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration.class)) {
					System.out.println("------------------------------------------------------------");
					System.out.print("Class.Name\n"+((ClassOrInterfaceDeclaration)child2).getNameExpr()+"\n");
				
			}
		}
		
		private void ClassExtension(Node child2){ 
			if(child2.getClass().equals(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration.class)) {
					System.out.println("------------------------------------------------------------");
					System.out.print("Class.ExtensionOf\n"+((ClassOrInterfaceDeclaration)child2).getExtends().toString()+"\n");
				
			}
		}
			
		private void printMethodModifiers(Node child2) { 
		
				if(ModifierSet.isPrivate(((MethodDeclaration) child2).getModifiers())){
					System.out.println("------------------------------------------------------------");
					System.out.print("Method.Modifier\nprivate\n");
				}
				if(ModifierSet.isPublic(((MethodDeclaration) child2).getModifiers())){
					System.out.println("------------------------------------------------------------");
					System.out.print("Method.Modifier\npublic\n");
				}
				if(ModifierSet.isStatic(((MethodDeclaration) child2).getModifiers())){
					System.out.println("------------------------------------------------------------");
					System.out.print("Method.Modifier\nstatic\n");    			
				}
    			if(ModifierSet.isStrictfp(((MethodDeclaration) child2).getModifiers())){
    				System.out.println("------------------------------------------------------------");
    				System.out.print("Method.Modifier\nstrictfp\n");
				}
    			if(ModifierSet.isSynchronized(((MethodDeclaration) child2).getModifiers())){
    				System.out.println("------------------------------------------------------------");
    				System.out.print("Method.Modifier\nsyncronized\n");
				}
    			if(ModifierSet.isTransient(((MethodDeclaration) child2).getModifiers())){
    				System.out.println("------------------------------------------------------------");
    				System.out.print("Method.Modifier\ntransient\n");
    			}
    			if(ModifierSet.isVolatile(((MethodDeclaration) child2).getModifiers())){
    				System.out.println("------------------------------------------------------------");
    				System.out.print("Method.Modifier\nvolatile\n");
    			}
    	}	
		
		private void printClassIntModifiers(Node child2) {
			
			if(ModifierSet.isPrivate(((ClassOrInterfaceDeclaration) child2).getModifiers())){
				System.out.println("------------------------------------------------------------");
				System.out.print("ClassOrInterface.Modifier\nprivate\n");
			}
			if(ModifierSet.isPublic(((ClassOrInterfaceDeclaration) child2).getModifiers())){
				System.out.println("------------------------------------------------------------");
				System.out.print("ClassOrInterface.Modifier\npublic\n");
			}
			if(ModifierSet.isStatic(((ClassOrInterfaceDeclaration) child2).getModifiers())){
				System.out.println("------------------------------------------------------------");
				System.out.print("ClassOrInterface.Modifier\nstatic\n");    			
			}
			if(ModifierSet.isStrictfp(((ClassOrInterfaceDeclaration) child2).getModifiers())){
				System.out.println("------------------------------------------------------------");
				System.out.print("ClassOrInterface.Modifier\nstrictfp\n");
			}
			if(ModifierSet.isSynchronized(((ClassOrInterfaceDeclaration) child2).getModifiers())){
				System.out.println("------------------------------------------------------------");
				System.out.print("ClassOrInterface.Modifier\nsyncronized\n");
			}
			if(ModifierSet.isTransient(((ClassOrInterfaceDeclaration) child2).getModifiers())){
				System.out.println("------------------------------------------------------------");
				System.out.print("ClassOrInterface.Modifier\ntransient\n");
			}
			if(ModifierSet.isVolatile(((ClassOrInterfaceDeclaration) child2).getModifiers())){
				System.out.println("------------------------------------------------------------");
				System.out.print("ClassOrInterface.Modifier\nvolatile\n");
			}
	}
    	}
    
