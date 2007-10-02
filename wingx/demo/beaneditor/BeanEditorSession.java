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

import java.io.*;
import java.awt.event.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.wings.util.*;
import org.wings.*;
import org.wings.servlet.*;
import org.wings.session.*;

import org.wings.io.Device;
import org.wings.io.ServletDevice;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class BeanEditorSession
    extends SessionServlet
{
    private Person person;

    public BeanEditorSession(Session session) {
        super(session);
    }

    public void postInit(ServletConfig config) throws ServletException
    {
        super.postInit( config );

        initGUI();
    }

    void initGUI() {
        SForm form = new SForm();
        org.wingx.beans.beaneditor.BeanEditor beanEditor = new org.wingx.beans.beaneditor.BeanEditor();
        beanEditor.setBean(person = new Person());
        form.add(beanEditor);

        SButton button = new SButton("ok");
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.err.println("" + person);
                }
            });
        form.add(button);

        getFrame().getContentPane().add(form, SBorderLayout.CENTER);
    }

    /**
     * Servletinfo
     */
    public String getServletInfo() {
        return "BeanEditor ($Revision$)";
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * End:
 */
