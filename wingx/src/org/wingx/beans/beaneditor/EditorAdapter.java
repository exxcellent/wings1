/*
 * $Id$
 * (c) 2000 Thinking Objects Software GmbH
 */

package org.wingx.beans.beaneditor;

import java.beans.*;

import org.wings.*;
import org.wingx.beans.*;

/**
 * Component Editor Adapter
 *
 * @author Michael Reinsch, mreinsch@to.com
 * @version $Revision$
 */

public interface EditorAdapter
{
    void setEditor(SPropertyEditor editor);

    /** must be called before using the adapter */
    void init(PropertyDescriptor descriptor);

    /** resets the component */
    void reset();

    /** sets the value of the component */
    void setValue(Object obj);

    /** gets the value of the component */
    Object getValue();

    /** gets the component */
    SComponent getComponent();
}
