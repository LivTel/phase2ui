/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.util.validation;

/**
 *
 * @author nrc
 */
public class ValidationProcess {

    private Object object;
    
    //NO LONGER USED, simply got too complicated to abstract the problem to this level of wrapping
    
    /*
    public ValidationProcess(Object object) {
        this.object = object;
    }

    public ValidationResults validateObject() throws Phase2Exception {
        ValidationResults validationResults = null;
       
        if (object instanceof IProposal) {
            ProposalValidator proposalValidator = new ProposalValidator((IProposal)object);
            //want to validate the whole of the proposal (header and sub groups)
            validationResults = proposalValidator.getValidationResult(true);
            
        } else if (object instanceof IGroup) {
            GroupValidator groupValidator = new GroupValidator((IGroup)object);
            //want to validate the group (proposal header and sub groups)
            validationResults = groupValidator.getValidationResults(true, true);
            
        } else if (object instanceof ISequenceComponent) {
            SequenceValidator sequenceValidator = new SequenceValidator((ISequenceComponent)object, "Sequence in editor");
            //want to validate the sequence, the header of the group and the header of the proposal
            validationResults = sequenceValidator.getValidationResults();
            
        }

        return validationResults;
    }
    */
}
