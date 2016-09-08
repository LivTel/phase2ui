/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.tree.datatree;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import ngat.oss.client.AccessModelClient;
import ngat.oss.client.Phase2ModelClient;
import ngat.oss.client.gui.frame.MainFrame;
import ngat.oss.client.gui.reference.CONST;
import ngat.oss.client.gui.reference.Session;
import ngat.oss.client.gui.wrapper.AccessPermissionWrapper;
import ngat.oss.exception.Phase2Exception;
import ngat.phase2.IAccessPermission;
import ngat.phase2.IGroup;
import ngat.phase2.IProgram;
import ngat.phase2.IProposal;
import ngat.phase2.ITag;
import ngat.phase2.IUser;
import ngat.phase2.XGroup;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class DataTreeChildrenLoader {

    static Logger logger = Logger.getLogger(DataTreeChildrenLoader.class);

    AccessModelClient accessModelClient = AccessModelClient.getInstance();
    Phase2ModelClient phase2ModelClient = Phase2ModelClient.getInstance();
    
    private JTree tree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    
    public DataTreeChildrenLoader(JTree tree) {
        this.tree = tree;
        this.treeModel = (DefaultTreeModel) tree.getModel();
        this.rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
    }


    public void loadNodeChildren(DefaultMutableTreeNode parentNode) {

        //System.err.println("loadNodeChildren(parentNode=" + parentNode + ")");

        logger.info("loadNodeChildren( " + parentNode + ")");

        //store the paths to all the nodes that are currently expanded
        Vector<String> expandedPathsList = getExpandedPathsList();
        
        //clear out the children of the parent
        parentNode.removeAllChildren();

        loadChildrenOfMaybeExpandedNode(parentNode, expandedPathsList);

        treeModel.nodeStructureChanged(parentNode);
        
        expandNodeThatHasLoadedChildren(parentNode, expandedPathsList);
    }

    /**
     * Takes list of nodes that were expanded earlier and recursively loads their children
     * @param node
     * @param expandedPathList
     */
    private void loadChildrenOfMaybeExpandedNode(DefaultMutableTreeNode node, Vector<String> expandedPathList) {
        //logger.info("loadChildrenOfMaybeExpandedNode(" + node + "," + expandedPathList+ ")");

        TreePath nodeAsPath = new TreePath(node.getPath());
        String nodePathString = nodeAsPath.toString();

        if (expandedPathList.contains(nodePathString)) {
            loadChildren(node);
            for (int cc = 0; cc < node.getChildCount(); cc++) {
                DefaultMutableTreeNode childNode =  (DefaultMutableTreeNode) node.getChildAt(cc);
                loadChildrenOfMaybeExpandedNode(childNode, expandedPathList);
            }
        } 
    }

    /**
     * Takes list of nodes that were expanded earlier and if they should be expanded, then expands them in the tree
     * @param node
     * @param expandedPathList
     */
    private void expandNodeThatHasLoadedChildren(DefaultMutableTreeNode node, Vector<String> expandedPathList) {
        //logger.info("expandNodeThatHasLoadedChildren(" + node + "," + expandedPathList+ ")");

        TreePath nodeAsPath = new TreePath(node.getPath());
        String nodePathString = nodeAsPath.toString();

        if (expandedPathList.contains(nodePathString)) {
            for (int cc = 0; cc < node.getChildCount(); cc++) {
                DefaultMutableTreeNode childNode =  (DefaultMutableTreeNode) node.getChildAt(cc);
                expandNodeThatHasLoadedChildren(childNode, expandedPathList);
            }
            tree.expandPath(nodeAsPath);
        } 
    }

    public void loadChildren(DefaultMutableTreeNode node) {

        Object object = node.getUserObject();
        logger.info("loadChildren(" + node + "), object = " + object.getClass().getName());
        
        if (object instanceof IUser) {
            loadUserAccessPermissionChildren((IUser)object, node);
        } else if (object instanceof AccessPermissionWrapper) {
            AccessPermissionWrapper accessPermissionWrapper = (AccessPermissionWrapper) object;
            loadAccessPermissionChildren(accessPermissionWrapper.getAccessPermission(), node);
        } else if (object instanceof IProposal) {
            loadProposalChildren((IProposal)object, MainFrame.getInstance().includeDisabledGroupsIsTicked(), node);
        } else if (object instanceof IProgram) {
            loadProgramChildren((IProgram)object, node);
        } else if (object instanceof ITag) {
            loadTagChildren((ITag)object, node);
        } else if (object instanceof String) {
            try {
                loadRootNodeChildren((String) object, node);
            } catch (Phase2Exception ex) {
                ex.printStackTrace();
                logger.error(ex);
            }
        } 
    }

    private Vector<String> getExpandedPathsList() {
        logger.info("getExpandedPathsList()");
        
        //store the expanded nodes paths
        Vector<String> expandedPaths = new Vector();
        boolean hadExpandedPaths = false;
        //System.err.println("... expanded paths are :");
        for (int row = 0; row< tree.getRowCount(); row++) {
            if (tree.isExpanded(row)) {
                TreePath treePathExpanded = tree.getPathForRow(row);
                //System.err.println("adding expanded path: " + treePathExpanded);
                String treePathAsString = treePathExpanded.toString();
                expandedPaths.add(treePathAsString);
                //System.err.println("... ... " + treePathExpanded);
                hadExpandedPaths = true;
            }
        }
        if (!hadExpandedPaths) {
            //System.err.println("... ... NONE");
        }
        return expandedPaths;
    }


    private void expandPaths(Vector<TreePath> expandedPathsList) {
        logger.info("expandPaths(" + expandedPathsList + ")");

        Iterator eri = expandedPathsList.iterator();
        
        //store the tree path list as a list of strings
        Vector<String> expandedPathsListAsStrings = new Vector<String>();
        while (eri.hasNext()) {
            TreePath treePathToExpand = (TreePath)eri.next();
            String treePathAsString = treePathToExpand.toString();
            //System.err.println("expandedPathsListAsStrings.add(" + treePathAsString + ")");
            expandedPathsListAsStrings.add(treePathAsString);
        }

        //iterate through the rows of the tree and get the string of the tree path of that row
        //if it is the same as one of the treePaths stored already, expand it
        for (int row = 0; row< tree.getRowCount(); row++) {
            final TreePath treePath = tree.getPathForRow(row);
            String treePathAsString = treePath.toString();
            //System.err.println("... found path in tree: " + treePathAsString);
            if (expandedPathsListAsStrings.contains(treePathAsString)) {
                //System.err.println("... ... path is in expanded paths list");
                //EventQueue.invokeLater(new Runnable() {
                //    public void run() {
                        //System.err.println("... ... path is collapsed = " + tree.isCollapsed(treePath));
                        //System.err.println("... ... expanding path");

                        //loadUserChildren here

                        //get children of the node with path treePath

                        tree.expandPath(treePath);
                        //System.err.println("... ... expanded path");
                        //System.err.println("... ... tree.isExpanded(treePath)=" + tree.isExpanded(treePath));
                        
                //    }
                //});
            } else {
                //System.err.println("... ... path is NOT in expanded paths list");
            }
        }
    }

    private void loadRootNodeChildren(String userObject, DefaultMutableTreeNode parentNode) throws Phase2Exception {

        logger.info("loadRootNodeChildren(" + userObject + ", " + parentNode + ")");

        if (userObject.equals(CONST.USERS_TREE_ROOT_NAME)) {
            loadUsersRootNode(parentNode);
        } else if (userObject.equals(CONST.PROGRAMMES_TREE_ROOT_NAME)) {
            loadProgrammesRootNode(parentNode);
        } else if (userObject.equals(CONST.TAGS_TREE_ROOT_NAME)) {
            loadTagsRootNode(parentNode);
        } else {
            //test for 'user' root nodes
            if (userObject.indexOf(CONST.USER_ACCESS_PERMISSIONS_POSTFIX) >0) {
                //we have a user root node that requires access permission children
                loadUserAccessPermissionChildren(Session.getInstance().getUser(), parentNode);
            } else if (userObject.indexOf(CONST.USER_PROGRAMMES_POSTFIX) >0) {
                //we have a user root node that requires program children
                loadUserProgrammeChildren(Session.getInstance().getUser(), parentNode);
            } else {
                try {
                    throw new Exception("loadNodeChildren on unrecognised object: " + userObject + " (" + userObject.getClass().getName() + ") FAILED, parentNode=" + parentNode);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    logger.error(ex);
                }
            }
        }
    }

    private void loadUsersRootNode(DefaultMutableTreeNode parentNode) throws Phase2Exception {
        logger.info("loadUsersRootNode(" + parentNode + ")");

        List childrenList = accessModelClient.listUsers();

        Iterator cli = childrenList.iterator();
        while (cli.hasNext()) {
            IUser user = (IUser) cli.next();
            addObject(parentNode, user, true);
        }
    }

    private void loadProgrammesRootNode(DefaultMutableTreeNode parentNode) throws Phase2Exception {
        logger.info("loadProgrammesRootNode(" + parentNode + ")");

        List childrenList = phase2ModelClient.listProgrammes();

        Iterator cli = childrenList.iterator();
        while (cli.hasNext()) {
            IProgram program = (IProgram) cli.next();
            addObject(parentNode, program, true);
        }
    }

    private void loadTagsRootNode(DefaultMutableTreeNode parentNode) throws Phase2Exception {
        logger.info("loadTagsRootNode(" + parentNode + ")");

        List childrenList = phase2ModelClient.listTags();

        Iterator cli = childrenList.iterator();
        while (cli.hasNext()) {
            ITag tag = (ITag) cli.next();
            addObject(parentNode, tag, true);
        }
    }

    private void loadTagChildren(ITag tag, DefaultMutableTreeNode parentNode) {
        logger.info("loadTagChildren(" + tag + "," + parentNode);

        List childrenList;
        try {
            parentNode.removeAllChildren();
            childrenList = phase2ModelClient.listProposalsOfTag(tag.getID());

            Iterator cli = childrenList.iterator();
            while (cli.hasNext()) {
                IProposal proposal = (IProposal) cli.next();
                addObject(parentNode, proposal, true);
            }
        } catch (Phase2Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            return;
        }
    }

    private void loadProgramChildren(IProgram program, DefaultMutableTreeNode parentNode) {
        logger.info("loadProgramChildren(" + program + "," + parentNode);

        IUser currentUser = Session.getInstance().getUser();
        List childrenList;
        try {
            parentNode.removeAllChildren();
            childrenList = phase2ModelClient.listProposalsOfProgramme(program.getID());

            Iterator cli = childrenList.iterator();
            while (cli.hasNext()) {
                IProposal proposal = (IProposal) cli.next();

                if (currentUser.isSuperUser()) {
                    //current user is super user - show all proposals
                    addObject(parentNode, proposal, true);
                } else {
                    List accessPermissionsOnProposal = accessModelClient.listAccessPermissionsOnProposal(proposal.getID());
                    Iterator apopi = accessPermissionsOnProposal.iterator();
                    while (apopi.hasNext()) {
                        IAccessPermission accessPermission = (IAccessPermission) apopi.next();
                        if (accessPermission.getUserID() == currentUser.getID()) {
                            //only add the proposal if the current user is its PI
                            addObject(parentNode, proposal, true);
                        }
                    }
                }
            }
        } catch (Phase2Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            return;
        }
    }

    private void loadProposalChildren(IProposal proposal, boolean includeInactiveGroups, DefaultMutableTreeNode parentNode) {
        logger.info("loadProposalChildren(" + proposal + "," + parentNode);

        List childrenList;
        try {
            parentNode.removeAllChildren();
            childrenList = phase2ModelClient.listGroups(proposal.getID(), includeInactiveGroups);

            Iterator cli = childrenList.iterator();
            while (cli.hasNext()) {
                IGroup group = (IGroup) cli.next();
                addObject(parentNode, group, true);
            }
        } catch (Phase2Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            return;
        }
    }

    private void loadAccessPermissionChildren(IAccessPermission accessPermission, DefaultMutableTreeNode parentNode) {
        logger.info("loadAccessPermissionChildren(" + accessPermission + "," + parentNode);

        try {
            IProposal proposal = phase2ModelClient.getProposal(accessPermission.getProposalID());
            parentNode.removeAllChildren();
            addObject(parentNode, proposal, true);
        } catch (Phase2Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            return;
        }
    }

    private void loadUserAccessPermissionChildren(IUser user, DefaultMutableTreeNode parentNode) {
        logger.info("loadUserAccessPermissionChildren(" + user + "," + parentNode);

        List childrenList;
        try {
            parentNode.removeAllChildren();
            childrenList = accessModelClient.listAccessPermissionsOfUser(user.getID());
            Iterator cli = childrenList.iterator();
            while (cli.hasNext()) {
                IAccessPermission accessPermission = (IAccessPermission) cli.next();
                IProposal proposal = Phase2ModelClient.getInstance().getProposal(accessPermission.getProposalID());
                AccessPermissionWrapper accessPermissionWrapper = new AccessPermissionWrapper(accessPermission, user, proposal);
                addObject(parentNode, accessPermissionWrapper, true);
            }
        } catch (Phase2Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            return;
        }
    }

    private void loadUserProgrammeChildren(IUser user, DefaultMutableTreeNode parentNode) {
        logger.info("loadUserProgrammeChildren(" + user + "," + parentNode);

        List childrenList;
        try {
            parentNode.removeAllChildren();
            childrenList = phase2ModelClient.listProgrammesOfUser(user.getID());
            Iterator cli = childrenList.iterator();
            while (cli.hasNext()) {
                IProgram program = (IProgram) cli.next();
                addObject(parentNode, program, true);
            }
        } catch (Phase2Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            return;
        }
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child, boolean shouldBeVisible) {

        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

        //add a fake kid if it's anything other than a XGroup
        if (child.getClass() != XGroup.class) {
            childNode.add(new DefaultMutableTreeNode()); //NEW: Add a kid
        }
        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());
        return childNode;
    }
}
