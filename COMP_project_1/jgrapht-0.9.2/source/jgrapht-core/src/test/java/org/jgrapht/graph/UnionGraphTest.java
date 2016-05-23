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
/* ------------------------
 * UnionGraphTest.java
 * ------------------------
 * (C) Copyright 2003-2008, by Joris Kinable and Contributors.
 *
 * Original Author:  Joris Kinable
 * Contributor(s):   -
 *
 * $Id$
 *
 * Changes
 * -------
 * 24-Aug-2015 : Initial revision (JK);
 *
 */
package org.jgrapht.graph;

import junit.framework.TestCase;
import org.jgrapht.*;
import org.jgrapht.util.WeightCombiner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Unit test for the {@link DirectedGraphUnion} class, {@link UndirectedGraphUnion} class, {@link MixedGraphUnion} class.
 *
 * @author Joris Kinable
 * @since Aug 24, 2015
 */
public class UnionGraphTest extends TestCase {

    //~ Instance fields --------------------------------------------------------

    private String v0 = "v0";
    private String v1 = "v1";
    private String v2 = "v2";
    private String v3 = "v3";
    private String v4 = "v4";

    private DefaultEdge e1= new DefaultEdge(); //(v0,v1);
    private DefaultEdge e2 = new DefaultEdge(); //(v1,v4);
    private DefaultEdge e3= new DefaultEdge(); //(v4,v0);
    private DefaultEdge e4= new DefaultEdge(); //(v1,v2);
    private DefaultEdge e5= new DefaultEdge(); //(v2,v3);
    private DefaultEdge e6= new DefaultEdge(); //(v3,v4);
    private DefaultEdge e7= new DefaultEdge(); //(v4,v1);

    SimpleGraph<String, DefaultEdge> undirectedGraph1;
    SimpleGraph<String, DefaultEdge> undirectedGraph2;

    DirectedGraph<String, DefaultEdge> directedGraph1;
    DirectedGraph<String, DefaultEdge> directedGraph2;


    //~ Methods ----------------------------------------------------------------

    @Override
    public void setUp(){
        undirectedGraph1=new SimpleWeightedGraph<String, DefaultEdge>(DefaultEdge.class);
        undirectedGraph2=new SimpleWeightedGraph<String, DefaultEdge>(DefaultEdge.class);
        directedGraph1=new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
        directedGraph2=new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

        Graphs.addAllVertices(undirectedGraph1, Arrays.asList(v0, v1, v4));
        Graphs.addAllVertices(undirectedGraph2, Arrays.asList(v1, v2, v3, v4));
        Graphs.addAllVertices(directedGraph1, Arrays.asList(v0, v1, v4));
        Graphs.addAllVertices(directedGraph2, Arrays.asList(v1, v2, v3, v4));

        undirectedGraph1.addEdge(v0, v1, e1);
        undirectedGraph1.addEdge(v1, v4, e2);
        undirectedGraph1.addEdge(v4, v0, e3);

        directedGraph1.addEdge(v0, v1, e1);
        directedGraph1.addEdge(v1, v4, e2);
        directedGraph1.addEdge(v4, v0, e3);

        undirectedGraph2.addEdge(v4, v1, e7);
        undirectedGraph2.addEdge(v1, v2, e4);
        undirectedGraph2.addEdge(v2, v3, e5);
        undirectedGraph2.addEdge(v3, v4, e6);

        directedGraph2.addEdge(v4, v1, e7);
        directedGraph2.addEdge(v1, v2, e4);
        directedGraph2.addEdge(v2, v3, e5);
        directedGraph2.addEdge(v3, v4, e6);
    }

