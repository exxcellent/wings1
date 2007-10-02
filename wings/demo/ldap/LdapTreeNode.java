package ldap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LdapTreeNode
    implements MutableTreeNode 
{
    private final static Log logger = LogFactory.getLog("ldap");

    private static String[] RETURNING_ATTRIBUTES = new String [] { "objectclass" };
    private static String SEARCH_FILTER = "(objectclass=*)";

    private String dn;

    private DirContext context;
    private Hashtable environment;
    private LdapTreeNode parent;
    private ArrayList children = null;

    public LdapTreeNode(DirContext context, LdapTreeNode parent, String dn) {
	this.context = context;
	this.dn = dn;
	this.parent = parent;

        try {
            this.environment = context.getEnvironment();
        }
        catch (NamingException e) { /* ignore */ }
    }

    public LdapTreeNode(String dn) {
       	this.dn = dn;
    }

    public TreeNode getChildAt(int childIndex) {
	if (children == null)
	    getChildren();

	return (TreeNode)children.get(childIndex);
    }
    
    public int getChildCount() {
	if (children == null)
	    getChildren();

	return children.size();
    }

    public void setParent(MutableTreeNode node) {
        this.parent = parent;
    }
    public TreeNode getParent() {
	return parent;
    }

    public void removeFromParent() {
        this.parent = null;
    }

    public void add(MutableTreeNode child) {
	child.setParent(this);
	children.add(child);
    }

    public void insert(MutableTreeNode child, int index) {
	child.setParent(this);
	children.add(index, child);
    }

    public void remove(MutableTreeNode child) {
	children.remove(child);
    }

    public void remove(int index) {
	children.remove(index);
    }

    public void setUserObject(Object object) {
        dn = (String)object;
    }

    public int getIndex(TreeNode node) {
	if (children == null)
	    getChildren();

	return children.indexOf(node);
    }

    public boolean getAllowsChildren() {
	return true;
    }

    public boolean isLeaf() {
	if (children == null)
	    getChildren();

        return children.isEmpty();
    }

    public Enumeration children() {
	if (children == null)
            getChildren();

	return Collections.enumeration(children);
    }

    private void getChildren() {
        try {
            children = new ArrayList();

            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.ONELEVEL_SCOPE);
            constraints.setReturningAttributes(RETURNING_ATTRIBUTES);

            NamingEnumeration results = getContext().search(getDN(), SEARCH_FILTER, constraints);
            while (results != null && results.hasMoreElements()) {
                SearchResult sr  = (SearchResult)results.next(); 
                children.add(new LdapTreeNode(context, this, sr.getName()));
            }
        }
        catch (CommunicationException e) {
            logger.warn( (String)environment.get(Context.PROVIDER_URL), e);
            context = null;
        }
        catch (NamingException e) {
            logger.fatal( "get children failed", e);
        }
    }

    public String getDN() {
        if (getParent() == null || !(getParent() instanceof LdapTreeNode))
            return dn;
        else {
            String actDN = dn + "," + ((LdapTreeNode)getParent()).getDN();
            if (actDN.endsWith(",")) {
                int index = actDN.lastIndexOf(",");
                actDN = actDN.substring(0,index);
            }
            return actDN;
        }
    }

    public String toString() {
        return dn;
    }

    protected DirContext getContext()
        throws NamingException
    {
        if (context == null)
            context = new InitialDirContext(environment);
        return context;
    }

    public static void main(String[] args) {
        String server = args[0];
        String bindDN = args[1];
        String passwd = args[2];
        String rootDN = args[3];

	Hashtable env = new Hashtable();
	env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	env.put(Context.PROVIDER_URL, "ldap://" + server);
	env.put(Context.SECURITY_PRINCIPAL, bindDN);
	env.put(Context.SECURITY_CREDENTIALS, passwd);

	try {
	    DirContext context = new InitialDirContext(env);
            LdapTreeNode node = new LdapTreeNode(context, null, rootDN);

            DefaultMutableTreeNode root = new DefaultMutableTreeNode();
            root.add(node);

            Enumeration enum = root.breadthFirstEnumeration();
        }
	catch (NamingException e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
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
