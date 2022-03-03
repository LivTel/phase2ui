/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ngat.oss.client.gui.util.validation;

import java.util.Iterator;
import java.util.List;
import ngat.oss.client.Phase2ModelClient;
import ngat.oss.client.gui.wrapper.ValidationResult;
import ngat.oss.client.gui.wrapper.ValidationResults;
import ngat.oss.exception.Phase2Exception;
import ngat.phase2.IGroup;
import ngat.phase2.IObservingConstraint;
import ngat.phase2.IProposal;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.ITimingConstraint;
import ngat.phase2.XEphemerisTimingConstraint;
import ngat.phase2.XFixedTimingConstraint;
import ngat.phase2.XFlexibleTimingConstraint;
import ngat.phase2.XMinimumIntervalTimingConstraint;
import ngat.phase2.XMonitorTimingConstraint;
import ngat.phase2.XPhotometricityConstraint;
import ngat.phase2.XSeeingConstraint;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class GroupValidator {

    static Logger logger = Logger.getLogger(GroupValidator.class);
    private IGroup group;

    public GroupValidator(IGroup group) throws Phase2Exception {
        this.group = group;
    }

    
    public ValidationResults getValidationResults(boolean validateProposalHeader, boolean validateSequence) throws Phase2Exception {

        logger.info("getValidationResults()");
        
        ValidationResults validationResults = new ValidationResults();

        String msg = "Validation of group started";
        
        if (validateProposalHeader) {
            msg += " (including parent proposal header).";
        }
        
        validationResults.addValidationResult(
                new ValidationResult(
                    "Group: " + group.getName(), ValidationResult.MESSAGE, msg
                )
        );
        
        
        if (validateProposalHeader) {
            //first, validate the header of the proposal, and add all the results to the extant validationResults
            IProposal proposal = Phase2ModelClient.getInstance().getProposalOfGroup(group.getID());
            ProposalValidator proposalValidator = new ProposalValidator(proposal);
            validationResults.addValidationResults(proposalValidator.getValidationResult(false));
        }
  
        
        //group level validation
        validationResults = testGroupIsEnabled(validationResults);
        validationResults = testGroupHasTimingConstraint(validationResults);
        validationResults = testTimingConstraintFields(validationResults);
        validationResults = testTimingConstraintAgainstObsConstraints(validationResults);
        validationResults = testGroupHasObservationSequence(validationResults);
                
        if (validateSequence) {
            //sequence level validation
            ISequenceComponent sequenceComponent = Phase2ModelClient.getInstance().getObservationSequenceOfGroup(group.getID());
            SequenceValidator sequenceValidator = new SequenceValidator(sequenceComponent, group);
            ValidationResults sequenceValidationResults = sequenceValidator.getValidationResults(false, false);
            List sequenceValidationResultsList = sequenceValidationResults.listResults();
            if (sequenceValidationResultsList != null) {
                Iterator svri = sequenceValidationResultsList.iterator();
                while (svri.hasNext()) {
                    ValidationResult result = (ValidationResult) svri.next();
                    validationResults.addValidationResult(result);
                }
            }
        }
        return validationResults;
    }

    private ValidationResults testGroupIsEnabled(ValidationResults validationResults) throws Phase2Exception {
        //update validationResult and return it
        if (!group.isActive()) {
            validationResults.addValidationResult(new ValidationResult("Group: " + group.getName(), ValidationResult.FAILURE, "The group is not enabled"));
        }
        return validationResults;
    }
    
    private ValidationResults testGroupHasTimingConstraint(ValidationResults validationResults) throws Phase2Exception {
        //update validationResult and return it
        if (group.getTimingConstraint() == null) {
            validationResults.addValidationResult(new ValidationResult("Group: " + group.getName(), ValidationResult.FAILURE, "The group has no Timing Constraints"));
        }
        return validationResults;
    }

    private ValidationResults testTimingConstraintFields(ValidationResults validationResults) throws Phase2Exception {
        String groupTitle = "Group: " + group.getName();

        ITimingConstraint timingConstraint = group.getTimingConstraint();

        if (timingConstraint instanceof XFixedTimingConstraint) {
            XFixedTimingConstraint fixedTimingConstraint = (XFixedTimingConstraint) timingConstraint;
            if (fixedTimingConstraint.getSlack() == 0) {
                //WARN
                validationResults.addValidationResult(new ValidationResult(groupTitle, ValidationResult.WARNING, "The timing constraint has a zero slack value."));
            }
        } else if (timingConstraint instanceof XFlexibleTimingConstraint) {
            XFlexibleTimingConstraint flexibleTimingConstraint = (XFlexibleTimingConstraint) timingConstraint;
            if (flexibleTimingConstraint.getEndTime() <= flexibleTimingConstraint.getStartTime()) {
                //FAIL
                validationResults.addValidationResult(new ValidationResult(groupTitle, ValidationResult.FAILURE, "The timing constraint has an end time <= the start time."));
            }
        } else if (timingConstraint instanceof XMonitorTimingConstraint) {
            XMonitorTimingConstraint monitorTimingConstraint = (XMonitorTimingConstraint) timingConstraint;
            if (monitorTimingConstraint.getEndDate() <= monitorTimingConstraint.getStartDate()) {
                //FAIL
                validationResults.addValidationResult(new ValidationResult(groupTitle, ValidationResult.FAILURE, "The timing constraint has an end date <= the start date."));
            }
            if (monitorTimingConstraint.getPeriod()==0) {
                //FAIL
                validationResults.addValidationResult(new ValidationResult(groupTitle, ValidationResult.FAILURE, "The timing constraint has a zero period value."));
            }
            if (monitorTimingConstraint.getWindow()==0) {
                //WARN
                validationResults.addValidationResult(new ValidationResult(groupTitle, ValidationResult.WARNING, "The timing constraint has a zero window value."));
            }
        } else if (timingConstraint instanceof XEphemerisTimingConstraint) {
            XEphemerisTimingConstraint ephemerisTimingConstraint = (XEphemerisTimingConstraint) timingConstraint;
            if (ephemerisTimingConstraint.getCyclePeriod()==0) {
                //FAIL
                validationResults.addValidationResult(new ValidationResult(groupTitle, ValidationResult.FAILURE, "The timing constraint has a zero cycle period value."));
            }
            if (ephemerisTimingConstraint.getWindow()==0) {
                //WARN
                validationResults.addValidationResult(new ValidationResult(groupTitle, ValidationResult.WARNING, "The timing constraint has a zero window value."));
            }
            if ((ephemerisTimingConstraint.getPhase() < 0.0)||(ephemerisTimingConstraint.getPhase() > 1.0)) {
                //FAIL
                validationResults.addValidationResult(new ValidationResult(groupTitle, ValidationResult.FAILURE, "The timing constraint phase value must be between 0 and 1 inclusive."));
            }
        } else if (timingConstraint instanceof XMinimumIntervalTimingConstraint) {
            XMinimumIntervalTimingConstraint minimumIntervalTimingConstraint = (XMinimumIntervalTimingConstraint) timingConstraint;
            if (minimumIntervalTimingConstraint.getEndTime() <= minimumIntervalTimingConstraint.getStartTime()) {
                //FAIL
                validationResults.addValidationResult(new ValidationResult(groupTitle, ValidationResult.FAILURE, "The timing constraint has an end date <= the start date."));
            }
            if (minimumIntervalTimingConstraint.getMaximumRepeats() ==0) {
                //FAIL
                validationResults.addValidationResult(new ValidationResult(groupTitle, ValidationResult.FAILURE, "The minimum interval timing constraint has a repeat count of 0."));
            }
            if (minimumIntervalTimingConstraint.getMaximumRepeats() ==1) {
                //WARN
                validationResults.addValidationResult(new ValidationResult(groupTitle, ValidationResult.WARNING, "The minimum interval timing constraint has a repeat count of 1."));
            }
        }
        return validationResults;
    }

    private ValidationResults testTimingConstraintAgainstObsConstraints(ValidationResults validationResults) throws Phase2Exception {
        
        String groupTitle = "Group: " + group.getName();
        if (group.getTimingConstraint() instanceof XFixedTimingConstraint) {
            List obsConstraints = group.listObservingConstraints();
            Iterator oci = obsConstraints.iterator();
            while (oci.hasNext()) {
                IObservingConstraint observingConstraint =  (IObservingConstraint) oci.next();
                if (observingConstraint instanceof XSeeingConstraint) {
                    //ok
                } else if (observingConstraint instanceof XPhotometricityConstraint) {
                    //that's ok too
                } else {
                    validationResults.addValidationResult(new ValidationResult(groupTitle, ValidationResult.FAILURE, "The group uses a Fixed Timing Constraint, but also contains observing constraints other than 'Seeing' and 'Photometricity'."));
                }
            }
        }
        
        return validationResults;
    }
   

    private ValidationResults testGroupHasObservationSequence(ValidationResults validationResults) throws Phase2Exception {
        //update validationResult and return it
        ISequenceComponent sequenceComponent = Phase2ModelClient.getInstance().getObservationSequenceOfGroup(group.getID());
        if (sequenceComponent == null) {
            validationResults.addValidationResult(new ValidationResult("Group: " + group.getName(), ValidationResult.FAILURE, "The group has no Observation Sequence"));
        }
        return validationResults;
    }


}
