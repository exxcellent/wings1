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

import java.awt.*;
import java.awt.event.*;

import org.wings.*;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class ButtonExample
    extends WingSetPane
{
    static final ClassLoader cl = WingSet.class.getClassLoader();
    static final SIcon icon = 
        new SResourceIcon(cl, "wingset/icons/ButtonIcon.gif");
    static final SIcon disabledIcon = 
        new SResourceIcon(cl, "wingset/icons/ButtonDisabledIcon.gif");
    static final SIcon pressedIcon = 
        new SResourceIcon(cl, "wingset/icons/ButtonPressedIcon.gif");
    static final SIcon rolloverIcon = 
        new SResourceIcon(cl, "wingset/icons/ButtonRolloverIcon.gif");
    
    public SComponent createExample() {
        SPanel p = new SPanel(new SGridLayout(2));
        
        p.add(new SLabel("<html><h4>Buttons outside forms</h4>"));
        p.add(new SLabel("<html><h4>Image buttons outside forms</h4>"));
        p.add(createButtonExample());
        p.add(createImageButtonExample());
        
        SForm form = new SForm();
        form.add(new SLabel("<html><h4>Buttons in a form</h4>"));
        form.add(createButtonExample());
        p.add(form);
            
        form = new SForm();
        form.add(new SLabel("<html><h4>Image buttons in a form</h4>"));
        form.add(createImageButtonExample());
        p.add(form);
        return p;
    }

    SContainer createButtonExample() {
        SPanel text = new SPanel();

        final SLabel pressed = new SLabel("no Button pressed");

        ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pressed.setText("Button \"" + e.getActionCommand() + "\" pressed");
            }
        };

        for ( int i=0; i<3; i++ ) {
            SButton b = new SButton("text " + (i+1));
            // b.setBorder(new SLineBorder(1));
            b.addActionListener(action);
            text.add(b);
        }
        text.add(new SLabel("<html><br />"));
        text.add(pressed);

        return text;
    }

    SContainer createImageButtonExample() {

        SButton[] buttons = new SButton[9];
        buttons[0] = new SButton("testTL");
        buttons[1] = new SButton("testTC");
        buttons[2] = new SButton("testTR");
        buttons[3] = new SButton("testCL");
        buttons[4] = new SButton("testCC");
        buttons[5] = new SButton("testCR");
        buttons[6] = new SButton("testBL");
        buttons[7] = new SButton("testBC");
        buttons[8] = new SButton("testBR");

        for ( int i=0; i<buttons.length; i++ ) {
            buttons[i].setActionCommand(buttons[i].getText());
            if ( i!=4 ) {
                buttons[i].setIcon(icon);
                buttons[i].setDisabledIcon(disabledIcon);
                buttons[i].setRolloverIcon(rolloverIcon);
                buttons[i].setPressedIcon(pressedIcon);
                buttons[i].setToolTipText("Button " + i);
                buttons[i].setName(buttons[i].getText());
            }
        }


        buttons[0].setVerticalTextPosition(SConstants.TOP);
        buttons[0].setHorizontalTextPosition(SConstants.LEFT);

        buttons[1].setVerticalTextPosition(SConstants.TOP);
        buttons[1].setHorizontalTextPosition(SConstants.CENTER);

        buttons[2].setVerticalTextPosition(SConstants.TOP);
        buttons[2].setHorizontalTextPosition(SConstants.RIGHT);

        buttons[3].setVerticalTextPosition(SConstants.CENTER);
        buttons[3].setHorizontalTextPosition(SConstants.LEFT);

        buttons[4].setVerticalTextPosition(SConstants.CENTER);
        buttons[4].setHorizontalTextPosition(SConstants.CENTER);

        buttons[5].setVerticalTextPosition(SConstants.CENTER);
        buttons[5].setHorizontalTextPosition(SConstants.RIGHT);

        buttons[6].setVerticalTextPosition(SConstants.BOTTOM);
        buttons[6].setHorizontalTextPosition(SConstants.LEFT);

        buttons[7].setSelected(true);
        buttons[7].setEnabled(false);
        buttons[7].setVerticalTextPosition(SConstants.BOTTOM);
        buttons[7].setHorizontalTextPosition(SConstants.CENTER);

        buttons[8].setSelected(false);
        buttons[8].setEnabled(false);
        buttons[8].setVerticalTextPosition(SConstants.BOTTOM);
        buttons[8].setHorizontalTextPosition(SConstants.RIGHT);

        SPanel erg = new SPanel();

        SGridLayout grid = new SGridLayout(3,3);
        grid.setBorder(1);
        SPanel b = new SPanel(grid);

        final SLabel pressed = new SLabel("no Button pressed");

        ActionListener action = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pressed.setText("Button \"" + e.getActionCommand() + "\" pressed");
            }
        };

        for ( int i=0; i<buttons.length; i++ ) {
            buttons[i].addActionListener(action);
        }

        for ( int i=0; i<buttons.length; i++ )
            b.add(buttons[i]);

        erg.add(b);
        erg.add(new SLabel("<html><br />"));
        erg.add(pressed);

        return erg;
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
