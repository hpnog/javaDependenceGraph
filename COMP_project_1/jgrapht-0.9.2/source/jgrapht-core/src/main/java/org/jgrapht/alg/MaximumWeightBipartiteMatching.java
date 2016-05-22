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
 * MaximumWeightBipartiteMatching.java
 * -------------------------
 * (C) Copyright 2015, by Graeme Ahokas and Contributors.
 *
 * Original Author:  Graeme Ahokas
 * Contributor(s):
 *
 * Changes
 * -------
 * 30-Sep-2015 : Initial revision (GA);
 *
 */
package org.jgrapht.alg;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.*;


/**
 * This class finds a maximum weight matching of a simple undirected weighted
 * bipartite graph. The algorithm runs in O(V|E|^2). The algorithm is described
 * in The LEDA Platform of Combinatorial and Geometric Computing, Cambridge
 * University Press, 1999. https://people.mpi-inf.mpg.de/~mehlhorn/LEDAbook.html
 * Note: the input graph must be bipartite with positive integer edge weights
 *
 * @author Graeme Ahokas
 */
public class MaximumWeightBipartiteMatching<V, E>
    implements WeightedMatchingAlgorithm<V, E>
{
    private final WeightedGraph<V, E> graph;
    private final Set<V> partition1;
    private final Set<V> partition2;

    private Map<V, Long> vertexWeights;
    private Map<V, Boolean> hasVertexBeenProcessed;
    private Map<E, Boolean> isEdgeMatched;

    private Set<E> bipartiteMatching;

    /**
     * Creates a new MaximumWeightBipartiteMatching algorithm instance. The
     * union of vertexPartition1 and vertexParition2 should be equal to the
     * vertex set of the graph Every edge in the graph must connect a vertex in
     * vertexPartition1 with a vertex in vertexPartition2
     *
     * @param graph simple undirected weighted bipartite graph to find matching
     * in, with positive integer edge weights
     * @param vertexPartition1 first vertex partition of the bipartite graph,
     * disjoint from vertexPartition2
     * @param vertexPartition2 second vertex partition of the bipartite graph,
     * disjoint from vertexPartition1
     */
    public MaximumWeightBipartiteMatching(
        final WeightedGraph<V, E> graph,
        Set<V> vertexPartition1,
        Set<V> vertexPartition2)
    {
        this.graph = graph;
        partition1 = vertexPartition1;
        partition2 = vertexPartition2;

        vertexWeights = new HashMap<V, Long>();
        hasVertexBeenProcessed = new HashMap<V, Boolean>();
        isEdgeMatched = new HashMap<E, Boolean>();

        initializeVerticesAndEdges();
    }

    @Override public Set<E> getMatching()
    {
        if (bipartiteMatching == null) {
            bipartiteMatching = maximumWeightBipartiteMatching();
        }
        return bipartiteMatching;
    }

    @Override public double getMatchingWeight()
    {
        if (bipartiteMatching == null) {
            getMatching();
        }

        long weight = 0;
        for (E edge : bipartiteMatching) {
            weight += graph.getEdgeWeight(edge);
        }

        return weight;
    }

    private void initializeVerticesAndEdges()
    {
        for (V vertex : graph.vertexSet()) {
            if (isTargetVertex(vertex)) {
                hasVertexBeenProcessed.put(vertex, true);
                setVertexWeight(vertex, (long) 0);
            } else {
                hasVertexBeenProcessed.put(vertex, false);
                setVertexWeight(
                    vertex,
                    (long) maximumWeightOfEdgeIncidentToVertex(vertex));
            }
        }

        for (E edge : graph.edgeSet()) {
            isEdgeMatched.put(edge, false);
        }
    }

    private long maximumWeightOfEdgeIncidentToVertex(V vertex)
    {
        long maxWeight = 0;
        for (E edge : graph.edgesOf(vertex)) {
            if (graph.getEdgeWeight(edge) > maxWeight) {
                maxWeight = (long) graph.getEdgeWeight(edge);
            }
        }
        return maxWeight;
    }

    private boolean isSourceVertex(V vertex)
    {
        return partition1.contains(vertex);
    }

    private boolean isTargetVertex(V vertex)
    {
        return partition2.contains(vertex);
    }

    private long vertexWeight(V vertex)
    {
        return vertexWeights.get(vertex);
    }

    private void setVertexWeight(V vertex, Long weight)
    {
        vertexWeights.put(vertex, weight);
    }

    private long reducedWeight(E edge)
    {
        return (long) (vertexWeight(graph.getEdgeSource(edge))
            + vertexWeight(graph.getEdgeTarget(edge))
            - graph.getEdgeWeight(edge));
    }

    private boolean isVertexMatched(V vertex, Set<E> matchings)
    {
        for (E edge : matchings) {
            if (graph.getEdgeSource(edge).equals(vertex)
                || graph.getEdgeTarget(edge).equals(vertex))
            {
                return true;
            }
        }
        return false;
    }

    private void addPathToMatchings(List<E> path, Set<E> matchings)
    {
        for (int i = 0; i < path.size(); i++) {
            E edge = path.get(i);
            if ((i % 2) == 0) {
                isEdgeMatched.put(edge, true);
                matchings.add(edge);
            } else {
                isEdgeMatched.put(edge, false);
                matchings.remove(edge);
            }
        }
    }

    private void adjustVertexWeights(Map<V, List<E>> reachableVertices)
    {
        long alpha = Long.MAX_VALUE;
        for (V vertex : reachableVertices.keySet()) {
            if (isSourceVertex(vertex) && (vertexWeights.get(vertex) < alpha)) {
                alpha = vertexWeights.get(vertex);
            }
        }

        long beta = Long.MAX_VALUE;
        for (V vertex : reachableVertices.keySet()) {
            if (isTargetVertex(vertex)) {
                continue;
            }
            for (E edge : graph.edgesOf(vertex)) {
                if (hasVertexBeenProcessed.get(
                        Graphs.getOppositeVertex(graph, edge, vertex))
                    && !reachableVertices.keySet().contains(
                        Graphs.getOppositeVertex(graph, edge, vertex))
                    && (reducedWeight(edge) < beta))
                {
                    beta = reducedWeight(edge);
                }
            }
        }

        assert ((alpha > 0) && (beta > 0));

        long minValue = Math.min(alpha, beta);

        for (V vertex : reachableVertices.keySet()) {
            if (isSourceVertex(vertex)) {
                vertexWeights.put(vertex, vertexWeights.get(vertex) - minValue);
            } else {
                vertexWeights.put(vertex, vertexWeights.get(vertex) + minValue);
            }
        }
    }

    private Map<V, List<E>> verticesReachableByTightAlternatingEdgesFromVertex(
        V vertex)
    {
        Map<V, List<E>> pathsToVertices = new HashMap<V, List<E>>();
        pathsToVertices.put(vertex, new ArrayList<E>());
        findPathsToVerticesFromVertices(
            Arrays.asList(vertex),
            false,
            pathsToVertices);
        return pathsToVertices;
    }

    private void findPathsToVerticesFromVertices(
        List<V> verticesToProcess,
        boolean needMatchedEdge,
        Map<V, List<E>> pathsToVertices)
    {
        if (verticesToProcess.size() == 0) {
            return;
        }
        List<V> nextVerticesToProcess = new ArrayList<V>();
        for (V vertex : verticesToProcess) {
            for (E edge : graph.edgesOf(vertex)) {
                V adjacentVertex =
                    Graphs.getOppositeVertex(graph, edge, vertex);
                if (hasVertexBeenProcessed.get(adjacentVertex)
                    && (reducedWeight(edge) == 0)
                    && !pathsToVertices.keySet().contains(adjacentVertex))
                {
                    if ((needMatchedEdge && isEdgeMatched.get(edge))
                        || (!needMatchedEdge && !isEdgeMatched.get(edge)))
                    {
                        nextVerticesToProcess.add(adjacentVertex);
                        List<E> pathToAdjacentVertex =
                            new ArrayList<E>(pathsToVertices.get(vertex));
                        pathToAdjacentVertex.add(edge);
                        pathsToVertices.put(
                            adjacentVertex,
                            pathToAdjacentVertex);
                    }
                }
            }
        }
        findPathsToVerticesFromVertices(
            nextVerticesToProcess,
            !needMatchedEdge,
            pathsToVertices);
    }

    private Set<E> maximumWeightBipartiteMatching()
    {
        Set<E> matchings = new HashSet<E>();
        for (V vertex : partition1) {
            hasVertexBeenProcessed.put(vertex, true);
            while (true) {
                Map<V, List<E>> reachableVertices =
                    verticesReachableByTightAlternatingEdgesFromVertex(vertex);
                boolean successful = false;
                for (V reachableVertex : reachableVertices.keySet()) {
                    if (isSourceVertex(reachableVertex)
                        && (vertexWeight(reachableVertex) == 0))
                    {
                        addPathToMatchings(
                            reachableVertices.get(reachableVertex),
                            matchings);
                        successful = true;
                        break;
                    }
                    if (isTargetVertex(reachableVertex)
                        && !isVertexMatched(reachableVertex, matchings))
                    {
                        addPathToMatchings(
                            reachableVertices.get(reachableVertex),
                            matchings);
                        successful = true;
                        break;
                    }
                }
                if (successful) {
                    break;
                }
                adjustVertexWeights(reachableVertices);
            }
        }
        return matchings;
    }
}

// End MaximumWeightBipartiteMatching.java
