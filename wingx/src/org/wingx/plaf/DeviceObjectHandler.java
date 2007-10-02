/*
 * $Id$
 */

package org.wingx.plaf;

import java.io.*;

import org.wings.externalizer.*;
import org.wings.io.StringBufferDevice;

public class DeviceObjectHandler
    extends TextExternalizer
{

    String extension = null;
    String mimeType = null;

    public DeviceObjectHandler(String mimeType, String extension) {
	this.mimeType = mimeType;
	this.extension = extension;
    }
    public DeviceObjectHandler(String mimeType) {
	this.mimeType = mimeType;
	this.extension = "txt";
    }
    public DeviceObjectHandler() {
	this.mimeType = "text/plain";
	this.extension = "txt";
    }

    public void setExtension(String extension) {
	this.extension = extension;
    }
    public String getExtension(Object obj) {
	return extension;
    }

    public  void setMimeType(String mimeType) {
	this.mimeType = mimeType;
    }
    public String getMimeType(Object obj) {
	return mimeType;
    }

    public boolean isFinal(Object obj) {
	return false;
    }

    public void write(Object obj, java.io.OutputStream out)
	throws java.io.IOException
    {
	Reader reader = new StringReader(obj.toString());
	Writer writer = new OutputStreamWriter(out);
	char[] buffer = new char[2048];
	int num;
	while ((num = reader.read(buffer)) > 0) {
	    writer.write(buffer, 0, num);
	}
	reader.close();
	writer.close();
    }

    public Class getSupportedClass() {
	return StringBufferDevice.class;
    }
}
