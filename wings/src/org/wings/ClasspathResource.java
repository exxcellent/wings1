/*
 * $Id$
 * (c) Copyright 2000 wingS development team.
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

package org.wings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.*;

import org.wings.session.*;
import org.wings.externalizer.ExternalizeManager;
import org.wings.externalizer.AbstractExternalizeManager;

/**
 * A Classpath Resource is a static resource whose content is 
 * read from a classloader.
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @author <a href="mailto:H.Zeller@acm.org">Henner Zeller</a>
 * @version $Revision$
 */
public class ClasspathResource
    extends StaticResource implements NamedResource
{
    /**
     * The class loader from which the resource is loaded
     */
    protected final ClassLoader classLoader;

    /**
     * The name that identifies the resource in the classpath
     */
    protected final String resourceFileName;

    /**
     * A static resource that is obtained from the default classpath.
     *
     * @param resourceFileName
     */
    public ClasspathResource(String resourceFileName) {
        this(Resource.class.getClassLoader(), resourceFileName, "unkonwn");
    }

    /**
     * A static resource that is obtained from the default classpath.
     *
     * @param resourceFileName
     */
    public ClasspathResource(String resourceFileName, String mimeType) {
        this(Resource.class.getClassLoader(), resourceFileName, mimeType);
    }

    /**
     * A static resource that is obtained from the specified class loader
     *
     * @param classLoader the classLoader from which the resource is obtained
     * @param resourceFileName the resource relative to the baseClass
     */
    public ClasspathResource(ClassLoader classLoader, String resourceFileName) {
        this(classLoader, resourceFileName, "unknown");
    }

    /**
     * A static resource that is obtained from the specified class loader
     *
     * @param classLoader the classLoader from which the resource is obtained
     * @param resourceFileName the resource relative to the baseClass
     */
    public ClasspathResource(ClassLoader classLoader, String resourceFileName, String mimeType) {
        super(null, mimeType);
        this.classLoader = classLoader;
        this.resourceFileName = resourceFileName;
        int dotIndex = resourceFileName.lastIndexOf('.');
        if (dotIndex > -1) {
            extension = resourceFileName.substring(dotIndex + 1);
        }
        externalizerFlags = ExternalizeManager.GLOBAL | ExternalizeManager.FINAL;
    }


    public String toString() {
        return getId() + " " + resourceFileName;
    }

    protected final InputStream getResourceStream() {
        return classLoader.getResourceAsStream(resourceFileName);
    }

    /*
     * the equal() and hashCode() method make sure, that the same resources
     * get the same name in the SystemExternalizer.
     */

    /**
     * resources using the same classloader and are denoting the same
     * name, do have the same hashCode(). Thus the same resources get the
     * same ID in the System externalizer.
     *
     * @return a hashcode, comprised from the hashcodes of the classloader
     *         and from the file name of the resource.
     */
    public int hashCode() {
        return classLoader.hashCode() ^ resourceFileName.hashCode();
    }

    /**
     * Two ClasspathResouces are equal if both of them use the same
     * classloader and point to a resource with the same name.
     *
     * @return true if classloader and resource name are equal.
     */
    public boolean equals(Object o) {
        if (o instanceof ClasspathResource) {
            ClasspathResource other = (ClasspathResource) o;
            return ((this == other)
                    || (classLoader.equals(other.classLoader)
                        && resourceFileName.equals(other.resourceFileName)));
        }
        return false;
    }


    public String getResourceName() {
        return resourceFileName;
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * End:
 */
