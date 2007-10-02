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
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ArrayList;
import java.util.Locale;

import org.wings.SContainer;
import org.wings.SLayoutManager;
import org.wings.session.Session;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public abstract class AbstractDateChooser
    extends SContainer
    implements DateChooser, PropertyChangeListener
{
    private static final boolean DEBUG = true;

    /*
     * Aus diesem Calendar bekomme ich alle relevanten Informationen
     */
    protected GregorianCalendar calendar = null;

    /*
     * Im Date Chooser kann man auch Null Werte selektieren. Wenn man im
     * Jahr Feld nichts eintraegt. Dieses Tag zeigt an, wenn ein Null
     * Wert repraesntiert wird.
     */
    protected boolean nullDate = true;

    /*
     * Alle die es interessiert, wenn sich der wert aendert.
     */
    private final ArrayList listenerList = new ArrayList(2);

    private GregorianCalendar convertCalendar = null;
    private Date convertDate = new Date();

    private boolean adjusting = false;


    public AbstractDateChooser() {
        this(null);
    }

    public AbstractDateChooser(SLayoutManager l) {
        super(l);
        initLocaleDependent();
    }


    /** init all local dependet objects */
    protected void initLocaleDependent() {
        Locale l = getLocale();

        if ( calendar!=null ) {
            Calendar oldCalendar = calendar;
            calendar = new GregorianCalendar(l);
            calendar.set(Calendar.ERA, oldCalendar.get(Calendar.ERA));
            calendar.set(oldCalendar.get(Calendar.YEAR),
                         oldCalendar.get(Calendar.MONTH),
                         oldCalendar.get(Calendar.DAY_OF_MONTH));
        }
        else
            calendar = new GregorianCalendar(l);

        convertCalendar = new GregorianCalendar(l);
    }


    public void set(int year, int month, int day) {
        if ( day < 1 ) {
            nullDate = true;
        }
        else {
            // System.out.println("Set " + year + " " + month + " " + day);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            if ( day<0 ) {
                nullDate = true;
                calendar.set(Calendar.DATE,
                             Math.min(calendar.get(Calendar.DATE),
                                      getMonthLength(month,year)));
            } else {
                nullDate = false;
                calendar.set(Calendar.DATE,
                             Math.min(day, getMonthLength(month,year)));
            }
        }

        updateChooser();
        fireActionPerformed();
    }


    protected abstract void updateChooser();


    public void set(long longDate) {
        set((int)((longDate/10000)%10000),
            (int)(((longDate/100)%100)%13)-1,
            (int)((longDate%100)%32));
    }

    public void setTime(long timeInMillis) {
        convertDate.setTime(timeInMillis);
        setDate(convertDate);
    }

    public void setDate(Date d) {
        convertCalendar.setTime(d);
        setDate(convertCalendar);
    }

    public void setDate(Calendar c) {
        set(c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DATE));
    }

    /*
     * Abhaenging, ob Schaltjahr oder nicht Anzahl der Tage im Monat
     * zurueckgeben.
     */
    protected final int getMonthLength(int month, int year) {
        return calendar.isLeapYear(year) ? LEAP_MONTH_LENGTH[month] :
            MONTH_LENGTH[month];
    }

    /*
     * Gibt das Datum als long Value in Millisekunden seit 1.1.1970
     * zurueck. Achtung, das kann natuerlich Probleme geben mit Datum
     * kleiner als 1970 bzw. groesser als 2036 (?). Eine negative Zahl bedeutet
     * kein Datum (also null)
     */
    public long getTime() {
        if ( nullDate )
            return -1;
        return calendar.getTime().getTime();
    }

    public Date getDate() {
        if ( nullDate )
            return null;
        return calendar.getTime();
    }

    /*
     * Gibt das gesetzte Datum in folgendem Format zurueck:<BR>
     * <UL>
     * <LI> die ersten 4 Ziffern sind das Jahrhundert
     * <LI> die 5. und die 6. Ziffer der Monat
     * <LI> die 7. und die 8. Ziffer der Tag
     * <LI> das Vorzeichen bestimmt die Aera: - BC und + AD
     * <LI> -1 entspricht keinem Datum (also null)
     * </UL>
     */
    public long getLongDate() {
        if ( nullDate )
            return -1;

        int era = calendar.get(Calendar.ERA);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // System.out.println("year " + year);
        // System.out.println("month " + month);
        // System.out.println("day " + day);
        // System.out.println("erg " + (day*1000000+(month+1)*10000+year));

        return (era==GregorianCalendar.AD?1:-1) *
            Math.abs(year)*10000+(month+1)*100+day;
    }

    /*
     * Alle die es interessiert, wenn sich der Jahreswert aendert,
     * sollten sich hiermit registrieren.
     */
    public void addActionListener(ActionListener al) {
        listenerList.add(al);
    }

    /*
     * Alle die es nicht meht interessiert, wenn sich der Jahreswert aendert,
     * sollten sich hiermit ent-registrieren.
     */
    public void removeActionListener(ActionListener al) {
        listenerList.remove(al);
    }

    /*
     * Benachrichtigt alle Interessenten, wenn sich der Jahreswert
     * geaendert hat.
     */
    protected void fireActionPerformed() {
        if ( !adjusting ) {
            adjusting = true;

            if ( listenerList.size()>0 ) {
                ActionEvent e =
                    new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "" + getDate());

                for ( int i=0; i<listenerList.size(); i++ ) {
                    ((ActionListener)listenerList.get(i)).actionPerformed(e);
                }
            }

            adjusting = false;
        }
    }

    public void propertyChange( PropertyChangeEvent evt ) {
        initLocaleDependent();
        updateChooser();
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
