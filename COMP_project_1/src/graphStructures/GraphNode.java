package graphStructures;

public class GraphNode extends Object {
	private String info;
	
	public GraphNode(String string) {
		this.info = string;
	}
	
	@Override
	public String toString() {
		return this.info;
	}
	
	@Override
	public boolean equals(Object obj) {		
		if (obj == null) {
		   return false;
		}
		if (!GraphNode.class.isAssignableFrom(obj.getClass())) {
		    return false;
		}
		final GraphNode other = (GraphNode) obj;
		if (this.info != other.info || other.info == null) {
		    return false;
		}
		return true;
	}
}
