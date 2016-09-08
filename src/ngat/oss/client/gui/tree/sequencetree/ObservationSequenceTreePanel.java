/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.tree.sequencetree;

import java.awt.GridLayout;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import ngat.oss.client.Phase2ModelClient;
import ngat.oss.client.gui.listeners.SequenceComponentSelectionListener;
import ngat.phase2.IGroup;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.XBranchComponent;
import ngat.phase2.XExecutiveComponent;
import ngat.phase2.XIteratorComponent;
import ngat.phase2.XIteratorRepeatCountCondition;
import org.apache.log4j.Logger;

/**
 * This class keeps track of two concurrent data models
 * one is the treeModel, with a root of type DefaultMutableTreeNode
 * the other is a ISequenceComponent with a root of type XIteratorComponent
 * insertions and deletions update both models.
 * @author nrc
 */
public class ObservationSequenceTreePanel extends JPanel  implements TreeSelectionListener {

    static Logger logger = Logger.getLogger(ObservationSequenceTreePanel.class);

    //DefaultTreeModel root node
    private DefaultMutableTreeNode rootNode;

    //phase2 model root object of obs sequence
    private XIteratorComponent rootComponent;

    private SequenceComponentSelectionListener componentSelectionListener;

    private DefaultTreeModel treeModel;
    private JTree tree;

    public static final int UP = 1;
    public static final int DOWN = 2;
    private static final String ROOTNAME="Root";

