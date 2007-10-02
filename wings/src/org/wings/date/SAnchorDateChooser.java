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
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ArrayList;

import org.wings.SContainer;
import org.wings.SBorderLayout;
import org.wings.SFont;
import org.wings.session.Session;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class SAnchorDateChooser
    extends AbstractDateChooser
    implements ActionListener
{
    private static final boolean DEBUG = true;

    protected SAnchorDayChooser dayChooser;
    protected SAnchorYearMonthChooser yearMonthChooser;


    public SAnchorDateChooser() {
        super(new SBorderLayout());

        dayChooser = new SAnchorDayChooser();
        dayChooser.addActionListener(this);

        yearMonthChooser = new SAnchorYearMonthChooser();
        yearMonthChooser.addActionListener(this);

        getSession().addPropertyChangeListener(Session.LOCALE_PROPERTY, this);
        buildGUI();
    }


    protected void buildGUI() {
        removeAll();
        add(dayChooser, SBorderLayout.CENTER);
        add(yearMonthChooser, SBorderLayout.NORTH);
    }


    public void setFont(SFont f) {
        super.setFont(f);
        dayChooser.setFont(f);
        yearMonthChooser.setFont(f);
    }


    public void actionPerformed(ActionEvent e) {
        if ( e.getSource()==dayChooser ||
             e.getSource()==yearMonthChooser ) {
            set(yearMonthChooser.getYear(),
                yearMonthChooser.getMonth(),
                dayChooser.getDay());
        }
    }

    /*
     * Fuellt die Werte des dayChoosers mit denen des gesetzten
     * Kalenders. <BR>
     * Der Algorithmus hat einiges an Zeit gekostet. Das Problem war,
     * welches ist der erste Wochentag des Monats. Loesung:<BR>
     * ( weekDay - day + 35 ) % 7<BR>
     * 35 ist nur noetig, um negative Zahlen zu vermeiden.
     */
    protected void updateChooser() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);

        yearMonthChooser.set(month, year);

        if ( !nullDate ) {
            dayChooser.setFirstWeekDayOfMonth((weekDay-day+35)%7);
            dayChooser.setDayCount(getMonthLength(month, year));
            dayChooser.setDay(day);
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
