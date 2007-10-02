/*
 * $Id$
 * (c) Copyright 2000 wingS development team.
 *
 * This file is part of wingS (http://wings.mercatis.de).
 *
 * wingS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */

package org.wingx;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.wings.io.Device;

import org.wings.*;
import org.wings.plaf.ComponentCG;
import org.wings.plaf.xhtml.Utils;
import org.wings.style.AttributeSet;
import org.wings.style.SimpleAttributeSet;

/**
 * This is an Panel with additional Menu on the left side.
 * <br><br>
 * <table border="1">
 * <tr>
 *   <td>
 *     <table width="100%" border="0">
 *       <tr> 
 *         <td nowrap bgcolor="#CCCCCC"><font face="Courier New, Courier, mono">SMenu 
 *           1</font></td>
 *       </tr>
 *       <tr> 
 *         <td nowrap bgcolor="#666666"><font color="#FFFFFF" face="Courier New, Courier, mono">SMenuItem 
 *           1.1</font></td>
 *       </tr>
 *       <tr> 
 *         <td nowrap bgcolor="#666666"><font color="#FFFFFF" face="Courier New, Courier, mono">SMenuItem 
 *           1.2</font></td>
 *       </tr>
 *       <tr> 
 *         <td nowrap bgcolor="#666666"><font color="#FFFFFF" face="Courier New, Courier, mono">SMenuItem 
 *           1.3</font></td>
 *       </tr>
 *       <tr> 
 *         <td nowrap bgcolor="#666666"><font face="Courier New, Courier, mono"></font>&nbsp;</td>
 *       </tr>
 *       <tr> 
 *         <td nowrap bgcolor="#CCCCCC"><font face="Courier New, Courier, mono">SMenu 
 *           2</font></td>
 *       </tr>
 *       <tr> 
 *         <td nowrap bgcolor="#CCCCCC"><font face="Courier New, Courier, mono">SMenu 
 *           3</font></td>
 *       </tr>
 *     </table>
 *   </td>
 *   <td nowrap align="center" width="200"><font face="Courier New, Courier, mono">content 
 *     panel<br>
 *     (SCardLayout)</font></td>
 *  </tr>
 * </table>
 * <br><br>
 * The content panel is a panel with {@link SCardLayout}. When user selects a menu or menuitem on
 * left side, the content is switched.
 * Example:
 * <blockquote><pre>
 *    SMenuPane mpane = new SMenuPane();
 *    SMenu m = mpane.add(new SMenu("SMenu 1"), new SLabel("Menu Content 1"));
 *    mpane.add(new SMenu("SMenu 2"), new SLabel("Menu Content 2"));
 *    mpane.add(new SMenu("SMenu 3"), new SLabel("Menu Content 3"));
 *       
 *    mpane.addMenuItem(m, new SMenuItem("SMenuItem 1.1"), new SLabel("MenuItem Content 1.1"));
 *    mpane.addMenuItem(m, new SMenuItem("SMenuItem 1.2"), new SLabel("MenuItem Content 1.2"));
 *    mpane.addMenuItem(m, new SMenuItem("SMenuItem 1.3"), new SLabel("MenuItem Content 1.3"));
 * </pre></blockquote>
 * @see STabbedPane
 * @author <a href="mailto:andre.lison@general-bytes.com">Andre Lison</a>, <a href="http://www.general-bytes.com">GENERAL BYTES</a>
 * @version $Revision$
 */
public class SMenuPane extends SContainer implements ActionListener, PropertyChangeListener
{

    private static final String cgClassID = "MenuPaneCG";

    /**
     * The list of menues
     */
    private final List fMenus = new ArrayList();

    /**
     * The with of the left menu panel in pixel.
     */
    protected int fMenuWidth = -1;

    /**
     * The menu background
     */
    protected Color fMenuBackground = new Color(153,153,153);

    /**
     * For registered change listeners.
     */
    protected final EventListenerList listenerList = new EventListenerList();

    /**
     * The current selected component.
     */
    protected SComponent fSelectedMenu = null;
    
    private Logger fLogger = Logger.getLogger("org.wingx.SMenuPane");
    
