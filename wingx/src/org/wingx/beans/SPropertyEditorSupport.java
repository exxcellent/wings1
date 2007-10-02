package org.wingx.beans;

import java.beans.*;
import org.wings.*;
import org.wings.io.*;

/**
 * This is the pendant to the java.beans.PropertyEditorSupport.
 * <p>
 * For documentation refer to the JDK API Specification.
 */
public class SPropertyEditorSupport implements SPropertyEditor
{
    private Object value;
    private Object source;
    private java.util.Vector listeners;

    protected SPropertyEditorSupport() {
	source = this;
    }

    protected SPropertyEditorSupport(Object source) {
	if (source == null) {
	   throw new NullPointerException();
	}
	this.source = source;
    }

    public void setValue(Object value) {
	this.value = value;
	firePropertyChange();
    }

    public Object getValue() {
	return value;
    }

    public boolean isWriteable() {
	return false;
    }

    public void writeValue(Device d) {}

    public String getJavaInitializationString() {
	return "???";
    }

    public String getAsText() {
	if (value instanceof String) {
	    return (String)value;
	}
	return "" + value;
    }

    public void setAsText(String text)
	throws java.lang.IllegalArgumentException
    {
	if (value instanceof String) {
	    setValue(text);
	    return;
	}
	throw new java.lang.IllegalArgumentException(text);
    }

    public String[] getTags() {
	return null;
    }

    public SComponent getCustomEditor() { return null;  }
    public boolean supportsCustomEditor() { return false; }
  
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
	if (listeners == null)
	    listeners = new java.util.Vector();

	listeners.addElement(listener);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
	if (listeners == null)
	    return;

	listeners.removeElement(listener);
    }

    public void firePropertyChange() {
	java.util.Vector targets;
	synchronized (this) {
	    if (listeners == null)
	    	return;
	    targets = (java.util.Vector)listeners.clone();
	}

        PropertyChangeEvent evt = new PropertyChangeEvent(source, null, null, null);

	for (int i = 0; i < targets.size(); i++) {
	    PropertyChangeListener target = (PropertyChangeListener)targets.elementAt(i);
	    target.propertyChange(evt);
	}
    }
}
