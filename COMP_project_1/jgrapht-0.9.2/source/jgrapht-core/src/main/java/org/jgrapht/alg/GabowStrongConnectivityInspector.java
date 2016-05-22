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
 * GabowStrongConnectivityInspector.java
 * -------------------------
 * (C) Copyright 2013-2015, by Sarah Komla-Ebri and Contributors
 *
 * Original Author:  Sarah Komla-Ebri
 * Contributor(s):   Joris Kinable
 *
 *
 * Changes
 * -------
 * 25-Aug-2015 : Initial revision;
 *
 */
package org.jgrapht.alg;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.graph.*;


/**
 * Allows obtaining the strongly connected components of a directed graph. The
 * implemented algorithm follows Cheriyan-Mehlhorn/Gabow's algorithm Presented
 * in Path-based depth-first search for strong and biconnected components by
 * Gabow (2000). The running time is order of O(|V|+|E|)
 *
 * @author Sarah Komla-Ebri
 * @since September, 2013
 */

public class GabowStrongConnectivityInspector<V, E>
    implements StrongConnectivityAlgorithm<V, E>
{
    // the graph to compute the strongly connected sets
    private final DirectedGraph<V, E> graph;

    // stores the vertices
    private Deque<VertexNumber<V>> stack = new ArrayDeque<VertexNumber<V>>();

    // the result of the computation, cached for future calls
    private List<Set<V>> stronglyConnectedSets;

    // the result of the computation, cached for future calls
    private List<DirectedSubgraph<V, E>> stronglyConnectedSubgraphs;

    // maps vertices to their VertexNumber object
    private Map<V, VertexNumber<V>> vertexToVertexNumber;

    //store the numbers
    private Deque<Integer> B = new ArrayDeque<Integer>();

    //number of vertices
    private int c;

    /**
     * The constructor of GabowStrongConnectivityInspector class.
     *
     * @param directedGraph the graph to inspect
     *
     * @throws IllegalArgumentException
     */
    public GabowStrongConnectivityInspector(DirectedGraph<V, E> directedGraph)
    {
        if (directedGraph == null) {
            throw new IllegalArgumentException("null not allowed for graph!");
        }

        graph = directedGraph;
        vertexToVertexNumber = null;

        stronglyConnectedSets = null;
    }

    /**
     * Returns the graph inspected
     *
     * @return the graph inspected
     */
    public DirectedGraph<V, E> getGraph()
    {
        return graph;
    }

    /**
     * Returns true if the graph instance is strongly connected.
     *
     * @return true if the graph is strongly connected, false otherwise
     */
    public boolean isStronglyConnected()
    {
        return stronglyConnectedSets().size() == 1;
    }

    /**
     * Computes a {@link List} of {@link Set}s, where each set contains vertices
     * which together form a strongly connected component within the given
     * graph.
     *
     * @return <code>List</code> of <code>Set</code> s containing the strongly
     * connected components
     */
    public List<Set<V>> stronglyConnectedSets()
    {
        if (stronglyConnectedSets == null) {
            stronglyConnectedSets = new Vector<Set<V>>();

            // create VertexData objects for all vertices, store them
            createVertexNumber();

            // perform  DFS
            for (VertexNumber<V> data : vertexToVertexNumber.values()) {
                if (data.getNumber() == 0) {
                    dfsVisit(graph, data);
                }
            }

            vertexToVertexNumber = null;
            stack = null;
            B = null;
        }

        return stronglyConnectedSets;
    }

    /**
     * <p>Computes a list of {@link DirectedSubgraph}s of the given graph. Each
     * subgraph will represent a strongly connected component and will contain
     * all vertices of that component. The subgraph will have an edge (u,v) iff
     * u and v are contained in the strongly connected component.</p>
     *
     * <p>NOTE: Calling this method will first execute {@link
     * GabowStrongConnectivityInspector#stronglyConnectedSets()}. If you don't
     * need subgraphs, use that method.</p>
     *
     * @return a list of subgraphs representing the strongly connected
     * components
     */
    public List<DirectedSubgraph<V, E>> stronglyConnectedSubgraphs()
    {
        if (stronglyConnectedSubgraphs == null) {
            List<Set<V>> sets = stronglyConnectedSets();
            stronglyConnectedSubgraphs =
                new Vector<DirectedSubgraph<V, E>>(sets.size());

            for (Set<V> set : sets) {
                stronglyConnectedSubgraphs.add(
                    new DirectedSubgraph<V, E>(
                        graph,
                        set,
                        null));
            }
        }

        return stronglyConnectedSubgraphs;
    }

    /*
     * Creates a VertexNumber object for every vertex in the graph and stores
     * them
     * in a HashMap.
     */

    private void createVertexNumber()
    {
        c = graph.vertexSet().size();
        vertexToVertexNumber = new HashMap<V, VertexNumber<V>>(c);

        for (V vertex : graph.vertexSet()) {
            vertexToVertexNumber.put(
                vertex,
                new VertexNumber<V>(vertex, 0));
        }

        stack = new ArrayDeque<VertexNumber<V>>(c);
        B = new ArrayDeque<Integer>(c);
    }

    /*
     * The subroutine of DFS.
     */
    private void dfsVisit(DirectedGraph<V, E> visitedGraph, VertexNumber<V> v)
    {
        VertexNumber<V> w;
        stack.add(v);
        B.add(v.setNumber(stack.size() - 1));

        // follow all edges

        for (E edge : visitedGraph.outgoingEdgesOf(v.getVertex())) {
            w = vertexToVertexNumber.get(
                visitedGraph.getEdgeTarget(edge));

            if (w.getNumber() == 0) {
                dfsVisit(graph, w);
            } else { /*contract if necessary*/
                while (w.getNumber() < B.getLast()) {
                    B.removeLast();
                }
            }
        }
        Set<V> L = new HashSet<V>();
        if (v.getNumber() == (B.getLast())) {
            /* number vertices of the next
                strong component */
            B.removeLast();

            c++;
            while (v.getNumber() <= (stack.size() - 1)) {
                VertexNumber<V> r = stack.removeLast();
                L.add(r.getVertex());
                r.setNumber(c);
            }
            stronglyConnectedSets.add(L);
        }
    }

    private static final class VertexNumber<V>
    {
        V vertex;
        int number = 0;

        private VertexNumber(
            V vertex,
            int number)
        {
            this.vertex = vertex;
            this.number = number;
        }

        int getNumber()
        {
            return number;
        }

        V getVertex()
        {
            return vertex;
        }

        Integer setNumber(int n)
        {
            return number = n;
        }
    }
}

// End GabowStrongConnectivityInspector.java
