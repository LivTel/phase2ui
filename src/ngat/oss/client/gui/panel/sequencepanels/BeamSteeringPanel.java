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

import ngat.oss.client.gui.dialog.BeamSteeringEditingDialog_TEMP;
import ngat.oss.client.gui.panel.*;
import ngat.oss.client.gui.reference.DefaultObjectFactory;
import ngat.oss.client.gui.reference.TelescopeConfiguration;
import ngat.oss.client.gui.wrapper.IOpticalSlideElement;
import ngat.oss.client.gui.wrapper.SlideArrangement;
import ngat.phase2.IBeamSteeringConfig;
import ngat.phase2.IExecutiveAction;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.XBeamSteeringConfig;
import ngat.phase2.XExecutiveComponent;
import ngat.phase2.XOpticalSlideConfig;

/**
 *
 * @author nrc
 */
public class BeamSteeringPanel extends javax.swing.JPanel implements SequenceComponentPanel {

    private IBeamSteeringConfig beamSteeringConfig;

    private SlideArrangement upperArrangement, lowerArrangement;
    
    public BeamSteeringPanel() {
        this(DefaultObjectFactory.getDefaultBeamSteeringConfig());
    }

    public BeamSteeringPanel(IBeamSteeringConfig beamSteeringConfig) {
        initComponents();
        loadSlideArrangements();
        setBeamSteeringConfig(beamSteeringConfig);
    }

    private void loadSlideArrangements() {
        upperArrangement = TelescopeConfiguration.getInstance().getSlideArrangementsContainer().getSlideArrangement(0);
        lowerArrangement = TelescopeConfiguration.getInstance().getSlideArrangementsContainer().getSlideArrangement(1);
    }

    private void setBeamSteeringConfig(IBeamSteeringConfig beamSteeringConfig) {
        this.beamSteeringConfig = beamSteeringConfig;
        populateComponents(beamSteeringConfig);
    }

    private void populateComponents(IBeamSteeringConfig beamSteeringConfig) {
      
        if (beamSteeringConfig != null) {
            //upper slide stuff
            String upperSlideName = upperArrangement.getName();
            jlblUpperSlideName.setText(upperSlideName);
            //get jcb options
            String[] upperSlideSelections = getSlideNameOptions(upperArrangement);
            //set the model from the options
            jcbUpperSlideSelection.setModel(new javax.swing.DefaultComboBoxModel(upperSlideSelections));
            //set the selection from the upper slide config
            jcbUpperSlideSelection.setSelectedItem(beamSteeringConfig.getUpperSlideConfig().getElementName());

            //lower slide stuff
            String lowerSlideName = lowerArrangement.getName();
            jlblLowerSlideName.setText(lowerSlideName);
            //get jcb options
            String[] lowerSlideSelections = getSlideNameOptions(lowerArrangement);
            //set the model from the options
            jcbLowerSlideSelection.setModel(new javax.swing.DefaultComboBoxModel(lowerSlideSelections));
            //set the selection from the lower slide config
            jcbLowerSlideSelection.setSelectedItem(beamSteeringConfig.getLowerSlideConfig().getElementName());

            /*
            String upperSlideName = beamSteeringConfig.getUpperSlideConfig().getSlideName();
            String upperSlideElementName = beamSteeringConfig.getUpperSlideConfig().getElementName();
            jlblUpperSlideName.setText(upperSlideName);

            int upperSlidePosition = beamSteeringConfig.getUpperSlideConfig().getPosition();
            switch (upperSlidePosition) {
                case IOpticalSlideConfig.POSITION_CLEAR:
                    jcbUpperSlideSelection.setSelectedItem(CLEAR_NAME);
                    break;
                case IOpticalSlideConfig.POSITION_AL_MIRROR:
                    jcbUpperSlideSelection.setSelectedItem(MIRROR_NAME);
                    break;
                case IOpticalSlideConfig.POSITION_DI_RB:
                    jcbUpperSlideSelection.setSelectedItem(RB_DICHROIC_NAME);
                    break;
            }

            String lowerSlideName = beamSteeringConfig.getLowerSlideConfig().getSlideName();
            String lowerSlideElementName = beamSteeringConfig.getLowerSlideConfig().getElementName();
            jlblLowerSlideName.setText(lowerSlideName);

            int lowerSlidePosition = beamSteeringConfig.getLowerSlideConfig().getPosition();
            switch (lowerSlidePosition) {
                case IOpticalSlideConfig.POSITION_DI_RB:
                    jcbLowerSlideSelection.setSelectedItem(RB_DICHROIC_NAME);
                    break;
                case IOpticalSlideConfig.POSITION_AL_MIRROR:
                    jcbLowerSlideSelection.setSelectedItem(MIRROR_NAME);
                    break;
                case IOpticalSlideConfig.POSITION_DI_BR:
                    //jcbLowerSlideSelection.setSelectedItem(BR_DICHROIC_NAME); //CHANGED-13-12-11, replaced this line with one below - to catch extant BR configs
                    jcbLowerSlideSelection.setSelectedItem(CLEAR_NAME);
                    break;
                //CHANGED-13-12-11, added the following option (instead of BR)
                case IOpticalSlideConfig.POSITION_CLEAR:
                    jcbLowerSlideSelection.setSelectedItem(CLEAR_NAME);
                    break;
            }
             */
        } else {
            jlblUpperSlideName.setText("Upper Slide:");
            jlblLowerSlideName.setText("Lower Slide:");
        }
    }

