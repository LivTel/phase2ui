/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.wrapper;

import ngat.phase2.IExposure;
import ngat.phase2.IInstrumentConfig;
import ngat.phase2.XMultipleExposure;
import ngat.phase2.XPeriodExposure;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class PhotometricSeqWizardTableLineEntry {

    static Logger logger = Logger.getLogger(PhotometricSeqWizardTableLineEntry.class);

    public static final String[] COL_NAMES = new String[]{"Instrument", "Config", "Expose(mS)", "Count"};

    private String instrumentName;
    private IInstrumentConfig instrumentConfig;
    private IExposure exposure;

    public PhotometricSeqWizardTableLineEntry(String instrumentName, IInstrumentConfig instrumentConfig, IExposure exposure) {
        this.instrumentName = instrumentName;
        this.instrumentConfig = instrumentConfig;
        this.exposure = exposure;
    }
    
    public static int getColumnCount() {
        return COL_NAMES.length;
    }
    
    public static String getColumnName(int col) {
        return COL_NAMES[col];
    }
    
    public Object getvalueAt(int col) {
        double exposureTime;
        int exposureCount;
        
        try {
            exposureTime = getExposureTime();
            exposureCount = getExposureCount();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            return null;
        }

        switch (col) {
            case 0:
                return instrumentName;
            case 1:
                return instrumentConfig.getName();
            case 2:
                return exposureTime;
            case 3:
                return exposureCount;
        }
        return null;
    }

    private double getExposureTime() throws Exception {
        double exposureTime;

        if (exposure instanceof XMultipleExposure) {
            exposureTime = ((XMultipleExposure)exposure).getExposureTime();
        } else if (exposure instanceof XPeriodExposure) {
            exposureTime = ((XPeriodExposure)exposure).getExposureTime();
        } else {
            throw new Exception("unknown exposure type: " + exposure.getClass().getName());
        }
        return exposureTime;
    }

    private int getExposureCount() throws Exception {
        int exposureCount;

        if (exposure instanceof XMultipleExposure) {
            exposureCount = ((XMultipleExposure)exposure).getRepeatCount();
        } else if (exposure instanceof XPeriodExposure) {
            exposureCount = 1;
        } else {
            throw new Exception("unknown exposure type: " + exposure.getClass().getName());
        }
        return exposureCount;
    }

    public IExposure getExposure() {
        return exposure;
    }

    public IInstrumentConfig getInstrumentConfig() {
        return instrumentConfig;
    }

    public String getInstrumentName() {
        return instrumentName;
    }
    
    public String toString() {
        String s = this.getClass().getName() + "[";
        s += instrumentName + ",";
        s += instrumentConfig + ",";
        s += "exposure=" + exposure;
        return s;
    }
}
