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

import org.wings.SComponent;
import org.wings.SLabel;
import org.wings.STable;
import org.wings.table.STableCellRenderer;

/**
 * TODO: documentation
 *
 * @author Armin Haaf
 * @version $Revision$
 */
public class SizeTableCellRenderer extends SLabel 
    implements STableCellRenderer {

    private static final int KByte = 1024;
    private static final int MByte = KByte*KByte;

    /**
     *
     */
    public SizeTableCellRenderer() {
        setHorizontalAlignment(RIGHT);
    }
  
    public SComponent getTableCellRendererComponent(STable table,
                                                    Object value,
                                                    boolean isSelected,
                                                    int row,
                                                    int column) {

        String text = "";
        if ( value!=null ) {
            long size = ((Long)value).longValue();

            if ( size<KByte ) {
                text = Long.toString(size);
            } else if ( size<MByte ) {
                text = Long.toString(size/KByte) + " kB";
            } else {
                text = Long.toString(size/MByte) + " MB";
            }
        }

        setText("&nbsp;&nbsp;" + text);

        return this;
    }
} // DateTableCellRenderer


/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */





