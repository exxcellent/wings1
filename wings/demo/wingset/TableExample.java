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

package wingset;

import java.awt.Color;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.wings.SResourceIcon;
import org.wings.SComponent;
import org.wings.SDimension;
import org.wings.SIcon;
import org.wings.SLabel;
import org.wings.SListSelectionModel;
import org.wings.SPanel;
import org.wings.STable;
import org.wings.STemplateLayout;
import org.wings.plaf.css1.TableCG;
import org.wings.table.SDefaultTableCellRenderer;

import wingset.WingSetPane;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class TableExample
    extends WingSetPane
{
    static final SIcon image = new SResourceIcon("org/wings/icons/JavaCup.gif");

    public final MyCellRenderer cellRenderer = new MyCellRenderer();

    final static Color[] colors = {
        Color.black,
        Color.cyan,
        Color.yellow,
        Color.magenta,
        Color.orange,
        Color.pink,
        Color.red,
        Color.darkGray,
        Color.gray,
        Color.green,
        Color.lightGray,
        Color.white,
        Color.blue
    };

    public SComponent createExample() {
        SPanel panel = new SPanel();

        try {
            java.net.URL templateURL = 
                getClass().getResource("/wingset/templates/TableExample.thtml");
            STemplateLayout layout = new STemplateLayout(templateURL);
            panel.setLayout(layout);
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

        STable table = new STable(new MyTableModel(7, 5));
        table.setName("table");
        table.setShowGrid(true);
        table.setSelectionMode(MULTIPLE_SELECTION);
        table.setDefaultRenderer(cellRenderer);
        panel.add(table, "MultiSelectionTable");

        STable formTable = new MyTable(new MyTableModel(7, 5));
        formTable.setName("formtable");
        //formTable.setShowGrid(true);
        formTable.setSelectionMode(SINGLE_SELECTION);
        formTable.setDefaultRenderer(cellRenderer);
        panel.add(formTable, "SingleSelectionTable");

        STable simple = new STable(new ROTableModel(7,10));
        simple.setName("simpletable");
        simple.setAttribute("border", "1px solid black");
        simple.setAttribute("bgcolor", "white");
        simple.setSelectionMode(SListSelectionModel.NO_SELECTION);
        simple.setDefaultRenderer(cellRenderer);
        simple.setHeaderVisible(false);
        panel.add(simple, "ReadOnlyTable");

        return panel;
    }


    static class MyCellRenderer extends SDefaultTableCellRenderer {
        SLabel colorOut = new SLabel();

        public SComponent getTableCellRendererComponent(STable table,
                                                        Object value,
                                                        boolean selected,
                                                        int row,
                                                        int col) {
            if (value instanceof Color) {
                Color c = (Color) value;

                // unfortunately this does not work, because the style sheet visitor does
                // not visit cellrenderers
                // colorOut.setText("[" + c.getRed() + "," 
                //                  + c.getGreen() + "," + c.getBlue() + "]");
                // colorOut.setForeground(c);
                colorOut.setText("<html><span style=\"color:" + colorToHex(c) +
                                 "\">" +
                                 colorToHex(c) +
                                 "</span>");
                return colorOut;
            }
            else
                return super.getTableCellRendererComponent(table,value,
                                                           selected,row,col);
        }
    }

    static class MyTableModel extends AbstractTableModel
    {
        int cols, rows;

        Object[][] data;
        boolean asc[];

        MyTableModel(int cols, int rows) {
            this.cols = cols;
            this.rows = rows;

            data = new Object[rows][cols];
            asc = new boolean[cols];

            for (int c=0; c < cols; c++) {
                for (int r=0; r < rows; r++)
                    data[r][c] = "cell " + r + ":" + c;
            }
            for (int r=0; r < rows; r++)
                data[r][2] = createColor(r);
            for (int r=0; r < rows; r++)
                data[r][3] = createImage(r);
            for (int r=0; r < rows; r++)
                data[r][4] = createBoolean(r);
        }

        public int getColumnCount() {
            return cols;
        }

        public String getColumnName(int col) {
            return "col " + col;
        }

        public int getRowCount() {
            return rows;
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        public void setValueAt(Object value, int row, int col) {
            if (getColumnClass(col).isAssignableFrom(String.class))
                data[row][col] = value.toString();
            else if (getColumnClass(col).isAssignableFrom(Boolean.class))
                data[row][col] = new Boolean(((Boolean)value).booleanValue());
        }

        public Class getColumnClass(int columnIndex) {
            return getValueAt(0, columnIndex).getClass();
        }

        public Color createColor(int row) {
            return colors[row % colors.length];
        }

        public Object createImage(int row) {
            return image;
        }

        public Boolean createBoolean(int row) {
            if (row % 2 == 1)
                return new Boolean(false);
            else
                return new Boolean(true);
        }

        public void sort(int col, boolean ascending) {
            System.out.println("sort");
            if ( col<asc.length )
                asc[col] = !ascending;
        }

        public boolean isCellEditable(int row, int col) {
            if (getColumnClass(col).isAssignableFrom(String.class) ||
                getColumnClass(col).isAssignableFrom(Boolean.class))
                return true;
            else
                return false;
        }
    }

    static class ROTableModel extends MyTableModel
    {
        public ROTableModel(int cols, int rows) {
            super(cols, rows);
        }

        public boolean isCellEditable(int row, int col) { return false; }
    }

    static String colorToHex(Color color) {
	String colorstr = new String("#");

	// Red
	String str = Integer.toHexString(color.getRed());
	if (str.length() > 2)
	    str = str.substring(0, 2);
	else if (str.length() < 2)
	    colorstr += "0" + str;
	else
	    colorstr += str;

	// Green
	str = Integer.toHexString(color.getGreen());
	if (str.length() > 2)
	    str = str.substring(0, 2);
	else if (str.length() < 2)
	    colorstr += "0" + str;
	else
	    colorstr += str;

	// Blue
	str = Integer.toHexString(color.getBlue());
	if (str.length() > 2)
	    str = str.substring(0, 2);
	else if (str.length() < 2)
	    colorstr += "0" + str;
	else
	    colorstr += str;

	return colorstr;
    }
    
    
    /** Proof that we can do some really nice tables with j-wings.  */
    public class MyTable extends STable {
        private final /*static*/ TableCG myTableCG = new TableCG();

        public MyTable(TableModel tm) {
            super(tm);
            myTableCG.setFixedTableBorderWidth("0"); 
            setCG(myTableCG);
            setStyle("table_style");
            setHeaderStyle("table_hdr");
            setIntercellPadding(new SDimension("4", "4"));
            setIntercellSpacing(new SDimension("1", "1"));
        }
    
        /** Returns the CSS style for a row (<td style="xxx") */
        public String getRowStyle(int row) {
            return isRowSelected(row)?"table_selected_row":(row % 2 == 0 ? "table_row1" : "table_row2");
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
