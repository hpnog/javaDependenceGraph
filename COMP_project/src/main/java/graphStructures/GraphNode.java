package graphStructures;

import java.util.Objects;

public class GraphNode {
    private String info;
    private int id;
    public static boolean exporting = false;

    public GraphNode(int id, String string) {
        this.info = string;
        this.id = id;
    }

    @Override
    public String toString() {
        if(!exporting)
            return ("[" + this.id + "] " + this.info);
        return "Line_" + this.id;
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
        return !(!Objects.equals(this.info, other.info) || other.info == null);
    }
}
