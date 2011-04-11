package ldap;

// uebergangsweise ..
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.wings.session.SessionManager;

public class LdapWorker
{
    private DirContext ctx;

    private boolean success = true;
    private String baseDN;
    private String bindDN;
    private String password;
    private String server;
    private final static String DELIM = ":";

    private boolean addOp = false;

    public LdapWorker (String s, String base, String bind, String p) {
	setBaseDN(base);
	setBindDN(bind);
	setServer(s);
	setPassword(p);

	this.password = p;
	Hashtable env = new Hashtable(SessionManager.getSession().getProperties());
	try {
	    ctx = new InitialDirContext(env);
	}
	catch(NamingException e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
	    setSuccess(false);
        }
    }

    public boolean getAddOp() {
        return addOp;
    }

    public void setAddOp(boolean status) {
        addOp = status;
    }

    private void setBaseDN(String b) {
	baseDN = b;
    }
    private void setBindDN(String b) {
	bindDN = b;
    }
    private void setServer(String b) {
	server = b;
    }
    
    private void setPassword(String p) {
	password = p;
    }
        
    public String getBaseDN() {
	return baseDN;
    }
    
    public String getBindDN() {
	return bindDN;
    }
    
    public String getServer() {
	return server;
    }

    private String getPassword() {
	return password;
    }

    private void setSuccess(boolean s) {
	success = s;
    }

    public boolean getSuccess() {
	return success;
    }

    public DirContext getContext() { return ctx; }

    //veraendert attribute eines entry's mit dem DN=dn
    public boolean modifyAttributes(String dn, BasicAttributes attributes) {
	try {
	    ctx.modifyAttributes(dn, ctx.REPLACE_ATTRIBUTE, attributes);
	    return true;
	}
	catch(Exception e) {
	    System.out.println(e);
	    return false;
	}
    }

    //gibt einen Vector der unterstuetzten objektklassen zurueck
    public ArrayList getObjects() {

	ArrayList ol = null;
	try {
	    String [] attrs = null;
	    attrs = new String [] {"objectclasses"};
	    
	    SearchControls ctrl = new SearchControls();
	    ctrl.setSearchScope(SearchControls.OBJECT_SCOPE);
	    ctrl.setReturningAttributes(attrs);
	    
	    // Search for objects that have those matching attributes
	    NamingEnumeration answer = ctx.search("cn=subschema","(objectclass=*)",ctrl);
	    
	    Attribute attr = null;
	    while (answer.hasMore()) {
		SearchResult sr = (SearchResult)answer.next();
                Attributes attribs = sr.getAttributes();
		for (NamingEnumeration ae = attribs.getAll();
		     ae.hasMore();) {
		    attr = (Attribute)ae.next();
                }

		NamingEnumeration ne = attr.getAll();
		ol = getObjNames(ne);
	    }
	} 
	catch (Exception e) {
	    e.printStackTrace();
	}
	return ol;
    }
    
    //parst die Objektklassen und gibt nur die Namen zurueck
    private ArrayList getObjNames(NamingEnumeration ne) {
	ArrayList objArray = new ArrayList();
	
	try {
	    while (ne!=null && ne.hasMore()) {
		String oString = ne.next().toString();
		StringTokenizer sto = new StringTokenizer(oString,new String("'"));
		String obj = sto.nextToken();
		obj = sto.nextToken();
		
		int index = oString.indexOf("SUP") + 4;
		String rest = null;
		String sup = null;
		if (index > 4)
  		    rest = oString.substring(index);
  		if (rest!=null) { 
  		    StringTokenizer suptok = new StringTokenizer(rest," ");
  		    sup = suptok.nextToken();
  		}
  		if (sup!=null && !sup.equals("top")) {
  		    obj = obj + " SUP " +"(" + sup + ")";
                }
		
  		objArray.add(obj);
  	    }
  	}
  	catch (NamingException nex) {
  	    System.out.println(nex);
  	}
  	return objArray;
      }
    
    
      //gibt die attribute eines entry's zurueck
      public Attributes getDNAttributes(String dn)
    {
	System.out.println("bei dn "  +dn);
	ArrayList attrList = new ArrayList();
	Hashtable h = new Hashtable();
	Attributes a = null;
	NamingEnumeration e;
	try {
	    a = ctx.getAttributes(dn);
	    e = a.getAll();
	}
	catch (NamingException exc) {
	}
	return (a);
    }
    
