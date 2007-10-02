/*
 * $Id$
 * (c) Copyright 2001 wingS development team.
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

import java.awt.Color;
import java.beans.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import javax.swing.Icon;

import org.wings.*;
import org.wings.io.Device;
import org.wings.plaf.*;
import org.wings.style.StyleSheet;
import org.wings.session.Session;
import org.wings.session.SessionManager;

/**
 * @author <a href="mailto:engels@mercatis.de">Holger Engels</a>
 * @version $Revision$
 */
public class SFrameSet
extends SFrame {
    
    private boolean frameborderVisible = true;
    
    public SFrameSet() {}
    
    public SFrameSet(SFrameSetLayout layout) {
        setLayout(layout);
    }
    
    public final SContainer getContentPane() {
        return null; // heck :-)
    }
    
    /**
     * Removes the given component from the container.
     *
     * @param c the component to remove
     * @see #removeComponent(org.wings.SComponent)
     */
    public void remove(SComponent c) {
        if ( c==null )  return;

        if ( layout!=null )
            layout.removeComponent(c);

        int index = getComponentList().indexOf(c);
        if ( getComponentList().remove(c) ) {
            getConstraintList().remove(index);
//
//            fireContainerEvent(org.wings.event.SContainerEvent.COMPONENT_REMOVED, c);

            c.setParent(null);
            reload(ReloadManager.RELOAD_CODE);
        }
    }

    /**
     * Removes the component at the given position from the container.
     *
     * @param index remove the component at position <i>index</i>
     * 	from this container
     */
    public void remove(int index) {
        SComponent c = getComponent(index);
        remove(c);
    }

    /**
     * Removes all components from the container.
     */
    public void removeAll() {
        while ( getComponentCount() > 0 ) {
            remove(0);
        }
    }    
    
    /**
     * Only SFrameSets or SFrames are allowed.
     */
    public SComponent addComponent(SComponent c, Object constraint, int index) {
        if (c == null)
            return null;
        
        if (!(c instanceof SFrame))
            throw new IllegalArgumentException("Only SFrameSets or SFrames are allowed.");
        
        if (layout != null)
            layout.addComponent(c, constraint, index);
        
        getComponentList().add(index, c);
        getConstraintList().add(index, constraint);
        c.setParent(this);
        
        return c;
        //return super.addComponent(c, constraint);
    }
    
    /**
     * Only SFrameSets or SFrames are allowed.
     */
    public void removeComponent(SComponent c) {
        if (c == null) return;
        
        if (!(c instanceof SFrame))
            throw new IllegalArgumentException("Only SFrameSets or SFrames are allowed.");
        
        if (layout != null)
            layout.removeComponent(c);
        
        c.setParent(null);
        
        int index = getComponentList().indexOf(c);
        
        if (getComponentList().remove(c)) {
            getConstraintList().remove(index);
        }
        //return super.removeComponent(c);
    }
    
    /**
     * Sets the parent frameset container.
     *
     * @param p the container
     */
    public void setParent(SContainer p) {
        if (!(p instanceof SFrameSet))
            throw new IllegalArgumentException("SFrameSets can only be added to SFrameSets.");
        
        parent = p;
    }
    
    /**
     * There is no parent frame.
     *
     * @param f the frame
     */
    protected void setParentFrame(SFrame f) {}
    
    /**
     * There is no parent frame.
     *
     * @return
     */
    public SFrame getParentFrame() {
        return null;
    }
    
    /**
     * Set the base target and propagate it to all frames
     */
    public void setBaseTarget(String baseTarget) {
        this.baseTarget = baseTarget;
        
        // propagate it to all frame(set)s
        Iterator iterator = getComponentList().iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (object instanceof SFrame)
                ((SFrame)object).setBaseTarget(baseTarget);
        }
    }
    
    public void setLayout(SLayoutManager l) {
        if (!(l instanceof SFrameSetLayout))
            throw new IllegalArgumentException("Only SFrameSetLayout is allowed.");
        
        super.setLayout(l);
    }
    
    public void write(Device s) throws IOException {
        layout.write(s);
    }
    
    public void setFrameborderVisible ( boolean bool ) {
        this.frameborderVisible = bool;
    }
    public boolean isFrameBorderVisible () {
        return frameborderVisible;
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
