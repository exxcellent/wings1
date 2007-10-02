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

package org.wingx.plaf.xhtml;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;

import java.util.logging.Logger;
import org.wings.DynamicResource;
import org.wings.ResourceImageIcon;
import org.wings.SButton;
import org.wings.SCardLayout;
import org.wings.SComponent;
import org.wings.SDimension;
import org.wings.SFrame;
import org.wings.SIcon;
import org.wings.SMenu;
import org.wings.SMenuItem;
import org.wings.header.Link;
import org.wings.io.Device;
import org.wings.plaf.ComponentCG;
import org.wings.plaf.xhtml.css1.Utils;
import org.wings.style.AttributeSet;
import org.wings.style.CSSStyleSheet;
import org.wings.style.SimpleAttributeSet;
import org.wings.style.Style;
import org.wings.style.StyleConstants;
import org.wings.style.StyleSheet;
import org.wingx.SMenuPane;


/**
  * CG for {@link org.wingx.SMenuPane}
  * @author <a href="mailto:andre@lison.de">Andre Lison</a>
  * @version $Revision$
  */
public class MenuPaneCG implements ComponentCG
{

	private Logger logger = Logger.getLogger("org.wingx.plaf");

    protected final static String propertyPrefix = "MenuPane";
    
    private static Font fMenuFont = null;

    private Graphics fGraphics = null;

	private String fMenuColor = null;
	
	private MenuPaneStyleSheet fStyleSheet = null;

    public MenuPaneCG()
    {
    }

    public void installCG(SComponent component) { }
    public void uninstallCG(SComponent component) { }

    protected static final SIcon fBlindImage = 
        new ResourceImageIcon("org/wings/icons/transdot.gif"); // blind.gif

    protected void writeHorizontalFrame(Device d)
    	throws IOException
    {
        for (int i=0; i<5; i++)
        {
            d.print("<td height=\"3\" ");
            if (i%2!=1)
            	d.print(" width=\"3\" ");
			d.print("class=\"");
			d.print(fStyleSheet.getOuterBorderStyle().getName());
			d.print("\">");
            org.wings.plaf.xhtml.Utils.printBlindIcon(d, fBlindImage, 3, 3);
			d.print("</td>");
        }
    }
    
