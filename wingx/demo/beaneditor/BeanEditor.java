/*
 * $Id$
 * (c) Copyright 2000 wingS development team.
 *
 * This file is part of wingS (http://wings.mercatis.de).
 *
 * wingS is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */

package beaneditor;

import java.io.IOException;
import java.net.URL;

import javax.servlet.*;
import javax.servlet.http.*;

import org.wings.*;
import org.wings.plaf.*;
import org.wings.servlet.*;
import org.wings.session.*;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class BeanEditor
    extends WingServlet
{
    public SessionServlet generateSessionServlet(HttpServletRequest req)
        throws Exception
    {
        DefaultSession session = new DefaultSession();

        if (!LookAndFeelFactory.isDeployed("xhtml/css1")) {
            try {
                URL url = servletConfig.getServletContext().getResource("/css1.jar");
                LookAndFeelFactory.deploy(url);
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace(System.err);
            }
        }
        session.getCGManager().setLookAndFeel("xhtml/css1");

        return new BeanEditorSession(session);
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * End:
 */
