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

import java.net.URL;
import java.net.MalformedURLException;

/**
 * SURLIcon - Some icon whose only thing we know is its URL.
 *
 * @author <a href="mailto:armin@hyperion.intranet.mercatis.de">Armin Haaf</a>
 * @version $Revision
 */
public class SURLIcon extends SAbstractIcon
{

    protected SimpleURL url;

    public SURLIcon(URL u) {
        this(u.toString());
    }

    public SURLIcon(SimpleURL u) {
        url = u;
    }

    public SURLIcon(String u) {
        this(new SimpleURL(u));
    }

    public SURLIcon(String u, int width, int height) {
        this(u);

        setIconWidth(width);
        setIconHeight(height);
    }

  
    public SimpleURL getURL() {
        return url;
    }

}// SURLIcon

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