    /**
     * Constructor for SMenuPane.
     */
    public SMenuPane()
    {
        super(new SCardLayout());
        setBackground(null);
        this.addPropertyChangeListener(this);
    }

    /**
     * Append the specific menu to the end of
     * the navigation pane.
     * @param menu Add this menu
     * @param comp show this component, when menu is 
     *   selected. If comp is null, no action is performed.
     * @return the added menu
     */
    public SMenu add(SMenu menu, SComponent comp)
    {
        if (!(menu instanceof SMenu))
            throw new IllegalArgumentException(
                "Added component is not of type SMenu! It is " + menu.getClass());

        fMenus.add(menu);
        if (comp != null)
        {
            if (comp.getName() != null)
                menu.setName(comp.getName());
            else
                menu.setName(comp.getComponentId());
            this.add(comp, menu.getName());
            menu.addActionListener(this);
        }
        if (menu.isPopupMenuVisible())
            fSelectedMenu = menu;
        return menu;
    }

    /**
     * Add a menuItem to given Menu in MenuPane.
     * @param menu add item to this menu. If <tt>menu</tt> doesn't exist,
     *   it is added to menuPane.
     * @param item add this item
     * @param comp display this component, when item is selected. If comp is <tt>null</tt>
     *   no action is performed
     * @return the added menu item
     */
    public SMenuItem addMenuItem(SMenu menu, SMenuItem item, SComponent comp)
    {
        if (!(item instanceof SMenuItem))
            throw new IllegalArgumentException(
                "Added component is not of type SMenuItem! It is " + menu.getClass());
        menu.add(item);
        if (comp != null)
        {
            if (comp.getName() != null)
                item.setName(comp.getName());
            else
                item.setName(comp.getComponentId());
            this.add(comp, item.getName());
            item.addActionListener(this);
        }
        if (!fMenus.contains(menu))
            add(menu, comp);

        return item;
    }

    /**
     * Set given menu active and switch content
     * panel. Fires ChangeEvent, if new selection differes
     * from old one.
     */
    public void setSelectedMenu(SComponent comp)
    {
        /*
        if (fSelectedMenu == comp)
            return;
        */
        if (!(comp instanceof SMenu) && !(comp instanceof SMenuItem))
            throw new IllegalArgumentException(
                "Component is not of type SMenu or SMenuItem! It is " + comp.getClass());
        if (comp instanceof SMenu)
        {
            if (fSelectedMenu != null && fSelectedMenu instanceof SMenu)
                 ((SMenu) fSelectedMenu).setPopupMenuVisible(false);
            ((SMenu) comp).setPopupMenuVisible(true);
            fSelectedMenu = comp;
        }
        else
        {
            fSelectedMenu = null;
            // search for menuitems in all menues
            for (int m = 0; m < getMenuCount(); m++)
            {
                SMenu menu = getMenu(m);
                if (menu.isPopupMenuVisible())
                    menu.setPopupMenuVisible(false);
                for (int mi = 0;
                    fSelectedMenu == null && mi < menu.getMenuComponentCount();
                    mi++)
                    if (menu.getMenuComponent(mi).equals(comp))
                    {
                        menu.setPopupMenuVisible(true);
                        fSelectedMenu = menu.getMenuComponent(mi);
                        break;
                    }
            }
        }
        if (comp.getName() == null)
            comp.setName(comp.getComponentId());
        ((SCardLayout) this.getLayout()).show(comp.getName());
        fireStateChanged();
    }

    /**
      * Add this menu to the menuPane. No action is performed,
      * when menu is selected.
      * @param menu add this menu
      * @return the added menu
      */
    public SMenu add(SMenu menu)
    {
        return add(menu, null);
    }

    /**
     * Get the {@link SMenu} at given index.
     * @param index the index
     */
    public SMenu getMenu(int index)
    {
        return (SMenu) fMenus.get(index);
    }

    /**
     * Get count of menues in this menuPane.
     * @return the absolute menu count
     */
    public int getMenuCount()
    {
        return fMenus.size();
    }

    /**
     * Returns the name of the CGFactory class that generates the
     * look and feel for this component.
     *
     * @return "MenuPaneCG"
     * @see SComponent#getCGClassID
     * @see CGDefaults#getCG
     */
    public String getCGClassID()
    {
        return cgClassID;
    }

