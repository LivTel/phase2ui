/*
 * MainTreeSelectionListener.java
 *
 * Created on 03 December 2007, 16:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package ngat.oss.client.gui.tree.datatree;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import ngat.phase2.IProposal;
import ngat.phase2.IUser;
import ngat.oss.client.gui.frame.MainFrame;
import ngat.oss.client.gui.panel.headers.AccessPermissionHeaderPanel;
import ngat.oss.client.gui.panel.headers.GroupHeaderPanel;
import ngat.oss.client.gui.panel.headers.ProgrammeHeaderPanel;
import ngat.oss.client.gui.panel.headers.ProposalHeaderPanel;
import ngat.oss.client.gui.panel.headers.TagHeaderPanel;
import ngat.oss.client.gui.panel.headers.UserHeaderPanel;
import ngat.oss.client.gui.reference.Session;
import ngat.oss.client.gui.render.AccessPermissionRenderer;
import ngat.oss.client.gui.wrapper.AccessPermissionWrapper;
import ngat.phase2.IAccessPermission;
import ngat.phase2.IGroup;
import ngat.phase2.IProgram;
import ngat.phase2.ITag;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class DataTreeSelectionListener implements TreeSelectionListener {

    static Logger logger = Logger.getLogger(DataTreeSelectionListener.class);
    private JTree tree;
    private boolean inValueChanged;

    /** Creates a new instance of MainTreeSelectionListener */
    public DataTreeSelectionListener(JTree tree) {
        this.tree = tree;
    }

    /*
     * value selected in tree has changed, update detailPanel
     */
    public void valueChanged(TreeSelectionEvent e) {

        TreePath treePath = e.getPath();

        if (treePath == null) {
            return;
        }

        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
        Object userObject = treeNode.getUserObject();

        MainFrame mainFrame = MainFrame.getInstance();
        mainFrame.clearPanels();

        //was:
        //JPanel panel = mainFrame.getDetailPanel();
        //panel.removeAll();

        //set sub panel editable or not here

        if (userObject instanceof IGroup) {
            IGroup group = (IGroup) userObject;
            mainFrame.displayMessage("Displaying Group: " + group.getName());
            mainFrame.showHeaderPanel(new GroupHeaderPanel(group));

        } else if (userObject instanceof AccessPermissionWrapper) {
            AccessPermissionWrapper accessPermissionWrapper = (AccessPermissionWrapper) userObject;
            mainFrame.displayMessage("Displaying Access Permission: " + AccessPermissionRenderer.getRenderedAccessPermission(accessPermissionWrapper));
            mainFrame.showHeaderPanel(new AccessPermissionHeaderPanel(accessPermissionWrapper.getAccessPermission(), tree, treePath));

        } else if (userObject instanceof IProposal) {
            IProposal proposal = (IProposal) userObject;
            mainFrame.displayMessage("Displaying Proposal: " + proposal.getName());
            mainFrame.showHeaderPanel(new ProposalHeaderPanel(proposal));

        } else if (userObject instanceof IUser) {
            IUser user = (IUser) userObject;
            mainFrame.displayMessage("Displaying User: " + user.getName());
            mainFrame.showHeaderPanel(new UserHeaderPanel(user));

        } else if (userObject instanceof IProgram) {
            IProgram programme = (IProgram) userObject;
            mainFrame.displayMessage("Displaying Programme: " + programme.getName());
            mainFrame.showHeaderPanel(new ProgrammeHeaderPanel(programme));

        } else if (userObject instanceof ITag) {
            ITag tag = (ITag) userObject;
            mainFrame.displayMessage("Displaying TAG: " + tag.getName());
            mainFrame.showHeaderPanel(new TagHeaderPanel(tag));

        } else if (userObject instanceof String) {
            IUser loggedInUser = Session.getInstance().getUser();
            if (!loggedInUser.isSuperUser()) {
                mainFrame.displayMessage("Displaying User: " + loggedInUser.getName());
                mainFrame.showHeaderPanel(new UserHeaderPanel(loggedInUser));
            } else {
                mainFrame.clearPanels();
            }

        } else {
            logger.info("not handling " + userObject.getClass().getName());
            return;
        }
        //mainFrame.pack();//new
    }

}
