/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ngat.oss.client.gui.tree.datatree;

import ngat.oss.client.gui.tree.datatree.DataTreePopupMenuListener;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import ngat.oss.client.gui.reference.Session;
import ngat.phase2.IGroup;
import ngat.phase2.IProgram;
import ngat.phase2.IProposal;
import ngat.phase2.ITag;
import ngat.phase2.IUser;

/**
 *
 * @author nrc
 */
public class DataTreePopupMenuFactory {

    public static final String POPUP_ACTION_NEW_USER = "POPUP_ACTION_NEW_USER";
    public static final String POPUP_ACTION_NEW_PROGRAMME = "POPUP_ACTION_NEW_PROGRAMME";
    public static final String POPUP_ACTION_NEW_TAG = "POPUP_ACTION_NEW_TAG";
    public static final String POPUP_ACTION_NEW_ACCESS_PERMISSION = "POPUP_ACTION_NEW_ACCESS_PERMISSION";
    public static final String POPUP_ACTION_NEW_PROPOSAL = "POPUP_ACTION_NEW_PROPOSAL";
    public static final String POPUP_ACTION_NEW_GROUP = "POPUP_ACTION_NEW_GROUP";

    public static final String POPUP_ACTION_DELETE_GROUP = "POPUP_ACTION_DELETE_GROUP";
    public static final String POPUP_ACTION_DELETE_PROPOSAL = "POPUP_ACTION_DELETE_PROPOSAL";
    public static final String POPUP_ACTION_DELETE_PROGRAMME = "POPUP_ACTION_DELETE_PROGRAMME";
    public static final String POPUP_ACTION_DELETE_USER = "POPUP_ACTION_DELETE_USER";

    public static JPopupMenu getUsersRootPopupMenu() {

        ActionListener actionListener = new DataTreePopupMenuListener();
        JPopupMenu popup = new JPopupMenu();
        popup.add(createMenuItem("New User", "path to gif", POPUP_ACTION_NEW_USER, actionListener));
        return popup;
    }
    
    public static JPopupMenu getProgrammesRootPopupMenu() {

        ActionListener actionListener = new DataTreePopupMenuListener();
        JPopupMenu popup = new JPopupMenu();
        popup.add(createMenuItem("New Programme", "path to gif", POPUP_ACTION_NEW_PROGRAMME, actionListener));
        return popup;
    }
    
    public static JPopupMenu getTagsRootPopupMenu() {

        ActionListener actionListener = new DataTreePopupMenuListener();
        JPopupMenu popup = new JPopupMenu();
        popup.add(createMenuItem("New TAG", "path to gif", POPUP_ACTION_NEW_TAG, actionListener));
        return popup;
    }
    
    public static JPopupMenu getUserPopupMenu(IUser selectedUser) {
        ActionListener actionListener = new DataTreePopupMenuListener(selectedUser);
        JPopupMenu popup = new JPopupMenu();
        popup.add(createMenuItem("New Access Permission", "path to gif", POPUP_ACTION_NEW_ACCESS_PERMISSION, actionListener));
        if (Session.getInstance().getUser().isSuperUser()) {
            popup.add(createMenuItem("Delete User", "path to gif", POPUP_ACTION_DELETE_USER, actionListener));
        }
        return popup;
    }
    
    public static JPopupMenu getProgrammePopupMenu(IProgram selectedProgram) {
        ActionListener actionListener = new DataTreePopupMenuListener(selectedProgram);
        JPopupMenu popup = new JPopupMenu();
        popup.add(createMenuItem("New Proposal", "path to gif", POPUP_ACTION_NEW_PROPOSAL, actionListener));

        //placeholder
        /*
        if (Session.getInstance().getUser().isSuperUser()) {
            popup.add(createMenuItem("Delete Programme", "path to gif", POPUP_ACTION_DELETE_PROGRAMME, actionListener));
        }
        */
        return popup;
    }

    public static JPopupMenu getTagPopupMenu(ITag selectedTag) {
        ActionListener actionListener = new DataTreePopupMenuListener(selectedTag);
        JPopupMenu popup = new JPopupMenu();
        popup.add(createMenuItem("New Proposal", "path to gif", POPUP_ACTION_NEW_PROPOSAL, actionListener));
        return popup;
    }

    public static JPopupMenu getProposalPopupMenu(IProposal selectedProposal) {
        ActionListener actionListener = new DataTreePopupMenuListener(selectedProposal);
        JPopupMenu popup = new JPopupMenu();
        popup.add(createMenuItem("New Group", "path to gif", POPUP_ACTION_NEW_GROUP, actionListener));
        if (Session.getInstance().getUser().isSuperUser()) {
            popup.add(createMenuItem("Delete Proposal", "path to gif", POPUP_ACTION_DELETE_PROPOSAL, actionListener));
        }
        return popup;
    }

    public static JPopupMenu getGroupPopupMenu(IGroup selectedGroup) {
        ActionListener actionListener = new DataTreePopupMenuListener(selectedGroup);
        JPopupMenu popup = new JPopupMenu();
        popup.add(createMenuItem("GroupID: " +selectedGroup.getID(), "path to gif", "", null));
        popup.add(createMenuItem("Delete Group", "path to gif", POPUP_ACTION_DELETE_GROUP, actionListener));
        return popup;
    }
    
    private static JMenuItem createMenuItem(String text, String iconText, String cmd, ActionListener actionListener) {

        JMenuItem item = new JMenuItem(text);
        //item.setIcon(new ImageIcon(iconText));
        item.setActionCommand(cmd);
        item.addActionListener(actionListener);
        return item;
    }

}
