package pdg;

import java.util.Hashtable;

import com.github.javaparser.ast.Node;

public class LoopScope {
	String MethodName;
	String ClassName;
	Node loopNode;
	Hashtable<String,String> localVarTable =  new Hashtable<String, String>();
}
