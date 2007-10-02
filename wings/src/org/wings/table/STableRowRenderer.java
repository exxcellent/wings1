package org.wings.table;

import org.wings.STable;

/**
 * 
 * @author armin
 * created at 09.01.2004 11:50:18
 */
public interface STableRowRenderer {
    String getTableRowStyle(STable table, int row, boolean selected);
}
