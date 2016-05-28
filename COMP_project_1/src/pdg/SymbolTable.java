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

	
	public void printSymbolTable(){
		for(int i = 0; i < scopes.size(); i++){
			System.out.println("SCOPE "+ scopes.get(i)+"\n");
			if(scopes.get(i).getClass()==ClassScope.class){
				System.out.println(((ClassScope)scopes.get(i)).fieldTable.toString());	
				System.out.println(((ClassScope)scopes.get(i)).funcTable.toString());
				
			}
			if(scopes.get(i).getClass()==MethodScope.class){
				System.out.println(((MethodScope)scopes.get(i)).paramTable.toString());	
				System.out.println(((MethodScope)scopes.get(i)).localVarTable.toString());
				
			}
			if(scopes.get(i).getClass()==LoopScope.class){
				((LoopScope)scopes.get(i)).localVarTable.toString();
			}
		}
	}
	
	public boolean addNode(Node node) {
		if(node.getClass().equals(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration.class)){
			ClassScope classScp = new ClassScope();
			classScp.Name = ((ClassOrInterfaceDeclaration)node).getNameExpr().toString();
			/*
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
			}*/
			classScp.Type=((ClassOrInterfaceDeclaration)node).getExtends().toString();
			if(!scopes.contains(classScp)){
			scopes.add(classScp);
			lastClass = classScp;
			lastScope = classScp;}
			else{
				System.out.println("error:repeated class/interface declaration");
				return false;
			}
				
	
		}    		
		else if(node.getClass().equals(com.github.javaparser.ast.body.MethodDeclaration.class)){
			MethodScope methodScp = new MethodScope();
			methodScp.Type = ((MethodDeclaration)node).getType().toString();
			methodScp.Name = ((MethodDeclaration)node).getNameExpr().toString();
			methodScp.className = lastClass.Name;
			
			lastClass.funcTable.put(methodScp.Name, methodScp.Type);
			
			if(!scopes.contains(methodScp)){
			scopes.add(methodScp);
			lastMethod = methodScp;
			lastScope = methodScp;}
			else{
				System.out.println("error:repeated method declaration");
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
				System.out.println("error: duplicated param identifier  : "+paramName+" in Method:"+lastMethod.Name+"");
				return false;
			}
			
		}
		else if (node.getClass().equals(com.github.javaparser.ast.expr.VariableDeclarationExpr.class)) {
			int c=0,i = 0;
			String varName = null;
			String varType = null;
		
			
			for(Node child: node.getChildrenNodes()){			
				if(i == 0){
					varType = child.toString();
					i++;
				}
				if(i==1){
					if(child.getClass().equals(com.github.javaparser.ast.body.VariableDeclarator.class))
					for(Node child2: child.getChildrenNodes()){
						if(c == 0){
							varName = child2.toString();
							c++;
						}
					}
					else varName = child.toString();
				}
			
			}
			
		
			if(lastScope.equals(lastMethod)){
				if(!lastMethod.localVarTable.containsKey(varName))
					lastMethod.localVarTable.put(varName, varType);
				else{
					System.out.println("error: duplicated variable declaration: "+varName+"");
					return false;
				}
			}
			else if(lastScope.equals(lastLoop)){
				if(!lastLoop.localVarTable.containsKey(varName))
					lastLoop.localVarTable.put(varName, varType);
				//variables can be redeclared inside loop scopes in java it seems http://stackoverflow.com/questions/30857753/re-declaring-variables-inside-loops-in-java
				/*else{
					System.out.println("error: duplicated variable declaration: "+varName+"");
					return false;
				}*/
			}
		}
		else if (node.getClass().equals(com.github.javaparser.ast.body.FieldDeclaration.class)) {
			int i=0,c = 0;
			String fieldName = null;
			String fieldType = null;
		
			
			for(Node child: node.getChildrenNodes()){			
				if(i == 0){
					fieldType = child.toString();
					i++;
				}
				if(i==1){
					for(Node child2: child.getChildrenNodes()){
						if(c == 0){
							fieldName = child2.toString();
							c++;
						}
					}
				}
				
			
			}
				if(!lastClass.fieldTable.containsKey(fieldName))
					lastClass.fieldTable.put(fieldName, fieldType);
				else{
					System.out.println("error: duplicated fields");
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
		
			
		}
		else if (node.getClass().equals(com.github.javaparser.ast.stmt.DoStmt.class)) {
			LoopScope loopScp = new LoopScope();
			
			loopScp.ClassName = lastClass.Name;
			loopScp.MethodName = lastMethod.Name;
			
			if(!scopes.contains(loopScp)){
				scopes.add(loopScp);
				lastLoop = loopScp;
				lastScope = loopScp;}
		
		}
		else if (node.getClass().equals(com.github.javaparser.ast.stmt.WhileStmt.class)) {
			LoopScope loopScp = new LoopScope();
			
			loopScp.ClassName = lastClass.Name;
			loopScp.MethodName = lastMethod.Name;
			
			if(!scopes.contains(loopScp)){
				scopes.add(loopScp);
				lastLoop = loopScp;
				lastScope = loopScp;}

			
		}
		else if(node.getClass().equals(com.github.javaparser.ast.expr.AssignExpr.class)){
			boolean varfound=false;
			int i = 0;
			for(Node child: node.getChildrenNodes()){			
				if(i==0){
					if(child.getClass().equals(com.github.javaparser.ast.expr.NameExpr.class)){
						if(lastScope.getClass()==MethodScope.class){
							if(lastMethod.paramTable.containsKey(child.toString()))
								varfound=true;
							if(lastMethod.localVarTable.containsKey(child.toString()))
								varfound=true;
						}
						if(lastScope.getClass()==LoopScope.class){
							if(lastMethod.paramTable.containsKey(child.toString()))
								varfound=true;
							if(lastMethod.localVarTable.containsKey(child.toString()))
								varfound=true;
							if(lastLoop.localVarTable.containsKey(child.toString()))
								varfound=true;
						}
						if(!varfound){
							System.out.println("error:Variable with identifier "+child.toString()+" is undefined");
							return false;
						}
					}
				}
				i++;
			}
		}
		else if(node.getClass().equals(com.github.javaparser.ast.stmt.ReturnStmt.class)){
			int i=0;
			boolean varfound = false;
			for(Node child: node.getChildrenNodes()){			
				if(child.getClass().equals(com.github.javaparser.ast.expr.NameExpr.class)){
					if(lastMethod.paramTable.containsKey(child.toString())){
						if(!lastMethod.paramTable.get(child.toString()).equals(lastMethod.Type)){
							System.out.println("error:Type of return value doesnt match method: "+lastMethod.Name+" type "+lastMethod.Type+"");
							return false;
						}
						varfound=true;
					}
					if(lastMethod.localVarTable.containsKey(child.toString())){
						if(!lastMethod.localVarTable.get(child.toString()).equals(lastMethod.Type)){
							System.out.println("error:Type of return value doesnt match method: "+lastMethod.Name+" type "+lastMethod.Type+"");
							return false;
						}
						varfound=true;
					}
					if(!varfound){
						System.out.println("error:Variable with identifier "+child.toString()+" is undefined");
						return false;
					}
				}
				else if(child.getClass().equals(com.github.javaparser.ast.expr.AssignExpr.class)){
					for(Node child2: child.getChildrenNodes()){			
						if(i==0){
							if(child2.getClass().equals(com.github.javaparser.ast.expr.NameExpr.class)){
								if(lastMethod.paramTable.containsKey(child2.toString())){
									if(!lastMethod.paramTable.get(child2.toString()).equals(lastMethod.Type)){
										System.out.println(""+lastMethod.paramTable.get(child2.toString())+"");	
										System.out.println(""+lastMethod.Type+"");	
										System.out.println("error:Type of return value doesnt match method: "+lastMethod.Name+" type "+lastMethod.Type+"");
										return false;
									}
									varfound=true;
								}
								if(lastMethod.localVarTable.containsKey(child2.toString())){
									if(!lastMethod.localVarTable.get(child2.toString()).equals(lastMethod.Type)){	
										System.out.println(""+lastMethod.localVarTable.get(child2.toString())+"");
										System.out.println("error:Type of return value doesnt match method: "+lastMethod.Name+" type "+lastMethod.Type+"");
										return false;
									}
									varfound=true;
								}
								if(!varfound){
									System.out.println("error:Variable with identifier "+child2.toString()+" is undefined");
									return false;
								}
							}
						}
						i++;
					}
				}
			}
		}
		return true;
	}

}
