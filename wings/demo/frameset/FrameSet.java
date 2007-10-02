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

package frameset;

import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.wings.util.*;
import org.wings.*;
import org.wings.event.*;
import org.wings.session.*;

import org.wings.io.Device;
import org.wings.io.ServletDevice;

/**
 * A simple application to demonstrate the features of the ReloadManagerFrame.
 * This frame is used to only reload the frames, whose code actually changed.
 *
 * @author Holger Engels
 * @version $Revision$
 */
public class FrameSet
{
    private SFrameSet vertical;
    private SFrameSet horizontal;
    private SFrame toolbarFrame;
    private SFrame leftFrame;
    private SFrame rightFrame;
    private ReloadManagerFrame reloadManagerFrame;

    private SButton leftButton = new SButton("left frame: 0");
    private SButton rightButton = new SButton("right frame: 0");

    private int leftCount = 0;
    private int rightCount = 0;
    
    private static String VERTICAL_LAYOUT="100,*";

    public FrameSet() {
        vertical = new SFrameSet(new SFrameSetLayout(null, VERTICAL_LAYOUT));
        toolbarFrame = new SFrame("toolbar");
        vertical.add(toolbarFrame);

        horizontal = new SFrameSet(new SFrameSetLayout("50%,50%", null));
        vertical.add(horizontal);
        leftFrame = new SFrame("left frame");
        horizontal.add(leftFrame);
        rightFrame = new SFrame("right frame");
        horizontal.add(rightFrame);

        vertical.setBaseTarget("_top");
        vertical.show();

        buildFrames(toolbarFrame, leftFrame, rightFrame);
        uninstallReloadManagerFrame();

        final Session session = SessionManager.getSession();
        session.addRequestListener(new SRequestListener() {
		public void processRequest(SRequestEvent e) {
		    if (SRequestEvent.DISPATCH_DONE != e.getType())
                        return;

                    if (reloadManagerFrame != null) {
                        //                        e.getRequestedResource().getObject() == reloadManagerFrame.getDynamicResource(DynamicCodeResource.class)) {
                        Set dirtyResources = new HashSet(session.getReloadManager().getDirtyResources());
                        reloadManagerFrame.setDirtyResources(dirtyResources);
                        session.getReloadManager().clear();
                    }
		}
	    });
    }

    protected void buildFrames(SFrame toolbar, SFrame left, SFrame right) {
        ActionListener leftIncrement = new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    leftCount++;
                    leftButton.setText("left frame: " + leftCount);
                }
            };
        ActionListener rightIncrement = new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    rightCount++;
                    rightButton.setText("right frame: " + rightCount);
                }
            };

        ActionListener leftDecrement = new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    leftCount--;
                    leftButton.setText("left frame: " + leftCount);
                }
            };
        ActionListener rightDecrement = new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    rightCount--;
                    rightButton.setText("right frame: " + rightCount);
                }
            };

        // Form to increment all frames.
        SButton leftModifier = new SButton("< increment left");
        leftModifier.addActionListener(leftIncrement);

        SButton rightModifier = new SButton("increment right >");
        rightModifier.addActionListener(rightIncrement);

        SButton bothModifier = new SButton("< increment both >");
        bothModifier.addActionListener(leftIncrement);
        bothModifier.addActionListener(rightIncrement);

        // some explanation.
        toolbar.getContentPane().add(new SLabel("With the reload-manager frame, only frames whose components actually changed, are refetched"));

        SForm incForm = new SForm();
        incForm.add(leftModifier);
        incForm.add(bothModifier);
        incForm.add(rightModifier);
        toolbar.getContentPane().add(incForm);

        left.getContentPane().setLayout(null);
        left.getContentPane().add(leftButton);
        left.getContentPane().add(createDecrementerForm(leftDecrement, 
                                                        rightDecrement));
        left.getContentPane().add(new RenderTimeLabel()); // see below.

        right.getContentPane().setLayout(null);
        right.getContentPane().add(rightButton);
        right.getContentPane().add(createDecrementerForm(leftDecrement, 
                                                         rightDecrement));
        right.getContentPane().add(new RenderTimeLabel()); // see below.

        leftButton.addActionListener(leftDecrement);
        rightButton.addActionListener(rightDecrement);

        final SCheckBox toggleReloadManager = new SCheckBox("use reload manager frame");
        toggleReloadManager.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (toggleReloadManager.isSelected())
                        installReloadManagerFrame();
                    else
                        uninstallReloadManagerFrame();
                }
            });
        toolbar.getContentPane().add(toggleReloadManager);
    }

    SForm createDecrementerForm(ActionListener left, ActionListener right) {
        SButton leftDecrementer = new SButton("decrement left");
        leftDecrementer.addActionListener(left);
        
        SButton rightDecrementer = new SButton("decrement right");
        rightDecrementer.addActionListener(right);
        
        SForm decForm = new SForm();
        decForm.add(leftDecrementer);
        decForm.add(rightDecrementer);
        return decForm;
    }

    void installReloadManagerFrame() {
        // add reload manager frame. Create a frame of size 10 for
        // demonstration purposes. In 'real' applications, this would obviously
        // be of size zero.
        vertical.setLayout(new SFrameSetLayout(null, VERTICAL_LAYOUT+",10"));
        reloadManagerFrame = new ReloadManagerFrame();
        vertical.add(reloadManagerFrame);

        // set base target
        toolbarFrame.setBaseTarget("frame" + reloadManagerFrame.getComponentId());
        leftFrame.setBaseTarget("frame" + reloadManagerFrame.getComponentId());
        rightFrame.setBaseTarget("frame" + reloadManagerFrame.getComponentId());

        DynamicResource targetResource
            = reloadManagerFrame.getDynamicResource(DynamicCodeResource.class);
        // set target resource
        toolbarFrame.setTargetResource(targetResource.getId());
        leftFrame.setTargetResource(targetResource.getId());
        rightFrame.setTargetResource(targetResource.getId());
    }

    void uninstallReloadManagerFrame() {
        if (reloadManagerFrame != null) {
            Set dirtyResources = new HashSet();
            dirtyResources.add(vertical.getDynamicResource(DynamicCodeResource.class));
            reloadManagerFrame.setDirtyResources(dirtyResources);

            vertical.remove(reloadManagerFrame);
            vertical.setLayout(new SFrameSetLayout(null, VERTICAL_LAYOUT));
            reloadManagerFrame = null;
        }

        toolbarFrame.setBaseTarget("_top");
        leftFrame.setBaseTarget("_top");
        rightFrame.setBaseTarget("_top");

        DynamicResource targetResource
            = vertical.getDynamicResource(DynamicCodeResource.class);
        toolbarFrame.setTargetResource(targetResource.getId());
        leftFrame.setTargetResource(targetResource.getId());
        rightFrame.setTargetResource(targetResource.getId());
    }

    /**
     * A Label that returns the current time every time it is rendered.
     * This is to demonstrate, that using the reload-frame does not cause
     * re-rendering of unaffected Frames. In real-live, you would never have
     * a component whose content changes every time it is rendered.
     */
    private static class RenderTimeLabel extends SLabel {
        long startTime = System.currentTimeMillis();
        public String getText() {
            return "(rendered frame at t=" 
                + (System.currentTimeMillis()-startTime)/1000.0 + ")";
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
