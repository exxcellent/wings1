package ide;

import java.awt.Color;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.*;
import javax.xml.parsers.*;

import org.apache.tools.ant.*;
import org.wings.*;
import org.wings.event.*;
import org.wings.plaf.*;
import org.wings.session.*;
import org.w3c.dom.*;

public class TestFrame
    extends SFrame
{
    static final Collection forbidden = new ArrayList();
    static {
	forbidden.add("background");
	forbidden.add("font");
	forbidden.add("foreground");
	forbidden.add("locale");
	forbidden.add("model");
	forbidden.add("parent");
	forbidden.add("showAsFormComponent");
    }

    Map modules;

    SPanel panel = new SPanel();
    SForm form = new SForm();

    public TestFrame(String name, Map modules) {
	super("test frame");
	this.modules = modules;
	setName(name);

	getContentPane().setLayout(new SFlowDownLayout());
	getContentPane().add(panel);
	getContentPane().add(form);

	final PropertyPanel propertyPanel = (PropertyPanel)modules.get("properties");
	// eigentlich sollte man einen property change listener bei der test component
	// registrieren!
	propertyPanel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent event) {
		    copyState(propertyPanel.getComponent());
		}
	    });
    }

    public void setComponent(SComponent component) {
	provideState(component);
	panel.removeAll();
	panel.add(component);
	copyState(component);
    }

    protected void copyState(SComponent component) {
	try {
	    System.out.println("copy state");
	    Class clazz = component.getClass();
	    SComponent copy = (SComponent)clazz.newInstance();
	    form.removeAll();
	    form.add(copy);

	    provideState(copy);

	    ComponentCG cg = component.getCG();
	    Method cgSetter = LafPanel.findCGSetter(clazz);
	    System.out.println("copying cg=" + cg);
	    cgSetter.invoke(copy, new Object[] { cg });

	    BeanInfo info = Introspector.getBeanInfo(clazz);
	    PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
	    for (int i=0; i < descriptors.length; i++) {
		Method getter = descriptors[i].getReadMethod();
		Method setter = descriptors[i].getWriteMethod();

		if (getter == null || setter == null)
		    continue;
		if (descriptors[i] instanceof IndexedPropertyDescriptor)
		    continue;
		if (forbidden.contains(descriptors[i].getName()))
		    continue;

		Object value = getter.invoke(component, null);
		System.out.println("copying " + descriptors[i].getName() + "=" + value);
		setter.invoke(copy, new Object[] { value });
	    }
	}
	catch (Exception e) {
	    form.removeAll();
	    form.add(new SLabel(e.getClass().getName() + ": " + e.getMessage()));
	    e.printStackTrace(System.err);
	}
    }

    public void provideState(SComponent component) {
	if (component instanceof SLabel)
	    provideState((SLabel)component);
	if (component instanceof SButton)
	    provideState((SButton)component);
	else if (component instanceof SList)
	    provideState((SList)component);
    }

    public void provideState(SLabel label) {
	label.setText("test");
	label.setIcon(new ResourceImageIcon("org/wings/icons/Warn.gif"));
    }

    public void provideState(SButton button) {
	button.setText("test");
	button.setIcon(new ResourceImageIcon("org/wings/icons/Warn.gif"));
    }

    public void provideState(SList list) {
        SLabel color = new SLabel("");
        color.setForeground(Color.green);
        color.setText(Color.green.toString());

        Object[] values = {
            "element1",
            color,
            "element3",
            "element4"
        };

        list.setListData(values);
    }
}
