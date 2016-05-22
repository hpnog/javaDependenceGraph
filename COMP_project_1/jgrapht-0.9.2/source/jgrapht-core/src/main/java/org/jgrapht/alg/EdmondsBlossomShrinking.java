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
 * EdmondsBlossomShrinking.java
 * -------------------------
 * (C) Copyright 2012-2012, by Alejandro Ramon Lopez del Huerto and Contributors.
 *
 * Original Author:  Alejandro Ramon Lopez del Huerto
 * Contributor(s):
 *
 * Changes
 * -------
 * 24-Jan-2012 : Initial revision (ARLH);
 *
 */
package org.jgrapht.alg;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.util.*;


/**
 * An implementation of Edmonds Blossom Shrinking algorithm for constructing
 * maximum matchings on graphs. The algorithm runs in time O(V^4).
 *
 * @author Alejandro R. Lopez del Huerto
 * @since Jan 24, 2012
 */
public class EdmondsBlossomShrinking<V, E>
    implements MatchingAlgorithm<V, E>
{
    // ~ Instance fields
    // --------------------------------------------------------

    private UndirectedGraph<V, E> graph;

    private Set<E> matching;

    private Map<V, V> match;
    private Map<V, V> path;
    private Map<V, V> contracted;

    // ~ Constructors
    // ----------------------------------------------------------------

    @Deprecated public EdmondsBlossomShrinking()
    {
    }

    public EdmondsBlossomShrinking(final UndirectedGraph<V, E> G)
    {
        this.graph = G;
    }

    // ~ Deprecated Methods
    // ----------------------------------------------------------------

    /**
     * See `getMatching` as preferred alternative to this one
     */
    @Deprecated public Set<E> findMatch(final UndirectedGraph<V, E> g)
    {
        return new EdmondsBlossomShrinking<V, E>(g).getMatching();
    }

    // ~ Methods
    // ----------------------------------------------------------------

    @Override public Set<E> getMatching()
    {
        if (matching == null) {
            matching = findMatch();
        }
        return Collections.unmodifiableSet(matching);
    }

    /**
     * Runs the algorithm on the input graph and returns the match edge set.
     *
     * @return set of Edges
     */
    private Set<E> findMatch()
    {
        Set<E> result = new ArrayUnenforcedSet<E>();
        match = new HashMap<V, V>();
        path = new HashMap<V, V>();
        contracted = new HashMap<V, V>();

        for (V i : graph.vertexSet()) {
            // Any augmenting path should start with _exposed_ vertex
            // (vertex may not escape match-set being added once)
            if (!match.containsKey(i)) {
                // Match is maximal iff graph G contains no more augmenting
                // paths
                V v = findPath(i);
                while (v != null) {
                    V pv = path.get(v);
                    V ppv = match.get(pv);
                    match.put(v, pv);
                    match.put(pv, v);
                    v = ppv;
                }
            }
        }

        Set<V> seen = new HashSet<V>();
        for (V v : graph.vertexSet()) {
            if (!seen.contains(v) && match.containsKey(v)) {
                seen.add(v);
                seen.add(match.get(v));
                result.add(graph.getEdge(v, match.get(v)));
            }
        }

        return result;
    }

    private V findPath(V root)
    {
        Set<V> used = new HashSet<V>();
        Queue<V> q = new ArrayDeque<V>();

        // Expand graph back from its contracted state
        path.clear();
        contracted.clear();

        for (V i : graph.vertexSet()) {
            contracted.put(i, i);
        }

        used.add(root);
        q.add(root);

        while (!q.isEmpty()) {
            V v = q.remove();

            for (E e : graph.edgesOf(v)) {
                V to = graph.getEdgeSource(e);

                if (to == v) {
                    to = graph.getEdgeTarget(e);
                }

                if ((contracted.get(v) == contracted.get(to))
                    || (match.get(v) == to))
                {
                    continue;
                }

                // Check whether we've hit a 'blossom'
                if ((to == root)
                    || ((match.containsKey(to))
                        && (path.containsKey(match.get(to)))))
                {
                    V stem = lca(v, to);

                    Set<V> blossom = new HashSet<V>();

                    // ?
                    markPath(v, to, stem, blossom);
                    markPath(to, v, stem, blossom);

                    for (V i : graph.vertexSet()) {
                        if (contracted.containsKey(i)
                            && blossom.contains(contracted.get(i)))
                        {
                            contracted.put(i, stem);

                            // ???
                            if (!used.contains(i)) {
                                used.add(i);
                                q.add(i);
                            }
                        }
                    }

                    // Check whether we've had hit a loop (of even length (!)
                    // presumably)
                } else if (!path.containsKey(to)) {
                    path.put(to, v);

                    if (!match.containsKey(to)) {
                        return to;
                    }

                    to = match.get(to);

                    used.add(to);
                    q.add(to);
                }
            }
        }
        return null;
    }

    private void markPath(V v, V child, V stem, Set<V> blossom)
    {
        while (contracted.get(v) != stem) {
            blossom.add(contracted.get(v));
            blossom.add(contracted.get(match.get(v)));
            path.put(v, child);
            child = match.get(v);
            v = path.get(match.get(v));
        }
    }

    private V lca(V a, V b)
    {
        Set<V> seen = new HashSet<V>();
        for (;;) {
            a = contracted.get(a);
            seen.add(a);
            if (!match.containsKey(a)) {
                break;
            }
            a = path.get(match.get(a));
        }
        for (;;) {
            b = contracted.get(b);
            if (seen.contains(b)) {
                return b;
            }
            b = path.get(match.get(b));
        }
    }
}

// End EdmondsBlossomShrinking.java
