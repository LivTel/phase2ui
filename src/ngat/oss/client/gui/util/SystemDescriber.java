/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import ngat.phase2.XLoginRecord;

/**
 *
 * @author nrc
 */
public class SystemDescriber {

    private static SystemDescriber instance;
    
    public static SystemDescriber getInstance() {
        if (instance == null) {
            instance = new SystemDescriber();
        }
        return instance;
    }
    
    private SystemDescriber() {

    }

    public XLoginRecord buildLoginRecord() {
        XLoginRecord loginRecord = new XLoginRecord();
        
        loginRecord.setJavaVersion(System.getProperty("java.version"));
        loginRecord.setOsArch(System.getProperty("os.arch"));
        loginRecord.setOsName(System.getProperty("os.name"));
        loginRecord.setOsVersion(System.getProperty("os.version"));
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        String screenSizeStr = screenSize.width  + "x" + screenSize.height;
        loginRecord.setScreenSize(screenSizeStr);
        
        return loginRecord;
    }
    
}
