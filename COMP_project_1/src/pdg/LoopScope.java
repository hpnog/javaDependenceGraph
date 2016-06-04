package pdg;

import java.util.ArrayList;
import java.util.Hashtable;

import com.github.javaparser.ast.Node;

import graphStructures.GraphNode;
import graphStructures.VarChanges;

public class LoopScope extends Scope{
	String MethodName;
	String ClassName;
	Node loopNode;
	
	GraphNode gn;
	Node node;
	boolean dependenciesChecked = false;
	
	Hashtable<String,String> localVarTable =  new Hashtable<String, String>();
}
