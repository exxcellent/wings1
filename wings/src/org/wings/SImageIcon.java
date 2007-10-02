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
package org.wings;

import org.wings.externalizer.ImageExternalizer;
import org.wings.session.SessionManager;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;

import java.awt.image.IndexColorModel;
import java.awt.image.PixelGrabber;

import javax.swing.ImageIcon;

public class SImageIcon extends SAbstractIcon {

    private final ImageIcon img;
    private final SimpleURL url;

    public SImageIcon(ImageIcon image) {
        this.img = image;
        url = new SimpleURL(SessionManager.getSession()
                            .getExternalizeManager()
                            .externalize(image, determineMimeType(image.getImage())));

        setIconWidth(img.getIconWidth());
        setIconHeight(img.getIconHeight());
    }

    public SImageIcon(java.awt.Image image) {
        this(new ImageIcon(image));
    }

    public SImageIcon(String name) {
        this(new ImageIcon(name));
    }

    /**
     * returns the URL, the icon can be fetched from. This URL may
     * be relative, usually if generated from the externalizer.
     */
    public SimpleURL getURL() {
        return url;
    }

    // get the image e.g. if you want to grey it out
    public java.awt.Image getImage() {
        return img.getImage();
    }
    
    protected String determineMimeType(Image image) {
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            System.err.println("interrupted waiting for pixels!");
        }
		
        String mimeType = "image/";
        if (!(pg.getColorModel() instanceof IndexColorModel))
            mimeType += ImageExternalizer.FORMAT_PNG;
        else
            mimeType += ImageExternalizer.FORMAT_GIF;
		
        return mimeType;
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
