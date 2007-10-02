/**
 */
package ide;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.wings.SComponent;
import org.wings.SContainer;
import org.wings.SIcon;
import org.wings.SImageIcon;
import org.wings.SScrollPane;
import org.wings.STree;
import org.wings.event.SContainerEvent;
import org.wings.event.SContainerListener;
import org.wings.tree.SDefaultTreeCellRenderer;


/**
 * Panel which encapsulates a Containment hierarchy in a scrollpane.
 * This tree only displays visual beans.
 */
public class TreePanel extends SScrollPane {

    private STree tree;
    private ContainerTreeModel treeModel;
    private Logger logger = Logger.getLogger("ide.TreePanel");

    /**
     * Creates a tree panel with the tree model
     */
    public TreePanel()  {
        tree = new STree();
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        add(tree);
    }

    /**
     * Sets the root of the tree.
     * @param root Root of the tree node.
     */
    public void setRoot(Object root)  {
        if (root instanceof SContainer)  {
	    SContainer container = (SContainer)root;

	    treeModel = new ContainerTreeModel(container);
            tree.setModel(treeModel);
            tree.setCellRenderer(new BeanCellRenderer());

            // Expand all nodes
            int row = 0;
            while(row < tree.getRowCount())  {
                tree.expandRow(row);
                row++;
            }
        }
	else
	    logger.warning("not an instance of SContainer " + root);
    }
    /**
     * Updates the tree selection. Currently supports visual hierarchy.
     */
    public void setSelectedItem(Object item) {
        if (item instanceof SComponent)  {
            Object[] path = treeModel.getPathToRoot((SComponent)item);
            tree.setSelectionPath(new TreePath(path));
        }
    }

    /**
     * Returns the tree (for listener registration).
     */
    public STree getTree()  {
        return tree;
    }

    /**
     * Returns the tree model
     */
    public ContainerTreeModel getModel()  {
        return treeModel;
    }


    /**
     * Renders a node in the tree.
     */
    public class BeanCellRenderer
	extends SDefaultTreeCellRenderer
    {
        // Caches the icons from the BeanInfo on a per type basis.
        Hashtable icons = new Hashtable();

        /**
         * This is called from STree whenever it needs to get the size
         * of the component or it wants to draw it.
         */
        public SComponent getTreeCellRendererComponent(STree tree, Object value,
						      boolean sel,
                                                    boolean expanded,
                                                    boolean leaf, int row,
                                                    boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded,
                                        leaf, row, hasFocus );

            if (value instanceof SComponent)  {
                Class cls = value.getClass();
                SIcon icon = (SIcon)icons.get(cls);
                if (icon == null)  {
		    ImageIcon image = (ImageIcon)BeanInfoFactory.getIcon(cls);
                    if (image != null) {
			icon = new SImageIcon(image);
                        icons.put(cls, icon);
                    }
		}

		this.setIcon(icon);
                this.setText(cls.getName());
            }
            return this;
        }
        
    }

    /**
     * A custom tree model which is based on the containment hiearchy.
     * A container is added as the root of the tree model. 
     * This model listens to the root container for SComponents added or removed.
     * <p>
     * This model class uses BeanInfo to retrieve custom attributes
     * and the Icon.
     */
    public class ContainerTreeModel extends TreeModelSupport implements SContainerListener  {
        private SContainer root;

        public ContainerTreeModel(SContainer root)  {
            setRoot(root);
        }

	public void setRoot(Object root) {
	    if (root instanceof SContainer) {
		this.root = (SContainer)root;
	    }
	}

	public Object getRoot() {
	    return root;
	}

        public Object getChild(Object parent, int index)  {
            if (parent instanceof SContainer)  {
                SContainer c = (SContainer)parent;
                return c.getComponent(index);
            }
            return null;
        }

        public int getChildCount(Object parent)  {
            if (parent instanceof SContainer)  {
                SContainer c = (SContainer)parent;
                return c.getComponentCount();
            }
            return 0;
        }

        public int getIndexOfChild(Object parent, Object child)  {
            if (parent instanceof SContainer)  {
                SContainer c = (SContainer)parent;

                SComponent[] comps = c.getComponents();
                for (int i = 0; i < comps.length; i++) {
                    if (comps[i] == child)  {
                        return i;
                    }
                }
            }
            return -1;
        }

        /**
         * Determines if this component is a container by looking at the
         * "isContainer" BeanInfo attribute.
         */
        public boolean isContainer(Object node)  {
            BeanInfo info = BeanInfoFactory.getBeanInfo(node.getClass());

            if (info != null)  {
                BeanDescriptor desc = info.getBeanDescriptor();
                Boolean flag = (Boolean)desc.getValue("isContainer");
                if (flag != null)  {
                    return flag.booleanValue();
                }
            }

            // The isContainer attribute is not found.
            if (node instanceof SContainer)
                return true;
            else
                return false;
        }


        /**
         * Tests to see if the node is a leaf.
         */
        public boolean isLeaf(Object node)  {
            if (isContainer(node))  {
                SContainer container = (SContainer)node;
                if (container.getComponentCount() == 0)
                    return true;
                else
                    return false;
            }

            return true;
        }

        /**
         * Builds the parents of a component up to and including the root node.
         * @see javax.swing.tree.DefaultTreeModel#getPathToRoot
         */
        public Object[] getPathToRoot(SComponent comp)  {
            return getPathToRoot(comp, 0);
        }

        /**
         * Builds the parents of a component up to and including the root node.
         * @see javax.swing.tree.DefaultTreeModel#getPathToRoot
         */
        protected Object[] getPathToRoot(SComponent comp, int depth)  {
            Object[] retObjs;

            if (comp == null)  {
                if (depth == 0)
                    return null;
                else
                    retObjs = new Object[depth];
            } else {
                depth++;
                if (comp == getRoot())
                    retObjs = new Object[depth];
                else
                    retObjs = getPathToRoot(comp.getParent(), depth);
                retObjs[retObjs.length - depth] = comp;
            }
            return retObjs;
        }

        //
        // ContainerListener methods.
        //

        public void componentAdded(SContainerEvent evt)  {
            SContainer container = evt.getContainer();
            SComponent comp = evt.getChild();
            int[] indexes = new int[] { getIndexOfChild(container, comp) };

            fireTreeNodesInserted(this, getPathToRoot(container), 
				  indexes, new Object[]{ comp });
        }

        public void componentRemoved(SContainerEvent evt)  {
            SContainer container = evt.getContainer();
            SComponent comp = evt.getChild();

            Object[] parentPath = getPathToRoot(evt.getContainer());

            fireTreeNodesRemoved(new TreeModelEvent(this, parentPath));
        }

        public void valueForPathChanged(TreePath path, Object newValue)  { }
    }
}
