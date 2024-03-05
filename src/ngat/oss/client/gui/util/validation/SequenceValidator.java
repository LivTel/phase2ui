/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ngat.oss.client.gui.util.validation;

import java.util.Iterator;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import ngat.oss.client.gui.reference.CONST;
import ngat.oss.client.gui.util.SequenceWalker;
import ngat.oss.client.gui.wrapper.ValidationResult;
import ngat.oss.client.gui.wrapper.ValidationResults;
import ngat.oss.exception.Phase2Exception;
import ngat.phase2.IAcquisitionConfig;
import ngat.phase2.IAutoguiderConfig;
import ngat.phase2.IBeamSteeringConfig;
import ngat.phase2.ICalibration;
import ngat.phase2.IExecutiveAction;
import ngat.phase2.IExposure;
import ngat.phase2.IGroup;
import ngat.phase2.IInstrumentConfig;
import ngat.phase2.IInstrumentConfigSelector;
import ngat.phase2.IRotatorConfig;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.ISlew;
import ngat.phase2.ITarget;
import ngat.phase2.ITimingConstraint;
import ngat.phase2.XArc;
import ngat.phase2.XBias;
import ngat.phase2.XBlueTwoSlitSpectrographInstrumentConfig;
import ngat.phase2.XBranchComponent;
import ngat.phase2.XDark;
import ngat.phase2.XDualBeamSpectrographInstrumentConfig;
import ngat.phase2.XEphemerisTarget;
import ngat.phase2.XEphemerisTrackNode;
import ngat.phase2.XExecutiveComponent;
import ngat.phase2.XExtraSolarTarget;
import ngat.phase2.XFilterDef;
import ngat.phase2.XFixedTimingConstraint;
import ngat.phase2.XImagerInstrumentConfig;
import ngat.phase2.XInstrumentConfigSelector;
import ngat.phase2.XIteratorComponent;
import ngat.phase2.XLampFlat;
import ngat.phase2.XMoptopInstrumentConfig;
import ngat.phase2.XLiricInstrumentConfig;
import ngat.phase2.XMultipleExposure;
import ngat.phase2.XPeriodExposure;
import ngat.phase2.XPeriodRunAtExposure;
import ngat.phase2.XPolarimeterInstrumentConfig;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class SequenceValidator 
{
    static Logger logger = Logger.getLogger(SequenceValidator.class);
    private String obsSeqName;
    private TelescopeState telescopeState;
    private XIteratorComponent rootComponent;
    private IGroup group;

    //sequence without any idea of context (i.e. group, timing constraints, observing constraints)
    /*
     // no longer used, we always know the parent group now, and we validate against that and it's parent proposal
     public SequenceValidator(ISequenceComponent sequenceComponent, String objectName) throws Phase2Exception {
     this.objectName = objectName;
     rootComponent = (XIteratorComponent) sequenceComponent;
     telescopeState = new TelescopeState(objectName);
        
     JOptionPane.showMessageDialog(new JFrame(), "Called, NOT knowing group");
     }
     */
    //validate sequence given group (invoked from group header panel)
    public SequenceValidator(ISequenceComponent sequenceComponent, IGroup group) throws Phase2Exception 
    {
        this.obsSeqName = "Observation sequence of group: " + group.getName();
        rootComponent = (XIteratorComponent) sequenceComponent;
        this.group = group;

        telescopeState = new TelescopeState(obsSeqName);

        //store the group - we might have to look at it's constraints in some circumstances
        telescopeState.setGroup(group);

        //JOptionPane.showMessageDialog(new JFrame(), "Called, knowing group");
        //if it's a fixed group timing constraint, include a validation check for overlap with other groups
        ITimingConstraint timingConstraint = group.getTimingConstraint();
        if (timingConstraint instanceof XFixedTimingConstraint) 
        {
            telescopeState.receiveFixedGroupTimingConstraint(group);
        }
    }

    public ValidationResults getValidationResults(boolean validateProposalHeader, boolean validateGroupHeader) throws Phase2Exception 
    {

        logger.info("getValidationResults()");

        //walk through the observation sequence
        //using a state-model update that keeps track of:
        //state model params:
        //slew - clears aperture offsets, sets target
        //config - selects instrument
        //rules:
        //before an exposure, we need an instrument, else FAIL
        //before an exposure we need aperture offsets, else WARN
        //before an exposure we need target, else WARN.
        //before a rot card we need target, FAIL
        //sequence has a slew but no exposures, WARN
        //if using frodospec, then there needs to be a dual-beam spec instr config before an autoguide
        //parent node =rootNode
        //root component = rootComponent
        //easier to do some validation / look ahead using a non event parsing method of walking the sequence
        //is there a frodo-branch in the sequence? if so, inform the telescopeState
        if (validateGroupHeader) 
        {
            //validate the group as well (just it's header, and it's proposal header) and add the results to the TelescopeState validation results
            GroupValidator groupValidator = new GroupValidator(group);
            ValidationResults groupValResults = groupValidator.getValidationResults(validateProposalHeader, false);
            telescopeState.addValidationResults(groupValResults);
        }

        //validate the sequence
        SequenceWalker sequenceWalker = new SequenceWalker(rootComponent);

        boolean containsFrodoBranch = sequenceWalker.sequenceContainsFrodoBranch();
        telescopeState.setContainsFrodoBranch(containsFrodoBranch);

        boolean isALOTUSSequence = sequenceWalker.isALOTUSSequence();
        telescopeState.setIsALOTUSSequence(isALOTUSSequence);

        //is there an invalid supircam exposure in the sequence? if so, inform the telescopeState
        boolean containsInvalidSupircamExposure = sequenceWalker.sequenceContainsInvalidSupircamExposure();
        if (containsInvalidSupircamExposure) 
        {
            telescopeState.setContainsInvalidSupIRCamState();
        }

        //is there a slew but no exposures? if so, inform the telescopeState
        int numExposures = sequenceWalker.getExposures().size();
        int numSlews = sequenceWalker.getSlews().size();
        if (numSlews > 0) 
        {
            if (numExposures == 0) 
            {
                telescopeState.setContainsSlewButNoExposure();
            }
        }

        //iterate through root component, taking each sub component and sending it for validation against the telescope state object
        if (rootComponent != null) 
        {
            List childrenList = rootComponent.listChildComponents();
            if (childrenList != null) 
            {
                Iterator childrenIterator = childrenList.iterator();
                while (childrenIterator.hasNext())
                {
                    ISequenceComponent childComponent = (ISequenceComponent) childrenIterator.next();
                    handleComponent(childComponent);
                }
            }
        }

        return telescopeState.getValidationResults();
    }

    private void handleComponent(ISequenceComponent component) 
    {

        //checks the state model
        telescopeState.receiveSequenceComponent(component);

        if ((component instanceof XIteratorComponent) | (component instanceof XBranchComponent)) 
        {
            List childrenList = component.listChildComponents();
            if (childrenList != null) 
            {
                Iterator childrenIterator = childrenList.iterator();
                while (childrenIterator.hasNext()) 
                {
                    ISequenceComponent childComponent = (ISequenceComponent) childrenIterator.next();
                    handleComponent(childComponent);
                }
            }
        }
    }

}

