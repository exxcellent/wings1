package org.wings.template;

import bsh.Interpreter;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.SComponent;
import org.wings.session.SessionManager;

/**
 * DefaultPropertyManager.java
 *
 *
 * Created: Tue Aug  6 16:43:03 2002
 *
 * @author (c) mercatis information systems gmbh, 1999-2002
 * @author <a href="mailto:armin.haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class DefaultPropertyManager implements PropertyManager {

    private static final Log logger = LogFactory.getLog("org.wings.template");
    static final Class[] classes = {SComponent.class};

    public final HashMap propertyValueConverters = new HashMap();

    public static final DefaultPropertyValueConverter
        DEFAULT_PROPERTY_VALUE_CONVERTER = DefaultPropertyValueConverter.INSTANCE;

    private boolean scriptEnabled = false;

    /**
     *
     */
    public DefaultPropertyManager() {

    }

    protected Interpreter createInterpreter() {
        return new Interpreter();
    }

    public void setProperty(SComponent comp, String name, String value) {
        if (scriptEnabled && "SCRIPT".equals(name)) {
            Interpreter interpreter = createInterpreter();

            try {
                logger.debug("eval script " + value);

                interpreter.set("component", comp);
                interpreter.set("session", SessionManager.getSession());

                interpreter.eval(value);
            } catch (Exception ex) {
                ex.printStackTrace();
                // ignore it, maybe log it
            } // end of try-catch

// reset interpreter

        } // end of if ()


        Method[] methods = comp.getClass().getMethods();

        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];

            if (method.getName().startsWith("set") &&
                name.equals(method.getName().substring(3).toUpperCase()) &&
                method.getParameterTypes().length == 1) {

                Class paramType = method.getParameterTypes()[0];

                PropertyValueConverter valueConverter = getValueConverter(paramType);

                if (valueConverter != null) {
                    try {
                        //System.out.println("invoke " + method);
                        method.setAccessible(true);                    
                        method.invoke(comp,
                                      new Object[]{valueConverter.convertPropertyValue(value, paramType)});
                        return;
                    } catch (Exception ex) {
                        // ignore it, maybe log it...
                    } // end of try-catch

                } // end of if ()
            } // end of if ()
        } // end of for (int i=0; i<; i++)
    }

    public void addPropertyValueConverter(PropertyValueConverter valueConverter,
                                          Class clazz) {

        propertyValueConverters.put(clazz, valueConverter);
    }

    protected PropertyValueConverter getValueConverter(Class clazz) {
        if (clazz == null) {
            return DEFAULT_PROPERTY_VALUE_CONVERTER;
        } // end of if ()

        if (propertyValueConverters.containsKey(clazz)) {
            return (PropertyValueConverter)propertyValueConverters.get(clazz);
        } // end of if ()

        return getValueConverter(clazz.getSuperclass());
    }

    public Class[] getSupportedClasses() {
        return classes;
    }

}// DefaultPropertyManager

/*
   $Log$
   Revision 1.7  2004/10/08 08:43:26  blueshift
   BATCH UPDATE
   - Switched logging to commons logging
   - Correct handling of default button
   - Implemented outdated request feature
   - Implemented an 'approximation' back button
   - More usage of FORM Submit via updated javascript
   - Modified javscript to distinguish jsSubmit from submits by enter-key
   - JavaDoc

   Revision 1.6  2003/10/31 10:15:38  hengels
   o java logging instead of system.out / system.err
   o patches from doug porter
   o fixed form action problem

   Revision 1.5  2003/10/15 06:48:43  arminhaaf
   o make methods accessible

   Revision 1.4  2002/11/19 15:39:05  ahaaf
   o use shared instance

   Revision 1.3  2002/10/26 11:59:35  ahaaf
   o deactivate scripting per default

   Revision 1.2  2002/09/04 14:26:35  ahaaf
   o SComponent now have a EventListenerList (not allocated by default)
   o every component which manages listeners uses now the SComponent EventListener

   Revision 1.1  2002/08/06 16:45:55  ahaaf
   add DefaultPropertyManager using reflection and bean shell scripting support

*/
