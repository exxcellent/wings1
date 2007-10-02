package ldap.editors;

import java.util.StringTokenizer;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;

import org.wings.SComponent;
import org.wings.STextArea;


public class MultiLineTextEditor
    implements Editor
{
  
  private final String DELIM = "$";
  
  
  public SComponent createComponent(Attributes attributes) {
    STextArea c = new STextArea();
	c.setColumns(60);
	c.setRows(3);
	return c;
  }

  public void setValue(SComponent component, Attribute attribute)
    throws NamingException
    {
      if (attribute == null) {
        ((STextArea)component).setText(null);
	    return;
	}

	if (attribute.size() == 1)
	    ((STextArea)component).setText("" + attribute.get(0));
        
        if (attribute.size() > 1) {
          StringBuffer sb = new StringBuffer();
          for (int i = 0;i<attribute.size();i++)
            sb.append(attribute.get(i) + DELIM);
          ((STextArea)component).setText(sb.toString().trim());
        }

    }

  public Attribute getValue(SComponent component, String id) {
    String value = ((STextArea)component).getText();
    Attribute attribute = new BasicAttribute(id);
    
    if (value != null && value.length() > 0) {
      StringTokenizer stk = new StringTokenizer(value,DELIM);
      while(stk.hasMoreTokens()) {
        attribute.add(stk.nextToken());
      }
    }
    return attribute;
          
  }
}

