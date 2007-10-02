package menupane;
/**
 * $Id$
 * (c) Copyright 2000 wingS development team.
 *
 * This file is part of the wingS demo (http://wings.mercatis.de).
 *
 * The wingS demo is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;
import org.wings.DynamicResource;
import org.wings.SConstants;
import org.wings.SContainer;
import org.wings.SDimension;
import org.wings.SForm;
import org.wings.SFrame;
import org.wings.SLabel;
import org.wings.SMenu;
import org.wings.SMenuItem;
import org.wings.io.Device;
import org.wings.session.SessionManager;
import org.wings.style.CSSStyleSheet;
import org.wings.style.SimpleAttributeSet;
import org.wings.style.Style;
import org.wings.style.StyleConstants;
import org.wings.style.StyleSheet;
import org.wingx.SMenuPane;


/**
 * TODO: documentation
 *
 * @author Holger Engels
 * @version $Revision$
 */
public class MenuPane
    implements SConstants
{
    SMenuPane mpane = null;
    
    String[] icons = {
            "File.gif",
            "FloppyDrive.gif",
            "HardDrive.gif",
            "Computer.gif"
        };
    
    public MenuPane() {
        SFrame frame = new SFrame("MenuPane Demo");
        SessionManager.
            getSession().
            getCGManager().
            getDefaults().
            put("MenuPaneCG", new org.wingx.plaf.xhtml.MenuPaneCG());

        SContainer root = frame.getContentPane();
        // root.add(new SLabel(getServletInfo()));
		
		root.add(new SLabel(getServletInfo()));


        SForm form = new SForm();
        
        System.out.println("Creating MenuPane");
        mpane = new SMenuPane();
        System.out.println("Ready creating MenuPane");
        
        SMenu menu = null;
        SMenuItem menuitem = null;
        
        for (int m = 0; m < 5; m++)
        {
            menu = new SMenu("Menu " + m);
            menu.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent ae) {
                    mpane.setMenuBackground(null);
                    mpane.setBackground(null);
                }
            });
            mpane.add(menu, new SLabel("Content " + m));
            for (int mi = 0; mi < 10; mi++)
            {
                menuitem = new SMenuItem("MenuItem " + m + "." + mi);
                /*
                if (m == 0 && mi < icons.length)
                    menuitem.setIcon(new ResourceImageIcon("org/wings/icons/"+icons[mi]));
                */
                menuitem.setActionCommand((1+m)+"."+mi);
                menuitem.addActionListener(new RandomColor());
                mpane.addMenuItem(menu, menuitem, new SLabel("Content " + m + "." + mi));
            }
            
        }
        
        //mpane.setPreferredSize(new SDimension(100, 100));
        // form.add(mpane);
        // root.add(form);
        System.out.println("Adding MenuPane...");
        root.add(mpane);
        frame.show();
    }

    public class RandomColor implements ActionListener
    {
        public void actionPerformed(ActionEvent ae)
        {
            int r = (int) (Math.random() * 255);
            int g = (int) (Math.random() * 255);
            int b = (int) (Math.random() * 255);
            mpane.setMenuBackground(new java.awt.Color(r,g,b));
            
            r = (int) (Math.random() * 255);
            g = (int) (Math.random() * 255);
            b = (int) (Math.random() * 255);
            mpane.setBackground(new java.awt.Color(r,g,b));
        }
    }

    /**
     * Servletinfo
     */
    public String getServletInfo() {
        return "MenuPane ($Revision$)";
    }
    
    
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * End:
 */
