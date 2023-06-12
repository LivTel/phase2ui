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
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("PG0934+554,09 38 20.34,+55 05 49.06,12.16,FK5,2023.39"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("Feige_34,10 39 36.77,+43 06 08.92,10.41,FK5,2023.41"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("GD140,11 37 04.84,+29 47 58.34,12.41,FK5,2023.37"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("Feige_56,12 06 47.25,+11 40 12.86,10.41,FK5,2023.41"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("Feige_66,12 37 23.53,+25 03 59.69,10.41,FK5,2023.41"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("HZ_44,13 23 35.15,+36 07 59.85,11.74,FK5,2023.37"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("Feige_92,14 11 31.94,+50 07 04.50,10.41,FK5,2023.41"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("Feige_98,14 38 15.75,+27 29 32.96,10.41,FK5,2023.41"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("BD+33_2642,15 51 59.87,+32 56 54.55,10.41,FK5,2023.41"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("Kopff_27,17 43 55.84,+05 24 48.08,10.31,FK5,2023.38"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("BD+25_3941,19 44 26.15,+26 13 16.77,10.36,FK5,2023.38"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("Wolf_1346,20 34 21.20,+25 03 36.73,11.55,FK5,2023.38"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("BD+28_4211,21 51 10.97,+28 51 49.10,10.51,FK5,2023.38"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("BD+25_4655,21 59 41.91,+26 25 56.55,9.65,FK5,2023.38"));
        standardTargetList.add(new ngat.oss.client.gui.panel.target.StandardTargetWrapper("BD+17_4708,22 11 32.21,+18 05 35.77,9.45,FK5,2023.39"));
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
