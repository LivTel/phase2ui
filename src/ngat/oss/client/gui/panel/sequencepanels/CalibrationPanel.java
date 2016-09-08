/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AutoguidePanel.java
 *
 * Created on Sep 9, 2009, 10:59:54 AM
 */

package ngat.oss.client.gui.panel.sequencepanels;

import ngat.oss.client.gui.panel.*;
import ngat.oss.client.gui.panel.sequencepanels.ArcPanel;
import ngat.oss.client.gui.panel.sequencepanels.BiasPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import ngat.phase2.ISequenceComponent;
import ngat.phase2.XArc;
import ngat.phase2.XBias;
import ngat.phase2.XCalibration;
import ngat.phase2.XDark;
import ngat.phase2.XLampDef;
import ngat.phase2.XLampFlat;

/**
 *
 * @author nrc
 */
public class CalibrationPanel extends javax.swing.JPanel implements SequenceComponentPanel, ActionListener {

    private static final String CMD_ARC = "CMD_ARC";
    private static final String CMD_BIAS = "CMD_BIAS";
    private static final String CMD_DARK = "CMD_DARK";
    private static final String CMD_LAMP_FLAT = "CMD_LAMP_FLAT";

    private ButtonGroup buttonGroup;

    /** Creates new form AutoguidePanel */
    public CalibrationPanel() {
        this(null);
    }

    public CalibrationPanel(XCalibration calibration) {
        initComponents();
        initComponents2();
        populateComponents2(calibration);
    }

    private void initComponents2() {

        buttonGroup = new ButtonGroup();
        buttonGroup.add(jrbArc);
        buttonGroup.add(jrbBias);
        buttonGroup.add(jrbDark);
        buttonGroup.add(jrbLampFlat);

        jrbArc.setActionCommand(CMD_ARC);
        jrbBias.setActionCommand(CMD_BIAS);
        jrbDark.setActionCommand(CMD_DARK);
        jrbLampFlat.setActionCommand(CMD_LAMP_FLAT);

        jrbArc.addActionListener(this);
        jrbBias.addActionListener(this);
        jrbDark.addActionListener(this);
        jrbLampFlat.addActionListener(this);        
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String actionCommand = actionEvent.getActionCommand();

        XCalibration calibration;
        if (actionCommand.equals(CMD_ARC)) {
            XArc arc = new XArc();
            arc.setLamp(new XLampDef("Xe"));
            calibration = arc;
        } else if (actionCommand.equals(CMD_BIAS)) {
            calibration = new XBias();
        } else if (actionCommand.equals(CMD_DARK)) {
            calibration = new XDark();
        } else if (actionCommand.equals(CMD_LAMP_FLAT)) {
             XLampFlat lampFlat = new XLampFlat();
            lampFlat.setLamp(new XLampDef("Xe"));
            calibration = lampFlat;
        } else {
            return;
        }

        populateComponents2(calibration);
    }
    
    private void populateComponents2(XCalibration calibration) {

        if (calibration == null) {
            setButtons(CMD_ARC);
            XArc arc = new XArc();
            arc.setLamp(new XLampDef("Xe"));
            showCalibrationPanel(arc);
            return;
        }
        
        String mode;

        if (calibration instanceof XArc) {
            mode = CMD_ARC;
        } else if (calibration instanceof XBias) {
            mode = CMD_BIAS;
        } else if (calibration instanceof XDark) {
            mode = CMD_DARK;
        } else if (calibration instanceof XLampFlat) {
            mode = CMD_LAMP_FLAT;
        } else {
            return;
        }

        setButtons(mode);
        showCalibrationPanel(calibration);
    }

    private void setButtons(String mode) {
        jrbArc.getModel().setSelected(mode.equals(CMD_ARC));
        jrbBias.getModel().setSelected(mode.equals(CMD_BIAS));
        jrbDark.getModel().setSelected(mode.equals(CMD_DARK));
        jrbLampFlat.getModel().setSelected(mode.equals(CMD_LAMP_FLAT));
    }

