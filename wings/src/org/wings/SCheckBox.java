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

import org.wings.plaf.*;
import org.wings.style.*;
import org.wings.io.Device;

/*
 * Checkboxen sind etwas besondere {@link SFormComponent}, denn
 * ihre eigentliche Identitaet ({@link #getUID}) steckt nicht im Name,
 * sondern im Value. Das ist deswegen so, weil in HTML Gruppen
 * durch den selben Namen generiert werden. Das ist natuerlich
 * problematisch. Eine anderes Problem, welches hier auftaucht ist,
 * das HTML immer nur rueckmeldet, wenn eine Checkbox markiert ist.
 * Deshalb wird hintenan immer ein Hidden Form Element gehaengt,
 * welches rueckmeldet, dass die Checkbox bearbeitet wurde.
 */
/**
 * TODO: documentation
 *
 * @author <a href="mailto:armin.haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class SCheckBox extends SAbstractButton
{
    private static final String cgClassID = "CheckBoxCG";

    /**
     * TODO: documentation
     *
     */ 
    public SCheckBox() {
        this(false);
    }

    /**
     * create a checkbox with a text-label.
     *
     * @param text
     */
    public SCheckBox(String text) {
        this(false);
        setText(text);
    }

    /**
     * TODO: documentation
     *
     * @param selected
     */
    public SCheckBox(boolean selected) {
        setSelected(selected);

        setHorizontalTextPosition(NO_ALIGN);
        setVerticalTextPosition(NO_ALIGN);

        super.setType(CHECKBOX);
    }

    protected void setGroup(SButtonGroup g) {
        if ( g!=null ) {
            throw new IllegalArgumentException("SCheckBox don`t support button groups, use SRadioButton");
        } // end of if ()
    }

    /**
     * TODO: documentation
     *
     * @param t
     */
    public final void setType(String t) {
        if ( !CHECKBOX.equals(t) )
            throw new IllegalArgumentException("type change not supported, type is fix: checkbox");
        
        super.setType(t);
    }

    public String getCGClassID() {
        return cgClassID;
    }

    public void setCG(CheckBoxCG cg) {
        super.setCG(cg);
    }

    /**
     * Indicates, if this component is rendered as a "native" from input
     * element, or as a image form element.
     *
     * @return a <code>boolean</code> value
     */
    protected boolean isRenderedAsFormInput() {
        return getShowAsFormComponent() &&
            getIcon()==null;
    }

    public void processLowLevelEvent(String action, String[] values) {
        boolean requestSelection;
    
        if ( isRenderedAsFormInput() ) {
            // one hidden and one checked event from the form says select
            // it, else deselect it (typically only the hidden event)
            requestSelection = values.length==2;
        } else {
            requestSelection = parseSelectionToggle(values[0]);
        }        
 
        if ( requestSelection!=isSelected() ) {
            delayEvents(true);
            setSelected(requestSelection);
            // got an event, that is a select...
            SForm.addArmedComponent(this);
        } // end of if ()
    }

    /**
     * in form components the parameter value of an button is the button
     * text. So just toggle selection, in process request, if it is a request
     * for me.
     */
    protected boolean parseSelectionToggle(String toggleParameter) {
	// a button/image in a form has no value, so just toggle selection...
	if ( getShowAsFormComponent() ) {
	    return !isSelected();
	} // end of if ()

	if ( "1".equals(toggleParameter) )
	    return true;
	else if ( "0".equals(toggleParameter) )
	    return false;
	
	
	// don't change...
	return isSelected();
    }

}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
