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
import java.util.BitSet;
import java.util.ArrayList;

import org.wings.SComboBox;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class SFormDayChooser
    extends SComboBox
{
    private static final boolean DEBUG = true;

    /*
     * Die Anzahl der Tage, die dargestellt werden.
     */
    protected static final Integer[] DAY_CACHE = new Integer[31];

    static {
        for ( int i=0; i<DAY_CACHE.length; i++ ) {
            DAY_CACHE[i] = new Integer(i+1);
        }
    }

    /*
     * Die Anzahl der Tage, die dargestellt werden.
     */
    protected int days = 31;

    /*
     * Der erste Wochentag des Monats
     */
    protected int firstWeekDayOfMonth = 0;

    /*
     * der kleinste waehlbare Tag
     */
    protected int minimum = 1;

    /*
     * der groesste waehlbare Tag
     */
    protected int maximum = 31;

    /*
     * All die Wochentage, die nicht selektierbar sind: Bit 0 = Sonntag,
     * Bit 1 = Montag,...
     */
    protected final BitSet unselectableWeekdays = new BitSet(7);

    /*
     * Ist adjusting gesetzt, werden keine Events gefeuert.
     */
    protected boolean adjusting = false;

    int day = minimum;

    public SFormDayChooser() {
        buildGUI();

        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (getSelectedIndex() > 0)
                    day = ((Integer)getSelectedItem()).intValue();
                else
                    day = -1;
            } } );
    }

    /*
     * Baut die gesamte GUI neu auf und zeichnet Componente neu.
     */
    protected void buildGUI() {
        removeAllItems();
        for ( int i=0; i<getDayCount(); i++ ) {
            if ( isDaySelectable(i+1) ) {
                addItem(DAY_CACHE[i]);
            }
        }
        setSelectedItem(DAY_CACHE[day-1]);
    }

    /*
     * Setzt den ersten Wochentag des Monats.
     */
    public void setFirstWeekDayOfMonth(int weekday) {
        firstWeekDayOfMonth = weekday % 7;
        buildGUI();
    }

    /*
     * Setzt die Anzahl der Tage, die dargestellt werden.
     * Hier wird der Tag eventuell auf die Anzahl
     * der Tage angepasst.
     */
    public void setDayCount(int d) {
        days = d;
        if ( days <= getDay() )
            setDay(days);
        buildGUI();
    }

    public int getDayCount() {
        return days;
    }

    /*
     * Gibt den selektierten Tag zurueck
     */
    public int getDay() {
        return day;
    }

    /*
     * Setzt den selektierten Tag.
     * Hier wird der Tag eventuell auf die Anzahl
     * der Tage angepasst.
     */
    public void setDay(int d) {
        day = getNextSelectableDay(d);
        setSelectedItem(DAY_CACHE[day-1]);
    }

    /*
     * Gibt zurueck, ob der Tag anwaehlbar ist, oder nicht.
     */
    public boolean isDaySelectable(int d) {
        d = normalize(d);

        return !unselectableWeekdays.get((d+firstWeekDayOfMonth-1)%7) &&
            d>=minimum && d<=maximum;
    }

    /*
     * Berechnet den naechsten anwaehlbaren Tag.
     */
    public int getNextSelectableDay(int d) {
        d = normalize(d);
        if ( isDaySelectable(d) )
            return d;
        else
            return getNextSelectableDay(++d);
    }

    /*
     * Ergebnis ist >=1 und <=DayCount!!
     */
    public int normalize(int d) {
        d = Math.max(d, 1);
        return ((d-1)%getDayCount())+1;
    }

    /*
     * Benachrichtigt alle Interessenten, wenn sich der Jahreswert
     * geaendert hat.
     */
    protected void fireActionPerformed() {
        if ( !adjusting ) {
            adjusting = true;

            ActionEvent e =
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""+getDay());

            final Object[] listeners = getListenerList();
            for( int i = listeners.length - 2; i >= 0; i -= 2 )
                if( listeners[ i ] == ActionListener.class )
                    ((ActionListener) listeners[ i + 1 ]).actionPerformed( e );

            adjusting = false;
        }
    }

    public void setMinimum(int m) {
        minimum = m;
        if ( minimum<1 )
            minimum = 1;
        if ( minimum > 31 )
            minimum = 31;
        setDay(getDay());
        buildGUI();
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMaximum(int m) {
        maximum = m;
        if ( maximum<1 )
            maximum = 1;
        if ( maximum > 31 )
            maximum = 31;
        setDay(getDay());
        buildGUI();
    }

    public int getMaximum() {
        return maximum;
    }

    /*
     * Der angebene Wochentag kann nicht mehr selektiert werden.
     */
    public void excludeWeekday(int i) {
        unselectableWeekdays.set(i);
    }

    /*
     * Der angebene Wochentag kann selektiert werden.
     */
    public void includeWeekday(int i) {
        unselectableWeekdays.clear(i);
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
