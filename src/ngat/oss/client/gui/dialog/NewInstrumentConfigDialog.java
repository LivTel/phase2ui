/*
 * NewTimingConstraintDialog.java
 *
 * Created on April 30, 2009, 10:38 AM
 */
package ngat.oss.client.gui.dialog;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import ngat.oss.client.gui.reference.CONST;
import ngat.oss.client.gui.reference.Session;
import ngat.phase2.IInstrumentConfig;
import ngat.phase2.XBlueTwoSlitSpectrographInstrumentConfig;
import ngat.phase2.XDualBeamSpectrographInstrumentConfig;
import ngat.phase2.XImagerInstrumentConfig;
import ngat.phase2.XImagingSpectrographInstrumentConfig;
import ngat.phase2.XInstrumentConfig;
import ngat.phase2.XLiricInstrumentConfig;
import ngat.phase2.XPolarimeterInstrumentConfig;
import ngat.phase2.XMoptopInstrumentConfig;
import ngat.phase2.XTipTiltImagerInstrumentConfig;
import org.apache.log4j.Logger;

/**
 *
 * @author  nrc
 */
public class NewInstrumentConfigDialog extends javax.swing.JDialog implements ActionListener {

    static Logger logger = Logger.getLogger(NewInstrumentConfigDialog.class);

    private static final String IMAGER = "IMAGER";
    private static final String POLARIMETER = "POLARIMETER";
    private static final String MOPTOP = "MOPTOP";
    private static final String DUAL_BEAM_SPEC = "DUAL_BEAM_SPEC";
    private static final String IMAGING_SPECTROGRAPH = "IMAGING_SPEC";
    private static final String TIP_TILT = "TIP_TILT";
    private static final String TWO_SLIT_SPECTROGRAPH = "TWO_SLIT_SPECTROGRAPH";
    
    private boolean wasKilled = false;
    
    public NewInstrumentConfigDialog(boolean modal, String instrumentName) {
        this.setModal(modal);
        initComponents();
        initComponents2();
                
        setupForInstrument(instrumentName);
        centerFrame();
        addListeners();
    }
    
    public NewInstrumentConfigDialog(boolean modal) {
        this.setModal(modal);
        initComponents();
        initComponents2();
        
        centerFrame();
        addListeners();
    }

