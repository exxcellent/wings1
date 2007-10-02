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

import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.text.DateFormatSymbols;
import java.util.BitSet;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ArrayList;

import org.wings.SContainer;
import org.wings.SToggleButton;
import org.wings.SButtonGroup;
import org.wings.SLabel;
import org.wings.SGridLayout;
import org.wings.SFont;
import org.wings.session.Session;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class SAnchorDayChooser
    extends SContainer
    implements ActionListener, PropertyChangeListener
{
    private static final boolean DEBUG = true;

    /*
     * Die Tage der Woche. Hier ist mit noch nicht ganz klar, was
     * DateFormatSymbols.getShortWeekdays liefert. Anscheinend ist der
     * erste Wochentab in Feld 1 untergebracht. Kann sein, dass bei
     * manchen Locales die Sache bei 0 anfaengt, z.B. Frankreicht dessen
     * Wochentage bei Samstag beginnen im Gegensatz zu Deutschland, wo sie
     * Sonntag beginnen.
     */
    protected String[] WEEK_DAYS;

    /*
     * Fuer jeden moeglichen Tag einen Button.
     */
    protected final SToggleButton[] buttons = new SToggleButton[31];

    /*
     * Fuer jeden moeglichen Wochentag einen Label.
     */
    protected final SLabel[] weekDays = new SLabel[7];

    /*
     * Fuer jedes moegliche leere Feld einen Label
     */
    protected final SLabel[] empty = new SLabel[6];

    /*
     * Die Button Group der Tage. Nur ein Tag darf selektiert sein.
     */
    protected final SButtonGroup group = new SButtonGroup();

    /*
     * Der erste Wochentag des Monats
     */
    protected int firstWeekDayOfMonth = 0;

    /*
     * Der erste Tag der Woche (Sonntag (England) oder Montag (Deutschland)
     */
    protected int firstDayOfWeek = 0;

    /*
     * Die Anzahl der Tage, die dargestellt werden.
     */
    protected int days = 31;

    /*
     * Alle die es interessiert, wenn sich der Tag aendert.
     */
    private final ArrayList listenerList = new ArrayList(2);


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


    public SAnchorDayChooser() {
        super(new SGridLayout(0,7));

        // call this first, initLocaleDependent needs some components
        initComponents();

        getSession().addPropertyChangeListener(Session.LOCALE_PROPERTY, this);
        initLocaleDependent();
    }


    /** init all local dependet objects */
    protected void initLocaleDependent() {
        Locale l = getLocale();
        WEEK_DAYS = new DateFormatSymbols(l).getShortWeekdays();

        GregorianCalendar cal = new GregorianCalendar(l);

        // ist um eins zur internen Repraesentation versetzt
        firstDayOfWeek = cal.getFirstDayOfWeek()-1;

        // 0 ist Sonntag, 1 ist Montag, ...
        for ( int i=0; i<weekDays.length; i++ )
            weekDays[i].setText(WEEK_DAYS[i+1]);
    }


    public void setEnabled(boolean b) {
        super.setEnabled(b);
        for ( int i=0; i<buttons.length; i++ )
            buttons[i].setEnabled(b);

        for ( int i=0; i<weekDays.length; i++ )
            weekDays[i].setEnabled(b);

        for ( int i=0; i<empty.length; i++ )
            empty[i].setEnabled(b);
    }


    /*
     * Setzt den ersten Wochentag des Monats. 0 entspricht So, 1 Mo, 2 Di, 3 Mi,...
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
     * Baut die gesamte GUI neu auf und zeichnet Componente neu.
     */
    protected void buildGUI() {
        removeAll();

        minimum = Math.min(minimum, maximum);
        maximum = Math.max(minimum, maximum);

        for ( int i=firstDayOfWeek; i<weekDays.length+firstDayOfWeek; i++ )
            add(weekDays[i%7]);

        for ( int i=0; i<firstWeekDayOfMonth-firstDayOfWeek; i++ )
            add(empty[i]);

        for ( int i=0; i<days; i++ ) {
            add(buttons[i]);
            buttons[i].setEnabled(isDaySelectable(i+1));
        }
    }

    /*
     * Erzeugt alle noetigen Komponenten und baut die GUI auf.
     */
    protected void initComponents() {
        for ( int i=0; i<empty.length; i++ ) {
            empty[i] = new SLabel();
        }

        for ( int i=0; i<weekDays.length; i++ ) {
            weekDays[i] = new SLabel("", CENTER);
            weekDays[i].setBackground(Color.lightGray);
        }

        for ( int i=0; i<buttons.length; i++ ) {
            buttons[i] = new SToggleButton(""+(i+1));
            buttons[i].setShowAsFormComponent(false);
            buttons[i].addActionListener(this);
            buttons[i].setHorizontalAlignment(RIGHT);
            group.add(buttons[i]);
        }

        buttons[0].setSelected(true);
        buildGUI();
    }

    /*
     * Gibt den selektierten Tag zurueck
     */
    public int getDay() {
        for ( int i=0; i<buttons.length; i++ ) {
            if ( buttons[i].isSelected() )
                return i+1;
        }
        return -1;
    }

    /*
     * Gibt den Wochentag des selektierten Tages in gesetzten Locale
     * zurueck.
     */
    public String getWeekDay() {
        int wd = (firstWeekDayOfMonth + getDay() - 1) % 7;
        return WEEK_DAYS[wd+1];
    }

    /*
     * Setzt den selektierten Tag.
     * Hier wird der Tag eventuell auf die Anzahl
     * der Tage angepasst.
     */
    public void setDay(int d) {
        d = getNextSelectableDay(d);

        buttons[d-1].setSelected(true);
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

    public void actionPerformed(ActionEvent e) {
        fireActionPerformed();
    }

    /*
     * Alle die es interessiert, wenn sich der Tag aendert,
     * sollten sich hiermit registrieren.
     */
    public void addActionListener(ActionListener al) {
        listenerList.add(al);
    }

    /*
     * Alle die es nicht meht interessiert, wenn sich der Tag aendert,
     * sollten sich hiermit ent-registrieren.
     */
    public void removeActionListener(ActionListener al) {
        listenerList.remove(al);
    }

    /*
     * Benachrichtigt alle Interessenten, wenn sich der Tag
     * geaendert hat.
     * Interessenten werden 2mal benachrichtigt. Einmal, wenn der Tag deselektiert
     * wird und dann wenn er wieder selectiert wird. Das liesse sich nur aendern,
     * wenn in der ButtonGroup die ActionListener verwaltet werden.
     */
    protected void fireActionPerformed() {
        if ( !adjusting ) {
            adjusting = true;

            ActionEvent e =
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""+getDay());

            for ( int i=0; i<listenerList.size(); i++ )
                ((ActionListener)listenerList.get(i)).actionPerformed(e);

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

    /*
     * Setzt den Font in allen Subkomponenten
     */
    public void setFont(SFont f) {
        super.setFont(f);
        for ( int i=0; i<buttons.length; i++ )
            buttons[i].setFont(f);
        for ( int i=0; i<weekDays.length; i++ )
            weekDays[i].setFont(f);
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
