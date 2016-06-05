package graphStructures;

/**
 * The Class VarChanges.
 */
public class VarChanges {
    
    /** The where. */
    private GraphNode where;
    
    /** The var. */
    private String var;

    /**
     * Instantiates a new var changes.
     *
     * @param gn the graph node
     * @param v the variable
     */
    public VarChanges(GraphNode gn, String v) {
        this.where = gn;
        this.var = v;
    }

    /**
     * Gets the graph node.
     *
     * @return the graph node
     */
    public GraphNode getGraphNode() {
        return this.where;
    }
    
    /**
     * Gets the var.
     *
     * @return the variable
     */
    public String getVar() {
        return this.var;
    }
}
