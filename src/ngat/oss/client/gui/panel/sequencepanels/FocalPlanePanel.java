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
 * This could also be called an Instrument Change Panel
 */
public class FocalPlanePanel extends javax.swing.JPanel implements SequenceComponentPanel {

    static Logger logger = Logger.getLogger(FocalPlanePanel.class);
    
    public FocalPlanePanel() {
        initComponents();
        
        //jcbTargetInstrument.setModel(new javax.swing.DefaultComboBoxModel(CONST.ACQUISITION_SCIENCE_INSTRUMENTS));
        
        if (Session.getInstance().getUser().isSuperUser()) {
            jcbTargetInstrument.setModel(new javax.swing.DefaultComboBoxModel(CONST.ACQUISITION_SCIENCE_INSTRUMENTS));
        } else {
            jcbTargetInstrument.setModel(new javax.swing.DefaultComboBoxModel(CONST.ACQUISITION_SCIENCE_INSTRUMENTS));
        }
    }

    public FocalPlanePanel(XAcquisitionConfig acquisitionConfig) {
        initComponents();
        
        //jcbTargetInstrument.setModel(new javax.swing.DefaultComboBoxModel(CONST.ACQUISITION_SCIENCE_INSTRUMENTS));
        
        if (Session.getInstance().getUser().isSuperUser()) {
            jcbTargetInstrument.setModel(new javax.swing.DefaultComboBoxModel(CONST.ACQUISITION_SCIENCE_INSTRUMENTS));
        } else {
            jcbTargetInstrument.setModel(new javax.swing.DefaultComboBoxModel(CONST.ACQUISITION_SCIENCE_INSTRUMENTS));
        }

        String targetInstrumentName = acquisitionConfig.getTargetInstrumentName();

        if (targetInstrumentName != null) {
            if (targetInstrumentName.equalsIgnoreCase(CONST.RATCAM)) {
                JOptionPane.showMessageDialog(this, "RATCam is no longer available");
            }
            if (targetInstrumentName.equalsIgnoreCase(CONST.IO_THOR)) {
                JOptionPane.showMessageDialog(this, "THOR is no longer available");
            }
        }

        jcbTargetInstrument.setSelectedItem(targetInstrumentName);
        
    }


    /**
     * 
     * @return XExecutiveComponent containing ACQUSITION_CONFIG configured as a FOCAL PLANE for a target instrument (i.e. an instrument change)
     */
    public ISequenceComponent getSequenceComponent() {

        String targetInstrumentName = (String) jcbTargetInstrument.getSelectedItem();
        
        
        XAcquisitionConfig acquisitionConfig = new XAcquisitionConfig();
        
        acquisitionConfig.setMode(IAcquisitionConfig.INSTRUMENT_CHANGE);
        acquisitionConfig.setPrecision(IAcquisitionConfig.PRECISION_NOT_SET);
        acquisitionConfig.setTargetInstrumentName(targetInstrumentName);
        acquisitionConfig.setAcquisitionInstrumentName(null);
        acquisitionConfig.setAllowAlternative(false);
        
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
        jLabel1 = new javax.swing.JLabel();
        jcbTargetInstrument = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtpHelpPane = new javax.swing.JTextPane();

        jLabel13.setText("Focal Plane");
        jLabel13.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Target Instrument:");

        jcbTargetInstrument.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbTargetInstrumentActionPerformed(evt);
            }
        });

        jtpHelpPane.setEditable(false);
        jtpHelpPane.setText("Offset the telescope to place the target on the default field position for the specified instrument. E.g. for an imager this is the approximate FoV centre.\n\nNote that if you are using an instrument to fine tune the pointing, you should select that instrument and not the science instrument.");
        jScrollPane1.setViewportView(jtpHelpPane);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(24, 24, 24)
                        .add(jLabel1)
                        .add(5, 5, 5)
                        .add(jcbTargetInstrument, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 152, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jLabel13))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel13)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jcbTargetInstrument, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jcbTargetInstrumentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbTargetInstrumentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jcbTargetInstrumentActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox jcbTargetInstrument;
    private javax.swing.JTextPane jtpHelpPane;
    // End of variables declaration//GEN-END:variables

}
