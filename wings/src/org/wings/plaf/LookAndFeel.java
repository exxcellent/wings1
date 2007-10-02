/*
 * $Id$

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

package org.wings.plaf;

import org.wings.ClasspathResource;
import org.wings.Resource;
import org.wings.SDimension;
import org.wings.SIcon;
import org.wings.SResourceIcon;
import org.wings.style.AttributeSet;
import org.wings.style.CSSStyleSheet;
import org.wings.style.SimpleAttributeSet;
import org.wings.style.StyleSheet;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * A Look-and-Feel consists of a bunch of CGs and resource properties.
 * wingS provides a pluggable look-and-feel (laf or plaf) concept similar to that of Swing.
 * A certain plaf implementation normally adresses a specific browser.
 * 
 * @see org.wings.plaf.ComponentCG
 */
public class LookAndFeel
{
    private final static Log logger = LogFactory.getLog("org.wings.plaf");

    private static Map wrappers = new HashMap();
    static {
        wrappers.put(Boolean.TYPE, Boolean.class);
        wrappers.put(Character.TYPE, Character.class);
        wrappers.put(Byte.TYPE, Byte.class);
        wrappers.put(Short.TYPE, Short.class);
        wrappers.put(Integer.TYPE, Integer.class);
        wrappers.put(Long.TYPE, Long.class);
        wrappers.put(Float.TYPE, Float.class);
        wrappers.put(Double.TYPE, Double.class);
    }

    protected final ClassLoader classLoader;
    protected Properties properties;

    private final Map finalResources = Collections.synchronizedMap(new HashMap());

    /**
     * Instantiate a laf using the war's classLoader.
     * @param properties the configuration of the laf
     */
    public LookAndFeel(Properties properties) {
	this.properties = properties;
	this.classLoader = getClass().getClassLoader();
    }

    /**
     * Instantiate a laf using the specified classLoader.
     * The properties are read from the classLoader's classpath as a resource
     * with name <i>default.properties</i>.
     * @param classLoader the classLoader that will load the CGs
     */
    public LookAndFeel(ClassLoader classLoader)
        throws IOException
    {
        this.classLoader = classLoader;
        this.properties = new Properties();
        InputStream in = classLoader.getResourceAsStream("default.properties");
        if (in == null) {
            throw new IOException("'default.properties' not found in toplevel package of classpath");
        }
        this.properties.load(in);
        in.close();

        logger.debug("create LookAndFeel");
    }

    /**
     * Return a unique string that identifies this look and feel, e.g.
     * "konqueror"
     */
    public String getName() {
        return properties.getProperty("lookandfeel.name");
    }

    /**
     * Return a one line description of this look and feel implementation,
     * e.g. "Optimized for KDE's Konqueror Browser".
     */
    public String getDescription() {
        return properties.getProperty("lookandfeel.description");
    }

    /**
     * Return the ClassLoader, that is used to load the CGs.
     * @return the ClassLoader
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * create a fresh CGDefaults map. One defaults map per Session is generated
     * in its CGManager. It is necessary to create a fresh defaults map, since
     * it caches values that might be modified within the sessions. A prominent
     * example of changed values per sessions are the CG's themselves: 
     * CG-properties might be changed per session...
     *
     * @return the laf's defaults
     */
    public CGDefaults createDefaults() {
        return new ResourceFactory();
    }

    /**
     * Create a CG instance.
     * @param className the full qualified class name of the CG
     * @return a new CG instance
     */
    public Object makeCG(String className) {
        Object result = finalResources.get(className);
        if ( result==null ) {
            result = makeCG(classLoader, className);
            synchronized ( finalResources ) {
                if ( !finalResources.containsKey(className)) {
                    finalResources.put(className, result);
                } else {
                    result = finalResources.get(className);
                } // end of if ()
            }
        } // end of if ()
        return result;
    }

    /**
     * Create a CG instance.
     * @param className the full qualified class name of the CG
     * @return a new CG instance
     */
    public static Object makeCG(ClassLoader classLoader, String className) {
        try {
            Class cgClass = Class.forName(className, true, classLoader);
            return cgClass.newInstance();
        }
        catch ( Exception ex ) {
            logger.fatal( null, ex);
        }
        return null;
    }

    /**
     * Utility method that creates an Icon from a resource
     * located realtive to the given base class.
     * @param classLoader the ClassLoader that should load the icon
     * @param fileName of the image file
     * @return a newly allocated Icon
     */
    public static SIcon makeIcon(ClassLoader classLoader, String fileName) {
        return new SResourceIcon(classLoader, fileName);
    }

