package graphStructures;

public class VarChanges {
	GraphNode where;
	String var;
	boolean declared;
	
	public VarChanges(GraphNode gn, String v, boolean b) {
		this.where = gn;
		this.var = v;
		this.declared = b;
	}
	
	public VarChanges(GraphNode gn, String v) {
		this.where = gn;
		this.var = v;
		this.declared = false;
	}
	
	public GraphNode getGraphNode() {
		return this.where;
	}
	
	public String getVar() {
		return this.var;
	}
	
	public boolean getDeclared() {
		return this.declared;
	}
}
