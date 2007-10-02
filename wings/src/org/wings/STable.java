/*
 * $Id$
 * (c) Copyright 2000 wingS development team.
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
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;

import javax.swing.event.*;
import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListSelectionModel;

import org.wings.table.*;
import org.wings.externalizer.ExternalizeManager;
import org.wings.plaf.*;
import org.wings.style.*;


/**
 * TODO: documentation
 *
 * @author <a href="mailto:engels@mercatis.de">Holger Engels</a>
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class STable
    extends SComponent
    implements TableModelListener, Scrollable, CellEditorListener, LowLevelEventListener, SSelectionComponent, SForm.FormComponent {
    /**
     * <p>stores the ID of the class to access the <code>ComponentGenerator</code>.</p>
     * @see #getCGClassID
     */
    private static final String cgClassID = "TableCG";

    /**
     * <p>the table model.</p>
     */
    protected TableModel model;

    /**
     * <p>the selection model.</p>
     */
    protected SListSelectionModel selectionModel;

    /**
     * <p>The default renderer is used if no other renderer is set for the
     * content of a cell.</p>
     **/
    protected STableCellRenderer defaultRenderer;

    /**
     * <p>The <code>headerRenderer</code> is used to render the header line.</p>
     **/
    protected STableCellRenderer headerRenderer;

    /**
     * is used to get the row style
     */
    protected STableRowRenderer rowRenderer;

    /**
     * <p>In this <code>Map</code>, the renderers for the different
     * classes of cell content are stored.</p><p>The class is treated
     * as key, the renderer as the value.</p>
     **/
    protected final HashMap renderer = new HashMap();

    /**
     * <p>If editing, this is the <code>SComponent</code> that is handling the editing.
     **/
    transient protected SComponent editorComp;

    /**
     * <p>The object that overwrites the screen real estate occupied by the
     * current cell and allows the user to change those contents.</p>
     **/
    transient protected STableCellEditor cellEditor;

    /**
     * <p>Identifies the column of the cell being edited.</p>
     **/
    transient protected int editingColumn = -1;

    /**
     * <p>Identifies the row of the cell being edited.</p>
     **/
    transient protected int editingRow = -1;

    /**
     * <p>In this <code>Map</code>, the <code>STableCellEditor</code>s for the different
     * classes of cell content are stored.</p><p>The class is treated
     * as key, the <code>STableCellEditor</code> as the value.</p>
     **/
    protected final HashMap editors = new HashMap();

    /**
     * <p>The style of selected cells.</p>
     **/
    protected String selectionStyle;

    /**
     * <p>The dynamic attributes of selected cells.</p>
     **/
    protected AttributeSet selectionAttributes = new SimpleAttributeSet();

    /**
     * <p>Determines whether the header is visible or not.</p><p>By
     * default the header is visible.</p> <p><em>CAVEAT:</em>The
     * header is not (yet) implemented like in Swing. But maybe
     * someday.  So you can disable it if you like. TODO.</p>
     */
    protected boolean headerVisible = true;

    /**
     * <p>The style of header cells.</p>
     **/
    protected String headerStyle;

    /** The dynamic attributes of header cells */
    protected AttributeSet headerAttributes = new SimpleAttributeSet();

    /**
     * <p>Determines if horizontal lines in the table should be
     * painted.</p><p>This is off by default.</p>
     **/
    protected boolean showHorizontalLines = false;

    /**
     * <p>Determines if vertical lines in the table should be
     * painted.</p><p>This is off by default.</p>
     **/
    protected boolean showVerticalLines = false;

    /**
     * TODO: documentation
     */
    protected SDimension intercellSpacing;

    /**
     * TODO: documentation
     */
    protected SDimension intercellPadding = new SDimension("1", "1");

    /**
     * <p>A special cell renderer, that displays the control used to select
     * a table row.</p><p>Ususally, this would be some checkbox. The plaf is the
     * last instance to decide this.</p>
     */
    protected STableCellRenderer rowSelectionRenderer;

    /**
     * <p>The column where the row selection element should be rendered. If
     * negative, no selection element is rendered.</p>
     **/
    protected int rowSelectionColumn = Integer.MAX_VALUE;

    /**
     * TODO: documentation
     */
    protected Rectangle viewport;

    /**
     * <p>indicates if this component - if it is inside a {@link SForm} -  renders
     * itself as form component or not.</p>
     */
    private boolean showAsFormComponent = true;

    /**
     * <p>Determines an icon for a selected row.</p>
     * <p>Default is none.</p>
     */
    protected SIcon fSelectedIcon;

    /**
     * <p>Determines an icon for a deselected row.</p>
     * <p>Default is none.</p>
     **/
    protected SIcon fDeselectedIcon;
    
    /** @see LowLevelEventListener#isEpochChecking() */
    protected boolean epochChecking = true;       

    /**
     * <p>Creates a new <code>STable</code>.</p>
     **/
    public STable() {
        this(null);
    }

    /**
     * <p>Creates a new <code>STable</code>.</p>
     *
     * @param tm the <code>TableModel</code> for the table's contents.
     **/
    public STable(TableModel tm) {
        setSelectionModel(new SDefaultListSelectionModel());
        createDefaultEditors();
        setModel(tm);
    }

    /**
     * <p>Sets the model of the table.</p>
     *
     * @param tm the <code>TableModel</code> to set.
     **/
    public void setModel(TableModel tm) {
        if (model != null)
            model.removeTableModelListener(this);

        reloadIfChange(ReloadManager.RELOAD_CODE, model, tm);

        model = tm;
        if (model == null)
            model = new DefaultTableModel();

        model.addTableModelListener(this);
    }

    /**
     * <p>returns the model of the table</p>
     *
     * @return
     */
    public TableModel getModel() {
        return model;
    }

    /**
     * TODO: documentation
     *
     * @return
     */
    public int getColumnCount() {
        return model.getColumnCount();
    }

    /**
     * TODO: documentation
     *
     * @param col
     * @return
     */
    public String getColumnName(int col) {
        return model.getColumnName(col);
    }

    /**
     * TODO: documentation
     *
     * @param col
     * @return
     */
    public Class getColumnClass(int col) {
        return model.getColumnClass(col);
    }

    /**
     * indicates if this component - if it is inside a {@link SForm} -  renders
     * itself as form component or not.
     */
    public void setShowAsFormComponent(boolean showAsFormComponent) {
        this.showAsFormComponent = showAsFormComponent;
    }

    /**
     * is this component rendered as form component.
     * @return true, if the component resides in a {@link SForm} and
     * {@link #setShowAsFormComponent} is set to true (the default)
     */
    public boolean getShowAsFormComponent() {
        return showAsFormComponent && getResidesInForm();
    }

    /**
     * TODO: documentation
     *
     * @return
     */
    public int getRowCount() {
        return model.getRowCount();
    }

    public Object getValueAt(int row, int column) {
        return model.getValueAt(row, column);
    }

    public void setValueAt(Object v, int row, int column) {
        model.setValueAt(v, row, column);
    }

    public int convertColumnIndexToModel(int viewColumnIndex) {
        return viewColumnIndex;
    }

    /**
     * Adds the row from <i>index0</i> to <i>index0</i> inclusive to the current selection.
     */
    public void addRowSelectionInterval(int index0, int index1) {
        selectionModel.addSelectionInterval(index0, index1);
    }

    public void setParent(SContainer p) {
        super.setParent(p);

        if (getCellRendererPane() != null)
            getCellRendererPane().setParent(p);

        if (editorComp != null)
            editorComp.setParent(p);
    }

    protected void setParentFrame(SFrame f) {
        super.setParentFrame(f);
        if (getCellRendererPane() != null)
            getCellRendererPane().setParentFrame(f);
    }

    public void processLowLevelEvent(String action, String[] values) {
        // is it for me ?
        /*
        if ( !action.startsWith(getLowLevelEventId()) ) {
            return;
        }
        */

        // delay events...
        getSelectionModel().setDelayEvents(true);
        getSelectionModel().setValueIsAdjusting(true);

        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            if (value.length() > 1) {

                char modus = value.charAt(0);
                value = value.substring(1);

                int colonIndex = value.indexOf(':');
                if (colonIndex < 0)
                    continue; // maybe next value fits ...

                try {

                    int row = Integer.parseInt(value.substring(0, colonIndex));
                    int col = Integer.parseInt(value.substring(colonIndex + 1));
                    // editor event
                    switch (modus) {
                        case 'e':
                            editCellAt(row, col, null);
                            break;
                        case 't':
                            if (getSelectionModel().isSelectedIndex(row))
                                getSelectionModel().removeSelectionInterval(row, row);
                            else
                                getSelectionModel().addSelectionInterval(row, row);
                            break;
                        case 's':
                            getSelectionModel().addSelectionInterval(row, row);
                            break;
                        case 'd':
                            getSelectionModel().removeSelectionInterval(row, row);
                            break;
                    }
                } catch (NumberFormatException ex) {
                    // hier loggen...
                    ex.printStackTrace();
                }
            }
        }

        getSelectionModel().setValueIsAdjusting(false);
        getSelectionModel().setDelayEvents(false);
        SForm.addArmedComponent(this);

    }

    private SCellRendererPane cellRendererPane = new SCellRendererPane();

    /**
     * TODO: documentation
     *
     * @return
     */
    public SCellRendererPane getCellRendererPane() {
        return cellRendererPane;
    }

    /**
     * TODO: documentation
     *
     * @param r
     */
    public void setDefaultRenderer(STableCellRenderer r) {
        defaultRenderer = r;
    }

    /**
     * TODO: documentation
     *
     * @return
     */
    public STableCellRenderer getDefaultRenderer() {
        return defaultRenderer;
    }

    public void setDefaultRenderer(Class columnClass, STableCellRenderer r) {
        renderer.remove(columnClass);
        if (renderer != null)
            renderer.put(columnClass, r);
    }

    /**
     * TODO: documentation
     *
     * @param columnClass
     * @return
     */
    public STableCellRenderer getDefaultRenderer(Class columnClass) {
        if (columnClass == null) {
            return defaultRenderer;
        } else {
            Object r = renderer.get(columnClass);
            if (r != null) {
                return (STableCellRenderer) r;
            } else {
                return getDefaultRenderer(columnClass.getSuperclass());
            }
        }
    }

    /**
     * TODO: documentation
     *
     * @param r
     */
    public void setHeaderRenderer(STableCellRenderer r) {
        headerRenderer = r;
    }

    /**
     * TODO: documentation
     *
     * @return
     */
    public STableCellRenderer getHeaderRenderer() {
        return headerRenderer;
    }

    public STableRowRenderer getRowRenderer() {
        return rowRenderer;
    }

    public void setRowRenderer(STableRowRenderer pRowRenderer) {
        rowRenderer = pRowRenderer;
    }

    public STableCellRenderer getCellRenderer(int row, int column) {
        return getDefaultRenderer(getColumnClass(column));
    }

    public SComponent prepareRenderer(STableCellRenderer r, int row, int col) {
        return r.getTableCellRendererComponent(this,
                                               model.getValueAt(row, col),
                                               isRowSelected(row),
                                               row, col);
    }

    /**
     * TODO: documentation
     *
     * @param col
     * @return
     */
    public SComponent prepareHeaderRenderer(int col) {
        return headerRenderer.getTableCellRendererComponent(this,
                                                            col >= 0 ? model.getColumnName(col) : null,
                                                            false,
                                                            -1, col);
    }

    public void setRowSelectionRenderer(STableCellRenderer r) {
        rowSelectionRenderer = r;
    }

    public STableCellRenderer getRowSelectionRenderer() {
        return rowSelectionRenderer;
    }

    /**
     * Set a default editor to be used if no editor has been set in
     * a TableColumn. If no editing is required in a table, or a
     * particular column in a table, use the isCellEditable()
     * method in the TableModel interface to ensure that the
     * STable will not start an editor in these columns.
     * If editor is null, remove the default editor for this
     * column class.
     *
     * @see     TableModel#isCellEditable
     * @see     #getDefaultEditor
     * @see     #setDefaultRenderer
     */
    public void setDefaultEditor(Class columnClass, STableCellEditor r) {
        editors.remove(columnClass);
        if (editors != null)
            editors.put(columnClass, r);
    }

    /*
     * Returns the editor to be used when no editor has been set in
     * a TableColumn. During the editing of cells the editor is fetched from
     * a Map of entries according to the class of the cells in the column. If
     * there is no entry for this <I>columnClass</I> the method returns
     * the entry for the most specific superclass. The STable installs entries
     * for <I>Object</I>, <I>Number</I> and <I>Boolean</I> all which can be modified
     * or replaced.
     *
     * @param columnClass
     * @return
     * @see     #setDefaultEditor
     * @see     #getColumnClass
     */
    public STableCellEditor getDefaultEditor(Class columnClass) {
        if (columnClass == null) {
            return null;
        } else {
            Object r = editors.get(columnClass);
            if (r != null) {
                return (STableCellEditor) r;
            } else {
                return getDefaultEditor(columnClass.getSuperclass());
            }
        }
    }

    //
    // Editing Support
    //

    /**
     * Programmatically starts editing the cell at <I>row</I> and
     * <I>column</I>, if the cell is editable.
     *
     * @param   row                             the row to be edited
     * @param   column                          the column to be edited
     * @exception IllegalArgumentException      If <I>row</I> or <I>column</I>
     *                                          are not in the valid range
     * @return  false if for any reason the cell cannot be edited.
     */
    public boolean editCellAt(int row, int column) {
        return editCellAt(row, column, null);
    }

    /**
     * Programmatically starts editing the cell at <I>row</I> and
     * <I>column</I>, if the cell is editable.
     * To prevent the STable from editing a particular table, column or
     * cell value, return false from the isCellEditable() method in the
     * TableModel interface.
     *
     * @param   row                             the row to be edited
     * @param   column                          the column to be edited
     * @param   e                               event to pass into
     *                                          shouldSelectCell
     * @exception IllegalArgumentException      If <I>row</I> or <I>column</I>
     *                                          are not in the valid range
     * @return  false if for any reason the cell cannot be edited.
     */
    public boolean editCellAt(int row, int column, EventObject e) {
        if (isEditing()) {
            // Try to stop the current editor
            if (cellEditor != null) {
                boolean stopped = cellEditor.stopCellEditing();
                if (!stopped)
                    return false;       // The current editor not resigning
            }
        }

        if (!isCellEditable(row, column))
            return false;

        STableCellEditor editor = getCellEditor(row, column);
        if (editor != null) {
            // set up editor environment and make it possible for the editor, to
            // stop/cancel editing on preparation
            editor.addCellEditorListener(this);
            setCellEditor(editor);
            setEditingRow(row);
            setEditingColumn(column);

            // prepare editor
            editorComp = prepareEditor(editor, row, column);

            if (editor.isCellEditable(e) && editor.shouldSelectCell(e)) {
                return true;
            } else {
                setValueAt(editor.getCellEditorValue(), row, column);
                removeEditor();
            } // end of else

        }
        return false;
    }

    /**
     * Returns true if the cell at <I>row</I> and <I>column</I>
     * is editable.  Otherwise, setValueAt() on the cell will not change
     * the value of that cell.
     *
     * @param   row      the row whose value is to be looked up
     * @param   col the column whose value is to be looked up
     * @return  true if the cell is editable.
     * @see #setValueAt
     */
    public boolean isCellEditable(int row, int col) {
        if (col >= getColumnCount() || row == -1)
            return false;
        else
            return getModel().isCellEditable(row, col);
    }

    /**
     * Returns  true is the table is editing a cell.
     *
     * @return  true is the table is editing a cell
     * @see     #editingColumn
     * @see     #editingRow
     */
    public boolean isEditing() {
        return (cellEditor == null) ? false : true;
    }

    /**
     * If the receiver is currently editing this will return the Component
     * that was returned from the CellEditor.
     *
     * @return  SComponent handling editing session
     */
    public SComponent getEditorComponent() {
        return editorComp;
    }

    /**
     * This returns the index of the editing column.
     *
     * @return  the index of the column being edited
     * @see #editingRow
     */
    public int getEditingColumn() {
        return editingColumn;
    }

    /**
     * Returns the index of the editing row.
     *
     * @return  the index of the row being edited
     * @see #editingColumn
     */
    public int getEditingRow() {
        return editingRow;
    }

    /**
     * Return the cellEditor.
     *
     * @return the STableCellEditor that does the editing
     * @see #cellEditor
     */
    public STableCellEditor getCellEditor() {
        return cellEditor;
    }

    /**
     * Set the cellEditor variable.
     *
     * @param anEditor  the STableCellEditor that does the editing
     * @see #cellEditor
     */
    protected void setCellEditor(STableCellEditor anEditor) {
        STableCellEditor oldEditor = cellEditor;
        cellEditor = anEditor;
    }

    /**
     * Set the editingColumn variable.
     *
     * @see #editingColumn
     */
    public void setEditingColumn(int aColumn) {
        int oldEditingColumn = editingColumn;
        editingColumn = aColumn;
        if (editingColumn != oldEditingColumn)
            reload(ReloadManager.RELOAD_CODE);
    }

    /**
     * Set the editingRow variable.
     *
     * @see #editingRow
     */
    public void setEditingRow(int aRow) {
        int oldEditingRow = editingRow;
        editingRow = aRow;
        if (editingRow != oldEditingRow)
            reload(ReloadManager.RELOAD_CODE);
    }

    /**
     * Return an appropriate editor for the cell specified by this row and
     * column. If the TableColumn for this column has a non-null editor, return that.
     * If not, find the class of the data in this column (using getColumnClass())
     * and return the default editor for this type of data.
     *
     * @param row       the row of the cell to edit, where 0 is the first
     * @param column    the column of the cell to edit, where 0 is the first
     */
    public STableCellEditor getCellEditor(int row, int column) {
        // TableColumn tableColumn = getColumnModel().getColumn(column);
        // STableCellEditor editor = tableColumn.getCellEditor();
        // if (editor == null) {
        STableCellEditor editor = getDefaultEditor(getColumnClass(column));
        // }
        return editor;
    }


    /**
     * Prepares the specified editor using the value at the specified cell.
     *
     * @param editor  the TableCellEditor to set up
     * @param row     the row of the cell to edit, where 0 is the first
     * @param col     the column of the cell to edit, where 0 is the first
     */
    protected SComponent prepareEditor(STableCellEditor editor, int row, int col) {
        return editor.getTableCellEditorComponent(this,
                                                  model.getValueAt(row, col),
                                                  isRowSelected(row), // true?
                                                  row, col);
    }

    /**
     * Discard the editor object and return the real estate it used to
     * cell rendering.
     */
    public void removeEditor() {
        STableCellEditor editor = getCellEditor();
        if (editor != null) {
            editor.removeCellEditorListener(this);
            //remove(editorComp);
            setCellEditor(null);
            setEditingColumn(-1);
            setEditingRow(-1);
            if (editorComp != null) {
                editorComp.setParent(null);
            } // end of if ()
            editorComp = null;
        }
    }


    //
    // Implementing the CellEditorListener interface
    //

    /**
     * Invoked when editing is finished. The changes are saved and the
     * editor object is discarded.
     *
     * @see CellEditorListener
     */
    public void editingStopped(ChangeEvent e) {
        // Take in the new value
        STableCellEditor editor = getCellEditor();
        if (editor != null) {
            Object value = editor.getCellEditorValue();
            setValueAt(value, editingRow, editingColumn);
            removeEditor();
            reload(ReloadManager.RELOAD_CODE);
        }
    }

    /**
     * Invoked when editing is canceled. The editor object is discarded
     * and the cell is rendered once again.
     *
     * @see CellEditorListener
     */
    public void editingCanceled(ChangeEvent e) {
        removeEditor();
        reload(ReloadManager.RELOAD_CODE);
    }

    /**
     * Creates default cell editors for Objects, numbers, and boolean values.
     */
    protected void createDefaultEditors() {
        editors.clear();

        // Objects
        STextField textField = new STextField();
        setDefaultEditor(Object.class, new SDefaultCellEditor(textField));
        setDefaultEditor(Number.class, new SDefaultCellEditor(textField));

        // Numbers
        //STextField rightAlignedTextField = new STextField();
        //rightAlignedTextField.setHorizontalAlignment(STextField.RIGHT);
        //setDefaultEditor(Number.class, new SDefaultCellEditor(rightAlignedTextField));

        // Booleans
        SCheckBox centeredCheckBox = new SCheckBox();
        //centeredCheckBox.setHorizontalAlignment(JCheckBox.CENTER);
        setDefaultEditor(Boolean.class, new SDefaultCellEditor(centeredCheckBox));
    }


    /**
     * TODO: documentation
     *
     * @return
     */
    public SListSelectionModel getSelectionModel() {
        return selectionModel;
    }

    /**
     * Sets the row selection model for this table to <code>model</code>.
     *
     * @param   model        the new selection model
     * @exception IllegalArgumentException    if <code>model</code>
     *                                        is <code>null</code>
     * @see     #getSelectionModel
     */
    public void setSelectionModel(SListSelectionModel model) {
        if (getSelectionModel() != null) {
            removeSelectionListener(reloadOnSelectionChangeListener);
        }

        if (model == null) {
            throw new IllegalArgumentException("cannot set a null SListSelectionModel");
        }
        selectionModel = model;

        addSelectionListener(reloadOnSelectionChangeListener);
    }

    /**
     * TODO: documentation
     *
     * @return
     */
    public int getSelectedRowCount() {
        int result = 0;
        for (int i = getSelectionModel().getMinSelectionIndex();
             i <= getSelectionModel().getMaxSelectionIndex(); i++) {
            if (getSelectionModel().isSelectedIndex(i))
                result++;
        }

        return result;
    }

    /**
     * TODO: documentation
     *
     * @return
     */
    public int getSelectedRow() {
        return getSelectionModel().getMinSelectionIndex();
    }

    public int[] getSelectedRows() {
        int[] result = new int[getSelectedRowCount()];

        int index = 0;
        for (int i = getSelectionModel().getMinSelectionIndex();
             i <= getSelectionModel().getMaxSelectionIndex(); i++) {
            if (getSelectionModel().isSelectedIndex(i))
                result[index++] = i;
        }

        return result;
    }

    /**
     * Deselects all selected columns and rows.
     */
    public void clearSelection() {
        getSelectionModel().clearSelection();
    }


    /**
     * TODO: documentation
     *
     * @param row
     * @return
     */
    public boolean isRowSelected(int row) {
        return getSelectionModel().isSelectedIndex(row);
    }

    /**
     * Sets the selection mode. Use one of the following values:
     * <UL>
     * <LI> {@link SConstants#NO_SELECTION}
     * <LI> {@link ListSelectionModel#SINGLE_SELECTION} or
     *      {@link SConstants#SINGLE_SELECTION}
     * <LI> {@link ListSelectionModel#SINGLE_INTERVAL_SELECTION} or
     *      {@link SConstants#SINGLE_INTERVAL_SELECTION}
     * <LI> {@link ListSelectionModel#MULTIPLE_INTERVAL_SELECTION} or
     *      {@link SConstants#MULTIPLE_SELECTION}
     * </UL>
     */
    public void setSelectionMode(int s) {
        getSelectionModel().setSelectionMode(s);
    }

    /**
     * TODO: documentation
     * @return
     * <UL>
     * <LI> {@link SConstants#NO_SELECTION}
     * <LI> {@link ListSelectionModel#SINGLE_SELECTION} or
     *      {@link SConstants#SINGLE_SELECTION}
     * <LI> {@link ListSelectionModel#SINGLE_INTERVAL_SELECTION} or
     *      {@link SConstants#SINGLE_INTERVAL_SELECTION}
     * <LI> {@link ListSelectionModel#MULTIPLE_INTERVAL_SELECTION} or
     *      {@link SConstants#MULTIPLE_SELECTION}
     * </UL>
     */
    public int getSelectionMode() {
        return getSelectionModel().getSelectionMode();
    }

    /**
     * TODO: documentation
     *
     * @param listener
     */
    public void addSelectionListener(ListSelectionListener listener) {
        getSelectionModel().addListSelectionListener(listener);
    }

    /**
     * TODO: documentation
     *
     * @param listener
     */
    public void removeSelectionListener(ListSelectionListener listener) {
        getSelectionModel().removeListSelectionListener(listener);
    }

    public void fireIntermediateEvents() {
        getSelectionModel().fireDelayedIntermediateEvents();
    }

    public void fireFinalEvents() {
        // fire selection events...
        getSelectionModel().fireDelayedFinalEvents();
    }

    /** @see LowLevelEventListener#isEpochChecking() */
    public boolean isEpochChecking() {
        return epochChecking;
    }
  
    /** @see LowLevelEventListener#isEpochChecking() */
    public void setEpochChecking(boolean epochChecking) {
        this.epochChecking = epochChecking;
    } 

    /**
     * TODO: documentation
     *
     * @param e
     */
    public void tableChanged(TableModelEvent e) {
        // kill active editors
        editingCanceled(null);

        if (e == null || e.getFirstRow() == TableModelEvent.HEADER_ROW) {
            // The whole thing changed
            clearSelection();
        } else {
            switch (e.getType()) {
                case TableModelEvent.INSERT:
                    if (e.getFirstRow() >= 0)
                        getSelectionModel().insertIndexInterval(e.getFirstRow(),
                                                                e.getLastRow(), true);
                    break;

                case TableModelEvent.DELETE:
                    if (e.getFirstRow() >= 0)
                        getSelectionModel().removeIndexInterval(e.getFirstRow(),
                                                                e.getLastRow());
                    break;
            }
        }
        reload(ReloadManager.RELOAD_CODE);
    }

    /**
     * @param selectionStyle the style of selected cells
     */
    public void setSelectionStyle(String selectionStyle) {
        this.selectionStyle = selectionStyle;
    }

    /**
     * @return the style of selected cells.
     */
    public String getSelectionStyle() {
        return selectionStyle;
    }

    /**
     * Override this method, if you want to give rows different
     * attributes. E.g. for displaying an alternating background color
     * for rows.
     *
     * @return the style of a specific row.
     */
    public String getRowStyle(int row) {
        if (rowRenderer != null) {
            return rowRenderer.getTableRowStyle(this, row, isRowSelected(row));
        } else {
            return null;
        }
    }

    /**
     * Set the selectionAttributes.
     * @param selectionAttributes the selectionAttributes
     */
    public void setSelectionAttributes(AttributeSet selectionAttributes) {
        if (selectionAttributes == null)
            throw new IllegalArgumentException("null not allowed");

        if (!this.selectionAttributes.equals(selectionAttributes)) {
            this.selectionAttributes = selectionAttributes;
            reload(ReloadManager.RELOAD_STYLE);
        }
    }

    /**
     * @return the current selectionAttributes
     */
    public AttributeSet getSelectionAttributes() {
        return selectionAttributes;
    }

    /**
     * Set the background color.
     * @param color the new background color
     */
    public void setSelectionBackground(Color color) {
        boolean changed = selectionAttributes.putAll(CSSStyleSheet.getAttributes(color, Style.BACKGROUND_COLOR));
        if (changed)
            reload(ReloadManager.RELOAD_STYLE);
    }

    /**
     * Return the background color.
     * @return the background color
     */
    public Color getSelectionBackground() {
        return CSSStyleSheet.getBackground(selectionAttributes);
    }

    /**
     * Set the foreground color.
     * @param color the foreground color of selected cells
     */
    public void setSelectionForeground(Color color) {
        boolean changed = selectionAttributes.putAll(CSSStyleSheet.getAttributes(color, Style.COLOR));
        if (changed)
            reload(ReloadManager.RELOAD_STYLE);
    }

    /**
     * Return the foreground color.
     * @return the foreground color
     */
    public Color getSelectionForeground() {
        return CSSStyleSheet.getForeground(selectionAttributes);
    }

    /**
     * TODO: documentation
     *
     * @param hv
     */
    public void setHeaderVisible(boolean hv) {
        boolean oldHeaderVisible = headerVisible;
        headerVisible = hv;
        if (oldHeaderVisible != headerVisible)
            reload(ReloadManager.RELOAD_CODE);
    }

    /**
     * TODO: documentation
     *
     * @return
     */
    public boolean isHeaderVisible() {
        return headerVisible;
    }

    /**
     * TODO: documentation
     */
    public void setHeaderStyle(String style) {
        this.headerStyle = style;
    }

    /**
     * TODO: documentation
     */
    public String getHeaderStyle() {
        return headerStyle;
    }

    /**
     * Set the headerAttributes.
     * @param headerAttributes the headerAttributes
     */
    public void setHeaderAttributes(AttributeSet headerAttributes) {
        if (headerAttributes == null)
            throw new IllegalArgumentException("null not allowed");

        if (!this.headerAttributes.equals(headerAttributes)) {
            this.headerAttributes = headerAttributes;
            reload(ReloadManager.RELOAD_STYLE);
        }
    }

    /**
     * @return the current headerAttributes
     */
    public AttributeSet getHeaderAttributes() {
        return headerAttributes;
    }

    /**
     * Set the background color.
     * @param color the new background color
     */
    public void setHeaderBackground(Color color) {
        boolean changed = headerAttributes.putAll(CSSStyleSheet.getAttributes(color, "background-color"));
        if (changed)
            reload(ReloadManager.RELOAD_STYLE);
    }

    /**
     * Return the background color.
     * @return the background color
     */
    public Color getHeaderBackground() {
        return CSSStyleSheet.getBackground(headerAttributes);
    }

    /**
     * Set the foreground color.
     * @param color the new foreground color
     */
    public void setHeaderForeground(Color color) {
        boolean changed = headerAttributes.putAll(CSSStyleSheet.getAttributes(color, "color"));
        if (changed)
            reload(ReloadManager.RELOAD_STYLE);
    }

    /**
     * Return the foreground color.
     * @return the foreground color
     */
    public Color getHeaderForeground() {
        return CSSStyleSheet.getForeground(headerAttributes);
    }

    /**
     * TODO: documentation
     *
     * @param b
     */
    public void setShowGrid(boolean b) {
        setShowHorizontalLines(b);
        setShowVerticalLines(b);
    }

    /**
     * TODO: documentation
     *
     * @param b
     */
    public void setShowHorizontalLines(boolean b) {
        boolean oldShowHorizontalLines = showHorizontalLines;
        showHorizontalLines = b;
        if (showHorizontalLines != oldShowHorizontalLines)
            reload(ReloadManager.RELOAD_CODE);
    }

    /**
     * TODO: documentation
     *
     * @return
     */
    public boolean getShowHorizontalLines() {
        return showHorizontalLines;
    }

    /**
     * TODO: documentation
     *
     * @param b
     */
    public void setShowVerticalLines(boolean b) {
        boolean oldShowVerticalLines = showVerticalLines;
        showVerticalLines = b;
        if (showVerticalLines != oldShowVerticalLines)
            reload(ReloadManager.RELOAD_CODE);
    }

    /**
     * TODO: documentation
     *
     * @return
     */
    public boolean getShowVerticalLines() {
        return showVerticalLines;
    }

    /*
     * Implementiert das cellspacing Attribut des HTML Tables. Da dieses
     * nur eindimensional ist, wird nur der width Wert der Dimension in
     * den HTML Code uebernommen.
     */
    /**
     * TODO: documentation
     *
     * @param d
     */
    public void setIntercellSpacing(SDimension d) {
        SDimension oldIntercellSpacing = intercellSpacing;
        intercellSpacing = d;
        if ((intercellSpacing == null && oldIntercellSpacing != null) ||
            intercellSpacing != null && !intercellSpacing.equals(oldIntercellSpacing))
            reload(ReloadManager.RELOAD_CODE);
    }

    /**
     * TODO: documentation
     *
     * @return
     */
    public SDimension getIntercellSpacing() {
        return intercellSpacing;
    }

    /*
     * Implementiert das cellpadding Attribut des HTML Tables. Da dieses
     * nur eindimensional ist, wird nur der width Wert der Dimension in
     * den HTML Code uebernommen.
     */
    /**
     * TODO: documentation
     *
     * @param d
     */
    public void setIntercellPadding(SDimension d) {
        SDimension oldIntercellPadding = intercellPadding;
        intercellPadding = d;
        if ((intercellPadding == null && oldIntercellPadding != null) ||
            intercellPadding != null && !intercellPadding.equals(oldIntercellPadding))
            reload(ReloadManager.RELOAD_CODE);
    }

    /**
     * TODO: documentation
     *
     * @return
     */
    public SDimension getIntercellPadding() {
        return intercellPadding;
    }

    public String getCGClassID() {
        return cgClassID;
    }

    public void setCG(TableCG cg) {
        super.setCG(cg);
    }

    public String getEditParameter(int row, int col) {
        return "e" + row + ":" + col;
    }

    public String getToggleSelectionParameter(int row, int col) {
        return "t" + row + ":" + col;
    }

    public String getSelectionParameter(int row, int col) {
        return "s" + row + ":" + col;
    }

    public String getDeselectionParameter(int row, int col) {
        return "d" + row + ":" + col;
    }

    /**
     * Set the column, after which the selection control should be
     * located. The selection control usually is the checkbox that is
     * displayed for the user to select the row. Note, that the plaf
     * may choose to provide other means to make selection accessible
     * to the user.
     *
     * @param column The column, after which the selection control
     *               should be placed. If this is less than 1, then
     *               the selection control is not displayed at all. If
     *               you set this to <code>Integer.MAX_VALUE</code>, the
     *               selection control is always displayed as last element
     *               in the row.
     */
    public void setRowSelectionColumn(int column) {
        rowSelectionColumn = column;
    }

    /**
     * returns the selection control position.
     * @see #setRowSelectionColumn(int)
     */
    public int getRowSelectionColumn() {
        return rowSelectionColumn;
    }

    /**
     * Returns the maximum size of this table.
     *
     * @return maximum size
     */
    public Rectangle getScrollableViewportSize() {
        return new Rectangle(0, 0, getColumnCount(), getRowCount());
    }

    /*
     * Setzt den anzuzeigenden Teil
     */
    /**
     * TODO: documentation
     *
     * @param d
     */
    public void setViewportSize(Rectangle d) {
        if (isDifferent(viewport, d)) {
            viewport = d;
            reload(ReloadManager.RELOAD_CODE);
        }
    }

    /**
     * TODO: documentation
     *
     * @return
     */
    public Rectangle getViewportSize() {
        return viewport;
    }

    public Dimension getPreferredExtent() {
        return null;
    }

    /**
     * if selection changes, we have to reload code...
     */
    protected final ListSelectionListener reloadOnSelectionChangeListener =
        new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                reload(ReloadManager.RELOAD_CODE);
            }
        };

    /**
     * Gets the deselected Icon.
     * @return Returns a SIcon
     */
    public SIcon getDeselectedIcon() {
        return fDeselectedIcon;
    }

    /**
     * Sets the deselected Icon.
     * @param deselectedIcon The deselectedIcon to set
     */
    public void setDeselectedIcon(SIcon deselectedIcon) {
        fDeselectedIcon = deselectedIcon;
    }

    /**
     * Gets the selected Icon.
     * @return Returns a SIcon
     */
    public SIcon getSelectedIcon() {
        return fSelectedIcon;
    }

    /**
     * Sets the selected Icon.
     * @param selectedIcon The selectedIcon to set
     */
    public void setSelectedIcon(SIcon selectedIcon) {
        fSelectedIcon = selectedIcon;
    }


    protected int editClickCount = 1;

    public int getEditClickCount() {
        return editClickCount;
    }

    public void setEditClickCount(int editClickCount) {
        this.editClickCount = editClickCount;
    }


    protected int selectClickCount = 0;

    public int getSelectClickCount() {
        return selectClickCount;
    }

    public void setSelectClickCount(int selectClickCount) {
        this.selectClickCount = selectClickCount;
    }

}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
