/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.reference;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import ngat.oss.client.gui.wrapper.SlideArrangement;
import ngat.phase2.IOpticalSlideConfig;
import ngat.phase2.ITipTiltAbsoluteOffset;
import ngat.phase2.XBeamSteeringConfig;
import ngat.phase2.XDetectorConfig;
import ngat.phase2.XOpticalSlideConfig;
import ngat.phase2.XTipTiltAbsoluteOffset;

/**
 *
 * @author nrc
 */
public class DefaultObjectFactory {

    public static XBeamSteeringConfig getDefaultBeamSteeringConfig() {

        //use the current slide arrangement to create the defaults
        SlideArrangement upperSlideArrangement = TelescopeConfiguration.getInstance().getSlideArrangementsContainer().getSlideArrangement(0);
        SlideArrangement lowerSlideArrangement = TelescopeConfiguration.getInstance().getSlideArrangementsContainer().getSlideArrangement(1);

        XOpticalSlideConfig upperOpticalSlideConfig = new XOpticalSlideConfig(IOpticalSlideConfig.SLIDE_UPPER);
            upperOpticalSlideConfig.setElementName(upperSlideArrangement.getElementAt(0).getName());
        XOpticalSlideConfig lowerOpticalSlideConfig = new XOpticalSlideConfig(IOpticalSlideConfig.SLIDE_LOWER);
            lowerOpticalSlideConfig.setElementName(lowerSlideArrangement.getElementAt(0).getName());

        return new XBeamSteeringConfig(upperOpticalSlideConfig, lowerOpticalSlideConfig);
    }

    
    public static XTipTiltAbsoluteOffset getDefaultTipTiltAbsoluteOffset() {
        return new XTipTiltAbsoluteOffset(0, 0, CONST.IO_O, ITipTiltAbsoluteOffset.OFFSET_TYPE_SKY, ITipTiltAbsoluteOffset.TIPTILT_BOTTOM);
    }

    public static  XDetectorConfig getDefaultDetectorConfig(String instrumentName) {
        
        XDetectorConfig detectorConfig = new XDetectorConfig();

        if (instrumentName != null) {
            
            if (instrumentName.equalsIgnoreCase(CONST.IO_O)) {
                detectorConfig.setXBin(2);
                detectorConfig.setYBin(2);
            } else if (instrumentName.equalsIgnoreCase(CONST.RISE)) {
                detectorConfig.setXBin(2);
                detectorConfig.setYBin(2);
            }  else if (instrumentName.equalsIgnoreCase(CONST.SPRAT)) {       
                detectorConfig.setXBin(1);
                detectorConfig.setYBin(1);
            } else if (instrumentName.equalsIgnoreCase(CONST.RAPTOR)) {
                detectorConfig.setXBin(1);
                detectorConfig.setYBin(1);
            }
            else
            {
                detectorConfig.setXBin(2);
                detectorConfig.setYBin(2);            
            }
        } else {
            //default to IO_O
            instrumentName = CONST.IO_O;
            detectorConfig.setXBin(2);
            detectorConfig.setYBin(2);
        }
        
        return detectorConfig;
    }

}
