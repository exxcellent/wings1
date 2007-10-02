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
package org.wings.plaf.compiler;

final class Property implements Comparable {
    private final static String[] TEST_PKG_PREFIX = { "", 
                                                      "java.lang.", 
                                                      "org.wings.",
                                                      "org.wings.style." };
    final String name;
    final String typeName;
    final Class  type;
    boolean cached = true;
    String value;

    public Property(String typeName, String name) 
        throws ClassNotFoundException {
	this.name = name;
        this.typeName = typeName;
        Class tempType = null;
        for (int i=0; i < TEST_PKG_PREFIX.length && tempType == null; ++i) {
            String testTypeName = TEST_PKG_PREFIX[i] + typeName;
            try {
                tempType = Class.forName(testTypeName);
            }
            catch (ClassNotFoundException e) { /* ignore */ }
        }
        if (tempType == null) {
            if ("int".equals(typeName)) {
                tempType = Integer.TYPE;
            }
            else if ("boolean".equals(typeName)) {
                tempType = Boolean.TYPE;
            }
            else {
                throw new ClassNotFoundException("invalid type '" + typeName + "'; if this is not a misspelling, try the fully qualified name!");
            }
        }
        this.type = tempType;
    }

    public void setValue(String v) { value = v.trim(); }
    public String getValue() { return value; }
    public String getName() { return name; }
    public Class getType() { return type; }
    public String getTypeName() { return typeName; }
    
    /**
     * set, whether the property manager should cache the value instantiated.
     */
    public void setCached(boolean c) { cached = c; }
    public boolean shouldCache() { return cached; }

    public int compareTo(Object o) {
        Property other = (Property) o;
        return name.compareTo(other.name);
    }
}
/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
