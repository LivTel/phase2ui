/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ngat.oss.client.gui.tree.datatree;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

/**
 *
 * @author nrc
 */
public class DataTreeWillExpandListener implements TreeWillExpandListener {

    JTree tree;
    DefaultTreeModel treeModel;

    public DataTreeWillExpandListener(JTree tree, DefaultTreeModel treeModel) {
        this.tree = tree;
        this.treeModel = treeModel;
    }
    
    //Required by TreeWillExpandListener interface.
    //to cancel the tree expansion: throw new ExpandVetoException(e)
    public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
        DefaultMutableTreeNode parentNode = null;
        TreePath parentPath = e.getPath();

        parentNode = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
        parentNode.removeAllChildren();

        DataTreeChildrenLoader dataTreeChildrenLoader_NEW = new DataTreeChildrenLoader(tree);
        dataTreeChildrenLoader_NEW.loadChildren(parentNode);

        treeModel.nodeStructureChanged(parentNode); //will do weird stuff?

        //expand the node
        //tree.expandPath(parentPath); //don't need to throw? NO.
    }

    //Required by TreeWillExpandListener interface.
    public void treeWillCollapse(TreeExpansionEvent e) {

    }
}
