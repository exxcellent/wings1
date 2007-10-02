/*
 * $Id$
 * (c) Copyright 2002 wingS development team.
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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.wings.SComponent;
import org.wings.plaf.DateChooserCG;


/**
 * <!--
 * SDateChooser.java
 * Created: Mon Nov 18 20:34:25 2002
 * -->
 *
 *
 *
 * @author <a href="mailto:armin.haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class SDateChooser extends SComponent implements LowLevelEventListener {

    private static final String cgClassID = "DateChooserCG";

    private Calendar calendar;

    private DateParseException parseException;

    private boolean isNull = true;
    
    /** @see LowLevelEventListener#isEpochChecking() */
    protected boolean epochChecking = true;

    /**
     *
     */
    public SDateChooser() {
        calendar = new GregorianCalendar(getSession().getLocale());
    }

    /**
     *
     */
    public SDateChooser(Calendar calendar) {
        this.calendar = calendar;
    }

    public Calendar getCalendar() throws DateParseException {
        checkException();
        if (isNull) {
            return null;
        } else {
            return calendar;
        }
    }

    public void setTimeInMillis(long millis) {
        calendar.setTimeInMillis(millis);
        resetException();
        fireActionPerformed("date changed");
    }

    protected final void checkException() throws DateParseException {
        if (parseException != null) {
            parseException.fillInStackTrace();
            throw parseException;
        }
    }

    protected final void resetException() {
        parseException = null;
    }

    public long getTimeInMillis() throws DateParseException {
        checkException();
        if (isNull) {
            return Long.MIN_VALUE;
        } else {
            return calendar.getTimeInMillis();
        }
    }

    public void setDate(Date d) {
        if (d == null) {
            isNull = true;
        } else {
            isNull = false;
            calendar.setTimeInMillis(d.getTime());
        }
        resetException();
        fireActionPerformed("date changed");
    }

    public void setDate(Calendar pCalendar) {
        if (pCalendar == null) {
            isNull = true;
        } else {
            isNull = false;
            calendar = pCalendar;
        }
        resetException();
        fireActionPerformed("calendar changed");
    }

    public Date getDate() throws DateParseException {
        checkException();
        if (isNull) {
            return null;
        } else {
            return calendar.getTime();
        }
    }


    public String getCGClassID() {
        return cgClassID;
    }

    public void setCG(DateChooserCG cg) {
        super.setCG(cg);
    }

    public void addActionListener(ActionListener al) {
        addEventListener(ActionListener.class, al);
    }

    public void removeActionListener(ActionListener al) {
        removeEventListener(ActionListener.class, al);
    }

    protected void fireActionPerformed(String actionCommand) {
        // Guaranteed to return a non-null array
        Object[] listeners = getListenerList();
        ActionEvent event = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                if (event == null) {
                    event = new ActionEvent(this,
                                            ActionEvent.ACTION_PERFORMED,
                                            actionCommand);
                }
                ((ActionListener)listeners[i + 1]).actionPerformed(event);
            }
        }
    }

    public void processLowLevelEvent(String name, String[] values) {
        String tDateString = values[0];
        try {
            if (tDateString == null || tDateString.trim().length() == 0) {
                isNull = true;
            } else {
                isNull = false;
                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, getLocale());
                calendar.setTime(dateFormat.parse(tDateString));
            }
            resetException();
            SForm.addArmedComponent(this);

        } catch (ParseException ex) {
            parseException = new DateParseException(tDateString, ex);
        }
    }

    public void fireIntermediateEvents() {
    }

    public void fireFinalEvents() {
        fireActionPerformed("date changed");
    }

    
    /** @see LowLevelEventListener#isEpochChecking() */
    public boolean isEpochChecking() {
        return epochChecking;
    }
  
    /** @see LowLevelEventListener#isEpochChecking() */
    public void setEpochChecking(boolean epochChecking) {
        this.epochChecking = epochChecking;
    }
    
    public static class DateParseException extends Exception {

        public DateParseException(String pDate, ParseException pCause) {
            super(pDate, pCause);
        }

        public String getDateString() {
            return getMessage();
        }

        public ParseException getCauseParseException() {
            return (ParseException)getCause();
        }

    }

}// SDateChooser

/*
   $Log$
   Revision 1.5  2005/01/16 01:00:46  oliverscheck
   Project URL modified to reflect new domain j-wings.org.

   Revision 1.4  2004/10/08 08:43:30  blueshift
   BATCH UPDATE
   - Switched logging to commons logging
   - Correct handling of default button
   - Implemented outdated request feature
   - Implemented an 'approximation' back button
   - More usage of FORM Submit via updated javascript
   - Modified javscript to distinguish jsSubmit from submits by enter-key
   - JavaDoc

   Revision 1.3  2003/12/22 08:59:56  arminhaaf
   o add support for null date

   Revision 1.2  2003/12/19 11:07:15  arminhaaf
   o make it workable and usable

   Revision 1.1  2002/11/19 19:21:01  ahaaf
   o initial

*/