    protected void writeMenu(Device d, SMenu menu)
    	throws IOException
    {
        d.print("    <tr>\n");
        d.print("     <td class=\"");
        d.print(fStyleSheet.getMenuStyle().getName());
        d.print("\" nowrap>");
        menu.write(d);
        d.print("</td>\n");
        d.print("    </tr>\n");
    }

    
    public void write(Device d, SComponent c)
        throws IOException
    {

        SMenuPane pane = (SMenuPane) c;

        int i = 0;
        int maxSubMenuCount = 0;
        int menuCount = pane.getMenuCount();
        MenuButtonCG menuButtonCG = new MenuButtonCG();
		
        // get max count of submenu items in each menu
        // and test for activated menues
        for (i=0; i<menuCount;i++) {
            SMenu menu = pane.getMenu(i);
        	maxSubMenuCount = Math.max(
                maxSubMenuCount,
                menuCount-i+menu.getMenuComponentCount());
        }
		// System.out.println("maxSubMenuCount="+maxSubMenuCount);
		
		// now getting or trying to guess the menu width
		int menuWidth = pane.getMenuWidth();
		if (pane.getMenuWidth() == -1) // nothin' set -> calculate it
		{
		    if (fMenuFont == null) {
			    fMenuFont = new Font("SansSerif", Font.PLAIN, 12);
			    // fMenuFont = fMenuFont.deriveFont();
		    	// System.out.println("Font:"+fMenuFont);
		    }
		    if (fGraphics == null) {
		    	GraphicsEnvironment ge = 
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
			    /*
			    String[] ffamilies = ge.getAvailableFontFamilyNames();
			    for (int s=0;s<ffamilies.length;s++)
			    	System.out.println("Font : " + ffamilies[s]);
			     */
		    	fGraphics = ge.createGraphics(
                    new BufferedImage(1,1,BufferedImage.TYPE_USHORT_GRAY));
		    }
		    FontMetrics metrics = fGraphics.getFontMetrics(fMenuFont);
		    for (i=0; i<menuCount; i++) {
		        SMenu menu = pane.getMenu(i);
		    	menuWidth = Math.max(metrics.stringWidth(menu.getText()), menuWidth);
		    	SIcon icn = menu.getIcon();
		    	if (icn != null) {
		    	    menuWidth = Math.max(icn.getIconWidth(), menuWidth);
		    	}
		    	for (int mi=0;mi<menu.getMenuComponentCount(); mi++) {
		    	    SMenuItem item = (SMenuItem) menu.getMenuComponent(mi);
		    	    icn = item.getIcon();
			    	if (icn != null) {
			    	    menuWidth = Math.max(icn.getIconWidth(), menuWidth);
			    	}
			    	if (item.getText() == null) continue;
		    	    menuWidth = Math.max(
		    	    	metrics.stringWidth(item.getText()),menuWidth);
		    	}
		    }
		    menuWidth+=20; // add 10px space left and right
		}			

        d.print("<!-- Menu Pane -->\n");
		d.print("<table border=\"1\" cellpadding=\"0\" cellspacing=\"0\" class=\"")
			.print(fStyleSheet.getMenuStyle().getName())
			.print("\" ");
		if (c.getPreferredSize() != null)
        {
            d.print("width=\"").print(c.getPreferredSize().getIntWidth()).print("\" ");
            d.print("height=\"").print(c.getPreferredSize().getIntHeight()).print("\"");
        }
		d.print(">");
		d.print("<tr>");
		writeHorizontalFrame(d);
		d.print("</tr><tr>\n");
	    d.print("  <td rowspan=\"2\" width=\"3\" class=\"").
	    	print(fStyleSheet.getOuterBorderStyle().getName()).
	    	print("\">");
	    org.wings.plaf.xhtml.Utils.printBlindIcon(d, fBlindImage, 3, 3);
	    d.print("</td>\n");
	    d.print("<td valign=\"top\" class=\"").print(fStyleSheet.getInnerBorderTopStyle().getName()).print("\">");
        d.print("<table border=\"1\" height=\"1%\" cellspacing=\"0\" cellpadding=\"3\" width=\"");
        d.print(menuWidth);
        // small trick for konqueror(set and delete border to enable 
        // cell-border drawing on table with no border)
        d.print("\" border=1 style=\"border: none;\">\n");
        // write menus
        SMenu menu = null;
        int m = 0;
        for (;m<pane.getMenuCount(); m++) {
            menu = pane.getMenu(m);
			menu.setCG(menuButtonCG);
			menu.setStyle(fStyleSheet.getMenuLinkStyle().getName());
            writeMenu(d, menu);
            if (menu.isPopupMenuVisible())
            	break;
        }
        int subMenuCount = menu.getMenuComponentCount();
        /* write maxSubMenuCount - 1 rows, if last menu is opened */
        int absRows = pane.getMenuCount() - 1 == m?maxSubMenuCount-1:maxSubMenuCount;
        for (i=0;i<absRows;i++) {
            d.print("     <tr>\n");
            d.print("      <td class=\"")
            	.print(fStyleSheet.getMenuItemBgStyle().getName())
            	.print("\">");
            if (i<subMenuCount) {
                SMenuItem submenu = (SMenuItem) menu.getMenuComponent(i);
                submenu.setCG(menuButtonCG);
                submenu.setStyle(fStyleSheet.getMenuItemStyle().getName());
            	submenu.write(d);
            }
            else {
                d.print("&nbsp;");
            }
            d.print("</td>\n");
            d.print("     </tr>\n");
        }
		d.print("   </table>");
		d.print("</td>\n");
		d.print("  <td rowspan=\"2\" width=\"3\" class=\"")
			.print(fStyleSheet.getOuterBorderStyle().getName())
			.print("\">");
		org.wings.plaf.xhtml.Utils.printBlindIcon(d, fBlindImage, 3, 3);
		d.print("</td>\n");
		
        /* -- here goes the content -- */
        d.print("  <td rowspan=\"2\" bgcolor=\"#FFFFFF\" class=\"")
        	.print(fStyleSheet.getContentInnerBorderStyle().getName())
        	.print("\" ");
        org.wings.plaf.xhtml.Utils.printTableCellAlignment(
            d,
            ((SCardLayout) pane.getLayout()).getVisibleComponent());
        d.print(">\n");

        // save dimension and color of panel and remove it then
        SDimension dim = pane.getPreferredSize();
        Color color = pane.getBackground();
        pane.setPreferredSize(null);
        pane.setBackground(java.awt.Color.white);
        // write content panel
        org.wings.plaf.xhtml.Utils.writeContainerContents(d, pane);
        
        /* -- content end -- */
        
        // restore old dimension and color
        pane.setPreferredSize(dim);
        pane.setBackground(color);
        
        d.print("  </td>\n");
        d.print("  <td rowspan=\"2\" width=\"3\" class=\"")
			.print(fStyleSheet.getOuterBorderStyle().getName())
			.print("\">");
        org.wings.plaf.xhtml.Utils.printBlindIcon(d, fBlindImage, 3, 3);
        d.print("</td>\n");
		d.print(" </tr><tr>\n");
		d.print("<td valign=\"bottom\" width=\"1\" height=\"1\" class=\"")
			.print(fStyleSheet.getInnerBorderBottomStyle().getName())
			.print("\">\n");
		m++;
		// continue menus
		if (m<pane.getMenuCount()) {
		    d.print("   <table width=\"");
		    d.print(menuWidth);
		    d.print("\" cellspacing=\"0\" cellpadding=\"3\"");
	        // small trick for konqueror(set and delete border to enable 
	        // cell-border drawing on table with no border)
	        d.print(" border=\"1\" style=\"border: none;\">\n");
	        for (;m<pane.getMenuCount(); m++) {
	            menu = pane.getMenu(m);
	            menu.setCG(menuButtonCG);
				menu.setStyle(fStyleSheet.getMenuLinkStyle().getName());
	            writeMenu(d, menu);
	        }
			d.print("   </table>\n");
		}
		else
			d.print("&nbsp;");
		d.print("  </td>\n");
		d.print(" </tr><tr>");
		writeHorizontalFrame(d);
		d.print("</tr>\n");
		d.print("</table>");
        d.print("\n<!-- Menu Pane END -->");
    }

