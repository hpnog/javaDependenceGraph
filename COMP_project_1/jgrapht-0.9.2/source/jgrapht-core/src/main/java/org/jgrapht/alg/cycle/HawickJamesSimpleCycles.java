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
 * HawickAndJamesSimpleCycles.java
 * -------------------------
 * (C) Copyright 2014, by Luiz Kill
 *
 * Original Author: Luiz Kill.
 * Contributor(s) :
 *
 * $Id$
 *
 * Changes
 * -------
 * 18-Jun-2014 : Initial revision (NO);
 */
package org.jgrapht.alg.cycle;

import java.util.*;

import org.jgrapht.*;


/**
 * Find all simple cycles of a directed graph using the algorithm described by
 * Hawick and James.
 *
 * <p>See:<br>
 * K. A. Hawick, H. A. James. Enumerating Circuits and Loops in Graphs with
 * Self-Arcs and Multiple-Arcs. Computational Science Technical Note CSTN-013,
 * 2008
 *
 * @param <V> the vertex type.
 * @param <E> the edge type.
 *
 * @author Luiz Kill
 */
public class HawickJamesSimpleCycles<V, E>
    implements DirectedSimpleCycles<V, E>
{
    private enum Operation
    {
        ENUMERATE, PRINT_ONLY, COUNT_ONLY
    }

    // The graph
    private DirectedGraph<V, E> graph = null;

    // Number of vertices
    private int nVertices = 0;

    // Number of simple cycles
    private long nCycles = 0;

    // Simple cycles found
    private List<List<V>> cycles = null;

    // The main state of the algorithm
    private Integer start = 0;
    private List<Integer> [] Ak = null;
    private List<Integer> [] B = null;
    private boolean [] blocked = null;
    private ArrayDeque<Integer> stack = null;

    // Giving an index to every V
    private V [] iToV = null;
    private Map<V, Integer> vToI = null;

    /**
     * Create a simple cycle finder with an unspecified graph.
     */
    public HawickJamesSimpleCycles()
    {
    }

    /**
     * Create a simple cycle finder for the specified graph.
     *
     * @param graph the DirectedGraph in which to find cycles.
     *
     * @throws IllegalArgumentException if the graph argument is <code>
     * null</code>.
     */
    public HawickJamesSimpleCycles(DirectedGraph<V, E> graph)
        throws IllegalArgumentException
    {
        if (graph == null) {
            throw new IllegalArgumentException("Null graph argument.");
        }
        this.graph = graph;
    }

    @SuppressWarnings("unchecked")
    private void initState(Operation o)
    {
        nCycles = 0;
        nVertices = graph.vertexSet().size();
        if (o == Operation.ENUMERATE) {
            cycles = new ArrayList<List<V>>();
        }
        blocked = new boolean[nVertices];
        stack = new ArrayDeque<Integer>(nVertices);

        B = new ArrayList[nVertices];
        for (int i = 0; i < nVertices; i++) {
            //B[i] = new ArrayList<Integer>(nVertices);
            B[i] = new ArrayList<Integer>();
        }

        iToV = (V []) graph.vertexSet().toArray();
        vToI = new HashMap<V, Integer>();
        for (int i = 0; i < iToV.length; i++) {
            vToI.put(iToV[i], i);
        }

        Ak = buildAdjacencyList();

        stack.clear();
    }

    @SuppressWarnings("unchecked")
    private List<Integer> [] buildAdjacencyList()
    {
        @SuppressWarnings("rawtypes")
        List [] Ak = new ArrayList[nVertices];
        for (int j = 0; j < nVertices; j++) {
            V v = iToV[j];
            List<V> s = Graphs.successorListOf(graph, v);
            Ak[j] = new ArrayList<Integer>(s.size());

            Iterator<V> iterator = s.iterator();
            while (iterator.hasNext()) {
                Ak[j].add(vToI.get(iterator.next()));
            }
        }

        return Ak;
    }

    private void clearState()
    {
        Ak = null;
        nVertices = 0;
        blocked = null;
        stack = null;
        iToV = null;
        vToI = null;

        for (int i = 0; i < nVertices; i++) {
            Ak[i] = null;
            B[i] = null;
        }

        Ak = null;
        B = null;
    }

    private boolean circuit(Integer v, Operation o)
    {
        boolean f = false;

        stack.push(v);
        blocked[v] = true;

        Iterator<Integer> iteratorAk = Ak[v].iterator();
        while (iteratorAk.hasNext()) {
            Integer w = iteratorAk.next();

            if (w < start) {
                continue;
            }

            if (w == start) {
                if (o == Operation.ENUMERATE) {
                    List<V> cycle = new ArrayList<V>(stack.size());

                    Iterator<Integer> iteratorStack = stack.iterator();
                    while (iteratorStack.hasNext()) {
                        cycle.add(iToV[iteratorStack.next()]);
                    }

                    cycles.add(cycle);
                }

                if (o == Operation.PRINT_ONLY) {
                    for (Integer i : stack) {
                        System.out.print(iToV[i].toString() + " ");
                    }
                    System.out.println("");
                }

                nCycles++;

                f = true;
            } else if (!blocked[w]) {
                if (circuit(w, o)) {
                    f = true;
                }
            }
        }

        if (f) {
            unblock(v);
        } else {
            iteratorAk = Ak[v].iterator();
            while (iteratorAk.hasNext()) {
                Integer w = iteratorAk.next();

                if (w < start) {
                    continue;
                }

                if (!B[w].contains(v)) {
                    B[w].add(v);
                }
            }
        }

        stack.pop();

        return f;
    }

    private void unblock(Integer u)
    {
        blocked[u] = false;

        for (int wPos = 0; wPos < B[u].size(); wPos++) {
            Integer w = B[u].get(wPos);

            wPos -= removeFromList(B[u], w);

            if (blocked[w]) {
                unblock(w);
            }
        }
    }

    /**
     * Remove all occurrences of a value from the list.
     *
     * @param u the Integer to be removed.
     * @param list the list from which all the occurrences of u must be removed.
     */
    private int removeFromList(List<Integer> list, Integer u)
    {
        int nOccurrences = 0;

        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            Integer w = iterator.next();
            if (w == u) {
                nOccurrences++;
                iterator.remove();
            }
        }

        return nOccurrences;
    }

    /**
     * {@inheritDoc}
     */
    @Override public DirectedGraph<V, E> getGraph()
    {
        return graph;
    }

    /**
     * {@inheritDoc}
     */
    @Override public void setGraph(DirectedGraph<V, E> graph)
        throws IllegalArgumentException
    {
        if (graph == null) {
            throw new IllegalArgumentException("Null graph argument.");
        }
        this.graph = graph;
    }

    /**
     * {@inheritDoc}
     */
    @Override public List<List<V>> findSimpleCycles()
        throws IllegalArgumentException
    {
        if (graph == null) {
            throw new IllegalArgumentException("Null graph.");
        }

        initState(Operation.ENUMERATE);

        for (int i = 0; i < nVertices; i++) {
            for (int j = 0; j < nVertices; j++) {
                blocked[j] = false;
                B[j].clear();
            }

            start = vToI.get(iToV[i]);
            circuit(start, Operation.ENUMERATE);
        }

        List<List<V>> result = cycles;
        clearState();
        return result;
    }

    /**
     * Print to the standard output all simple cycles without building a list to
     * keep them, thus avoiding high memory consumption when investigating large
     * and much connected graphs.
     */
    public void printSimpleCycles()
    {
        if (graph == null) {
            throw new IllegalArgumentException("Null graph.");
        }

        initState(Operation.PRINT_ONLY);

        for (int i = 0; i < nVertices; i++) {
            for (int j = 0; j < nVertices; j++) {
                blocked[j] = false;
                B[j].clear();
            }

            start = vToI.get(iToV[i]);
            circuit(start, Operation.PRINT_ONLY);
        }

        clearState();
    }

    /**
     * Count the number of simple cycles. It can count up to Long.MAX cycles in
     * a graph.
     */
    public long countSimpleCycles()
    {
        if (graph == null) {
            throw new IllegalArgumentException("Null graph.");
        }

        initState(Operation.COUNT_ONLY);

        for (int i = 0; i < nVertices; i++) {
            for (int j = 0; j < nVertices; j++) {
                blocked[j] = false;
                B[j].clear();
            }

            start = vToI.get(iToV[i]);
            circuit(start, Operation.COUNT_ONLY);
        }

        clearState();

        return nCycles;
    }
}

// End HawickAndJamesSimpleCycles.java
