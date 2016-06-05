package pdg;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import graphStructures.GraphNode;
import graphStructures.RelationshipEdge;
import graphStructures.ReturnObject;
import org.jgrapht.DirectedGraph;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class PDGCore {

	public PDGCore() {	}
	
	public boolean addFile(FileInputStream inArg, @SuppressWarnings("rawtypes") DirectedGraph<GraphNode, RelationshipEdge> hrefGraph, GraphNode previousNode, JTextArea consoleText) throws ParseException, IOException {
		CompilationUnit cu;
		try {
		// parse the file
			cu = JavaParser.parse(inArg);
			inArg.close();
		} catch(Exception e) {
			consoleText.setText(consoleText.getText() + "Syntatic Error - " + e.getMessage() + "\n");
			return false;
		}
		
		CodeVisitor cv = new CodeVisitor();

		cv.astPrint(cu);
		cv.semanticAnalysis(cu, hrefGraph, previousNode, new ArrayList<>());
		SymbolTable st = cv.st;
		
		st.addDependencies(hrefGraph);

		st.printSymbolTable();
		//check for errors
		if(cv.errorList.size()!=0){
			cv.printSemanticErrors(consoleText);
		} else {
			consoleText.setText(consoleText.getText() + "No semantic errors were found\n");
		}
		return true;
	}
	
}

    class CodeVisitor extends VoidVisitorAdapter<Object> {
    	
    	ArrayList<String> errorList = new ArrayList<>();
    	SymbolTable st = new SymbolTable();
    	
    	CodeVisitor(){}
    	
    	//SEMANTIC ANALYSIS
    	ArrayList<String> semanticAnalysis(Node node, @SuppressWarnings("rawtypes") DirectedGraph<GraphNode, RelationshipEdge> hrefGraph, GraphNode previousNode, ArrayList<Scope> ls){  		
    		ReturnObject ret;
    		GraphNode nextNode = previousNode;
    		ArrayList<Scope> lastScopes = new ArrayList<>(ls);
    			
    		if(relevant(node)) {
    			ret = st.SemanticNodeCheck(node, hrefGraph, previousNode, lastScopes);
        		if(ret.hasError()) {	
        			errorList.add(ret.getError());
        		}
        		else {
        			nextNode = ret.getGraphNode();
        		}
    		}
    		
    		for(Node child: node.getChildrenNodes()){
    			semanticAnalysis(child, hrefGraph, nextNode, lastScopes);
    		}
    		
    			
    		return errorList;
    	}

    	ArrayList<String> printSemanticErrors(JTextArea textArea) {
    		
    		ReturnObject ret;
    		//add undefined methods error
    		if(st.pendingMethodDeclarations.size()>0)
				errorList.addAll(st.pendingMethodDeclarations.stream().map(undeclaredMethod -> "error:Undeclared Method " + undeclaredMethod.methodName + " in class " + undeclaredMethod.methodScope + "").collect(Collectors.toList()));
    		//check method calls that were pending
    		for(int i=0;i<st.pendingMethodNodes.size();i++){
    			ret=st.postProcessMethodCallNode(st.pendingMethodNodes.get(i).method,st.pendingMethodNodes.get(i).classScope,st.pendingMethodNodes.get(i).methodName,st.pendingMethodNodes.get(i).callerMethod);
    			if(ret.hasError()) {	
        			errorList.add(ret.getError());
        		}
    		}
	    	textArea.setText(textArea.getText() + "Semantic errors:\n");

    		for(String error: errorList){
    	    	textArea.setText(textArea.getText() + error + "\n");
			}	    		
	    	textArea.setText(textArea.getText() + "Ended semantic errors\n");
    		return errorList;
    	}

		//AST PRINTING
		void astPrint(Node child2){
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
    			
    			else{
    				System.out.println("------------------------------------------------------------");
    				System.out.println(child2.getClass());
    				System.out.println(child2.toString());
    			}
    		}
			child2.getChildrenNodes().forEach(this::astPrint);
    	}

		private boolean relevant(Node child2) {
			return !child2.getClass().equals(com.github.javaparser.ast.body.VariableDeclarator.class) &&
					!child2.getClass().equals(CompilationUnit.class)
                    && !child2.getClass().equals(com.github.javaparser.ast.stmt.ExpressionStmt.class)
                    && !child2.getClass().equals(com.github.javaparser.ast.stmt.BlockStmt.class)
                    && !child2.getClass().equals(com.github.javaparser.ast.type.VoidType.class)
                    && !child2.getClass().equals(com.github.javaparser.ast.type.ClassOrInterfaceType.class);
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
    
