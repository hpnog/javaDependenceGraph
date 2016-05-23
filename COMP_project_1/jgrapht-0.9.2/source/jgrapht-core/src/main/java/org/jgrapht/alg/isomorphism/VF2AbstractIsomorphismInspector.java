/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
/* -------------------------
 * VF2AbstractIsomorphismInspector.java
 * -------------------------
 * (C) Copyright 2015, by Fabian Späh and Contributors.
 *
 * Original Author:  Fabian Späh
 * Contributor(s):   Rita Dobler
 *
 * $Id$
 *
 * Changes
 * -------
 * 20-Jun-2015 : Initial revision (FS);
 *
 */
package org.jgrapht.alg.isomorphism;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;


public abstract class VF2AbstractIsomorphismInspector<V, E>
    implements IsomorphismInspector<V, E>
{
    protected Graph<V, E> graph1, graph2;

    protected Comparator<V> vertexComparator;
    protected Comparator<E> edgeComparator;

    protected GraphOrdering<V, E> ordering1, ordering2;

    /**
     * This implementation of the VF2 algorithm does not support graphs with
     * multiple edges.
     *
     * @param graph1 the first graph
     * @param graph2 the second graph
     * @param vertexComparator comparator for semantic equivalence of vertices
     * @param edgeComparator comparator for semantic equivalence of edges
     * @param cacheEdges if true, edges get cached for faster access
     */
    public VF2AbstractIsomorphismInspector(
        Graph<V, E> graph1,
        Graph<V, E> graph2,
        Comparator<V> vertexComparator,
        Comparator<E> edgeComparator,
        boolean cacheEdges)
    {
        if ((graph1 instanceof Multigraph)
            || (graph2 instanceof Multigraph)
            || (graph1 instanceof Pseudograph)
            || (graph2 instanceof Pseudograph)
            || (graph1 instanceof DirectedMultigraph)
            || (graph2 instanceof DirectedMultigraph)
            || (graph1 instanceof DirectedPseudograph)
            || (graph2 instanceof DirectedPseudograph))
        {
            throw new UnsupportedOperationException(
                "graphs with multiple "
                + "edges are not supported");
        }

        if (((graph1 instanceof DirectedGraph)
                && (graph2 instanceof UndirectedGraph))
            || ((graph1 instanceof UndirectedGraph)
                && (graph2 instanceof DirectedGraph)))
        {
            throw new IllegalArgumentException(
                "can not match directed with "
                + "undirected graphs");
        }

        this.graph1 = graph1;
        this.graph2 = graph2;
        this.vertexComparator = vertexComparator;
        this.edgeComparator = edgeComparator;
        this.ordering1 = new GraphOrdering<V, E>(graph1, true,
            cacheEdges);
        this.ordering2 = new GraphOrdering<V, E>(graph2, true,
            cacheEdges);
    }

    public VF2AbstractIsomorphismInspector(
        Graph<V, E> graph1,
        Graph<V, E> graph2,
        Comparator<V> vertexComparator,
        Comparator<E> edgeComparator)
    {
        this(graph1,
            graph2,
            vertexComparator,
            edgeComparator,
            true);
    }

    public VF2AbstractIsomorphismInspector(
        Graph<V, E> graph1,
        Graph<V, E> graph2,
        boolean cacheEdges)
    {
        this(graph1,
            graph2,
            null,
            null,
            cacheEdges);
    }

    public VF2AbstractIsomorphismInspector(
        Graph<V, E> graph1,
        Graph<V, E> graph2)
    {
        this(graph1,
            graph2,
            true);
    }

    @Override public abstract Iterator<GraphMapping<V, E>> getMappings();

    @Override public boolean isomorphismExists()
    {
        Iterator<GraphMapping<V, E>> iter = getMappings();
        return iter.hasNext();
    }
}

// End VF2AbstractIsomorphismInspector.java
