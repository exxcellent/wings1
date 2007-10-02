/*
 * $Id$
 * (c) Copyright 2000 wingS development team.
 *
 * This file is part of wingS (http://j-wings.org).
 *
 * wingS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */

package org.wingx.plaf.xhtml;

import java.io.IOException;

import org.wings.*;
import org.wings.border.*;
import org.wings.io.Device;
import java.util.*;
import org.wings.script.*;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import org.wings.plaf.ContainerCG;

/**
 *
 *  * @author <a href="mailto:e.habicht@thiesen.com">Erik Habicht</a>
 */
public class CalendarCG implements ContainerCG {

    public  Locale     locale        = null;
    private DateFormat dateFormat    = null;

    private final static byte[] __table_cellspac= "\n<table cellspacing=\"0\" cellpadding=\"0\">\n<tr><td>\n".getBytes();
    private final static byte[] __td_td_input_st= "\n</td><td>\n<input style=\"margin-left:2px;\" type=\"image\" id=\"".getBytes();
    private final static byte[] __src           = "\" src=\"".getBytes();
    private final static byte[] __alt           = "\" alt=\"".getBytes();
    private final static byte[] __              = "\"".getBytes();
    private final static byte[] __disabled      = " disabled".getBytes();
    private final static byte[] ___1            = ">".getBytes();
    private final static byte[] __td_tr_table_sc= "\n</td></tr>\n</table>\n<script type=\"text/javascript\">\n".getBytes();
    private final static byte[] __function_fireC= "    function fireChangeEvent() {\n        fireEvent(\"".getBytes();
    private final static byte[] __change        = "\", \"change\");\n    }\n".getBytes();
 
    public void installCG(final SComponent comp) {
        final org.wingx.XCalendar component = (org.wingx.XCalendar) comp;

        component.addScriptListener(CALENDAR_SETUP_SCRIPT_LOADER);
        component.addScriptListener(getLangScriptLoader());
        component.addScriptListener(CALENDAR_SCRIPT_LOADER);
    }
    public void uninstallCG(final SComponent comp) {
        final org.wingx.XCalendar component = (org.wingx.XCalendar) comp;

        component.removeScriptListener(CALENDAR_SETUP_SCRIPT_LOADER);
        if ( calendarLangScriptLoader != null ) {
            component.removeScriptListener(calendarLangScriptLoader);
        }
        component.removeScriptListener(CALENDAR_SCRIPT_LOADER);
    }
    
    public static final JavaScriptListener CALENDAR_SCRIPT_LOADER =
    new JavaScriptListener("", "", loadScript("org/wingx/calendar/calendar.js"));

    public static final JavaScriptListener CALENDAR_SETUP_SCRIPT_LOADER =
    new JavaScriptListener("", "", loadScript("org/wingx/calendar/calendar-setup.js"));

    public JavaScriptListener calendarLangScriptLoader = null;

    private JavaScriptListener getLangScriptLoader () {
        
        if ( calendarLangScriptLoader == null ) {
            calendarLangScriptLoader = new JavaScriptListener( "", "", loadScript( getLangScriptURL() ) );
        }
        return calendarLangScriptLoader;
        
    } 
    
    /**
     * Returns the language file.
     *
     */
    private String getLangScriptURL () {
        String retVal = "org/wingx/calendar/lang/calendar-" + getLocale().getLanguage() + ".js";
        java.net.URL url = org.wings.plaf.MenuCG.class.getClassLoader().getResource( retVal );
        if ( url == null ) {    
            retVal = "org/wingx/calendar/lang/calendar-en.js";
        }
        return retVal;
    }    

    public static final SResourceIcon DATE_ICON = new SResourceIcon("org/wingx/calendar/images/date.png");

