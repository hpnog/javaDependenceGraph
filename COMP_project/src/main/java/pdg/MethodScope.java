package pdg;

import com.github.javaparser.ast.Node;

import java.util.Hashtable;

/**
 * The Class MethodScope.
 */
class MethodScope extends Scope{
	
	/** The class name. */
	String className;
	
	/** The Type. */
	String Type;
	
	/** The Name. */
	String Name;
	
	/** The method node. */
	Node methodNode;
	
	/** The parameters table. */
	Hashtable<String,String> paramTable = new Hashtable<>();
	
	/** The local variable table. */
	Hashtable<String,String> localVarTable = new Hashtable<>();
}
