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
 * VF2GraphIsomorphismInspector.java
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


/**
 * This is an implementation of the VF2 algorithm using its feature of detecting
 * <a href="http://mathworld.wolfram.com/GraphIsomorphism.html">isomorphism
 * between two graphs</a> as described in Cordella et al. A (sub)graph
 * isomorphism algorithm for matching large graphs (2004),
 * DOI:10.1109/TPAMI.2004.75, <a
 * href="http://ieeexplore.ieee.org/xpl/articleDetails.jsp?arnumber=1323804">
 * http://ieeexplore.ieee.org/xpl/articleDetails.jsp?arnumber=1323804</a>
 *
 * @param <V> the type of the vertices
 * @param <E> the type of the edges
 */
public class VF2GraphIsomorphismInspector<V, E>
    extends VF2AbstractIsomorphismInspector<V, E>
{
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
    public VF2GraphIsomorphismInspector(
        Graph<V, E> graph1,
        Graph<V, E> graph2,
        Comparator<V> vertexComparator,
        Comparator<E> edgeComparator,
        boolean cacheEdges)
    {
        super(graph1,
            graph2,
            vertexComparator,
            edgeComparator,
            cacheEdges);
    }

    public VF2GraphIsomorphismInspector(
        Graph<V, E> graph1,
        Graph<V, E> graph2,
        Comparator<V> vertexComparator,
        Comparator<E> edgeComparator)
    {
        super(graph1,
            graph2,
            vertexComparator,
            edgeComparator,
            true);
    }

    public VF2GraphIsomorphismInspector(
        Graph<V, E> graph1,
        Graph<V, E> graph2,
        boolean cacheEdges)
    {
        super(graph1,
            graph2,
            null,
            null,
            cacheEdges);
    }

    public VF2GraphIsomorphismInspector(Graph<V, E> graph1, Graph<V, E> graph2)
    {
        super(graph1,
            graph2,
            true);
    }

    @Override public VF2GraphMappingIterator<V, E> getMappings()
    {
        return new VF2GraphMappingIterator<V, E>(
            ordering1,
            ordering2,
            vertexComparator,
            edgeComparator);
    }
}

// End VF2GraphIsomorphismInspector.java
