package pdg;

import com.github.javaparser.ast.Node;

import java.util.Hashtable;

class MethodScope extends Scope{
	String className;
	String Type;
	String Name;
	Node methodNode;
	Hashtable<String,String> paramTable = new Hashtable<>();
	Hashtable<String,String> localVarTable = new Hashtable<>();
}