    public static String loadScript(String js) {
        InputStream in = null;
        BufferedReader reader = null;

        try {
            in = org.wings.plaf.MenuCG.class.getClassLoader().getResourceAsStream(js);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null)
            buffer.append(line).append("\n");

            return buffer.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            try { in.close(); } catch (Exception ign) {}
            try { reader.close(); } catch (Exception ign1) {}
        }
    }

    
    /**
     * Returns a jscalender usable date pattern.
     * @return the pattern
     */
    private String getPattern () {    
        
        GregorianCalendar calendar = new GregorianCalendar(3,0,2);
        
        String  pattern = dateFormat.format(calendar.getTime());
            pattern = pattern.replaceAll( "3", "%Y" );
            pattern = pattern.replaceAll( "1", "%m" );
            pattern = pattern.replaceAll( "2", "%d" );
            pattern = pattern.replaceAll( "\\d", "" );
            
        return pattern;
    }    
    
    public static class CalendarStyleSheet extends org.wings.DynamicResource {

        public CalendarStyleSheet(org.wingx.XCalendar comp)
            {
            super(comp.getParentFrame(), "css", "text/css");
            comp.getParentFrame().addDynamicResource(this);
            comp.getParentFrame().addHeader(new org.wings.header.Link("stylesheet", null, "text/css", null, this));
        }

        public void write(Device d) throws IOException {
            d.write( loadScript("org/wingx/calendar/calendar.css").getBytes() );
        }
    }

    public DynamicResource installStyleSheet(org.wingx.XCalendar comp) {
        return new CalendarStyleSheet(comp);
    }
    
    public void setDateFormat( DateFormat dateFormat ) {
        this.dateFormat = dateFormat;
    }
    
    public void setLocale( Locale locale ) {
        this.locale = locale;
    }
    
    public Locale getLocale( ) {
        if ( this.locale != null ) {
            return locale;
        } else {
            return Locale.getDefault();
        }
    }
    
    public void write(final org.wings.io.Device device,
                      final org.wings.SComponent _c)
        throws java.io.IOException {
        if ( !_c.isVisible() ) return;
        _c.fireRenderEvent(SComponent.START_RENDERING);
        final org.wingx.XCalendar component = (org.wingx.XCalendar) _c;
        final SBorder _border = component.getBorder();
        if (_border != null) { _border.writePrefix(device); }
        
        STextField textField = component.getTextField();
        SButton    button    = component.getButton();
        
        String textFieldId = textField.getComponentId();
        String buttonId    = button.getComponentId();

        device.write(__table_cellspac);
        textField.write(device); 
        device.write(__td_td_input_st);
        org.wings.plaf.compiler.Utils.write( device, buttonId);
        device.write(__src);
        org.wings.plaf.compiler.Utils.write( device, DATE_ICON.getURL());
        device.write(__alt);
        org.wings.plaf.compiler.Utils.write( device, button.getText());
        device.write(__);
        if ( !button.isEnabled() ) {
            device.write(__disabled);
        }
        device.write(___1);
        device.write(__td_tr_table_sc);
        if ( component.getActionListeners().length > 0 ) {  
            device.write(__function_fireC);
            org.wings.plaf.compiler.Utils.write( device, textFieldId);
            device.write(__change);
        }

        device.write("\n    Calendar.setup(\n    {".getBytes() );
        device.write("\n      inputField  : \"".getBytes() ).write( textFieldId.getBytes() ).write( "\"".getBytes() );
        device.write(",\n      ifFormat    : \"".getBytes() ).write( getPattern().getBytes() ).write( "\"".getBytes() );
        device.write(",\n      button      : \"".getBytes() ).write( buttonId.getBytes() ).write( "\"".getBytes() );  
        device.write(",\n      showOthers  : true".getBytes() );
        device.write(",\n      electric    : false".getBytes() );
        if ( component.getActionListeners().length > 0 ) {  
            device.write(",\n      onUpdate    : fireChangeEvent\n".getBytes() );
        }
        device.write("    });\n".getBytes() );
        device.write("</script>\n".getBytes() );

        if (_border != null) { _border.writePostfix(device); }
        _c.fireRenderEvent(SComponent.DONE_RENDERING);

    }
}
