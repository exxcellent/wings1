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
import javax.swing.Action;

/**
 * This is a button implementation.
 * <p>
 * A button can be composed of either text, an image, or both. When inside a
 * {@link org.wings.SForm} it will be rendered as HTML button, otherwise as
 * HTML anchor. You can influence this behaviour calling 
 * {@link #setShowAsFormComponent(boolean)}.      
 * <p>
 * Besides a button's typical usage you may return SButton objects in  
 * renders, e.g. in a {@link org.wings.table.STableCellRenderer} 
 * implementation.
 * <p>
 * This button implementation encodes its action command into the low level
 * event and fires the encoded action command and not the actual action 
 * command, if a low level event triggers a push of a button.
 *
 * @author <a href="mailto:armin.haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class SButton extends SAbstractButton {

    /**
     * Creates a button with text.
     *
     * @param text  the text of the button
     */
    public SButton(String text) {
        super(text);
    }
  
    /**
     * Creates a button where properties are taken from the
     * Action supplied.
     *
     * @param action the Action used to specify the new button
     */
    public SButton(Action action) {
        super(action);
    }
  
    /**
     * Creates a button with no text or icon set.
     */
    public SButton() {
        super();
    }
  
    /**
     * Creates a button with a icon.
     *
     * @param i the Icon image to display on the button
     */
    public SButton(SIcon i) {
        super();
        setIcon(i);
    }
  
    /**
     * Creates a button with initial text and an icon.
     *
     * @param text the text of the button
     * @param i    the Icon image to display on the button
     */
  
    public SButton(String text, SIcon i) {
        super(text);
        setIcon(i);
    }

    protected void setGroup(SButtonGroup g) {
        if ( g!=null ) {
            throw new IllegalArgumentException("SButton don`t support button groups, use SToggleButton");
        } // end of if ()
    }
  
    public boolean isSelected() {
        return false;
    }

    private String actionCommandToFire;

    public void processLowLevelEvent(String action, String[] values) {
        // got an event, that is a select...
        SForm.addArmedComponent(this);

        if ( getShowAsFormComponent() && 
             getActionCommand()!=null ) {
            actionCommandToFire = getActionCommand();
        } else {
            actionCommandToFire = values[0];
        }
    }   

    public void fireFinalEvents() {
        fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, actionCommandToFire));
        if ( getGroup()!=null ) {
            getGroup().fireDelayedFinalEvents();
        }
    }
     

    public String getSelectionParameter() {
        return getActionCommand()!=null ? getActionCommand() : "1";
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */





