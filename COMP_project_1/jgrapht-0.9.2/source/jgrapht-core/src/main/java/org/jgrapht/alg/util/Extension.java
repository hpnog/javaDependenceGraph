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
/* -----------------
 * MaximumFlowAlgorithmBase.java
 * -----------------
 * (C) Copyright 2015-2015, by Alexey Kudinkin and Contributors.
 *
 * Original Author:  Alexey Kudinkin
 * Contributor(s):
 *
 * $Id$
 *
 * Changes
 * -------
 */
package org.jgrapht.alg.util;

import java.util.*;


/**
 * Abstract extension manager allowing to extend given class-types with supplied
 * extension's class-type saving source class object references
 *
 * @param <T> class-type to be extended
 * @param <E> extension concept class-type
 */
public class Extension<T, E>
{
    private ExtensionFactory<E> extensionFactory;
    private Map<T, E> extensions = new HashMap<T, E>();

    public Extension(ExtensionFactory<E> factory)
    {
        this.extensionFactory = factory;
    }

    public E createInstance()
    {
        return extensionFactory.create();
    }

    public E get(T t)
    {
        if (extensions.containsKey(t)) {
            return extensions.get(t);
        }

        E x = createInstance();
        extensions.put(t, x);
        return x;
    }

    /**
     * Factory capable of producing given extension objects of the given
     * class-type
     *
     * @param <E> extension concept class-type
     */
    public interface ExtensionFactory<E>
    {
        E create();
    }

    public static abstract class BaseExtension
    {
        public BaseExtension()
        {
        }
    }

    public static class ExtensionManagerInstantiationException
        extends RuntimeException
    {
        Exception exception;

        public ExtensionManagerInstantiationException(Exception e)
        {
            exception = e;
        }
    }
}

// End Extension.java
