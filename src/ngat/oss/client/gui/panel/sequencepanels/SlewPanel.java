/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TargetSelectorPanel.java
 *
 * Created on Sep 9, 2009, 9:27:40 AM
 */

package ngat.oss.client.gui.panel.sequencepanels;

import ngat.oss.client.gui.panel.*;
import java.util.Iterator;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import ngat.oss.client.gui.dialog.NewTargetDialog;
import ngat.oss.client.Phase2ModelClient;
import ngat.oss.client.gui.reference.CONST;
import ngat.oss.client.gui.reference.Session;
import ngat.oss.client.gui.render.TargetRenderer;
import ngat.oss.exception.Phase2Exception;
import ngat.phase2.IExecutiveAction;
import ngat.phase2.IProgram;
import ngat.phase2.IRotatorConfig;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.ITarget;
import ngat.phase2.XEphemerisTarget;
import ngat.phase2.XExecutiveComponent;
import ngat.phase2.XRotatorConfig;
import ngat.phase2.XSlew;
import ngat.phase2.util.Rounder;
import ngat.phase2.util.UnitConverter;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class SlewPanel extends javax.swing.JPanel implements SequenceComponentPanel {

    static Logger logger = Logger.getLogger(SlewPanel.class);

    IProgram program;
    List targetList;

    private static final String NON_SIDEREAL_TRACKING = "Non-Sidereal Tracking";
    private static final String SIDEREAL_TRACKING = "Sidereal Tracking";
    
    private static final String SKY = "SKY";
    private static final String MOUNT = "MOUNT";
    private static final String CARDINAL = "Automatic (Cardinal)";
    private static final String MANUAL = "Manual";

    private ButtonGroup skyMountButtonGroup;
    
    /** Creates new form TargetSelectorPanel */
    public SlewPanel(IProgram program) {
        this.program = program;

        initComponents();
        populateInstrumentList();
        try {
            skyMountButtonGroup = new ButtonGroup();
            skyMountButtonGroup.add(jrbSky);
            skyMountButtonGroup.add(jrbMount);

            jrbSky.setActionCommand(SKY);
            jrbMount.setActionCommand(MOUNT);
            
            populateTargetList();
            setTracking(false);
            setRotator(null);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            JOptionPane.showMessageDialog(this, e);
        }
    }

    public SlewPanel(XSlew slew, IProgram program) {
        this(program);

        populateInstrumentList();
        setTarget(slew.getTarget());
        setRotator(slew.getRotatorConfig());
        setTracking(slew.usesNonSiderealTracking());
    }

    private void populateInstrumentList() {
        
        //jcbInstrumentName.setModel(new javax.swing.DefaultComboBoxModel(CONST.ALL_INSTRUMENTS_FOR_ROTATOR));
        
        
        if (Session.getInstance().getUser().isSuperUser()) {
            jcbInstrumentName.setModel(new javax.swing.DefaultComboBoxModel(CONST.ALL_INSTRUMENTS_FOR_ROTATOR));
        } else {
            jcbInstrumentName.setModel(new javax.swing.DefaultComboBoxModel(CONST.ALL_INSTRUMENTS_FOR_ROTATOR));
        }
        
    }
    
    private void populateTargetList() throws Phase2Exception {

        jcbTarget.removeAllItems();
        targetList = Phase2ModelClient.getInstance().listTargets(program.getID());
        Iterator ti = targetList.iterator();
        while (ti.hasNext()) {
            ITarget target = (ITarget) ti.next();
            String targetDescription = TargetRenderer.getShortDescription(target);
            jcbTarget.addItem(targetDescription);
        }
        
    }

    private int findIndexOfTarget(ITarget target) {
        //find index of instrumentConfig in  instrumentConfigList
        Iterator i = targetList.iterator();
        int index = 0;
        while (i.hasNext()) {
            ITarget targetFound = (ITarget) i.next();
            if (targetFound.getName().equals(target.getName())) {
                return index;
            }
            index ++;
        }
        return -1;
    }

    private void setTarget(ITarget target) {

        //select the given target in the controls
        //targetList has been populated by populateTargetList([String])

        Iterator i = targetList.iterator();

        while (i.hasNext()) {
            ITarget foundTarget = (ITarget) i.next();

            if (foundTarget.getID() == target.getID()) {
                jcbTarget.setSelectedItem(TargetRenderer.getShortDescription(foundTarget));
            }
        }
    }

    private void setRotator(IRotatorConfig rotatorConfig) {

        /*
        if (rotatorName.equalsIgnoreCase(MANUAL)) {
            jplMountSkyPanel.setVisible(true);
        } else if (rotatorName.equalsIgnoreCase(CARDINAL)) {

        } else {
            JOptionPane.showMessageDialog(this, "unknown rotatorName: " + rotatorName);
        }
        */
        
        if (rotatorConfig != null) {
            if (rotatorConfig.getInstrumentName() != null) {
                if (rotatorConfig.getInstrumentName().equalsIgnoreCase(CONST.RATCAM)) {
                    JOptionPane.showMessageDialog(this, "RATCam is no longer available");
                }
                if (rotatorConfig.getInstrumentName().equalsIgnoreCase(CONST.IO_THOR)) {
                    JOptionPane.showMessageDialog(this, "THOR is no longer available");
                }
            }
        }
        
       if (rotatorConfig == null) {
           jrbSky.setSelected(true);
           jcbRotator.setSelectedItem("Cardinal");
           jpRotatorAnglePanel.setVisible(false);
           jtfRotAngle.setText("0.0");
           return;
       }

       //select the given rotator
       int rotMode = rotatorConfig.getRotatorMode();
       double rotAngle = rotatorConfig.getRotatorAngle();

       switch (rotMode) {
            case IRotatorConfig.CARDINAL:
                jplMountSkyPanel.setVisible(false);
                jplInstrumentPanel.setVisible(false);
                jtfRotAngle.setText("0.0");
                jcbRotator.setSelectedItem(CARDINAL);
                jcbInstrumentName.setSelectedItem(rotatorConfig.getInstrumentName());
                break;
            case IRotatorConfig.MOUNT:
                jplMountSkyPanel.setVisible(true);
                jplInstrumentPanel.setVisible(false);
                jrbMount.setSelected(true);

                jcbRotator.setSelectedItem(MANUAL);
                double mountAngleDegs = UnitConverter.convertRadsToDegs(rotatorConfig.getRotatorAngle());
                jtfRotAngle.setText(String.valueOf(Rounder.round(mountAngleDegs, 2)));

                break;
           case IRotatorConfig.SKY:
                jplMountSkyPanel.setVisible(true);
                jplInstrumentPanel.setVisible(true);
                jrbSky.setSelected(true);

                jcbRotator.setSelectedItem(MANUAL);
                double skyAngleDegs = UnitConverter.convertRadsToDegs(rotatorConfig.getRotatorAngle());
                jtfRotAngle.setText(String.valueOf(Rounder.round(skyAngleDegs, 2)));

                jcbInstrumentName.setSelectedItem(rotatorConfig.getInstrumentName());

                break;
        }
    }

    private void setTracking(boolean usesNonSiderealTracking) {
        if (usesNonSiderealTracking) {
            jbtnTrackingType.setText(SlewPanel.NON_SIDEREAL_TRACKING);
        } else {
            jbtnTrackingType.setText(SlewPanel.SIDEREAL_TRACKING);
        }
    }

    public ISequenceComponent getSequenceComponent() {

        //target
        int targetIndex = jcbTarget.getSelectedIndex();
        if (targetIndex == -1) {
            return null;
        }

        ITarget target = (ITarget) targetList.get(targetIndex);

        //rotator config
        int mode;
        String selectedRot = (String) jcbRotator.getSelectedItem();
        String instrumentName = "";
        double angleRads = 0;
        if (selectedRot.equalsIgnoreCase(CARDINAL)) {
            mode = IRotatorConfig.CARDINAL;
            instrumentName = (String) jcbInstrumentName.getSelectedItem();
        } else {
            //manual
            String skyMountActionCmd = skyMountButtonGroup.getSelection().getActionCommand();
            if (skyMountActionCmd.equalsIgnoreCase(SKY)) {
                mode = IRotatorConfig.SKY;
                angleRads = UnitConverter.convertDegsToRads(Double.parseDouble(jtfRotAngle.getText()));
                instrumentName = (String) jcbInstrumentName.getSelectedItem();
            } else {
                mode = IRotatorConfig.MOUNT;
                angleRads = UnitConverter.convertDegsToRads(Double.parseDouble(jtfRotAngle.getText()));
            }
        }
        
        XRotatorConfig rotatorConfig = new XRotatorConfig(mode, angleRads, instrumentName);

        //tracking type
        boolean usesNonSiderealTracking = jbtnTrackingType.getText().equals(SlewPanel.NON_SIDEREAL_TRACKING);

        //build slew
        XSlew slew = new XSlew(target, rotatorConfig, usesNonSiderealTracking);
        
        return new XExecutiveComponent(target.getName(),(IExecutiveAction) slew);
    }

    public boolean isValidData() {
        try {
            Double.parseDouble(jtfRotAngle.getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jcbTarget = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jbtnCreateNewTarget = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jcbRotator = new javax.swing.JComboBox();
        jplInstrumentPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jcbInstrumentName = new javax.swing.JComboBox();
        jplMountSkyPanel = new javax.swing.JPanel();
        jrbSky = new javax.swing.JRadioButton();
        jrbMount = new javax.swing.JRadioButton();
        jpRotatorAnglePanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jtfRotAngle = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jbtnTrackingType = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtpHelpPane = new javax.swing.JTextPane();

        jLabel2.setText("Target:");

        jcbTarget.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbTargetItemStateChanged(evt);
            }
        });
        jcbTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbTargetActionPerformed(evt);
            }
        });

        jLabel3.setText("or");

        jbtnCreateNewTarget.setText("Create New Target");
        jbtnCreateNewTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCreateNewTargetActionPerformed(evt);
            }
        });

        jLabel6.setText("Slew");
        jLabel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jcbRotator.setModel(new javax.swing.DefaultComboBoxModel(new String[] {CARDINAL, MANUAL}));
        jcbRotator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbRotatorActionPerformed(evt);
            }
        });

        jLabel1.setText("Align to instrument:");

        jcbInstrumentName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbInstrumentNameActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jplInstrumentPanelLayout = new org.jdesktop.layout.GroupLayout(jplInstrumentPanel);
        jplInstrumentPanel.setLayout(jplInstrumentPanelLayout);
        jplInstrumentPanelLayout.setHorizontalGroup(
            jplInstrumentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jplInstrumentPanelLayout.createSequentialGroup()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jcbInstrumentName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 183, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jplInstrumentPanelLayout.setVerticalGroup(
            jplInstrumentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jplInstrumentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jplInstrumentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jcbInstrumentName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jrbSky.setText("Sky");
        jrbSky.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbSkyActionPerformed(evt);
            }
        });

        jrbMount.setText("Mount");
        jrbMount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbMountActionPerformed(evt);
            }
        });

        jLabel5.setText("(degrees)");

        jtfRotAngle.setText("0.0");

        org.jdesktop.layout.GroupLayout jpRotatorAnglePanelLayout = new org.jdesktop.layout.GroupLayout(jpRotatorAnglePanel);
        jpRotatorAnglePanel.setLayout(jpRotatorAnglePanelLayout);
        jpRotatorAnglePanelLayout.setHorizontalGroup(
            jpRotatorAnglePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpRotatorAnglePanelLayout.createSequentialGroup()
                .add(jtfRotAngle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 53, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel5))
        );
        jpRotatorAnglePanelLayout.setVerticalGroup(
            jpRotatorAnglePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpRotatorAnglePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jtfRotAngle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jLabel5))
        );

        org.jdesktop.layout.GroupLayout jplMountSkyPanelLayout = new org.jdesktop.layout.GroupLayout(jplMountSkyPanel);
        jplMountSkyPanel.setLayout(jplMountSkyPanelLayout);
        jplMountSkyPanelLayout.setHorizontalGroup(
            jplMountSkyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpRotatorAnglePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(jplMountSkyPanelLayout.createSequentialGroup()
                .add(jrbSky)
                .add(18, 18, 18)
                .add(jrbMount))
        );
        jplMountSkyPanelLayout.setVerticalGroup(
            jplMountSkyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jplMountSkyPanelLayout.createSequentialGroup()
                .add(jplMountSkyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jrbMount)
                    .add(jrbSky))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jpRotatorAnglePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jLabel4.setText("Rotator:");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jplMountSkyPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jplInstrumentPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jcbRotator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 273, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(jcbRotator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(17, 17, 17)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jplMountSkyPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jplInstrumentPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel7.setText("Tracking is:");

        jbtnTrackingType.setText("Sidereal Tracking");
        jbtnTrackingType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnTrackingTypeActionPerformed(evt);
            }
        });

        jtpHelpPane.setEditable(false);
        jtpHelpPane.setText("Non-sidereal tracking is only available for Ephemeris Targets. The non-sidereal rate is calculated by interpolation on the Ephemeris table.");
        jScrollPane1.setViewportView(jtpHelpPane);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jLabel7)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jbtnTrackingType))
                    .add(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 254, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(jbtnTrackingType))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel6)
                    .add(layout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(jLabel2)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(12, 12, 12)
                                .add(jLabel3)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jbtnCreateNewTarget)
                                .add(260, 260, 260)
                                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jcbTarget, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 722, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel6)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jcbTarget, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jbtnCreateNewTarget)
                            .add(jLabel3))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

   
    private void jbtnCreateNewTargetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCreateNewTargetActionPerformed

        NewTargetDialog dialog = new NewTargetDialog(true);
        dialog.setVisible(true);

        ITarget target = dialog.getTarget();

        dialog.setVisible(false);
        dialog.dispose();
        if (target == null) {
            return;
        }
        if (dialog.wasKilled()) {
            return;
        }
        try {
            Phase2ModelClient.getInstance().addTarget(program.getID(), target);
            populateTargetList();
            //select new target in combo box
            jcbTarget.setSelectedIndex(findIndexOfTarget(target));
        } catch (Phase2Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
}//GEN-LAST:event_jbtnCreateNewTargetActionPerformed

    private void jcbTargetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbTargetActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jcbTargetActionPerformed

    private void jbtnTrackingTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnTrackingTypeActionPerformed
        //change the tracking type selected
        boolean usesNonSiderealTracking = jbtnTrackingType.getText().equals(SlewPanel.NON_SIDEREAL_TRACKING);
        if (usesNonSiderealTracking) {
            jbtnTrackingType.setText(SlewPanel.SIDEREAL_TRACKING);
        } else {
            jbtnTrackingType.setText(SlewPanel.NON_SIDEREAL_TRACKING);
        }
    }//GEN-LAST:event_jbtnTrackingTypeActionPerformed

    private void allowNonSiderealTracking(boolean allowNST) {
        
        if (allowNST) {
            jbtnTrackingType.setEnabled(true);
        } else {
            //nonSiderealTrackingAllowed not allowed
            jbtnTrackingType.setText(SlewPanel.SIDEREAL_TRACKING);
            jbtnTrackingType.setEnabled(false);
        }
    }

    private void jcbTargetItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbTargetItemStateChanged

        //change whether non-sidereal tracking is available as an option in the panel
        //(only available if target is instanceof XEphemerisTarget)
        int targetIndex = jcbTarget.getSelectedIndex();
        ITarget target;
        if (targetIndex == -1) {
            allowNonSiderealTracking(true);
            return;
        } else {
            target = (ITarget) targetList.get(targetIndex);
            if (target instanceof XEphemerisTarget) {
                allowNonSiderealTracking(true);
            } else {
                allowNonSiderealTracking(false);
            }
        }
    }//GEN-LAST:event_jcbTargetItemStateChanged

    private void jcbRotatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbRotatorActionPerformed

        String rotatorName = (String) jcbRotator.getSelectedItem();
        if (rotatorName.equalsIgnoreCase(MANUAL)) {
            jplMountSkyPanel.setVisible(true);
            jpRotatorAnglePanel.setVisible(true);
            if (skyMountButtonGroup.getSelection().getActionCommand().equalsIgnoreCase(SKY)) {
                jrbSky.setSelected(true);
                jplInstrumentPanel.setVisible(true);
            } else if (skyMountButtonGroup.getSelection().getActionCommand().equalsIgnoreCase(MOUNT)) {
                jrbMount.setSelected(true);
                jplInstrumentPanel.setVisible(false);
            }
        } else if (rotatorName.equalsIgnoreCase(CARDINAL)) {
            jplMountSkyPanel.setVisible(false);
            jplInstrumentPanel.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "unknown rotatorName: " + rotatorName);
        }
}//GEN-LAST:event_jcbRotatorActionPerformed

    private void jcbInstrumentNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbInstrumentNameActionPerformed

}//GEN-LAST:event_jcbInstrumentNameActionPerformed

    private void jrbSkyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbSkyActionPerformed
        String actionCommand = skyMountButtonGroup.getSelection().getActionCommand();
        jplInstrumentPanel.setVisible(actionCommand.equalsIgnoreCase(SKY));
}//GEN-LAST:event_jrbSkyActionPerformed

    private void jrbMountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbMountActionPerformed
        String actionCommand = skyMountButtonGroup.getSelection().getActionCommand();
        jplInstrumentPanel.setVisible(actionCommand.equalsIgnoreCase(SKY));
}//GEN-LAST:event_jrbMountActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtnCreateNewTarget;
    private javax.swing.JButton jbtnTrackingType;
    private javax.swing.JComboBox jcbInstrumentName;
    private javax.swing.JComboBox jcbRotator;
    private javax.swing.JComboBox jcbTarget;
    private javax.swing.JPanel jpRotatorAnglePanel;
    private javax.swing.JPanel jplInstrumentPanel;
    private javax.swing.JPanel jplMountSkyPanel;
    private javax.swing.JRadioButton jrbMount;
    private javax.swing.JRadioButton jrbSky;
    private javax.swing.JTextField jtfRotAngle;
    private javax.swing.JTextPane jtpHelpPane;
    // End of variables declaration//GEN-END:variables

}
