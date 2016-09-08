/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.render;

import ngat.oss.client.gui.wrapper.AccessPermissionWrapper;
import ngat.phase2.IAccessPermission;
import ngat.phase2.IProposal;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class AccessPermissionRenderer {

    static Logger logger = Logger.getLogger(AccessPermissionRenderer.class);

    public static String getRenderedAccessPermission(AccessPermissionWrapper accessPermissionWrapper) {

        IAccessPermission accessPermission = accessPermissionWrapper.getAccessPermission();
        IProposal proposal = accessPermissionWrapper.getProposal();

        String proposalName;
        if (proposal != null) {
            proposalName = proposal.getName();
        } else {
            proposalName = "UNKNOWN PROPOSAL";
        }
        String userRole = "";
        switch (accessPermission.getUserRole()) {
            case IAccessPermission.PRINCIPLE_INVESTIGATOR_ROLE:
                userRole = "PI";
                break;
            case IAccessPermission.CO_INVESTIGATOR_ROLE:
                userRole = "CoI";
                break;
            case IAccessPermission.ASSISTANT_INVESTIGATOR_ROLE:
                userRole = "AI";
                break;
        }
        //String representation = userFirstName + " " + userLastName + " - " + userRole + " - " + proposalName;
        String representation = userRole + " - " + proposalName;
        return representation;
    }

    //don't use
    /*
    public static String getRenderedAccessPermission(IAccessPermission accessPermission) {
        try {
            IProposal proposal = Phase2ModelClient.getInstance().getProposal(accessPermission.getProposalID()); //IS SLOW DURING TABLE SCROLL
            
            String proposalName;
            if (proposal != null) {
                proposalName = proposal.getName();
            } else {
                proposalName = "UNKNOWN PROPOSAL";
            }
            String userRole = "";
            switch (accessPermission.getUserRole()) {
                case IAccessPermission.PRINCIPLE_INVESTIGATOR_ROLE:
                    userRole = "PI";
                    break;
                case IAccessPermission.CO_INVESTIGATOR_ROLE:
                    userRole = "CoI";
                    break;
                case IAccessPermission.ASSISTANT_INVESTIGATOR_ROLE:
                    userRole = "AI";
                    break;
            }
            //String representation = userFirstName + " " + userLastName + " - " + userRole + " - " + proposalName;
            String representation = userRole + " - " + proposalName;
            return representation;
        } catch (Phase2Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            return "ERROR";
        }
    }
    */
}
