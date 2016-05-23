package pdg;

import java.util.ArrayList;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;

public class SymbolTable {
	//this array can accept any type of object, will contain classScope,GlobalScope,MethodScope and LoopScope var types
	//overall symbol tables can be accessed through here
	ArrayList<Object> scopes;
	public SymbolTable(){
		scopes = new ArrayList<Object>();
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
	
	ClassScope lastClass = null;
	MethodScope lastMethod = null;
	LoopScope lastLoop = null;
	Object lastScope = null;
	
	public boolean addNode(Node node) {
		if(node.getClass().equals(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration.class)){
			ClassScope classScp = new ClassScope();
			classScp.Name = ((ClassOrInterfaceDeclaration)node).getNameExpr().toString();
			
			if(ModifierSet.isPrivate(((ClassOrInterfaceDeclaration) node).getModifiers())){
				classScp.Type = "private";
			}
			if(ModifierSet.isPublic(((ClassOrInterfaceDeclaration) node).getModifiers())){
				classScp.Type = "public";
			}
			if(ModifierSet.isStatic(((ClassOrInterfaceDeclaration) node).getModifiers())){
				classScp.Type = "static";	
			}
			if(ModifierSet.isStrictfp(((ClassOrInterfaceDeclaration) node).getModifiers())){
				classScp.Type = "strictfp";
			}
			if(ModifierSet.isSynchronized(((ClassOrInterfaceDeclaration) node).getModifiers())){
				classScp.Type = "synchronized";
			}
			if(ModifierSet.isTransient(((ClassOrInterfaceDeclaration) node).getModifiers())){
				classScp.Type = "transient";
			}
			if(ModifierSet.isVolatile(((ClassOrInterfaceDeclaration) node).getModifiers())){
				classScp.Type = "volatile";
			}
			if(!scopes.contains(classScp)){
			scopes.add(classScp);
			lastClass = classScp;
			lastScope = classScp;}
			else{
				System.out.println("erro semantico");
				return false;
			}
				
	
		}    		
		else if(node.getClass().equals(com.github.javaparser.ast.body.MethodDeclaration.class)){
			MethodScope methodScp = new MethodScope();
			methodScp.Type = ((MethodDeclaration)node).getType().toString();
			methodScp.Name = ((MethodDeclaration)node).getNameExpr().toString();
			methodScp.className = lastClass.Name;
			
			//tem q aceitar mesmo nome se tiver tipo diferente
			lastClass.funcTable.put(methodScp.Name, methodScp.Type);
			
			if(!scopes.contains(methodScp)){
			scopes.add(methodScp);
			lastMethod = methodScp;
			lastScope = methodScp;}
			else{
				System.out.println("erro semantico");
				return false;
			}
		
		}
		else if(node.getClass().equals(com.github.javaparser.ast.body.Parameter.class)){
			int i = 0;
			String paramName = null;
			String paramType = null;
			
			for(Node child: node.getChildrenNodes()){
			if(relevant(child)){
				if(i == 0){
					paramName = child.toString();
					i++;
				}
				else
					paramType = child.toString();
			}
			}			
			
			if(!lastMethod.paramTable.containsKey(paramName))
				lastMethod.paramTable.put(paramName, paramType);
			else{
				System.out.println("erro semantico");
				return false;
			}
			
		}
		else if (node.getClass().equals(com.github.javaparser.ast.expr.VariableDeclarationExpr.class)) {
			int i = 0;
			String varName = null;
			String varType = null;
		
			
			for(Node child: node.getChildrenNodes()){			
				if(i == 0){
					varType = child.toString();
					i++;
				}
				else
					varName = child.toString();
			
			}
			
		
			if(lastScope.equals(lastMethod)){
				if(!lastMethod.localVarTable.containsKey(varName))
					lastMethod.localVarTable.put(varName, varType);
				else{
					System.out.println("erro semantico");
					return false;
				}
			}
			else if(lastScope.equals(lastLoop)){
				if(!lastLoop.localVarTable.containsKey(varName))
					lastLoop.localVarTable.put(varName, varType);
				else{
					System.out.println("erro semantico");
					return false;
				}
			}
		}
		else if (node.getClass().equals(com.github.javaparser.ast.body.FieldDeclaration.class)) {
			int i = 0;
			String fieldName = null;
			String fieldType = null;
		
			
			for(Node child: node.getChildrenNodes()){			
				if(i == 0){
					fieldType = child.toString();
					i++;
				}
				else
					fieldName = child.toString();
			
			}
				if(!lastClass.fieldTable.containsKey(fieldName))
					lastClass.fieldTable.put(fieldName, fieldType);
				else{
					System.out.println("erro semantico");
					return false;
				}
		}
		else if (node.getClass().equals(com.github.javaparser.ast.stmt.ForStmt.class)) {
			LoopScope loopScp = new LoopScope();
			
			loopScp.ClassName = lastClass.Name;
			loopScp.MethodName = lastMethod.Name;
			
			if(!scopes.contains(loopScp)){
				scopes.add(loopScp);
				lastLoop = loopScp;
				lastScope = loopScp;}
				else{
					System.out.println("erro semantico");
					return false;
				}
			
			
		}
		else if (node.getClass().equals(com.github.javaparser.ast.stmt.DoStmt.class)) {
			LoopScope loopScp = new LoopScope();
			
			loopScp.ClassName = lastClass.Name;
			loopScp.MethodName = lastMethod.Name;
			
			if(!scopes.contains(loopScp)){
				scopes.add(loopScp);
				lastLoop = loopScp;
				lastScope = loopScp;}
				else{
					System.out.println("erro semantico");
					return false;
				}
		}
		else if (node.getClass().equals(com.github.javaparser.ast.stmt.WhileStmt.class)) {
			LoopScope loopScp = new LoopScope();
			
			loopScp.ClassName = lastClass.Name;
			loopScp.MethodName = lastMethod.Name;
			
			if(!scopes.contains(loopScp)){
				scopes.add(loopScp);
				lastLoop = loopScp;
				lastScope = loopScp;}
				else{
					System.out.println("erro semantico");
					return false;
				}
			
		}
		
		return true;
	}

}
