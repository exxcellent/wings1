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

package org.wings.plaf;

import java.io.IOException;

import org.wings.*; import org.wings.border.*;
import org.wings.io.*;
import org.wings.style.*;


public class DefaultComponentCG implements ComponentCG, SConstants
{

    public void reload(SComponent comp, int aspect) {
        if ( comp==null )
            return;

        SFrame parent = comp.getParentFrame();

        if ( parent==null )
            return;

        ReloadManager reloadManager = comp.getSession().getReloadManager();

        switch ( aspect ) {
        case ReloadManager.RELOAD_CODE: 
            reloadManager.markDirty(parent.getDynamicResource(DynamicCodeResource.class));
            break;
        case ReloadManager.RELOAD_STYLE: 
            reloadManager.markDirty(parent.getDynamicResource(DynamicStyleSheetResource.class));
            break;
        case ReloadManager.RELOAD_SCRIPT: 
            // TODO
            //            reloadManager.markDirty(parent.getDynamicResource(DynamicScriptResource.class));
            break;
                                    
        }
    }

    public void installCG(SComponent c) {
    }

    public void uninstallCG(SComponent c) {
    }

    public void write(Device d, SComponent c) throws IOException {
        d.print("no plaf for component " + c.getClass());
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
