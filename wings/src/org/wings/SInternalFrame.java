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

import java.io.*;

import javax.swing.event.EventListenerList;

import org.wings.*;
import org.wings.event.*;
import org.wings.io.*;
import org.wings.plaf.*;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:engels@mercatis.de">Holger Engels</a>
 * @version $Revision$
 */
public class SInternalFrame
    extends SRootContainer
    implements LowLevelEventListener
{
    /**
     * @see #getCGClassID
     */
    private static final String cgClassID = "InternalFrameCG";

    private boolean iconifyable = true;
    private boolean maximizable = true;
    private boolean closable = true;

    private boolean iconified = false;
    private boolean maximized = false;
    private boolean closed = false;

    /**
     * TODO: documentation
     */
    protected SIcon icon = null;

    /**
     * TODO: documentation
     */
    protected String title = null;

    /**
     * TODO: documentation
     *
     */
    public SInternalFrame() {
        super();
    }

    public void setIconifyable(boolean v) {
        boolean old = iconifyable;
        iconifyable = v;
        if (old != iconifyable)
            reload(ReloadManager.RELOAD_CODE);
    }
    public boolean isIconifyable() { return iconifyable; }

    public void setMaximizable(boolean v) {
        boolean old = maximizable;
        maximizable = v;
        if (old != maximizable)
            reload(ReloadManager.RELOAD_CODE);
    }
    public boolean isMaximizable() { return maximizable; }

    public void setClosable(boolean v) {
        boolean old = closable;
        closable = v;
        if (old != closable)
            reload(ReloadManager.RELOAD_CODE);
    }
    public boolean isClosable() { return closable; }

    public void setIconified(boolean v) {
        v &= isIconifyable();
        boolean old = iconified;
        iconified = v;
        if (old != iconified) {
            reload(ReloadManager.RELOAD_CODE);
            if (iconified)
                setMaximized(false);
        }
    }
    public boolean isIconified() { return iconified; }

    public void setMaximized(boolean v) {
        v &= isMaximizable();
        boolean old = maximized;
        maximized = v;
        if (old != maximized) {
            reload(ReloadManager.RELOAD_CODE);
            if (maximized)
                setIconified(false);
        }
    }
    public boolean isMaximized() { return maximized; }

    public void setClosed(boolean v) {
        v &= isClosable();
        boolean old = closed;
        closed = v;
        if (old != closed)
            reload(ReloadManager.RELOAD_CODE);
    }
    public boolean isClosed() { return closed; }

    /**
     * TODO: documentation
     *
     * @param i
     */
    public void setIcon(SIcon i) {
        if ( i!=icon || i!=null && !i.equals(icon) ) {
            icon = i;
            reload(ReloadManager.RELOAD_CODE);
        }
    }

    /**
     * TODO: documentation
     *
     * @return
     */
    public SIcon getIcon() {
        return icon;
    }

    /**
     * TODO: documentation
     *
     * @param t
     */
    public void setTitle(String t) {
        String oldTitle = title;
        title = t;
        if ((title == null && oldTitle != null) ||
            (title != null && !title.equals(oldTitle)))
            reload(ReloadManager.RELOAD_CODE);
    }

    /**
     * TODO: documentation
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * TODO: documentation
     *
     */
    public void dispose() {
        SDesktopPane desktop = (SDesktopPane)getParent();
        desktop.remove(this);
    }

    /**
     * TODO: documentation
     *
     * @param visible
     */
    public void setVisible(boolean visible) {
        if (visible)
            show();
        else
            hide();
    }

    /**
     * TODO: documentation
     *
     */
    public void show() {
        super.setVisible(true);
        if (isIconified())
            setIconified(false);
    }

    /**
     * TODO: documentation
     *
     */
    public void hide() {
        super.setVisible(false);
    }

    /**
     * TODO: documentation
     *
     * @param listener
     */
    public void addInternalFrameListener(SInternalFrameListener listener) {
        addEventListener(SInternalFrameListener.class, listener);
    }

    /**
     * TODO: documentation
     *
     * @param listener
     */
    public void removeInternalFrameListener(SInternalFrameListener listener) {
        removeEventListener(SInternalFrameListener.class, listener);
    }

    private SInternalFrameEvent event;

    // LowLevelEventListener interface. Handle own events.
    public void processLowLevelEvent(String name, String[] values) {
        switch (new Integer(values[0]).intValue()) {
        case SInternalFrameEvent.INTERNAL_FRAME_CLOSED:
            setClosed(true);
            break;

        case SInternalFrameEvent.INTERNAL_FRAME_ICONIFIED:
            setIconified(true);
            break;

        case SInternalFrameEvent.INTERNAL_FRAME_DEICONIFIED:
            setIconified(false);
            break;

        case SInternalFrameEvent.INTERNAL_FRAME_MAXIMIZED:
            setMaximized(true);
            break;

        case SInternalFrameEvent.INTERNAL_FRAME_UNMAXIMIZED:
            setMaximized(false);
            break;

        default:
            throw new RuntimeException("unknown id: " + values[0]);
        }

        event = new SInternalFrameEvent(this, new Integer(values[0]).intValue());
        SForm.addArmedComponent(this); // trigger later invocation of fire*()
    }

    public void fireIntermediateEvents() {}

    public void fireFinalEvents() {
        // Guaranteed to return a non-null array
        Object[] listeners = getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i] == SInternalFrameListener.class) {
                SInternalFrameListener listener;
                listener = (SInternalFrameListener) listeners[i+1];
                switch (event.getID()) {
                case SInternalFrameEvent.INTERNAL_FRAME_CLOSED:
                    listener.internalFrameClosed(event);
                    break;
                    
                case SInternalFrameEvent.INTERNAL_FRAME_ICONIFIED:
                    listener.internalFrameIconified(event);
                    break;
                    
                case SInternalFrameEvent.INTERNAL_FRAME_DEICONIFIED:
                    listener.internalFrameDeiconified(event);
                    break;
                    
                case SInternalFrameEvent.INTERNAL_FRAME_MAXIMIZED:
                    listener.internalFrameMaximized(event);
                    break;
                    
                case SInternalFrameEvent.INTERNAL_FRAME_UNMAXIMIZED:
                    listener.internalFrameUnmaximized(event);
                    break;
                }
            }
        }

        event = null;
    }

    public boolean isEpochChecking() {
        return true;
    }

    public String getCGClassID() {
        return cgClassID;
    }

    public void setCG(InternalFrameCG cg) {
        super.setCG(cg);
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
