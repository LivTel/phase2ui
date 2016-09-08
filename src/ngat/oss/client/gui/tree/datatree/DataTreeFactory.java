/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ngat.oss.client.gui.tree.datatree;

import ngat.oss.client.gui.tree.*;
import java.awt.event.MouseListener;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import ngat.oss.client.gui.listeners.ControlPanelSelector;
import ngat.oss.client.gui.reference.CONST;
import ngat.oss.client.gui.reference.Session;
import ngat.phase2.IUser;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class DataTreeFactory {

    static Logger logger = Logger.getLogger(DataTreeFactory.class);

    public static final int ACCESS_PERMISSION_TYPE_USER_TREE = 1;
    public static final int PROGRAMMES_TYPE_USER_TREE = 2;
    //private static AccessModelClient accessModelClient = AccessModelClient.getInstance();
    //private static Phase2ModelClient phase2ModelClient = Phase2ModelClient.getInstance();

    /** public **/
    public static JTree getAllUsersTree() {

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(CONST.USERS_TREE_ROOT_NAME);
        return getTree(rootNode);
    }

    public static JTree getAllProgrammesTree() {

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(CONST.PROGRAMMES_TREE_ROOT_NAME);
        return getTree(rootNode);
    }

    public static JTree getAllTagsTree() {

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(CONST.TAGS_TREE_ROOT_NAME);
        return getTree(rootNode);
    }


    public static JTree getUserTree(int treeType) {

        //System.err.println("getUserTree(" + treeType + "," + includeInactiveGroups + ")");
        
        DefaultMutableTreeNode rootNode = null;
        IUser user = Session.getInstance().getUser();

        try {
            switch (treeType) {
                case DataTreeFactory.ACCESS_PERMISSION_TYPE_USER_TREE:
                    rootNode = new DefaultMutableTreeNode(user.getName() + " " +CONST.USER_ACCESS_PERMISSIONS_POSTFIX);
                    break;
                case DataTreeFactory.PROGRAMMES_TYPE_USER_TREE:
                    rootNode = new DefaultMutableTreeNode(user.getName() + " " + CONST.USER_PROGRAMMES_POSTFIX);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            return null;
        }
        return getTree(rootNode);
    }


    /** private **/
    /*
    private static JTree getTree_old(DefaultMutableTreeNode rootNode) {
        DefaultTreeModel updateableTreeModel = new DefaultTreeModel(rootNode);
        JTree tree = new JTree(updateableTreeModel);
        tree.setEditable(false);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new DataTreeRenderer());
        tree.addTreeSelectionListener(new ControlPanelSelector(tree));
        tree.addTreeSelectionListener(new DataTreeSelectionListener(tree));
        //tree.addTreeWillExpandListener(new DataTreeWillExpandListener(tree, includeInactiveGroups));
        MouseListener popupListener = new TreePopupListener(tree);
            tree.addMouseListener(popupListener);
        MouseListener doubleClickListener = new DataTreeDoubleClickListener(tree);
            tree.addMouseListener(doubleClickListener);

        //load the children of the root node
        DataTreeChildrenLoader dataTreeChildrenLoader = new DataTreeChildrenLoader(tree);
        dataTreeChildrenLoader.loadChildren(rootNode);
        updateableTreeModel.nodeStructureChanged(rootNode);
        return tree;
    }
    */

    private static JTree getTree(DefaultMutableTreeNode rootNode) {
        DefaultTreeModel updateableTreeModel = new DefaultTreeModel(rootNode);
        JTree tree = new JTree(updateableTreeModel);
        tree.setEditable(false);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new DataTreeRenderer());
        tree.addTreeSelectionListener(new ControlPanelSelector(tree));
        tree.addTreeSelectionListener(new DataTreeSelectionListener(tree));
        DataTreeWillExpandListener dataTreeWillExpandListener = new DataTreeWillExpandListener(tree, updateableTreeModel);
        tree.addTreeWillExpandListener(dataTreeWillExpandListener);
        //tree.addTreeWillExpandListener(new DataTreeWillExpandListener(tree, includeInactiveGroups));
        MouseListener popupListener = new TreePopupListener(tree);
            tree.addMouseListener(popupListener);
        //MouseListener doubleClickListener = new DataTreeDoubleClickListener(tree);
        //  tree.addMouseListener(doubleClickListener);

        //load the children of the root node
        DataTreeChildrenLoader dataTreeChildrenLoader_NEW = new DataTreeChildrenLoader(tree);
        dataTreeChildrenLoader_NEW.loadChildren(rootNode);
        updateableTreeModel.nodeStructureChanged(rootNode);
        return tree;
    }


}
