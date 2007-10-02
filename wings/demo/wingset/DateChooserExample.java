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

package wingset;

import org.wings.SComponent;
import org.wings.SDateChooser;
import org.wings.SForm;
import org.wings.SLabel;
import org.wings.SButton;
import wingset.WingSetPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.util.Date;


/**
 * <!--
 * DateChooserExample.java
 * Created: Mon Nov 18 21:11:24 2002
 * -->
 *
 *
 *
 * @author <a href="mailto:armin.haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class DateChooserExample extends WingSetPane {

    protected SDateChooser dateChooser = new SDateChooser();
    protected SLabel dateLabel = new SLabel();

    /**
     *
     */
    public DateChooserExample() {

    }

    public SComponent createExample() {
        SForm form = new SForm();

        form.add(dateChooser);
        form.add(dateLabel);

        form.add(new SButton("ok"));

        form.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Date tDate = dateChooser.getDate();
                    if (tDate == null) {
                        dateLabel.setText("null");
                    } else {
                        dateLabel.setText(DateFormat.getDateInstance(DateFormat.MEDIUM, dateChooser.getLocale()).format(dateChooser.getDate()));
                    }
                } catch (SDateChooser.DateParseException ex) {
                    dateLabel.setText(ex.getCauseParseException().getMessage());
                }
            }
        });

        return form;
    }


}// DateChooserExample

/*
   $Log$
   Revision 1.8  2005/01/16 01:01:11  oliverscheck
   Project URL modified to reflect new domain j-wings.org.

   Revision 1.7  2003/12/22 08:59:56  arminhaaf
   o add support for null date

   Revision 1.6  2003/12/19 11:07:15  arminhaaf
   o make it workable and usable

   Revision 1.5  2002/11/19 19:21:18  ahaaf
   o initial

*/
