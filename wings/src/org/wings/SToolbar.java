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

import org.wings.plaf.ToolbarCG;

/**
 * The basic SToolbar is just an SPanel without an SLayoutManager.
 * However, the Plaf might choose to set a different layout manager.
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class SToolbar extends SPanel
{
    /**
     * @see #getCGClassID
     */
    private static final String cgClassID = "ToolbarCG";

    /**
     * create a new SToolbar.
     */
    public SToolbar() { }

    public String getCGClassID() {
        return cgClassID;
    }

    public void setCG(ToolbarCG cg) {
        super.setCG(cg);
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
