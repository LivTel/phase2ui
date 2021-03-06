/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ngat.oss.client.gui.panel.target;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import ngat.phase2.XExtraSolarTarget;

/**
 *
 * @author nrc
 */
public class StandardTargetList {
    
    private static StandardTargetList instance;
    private ArrayList standardTargetList;
    
    public static StandardTargetList getInstance() {
        if (instance == null) {
            instance = new StandardTargetList();
        }
        return instance;
    }
    
    private StandardTargetList() {
        try {
            populateList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public List getStandardTargetList() {
        return standardTargetList;
    }
    
    /**
     * @return a String array of the standard targets, each element is a good description of the target.
     */
    public String[] getStandardTargetListDescriptions() {
        
        String desc = "";
        String[] descsArray = new String[standardTargetList.size()];
        
        Iterator stwi = standardTargetList.iterator();
        int p=0;
        while (stwi.hasNext()) {
            StandardTargetWrapper stw = (StandardTargetWrapper) stwi.next();
            descsArray[p] = stw.getDescription();
            p++;
        }
        
        return descsArray;
    }
    
    private void populateList() throws Exception {
        
        standardTargetList = new ArrayList();
        
        // 2020-07-27
        // See bug 2457 for description of fields.
        // Equinox = J2000, Epoch = various
        // Positions are true for the specified epoch, so proper motions have been applied, but not precession
        // because we are still in FK5/J2000. The TCS does the precession automatically as it does for any J2000.

        // Most up-to-date list always kept on wiki at
        // http://telescope.livjm.ac.uk/pmwiki/index.php?n=Main.SpectrophotometricStandards

        // Field order: targetName, raStr, decStr, magStr, refFrame, epoch
        
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("Feige_15,01 49 09.5,+13 33 12,10.41,FK5,2016.9"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("Feige_25,02 38 37.7,+05 28 11,12.01,FK5,2016.9"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("G191-B2B,05 05 30.64,+52 49 50.5,11.80,FK5,2020.14"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("He_3,06 47 37.7,+37 30 41,12.08,FK5,2016.9"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("BD+75_325,08 10 49.5,+74 57 58,9.52,FK5,2016.9"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("PG0934+554,09 38 20.4,+55 05 49,12.16,FK5,2016.9"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("Feige_34,10 39 36.8,+43 06 09,11.25,FK5,2016.9"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("GD140,11 37 04.93,+29 47 59.3,12.41,FK5,2020.03"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("Feige_56,12 06 47.2,+11 40 13,11.06,FK5,2016.9"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("Feige_66,12 37 23.6,+25 03 59,10.54,FK5,2016.9"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("HZ_44,13 23 34.5,+36 08 02,11.74,FK5,2016.9"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("Feige_92,14 11 31.92,+50 07 04.43,11.62,FK5,2020.53"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("Feige_98,14 38 15.75,+27 29 32.85,11.84,FK5,2020.53"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("BD+33_2642,15 51 59.9,+32 56 53,10.74,FK5,2000"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("Kopff_27,17 43 55.8,+05 24 48,10.31,FK5,2000"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("BD+25_3941,19 44 26.1,+26 13 16,10.36,FK5,2016.9"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("Wolf_1346,20 34 21.28,+25 03 38.32,11.55,FK5,2020.53"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("BD+28_4211,21 51 11.0,+28 51 50,10.51,FK5,2016.9"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("BD+25_4655,21 59 41.9,+26 25 57,9.65,FK5,2016.9"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("BD+17_4708,22 11 32.10,+18 05 35.5,9.45,FK5,2020.53"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("Feige_110,23 19 58.4,-05 09 56,11.82,FK5,2016.9"));
        
        // need to be in following order:
        // targetName, raStr, decStr, magStr, pmRAStr, pmDECStr, parallax, epoch, frame
        
    }
}

class StandardTargetWrapper{
    
    String targetDescription;
    String targetName, raStr, decStr, magStr, frameStr, epochStr;
 
    double ra, dec, epoch;
    int frame;

    public StandardTargetWrapper(String targetDescription) throws Exception {
        this.targetDescription = targetDescription;
        extractFields();
    }
    
    public XExtraSolarTarget getExtraSolarTarget() {
        
        XExtraSolarTarget extraSolarTarget = new XExtraSolarTarget();
        
        // cannot set ID, object not in database yet
        // extraSolarTarget.setID();
        
        extraSolarTarget.setName(targetName);
        extraSolarTarget.setRa(ra);
        extraSolarTarget.setDec(dec);
        //extraSolarTarget.setPmRA(pmRa);
        //extraSolarTarget.setPmDec(pmDec);
        //extraSolarTarget.setParallax(parallax);
        //extraSolarTarget.setRadialVelocity(radialVelocity);
        extraSolarTarget.setEpoch(epoch);
        extraSolarTarget.setFrame(frame);
        
        return extraSolarTarget;
    }
    
    private void extractFields() throws Exception {
        
        StringTokenizer st = new StringTokenizer(targetDescription, ",");
        
        int fieldnum = 0;
        while (st.hasMoreElements()) {
            String field = (String) st.nextElement();
            switch (fieldnum) {
                case 0:
                    targetName = field.trim();
                    break;
                case 1:
                    raStr = field.trim();
                    ra = ngat.astrometry.Position.parseHMS(raStr, " ");
                    break;
                case 2:
                    decStr = field.trim();
                    dec=ngat.astrometry.Position.parseDMS(decStr, " ");
                    break;
                case 3:
                    //NOT USED other than in toString()
                    magStr = field.trim();
                    break;
                case 4:
                    frameStr = field.trim();
                    //trim off the FK
                    String f=frameStr.substring(frameStr.length()-1);
                    frame=Integer.parseInt(f);
                    break;
                case 5:
                    epochStr = field.trim();
                    epoch=Double.parseDouble(epochStr);
                    break;
            }
            fieldnum++;
        }
    }


    public String getDescription() {
        return targetName + " [RA: " + getRaStr() + ", Dec: " + getDecStr() + ", V=" + getMagStr() + ", frame=" + getFrameStr() + ", epoch=" + getEpochStr() + "]";
    }
            
    public String getTargetName() {
        return targetName;
    }

    public String getRaStr() {
        return raStr;
    }

    public String getDecStr() {
        return decStr;
    }

    public String getMagStr() {
        return magStr;
    }

    public double getRa() {
        return ra;
    }

    public double getDec() {
        return dec;
    }

    public String getFrameStr() {
        return frameStr;
    }

    public String getEpochStr() {
        return epochStr;
    }
    
    
}
