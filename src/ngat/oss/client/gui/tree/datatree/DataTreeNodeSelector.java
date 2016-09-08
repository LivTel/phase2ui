package ngat.oss.client.gui.tree.datatree;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import ngat.phase2.IPhase2Identity;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */

public class DataTreeNodeSelector {

    static Logger logger = Logger.getLogger(DataTreeNodeSelector.class);

    private JTree tree;
    private volatile boolean foundNode = false;
    private DefaultMutableTreeNode rootNode;

    public DataTreeNodeSelector(DefaultMutableTreeNode startNode, JTree tree) {
        logger.info("DataTreeNodeSelector(" + startNode + ", " + tree + ")");
        
        this.tree = tree;
        this.rootNode = startNode;
    }

    public void selectNodeOfUserObject(IPhase2Identity userObject) {
        maybeSelectNode(new TreePath(rootNode), userObject);

        tree.scrollPathToVisible(new TreePath(rootNode));
    }

    private void maybeSelectNode(TreePath path, IPhase2Identity userObject) {
        //System.err.println("maybeSelectNode(" + userObject + ")");

        if (foundNode) {
            return;
        }

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

        if (node == null) {
            return;
        }
        
        Object foundUserObject = node.getUserObject();
        if (foundUserObject == null) {
            return;
        }
        
        //System.err.println("... foundUserObject=" + foundUserObject);

        if (foundUserObject.equals(userObject)) {
            //System.err.println("... foundUserObject.equals(phase2Object)");
            selectNodeInTree(node);
            foundNode = true;
            return;
        }

        if (!(foundUserObject instanceof String)) {
            //System.err.println("... !String");
            if (foundUserObject.getClass().equals(userObject.getClass())) {
                //System.err.println("... same class");
                 IPhase2Identity foundPhase2Object = (IPhase2Identity) foundUserObject;
                if (foundPhase2Object.getID() == userObject.getID()) {
                    //System.err.println("... same id");
                    //System.err.println("... foundNode");
                    selectNodeInTree(node);
                    foundNode = true;
                    return;
                }
            }
        } 

        if (!foundNode) {
            //System.err.println("... !foundNode, going through child nodes");
            if (node.getChildCount() >= 0) {
                int childCount = node.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
                    TreePath newPath = path.pathByAddingChild(childNode);
                    maybeSelectNode(newPath, userObject);
                }
            }
        }
    }

    private void selectNodeInTree(DefaultMutableTreeNode node) {

        //System.err.println(this.getClass().getName() + ".selectNodeInTree(" + node + ")");

        final TreePath foundNodeTreePath = new TreePath(node.getPath());

        //Swing operation
        tree.setSelectionPath(foundNodeTreePath);
        //tree.expandPath(foundNodeTreePath);
        //tree.scrollPathToVisible(foundNodeTreePath);

        DefaultTreeModel defaultTreeModel = (DefaultTreeModel) tree.getModel();
        tree.repaint();
        tree.treeDidChange();

    }
}
