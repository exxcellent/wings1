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

import java.util.Date;
import java.util.Locale;
import java.text.DateFormat;

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
public class DateTableCellRenderer extends SLabel 
    implements STableCellRenderer {

    private final DateFormat formatter = 
        DateFormat.getDateTimeInstance(DateFormat.SHORT,
                                       DateFormat.MEDIUM,
                                       Locale.GERMAN);

    /**
     *
     */
    public DateTableCellRenderer() {
    }
  
    public SComponent getTableCellRendererComponent(STable table,
                                                    Object value,
                                                    boolean isSelected,
                                                    int row,
                                                    int column) {

        if ( value==null ) {
            setText("&nbsp;");
        } else {
            Date d = (Date)value;

            setText(formatter.format(d));
        }

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





