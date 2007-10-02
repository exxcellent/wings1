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

package org.wings.batch;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.wings.*;
import org.wings.io.*;
import org.wings.plaf.*;
import org.wings.session.*;
import org.wings.externalizer.*;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:engels@mercatis.de">Holger Engels</a>
 * @version $Revision$
 */
public class BatchSession
    extends Session
{
    ExternalizeManager externalizeManager = new ExternalizeManager(null);

    public BatchSession()
	throws Exception
    {
	SessionManager.setSession(this);
	setReloadManager(new DefaultReloadManager());

	URL url = getClass().getClassLoader().getResource("css1.jar");
	ClassLoader classLoader = new URLClassLoader(new URL[] { url },
						     getClass().getClassLoader());
	LookAndFeel laf = new LookAndFeel(classLoader);
	getCGManager().setLookAndFeel(laf);
    }

    public ExternalizeManager getExternalizeManager() {
	return externalizeManager;
    }

    public static void main(String[] args) {
	try {
	    BatchSession session = new BatchSession();

	    Class clazz = Class.forName(args[0]);
	    SComponent component = (SComponent)clazz.newInstance();

	    BatchFrame frame = new BatchFrame();
	    frame.getContentPane().add(component);
	    frame.show();

	    Device device = new StringBufferDevice();
	    component.write(device);
	    device.flush();
	    device.close();
	    System.out.println("===");
	    System.out.println(device.toString());
	}
	catch (Exception e) {
	    System.err.println(e.getMessage());
	    e.printStackTrace(System.err);
	}
    }
}
