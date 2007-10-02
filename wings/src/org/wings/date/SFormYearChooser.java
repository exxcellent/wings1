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

package org.wings.date;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.DateFormatSymbols;
import java.util.BitSet;
import java.util.ArrayList;

import org.wings.SComboBox;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class SFormYearChooser
    extends SComboBox
{
    private static final boolean DEBUG = true;

    /*
     * das kleinste waehlbare Jahr
     */
    protected int minimum = 1999;

    /*
     * das groesste waehlbare Jahr
     */
    protected int maximum = 2003;

    /*
     * Ist adjusting gesetzt, werden keine Events gefeuert.
     */
    protected boolean adjusting = false;

    protected int year = minimum;

    public SFormYearChooser() {
        buildGUI();

        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ( getSelectedIndex()>=0 )
                    year = ((Integer)getSelectedItem()).intValue();
                else
                    year = -1;
            } } );
    }

    /*
     * Baut die gesamte GUI neu auf und zeichnet Componente neu.
     */
    protected void buildGUI() {
        removeAllItems();
        for ( int i=minimum; i<=maximum; i++ ) {
            addItem(new Integer(i));
        }

        setSelectedIndex(year-minimum);
    }

    /*
     * Gibt das selektierte Jahr zurueck
     */
    public int getYear() {
        return year;
    }

    /*
     * Setzt ein neues Jahr.
     * Hier wird der Tag eventuell auf die Anzahl
     * der Tage angepasst.
     */
    public void setYear(int d) {
        year = getNextSelectableYear(d);
        setSelectedIndex(year-minimum);
    }

    /*
     * Gibt zurueck, ob der Tag anwaehlbar ist, oder nicht.
     */
    public boolean isYearSelectable(int d) {
        return d>=minimum && d<=maximum;
    }

    /*
     * Berechnet den naechsten anwaehlbaren Tag.
     */
    public int getNextSelectableYear(int d) {
        if ( d<minimum )
            d = minimum;
        if ( d>maximum )
            d = maximum;

        return d;
    }

    /*
     * Benachrichtigt alle Interessenten, wenn sich der Jahreswert
     * geaendert hat.
     */
    protected void fireActionPerformed() {
        if ( !adjusting ) {
            adjusting = true;

            ActionEvent e =
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""+getYear());

            final Object[] listeners = getListenerList();
            for( int i = listeners.length - 2; i >= 0; i -= 2 )
                if( listeners[ i ] == ActionListener.class )
                    ((ActionListener) listeners[ i + 1 ]).actionPerformed( e );

            adjusting = false;
        }
    }

    public void setMinimum(int m) {
        minimum = m;
        setYear(getYear());
        buildGUI();
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMaximum(int m) {
        maximum = m;
        setYear(getYear());
        buildGUI();
    }

    public int getMaximum() {
        return maximum;
    }

    public void setAdjusting(boolean a) {
        adjusting = a;
    }

    public boolean getAdjusting() {
        return adjusting;
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
