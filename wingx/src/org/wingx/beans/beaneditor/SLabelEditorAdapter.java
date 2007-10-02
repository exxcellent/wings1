/*
 * $Id$
 * (c) 2000 Thinking Objects Software GmbH
 */

package org.wingx.beans.beaneditor;

import java.beans.*;
import org.wings.*;
import org.wingx.beans.*;

/**
 * Label Component Editor Adapter
 *
 * @author Michael Reinsch, mreinsch@to.com
 * @version $Revision$
 */

public class SLabelEditorAdapter
    extends AbstractEditorAdapter
{
    protected SLabel component;

    public SLabelEditorAdapter() {
        component = new SLabel();
    }

    public void reset() {
        component.setText(null);
    }

    public void setValue(Object obj) {
	editor.setValue(obj);
	component.setText(editor.getAsText());
    }

    public Object getValue() {
        editor.setAsText(component.getText());
	return editor.getValue();
    }

    public SComponent getComponent() {
        return component;
    }
}
