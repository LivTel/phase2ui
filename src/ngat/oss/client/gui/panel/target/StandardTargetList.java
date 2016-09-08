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
        
        //extracted via LibreOffice Calc from http://telescope.livjm.ac.uk/pmwiki/uploads/Main/specstand_candidates.txt
        
        standardTargetList.add(new StandardTargetWrapper("Feige_15,01 49 09.482,+13 33 11.76,10.41"));
        standardTargetList.add(new StandardTargetWrapper("Feige_25,02 38 37.784,+05 28 11.28,12.01"));
        standardTargetList.add(new StandardTargetWrapper("40_Eri_B,04 15 21.786,-07 39 29.22, 9.55"));
        standardTargetList.add(new StandardTargetWrapper("G191-B2B,05 05 30.6,+52 49 56,11.80"));
        standardTargetList.add(new StandardTargetWrapper("He_3,06 47 37.99,+37 30 57.07,12.08"));
        standardTargetList.add(new StandardTargetWrapper("BD+75_325,08 10 49.3,+74 57 57,9.52"));
        standardTargetList.add(new StandardTargetWrapper("PG0934+554,09 38 20.4,+55 05 54,12.16"));
        standardTargetList.add(new StandardTargetWrapper("Feige_34,10 39 36.7,+43 06 10,11.25"));
        standardTargetList.add(new StandardTargetWrapper("GD140,11 37 05.1,+29 47 58,12.41"));
        standardTargetList.add(new StandardTargetWrapper("Feige_56,12 06 47.23,+11 40 12.64,11.06"));
        standardTargetList.add(new StandardTargetWrapper("Feige_66,12 37 23.6,+25 03 59,10.54"));
        standardTargetList.add(new StandardTargetWrapper("HZ_44,13 23 35.3,+36 08 01,11.74"));
        standardTargetList.add(new StandardTargetWrapper("Feige_92,14 11 31.877,+50 07 04.14,11.62"));
        standardTargetList.add(new StandardTargetWrapper("Feige_98,14 38 15.76,+27 29 32.99,11.84"));
        standardTargetList.add(new StandardTargetWrapper("BD+33_2642,15 51 59.9,+32 56 53,10.74"));
        standardTargetList.add(new StandardTargetWrapper("Kopff_27,17 43 55.84,+05 24 48.23,10.31"));
        standardTargetList.add(new StandardTargetWrapper("BD+25_3941,19 44 26.135,+26 13 16.66,10.36"));
        standardTargetList.add(new StandardTargetWrapper("Wolf_1346,20 34 21.883,+25 03 49.74,11.55"));
        standardTargetList.add(new StandardTargetWrapper("BD+28_4211,21 51 11.1,+28 51 52,10.51"));
        standardTargetList.add(new StandardTargetWrapper("BD+25_4655,21 59 41.9,+26 25 57,9.65"));
        standardTargetList.add(new StandardTargetWrapper("BD+17_4708,22 11 31.374,+18 05 34.17,9.45"));
        standardTargetList.add(new StandardTargetWrapper("Feige_110,23 19 58.4,-05 09 56,11.82"));
    }
}

class StandardTargetWrapper{
    
    String targetDescription;
    
    String targetName;
    String raStr;
    String decStr;
    String magStr;
    
    double ra, dec;
    
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
        //extraSolarTarget.setEpoch(epoch);
        //extraSolarTarget.setFrame(frame);
        
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
                    magStr = field.trim();
                    break;
            }
            fieldnum++;
        }
    }

    public String getDescription() {
        return targetName + " [RA: " + getRaStr() + ", Dec: " + getDecStr() + "] V=" + getMagStr();
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
}
