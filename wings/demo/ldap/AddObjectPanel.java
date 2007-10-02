package ldap;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
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
import javax.naming.directory.InvalidAttributeValueException;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.SButton;
import org.wings.SFlowDownLayout;
import org.wings.SForm;
import org.wings.SLabel;
import org.wings.SList;
import org.wings.SPanel;
import org.wings.STextField;
import org.wings.STree;
import org.wings.session.Session;
import org.wings.session.SessionManager;

public class AddObjectPanel
    extends SForm
{
    private final static Log logger = LogFactory.getLog("ldap");

    Properties dn;
    
    ResourceBundle objectClassBundle;
    ResourceBundle attributeBundle;

    Attributes emptyAttributes = new BasicAttributes();

    STextField dnTextField;
    SLabel objectClassLabel;
    SList objectClassList;
    AttributesEditor editor;
    SButton addButton;
    SButton okButton;
    SButton cancelButton;
   
    String parent = (String)SessionManager.getSession().getProperty("java.naming.provider.basedn");
    
    public AddObjectPanel()
        throws NamingException
    {
        super(new SFlowDownLayout());
        setEncodingType("multipart/form-data");

        dnTextField = new STextField();
        
        dnTextField.setColumns(60);
        dnTextField.setAttribute("width", "100%");
        add(dnTextField);

        objectClassList = new SList();
        objectClassList.setAttribute("width", "100%");
        objectClassList.setListData(LDAP.getObjectClasses(getSchema()));
        objectClassList.setSelectionMode(SINGLE_SELECTION);
        add(objectClassList);

        objectClassLabel = new SLabel();
        add(objectClassLabel);

        editor = new AttributesEditor();
        add(editor);

        SPanel buttons = new SPanel(null);
        buttons.setAttribute("align", "right");

        addButton = new SButton("add");
        addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    add();
                }
            });
        buttons.add(addButton);

        okButton = new SButton("ok");
        okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    ok();
                }
            });
        buttons.add(okButton);

        cancelButton = new SButton("cancel");
        cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    cancel();
                }
            });
        buttons.add(cancelButton);

        add(buttons);

        objectClassLabel.setVisible(false);
        editor.setVisible(false);
        okButton.setVisible(false);
        cancelButton.setVisible(false);
    }

    public void setParent(String parent) {
        this.parent = parent;
        dnTextField.setText(parent);
    }

    protected void add() {
        String objectClass = (String)objectClassList.getSelectedValue();
        try {
            List definitions = LDAP.getClassDefinitions(getSchema(), objectClass);
            editor.clearClassDefinitions();
            
            Iterator it = definitions.iterator();

            while (it.hasNext()) {
                Attributes definition = (Attributes)it.next();
                if (!"TOP".equalsIgnoreCase((String)definition.get("NAME").get(0)))
                    editor.addClassDefinition(definition);
            }
            
            addButton.setVisible(false);
            objectClassList.setVisible(false);
            objectClassLabel.setVisible(true);
            editor.setVisible(true);
            okButton.setVisible(true);
            cancelButton.setVisible(true);

            objectClassLabel.setText(" [" + objectClass + "]");
        }
	catch (NamingException e) {
            logger.fatal( "objectClass: " + objectClass, e);
	}
    }

    
    protected void ok() {
        String dnKey = null;
        String dnAttribute = null;
        String objectClass = (String)objectClassList.getSelectedValue();
        try {
            dn = new Properties();
            InputStream in = AddObjectPanel.class.getClassLoader().getResourceAsStream("ldap/editors/dn.properties");
            dn.load(in);
            
            dnKey = dn.getProperty(objectClass.toLowerCase() + ".dncomponents");
            dnAttribute = dn.getProperty(objectClass.toLowerCase() + ".dnattribute");
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            logger.fatal( null, e);
	}
        
        try {
            boolean automatic = false;
            Vector keyComponents = new Vector();
            String dn = dnTextField.getText();
            
            if (parent.trim().equals(dn.trim())) {
                if (dnKey!=null) {
                    automatic = true;
                    StringTokenizer st = new StringTokenizer(dnKey,",");
                    while (st.hasMoreTokens()) {
                        keyComponents.add(st.nextToken());
                    }
                }
            }
            
            Attributes attributes = editor.getData();
            Attributes specifiedAttributes = new BasicAttributes();
            NamingEnumeration enum = attributes.getAll();
            HashMap dnMap = new HashMap();
	    while (enum.hasMore()) {
		Attribute attribute = (Attribute)enum.next();
                if (attribute.size() != 0) {
                    if (automatic) {
                        if (keyComponents.contains(attribute.getID().toLowerCase()))
                            dnMap.put((attribute.getID().toLowerCase()),attribute.get(0));
                    }
                    specifiedAttributes.put(attribute);
                }
            }
            
            specifiedAttributes.put(new BasicAttribute("objectClass",objectClass));
            StringBuffer dnBuffer = new StringBuffer();
            if (dnMap.size()>0) {
                for (int i = 0;i<dnMap.size();i++) 
                    dnBuffer.append((String)dnMap.get(keyComponents.get(i)) + "_");
                dn = dnBuffer.toString();
                int l = dn.length();
                if (dn.endsWith("_"))
                    dn = dn.substring(0,l-1);
                dn = dnAttribute + "=" + dn + "," + parent; 
            }
            
            String ownDN = dn;
            int index =  dn.indexOf(",");
            if (index>0)
                ownDN = dn.substring(0,index);

            // create
	    getContext().createSubcontext(dn, specifiedAttributes);

            // tell the tree
            STree tree = (STree)SessionManager.getSession().getProperty("tree");
            LdapTreeNode parentNode = (LdapTreeNode)tree.getLastSelectedPathComponent();
            if (parentNode == null)
                parentNode = (LdapTreeNode)(((DefaultTreeModel)(tree.getModel())).getRoot());

            LdapTreeNode node = new LdapTreeNode(getContext(), parentNode,ownDN);
            DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
            model.insertNodeInto(node,parentNode,0);
            //model.nodesWereInserted(parentNode, new int[]
            //  {parentNode.getChildCount()-1});
            
            editor.setData(emptyAttributes);
            objectClassLabel.setVisible(false);
            editor.setVisible(false);
            okButton.setVisible(false);
            cancelButton.setVisible(false);
            addButton.setVisible(true);
            objectClassList.setVisible(true);
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
	catch (Exception e) {
            logger.fatal( null, e);
	}
    }

    protected void cancel() {
        objectClassLabel.setVisible(false);
        editor.setVisible(false);
        okButton.setVisible(false);
        cancelButton.setVisible(false);
        addButton.setVisible(true);
        objectClassList.setVisible(true);
        try {
            editor.setData(emptyAttributes);
        }
	catch (NamingException e) {
            logger.warn( "ignorable", e);
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
