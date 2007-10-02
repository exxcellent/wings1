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

package explorer;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.tree.*;
import javax.swing.event.*;

import org.wings.*;

/**
 * TODO: documentation
 *
 * @author Holger Engels
 * @author Andreas Gruener
 * @author Armin Haaf
 * @version $Revision$
 */
public class FExplorerPanel
    extends SFrameSet
{
    private final ExplorerComponents components = new ExplorerComponents();

    public FExplorerPanel(String dir) {
        super(new SFrameSetLayout(null, "*,50"));

        // build frames
        SFrame tableFrame = new SFrame("Table");
        SFrame treeFrame = new SFrame("Tree");
        SFrame toolbarFrame = new SFrame("Toolbar");

        SFrameSet vertical = new SFrameSet(new SFrameSetLayout("200,*", null));
        vertical.add(treeFrame);
        vertical.add(tableFrame);

        add(vertical);
        add(toolbarFrame);

	treeFrame.getContentPane().add(components.getTree());
	tableFrame.getContentPane().add(components.getTable());
        toolbarFrame.getContentPane().add(components.getCurrentDirLabel());
	toolbarFrame.getContentPane().add(components.createUpload());
	toolbarFrame.getContentPane().add(components.createDeleteButton());
        
        components.setExplorerBaseDir(dir);
    }

    public void setExplorerBaseDir(String dir) {
        components.setExplorerBaseDir(dir);
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