class TelescopeState 
{

    static Logger logger = Logger.getLogger(TelescopeState.class);

    private volatile ISequenceComponent previousComponent = null;
    private volatile boolean containsFrodoBranch = false;
    private volatile boolean isALotusSequence = false;
    private volatile boolean haveInstrument = false;
    private volatile boolean haveBeamSteeringConfig = false;
    private volatile boolean haveApertureOffsets = false;
    private volatile boolean haveTarget = false;
    private volatile boolean autoguiderMightBeOn = false;
    private volatile int lastReceivedLOTUSSlitPosition = -1;
    
    private ValidationResults validationResults = new ValidationResults();
    private String objectName;
    private String latestInstrumentConfigInstrumentName;
    private int latestMoptopRotorSpeed = XMoptopInstrumentConfig.ROTOR_SPEED_UNKNOWN;
    private int latestLiricCoaddExposureLength = 0;
    private ISlew latestSlew = null;
    private IGroup group;

    public TelescopeState(String objectName) 
    {
        logger.info("TelescopeState(" + objectName + ")");
        this.objectName = objectName;
    }

    public void setGroup(IGroup group) 
    {
        this.group = group;
    }

    public void setContainsFrodoBranch(boolean containsFrodoBranch) 
    {
        logger.info("setContainsFrodoBranch(" + containsFrodoBranch + ")");
        this.containsFrodoBranch = containsFrodoBranch;
    }

    public void setIsALOTUSSequence(boolean isALotusSequence) 
    {
        logger.info("setIsALOTUSSequence(" + isALotusSequence + ")");
        this.isALotusSequence = isALotusSequence;
    }

