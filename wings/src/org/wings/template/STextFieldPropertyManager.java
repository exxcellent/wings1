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

package org.wings.template;

import org.wings.SComponent;
import org.wings.STextField;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class STextFieldPropertyManager
    extends SComponentPropertyManager
{
    static final Class[] classes = {STextField.class};

    public STextFieldPropertyManager() {
    }

    public void setProperty(SComponent comp, String name, String value) {
        STextField c = (STextField)comp;
        if ( name.equals("COLS") )
            c.setColumns(Integer.parseInt(value));
        else if ( name.equals("SIZE") )
            c.setColumns(Integer.parseInt(value));
        else if ( name.equals("MAXSIZE") )
            c.setMaxColumns(Integer.parseInt(value));
        else if ( name.equals("MAXLENGTH") )
            c.setMaxColumns(Integer.parseInt(value));
        else
            super.setProperty(comp, name, value);
    }

    public Class[] getSupportedClasses() {
        return classes;
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
