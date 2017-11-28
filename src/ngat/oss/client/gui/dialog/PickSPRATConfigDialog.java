/*
 * NewTimingConstraintDialog.java
 *
 * Created on April 30, 2009, 10:38 AM
 */
package ngat.oss.client.gui.dialog;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ngat.oss.client.Phase2ModelClient;
import ngat.oss.exception.Phase2Exception;
import ngat.phase2.IInstrumentConfig;
import ngat.phase2.IProgram;
import ngat.phase2.XImagingSpectrographInstrumentConfig;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class PickSPRATConfigDialog extends javax.swing.JDialog {

    static Logger logger = Logger.getLogger(PickSPRATConfigDialog.class);

    private XImagingSpectrographInstrumentConfig imagingSpectrographInstrumentConfig;
    private IProgram program;
    private ArrayList SPRATConfigsInProgram = new ArrayList();

    public PickSPRATConfigDialog(boolean modal, IProgram program) {
        this.setModal(modal);
        this.program = program;
        initComponents();
        populateComponents();
        centerFrame();
        addListeners();
    }

    private void populateComponents() {
        //get SPRAT instrument configs in programme and populate jcbInstrumentConfigNames and SPRATConfigsInProgram list
        Phase2ModelClient phase2ModelClient = Phase2ModelClient.getInstance();
        try {
            List instrumentCfgsOfProgram = phase2ModelClient.listInstrumentConfigs(program.getID());
            Iterator i = instrumentCfgsOfProgram.iterator();
            while (i.hasNext()) {
                IInstrumentConfig instrumentConfig = (IInstrumentConfig) i.next();
                if (instrumentConfig instanceof XImagingSpectrographInstrumentConfig) {
                    XImagingSpectrographInstrumentConfig imagingSpectrographInstrumentConfig = (XImagingSpectrographInstrumentConfig) instrumentConfig;
                    SPRATConfigsInProgram.add(imagingSpectrographInstrumentConfig);
                    jcbInstrumentConfigNames.addItem(imagingSpectrographInstrumentConfig.getName());
                }
            }
        } catch (Phase2Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
        }

        //change gui state dependent upon list of configs
        jlblWarnOfNoConfigs.setVisible(SPRATConfigsInProgram.size() == 0);
        jbtnCreate.setEnabled(SPRATConfigsInProgram.size() != 0);
    }

    private void centerFrame() {
        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        double hd = screenDimension.getHeight();
        double wd = screenDimension.getWidth();

        double yd = (hd - this.getBounds().getHeight()) / 2;
        double xd = (wd - this.getBounds().getWidth()) / 2;

        final int x = (int) xd;
        final int y = (int) yd;

        EventQueue.invokeLater(
                new Runnable() {
                    public void run() {
                        PickSPRATConfigDialog.this.setLocation(x, y);
                    }
                });
    }

    private void addListeners() {
        this.addWindowListener(new java.awt.event.WindowAdapter() {

            public void windowClosing(java.awt.event.WindowEvent e) {
                imagingSpectrographInstrumentConfig = null;
                
                PickSPRATConfigDialog.this.setVisible(false);
                PickSPRATConfigDialog.this.dispose();
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jbtnCreate = new javax.swing.JButton();
        jbtnCancel = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jcbInstrumentConfigNames = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jlblWarnOfNoConfigs = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select Instrument Config");

        jbtnCreate.setForeground(new java.awt.Color(255, 0, 0));
        jbtnCreate.setText("Add");
        jbtnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCreateActionPerformed(evt);
            }
        });

        jbtnCancel.setText("Cancel");
        jbtnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCancelActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jLabel3.setText("Select");

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 10)); // NOI18N
        jLabel1.setText("SPRAT Configuration");

        jlblWarnOfNoConfigs.setFont(new java.awt.Font("Lucida Grande", 1, 12)); // NOI18N
        jlblWarnOfNoConfigs.setForeground(new java.awt.Color(204, 0, 0));
        jlblWarnOfNoConfigs.setText("NO SPRAT CONFIGS HAVE BEEN SET UP");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel1))
                    .add(layout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jcbInstrumentConfigNames, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 331, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jbtnCreate)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jbtnCancel))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jlblWarnOfNoConfigs, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .add(20, 20, 20)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(jcbInstrumentConfigNames, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(13, 13, 13)
                .add(jlblWarnOfNoConfigs)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jbtnCancel)
                    .add(jbtnCreate))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


private void jbtnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCreateActionPerformed
    
    //set the dualBeamSpectrographInstrumentConfig object, dependent on GUI selection
    int selectedIndex = jcbInstrumentConfigNames.getSelectedIndex();
    if (selectedIndex == -1) {
        imagingSpectrographInstrumentConfig = null;
        return;
    }
    imagingSpectrographInstrumentConfig = (XImagingSpectrographInstrumentConfig) SPRATConfigsInProgram.get(selectedIndex);
   
    this.setVisible(false);
    this.dispose();
}//GEN-LAST:event_jbtnCreateActionPerformed

private void jbtnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelActionPerformed
    imagingSpectrographInstrumentConfig = null;
    this.setVisible(false);
    this.dispose();
}//GEN-LAST:event_jbtnCancelActionPerformed

public XImagingSpectrographInstrumentConfig getConfig() {
    return this.imagingSpectrographInstrumentConfig;
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton jbtnCancel;
    private javax.swing.JButton jbtnCreate;
    private javax.swing.JComboBox jcbInstrumentConfigNames;
    private javax.swing.JLabel jlblWarnOfNoConfigs;
    // End of variables declaration//GEN-END:variables
}
