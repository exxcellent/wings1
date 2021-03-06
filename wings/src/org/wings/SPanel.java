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

import org.wings.plaf.*;

/**
 * An SPanel is basically a container that can be displayed with
 * its own CG. The components in the container are placed with a
 * Layout Manager. 
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class SPanel
    extends SContainer
{
    /**
     * @see #getCGClassID
     */
    private static final String cgClassID = "PanelCG";

    /**
     * TODO: documentation
     *
     */
    public SPanel() {
        super(new SFlowLayout());
    }

    /**
     * TODO: documentation
     *
     * @param l
     */
    public SPanel(SLayoutManager l) {
        super(l);
    }

    public String getCGClassID() {
        return cgClassID;
    }

    public void setCG(PanelCG cg) {
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
