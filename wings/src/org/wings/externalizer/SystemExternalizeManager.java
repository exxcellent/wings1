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

package org.wings.externalizer;

import java.util.*;
import java.util.logging.*;

/**
 * This singleton externalizes 
 * {#link AbstractExternalizeManager#GLOBAL global} scope. Every object
 * externalized by the SystemExternalizeManager (global scope) is available 
 * over the life time of the servlet container and is not garbage collected.
 *
 * Created: Sat Nov 10 15:49:15 2001
 *
 * @author <a href="mailto:armin@hyperion.intranet.mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */

public class SystemExternalizeManager extends AbstractExternalizeManager
{
    /**
     * singleton implementation
     */
    private static final SystemExternalizeManager sharedInstance = new SystemExternalizeManager();
  
    /**
     * TODO: documentation
     */
    protected final Map/*<String, ExternalizedResource>*/ externalized;

    /**
     * 
     */
    private SystemExternalizeManager () {
        externalized = Collections.synchronizedMap( new HashMap() );
    }

    /**
     * get the single system wide instance.
     *
     * @return the SystemExternalizeManager instance.
     */
    public static SystemExternalizeManager getSharedInstance() {
        return sharedInstance;
    }

    public void setPrefix(final String prefix) {
        if (prefix.startsWith("-"))
            super.setPrefix(prefix);
        else  // The prefix MUST start with a - as this is the identifiert for global resources.
            super.setPrefix("-" + prefix);
    }

    protected void storeExternalizedResource(String identifier,
                                             ExternalizedResource extInfo) {
        if (logger.isTraceEnabled()) {
            logger.debug("store identifier " + identifier + " " + extInfo.getObject().getClass());
            logger.debug("flags " + extInfo.getFlags());
        }

        externalized.put(identifier, extInfo);
    }

    public ExternalizedResource getExternalizedResource(String identifier) {
        if ( identifier == null || identifier.length() < 1 )
            return null;
        
        logger.debug("system externalizer: " + identifier);

        ExternalizedResource resource = (ExternalizedResource)externalized.get(identifier);

        //if (resource == null) { // not found? Try to cut of tailing extensions
        //    int pos = identifier.indexOf(".");
        //    if (pos > -1) {
        //        identifier = identifier.substring(0, pos);
        //    }
        //    resource = (ExternalizedResource)externalized.get(identifier);
        //}

        return resource;
    }

    public final void removeExternalizedResource(String identifier) {
        externalized.remove(identifier);
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */

