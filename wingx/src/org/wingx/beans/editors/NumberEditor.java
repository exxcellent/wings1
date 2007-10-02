package org.wingx.beans.editors;

import org.wingx.beans.SPropertyEditorSupport;

public abstract class NumberEditor extends SPropertyEditorSupport
{

    public NumberEditor() {}

    public String getJavaInitializationString()
    {
        return "" + getValue();
    }
}
