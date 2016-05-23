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
 * RandomGraphGeneratorTest.java
 * -----------------
 * (C) Copyright 2005-2008, by Assaf Lehr and Contributors.
 *
 * Original Author:  Assaf Lehr
 * Contributor(s):   -
 *
 * $Id$
 *
 * Changes
 * -------
 */
package org.jgrapht.generate;

import java.util.*;

import junit.framework.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;


/**
 * @author Assaf
 * @since Aug 6, 2005
 */
public class RandomGraphGeneratorTest
    extends TestCase
{
    //~ Methods ----------------------------------------------------------------

    public void testGenerateDirectedGraph()
    {
        List<Graph<Integer, DefaultEdge>> graphArray =
            new ArrayList<Graph<Integer, DefaultEdge>>();
        for (int i = 0; i < 3; ++i) {
            graphArray.add(
                new SimpleDirectedGraph<Integer, DefaultEdge>(
                    DefaultEdge.class));
        }

        generateGraphs(graphArray, 11, 100);

        assertTrue(
            EdgeTopologyCompare.compare(graphArray.get(0), graphArray.get(1)));
        // cannot assert false , cause it may be true once in a while (random)
        // but it generally should work.
        // assertFalse(EdgeTopologyCompare.compare(graphArray.get(1),graphArray.get(2)));
    }

    public void testGenerateListenableUndirectedGraph()
    {
        List<Graph<Integer, DefaultEdge>> graphArray =
            new ArrayList<Graph<Integer, DefaultEdge>>();
        for (int i = 0; i < 3; ++i) {
            graphArray.add(
                new ListenableUndirectedGraph<Integer, DefaultEdge>(
                    DefaultEdge.class));
        }

        generateGraphs(graphArray, 11, 50);

        assertTrue(
            EdgeTopologyCompare.compare(graphArray.get(0), graphArray.get(1)));
    }

    public void testBadVertexFactory()
    {
        RandomGraphGenerator<String, DefaultEdge> randomGen =
            new RandomGraphGenerator<String, DefaultEdge>(
                10,
                3);
        Graph<String, DefaultEdge> graph =
            new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
        try {
            randomGen.generateGraph(
                graph,
                new ClassBasedVertexFactory<String>(String.class),
                null);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    /**
     * Generates 3 graphs with the same numOfVertex and numOfEdges. The first
     * two are generated using the same RandomGraphGenerator; the third is
     * generated using a new instance.
     *
     * @param graphs array of graphs to generate
     * @param numOfVertex number of vertices to generate per graph
     * @param numOfEdges number of edges to generate per graph
     */
    private static void generateGraphs(
        List<Graph<Integer, DefaultEdge>> graphs,
        int numOfVertex,
        int numOfEdges)
    {
        RandomGraphGenerator<Integer, DefaultEdge> randomGen =
            new RandomGraphGenerator<Integer, DefaultEdge>(
                numOfVertex,
                numOfEdges);

        randomGen.generateGraph(
            graphs.get(0),
            new IntegerVertexFactory(),
            null);

        // use the same randomGen
        randomGen.generateGraph(
            graphs.get(1),
            new IntegerVertexFactory(),
            null);

        // use new randomGen here
        RandomGraphGenerator<Integer, DefaultEdge> newRandomGen =
            new RandomGraphGenerator<Integer, DefaultEdge>(
                numOfVertex,
                numOfEdges);

        newRandomGen.generateGraph(
            graphs.get(2),
            new IntegerVertexFactory(),
            null);
    }

    static class EdgeTopologyCompare
    {
        /**
         * Compare topology of the two graphs. It does not compare the contents of
         * the vertexes/edges, but only the relationships between them.
         *
         * @param g1
         * @param g2
         */
        @SuppressWarnings("unchecked")
        public static boolean compare(Graph g1, Graph g2)
        {
            boolean result = false;
            VertexOrdering lg1 = new VertexOrdering(g1);
            VertexOrdering lg2 = new VertexOrdering(g2);
            result = lg1.equalsByEdgeOrder(lg2);

            return result;
        }
    }

    static class IntegerVertexFactory
        implements VertexFactory<Integer>
    {
        private int counter;

        /**
         * Equivalent to IntegerVertexFactory(0);
         *
         * @author Assaf
         * @since Aug 6, 2005
         */
        public IntegerVertexFactory()
        {
            this(0);
        }

        public IntegerVertexFactory(int oneBeforeFirstValue)
        {
            this.counter = oneBeforeFirstValue;
        }

        @Override
        public Integer createVertex()
        {
            this.counter++;
            return new Integer(this.counter);
        }
    }

    static class VertexOrdering<V, E>
    {
        /**
         * Holds a mapping between key=V(vertex) and value=Integer(vertex order). It
         * can be used for identifying the order of regular vertex/edge.
         */
        private Map<V, Integer> mapVertexToOrder;

        /**
         * Holds a HashSet of all LabelsGraph of the graph.
         */
        private Set<LabelsEdge> labelsEdgesSet;



        /**
         * Creates a new labels graph according to the regular graph. After its
         * creation they will no longer be linked, thus changes to one will not
         * affect the other.
         *
         * @param regularGraph
         */
        public VertexOrdering(Graph<V, E> regularGraph)
        {
            this(regularGraph, regularGraph.vertexSet(), regularGraph.edgeSet());
        }

        /**
         * Creates a new labels graph according to the regular graph. After its
         * creation they will no longer be linked, thus changes to one will not
         * affect the other.
         *
         * @param regularGraph
         * @param vertexSet
         * @param edgeSet
         */
        public VertexOrdering(
            Graph<V, E> regularGraph,
            Set<V> vertexSet,
            Set<E> edgeSet)
        {
            init(regularGraph, vertexSet, edgeSet);
        }



        private void init(Graph<V, E> g, Set<V> vertexSet, Set<E> edgeSet)
        {
            // create a map between vertex value to its order(1st,2nd,etc)
            // "CAT"=1 "DOG"=2 "RHINO"=3

            this.mapVertexToOrder = new HashMap<V, Integer>(vertexSet.size());

            int counter = 0;
            for (V vertex : vertexSet) {
                mapVertexToOrder.put(vertex, new Integer(counter));
                counter++;
            }

            // create a friendlier representation of an edge
            // by order, like 2nd->3rd instead of B->A
            // use the map to convert vertex to order
            // on directed graph, edge A->B must be (A,B)
            // on undirected graph, edge A-B can be (A,B) or (B,A)

            this.labelsEdgesSet = new HashSet<LabelsEdge>(edgeSet.size());
            for (E edge : edgeSet) {
                V sourceVertex = g.getEdgeSource(edge);
                Integer sourceOrder = mapVertexToOrder.get(sourceVertex);
                int sourceLabel = sourceOrder.intValue();
                int targetLabel =
                    (mapVertexToOrder.get(g.getEdgeTarget(edge))).intValue();

                LabelsEdge lablesEdge = new LabelsEdge(sourceLabel, targetLabel);
                this.labelsEdgesSet.add(lablesEdge);

                if (g instanceof UndirectedGraph<?, ?>) {
                    LabelsEdge oppositeEdge =
                        new LabelsEdge(targetLabel, sourceLabel);
                    this.labelsEdgesSet.add(oppositeEdge);
                }
            }
        }

        /**
         * Tests equality by order of edges
         */
        public boolean equalsByEdgeOrder(VertexOrdering otherGraph)
        {
            boolean result =
                this.getLabelsEdgesSet().equals(otherGraph.getLabelsEdgesSet());

            return result;
        }

        public Set<LabelsEdge> getLabelsEdgesSet()
        {
            return labelsEdgesSet;
        }

        /**
         * This is the format example:
         *
         * <pre>
         mapVertexToOrder=        labelsOrder=
         * </pre>
         */
        @Override public String toString()
        {
            StringBuffer sb = new StringBuffer();
            sb.append("mapVertexToOrder=");

            // vertex will be printed in their order
            Object [] vertexArray = new Object[this.mapVertexToOrder.size()];
            Set<V> keySet = this.mapVertexToOrder.keySet();
            for (V currVertex : keySet) {
                Integer index = this.mapVertexToOrder.get(currVertex);
                vertexArray[index.intValue()] = currVertex;
            }
            sb.append(Arrays.toString(vertexArray));
            sb.append("labelsOrder=").append(this.labelsEdgesSet.toString());
            return sb.toString();
        }



        private class LabelsEdge
        {
            private int source;
            private int target;
            private int hashCode;

            public LabelsEdge(int aSource, int aTarget)
            {
                this.source = aSource;
                this.target = aTarget;
                this.hashCode =
                    new String(this.source + "" + this.target).hashCode();
            }

            /**
             * Checks both source and target. Does not check class type to be fast,
             * so it may throw ClassCastException. Careful!
             *
             * @see java.lang.Object#equals(java.lang.Object)
             */
            @Override public boolean equals(Object obj)
            {
                LabelsEdge otherEdge = (LabelsEdge) obj;
                if ((this.source == otherEdge.source)
                    && (this.target == otherEdge.target))
                {
                    return true;
                } else {
                    return false;
                }
            }

            /**
             * @see java.lang.Object#hashCode()
             */
            @Override public int hashCode()
            {
                return this.hashCode; // filled on constructor
            }

            @Override public String toString()
            {
                return this.source + "->" + this.target;
            }
        }
    }
}

// End RandomGraphGeneratorTest.java
