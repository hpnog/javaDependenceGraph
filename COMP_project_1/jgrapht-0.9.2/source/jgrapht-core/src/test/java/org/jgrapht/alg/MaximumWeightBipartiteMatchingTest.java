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
 * MaximumWeightBipartiteMatchingTest
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

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.Before;
import org.junit.Test;

public class MaximumWeightBipartiteMatchingTest {

	private SimpleWeightedGraph<String, DefaultWeightedEdge> graph;
	private Set<String> partition1;
	private Set<String> partition2;
	
	private MaximumWeightBipartiteMatching<String, DefaultWeightedEdge> matcher;
	
	@Before
	public void setUpGraph() {
		graph = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		graph.addVertex("s1");
		graph.addVertex("s2");
		graph.addVertex("s3");
		graph.addVertex("s4");
		graph.addVertex("t1");
		graph.addVertex("t2");
		graph.addVertex("t3");
		graph.addVertex("t4");
		
		partition1 = new HashSet<String>();
		partition1.add("s1");
		partition1.add("s2");
		partition1.add("s3");
		partition1.add("s4");
		
		partition2 = new HashSet<String>();
		partition2.add("t1");
		partition2.add("t2");
		partition2.add("t3");
		partition2.add("t4");
	}
	
	@Test
	public void maximumWeightBipartiteMatching1() {
		DefaultWeightedEdge e1 = graph.addEdge("s1", "t1");
		graph.setEdgeWeight(e1, 1);
		matcher = new MaximumWeightBipartiteMatching<String, DefaultWeightedEdge>(graph, partition1, partition2);
		Set<DefaultWeightedEdge> matchings = matcher.getMatching();
		assertEquals(1, matchings.size());
		assertTrue(matchings.contains(e1));
	}
	
	@Test
	public void maximumWeightBipartiteMatching2() {
		DefaultWeightedEdge e1 = graph.addEdge("s1", "t1");
		graph.setEdgeWeight(e1, 1);
		DefaultWeightedEdge e2 = graph.addEdge("s2", "t1");
		graph.setEdgeWeight(e2, 2);

		matcher = new MaximumWeightBipartiteMatching<String, DefaultWeightedEdge>(graph, partition1, partition2);
		Set<DefaultWeightedEdge> matchings = matcher.getMatching();
		assertEquals(1, matchings.size());
		assertTrue(matchings.contains(e2));
	}

	@Test
	public void maximumWeightBipartiteMatching3() {
		DefaultWeightedEdge e1 = graph.addEdge("s1", "t1");
		graph.setEdgeWeight(e1, 2);
		DefaultWeightedEdge e2 = graph.addEdge("s1", "t2");
		graph.setEdgeWeight(e2, 1);
		DefaultWeightedEdge e3 = graph.addEdge("s2", "t1");
		graph.setEdgeWeight(e3, 2);
		
		matcher = new MaximumWeightBipartiteMatching<String, DefaultWeightedEdge>(graph, partition1, partition2);
		Set<DefaultWeightedEdge> matchings = matcher.getMatching();
		assertEquals(2, matchings.size());
		assertTrue(matchings.contains(e2));
		assertTrue(matchings.contains(e3));
	}

	@Test
	public void maximumWeightBipartiteMatching4() {
		DefaultWeightedEdge e1 = graph.addEdge("s1", "t1");
		graph.setEdgeWeight(e1, 1);
		DefaultWeightedEdge e2 = graph.addEdge("s1", "t2");
		graph.setEdgeWeight(e2, 1);
		DefaultWeightedEdge e3 = graph.addEdge("s2", "t2");
		graph.setEdgeWeight(e3, 1);
		
		matcher = new MaximumWeightBipartiteMatching<String, DefaultWeightedEdge>(graph, partition1, partition2);
		Set<DefaultWeightedEdge> matchings = matcher.getMatching();
		assertEquals(2, matchings.size());
		assertTrue(matchings.contains(e1));
		assertTrue(matchings.contains(e3));
	}

	@Test
	public void maximumWeightBipartiteMatching5() {
		DefaultWeightedEdge e1 = graph.addEdge("s1", "t1");
		graph.setEdgeWeight(e1, 1);
		DefaultWeightedEdge e2 = graph.addEdge("s1", "t2");
		graph.setEdgeWeight(e2, 2);
		DefaultWeightedEdge e3 = graph.addEdge("s2", "t2");
		graph.setEdgeWeight(e3, 2);
		DefaultWeightedEdge e4 = graph.addEdge("s3", "t2");
		graph.setEdgeWeight(e4, 2);		
		DefaultWeightedEdge e5 = graph.addEdge("s3", "t3");
		graph.setEdgeWeight(e5, 1);
		DefaultWeightedEdge e6 = graph.addEdge("s4", "t1");
		graph.setEdgeWeight(e6, 1);
		DefaultWeightedEdge e7 = graph.addEdge("s4", "t4");
		graph.setEdgeWeight(e7, 1);
		
		matcher = new MaximumWeightBipartiteMatching<String, DefaultWeightedEdge>(graph, partition1, partition2);
		Set<DefaultWeightedEdge> matchings = matcher.getMatching();
		assertEquals(4, matchings.size());
		assertTrue(matchings.contains(e1));
		assertTrue(matchings.contains(e3));
		assertTrue(matchings.contains(e5));
		assertTrue(matchings.contains(e7));
	}
	
}
