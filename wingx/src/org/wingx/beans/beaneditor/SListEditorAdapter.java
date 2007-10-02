/*
 * $Id$
 * (c) 2000 Thinking Objects Software GmbH
 */

package org.wingx.beans.beaneditor;

import java.beans.*;

import org.wings.*;

/**
 * List Editor Adapter
 *
 * @author Michael Reinsch, mreinsch@to.com
 * @version $Revision$
 */

public class SListEditorAdapter
    extends AbstractEditorAdapter
{
    protected SList component;

    public void reset() {
        component.clearSelection();
    }

    public void setValue( Object obj ) {
        component.setSelectedValue( obj );
    }

    public Object getValue() {
        return component.getSelectedValue();
    }

    public void init( PropertyDescriptor descriptor ) {
    }

    public SComponent getComponent() {
        return component;
    }
}
