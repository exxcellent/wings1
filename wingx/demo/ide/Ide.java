/*
 * $Id$
 * (c) Copyright 2000 wingS development team.
 *
 * This file is part of the wingS demo (http://wings.mercatis.de).
 *
 * The wingS demo is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */

package ide;

import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.swing.*;
import javax.swing.event.*;

import org.wings.*;
import org.wings.io.Device;
import org.wings.io.ServletDevice;
import org.wings.servlet.*;
import org.wings.session.*;
import org.wings.util.*;

import org.wings.event.SContainerListener;
import org.wings.event.SContainerEvent;
/**
 * TODO: documentation
 *
 * @author <a href="mailto:haaf@mercatis.de">Holger Engels</a>
 * @version $Revision$
 */
public class Ide
    implements SConstants
{
    Map modules = new HashMap();
    SMenu windowMenu;

    SDesktopPane desktop;
    TreePanel treePanel;
    PropertyPanel propertyPanel;
    LafPanel lafPanel;
    LogPanel logPanel;
    SPanel builderPanel;
    Logger fLogger = Logger.getLogger("ide.Ide");

    public Ide() {
        SFrame frame = new SFrame("Ide");
        fLogger.info("new Ide");
        init(frame);
    }

    protected void init(SFrame aFrame) {
        SContainer contentPane = aFrame.getContentPane();

        SMenuBar menuBar = createMenu();
        contentPane.add(menuBar);

        desktop = new SDesktopPane();
        contentPane.add(desktop);

        // add the frames to the window-menu ..
        desktop.addContainerListener(new SContainerListener() {
                public void componentAdded(SContainerEvent e) {
                    SInternalFrame frame = (SInternalFrame) e.getChild();
                    SMenuItem windowItem = new WindowMenuItem(desktop, frame);
                    windowMenu.add(windowItem);
                }
                public void componentRemoved(SContainerEvent e) {
                    SInternalFrame frame = (SInternalFrame) e.getChild();
                    SMenuItem windowItem = new WindowMenuItem(frame);
                    windowMenu.remove(windowItem);
                }
            });

        // Tree
        SInternalFrame treeFrame = new SInternalFrame();
        treeFrame.setTitle("components");
        addNewFrame(treeFrame);

        treePanel = new TreePanel();
        treeFrame.getContentPane().add(treePanel);
        modules.put("tree", treePanel);

        // Builder
        SInternalFrame builderFrame = new SInternalFrame();
        builderFrame.setTitle("builder");
        addNewFrame(builderFrame);

        builderPanel = new SPanel();
        builderFrame.getContentPane().add(builderPanel);
        modules.put("builder", builderPanel);

        // Property
        SInternalFrame propertyFrame = new SInternalFrame();
        propertyFrame.setTitle("properties");
        addNewFrame(propertyFrame);

        propertyPanel = new PropertyPanel();
        propertyFrame.getContentPane().add(propertyPanel);
        modules.put("properties", propertyPanel);

        treePanel.getTree().addTreeSelectionListener(new TreeSelectionAdapter());
        treePanel.setRoot(builderPanel);

        // for testing ..
        Example example = new Example();
        builderPanel.add(example.createExample());

        // Laf
        SInternalFrame lafFrame = new SInternalFrame();
        lafFrame.setTitle("laf");
        addNewFrame(lafFrame);

        lafPanel = new LafPanel(modules);
        lafFrame.getContentPane().add(lafPanel);
        modules.put("laf", lafPanel);

        // Log
        SInternalFrame logFrame = new SInternalFrame();
        logFrame.setTitle("log");
        addNewFrame(logFrame);

        logPanel = new LogPanel();
        logFrame.getContentPane().add(logPanel);
        modules.put("log", logPanel);

	treeFrame.setClosed(true);
	builderFrame.setClosed(true);
	propertyFrame.setClosed(true);
	logFrame.setClosed(true);
    }

    void list(Object o, String indent) {
        System.out.println(indent + o.getClass().getName());

        if (o instanceof SContainer) {
            SContainer c = (SContainer)o;

            SComponent[] comps = c.getComponents();
            for (int i=0; i < comps.length; i++)
                list(comps[i], indent + "  ");
        }
    }

    protected SMenuBar createMenu() {
        windowMenu = new SMenu("Window");

        SMenuBar menuBar = new SMenuBar();
        menuBar.add(windowMenu);

        return menuBar;
    }

    /**
     * add a new frame. If there is a frame, that is maximized, then
     * move the maximization to the new frame.
     */
    public void addNewFrame(final SInternalFrame frame) {
        desktop.add(frame);
        try {
            desktop.invite(new ComponentVisitor() {
                    public void visit(SComponent c) {
                        if (! (c instanceof SInternalFrame))
                            return;
                        SInternalFrame ff = (SInternalFrame) c;
                        if (ff != frame && ff.isMaximized()) {
                            ff.setMaximized(false);
                            // set _our_ frame maximized, then.
                            frame.setMaximized(true);
                        }
                    }
                    public void visit(SContainer c) {}
                });
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }

    class TreeSelectionAdapter
        implements TreeSelectionListener
    {
        public void valueChanged(TreeSelectionEvent e) {
            SComponent component = (SComponent)treePanel.getTree().getLastSelectedPathComponent();
            if (component == null) {
                System.out.println("nothing selected");
                return;
            }
            SComponent old = propertyPanel.getComponent();
            if (old != null)
                old.setBackground(null);

            component.setBackground(new java.awt.Color(153, 204,204));
            propertyPanel.setComponent(component);
        }
    }

    /**
     * A menu item, that handles the position of an internal frame within
     * the desktop - whenever it is clicked, the frame is put on top.
     */
    private static class WindowMenuItem extends SMenuItem {
        private final SInternalFrame frame;
        
        public WindowMenuItem(SInternalFrame f) {
            frame = f;
        }

        public WindowMenuItem(final SDesktopPane d, final SInternalFrame f) {
            frame = f;
            /*
             * when clicked, put that frame on top. If some other frame was
             * maximized at that point, then maximize _our_ frame instead.
             */
            addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        d.setPosition(f, 0);
                        if (f.isIconified()) {
                            f.setIconified(false);
                        }
                        if (f.isClosed()) {
                            f.setClosed(false);
                        }
                        /*
                         * if some other frame is maximized, then we want
                         * to toggle this maximization..
                         */
                        try {
                            d.invite(new ComponentVisitor() {
                                    public void visit(SComponent c) {
                                        if (! (c instanceof SInternalFrame))
                                            return;
                                        SInternalFrame ff = (SInternalFrame) c;
                                        if (ff != frame && ff.isMaximized()) {
                                            ff.setMaximized(false);
                                            // set _our_ frame maximized, then.
                                            frame.setMaximized(true);
                                        }
                                    }
                                    public void visit(SContainer c) {}
                                });
                        }
                        catch (Exception e) {
                            System.err.println(e);
                        }
                    }
                });
        }

        /**
         * returns the title of the frame.
         */
        public String getText() {
            String title = frame.getTitle();
            return (title == null || title.length()==0) ? "[noname]" : title;
        }
        
        /**
         * remove menu item by frame ..
         */
        public boolean equals(Object o) {
            if (o instanceof WindowMenuItem) {
                WindowMenuItem wme = (WindowMenuItem) o;
                return (frame != null && wme.frame == frame);
            }
            return false;
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
