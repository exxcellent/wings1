package ldap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class LDAP
{
    public final static int MAY = 1;
    public final static int MUST = 2;

    private final static List objectClasses = new ArrayList();
    public static List getObjectClasses(DirContext schema)
	throws NamingException
    {
	if (objectClasses.size() == 0) {
	    NamingEnumeration num = schema.listBindings("ClassDefinition");
	    while (num.hasMore()) {
		Binding binding = (Binding)num.next();
		objectClasses.add(binding.getName());
	    }
	}
	return objectClasses;
    }

    private final static Map classDefinitionCache = new HashMap();

    public static Attributes getClassDefinition(DirContext schema, String objectClass)
	throws NamingException
    {
	Attributes classDefinition = (Attributes)classDefinitionCache.get(objectClass);
	if (classDefinition == null) {
	    classDefinition = schema.getAttributes("ClassDefinition/" + objectClass);
	    classDefinitionCache.put("objectClass", classDefinition);
	}
	return classDefinition;
    }

    public static List getClassDefinitions(DirContext schema, String objectClass)
	throws NamingException
    {
	List definitions = new LinkedList();

	Attribute sup = new BasicAttribute("SUP", objectClass);
	do {
	    objectClass = (String)sup.get(0);
	    Attributes definition = getClassDefinition(schema, objectClass);
	    definitions.add(definition);
	    sup = definition.get("SUP");
	} while (sup != null);

	return definitions;
    }

    private final static Map attributeDefinitionCache = new HashMap();

    public static Attributes getAttributeDefinition(DirContext schema, String attributeType)
	throws NamingException
    {
	Attributes attributeDefinition = (Attributes)attributeDefinitionCache.get(attributeType);
	if (attributeDefinition == null) {
	    attributeDefinition = schema.getAttributes("AttributeDefinition/" + attributeType);
	    attributeDefinitionCache.put("objectAttribute", attributeDefinition);
	}
	return attributeDefinition;
    }

    public static Map getAttributeDefinitions(DirContext schema, Attributes classDefinition, int maymust)
	throws NamingException
    {
	Map attributes = new HashMap();
	Attribute names = classDefinition.get((maymust == MUST) ? "MUST" : "MAY");
	if (names != null) {
	    NamingEnumeration num = names.getAll();
	    while (num.hasMore()) {
		String name = (String)num.next();
		attributes.put(name, getAttributeDefinition(schema, name));
	    }
	}
	return attributes;
    }

    public static void main(String[] args) {
	try {
	    Hashtable env = new Hashtable();
	    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	    env.put(Context.PROVIDER_URL, "ldap://localhost:389/");
	    env.put(Context.SECURITY_PRINCIPAL, "cn=manager,dc=to,dc=com");
	    env.put(Context.SECURITY_CREDENTIALS, "secret");
	    DirContext context = new InitialDirContext(env);
	    DirContext schema = context.getSchema("");

	    List definitions = getClassDefinitions(schema, args[0]);
	    Iterator all = definitions.iterator();
	    while (all.hasNext()) {
		Attributes classDefinition = (Attributes)all.next();
		System.out.println("---");
		System.out.println("class: " + classDefinition.get("NAME").get(0));

		Attribute must = classDefinition.get("MUST");
		if (must != null) {
		    NamingEnumeration num = must.getAll();
		    while (num.hasMore()) {
			String name = (String)num.next();
			System.out.println("attribute (MUST): " + name);
			
			Attributes attributes = schema.getAttributes("AttributeDefinition/" + name);
			NamingEnumeration eenum = attributes.getAll();
			while (eenum.hasMore()) {
			    Attribute attribute = (Attribute)eenum.next();
			    System.out.println("" + attribute);
			}
		    }
		}

		Attribute may = classDefinition.get("MAY");
		if (may != null) {
		    NamingEnumeration num = may.getAll();
		    while (num.hasMore()) {
			String name = (String)num.next();
			System.out.println("attribute (MAY): " + name);
			
			Attributes attributes = schema.getAttributes("AttributeDefinition/" + name);
			NamingEnumeration eenum = attributes.getAll();
			while (eenum.hasMore()) {
			    Attribute attribute = (Attribute)eenum.next();
			    System.out.println("" + attribute);
			}
		    }
		}
	    }
	}
	catch (NamingException e) {
	    e.printStackTrace(System.err);
	}
    }
}
