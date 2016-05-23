/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://org.org.jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2013, by Barak Naveh and Contributors.
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
 * StrongConnectivityAlgorithm.java
 * -------------------------
 *
 * Original Author:  Sarah Komla-Ebri
 * Contributor(s): Joris Kinable
 *
 */
package org.jgrapht.alg.interfaces;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;


/**
 * An interface to the StrongConnectivityInspector algorithm classes. These
 * classes verify whether the graph is strongly connected.
 *
 * @param <V> vertex concept type
 * @param <E> edge concept type
 *
 * @author Sarah Komla-Ebri
 * @since September, 2013
 */
public interface StrongConnectivityAlgorithm<V, E>
{
    /**
     * Returns the graph inspected by the StrongConnectivityAlgorithm.
     *
     * @return the graph inspected by this StrongConnectivityAlgorithm
     */
    public DirectedGraph<V, E> getGraph();

    /**
     * Returns true if the graph of this <code>
     * StrongConnectivityAlgorithm</code> instance is strongly connected.
     *
     * @return true if the graph is strongly connected, false otherwise
     */
    public boolean isStronglyConnected();

    /**
     * Computes a {@link List} of {@link Set}s, where each set contains vertices
     * which together form a strongly connected component within the given
     * graph.
     *
     * @return <code>List</code> of <code>Set</code> s containing the strongly
     * connected components
     */
    public List<Set<V>> stronglyConnectedSets();

    /**
     * <p>Computes a list of {@link DirectedSubgraph}s of the given graph. Each
     * subgraph will represent a strongly connected component and will contain
     * all vertices of that component. The subgraph will have an edge (u,v) iff
     * u and v are contained in the strongly connected component.</p>
     *
     * @return a list of subgraphs representing the strongly connected
     * components
     */
    public List<DirectedSubgraph<V, E>> stronglyConnectedSubgraphs();
}

// End StrongConnectivityAlgorithm.java