    /**
     * Constructor
     */
    public ObservationSequenceTreePanel(IGroup group, SequenceComponentSelectionListener componentSelectionListener) {
        super(new GridLayout(1,0));

        this.componentSelectionListener = componentSelectionListener;
        
        rootComponent = new XIteratorComponent(ROOTNAME, new XIteratorRepeatCountCondition(1));
        rootNode = new DefaultMutableTreeNode(ROOTNAME);
        treeModel = new DefaultTreeModel(rootNode);
	treeModel.addTreeModelListener(new PhotomSeqTreeModelListener());
        tree = new JTree(treeModel);
        tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        //set the cell renderer to an instance of the ObsSeqTreeRenderer
        tree.setCellRenderer(new ObservationSequenceTreeRenderer());
        tree.addTreeSelectionListener(this);
        populate(group);
                
        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);
    }

    public void populate(IGroup group) {

        if (group == null) {
            return;
        }
        
        Phase2ModelClient phase2ModelClient = Phase2ModelClient.getInstance();
        try {
            rootComponent = (XIteratorComponent) phase2ModelClient.getObservationSequenceOfGroup(group.getID());
            
            //parent node =rootNode
            //root component = rootComponent

            //iterate through root component, taking each sub component and adding it to rootNode

            List childrenList = rootComponent.listChildComponents();
            if (childrenList != null) {
                Iterator childrenIterator = childrenList.iterator();
                while (childrenIterator.hasNext()) {
                    ISequenceComponent childComponent = (ISequenceComponent) childrenIterator.next();
                    addComponent(rootNode, childComponent);
                }
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
            logger.error(ex);
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void addComponent(DefaultMutableTreeNode parent, ISequenceComponent component) {
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(component);
        parent.add(newNode);
        
        if ((component instanceof XBranchComponent) || (component instanceof XIteratorComponent)) {
            List childrenList = component.listChildComponents();
            if (childrenList != null) {
                Iterator childrenIterator = childrenList.iterator();
                while (childrenIterator.hasNext()) {
                    ISequenceComponent childComponent = (ISequenceComponent) childrenIterator.next();
                    addComponent(newNode, childComponent);
                }
            }
        } 
    }

    /**
     * set the embedded tree enabled | disabled
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        tree.setEnabled(enabled);
    }

    /** Remove all nodes except the root node. */
    public void clear() {
        rootNode.removeAllChildren();
        treeModel.reload();
    }

    /** 
     * Add child to the currently selected tree node and underlying ISequenceComponent
     */
    public DefaultMutableTreeNode addSequenceComponent(ISequenceComponent newSequenceComponent)  {

        ISequenceComponent selectedComponent, parentUserComponent;
        DefaultMutableTreeNode selectedNode, parentNode, newNode;

        int insertionIndex;

        selectedNode = getSelectedNode();
        selectedComponent = getUserObjectOfNode(selectedNode);

        newNode = new DefaultMutableTreeNode(newSequenceComponent);
        
        //check the properties of the selectedNode (i.e. can it have children or not)
        //set the parentNode and the insertion index
        if (selectedComponent instanceof XExecutiveComponent) {
            //selected node cannot have children
            //parent node is therefore selected node's parent.
            //insertionIndex is the next index along from selectedNode
            parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
            insertionIndex = parentNode.getIndex(selectedNode) + 1;
        } else {
            //parent node is the selected node, insertionIndex is last location
            parentNode = selectedNode;
            insertionIndex = selectedNode.getChildCount();
        }

        parentUserComponent = getUserObjectOfNode(parentNode);

        //test if insertion is possible
        if ((parentUserComponent instanceof XBranchComponent) && (!(newSequenceComponent instanceof XIteratorComponent))) {
            JOptionPane.showMessageDialog(this, "You can only insert iterators into branch components");
            return null;
        }

        if (parentUserComponent instanceof XBranchComponent) {
            XBranchComponent branchComponent = (XBranchComponent) parentUserComponent;
            if (!branchComponent.canAddMoreChildren()) {
                JOptionPane.showMessageDialog(this, "The branch component is already full.");
                return null;
            }
        }

        //insert newUserObject into parent iterator / branch at position insertionIndex
        if (parentUserComponent instanceof XBranchComponent) {
            XBranchComponent parentAsBranchComponent = (XBranchComponent) parentUserComponent;
            parentAsBranchComponent.addChildComponent(newSequenceComponent);
           
        } else if (parentUserComponent instanceof XIteratorComponent) {
            XIteratorComponent parentAsIteratorComponent = (XIteratorComponent) parentUserComponent;
            parentAsIteratorComponent.addElement(insertionIndex, newSequenceComponent);
             
        } else {
            JOptionPane.showMessageDialog(this, "Cannot add child item to object of type: " + parentUserComponent.getClass().getName());
            return null;
        }

        //do the insertion into the treeModel
        treeModel.insertNodeInto(newNode, parentNode, insertionIndex);

        if (newSequenceComponent instanceof XBranchComponent) {
            //now need to add the underlying  iterators to the tree branch (they've been added to newSequenceComponent already)
            XBranchComponent newSequenceComponentAsBranch = (XBranchComponent) newSequenceComponent;
            List branchComponents = newSequenceComponentAsBranch.listChildComponents();
            XIteratorComponent branchIterator1= (XIteratorComponent) branchComponents.get(0);
            XIteratorComponent branchIterator2= (XIteratorComponent) branchComponents.get(1);

            DefaultMutableTreeNode newBranchIteratorNode1 = new DefaultMutableTreeNode(branchIterator1);
            DefaultMutableTreeNode newBranchIteratorNode2 = new DefaultMutableTreeNode(branchIterator2);

            treeModel.insertNodeInto(newBranchIteratorNode1, newNode, 0);
            treeModel.insertNodeInto(newBranchIteratorNode2, newNode, 1);
        }

        TreePath newNodePath = new TreePath(newNode.getPath());
        tree.scrollPathToVisible(newNodePath);
        tree.setSelectionPath(newNodePath);
        
        return newNode;
    }

    /**
     * Delete node from the currently selected tree node and underlying ISequenceComponent
     * @return whether deletion was sucessful
     * @throws Exception
     */
    public boolean deleteSelectedSequenceComponent() throws Exception {
        ISequenceComponent selectedComponent, parentUserComponent;

        DefaultMutableTreeNode selectedNode;
        TreePath selectedPath = tree.getSelectionPath();

        //set selectedNode to node selected by user
        if (selectedPath == null) {
            //can't move root node
            return false;
        } else {
            selectedNode = (DefaultMutableTreeNode)(selectedPath.getLastPathComponent());
            selectedComponent = getUserObjectOfNode(selectedNode);
        }

        if (selectedNode != null) {
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
            if (parentNode != null) {
                //delete selectedNode from tree model
                treeModel.removeNodeFromParent(selectedNode);

                //delete user object from it's parent
                parentUserComponent = getUserObjectOfNode(parentNode);

                if (parentUserComponent instanceof XIteratorComponent) {
                    XIteratorComponent parentIteratorComponent = (XIteratorComponent) parentUserComponent;
                    parentIteratorComponent.removeElement(selectedComponent);
                } else if (parentUserComponent instanceof XBranchComponent) {
                    XBranchComponent parentBranchComponent = (XBranchComponent) parentUserComponent;
                    parentBranchComponent.removeElement(selectedComponent);
                } else {
                    throw new Exception("cannot delete child object from " + parentUserComponent.getClass().getName());
                }
            }
        }
        return true;
    }

    //return the branch iterator that the currently selected node is in, else return null
    public XIteratorComponent getSelectedBranchIterator() {
        TreePath selectedPath = tree.getSelectionPath();
        DefaultMutableTreeNode selectedNode;
        ISequenceComponent selectedComponent;
        
        if (selectedPath == null) {
            //can't move root node
            return null;
        } else {
            selectedNode = (DefaultMutableTreeNode)(selectedPath.getLastPathComponent());
            selectedComponent = getUserObjectOfNode(selectedNode);
        }
        
        boolean shouldIterate = true;
        TreePath path = selectedPath;
        ISequenceComponent lastComponent = selectedComponent;
        
        //iterate back up the tree until we find a XBranchComponent
        //when we do, then return the component we were at just before that, it should be a XBranchComponent
        
        while (shouldIterate) {
            
            if (path == null) {
                return null;
            }
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)(path.getLastPathComponent());
           
            if (node == null) {
                return null;
            }
            ISequenceComponent component = getUserObjectOfNode(node);
            
            if (component == null) {
                return null;
            }
            if (component instanceof XBranchComponent) {
                
                if (lastComponent instanceof XIteratorComponent) {
                    return (XIteratorComponent) lastComponent;
                } else {
                    return null;
                }
            }
            path = path.getParentPath();
            lastComponent = component;
        }
        
        return null;
    }

    /**
     * @param upDown one of UP | DOWN
     */
    
    public void moveSelectedSequenceComponent(int direction) throws Exception{

        String directionAsString;
        switch(direction) {
            case UP:
                directionAsString = "UP";
                break;
            case DOWN:
                directionAsString = "DOWN";
                break;
            default:
                directionAsString = "UNKNOWN";
                break;
        }
        
        DefaultMutableTreeNode selectedNode,parentNode;
        ISequenceComponent parentComponent, selectedComponent;
        TreePath selectedPath = tree.getSelectionPath();

        //set selectedNode to either root or node selected by user
        if (selectedPath == null) {
            //can't move root node
            return;
        } else {
            selectedNode = (DefaultMutableTreeNode)(selectedPath.getLastPathComponent());
        }

        if (selectedNode != null) {
            //get selected component object
            selectedComponent = getUserObjectOfNode(selectedNode);
            
            //get parent node
            parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
            
            if (parentNode != null) {
                
                int movingNodeIndex = selectedNode.getParent().getIndex(selectedNode);
                int newIndex;
                if (direction == UP) {
                    if (movingNodeIndex > 0) {
                        newIndex = movingNodeIndex - 1;
                    } else {
                        return;
                    }
                } else {
                    if (movingNodeIndex < parentNode.getChildCount() - 1) {
                        newIndex = movingNodeIndex + 1;
                    } else {
                        return;
                    }
                }

                //parent node must be either an iterator or a branch component
                parentComponent = getUserObjectOfNode(parentNode);

                //move the object in the underlying data model
                if (parentComponent instanceof XBranchComponent) {
                    //move the child in the branch
                    XBranchComponent parentAsBranchComponent = (XBranchComponent) parentComponent;
                    boolean moved = parentAsBranchComponent.moveElement(selectedComponent, newIndex);
                    
                } else if (parentComponent instanceof XIteratorComponent) {
                    //move the child in the iterator
                    XIteratorComponent parentAsIteratorComponent = (XIteratorComponent) parentComponent;
                    boolean moved = parentAsIteratorComponent.moveElement(selectedComponent, newIndex);
                } else {
                    throw new Exception("cannot move objects within parents of type " + parentComponent.getClass().getName());
                }

                //move the node
                treeModel.removeNodeFromParent(selectedNode);
                treeModel.insertNodeInto(selectedNode, parentNode, newIndex);
                treeModel.nodeChanged(selectedNode);

                //select the node
                TreePath treePath = new TreePath(selectedNode.getPath());

                tree.setSelectionPath(treePath);


                tree.scrollPathToVisible(treePath);
            }
        }
    }

    public void replaceSelectedSequenceComponent(ISequenceComponent newSequenceComponent) {

        DefaultMutableTreeNode selectedNode, parentNode, newNode;
        int insertionIndex;

        selectedNode = getSelectedNode();

        //check the properties of the selectedNode (i.e. can it have children or not)
        //set the parentNode for the object to be inserted into the tree.

        if (selectedNode.getUserObject() instanceof XExecutiveComponent) {
            //replacing an XExecutiveComponent

            //selected node cannot have children
            parentNode = (DefaultMutableTreeNode) selectedNode.getParent();

            insertionIndex = parentNode.getIndex(selectedNode);

            //TREE MODEL OPERATIONS

            //delete the selected node from the tree
            treeModel.removeNodeFromParent(selectedNode);

            //insert the new node into the tree
            newNode = new DefaultMutableTreeNode(newSequenceComponent);
            treeModel.insertNodeInto(newNode, parentNode, insertionIndex);

             //make sure the user can see the new node.
            tree.scrollPathToVisible(new TreePath(newNode.getPath()));

            //OBJECT MODEL OPERATIONS

            XExecutiveComponent selectedObjectAsExecutiveComponent = (XExecutiveComponent) getUserObjectOfNode(selectedNode);
            XIteratorComponent parentIteratorComponent = (XIteratorComponent) getUserObjectOfNode(parentNode); //assumes parent is iterator

            parentIteratorComponent.removeElement(selectedObjectAsExecutiveComponent);
            parentIteratorComponent.addElement(insertionIndex, newSequenceComponent);
           

        } else if (selectedNode.getUserObject() instanceof XIteratorComponent) {
            //we're replacing an XIteratorComponent, the parent must be root or another iterator (the gui will not allow editing of iterators in a branch)

            //TREE MODEL OPERATIONS

            //selected node has children
            parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
            insertionIndex = parentNode.getIndex(selectedNode);

            //save off the children of the selected node
            ArrayList savedChildren = new ArrayList();
            int childCount = selectedNode.getChildCount();

            for (int c=0; c < childCount; c++) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) selectedNode.getChildAt(c);
                savedChildren.add(childNode);
            }
            //delete the selected node
            treeModel.removeNodeFromParent(selectedNode);
            //insert the new node
            newNode = new DefaultMutableTreeNode(newSequenceComponent);
            //add the children back onto the new node
            Iterator i = savedChildren.iterator();
            while (i.hasNext()) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) i.next();
                newNode.add(childNode);
            }

            treeModel.insertNodeInto(newNode, parentNode, insertionIndex);
            //make sure the user can see the new node.
            tree.scrollPathToVisible(new TreePath(newNode.getPath()));

            //OBJECT MODEL OPERATIONS

            XIteratorComponent selectedObjectAsIteratorComponent = (XIteratorComponent) getUserObjectOfNode(selectedNode);
            List selectedIteratorComponentChildren = selectedObjectAsIteratorComponent.listChildComponents();

            XIteratorComponent parentIteratorComponent = (XIteratorComponent) getUserObjectOfNode(parentNode); //assumes parent is iterator

            parentIteratorComponent.removeElement(selectedObjectAsIteratorComponent);

            XIteratorComponent newIteratorComponent = (XIteratorComponent) newSequenceComponent;
            newIteratorComponent.setSequence(selectedIteratorComponentChildren);
            parentIteratorComponent.addElement(insertionIndex, newIteratorComponent);
            
        } else {
            JOptionPane.showMessageDialog(this, "Unable to perform replace operation on object of type: " + selectedNode.getUserObject().getClass().getName());
        }
    }


    /**
     * returns gui selected node, or root node if nothing selected
     */
    public DefaultMutableTreeNode getSelectedNode() {
        DefaultMutableTreeNode selectedNode;

        TreePath selectedPath = tree.getSelectionPath();

        //set selectedNode to either root or node selected by user
        if (selectedPath == null) {
            selectedNode = rootNode;
        } else {
            //parentNode is the node selected by the user
            selectedNode = (DefaultMutableTreeNode)(selectedPath.getLastPathComponent());
        }
        return selectedNode;
    }


    //get the user object associated with the tree node
    private ISequenceComponent getUserObjectOfNode(DefaultMutableTreeNode node) {

        if (node == rootNode) {
            return rootComponent;
        }

        ISequenceComponent userObject = (ISequenceComponent) node.getUserObject();
        return userObject;
    }

    //very simple, just return the rootComponent
    public ISequenceComponent getObservationSequence() throws Exception {
        return rootComponent;
    }

    //invoked if tree selection changes
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {

        DefaultMutableTreeNode selectedNode;
        ISequenceComponent selectedComponent;

        TreePath treePath = treeSelectionEvent.getPath();

        //set selectedNode to node selected by user
        if (treePath == null) {
            //can't move root node
            return;
        } else {
            selectedNode = (DefaultMutableTreeNode)(treePath.getLastPathComponent());
            selectedComponent = getUserObjectOfNode(selectedNode);
            
            //send the selected component to the componentSelectionListener (likely to be PhotomSeqEditorPanel instance)
            componentSelectionListener.receiveComponentSelected(selectedComponent);
        }
    }

    // If expand is true, expands all nodes in the tree.
    // Otherwise, collapses all nodes in the tree.
    public void expandAll(boolean expand) {
        TreeNode root = (TreeNode)tree.getModel().getRoot();

        // Traverse tree from root
        expandAll(new TreePath(root), expand);
    }

    private void expandAll(TreePath parent, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode)parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e=node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode)e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(path, expand);
            }
        }

        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }






    class PhotomSeqTreeModelListener implements TreeModelListener {
        public void treeNodesChanged(TreeModelEvent e) {
            DefaultMutableTreeNode node;
            node = (DefaultMutableTreeNode)(e.getTreePath().getLastPathComponent());

            /*
             * If the event lists children, then the changed
             * node is the child of the node we've already
             * gotten.  Otherwise, the changed node and the
             * specified node are the same.
             */

                int index = e.getChildIndices()[0];
                node = (DefaultMutableTreeNode)(node.getChildAt(index));
        }
        public void treeNodesInserted(TreeModelEvent e) {
        }
        public void treeNodesRemoved(TreeModelEvent e) {
        }
        public void treeStructureChanged(TreeModelEvent e) {
        }
    }
}