    /**
     * Utility method that creates an java.awt.Color from a html color hex string
     * @return the create color
     */
    public static Color makeColor(String colorString) {
        if ( colorString!=null ) {
            try {
            return Color.decode(colorString.trim());
            } catch ( Exception ex ) {
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * Utility method that creates an java.awt.Color from a html color hex string
     * @return the create color
     */
    public static SDimension makeDimension(String dimensionString) {
        if ( dimensionString!=null ) {
            int commaIndex = dimensionString.indexOf(',');
            if ( commaIndex>0 ) {
                return new SDimension(dimensionString.substring(0, commaIndex),
                                      dimensionString.substring(commaIndex+1));
            }
        }
        return null;
    }

    /**
     * Utility method that creates an Icon from a resource
     * located realtive to the given base class. Uses the ClassLoader
     * of the LookAndFeel
     *
     * @see LookAndFeel.LookAndFeel(Properties p, ClassLoader cl)
     * @param fileName of the image file
     * @return a newly allocated Icon
     */
    public SIcon makeIcon(String fileName) {
        SIcon result = (SIcon)finalResources.get(fileName);
        if ( result==null ) {
            result = makeIcon(classLoader, fileName);
            synchronized ( finalResources ) {
                if ( !finalResources.containsKey(fileName)) {
                    finalResources.put(fileName, result);
                } else {
                    result = (SIcon)finalResources.get(fileName);
                } // end of if ()
            }
        } // end of if ()
        return result;
    }

    /**
     * Utility method that creates an AttributeSet from a String
     *
     * @param attributes attributes string
     * @return a newly allocated AttributeSet
     */
    public static AttributeSet makeAttributeSet(String string) {
        AttributeSet attributes = new SimpleAttributeSet();
        StringTokenizer tokens = new StringTokenizer(string, ";");
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            int pos = token.indexOf(":");
            if (pos >= 0) {
                attributes.put(token.substring(0, pos), 
                               token.substring(pos + 1));
            }
        }
        return attributes;
    }

    /**
     * Utility method that creates a styleSheet from a resource string
     * @param value styleSheet as a string
     * @return the style
     */
    public static Resource makeResource(ClassLoader classLoader, String resourceName) {
        return new ClasspathResource(classLoader, resourceName);
    }

    /**
     * Utility method that creates a styleSheet from a string
     * @param value styleSheet as a string
     * @return the styleSheet
     */
    public Resource makeResource(String resourceName) {
        Resource result = (Resource)finalResources.get(resourceName);
        if ( result==null ) {
            result = makeResource(classLoader, resourceName);
            synchronized ( finalResources ) {
                if ( !finalResources.containsKey(resourceName)) {
                finalResources.put(resourceName, result);
                } else {
                    result = (Resource)finalResources.get(resourceName);
                } // end of if ()
            }
        } // end of if ()
        return result;
    }

    /**
     * Utility method that creates a stylesheet object from a resource
     * @param resourceName
     * @return the styleSheet
     */
    public StyleSheet makeStyleSheet(String resourceName) {
        return makeStyleSheet(classLoader, resourceName);
    }

    /**
     * Utility method that creates a stylesheet object from a resource
     * @param resourceName
     * @return the styleSheet
     */
    public static StyleSheet makeStyleSheet(ClassLoader classLoader, String resourceName) {
        try {
            CSSStyleSheet styleSheet = new CSSStyleSheet();
            InputStream in = classLoader.getResourceAsStream(resourceName);
            styleSheet.read(in);
            in.close();
            return styleSheet;
        }
        catch (Exception e) {
            logger.warn( null, e);
            return null;
        }
    }

    /**
     * Utility method that creates an Object of class <code>clazz</code>
     * using the single String arg constructor.
     * @param classLoader the classLoader to be used
     * @param value object as a string
     * @param clazz class of the object
     * @return the object
     */
    public static Object makeObject(ClassLoader classLoader, String value, 
                                    Class clazz) 
    {
        try {
            if (value.startsWith("new ")) {
                int bracket = value.indexOf("(");
                String name = value.substring("new ".length(), bracket);
                clazz = Class.forName(name, true, classLoader);
                return clazz.newInstance();
            }
            else {
                if (clazz.isPrimitive())
                    clazz = (Class)wrappers.get(clazz);
                Constructor constructor = clazz.getConstructor(new Class[] { String.class });
                return constructor.newInstance(new Object[] { value });
            }
        }
        catch (NoSuchMethodException e) {
            logger.fatal( value + " : " + clazz.getName() 
                       + " doesn't have a single String arg constructor", e);
            return null;
        }
        catch (Exception e) {
            logger.fatal( 
                       e.getClass().getName() + " : " + value, e);
            return null;
        }
    }

    /**
     * Utility method that creates an Object of class <code>clazz</code>
     * using the single String arg constructor.
     * @param value object as a string
     * @param clazz class of the object
     * @return the object
     */
    public Object makeObject(String value, Class clazz) {
        return makeObject(classLoader, value, clazz);
    }

    /**
     * Returns a string that displays and identifies this
     * object's properties.
     *
     * @return a String representation of this object
     */
    public String toString() {
        return "[" + getDescription() + " - " + getClass().getName() + "]";
    }


    class ResourceFactory extends CGDefaults {

        public ResourceFactory() { super(null); }
        
        public Object get(String id, Class type) {
            Object value = get(id);
            if (value != null)
                return value;

            String property = properties.getProperty(id);
            if (property == null) {
                put(id, null);
                return null;
            }
            
            if (ComponentCG.class.isAssignableFrom(type)
                || LayoutCG.class.isAssignableFrom(type)
                || BorderCG.class.isAssignableFrom(type)) {
                // some CG is requested. We do not check, whether the
                // value returned actually fulfills
                // type.isAssignableFrom(value.getClass());
                value = makeCG(property);
            }
            else if (type.isAssignableFrom(SIcon.class)) {
                value = makeIcon(property);
            } else if (type.isAssignableFrom(Resource.class))
                value = makeResource(property);
            else if (type.isAssignableFrom(AttributeSet.class))
                value = makeAttributeSet(property);
            else if (type.isAssignableFrom(StyleSheet.class))
                value = makeStyleSheet(property);
            else if (type.isAssignableFrom(Color.class))
                value = makeColor(property);
            else if (type.isAssignableFrom(SDimension.class))
                value = makeDimension(property);
            else
                value = makeObject(property, type);
            
            // cache the object requested here for future use. A property,
            // whose name ends with '.nocache' is not cached, thus always
            // a new instance is created.
            if (!id.endsWith(".nocache")) {
                put(id, value);
            }
            return value;
        }
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
