package org.wingx.beans;

import java.beans.*;
import org.wings.*;
import org.wings.io.*;

/**
 * This is the pendant to the java.beans.PropertyEditorManager.
 * <p>
 * For documentation refer to the JDK API Specification.
 */
public class SPropertyEditorManager
{
    public static void registerEditor(Class targetType, Class editorClass) {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    sm.checkPropertiesAccess();
	}
	initialize();
	if (editorClass == null) {
	    registry.remove(targetType);
	} else {
	    registry.put(targetType, editorClass);
	}
    }

    public static synchronized SPropertyEditor findEditor(Class targetType) {
	initialize();
	Class editorClass = (Class)registry.get(targetType);
	if (editorClass != null) {
	    try {
		Object o = editorClass.newInstance();
        	return (SPropertyEditor)o;
	    } catch (Exception ex) {
	 	System.err.println("Couldn't instantiate type editor \"" +
			editorClass.getName() + "\" : " + ex);
	    }
	}

	// Now try <className>Editor

	String editorName = targetType.getName() + "Editor";
	try {
	    return (SPropertyEditor)instantiate(targetType, editorName);
	} catch (Exception ex) {}

	// Now try looking for <searchPath>.fooEditor
	editorName = targetType.getName();
   	while (editorName.indexOf('.') > 0) {
	    editorName = editorName.substring(editorName.indexOf('.')+1);
	}
	for (int i = 0; i < searchPath.length; i++) {
	    String name = searchPath[i] + "." + editorName + "Editor";
	    try {
	        return (SPropertyEditor)instantiate(targetType, name);
	    } catch (Exception ex) {}
	}

	// No suitable Editor.
	return null;
    }

    public static synchronized String[] getEditorSearchPath() {
	String result[] = new String[searchPath.length];
	for (int i = 0; i < searchPath.length; i++) {
	    result[i] = searchPath[i];
	}
	return result;
    }

    public static synchronized void setEditorSearchPath(String path[]) {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    sm.checkPropertiesAccess();
	}
	if (path == null) {
	    path = new String[0];
	}
	searchPath = path;
    }

    private static synchronized void load(Class targetType, String name) {
	String editorName = name;
	for (int i = 0; i < searchPath.length; i++) {
	    try {
	        editorName = searchPath[i] + "." + name;
	        Class cls = Class.forName(editorName);
	        registry.put(targetType, cls);
		return;
	    } catch (Exception ex) {}
	}
	// This shouldn't happen.
	System.err.println("load of " + editorName + " failed");
    }


    private static synchronized void initialize() {
	if (registry != null) {
	    return;
	}
	registry = new java.util.Hashtable();
	load(Byte.TYPE, "ByteEditor");
	load(Short.TYPE, "ShortEditor");
	load(Integer.TYPE, "IntEditor");
	load(Long.TYPE ,"LongEditor");
	load(Boolean.TYPE, "BoolEditor");
	load(Float.TYPE, "FloatEditor");
	load(Double.TYPE, "DoubleEditor");
    }

    private static String[] searchPath = { "org.wingx.beans.editors" };
    private static java.util.Hashtable registry;


    static Object instantiate(Class sibling, String className)
		 throws InstantiationException, IllegalAccessException, ClassNotFoundException
    {
	// First check with sibling's classloader (if any). 
	ClassLoader cl = sibling.getClassLoader();
	if (cl != null) {
	    try {
	        Class cls = cl.loadClass(className);
		return cls.newInstance();
	    } catch (Exception ex) {
	        // Just drop through and try the system classloader.
	    }
        }
	// Now try the system classloader.
	try {
	    cl = ClassLoader.getSystemClassLoader();
	    if (cl != null) {
	        Class cls = cl.loadClass(className);
		return cls.newInstance();
	    }
        } catch (Exception ex) {}

	// Now try the bootstrap classloader.
	Class cls = Class.forName(className);
	return cls.newInstance();
    }
}
