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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.text.DateFormatSymbols;
import java.util.BitSet;
import java.util.Locale;
import java.util.ArrayList;

import org.wings.SComboBox;
import org.wings.session.Session;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class SFormMonthChooser
    extends SComboBox
    implements PropertyChangeListener
{
    private static final boolean DEBUG = true;

    /*
     * der kleinste waehlbare Monat
     */
    protected int minimum = 0;

    /*
     * der groesste waehlbare Monat
     */
    protected int maximum = 11;

    /*
     * Die Namen aller Monate im aktuellen Locale (Cache)
     */
    protected String[] monthLabels;

    /*
     * Ist adjusting gesetzt, werden keine Events gefeuert.
     */
    protected boolean adjusting = false;

    protected int month = minimum;

    public SFormMonthChooser() {
        getSession().addPropertyChangeListener(Session.LOCALE_PROPERTY, this);
        initLocaleDependent();
        buildGUI();
    }


    /** init all local dependet objects */
    protected void initLocaleDependent() {
        Locale l = getLocale();
        monthLabels = new DateFormatSymbols(l).getMonths();
    }


    /*
     * Baut die gesamte GUI neu auf und zeichnet Componente neu.
     */
    protected void buildGUI() {
        removeAllItems();
        for ( int i=0; i<12; i++ ) {
            if ( isMonthSelectable(i) ) {
                addItem(monthLabels[i]);
            }
        }
        setSelectedItem(monthLabels[month]);
    }


    /*
     * Gibt den selektierten Tag zurueck
     */
    public int getMonth() {
        for ( int i=0; i<monthLabels.length; i++ ) {
            if ( getSelectedItem()==monthLabels[i] ) {
                month = i;
                return month;
            }
        }

        return -1;
    }

    /*
     * Setzt den selektierten Tag.
     * Hier wird der Tag eventuell auf die Anzahl
     * der Tage angepasst.
     */
    public void setMonth(int d) {
        month = getNextSelectableMonth(d);
        setSelectedItem(monthLabels[month]);
    }

    /*
     * Gibt zurueck, ob der Tag anwaehlbar ist, oder nicht.
     */
    public boolean isMonthSelectable(int d) {
        d = normalize(d);

        return d>=minimum && d<=maximum;
    }

    /*
     * Berechnet den naechsten anwaehlbaren Tag.
     */
    public int getNextSelectableMonth(int d) {
        d = normalize(d);
        if ( isMonthSelectable(d) )
            return d;
        else
            return getNextSelectableMonth(++d);
    }

    /*
     * Ergebnis ist >=0 und <=11!!
     */
    public int normalize(int d) {
        d = Math.min(d, 11);
        d = Math.max(d, 0);
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
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""+getMonth());

            final Object[] listeners = getListenerList();
            for( int i = listeners.length - 2; i >= 0; i -= 2 )
                if( listeners[ i ] == ActionListener.class )
                    ((ActionListener) listeners[ i + 1 ]).actionPerformed( e );

            adjusting = false;
        }
    }

    public void setMinimum(int m) {
        minimum = m;
        normalize(minimum);
        setMonth(getMonth());
        buildGUI();
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMaximum(int m) {
        maximum = m;
        normalize(maximum);
        setMonth(getMonth());
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

    public void propertyChange( PropertyChangeEvent evt ) {
        initLocaleDependent();
        buildGUI();
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
