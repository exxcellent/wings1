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
package explorer;

import java.io.File;
import org.wings.SComponent;
import org.wings.STree;
import org.wings.tree.SDefaultTreeCellRenderer;

public class DirectoryTreeCellRenderer 
    extends SDefaultTreeCellRenderer 
    implements FileRendererIcons 
{
    public SComponent getTreeCellRendererComponent(STree tree,
                                                   Object value,
                                                   boolean selected,
                                                   boolean expanded,
                                                   boolean leaf,
                                                   int row,
                                                   boolean hasFocus)
    {
	super.getTreeCellRendererComponent(tree,value,selected,expanded,leaf,
					   row, hasFocus);
	File f = (File) value;
	setText(f.getName());  // this returns only the last name component.
        setLeafIcon(f.isDirectory() ? DIR_ICON : FILE_ICON);
	return this;
    }
}
/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