	protected class MenuButtonCG
		extends org.wings.plaf.css1.ButtonCG
	{
	    public MenuButtonCG()
	    {
	        super();
	    }
	    
	    public void write(Device d, SComponent c)
	        throws IOException
	    {
	        SButton button = (SButton)c;
	        SIcon icon = button.getIcon();
            
	        if (icon != null) {
	            writeFormIcon(d, button, null);
	            d.print("<br>");
	        }

			writeAnchorText(d, button, button.getStyle());
	    }

	    protected void writeAnchorPrefix(Device d, SButton button)
	        throws IOException
	    {
	        String tooltip = button.getToolTipText();
            d.print("<a href=\"");
            writeAnchorAddress(d, button);
            d.print("\" class=\"");
            d.print(button.getStyle());
            d.print("\" ");

            if (tooltip != null)
                d.print(" title=\"").print(tooltip).print("\"");

            d.print(">");
	    }
	}

	public void installStyleSheet(SMenuPane aMenuPane)
	{
        fStyleSheet = new MenuPaneStyleSheet(aMenuPane);
	}

	public DynamicResource getStyleSheet()
	{
	    return fStyleSheet;
	}

    /**
     * Generate dynamic stylesheet for menupane
     */
    public class MenuPaneStyleSheet extends DynamicResource
    {

        private StyleSheet fStyleSheet = null;

        private Style fMenuItemStyle = null;
        private Style fOuterBorderStyle = null;
        private Style fMenuLinkStyle = null;
        private Style fMenuItemBgStyle = null;
        private Style fMenuStyle = null;
        private Style fInnerBorderTopStyle = null;
        private Style fInnerBorderBottomStyle = null;
        private Style fContentInnerBorderStyle = null;
        

        private SMenuPane fMenuPane = null;

