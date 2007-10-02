/*
 * $Id$
 * (c) Copyright 2000 wingS development team.
 *
 * This file is part of the wingS demo (http://j-wings.org).
 *
 * The wingS demo is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */

/**
 *
 */
package jfc.TableExample;

import javax.servlet.*;
import javax.servlet.http.*;

import org.wings.*;
import org.wings.session.*;

public class TableExample2
{
    JDBCAdapter dt = null;

    public TableExample2() {
        Session session = SessionManager.getSession();
        String URL = (String)session.getProperty("URL");
        String driver = (String)session.getProperty("driver");
        String user = (String)session.getProperty("user");
        String passwd = (String)session.getProperty("passwd");
        String query = (String)session.getProperty("query");

        SFrame frame = new SFrame("TableExample2");

        try {
            if ( URL!=null && driver!=null && user!=null && passwd!=null &&
                 query!=null && dt==null ) {

                dt = new JDBCAdapter(URL, driver, user, passwd);
                dt.executeQuery(query);

                // Create the table
                STable tableView = new STable(dt);
                SScrollPane scrollpane = new SScrollPane(tableView);
                frame.getContentPane().add(scrollpane);
            }
            else if ( query!=null ) {
                dt.executeQuery(query);
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
            dt = null;
            frame.getContentPane().removeAll();
            frame.getContentPane().add(new SLabel(e.toString()));
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
