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

package org.wings.externalizer;

import Acme.JPM.Encoders.GifEncoder;
import com.keypoint.PngEncoder;
import java.awt.Image;
import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.io.Device;
import org.wings.io.DeviceOutputStream;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @author <a href="mailto:mreinsch@to.com">Michael Reinsch</a>
 * @version $Revision$
 */
public class ImageExternalizer implements Externalizer {

    private final static Log logger = LogFactory.getLog("org.wings.externalizer");

    public static final String FORMAT_PNG = "png";
    public static final String FORMAT_GIF = "gif";

    private static final String[] SUPPORTED_FORMATS = {FORMAT_PNG, FORMAT_GIF};
    private static final Class[] SUPPORTED_CLASSES = {Image.class};

    public static final ImageExternalizer SHARED_GIF_INSTANCE = new ImageExternalizer(FORMAT_GIF);
    public static final ImageExternalizer SHARED_PNG_INSTANCE = new ImageExternalizer(FORMAT_PNG);

    protected String format;

    protected final String[] supportedMimeTypes = new String[1];

    public ImageExternalizer() {
        this(FORMAT_PNG);
    }

    public ImageExternalizer(String format) {
        this.format = format;
        checkFormat();

        supportedMimeTypes[0] = getMimeType(null);
    }

    protected void checkFormat() {
        for (int i = 0; i < SUPPORTED_FORMATS.length; i++) {
            if (SUPPORTED_FORMATS[i].equals(format))
                return;
        }
        throw new IllegalArgumentException("Unsupported Format " + format);
    }


    public String getId(Object obj) {
        return null;
    }

    public String getExtension(Object obj) {
        return format;
    }

    public String getMimeType(Object obj) {
        return "image/" + format;
    }

    public int getLength(Object obj) {
        return -1;
    }

    public boolean isFinal(Object obj) {
        return false;
    }

    public Class[] getSupportedClasses() {
        return SUPPORTED_CLASSES;
    }

    public String[] getSupportedMimeTypes() {
        return supportedMimeTypes;
    }

    public void write(Object obj, Device out)
            throws java.io.IOException {
        Image img = (Image) obj;
        if (FORMAT_PNG.equals(format))
            writePNG(img, out);
        else
            writeGIF(img, out);
    }

    /**
     * writes a image as gif to the OutputStream
     */
    public void writeGIF(Image img, Device out)
            throws java.io.IOException {
        GifEncoder encoder = new GifEncoder(img, new DeviceOutputStream(out),
                                            true);
        encoder.encode();
    }

    /**
     * writes a image as png to the OutputStream
     */
    public void writePNG(Image img, Device out)
            throws java.io.IOException {
        PngEncoder png = new PngEncoder(img, PngEncoder.ENCODE_ALPHA,
                                        PngEncoder.FILTER_NONE, 6);
        byte[] pngbytes = png.pngEncode();
        if (pngbytes == null) {
            logger.fatal("null image");
        } else {
            out.write(pngbytes);
        }
        out.flush();
    }

    public Collection getHeaders(Object obj) {
        return null;
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
