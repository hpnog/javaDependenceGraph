/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2009, by Barak Naveh and Contributors.
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
 * FloydWarshallShortestPaths.java
 * -------------------------
 * (C) Copyright 2009-2009, by Tom Larkworthy and Contributors
 *
 * Original Author:  Tom Larkworthy
 * Contributor(s):   Soren Davidsen, Joris Kinable
 *
 * $Id: FloydWarshallShortestPaths.java 755 2012-01-18 23:50:37Z perfecthash $
 *
 * Changes
 * -------
 * 29-Jun-2009 : Initial revision (TL);
 * 03-Dec-2009 : Optimized and enhanced version (SD);
 * Aug 2015: Algorithm now works with Mixed-Graphs. Included some performance tweaks.
 *
 */
package org.jgrapht.alg;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;


/**
 * The <a href="http://en.wikipedia.org/wiki/Floyd-Warshall_algorithm">
 * Floyd-Warshall algorithm</a> finds all shortest paths (all n^2 of them) in
 * O(n^3) time. It can also calculate the graph diameter. Note that during
 * construction time, no computations are performed! All computations are
 * performed the first time one of the member methods of this class is invoked.
 * The results are stored, so all subsequent calls to the same method are
 * computationally efficient. Warning: This code has not been tested (and
 * probably doesn't work) on multi-graphs. Code should be updated to work
 * properly on multi-graphs.
 *
 * @author Tom Larkworthy
 * @author Soren Davidsen (soren@tanesha.net)
 * @author Joris Kinable
 */
public class FloydWarshallShortestPaths<V, E>
{
    private final Graph<V, E> graph;
    private final List<V> vertices;
    private final Map<V, Integer> vertexIndices;

    private int nShortestPaths = 0;
    private double diameter = Double.NaN;
    private double [][] d = null;
    private int [][] backtrace = null;
    private Map<V, List<GraphPath<V, E>>> paths = null;

    public FloydWarshallShortestPaths(Graph<V, E> graph)
    {
        this.graph = graph;
        this.vertices = new ArrayList<V>(graph.vertexSet());
        this.vertexIndices = new HashMap<V, Integer>(this.vertices.size());
        int i = 0;
        for (V vertex : vertices) {
            vertexIndices.put(vertex, i++);
        }
    }

    /**
     * @return the graph on which this algorithm operates
     */
    public Graph<V, E> getGraph()
    {
        return graph;
    }

    /**
     * @return total number of shortest paths
     */
    public int getShortestPathsCount()
    {
        lazyCalculatePaths();
        return nShortestPaths;
    }

    /**
     * Calculates the matrix of all shortest paths, but does not populate the
     * paths map.
     */
    private void lazyCalculateMatrix()
    {
        if (d != null) {
            // already done
            return;
        }

        int n = vertices.size();

        // init the backtrace matrix
        backtrace = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(backtrace[i], -1);
        }

        // initialize matrix, 0
        d = new double[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(d[i], Double.POSITIVE_INFINITY);
        }

        // initialize matrix, 1
        for (int i = 0; i < n; i++) {
            d[i][i] = 0.0;
        }

        // initialize matrix, 2
        if (graph instanceof UndirectedGraph<?, ?>) {
            for (E edge : graph.edgeSet()) {
                int v_1 = vertexIndices.get(graph.getEdgeSource(edge));
                int v_2 = vertexIndices.get(graph.getEdgeTarget(edge));
                d[v_1][v_2] = d[v_2][v_1] = graph.getEdgeWeight(edge);
                backtrace[v_1][v_2] = v_2;
                backtrace[v_2][v_1] = v_1;
            }
        } else { //This works for both Directed and Mixed graphs! Iterating over
                 //the arcs and querying source/sink does not suffice for graphs
                 //which contain both edges and arcs
            DirectedGraph<V, E> directedGraph = (DirectedGraph<V, E>) graph;
            for (V v1 : directedGraph.vertexSet()) {
                int v_1 = vertexIndices.get(v1);
                for (V v2 : Graphs.successorListOf(directedGraph, v1)) {
                    int v_2 = vertexIndices.get(v2);
                    d[v_1][v_2] =
                        directedGraph.getEdgeWeight(
                            directedGraph.getEdge(v1, v2));
                    backtrace[v_1][v_2] = v_2;
                }
            }
        }