    //gibt die Attribute der Objektklassen eines entry's zurueck
    public Vector getObjectAttributes(String dn) {
	Vector myNames = null;
	try {
	    DirContext dc = ctx.getSchemaClassDefinition(dn);
	    
	    NamingEnumeration num = dc.search("", null);
	    myNames = new Vector();
	    while (num.hasMore()) {
		SearchResult si = (SearchResult)num.next();
		Attributes ocAttrs = si.getAttributes();
		Attribute name = ocAttrs.get("NAME");
		Attribute must = ocAttrs.get("MUST");
		
		if (must!=null) {
		    NamingEnumeration mt = must.getAll();
		    while (mt!= null && mt.hasMore()) {
			myNames.addElement((String)mt.next());
		    }
		}
		Attribute may = ocAttrs.get("MAY");
		// optional attributes
		if (may !=null) {
		    NamingEnumeration my = may.getAll();
		    while (my !=null && my.hasMoreElements()) {
			myNames.addElement((String)my.next());
		    }
		}
	}
	return myNames;
	}
	catch (NamingException ne) {
	    System.out.println(ne);
	}
	return myNames;
    }
    

    NamingEnumeration list(String searchbase) {
	BasicAttribute n;
	Integer i;
	
	String [] attrs = null;
	NamingEnumeration results = null;
	
	attrs = new String [] {"objectclass"};
	try {
	    results = search(searchbase, "(objectclass=*)",attrs, SearchControls.ONELEVEL_SCOPE);
	}
	catch (NamingException e) {
	    System.err.println(e);
	}
	return results;
    }

    //gibt den Wert fuer das genannte Attribut zurueck
    public Object getOAttributeValues (String dn,String attrName) {

	System.out.println("na was denn");
	Object val = null;
	System.out.println(dn);
	try {
	    Attributes matchAttrs = new BasicAttributes(true); // ignore attribute name case
	    matchAttrs.put(new BasicAttribute(attrName));
	    //Search for objects that have those matching attributes
	    
	    NamingEnumeration num = search(dn, "(objectclass=*)",new String[] {attrName}, SearchControls.OBJECT_SCOPE);
	    while (num.hasMore()) {
		SearchResult sr = (SearchResult)num.next();
		BasicAttributes bas = (BasicAttributes)sr.getAttributes();
		BasicAttribute ba = (BasicAttribute)bas.get(attrName);
		if (ba != null)
		    val = ba.get();
		else val = "";
	    }
	}
	catch(NamingException u) {
	}
	return val;
    }
    
    //attribute-dn mapping- gibt die dn 's zurueck fuer die es das Attribut "attribute" gefunden wurde
    public HashMap  getAttributeDNValues(String attribute,String baseDN) {
	
	ArrayList attrList = new ArrayList();
	HashMap peopleDNMap = new HashMap(); 

	try {
	    Attributes matchAttrs = new BasicAttributes(true); // ignore attribute name case
	    matchAttrs.put(new BasicAttribute(attribute));
	    
	    // Search for objects that have those matching attributes
	    //NamingEnumeration enum = ctx.search(getBaseDN(), matchAttrs);
	    SearchControls cons = new SearchControls();
	    cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
	    NamingEnumeration num = ctx.search(getBaseDN(), "(cn=*)",cons);
	    	   
	    while (num.hasMore()) {
		SearchResult sr = (SearchResult) num.next();
		BasicAttributes ba = (BasicAttributes)sr.getAttributes();
		BasicAttribute cn = (BasicAttribute)ba.get("cn");
		attrList.add(cn.get());
		peopleDNMap.put(cn.get(),sr.getName());
	    }
	}
	catch(NamingException u) {
	}
	return peopleDNMap;
    }

    //gibt die Liste der DN's die filter haben
    public ArrayList getFilteredAllDN(String baseDN,String f) {
	String filter = f;
	String [] attribs = {"cn"};
	ArrayList l = new ArrayList();
	try {
	    NamingEnumeration en = search(getBaseDN(),f,attribs,2);
	    while (en!=null && en.hasMoreElements()) {
		SearchResult sr = (SearchResult)en.next();
		l.add(sr.getName());
	    }
	}
	catch(NamingException ex) {
	}
	return l;	
    }

    
    private NamingEnumeration search(String dn, String filter, String []attribs,
				     int type) 
	throws NamingException {
	/* specify search constraints to search subtree */
	SearchControls constraints = new SearchControls();
	
	constraints.setSearchScope(type);	 
	constraints.setCountLimit(0);
	constraints.setTimeLimit(0);
	constraints.setReturningAttributes(attribs);
	
	NamingEnumeration results = ctx.search(dn, filter, constraints);	
	if (results == null) 
	    System.out.println("nichts im result");
	
	return results;
    }
    

