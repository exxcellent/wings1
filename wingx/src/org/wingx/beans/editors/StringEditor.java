package org.wingx.beans.editors;

import org.wingx.beans.SPropertyEditorSupport;

public class StringEditor extends SPropertyEditorSupport
{

    public StringEditor()
    {
    }

    public String getJavaInitializationString()
    {
        return "\"" + getValue() + "\"";
    }

    public void setAsText(String s)
    {
        setValue(s);
    }
}
