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
/* ------------------------------
 * ConnectivityInspectorTest.java
 * ------------------------------
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * Original Author:  Barak Naveh
 * Contributor(s):   John V. Sichi
 *
 * $Id$
 *
 * Changes
 * -------
 * 07-Aug-2003 : Initial revision (BN);
 * 20-Apr-2005 : Added KosarajuStrongConnectivityInspector test (JVS);
 *
 */
package org.jgrapht.alg;

import java.util.*;

import junit.framework.*;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.StrongConnectivityAlgorithm;
import org.jgrapht.generate.*;
import org.jgrapht.graph.*;


/**
 * .
 *
 * @author Barak Naveh
 */
public class ConnectivityInspectorTest
    extends TestCase
{
    //~ Static fields/initializers ---------------------------------------------

    private static final String V1 = "v1";
    private static final String V2 = "v2";
    private static final String V3 = "v3";
    private static final String V4 = "v4";

    //~ Instance fields --------------------------------------------------------

    //
    DefaultEdge e1;
    DefaultEdge e2;
    DefaultEdge e3;
    DefaultEdge e3_b;
    DefaultEdge u;

    //~ Methods ----------------------------------------------------------------

    /**
     * .
     *
     * @return a graph
     */
    public Pseudograph<String, DefaultEdge> create()
    {
        Pseudograph<String, DefaultEdge> g =
            new Pseudograph<String, DefaultEdge>(DefaultEdge.class);

        assertEquals(0, g.vertexSet().size());
        g.addVertex(V1);
        assertEquals(1, g.vertexSet().size());
        g.addVertex(V2);
        assertEquals(2, g.vertexSet().size());
        g.addVertex(V3);
        assertEquals(3, g.vertexSet().size());
        g.addVertex(V4);
        assertEquals(4, g.vertexSet().size());

        assertEquals(0, g.edgeSet().size());

        e1 = g.addEdge(V1, V2);
        assertEquals(1, g.edgeSet().size());

        e2 = g.addEdge(V2, V3);
        assertEquals(2, g.edgeSet().size());

        e3 = g.addEdge(V3, V1);
        assertEquals(3, g.edgeSet().size());

        e3_b = g.addEdge(V3, V1);
        assertEquals(4, g.edgeSet().size());
        assertNotNull(e3_b);

        u = g.addEdge(V1, V1);
        assertEquals(5, g.edgeSet().size());
        u = g.addEdge(V1, V1);
        assertEquals(6, g.edgeSet().size());

        return g;
    }

    /**
     * .
     */
    public void testDirectedGraph()
    {
        ListenableDirectedGraph<String, DefaultEdge> g =
            new ListenableDirectedGraph<String, DefaultEdge>(
                DefaultEdge.class);
        g.addVertex(V1);
        g.addVertex(V2);
        g.addVertex(V3);

        g.addEdge(V1, V2);

        ConnectivityInspector<String, DefaultEdge> inspector =
            new ConnectivityInspector<String, DefaultEdge>(g);
        g.addGraphListener(inspector);

        assertEquals(false, inspector.isGraphConnected());

        g.addEdge(V1, V3);

        assertEquals(true, inspector.isGraphConnected());
    }

    /**
     * .
     */
    public void testIsGraphConnected()
    {
        Pseudograph<String, DefaultEdge> g = create();
        ConnectivityInspector<String, DefaultEdge> inspector =
            new ConnectivityInspector<String, DefaultEdge>(g);

        assertEquals(false, inspector.isGraphConnected());

        g.removeVertex(V4);
        inspector = new ConnectivityInspector<String, DefaultEdge>(g);
        assertEquals(true, inspector.isGraphConnected());

        g.removeVertex(V1);
        assertEquals(1, g.edgeSet().size());

        g.removeEdge(e2);
        g.addEdge(V2, V2);
        assertEquals(1, g.edgeSet().size());

        inspector = new ConnectivityInspector<String, DefaultEdge>(g);
        assertEquals(false, inspector.isGraphConnected());
    }

}

// End ConnectivityInspectorTest.java