    //gibt alle Attribute fuer die Liste zurueck, die must attr. objectclass haben schon ihr value zugeordnet
    public Hashtable getAttributes(ArrayList objectClasses) {
	
		
	Hashtable attributes = new Hashtable();
	ArrayList attrValues;
	try {
	    DirContext sch =ctx.getSchema("");
	    
	    // From object classes, get list of mandatory and optional attributes
	    Vector[] attrNames = getAttributeLists(sch, objectClasses);
	    
	    Attributes attrs = new BasicAttributes(true);
	    
	    // Remove "objectclasses" from mandatory so that we don't ask user again
	    attrNames[0].removeElement("objectclass");
	    attrValues = new ArrayList();
	    Attribute oc = new BasicAttribute("objectclass");
	    for (int i = 0; i < objectClasses.size(); i++) {
		System.out.println("i  " + (String)objectClasses.get(i));
		attrValues.add(((String)objectClasses.get(i)).trim());
		oc.add(((String)objectClasses.get(i)).trim());
	    }
	    attrs.put(oc);
	    attributes.put("objectclass",attrValues);
	    
	    for (int i = 0;i< attrNames[0].size() ;i++) {
		String name = (String)(attrNames[0].elementAt(i));
		Attributes attrSchema = 
		sch.getAttributes("AttributeDefinition/" + name);
				//die must attribute haben noch ein sternchen
		String msg;
		msg = "*" + name;
		attrValues  = new ArrayList();
 		attributes.put(msg,attrValues);
	    }
	    
	    for (int i = 0;i< attrNames[1].size() ;i++) {
		String name = (String)(attrNames[1].elementAt(i));
		Attributes attrSchema = 
		sch.getAttributes("AttributeDefinition/" + name);
				
		String msg;
		msg = name;
		attrValues  = new ArrayList();
 		attributes.put(msg,attrValues);
	    }
	}
	catch (NamingException e) {
	    System.out.println(e.getMessage());
	    e.printStackTrace();
	}
	return attributes;
    }
    
   
    //gibt die liste von MUST und MAY attribute zurueck 
    private Vector[] getAttributeLists(DirContext schema, ArrayList objectClasses) 
	throws NamingException {
	
        Vector mandatory = new Vector();
	Vector optional = new Vector();
		
	for (int i = 0; i < objectClasses.size(); i++) {
	    String oc = (String)objectClasses.get(i);
	    System.out.println("da noch O.K " + oc  );
	    Attributes ocAttrs = schema.getAttributes("ClassDefinition/" + oc.trim());
	    System.out.println("gut");
	    Attribute must = ocAttrs.get("MUST");
	    Attribute may = ocAttrs.get("MAY");
	    if (must !=null) {
		addAttrNameToList(mandatory,must.getAll()); 
	    }
	    if (may!=null) {
		addAttrNameToList(optional,may.getAll());	    
	    }
	}
	return new Vector[] {mandatory,optional};
    }

    
    
    private void addAttrNameToList(Vector store,NamingEnumeration vals) 
	throws NamingException {

	while (vals.hasMore()) {
	    Object val = vals.next();
	    if (!store.contains(val)) {
		store.addElement(val);
	    }
	}
	
    }

    
    public void addNewEntry(String dn, Hashtable vals) {
	try {
            System.err.println("die dn ist " + dn);
            System.err.println("die Attribute " + vals);
            Attributes attrs = getAttributes(vals);
            System.err.println("die bearbeiteten Attribute  " + attrs);
            ctx.createSubcontext(dn,attrs);
            addOp = true;
            
        }
	catch (NamingException e){
	    e.printStackTrace();
	}
    }
    
    
    private Attributes getAttributes(Hashtable vals) {
        Attributes attrs = new BasicAttributes();
	Enumeration e;
	e = vals.keys();
	while (e!=null && e.hasMoreElements()) {
	    Object key = e.nextElement();
	    Attribute attr = new BasicAttribute((String)key);
	    String values = (String)vals.get(key);
            	    StringTokenizer parser = new StringTokenizer(values, DELIM);
	    while (parser.hasMoreTokens()) {
                attr.add(parser.nextToken());
	    }
	    attrs.put(attr);
	}
	return attrs;
    }

    //remove the entry
    public void removeEntry(String dn) {
	try { 
            ctx.destroySubcontext(dn);
	}
	catch (NamingException exc) {
	    System.out.println("entry to remove not found");
	}
    }
    
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
