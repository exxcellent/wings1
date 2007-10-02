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

package org.wingx;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import org.wings.SButton;
import org.wings.SContainer;
import org.wings.SFrame;
import org.wings.STextField;
import org.wings.plaf.ComponentCG;

/**
 *
 *  * @author <a href="mailto:e.habicht@thiesen.com">Erik Habicht</a>
 */
public class XCalendar extends SContainer {
 
    private DateFormat  dateFormat  = null;
    
    private STextField  textField   = new STextField();
    private SButton     button      = new SButton("...");
    
    private Date        currentDate = null;
    private Date        lastDate    = null;
    
    private Locale      locale      = null;
    private TimeZone    timezone    = null;

    private static final String cgClassID = "CalendarCG";
    
    private boolean enabled     = true;
    
    private ActionListener actionListener = null;
    
    private ActionListener getActionListener() {
        if ( actionListener == null ) {
            actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    parseDate();
                    fireActionEvents();
                }
            };
        }
        return actionListener;
    }

    /** Creates a new instance of SDateChooser */
    public XCalendar() {
        this( new GregorianCalendar().getTime() );
    }
    
    /** Creates a new instance of SDateChooser */    
    public XCalendar( Date date ) {
        this( date, null );
    }
    
    public XCalendar( Date date, Locale locale ) {
        this( date, locale, null );
    }
    
    public XCalendar( Date date, Locale locale, TimeZone timezone ) {
        super();
        this.locale     = locale;
        this.timezone   = timezone;
        initDateFormat();
        this.setDate( date );
        this.add( textField );
        this.add( button );        
    }
    
    public void setTimeZone( TimeZone timezone ) {
        this.timezone = timezone;
        initDateFormat();
    }
    
    private void initDateFormat () {
        
        if ( this.locale != null ) {
            dateFormat = DateFormat.getDateInstance( DateFormat.SHORT, this.locale );
        } else {
            dateFormat = DateFormat.getDateInstance( DateFormat.SHORT );
        }
        if ( timezone != null ) {
            dateFormat.setTimeZone( timezone );
        }
        
    }
 
    /**
     * Fire an ActionEvent at each registered listener.
     *
     * @param event supplied ActionEvent
     */
    protected void fireActionPerformed(ActionEvent event) {
        // Guaranteed to return a non-null array
        Object[] listeners = getListenerList();
        ActionEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                if (e == null) {
                    e = new ActionEvent(XCalendar.this,
                                        ActionEvent.ACTION_PERFORMED,
                                        "",
                                        event.getWhen(),
                                        event.getModifiers());
                }
                ((ActionListener) listeners[i + 1]).actionPerformed(e);
            }
        }
    }
    
    private void fireActionEvents() {
        fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
    }

    /**
     * Adds an ActionListener to the button.
     *
     * @param listener the ActionListener to be added
     */
    public void addActionListener(ActionListener listener) {
        addEventListener(ActionListener.class, listener);
        if ( getActionListeners().length > 0 && textField.getActionListeners().length == 0 ) {
            textField.addActionListener( getActionListener() );
        }
    }
    
    /**
     * Removes the supplied Listener from the listener list
     *
     * @param listener
     */
    public void removeActionListener(ActionListener listener) {
        removeEventListener(ActionListener.class, listener);
        if ( getActionListeners().length == 0 ) {
            textField.removeActionListener( getActionListener() );
        }
    }
    
    /**
     * Returns an array of all the <code>ActionListener</code>s added
     * to this AbstractButton with addActionListener().
     *
     * @return all of the <code>ActionListener</code>s added or an empty
     *         array if no listeners have been added
     */
    public ActionListener[] getActionListeners() {
        return (ActionListener[]) (getListeners(ActionListener.class));
    }            
    
    public String getCGClassID() {
        return cgClassID;
    }
    
    private void parseDate() {
        String text = textField.getText();
        try {
            currentDate = dateFormat.parse( text );
            lastDate    = currentDate;
        } catch ( ParseException pe ) {
            pe.printStackTrace();
        } finally {
            setDate( lastDate );
        }
    }
    
    
    
    /**
     * Returns the selected date.
     * @return the selected date
     */
    public Date getDate() {
        if ( textField.getActionListeners().length == 0 ) {
            parseDate();
        }
        return currentDate;
    }
    
    /**
     * Set the selected date.
     * @param date a date Object
     */
    public void setDate( Date date ) {
        if ( date != null ) {
            textField.setText( dateFormat.format( date ) );
            currentDate = date;
        }
    }
    
    /**
     * Return true if this component is enabled.
     * @return true if component is enabled
     */
    public boolean isEnabled() {
        return this.enabled;
    }
    
    /**
     * Set wether this component should be enabled.
     * @param bool true if the component is enabled, false otherwise
     */
    public void setEnabled( boolean bool ) {
        if ( enabled != bool ) {
            enabled = bool;
            textField.setEnabled( bool );
            button.setEnabled(bool);
        }
    }
    
    public STextField getTextField() {
        return this.textField;
    }
    public SButton getButton() {
        return this.button;
    }

    /**
     * Set the parent frame of this XCalendar
     * @param f the parent frame.
     */
    public void setParentFrame(SFrame f)
    {
        super.setParentFrame(f);
        ComponentCG cg = this.getCG();
        if (f != null && cg instanceof org.wingx.plaf.xhtml.CalendarCG) {
            org.wingx.plaf.xhtml.CalendarCG calendarCG = (org.wingx.plaf.xhtml.CalendarCG)cg;            
            calendarCG.installStyleSheet(this);
            calendarCG.setDateFormat( dateFormat ); 
            calendarCG.setLocale( locale );
        }
    }
  
}
