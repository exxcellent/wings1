package ldap;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.swing.AbstractAction;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.wings.SButton;
import org.wings.SConstants;
import org.wings.SDesktopPane;
import org.wings.SFileIcon;
import org.wings.SFlowLayout;
import org.wings.SForm;
import org.wings.SFrame;
import org.wings.SInternalFrame;
import org.wings.SLabel;
import org.wings.SList;
import org.wings.SPanel;
import org.wings.STable;
import org.wings.STemplateLayout;
import org.wings.STextField;
import org.wings.border.SBevelBorder;
import org.wings.session.Session;
import org.wings.session.SessionManager;

public class LdapBrowser
    implements SConstants {
    private SFrame frame;
    private SList namesList;
    private SForm namesForm;

    private DirContext context;

    private Vector namesVector;


    private String[] overviewAttributes;
    private String[] overviewLabels;
    private String[] detailviewAttributes;
    private String[] detailviewLabels;

    private String[] detailviewTemplateLabels =
        {"cn", "sn", "uid", "mail", "labeledURI", "telephoneNumber", "Geburtsjahr", "Taetigkeitsbereiche", "Zusatzausbildungen", "BeruflicherWerdegang", "Seit", "Teamzugehoerigkeit", "jpegPhoto"};

    private String searchLabel;
    private String searchAttribute;

    private String server;
    private String basedn;
    private String binddn;
    private String password;

    private SDesktopPane desktop;
    private SInternalFrame searchFrame;
    private SInternalFrame detailFrame;
    //private SFrame searchFrame;
    //private SFrame detailFrame;

    private STextField searchTextField;
    private STable table;
    private SPanel detailPanel;
    private SForm detailForm;

    private OverviewModel overviewModel;

    private final String SUBMIT = "Suche starten";

    public LdapBrowser() {
        frame = new SFrame("LDAP Browser");

        Session properties = SessionManager.getSession();
        StringTokenizer attributes, labels;
        int i;

        server = (String) properties.getProperty("ldap.server.host");
        basedn = (String) properties.getProperty("ldap.server.basedn");
        binddn = (String) properties.getProperty("ldap.server.binddn");
        password = (String) properties.getProperty("ldap.server.password");

        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, server);
        env.put(Context.SECURITY_PRINCIPAL, binddn);
        env.put(Context.SECURITY_CREDENTIALS, password);


        try {
            context = new InitialDirContext(env);
        } catch (NamingException e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
        namesVector = new Vector();

        attributes = new StringTokenizer((String) properties.getProperty("overview.attributes"), ",");
        labels = new StringTokenizer((String) properties.getProperty("overview.labels"), ",");
        overviewAttributes = new String[attributes.countTokens()];
        overviewLabels = new String[labels.countTokens()];
        i = 0;
        while (attributes.hasMoreTokens()) {
            overviewAttributes[i] = attributes.nextToken();
            overviewLabels[i] = labels.nextToken();
            i++;
        }

        attributes = new StringTokenizer((String) properties.getProperty("detailview.attributes"), ",");
        labels = new StringTokenizer((String) properties.getProperty("detailview.labels"), ",");
        detailviewAttributes = new String[attributes.countTokens()];
        detailviewLabels = new String[labels.countTokens()];
        i = 0;
        while (attributes.hasMoreTokens()) {
            detailviewAttributes[i] = attributes.nextToken();
            detailviewLabels[i] = labels.nextToken();
            i++;
        }

        this.searchAttribute = (String) properties.getProperty("search.attribute");
        this.searchLabel = (String) properties.getProperty("search.label");

        desktop = new SDesktopPane();
        frame.getContentPane().add(desktop);

        //searchFrame = new SFrame();
        //frame.getContentPane().add(searchFrame);
        searchFrame = new SInternalFrame();
        searchFrame.setClosable(false);
        searchFrame.setTitle("PGD");
        desktop.add(searchFrame);

        //detailFrame = new SFrame();
        //frame.getContentPane().add(detailFrame);
        detailFrame = new SInternalFrame();
        detailFrame.setTitle("PGD");
        desktop.add(detailFrame);

        SForm searchForm = new SForm(new SFlowLayout());
        searchForm.setBorder(new SBevelBorder());

        SLabel label = new SLabel(searchLabel);
        searchTextField = new STextField();

        SButton submit = new SButton(SUBMIT);
        submit.addActionListener(new SearchAction());

        String[] data = {"ones", "two", "three", "four"};

        namesList = new SList(data);

        namesList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
            }
        });
        namesList.setVisibleRowCount(3);
        namesList.setSelectionMode(SINGLE_SELECTION);

        searchForm.add(label);
        searchForm.add(searchTextField);
        searchForm.add(submit);
        //searchForm.add(namesList);

        searchFrame.getContentPane().add(searchForm, "North");

        overviewModel = new OverviewModel();


        namesForm = new SForm();
        //namesForm.add(namesList);
        table = new STable(overviewModel);
        table.setShowGrid(false);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);

        table.setSelectionMode(SINGLE_SELECTION);

        ListSelectionModel rowSM = table.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty()) {
                } else {
                    int selectedRow = lsm.getMinSelectionIndex();
                    showDetails(overviewModel.get(selectedRow));
                }
            }
        });
        AttributesCellRenderer renderer = new AttributesCellRenderer();
        renderer.setSelectableColumns(new int[]{0});
        renderer.addCellSelectionListener(new CellSelectionListener() {
            public void cellSelected(CellSelectionEvent e) {
                System.out.println("select: x=" + e.getXPosition() + ", y=" + e.getYPosition());
                showDetails(overviewModel.get(e.getYPosition()));
            }
        });
        table.setDefaultRenderer(Attribute.class, renderer);

        searchFrame.getContentPane().add(table);
        try {

            System.err.println(getClass().getResource("ldapbrowser.html"));
            detailPanel = new SPanel(new
                STemplateLayout(getClass().getResource("ldapbrowser.html")));
            detailFrame.getContentPane().add(detailPanel, "Center");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        SButton backButton = new SButton("ok");
        backButton.addActionListener(new BackAction());
        detailFrame.getContentPane().add(backButton, "South");

        // the SInternalFrame
        searchFrame.show();
        // the SFrame
        frame.show();
    }

    protected void showDetails(Attributes attributes) {
        System.err.println("show detailFrame");
        //detailPanel.removeAll();

        for (int i = 0; i < detailviewAttributes.length; i++) {
            String value = "value";

            try {

                Attribute attribute = attributes.get(detailviewAttributes[i]);
                SLabel detailLabel = new SLabel(detailviewLabels[i]);
                detailLabel.setForeground(Color.blue);

                detailPanel.add(detailLabel, detailviewTemplateLabels[i]);
                if (attribute != null) {
                    if (attribute.get().getClass().getName().equals("[B")) {
                        if (attribute.getID().equals("jpegPhoto")) {
                            SLabel iconL = new SLabel();
                            try {
                                File tmp = File.createTempFile("photo", "tmp", null);
                                FileOutputStream fos = new FileOutputStream(tmp);
                                fos.write((byte[]) attribute.get());
                                fos.close();
                                iconL.setIcon(new SFileIcon(tmp));
                            } catch (java.io.FileNotFoundException e) {
                                e.printStackTrace();
                                iconL.setText("photo kann nicht angezeigt werden");
                            } catch (java.io.IOException e) {
                                e.printStackTrace();
                                iconL.setText("photo kann nicht angezeigt werden");
                            }


//SImageIcon icon = new SImageIcon(new ImageIcon((byte[])attribute.get()));
//detailPanel.add(new SLabel(icon),detailviewTemplateLabels[i]+"_"+ value);
                            detailPanel.add(iconL, detailviewTemplateLabels[i] + "_" + value);
                        } else if (attribute.getID().equals("userPassword")) {
                            detailPanel.add(new
                                SLabel("-"), detailviewTemplateLabels[i] + "_" + value);
                        }
                    } else {
                        StringBuffer buffer = new StringBuffer();

                        for (int i2 = 0; i2 < attribute.size(); i2++) {
                            if (i2 > 0)
                                buffer.append(", ");
                            buffer.append(attribute.get(i2).toString());
                        }
                        detailPanel.add(new
                            SLabel(buffer.toString()), detailviewTemplateLabels[i] + "_" + value);
                    }
                } else
                    detailPanel.add(new SLabel("-"), detailviewTemplateLabels[i] + "_" + value);
            } catch (NullPointerException e) {
                e.printStackTrace();
                detailPanel.add(new SLabel("-"), detailviewTemplateLabels[i] + "_" + value);
            } catch (NamingException e) {
                detailPanel.add(new SLabel(e.getMessage()), value);
            }
        }

        detailFrame.setIconified(false);
        detailFrame.setClosed(false);
        searchFrame.setMaximized(false);
    }

    class SearchAction
        extends AbstractAction {
        public void actionPerformed(ActionEvent evt) {
//(||(sn=Geisel)(mail=*))";
            StringBuffer fB = new StringBuffer();
            fB.append("(&");
            fB.append("(|");
//   fB.append("(" + searchAttribute + "=*" + searchTextField.getText() + "*)");
//fB.append("(objectclass=PGDPerson)");
            fB.append(getSearchFilter(searchAttribute, searchTextField.getText().trim()));
            fB.append(")");
            fB.append("(objectclass=PGDPerson)");
            fB.append(")");

            String filter = fB.toString();
            System.out.println(filter);
            overviewModel.setFilter(filter);
//da
            populateList(filter);
        }

        private String getSearchFilter(String searchAttribute, String filter) {
            StringBuffer sb = new StringBuffer();
            StringTokenizer stk = new StringTokenizer(searchAttribute, ",");
            while (stk.hasMoreTokens()) {
                sb.append("(");
                sb.append(stk.nextToken() + "=*" + filter + "*)");
            }
            return sb.toString();
        }

    }

    class BackAction
        extends AbstractAction {
        public void actionPerformed(ActionEvent evt) {
            detailFrame.setIconified(true);
        }
    }

    public void populateList(String filter) {
        namesVector.clear();
        try {
            SearchControls cons = new SearchControls();
            cons.setSearchScope(SearchControls.SUBTREE_SCOPE);

            NamingEnumeration num = context.search(basedn, filter, cons);
            int i = 0;
            while (num.hasMore()) {
                i++;
                SearchResult searchResult = (SearchResult) num.next();
                //da ist sn hart kodiert und noch nicht so schoen
                namesVector.add(searchResult.getAttributes().get("sn").toString());

            }
            num.close();
            //namesList.setListData(namesVector);
            detailPanel.removeAll();
        } catch (NamingException e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }


    class OverviewModel
        extends AbstractTableModel {
        private String filter = null;
        private List data = new LinkedList();

        public void setFilter(String filter) {
            this.filter = filter;
            populateModel();
        }

        protected void populateModel() {
            try {
                data.clear();
                SearchControls cons = new SearchControls();
                cons.setSearchScope(SearchControls.SUBTREE_SCOPE);

                NamingEnumeration num = context.search(basedn, filter, cons);
                int i = 0;
                while (num.hasMore()) {
                    i++;
                    SearchResult searchResult = (SearchResult) num.next();
                    data.add(searchResult.getAttributes());
                }
                num.close();
//if (i==0)
                detailPanel.removeAll();
            } catch (NamingException e) {
                System.err.println(e.getMessage());
                e.printStackTrace(System.err);
            }
        }

        public int getRowCount() {
            return data.size();
        }

        public int getColumnCount() {
            return overviewAttributes.length;
        }

        public Class getColumnClass(int columnIndex) {
            return Attribute.class;
        }

        public Object getValueAt(int row, int column) {
            Attributes attributes = (Attributes) data.get(row);

            Attribute attr = attributes.get(overviewAttributes[column]);
            if (attr != null) {
                String aValue = attr.toString();
                int index = aValue.indexOf(":");
                if (index > 0)
                    return (aValue.substring(index + 1));
            }
            return attributes.get(overviewAttributes[column]);
        }

        public Attributes get(int row) {
            return (Attributes) data.get(row);
        }

        public String getColumnName(int column) {
            return overviewLabels[column];
        }
    }


    public String getServletInfo() {
        return "LdapBrowser $Revision$";
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