        // run fw alg
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    double ik_kj = d[i][k] + d[k][j];
                    if (ik_kj < d[i][j]) {
                        d[i][j] = ik_kj;
                        backtrace[i][j] = backtrace[i][k];
                    }
                }
            }
        }
    }

    /**
     * Get the length of a shortest path.
     *
     * @param a first vertex
     * @param b second vertex
     *
     * @return shortest distance between a and b
     */
    public double shortestDistance(V a, V b)
    {
        lazyCalculateMatrix();

        return d[vertexIndices.get(a)][vertexIndices.get(b)];
    }

    /**
     * @return the diameter (longest of all the shortest paths) computed for the
     * graph. If the graph is vertexless, return 0.0.
     */
    public double getDiameter()
    {
        lazyCalculateMatrix();

        if (Double.isNaN(diameter)) {
            diameter = 0.0;
            int n = vertices.size();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (!Double.isInfinite(d[i][j]) && (d[i][j] > diameter)) {
                        diameter = d[i][j];
                    }
                }
            }
        }
        return diameter;
    }

    /**
     * Get the shortest path between two vertices.
     *
     * @param a From vertice
     * @param b To vertice
     *
     * @return the path, or null if none found
     */
    public GraphPath<V, E> getShortestPath(V a, V b)
    {
        lazyCalculateMatrix();

        int v_a = vertexIndices.get(a);
        int v_b = vertexIndices.get(b);

        if (backtrace[v_a][v_b] == -1) { //No path exists
            return null;
        }

        //Reconstruct the path
        List<E> edges = new ArrayList<E>();
        int u = v_a;
        while (u != v_b) {
            int v = backtrace[u][v_b];
            edges.add(graph.getEdge(vertices.get(u), vertices.get(v)));
            u = v;
        }
        return new GraphPathImpl<V, E>(graph, a, b, edges, d[v_a][v_b]);
    }

    public List<V> getShortestPathAsVertexList(V a, V b)
    {
        lazyCalculateMatrix();

        int v_a = vertexIndices.get(a);
        int v_b = vertexIndices.get(b);

        if (backtrace[v_a][v_b] == -1) { //No path exists
            return null;
        }

        //Reconstruct the path
        List<V> pathVertexList = new ArrayList<V>();
        pathVertexList.add(a);
        int u = v_a;
        while (u != v_b) {
            int v = backtrace[u][v_b];
            pathVertexList.add(vertices.get(v));
            u = v;
        }
        return pathVertexList;
    }

    /**
     * Get shortest paths from a vertex to all other vertices in the graph.
     *
     * @param v the originating vertex
     *
     * @return List of paths
     */
    public List<GraphPath<V, E>> getShortestPaths(V v)
    {
        lazyCalculatePaths();
        return Collections.unmodifiableList(paths.get(v));
    }

    /**
     * Get all shortest paths in the graph.
     *
     * @return List of paths
     */
    public List<GraphPath<V, E>> getShortestPaths()
    {
        lazyCalculatePaths();
        List<GraphPath<V, E>> allPaths = new ArrayList<GraphPath<V, E>>();
        for (List<GraphPath<V, E>> pathSubset : paths.values()) {
            allPaths.addAll(pathSubset);
        }

        return allPaths;
    }

    /**
     * Calculate the shortest paths (not done per default) TODO: This method can
     * be optimized. Instead of calculating each path individidually, use a
     * constructive method. TODO: I.e. if we have a shortest path from i to j:
     * [i,....j] and we know that the shortest path from j to k, we can simply
     * glue the paths together to obtain the shortest path from i to k
     */
    private void lazyCalculatePaths()
    {
        // already we have calculated it once.
        if (paths != null) {
            return;
        }

        lazyCalculateMatrix();

        paths = new LinkedHashMap<V, List<GraphPath<V, E>>>();
        int n = vertices.size();
        for (int i = 0; i < n; i++) {
            V v_i = vertices.get(i);
            paths.put(v_i, new ArrayList<GraphPath<V, E>>());
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    continue;
                }

                V v_j = vertices.get(j);

                GraphPath<V, E> path = getShortestPath(v_i, v_j);

                if (path != null) {
                    paths.get(v_i).add(path);
                    nShortestPaths++;
                }
            }
        }
    }
}

// End FloydWarshallShortestPaths.java
