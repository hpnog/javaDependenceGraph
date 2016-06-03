package pdg;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.jgrapht.DirectedGraph;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;

import pdg_gui.RelationshipEdge;

public class SymbolTable {
	//this array can accept any type of object, will contain classScope,GlobalScope,MethodScope and LoopScope var types
	//overall symbol tables can be accessed through here
	ArrayList<Object> scopes;
	private ClassScope lastClass = null;
	private MethodScope lastMethod = null;
	private LoopScope lastLoop = null;
	private Object lastScope = null;
	
	class Parameter {
		String paramName;
		String paramType;
		Parameter(){
			paramName = null;
			paramType = null;
		}
	}
	
	class Variable {
		String varName;
		String varType;
		Variable(){
			varName = null;
			varType = null;
		}
	}
	
	class Field {
		String fieldName;
		String fieldType;
		Field(){
			fieldName=null;
			fieldType=null;
		}
	}
	
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
				System.out.println(((LoopScope)scopes.get(i)).localVarTable.toString());
			}
		}
	}
	
	private void fillClassScope(Node node,ClassScope classScp){ 
		
		classScp.Name = ((ClassOrInterfaceDeclaration)node).getNameExpr().toString();
		classScp.Type=((ClassOrInterfaceDeclaration)node).getExtends().toString();
	}
	
	private boolean addClassScope(ClassScope cs){ 
		if(!scopes.contains(cs)){
			scopes.add(cs);
			lastClass = cs;
			lastScope = cs;
			return true;
		}
		else return false;
	}
	
	private void fillLoopScope(Node node,LoopScope loopScp){
		loopScp.ClassName = lastClass.Name;
		loopScp.MethodName = lastMethod.Name;
		
	}

	private void addLoopScope(LoopScope ls){
			scopes.add(ls);
			lastLoop = ls;
			lastScope = ls;
	}
	
	private void fillMethodScope(Node node,MethodScope methodScp){
		methodScp.Type = ((MethodDeclaration)node).getType().toString();
		methodScp.Name = ((MethodDeclaration)node).getNameExpr().toString();
		methodScp.className = lastClass.Name;
	}
	
	private boolean addMethodScope(MethodScope methodScp){
		if(!lastClass.funcTable.contains(methodScp.Name)){
			lastClass.funcTable.put(methodScp.Name, methodScp.Type);
			scopes.add(methodScp);
			lastMethod = methodScp;
			lastScope = methodScp;
			return true;
		}
		return false;
	}
	
	private boolean addParameter(Node node,Parameter param){
		int i = 0;
		
		
		for(Node child: node.getChildrenNodes()){
			if(relevant(child)){
				if(i == 0){
					param.paramName = child.toString();
					i++;
				}
				else
					param.paramType = child.toString();
			}
		}	
		if(!lastMethod.paramTable.containsKey(param.paramName)){
			lastMethod.paramTable.put(param.paramName, param.paramType);
			return true;
		}
		return false;
	}
	
	private boolean addVariable(Node node,Variable var){
		int c=0,i = 0;
		
		
		for(Node child: node.getChildrenNodes()){			
			if(i == 0){
				var.varType = child.toString();
				i++;
			}
			if(i==1){
				if(child.getClass().equals(com.github.javaparser.ast.body.VariableDeclarator.class))
				for(Node child2: child.getChildrenNodes()){
					if(c == 0){
						var.varName = child2.toString();
						c++;
					}
				}
				else var.varName = child.toString();
			}
		
		}
	
		if(lastScope.equals(lastMethod)){
			if(!lastMethod.localVarTable.containsKey(var.varName)){
				lastMethod.localVarTable.put(var.varName, var.varType);
				return true;
			}
			
		}
		
		else if(lastScope.equals(lastLoop)){
			if(!lastLoop.localVarTable.containsKey(var.varName)){
				lastLoop.localVarTable.put(var.varName, var.varType);
				return true;
			}
		}
		
		return false;
	}
	
	private boolean addField(Node node, Field fld){
		int i=0,c = 0;
		for(Node child: node.getChildrenNodes()){			
			
			if(i == 0){
				fld.fieldType = child.toString();
				i++;
			}
			
			if(i==1){
				for(Node child2: child.getChildrenNodes()){
					if(c == 0){
						fld.fieldName = child2.toString();
						c++;
					}
				}
			}
			
		}
		
		if(!lastClass.fieldTable.containsKey(fld.fieldName)){
			lastClass.fieldTable.put(fld.fieldName, fld.fieldType);
			return true;
		}
		
		return false;
	}
	
	public String SemanticNodeCheck(Node node, DirectedGraph<String, RelationshipEdge> hrefGraph, String previousNode) {
		String nodeToSend = null;
		
		if(node.getClass().equals(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration.class)){
			ClassScope classScp = new ClassScope();
			fillClassScope(node,classScp);
			if(!addClassScope(classScp))
				return "error:repeated class/interface declaration of "+classScp.Name+" ";
			
			hrefGraph.addVertex(node.toString());
			nodeToSend = node.toString();
			hrefGraph.addEdge(previousNode, node.toString());
		}    		
		
		else if(node.getClass().equals(com.github.javaparser.ast.body.MethodDeclaration.class)){
			MethodScope methodScp = new MethodScope();
			fillMethodScope(node,methodScp);
			if(!addMethodScope(methodScp))
				return "error:repeated method declaration of "+methodScp.Name+", please use different identifiers for methods in same class";
			hrefGraph.addVertex(node.toString());
			nodeToSend = node.toString();
			hrefGraph.addEdge(previousNode, node.toString());
		}
		
		else if(node.getClass().equals(com.github.javaparser.ast.body.Parameter.class)){
			Parameter param = new Parameter();
			if(!addParameter(node,param))
				return "error: duplicated param identifier  : "+param.paramName+" in Method:"+lastMethod.Name+"";
			hrefGraph.addVertex(node.toString());
			nodeToSend = node.toString();
			hrefGraph.addEdge(previousNode, node.toString());	
		}
		
		else if (node.getClass().equals(com.github.javaparser.ast.expr.VariableDeclarationExpr.class)) {
			Variable var = new Variable();
			if(!addVariable(node,var))
				return "error: duplicated variable declaration: "+var.varName+" in Method:"+lastMethod.Name+"";
			hrefGraph.addVertex(node.toString());
			nodeToSend = node.toString();
			hrefGraph.addEdge(previousNode, node.toString());	
		}
		
		else if (node.getClass().equals(com.github.javaparser.ast.body.FieldDeclaration.class)) {
			Field fld= new Field();
			if(!addField(node,fld))
				return "error: duplicated fields: "+fld.fieldName+" in class : "+lastClass.Name+"";
			hrefGraph.addVertex(node.toString());
			nodeToSend = node.toString();
			hrefGraph.addEdge(previousNode, node.toString());	
		}
		
		else if (node.getClass().equals(com.github.javaparser.ast.stmt.ForStmt.class)) {
			LoopScope loopScp = new LoopScope();
			fillLoopScope(node,loopScp);
			addLoopScope(loopScp);
			
			hrefGraph.addVertex(node.toString());
			nodeToSend = node.toString();
			hrefGraph.addEdge(previousNode, node.toString());
		}
		
		else if (node.getClass().equals(com.github.javaparser.ast.stmt.DoStmt.class)) {
			LoopScope loopScp = new LoopScope();
			fillLoopScope(node,loopScp);
			addLoopScope(loopScp);
			
			hrefGraph.addVertex(node.toString());
			nodeToSend = node.toString();
			hrefGraph.addEdge(previousNode, node.toString());
		}
		
		else if (node.getClass().equals(com.github.javaparser.ast.stmt.WhileStmt.class)) {
			LoopScope loopScp = new LoopScope();
			fillLoopScope(node,loopScp);
			addLoopScope(loopScp);
			
			hrefGraph.addVertex(node.toString());
			nodeToSend = node.toString();
			hrefGraph.addEdge(previousNode, node.toString());
		}
		
		else if(node.getClass().equals(com.github.javaparser.ast.expr.MethodCallExpr.class)){
			hrefGraph.addVertex(node.toString());
			nodeToSend = node.toString();
			hrefGraph.addEdge(previousNode, node.toString());
			
			//if not null, it's not an user defined method
			if(((MethodCallExpr)node).getScope()==null){
				boolean varfound=false;
				int nargs= 0;
			    StringTokenizer stok = new StringTokenizer(node.toString(),"(");
			    String methodName=stok.nextToken();
			    System.out.println(methodName);
			    //check if this method is defined and determine nr of args
				//verify getArgs.size = method.ParamTable.size
			   
				System.out.println("ARGS OF METHODCALL"+(((MethodCallExpr)node).getArgs().toString()));
				//TYPE CHECK EACH ARGUMENT 
				//CHECK IF ARGUMENT IS DEFINED(COULD BE FUNC PARAM,METHOD FROM CURRENTCLASS AND LOCALVAR)
				for(Node child: node.getChildrenNodes()){
					
					System.out.println("CHILD OF METHODCALL"+child.toString());
					
					//CHECK IF THE NAMEEXPRESSION IS IN THE LIST OF ARGUMENTS ,IF NOT IT IS THE SCOPE(class) OF THE FUNCTION 
					if(child.getClass().equals(com.github.javaparser.ast.expr.NameExpr.class)){
							/*if(lastScope.getClass()==MethodScope.class){
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
								return "error:Variable with identifier "+child.toString()+" is undefined";
							}
						}*/
					}
					//TYPE CHECK A FIELD PASSED AS ARGUMENT, NO NEED TO SEE IF FIELD IS DEFINED
					if(child.getClass().equals(com.github.javaparser.ast.expr.FieldAccessExpr.class)){
						
					}
	
				}
		
			}
		}
		else if(node.getClass().equals(com.github.javaparser.ast.expr.FieldAccessExpr.class)){
			//SCOPE will get me the class Scope of the fieldAccessExpr, check scopes != System 
			//if SCOPE is this, get current class
			System.out.println("SCOPE:"+((FieldAccessExpr) node).getScope().toString());
			//FIELD will get me the actual field
			System.out.println("FIELD:"+((FieldAccessExpr) node).getField().toString());
		}
		else if(node.getClass().equals(com.github.javaparser.ast.expr.AssignExpr.class)){			
			boolean varfound=false;			
			for(Node child: node.getChildrenNodes()){			
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
						return "error:Variable with identifier "+child.toString()+" is undefined";
					} else {
						hrefGraph.addVertex(node.toString());
						nodeToSend = node.toString();
						hrefGraph.addEdge(previousNode, node.toString());
					}
				
				}
			}
		}
		
		else if(node.getClass().equals(com.github.javaparser.ast.stmt.ReturnStmt.class)){
			hrefGraph.addVertex(node.toString());
			nodeToSend = node.toString();
			hrefGraph.addEdge(previousNode, node.toString());
			
			int i=0;
			boolean varfound = false;
			for(Node child: node.getChildrenNodes()){
				
				if(child.getClass().equals(com.github.javaparser.ast.expr.NameExpr.class)){
					if(lastMethod.paramTable.containsKey(child.toString())){
						if(!lastMethod.paramTable.get(child.toString()).equals(lastMethod.Type)){

							return "error:Type of return value -"+lastMethod.paramTable.get(child.toString())+"- doesnt match method's: "+lastMethod.Name+" should return: "+lastMethod.Type+"";
						}
						varfound=true;
					}
					if(lastMethod.localVarTable.containsKey(child.toString())){
						if(!lastMethod.localVarTable.get(child.toString()).equals(lastMethod.Type)){
							return "error:Type of return value -"+lastMethod.localVarTable.get(child.toString())+"- doesnt match method's: "+lastMethod.Name+" should return: "+lastMethod.Type+"";
						}
						varfound=true;
					}
					if(!varfound){
						return "error:Variable with identifier "+child.toString()+" is undefined";
					}
				}
				else if(child.getClass().equals(com.github.javaparser.ast.expr.AssignExpr.class)){
					for(Node child2: child.getChildrenNodes()){			
						if(i==0){
							if(child2.getClass().equals(com.github.javaparser.ast.expr.NameExpr.class)){
								if(lastMethod.paramTable.containsKey(child2.toString())){
									if(!lastMethod.paramTable.get(child2.toString()).equals(lastMethod.Type)){
										return "error:Type of return value -"+lastMethod.paramTable.get(child2.toString())+"- doesnt match method's: "+lastMethod.Name+" should return: "+lastMethod.Type+"";
									}
								}
								if(lastMethod.localVarTable.containsKey(child2.toString())){
									if(!lastMethod.localVarTable.get(child2.toString()).equals(lastMethod.Type)){	
										return "error:Type of return value -"+lastMethod.localVarTable.get(child2.toString())+"- doesnt match method's: "+lastMethod.Name+" should return: "+lastMethod.Type+"";
									}
								}
							}
						}
						i++;
					}
				}
			}
		}
		return "clear" + nodeToSend;
	}

}
