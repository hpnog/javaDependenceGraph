/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2012, by Barak Naveh and Contributors.
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
 * AStarShortestPathTest.java
 * -------------------------
 * (C) Copyright 2015-2015, by Joris Kinable, Jon Robison, Thomas Breitbart and Contributors.
 *
 * Original Author:  Joris Kinable
 * Contributor(s):
 *
 * Changes
 * -------
 * Aug-2015 : Initial version;
 *
 */
package org.jgrapht.alg;

import junit.framework.TestCase;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.WeightedMultigraph;

/**
 * .Test class for AStarShortestPath implementation
 *
 * @author Joris Kinable
 * @since Aug 21, 2015
 */
public class AStarShortestPathTest extends TestCase{
    private final String[] labyrinth1={
                ". . . . . . . . . . . . . . . . . . . . . ####. . . . . . .",
                ". . . . . . . . . . . . . . . . . . . . . ####. . . . . . .",
                ". . . . . . . . . . . . . . . . . . . . . ####. . . . . . .",
                ". . . ####. . . . . . . . . . . . . . . . ####. . . . . . .",
                ". . . ####. . . . . . . . ####. . . . . . ####T . . . . . .",
                ". . . ####. . . . . . . . ####. . . . . . ##########. . . .",
                ". . . ####. . . . . . . . ####. . . . . . ##########. . . .",
                ". . . ####. . . . . . . . ####. . . . . . . . . . . . . . .",
                ". . . ####. . . . . . . . ####. . . . . . . . . . . . . . .",
                ". . . ####. . . . . . . . ####. . . . . . . . . . . . . . .",
                ". . . ####. . . . . . . . ####. . . . . . . . . . . . . . .",
                ". . . ####. . . . . . . . ####. . . . . . . . . . . . . . .",
                ". . . . . . . . . . . . . ####. . . . . . . . . . . . . . .",
                ". . . . . . . . . . . . . ####. . . . . . . . . . . . . . .",
                "S . . . . . . . . . . . . ####. . . . . . . . . . . . . . ."
    };

    private final String[] labyrinth2={ //Target node is unreachable
            ". . . . . . . . . . . . . . . . . . . . . ####. . . . . . .",
            ". . . . . . . . . . . . . . . . . . . . . ####. . . . . . .",
            ". . . . . . . . . . . . . . . . . . . . . ####. . . . . . .",
            ". . . ####. . . . . . . . . . . . . . . . ####### . . . . .",
            ". . . ####. . . . . . . . ####. . . . . . ####T## . . . . .",
            ". . . ####. . . . . . . . ####. . . . . . ##########. . . .",
            ". . . ####. . . . . . . . ####. . . . . . ##########. . . .",
            ". . . ####. . . . . . . . ####. . . . . . . . . . . . . . .",
            ". . . ####. . . . . . . . ####. . . . . . . . . . . . . . .",
            ". . . ####. . . . . . . . ####. . . . . . . . . . . . . . .",
            ". . . ####. . . . . . . . ####. . . . . . . . . . . . . . .",
            ". . . ####. . . . . . . . ####. . . . . . . . . . . . . . .",
            ". . . . . . . . . . . . . ####. . . . . . . . . . . . . . .",
            ". . . . . . . . . . . . . ####. . . . . . . . . . . . . . .",
            "S . . . . . . . . . . . . ####. . . . . . . . . . . . . . ."
    };

    private WeightedGraph<Node, DefaultWeightedEdge> graph;
    private Node sourceNode;
    private Node targetNode;

    private void readLabyrinth(String[] labyrinth){
        graph=new SimpleWeightedGraph<Node, DefaultWeightedEdge>(DefaultWeightedEdge.class);

        //Create the nodes
        Node[][] nodes=new Node[labyrinth.length][labyrinth[0].length()];
        for(int i=0; i<labyrinth.length; i++){
            for(int j=0; j<labyrinth[0].length(); j++){
                if(labyrinth[i].charAt(j)=='#' || labyrinth[i].charAt(j)==' ')
                    continue;
                nodes[i][j]=new Node(i,j);
                graph.addVertex(nodes[i][j]);
                if(labyrinth[i].charAt(j)=='S')
                    sourceNode=nodes[i][j];
                else if(labyrinth[i].charAt(j)=='T')
                    targetNode=nodes[i][j];
            }
        }
        //Create the edges
        //a. Horizontal edges
        for(int i=0; i<labyrinth.length; i++) {
            for (int j = 0; j < labyrinth[0].length()-2; j++) {
                if(nodes[i][j] == null || nodes[i][j+2]==null)
                    continue;
                Graphs.addEdge(graph, nodes[i][j], nodes[i][j + 2], 1);
            }
        }
        //b. Vertical edges
        for(int i=0; i<labyrinth.length-1; i++) {
            for (int j = 0; j < labyrinth[0].length(); j++) {
                if(nodes[i][j] == null || nodes[i+1][j]==null)
                    continue;
                Graphs.addEdge(graph, nodes[i][j], nodes[i+1][j], 1);
            }
        }
    }

