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

package org.wings.header;

import java.io.*;
import org.wings.*;
import org.wings.io.Device;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:hengels@mercatis.de">Holger Engels</a>
 * @version $Revision$
 */
public class Script implements Renderable
{
    protected String language = null;
    protected String type = null;
    protected URLResource urlSource = null;

    public Script(String language, String type, URLResource urlSource) {
	this.language = language;
        this.type = type;
        this.urlSource = urlSource;
    }

    public void setLanguage(String language) {
	this.language = language;
    }
    public String getLanguage() { return language; }

    public void setType(String type) {
	this.type = type;
    }
    public String getType() { return type; }

    public SimpleURL getURL() { return urlSource.getURL(); }

    public void write(Device d)
        throws IOException
    {
        d.print("<script");
        if (language != null)
            d.print(" language=\"" + language + "\"");
        if (type != null)
            d.print(" type=\"" + type + "\"");
        
        if (urlSource != null && urlSource.getURL() != null) {
            d.print(" src=\"");
            urlSource.getURL().write(d);
            d.print("\"");
        }
        d.print("></script>");
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
