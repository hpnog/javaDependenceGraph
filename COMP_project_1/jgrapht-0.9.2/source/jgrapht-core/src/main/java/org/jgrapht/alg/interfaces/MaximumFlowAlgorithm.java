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
 * MaximumFlowAlgorithm.java
 * -----------------
 * (C) Copyright 2015-2015, by Alexey Kudinkin and Contributors.
 *
 * Original Author:  Alexey Kudinkin
 * Contributor(s):
 *
 * $Id$
 *
 * Changes
 * -------
 */
package org.jgrapht.alg.interfaces;

import java.util.*;


/**
 * Allows to derive <a
 * href="https://en.wikipedia.org/wiki/Maximum_flow_problem">maximum-flow</a>
 * from the supplied <a href="https://en.wikipedia.org/wiki/Flow_network">flow
 * network</a>
 *
 * @param <V> vertex concept type
 * @param <E> edge concept type
 */
public interface MaximumFlowAlgorithm<V, E>
{
    /**
     * Builds maximum flow for the supplied network flow, for the supplied
     * ${source} and ${sink}
     *
     * @param source source of the flow inside the network
     * @param sink sink of the flow inside the network
     *
     * @return maximum flow
     */
    MaximumFlow<V, E> buildMaximumFlow(V source, V sink);

    interface MaximumFlow<V, E>
    {
        /**
         * Returns value of the maximum-flow for the given network
         *
         * @return value of th maximum-flow
         */
        public Double getValue();

        /**
         * Returns mapping from edge to flow value through this particular edge
         *
         * @return maximum flow
         */
        public Map<E, Double> getFlow();
    }

    class MaximumFlowImpl<V, E>
        implements MaximumFlow
    {
        private Double value;
        private Map<E, Double> flow;

        public MaximumFlowImpl(Double value, Map<E, Double> flow)
        {
            this.value = value;
            this.flow = Collections.unmodifiableMap(flow);
        }

        @Override public Double getValue()
        {
            return value;
        }

        @Override public Map<E, Double> getFlow()
        {
            return flow;
        }
    }
}

// End MaximumFlowAlgorithm.java
