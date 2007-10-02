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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.wings.*;
import org.wings.session.Session;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class SAnchorYearMonthChooser
    extends SContainer
    implements ActionListener, PropertyChangeListener
{
    private static final boolean DEBUG = true;

    protected SLabel label = new SLabel();
    protected SButton forward = new SButton("");
    protected SButton backward = new SButton("");

    protected int month;
    protected int year;

    protected int minimum = Integer.MIN_VALUE;
    protected int maximum = Integer.MAX_VALUE;

    /*
     * Die Namen aller Monate im aktuellen Locale (Cache)
     */
    protected String[] monthLabels;

    /*
     * Alle die es interessiert, wenn sich der Jahreswert aendert.
     */
    private final ArrayList listenerList = new ArrayList(2);

    private boolean adjusting = false;


    public SAnchorYearMonthChooser() {
        super(new SFlowLayout(CENTER));

        getSession().addPropertyChangeListener(Session.LOCALE_PROPERTY, this);
        initLocaleDependent();

        backward.setIcon(new SResourceIcon("icons/ScrollLeft.gif"));
        backward.setToolTipText("&lt;");
        backward.setVerticalTextPosition(TOP);
        backward.setHorizontalTextPosition(RIGHT);
        backward.setShowAsFormComponent(false);
        backward.addActionListener(this);

        forward.setIcon(new SResourceIcon("icons/ScrollRight.gif"));
        forward.setToolTipText("&gt;");
        forward.setVerticalTextPosition(TOP);
        forward.setHorizontalTextPosition(RIGHT);
        forward.setShowAsFormComponent(false);
        forward.addActionListener(this);

        add(backward);
        add(label);
        add(forward);

        set(new java.util.GregorianCalendar(getLocale()));
    }


    /** init all local dependet objects */
    protected void initLocaleDependent() {
        Locale l = getLocale();
        monthLabels = new DateFormatSymbols(l).getMonths();
    }


    public void set(int month, int year) {
        this.month = month;
        this.year = year;
        makeConsistent();
        repaint();
    }

    public void set(Calendar c) {
        set(c.get(Calendar.MONTH), c.get(Calendar.YEAR));
    }


    protected void repaint() {
        forward.setVisible((year*100+month)<maximum);
        backward.setVisible((year*100+month)>minimum);
        label.setText(" " + monthLabels[month] + " " + year + " ");
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int m) {
        month = m;
        makeConsistent();
        repaint();
    }

    public int getYear() {
        return year;
    }

    public void setYear(int y) {
        year = y;
        makeConsistent();
        repaint();
    }

    public void setMaximum(int m, int y) {
        maximum = y*100 + m;
    }

    public void setMinimum(int m, int y) {
        minimum = y*100 + m;
    }

    protected void makeConsistent() {
        if ( month>11 ) {
            year += month/12;
            month = month%12;
        }

        if ( month<0 ) {
            while ( month<0 ) {
                year--;
                month+=12;
            }
        }

        if ( (year*100+month)>maximum ) {
            month = maximum%100;
            year = maximum/100;
        }

        if ( (year*100+month)<minimum ) {
            month = minimum%100;
            year = minimum/100;
        }
    }

    public void actionPerformed(ActionEvent e) {
        if ( e.getSource()==forward ) {
            System.out.println("forward");
            month+=1;
            makeConsistent();
        }

        if ( e.getSource()==backward ) {
            System.out.println("backward");
            month-=1;
            makeConsistent();
        }

        repaint();
        fireActionPerformed();
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

            String year = null;
            try {
                year = ""+getYear();
            }
            catch ( Exception ex ) {
            }

            ActionEvent e =
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, year);

            for ( int i=0; i<listenerList.size(); i++ )
                ((ActionListener)listenerList.get(i)).actionPerformed(e);

            adjusting = false;
        }
    }

    public void propertyChange( PropertyChangeEvent evt ) {
        initLocaleDependent();
        repaint();
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
