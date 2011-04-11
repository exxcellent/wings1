package ldap;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.AttributeModificationException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.directory.ModificationItem;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.SButton;
import org.wings.SFlowDownLayout;
import org.wings.SForm;
import org.wings.SLabel;
import org.wings.SPanel;
import org.wings.SResetButton;
import org.wings.STree;
import org.wings.session.Session;
import org.wings.session.SessionManager;

public class EditObjectPanel
    extends SForm
{
    private final static Log logger = LogFactory.getLog("ldap");

    Properties attributeOrdering;
    Attributes backedAttributes;

    SLabel dnLabel;

    AttributesEditor editor;
    SButton okButton;
    SButton removeButton;
    SResetButton cancelButton;

    String dn;

    public EditObjectPanel()
        throws NamingException
    {
        super(new SFlowDownLayout());
        setEncodingType("multipart/form-data");

        attributeOrdering = new Properties();
        try {
            InputStream in = getClass().getResourceAsStream("attributeorder.properties");
            attributeOrdering.load(in);
        }
        catch (Exception e) {
	    logger.warn( "no attribute ordering", e);
        }

        dnLabel = new SLabel();
        add(dnLabel);
        
        editor = new AttributesEditor();
        add(editor);

        SPanel buttons = new SPanel(null);
        buttons.setAttribute("align", "right");

        okButton = new SButton("ok");
        okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    ok();
                }
            });
        buttons.add(okButton);

        removeButton = new SButton("remove");
        removeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    remove();
                }
            });
        buttons.add(removeButton);

        cancelButton = new SResetButton("cancel");
        buttons.add(cancelButton);

        add(buttons);
    }

    public void setDn(String dn)
	throws NamingException
    {
        this.dn = dn;

	Attributes attributes = getContext().getAttributes(dn);

	Attribute objectClassAttribute = attributes.get("objectClass");
	String objectClass = (String)objectClassAttribute.get();
	List definitions = LDAP.getClassDefinitions(getSchema(), objectClass);

        String order = attributeOrdering.getProperty(objectClass);
        if (order != null)
            editor.setSorter(new AttributeOrderComparator(order));

	editor.clearClassDefinitions();
	Iterator it = definitions.iterator();
	while (it.hasNext()) {
	    Attributes definition = (Attributes)it.next();
	    if (!"TOP".equalsIgnoreCase((String)definition.get("NAME").get(0)))
		editor.addClassDefinition(definition);
	}
	editor.setData(attributes);

        dnLabel.setText(dn + " [" + objectClass + "]");
	backedAttributes = attributes;
    }

    protected void ok() {
        Attributes attributes = null;
	try {
            attributes = editor.getData();
        }
	catch (NamingException e) {
	    logger.warn( "modify failed", e);
            return;
	}

        try {
	    List modifications = new LinkedList();
	    NamingEnumeration num = attributes.getAll();
	    while (num.hasMore()) {
		Attribute editedAttribute = (Attribute)num.next();
		Attribute backedAttribute = backedAttributes.get(editedAttribute.getID());

		if (editedAttribute.equals(backedAttribute))
		    continue;

		modifications.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, editedAttribute));
	    }

	    ModificationItem[] items
		= (ModificationItem[])modifications.toArray(new ModificationItem[modifications.size()]);
	    getContext().modifyAttributes(dn, items);
	}
	catch (InvalidAttributeValueException e) {
	    String message = e.getMessage();
	    if (message != null) {
		int minus = message.indexOf("-");
		int colon = message.indexOf(':', minus);
		if (minus != -1 && colon != -1) {
		    String id = message.substring(minus + 2, colon);
		    editor.addMessage(id, "invalid value");
		}
	    }
	}
	catch (AttributeModificationException e) {
	    logger.warn( "modify failed", e);

	    ModificationItem[] items = e.getUnexecutedModifications();
	    if (items != null)
		for (int i=0; i < items.length; i++)
		    editor.addMessage(items[i].getAttribute().getID(), "schema violation");
	}
	catch (NamingException e) {
	    logger.warn( "modify failed", e);
	}
        catch (Exception e) {
	    logger.warn( "modify failed", e);
        }
    }

    protected void remove() {
	try {
            STree tree = (STree)SessionManager.getSession().getProperty("tree");
            
            LdapTreeNode node =
                (LdapTreeNode)tree.getLastSelectedPathComponent();
                        
            if (node.isLeaf()) {
                DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
                model.removeNodeFromParent(node);
                //model.nodesWereInserted(parentNode, new int[]
                //  {parentNode.getChildCount()-1});
                
                getContext().destroySubcontext(dn);
                editor.clearClassDefinitions();
                editor.removeAll();
            }
            else
                System.out.println("bitte child auswaehlen");
	}
	catch (NamingException e) {
	    logger.warn( "remove failed", e);
	}
    }

    private DirContext context = null;

    protected DirContext getContext()
        throws NamingException
    {
        if (context == null) {
            Session session = getSession();
	    context = new InitialDirContext(new Hashtable(getSession().getProperties()));
        }
        return context;
    }

    private DirContext schema = null;

    protected DirContext getSchema()
        throws NamingException
    {
        if (schema == null) {
            Session session = getSession();
	    schema = getContext().getSchema("");
        }
        return schema;
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */




