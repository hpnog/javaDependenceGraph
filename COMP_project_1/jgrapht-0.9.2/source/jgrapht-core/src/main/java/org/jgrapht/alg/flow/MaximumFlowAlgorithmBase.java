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
 * MaximumFlowAlgorithmBase.java
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
package org.jgrapht.alg.flow;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.alg.util.*;
import org.jgrapht.alg.util.Extension.*;


/**
 * Base class backing algorithms allowing to derive <a
 * href="https://en.wikipedia.org/wiki/Maximum_flow_problem">maximum-flow</a>
 * from the supplied <a href="https://en.wikipedia.org/wiki/Flow_network">flow
 * network</a>
 *
 * @param <V> vertex concept type
 * @param <E> edge concept type
 *
 * @author Alexey Kudinkin
 */
public abstract class MaximumFlowAlgorithmBase<V, E>
    implements MaximumFlowAlgorithm<V, E>
{
    /**
     * Default tolerance.
     */
    public static final double DEFAULT_EPSILON = 1e-9;

    private Extension<V, ? extends VertexExtensionBase> vXs;
    private Extension<E, ? extends EdgeExtensionBase> eXs;

    abstract DirectedGraph<V, E> getNetwork();

    <VE extends VertexExtensionBase, EE extends EdgeExtensionBase> void init(
        ExtensionFactory<VE> vertexExtensionFactory,
        ExtensionFactory<EE> edgeExtensionFactory)
    {
        vXs = new Extension<V, VE>(vertexExtensionFactory);
        eXs = new Extension<E, EE>(edgeExtensionFactory);

        buildInternal();
    }

    private void buildInternal()
    {
        DirectedGraph<V, E> n = getNetwork();

        for (V u : n.vertexSet()) {
            VertexExtensionBase ux = extendedVertex(u);

            ux.prototype = u;

            for (E e : n.outgoingEdgesOf(u)) {
                V v = n.getEdgeTarget(e);

                VertexExtensionBase vx = extendedVertex(v);

                EdgeExtensionBase ex =
                    createEdge(ux, vx, e, n.getEdgeWeight(e));
                EdgeExtensionBase iex = createInverse(ex, n);

                ux.getOutgoing().add(ex);

                // NB: Any better?
                if (iex.prototype == null) {
                    vx.getOutgoing().add(iex);
                }
            }
        }
    }

    private EdgeExtensionBase createEdge(
        VertexExtensionBase source,
        VertexExtensionBase target,
        E e,
        double weight)
    {
        EdgeExtensionBase ex = extendedEdge(e);

        ex.source = source;
        ex.target = target;
        ex.capacity = weight;
        ex.prototype = e;

        return ex;
    }

    private EdgeExtensionBase createInverse(
        EdgeExtensionBase ex,
        DirectedGraph<V, E> n)
    {
        EdgeExtensionBase iex;

        if (n.containsEdge(ex.target.prototype, ex.source.prototype)) {
            E ie = n.getEdge(ex.target.prototype, ex.source.prototype);
            iex = createEdge(ex.target, ex.source, ie, n.getEdgeWeight(ie));
        } else {
            iex = eXs.createInstance();

            iex.source = ex.target;
            iex.target = ex.source;
        }

        ex.inverse = iex;
        iex.inverse = ex;

        return iex;
    }

    private VertexExtensionBase extendedVertex(V v)
    {
        return this.<VertexExtensionBase>vertexExtended(v);
    }

    private EdgeExtensionBase extendedEdge(E e)
    {
        return this.<EdgeExtensionBase>edgeExtended(e);
    }

    protected <VE extends VertexExtensionBase> VE vertexExtended(V v)
    {
        return (VE) vXs.get(v);
    }

    protected <EE extends EdgeExtensionBase> EE edgeExtended(E e)
    {
        return (EE) eXs.get(e);
    }

    protected void pushFlowThrough(EdgeExtensionBase ex, double f)
    {
        EdgeExtensionBase iex = ex.<EdgeExtensionBase>getInverse();

        assert ((compareFlowTo(ex.flow, 0.0) == 0)
            || (compareFlowTo(iex.flow, 0.0) == 0));

        if (compareFlowTo(iex.flow, f) == -1) {
            double d = f - iex.flow;

            ex.flow += d;
            ex.capacity -= iex.flow;

            iex.flow = 0;
            iex.capacity += d;
        } else {
            ex.capacity -= f;
            iex.flow -= f;
        }
    }

    protected Map<E, Double> composeFlow()
    {
        Map<E, Double> maxFlow = new HashMap<E, Double>();
        for (E e : getNetwork().edgeSet()) {
            EdgeExtensionBase ex = extendedEdge(e);
            maxFlow.put(e, ex.flow);
        }

        return maxFlow;
    }

    protected int compareFlowTo(double flow, double val)
    {
        double diff = flow - val;
        if (Math.abs(diff) < DEFAULT_EPSILON) {
            return 0;
        } else {
            return (diff < 0) ? -1 : 1;
        }
    }

    class VertexExtensionBase
        extends Extension.BaseExtension
    {
        private final List<? extends EdgeExtensionBase> outgoing =
            new ArrayList<EdgeExtensionBase>();

        V prototype;

        double excess;

        public <EE extends EdgeExtensionBase> List<EE> getOutgoing()
        {
            return (List<EE>) outgoing;
        }
    }

    class EdgeExtensionBase
        extends Extension.BaseExtension
    {
        private VertexExtensionBase source;
        private VertexExtensionBase target;

        private EdgeExtensionBase inverse;

        E prototype;

        double capacity;
        double flow;

        public <VE extends VertexExtensionBase> VE getSource()
        {
            return (VE) source;
        }

        public void setSource(VertexExtensionBase source)
        {
            this.source = source;
        }

        public <VE extends VertexExtensionBase> VE getTarget()
        {
            return (VE) target;
        }

        public void setTarget(VertexExtensionBase target)
        {
            this.target = target;
        }

        public <EE extends EdgeExtensionBase> EE getInverse()
        {
            return (EE) inverse;
        }

        public void setInverse(EdgeExtensionBase inverse)
        {
            this.inverse = inverse;
        }
    }
}

// End MaximumFlowAlgorithmBase.java
