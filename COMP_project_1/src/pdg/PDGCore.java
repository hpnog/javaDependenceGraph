package pdg;
import java.io.FileInputStream; 
import java.io.IOException;
import java.util.ArrayList;

import org.jgrapht.DirectedGraph;

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

public class PDGCore {
	private static FileInputStream in;
	private SymbolTable st;
	
	
	public PDGCore() {	}
	
	public void addFile(FileInputStream inArg, DirectedGraph<GraphNode, RelationshipEdge> hrefGraph, GraphNode previousNode) throws ParseException, IOException {
		in = inArg;
		CompilationUnit cu;
		try {
		// parse the file
			cu = JavaParser.parse(in);
		} finally {
		    in.close();
		}
		
		//new CodeVisitor().buildGraph(cu, hrefGraph, previousNode, st);
		CodeVisitor cv = new CodeVisitor();

		cv.astPrint(cu);
		cv.semanticAnalysis(cu, hrefGraph, previousNode, new ArrayList<Scope>());
		
		//cv.buildGraph(cu,hrefGraph,previousNode,st);
		st = cv.st;
		st.addDependencies(hrefGraph);

		st.printSymbolTable();
		//check for errors
		if(cv.errorlist.size()!=0){
			cv.printSemanticErrors();
		}
	}
	
}

    /**
     * Simple visitor implementation for visiting nodes. 
     */
    class CodeVisitor extends VoidVisitorAdapter<Object> {
    	
    	ArrayList<String> errorlist;
    	SymbolTable st;
    	
    	CodeVisitor(){
    		errorlist= new ArrayList<String>();

    		st= new SymbolTable();
    	}
    	
    	//SEMANTIC ANALYSIS
    	ArrayList<String> semanticAnalysis(Node node, DirectedGraph<GraphNode, RelationshipEdge> hrefGraph, GraphNode previousNode, ArrayList<Scope> ls){  		
    		ReturnObject ret = null;
    		GraphNode nextNode = previousNode;
    		ArrayList<Scope> lastScopes = new ArrayList<Scope>(ls);
    		
    		if(ls.size() != 0)
    			st.lastScope = lastScopes.get(lastScopes.size() - 1);

    		
    		if(relevant(node)) {
    			ret = st.SemanticNodeCheck(node, hrefGraph, previousNode, lastScopes);
        		if(ret.hasError()) {	
        			errorlist.add(ret.getError());
        		}
        		else {
        			nextNode = ret.getGraphNode();
        		}
    		}
    		
    		for(Node child: node.getChildrenNodes()){
    			semanticAnalysis(child, hrefGraph, nextNode, lastScopes);
    		}
    		
    			
    		return errorlist;
    	}
    	
    	void printSemanticErrors() {
    		ReturnObject ret = null;
    		//add undefined methods error
    		if(st.pendingMethodDeclarations.size()>0)
    			for(SymbolTable.Method undeclaredMethod: st.pendingMethodDeclarations)
    				errorlist.add("error:Undeclared Method "+undeclaredMethod.methodName+" in class "+undeclaredMethod.methodScope+"");
    		//check method calls that were pending
    		for(int i=0;i<st.pendingMethodNodes.size();i++){
    			ret=st.postProcessMethodCallNode(st.pendingMethodNodes.get(i).method,st.pendingMethodNodes.get(i).classScope,st.pendingMethodNodes.get(i).methodName);
    			if(ret.hasError()) {	
        			errorlist.add(ret.getError());
        		}
    		}
    		for(String error: errorlist){
				System.out.println(error);
			}	
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
    		for(Node child: child2.getChildrenNodes()){
    			astPrint(child);
    		}
    	}

		private boolean relevant(Node child2) {
			if(child2.getClass().equals(com.github.javaparser.ast.body.VariableDeclarator.class))
				return false;
			if(child2.getClass().equals(com.github.javaparser.ast.CompilationUnit.class))
				return false;
			if(child2.getClass().equals(com.github.javaparser.ast.stmt.ExpressionStmt.class))
				return false;
			//if(child2.getClass().equals(com.github.javaparser.ast.expr.FieldAccessExpr.class))
			//	return false;
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
    
