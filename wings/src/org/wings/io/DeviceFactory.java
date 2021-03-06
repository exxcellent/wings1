package org.wings.io;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.externalizer.ExternalizedResource;
import org.wings.session.Session;
import org.wings.session.SessionManager;

public abstract class DeviceFactory
{
    private static Log logger = LogFactory.getLog("org.wings.io");

    private static String DEFAULT_DEVICE_FACTORY = "org.wings.io.DeviceFactory$Default";

    private static DeviceFactory factory;

    public static void setDeviceFactory(DeviceFactory factory) {
        DeviceFactory.factory = factory;
    }
    public static DeviceFactory getDeviceFactory() {
        if (factory == null) {
	    synchronized (DeviceFactory.class) {
		if (factory == null) {
		    String className = (String)SessionManager.getSession().getProperty("wings.device.factory");
		    if (className == null)
			className = DEFAULT_DEVICE_FACTORY;

		    try {
			Class factoryClass = null;
			try {
			    factoryClass = Class.forName(className, true,
							 Thread.currentThread()
							 .getContextClassLoader());
			}
			catch (ClassNotFoundException e) {
			    // fallback, in case the servlet container fails to set the
			    // context class loader.
			    factoryClass = Class.forName(className);
			}
			factory = (DeviceFactory)factoryClass.newInstance();
		    }
		    catch (Exception e) {
			logger.fatal( "could not load wings.device.factory: " +
				   className, e);
			throw new RuntimeException("could not load wings.device.factory: " +
				   className + "(" + e.getMessage() +")");
		    }
		}
	    }
        }
        return factory;
    }

    public static Device createDevice(ExternalizedResource externalizedResource)
	throws IOException
    {
        return getDeviceFactory().create(externalizedResource);
    }

    protected abstract Device create(ExternalizedResource externalizedResource) throws IOException;

    static class Default
	extends DeviceFactory
    {
	protected Device create(ExternalizedResource externalizedResource)
	    throws IOException
	{
        Session session = SessionManager.getSession();
        return new ServletDevice(session.getServletResponse().getOutputStream(), session.getCharacterEncoding());
	}
    }
}
