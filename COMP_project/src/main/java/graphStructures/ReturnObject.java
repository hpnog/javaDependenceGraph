package graphStructures;

import java.util.Objects;

/**
 * The Class ReturnObject.
 */
public class ReturnObject {
    
    /** The error. */
    private String error;
    
    /** The graph node. */
    private GraphNode gn;

    /**
     * Instantiates a new return object.
     *
     * @param er the error
     */
    public ReturnObject(String er) {
        this.error = er;
        this.gn = null;
    }

    /**
     * Instantiates a new return object.
     *
     * @param g the graph node
     */
    public ReturnObject(GraphNode g) {
        this.error = null;
        this.gn = g;
    }

    /**
     * Checks for error.
     *
     * @return true, if an error exists
     */
    public boolean hasError() {
        return (error != null) && (!Objects.equals(error, "clear"));
    }

    /**
     * Gets the error.
     *
     * @return the error
     */
    public String getError() {
        return error;
    }

    /**
     * Gets the graph node.
     *
     * @return the graph node
     */
    public GraphNode getGraphNode() {
        return gn;
    }
}
