package ldap.editors;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.wings.SComponent;

public interface Editor
{
    SComponent createComponent(Attributes attributes);
    void setValue(SComponent component, Attribute attribute)
	throws NamingException;
    Attribute getValue(SComponent component, String id) throws NamingException;
}
