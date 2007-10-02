/*
 * $Id$
 * (c) Copyright 2001 wingS development team.
 *
 * This file is part of wingS (http://j-wings.org).
 *
 * wingS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */
package org.wings.style;

import org.wings.io.Device;

import java.util.*;
import java.io.*;

/**
 * A straightforward implementation of AttributeSet using a hash map.
 *
 * @author <a href="mailto:engels@mercatis.de">Holger Engels</a>
 * @version $Revision$
 */
public class SimpleAttributeSet
    implements AttributeSet, Serializable, Cloneable {
    private Map map;

    /**
     * create a SimpleAttributeSet from the given HashMap.
     */
    private SimpleAttributeSet(HashMap map) {
        this.map = map;
    }

    /**
     * Creates a new, empty atribute set.
     */
    public SimpleAttributeSet() {
    }

    /**
     * Creates a new attribute set based on a supplied set of attributes.
     *
     * @param source the set of attributes
     */
    public SimpleAttributeSet(AttributeSet source) {
        putAll(source);
    }

    /**
     * Checks whether the set of attributes is empty.
     *
     * @return true if the set is empty else false
     */
    public final boolean isEmpty() {
        return map == null || map.isEmpty();
    }

    /**
     * Gets a count of the number of attributes.
     *
     * @return the count
     */
    public final int size() {
        return map == null ? 0 : map.size();
    }

    public final void clear() {
        if (map != null) {
            map.clear();
        }
    }

    /**
     * Tells whether a given attribute is defined.
     *
     * @param attrName the attribute name
     * @return true if the attribute is defined
     */
    public final boolean contains(String name) {
        return map == null ? false : map.containsKey(name);
    }

    /**
     * Gets the names of the attributes in the set.
     *
     * @return the names as an <code>Enumeration</code>
     */
    public final Set names() {
        return map == null ? Collections.EMPTY_SET : map.keySet();
    }

    /**
     * Gets the value of an attribute.
     *
     * @param name the attribute name
     * @return the value
     */
    public final String get(String name) {
        return map == null ? null : (String) map.get(name);
    }

    /**
     * Adds an attribute to the list.
     *
     * @param name the attribute name
     * @param value the attribute value
     */
    public String put(String name, String value) {
        if (map == null) {
            map = new HashMap(8);
        }

        if (value == null)
            return remove(name);
        return (String) map.put(name, value);
    }

    /**
     * Adds a set of attributes to the list.
     *
     * @param attributes the set of attributes to add
     */
    public boolean putAll(AttributeSet attributes) {
        if (map == null) {
            map = new HashMap(8);
        }

        boolean changed = false;
        Iterator names = attributes.names().iterator();
        while (names.hasNext()) {
            String name = (String) names.next();
            changed = changed || (put(name, attributes.get(name)) != null);
        }
        return changed;
    }

    /**
     * Removes an attribute from the list.
     *
     * @param name the attribute name
     */
    public String remove(String name) {
        return map == null ? null : (String) map.remove(name);
    }

    // --- Object methods ---------------------------------

    /**
     * Clones a set of attributes.
     *
     * @return the new set of attributes
     */
    public Object clone() {
        SimpleAttributeSet attr;
        /*
        try {
            attr = (SimpleAttributeSet)super.clone();
            attr.putAll(this);
        } catch (CloneNotSupportedException cnse) {
            attr = null;
        }
        */

        if (isEmpty()) {
            return new SimpleAttributeSet();
        } else {
            return new SimpleAttributeSet((HashMap) ((HashMap) map).clone());
        }
    }

    /**
     * Returns a hashcode for this set of attributes.
     * @return     a hashcode value for this set of attributes.
     */
    public int hashCode() {
        return map == null ? 0 : map.hashCode();
    }

    /**
     * Compares two attribute sets.
     *
     * @param attr the second attribute set
     * @return true if the sets are equal, false otherwise
     */
    public boolean equals(Object object) {
        if (!(object instanceof AttributeSet))
            return false;
        AttributeSet other = (AttributeSet) object;

        if (size() != other.size())
            return false;

        Iterator names = other.names().iterator();
        while (names.hasNext()) {
            String name = (String) names.next();
            if (!other.get(name).equals(get(name)))
                return false;
        }
        return true;
    }

    /**
     * Write style definition to the device
     */
    public void write(Device d)
        throws IOException {
        if (map != null) {
            Iterator names = map.entrySet().iterator();
            while (names.hasNext()) {
                Map.Entry next = (Map.Entry) names.next();
                d.print(next.getKey()).print(":")
                    .print(next.getValue())
                    .print("; ");
            }
        }
    }

    /**
     * Converts the attribute set to a String.
     *
     * @return the string
     */
    public String toString() {
        StringBuffer b = new StringBuffer();

        if (map != null) {
            Iterator names = map.entrySet().iterator();
            while (names.hasNext()) {
                Map.Entry next = (Map.Entry) names.next();
                b.append(next.getKey());
                b.append(":");
                b.append(next.getValue());
                b.append("; ");
            }
        }
        return b.toString();
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