    private String[] getSlideNameOptions(SlideArrangement slideArrangement) {

        //System.err.println("getSlideNameOptions(" + slideArrangement + ")");

        int numberOfElements = slideArrangement.getOpticalSlideElements().size();
        String[] slideNames = new String[numberOfElements];

        for (int i=0; i<numberOfElements; i++) {
            IOpticalSlideElement element = slideArrangement.getElementAt(i);
            //System.err.println("... element=" + element);
            slideNames[i] = element.getName();
        }
        
        return slideNames;
    }

    private void setConfigFromControls() {

        XOpticalSlideConfig upperOpticalSlideConfig = new XOpticalSlideConfig();
        upperOpticalSlideConfig.setSlide(0);
        upperOpticalSlideConfig.setElementName((String) jcbUpperSlideSelection.getSelectedItem());

        XOpticalSlideConfig lowerOpticalSlideConfig = new XOpticalSlideConfig();
        lowerOpticalSlideConfig.setSlide(1);
        lowerOpticalSlideConfig.setElementName((String) jcbLowerSlideSelection.getSelectedItem());

        XBeamSteeringConfig xBeamSteeringConfig = new XBeamSteeringConfig();
        xBeamSteeringConfig.setUpperSlideConfig(upperOpticalSlideConfig);
        xBeamSteeringConfig.setLowerSlideConfig(lowerOpticalSlideConfig);
        setBeamSteeringConfig(xBeamSteeringConfig);
        
        /*
        int upperPosition, lowerPosition;

        String upperSelection = (String) jcbUpperSlideSelection.getSelectedItem();
        if (upperSelection.equals(RB_DICHROIC_NAME)) {
            upperPosition =  IOpticalSlideConfig.POSITION_DI_RB;
        } else if (upperSelection.equals(MIRROR_NAME)) {
            upperPosition =  IOpticalSlideConfig.POSITION_AL_MIRROR;
        } else if (upperSelection.equals(CLEAR_NAME)) {
            upperPosition =  IOpticalSlideConfig.POSITION_CLEAR;
        } else {
            upperPosition = IOpticalSlideConfig.POSITION_UNKNOWN;
        }
        XOpticalSlideConfig upperOpticalSlideConfig = new XOpticalSlideConfig(IOpticalSlideConfig.SLIDE_UPPER, upperPosition);

        String lowerSelection = (String) jcbLowerSlideSelection.getSelectedItem();
        if (lowerSelection.equals(BR_DICHROIC_NAME)) {
            lowerPosition =  IOpticalSlideConfig.POSITION_DI_BR;

        //CHANGED-13-12-11, added the following option
        } else if (lowerSelection.equals(CLEAR_NAME)) {
            lowerPosition =  IOpticalSlideConfig.POSITION_CLEAR;

        } else if (lowerSelection.equals(MIRROR_NAME)) {
            lowerPosition =  IOpticalSlideConfig.POSITION_AL_MIRROR;
        } else if (lowerSelection.equals(RB_DICHROIC_NAME)) {
            lowerPosition =  IOpticalSlideConfig.POSITION_DI_RB;
        } else {
            lowerPosition = IOpticalSlideConfig.POSITION_UNKNOWN;
        }
        XOpticalSlideConfig lowerOpticalSlideConfig = new XOpticalSlideConfig(IOpticalSlideConfig.SLIDE_LOWER, lowerPosition);

        XBeamSteeringConfig xBeamSteeringConfig = new XBeamSteeringConfig();
        xBeamSteeringConfig.setUpperSlideConfig(upperOpticalSlideConfig);
        xBeamSteeringConfig.setLowerSlideConfig(lowerOpticalSlideConfig);
        
        setBeamSteeringConfig(xBeamSteeringConfig);
         * */
    }