    /**
     * Is called by internal action, when menu or menuItem
     * is selected. If component was given during adding menu, show this
     * component in content panel.
     * @see ActionListener#actionPerformed(ActionEvent)
     * @see #add(SMenu)
     * @see #addMenu(SMenu, SComponent)
     * @see #addItem(SMenu, SMenuItem, SComponent)
     */
    public void actionPerformed(ActionEvent ae)
    {
        SAbstractButton button = (SAbstractButton) ae.getSource();
        if (button instanceof SMenu)
        {
            for (int i = 0; i < fMenus.size(); i++) {
                if (((SMenu) fMenus.get(i)).isPopupMenuVisible())
                     ((SMenu) fMenus.get(i)).setPopupMenuVisible(false);
            }
            ((SMenu) ae.getSource()).setPopupMenuVisible(true);
        }

        setSelectedMenu(button);
    }

    /**
     * Set the parent frame of this menuPane.
     * @param f the parent frame.
     */
    public void setParentFrame(SFrame f)
    {
        fLogger.entering("SMenuPane.setParentFrame", f==null?"null":f.getTitle());
        super.setParentFrame(f);
        for (int i = 0; i < fMenus.size(); i++)
             ((SMenu) fMenus.get(i)).setParentFrame(f);
		ComponentCG cg = this.getCG();
		fLogger.log(Level.FINER, "cg is " + cg);
		if (f != null && cg instanceof org.wingx.plaf.xhtml.MenuPaneCG)
		{
		    fLogger.log(Level.FINEST, "SMenupane.setParentFrame, Installing stylesheet ...");
		    ((org.wingx.plaf.xhtml.MenuPaneCG) cg).installStyleSheet(this);
		}
    }

    /**
     * Get the width of the left menu panel.
     * @return the width in pixel, <tt>-1</tt> if not set.
     */
    public int getMenuWidth()
    {
        return fMenuWidth;
    }

    /**
     * Set the width of the left menu panel.
     * @param width the absolute inner width in pixel.
     */
    public void setMenuWidth(int width)
    {
        fMenuWidth = width;
    }

    /**
     * Set the background for the opened menu panel.
     * @param color set this color.
     */
    public void setMenuBackground(Color color)
    {
        Color oldColor = fMenuBackground;
        if (color == null)
            fMenuBackground = new Color(153,153,153);
        else
            fMenuBackground = color;
		firePropertyChange("MenuBackground", oldColor, fMenuBackground);
    }

    /**
     * Get the background for the opened menu panel.
     * @return the background color
     */
    public Color getMenuBackground()
    {
        return fMenuBackground;
    }

    /**
     * Set the background color for menues and menuItems
     * and the border.
     * @param color the color.
     */
    public void setBackground(Color color)
    {
        Color oldBg = getBackground();
        if (color == null)
            color = new Color(221,221,221);
        super.setBackground(color);
        firePropertyChange("Background", oldBg, color);
    }

    /**
     * Returns the currently selected menu or menuitem for this menupane. Returns null if there is no currently selected menu.
     * @return the selected menu or menuitem
     */
    public SComponent getSelectedMenu()
    {
        return fSelectedMenu;
    }

	/**
      * Returns the currently selected component for this tabbedpane. Returns null if there is no currently selected tab.
      * @return the component corresponding to the selected menu or menuitem
      */
	public SComponent getSelectedComponent()
	{
	    return ((SCardLayout) this.getLayout()).getVisibleComponent();
	}

    /**
     * Get the index of the selected menu (not menu item !).
     * @return the index or -1 if no component is selected
     */
    public int getSelectedIndex()
    {
        if (fSelectedMenu != null)
        {
            if (fSelectedMenu instanceof SMenu)
                return fMenus.indexOf(fSelectedMenu);
            else
            if (fSelectedMenu instanceof SMenuItem)
            {
                for (int m = 0; m < getMenuCount(); m++)
                {
                    SMenu menu = getMenu(m);
                    for (int mi = 0; mi < menu.getMenuComponentCount(); mi++)
                    {
                        if (menu.getMenuComponent(mi).equals(fSelectedMenu))
                            return m;
                    }
                }
                return -1;
            }
        }
        return -1;
    }

