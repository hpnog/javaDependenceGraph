package pdg;

import java.util.Hashtable;

/**
 * The Class ClassScope.
 */
class ClassScope extends Scope{
	
	/** The Type. */
	String Type;
	
	/** The Name. */
	String Name;
	
	/** The field table. */
	Hashtable<String,String> fieldTable = new Hashtable<>();
	
	/** The function table. */
	Hashtable<String,String> funcTable = new Hashtable<>();
}