    public void setContainsInvalidSupIRCamState() 
    {
        validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "The sequence contains a SupIRCam exposure that is not surrounded by Darks of the same duration."));
    }

    public void setContainsSlewButNoExposure() 
    {
        validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "The sequence contains at least one Slew, but has no defined Exposures."));
    }

    public void addValidationResults(ValidationResults validationResults) 
    {
        this.validationResults.addValidationResults(validationResults);
    }

    public ValidationResults getValidationResults() 
    {
        return validationResults;
    }

    public void receiveFixedGroupTimingConstraint(IGroup group) 
    {
        logger.info("receiveFixedGroupTimingConstraint(" + group + ")");
        validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "The group called " + group.getName() + " has a Fixed Timing constraint. We advise that you use the Future Fixed Groups reporting tool on the View menu to look for possible clashing groups."));
    }

    public void receiveSequenceComponent(ISequenceComponent component) 
    {
        //logger.info("receiveSequenceComponent(" + component + ")");

        if (component instanceof XExecutiveComponent) 
        {
            XExecutiveComponent xExecutiveComponent = (XExecutiveComponent) component;
            IExecutiveAction executiveAction = xExecutiveComponent.getExecutiveAction();

            if (executiveAction instanceof ISlew) 
            {
                receiveSlew((ISlew) executiveAction);
            } 
            else if (executiveAction instanceof IRotatorConfig) 
            {
                receiveRotatorConfig((IRotatorConfig) executiveAction);
            } 
            else if (executiveAction instanceof IInstrumentConfigSelector) 
            {
                receiveInstrumentConfig((IInstrumentConfigSelector) executiveAction);
            } 
            else if (executiveAction instanceof IBeamSteeringConfig) 
            {
                receiveBeamSteeringConfig((IBeamSteeringConfig) executiveAction);
            } 
            else if (executiveAction instanceof IExposure) 
            {
                receiveExposure((IExposure) executiveAction);
            } 
            else if (executiveAction instanceof IAcquisitionConfig) 
            {
                receiveAcquisitionConfig((IAcquisitionConfig) executiveAction);
            } 
            else if (executiveAction instanceof IAutoguiderConfig) 
            {
                IAutoguiderConfig autoguiderConfig = (IAutoguiderConfig) executiveAction;
                if ((autoguiderConfig.getAutoguiderCommand() == IAutoguiderConfig.ON) || (autoguiderConfig.getAutoguiderCommand() == IAutoguiderConfig.ON_IF_AVAILABLE)) 
                {
                    receiveAutoguiderOn();
                } 
                else 
                {
                    receiveAutoguiderOff();
                }
            } 
            else if (executiveAction instanceof ICalibration) 
            {
                receiveCalibration((ICalibration) executiveAction);
            }
        } 
        else if (component instanceof XBranchComponent) 
        {
            List childrenList = component.listChildComponents();
            XIteratorComponent firstIterator = (XIteratorComponent) childrenList.get(0);
            if (firstIterator != null) 
            {
                receiveBranchIterator(firstIterator);
            }
            XIteratorComponent secondIterator = (XIteratorComponent) childrenList.get(1);
            if (secondIterator != null) 
            {
                receiveBranchIterator(secondIterator);
            }
        }
        //set the previous component pointer
        previousComponent = component;
    }

    public void receiveSlew(ISlew slew)
    {
        logger.info("receiveSlew(" + slew + ")");

        this.latestSlew = slew;
        haveApertureOffsets = false;
        haveTarget = true;

        if (this.autoguiderMightBeOn) 
        {
            validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "A SLEW was attempted with the Autoguider ON."));
        }
               
        IRotatorConfig rotCfg = slew.getRotatorConfig();
        if (rotCfg != null) 
        {
            String instName = rotCfg.getInstrumentName();
            if (instName != null) 
            {
                if (instName.equalsIgnoreCase(CONST.RATCAM)) 
                {
                    validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "A slew was found that uses a rotator config that aligns to RATCam."));
                }
            }
        }

        ITarget target = slew.getTarget();
        if (!(target instanceof XEphemerisTarget)) 
        {
            if (slew.usesNonSiderealTracking()) 
            {
                //warning if target is not an ephemeris target, but non-sidereal tracking is being used
                validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "A slew was found that uses non-sidereal tracking for a non-ephemeris target."));
            }
        } 
        else
        {
            //validate Ephemeris Target
            XEphemerisTarget ephemerisTarget = (XEphemerisTarget) target;
            Iterator trackNodeIterator = ephemerisTarget.getEphemerisTrack().iterator();
            double previousRa = -1000; double previousDec = -1000;
            boolean shouldContinueIterating = true;
            while (trackNodeIterator.hasNext() && shouldContinueIterating)
            {
                XEphemerisTrackNode ephemerisTrackNode = (XEphemerisTrackNode) trackNodeIterator.next();
                //System.err.println("ra=" + ephemerisTrackNode.ra + " dec=" + ephemerisTrackNode.dec);
                
                if (ephemerisTrackNode.ra == previousRa)
                {
                    shouldContinueIterating = false;
                    validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "The target " + target.getName() + " is an ephemeris target with track nodes that are too close to one another. This will cause a slew failure."));
                } 
                else if (ephemerisTrackNode.dec == previousDec) 
                {
                    shouldContinueIterating = false;
                    validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "The target " + target.getName() + " is an ephemeris target with track nodes that are too close to one another. This will cause a slew failure."));
                }
                
                previousRa = ephemerisTrackNode.ra;
                previousDec = ephemerisTrackNode.dec;
            }
        }
    }

    public void receiveInstrumentConfig(IInstrumentConfigSelector instrumentConfigSelector)
    {
        logger.info("receiveInstrumentConfig(" + instrumentConfigSelector + ")");
        IInstrumentConfig instrumentConfig = instrumentConfigSelector.getInstrumentConfig();
        latestInstrumentConfigInstrumentName = instrumentConfig.getInstrumentName();

        //return on null instrument name
        if (latestInstrumentConfigInstrumentName == null)
        {
            validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "Instrument name is null"));
            return;
        }

        if (latestInstrumentConfigInstrumentName.equals(CONST.RATCAM)) {
            validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "RATCam is no longer on the telescope"));
        }

        if (latestInstrumentConfigInstrumentName.equals(CONST.IO_THOR))
        {
            validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "THOR is no longer on the telescope"));
        }

        if (latestInstrumentConfigInstrumentName.equals(CONST.IO_O))
        {
            XImagerInstrumentConfig imagerInstrumentConfig = (XImagerInstrumentConfig) instrumentConfig;

            List filterList = imagerInstrumentConfig.getFilterSpec().getFilterList();
            Iterator i = filterList.iterator();
            boolean neutralDensityFilterWasFound = false;
            while (i.hasNext())
            {
                XFilterDef filterDef = (XFilterDef) i.next();
                //if there's an ND filter in there, create a warning
                if (filterDef.getFilterName().equalsIgnoreCase(CONST.ND1_5) || filterDef.getFilterName().equalsIgnoreCase(CONST.ND3_0))
                {
                    neutralDensityFilterWasFound = true;
                }
            }
            if (neutralDensityFilterWasFound)
            {
                validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "A neutral density filter has been selected in an instrument config."));
            }
        }

        if (latestInstrumentConfigInstrumentName.equals(CONST.RINGO3))
        {
            validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "RINGO3 is no longer on the telescope"));
            //XPolarimeterInstrumentConfig polarimeterInstrumentConfig = (XPolarimeterInstrumentConfig) instrumentConfig;
            //int gain = polarimeterInstrumentConfig.getGain();
            //if (gain != 100)
            //{
            //    validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "A GAIN of " + gain + " is selected in a polarimeter config, the default is 100."));
            //}
        }
        
        if (latestInstrumentConfigInstrumentName.equals(CONST.MOPTOP))
        {
            XMoptopInstrumentConfig moptopInstrumentConfig = (XMoptopInstrumentConfig) instrumentConfig;
            latestMoptopRotorSpeed = moptopInstrumentConfig.getRotorSpeed();
            if ((latestMoptopRotorSpeed != XMoptopInstrumentConfig.ROTOR_SPEED_SLOW)&&(latestMoptopRotorSpeed != XMoptopInstrumentConfig.ROTOR_SPEED_FAST))
            {
                validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "MOPTOP needs a SLOW or FAST rotor speed."));
            }
        }
        
        if (latestInstrumentConfigInstrumentName.equals(CONST.LIRIC))
        {
            logger.info("receiveInstrumentConfig:LIRIC instrument is actually a:" + instrumentConfig);
            XLiricInstrumentConfig liricInstrumentConfig = (XLiricInstrumentConfig) instrumentConfig;
            int nudgematicOffsetSize = liricInstrumentConfig.getNudgematicOffsetSize();
            latestLiricCoaddExposureLength = liricInstrumentConfig.getCoaddExposureLength();
            
            if ((nudgematicOffsetSize != XLiricInstrumentConfig.NUDGEMATIC_OFFSET_SIZE_SMALL)&&
                    (nudgematicOffsetSize != XLiricInstrumentConfig.NUDGEMATIC_OFFSET_SIZE_LARGE)&&
                    (nudgematicOffsetSize != XLiricInstrumentConfig.NUDGEMATIC_OFFSET_SIZE_NONE))
            {
                validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "LIRIC nudgematic offset size should be SMALL, LARGE or NONE."));
            }
            // Coadd exposure length currently has to be 0, 100 or 1000 ms. This is defined by the .fmt files
            // available to the instrument robotic control layer
            if ((latestLiricCoaddExposureLength != 100)&&(latestLiricCoaddExposureLength != 1000))
            {
                validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "Illegal LIRIC Coadd Exposure Length."));
            }
        }
        
        if (latestInstrumentConfigInstrumentName.equals(CONST.LOTUS))
        {
            validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "LOTUS is no longer on the telescope"));
            //if (instrumentConfig instanceof XBlueTwoSlitSpectrographInstrumentConfig)
            //{
            //    XBlueTwoSlitSpectrographInstrumentConfig blueTwoSlitSpectrographInstrumentConfig = (XBlueTwoSlitSpectrographInstrumentConfig) instrumentConfig;
            //    int slitWidthNow = blueTwoSlitSpectrographInstrumentConfig.getSlitWidth();
                
            //    if (slitWidthNow != lastReceivedLOTUSSlitPosition)
            //    {
            //        if (this.autoguiderMightBeOn)
            //        {
            //            validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "A LOTUS SLIT position change was attempted with the Autoguider ON."));
            //        }
            //    }
            //    lastReceivedLOTUSSlitPosition = slitWidthNow;
            //}
        }
        haveInstrument = true;
    }

    public void receiveBeamSteeringConfig(IBeamSteeringConfig beamSteeringConfig) {
        logger.info("receiveBeamSteeringConfig(" + beamSteeringConfig + ")");

        haveBeamSteeringConfig = true;
    }

    public void receiveAcquisitionConfig(IAcquisitionConfig acquisitionConfig) {
        logger.info("receiveAcquisitionConfig()");
        /*
         if (acquisitionConfig.getAcquisitionInstrumentName() != null) {
         if (acquisitionConfig.getAcquisitionInstrumentName().equals(CONST.IO_O)) {
         validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE,  "The IO:O instrument is not yet available for acquisition."));
         }
         }
         if (acquisitionConfig.getTargetInstrumentName() != null) {
 
         if (acquisitionConfig.getTargetInstrumentName().equals(CONST.IO_O)) {
         validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE,  "The IO:O instrument is not yet available for science."));
         }
         }
         */

        String description = "";
        switch (acquisitionConfig.getMode()) {
            case IAcquisitionConfig.BRIGHTEST:
                description = "Fine Tune";
                break;
            case IAcquisitionConfig.WCS_FIT:
                description = "Fine Tune";
                break;
            case IAcquisitionConfig.INSTRUMENT_CHANGE:
                description = "Focal Plane";
                break;
        }

        if (acquisitionConfig.getAcquisitionInstrumentName() != null) {
            if (acquisitionConfig.getAcquisitionInstrumentName().equalsIgnoreCase(CONST.RATCAM)) {
                validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "An acquisition config refering to RATCam is not allowed."));
            }
        }
        if (acquisitionConfig.getTargetInstrumentName() != null) {
            if (acquisitionConfig.getTargetInstrumentName().equalsIgnoreCase(CONST.RATCAM)) {
                validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "An acquisition config refering to RATCam is not allowed."));
            }
        }

        if (latestSlew == null) {
            validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "A " + description + " was found without a Slew occurring before it."));
        }

        if (acquisitionConfig.getMode() == IAcquisitionConfig.INSTRUMENT_CHANGE) {
            haveApertureOffsets = true;
            logger.info("... haveApertureOffsets=true");
        } else {
            if (acquisitionConfig.getMode() == IAcquisitionConfig.WCS_FIT) {
                if (latestSlew != null) {
                    ITarget latestTarget = latestSlew.getTarget();
                    if (latestTarget instanceof XExtraSolarTarget) {
                        XExtraSolarTarget latestExtraSolarTarget = (XExtraSolarTarget) latestTarget;
                        double latestTargetEpoch = latestExtraSolarTarget.getEpoch();
                        if (latestTargetEpoch != 2000) {
                            validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "A target with an epoch of " + latestTargetEpoch + " has been requested with an acquisition mode of type WCS."));
                        }
                    }
                }
            }
        }
    }

    public void receiveCalibration(ICalibration calibration) {
        logger.info("receiveCalibration(" + calibration + ")");

        String calibrationType = "";
        boolean calibrationRequiresFRODOConfig = false;

        if (calibration instanceof XArc) {
            calibrationType = "an Arc";
            calibrationRequiresFRODOConfig = true;
        } else if (calibration instanceof XBias) {
            calibrationType = "a Bias";
        } else if (calibration instanceof XDark) {
            calibrationType = "a Dark";
        } else if (calibration instanceof XLampFlat) {
            calibrationType = "a Lamp-Flat";
            calibrationRequiresFRODOConfig = true;
        }

        if (!haveInstrument) {
            logger.info("... !haveInstrument");
            validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "An Instrument Config is required before " + calibrationType));
        }
        if (!haveApertureOffsets) {
            logger.info("... !haveApertureOffsets");
            validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "It is recommended that there is a Focal Plane before " + calibrationType));
        }
        if (!haveTarget) {
            logger.info("... !haveTarget");
            validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "It is recommended that you select a Target before " + calibrationType));
        }

        if (latestInstrumentConfigInstrumentName == null) {
            logger.info("... latestInstrumentConfigInstrumentName = null");
            validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, calibrationType + " exists without an instrument Config with a valid instrument name before it."));
        } else {
            if (!latestInstrumentConfigInstrumentName.startsWith(CONST.FRODO) && !latestInstrumentConfigInstrumentName.startsWith(CONST.SPRAT)) {
                if (calibrationRequiresFRODOConfig) {
                    logger.info("... latestInstrumentConfigInstrumentName = " + latestInstrumentConfigInstrumentName + ", but calibration is " + calibrationType);
                    validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "A FRODOSpec Instrument Config is required before " + calibrationType));
                }
            }
        }
    }

    public void receiveExposure(IExposure exposure) {
        logger.info("receiveExposure(" + exposure + ")");

        if (!haveInstrument) {
            logger.info("... !haveInstrument");
            validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "An Instrument Config is required before an Exposure."));
        }
        if (!haveApertureOffsets) {
            logger.info("... !haveApertureOffsets");
            validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "It is recommended that there is a Focal Plane before an Exposure."));
        }
        if (!haveTarget) {
            logger.info("... !haveTarget");
            validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "It is recommended that you select a Target before an Exposure."));
        }

        if (latestInstrumentConfigInstrumentName == null) 
        {
            logger.info("... latestInstrumentConfigInstrumentName = null");
            validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "An Exposure exists without an instrument Config with a valid instrument name before it."));
        } 
        else 
        {
            if (exposure instanceof XPeriodExposure) 
            {
                XPeriodExposure periodExposure = (XPeriodExposure) exposure;
                //i.e. a ringo 2/3/moptop duration exposure, make sure the last config was a ringo 2/3 one
                if (!(latestInstrumentConfigInstrumentName.equals(CONST.RINGO3)||
                        latestInstrumentConfigInstrumentName.equals(CONST.MOPTOP))) 
                {
                    logger.info("... latestInstrumentConfigInstrumentName !=" + CONST.RINGO3 + " or " + CONST.MOPTOP);
                    validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "A Duration Exposure exists without a RINGO/MOPTOP instrument config before it."));
                }
                if (latestInstrumentConfigInstrumentName.equals(CONST.RINGO3)) 
                {
                    if (periodExposure.getExposureTime() < 20000) 
                    {
                        validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "It is recommended that RINGO3 exposures last longer than 20 seconds"));
                    }
                }
                // Moptop period exposures can be between 1 and 100 rotations long.
                // The rotator moves at 45 deg/s (FAST rotor speed) or at 4.5 deg/s (SLOW rotor speed)
                // FAST 1 rotation
                // exposure_length_ms = 1*(360.0/45)*1000 = 8000ms = 8s
                // FAST 100 rotations
                // exposure_length_ms = 100*(360.0/45)*1000 = 800000 = 800s
                // SLOW 1 rotation
                // exposure_length_ms = 1*(360.0/4.5)*1000 = 80000 = 80s
                // SLOW 100 rotations
                // exposure_length_ms = 100*(360.0/4.5)*1000 = 8000000 = 8000s
                if (latestInstrumentConfigInstrumentName.equals(CONST.MOPTOP)) 
                {
                    if(latestMoptopRotorSpeed == XMoptopInstrumentConfig.ROTOR_SPEED_FAST)
                    {
                        if (periodExposure.getExposureTime() < 8000) 
                        {
                            validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "MOPTOP exposures must be at least 8 seconds long (1 rotation, FAST rotator speed)."));
                        }
                        if (periodExposure.getExposureTime() > 800000) 
                        {
                            validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "MOPTOP exposures cannot be longer than 800 seconds long (100 rotations, FAST rotator speed)."));
                        }
                    }
                    if(latestMoptopRotorSpeed == XMoptopInstrumentConfig.ROTOR_SPEED_SLOW)
                    {
                        if (periodExposure.getExposureTime() < 80000) 
                        {
                            validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "MOPTOP exposures must be at least 80 seconds long (1 rotation, SLOW rotator speed)."));
                        }
                        if (periodExposure.getExposureTime() > 8000000) 
                        {
                            validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "MOPTOP exposures cannot be longer than 8000 seconds long (100 rotations, SLOW rotator speed)."));
                        }
                    }
                }
            } else if (exposure instanceof XMultipleExposure) {

                XMultipleExposure multipleExposure = (XMultipleExposure) exposure;
                if (latestInstrumentConfigInstrumentName.equals(CONST.IO_O)) {
                    //if it's for IO:O, make sure exposure length is okay
                    if (multipleExposure.getExposureTime() < 1000) {
                        validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "IO:O Exposure Times < 1 second are not allowed."));
                    } else if (multipleExposure.getExposureTime() < 10000) {
                        validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "IO:O exposure times < 10 seconds will have significant shutter effects."));
                    }
                    //make sure a BeamSteer has been received.
                    /*
                     if (!haveBeamSteeringConfig) {
                     logger.info("... latestInstrumentConfigInstrumentName == " + CONST.IO_O + ", but no BeamSteer received");
                     validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE,  "An Exposure exists using the IO:O instrument that doesn't have a Beam-Steering Config before it."));
                     } 
                     */
                } 
                else if (latestInstrumentConfigInstrumentName.equals(CONST.IO_I)) 
                {
                    if (multipleExposure.getExposureTime() < 5820) {
                        validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "IO:I Exposure Times < 5820ms are not allowed."));
                    } else if ((multipleExposure.getExposureTime() >= 7276) && (multipleExposure.getExposureTime() <= 8729)) {
                        validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "IO:I Exposure Times within the range 7276ms to 8729ms are not allowed."));
                    }
                } 
                else if (latestInstrumentConfigInstrumentName.equals(CONST.LIRIC)) 
                {
                    if (multipleExposure.getExposureTime() < latestLiricCoaddExposureLength)
                    {
                        validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "LIRIC Exposure Times less than the coadd exposure length are not allowed."));                    
                    }
                }
                else if (latestInstrumentConfigInstrumentName.equals(CONST.RINGO3))
                {
                    logger.info("... latestInstrumentConfigInstrumentName == " + CONST.RINGO3);
                    validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "A Multiple Exposure exists with a RINGO instrument config before it. Use only 'Duration' type exposures for RINGO."));
                } 
                else if (latestInstrumentConfigInstrumentName.equals(CONST.LOTUS)) 
                {
                    if (multipleExposure.getExposureTime() < 3000) 
                    {
                        validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "LOTUS Exposure Times < 3 seconds are not allowed."));
                    } 
                    else if (multipleExposure.getExposureTime() > 300000)
                    {
                        validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "LOTUS Exposure Times > 300 seconds are not allowed."));
                    }
                } /*else if (latestInstrumentConfigInstrumentName.equals(CONST.IO_THOR)) {
                 logger.info("... latestInstrumentConfigInstrumentName == " + CONST.IO_THOR);
                 if (multipleExposure.getExposureTime() > 10000) {
                 validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE,  "An exposure of > 10 seconds has been requested on the IO:THOR instrument. With this integration time, THOR will saturate."));
                 }
                 if (group != null) {
                 //we have a group (i.e. we're not just validating the sequence)
                 //we have THOR as our instrument
                 //validate the group's sky brightness constraint if it is 6, 10 or unconstrained then throw up a warning
                 boolean foundSkyBrightnessConstraint = false;
                 List obervingConstraints =  group.listObservingConstraints();
                 Iterator oci = obervingConstraints.iterator();
                 while (oci.hasNext()) { 
                 IObservingConstraint observingConstraint = (IObservingConstraint) oci.next();
                 if (observingConstraint instanceof XSkyBrightnessConstraint) {
                 foundSkyBrightnessConstraint = true;
                 XSkyBrightnessConstraint skyBrightnessConstraint = (XSkyBrightnessConstraint)observingConstraint;
                 int skyBCat = skyBrightnessConstraint.getSkyBrightnessCategory();
                 boolean foundInvalidSkyBrightnessConstraint = false; 
                 String skyBDesc = "";
                 switch (skyBCat) {
                 case IObservingConstraint.MAG_6:
                 foundInvalidSkyBrightnessConstraint = true;
                 skyBDesc = "6";
                 break;
                 case IObservingConstraint.MAG_10:
                 foundInvalidSkyBrightnessConstraint = true;
                 skyBDesc = "10";
                 break;
                 case IObservingConstraint.DAYTIME:
                 foundInvalidSkyBrightnessConstraint = true;
                 skyBDesc = "DAYTIME";
                 break;
                 default:
                 break;
                 }
                 if (foundInvalidSkyBrightnessConstraint) {
                 validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING,  "This group has a sky brightness of '" + skyBDesc + "', and the IO:THOR instrument has been selected within the observation sequence. This will probably lead to saturation."));
                 }
                 }
                 } // / while
                 if (foundSkyBrightnessConstraint == false) {
                 validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING,  "This group has no sky brightness constraint set and the IO:THOR instrument has been selected within the observation sequence. This will possibly lead to saturation."));
                 }
                 }
                 }
                 */

                //check for invalid mult-run fields
                if (multipleExposure.getRepeatCount() <= 0) {
                    logger.info("... multipleExposure with <=0 repeatCount");
                    validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "A Multiple Exposure exists with a zero or negative repeat count."));
                }
                if (multipleExposure.getExposureTime() < 0) {
                    logger.info("... multipleExposure with < 0 exposure time");
                    validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "A Multiple Exposure exists with a negative exposure time."));
                }

            } else if (exposure instanceof XPeriodRunAtExposure)
            {
                //make sure that the last config was not a ringo2/ringo3/moptop one
                if (latestInstrumentConfigInstrumentName.equals(CONST.RINGO3)||
                        latestInstrumentConfigInstrumentName.equals(CONST.MOPTOP)) 
                {
                    logger.info("... latestInstrumentConfigInstrumentName == " + CONST.RINGO3 +" or " + CONST.MOPTOP);
                    validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "A Multiple Exposure exists with a RINGO3/MOPTOP instrument config before it. Use only 'Duration' type exposures for RINGO3/MOPTOP."));
                }
                //placeholder
            }
        }
    }

    public void receiveRotatorConfig(IRotatorConfig rotatorConfig) {

        String instrumentName = rotatorConfig.getInstrumentName();

        if (rotatorConfig.getRotatorMode() == IRotatorConfig.CARDINAL) {
            if (!haveTarget) {
                logger.info("... !haveTarget and received cardinal rotator");
                validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "A Target is required before a Rotator-Config."));
            }

            if (instrumentName != null) {
                if (instrumentName.equalsIgnoreCase(CONST.RATCAM)) {
                    validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "A slew was found that uses a rotator config that aligns to RATCam."));
                }
            }
        } else if (rotatorConfig.getRotatorMode() == IRotatorConfig.MOUNT) {
            //no instrument chweck necessary

        } else if (rotatorConfig.getRotatorMode() == IRotatorConfig.SKY) {
            if (instrumentName != null) {
                if (instrumentName.equalsIgnoreCase(CONST.RATCAM)) {
                    validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "A slew was found that uses a rotator config that aligns to RATCam."));
                }
            }
        }

    }

    public void receiveBranchIterator(XIteratorComponent iteratorComponent) {
        logger.info("receiveBranchIterator(" + iteratorComponent + ")");

        List branchChildren = iteratorComponent.listChildComponents();
        if (branchChildren == null) {
            return;
        }
        Iterator i = branchChildren.iterator();
        boolean instrumentConfigInBranch = false; //keep track of this local to the arm (aperture offsets and target are affected by sequence components outside the arm)
        while (i.hasNext()) {

            ISequenceComponent sequenceComponent = (ISequenceComponent) i.next();
            if (sequenceComponent instanceof XExecutiveComponent) {
                IExecutiveAction executiveAction = ((XExecutiveComponent) sequenceComponent).getExecutiveAction();
                if (executiveAction instanceof IInstrumentConfigSelector) {
                    instrumentConfigInBranch = true;
                }

                if (executiveAction instanceof IExposure) {
                    //same as receive Exposure
                    if (!instrumentConfigInBranch) {
                        logger.info("... !instrumentConfigInBranch");
                        validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.FAILURE, "An Instrument Config is required before an Exposure."));
                    }
                    if (!haveApertureOffsets) {
                        logger.info("... !haveApertureOffsets");
                        validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "It is recommended that there is a Focal Plane before an Exposure."));
                    }
                    if (!haveTarget) {
                        logger.info("... !haveTarget");
                        validationResults.addValidationResult(new ValidationResult(objectName, ValidationResult.WARNING, "It is recommended that you select a Target before an Exposure."));
                    }
                }
            }
        }
    }

    public void receiveAutoguiderOff() {
        autoguiderMightBeOn = false;
    }
    
    public void receiveAutoguiderOn() {
        logger.info("receiveAutoguiderOn()");

        autoguiderMightBeOn = true;
            
        //if an autoguider on has been received, we need to make sure that the previous slew didn't use non-sidereal tracking
        if (latestSlew != null) {
            //here - check if last slew had non-sidereal tracking in it, if so give warning
            if (latestSlew.usesNonSiderealTracking()) {
                ValidationResult agOnNonSidTrackingValidationResult = new ValidationResult(objectName, ValidationResult.WARNING, "The autoguider is requested on a non-sidereal tracking target.");
                validationResults.addValidationResult(agOnNonSidTrackingValidationResult);
            }
        }

        //if this sequence has a frodo branch in it, we need to make sure that directly previous to an autoguide command there is a frodospec instrument config
        if (this.containsFrodoBranch) {

            //set up the message
            ValidationResult frodoInstConfigWarningValidationResult = new ValidationResult(objectName, ValidationResult.WARNING, "A FRODOSpec Instrument Config is required before an Autoguider command for FRODOSpec sequences.");

            logger.info("... this.containsFrodoBranch=true");
            if (previousComponent != null) {
                logger.info("... previousComponent != null, previousComponent=" + previousComponent);
                if (previousComponent instanceof XExecutiveComponent) {
                    XExecutiveComponent previousComponentAsExecutiveComponent = (XExecutiveComponent) previousComponent;
                    IExecutiveAction previousExecutiveAction = previousComponentAsExecutiveComponent.getExecutiveAction();
                    if (previousExecutiveAction instanceof XInstrumentConfigSelector) {
                        XInstrumentConfigSelector instrumentConfigSelector = (XInstrumentConfigSelector) previousExecutiveAction;
                        if (instrumentConfigSelector.getInstrumentConfig() instanceof XDualBeamSpectrographInstrumentConfig) {
                            //ok, that's what we want.
                            logger.info("... previousExecutiveAction is instrument config selector, and is for XDualBeamSpectrographInstrumentConfig, OK");
                        } else {
                            logger.info("... previousExecutiveAction is instrument config selector, but not for XDualBeamSpectrographInstrumentConfig, previousComponent=" + previousComponent);
                            validationResults.addValidationResult(frodoInstConfigWarningValidationResult);
                        }
                    } else {
                        logger.info("... previousExecutiveAction NOT instanceof XInstrumentConfigSelector, previousComponent=" + previousComponent);
                        validationResults.addValidationResult(frodoInstConfigWarningValidationResult);
                    }
                } else {
                    logger.info("... previousComponent NOT instanceof XExecutiveComponent, previousComponent=" + previousComponent);
                    validationResults.addValidationResult(frodoInstConfigWarningValidationResult);
                }
            }
        }

        //set up the message
        ValidationResult lotusInstConfigWarningValidationResult = new ValidationResult(objectName, ValidationResult.WARNING, "A LOTUS Instrument Config is required before an Autoguider command for LOTUS sequences.");

        //if the sequence either has an instrument config selecting LOTUS, or a FINE TUNE where LOTUS is the selected science instrument.
        //then, check to see if the previous component was a XBlueTwoSlitSpectrographInstrumentConfig, if not, add warning
        if (isALotusSequence) {
            //if this sequence has LOTUS instrument config in it, we need to make sure that directly previous to an autoguide command there is a frodospec instrument config
            if (latestInstrumentConfigInstrumentName == null) {
                //latestInstrumentConfigInstrumentName is null, but we know it's a LOTUS sequence
                //therefore, there is no instrument config before this received AG on. Therefore, warn. 
                logger.info("... no instrument config before AG, previousComponent=" + previousComponent);
                validationResults.addValidationResult(lotusInstConfigWarningValidationResult);
            } else {
                if (latestInstrumentConfigInstrumentName.equals(CONST.LOTUS)) {
                    logger.info("... latestInstrumentConfigInstrumentName.equals(CONST.LOTUS)");
                    if (previousComponent != null) {
                        logger.info("... previousComponent != null, previousComponent=" + previousComponent);
                        if (previousComponent instanceof XExecutiveComponent) {
                            XExecutiveComponent previousComponentAsExecutiveComponent = (XExecutiveComponent) previousComponent;
                            IExecutiveAction previousExecutiveAction = previousComponentAsExecutiveComponent.getExecutiveAction();
                            if (previousExecutiveAction instanceof XInstrumentConfigSelector) {
                                XInstrumentConfigSelector instrumentConfigSelector = (XInstrumentConfigSelector) previousExecutiveAction;
                                if (instrumentConfigSelector.getInstrumentConfig() instanceof XBlueTwoSlitSpectrographInstrumentConfig) {
                                    //ok, that's what we want.
                                    logger.info("... previousExecutiveAction is instrument config selector, and is for XBlueTwoSlitSpectrographInstrumentConfig, OK");
                                } else {
                                    logger.info("... previousExecutiveAction is instrument config selector, but not for XBlueTwoSlitSpectrographInstrumentConfig, previousComponent=" + previousComponent);
                                    validationResults.addValidationResult(lotusInstConfigWarningValidationResult);
                                }
                            } else {
                                logger.info("... previousExecutiveAction NOT instanceof XInstrumentConfigSelector, previousComponent=" + previousComponent);
                                validationResults.addValidationResult(lotusInstConfigWarningValidationResult);
                            }
                        } else {
                            logger.info("... previousComponent NOT instanceof XExecutiveComponent, previousComponent=" + previousComponent);
                            validationResults.addValidationResult(lotusInstConfigWarningValidationResult);
                        }
                    }
                }
            }
        }

    }
}
