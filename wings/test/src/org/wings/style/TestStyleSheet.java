package org.wings.style;

import java.awt.Color;
import java.awt.Font;
import java.beans.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import org.wings.*;

import junit.framework.*;

public class TestStyleSheet
    extends TestCase
{
    private CSSStyleSheet sheet;

    public TestStyleSheet(String name) {
	super(name);
    }

    public void setUp()
	throws Exception
    {
	sheet = new CSSStyleSheet();
	sheet.read(getClass().getResourceAsStream("test.css"));
    }

    public void testRead()
	throws IOException
    {
	/*
	Iterator iterator = sheet.styles().iterator();
	while (iterator.hasNext()) {
	    Style style = (Style)iterator.next();
	    System.err.println(style);
	}
	*/
	Style style = sheet.getStyle("body");
	Assert.assertNotNull(style);
	Assert.assertEquals("#000000", style.get("color"));

	style = sheet.getStyle(".frameborder");
	Assert.assertNotNull(style);
	Assert.assertEquals("#EEEEEE #999999 #999999 #eeeeee", style.get("border-color"));

	style = sheet.getStyle("p");
	Assert.assertNotNull(style);
	SFont font = CSSStyleSheet.getFont(style);
	Assert.assertEquals("sans-serif", font.getFace());
	Assert.assertEquals(Font.PLAIN, font.getStyle());
	Assert.assertEquals(12, font.getSize());

	AttributeSet attributes = CSSStyleSheet.getAttributes(font);
	Assert.assertEquals("sans-serif", attributes.get("font-family"));
	//Assert.assertEquals("normal", attributes.get("font-style"));
	//Assert.assertEquals("normal", attributes.get("font-weight"));
	Assert.assertEquals("12pt", attributes.get("font-size"));

	font.setStyle(Font.BOLD|Font.ITALIC);
	attributes = CSSStyleSheet.getAttributes(font);
	Assert.assertEquals("italic", attributes.get("font-style"));
	Assert.assertEquals("bold", attributes.get("font-weight"));
    }
}
