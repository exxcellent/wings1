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
public class ExplorerPanel
    extends SPanel
{
    private final ExplorerComponents components = new ExplorerComponents();

    public ExplorerPanel(String dir) {
        try {
            java.net.URL templateURL = getClass()
                .getResource("/explorer/Explorer.thtml");
            // you can of course directly give files here.
            STemplateLayout layout = new STemplateLayout( templateURL );
            setLayout(new STemplateLayout(templateURL));
        }
        catch ( Exception e ) {
            setLayout(new SFlowLayout());
        }

	add(components.getTree(), "DirTree");
	add(components.getTable(), "FileTable");
	add(components.createUpload(), "UploadForm");
	add(components.createDeleteButton(), "DeleteButton");
        add(components.getCurrentDirLabel(), "currentDir");
        
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
