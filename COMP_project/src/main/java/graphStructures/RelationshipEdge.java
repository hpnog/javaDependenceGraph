package graphStructures;

import org.jgrapht.graph.DefaultEdge;

@SuppressWarnings("all")
public class RelationshipEdge<V> extends DefaultEdge {

    String type;

    private static final long serialVersionUID = 1L;

    public RelationshipEdge(String t) {
        super();
        this.type = t;
    }

    public RelationshipEdge() {
        super();
        this.type = "CD";
    }

    @Override public String toString() {
        return this.type;
    }
}