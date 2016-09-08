/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ngat.oss.client.gui.wrapper;

import ngat.oss.client.gui.render.DetectorConfigRenderer;
import ngat.oss.client.gui.render.InstrumentConfigRenderer;
import ngat.phase2.IInstrumentConfig;

/**
 *
 * @author nrc
 */
public class InstrumentConfigTableLineEntry {

    public static final String[] COL_NAMES = new String[]{"Name", "Type", "Instrument Name", "Summary", "Detector Configuration"};
    IInstrumentConfig instrumentConfig;

    public InstrumentConfigTableLineEntry(IInstrumentConfig instrumentConfig) {
        this.instrumentConfig = instrumentConfig;
    }

    public IInstrumentConfig getInstrumentConfig() {
        return instrumentConfig;
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
                return instrumentConfig.getName();
            case 1:
                return InstrumentConfigRenderer.getInstrumentConfigTypeDescription(instrumentConfig);
            case 2:
               return instrumentConfig.getInstrumentName();
            case 3:
               return InstrumentConfigRenderer.getSummaryOfInstrumentConfig(instrumentConfig);
            case 4:
               return DetectorConfigRenderer.getSummaryOfDetectorConfig(instrumentConfig.getDetectorConfig());
        }
        return null;
    }

}
