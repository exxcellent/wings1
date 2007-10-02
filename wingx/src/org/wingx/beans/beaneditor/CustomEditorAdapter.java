/*
 * $Id$
 * (c) 2000 Thinking Objects Software GmbH
 */

package org.wingx.beans.beaneditor;

import java.beans.*;
import org.wings.*;
import org.wingx.beans.*;

/**
 * CustomAdapter
 *
 * @author Holger Engels
 * @version $Revision$
 */

public class CustomEditorAdapter
    extends AbstractEditorAdapter
{
    protected SComponent custom;

    public void setEditor(SPropertyEditor editor) {
	super.setEditor(editor);
	custom = editor.getCustomEditor();
	((SContainer)getPanel()).add(custom);
    }

    public SComponent getComponent() {
        return getPanel();
    }

    public void setValue(Object obj) {
	editor.setValue(obj);
    }
    public Object getValue() {
	return editor.getValue();
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * End:
 */
