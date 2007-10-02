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

package org.wings.plaf.xhtml.css1;

import java.io.*;
import java.util.Properties;
import org.wings.plaf.LookAndFeel;

public class CSS1LookAndFeel
    extends LookAndFeel
{
    public CSS1LookAndFeel() {
	super((Properties)null);
	try {
	    properties = new Properties();
        InputStream in = getClass().getResourceAsStream("/org/wings/plaf/xhtml/css1/default.properties");
        properties.load(in);
        in.close();
	}
	catch (IOException e) {
	    // assert false, e.getMessage();
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
