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
import java.util.Calendar;
import java.util.Date;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public interface DateChooser
{
    int MONTH_LENGTH[]= {
        31,28,31,30,31,30,31,31,30,31,30,31
    }; // 0-based

    int LEAP_MONTH_LENGTH[] = {
        31,29,31,30,31,30,31,31,30,31,30,31
    }; // 0-based


    /*
     * Month is 0 base (0 is jan, 1 feb,...).
     * So for 1.1.1999 it should be year 1999, month 0, day 1.
     */
    void set(int year, int month, int day);

    /*
     * Setzt das Datum in folgendem Format:<BR>
     * <UL>
     * <LI> die ersten 4 Ziffern sind das Jahrhundert
     * <LI> die 5. und die 6. Ziffer der Monat
     * <LI> die 7. und die 8. Ziffer der Tag
     * <LI> das Vorzeichen bestimmt die Aera: - BC und + AD
     * <LI> -1 entspricht keinem Datum (also null)
     * </UL>
     */
    void set(long longDate);

    void setTime(long timeInMillis);

    void setDate(Date d);

    void setDate(Calendar c);

    /*
     * Gibt das Datum als long Value in Millisekunden seit 1.1.1970
     * zurueck. Achtung, das kann natuerlich Probleme geben mit Datum
     * kleiner als 1970 bzw. groesser als 2036 (?). Eine negative Zahl bedeutet
     * kein Datum (also null)
     */
    long getTime();

    Date getDate();

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
    long getLongDate();

    /*
     * Alle die es interessiert, wenn sich das Datum aendert,
     * sollten sich hiermit registrieren.
     */
    void addActionListener(ActionListener al);

    /*
     * Alle die es nicht meht interessiert, wenn sich das Datum aendert,
     * sollten sich hiermit ent-registrieren.
     */
    void removeActionListener(ActionListener al);
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
