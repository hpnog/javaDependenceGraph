package pdg;

import java.util.ArrayList;
import java.util.Hashtable;

import com.github.javaparser.ast.Node;

import graphStructures.VarChanges;

public class MethodScope extends Scope{
	String className;
	String Type;
	String Name;
	Node methodNode;
	Hashtable<String,String> paramTable =  new Hashtable<String, String>();
	Hashtable<String,String> localVarTable =  new Hashtable<String, String>();
}
