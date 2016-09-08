/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.util.validation;

import java.util.Iterator;
import java.util.List;
import ngat.oss.client.AccessModelClient;
import ngat.oss.client.Phase2ModelClient;
import ngat.oss.client.gui.wrapper.ValidationResult;
import ngat.oss.client.gui.wrapper.ValidationResults;
import ngat.oss.exception.Phase2Exception;
import ngat.phase2.IAccessPermission;
import ngat.phase2.IGroup;
import ngat.phase2.IProposal;
import ngat.phase2.ITag;
import ngat.phase2.IUser;

/**
 *
 * @author nrc
 */
public class ProposalValidator {

    private IProposal proposal;

    public ProposalValidator(IProposal proposal) {
        this.proposal = proposal;
    }

    public ValidationResults getValidationResult(boolean validateGroups) throws Phase2Exception {

        ValidationResults validationResults = new ValidationResults();

        String msg = "Validation of proposal started";
        
        if (validateGroups) {
            msg += " (including sub-groups).";
        } else {
            msg += " (header only).";
        }
        
        validationResults.addValidationResult(
                new ValidationResult(
                    "Proposal: " + proposal.getName(), ValidationResult.MESSAGE, msg
                )
        );
        
        validationResults = testProposalIsEnabled(validationResults);
        validationResults = testProposalHasPI(validationResults);
        validationResults = testProposalHasTAG(validationResults);

        if (validateGroups) {
            //iterate throught the groups of the proposal
            long pid = proposal.getID();
            Phase2ModelClient phase2Client = Phase2ModelClient.getInstance();
            Iterator i;

            List groupsList = phase2Client.listGroups(pid, true);
            i = groupsList.iterator();
            while (i.hasNext()) {
                IGroup group = (IGroup) i.next();
                GroupValidator groupValidator = new GroupValidator(group);
                ValidationResults groupValidationResults = groupValidator.getValidationResults(false, true);
                List groupValidationResultsList = groupValidationResults.listResults();
                if (groupValidationResultsList != null) {
                    Iterator gvri = groupValidationResultsList.iterator();
                    while (gvri.hasNext()) {
                        ValidationResult result = (ValidationResult) gvri.next();
                        validationResults.addValidationResult(result);
                    }
                }
            }
        }
        
        return validationResults;
    }

    
    private ValidationResults testProposalIsEnabled(ValidationResults validationResults) throws Phase2Exception {
        //update  validationResult and return it
        if (!proposal.isEnabled()){
            validationResults.addValidationResult(new ValidationResult("Proposal: " + proposal.getName(), ValidationResult.FAILURE, "The proposal is not enabled."));
        }
        return validationResults;
    }
    
    private ValidationResults testProposalHasPI(ValidationResults validationResults) throws Phase2Exception {
        //update  validationResult and return it
         long pid = proposal.getID();
         AccessModelClient accessModelClient = AccessModelClient.getInstance();

         List accessPermissions = accessModelClient.listAccessPermissionsOnProposal(pid);
         Iterator i = accessPermissions.iterator();
         boolean foundExtantPI = false;
         while (i.hasNext()) {
             IAccessPermission accessPermission = (IAccessPermission) i.next();
             if (accessPermission.getUserRole() == IAccessPermission.PRINCIPLE_INVESTIGATOR_ROLE) {
                 long uid = accessPermission.getUserID();
                 IUser user = accessModelClient.getUser(uid);
                 if (user != null) {
                     foundExtantPI = true;
                 }
             }
         }

         if (!foundExtantPI) {
             validationResults.addValidationResult(new ValidationResult("Proposal: " + proposal.getName(), ValidationResult.FAILURE, "The proposal has not got a PI associated with it"));
         }
         return validationResults;
    }

    private ValidationResults testProposalHasTAG(ValidationResults validationResults) throws Phase2Exception {
        //update  validationResult and return it
        long pid = proposal.getID();
        Phase2ModelClient phase2ModelClient = Phase2ModelClient.getInstance();

        ITag tag = phase2ModelClient.getTagOfProposal(pid);
        if (tag == null) {
            validationResults.addValidationResult(new ValidationResult("Proposal: " + proposal.getName(), ValidationResult.FAILURE, "The proposal has not got a TAGI associated with it"));
        }
        return validationResults;
    }

}
