/*
 * @(#)BeanInfoFactory.java	1.4 01/01/04
 *
 * Copyright 1999 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package ide;

import java.awt.Image;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;

import java.util.WeakHashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * A wrapper for the Introspector to generate and return instances
 * of BeanInfos. Creating a BeanInfo with the Introspector is a 
 * really expensive operation. Since a BeanInfo is immutable for
 * each class, it makes sense to share instances of BeanInfo's
 * throughout the application.
 *
 * @version 1.4 01/04/01
 * @author  Mark Davidson
 */
public class BeanInfoFactory  {

    private static WeakHashMap infos = new WeakHashMap();
    
    /** 
     * Retrieves the BeanInfo for a Class
     */
    public static BeanInfo getBeanInfo(Class cls)  {
        BeanInfo beanInfo = (BeanInfo)infos.get(cls);
        
        if (beanInfo == null)  {
            try {
		// We use a custom introspector to get around a problem with
		// deploying Java Web Start. The caching works in 
		// the BeanIntrospector but it doesn't hurt to maintain the old architecture.
		beanInfo = BeanIntrospector.getBeanInfo(cls);
		//beanInfo = Introspector.getBeanInfo(cls);
                infos.put(cls, beanInfo);
            } catch (IntrospectionException ex) {
                // XXX - should handle this better.
                ex.printStackTrace();
            }
        }
        return beanInfo;
    }

    /** 
     * Retrieves the BeanInfo icon for the class.
     */
    public static Icon getIcon(Class cls)  {
	// Note: This should be a static method except that
	// the inner classes can't be static.
	Icon icon = null;
            
	if (cls == null)
	    return null;
               
	BeanInfo info = BeanInfoFactory.getBeanInfo(cls);
            
	if (info != null)  {
	    Image image = info.getIcon(BeanInfo.ICON_COLOR_16x16);
	    if (image != null)  {
		icon = new ImageIcon(image);
	    } else {
		return getIcon(cls.getSuperclass());
	    }
	}
	return icon;
    }

}
