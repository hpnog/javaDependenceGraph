package pdg;

import com.github.javaparser.ast.Node;
import graphStructures.GraphNode;

import java.util.Hashtable;

class LoopScope extends Scope{
	String MethodName;
	String ClassName;
	Node loopNode;
	
	GraphNode gn;
	Node node;

	Hashtable<String,String> localVarTable = new Hashtable<>();
}