    /**
     * Test on a graph with a path from the source node to the target node.
     */
    public void testLabyrinth1(){
        this.readLabyrinth(labyrinth1);
        AStarShortestPath<Node, DefaultWeightedEdge> aStarShortestPath=new AStarShortestPath<Node, DefaultWeightedEdge>(graph);
        GraphPath<Node, DefaultWeightedEdge> path=aStarShortestPath.getShortestPath(sourceNode, targetNode, new ManhattanDistance());
        assertNotNull(path);
        assertEquals((int)path.getWeight(), 47);
        assertEquals(path.getEdgeList().size(), 47);
        assertEquals(Graphs.getPathVertexList(path).size(), 48);

        path=aStarShortestPath.getShortestPath(sourceNode, targetNode, new EuclideanDistance());
        assertNotNull(path);
        assertEquals((int)path.getWeight(), 47);
        assertEquals(path.getEdgeList().size(), 47);
    }

    /**
     * Test on a graph where there is no path from the source node to the target node.
     */
    public void testLabyrinth2(){
        this.readLabyrinth(labyrinth2);
        AStarShortestPath<Node, DefaultWeightedEdge> aStarShortestPath=new AStarShortestPath<Node, DefaultWeightedEdge>(graph);
        GraphPath<Node, DefaultWeightedEdge> path=aStarShortestPath.getShortestPath(sourceNode, targetNode, new ManhattanDistance());
        assertNull(path);
    }

    /**
     * This test verifies whether multigraphs are processed correctly. In a multigraph, there are multiple edges between the same vertex pair.
     * Each of these edges can have a different cost. Here we create a simple multigraph A-B-C with multiple edges between (A,B) and (B,C) and
     * query the shortest path, which is simply the cheapest edge between (A,B) plus the cheapest edge between (B,C). The admissible heuristic
     * in this test is not important.
     */
    public void testMultiGraph(){
        WeightedMultigraph<Node, DefaultWeightedEdge> multigraph=new WeightedMultigraph<Node, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        Node n1=new Node(0,0);
        multigraph.addVertex(n1);
        Node n2=new Node(1,0);
        multigraph.addVertex(n2);
        Node n3=new Node(2,0);
        multigraph.addVertex(n3);
        Graphs.addEdge(multigraph,n1,n2, 5.0);
        Graphs.addEdge(multigraph,n1,n2, 4.0);
        Graphs.addEdge(multigraph,n1,n2, 8.0);
        Graphs.addEdge(multigraph,n2,n3, 7.0);
        Graphs.addEdge(multigraph,n2,n3, 9);
        Graphs.addEdge(multigraph,n2,n3, 2);
        AStarShortestPath<Node, DefaultWeightedEdge> aStarShortestPath=new AStarShortestPath<Node, DefaultWeightedEdge>(multigraph);
        GraphPath<Node, DefaultWeightedEdge> path=aStarShortestPath.getShortestPath(n1, n3, new ManhattanDistance());
        assertNotNull(path);
        assertEquals((int)path.getWeight(), 6);
        assertEquals(path.getEdgeList().size(), 2);
    }

    private class ManhattanDistance implements AStarAdmissibleHeuristic<Node> {
        @Override
        public double getCostEstimate(Node sourceVertex, Node targetVertex) {
            return Math.abs(sourceVertex.x- targetVertex.x)+Math.abs(sourceVertex.y- targetVertex.y);
        }
    }

    private class EuclideanDistance implements AStarAdmissibleHeuristic<Node> {
        @Override
        public double getCostEstimate(Node sourceVertex, Node targetVertex) {
            return Math.sqrt(Math.pow(sourceVertex.x- targetVertex.x,2)+Math.pow(sourceVertex.y- targetVertex.y,2));
        }
    }

    private class Node{
        public final int x;
        public final int y;

        private Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public String toString(){
            return "("+x+","+y+")";
        }
    }
}
