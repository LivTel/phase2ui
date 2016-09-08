/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.control;

import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import ngat.oss.client.AccessModelClient;
import ngat.oss.client.Phase2ModelClient;
import ngat.oss.client.gui.dialog.NewAccessPermissionDialog;
import ngat.oss.client.gui.dialog.NewGroupDialog;
import ngat.oss.client.gui.dialog.NewProgrammeDialog;
import ngat.oss.client.gui.dialog.NewProposalDialog;
import ngat.oss.client.gui.dialog.NewTagDialog;
import ngat.oss.client.gui.dialog.NewUserDialog;
import ngat.oss.client.gui.frame.MainFrame;
import ngat.oss.exception.Phase2Exception;
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
public class AdditionDeletionController {

    static Logger logger = Logger.getLogger(AdditionDeletionController.class);

    private static AdditionDeletionController instance = null;

    public static AdditionDeletionController getInstance() {
        if (instance == null) {
            instance = new AdditionDeletionController();
        }
        return instance;
    }
    
    private AdditionDeletionController() {}

    public void addNewUser() {
        NewUserDialog dialog = new NewUserDialog(true);
        dialog.setVisible(true);
        //blocks
        dialog.setVisible(false);
        dialog.dispose();
    }

    public void addNewProgramme() {
        NewProgrammeDialog dialog = new NewProgrammeDialog(true);
        dialog.setVisible(true);
        //blocks
        dialog.setVisible(false);
        dialog.dispose();
    }

    public void addNewTag() {
        NewTagDialog dialog = new NewTagDialog(true);
        dialog.setVisible(true);
        //blocks
        dialog.setVisible(false);
        dialog.dispose();
    }

    public void addNewAccessPermission(IUser user) {

        NewAccessPermissionDialog dialog = new NewAccessPermissionDialog(true, user);
        dialog.setVisible(true);
        //blocks
        dialog.setVisible(false);
        dialog.dispose();
    }

    public void addNewProposal(IProgram program) {
        NewProposalDialog dialog = new NewProposalDialog(true, program);
        dialog.setVisible(true);
        //blocks
        dialog.setVisible(false);
        dialog.dispose();
    }

    public void addNewProposal(ITag tag) {
        NewProposalDialog dialog = new NewProposalDialog(true, tag);
        dialog.setVisible(true);
        //blocks
        dialog.setVisible(false);
        dialog.dispose();
    }
    
    public void addNewGroup(IProposal proposal) {
        NewGroupDialog dialog = new NewGroupDialog(true, proposal);
        dialog.setVisible(true);
        //blocks
        dialog.setVisible(false);
        dialog.dispose();
    }

    public void deleteGroup(IGroup group) {
        String msg = "Are you sure you want to delete the group called " + group.getName();
        String hdr = "Confirm deletion.";
        int response = JOptionPane.showConfirmDialog(null, msg, hdr, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.OK_OPTION) {
            Phase2ModelClient phase2ModelClient = Phase2ModelClient.getInstance();
            try {
                phase2ModelClient.deleteGroup(group.getID());
                MainFrame.getInstance().receiveTreeObjectDeleted();
                //12/3/12
                MainFrame.getInstance().reloadSelectedNodeParent();

            } catch (Phase2Exception ex) {
                ex.printStackTrace();
                logger.error(ex);
                JOptionPane.showMessageDialog(MainFrame.getInstance(), "An error occurred whilst trying to delete the group");
                return;
            }
        }
    }

    public void deleteProposal(IProposal proposal) {
        String msg = "Are you sure you want to delete the proposal called " + proposal.getName();
        String hdr = "Confirm deletion.";
        int response = JOptionPane.showConfirmDialog(null, msg, hdr, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.OK_OPTION) {
            Phase2ModelClient phase2ModelClient = Phase2ModelClient.getInstance();
            try {
                AccessModelClient accessModelClient = AccessModelClient.getInstance();
                List accessPermissions = accessModelClient.listAccessPermissionsOnProposal(proposal.getID());
                phase2ModelClient.deleteProposal(proposal.getID());
                deleteAccessPermissions(accessPermissions);
                MainFrame.getInstance().reloadRootNode(); //a one off reload (it gets too complicated otherwise)
            } catch (Phase2Exception ex) {
                ex.printStackTrace();
                logger.error(ex);
                JOptionPane.showMessageDialog(MainFrame.getInstance(), "An error occurred whilst trying to delete the proposal");
                return;
            }
        }
    }


