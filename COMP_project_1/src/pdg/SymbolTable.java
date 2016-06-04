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
	public ArrayList<Scope> scopes;
	ArrayList<String> pendingMethodDeclarations;

	private ClassScope lastClass = null;
	private MethodScope lastMethod = null;
	private LoopScope lastLoop = null;
	public Scope lastScope = null;

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
		pendingMethodDeclarations = new ArrayList<String>();
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
			if(methodScp.Name.equals(pendingMethodDeclarations.get(i)))
					pendingMethodDeclarations.remove(pendingMethodDeclarations.get(i));
		}
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
		System.out.println("VAR NAME:" + var.varName);
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
			
			//if not null, it's not an user defined method
			if(((MethodCallExpr)node).getScope()==null){
				boolean varfound=false;
				boolean methodfound=false;
				int nargs= 0;
			    StringTokenizer stok = new StringTokenizer(node.toString(),"(");
			    String methodName=stok.nextToken();
			   
			    //check if this method is defined and determine nr of args
			    if(!lastClass.funcTable.containsKey(methodName))
			    	for(int i = 0; i < scopes.size(); i++){
					if(scopes.get(i).getClass()==ClassScope.class){
						if(node.getChildrenNodes().get(0).equals(methodName))
							methodfound=true;
					}
					
				}
			    else methodfound=true;
			    if(!methodfound)
			    	pendingMethodDeclarations.add(methodName);
			    else{
				//verify getArgs.size = method.ParamTable.size
			   
			    	//System.out.println("ARGS OF METHODCALL"+(((MethodCallExpr)node).getArgs().toString()));
				//TYPE CHECK EACH ARGUMENT 
				//CHECK IF ARGUMENT IS DEFINED(COULD BE FUNC PARAM,METHOD FROM CURRENTCLASS AND LOCALVAR)
				for(Node child: node.getChildrenNodes()){
					
						//System.out.println("CHILD OF METHODCALL"+child.toString());
					
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
					for(int i = 0; i < ls.size(); i++) {
						if(ls.get(i).getClass()==MethodScope.class){
							if(((MethodScope)ls.get(i)).paramTable.containsKey(child.toString())) {
								varfound=true;
								break;
							}
							if(((MethodScope)ls.get(i)).localVarTable.containsKey(child.toString())) {
								varfound=true;
								break;
							}
						}
						
						if(ls.get(i).getClass()==LoopScope.class){							
							if(((LoopScope)ls.get(i)).localVarTable.containsKey(child.toString())) {
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
							for(int i = scopes.size() - 1; i >= 0; i--) {
								ArrayList<VarChanges> vc = scopes.get(i).varChanges;
								for(int j = 0; j < vc.size(); j++)
									if(vc.get(j).getVar().equals(variable))
										addEdgeBetweenNodes(vc.get(j).getGraphNode(),nodeToSend, "FDG", hrefGraph);
							}
						}
					}
				}
			}
		}
		
		else if(node.getClass().equals(com.github.javaparser.ast.stmt.ReturnStmt.class)){

			nodeToSend = addNodeAndEdgeToGraph(node, hrefGraph, previousNode, false);
			
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
		}
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
