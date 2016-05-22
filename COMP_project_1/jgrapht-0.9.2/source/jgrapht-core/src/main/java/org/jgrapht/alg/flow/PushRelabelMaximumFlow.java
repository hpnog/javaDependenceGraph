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
 * PushRelabelMaximumFlow.java
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
import org.jgrapht.alg.util.*;
import org.jgrapht.alg.util.Extension.*;


/**
 * <p><a
 * href="https://en.wikipedia.org/wiki/Push%E2%80%93relabel_maximum_flow_algorithm">
 * Push-relabel maximum flow</a> algorithm designed by Andrew V. Goldberg and
 * Robert Tarjan. Current implementation complexity upper-bound is O(V^3). For
 * more details see: <i>"A new approach to the maximum flow problem"</i> by
 * Andrew V. Goldberg and Robert Tarjan <i>STOC '86: Proceedings of the
 * eighteenth annual ACM symposium on Theory of computing</i></p>
 *
 * @author Alexey Kudinkin
 */
public class PushRelabelMaximumFlow<V, E>
    extends MaximumFlowAlgorithmBase<V, E>
{
    // Diagnostic

    private static final boolean DIAGNOSTIC_ENABLED = false;

    private DirectedGraph<V, E> network;

    private final ExtensionFactory<VertexExtension> vertexExtensionsFactory;
    private final ExtensionFactory<EdgeExtension> edgeExtensionsFactory;

    // Label pruning helpers

    private Map<Integer, Integer> labeling;

    boolean flowBack;

    private PushRelabelDiagnostic diagnostic;

    public PushRelabelMaximumFlow(DirectedGraph<V, E> network)
    {
        this.network = network;

        this.vertexExtensionsFactory =
            new ExtensionFactory<VertexExtension>() {
                @Override public VertexExtension create()
                {
                    return PushRelabelMaximumFlow.this.new VertexExtension();
                }
            };

        this.edgeExtensionsFactory =
            new ExtensionFactory<EdgeExtension>() {
                @Override public EdgeExtension create()
                {
                    return PushRelabelMaximumFlow.this.new EdgeExtension();
                }
            };

        if (DIAGNOSTIC_ENABLED) {
            this.diagnostic = new PushRelabelDiagnostic();
        }
    }

    void init()
    {
        super.init(vertexExtensionsFactory, edgeExtensionsFactory);

        this.labeling = new HashMap<Integer, Integer>();
        this.flowBack = false;
    }

    @Override DirectedGraph<V, E> getNetwork()
    {
        return network;
    }

    public void initialize(
        VertexExtension source,
        VertexExtension sink,
        Queue<VertexExtension> active)
    {
        source.label = network.vertexSet().size();
        source.excess = Double.POSITIVE_INFINITY;

        label(source, sink);

        for (EdgeExtension ex : source.<EdgeExtension>getOutgoing()) {
            pushFlowThrough(ex, ex.capacity);

            if (ex.getTarget().prototype != sink.prototype) {
                active.offer(ex.<VertexExtension>getTarget());
            }
        }
    }

    private void label(VertexExtension source, VertexExtension sink)
    {
        Set<VertexExtension> seen = new HashSet<VertexExtension>();
        Queue<VertexExtension> q = new ArrayDeque<VertexExtension>();

        q.offer(sink);

        sink.label = 0;

        seen.add(sink);
        seen.add(source);

        while (!q.isEmpty()) {
            VertexExtension ux = q.poll();
            for (EdgeExtension ex : ux.<EdgeExtension>getOutgoing()) {
                VertexExtension vx = ex.getTarget();
                if (!seen.contains(vx)) {
                    seen.add(vx);

                    vx.label = ux.label + 1;
                    q.add(vx);

                    // NOTA BENE:
                    //  This is part of label-pruning mechanic which
                    //  targets to diminish all 'useless' relabels during
                    //  "flow-back" phase of the algorithm pushing excess
                    //  flow back to the source

                    if (!labeling.containsKey(vx.label)) {
                        labeling.put(vx.label, 1);
                    } else {
                        labeling.put(vx.label, labeling.get(vx.label) + 1);
                    }
                }
            }
        }

        if (DIAGNOSTIC_ENABLED) {
            System.out.println("INIT LABELING " + labeling);
        }
    }

    @Override public MaximumFlow<V, E> buildMaximumFlow(V source, V sink)
    {
        init();

        Queue<VertexExtension> active = new ArrayDeque<VertexExtension>();

        initialize(extendedVertex(source), extendedVertex(sink), active);

        while (!active.isEmpty()) {
            VertexExtension ux = active.poll();
            for (;;) {
                for (EdgeExtension ex : ux.<EdgeExtension>getOutgoing()) {
                    if (isAdmissible(ex)) {
                        if ((ex.getTarget().prototype != sink)
                            && (ex.getTarget().prototype != source))
                        {
                            active.offer(ex.<VertexExtension>getTarget());
                        }

                        // Check whether we're rip off the excess
                        if (discharge(ex)) {
                            break;
                        }
                    }
                }

                if (ux.hasExcess()) {
                    relabel(ux);
                } else {
                    break;
                }

                // Check whether we still have any vertices with the label '1'
                if (!flowBack
                    && !labeling.containsKey(0)
                    && !labeling.containsKey(1))
                {
                    // This supposed to drastically improve performance cutting
                    // off the necessity to drive labels of all vertices up to
                    // value 'N' one-by-one not entailing eny effective
                    // discharge -- at this point there is no vertex with the
                    // label <= 1 in the network & therefore no
                    // 'discharging-path' to the _sink_ also signalling that
                    // we're in the flow-back stage of the algorithm
                    extendedVertex(source).label =
                        Collections.max(labeling.keySet()) + 1;
                    flowBack = true;
                }
            }
        }

        Map<E, Double> maxFlow = composeFlow();

        double maxFlowValue = 0.0;
        for (E e : network.incomingEdgesOf(sink)) {
            maxFlowValue += maxFlow.get(e);
        }

        if (DIAGNOSTIC_ENABLED) {
            diagnostic.dump();
        }

        return new MaximumFlowImpl<V, E>(maxFlowValue, maxFlow);
    }

    private void relabel(VertexExtension vx)
    {
        assert (vx.hasExcess());

        int min = Integer.MAX_VALUE;
        for (EdgeExtension ex : vx.<EdgeExtension>getOutgoing()) {
            if (ex.hasCapacity()) {
                VertexExtension ux = ex.getTarget();
                if (min > ux.label) {
                    min = ux.label;
                }
            }
        }

        if (DIAGNOSTIC_ENABLED) {
            diagnostic.incrementRelabels(vx.label, min + 1);
        }

        assert (labeling.get(vx.label) > 0);
        updateLabeling(vx, min + 1);

        // Sanity
        if (min != Integer.MAX_VALUE) {
            vx.label = min + 1;
        }
    }

    private void updateLabeling(VertexExtension vx, int l)
    {
        if (labeling.get(vx.label) == 1) {
            labeling.remove(vx.label);
        } else {
            labeling.put(vx.label, labeling.get(vx.label) - 1);
        }

        if (!labeling.containsKey(l)) {
            labeling.put(l, 1);
        } else {
            labeling.put(l, labeling.get(l) + 1);
        }
    }

    private boolean discharge(EdgeExtension ex)
    {
        VertexExtension ux = ex.getSource();

        if (DIAGNOSTIC_ENABLED) {
            diagnostic.incrementDischarges(ex);
        }

        pushFlowThrough(ex, Math.min(ux.excess, ex.capacity - ex.flow));
        return !ux.hasExcess();
    }

    protected void pushFlowThrough(EdgeExtension ex, double f)
    {
        ex.getSource().excess -= f;
        ex.getTarget().excess += f;

        assert ((ex.getSource().excess >= 0.0) && (ex.getTarget().excess >= 0));

        super.pushFlowThrough(ex, f);
    }

    private boolean isAdmissible(EdgeExtension e)
    {
        return e.hasCapacity()
            && (e.<VertexExtension>getSource().label
                == (e.<VertexExtension>getTarget().label + 1));
    }

    private EdgeExtension extendedEdge(E e)
    {
        return this.edgeExtended(e);
    }

    private VertexExtension extendedVertex(V v)
    {
        return this.vertexExtended(v);
    }

    private class PushRelabelDiagnostic
    {
        // Discharges
        Map<Pair<V, V>, Integer> discharges =
            new HashMap<Pair<V, V>, Integer>();
        long dischargesCounter = 0;

        // Relabels
        Map<Pair<Integer, Integer>, Integer> relabels =
            new HashMap<Pair<Integer, Integer>, Integer>();
        long relabelsCounter = 0;

        private void incrementDischarges(EdgeExtension ex)
        {
            Pair<V, V> p =
                Pair.of(ex.getSource().prototype, ex.getTarget().prototype);
            if (!discharges.containsKey(p)) {
                discharges.put(p, 0);
            }
            discharges.put(p, discharges.get(p) + 1);

            dischargesCounter++;
        }

        private void incrementRelabels(int from, int to)
        {
            Pair<Integer, Integer> p = Pair.of(from, to);
            if (!relabels.containsKey(p)) {
                relabels.put(p, 0);
            }
            relabels.put(p, relabels.get(p) + 1);

            relabelsCounter++;
        }

        void dump()
        {
            Map<Integer, Integer> labels = new HashMap<Integer, Integer>();

            for (V v : network.vertexSet()) {
                VertexExtension vx = extendedVertex(v);

                if (!labels.containsKey(vx.label)) {
                    labels.put(vx.label, 0);
                }

                labels.put(vx.label, labels.get(vx.label) + 1);
            }

            System.out.println("LABELS  ");
            System.out.println("------  ");
            System.out.println(labels);

            List<Map.Entry<Pair<Integer, Integer>, Integer>> relabelsSorted =
                new ArrayList<Map.Entry<Pair<Integer, Integer>, Integer>>(
                    relabels.entrySet());

            Collections.sort(
                relabelsSorted,
                new Comparator<Map.Entry<Pair<Integer, Integer>, Integer>>() {
                    @Override public int compare(
                        Map.Entry<Pair<Integer, Integer>, Integer> o1,
                        Map.Entry<Pair<Integer, Integer>, Integer> o2)
                    {
                        return -(o1.getValue() - o2.getValue());
                    }
                });

            System.out.println("RELABELS    ");
            System.out.println("--------    ");
            System.out.println("    Count:  " + relabelsCounter);
            System.out.println("            " + relabelsSorted);

            List<Map.Entry<Pair<V, V>, Integer>> dischargesSorted =
                new ArrayList<Map.Entry<Pair<V, V>, Integer>>(
                    discharges.entrySet());

            Collections.sort(
                dischargesSorted,
                new Comparator<Map.Entry<Pair<V, V>, Integer>>() {
                    @Override public int compare(
                        Map.Entry<Pair<V, V>, Integer> one,
                        Map.Entry<Pair<V, V>, Integer> other)
                    {
                        return -(one.getValue() - other.getValue());
                    }
                });

            System.out.println("DISCHARGES  ");
            System.out.println("----------  ");
            System.out.println("    Count:  " + dischargesCounter);
            System.out.println("            " + dischargesSorted);
        }
    }

    public class VertexExtension
        extends VertexExtensionBase
    {
        private int label;

        private boolean hasExcess()
        {
            return excess > 0;
        }

        @Override public String toString()
        {
            return prototype.toString() + String.format(" { LBL: %d } ", label);
        }
    }

    public class EdgeExtension
        extends EdgeExtensionBase
    {
        private boolean hasCapacity()
        {
            return compareFlowTo(capacity, flow) > 0;
        }

        @Override public String toString()
        {
            return prototype.toString()
                + String.format(" { F/CAP: %f / %f } ", flow, capacity);
        }
    }
}

// End PushRelabelMaximumFlow.java