    /**
     * Removes the menu at <i>index</i>.
     * @return the removed menu, <i>null</i> if not found.
     * @param index remove menu at this index
     */
    public SMenu removeMenuAt(int index)
    {
        return (SMenu) fMenus.remove(index);
    }

    /**
     * Add a change listener which is called, when
     * selection of Menu or MenuItems changes.
     */
    public void addChangeListener(ChangeListener cl)
    {
        listenerList.add(ChangeListener.class, cl);
    }

    /**
     * Remove given change listener from listener list.
     * @param cl remove this listener
     */
    public void removeChangeListener(ChangeListener cl)
    
    {
        listenerList.remove(ChangeListener.class, cl);
    }

    public void write(Device d) throws IOException
    {
        // first set one menu active, if not already set
        if (getSelectedIndex() == -1 && getMenuCount() > 0)
        {
            ((SMenu) getMenu(0)).setPopupMenuVisible(true);
            this.setSelectedMenu((SAbstractButton) getMenu(0));
        }
        if (visible)
            this.getCG().write(d, this);
    }

    /**
     * Notifies all ChangeListeners about changed menu.
     */
    protected void fireStateChanged()
    {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] != ChangeListener.class)
                continue;
            ((ChangeListener) listeners[i + 1]).stateChanged(new ChangeEvent(this));
        }
    }

   /**
	 * Get either black or white, whichever contrasts
	 * better to this color.
     * @param aColor get the contrast color to this color.
	 * @return {@link java.awt.Color#black} or {@link java.awt.Color#white)
	 */
    public static Color getContrastColor(Color aColor)
    {
		final double brightness =
				aColor.getRed()*0.2125 +
				aColor.getGreen()*0.7145 +
				aColor.getBlue()*0.0721;
		return brightness<128.0 ? Color.white : Color.black;
    }

	/**
	 * Creates a brighter version of this color.
	 * This method behaves different to {@link java.awt.Color#brighter} for
	 * already realy bright colors. If saturation is nearly 
	 * 100 it decreases saturation by 0.4 to get an brighter color.
     * @param aColor brighten this color
	 * @return a brighter color
	 */
    public static Color getBrighterColor(Color aColor)
    {
        final float[] hsb =
            aColor.RGBtoHSB(aColor.getRed(), aColor.getGreen(), aColor.getBlue(), null);
        // saturation and brightness are very high
        if (hsb[0] > 0.95f)
            return aColor.getHSBColor(hsb[0] - 0.4f, hsb[1], hsb[2]);
        else
            return aColor.brighter();
    }
    
    public AttributeSet getAttributes() {
        AttributeSet aset = new SimpleAttributeSet();
        
        Color clr = getBackground();
        
        String menuBackground = "#"+Utils.toColorString(
            getMenuBackground());

        String menuColor = "#"+Utils.toColorString(
            getBackground());
        
	    String brdColorDark = "#"+Utils.toColorString(
            clr.darker().darker());
        
	    String brdColorLight = "#"+Utils.toColorString(
            getBrighterColor(clr));
        
	    String brdColor = "#"+Utils.toColorString(clr);

	    String contrastMenuColor = "#"+Utils.toColorString(
            getContrastColor(getBackground()));

	    String contrastMenuItemColor = "#"+Utils.toColorString(
            getContrastColor(getMenuBackground()));
            
        aset.put("color", contrastMenuColor);
        return aset;
    }
    
    /**
     * Invalidate stylesheet when properties changed.
     * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent pev)
    {
        ComponentCG cg = this.getCG();
        if (cg != null && cg instanceof org.wingx.plaf.xhtml.MenuPaneCG)
		{
		    // System.out.println("SMemnupane.propertyChange("+pev.getPropertyName()+"), invalidating stylesheet");
		    if (((org.wingx.plaf.xhtml.MenuPaneCG) cg).getStyleSheet() != null)
		    	((org.wingx.plaf.xhtml.MenuPaneCG) cg).getStyleSheet().invalidate();
		}
    }

}
