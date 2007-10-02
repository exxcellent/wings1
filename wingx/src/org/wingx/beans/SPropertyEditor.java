package org.wingx.beans;

import java.beans.*;
import org.wings.*;
import org.wings.io.*;

/**
 * This is the pendant to the java.beans.PropertyEditor.
 * <p>
 * For documentation refer to the JDK API Specification.
 */
public interface SPropertyEditor
{
    void setValue(Object value);
    Object getValue();

    boolean isWriteable();
    void writeValue(Device d);

    String getJavaInitializationString();

    String getAsText();
    void setAsText(String text) throws java.lang.IllegalArgumentException;
    String[] getTags();

    SComponent getCustomEditor();
    boolean supportsCustomEditor();

    void addPropertyChangeListener(PropertyChangeListener listener);
    void removePropertyChangeListener(PropertyChangeListener listener);
}
