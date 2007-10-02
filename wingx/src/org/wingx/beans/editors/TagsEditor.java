package org.wingx.beans.editors;

import org.wingx.beans.SPropertyEditorSupport;

public class TagsEditor extends SPropertyEditorSupport
{
    protected Object[] enumerationValues;
    protected String[] tags;

    public TagsEditor() {}

    public void setEnumerationValues(Object[] enumerationValues) {
	this.enumerationValues = enumerationValues;
	tags = new String[enumerationValues.length / 3];

	for (int i=0; i < tags.length; i++)
	    tags[i] = (String)enumerationValues[i*3];
    }

    public String getJavaInitializationString()
    {
	Object value = getValue();
	if (value == null)
	    return null;

	for (int i=0; i < enumerationValues.length; i+=3)
	    if (value.equals(enumerationValues[i+1]))
		return (String)enumerationValues[i+2];

	return null;
    }

    public String getAsText()
    {
	Object value = getValue();
	if (value == null)
	    return "null";

	for (int i=0; i < enumerationValues.length; i+=3)
	    if (value.equals(enumerationValues[i+1]))
		return (String)enumerationValues[i];

	return "null";
    }

    public void setAsText(String string)
        throws IllegalArgumentException
    {
	if (string == null)
            throw new IllegalArgumentException("null");

	for (int i=0; i < enumerationValues.length; i+=3)
	    if (string.equals(enumerationValues[i])) {
		setValue(enumerationValues[i+1]);
		return;
	    }

	throw new IllegalArgumentException(string);
    }

    public String[] getTags()
    {
        return tags;
    }
}
