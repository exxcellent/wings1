package ide.editors;

import java.util.*;
import org.wings.style.*;
import org.wings.session.*;

import org.wingx.beans.SPropertyEditorSupport;

public class StyleEditor extends SPropertyEditorSupport
{
    private String[] tags;

    public StyleEditor() {}

    public String getJavaInitializationString() {
	if (getValue() == null)
	    return "null";

	String name = ((Style)getValue()).getName();
	return "SessionManager.getSession().getCGManager().getLookAndFeel().getStyleSheet().getStyle(" + name + ")";
    }

    public String getAsText() {
	if (getValue() == null)
	    return "";
        return ((Style)getValue()).getName();
    }

    public void setAsText(String s)
        throws IllegalArgumentException
    {
	if (s == null || s.length() == 0) {
	    setValue(null);
	    return;
	}

	Style style = SessionManager.getSession().getRootFrame().getStyleSheet().getStyle(s);
	if (style == null)
            throw new IllegalArgumentException(s);

	setValue(style);
    }

    public String[] getTags() {
	if (tags == null) {
	    StyleSheet sheet = SessionManager.getSession().getRootFrame().getStyleSheet();
	    Style[] styles = (Style[])sheet.styles().toArray(new Style[sheet.styles().size()]);
	    tags = new String[styles.length + 1];
	    tags[0] = "";
	    for (int i=0; i < styles.length; i++)
		tags[i+1] = styles[i].getName();
	    Arrays.sort(tags);
	}
        return tags;
    }
}