    public ISequenceComponent getSequenceComponent() {

        XExecutiveComponent executiveComponent = new XExecutiveComponent("BEAM_STEERING_CONFIG",(IExecutiveAction) beamSteeringConfig);
        
        return executiveComponent;
    }
    
    public boolean isValidData() {
        return true;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel13 = new javax.swing.JLabel();
        jbtnEditConfig = new java.awt.Button();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtpHelpPane = new javax.swing.JTextPane();
        jPanel1 = new javax.swing.JPanel();
        jlblUpperSlideName = new javax.swing.JLabel();
        jlblLowerSlideName = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jcbUpperSlideSelection = new javax.swing.JComboBox();
        jcbLowerSlideSelection = new javax.swing.JComboBox();

        jLabel13.setText("Beam Steering");
        jLabel13.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jbtnEditConfig.setEnabled(false);
        jbtnEditConfig.setLabel("Edit Graphically");
        jbtnEditConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnEditConfigActionPerformed(evt);
            }
        });

        jtpHelpPane.setEditable(false);
        jtpHelpPane.setText("In the above dichroic names, the first wavelength is reflected, the second is transmitted.");
        jScrollPane1.setViewportView(jtpHelpPane);

        jlblUpperSlideName.setText("SLIDE_NAME");

        jlblLowerSlideName.setText("SLIDE_NAME");

        jLabel1.setText("Slide");

        jLabel2.setText("Selected Element");

        jcbUpperSlideSelection.setEnabled(false);
        jcbUpperSlideSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbUpperSlideSelectionActionPerformed(evt);
            }
        });

        jcbLowerSlideSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbLowerSlideSelectionActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                        .add(jlblUpperSlideName)
                        .add(jLabel1))
                    .add(jlblLowerSlideName))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel2)
                    .add(jcbUpperSlideSelection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 201, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jcbLowerSlideSelection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 201, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jcbUpperSlideSelection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jcbLowerSlideSelection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jlblUpperSlideName)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(33, 33, 33)
                                .add(jlblLowerSlideName)))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 574, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel13)
                    .add(layout.createSequentialGroup()
                        .add(jbtnEditConfig, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(40, 40, 40)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel13)
                        .add(21, 21, 21)
                        .add(jbtnEditConfig, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(27, 27, 27)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 48, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnEditConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEditConfigActionPerformed
        
        //CHANGED-13-12-11, swapped to BeamSteeringEditingDialog_TEMP
        BeamSteeringEditingDialog_TEMP dialog = new BeamSteeringEditingDialog_TEMP(true, beamSteeringConfig);
        dialog.setVisible(true);
        //blocks

        setBeamSteeringConfig(dialog.getBeamSteeringConfig());

        dialog.setVisible(false);
        dialog.dispose();
    }//GEN-LAST:event_jbtnEditConfigActionPerformed

    private void jcbUpperSlideSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbUpperSlideSelectionActionPerformed
        setConfigFromControls();
    }//GEN-LAST:event_jcbUpperSlideSelectionActionPerformed

    private void jcbLowerSlideSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbLowerSlideSelectionActionPerformed
       setConfigFromControls();
    }//GEN-LAST:event_jcbLowerSlideSelectionActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private java.awt.Button jbtnEditConfig;
    private javax.swing.JComboBox jcbLowerSlideSelection;
    private javax.swing.JComboBox jcbUpperSlideSelection;
    private javax.swing.JLabel jlblLowerSlideName;
    private javax.swing.JLabel jlblUpperSlideName;
    private javax.swing.JTextPane jtpHelpPane;
    // End of variables declaration//GEN-END:variables

}
