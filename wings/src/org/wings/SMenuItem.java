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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.*;
import java.net.URL;
import javax.swing.*;

import java.util.ArrayList;

import org.wings.plaf.*;
import org.wings.io.Device;
import org.wings.externalizer.ExternalizeManager;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:andre@lison.de">Andre Lison</a>
 * @author <a href="mailto:armin.haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class SMenuItem extends SButton
{
    private static final String cgClassID = "MenuItemCG";

    protected SMenu menuParent;

    /**
     * Create a new MenuItem.
     * <i>noBreak</i> is set to true
     * @see org.wings.SAbstractButton#setNoBreak(boolean)
     * @param text is display this text ( as href )
     */
    public SMenuItem( String text ) {
        super( text );
    }

    /**
     * Create a new MenuItem with default test "Button".
     * <i>noBreak</i> is set to true
     * @see org.wings.SAbstractButton#setNoBreak(boolean)
     */
    public SMenuItem() {
        setNoBreak( true );
    }


    public SMenuItem(SIcon icon) {
        super(icon);
    }

    public SMenuItem(String text, SIcon icon) {
        super(text, icon);
        setNoBreak( true );
    }

    final void setParentMenu(SMenu menuParent) {
        this.menuParent = menuParent;
        setParentFrame(menuParent!=null ? menuParent.getParentFrame() : null);
    }

    public SMenu getParentMenu() {
        return this.menuParent;
    }

    public String getCGClassID() {
        return cgClassID;
    }

    public void setCG(MenuBarCG cg) {
        super.setCG(cg);
    }}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
