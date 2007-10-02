/*
 * $Id$
 * (c) Copyright 2000 wingS development team.
 *
 * This file is part of wingS (http://wings.mercatis.de).
 *
 * wingS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */

package org.wingx.plaf;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.wings.RequestURL;
import org.wings.SComponent;
import org.wings.SFrame;
import org.wings.SScrollPane;
import org.wings.Scrollable;
import org.wings.externalizer.ExternalizeManager;
import org.wings.io.Device;
import org.wings.io.StringBufferDevice;
import org.wings.plaf.css1.FrameCG;
import org.wings.session.SessionManager;
import org.wings.style.StyleSheet;

public final class IFrameScrollPaneCG
    implements org.wings.plaf.ScrollPaneCG
{
    public void installCG(SComponent component) {}
    public void uninstallCG(SComponent component) {}
	Logger fLogger = Logger.getLogger("org.wingx.plaf.IFrameScrollPaneCG");

    public void write(Device d, SComponent c) throws IOException
    {
        SScrollPane scrollPane = (SScrollPane) c;

        // write the contents to another device
        SComponent contents = (SComponent) scrollPane.getScrollable();
        Scrollable scrollable = (Scrollable) contents;
        Rectangle rect = scrollable.getViewportSize();
        // rect.setSize(scrollable.getScrollableViewportSize());

        if (contents != null)
        {
            Device document = new StringBufferDevice();
            SFrame frame = c.getParentFrame();
            writeDocumentPrefix(document, frame);
            contents.write(document);
            writeDocumentPostfix(document);

            ExternalizeManager ext = SessionManager.getSession().getExternalizeManager();
            if (ext != null)
            {
                try
                {
                    d.print("<iframe frameborder=\"0\" src=\"");
                    d.print(ext.externalize(document));
                    d.print("\" width=\"");
                    d.print(scrollPane.getHorizontalExtent());
                    d.print("\" height=\"");
                    d.print(scrollPane.getVerticalExtent());
                    d.print("\"></iframe>");
                }
                catch (java.io.IOException e)
                {
                    d.print("sorry: something went wrong");
                    // dann eben nicht !!
                    e.printStackTrace(System.err);
                    fLogger.log(Level.ALL, e.toString());
                }
            }
        }
    }

    protected void writeDocumentPrefix(Device d, SFrame frame)
    	throws IOException
    {
        String language = "en"; // TODO: ???

        d.print("<?xml version=\"1.0\" encoding=\"");
        // d.print(FrameCG.(frame.getSession().getLocale()));
        d.print(org.wings.util.LocaleCharSet.getInstance().getCharSet(
        	frame.getSession().getLocale()));
        d.print("\"?>\n");
        d.print("<!DOCTYPE html\n");
		d.print("   PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n");
        d.print("   \"DTD/xhtml1-transitional.dtd\">\n");
        d.print("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"");
        d.print(language);
        d.print("\" lang=\"");
        d.print(language);
        d.print("\">\n");
        d.print("<head>");
        d.print("<meta http-equiv=\"Content-type\" content='text/html; charset=\"");
        //d.print(FrameCG.charSetFor(frame.getSession().getLocale()));
        d.print(org.wings.util.LocaleCharSet.getInstance().getCharSet(
        	frame.getSession().getLocale()));
        d.print("\"' />\n");
        d.print("<meta http-equiv=\"expires\" content=\"0\" />\n");
        d.print("<meta http-equiv=\"pragma\" content=\"no-cache\" />\n");
        d.print("<base target=\"_top\">\n");
        d.print("</head>\n");
	
        StyleSheet styleSheet = frame.getStyleSheet();
        if (styleSheet != null) {
            ExternalizeManager ext = SessionManager.getSession().getExternalizeManager();
            String link = null;

            if (ext != null) {
				link = ext.externalize(styleSheet);
            }
            if (link != null) {
                d.print("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
                d.print(link);
                d.print("\" />");
            }
        }
        else {
            fLogger.log(Level.WARNING, "Frame.styleSheet == null!");
        }
    }

    protected void writeDocumentPostfix(Device d)
    	throws IOException
    {
		d.print("</body></html>");
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * End:
 */
