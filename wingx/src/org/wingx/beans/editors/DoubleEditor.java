// Decompiled by Jad v1.5.7f. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DoubleEditor.java

package org.wingx.beans.editors;

import org.wingx.beans.SPropertyEditorSupport;

// Referenced classes of package org.wingx.beans.editors:
//            NumberEditor

public class DoubleEditor extends NumberEditor
{

    public DoubleEditor()
    {
    }

    public void setAsText(String s)
        throws IllegalArgumentException
    {
        setValue(Double.valueOf(s));
    }
}
