package graphStructures;

public class VarChanges {
    private GraphNode where;
    private String var;

    public VarChanges(GraphNode gn, String v) {
        this.where = gn;
        this.var = v;
    }

    public GraphNode getGraphNode() {
        return this.where;
    }
    public String getVar() {
        return this.var;
    }
}
