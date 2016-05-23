/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2009, by Barak Naveh and Contributors.
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
 * MixedGraphUnion.java
 * -------------------------
 *
 * This class implements a <a href="https://en.wikipedia.org/wiki/Mixed_graph">Mixed-Graph</a>. A Mixed-Graph consists of
 * both undirected edges, and directed arcs. The mixed graph is obtained by taking the union of a undirected and a directed graph.
 * Be careful: NOT all algorithm implementations in the JgraphT handle Mixed Graphs correctly, so thoroughly check the results! From the outside, a Mixed-Graph is a
 * DirectedGraph. However, the undirected edges should obviously be treated as undirected. Many algorithms have a switch checking whether the graph is Directed or Undirected, and behave
 * differently uppon the result; these algorithms do not necessarily handle hybrid cases like this correctly. An example algorithm which *does* handle the Mixed-Graphs correctly
 * is the FloydWarshallShortestPath implementation.
 *
 * Often, a Mixed-Graph is unnecessary: every undirected edge (i,j) can be replaced by two directed arcs: <i,j>,<j,i>, thereby obtaining a Directed Graph. This is the
 * preferred approach since all algorithm implementations work well for Directed Graphs. However, there are use-cases where a pair of directed arcs <i,j>,<j,i> have a different
 * meaning than a single undirected edge (i,j). In the latter case, this class is applicable.
 *
 * Original Author:  Joris Kinable.
 *
 * $Id$
 *
 * Changes
 * -------
 * 20-Aug-2015 : Initial revision (IR);
 *
 */
package org.jgrapht.graph;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.util.*;


public class MixedGraphUnion<V, E>
    extends GraphUnion<V, E, Graph<V, E>>
    implements DirectedGraph<V, E>
{
    private final UndirectedGraph<V, E> undirectedGraph;
    private final DirectedGraph<V, E> directedGraph;

    public MixedGraphUnion(
        UndirectedGraph<V, E> g1,
        DirectedGraph<V, E> g2,
        WeightCombiner operator)
    {
        super(g1, g2, operator);
        this.undirectedGraph = g1;
        this.directedGraph = g2;
    }

    public MixedGraphUnion(UndirectedGraph<V, E> g1, DirectedGraph<V, E> g2)
    {
        super(g1, g2);
        this.undirectedGraph = g1;
        this.directedGraph = g2;
    }

    @Override public int inDegreeOf(V vertex)
    {
        Set<E> res = incomingEdgesOf(vertex);
        return res.size();
    }

    @Override public Set<E> incomingEdgesOf(V vertex)
    {
        Set<E> res = new LinkedHashSet<E>();
        if (directedGraph.containsVertex(vertex)) {
            res.addAll(directedGraph.incomingEdgesOf(vertex));
        }
        if (undirectedGraph.containsVertex(vertex)) {
            res.addAll(undirectedGraph.edgesOf(vertex));
        }
        return Collections.unmodifiableSet(res);
    }

    @Override public int outDegreeOf(V vertex)
    {
        Set<E> res = outgoingEdgesOf(vertex);
        return res.size();
    }

    @Override public Set<E> outgoingEdgesOf(V vertex)
    {
        Set<E> res = new LinkedHashSet<E>();
        if (directedGraph.containsVertex(vertex)) {
            res.addAll(directedGraph.outgoingEdgesOf(vertex));
        }
        if (undirectedGraph.containsVertex(vertex)) {
            res.addAll(undirectedGraph.edgesOf(vertex));
        }
        return Collections.unmodifiableSet(res);
    }
}

// End MixedGraphUnion.java
