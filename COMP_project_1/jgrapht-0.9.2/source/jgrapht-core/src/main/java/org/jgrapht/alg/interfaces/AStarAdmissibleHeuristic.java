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
 * AStarAdmissibleHeuristic.java
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
package org.jgrapht.alg.interfaces;

/**
 * Interface for an admissible heuristic used in A* search.
 *
 * @param <V> vertex type
 *
 * @author Joris Kinable
 * @author Jon Robison
 * @author Thomas Breitbart
 */
public interface AStarAdmissibleHeuristic<V>
{
    /**
     * An admissible "heuristic estimate" of the distance from x, the
     * sourceVertex, to the goal (usually denoted h(x)). This is the good guess
     * function.
     */
    public double getCostEstimate(V sourceVertex, V targetVertex);
}

// End AStarAdmissibleHeuristic.java
