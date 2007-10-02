/*
 * $Id$
 * (c) 2000 Thinking Objects Software GmbH
 */

package org.wingx.beans.beaneditor;

import java.awt.event.*;
import javax.swing.*;

import org.wings.*;
import org.wingx.beans.*;

/**
 * Base List Editor Adapter
 *
 * @author Holger Engels
 * @version $Revision$
 */

public class SComboBoxEditorAdapter
    extends AbstractEditorAdapter
    implements ActionListener
{
    protected SComboBox comboBox;

    public SComboBoxEditorAdapter() {
        comboBox = new SComboBox();
	comboBox.addActionListener(this);
	setComponent(comboBox);
    }

    public void setEditor(SPropertyEditor editor) {
	super.setEditor(editor);
	comboBox.setModel(new DefaultComboBoxModel(editor.getTags()));
    }

    public void reset() {
        comboBox.setSelectedItem(null);
    }

    public void setValue(Object obj) {
	editor.setValue(obj);
	comboBox.setSelectedItem(editor.getAsText());
    }

    public Object getValue() {
        editor.setAsText((String)comboBox.getSelectedItem());
	return editor.getValue();
    }

    public void actionPerformed(ActionEvent e) {
	if (comboBox != e.getSource())
	    return;

	try {
	    editor.setAsText((String)comboBox.getSelectedItem());
	}
	catch (Exception x) {
	    handleException(x);
	}
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * End:
 */
