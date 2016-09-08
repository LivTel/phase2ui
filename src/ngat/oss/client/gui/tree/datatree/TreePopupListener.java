/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ngat.oss.client.gui.tree.datatree;

import ngat.oss.client.gui.tree.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import ngat.oss.client.gui.reference.CONST;
import ngat.oss.client.gui.reference.Session;
import ngat.phase2.IAccessPermission;
import ngat.phase2.IGroup;
import ngat.phase2.IProgram;
import ngat.phase2.IProposal;
import ngat.phase2.ITag;
import ngat.phase2.IUser;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class TreePopupListener extends MouseAdapter {

    private JTree tree;
    static Logger logger = Logger.getLogger(TreePopupListener.class);
            
    public TreePopupListener(JTree tree) {
        this.tree = tree;
    }

    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {

        if (e.isPopupTrigger()) {

            int row = tree.getRowForLocation(e.getX(), e.getY());
            TreePath path = tree.getClosestPathForLocation(e.getX(), e.getY());

            Object selectedDataNode = path.getLastPathComponent();

            tree.setSelectionPath(path);

            if (selectedDataNode == null) {
                return;
            }

            JPopupMenu popupMenu;

            if (selectedDataNode instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) selectedDataNode;
                Object userObject = defaultMutableTreeNode.getUserObject();
                if (userObject instanceof String) {
                    if (userObject.equals(CONST.USERS_TREE_ROOT_NAME)) {
                        //only su gets this menu
                        popupMenu = DataTreePopupMenuFactory.getUsersRootPopupMenu();
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    } else if (userObject.equals(CONST.PROGRAMMES_TREE_ROOT_NAME)) {
                        //only SU can add new programmes
                        if (Session.getInstance().getUser().isSuperUser()) {
                            popupMenu = DataTreePopupMenuFactory.getProgrammesRootPopupMenu();
                            popupMenu.show(e.getComponent(), e.getX(), e.getY());
                        }
                    } else if (userObject.equals(CONST.TAGS_TREE_ROOT_NAME)) {
                        //only su gets this menu
                        popupMenu = DataTreePopupMenuFactory.getTagsRootPopupMenu();
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                } else {
                    if (userObject instanceof IUser) {
                        //only SU can add access permissions for users.
                        if (Session.getInstance().getUser().isSuperUser()) {
                            popupMenu = DataTreePopupMenuFactory.getUserPopupMenu((IUser) userObject);
                            popupMenu.show(e.getComponent(), e.getX(), e.getY());
                        }
                    } else if (userObject instanceof IAccessPermission) {
                        //no pop-up on access permission
                    } else if (userObject instanceof IProposal) {
                        popupMenu = DataTreePopupMenuFactory.getProposalPopupMenu((IProposal) userObject);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    } else if (userObject instanceof IGroup) {
                        popupMenu = DataTreePopupMenuFactory.getGroupPopupMenu((IGroup) userObject);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    } else if (userObject instanceof IProgram) {
                        //only SU can add proposals
                        if (Session.getInstance().getUser().isSuperUser()) {
                            popupMenu = DataTreePopupMenuFactory.getProgrammePopupMenu((IProgram) userObject);
                            popupMenu.show(e.getComponent(), e.getX(), e.getY());
                        }
                    } else if (userObject instanceof ITag) {
                        //only su gets this menu
                        popupMenu = DataTreePopupMenuFactory.getTagPopupMenu((ITag) userObject);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                    //System.err.println(this.getClass().getName() + " userObject=" + userObject.getClass().getName());
                }
            } else {
                logger.info("selectedDataNode=" + selectedDataNode.getClass().getName() + " NOT CREATING POPUP");
            }

        }
    }
}
