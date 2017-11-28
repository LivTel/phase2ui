/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.render;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import ngat.astrometry.Position;
import ngat.phase2.IAcquisitionConfig;
import ngat.phase2.IDetectorConfig;
import ngat.phase2.IExecutiveAction;
import ngat.phase2.IRotatorConfig;
import ngat.phase2.ITarget;
import ngat.phase2.ITargetSelector;
import ngat.phase2.ITipTiltAbsoluteOffset;
import ngat.phase2.XAcquisitionConfig;
import ngat.phase2.XApertureConfig;
import ngat.phase2.XArc;
import ngat.phase2.XAutoguiderConfig;
import ngat.phase2.XBeamSteeringConfig;
import ngat.phase2.XBias;
import ngat.phase2.XBlueTwoSlitSpectrographInstrumentConfig;
import ngat.phase2.XBranchComponent;
import ngat.phase2.XDark;
import ngat.phase2.XDualBeamSpectrographInstrumentConfig;
import ngat.phase2.XEphemerisTarget;
import ngat.phase2.XExecutiveComponent;
import ngat.phase2.XExtraSolarTarget;
import ngat.phase2.XFilterSpec;
import ngat.phase2.XFocusControl;
import ngat.phase2.XFocusOffset;
import ngat.phase2.XImagerInstrumentConfig;
import ngat.phase2.XImagingSpectrographInstrumentConfig;
import ngat.phase2.XInstrumentConfig;
import ngat.phase2.XInstrumentConfigSelector;
import ngat.phase2.XIteratorComponent;
import ngat.phase2.XIteratorRepeatCountCondition;
import ngat.phase2.XLampFlat;
import ngat.phase2.XMultipleExposure;
import ngat.phase2.XOpticalSlideConfig;
import ngat.phase2.XOrbitalElementsTarget;
import ngat.phase2.XPeriodExposure;
import ngat.phase2.XPeriodRunAtExposure;
import ngat.phase2.XPolarimeterInstrumentConfig;
import ngat.phase2.XMoptopInstrumentConfig;
import ngat.phase2.XPositionOffset;
import ngat.phase2.XRotatorConfig;
import ngat.phase2.XSlaNamedPlanetTarget;
import ngat.phase2.XSlew;
import ngat.phase2.XSpectrographInstrumentConfig;
import ngat.phase2.XTarget;
import ngat.phase2.XTargetSelector;
import ngat.phase2.XTipTiltAbsoluteOffset;
import ngat.phase2.XTipTiltImagerInstrumentConfig;
import ngat.phase2.util.Rounder;

/**
 *
 * @author nrc
 */
public class ObservationSequenceComponentRenderer {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm z");
    private static ObservationSequenceComponentRenderer instance;

    public static ObservationSequenceComponentRenderer getInstance() {
        if (instance == null) {
            instance = new ObservationSequenceComponentRenderer();
        }
        return instance;
    }

