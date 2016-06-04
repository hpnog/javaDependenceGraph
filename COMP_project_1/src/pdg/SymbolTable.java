package pdg;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.jgrapht.DirectedGraph;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;

import graphStructures.GraphNode;
import graphStructures.RelationshipEdge;
import graphStructures.ReturnObject;
import graphStructures.VarChanges;

public class SymbolTable {
	//this array can accept any type of object, will contain classScope,GlobalScope,MethodScope and LoopScope var types
	//overall symbol tables can be accessed through here
	ArrayList<Scope> scopes;
	ArrayList<Method> pendingMethodDeclarations;
	ArrayList<MethodNode> pendingMethodNodes;
	private ClassScope lastClass = null;
	private MethodScope lastMethod = null;
	private LoopScope lastLoop = null;
	private Scope lastScope = null;
	class MethodNode{
		Node method;
		String classScope;
		String methodName;
		MethodNode(){
			method=null;
			classScope=null;
		}
		public MethodNode(Node node, String classScope2,String name) {
			method=node;
			classScope=classScope2;
			methodName=name;
		}
	
	}
	class Method{
		String methodName;
		String methodScope;
		Method(){
			methodName=null;
			methodScope=null;
		}
		Method(String n, String s){
			methodName=n;
			methodScope=s;
		}
	}

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
		public Variable(String name, String type) {
			varName=name;
			varType=type;
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
		scopes = new ArrayList<Scope>();
		pendingMethodDeclarations = new ArrayList<Method>();
		pendingMethodNodes = new ArrayList<MethodNode>();
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
	
	private boolean addClassScope(ClassScope cs, ArrayList<Scope> ls){ 
		if(!scopes.contains(cs)){
			scopes.add(cs);
			
			ls.add(cs);
			
			lastClass = cs;
			lastScope = cs;
			
			
			return true;
		}
		else return false;
	}
	
	private void fillLoopScope(Node node,LoopScope loopScp){
		loopScp.ClassName = lastClass.Name;
		loopScp.MethodName = lastMethod.Name;
		loopScp.loopNode=node;
		
	}

	private void addLoopScope(LoopScope ls, ArrayList<Scope> ls1){
			scopes.add(ls);
			
			ls1.add(ls);
			
			lastLoop = ls;
			lastScope = ls;
	}
	
	private void fillMethodScope(Node node,MethodScope methodScp){
		methodScp.Type = ((MethodDeclaration)node).getType().toString();
		methodScp.Name = ((MethodDeclaration)node).getNameExpr().toString();
		methodScp.className = lastClass.Name;
		methodScp.methodNode = node;
	}
	
	private boolean addMethodScope(MethodScope methodScp, ArrayList<Scope> ls){
		if(!lastClass.funcTable.contains(methodScp.Name)){
			lastClass.funcTable.put(methodScp.Name, methodScp.Type);
			scopes.add(methodScp);
			
			ls.add(methodScp);
			
			lastMethod = methodScp;
			lastScope = methodScp;
			return true;
		}
		return false;
	}
	
	private void checkPendingMethods(MethodScope methodScp){ 
		for(int i=0;i<pendingMethodDeclarations.size();i++){
			if(methodScp.className.equals(pendingMethodDeclarations.get(i).methodScope))
				if(methodScp.Name.equals(pendingMethodDeclarations.get(i).methodName)){
					pendingMethodDeclarations.remove(pendingMethodDeclarations.get(i));
				}
	    }
	}
	private String verifyMethodArguments(Node node,Method method){
		System.out.println((((MethodCallExpr)node).toString()));
		MethodScope methodscp=null;
		//find MethodScope
		for(int i=0;i<scopes.size();i++){
			if(scopes.get(i).getClass().equals(MethodScope.class))
				if(((MethodScope)scopes.get(i)).className.equals(method.methodScope))
					if(((MethodScope)scopes.get(i)).Name.equals(method.methodName))
						methodscp=((MethodScope)scopes.get(i));
		}
		if(!((MethodCallExpr)node).getArgs().isEmpty())
			if(!methodscp.paramTable.isEmpty())	
				if(((MethodCallExpr)node).getArgs().size()!=methodscp.paramTable.size())
					return "error:Method call of "+methodscp.Name+" in class "+methodscp.className+" has an invalid number of arguments("+((MethodCallExpr)node).getArgs().size()+" instead of "+methodscp.paramTable.size()+")";
			
		if(((MethodCallExpr)node).getArgs().isEmpty())
			if(!methodscp.paramTable.isEmpty())
				return "error:Method call of "+methodscp.Name+" in class "+methodscp.className+" has an invalid number of arguments(0 instead of "+methodscp.paramTable.size()+")";
		
		if(methodscp.paramTable.isEmpty())
			if(!((MethodCallExpr)node).getArgs().isEmpty())
				return "error:Method call of "+methodscp.Name+" in class "+methodscp.className+" has an invalid number of arguments("+((MethodCallExpr)node).getArgs().size()+" instead of 0)";
			
		   
		System.out.println("ARGS OF METHODCALL"+(((MethodCallExpr)node).getArgs().toString()));
		//TYPE CHECK EACH ARGUMENT 
		//CHECK IF ARGUMENT IS DEFINED(COULD BE FUNC PARAM,METHOD FROM CURRENTCLASS AND LOCALVAR)
		for(Node child: node.getChildrenNodes()){
			
			System.out.println("CHILD OF METHODCALL"+child.toString());
			
			//CHECK IF THE NAMEEXPRESSION IS IN THE LIST OF ARGUMENTS ,IF NOT IT IS THE SCOPE(class) OF THE FUNCTION 
			if(child.getClass().equals(com.github.javaparser.ast.expr.NameExpr.class)){
		
			}
			//TYPE CHECK A FIELD PASSED AS ARGUMENT, NO NEED TO SEE IF FIELD IS DEFINED
			if(child.getClass().equals(com.github.javaparser.ast.expr.FieldAccessExpr.class)){
				
			}

		}
		return "clear";
	}
	public ReturnObject postProcessMethodCallNode(Node node,String scope,String methodName){
			boolean methodfound=false;
		   	Method method =new Method(methodName,scope);
		    //check if this method is defined and determine nr of args
		   	for(int i = 0; i < scopes.size(); i++){
				if(scopes.get(i).getClass()==ClassScope.class){
					if(((ClassScope)scopes.get(i)).Name.equals(scope))
						if(((ClassScope)scopes.get(i)).funcTable.containsKey(methodName))
							methodfound=true;
				}	
		    }
		   
		    if(!methodfound){
		    	return new ReturnObject("clear");
		    }
		    else{
		    	String returnval;
		    	returnval=verifyMethodArguments(node, method);
		    	if(!returnval.equals("clear"))
		    		return new ReturnObject(returnval);
		    }
		
		return new ReturnObject("clear");
	}
	
	private ReturnObject processMethodCallNode(Node node){
		//for function calls of the type object.callfunction()
		if(((MethodCallExpr)node).getScope()!=null){
			 	StringTokenizer stok = new StringTokenizer(node.toString(),".");
			 	boolean methodfound=false;
			 	String method="";
			    while(stok.hasMoreTokens()){
			    method=stok.nextToken();
			    if(!stok.hasMoreTokens())
			    break;
			    }
			    stok= new StringTokenizer(method,"(");
			    method=stok.nextToken();
			   //ignore system method Calls
			   if(!((MethodCallExpr)node).getScope().toString().startsWith("System")){
				   String classScope="";
				   if(((MethodCallExpr)node).getScope().toString().startsWith("this"))
					   classScope=lastClass.Name;
				   else classScope=((MethodCallExpr)node).getScope().toString();
				   Method methodwithscope = new Method(method,classScope);
				   //check if this method is defined and determine nr of args
				    	for(int i = 0; i < scopes.size(); i++){
						if(scopes.get(i).getClass()==ClassScope.class){
							if(((ClassScope)scopes.get(i)).Name.equals(classScope))
								if(((ClassScope)scopes.get(i)).funcTable.containsKey(method))
									methodfound=true;
						}	
				    	}
				    	if(!methodfound){
				    		pendingMethodDeclarations.add(methodwithscope);
				    		pendingMethodNodes.add(new MethodNode(node,classScope,method));
				    	}
				    	else{
				    		String returnval;
					    	returnval=verifyMethodArguments(node, methodwithscope);
					    	if(!returnval.equals("clear"))
					    		return new ReturnObject(returnval);
				    	}
			   
			   }
		}
		
		//if not null, it's not an user defined method of current Class scope
		if(((MethodCallExpr)node).getScope()==null){
			boolean varfound=false;
			boolean methodfound=false;
			int nargs= 0;
		    StringTokenizer stok = new StringTokenizer(node.toString(),"(");
		    String methodName=stok.nextToken();
		    System.out.println(methodName);
		   	Method method =new Method(methodName,lastClass.Name);
		    //check if this method is defined and determine nr of args
		    if(lastClass.funcTable.containsKey(methodName))
		    	methodfound=true;
		   
		    if(!methodfound){
		    	pendingMethodDeclarations.add(method);
		    	pendingMethodNodes.add(new MethodNode(node,lastClass.Name,methodName));
		    }
		    else{
		    	String returnval;
		    	returnval=verifyMethodArguments(node, method);
		    	if(!returnval.equals("clear"))
		    		return new ReturnObject(returnval);
		    }
		}
		return new ReturnObject("clear");
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
	
	private ArrayList<Variable> addVariable(Node node,Variable var){
		int i = 0;
		int c = 0;
		ArrayList<Variable> repeatedVars = new ArrayList<Variable>();
		
		for(Node child: node.getChildrenNodes()){			
	
			if(i == 0){
				var.varType = child.toString();
			}
			
			if(child.getClass().equals(com.github.javaparser.ast.body.VariableDeclarator.class))
				c=0;
			for(Node child2: child.getChildrenNodes()){	
				
				if(i==1){
					if(c == 0){
						var.varName = child2.toString();
						c++;
					}
				}
				if(i>1 && child2.getClass().equals(com.github.javaparser.ast.body.VariableDeclaratorId.class)){
					if(c==0){
						Variable othervar= new Variable(child2.toString(),var.varType);
						if(!putVariable(othervar))
							repeatedVars.add(othervar);
						c++;	
					}	
				}
			}
				
				i++;
			
		}

		if(!putVariable(var))
			repeatedVars.add(var);
		return repeatedVars;
	}
	
	private boolean putVariable(Variable var){
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
	
	private ArrayList<String> assignExpressionCheck(Node node){
		boolean varfound=false;
		ArrayList<String> undeclared = new ArrayList<String>();
		for(Node child: node.getChildrenNodes()){
			System.out.println("CHILD OF ASSIGN"+child.toString()+child.getClass().toString());
			if(child.getClass().equals(com.github.javaparser.ast.expr.NameExpr.class)){
				System.out.println("VARIABLE"+child.toString());
				if(lastMethod.paramTable.containsKey(child.toString()))
					varfound=true;
				else if(lastMethod.localVarTable.containsKey(child.toString()))
					varfound=true;
				else for(int i=0;i<scopes.size();i++){
					if(scopes.getClass().equals(LoopScope.class)){
						if (((LoopScope)scopes.get(i)).MethodName.equals(lastMethod.Name)){
							if (((LoopScope)scopes.get(i)).ClassName.equals(lastClass.Name)){
								if(((LoopScope)scopes.get(i)).localVarTable.containsKey(child.toString()))
									varfound=true;
							}
						}
					}
				}
				if(!varfound){
					undeclared.add(child.toString());
				}
			}
		}
		return undeclared;
	}
	
	private ReturnObject checkReturn(Node node){

		int i=0;
		boolean varfound = false;
		for(Node child: node.getChildrenNodes()){
			
			if(child.getClass().equals(com.github.javaparser.ast.expr.NameExpr.class)){
				if(lastMethod.paramTable.containsKey(child.toString())){
					if(!lastMethod.paramTable.get(child.toString()).equals(lastMethod.Type)){

						return  new ReturnObject("error:Type of return value -"+lastMethod.paramTable.get(child.toString())+"- doesnt match method's: "+lastMethod.Name+" should return: "+lastMethod.Type+"");
					}
					varfound=true;
				}
				if(lastMethod.localVarTable.containsKey(child.toString())){
					if(!lastMethod.localVarTable.get(child.toString()).equals(lastMethod.Type)){
						return  new ReturnObject("error:Type of return value -"+lastMethod.localVarTable.get(child.toString())+"- doesnt match method's: "+lastMethod.Name+" should return: "+lastMethod.Type+"");
					}
					varfound=true;
				}
				if(!varfound){
					return  new ReturnObject("error:Variable with identifier "+child.toString()+" is undefined");
				}
			}
			else if(child.getClass().equals(com.github.javaparser.ast.expr.AssignExpr.class)){
				for(Node child2: child.getChildrenNodes()){			
					if(i==0){
						if(child2.getClass().equals(com.github.javaparser.ast.expr.NameExpr.class)){
							if(lastMethod.paramTable.containsKey(child2.toString())){
								if(!lastMethod.paramTable.get(child2.toString()).equals(lastMethod.Type)){
									return  new ReturnObject("error:Type of return value -"+lastMethod.paramTable.get(child2.toString())+"- doesnt match method's: "+lastMethod.Name+" should return: "+lastMethod.Type+"");
								}
							}
							if(lastMethod.localVarTable.containsKey(child2.toString())){
								if(!lastMethod.localVarTable.get(child2.toString()).equals(lastMethod.Type)){	
									return  new ReturnObject("error:Type of return value -"+lastMethod.localVarTable.get(child2.toString())+"- doesnt match method's: "+lastMethod.Name+" should return: "+lastMethod.Type+"");
								}
							}
						}
					}
					i++;
				}
			}
		}
		return null;
		
	}
	
	private void updateScopes(ArrayList<Scope> ls){
		if(ls.size()!=0)
		lastScope=ls.get(ls.size()-1);
	}

	public ReturnObject SemanticNodeCheck(Node node, DirectedGraph<GraphNode, RelationshipEdge> hrefGraph, GraphNode previousNode, ArrayList<Scope> ls) {
		GraphNode nodeToSend = null;
		updateScopes(ls);
		
		if(node.getClass().equals(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration.class)){
			ClassScope classScp = new ClassScope();
			fillClassScope(node,classScp);
			if(!addClassScope(classScp, ls))
				return new ReturnObject("error:repeated class/interface declaration of "+classScp.Name+" ");
			
			nodeToSend = addNodeAndEdgeToGraph(node, hrefGraph, previousNode, false); 
		}    		
		
		else if(node.getClass().equals(com.github.javaparser.ast.body.MethodDeclaration.class)){
			MethodScope methodScp = new MethodScope();
			fillMethodScope(node,methodScp);
			checkPendingMethods(methodScp);
			if(!addMethodScope(methodScp, ls))
				return  new ReturnObject("error:repeated method declaration of "+methodScp.Name+", please use different identifiers for methods in same class");

			nodeToSend = addNodeAndEdgeToGraph(node, hrefGraph, previousNode, false);
		}
		
		else if(node.getClass().equals(com.github.javaparser.ast.body.Parameter.class)){
			Parameter param = new Parameter();
			if(!addParameter(node,param))
				return  new ReturnObject("error:duplicated param identifier  : "+param.paramName+" in Method:"+lastMethod.Name+"");
		}
		
		else if (node.getClass().equals(com.github.javaparser.ast.expr.VariableDeclarationExpr.class)) {
			Variable var = new Variable();
			ArrayList<Variable> repeatedOcc = new ArrayList<Variable>();
			repeatedOcc=addVariable(node,var);
			if(repeatedOcc.size()>0){
				String returnstring = "error:duplicated variable declarations:";
				for(int i=0;i<repeatedOcc.size();i++){
					if(i==0)
					returnstring  = returnstring.concat(repeatedOcc.get(i).varName+" ");
					else returnstring  = returnstring.concat("and " + repeatedOcc.get(i).varName)+ " ";
				}
				returnstring = returnstring.concat("in Method:"+lastMethod.Name+"");			
				return  new ReturnObject(returnstring);
			}
			nodeToSend = addNodeAndEdgeToGraph(node, hrefGraph, previousNode, false);	

			for(Node child: node.getChildrenNodes()){
				if(child.getClass().equals(com.github.javaparser.ast.body.VariableDeclarator.class)) {
					for(Node childNode : child.getChildrenNodes()){
						if(childNode.getClass().equals(com.github.javaparser.ast.body.VariableDeclaratorId.class)) {
							lastScope.varChanges.add(new VarChanges(nodeToSend, childNode.toString(), true));
						}
					}
				}
			}
			
		}
		
		else if (node.getClass().equals(com.github.javaparser.ast.body.FieldDeclaration.class)) {
			Field fld= new Field();
			if(!addField(node,fld))
				return  new ReturnObject("error:duplicated fields: "+fld.fieldName+" in class : "+lastClass.Name+"");

			nodeToSend = addNodeAndEdgeToGraph(node, hrefGraph, previousNode, false);	
		}
		
		else if (node.getClass().equals(com.github.javaparser.ast.stmt.ForStmt.class)) {
			LoopScope loopScp = new LoopScope();
			fillLoopScope(node,loopScp);
			addLoopScope(loopScp, ls);
			
			nodeToSend = addNodeAndEdgeToGraph(node, hrefGraph, previousNode, true);
			analyseVariablesInLoop(node, hrefGraph, nodeToSend, loopScp);
		}
		
		else if (node.getClass().equals(com.github.javaparser.ast.stmt.DoStmt.class)) {
			LoopScope loopScp = new LoopScope();
			fillLoopScope(node,loopScp);
			addLoopScope(loopScp, ls);
			
			nodeToSend = addNodeAndEdgeToGraph(node, hrefGraph, previousNode, true);
			analyseVariablesInLoop(node, hrefGraph, nodeToSend, loopScp);
		}
		
		else if (node.getClass().equals(com.github.javaparser.ast.stmt.WhileStmt.class)) {
			LoopScope loopScp = new LoopScope();
			fillLoopScope(node,loopScp);
			addLoopScope(loopScp, ls);
			
			nodeToSend = addNodeAndEdgeToGraph(node, hrefGraph, previousNode, true);
			analyseVariablesInLoop(node, hrefGraph, nodeToSend, loopScp);
		}
		
		else if(node.getClass().equals(com.github.javaparser.ast.expr.MethodCallExpr.class)){			
			nodeToSend = addNodeAndEdgeToGraph(node, hrefGraph, previousNode, false);
			for(Node childNode : node.getChildrenNodes()) {
				if(childNode.getClass().equals(com.github.javaparser.ast.expr.NameExpr.class)){
					String variable = childNode.toString();

					lastScope.varAccesses.add(new VarChanges(nodeToSend, childNode.toString(), false));
					
					for(int i = scopes.size() - 1; i >= 0; i--) {
						ArrayList<VarChanges> vc = scopes.get(i).varChanges;
						for(int j = 0; j < vc.size(); j++) {
							if(vc.get(j).getVar().equals(variable)) {
								addEdgeBetweenNodes(vc.get(j).getGraphNode(),nodeToSend, "FDG", hrefGraph);
							}
						}
					}
				}
			}
			return processMethodCallNode(node);	
		}
				
		else if(node.getClass().equals(com.github.javaparser.ast.expr.FieldAccessExpr.class)){
			//SCOPE will get me the class Scope of the fieldAccessExpr, check scopes != System 
			//if SCOPE is this, get current class
				System.out.println("SCOPE:"+((FieldAccessExpr) node).getScope().toString());
			//FIELD will get me the actual field
				System.out.println("FIELD:"+((FieldAccessExpr) node).getField().toString());
		}
		
		else if(node.getClass().equals(com.github.javaparser.ast.expr.AssignExpr.class)){			
			ArrayList<String> undeclared=new ArrayList<String>();
			String returnstring = "error:Variables with identifiers:";
			undeclared=assignExpressionCheck(node);
			System.out.println(undeclared.toString());
			if(undeclared.size()>0){
				for(int i=0;i<undeclared.size();i++){
					if(i==0)
					returnstring  = returnstring.concat(undeclared.get(i)+" ");
					else returnstring  = returnstring.concat("and " + undeclared.get(i))+ " ";

			boolean varfound=false;			
			for(Node child: node.getChildrenNodes()){
				if(child.getClass().equals(com.github.javaparser.ast.expr.NameExpr.class)){
					for(int i1 = 0; i1 < ls.size(); i1++) {
						if(ls.get(i1).getClass()==MethodScope.class){
							if(((MethodScope)ls.get(i1)).paramTable.containsKey(child.toString())) {
								varfound=true;
								break;
							}
							if(((MethodScope)ls.get(i1)).localVarTable.containsKey(child.toString())) {
								varfound=true;
								break;
							}
						}
						
						if(ls.get(i1).getClass()==LoopScope.class){							
							if(((LoopScope)ls.get(i1)).localVarTable.containsKey(child.toString())) {
								varfound=true;
								break;
							}
						}
					}
					
					if(!varfound){
						return  new ReturnObject("error:Variable with identifier "+child.toString()+" is undefined");
					} else {
						nodeToSend = addNodeAndEdgeToGraph(node, hrefGraph, previousNode, false);
						lastScope.varChanges.add(new VarChanges(nodeToSend, child.toString()));
					}
				
				
				} else if(child.getClass().equals(com.github.javaparser.ast.expr.BinaryExpr.class)){ 
					for(Node childNode : child.getChildrenNodes()) {
						if(childNode.getClass().equals(com.github.javaparser.ast.expr.NameExpr.class)){

							lastScope.varAccesses.add(new VarChanges(nodeToSend, childNode.toString(), false));
							
							String variable = childNode.toString();
							for(int i1 = scopes.size() - 1; i1 >= 0; i1--) {
								ArrayList<VarChanges> vc = scopes.get(i1).varChanges;
								for(int j = 0; j < vc.size(); j++)
									if(vc.get(j).getVar().equals(variable))
										addEdgeBetweenNodes(vc.get(j).getGraphNode(),nodeToSend, "FDG", hrefGraph);
							}
						}
					}
				}
				returnstring = returnstring.concat("in Method:"+lastMethod.Name+" are not declared");			
				
				nodeToSend = addNodeAndEdgeToGraph(node, hrefGraph, previousNode, false);
				
				return  new ReturnObject(returnstring);
		}}}else if(node.getClass().equals(com.github.javaparser.ast.stmt.ReturnStmt.class)){

			nodeToSend = addNodeAndEdgeToGraph(node, hrefGraph, previousNode, false);
			ReturnObject returnObject = new ReturnObject("");
			if((returnObject=checkReturn(node))!=null)
				return returnObject;
		
		return new ReturnObject(nodeToSend);
	}

	private void analyseVariablesInLoop(Node node, DirectedGraph<GraphNode, RelationshipEdge> hrefGraph,
			GraphNode nodeToSend, LoopScope loopScp) {
		loopScp.gn = nodeToSend;
		loopScp.node = node;
		
		for(Node child : node.getChildrenNodes())
			if(child.getClass().equals(com.github.javaparser.ast.expr.BinaryExpr.class)) 
				for(Node childNode : child.getChildrenNodes())
					if(childNode.getClass().equals(com.github.javaparser.ast.expr.NameExpr.class)){
						
						lastScope.varAccesses.add(new VarChanges(nodeToSend, childNode.toString(), false));
						
						String variable = childNode.toString();
						for(int i = scopes.size() - 1; i >= 0; i--) {
							ArrayList<VarChanges> vc = scopes.get(i).varChanges;
							for(int j = 0; j < vc.size(); j++)
								if(vc.get(j).getVar().equals(variable))
									addEdgeBetweenNodes(vc.get(j).getGraphNode(),nodeToSend, "FDG", hrefGraph);
						}
					}
	}

	private void addEdgeBetweenNodes(GraphNode graphNode, GraphNode node, String string, DirectedGraph<GraphNode, RelationshipEdge> hrefGraph) {
		 try{
				hrefGraph.addEdge(graphNode, node, new RelationshipEdge(string));
		} catch (Exception e) {
			System.out.println("ERROR in graph - " + e.getMessage());	
			e.printStackTrace();
		}	
		return;
	}

	private GraphNode addNodeAndEdgeToGraph(Node node, DirectedGraph<GraphNode, RelationshipEdge> hrefGraph,
		GraphNode previousNode, boolean loop) {
		GraphNode nodeToSend = null;			
		try{
			GraphNode newNode = new GraphNode(node.getBeginLine(), node.toString());
			hrefGraph.addVertex(newNode);
			if(previousNode == null)
				nodeToSend = newNode;
			nodeToSend = newNode;
			hrefGraph.addEdge(previousNode, newNode);
		
			if(loop)
				hrefGraph.addEdge(newNode, newNode);
	} catch (Exception e) {
		System.out.println("ERROR in graph - " + e.getMessage());	
		e.printStackTrace();
	}
		
		return nodeToSend;
	}

	public void addDependencies(DirectedGraph<GraphNode,RelationshipEdge> hrefGraph) {
		for(Scope scope : scopes) {
			if(scope instanceof LoopScope) {
				LoopScope ls = (LoopScope) scope;
				for(Node child : (ls.node.getChildrenNodes())) 
					if(child.getClass().equals(com.github.javaparser.ast.expr.BinaryExpr.class)) 
						for(Node childNode : child.getChildrenNodes()) 
							if(childNode.getClass().equals(com.github.javaparser.ast.expr.NameExpr.class)){								
								String variable = childNode.toString();
								
								for(VarChanges vc : ls.varChanges) {
									if(vc.getVar().equals(variable)) {
										addEdgeBetweenNodes(vc.getGraphNode(),ls.gn, "FDG", hrefGraph);
									}
								}
							}
				for(VarChanges va : ls.varAccesses) 
					for(VarChanges vc : ls.varChanges) 
						if (va.getVar().equals(vc.getVar())) 
							addEdgeBetweenNodes(vc.getGraphNode(),va.getGraphNode(), "FDG", hrefGraph);
			}
		}
	}

}
