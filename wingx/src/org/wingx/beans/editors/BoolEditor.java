package org.wingx.beans.editors;

import org.wingx.beans.SPropertyEditorSupport;

public class BoolEditor extends SPropertyEditorSupport
{
    public BoolEditor() {}

    public String getJavaInitializationString()
    {
        if(((Boolean)getValue()).booleanValue())
            return "true";
        else
            return "false";
    }

    public String getAsText()
    {
        if(((Boolean)getValue()).booleanValue())
            return "True";
        else
            return "False";
    }

    public void setAsText(String s)
        throws IllegalArgumentException
    {
        if(s.toLowerCase().equals("true"))
            setValue(Boolean.TRUE);
        else
        if(s.toLowerCase().equals("false"))
            setValue(Boolean.FALSE);
        else
            throw new IllegalArgumentException(s);
    }

    public String[] getTags()
    {
        String as[] = {
            "True", "False"
        };
        return as;
    }
}
