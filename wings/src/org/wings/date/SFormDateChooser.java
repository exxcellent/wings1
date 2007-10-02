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
import java.util.ArrayList;
import java.util.Locale;

import org.wings.SContainer;
import org.wings.session.Session;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class SFormDateChooser
    extends AbstractDateChooser
    implements ActionListener
{
    private static final boolean DEBUG = true;

    protected SFormDayChooser dayChooser;
    protected SFormMonthChooser monthChooser;
    protected SFormYearChooser yearChooser;


    public SFormDateChooser() {
        super();

        dayChooser = new SFormDayChooser();
        dayChooser.addActionListener(this);

        monthChooser = new SFormMonthChooser();
        monthChooser.addActionListener(this);

        yearChooser = new SFormYearChooser();
        yearChooser.addActionListener(this);

        getSession().addPropertyChangeListener(Session.LOCALE_PROPERTY, this);
        buildGUI();
    }


    protected void buildGUI() {
        removeAll();
        add(dayChooser);
        add(monthChooser);
        add(yearChooser);
    }


    public void actionPerformed(ActionEvent e) {
        if ( e.getSource()==dayChooser ||
             e.getSource()==monthChooser ||
             e.getSource()==yearChooser ) {
            set(yearChooser.getYear(),
                monthChooser.getMonth(),
                dayChooser.getDay());
        }
    }


    protected void updateChooser() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);

        dayChooser.setAdjusting(true);
        monthChooser.setAdjusting(true);
        yearChooser.setAdjusting(true);

        if ( !nullDate ) {
            dayChooser.setFirstWeekDayOfMonth((weekDay-day+35)%7);
            dayChooser.setDayCount(getMonthLength(month, year));
            dayChooser.setDay(day);
        }

        yearChooser.setYear(year);
        monthChooser.setMonth(month);

        dayChooser.setAdjusting(false);
        monthChooser.setAdjusting(false);
        yearChooser.setAdjusting(false);
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
