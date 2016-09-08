/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.wrapper;

import ngat.oss.client.gui.render.InstrumentConfigRenderer;
import ngat.phase2.XArc;
import ngat.phase2.XBlueTwoSlitSpectrographInstrumentConfig;
import ngat.phase2.XDualBeamSpectrographInstrumentConfig;
import ngat.phase2.XImagingSpectrographInstrumentConfig;
import ngat.phase2.XLampFlat;

/**
 *
 * @author nrc
 */
public class WizardPanelTableLineEntry {
    
    public static final String[] COL_NAMES = new String[]{"Observing Sequence"};

    public static final int LINE_TYPE_FRODO_CONFIG = 1;
    public static final int LINE_TYPE_EXPOSE = 2;
    public static final int LINE_TYPE_LAMP_FLAT = 3;
    public static final int LINE_TYPE_ARC = 4;
    public static final int LINE_TYPE_SPRAT_CONFIG = 5;
    public static final int LINE_TYPE_LOTUS_CONFIG = 6;

    private XDualBeamSpectrographInstrumentConfig dualBeamSpectrographInstrumentConfig;
    private XImagingSpectrographInstrumentConfig imagingSpectrographInstrumentConfig;
    private XBlueTwoSlitSpectrographInstrumentConfig blueTwoSlitSpectrographInstrumentConfig;
            
    private int exposureCount;
    private double exposureTime;
    private XLampFlat lampFlat;
    private XArc arc;
    private int lineEntryType;

    public WizardPanelTableLineEntry(XDualBeamSpectrographInstrumentConfig dualBeamSpectrographInstrumentConfig) {
        this.dualBeamSpectrographInstrumentConfig = dualBeamSpectrographInstrumentConfig;
        this.lineEntryType = LINE_TYPE_FRODO_CONFIG;
    }

    public WizardPanelTableLineEntry(double exposureTime, int exposureCount) {
        this.exposureTime = exposureTime;
        this.exposureCount = exposureCount;
        this.lineEntryType = LINE_TYPE_EXPOSE;
    }

    public WizardPanelTableLineEntry(XLampFlat lampFlat) {
        this.lampFlat = lampFlat;
        this.lineEntryType = LINE_TYPE_LAMP_FLAT;
    }

    public WizardPanelTableLineEntry(XArc arc) {
        this.arc = arc;
        this.lineEntryType = LINE_TYPE_ARC;
    }

    public WizardPanelTableLineEntry(XImagingSpectrographInstrumentConfig imagingSpectrographInstrumentConfig) {
        this.imagingSpectrographInstrumentConfig = imagingSpectrographInstrumentConfig;
        this.lineEntryType = LINE_TYPE_SPRAT_CONFIG;
    }
    
    public WizardPanelTableLineEntry(XBlueTwoSlitSpectrographInstrumentConfig blueTwoSlitSpectrographInstrumentConfig) {
        this.blueTwoSlitSpectrographInstrumentConfig = blueTwoSlitSpectrographInstrumentConfig;
        this.lineEntryType = LINE_TYPE_LOTUS_CONFIG;
    }
    
    public static int getColumnCount() {
        return COL_NAMES.length;
    }
    
    public static String getColumnName(int col) {
        return COL_NAMES[col];
    }
    
    public Object getvalueAt(int col) {
        switch (col) {
            case 0:
                return getDescription();
        }
        return null;
    }

    //used by getvalueAt() for the table model
    private String getDescription() {
        String s = "";
        switch (lineEntryType) {
            case WizardPanelTableLineEntry.LINE_TYPE_FRODO_CONFIG:
                return getFRODOConfigDescription();
            case WizardPanelTableLineEntry.LINE_TYPE_EXPOSE:
                return getExposureDescription();
            case WizardPanelTableLineEntry.LINE_TYPE_LAMP_FLAT:
                return getLampFlatDescription();
            case WizardPanelTableLineEntry.LINE_TYPE_ARC:
                return getArcDescription();
            case WizardPanelTableLineEntry.LINE_TYPE_SPRAT_CONFIG:
                return getSPRATConfigDescription();
            case WizardPanelTableLineEntry.LINE_TYPE_LOTUS_CONFIG:
                return getLOTUSConfigDescription();
            default:
                return "UNKNOWN ENTRY TYPE";
        }
    }

    //used by getDescription
    private String getFRODOConfigDescription() {
        String s = "";
        s += "[Configure] ";
        s += dualBeamSpectrographInstrumentConfig.toResolutionString(dualBeamSpectrographInstrumentConfig.getResolution());
        return s;
    }

    private String getSPRATConfigDescription() {
        String s = "";
        s += "[Configure] ";
        s += imagingSpectrographInstrumentConfig.getName() + " [";
        s += InstrumentConfigRenderer.getSummaryOfInstrumentConfig(imagingSpectrographInstrumentConfig) + "]";
        return s;
    }
    
    private String getLOTUSConfigDescription() {
        String s = "";
        s += "[Configure] ";
        s += blueTwoSlitSpectrographInstrumentConfig.getName() + " [";
        s += InstrumentConfigRenderer.getSummaryOfInstrumentConfig(blueTwoSlitSpectrographInstrumentConfig) + "]";
        return s;
    }

    private String getExposureDescription() {
        String s = "";
        s += "[Exposure] " + exposureCount + " x " + exposureTime  + "sec ";
        return s;
    }

    private String getLampFlatDescription() {
        String s = "";
        s += "[Lamp Flat] " + lampFlat.getLamp().getLampName();
        return s;
    }

    private String getArcDescription() {
        String s = "";
        s += "[Arc] " + arc.getLamp().getLampName();
        return s;
    }

    //methods for returning the actual data objects
    public XArc getArc() {
        return arc;
    }

    public XDualBeamSpectrographInstrumentConfig getDualBeamSpectrographInstrumentConfig() {
        return dualBeamSpectrographInstrumentConfig;
    }

    public XImagingSpectrographInstrumentConfig getImagingSpectrographInstrumentConfig() {
        return imagingSpectrographInstrumentConfig;
    }
    
    public XBlueTwoSlitSpectrographInstrumentConfig getBlueTwoSlitSpectrographInstrumentConfig() {
        return blueTwoSlitSpectrographInstrumentConfig;
    }
    
    public int getExposureCount() {
        return exposureCount;
    }

    public double getExposureTime() {
        return exposureTime;
    }

    public XLampFlat getLampFlat() {
        return lampFlat;
    }

    public int getLineEntryType() {
        return lineEntryType;
    }

}