        public MenuPaneStyleSheet(SMenuPane pane)
        {
            super(pane.getParentFrame(), "css", "text/css");
            fMenuPane = pane;
            SFrame frame = pane.getParentFrame();
            this.fStyleSheet = new CSSStyleSheet();

            SimpleAttributeSet attrset = null;
            
            /* menuitem background */
            attrset = new SimpleAttributeSet();
            attrset.put(StyleConstants.BACKGROUND_COLOR, Utils.toColorString(fMenuPane.getMenuBackground()));
            attrset.put(StyleConstants.BORDER, "none");
            attrset.put(StyleConstants.TEXT_ALIGN, "center");
            fMenuItemBgStyle = new Style(".mp_" + fMenuPane.getComponentId() + "_mibg", attrset);
            this.fStyleSheet.putStyle(fMenuItemBgStyle);

            /* menuitems */
            attrset = new SimpleAttributeSet();
            attrset.put(StyleConstants.FONT_FAMILY, "Verdana,Arial,Helvetica");
            attrset.put(StyleConstants.FONT_SIZE, "11px");
            attrset.put(StyleConstants.FONT_WEIGHT, "normal");
            attrset.put(
                StyleConstants.COLOR,
                "#" + Utils.toColorString(
                	SMenuPane.getContrastColor(fMenuPane.getMenuBackground())));
            fMenuItemStyle = new Style("a.mp_" + fMenuPane.getComponentId() + "_mi", attrset);
            this.fStyleSheet.putStyle(fMenuItemStyle);

            /* menues (default equals menuitem style) */
            fMenuLinkStyle = new Style(
                "a.mp_" + fMenuPane.getComponentId() + "_m",
                (AttributeSet) attrset.clone());
            this.fStyleSheet.putStyle(fMenuLinkStyle);

			/* menu border style */
			attrset = new SimpleAttributeSet();
			attrset.put(StyleConstants.BORDER, "1px solid");
			attrset.put(StyleConstants.BORDER_COLOR, "#ffffff #6b6b6b #6b6b6b #ffffff");
			attrset.put(StyleConstants.TEXT_ALIGN, "center");
			attrset.put(StyleConstants.BACKGROUND_COLOR, "#"+Utils.toColorString(fMenuPane.getBackground()));
			fMenuStyle = new Style(".mp_" + fMenuPane.getComponentId() + "_mbrd", attrset);
			this.fStyleSheet.putStyle(fMenuStyle);
			
			/* menuitems top cell style */
			attrset = new SimpleAttributeSet();
			attrset.put(StyleConstants.BORDER_STYLE, "solid");
			attrset.put(StyleConstants.BORDER_TOP_WIDTH, "1px");
			attrset.put(StyleConstants.BORDER_LEFT_WIDTH, "1px");
			attrset.put(StyleConstants.BORDER_RIGHT_WIDTH, "1px");
			attrset.put(StyleConstants.BORDER_BOTTOM_WIDTH, "0px");
			fInnerBorderTopStyle = new Style(".mp_" + fMenuPane.getComponentId() + "_ibrdt", attrset);
			this.fStyleSheet.putStyle(fInnerBorderTopStyle);
			
			/* menuitems bottom cell style */
			attrset = (SimpleAttributeSet) attrset.clone();
			attrset.put(StyleConstants.BORDER_TOP_WIDTH, "0px");
			attrset.put(StyleConstants.BORDER_BOTTOM_WIDTH, "1px");
			fInnerBorderBottomStyle = new Style(".mp_" + fMenuPane.getComponentId() + "_ibrdb", attrset);
			this.fStyleSheet.putStyle(fInnerBorderBottomStyle);

			/* content inner border */
			attrset = (SimpleAttributeSet) attrset.clone();
			attrset.put(StyleConstants.BORDER_TOP_WIDTH, "1px");
			fContentInnerBorderStyle = new Style(".mp_" + fMenuPane.getComponentId() + "_cibrd", attrset);
			this.fStyleSheet.putStyle(fContentInnerBorderStyle);

			
			/* outer border */
            attrset = new SimpleAttributeSet();
            attrset.put(StyleConstants.BORDER, "none");
            attrset.put(StyleConstants.HEIGHT, "3px");
            fOuterBorderStyle = new Style(
            	".mp_" + fMenuPane.getComponentId() + "_ob", attrset);
            this.fStyleSheet.putStyle(fOuterBorderStyle);

            frame.addDynamicResource(this);
            frame.addHeader(new Link("stylesheet", null, "text/css", null, this));
        }        
        
		/**
         * @see Renderable#write(Device)
         */
        public void write(Device d) throws IOException
        {
	        SimpleAttributeSet attrset = null;

			/* menuitem bg */
	        fMenuItemBgStyle.put(StyleConstants.BACKGROUND_COLOR, "#"+Utils.toColorString(
				fMenuPane.getMenuBackground()));

	        /* menuitem */
	        fMenuItemStyle.put(StyleConstants.COLOR, "#"+Utils.toColorString(
				SMenuPane.getContrastColor(fMenuPane.getMenuBackground())));
	        // System.out.println("MenuItemStyle is = " + fMenuItemStyle);

			/* menues */
	        fMenuLinkStyle.put(StyleConstants.COLOR, "#"+Utils.toColorString(
				SMenuPane.getContrastColor(fMenuPane.getBackground())));

			/* menu border style */
		    String brdColorDark = "#"+Utils.toColorString(
	            fMenuPane.getBackground().darker().darker());
	        
		    String brdColorLight = "#"+Utils.toColorString(
	            SMenuPane.getBrighterColor(fMenuPane.getBackground()));
			fMenuStyle.put(StyleConstants.BORDER_COLOR, brdColorLight +" "+brdColorDark + " "+brdColorDark+" "+brdColorLight);
			fMenuStyle.put(StyleConstants.BACKGROUND_COLOR, "#"+Utils.toColorString(fMenuPane.getBackground()));

			/* menu inner borders  */
			String innerBorder = brdColorDark +" "+brdColorLight + " "+brdColorLight+" "+brdColorDark;
			fInnerBorderTopStyle.put(StyleConstants.BORDER_COLOR, innerBorder);
			fInnerBorderBottomStyle.put(StyleConstants.BORDER_COLOR, innerBorder);
			fContentInnerBorderStyle.put(StyleConstants.BORDER_COLOR, innerBorder);
			fInnerBorderTopStyle.put(StyleConstants.BACKGROUND_COLOR, "#"+Utils.toColorString(fMenuPane.getMenuBackground()));
			fInnerBorderBottomStyle.put(StyleConstants.BACKGROUND_COLOR, "#"+Utils.toColorString(fMenuPane.getMenuBackground()));

			/* outer border */
			fOuterBorderStyle.put(StyleConstants.BACKGROUND_COLOR, "#"+Utils.toColorString(fMenuPane.getBackground()));

	        // System.out.println("OuterBorderStyle is = " + fOuterBorderStyle);

            fStyleSheet.write(d);
        }
		
