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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.script.DynamicScriptResource;
import org.wings.style.DynamicStyleSheetResource;
import org.wings.session.SessionManager;

/**
 * This is the default implementation of the reload manager.
 *
 * @author <a href="mailto:engels@mercatis.de">Holger Engels</a>
 * @version $Revision$
 */
public class DefaultReloadManager
    implements ReloadManager
{
    private final static Log logger = LogFactory.getLog("org.wings");
    /**
     * a set of all resources, manged by this ReloadManager, that are marked
     * dirty.
     */
    protected final Set dirtyResources;

    public DefaultReloadManager() {
        dirtyResources = new HashSet();
    }
    
    public void reload(SComponent component, int aspect) {
        SFrame parent = component.getParentFrame();

        if (parent == null) {
            parent = SessionManager.getSession().getRootFrame();
        }

        if (parent == null) {
            return;
        }

        if ( (aspect & RELOAD_CODE) != 0 ) {
            markDirty(parent.getDynamicResource(DynamicCodeResource.class));
        }  
        if ( (aspect & RELOAD_STYLE) != 0 ) {
            markDirty(parent.getDynamicResource(DynamicStyleSheetResource.class));
        }
        if ( (aspect & RELOAD_SCRIPT) != 0 ) {
            markDirty(parent.getDynamicResource(DynamicScriptResource.class));
        }
    }

    public synchronized void markDirty(DynamicResource d) {
        if (d == null) {
            logger.warn("markDirty: null");
            return;
        }
        dirtyResources.add(d);
    }

    public Set getDirtyResources() {
        return dirtyResources;
    }

    public synchronized void clear() {
        dirtyResources.clear();
    }

    public synchronized void invalidateResources() {
        //Set frames = new HashSet();
        Iterator it = dirtyResources.iterator();
        while (it.hasNext()) {
            DynamicResource resource = (DynamicResource)it.next();
            resource.invalidate();
            it.remove();
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