    private void setupForInstrument(String instrumentName) 
    {
        
        boolean isImager = false;
        boolean isPolarimeter = false;
        boolean isImagingSpectrograph = false;
        boolean isDualBeamSpectrograph = false;
        boolean isTwoSlitSpectrograph = false;
        
        /*
        if (instrumentName.equals(CONST.RATCAM)) {
            isImager = true;
        */
        if (instrumentName.equals(CONST.RISE)) 
        {
            isImager = true;
        } 
        else if (instrumentName.equals(CONST.IO_O)) 
        {
            isImager = true;
        } 
        else if (instrumentName.equals(CONST.IO_I)) 
        {
            isImager = true;
        } 
        else if (instrumentName.equals(CONST.LIRIC)) 
        {
            isImager = true;
        } 
        else if (instrumentName.equals(CONST.LOCI)) 
        {
            isImager = true;
        } 
        else if (instrumentName.equals(CONST.RINGO3) ) 
        {
            isPolarimeter = true;
        } 
        else if (instrumentName.equals(CONST.MOPTOP) ) 
        {
            isPolarimeter = true;
        } 
        else if (instrumentName.equals(CONST.FRODO) || instrumentName.equals(CONST.FRODO_BLUE) || instrumentName.equals(CONST.FRODO_RED)) 
        {
            isDualBeamSpectrograph = true;
        } 
        else if (instrumentName.equals(CONST.SPRAT)) 
        {
            isImagingSpectrograph = true;
        } 
        else if (instrumentName.equals(CONST.LOTUS) ) 
        {
            isTwoSlitSpectrograph = true;
        } 

        boolean canChangeInstrument = true;

        //select radio buttons
        
        //build default instrument config for a known instrument
        XInstrumentConfig instrumentConfig = null;
        if (isImager) 
        {
            jrbImager.setSelected(true);
            jrbImager.setEnabled(true);
            jrbTwoSlitSpectrograph.setEnabled(false);
            jrbPolarimeter.setEnabled(false);
            jrbDualBeamSpec.setEnabled(false);
            jrbImagingSpectrograph.setEnabled(false);
            if (instrumentName.equals(CONST.LIRIC))
            {
                XLiricInstrumentConfig liricInstrumentConfig = new XLiricInstrumentConfig();
                liricInstrumentConfig.setInstrumentName(instrumentName);
                // configure default nudgematic offset size and coadd exposure length
                liricInstrumentConfig.setNudgematicOffsetSize(XLiricInstrumentConfig.NUDGEMATIC_OFFSET_SIZE_SMALL);
                liricInstrumentConfig.setCoaddExposureLength(1000);
                instrumentConfig = liricInstrumentConfig;
            }
            else
            {
                XImagerInstrumentConfig imagerInstrumentConfig = new XImagerInstrumentConfig();
                imagerInstrumentConfig.setInstrumentName(instrumentName);
                instrumentConfig = imagerInstrumentConfig;
            }            
        } 
        else if (isPolarimeter) 
        {
            jrbPolarimeter.setSelected(true);
            jrbPolarimeter.setEnabled(true);
            jrbTwoSlitSpectrograph.setEnabled(false);
            jrbDualBeamSpec.setEnabled(false);
            jrbImagingSpectrograph.setEnabled(false);
            jrbImager.setEnabled(false);
            // Assume moptop rather than Ringo3 here
            XMoptopInstrumentConfig polarimeterInstrumentConfig = new XMoptopInstrumentConfig();
            //XPolarimeterInstrumentConfig polarimeterInstrumentConfig = new XPolarimeterInstrumentConfig();
            polarimeterInstrumentConfig.setInstrumentName(instrumentName);
            instrumentConfig = polarimeterInstrumentConfig;
            canChangeInstrument = false;
            
        } 
        else if (isDualBeamSpectrograph) 
        {
            if (instrumentName.equalsIgnoreCase(CONST.FRODO)) 
            {
                jrbDualBeamSpec.setSelected(true);
                jrbDualBeamSpec.setEnabled(true);
                jrbPolarimeter.setEnabled(false);
                jrbImagingSpectrograph.setEnabled(false);
                jrbImager.setEnabled(false);
                instrumentConfig = new XDualBeamSpectrographInstrumentConfig();
            } 
            else if (instrumentName.equalsIgnoreCase(CONST.FRODO_RED) || (instrumentName.equalsIgnoreCase(CONST.FRODO_BLUE))) 
            {
                jrbDualBeamSpec.setSelected(true);
                jrbDualBeamSpec.setEnabled(true);
                jrbPolarimeter.setEnabled(false);
                jrbImagingSpectrograph.setEnabled(false);
                jrbImager.setEnabled(false);
                instrumentConfig = new XDualBeamSpectrographInstrumentConfig();
                instrumentConfig.setInstrumentName(instrumentName);
                canChangeInstrument = false;
            } 
            else 
            {
                JOptionPane.showMessageDialog(this, "instrument name " + instrumentName + " not implemented" );
                return;
            }   
            
        } 
        else if (isImagingSpectrograph) 
        {
            jrbImagingSpectrograph.setSelected(true);
            jrbImagingSpectrograph.setEnabled(false);
            jrbTwoSlitSpectrograph.setEnabled(false);
            jrbDualBeamSpec.setEnabled(false);
            jrbPolarimeter.setEnabled(false);
            jrbImager.setEnabled(false);
            XImagingSpectrographInstrumentConfig imagingSpectrographInstrumentConfig = new XImagingSpectrographInstrumentConfig();
            imagingSpectrographInstrumentConfig.setInstrumentName(instrumentName);
            instrumentConfig = imagingSpectrographInstrumentConfig;
            canChangeInstrument = false;
            
        } 
        else if (isTwoSlitSpectrograph) 
        {
            jrbTwoSlitSpectrograph.setEnabled(true);
            jrbTwoSlitSpectrograph.setSelected(true);
            jrbImagingSpectrograph.setEnabled(false);
            jrbDualBeamSpec.setEnabled(false);
            jrbPolarimeter.setEnabled(false);
            jrbImager.setEnabled(false);
            XBlueTwoSlitSpectrographInstrumentConfig blueTwoSlitSpectrographInstrumentConfig = new XBlueTwoSlitSpectrographInstrumentConfig();
            blueTwoSlitSpectrographInstrumentConfig.setInstrumentName(instrumentName);
            instrumentConfig = blueTwoSlitSpectrographInstrumentConfig;
            canChangeInstrument = false;
        } 
        else 
        {
            JOptionPane.showMessageDialog(this, "UNKNOWN instrument type");
            return;
        }

        try 
        {
            instrumentConfigEditorPanel.setInstrumentConfig(instrumentConfig, true, canChangeInstrument);
        } 
        catch (Exception ex) 
        {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
    
    public IInstrumentConfig getInstrumentConfig() {
        return instrumentConfigEditorPanel.getInstrumentConfig();
    }

    public void setInstrumentConfig(IInstrumentConfig instrumentConfig) throws Exception {
        instrumentConfigEditorPanel.setInstrumentConfig(instrumentConfig, true, true);
    }

    private void addListeners() {
        
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                wasKilled = true;
                NewInstrumentConfigDialog.this.setVisible(false);
                NewInstrumentConfigDialog.this.dispose();
            }
        });
        
