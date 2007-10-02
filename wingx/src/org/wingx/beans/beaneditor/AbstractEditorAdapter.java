package org.wingx.beans.beaneditor;

import java.awt.event.*;
import java.beans.*;
import org.wings.*;
import org.wingx.beans.*;

/**
 * Abstract Editor Adapter
 *
 * @author Holger Engels
 * @version$
 */

public abstract class AbstractEditorAdapter
    implements EditorAdapter, PropertyChangeListener
{
    private SPanel panel = null;
    private SComponent component;
    private SButton button = null;
    SPropertyEditor editor;

    public void setEditor(SPropertyEditor editor) {
	this.editor = editor;
	editor.addPropertyChangeListener(this);
    }    

    public void init(PropertyDescriptor descriptor) {}
    public void reset() {}

    public void propertyChange(PropertyChangeEvent evt) {
	editor.getAsText();
    }

    public void setValue(Object obj) {
	editor.setValue(obj);
    }

    protected void setComponent(SComponent component) {
	this.component = component;
    }

    public SComponent getComponent() {
	if (editor.supportsCustomEditor())
	    return getPanel();
	else
	    return component;
    }

    protected SComponent getPanel() {
	if (panel == null) {
	    panel = new SPanel();
	    panel.add(component);
	    button = new SButton("...");
	    button.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent event) {
			showCustomEditor();
		    }
		});
	    panel.add(button);
	}
	return panel;
    }

    private void showCustomEditor() {
	CustomEditorDialog dialog = new CustomEditorDialog(editor.getCustomEditor());
	dialog.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent event) {
		    setValue(editor.getValue());
		}
	    });
	dialog.show(component.getParentFrame());
    }

    public void handleException(Exception e) {
	SOptionPane.showMessageDialog(component.getParentFrame(), e, "ERROR setting property");
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * End:
 */
