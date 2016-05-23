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
/* --------------------------
 * AsWeightedGraphTest.java
 * --------------------------
 * (C) Copyright 2007, by Lucas J. Scharenbroich and Contributors.
 *
 * Original Author:  Lucas J. Scharenbroich
 * Contributor(s):   John V. Sichi, Joris Kinable
 *
 * $Id$
 *
 * Changes
 * -------
 * 22-Sep-2007 : Initial revision (JVS);
 * 24-Aug-2015: Added tests for AsWeightedDirectedGraphTest
 *
 */
package org.jgrapht.graph;

import java.util.*;

import org.jgrapht.*;


/**
 * A unit test for the AsWeightedGraph view and the AsDirectedWeightedGraph view.
 *
 * @author Lucas J. Scharenbroich
 * @author Joris Kinable
 */
public class AsWeightedGraphTest
    extends EnhancedTestCase
{
    //~ Instance fields --------------------------------------------------------

    public WeightedGraph<String, DefaultWeightedEdge> weightedGraph;
    public Graph<String, DefaultEdge> unweightedGraph;

    public SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> directedWeightedGraph;
    public DirectedGraph<String, DefaultEdge> directedUnweightedGraph;

    //~ Methods ----------------------------------------------------------------

    @Override
    public void setUp()
    {
        // Create a weighted, undirected graph
        weightedGraph = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        this.createdWeightedGraph(weightedGraph);

        //Create an undirected graph without weights
        unweightedGraph = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
        this.createdUnweightedGraph(unweightedGraph);

        //Create a weighted, directed graph
        directedWeightedGraph=new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        this.createdWeightedGraph(directedWeightedGraph);

        //Create a directed graph without weights
        directedUnweightedGraph =new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
        this.createdUnweightedGraph(directedUnweightedGraph);
    }

    private void createdWeightedGraph(WeightedGraph<String, DefaultWeightedEdge> graph){
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");

        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 1.);
        graph.setEdgeWeight(graph.addEdge("v2", "v3"), 2.);
        graph.setEdgeWeight(graph.addEdge("v3", "v1"), 3.);
    }

    private void createdUnweightedGraph(Graph<String, DefaultEdge> graph){
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");

        graph.addEdge("v1", "v2");
        graph.addEdge("v2", "v3");
        graph.addEdge("v3", "v1");
    }

    /*** Unweighted graphs ***/

    public void testUnweightedGraphs(){
        this.testUnweightedGraph(unweightedGraph);
        this.testUnweightedGraph(directedUnweightedGraph);
    }

    public void testUnweightedGraph(Graph<String, DefaultEdge> graph){
        DefaultEdge e1 = graph.getEdge("v1", "v2");
        DefaultEdge e2 = graph.getEdge("v2", "v3");
        DefaultEdge e3 = graph.getEdge("v3", "v1");

        Map<DefaultEdge, Double> weightMap = new HashMap<DefaultEdge, Double>();
        weightMap.put(e1, 9.0);

        assertEquals( graph.getEdgeWeight(e1), WeightedGraph.DEFAULT_EDGE_WEIGHT);

        WeightedGraph<String, DefaultEdge> graphView;
        if(graph instanceof DirectedGraph)
            graphView= new AsWeightedDirectedGraph<String, DefaultEdge>((DirectedGraph<String, DefaultEdge>)graph, weightMap);
        else
            graphView=new AsWeightedGraph<String, DefaultEdge>( graph, weightMap);

        assertEquals(graphView.getEdgeWeight(e1), 9.0);
        assertEquals(graphView.getEdgeWeight(e2), WeightedGraph.DEFAULT_EDGE_WEIGHT);
        assertEquals(graphView.getEdgeWeight(e3), WeightedGraph.DEFAULT_EDGE_WEIGHT);

        graphView.setEdgeWeight(e2, 5.0);
        assertEquals(graphView.getEdgeWeight(e2), 5.0);
        assertEquals(graph.getEdgeWeight(e2), WeightedGraph.DEFAULT_EDGE_WEIGHT);
    }

    /*** Weighted graphs ***/

    public void testWeightedGraphs(){
        this.testWeightedGraph(weightedGraph);
        this.testWeightedGraph(directedWeightedGraph);
    }

    public void testWeightedGraph(Graph<String, DefaultWeightedEdge> graph){
        DefaultWeightedEdge e1 = graph.getEdge("v1", "v2");
        DefaultWeightedEdge e2 = graph.getEdge("v2", "v3");
        DefaultWeightedEdge e3 = graph.getEdge("v3", "v1");

        Map<DefaultWeightedEdge, Double> weightMap = new HashMap<DefaultWeightedEdge, Double>();
        weightMap.put(e1, 9.0);
        weightMap.put(e3, 8.0);

        WeightedGraph<String, DefaultWeightedEdge> graphView;
        if(graph instanceof DirectedGraph)
            graphView= new AsWeightedDirectedGraph<String, DefaultWeightedEdge>((DirectedGraph<String, DefaultWeightedEdge>)graph, weightMap);
        else
            graphView=new AsWeightedGraph<String, DefaultWeightedEdge>(graph,weightMap);

        assertEquals(graphView.getEdgeWeight(e1), 9.0);
        assertEquals(graphView.getEdgeWeight(e2), 2.0);
        assertEquals(graphView.getEdgeWeight(e3), 8.0);

        graphView.setEdgeWeight(e2, 5.0);
        assertEquals(graphView.getEdgeWeight(e2), 5.0);
        assertEquals(graph.getEdgeWeight(e2), 5.0);

        try {
            double d = graphView.getEdgeWeight(null);
            // should not get here
            assertFalse();
        } catch (NullPointerException ex) {
            // expected, swallow
        }
    }
}

// End AsWeightedGraphTest.java
