/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.render;

import ngat.phase2.IRotatorConfig;

/**
 *
 * @author nrc
 */
public class StringNamingUtil {

    public static String getRotatorConfigTypeName(int rotMode) {
        
        String rotModeString = "UNKNOWN";
        switch (rotMode) {
            case IRotatorConfig.CARDINAL:
                rotModeString = "CARDINAL";
                break;
            case IRotatorConfig.FLOAT:
                rotModeString = "FLOAT";
                break;
            case IRotatorConfig.MOUNT:
                rotModeString = "MOUNT";
                break;
            case IRotatorConfig.VERTICAL:
                rotModeString = "VERTICAL";
                break;
            case IRotatorConfig.VFLOAT:
                rotModeString = "VFLOAT";
                break;
            case IRotatorConfig.SKY:
                rotModeString = "SKY";
                break;
        }
        return rotModeString;
    }
}
