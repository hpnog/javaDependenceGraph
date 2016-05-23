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
 * EdmondsBlossomShrinkingTest.java
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

import junit.framework.TestCase;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.Set;

/**
 * .
 *
 * @author Alejandro R. Lopez del Huerto
 * @since Jan 24, 2012
 */
public final class EdmondsBlossomShrinkingTest extends TestCase
{
    public void testOne()
    {
        // create an undirected graph
        UndirectedGraph<Integer, DefaultEdge> g =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        Integer v1 = 1;
        Integer v2 = 2;
        Integer v3 = 3;
        Integer v4 = 4;

        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);

        DefaultEdge e12 = g.addEdge(v1, v2);
        DefaultEdge e23 = g.addEdge(v2, v3);
        DefaultEdge e24 = g.addEdge(v2, v4);
        DefaultEdge e34 = g.addEdge(v3, v4);

        // compute max match
        EdmondsBlossomShrinking<Integer, DefaultEdge> matcher =
            new EdmondsBlossomShrinking<Integer, DefaultEdge>(g);
        Set<DefaultEdge> match = matcher.getMatching();
        assertEquals(2, match.size());
        assertTrue(match.contains(e12));
        assertTrue(match.contains(e34));
    }

    public void testCrash()
    {
        UndirectedGraph<Integer, DefaultEdge> g =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        Integer v1 = 1;
        Integer v2 = 2;
        Integer v3 = 3;
        Integer v4 = 4;
        Integer v5 = 5;

        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);
        g.addVertex(v5);

        DefaultEdge e12 = g.addEdge(v1, v2);
        DefaultEdge e13 = g.addEdge(v1, v3);
        DefaultEdge e23 = g.addEdge(v2, v3);
        DefaultEdge e24 = g.addEdge(v2, v4);
        DefaultEdge e34 = g.addEdge(v3, v4);
        DefaultEdge e35 = g.addEdge(v3, v5);
        DefaultEdge e45 = g.addEdge(v4, v5);

        EdmondsBlossomShrinking<Integer, DefaultEdge> matcher =
            new EdmondsBlossomShrinking<Integer, DefaultEdge>(g);

        Set<DefaultEdge> match = matcher.getMatching();

        assertEquals(2, match.size());

        assertTrue(match.contains(e12));
        assertTrue(match.contains(e34));
    }


    public void testCrash2()
    {
        UndirectedGraph<Integer, DefaultEdge> g =
            new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

        Integer vs[] = new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 };

        for (int i = 1; i < 14; ++i) {
            g.addVertex(vs[i]);
        }

        DefaultEdge e12 = g.addEdge(vs[1], vs[2]);
        DefaultEdge e23 = g.addEdge(vs[2], vs[3]);
        DefaultEdge e34 = g.addEdge(vs[3], vs[4]);
        DefaultEdge e45 = g.addEdge(vs[4], vs[5]);
        DefaultEdge e53 = g.addEdge(vs[5], vs[3]);
        DefaultEdge e56 = g.addEdge(vs[5], vs[6]);
        DefaultEdge e67 = g.addEdge(vs[6], vs[7]);
        DefaultEdge e78 = g.addEdge(vs[7], vs[8]);
        DefaultEdge e89 = g.addEdge(vs[8], vs[9]);
        DefaultEdge e97 = g.addEdge(vs[9], vs[7]);
        DefaultEdge e910 = g.addEdge(vs[9], vs[10]);
        DefaultEdge e1011 = g.addEdge(vs[10], vs[11]);
        DefaultEdge e1112 = g.addEdge(vs[11], vs[12]);
        DefaultEdge e1213 = g.addEdge(vs[12], vs[13]);
        DefaultEdge e1311 = g.addEdge(vs[13], vs[11]);

        EdmondsBlossomShrinking<Integer, DefaultEdge> matcher =
            new EdmondsBlossomShrinking<Integer, DefaultEdge>(g);

        Set<DefaultEdge> match = matcher.getMatching();

        assertEquals(6, match.size());

        assertTrue(match.contains(e12));
        assertTrue(match.contains(e34));
        assertTrue(match.contains(e56));
        assertTrue(match.contains(e78));
        assertTrue(match.contains(e910));
        assertTrue(match.contains(e1112));
    }
}
