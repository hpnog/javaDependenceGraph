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
/* -------------------------
 * VF2GraphIsomorphismInspectorTest.java
 * -------------------------
 * (C) Copyright 2015, by Fabian Späh and Contributors.
 *
 * Original Author:  Fabian Späh
 * Contributor(s):   Rita Dobler
 *
 * $Id$
 *
 * Changes
 * -------
 * 20-Jun-2015 : Initial revision (FS);
 *
 */
package org.jgrapht.alg.isomorphism;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphMapping;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.ListenableUndirectedGraph;
import org.junit.Test;


/**
 * This test class is fairly small compared with the tests for the
 * VF2SubgraphIsomorphismInspector class due to the similarities in both
 * algorithms (SubgraphIsomorphism and GraphIsomorphism)
 */
public class VF2GraphIsomorphismInspectorTest
{

    @Test
    public void testAutomorphism() {
        /*
         *   v1-----v2
         *    \    /
         *     \  /
         *      v3
         */
        SimpleGraph<String, DefaultEdge> g1 =
            new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);

        String v1 = "v1",
               v2 = "v2",
               v3 = "v3";

        g1.addVertex(v1);
        g1.addVertex(v2);
        g1.addVertex(v3);

        g1.addEdge(v1, v2);
        g1.addEdge(v2, v3);
        g1.addEdge(v3, v1);


        VF2GraphIsomorphismInspector<String, DefaultEdge> vf2 =
            new VF2GraphIsomorphismInspector<String, DefaultEdge>(g1, g1);

        Iterator<GraphMapping<String, DefaultEdge>> iter = vf2.getMappings();

        Set<String> mappings =
            new HashSet<String>(Arrays.asList("[v1=v1 v2=v2 v3=v3]",
                                              "[v1=v1 v2=v3 v3=v2]",
                                              "[v1=v2 v2=v1 v3=v3]",
                                              "[v1=v2 v2=v3 v3=v1]",
                                              "[v1=v3 v2=v1 v3=v2]",
                                              "[v1=v3 v2=v2 v3=v1]"));
        assertEquals(true, mappings.remove(iter.next().toString()));
        assertEquals(true, mappings.remove(iter.next().toString()));
        assertEquals(true, mappings.remove(iter.next().toString()));
        assertEquals(true, mappings.remove(iter.next().toString()));
        assertEquals(true, mappings.remove(iter.next().toString()));
        assertEquals(true, mappings.remove(iter.next().toString()));
        assertEquals(false, iter.hasNext());


        /*
         *   1 ---> 2 <--- 3
         */
        DefaultDirectedGraph<Integer, DefaultEdge> g2 =
            new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);

        g2.addVertex(1);
        g2.addVertex(2);
        g2.addVertex(3);

        g2.addEdge(1, 2);
        g2.addEdge(3, 2);


        VF2GraphIsomorphismInspector<Integer, DefaultEdge> vf3 =
            new VF2GraphIsomorphismInspector<Integer, DefaultEdge>(g2, g2);

        Iterator<GraphMapping<Integer, DefaultEdge>> iter2 = vf3.getMappings();

        Set<String> mappings2 =
            new HashSet<String>(Arrays.asList("[1=1 2=2 3=3]",
                                              "[1=3 2=2 3=1]"));
        assertEquals(true, mappings2.remove(iter2.next().toString()));
        assertEquals(true, mappings2.remove(iter2.next().toString()));
        assertEquals(false, iter2.hasNext());
    }

    @Test
    public void testSubgraph() {
        DirectedGraph<Integer, DefaultEdge> g1 =
            SubgraphIsomorphismTestUtils.randomGraph(10, 30, 12345);
        DirectedGraph<Integer, DefaultEdge> g2 =
            SubgraphIsomorphismTestUtils.randomSubgraph(g1, 7, 54321);

        VF2GraphIsomorphismInspector<Integer, DefaultEdge> vf2 =
            new VF2GraphIsomorphismInspector<Integer, DefaultEdge>(g1, g2);
        assertEquals(false, vf2.isomorphismExists());
    }

    @Test
    public void testSubgraph2() {

        ListenableUndirectedGraph<Integer, DefaultEdge> g1 =
            new ListenableUndirectedGraph<Integer, DefaultEdge>
                        (DefaultEdge.class);

        g1.addVertex(0);
        g1.addVertex(1);
        g1.addVertex(2);
        g1.addVertex(3);
        g1.addVertex(4);

        g1.addEdge(0, 1);
        g1.addEdge(0, 4);
        g1.addEdge(1, 2);
        g1.addEdge(1, 4);
        g1.addEdge(2, 3);
        g1.addEdge(2, 4);
        g1.addEdge(3, 4);

        ListenableUndirectedGraph<Integer, DefaultEdge> g2 =
            new ListenableUndirectedGraph<Integer, DefaultEdge>
                        (DefaultEdge.class);

        g2.addVertex(0);
        g2.addVertex(1);
        g2.addVertex(2);
        g2.addVertex(3);
        g2.addVertex(4);
        g2.addVertex(5);

        g2.addEdge(4, 2);
        g2.addEdge(2, 0);
        g2.addEdge(0, 1);
        g2.addEdge(1, 3);
        g2.addEdge(3, 4);
        g2.addEdge(4, 0);
        g2.addEdge(1, 4);

        ListenableUndirectedGraph<Integer, DefaultEdge> g3 =
            new ListenableUndirectedGraph<Integer, DefaultEdge>
                        (DefaultEdge.class);

        g3.addVertex(0);
        g3.addVertex(1);
        g3.addVertex(2);
        g3.addVertex(3);
        g3.addVertex(4);
        g3.addVertex(5);

        g3.addEdge(0, 1);
        g3.addEdge(1, 2);
        g3.addEdge(2, 3);
        g3.addEdge(3, 4);
        g3.addEdge(5, 2);
        g3.addEdge(5, 3);
        g3.addEdge(5, 4);

        ListenableUndirectedGraph<Integer, DefaultEdge> g4 =
            new ListenableUndirectedGraph<Integer, DefaultEdge>
                        (DefaultEdge.class);

        g4.addVertex(0);
        g4.addVertex(1);
        g4.addVertex(2);
        g4.addVertex(3);
        g4.addVertex(4);
        g4.addVertex(5);

        g4.addEdge(0, 1);
        g4.addEdge(1, 2);
        g4.addEdge(2, 3);
        g4.addEdge(4, 5);
        g4.addEdge(4, 2);
        g4.addEdge(5, 2);
        g4.addEdge(5, 3);

        VF2GraphIsomorphismInspector<Integer, DefaultEdge> vf2 =
            new VF2GraphIsomorphismInspector<Integer, DefaultEdge>(g2, g1),
                                                           vf3 =
            new VF2GraphIsomorphismInspector<Integer, DefaultEdge>(g1, g2),
                                                           vf4 =
            new VF2GraphIsomorphismInspector<Integer, DefaultEdge>(g3, g4);

        assertEquals(false, vf2.isomorphismExists());
        assertEquals(false, vf3.isomorphismExists());
        assertEquals(false, vf4.isomorphismExists());
    }

}
