package ide.editors;

import java.util.*;
import org.wings.*;

import org.wingx.beans.SPropertyEditorSupport;

public class SIconEditor extends SPropertyEditorSupport
{
    private String[] tags = new String[] { "",
					   "org/wings/icons/Warn.gif" };

    public SIconEditor() {}

    public String getJavaInitializationString() {
	return "null";
    }

    public String getAsText() {
	if (getValue() == null)
	    return "";
	else
	    return tags[1];
    }

    public void setAsText(String s)
        throws IllegalArgumentException
    {
	if (s == null || s.length() == 0) {
	    setValue(null);
	    return;
	}

	SIcon icon = new ResourceImageIcon(s);
	setValue(icon);
    }

    public String[] getTags() {
        return tags;
    }
}
