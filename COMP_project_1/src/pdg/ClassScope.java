package pdg;

import java.util.ArrayList;
import java.util.Hashtable;

import graphStructures.VarChanges;

public class ClassScope extends Scope{
	String Type;
	String Name;
	Hashtable<String,String> fieldTable =  new Hashtable<String, String>();
	//functable n pode ser hashtable
	Hashtable<String,String> funcTable =  new Hashtable<String, String>();
}