		/**
         * Gets the menuItemStyle.
         * @return Returns a Style
         */
        public Style getMenuItemStyle()
        {
            return fMenuItemStyle;
        }

        /**
         * Sets the menuItemStyle.
         * @param menuItemStyle The menuItemStyle to set
         */
        public void setMenuItemStyle(Style menuItemStyle)
        {
            fMenuItemStyle = menuItemStyle;
        }

		/**
         * Gets the outerBorderStyle.
         * @return Returns a Style
         */
        public Style getOuterBorderStyle()
        {
            return fOuterBorderStyle;
        }

        /**
         * Sets the outerBorderStyle.
         * @param outerBorderStyle The outerBorderStyle to set
         */
        public void setOuterBorderStyle(Style outerBorderStyle)
        {
            fOuterBorderStyle = outerBorderStyle;
        }
		
		/**
         * Gets the menuStyle.
         * @return Returns a Style
         */
        public Style getMenuLinkStyle()
        {
            return fMenuLinkStyle;
        }

        /**
         * Sets the menuStyle.
         * @param menuStyle The menuStyle to set
         */
        public void setMenuLinkStyle(Style menuStyle)
        {
            fMenuStyle = menuStyle;
        }

        /**
         * Gets the menuItemBg.
         * @return Returns a Style
         */
        public Style getMenuItemBgStyle()
        {
            return fMenuItemBgStyle;
        }

        /**
         * Sets the menuItemBg.
         * @param menuItemBg The menuItemBg to set
         */
        public void setMenuItemBgStyle(Style menuItemBg)
        {
            fMenuItemBgStyle = menuItemBg;
        }

        /**
         * Gets the menuStyle.
         * @return Returns a Style
         */
        public Style getMenuStyle()
        {
            return fMenuStyle;
        }

        /**
         * Sets the menuStyle.
         * @param menuStyle The menuStyle to set
         */
        public void setMenuStyle(Style menuStyle)
        {
            fMenuStyle = menuStyle;
        }

        /**
         * Gets the innerBorderBottomStyle.
         * @return Returns a Style
         */
        public Style getInnerBorderBottomStyle()
        {
            return fInnerBorderBottomStyle;
        }

        /**
         * Sets the innerBorderBottomStyle.
         * @param innerBorderBottomStyle The innerBorderBottomStyle to set
         */
        public void setInnerBorderBottomStyle(Style innerBorderBottomStyle)
        {
            fInnerBorderBottomStyle = innerBorderBottomStyle;
        }

        /**
         * Gets the innerBorderTopStyle.
         * @return Returns a Style
         */
        public Style getInnerBorderTopStyle()
        {
            return fInnerBorderTopStyle;
        }

        /**
         * Sets the innerBorderTopStyle.
         * @param innerBorderTopStyle The innerBorderTopStyle to set
         */
        public void setInnerBorderTopStyle(Style innerBorderTopStyle)
        {
            fInnerBorderTopStyle = innerBorderTopStyle;
        }

		/**
         * Gets the contentInnerBorderStyle.
         * @return Returns a Style
         */
        public Style getContentInnerBorderStyle()
        {
            return fContentInnerBorderStyle;
        }

        /**
         * Sets the contentInnerBorderStyle.
         * @param contentInnerBorderStyle The contentInnerBorderStyle to set
         */
        public void setContentInnerBorderStyle(Style contentInnerBorderStyle)
        {
            fContentInnerBorderStyle = contentInnerBorderStyle;
        }

	}


}

