/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.beans.guibeans;

import ngat.phase2.IDetectorConfig;

/**
 *
 * @author nrc
 */
public interface DetectorConfigPanel {

    public void setDetectorConfig(IDetectorConfig detectorConfig);
    public IDetectorConfig getDetectorConfig();
    public boolean containsValidDetectorConfig();
    
}
