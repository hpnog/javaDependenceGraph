package pdg;

import java.util.Hashtable;

public class ClassScope {
	String Type;
	String Name;
	Hashtable<String,String> fieldTable =  new Hashtable<String, String>();
	//functable n pode ser hashtable
	Hashtable<String,String> funcTable =  new Hashtable<String, String>();
}
