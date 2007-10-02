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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.io.Device;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Renders a {@link org.wings.SFrame}.<br>
 * Traverses the component hierarchy of a frame and lets the CGs compose the document.
 *
 * @author <a href="mailto:hengels@mercatis.de">Holger Engels</a>
 * @version $Revision$
 */
public class DynamicCodeResource
    extends DynamicResource
{
    private static Log logger = LogFactory.getLog("org.wings");
    private final PropertyChangeListener changeListener;


    /**
     * Create a code resource for the specified frame.
     * <p>The MIME-type for this frame will be <code>text/html; charset=<i>current encoding</i></code>
     */
    public DynamicCodeResource(final SFrame f) {
        super(f, "html", provideMimeType(f));
        // update session encoding if required.
        changeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                mimeType = provideMimeType(f);
            }
        };
        f.getSession().addPropertyChangeListener(changeListener);
    }

    /**
     * The MIME-type for this {@link Resource}. 
     */
    private static String provideMimeType(SFrame frame) {
        return "text/html; charset=" + frame.getSession().getCharacterEncoding();
    }

    /**
     * Renders and write the code of the {@link SFrame} attached to this <code>DynamicCodeResource</code>. 
     */
    public void write(Device out)
        throws IOException
    {
        try {
            getFrame().write(out);
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            logger.fatal( "resource: " + getId(), e);
            throw new IOException(e.getMessage()); // UndeclaredThrowable
        }
    }

    /**
     * The HTTP header parameteres attached to this dynamic code ressource.
     * This <b>static</b> list will by default contain entries to disable caching 
     * on the server side. Call <code>getHeaders().clear()</code> to avoid this
     * i.e. if you want to enable back buttons. 
     * @return A <code>Collection</code> of {@link HeaderEntry} objects.
     */
    public Collection getHeaders() {
        if (getFrame().isNoCaching())
            return NO_EXPIRATION_HEADERS;
        else
            return Collections.EMPTY_SET;
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
