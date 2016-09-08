/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.tree.datatree;

import ngat.oss.client.gui.tree.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import ngat.oss.client.gui.control.AdditionDeletionController;
import ngat.phase2.IGroup;
import ngat.phase2.IProgram;
import ngat.phase2.IProposal;
import ngat.phase2.ITag;
import ngat.phase2.IUser;

/**
 *
 * @author nrc
 */
public class DataTreePopupMenuListener implements ActionListener {

    private Object selectedObject;

    public DataTreePopupMenuListener() {
        //a root node of the tree
        this.selectedObject = null;
    }

    public DataTreePopupMenuListener(Object data) {
        this.selectedObject = data;
    }

    public void actionPerformed(ActionEvent ae) {

        String cmd = ae.getActionCommand();

        AdditionDeletionController additionDeletionRevisionController = AdditionDeletionController.getInstance();

        if (cmd.equals(DataTreePopupMenuFactory.POPUP_ACTION_NEW_USER)) {
            additionDeletionRevisionController.addNewUser();
        } else if (cmd.equals(DataTreePopupMenuFactory.POPUP_ACTION_NEW_PROGRAMME)) {
            additionDeletionRevisionController.addNewProgramme();
        } else if (cmd.equals(DataTreePopupMenuFactory.POPUP_ACTION_NEW_TAG)) {
            additionDeletionRevisionController.addNewTag();
        } else if (cmd.equals(DataTreePopupMenuFactory.POPUP_ACTION_NEW_ACCESS_PERMISSION)) {
            IUser user = (IUser)selectedObject;
            additionDeletionRevisionController.addNewAccessPermission(user);
        } else if (cmd.equals(DataTreePopupMenuFactory.POPUP_ACTION_NEW_PROPOSAL)) {
            if (selectedObject instanceof IProgram) {
                IProgram program = (IProgram)selectedObject;
                additionDeletionRevisionController.addNewProposal(program);
            } else if (selectedObject instanceof ITag) {
                ITag tag = (ITag)selectedObject;
                additionDeletionRevisionController.addNewProposal(tag);
            }
        } else if (cmd.equals(DataTreePopupMenuFactory.POPUP_ACTION_NEW_GROUP)) {
            IProposal proposal = (IProposal)selectedObject;
            additionDeletionRevisionController.addNewGroup(proposal);
        } else if (cmd.equals(DataTreePopupMenuFactory.POPUP_ACTION_DELETE_GROUP)) {
            IGroup group = (IGroup)selectedObject;
            additionDeletionRevisionController.deleteGroup(group);
        } else if (cmd.equals(DataTreePopupMenuFactory.POPUP_ACTION_DELETE_PROPOSAL)) {
            IProposal proposal = (IProposal)selectedObject;
            additionDeletionRevisionController.deleteProposal(proposal);
        } else if (cmd.equals(DataTreePopupMenuFactory.POPUP_ACTION_DELETE_PROGRAMME)) {
            IProgram programme = (IProgram)selectedObject;
            additionDeletionRevisionController.deleteProgramme(programme);
        } else if (cmd.equals(DataTreePopupMenuFactory.POPUP_ACTION_DELETE_USER)) {
            IUser user = (IUser)selectedObject;
            additionDeletionRevisionController.deleteUser(user);
        }

    }

    
}
