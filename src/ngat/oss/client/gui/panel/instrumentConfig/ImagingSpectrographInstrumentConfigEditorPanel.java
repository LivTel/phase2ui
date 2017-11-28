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
import ngat.phase2.XDetectorConfig;
import ngat.phase2.XImagingSpectrographInstrumentConfig;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class ImagingSpectrographInstrumentConfigEditorPanel extends javax.swing.JPanel implements IInstrumentConfigPanel {

    static Logger logger = Logger.getLogger(ImagingSpectrographInstrumentConfigEditorPanel.class);

    private boolean enabled;
    private final XImagingSpectrographInstrumentConfig originalImagingSpectrographInstrumentConfig;

    public ImagingSpectrographInstrumentConfigEditorPanel(XImagingSpectrographInstrumentConfig imagingSpectrographInstrumentConfig, boolean isNewInstrumentConfig) {
        
        imagingSpectrographInstrumentConfig.setInstrumentName(CONST.SPRAT);
        
        this.originalImagingSpectrographInstrumentConfig = imagingSpectrographInstrumentConfig;
        initComponents();
        populateComponents(imagingSpectrographInstrumentConfig, isNewInstrumentConfig);
    }

    private void populateComponents(XImagingSpectrographInstrumentConfig imagingSpectrographInstrumentConfig, boolean isNewInstrumentConfig) {
        if (imagingSpectrographInstrumentConfig == null) {
            return;
        }

        if (isNewInstrumentConfig) {
            populateForNewInstrumentConfig(imagingSpectrographInstrumentConfig);
        } else {
            populateForExistingInstrumentConfig(imagingSpectrographInstrumentConfig);

        }
    }

    private void populateForNewInstrumentConfig(XImagingSpectrographInstrumentConfig imagingSpectrographInstrumentConfig) {

        boolean limitInstrumentList = false;
        
        String instrumentName = imagingSpectrographInstrumentConfig.getInstrumentName();
        
        XDetectorConfig detectorConfig = DefaultObjectFactory.getDefaultDetectorConfig(instrumentName);

        limitInstrumentList = (instrumentName != null);
        
        detectorConfigStandardPanel.setBinningOptions(CONST.SPRAT_BINNING_OPTIONS);
        
        //populate binning lists for instrument
        try {
            detectorConfigStandardPanel.setDetectorConfig(detectorConfig); //change, this line added (was commented out) 20/6/11
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            JOptionPane.showMessageDialog(this, ex);
            return;
        }
    }

    
    private void populateForExistingInstrumentConfig(XImagingSpectrographInstrumentConfig imagingSpectrographInstrumentConfig) {
        try {
            IDetectorConfig detectorConfig = imagingSpectrographInstrumentConfig.getDetectorConfig();
            //populate binning lists for instrument
            detectorConfigStandardPanel.setDetectorConfig(detectorConfig);
            jtfInstrumentConfigName.setText(imagingSpectrographInstrumentConfig.getName());
            
            int grimsPos = imagingSpectrographInstrumentConfig.getGrismPosition();
            int grimsRot = imagingSpectrographInstrumentConfig.getGrismRotation();
            int grimsSlit = imagingSpectrographInstrumentConfig.getSlitPosition();
            
            
            if (grimsPos == XImagingSpectrographInstrumentConfig.GRISM_IN) {
                jcbGrismPos.setSelectedIndex(0);
            } else {
                jcbGrismPos.setSelectedIndex(1);
            }
            
            if (grimsRot == XImagingSpectrographInstrumentConfig.GRISM_ROTATED) {
                jcbGrismRot.setSelectedIndex(1);
            } else {
                jcbGrismRot.setSelectedIndex(0);
            }
            
            if (grimsSlit == XImagingSpectrographInstrumentConfig.SLIT_DEPLOYED) {
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

        XImagingSpectrographInstrumentConfig imagingSpectrographInstrumentConfig = new XImagingSpectrographInstrumentConfig();
        String name;

        imagingSpectrographInstrumentConfig.setID(originalImagingSpectrographInstrumentConfig.getID());
        name = jtfInstrumentConfigName.getText();

        //need name field to have been validated

        //get the detector config
        XDetectorConfig detectorConfig = (XDetectorConfig) detectorConfigStandardPanel.getDetectorConfig();
        imagingSpectrographInstrumentConfig.setDetectorConfig(detectorConfig);
        
        int grismPos, grismRot, slitPos;
        
        if (jcbGrismPos.getSelectedIndex() == 0) {
            grismPos = XImagingSpectrographInstrumentConfig.GRISM_IN;
        } else {
            grismPos = XImagingSpectrographInstrumentConfig.GRISM_OUT;
        }
        
        if (jcbGrismRot.getSelectedIndex() == 0) {
            grismRot = XImagingSpectrographInstrumentConfig.GRISM_NOT_ROTATED;
        } else {
            grismRot = XImagingSpectrographInstrumentConfig.GRISM_ROTATED;
        }
        
        if (jcbSlitPos.getSelectedIndex() == 0 ) {
            slitPos = XImagingSpectrographInstrumentConfig.SLIT_DEPLOYED;
        } else {
            slitPos = XImagingSpectrographInstrumentConfig.SLIT_STOWED;
        }
        
        imagingSpectrographInstrumentConfig.setGrismPosition(grismPos);
        imagingSpectrographInstrumentConfig.setGrismRotation(grismRot);
        imagingSpectrographInstrumentConfig.setSlitPosition(slitPos);
        imagingSpectrographInstrumentConfig.setName(name);
        imagingSpectrographInstrumentConfig.setInstrumentName(CONST.SPRAT);
  
        return imagingSpectrographInstrumentConfig;
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
        jpFilterWheel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jcbGrismPos = new javax.swing.JComboBox();
        jpUpperNDSlide = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jcbSlitPos = new javax.swing.JComboBox();
        jpFilterWheel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jcbGrismRot = new javax.swing.JComboBox();
        detectorConfigStandardPanel = new ngat.beans.guibeans.DetectorConfigStandardPanel();

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Instrument Name"));

        jtfInstrumentName.setText(CONST.SPRAT);
        jtfInstrumentName.setText("SPRAT");

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

        jpFilterWheel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Grism position ");

        jcbGrismPos.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "IN BEAM", "OUT OF BEAM" }));

        org.jdesktop.layout.GroupLayout jpFilterWheel1Layout = new org.jdesktop.layout.GroupLayout(jpFilterWheel1);
        jpFilterWheel1.setLayout(jpFilterWheel1Layout);
        jpFilterWheel1Layout.setHorizontalGroup(
            jpFilterWheel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpFilterWheel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jpFilterWheel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jpFilterWheel1Layout.createSequentialGroup()
                        .add(jLabel1)
                        .add(0, 151, Short.MAX_VALUE))
                    .add(jcbGrismPos, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jpFilterWheel1Layout.setVerticalGroup(
            jpFilterWheel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpFilterWheel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jcbGrismPos, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jpUpperNDSlide.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setText("Slit");

        jcbSlitPos.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "IN BEAM", "OUT OF BEAM" }));

        org.jdesktop.layout.GroupLayout jpUpperNDSlideLayout = new org.jdesktop.layout.GroupLayout(jpUpperNDSlide);
        jpUpperNDSlide.setLayout(jpUpperNDSlideLayout);
        jpUpperNDSlideLayout.setHorizontalGroup(
            jpUpperNDSlideLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpUpperNDSlideLayout.createSequentialGroup()
                .addContainerGap()
                .add(jpUpperNDSlideLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jpUpperNDSlideLayout.createSequentialGroup()
                        .add(jLabel5)
                        .add(0, 221, Short.MAX_VALUE))
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

        jpFilterWheel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Grism rotation");

        jcbGrismRot.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "RED OPTIMIZED", "BLUE OPTIMIZED" }));

        org.jdesktop.layout.GroupLayout jpFilterWheel2Layout = new org.jdesktop.layout.GroupLayout(jpFilterWheel2);
        jpFilterWheel2.setLayout(jpFilterWheel2Layout);
        jpFilterWheel2Layout.setHorizontalGroup(
            jpFilterWheel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpFilterWheel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jpFilterWheel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jpFilterWheel2Layout.createSequentialGroup()
                        .add(jLabel2)
                        .add(0, 156, Short.MAX_VALUE))
                    .add(jcbGrismRot, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jpFilterWheel2Layout.setVerticalGroup(
            jpFilterWheel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpFilterWheel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jcbGrismRot, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jpFilterWheel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jpUpperNDSlide, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jpFilterWheel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {jpFilterWheel1, jpFilterWheel2, jpUpperNDSlide}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jpFilterWheel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jpFilterWheel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(12, 12, 12)
                .add(jpUpperNDSlide, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jbtnRemoveFilter)
                .add(53, 53, 53))
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {jpFilterWheel1, jpFilterWheel2, jpUpperNDSlide}, org.jdesktop.layout.GroupLayout.VERTICAL);

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
                .addContainerGap(18, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnRemoveFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnRemoveFilterActionPerformed

}//GEN-LAST:event_jbtnRemoveFilterActionPerformed

    private void jtfInstrumentConfigNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtfInstrumentConfigNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtfInstrumentConfigNameActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ngat.beans.guibeans.DetectorConfigStandardPanel detectorConfigStandardPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JButton jbtnRemoveFilter;
    private javax.swing.JComboBox jcbGrismPos;
    private javax.swing.JComboBox jcbGrismRot;
    private javax.swing.JComboBox jcbSlitPos;
    private javax.swing.JPanel jpFilterWheel1;
    private javax.swing.JPanel jpFilterWheel2;
    private javax.swing.JPanel jpUpperNDSlide;
    private javax.swing.JTextField jtfInstrumentConfigName;
    private javax.swing.JTextField jtfInstrumentName;
    // End of variables declaration//GEN-END:variables

}
