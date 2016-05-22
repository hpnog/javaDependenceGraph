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
 * VF2MappingIterator.java
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

import java.util.*;

import org.jgrapht.*;


abstract class VF2MappingIterator<V, E>
    implements Iterator<GraphMapping<V, E>>
{
    protected Comparator<V> vertexComparator;
    protected Comparator<E> edgeComparator;

    protected IsomorphicGraphMapping<V, E> nextMapping;
    protected Boolean hadOneMapping;

    protected GraphOrdering<V, E> ordering1, ordering2;

    protected ArrayDeque<VF2State<V, E>> stateStack;

    public VF2MappingIterator(
        GraphOrdering<V, E> ordering1,
        GraphOrdering<V, E> ordering2,
        Comparator<V> vertexComparator,
        Comparator<E> edgeComparator)
    {
        this.ordering1 = ordering1;
        this.ordering2 = ordering2;
        this.vertexComparator = vertexComparator;
        this.edgeComparator = edgeComparator;
        this.stateStack = new ArrayDeque<VF2State<V, E>>();
    }

    /**
     * This function moves over all mappings between graph1 and graph2. It
     * changes the state of the whole iterator.
     *
     * @return null or one matching between graph1 and graph2
     */
    protected abstract IsomorphicGraphMapping<V, E> match();

    protected IsomorphicGraphMapping<V, E> matchAndCheck()
    {
        IsomorphicGraphMapping<V, E> rel = match();
        if (rel != null) {
            hadOneMapping = true;
        }
        return rel;
    }

    @Override public boolean hasNext()
    {
        if (nextMapping != null) {
            return true;
        }

        return (nextMapping = matchAndCheck()) != null;
    }

    @Override public IsomorphicGraphMapping<V, E> next()
    {
        if (nextMapping != null) {
            IsomorphicGraphMapping<V, E> tmp = nextMapping;
            nextMapping = null;
            return tmp;
        }

        IsomorphicGraphMapping<V, E> rel = matchAndCheck();
        if (rel == null) {
            throw new NoSuchElementException();
        }
        return rel;
    }

    @Override public void remove()
    {
        throw new UnsupportedOperationException();
    }
}

// End VF2MappingIterator.java