    private ObservationSequenceComponentRenderer() {
        dateFormat.setDateFormatSymbols(new DateFormatSymbols(Locale.UK)); //make sure months are spelt in English
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public String getTextRendering(Object  value) {
        return getTextRendering(value, false);
    }

    /*
    public String getTextRendering(MosTableCellData mosTableCellData, boolean shortDescription) {
        Object value = mosTableCellData.getComponent();
        return getTextRendering(value, shortDescription);
    }
    */

    public String getTextRendering(Object value, boolean shortDescription) {
        String shortText = "";
        String postText = "";
        if (value == null) {
            return "";
        }
        if (value.getClass() == null) {
            return "";
        }
        
        if (value.getClass().equals(String.class)) {
            shortText = (String) value; //in the case of root nodes created by DefaultTreeModel
        /*
        } else if (value.getClass().equals(MosTableStartOfIteratorComponent.class)) {
            MosTableStartOfIteratorComponent mosTableStartOfIteratorComponent = (MosTableStartOfIteratorComponent) value;
            shortText = "START ----- (" + mosTableStartOfIteratorComponent.getDepth() + ")";
        } else if (value.getClass().equals(MosTableEndOfIteratorComponent.class)) {
            MosTableEndOfIteratorComponent mosTableEndOfIteratorComponent = (MosTableEndOfIteratorComponent) value;
            shortText = "----- END (" + mosTableEndOfIteratorComponent.getDepth() + ")";
        */
        } else if (value.getClass().equals(XIteratorComponent.class)) {
            XIteratorComponent iteratorComponent = (XIteratorComponent)value;
            String description = iteratorComponent.getComponentName();
            XIteratorRepeatCountCondition repeatCountCondition= (XIteratorRepeatCountCondition) iteratorComponent.getCondition();
            int repeatCount = repeatCountCondition.getCount();
            shortText="[ITERATOR] ";
            postText ="REPEAT COUNT, repeats=" + repeatCount + " " + description;
        } else if (value.getClass().equals(XBranchComponent.class)) {
            XBranchComponent xBranchComponent = (XBranchComponent)value;
             shortText="[BRANCH] ";
             postText = xBranchComponent.getComponentName();
        } else if (value.getClass().equals(XExecutiveComponent.class)) {
            XExecutiveComponent executiveComponent = (XExecutiveComponent)value;
            IExecutiveAction executiveAction = executiveComponent.getExecutiveAction();
            if (executiveAction.getClass().equals(XAcquisitionConfig.class)) {
                XAcquisitionConfig acquisitionConfig = (XAcquisitionConfig)executiveAction;
                int mode = acquisitionConfig.getMode();
                String precision;
                switch (mode) {
                    case IAcquisitionConfig.BRIGHTEST:
                            shortText ="[Fine Tune] ";
                            postText ="Mode: BRIGHTEST, ";
                            postText += "Acquisition Instrument: " +acquisitionConfig.getAcquisitionInstrumentName();
                            postText += ", Science Instrument: " +acquisitionConfig.getTargetInstrumentName();
                            postText += ", Allow alt.: " +acquisitionConfig.getAllowAlternative();
                            
                            if (acquisitionConfig.getPrecision() == IAcquisitionConfig.PRECISION_HIGH){
                                precision = "HIGH_PRECISION";
                            } else if (acquisitionConfig.getPrecision() == IAcquisitionConfig.PRECISION_NORMAL){
                                precision = "NORMAL_PRECISION";
                            } else if (acquisitionConfig.getPrecision() == IAcquisitionConfig.PRECISION_NOT_SET){
                                precision = "NO_PRECISION_SET";
                            } else {
                                precision = "UNKNOWN";
                            }
                            postText += ", Precision: " + precision;
                            break;
                    case IAcquisitionConfig.WCS_FIT:
                            shortText ="[Fine Tune] ";
                            postText +="Mode: WCS_FIT, ";
                            postText += "Acquisition Instrument: " +acquisitionConfig.getAcquisitionInstrumentName();
                            postText += ", Science Instrument: " +acquisitionConfig.getTargetInstrumentName();
                            postText += ", Allow alt.: " +acquisitionConfig.getAllowAlternative();
                            
                            if (acquisitionConfig.getPrecision() == IAcquisitionConfig.PRECISION_HIGH){
                                precision = "HIGH_PRECISION";
                            } else if (acquisitionConfig.getPrecision() == IAcquisitionConfig.PRECISION_NORMAL){
                                precision = "NORMAL_PRECISION";
                            } else if (acquisitionConfig.getPrecision() == IAcquisitionConfig.PRECISION_NOT_SET){
                                precision = "NO_PRECISION_SET";
                            } else {
                                precision = "UNKNOWN";
                            }
                            postText += ", Precision: " + precision;
                            break;
                    case IAcquisitionConfig.INSTRUMENT_CHANGE:
                        shortText = "[Focal Plane] ";
                        postText = "Target Instrument: " + acquisitionConfig.getTargetInstrumentName();
                        break;
                }
            } else if (executiveAction.getClass().equals(XAutoguiderConfig.class)) {
                XAutoguiderConfig autoguiderConfig = (XAutoguiderConfig)executiveAction;
                shortText="[Autoguider Config] ";
                postText = "Command: " + XAutoguiderConfig.toModeString(autoguiderConfig.getAutoguiderCommand());
            } else if (executiveAction.getClass().equals(XMultipleExposure.class)) {
                XMultipleExposure multipleExposure = (XMultipleExposure) executiveAction;
                shortText="[Exposure] ";
                postText = "(Multrun) " + (int)multipleExposure.getRepeatCount() + " x " + (double)(multipleExposure.getExposureTime()/1000) + "sec ";
                if (multipleExposure.isStandard()) {
                    postText+= "(Standard)";
                }
            } else if (executiveAction.getClass().equals(XPeriodExposure.class)) {
                XPeriodExposure periodExposure = (XPeriodExposure) executiveAction;
                shortText="[Exposure] ";
                postText = "(Duration) " + (double)(periodExposure.getExposureTime()/1000) + "sec ";
                if (periodExposure.isStandard()) {
                    postText+= "(Standard)";
                }
            } else if (executiveAction.getClass().equals(XPeriodRunAtExposure.class)) {
                XPeriodRunAtExposure periodExposure = (XPeriodRunAtExposure) executiveAction;
                shortText="[Exposure] ";
                postText = "(Total Duration) " + (double)(periodExposure.getTotalExposureDuration()/1000) + "sec ";
                postText+="(Length) " + (double)(periodExposure.getExposureLength()/1000) + "sec ";

                long runAtTime = periodExposure.getRunAtTime();
                if (runAtTime > 0) {
                    String dateFormatted = dateFormat.format(new Date(runAtTime));
                    postText+= "(Run-At) ";
                    postText += dateFormatted;
                }
                
                if (periodExposure.isStandard()) {
                    postText+= " (Standard)";
                }
            } else if (executiveAction.getClass().equals(XFocusOffset.class)) {
                XFocusOffset focusOffset = (XFocusOffset)executiveAction;
                String offsetType = "";
                if (focusOffset.isRelative()) {
                    offsetType = "CUMULATIVE";
                } else {
                    offsetType = "NON-CUMULATIVE";
                }
                shortText="[Focus Offset] ";
                postText = offsetType + ", Amount: " + Rounder.round(focusOffset.getOffset(), 3) + " mm";
            } else if (executiveAction.getClass().equals(XInstrumentConfig.class)) {
                shortText="[Instrument Config] ";
                postText += executiveComponent.getComponentName();
           } else if (executiveAction.getClass().equals(XInstrumentConfigSelector.class)) {
                XInstrumentConfigSelector instrumentConfigSelector = (XInstrumentConfigSelector)executiveAction;
                XInstrumentConfig instrumentConfig = (XInstrumentConfig)instrumentConfigSelector.getInstrumentConfig();
                if (instrumentConfig != null) {
                    if (instrumentConfig.getClass().equals(XDualBeamSpectrographInstrumentConfig.class)) {
                        shortText="[Dual Beam Spectrograph Config] ";
                        postText = "Name: " + instrumentConfig.getName();
                    } else if (instrumentConfig.getClass().equals(XImagerInstrumentConfig.class)) {
                        shortText="[Imager Instrument Config] ";
                        postText = "Name: " + instrumentConfig.getName();
                        postText += " " + getDetectorConfigShortText(instrumentConfig.getDetectorConfig());
                        XImagerInstrumentConfig imagerInstrumentConfig = (XImagerInstrumentConfig) instrumentConfig;
                        postText += " " + getFilterSpecShortText(imagerInstrumentConfig.getFilterSpec());
                    } else if (instrumentConfig.getClass().equals(XPolarimeterInstrumentConfig.class)) {
                        shortText="[Polarimeter Instrument Config] ";
                        postText = "Name: " + instrumentConfig.getName();
                    } else if (instrumentConfig.getClass().equals(XMoptopInstrumentConfig.class)) {
                        shortText="[Moptop Polarimeter Instrument Config] ";
                        postText = "Name: " + instrumentConfig.getName();
                    } else if (instrumentConfig.getClass().equals(XSpectrographInstrumentConfig.class)) {
                        shortText = "[Spectrograph Instrument Config] ";
                        postText = "Name: " + instrumentConfig.getName();
                    } else if (instrumentConfig.getClass().equals(XTipTiltImagerInstrumentConfig.class)) {
                        shortText = "[Tip-Tilt Imager Instrument Config] ";
                        postText = "Name: " + instrumentConfig.getName();
                    } else if (instrumentConfig.getClass().equals(XImagingSpectrographInstrumentConfig.class)) {
                        shortText = "[Imaging Spectrograph Instrument Config] ";
                        postText = "Name: " + instrumentConfig.getName();
                        postText += " " + getDetectorConfigShortText(instrumentConfig.getDetectorConfig());
                        XImagingSpectrographInstrumentConfig imagingSpectrographInstrumentConfig = (XImagingSpectrographInstrumentConfig) instrumentConfig;
                        postText += " " + InstrumentConfigRenderer.getSummaryOfInstrumentConfig(imagingSpectrographInstrumentConfig);
                    } else if (instrumentConfig.getClass().equals(XBlueTwoSlitSpectrographInstrumentConfig.class)) {
                        shortText = "[Blue Two Slit Spectrograph Instrument Config] ";
                        postText = "Name: " + instrumentConfig.getName();
                        postText += " " + getDetectorConfigShortText(instrumentConfig.getDetectorConfig());
                        XBlueTwoSlitSpectrographInstrumentConfig blueTwoSlitSpectrographInstrumentConfig = (XBlueTwoSlitSpectrographInstrumentConfig) instrumentConfig;
                        postText += " " + InstrumentConfigRenderer.getSummaryOfInstrumentConfig(blueTwoSlitSpectrographInstrumentConfig);
                    } else {
                        shortText="[UNKNOWN INSTRUMENT CONFIG TYPE] ";
                        postText = instrumentConfig.getClass().getName();
                    }
                } else {
                    shortText="[Null Instrument Config]";
                }
            } else if (executiveAction.getClass().equals(XPositionOffset.class)) {
                XPositionOffset positionOffset = (XPositionOffset) executiveAction;
                double decRadOffset = positionOffset.getDecOffset();
                double raRadOffset = positionOffset.getRAOffset();
                double decArcSecOffset = Math.toDegrees(decRadOffset) * 3600;
                double raArcSecOffset = Math.toDegrees(raRadOffset) * 3600;
                String offsetType = "";
                if (positionOffset.isRelative()) {
                    offsetType = "CUMULATIVE";
                } else {
                    offsetType = "NON_CUMULATIVE";
                }
                shortText="[Position Offset] ";
                postText = offsetType + " (" + Rounder.round(raArcSecOffset, 1) + "), (" + Rounder.round(decArcSecOffset, 1) + ")";
            } else if (executiveAction.getClass().equals(XRotatorConfig.class)) {
                shortText = getRotatorDescription((XRotatorConfig)executiveAction, shortDescription);
            } else if (executiveAction.getClass().equals(XApertureConfig.class)) {
                shortText="[Aperture Offset] ";
                postText += executiveComponent.getComponentName();
            } else if (executiveAction.getClass().equals(XTargetSelector.class)) {
                ITargetSelector targetSelector = (ITargetSelector)executiveAction;
                XTarget target = (XTarget)targetSelector.getTarget();
                shortText = getTargetDescription(target, shortDescription);
            } else if (executiveAction.getClass().equals(XArc.class)) {
                XArc arc = (XArc) executiveAction;
                shortText="[Arc] ";
                postText += arc.getLamp().getLampName();
            } else if (executiveAction.getClass().equals(XBias.class)) {
                XBias bias = (XBias) executiveAction;
                shortText="[Bias]";
            } else if (executiveAction.getClass().equals(XDark.class)) {
                XDark dark = (XDark) executiveAction;
                shortText="[Dark] ";
                postText += (int)dark.getExposureTime() + "ms";
            } else if (executiveAction.getClass().equals(XLampFlat.class)) {
                XLampFlat lampFlat = (XLampFlat) executiveAction;
                shortText="[Lamp Flat] ";
                postText += lampFlat.getLamp().getLampName();
            } else if (executiveAction.getClass().equals(XSlew.class)) {
                XSlew slew = (XSlew) executiveAction;
                String slewDescription = "[Slew]";
                String targetText = getTargetDescription(slew.getTarget());

                IRotatorConfig rotatorConfig = slew.getRotatorConfig();
                String rotatorText = getRotatorDescription(rotatorConfig);
                String trackingText = getTrackingText(slew.usesNonSiderealTracking());
                shortText= slewDescription;
                postText += " " +   targetText + " " + rotatorText + " " + trackingText;
            } else if (executiveAction.getClass().equals(XFocusControl.class)) {
                XFocusControl focusControl = (XFocusControl) executiveAction;
                String focusControlDescription = "[Focus Control]";

                shortText= focusControlDescription;
                String instrumentText = focusControl.getInstrumentName();
                postText += " Instrument Name:" +   instrumentText;

            } else if (executiveAction.getClass().equals(XTipTiltAbsoluteOffset.class)) {
                shortText= "[Tip-Tilt Absolute Offset]";
                XTipTiltAbsoluteOffset tipTiltAbsoluteOffset = (XTipTiltAbsoluteOffset) executiveAction;
                switch (tipTiltAbsoluteOffset.getTipTiltId()) {
                    case ITipTiltAbsoluteOffset.TIPTILT_TOP:
                        postText += " " + "Tip-Tilt: UPPER";
                        break;
                    case ITipTiltAbsoluteOffset.TIPTILT_BOTTOM:
                        postText += " " + "Tip-Tilt: LOWER";
                        break;
                }
                switch (tipTiltAbsoluteOffset.getOffsetType()) {
                    case ITipTiltAbsoluteOffset.OFFSET_TYPE_FOCAL_PLANE:
                        postText += " Type: " + "FOCAL PLANE";
                        postText += " Instrument:" + tipTiltAbsoluteOffset.getInstrumentName();
                        break;
                    case ITipTiltAbsoluteOffset.OFFSET_TYPE_SKY:
                        postText += " Type: " + "SKY";
                        break;
                }
                
                postText += " Offset: (" + Rounder.round(tipTiltAbsoluteOffset.getOffset1(), 1) + " arcsec, " + Rounder.round(tipTiltAbsoluteOffset.getOffset2(), 1) + " arcsec)";

            } else if (executiveAction.getClass().equals(XBeamSteeringConfig.class)) {
                shortText= "[Beam-Steering Config] ";
                XBeamSteeringConfig beamSteeringConfig = (XBeamSteeringConfig) executiveAction;
                XOpticalSlideConfig opticalSlideConfig1 =  beamSteeringConfig.getUpperSlideConfig();
                String opticalSlideConfig1Description = "Slide: " + opticalSlideConfig1.getSlideName() + "  @ " + opticalSlideConfig1.getElementName();
                XOpticalSlideConfig opticalSlideConfig2 =  beamSteeringConfig.getLowerSlideConfig();
                String opticalSlideConfig2Description = "Slide: " + opticalSlideConfig2.getSlideName() + " @ " + opticalSlideConfig2.getElementName();
                postText += opticalSlideConfig1Description + " ; " + opticalSlideConfig2Description;

            } else {
                shortText="[UNKNOWN COMPONENT TYPE] " +executiveAction.getClass().getName();
            }
        } else {
            shortText="[UNKNOWN COMPONENT TYPE] " +value.getClass().getName();
        }

        if (shortDescription) {
            return shortText;
        } else {
            return shortText + postText;
        }
    }

    private String getDetectorConfigShortText(IDetectorConfig detectorConfig) {
        String s = "";
        s += "[" + detectorConfig.getXBin() + "x" + detectorConfig.getYBin() + "]";
        return s;
    }

    private String getFilterSpecShortText(XFilterSpec filterSpec) {
        String s = "";
        s += "[" + filterSpec.getFiltersString();
        s += "]";
        return s;
    }

    private String getTargetDescription(ITarget target) {
        return getTargetDescription(target, false);
    }

    private String getTargetDescription(ITarget target, boolean shortDescription) {
        String shortText = "";
        String postText = "";

        if (target != null) {
            if (target.getClass().equals(XEphemerisTarget.class)) {
                shortText="[Ephemeris Target] ";
                postText = target.getName();
            } else if (target.getClass().equals(XExtraSolarTarget.class)) {
                XExtraSolarTarget extraSolarTarget = (XExtraSolarTarget)target;
                shortText="[Extra Solar Target] ";
                postText = target.getName();
                postText += " RA:[" + Position.toHMSString(extraSolarTarget.getRa()) + "]";
                postText +=  " Dec:[" + Position.toDMSString(extraSolarTarget.getDec()) + "]";
            } else if (target.getClass().equals(XOrbitalElementsTarget.class)) {
                shortText="[Orbital Elements Target] ";
                postText += target.getName();
            } else if (target.getClass().equals(XSlaNamedPlanetTarget.class)) {
                shortText="[Sla Named Target] ";
                postText += target.getName();
            } else {
                shortText="[UNKNOWN TARGET TYPE] " + target.getClass().getName();
            }
        } else {
            shortText="[Null Target] ";
        }

        if (shortDescription) {
            return shortText;
        } else {
            return shortText + postText;
        }
    }

    private String getRotatorDescription(IRotatorConfig rotatorConfig) {
        return getRotatorDescription(rotatorConfig, false);
    }

    private String getRotatorDescription(IRotatorConfig rotatorConfig, boolean shortDescription) {
        String shortText = "";
        String postText = "";

        double rotAngle = Rounder.round(Math.toDegrees(rotatorConfig.getRotatorAngle()), 2);
        String rotModeString = StringNamingUtil.getRotatorConfigTypeName(rotatorConfig.getRotatorMode());
        String alignedInstrument = rotatorConfig.getInstrumentName();

        shortText="[Rotator] ";

        postText = "Mode: " + rotModeString;
        if (rotatorConfig.getRotatorMode() == IRotatorConfig.CARDINAL) {
            postText += ", Aligned to: " + alignedInstrument;
        } else if (rotatorConfig.getRotatorMode() == IRotatorConfig.MOUNT) {
            postText += ", Angle: " + rotAngle + " degrees";
        } else if (rotatorConfig.getRotatorMode() == IRotatorConfig.SKY) {
            postText += ", Aligned to: " + alignedInstrument + ", Angle: " + rotAngle + " degrees";
        }

        if (shortDescription) {
            return shortText;
        } else {
            return shortText + postText;
        }
    }

    private String getTrackingText(boolean usesNonSiderealTracking) {
        String text;
        if (usesNonSiderealTracking) {
            text = "[Non-Sidereal Tracking]";
        } else {
            text = "[Sidereal Tracking]";
        }
        return text;
    }
}
