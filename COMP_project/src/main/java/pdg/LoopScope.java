package pdg;

import com.github.javaparser.ast.Node;
import graphStructures.GraphNode;

import java.util.Hashtable;

/**
 * The Class LoopScope.
 */
class LoopScope extends Scope{
	
	/** The Method name. */
	String MethodName;
	
	/** The Class name. */
	String ClassName;
	
	/** The loop node. */
	Node loopNode;
	
	/** The graph node. */
	GraphNode gn;
	
	/** The node. */
	Node node;

	/** The local variable table. */
	Hashtable<String,String> localVarTable = new Hashtable<>();
}
