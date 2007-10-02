/*
 * @(#)ToolTipManager.java	1.00 04/10/06
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


package org.wings;

import java.io.*;
import java.util.WeakHashMap;
import org.wings.script.JavaScriptListener;

/**
 * Manages all the <code>ToolTips</code> in the system.
 * <p>
 * Despite the original swing Method this version is only to keep Track of the Tooltips and
 * the javascript Manager. In the case of a more advanced javascript ToolTip Manager we can add the mssing functionality
 */
public class ToolTipManager {
  
  final static ToolTipManager sharedInstance = new ToolTipManager();
  private final WeakHashMap componentMap = new WeakHashMap();
       
    private static final JavaScriptListener TOOLTIP_SCRIPT_LOADER = new JavaScriptListener("", "", loadTTScript());

    private static String loadTTScript() {
        InputStream in = null;
        BufferedReader reader = null;

        try {
            in = SToolTip.class.getClassLoader().getResourceAsStream("org/wings/plaf/css1/ToolTip.js");
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
    
    
  ToolTipManager() {
  }
  

  /**
   * Returns a shared <code>ToolTipManager</code> instance.
   *
   * @return a shared <code>ToolTipManager</code> object
   */
  public static ToolTipManager sharedInstance() {
    return sharedInstance;
  }
  
  
 
  
  // add keylistener here to trigger tip for access
  /**
   * Registers a component for tooltip management.
   * <p>
   * This will put the component in a weak hashMap for rference
   *
   * @param component  a <code>SToolTip</code> object to add
   * @param stt a<code>SToolTip</code> object to add
   */
  
  public void registerComponent(SToolTip component) {
    if (!componentMap.containsKey(component.getComponent())) {
      componentMap.put(component.getComponent(), component);
    }
  }
  
  /**
   * Removes a component from tooltip control.
   *
   * @param component  a <code>SComponent</code> object to remove
   */
  public void unregisterComponent(SComponent component) {
    componentMap.remove(component);
  }
  
  /**
   * Lookup and Return the ToolTip objekt
   * @param c a <code>SCompent</code> as key to the ToolTip
   * @return SToolTip when Found else NULL
   */
  public SToolTip lookupComponent(SComponent c) {
     return (SToolTip)componentMap.get(c);
  }
  
  
  public void installListener(SFrame sf) {
    sf.removeScriptListener(TOOLTIP_SCRIPT_LOADER);
    if (!componentMap.isEmpty()) {
        sf.addScriptListener(TOOLTIP_SCRIPT_LOADER);
      }
    }
}
