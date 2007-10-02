package ide.editors;

import java.util.StringTokenizer;

import org.wings.style.AttributeSet;
import org.wings.style.SimpleAttributeSet;
import org.wingx.beans.SPropertyEditorSupport;


public class AttributeSetEditor extends SPropertyEditorSupport
{
    public AttributeSetEditor() {}

    public String getJavaInitializationString() {
	return "null";
    }

    public String getAsText() {
	if (getValue() == null)
	    return "null";
	return ((AttributeSet)getValue()).toString();
    }

    public void setAsText(String s)
        throws IllegalArgumentException
    {
	SimpleAttributeSet set = new SimpleAttributeSet();
	if (!"null".equals(s)) {
	    StringTokenizer tokens = new StringTokenizer(s, ";");
	    while (tokens.hasMoreTokens()) {
		String token = tokens.nextToken();
		int pos = token.indexOf(":");
		if (pos == -1)
		    continue;
		String name = token.substring(0, pos);
		String value = token.substring(pos + 1);
		set.put(name, value);
	    }
	}
	setValue(set);
    }
}
