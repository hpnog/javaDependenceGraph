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
/* -----------------
 * EdmondsKarpMaximumFlow.java
 * -----------------
 * (C) Copyright 2008-2008, by Ilya Razenshteyn and Contributors.
 *
 * Original Author:  Ilya Razenshteyn
 * Contributor(s):   -
 *
 * $Id$
 *
 * Changes
 * -------
 */
package org.jgrapht.alg;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.*;


/**
 * @deprecated Use {@link org.jgrapht.alg.flow.EdmondsKarpMaximumFlow} instead
 */
@Deprecated public final class EdmondsKarpMaximumFlow<V, E>
    implements MaximumFlowAlgorithm<V, E>
{
    /**
     * Default tolerance.
     */
    public static final double DEFAULT_EPSILON = 0.000000001;
    private org.jgrapht.alg.flow.EdmondsKarpMaximumFlow<V, E> engine;

    private MaximumFlowAlgorithm.MaximumFlow<V, E> maxFlow;

    private V currentSource;
    private V currentSink;

    /**
     * Constructs <tt>MaximumFlow</tt> instance to work with <i>a copy of</i>
     * <tt>network</tt>. Current source and sink are set to <tt>null</tt>. If
     * <tt>network</tt> is weighted, then capacities are weights, otherwise all
     * capacities are equal to one. Doubles are compared using <tt>
     * DEFAULT_EPSILON</tt> tolerance.
     *
     * @param network network, where maximum flow will be calculated
     */
    public EdmondsKarpMaximumFlow(DirectedGraph<V, E> network)
    {
        this(network, DEFAULT_EPSILON);
    }

    /**
     * Constructs <tt>MaximumFlow</tt> instance to work with <i>a copy of</i>
     * <tt>network</tt>. Current source and sink are set to <tt>null</tt>. If
     * <tt>network</tt> is weighted, then capacities are weights, otherwise all
     * capacities are equal to one.
     *
     * @param network network, where maximum flow will be calculated
     * @param epsilon tolerance for comparing doubles
     */
    public EdmondsKarpMaximumFlow(DirectedGraph<V, E> network, double epsilon)
    {
        engine =
            new org.jgrapht.alg.flow.EdmondsKarpMaximumFlow<V, E>(
                network,
                epsilon);
    }

    private void build()
    {
        maxFlow = engine.buildMaximumFlow(currentSource, currentSink);
    }

    /**
     * Sets current source to <tt>source</tt>, current sink to <tt>sink</tt>,
     * then calculates maximum flow from <tt>source</tt> to <tt>sink</tt>. Note,
     * that <tt>source</tt> and <tt>sink</tt> must be vertices of the <tt>
     * network</tt> passed to the constructor, and they must be different.
     *
     * @param source source vertex
     * @param sink sink vertex
     */
    public void calculateMaximumFlow(
        V source,
        V sink)
    {
        currentSource = source;
        currentSink = sink;

        build();
    }

    /**
     * Returns maximum flow value, that was calculated during last <tt>
     * calculateMaximumFlow</tt> call, or <tt>null</tt>, if there was no <tt>
     * calculateMaximumFlow</tt> calls.
     *
     * @return maximum flow value
     */
    public Double getMaximumFlowValue()
    {
        return maxFlow.getValue();
    }

    /**
     * Returns maximum flow, that was calculated during last <tt>
     * calculateMaximumFlow</tt> call, or <tt>null</tt>, if there was no <tt>
     * calculateMaximumFlow</tt> calls.
     *
     * @return <i>read-only</i> mapping from edges to doubles - flow values
     */
    public Map<E, Double> getMaximumFlow()
    {
        return Collections.unmodifiableMap(maxFlow.getFlow());
    }

    /**
     * Returns current source vertex, or <tt>null</tt> if there was no <tt>
     * calculateMaximumFlow</tt> calls.
     *
     * @return current source
     */
    public V getCurrentSource()
    {
        return currentSource;
    }

    /**
     * Returns current sink vertex, or <tt>null</tt> if there was no <tt>
     * calculateMaximumFlow</tt> calls.
     *
     * @return current sink
     */
    public V getCurrentSink()
    {
        return currentSink;
    }

    @Override public MaximumFlow<V, E> buildMaximumFlow(V source, V sink)
    {
        currentSource = source;
        currentSink = sink;

        build();

        return maxFlow;
    }
}

// End EdmondsKarpMaximumFlow.java
