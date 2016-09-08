/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.render;

import ngat.phase2.IDetectorConfig;

/**
 *
 * @author nrc
 */
public class DetectorConfigRenderer {

    public static String getSummaryOfDetectorConfig(IDetectorConfig detectorConfig) {
        String s = "";
        
        int xBin = detectorConfig.getXBin();
        int yBin = detectorConfig.getYBin();
        s += "Binning=[" + xBin + "x" + xBin + "]";

        return s;
    }
}
