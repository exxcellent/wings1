package ldap.editors;

import java.util.StringTokenizer;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.SComponent;
import org.wings.STextField;


public class SingleLineTextEditor
    implements Editor
{
  private final static Log logger = LogFactory.getLog("ldap.editors");
  
  private final String DELIM = "$";
  
  public SComponent createComponent(Attributes attributes) {
    STextField c = new STextField();
    int cols = 80;
	try {
          Attribute syntaxAttribute = attributes.get("SYNTAX");
          if (syntaxAttribute != null) {
		String syntax = (String)syntaxAttribute.get();
		System.err.println("SYNTAX: " + syntax);
		int open = syntax.indexOf("{");
		int close = syntax.indexOf("}");
		if (open != -1 && close != -1) {
		    syntax = syntax.substring(open + 1, close);
		    cols = new Integer(syntax).intValue();
		}
	    }
	}
	catch (NamingException e) {
            logger.warn( "attributes " + attributes, e);
	}

	c.setMaxColumns(cols);
	c.setColumns(Math.min(cols, 60));
	return c;
    }

    public void setValue(SComponent component, Attribute attribute)
	throws NamingException
    {
	if (attribute == null) {
	    ((STextField)component).setText(null);
	    return;
	}

	if (attribute.size() == 1)
	    ((STextField)component).setText("" + attribute.get(0));
        
	if (attribute.size() > 1) {
          StringBuffer sb = new StringBuffer();
          for (int i = 0;i<attribute.size();i++)
            sb.append(attribute.get(i) + DELIM);
          ((STextField)component).setText(sb.toString().trim());
        }
    }

  public Attribute getValue(SComponent component, String id) {
    String value = ((STextField)component).getText();
    
    Attribute attribute = new BasicAttribute(id);
    
    if (value!=null && value.length() > 0) {
      StringTokenizer stk = new StringTokenizer(value,DELIM);
      while(stk.hasMoreTokens()) {
        attribute.add(stk.nextToken());
       }
    }
    return attribute;
  }
}


