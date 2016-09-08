/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RotatorPanel.java
 *
 * Created on Sep 9, 2009, 10:19:07 AM
 */
package ngat.oss.client.gui.panel.sequencepanels;

import ngat.oss.client.gui.panel.*;
import java.awt.BorderLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import ngat.oss.client.gui.reference.CONST;
import ngat.oss.client.gui.reference.Session;
import ngat.phase2.IExecutiveAction;
import ngat.phase2.IRotatorConfig;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.XExecutiveComponent;
import ngat.phase2.XRotatorConfig;
import ngat.phase2.util.UnitConverter;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class RotatorPanel extends javax.swing.JPanel implements SequenceComponentPanel {

    static Logger logger = Logger.getLogger(RotatorPanel.class);

    private static final String MOUNT = "MOUNT";
    private static final String SKY = "SKY";
    private static final String CARDINAL = "Cardinal";
    private static final String MANUAL = "Manual";

    private ButtonGroup skyMountButtonGroup;

    /**
     * Creates new form RotatorPanel
     */
    public RotatorPanel() {
        initComponents();
        setUpComponents();
        populateComponents();
    }

    public void populateComponents() {
        
        jcbInstrumentName.setModel(new javax.swing.DefaultComboBoxModel(CONST.ACQUISITION_SCIENCE_INSTRUMENTS));
        
        /*
        if (Session.getInstance().getUser().isSuperUser()) {
            jcbInstrumentName.setModel(new javax.swing.DefaultComboBoxModel(CONST.ALL_INSTRUMENTS_FOR_ROTATOR));
        } else {
            jcbInstrumentName.setModel(new javax.swing.DefaultComboBoxModel(CONST.ALL_INSTRUMENTS_FOR_ROTATOR_EXCEPT_IOI));
        }
        */
    }
    
    public RotatorPanel(XRotatorConfig rotatorConfig) {
        this();

        int rotMode = rotatorConfig.getRotatorMode();
        double mountAngleDegs;

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
        switch (rotMode) {
            case IRotatorConfig.CARDINAL:
                jcbRotator.setSelectedItem(CARDINAL);
                jplInstrumentPanel.setVisible(true);
                jcbInstrumentName.setSelectedItem(rotatorConfig.getInstrumentName());
                break;
            case IRotatorConfig.MOUNT:
                mountAngleDegs = UnitConverter.convertRadsToDegs(rotatorConfig.getRotatorAngle());
                jtfRotAngle.setText(String.valueOf(mountAngleDegs));
                jcbRotator.setSelectedItem(MANUAL);
                jrbMount.setSelected(true);
                jplInstrumentPanel.setVisible(false);
                break;
            case IRotatorConfig.SKY:
                mountAngleDegs = UnitConverter.convertRadsToDegs(rotatorConfig.getRotatorAngle());
                jtfRotAngle.setText(String.valueOf(mountAngleDegs));

                jcbRotator.setSelectedItem(MANUAL);
                jrbSky.setSelected(true);
                jplInstrumentPanel.setVisible(true);
                jcbInstrumentName.setSelectedItem(rotatorConfig.getInstrumentName());
                break;
            default:
                logger.error("unknown rotator mode:" + rotMode);
        }
    }

    public ISequenceComponent getSequenceComponent() {

        //currently assumes that the exposure type is a XMultipleExposure
        XRotatorConfig rotatorConfig = (XRotatorConfig) getRotatorConfig();
        return new XExecutiveComponent("ROTATOR_CONFIG", (IExecutiveAction) rotatorConfig);
    }

    public boolean isValidData() {
        try {
            double angleDegrees = Double.parseDouble(jtfRotAngle.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a numeric value for the rotator angle");
            return false;
        }
        return true;
    }

    private IRotatorConfig getRotatorConfig() {
        int mode;
        double angleDegrees;
        String instrumentName = "";

        String jcbRotatorSelection = (String) jcbRotator.getSelectedItem();
        if (jcbRotatorSelection.equalsIgnoreCase(MANUAL)) {
            if (skyMountButtonGroup.getSelection().getActionCommand().equals(SKY)) {
                mode = IRotatorConfig.SKY;
                instrumentName = (String) jcbInstrumentName.getSelectedItem();
            } else {
                mode = IRotatorConfig.MOUNT;
            }
        } else if (jcbRotatorSelection.equalsIgnoreCase(CARDINAL)) {
            mode = IRotatorConfig.CARDINAL;
            instrumentName = (String) jcbInstrumentName.getSelectedItem();
        } else {
            JOptionPane.showMessageDialog(this, "unknown rotator mode :" + jcbRotatorSelection);
            logger.error("unknown rotator mode :" + jcbRotatorSelection);
            return null;
        }

        try {
            angleDegrees = Double.parseDouble(jtfRotAngle.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            logger.error(e);
            return null;
        }

        //System.err.println("mode=" + mode + ", angleDegrees=" + UnitConverter.convertDegsToRads(angleDegrees) + ", instrumentName=" + instrumentName);
        XRotatorConfig rotatorConfig = new XRotatorConfig(mode, UnitConverter.convertDegsToRads(angleDegrees), instrumentName);
        return rotatorConfig;
    }

    private void setUpComponents() {
        jcbRotator.setSelectedItem("Cardinal");

        jplMountSkyPanel.setVisible(false);
        jtfRotAngle.setText("0.0");

        jrbSky.setActionCommand(SKY);
        jrbMount.setActionCommand(MOUNT);
        jrbSky.setSelected(true);

        skyMountButtonGroup = new ButtonGroup();
        skyMountButtonGroup.add(jrbSky);
        skyMountButtonGroup.add(jrbMount);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jcbRotator = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtpHelpPane = new javax.swing.JTextPane();
        jplMountSkyPanel = new javax.swing.JPanel();
        jrbSky = new javax.swing.JRadioButton();
        jrbMount = new javax.swing.JRadioButton();
        jpRotatorAnglePanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jtfRotAngle = new javax.swing.JTextField();
        jplInstrumentPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jcbInstrumentName = new javax.swing.JComboBox();

        jLabel4.setText("Rotator Setting");
        jLabel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jcbRotator.setModel(new javax.swing.DefaultComboBoxModel(new String[] {CARDINAL, MANUAL}));
        jcbRotator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbRotatorActionPerformed(evt);
            }
        });

        jtpHelpPane.setEditable(false);
        jtpHelpPane.setText("A Rotator Setting of Cardinal will cause the sides of the image to be aligned NSEW.\n\nThe Mount option moves to an angle w.r.t. the telescope axis that will correspond to a different SKY PA depending on the time of observation. In general, this is recommended for RINGO 2 observations where you need to calibrate for instrument polarisation errors.");
        jScrollPane1.setViewportView(jtpHelpPane);

        jplMountSkyPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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
                .add(jLabel5)
                .addContainerGap(30, Short.MAX_VALUE))
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
            .add(jplMountSkyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jplMountSkyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jplMountSkyPanelLayout.createSequentialGroup()
                        .add(29, 29, 29)
                        .add(jpRotatorAnglePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jplMountSkyPanelLayout.createSequentialGroup()
                        .add(jrbSky)
                        .add(18, 18, 18)
                        .add(jrbMount)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jplMountSkyPanelLayout.setVerticalGroup(
            jplMountSkyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jplMountSkyPanelLayout.createSequentialGroup()
                .add(jplMountSkyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jrbMount)
                    .add(jrbSky))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jpRotatorAnglePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jplInstrumentPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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
                .add(10, 10, 10)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jcbInstrumentName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 183, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jplInstrumentPanelLayout.setVerticalGroup(
            jplInstrumentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jplInstrumentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jplInstrumentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jcbInstrumentName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel4)
                    .add(layout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jcbRotator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 220, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jplMountSkyPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(18, 18, 18)
                                .add(jplInstrumentPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 787, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel4)
                .add(9, 9, 9)
                .add(jcbRotator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jplMountSkyPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jplInstrumentPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jcbRotatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbRotatorActionPerformed

        String rotatorName = (String) jcbRotator.getSelectedItem();
        if (rotatorName.equalsIgnoreCase(MANUAL)) {
            jplMountSkyPanel.setVisible(true);
        } else if (rotatorName.equalsIgnoreCase(CARDINAL)) {
            jplMountSkyPanel.setVisible(false);
            jtfRotAngle.setText("0.0");
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

    private String getRotatorModeAsString() {
        String selectedRot = (String) jcbRotator.getSelectedItem();
        return selectedRot;
    }

    public static void main(String[] args) {
        JFrame j = new JFrame();
        j.getContentPane().setLayout(new BorderLayout());
        j.getContentPane().add(new RotatorPanel(), BorderLayout.CENTER);
        j.setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox jcbInstrumentName;
    private javax.swing.JComboBox jcbRotator;
    private javax.swing.JPanel jpRotatorAnglePanel;
    private javax.swing.JPanel jplInstrumentPanel;
    private javax.swing.JPanel jplMountSkyPanel;
    private javax.swing.JRadioButton jrbMount;
    private javax.swing.JRadioButton jrbSky;
    private javax.swing.JTextField jtfRotAngle;
    private javax.swing.JTextPane jtpHelpPane;
    // End of variables declaration//GEN-END:variables

}
