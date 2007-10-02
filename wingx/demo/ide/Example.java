/*
 * $Id$
 * (c) Copyright 2000 wingS development team.
 *
 * This file is part of the wingS demo (http://wings.mercatis.de).
 *
 * The wingS demo is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */

package ide;

import java.awt.event.*;
import org.wings.*;


public class Example
    extends SPanel
{
    public SComponent createExample() {
        SPanel p = new SPanel();
        p.add(createTextComponentExample(new STextField()));
        p.add(new SSeparator());

        p.add(createTextComponentExample(new STextArea()));
        return p;
    }

    SForm createTextComponentExample(STextComponent textComp) {
        SForm f = new SForm();

        SGridLayout layout = new SGridLayout(2);
        f.setLayout(layout);

        SLabel desc = new SLabel("Enter some text: ");
        SLabel resultDesc = new SLabel("Entered text:");
        final SLabel result = new SLabel("");
        result.setEscapeSpecialChars(true);
        final SLabel buttonDesc = new SLabel("");
        buttonDesc.setEscapeSpecialChars(true);

        textComp.addTextListener(new TextListener() {
            public void textValueChanged(TextEvent evt) {
                result.setText(((STextComponent)evt.getSource()).getText());
                buttonDesc.setText("button not pressed");
            } } );

        SButton button = new SButton("Submit");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttonDesc.setText("button pressed");
            } } );

        f.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttonDesc.setText(buttonDesc.getText() + " + form event");
            } } );

        f.add(desc);
        f.add(textComp);
        f.add(resultDesc);
        f.add(result);
        f.add(button);
        f.add(buttonDesc);

        return f;
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
