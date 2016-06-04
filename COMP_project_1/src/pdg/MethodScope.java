package pdg;

import java.util.Hashtable;

import com.github.javaparser.ast.Node;

public class MethodScope {
	String className;
	String Type;
	String Name;
	Node methodNode;
	Hashtable<String,String> paramTable =  new Hashtable<String, String>();
	Hashtable<String,String> localVarTable =  new Hashtable<String, String>();
	
}