        jrbImager.addActionListener(this);
        jrbPolarimeter.addActionListener(this);
        jrbDualBeamSpec.addActionListener(this);
        jrbImagingSpectrograph.addActionListener(this);
        jrbTwoSlitSpectrograph.addActionListener(this);
        
    }

    private void initComponents2() {
        
        jrbImager.setActionCommand(IMAGER);
        jrbPolarimeter.setActionCommand(POLARIMETER);
        jrbDualBeamSpec.setActionCommand(DUAL_BEAM_SPEC);
        jrbImagingSpectrograph.setActionCommand(IMAGING_SPECTROGRAPH);
        jrbTwoSlitSpectrograph.setActionCommand(TWO_SLIT_SPECTROGRAPH);
        
        ButtonGroup group = new ButtonGroup();
        group.add(jrbImager);
        group.add(jrbPolarimeter);
        group.add(jrbDualBeamSpec);
        group.add(jrbImagingSpectrograph);
        group.add(jrbTwoSlitSpectrograph);

        //select the imager button and fire the related actions to that selection
        //jrbImager.setSelected(true);
        //handleCommand(IMAGER);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String actionCommand = actionEvent.getActionCommand();
        handleCommand(actionCommand);
    }

    private void handleCommand(String actionCommand) {
        IInstrumentConfig instrumentConfig = null;

        if (actionCommand.equals(DUAL_BEAM_SPEC)) {
            instrumentConfig = new XDualBeamSpectrographInstrumentConfig();
        } else if (actionCommand.equals(IMAGER)) {
             instrumentConfig = new XImagerInstrumentConfig();
        } else if (actionCommand.equals(POLARIMETER)) {
             instrumentConfig = new XMoptopInstrumentConfig();
        } else if (actionCommand.equals(IMAGING_SPECTROGRAPH)) {
             instrumentConfig = new XImagingSpectrographInstrumentConfig();
        } else if (actionCommand.equals(TIP_TILT)) {
             instrumentConfig = new XTipTiltImagerInstrumentConfig();
        } else if (actionCommand.equals(TWO_SLIT_SPECTROGRAPH)) {
             instrumentConfig = new XBlueTwoSlitSpectrographInstrumentConfig();
        } else {
            JOptionPane.showMessageDialog(this, "UNKNOWN action command:" + actionCommand);
            return;
        }
        
        try {
            instrumentConfigEditorPanel.setInstrumentConfig(instrumentConfig, true, true);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            JOptionPane.showMessageDialog(this, ex.getMessage());
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

        jPanel1 = new javax.swing.JPanel();
        jrbDualBeamSpec = new javax.swing.JRadioButton();
        jrbImager = new javax.swing.JRadioButton();
        jrbPolarimeter = new javax.swing.JRadioButton();
        jrbImagingSpectrograph = new javax.swing.JRadioButton();
        jrbTwoSlitSpectrograph = new javax.swing.JRadioButton();
        jbtnCreate = new javax.swing.JButton();
        jbtnCancel = new javax.swing.JButton();
        instrumentConfigEditorPanel = new ngat.beans.guibeans.InstrumentConfigEditorPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Instrument Config Creation");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                windowClosingHandler(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Select instrument config type"));

        jrbDualBeamSpec.setText("Dual-Beam Spectrograph");
        jrbDualBeamSpec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbDualBeamSpecActionPerformed(evt);
            }
        });

        jrbImager.setText("Imager");
        jrbImager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbImagerActionPerformed(evt);
            }
        });

        jrbPolarimeter.setText("Polarimeter");

        jrbImagingSpectrograph.setText("Imaging Spectrograph");

        jrbTwoSlitSpectrograph.setText("Two Slit Spectrograph");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jrbPolarimeter)
                    .add(jrbImager)
                    .add(jrbDualBeamSpec)
                    .add(jrbImagingSpectrograph)
                    .add(jrbTwoSlitSpectrograph))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jrbImager)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jrbPolarimeter)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jrbDualBeamSpec)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jrbImagingSpectrograph)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jrbTwoSlitSpectrograph)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jbtnCreate.setForeground(new java.awt.Color(255, 0, 0));
        jbtnCreate.setText("Create");
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

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(jbtnCreate)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jbtnCancel)
                .addContainerGap(411, Short.MAX_VALUE))
            .add(instrumentConfigEditorPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(instrumentConfigEditorPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jbtnCreate)
                    .add(jbtnCancel)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jbtnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCreateActionPerformed

    if (instrumentConfigEditorPanel.containsValidInstrumentConfig()) {
        this.setVisible(false);
        this.dispose();
    } else {
        JOptionPane.showMessageDialog(this, "Please make sure the instrument config fields are correctly populated");
    }

}//GEN-LAST:event_jbtnCreateActionPerformed

