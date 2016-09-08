/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ImagerInstrumentConfigEditorPanel2.java
 *
 * Created on Oct 26, 2010, 2:44:23 PM
 */

package ngat.oss.client.gui.panel.instrumentConfig;

import javax.swing.JOptionPane;
import ngat.oss.client.gui.panel.interfaces.IInstrumentConfigPanel;
import ngat.oss.client.gui.reference.CONST;
import ngat.oss.client.gui.reference.DefaultObjectFactory;
import ngat.phase2.IDetectorConfig;
import ngat.phase2.IInstrumentConfig;
import ngat.phase2.XBlueTwoSlitSpectrographInstrumentConfig;
import ngat.phase2.XDetectorConfig;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class TwoSlitSpectrographInstrumentConfigEditorPanel extends javax.swing.JPanel implements IInstrumentConfigPanel {

    static Logger logger = Logger.getLogger(TwoSlitSpectrographInstrumentConfigEditorPanel.class);

    private boolean enabled;
    private final XBlueTwoSlitSpectrographInstrumentConfig originalInstrumentConfig;

    public TwoSlitSpectrographInstrumentConfigEditorPanel(XBlueTwoSlitSpectrographInstrumentConfig blueTwoSlitSpectrographInstrumentConfig, boolean isNewInstrumentConfig) {
        this.originalInstrumentConfig = blueTwoSlitSpectrographInstrumentConfig;
        initComponents();
        populateComponents(blueTwoSlitSpectrographInstrumentConfig, isNewInstrumentConfig);
    }

    private void populateComponents(XBlueTwoSlitSpectrographInstrumentConfig blueTwoSlitSpectrographInstrumentConfig, boolean isNewInstrumentConfig) {
        if (blueTwoSlitSpectrographInstrumentConfig == null) {
            return;
        }

        if (isNewInstrumentConfig) {
            populateForNewInstrumentConfig(blueTwoSlitSpectrographInstrumentConfig);
        } else {
            populateForExistingInstrumentConfig(blueTwoSlitSpectrographInstrumentConfig);

        }
    }

    private void populateForNewInstrumentConfig(XBlueTwoSlitSpectrographInstrumentConfig blueTwoSlitSpectrographInstrumentConfig) {

        boolean limitInstrumentList = false;
        
        String instrumentName = blueTwoSlitSpectrographInstrumentConfig.getInstrumentName();
        
        XDetectorConfig detectorConfig = DefaultObjectFactory.getDefaultDetectorConfig(instrumentName);

        limitInstrumentList = (instrumentName != null);
        
        detectorConfigStandardPanel.setBinningOptions(CONST.LOTUS_BINNING_OPTIONS);
        
        //populate binning lists for instrument
        try {
            detectorConfigStandardPanel.setDetectorConfig(detectorConfig); 
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            JOptionPane.showMessageDialog(this, ex);
            return;
        }
    }

    
    private void populateForExistingInstrumentConfig(XBlueTwoSlitSpectrographInstrumentConfig blueTwoSlitSpectrographInstrumentConfig) {
        try {
            detectorConfigStandardPanel.setBinningOptions(CONST.LOTUS_BINNING_OPTIONS);
            
            IDetectorConfig detectorConfig = blueTwoSlitSpectrographInstrumentConfig.getDetectorConfig();
            //populate binning lists for instrument
            detectorConfigStandardPanel.setDetectorConfig(detectorConfig);
            jtfInstrumentConfigName.setText(blueTwoSlitSpectrographInstrumentConfig.getName());
            
            int slitWidth = blueTwoSlitSpectrographInstrumentConfig.getSlitWidth();
            
            if (slitWidth == XBlueTwoSlitSpectrographInstrumentConfig.SLIT_NARROW) {
                jcbSlitPos.setSelectedIndex(0);
            } else {
                jcbSlitPos.setSelectedIndex(1);
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            JOptionPane.showMessageDialog(this, ex);
            return;
        }
    }
    
    public IInstrumentConfig getInstrumentConfig() throws Exception {

        XBlueTwoSlitSpectrographInstrumentConfig blueTwoSlitSpectrographInstrumentConfig = new XBlueTwoSlitSpectrographInstrumentConfig();
        String name;

        blueTwoSlitSpectrographInstrumentConfig.setID(originalInstrumentConfig.getID());
        name = jtfInstrumentConfigName.getText();

        //need name field to have been validated

        //get the detector config
        XDetectorConfig detectorConfig = (XDetectorConfig) detectorConfigStandardPanel.getDetectorConfig();
        blueTwoSlitSpectrographInstrumentConfig.setDetectorConfig(detectorConfig);
        
        int slitWidth;
        
        if (jcbSlitPos.getSelectedIndex() == 0 ) {
            slitWidth = XBlueTwoSlitSpectrographInstrumentConfig.SLIT_NARROW;
        } else {
            slitWidth = XBlueTwoSlitSpectrographInstrumentConfig.SLIT_WIDE;
        }
        
        blueTwoSlitSpectrographInstrumentConfig.setSlitWidth(slitWidth);
        blueTwoSlitSpectrographInstrumentConfig.setName(name);
        blueTwoSlitSpectrographInstrumentConfig.setInstrumentName(CONST.LOTUS);
  
        return blueTwoSlitSpectrographInstrumentConfig;
    }
    
    public boolean containsValidInstrumentConfig() {

        if (jtfInstrumentConfigName.getText().trim().length() ==0) {
            return false;
        }
       
        return true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        //detectorConfigEditorPanel.setEnabled(enabled);
        jtfInstrumentName.setEnabled(enabled);
        jbtnRemoveFilter.setEnabled(enabled);
        jtfInstrumentConfigName.setEnabled(enabled);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jtfInstrumentName = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jtfInstrumentConfigName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jbtnRemoveFilter = new javax.swing.JButton();
        jpUpperNDSlide = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jcbSlitPos = new javax.swing.JComboBox();
        detectorConfigStandardPanel = new ngat.beans.guibeans.DetectorConfigStandardPanel();

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Instrument Name"));

        jtfInstrumentName.setText(CONST.SPRAT);
        jtfInstrumentName.setText("LOTUS");

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jtfInstrumentName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 191, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jtfInstrumentName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 17, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Instrument Config Name"));

        jtfInstrumentConfigName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtfInstrumentConfigNameActionPerformed(evt);
            }
        });

        jLabel3.setText("(no spaces please)");

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jtfInstrumentConfigName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 362, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jtfInstrumentConfigName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Optics"));

        jbtnRemoveFilter.setText(">");
        jbtnRemoveFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnRemoveFilterActionPerformed(evt);
            }
        });

        jpUpperNDSlide.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setText("Slit");

        jcbSlitPos.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "NARROW", "WIDE" }));

        org.jdesktop.layout.GroupLayout jpUpperNDSlideLayout = new org.jdesktop.layout.GroupLayout(jpUpperNDSlide);
        jpUpperNDSlide.setLayout(jpUpperNDSlideLayout);
        jpUpperNDSlideLayout.setHorizontalGroup(
            jpUpperNDSlideLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpUpperNDSlideLayout.createSequentialGroup()
                .addContainerGap()
                .add(jpUpperNDSlideLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jpUpperNDSlideLayout.createSequentialGroup()
                        .add(jLabel5)
                        .add(0, 0, Short.MAX_VALUE))
                    .add(jcbSlitPos, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jpUpperNDSlideLayout.setVerticalGroup(
            jpUpperNDSlideLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpUpperNDSlideLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel5)
                .add(12, 12, 12)
                .add(jcbSlitPos, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(460, 460, 460)
                        .add(jbtnRemoveFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jpUpperNDSlide, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(120, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(jpUpperNDSlide, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 96, Short.MAX_VALUE)
                .add(jbtnRemoveFilter)
                .add(53, 53, 53))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, detectorConfigStandardPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(detectorConfigStandardPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnRemoveFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnRemoveFilterActionPerformed

}//GEN-LAST:event_jbtnRemoveFilterActionPerformed

    private void jtfInstrumentConfigNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtfInstrumentConfigNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtfInstrumentConfigNameActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ngat.beans.guibeans.DetectorConfigStandardPanel detectorConfigStandardPanel;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JButton jbtnRemoveFilter;
    private javax.swing.JComboBox jcbSlitPos;
    private javax.swing.JPanel jpUpperNDSlide;
    private javax.swing.JTextField jtfInstrumentConfigName;
    private javax.swing.JTextField jtfInstrumentName;
    // End of variables declaration//GEN-END:variables

}
