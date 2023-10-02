/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ExposurePanel.java
 *
 * Created on Sep 9, 2009, 12:08:25 PM
 */
package ngat.oss.client.gui.panel.sequencepanels;

import javax.swing.JOptionPane;
import ngat.oss.client.gui.panel.*;
import ngat.oss.client.gui.reference.CONST;
import ngat.oss.client.gui.reference.Session;
import ngat.phase2.IAcquisitionConfig;
import ngat.phase2.IExecutiveAction;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.XAcquisitionConfig;
import ngat.phase2.XExecutiveComponent;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class FineTunePanel extends javax.swing.JPanel implements SequenceComponentPanel {

    static Logger logger = Logger.getLogger(FineTunePanel.class);
    
    public FineTunePanel() {
        initComponents();
        setUpComponents();
    }

    public FineTunePanel(XAcquisitionConfig acquisitionConfig) {
        initComponents();
        setUpComponents();
        populateComponents(acquisitionConfig);
    }

    private void setUpComponents() {
        
        jcbAcquisitionInstrument.setModel(new javax.swing.DefaultComboBoxModel(CONST.ACQUISITION_ACQUIRING_INSTRUMENTS));
        
        //jcbScienceInstrument.setModel(new javax.swing.DefaultComboBoxModel(CONST.ACQUISITION_SCIENCE_INSTRUMENTS));
        
        /*
        if (Session.getInstance().getUser().isSuperUser()) {
            jcbAcquisitionInstrument.setModel(new javax.swing.DefaultComboBoxModel(CONST.ACQUISITION_ACQUIRING_INSTRUMENTS));
        } else {
            jcbAcquisitionInstrument.setModel(new javax.swing.DefaultComboBoxModel(CONST.ACQUISITION_ACQUIRING_INSTRUMENTS_EXCEPT_SPRAT));
        }
        */
        
        
        if (Session.getInstance().getUser().isSuperUser()) {
            jcbScienceInstrument.setModel(new javax.swing.DefaultComboBoxModel(CONST.ACQUISITION_SCIENCE_INSTRUMENTS));
        } else {
            jcbScienceInstrument.setModel(new javax.swing.DefaultComboBoxModel(CONST.ACQUISITION_SCIENCE_INSTRUMENTS_EXCEPT_LIRIC));
        }
        
    }
    
    private void populateComponents(XAcquisitionConfig acquisitionConfig) {
        int mode = acquisitionConfig.getMode();
        String acquisitionInstrumentName = acquisitionConfig.getAcquisitionInstrumentName();
        String targetInstrumentName = acquisitionConfig.getTargetInstrumentName();

        if (acquisitionInstrumentName != null) {
            if (acquisitionInstrumentName.equalsIgnoreCase(CONST.RATCAM) || targetInstrumentName.equalsIgnoreCase(CONST.RATCAM)) {
                JOptionPane.showMessageDialog(this, "RATCam is no longer available");
            }
            if (acquisitionInstrumentName.equalsIgnoreCase(CONST.IO_THOR) || targetInstrumentName.equalsIgnoreCase(CONST.IO_THOR)) {
                JOptionPane.showMessageDialog(this, "THOR is no longer available");
            }
        }
        
        boolean allowAlternative = acquisitionConfig.getAllowAlternative();
        switch (mode) {
            case IAcquisitionConfig.WCS_FIT:
                //ASTROMETRIC FIT, BRIGHTEST OBJECT
                jcbAcquisitionMode.setSelectedItem("ASTROMETRIC FIT");
                jcbAcquisitionInstrument.setSelectedItem(acquisitionInstrumentName);
                jcbScienceInstrument.setSelectedItem(targetInstrumentName);
                if (acquisitionConfig.getPrecision() == IAcquisitionConfig.PRECISION_NORMAL) {
                    jcbPrecision.setSelectedItem("NORMAL_PRECISION");
                } else if (acquisitionConfig.getPrecision() == IAcquisitionConfig.PRECISION_HIGH) {
                    jcbPrecision.setSelectedItem("HIGH_PRECISION");
                } else {
                    logger.error("Loaded acquisitionConfig had precision set to PRECISION_NOT_SET");
                }

                break;
            case IAcquisitionConfig.BRIGHTEST:
                jcbAcquisitionMode.setSelectedItem("BRIGHTEST OBJECT");
                jcbAcquisitionInstrument.setSelectedItem(acquisitionInstrumentName);
                jcbScienceInstrument.setSelectedItem(targetInstrumentName);
                if (acquisitionConfig.getPrecision() == IAcquisitionConfig.PRECISION_NORMAL) {
                    jcbPrecision.setSelectedItem("NORMAL_PRECISION");
                } else if (acquisitionConfig.getPrecision() == IAcquisitionConfig.PRECISION_HIGH) {
                    jcbPrecision.setSelectedItem("HIGH_PRECISION");
                } else {
                    logger.error("Loaded acquisitionConfig had precision set to PRECISION_NOT_SET");
                }
                break;
            case IAcquisitionConfig.INSTRUMENT_CHANGE:
                //SHOULDN'T EVER BE THIS
                break;
        }
        if (allowAlternative) {
            jcbAllowAlternative.setSelectedItem("YES");
        } else {
            jcbAllowAlternative.setSelectedItem("NO");
        }
    }

    /*
    int precisionModeSelected = jcbPrecision.getSelectedIndex();
        int precision = IAcquisitionConfig.PRECISION_NOT_SET;
        switch(precisionModeSelected) {
            case 0:
                precision = IAcquisitionConfig.NORMAL_PRECISION;
                break;
            case 1:
                precision = IAcquisitionConfig.HIGH_PRECISION;
                break;
        }
    */
    /**
     * 
     * @return XExecutiveComponent containing ACQUSITION_CONFIG configured as a FINE TUNE
     * using either acquisition mode BRIGHTEST or WCS_FIT
     * The precision parameter can be either: 
     */
    public ISequenceComponent getSequenceComponent() {

        //currently assumes that the exposure type is a XMultipleExposure
        
        int mode;

        String selectedMode = (String) jcbAcquisitionMode.getSelectedItem();
        if (selectedMode.equalsIgnoreCase("ASTROMETRIC FIT")) {
            mode = IAcquisitionConfig.WCS_FIT;

        } else if (selectedMode.equalsIgnoreCase("BRIGHTEST OBJECT")) {
            mode = IAcquisitionConfig.BRIGHTEST;

        } else {
            return null;
        }

        String acquisitionInstrumentName = (String) jcbAcquisitionInstrument.getSelectedItem();
        String targetInstrumentName = (String) jcbScienceInstrument.getSelectedItem();
        boolean allowAlternative = ((String) jcbAllowAlternative.getSelectedItem()).equals("YES");

        String precisionSetStr = (String) jcbPrecision.getSelectedItem();
        
        int precision = IAcquisitionConfig.PRECISION_NOT_SET;
        
        if (precisionSetStr.equalsIgnoreCase("NORMAL_PRECISION")) {
            precision = IAcquisitionConfig.PRECISION_NORMAL;
            
        } else if (precisionSetStr.equalsIgnoreCase("HIGH_PRECISION")) {
            precision = IAcquisitionConfig.PRECISION_HIGH;
            
        } else {
            precision = IAcquisitionConfig.PRECISION_NOT_SET;
            
        }
        
        XAcquisitionConfig acquisitionConfig = new XAcquisitionConfig();
        
        acquisitionConfig.setMode(mode);
        acquisitionConfig.setAcquisitionInstrumentName(acquisitionInstrumentName);
        acquisitionConfig.setTargetInstrumentName(targetInstrumentName);
        acquisitionConfig.setAllowAlternative(allowAlternative);
        acquisitionConfig.setPrecision(precision);
        
        XExecutiveComponent executiveComponent = new XExecutiveComponent("ACQUISITION_CONFIG", (IExecutiveAction) acquisitionConfig);

        return executiveComponent;
    }

    public boolean isValidData() {
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel13 = new javax.swing.JLabel();
        jcbAcquisitionMode = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jcbAcquisitionInstrument = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jcbScienceInstrument = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtpHelpPane = new javax.swing.JTextPane();
        jLabel4 = new javax.swing.JLabel();
        jcbAllowAlternative = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jcbPrecision = new javax.swing.JComboBox();

        jLabel13.setText("Fine Tune");
        jLabel13.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jcbAcquisitionMode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ASTROMETRIC FIT", "BRIGHTEST OBJECT" }));

        jLabel3.setText("Mode:");

        jLabel2.setText("Acquire using:");

        jLabel1.setText("Science Instrument (with target pixel):");

        jcbScienceInstrument.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbScienceInstrumentActionPerformed(evt);
            }
        });

        jtpHelpPane.setEditable(false);
        jtpHelpPane.setText("Make sure that you have already configured the focal plane for the acquisition instrument. Do not issue another Focal Plane command following Fine Tuning. \n\nIn general, use 'BRIGHTEST OBJECT' for objects brighter than V=8, otherwise use ASTROMETRIC FIT.");
        jScrollPane1.setViewportView(jtpHelpPane);

        jLabel4.setText("Allow alternative acquisition instrument:");

        jcbAllowAlternative.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "YES", "NO" }));

        jLabel5.setText("Precision mode");

        jcbPrecision.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "NORMAL_PRECISION", "HIGH_PRECISION" }));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel13)
                        .add(837, 837, 837))
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel3)
                            .add(jLabel2)
                            .add(jLabel1)
                            .add(jLabel5))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jcbAcquisitionMode, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 242, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(jcbPrecision, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 242, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                        .add(jcbAcquisitionInstrument, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(jcbScienceInstrument, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 242, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(jLabel4))
                                    .add(layout.createSequentialGroup()
                                        .add(86, 86, 86)
                                        .add(jcbAllowAlternative, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                        .add(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel13)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(jcbAcquisitionMode, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jcbAcquisitionInstrument, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jcbScienceInstrument, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jcbAllowAlternative, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(jcbPrecision, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jcbScienceInstrumentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbScienceInstrumentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jcbScienceInstrumentActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox jcbAcquisitionInstrument;
    private javax.swing.JComboBox jcbAcquisitionMode;
    private javax.swing.JComboBox jcbAllowAlternative;
    private javax.swing.JComboBox jcbPrecision;
    private javax.swing.JComboBox jcbScienceInstrument;
    private javax.swing.JTextPane jtpHelpPane;
    // End of variables declaration//GEN-END:variables

}
