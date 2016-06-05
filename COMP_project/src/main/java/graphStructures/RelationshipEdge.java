package graphStructures;

import org.jgrapht.graph.DefaultEdge;

/**
 * The Class RelationshipEdge. Extends DefaultEdge to add more info to each Edge.
 *
 * @param <V> the value type
 */
@SuppressWarnings("all")
public class RelationshipEdge<V> extends DefaultEdge {

    /** The type. */
    String type;

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new relationship edge.
     *
     * @param t the t
     */
    public RelationshipEdge(String t) {
        super();
        this.type = t;
    }

    /**
     * Instantiates a new relationship edge.
     */
    public RelationshipEdge() {
        super();
        this.type = "CD";
    }

    /* (non-Javadoc)
     * @see org.jgrapht.graph.DefaultEdge#toString()
     */
    @Override public String toString() {
        return this.type;
    }
}