    /**
     * Create and test the union of two Undirected Graphs
     */
    public void testUndirectedGraphUnion(){
        GraphUnion<String, DefaultEdge, UndirectedGraph<String, DefaultEdge>> graphUnion=new UndirectedGraphUnion<String, DefaultEdge>(undirectedGraph1, undirectedGraph2);
        assertEquals(new HashSet<String>(Arrays.asList(v0, v1, v2, v3, v4)), graphUnion.vertexSet());
        assertEquals(new HashSet<DefaultEdge>(Arrays.asList(e1, e2, e3, e4, e5, e6, e7)), graphUnion.edgeSet());
        assertEquals(new HashSet<DefaultEdge>(Arrays.asList(e2, e3, e6, e7)), graphUnion.edgesOf(v4));

        assertTrue(graphUnion.getG1()==undirectedGraph1);
        assertTrue(graphUnion.getG2()==undirectedGraph2);

        assertTrue(graphUnion.getEdge(v1, v4)==e2);
        assertTrue(graphUnion.getEdge(v4, v1)==e2);
    }

    /**
     * Create and test the union of two Directed Graphs
     */
    public void testDirectedGraphUnion(){
        DirectedGraphUnion<String, DefaultEdge> graphUnion=new DirectedGraphUnion<String, DefaultEdge>(directedGraph1, directedGraph2);
        assertEquals(new HashSet<String>(Arrays.asList(v0, v1, v2, v3, v4)), graphUnion.vertexSet());
        assertEquals(new HashSet<DefaultEdge>(Arrays.asList(e1, e2, e3, e4, e5, e6, e7)), graphUnion.edgeSet());
        assertEquals(new HashSet<DefaultEdge>(Arrays.asList(e2, e3, e6, e7)), graphUnion.edgesOf(v4));
        assertEquals(new HashSet<DefaultEdge>(Arrays.asList(e3, e7)), graphUnion.outgoingEdgesOf(v4));
        assertEquals(new HashSet<DefaultEdge>(Arrays.asList(e2, e6)), graphUnion.incomingEdgesOf(v4));

        assertTrue(graphUnion.getG1()==directedGraph1);
        assertTrue(graphUnion.getG2()==directedGraph2);
        assertTrue(graphUnion instanceof DirectedGraph);

        assertFalse(directedGraph1.containsEdge(v4,v1));
        assertFalse(directedGraph2.containsEdge(v1,v4));
        assertTrue(graphUnion.getEdge(v1, v4)==e2);
        assertTrue(graphUnion.getEdge(v4, v1)==e7);

        assertEquals(2, graphUnion.outDegreeOf(v1));
        assertEquals(2, graphUnion.outDegreeOf(v4));
        assertEquals(2, graphUnion.inDegreeOf(v1));
        assertEquals(2, graphUnion.inDegreeOf(v4));
    }

    /**
     * Create and test a Mixed-Graph, obtained by taking the union of a undirected and a directed graph
     */
    public void testMixedGraphUnion(){
        MixedGraphUnion<String, DefaultEdge> graphUnion=new MixedGraphUnion<String, DefaultEdge>(undirectedGraph1, directedGraph2);
        assertEquals(new HashSet<String>(Arrays.asList(v0, v1, v2, v3, v4)), graphUnion.vertexSet());
        assertEquals(new HashSet<DefaultEdge>(Arrays.asList(e1, e2, e3, e4, e5, e6, e7)), graphUnion.edgeSet());
        assertEquals(new HashSet<DefaultEdge>(Arrays.asList(e2, e3, e6, e7)), graphUnion.edgesOf(v4));
        assertEquals(new HashSet<DefaultEdge>(Arrays.asList(e2, e3, e7)), graphUnion.outgoingEdgesOf(v4));
        assertEquals(new HashSet<DefaultEdge>(Arrays.asList(e2, e3, e6)), graphUnion.incomingEdgesOf(v4));

        assertTrue(graphUnion.getG1()==undirectedGraph1);
        assertTrue(graphUnion.getG2()==directedGraph2);
        assertTrue(graphUnion instanceof DirectedGraph);

        assertTrue(graphUnion.containsEdge(v0, v1)); //undirected edge
        assertTrue(graphUnion.containsEdge(v1, v0)); //undirected edge
        assertTrue(graphUnion.containsEdge(v3,v4)); //directed edge
        assertFalse(graphUnion.containsEdge(v4, v3)); //directed edge

        assertEquals(3, graphUnion.outDegreeOf(v1));
        assertEquals(3, graphUnion.outDegreeOf(v4));
        assertEquals(3, graphUnion.inDegreeOf(v1));
        assertEquals(3, graphUnion.inDegreeOf(v4));

    }

