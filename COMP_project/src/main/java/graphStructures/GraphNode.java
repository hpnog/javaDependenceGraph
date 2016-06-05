package graphStructures;

import java.util.Objects;

/**
 * The Class GraphNode.
 */
public class GraphNode {
    
    /** The info. */
    private String info;
    
    /** The id. */
    private int id;
    
    /** The exporting. */
    public static boolean exporting = false;

    /**
     * Instantiates a new graph node.
     *
     * @param id the id
     * @param string the string
     */
    public GraphNode(int id, String string) {
        this.info = string;
        this.id = id;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if(!exporting)
            return ("[" + this.id + "] " + this.info);
        return "Line_" + this.id;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!GraphNode.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final GraphNode other = (GraphNode) obj;
        return !(!Objects.equals(this.info, other.info) || other.info == null);
    }
}
