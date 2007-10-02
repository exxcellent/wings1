/*
 * $Id$
 * (c) 2000 Thinking Objects Software GmbH
 */

package org.wingx.beans.beaneditor;

import java.awt.event.*;

import org.wings.*;
import org.wingx.beans.*;

/**
 * Text Component Editor Adapter
 *
 * @author Michael Reinsch, mreinsch@to.com
 * @version $Revision$
 */

public abstract class STextComponentEditorAdapter
    extends AbstractEditorAdapter
    implements TextListener
{
    protected STextComponent textComponent;
    protected SPanel panel;

    public void reset() {
        textComponent.setText(null);
    }

    public void setValue(Object obj) {
	editor.setValue(obj);
	textComponent.setText(editor.getAsText());
    }

    public Object getValue() {
        editor.setAsText(textComponent.getText());
	return editor.getValue();
    }

    public void textValueChanged(TextEvent e) {
	if (textComponent != e.getSource())
	    return;

	try {
	    editor.setAsText(textComponent.getText());
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
