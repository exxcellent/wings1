/*
 * $Id$
 * (c) 2000 Thinking Objects Software GmbH
 */

package org.wingx.beans.beaneditor;

import java.awt.event.*;
import java.beans.*;

import org.wings.*;
import org.wingx.beans.*;

/**
 * Text Area Editor Adapter
 *
 * @author Michael Reinsch, mreinsch@to.com
 * @version $Revision$
 */

public class STextAreaEditorAdapter
    extends STextComponentEditorAdapter
    implements BeanEditorConstants
{
    public STextAreaEditorAdapter() {
        textComponent = new STextArea();
	textComponent.addTextListener(this);
	setComponent(textComponent);
    }

    public void init(PropertyDescriptor descriptor) {
        STextArea field = (STextArea)textComponent;
        Integer val;
        if ((val = (Integer)descriptor.getValue(COLUMNS)) != null)
            field.setColumns(val.intValue());
        if ((val = (Integer)descriptor.getValue(ROWS)) != null)
            field.setRows(val.intValue());
    }
}
