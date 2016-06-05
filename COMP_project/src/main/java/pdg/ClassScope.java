package pdg;

import java.util.Hashtable;

class ClassScope extends Scope{
	String Type;
	String Name;
	Hashtable<String,String> fieldTable = new Hashtable<>();
	Hashtable<String,String> funcTable = new Hashtable<>();
}
