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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.EventObject;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;

import org.wings.event.SRequestEvent;
import org.wings.event.SRequestListener;
import org.wings.session.SessionManager;
import org.wings.table.STableCellEditor;

/**
 * A default Table cell Editor. In order to see the graphics, you need the
 * Java look and feel graphics (jlfgr*.jar)
 *
 * @author <a href="mailto:engels@mercatis.de">Holger Engels</a>
 * @version $Revision$
 */
public class SDefaultCellEditor
    implements STableCellEditor
{
    /**
     * The default ok button icon.
     *
     */
    public static final SIcon OK_BUTTON_ICON = 
        new SResourceIcon("toolbarButtonGraphics/general/Save16.gif");

    /**
     * The default cancel button icon.
     *
     */
    public static final SIcon CANCEL_BUTTON_ICON = 
        new SResourceIcon("toolbarButtonGraphics/general/Stop16.gif");


    /**
     * Label for displaying (error)-messages. It is unvisible, until a message
     * is set.
     *
     */
    protected final SLabel messageLabel;

    /**
     * Form for edit fields.
     *
     */
    protected final SForm editorForm;

    /**
     * If this button is pressed, editing is tried to stop. If input validation
     * found no error, editing is stopped, else an error message is displayed
     *
     */
    protected final SButton ok;

    /**
     * If this button is pressed, editing is canceled. 
     *
     */
    protected final SButton cancel;

    /**
     * Store here the CellEditorListener
     *
     */
    protected final EventListenerList listenerList;

    /**
     * Indicates, that an stopped event occurs. A stop event occurs on pressing
     * the {@link #ok ok button} or on posting the {@link #editorForm form}. 
     *
     */
    protected boolean fireStoppedEvent;

    /**
     * Indicates, that an cancel event occurs. A cancel event occurs on pressing
     * the {@link #cancel cancel button}.
     *
     */
    protected boolean fireCanceledEvent;

    /**
     * Event listener, which set the fire... indicators. This event listener is
     * added to the three buttons {@link #ok}, {@link #cancel} and {@link #undo}
     * and the {@link #editorFrom form}
     *
     */
    private final ActionListener fireEventListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ( e.getSource()==editorForm || 
                     e.getSource()==ok ) {
                    fireStoppedEvent = true;
                } else if ( e.getSource()==cancel ) { 
                    fireCanceledEvent = true;
                } // end of if ()
            }
        };
    
    /**
     * This is a trick, after the dispatching is done, check, if this editor
     * should fire an event. 
     * the trick is needed, because it is possible to get one or two events on
     * editing, one from the form and sometimes one from a button. It is only
     * possible to decide, which event we should fire, if we have all events.
     *
     */
    private final SRequestListener eventChecker = new SRequestListener() {
            public void processRequest(SRequestEvent e) {
                if ( e.getType()==SRequestEvent.DISPATCH_DONE ) {
                    checkFireEvents();
                } 
            }
        };

    /**
     * Fast edit support is editing with reduced interaction. E.g. a boolean
     * value can only have to states, true or false. So if editing is started,
     * the editor just flips the state and fires editing stopped. 
     */
    private boolean fastEditSupport = true;

    protected EditorDelegate delegate;

    protected SComponent editorComponent;

    // default init
    {

        // add request listener as weak reference. This is the only way to make
        // this object garbage collectable inside the session
	SessionManager.getSession().addRequestListener(eventChecker);
    }

    /**
     * Initialize the DefaultCellEditor with an editor component (like an text
     * field for instance). After calling this constructor, the 
     * {@link EditorDelegate}, that links the CellEditor and the
     * editorComponent has to be passed to the delegate instance variable.
     *
     * @param editorComponent the component used
     * @param initializeButtons flag to indicate if the button texts and icons
     *                          should be initialized.
     */
    protected SDefaultCellEditor(SComponent editorComponent,
                                 boolean initializeButtons) 
    {
        this.messageLabel = new SLabel();
        this.editorForm = new SForm();
        this.ok = new SButton();
        this.cancel = new SButton();
        this.listenerList = new EventListenerList();
        this.editorComponent = editorComponent;
        //this.delegate = delegate; useless & unitialized!
        fireStoppedEvent = false;
        fireCanceledEvent = false;

        editorForm.add(messageLabel);
        editorForm.add(editorComponent);
        if (initializeButtons) {
            initButtons();
        }
    }

    /**
     * Constructs a DefaultCellEditor that uses a text field.
     *
     * @param x  a STextField object ...
     */
    public SDefaultCellEditor(STextField x) {
        this(x, true);
        this.delegate = new EditorDelegate() {
                public void setValue(Object v) {
                    super.setValue(v);

                    if (v != null)
                        ((STextField)editorComponent).setText(v.toString());
                    else
                        ((STextField)editorComponent).setText("");
                }

                public Object getCellEditorValue() {
                    return ((STextField)editorComponent).getText();
                }

                public boolean stopCellEditing() {
                    return true;
                }

                public boolean shouldSelectCell(EventObject anEvent) {
                    return true;
                }
            };
    }

    /**
     * Constructs a DefaultCellEditor object that uses a check box.
     *
     * @param x  a SCheckBox object ...
     */
    public SDefaultCellEditor(SCheckBox x) {
        this(x, true);
        this.delegate = new EditorDelegate() {
                public void setValue(Object v) {
                    // Try my best to do the right thing with v
                    boolean bool;
                    if (v instanceof Boolean) {
                        bool = ((Boolean)v).booleanValue();
                    }
                    else if (v instanceof String) {
                        Boolean b = Boolean.valueOf((String)v);
                        bool = b.booleanValue();
                    }
                    else {
                        bool = false;
                    }

                    if ( fastEditSupport ) {
                        ((SCheckBox)editorComponent).setSelected(!bool);
                        SDefaultCellEditor.this.stopCellEditing();
                    } else {
                        ((SCheckBox)editorComponent).setSelected(bool);
                    } // end of if ()
                    
                }

                public Object getCellEditorValue() {
                    return Boolean.valueOf(((SCheckBox)editorComponent).isSelected());
                }

                public boolean stopCellEditing() {
                    return true;
                }

                public boolean shouldSelectCell(EventObject anEvent) {
                    return false;
                }
            };
    }
    
    /**
     * Intializes the buttons with default icons, tooltip text and listener.
     */
    protected void initButtons() {
	ok.addActionListener(fireEventListener);
        ok.setIcon(OK_BUTTON_ICON);
        ok.setToolTipText("ok");
        
	cancel.addActionListener(fireEventListener);
        cancel.setIcon(CANCEL_BUTTON_ICON);
        cancel.setToolTipText("cancel");

	editorForm.addActionListener(fireEventListener);
        editorForm.add(ok);
        editorForm.add(cancel);
    }

    /**
     * Returns a reference to the editor component.
     *
     * @return the editor Component
     */
    public final SComponent getComponent() {
        return editorComponent;
    }

    /**
     *
     */
    public final SButton getOKButton() {
        return ok;
    }

    /**
     *
     */
    public final SButton getCancelButton() {
        return cancel;
    }

    /**
     * Fast edit support is editing with reduced interaction. E.g. a boolean
     * value can only have to states, true or false. So if editing is started,
     * the editor just flips the state and fires editing stopped.
     *
     * @param b a <code>boolean</code> value
     */
    public final void setFastEdit(boolean b) {
        fastEditSupport = b;
    }

    /**
     * Return if fast edit is activated.
     *
     * @return a <code>boolean</code> value
     * @see #setFastEdit
     */
    public final boolean getFastEdit() {
        return fastEditSupport;
    }

    /**
     * Checks if a ChangeEvent should be fired. This is done on every
     * request. Pressing a button just set flags what to do. This method
     * checks the flags and start the jobs. This is a trick which gets 
     * necessary because the {@link #editorForm form} fires an event on 
     * posting the form and it is not sure, if cancel, ok, undo or 
     * a editor component is responsible for this post. So flags are set 
     * and processed after dispatching of the request, when all flags are set.
     */
    protected void checkFireEvents() {
        // something is to be done, if the editor form has fired an action
        // event, so the fireStoppedEvent flag is true
        if ( fireStoppedEvent ) {
            // process cancel and undo events before stopped event, because the
            // form fires a stopped event in any case...
            if ( fireCanceledEvent ) {
                cancelCellEditing();
            } else {
                stopCellEditing();
            } // end of if ()
        
            // reset the flags.
            fireStoppedEvent = false;
            fireCanceledEvent = false;
        } 
    }

    //
    //  Implementing the CellEditor Interface
    //

    // implements javax.swing.CellEditor
    /**
     * TODO: documentation
     *
     * @return
     */
    public Object getCellEditorValue() {
        return delegate.getCellEditorValue();
    }

    // implements javax.swing.CellEditor
    /**
     * TODO: documentation
     *
     * @param anEvent
     * @return
     */
    public boolean isCellEditable(EventObject anEvent) {
        return delegate.isCellEditable(anEvent);
    }

    /**
     * TODO: documentation
     *
     * @param anEvent
     * @return
     */
    public boolean shouldSelectCell(EventObject anEvent) {
        // By default we want the cell to be selected so
        // we return true
        return delegate.shouldSelectCell(anEvent);
    }

    // implements javax.swing.CellEditor
    /**
     * TODO: documentation
     *
     * @return
     */
    public boolean stopCellEditing() {
        if ( delegate.stopCellEditing() ) {
            fireEditingStopped();
            return true;
        }

        return false;
    }

    // implements javax.swing.CellEditor
    /**
     * TODO: documentation
     *
     */
    public void cancelCellEditing() {
        delegate.cancelCellEditing();
        fireEditingCanceled();
    }

    //
    //  Handle the event listener bookkeeping
    //
    // implements javax.swing.CellEditor
    /**
     * TODO: documentation
     *
     * @param l
     */
    public void addCellEditorListener(CellEditorListener l) {
        listenerList.add(CellEditorListener.class, l);
    }

    // implements javax.swing.CellEditor
    /**
     * TODO: documentation
     *
     * @param l
     */
    public void removeCellEditorListener(CellEditorListener l) {
        listenerList.remove(CellEditorListener.class, l);
    }

    private ChangeEvent changeEvent = null;

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireEditingStopped() {
	Object[] listeners = listenerList.getListenerList();
	for ( int i=listeners.length-2; i>=0; i-=2 ) {
	    if ( listeners[i]==CellEditorListener.class ) {
		if ( changeEvent==null )
		    changeEvent = new ChangeEvent(this);
		((CellEditorListener)listeners[i+1]).editingStopped(changeEvent);
	    }	       
	}
    }


    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireEditingCanceled() {
	Object[] listeners = listenerList.getListenerList();
	for ( int i=listeners.length-2; i>=0; i-=2 ) {
	    if ( listeners[i]==CellEditorListener.class ) {
		if ( changeEvent==null )
		    changeEvent = new ChangeEvent(this);
		((CellEditorListener)listeners[i+1]).editingCanceled(changeEvent);
	    }	       
	}
    }

    //
    //  Implementing the TreeCellEditor Interface
    //

    // implements javax.swing.tree.TreeCellEditor
    public SComponent getTreeCellEditorComponent(STree tree, Object value,
                                                 boolean isSelected,
                                                 boolean expanded,
                                                 boolean leaf, int row) {

        String stringValue = (value != null)?value.toString():"";

        delegate.setValue(stringValue);
        return editorForm;
    }

    /**
     * TODO: documentation
     *
     * @param c
     * @return
     */
    public boolean containedInForm(SComponent c) {
        while (c != null) {
            if (c instanceof SForm)
                return true;
            c = c.getParent();
        }
        return false;
    }

    //
    //  Implementing the CellEditor Interface
    //

    // implements javax.swing.table.TableCellEditor
    public SComponent getTableCellEditorComponent(STable table, Object value,
                                                  boolean isSelected,
                                                  int row, int column) {
        delegate.setValue(value); 

	return editorForm;
    }


    //
    //  Protected EditorDelegate class
    //

    /**
     * The interface all editing boils down to: setting the value for
     * the editor and retrieve its value.
     */
    protected class EditorDelegate {
        protected Object value;

        /**
         * Retrieve the value from the component used as Editor.
         * This method is called by the CellEditor to retrieve the
         * value after editing.
         *
         * @return the value managed by the Editor.
         */
        public Object getCellEditorValue() {
            return value;
        }

        /**
         * Set the Editors value. The task of this method is to
         * pass the value to the editor component so that editing
         * can be started.
         *
         * @param the value to be edited.
         */
        public void setValue(Object x) {
            this.value = x;
        }

        /**
         * TODO: documentation
         *
         * @param anEvent
         * @return
         */
        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }

        /**
         * TODO: documentation
         *
         * @return
         */
        public boolean stopCellEditing() {
            return true;
        }

        /**
         * TODO: documentation
         *
         */
        public void cancelCellEditing() {
        }

        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
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
