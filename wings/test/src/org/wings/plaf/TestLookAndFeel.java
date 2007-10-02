package org.wings.plaf;

import java.awt.Color;
import java.awt.Font;
import java.beans.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import org.wings.*;
import org.wings.session.*;
import org.wings.style.*;

import junit.framework.*;

public class TestLookAndFeel
    extends TestCase
{
    boolean notified;

    public TestLookAndFeel(String name) {
	super(name);
    }

    public void setUp()
	throws Exception
    {}

    public void testLookAndFeel()
	throws IOException
    {
	URL url = getClass().getResource("/css1.jar");
	ClassLoader classLoader = new URLClassLoader(new URL[] { url }, getClass().getClassLoader());
	LookAndFeel laf = new LookAndFeel(classLoader);

	// makeCG
	ComponentCG componentCG = (ComponentCG)laf.makeCG("org.wings.plaf.css1.ButtonCG");
	Assert.assertNotNull(componentCG);
	LayoutCG layoutCG = (LayoutCG)laf.makeCG("org.wings.plaf.xhtml.css1.BorderLayoutCG");
	Assert.assertNotNull(layoutCG);
	BorderCG borderCG = (BorderCG)laf.makeCG("org.wings.plaf.xhtml.css1.EtchedBorderCG");
	Assert.assertNotNull(borderCG);

	// makeAttributeSet
	AttributeSet attributeSet = laf.makeAttributeSet("color:#123456");
	Assert.assertEquals("#123456", attributeSet.get("color"));

	// makeObject
	Object object = laf.makeObject("42", Integer.class);
	Assert.assertEquals(Integer.class, object.getClass());
	Assert.assertEquals(42, ((Integer)object).intValue());
    }

    public void testCGDefaults() {
	CGDefaults parent = new CGDefaults(null);
	parent.put("test", Boolean.TRUE);
	Assert.assertEquals(Boolean.TRUE, parent.get("test", Boolean.class));

	CGDefaults defaults = new CGDefaults(parent);
	Assert.assertEquals(Boolean.TRUE, defaults.get("test", Boolean.class));

	defaults.put("test", Boolean.FALSE);
	Assert.assertEquals(Boolean.FALSE, defaults.get("test", Boolean.class));
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
