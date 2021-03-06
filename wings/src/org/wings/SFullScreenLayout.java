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

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.util.*;

import org.wings.plaf.*;
import org.wings.io.Device;

/**
 * This is a {@link SBorderLayout}, which occupies the whole browser window.
 * Works perfect on IE, and pretty good on Netscape, Mozilla and Opera *sniff*.
 * The {@link SContainer} has a fixed position at 0x0 (upper left corner), so
 * it should only be used to set Layout for <tt>org.wings.SFrame.getContentPane()</tt>!
 *
 * @author <a href="mailto:andre@lison.de">Andre Lison</a>
 * @version $Revision$
 */
public class SFullScreenLayout
    extends SBorderLayout
{
    /**
     * @see #getCGClassID
     */
    private static final String _cgClassID = "FullScreenLayoutCG";

    private static final boolean DEBUG = true;
    
    private final static SDimension fDim = new SDimension("100%", "100%");

    public SFullScreenLayout() {
        super();
        setPreferredSize(fDim);
    }

    public void addComponent(SComponent c, Object constraint, int index) {
        if (constraint == null)
            constraint = CENTER;

        if (c.getHorizontalAlignment() == SConstants.NO_ALIGN) {
            if (constraint == WEST)
                c.setHorizontalAlignment(SConstants.LEFT);
            else
                if (constraint == EAST)
                    c.setHorizontalAlignment(SConstants.RIGHT);
            /* CENTER, SOUTH, NORTH */
                else
                    c.setHorizontalAlignment(SConstants.CENTER);
        }
        if (c.getVerticalAlignment() == SConstants.NO_ALIGN) {
            if (constraint == CENTER)
                c.setVerticalAlignment(SConstants.CENTER);
            else
                if (constraint == SOUTH)
                    c.setVerticalAlignment(SConstants.BOTTOM);
                else
                    if (constraint == NORTH)
                        c.setVerticalAlignment(SConstants.TOP);
            /* WEST, EAST */
                    else
                        c.setVerticalAlignment(SConstants.CENTER);
        }

        super.addComponent(c, constraint, index);
        setPreferredSize(fDim);
    }

    public String getCGClassID() {
        return _cgClassID;
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