private void jbtnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelActionPerformed
    wasKilled = true;
    try {
        setInstrumentConfig(null);
    } catch (Exception ex) {
        ex.printStackTrace();
        logger.error(ex);
    }
    this.setVisible(false);
    this.dispose();
}//GEN-LAST:event_jbtnCancelActionPerformed

private void windowClosingHandler(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosingHandler
    wasKilled = true;
    try {
        setInstrumentConfig(null);
    } catch (Exception ex) {
        ex.printStackTrace();
        logger.error(ex);
    }
}//GEN-LAST:event_windowClosingHandler

private void jrbDualBeamSpecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbDualBeamSpecActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_jrbDualBeamSpecActionPerformed

private void jrbImagerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbImagerActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_jrbImagerActionPerformed

public boolean wasKilled() {
        return wasKilled;
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
                        NewInstrumentConfigDialog.this.setLocation(x, y);
                    }
                });
    }
    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NewInstrumentConfigDialog dialog = new NewInstrumentConfigDialog(true);
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ngat.beans.guibeans.InstrumentConfigEditorPanel instrumentConfigEditorPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jbtnCancel;
    private javax.swing.JButton jbtnCreate;
    private javax.swing.JRadioButton jrbDualBeamSpec;
    private javax.swing.JRadioButton jrbImager;
    private javax.swing.JRadioButton jrbImagingSpectrograph;
    private javax.swing.JRadioButton jrbPolarimeter;
    private javax.swing.JRadioButton jrbTwoSlitSpectrograph;
    // End of variables declaration//GEN-END:variables

}
