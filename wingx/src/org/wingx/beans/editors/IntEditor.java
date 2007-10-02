package org.wingx.beans.editors;

import org.wingx.beans.SPropertyEditorSupport;

public class IntEditor extends NumberEditor
{
    public IntEditor() {}

    public void setAsText(String s)
        throws IllegalArgumentException
    {
        setValue(Integer.valueOf(s));
    }
}
