/*
 * $Id$
 * (c) Copyright 2000 wingS development team.
 *
 * This file is part of the wingS demo (http://j-wings.org).
 *
 * The wingS demo is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */
package wingset;


import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.wings.SResourceIcon;
import org.wings.SBorderLayout;
import org.wings.SButtonGroup;
import org.wings.SComponent;
import org.wings.SConstants;
import org.wings.SDimension;
import org.wings.SFlowDownLayout;
import org.wings.SFlowLayout;
import org.wings.SForm;
import org.wings.SIcon;
import org.wings.SLabel;
import org.wings.SPanel;
import org.wings.SRadioButton;
import org.wings.STabbedPane;
import org.wings.STextArea;

/**
 * Example for STabbedPane.
 * @author <a href="mailto:andre@lison.de">Andre Lison</a>
 */
public class TabbedPaneExample extends WingSetPane
{
    private final static SIcon JAVA_CUP_ICON = 
        new SResourceIcon("org/wings/icons/JavaCup.gif");

    private final static SIcon SMALL_COW_ICON = 
        new SResourceIcon("wingset/icons/cowSmall.gif");

    /**
     * Constructor for TabbedPaneExample.
     */
    protected SComponent createExample() {
        SForm c = new SForm(new SBorderLayout());

        final STabbedPane tpane = new STabbedPane();
        tpane.setPreferredSize(new SDimension(400, 200));
        tpane.setBackground(new java.awt.Color(200, 200, 255));
        tpane.setShowAsFormComponent(false);

        SButtonGroup grp = new SButtonGroup();
        SPanel choice = new SPanel(new SFlowLayout());
        choice.add(new SLabel("Tab Placement: "));
        Object[] btns = {
	    "Top",	new Integer(SConstants.TOP),
	    "Left",	new Integer(SConstants.LEFT),
	    "Bottom",	new Integer(SConstants.BOTTOM),
	    "Right",	new Integer(SConstants.RIGHT),
	};

        for (int i = 0; i < btns.length; i += 2) {
            SRadioButton btn = new SRadioButton(btns[i].toString());
            btn.setActionCommand(btns[i + 1].toString());
            btn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        tpane.setTabPlacement(Integer.parseInt(ae.getActionCommand()));
                    }
                });
            btn.setShowAsFormComponent(false);
            grp.add(btn);
            if (i == 0) {
                grp.setSelected(btn, true);
            }
            choice.add(btn);
        }

        choice.setHorizontalAlignment(SConstants.CENTER);
        c.add(choice, "North");

        final STextArea text = new STextArea();
        text.setPreferredSize(new SDimension(400, 200));
        for (int i = 0; i < 12; ++i) {
            SPanel p = new SPanel(new SBorderLayout());
            p.add(new SLabel("Tab # " + i), "North");
            p.add(text);
            tpane.add("Tab " + i, p);
        }
	tpane.setIconAt(3, JAVA_CUP_ICON);
	tpane.setIconAt(8, SMALL_COW_ICON);
	tpane.setEnabledAt(1, false);
        tpane.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent ce) {
		    String t = text.getText();
		    t += "\n";
		    t += "wingS: Changed to tab " + tpane.getSelectedIndex() + "\n";
		    text.setText(t);
		}
	    });
        c.add(tpane, "Center");

        choice = new SPanel(new SFlowDownLayout());
        grp = new SButtonGroup();
        choice.add(new SLabel("Color: "));
        final Object[] clrs = {
	    "Yellow",		Color.yellow,
	    "Green",		Color.green,
	    "Lightblue",	new Color(200, 200, 255),
	    "Lightgray",	Color.lightGray,
	    "Orange",		new Color(255, 153, 0)};

        for (int i = 0; i < clrs.length; i += 2) {
            SRadioButton btn = new SRadioButton(clrs[i].toString());
            btn.setActionCommand("" + i);
            btn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                       tpane.setBackground((Color) clrs[Integer.parseInt(ae.getActionCommand()) + 1]);
                    }
                });
            btn.setShowAsFormComponent(false);
            grp.add(btn);
            if (i == 0) {
                grp.setSelected(btn, true);
            }
            choice.add(btn);
        }
        choice.setHorizontalAlignment(SConstants.CENTER);
        c.add(choice, "West");
        
        return c;
    }
}