    /**
     * Test the weight combiner for graphs having an edge in common.
     */
    public void testWeightCombiner(){
        //Create two graphs, both having the same vertices {0,1} and the same weighted edge (0,1)
        SimpleWeightedGraph<Integer, DefaultWeightedEdge> g1=new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        Graphs.addAllVertices(g1, Arrays.asList(0,1));
        DefaultWeightedEdge edge=g1.addEdge(0,1);
        g1.setEdgeWeight(edge, 10);

        SimpleWeightedGraph<Integer, DefaultWeightedEdge> g2=new SimpleWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        Graphs.addAllVertices(g2, Arrays.asList(0,1));
        g2.addEdge(0,1, edge);
        //We need to create a mask of the second graph if we want to store the edge with a different weight. Simply setting g2.setEdgeWeight(edge,20) would override the edge weight for the same edge in g1 as well!
        Map<DefaultWeightedEdge, Double> weightMap=new HashMap<DefaultWeightedEdge, Double>();
        weightMap.put(edge, 20.0);
        WeightedGraph<Integer, DefaultWeightedEdge> g2Masked=new AsWeightedGraph<Integer, DefaultWeightedEdge>(g2, weightMap);

        GraphUnion<Integer, DefaultWeightedEdge, WeightedGraph<Integer, DefaultWeightedEdge>> graphUnionSum=new GraphUnion<Integer, DefaultWeightedEdge, WeightedGraph<Integer, DefaultWeightedEdge>>(g1, g2Masked, WeightCombiner.SUM);
        assertEquals(30.0, graphUnionSum.getEdgeWeight(edge));
        GraphUnion<Integer, DefaultWeightedEdge, WeightedGraph<Integer, DefaultWeightedEdge>> graphUnionFirst=new GraphUnion<Integer, DefaultWeightedEdge, WeightedGraph<Integer, DefaultWeightedEdge>>(g1, g2Masked, WeightCombiner.FIRST);
        assertEquals(10.0, graphUnionFirst.getEdgeWeight(edge));
        GraphUnion<Integer, DefaultWeightedEdge, WeightedGraph<Integer, DefaultWeightedEdge>> graphUnionSecond=new GraphUnion<Integer, DefaultWeightedEdge, WeightedGraph<Integer, DefaultWeightedEdge>>(g1, g2Masked, WeightCombiner.SECOND);
        assertEquals(20.0, graphUnionSecond.getEdgeWeight(edge));
        GraphUnion<Integer, DefaultWeightedEdge, WeightedGraph<Integer, DefaultWeightedEdge>> graphUnionMax=new GraphUnion<Integer, DefaultWeightedEdge, WeightedGraph<Integer, DefaultWeightedEdge>>(g1, g2Masked, WeightCombiner.MAX);
        assertEquals(20.0, graphUnionMax.getEdgeWeight(edge));
        GraphUnion<Integer, DefaultWeightedEdge, WeightedGraph<Integer, DefaultWeightedEdge>> graphUnionMin=new GraphUnion<Integer, DefaultWeightedEdge, WeightedGraph<Integer, DefaultWeightedEdge>>(g1, g2Masked, WeightCombiner.MIN);
        assertEquals(10.0, graphUnionMin.getEdgeWeight(edge));
        GraphUnion<Integer, DefaultWeightedEdge, WeightedGraph<Integer, DefaultWeightedEdge>> graphUnionMult=new GraphUnion<Integer, DefaultWeightedEdge, WeightedGraph<Integer, DefaultWeightedEdge>>(g1, g2Masked, WeightCombiner.MULT);
        assertEquals(200.0, graphUnionMult.getEdgeWeight(edge));

        assertEquals(10.0, g1.getEdgeWeight(edge));
        assertEquals(10.0, g2.getEdgeWeight(edge));
        assertEquals(20.0, g2Masked.getEdgeWeight(edge));
    }

}