    private void showCalibrationPanel(XCalibration calibration) {

        if (calibration instanceof XArc) {
            ArcPanel panel = new ArcPanel((XArc)calibration);
            showComponentPanel(panel);
        } else if (calibration instanceof XBias) {
            BiasPanel panel = new BiasPanel();
            showComponentPanel(panel);
        } else if (calibration instanceof XDark) {
            DarkPanel panel = new DarkPanel((XDark)calibration);
            showComponentPanel(panel);
        } else if (calibration instanceof XLampFlat) {
            LampFlatPanel panel = new LampFlatPanel((XLampFlat)calibration);
            showComponentPanel(panel);
        } else {
            return;
        }
    }

    private void showComponentPanel(final JPanel displayPanel) {
        final JPanel detailPanel = jplCalibPanelHolder;
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                detailPanel.removeAll();
                detailPanel.add(displayPanel, BorderLayout.CENTER);
                detailPanel.validate();
                detailPanel.repaint();
            }
        });
    }

    //return XExecutiveComponent
    public ISequenceComponent getSequenceComponent() {

         jplCalibPanelHolder.getComponent(0);
        JPanel calibrationPanel = (JPanel)jplCalibPanelHolder.getComponent(0);
        if (calibrationPanel == null) {
            return null;
        }

        ISequenceComponent sequenceComponent;
        if (calibrationPanel instanceof ArcPanel) {
            sequenceComponent = ((ArcPanel)calibrationPanel).getSequenceComponent();

        } else if (calibrationPanel instanceof BiasPanel) {
            sequenceComponent = ((BiasPanel)calibrationPanel).getSequenceComponent();

        } else if (calibrationPanel instanceof DarkPanel) {
            sequenceComponent = ((DarkPanel)calibrationPanel).getSequenceComponent();

        } else if (calibrationPanel instanceof LampFlatPanel) {
            sequenceComponent = ((LampFlatPanel)calibrationPanel).getSequenceComponent();

        } else {
            return null;
        }
        
        return sequenceComponent;
    }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          

    public boolean isValidData() {
        JPanel calibrationPanel = (JPanel)jplCalibPanelHolder.getComponent(0);
        if (calibrationPanel == null) {
            return false;
        }

        if (calibrationPanel instanceof ArcPanel) {
            return ((ArcPanel)calibrationPanel).isValidData();

        } else if (calibrationPanel instanceof BiasPanel) {
            return ((BiasPanel)calibrationPanel).isValidData();

        } else if (calibrationPanel instanceof DarkPanel) {
            return ((DarkPanel)calibrationPanel).isValidData();

        } else if (calibrationPanel instanceof LampFlatPanel) {
            return ((LampFlatPanel)calibrationPanel).isValidData();

        } else {
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

        jLabel6 = new javax.swing.JLabel();
        jrbArc = new javax.swing.JRadioButton();
        jrbBias = new javax.swing.JRadioButton();
        jrbDark = new javax.swing.JRadioButton();
        jrbLampFlat = new javax.swing.JRadioButton();
        jplCalibPanelHolder = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtpHelpPane = new javax.swing.JTextPane();

        jLabel6.setText("Calibration");
        jLabel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jrbArc.setText("Arc");

        jrbBias.setText("Bias");

        jrbDark.setText("Dark");

        jrbLampFlat.setText("Lamp Flat");

        jplCalibPanelHolder.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jplCalibPanelHolder.setLayout(new java.awt.CardLayout());

        jtpHelpPane.setEditable(false);
        jtpHelpPane.setText("Arcs are only used with FRODOSpec and take an infocus image.\nLamp Flats are only used with FRODOSpec and take an out-of-focus image.\nBias frames are not usually necessary.\nDark frames are necessary for SupIRCam where we recommend a Dark frame be observed before a set of science frames.");
        jScrollPane1.setViewportView(jtpHelpPane);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jrbLampFlat)
                            .add(jrbBias)
                            .add(jrbArc)
                            .add(jrbDark)
                            .add(jLabel6))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jplCalibPanelHolder, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel6)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jrbArc)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jrbBias)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jrbDark)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jrbLampFlat))
                    .add(jplCalibPanelHolder, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 133, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel jplCalibPanelHolder;
    private javax.swing.JRadioButton jrbArc;
    private javax.swing.JRadioButton jrbBias;
    private javax.swing.JRadioButton jrbDark;
    private javax.swing.JRadioButton jrbLampFlat;
    private javax.swing.JTextPane jtpHelpPane;
    // End of variables declaration//GEN-END:variables

}