    public void deleteProgramme(IProgram program) {
        String msg = "Are you sure you want to delete the programme called " + program.getName();
        String hdr = "Confirm deletion.";
        int response = JOptionPane.showConfirmDialog(null, msg, hdr, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.OK_OPTION) {
            Phase2ModelClient phase2ModelClient = Phase2ModelClient.getInstance();
            try {
                //placeholder for this
                throw new Phase2Exception("Rubbish");
            } catch (Phase2Exception ex) {
                ex.printStackTrace();
                logger.error(ex);
                JOptionPane.showMessageDialog(MainFrame.getInstance(), "An error occurred whilst trying to delete the proposal");
                return;
            }
        }
    }

    public void deleteAccessPermission(IAccessPermission accessPermission) {

        String msg = "Are you sure you want to revoke this Access Permission?";
        String hdr = "Confirm Permission Deletion";
        int response = JOptionPane.showConfirmDialog(null, msg, hdr, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.OK_OPTION) {
            try {
                AccessModelClient.getInstance().revokePermission(accessPermission.getID());
                JOptionPane.showMessageDialog(MainFrame.getInstance(), "Permission revoked");
                MainFrame.getInstance().displayMessage("Access Permission revoked.");
                MainFrame.getInstance().receiveTreeObjectDeleted();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(MainFrame.getInstance(), e.getMessage());
            }
        }
    }

    public void deleteUser(IUser user) {
        String msg = "Are you sure you want to delete the user called " + user.getName();
        String hdr = "Confirm deletion.";
        int response = JOptionPane.showConfirmDialog(null, msg, hdr, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.OK_OPTION) {
            AccessModelClient accessModelClient = AccessModelClient.getInstance();
            Phase2ModelClient phase2ModelClient = Phase2ModelClient.getInstance();
            try {
                List accessPermissions = accessModelClient.listAccessPermissionsOfUser(user.getID());
                Iterator i = accessPermissions.iterator();
                msg = user.getName() + " has the following access permissions:/n";
                boolean hadAccessPermissions = false;

                while (i.hasNext()) {
                    hadAccessPermissions = true;
                    IAccessPermission accessPermission = (IAccessPermission) i.next();
                    String permissionType = "";
                    switch (accessPermission.getUserRole()) {
                        case IAccessPermission.PRINCIPLE_INVESTIGATOR_ROLE:
                            permissionType = "PI";
                            break;
                        case IAccessPermission.CO_INVESTIGATOR_ROLE:
                            permissionType = "CoI";
                            break;
                        case IAccessPermission.ASSISTANT_INVESTIGATOR_ROLE:
                            permissionType = "AI";
                            break;
                        default:
                            permissionType = "UNKNOWN";
                            break;
                    }
                    String proposalName = phase2ModelClient.getProposal(accessPermission.getProposalID()).getName();
                    msg += permissionType + " on proposal " + proposalName + "/n";
                }
                if (hadAccessPermissions) {
                    msg += "these will also be deleted.";
                    response = JOptionPane.showConfirmDialog(null, msg, hdr, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (response == JOptionPane.OK_OPTION) {
                        accessModelClient.deleteUser(user.getID());
                        deleteAccessPermissions(accessPermissions);
                        MainFrame.getInstance().receiveTreeObjectDeleted();
                    }
                } else {
                    //no access permissions, just go ahead and delete
                    accessModelClient.deleteUser(user.getID());
                    MainFrame.getInstance().receiveTreeObjectDeleted();
                }

            } catch (Phase2Exception ex) {
                ex.printStackTrace();
                logger.error(ex);
                JOptionPane.showMessageDialog(MainFrame.getInstance(), "An error occurred whilst trying to delete the proposal");
                return;
            }
        }
    }

    private void deleteAccessPermissions(List accessPermissions) {
        Iterator i = accessPermissions.iterator();
        while (i.hasNext()) {
            IAccessPermission accessPermission = (IAccessPermission) i.next();
            try {
                AccessModelClient.getInstance().revokePermission(accessPermission.getID());
            } catch (Phase2Exception ex) {
                ex.printStackTrace();
                logger.error(ex);
            }
        }
    }
}
