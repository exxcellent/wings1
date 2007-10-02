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

import org.wings.SComponent;
import org.wings.SContainer;
import org.wings.SCardLayout;
import org.wings.SForm;
import org.wings.SFont;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class SDateChooser
    extends SContainer
    implements DateChooser, ActionListener
{
    private static final boolean DEBUG = true;

    protected SAnchorDateChooser anchorDateChooser = new SAnchorDateChooser();
    protected SFormDateChooser formDateChooser = new SFormDateChooser();

    protected boolean isFormComponent = true;

    protected SCardLayout cardLayout;

    protected static final String ANCHOR_DATECHOOSER = "ANCHOR";
    protected static final String FORM_DATECHOOSER = "FORM";


    public SDateChooser() {
        initGUI();
    }


    protected void initGUI() {
        cardLayout = new SCardLayout();
        setLayout(cardLayout);

        add(anchorDateChooser, ANCHOR_DATECHOOSER);
        add(formDateChooser, FORM_DATECHOOSER);

        anchorDateChooser.addActionListener(this);
        formDateChooser.addActionListener(this);

        formDateChooser.set(anchorDateChooser.getLongDate());
    }

    public void setFormComponent(boolean b) {
        isFormComponent = b;
    }

    public void setFont(SFont f) {
        super.setFont(f);
        anchorDateChooser.setFont(f);
    }

    /*
     * This method could be saved, if the ContainerListener functionality in
     * {@link SContainer} is implemented.
     */
    protected final boolean isFormComponent() {
        if ( isFormComponent ) {
            SComponent parent = getParent();
            while ( parent!=null && !(parent instanceof SForm) ) {
                parent = parent.getParent();
            }

            if ( parent!=null && parent instanceof SForm )
                return true;
        }

        return false;
    }

    public void set(int year, int month, int day) {
        anchorDateChooser.set(year, month, day);
        formDateChooser.set(year, month, day);
    }

    public void set(long longDate) {
        anchorDateChooser.set(longDate);
    }

    public void setTime(long timeInMillis) {
        anchorDateChooser.setTime(timeInMillis);
    }

    public void setDate(Date d) {
        anchorDateChooser.setDate(d);
    }

    public void setDate(Calendar c) {
        anchorDateChooser.setDate(c);
    }

    /*
     * Gibt das Datum als long Value in Millisekunden seit 1.1.1970
     * zurueck. Achtung, das kann natuerlich Probleme geben mit Datum
     * kleiner als 1970 bzw. groesser als 2036 (?). Eine negative Zahl bedeutet
     * kein Datum (also null)
     */
    public long getTime() {
        return anchorDateChooser.getTime();
    }

    public Date getDate() {
        return anchorDateChooser.getDate();
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
        return anchorDateChooser.getLongDate();
    }

    /*
     * Alle die es interessiert, wenn sich der Jahreswert aendert,
     * sollten sich hiermit registrieren.
     */
    public void addActionListener(ActionListener al) {
        anchorDateChooser.addActionListener(al);
    }

    /*
     * Alle die es nicht meht interessiert, wenn sich der Jahreswert aendert,
     * sollten sich hiermit ent-registrieren.
     */
    public void removeActionListener(ActionListener al) {
        anchorDateChooser.removeActionListener(al);
    }

    public void appendPrefix(org.wings.io.Device s) {
        cardLayout.show(isFormComponent() ? (SComponent)formDateChooser :
                        (SComponent)anchorDateChooser);
    }

    protected boolean adjusting = false;

    public void actionPerformed(ActionEvent e) {
        if ( !adjusting ) {
            adjusting = true;
            if ( e.getSource()==anchorDateChooser ) {
                // hier kann es sein, dass 2 Events kommen, zum einen vom DayChooser den
                // Tag deselektiert, dann einen anderen selektiert.
                System.out.println("set Form " + anchorDateChooser.getLongDate());
                formDateChooser.set(anchorDateChooser.getLongDate());
            }
            else if ( e.getSource()==formDateChooser ) {
                System.out.println("set Anchor " + formDateChooser.getLongDate());
                anchorDateChooser.set(formDateChooser.getLongDate());
            }
            adjusting = false;
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
