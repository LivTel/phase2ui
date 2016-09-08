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
import java.util.Date;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import ngat.phase2.ITimingConstraint;
import ngat.phase2.XEphemerisTimingConstraint;
import ngat.phase2.XFixedTimingConstraint;
import ngat.phase2.XFlexibleTimingConstraint;
import ngat.phase2.XMinimumIntervalTimingConstraint;
import ngat.phase2.XMonitorTimingConstraint;
import org.apache.log4j.Logger;

/**
 *
 * @author  nrc
 */
public class NewTimingConstraintDialog extends javax.swing.JDialog implements ActionListener {

    static Logger logger = Logger.getLogger(NewTimingConstraintDialog.class);

    //private ITimingConstraint timingConstraint;
    private static final String EPHEMERIS = "Ephemeris";
    private static final String FIXED = "Fixed";
    private static final String FLEXIBLE = "Flexible";
    private static final String MIN_INTERVAL = "MinInterval";
    private static final String MONITOR = "Monitor";
    private boolean allowFixedTimingConstraints;

    /** Creates new form NewTimingConstraintDialog */
    public NewTimingConstraintDialog(boolean allowFixedTimingConstraints, boolean modal) {
        this.setModal(modal);
        this.allowFixedTimingConstraints = allowFixedTimingConstraints;
        
        initComponents();
        initComponents2();
        centerFrame();
        addListeners();
    }
    
    public ITimingConstraint getTimingConstraint() {
        return timingConstraintEditorPanel.getTimingConstraint();
    }

    public void setTimingConstraint(ITimingConstraint timingConstraint) throws Exception {
        timingConstraintEditorPanel.setTimingConstraint(timingConstraint);
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
                        NewTimingConstraintDialog.this.setLocation(x, y);
                    }
                });
    }

    private void addListeners() {
        this.addWindowListener(new java.awt.event.WindowAdapter() {

            public void windowClosing(java.awt.event.WindowEvent e) {
                NewTimingConstraintDialog.this.setVisible(false);
                NewTimingConstraintDialog.this.dispose();
            }
        });

        jrbEphemeris.addActionListener(this);
        jrbFixed.addActionListener(this);
        jrbFlexible.addActionListener(this);
        jrbMinInterval.addActionListener(this);
        jrbMonitor.addActionListener(this);
    }

    private void initComponents2() {

        jrbFixed.setEnabled(allowFixedTimingConstraints);

        timingConstraintEditorPanel.setControlButtonsVisibility(false);

        jrbEphemeris.setActionCommand(EPHEMERIS);
        jrbFixed.setActionCommand(FIXED);
        jrbFlexible.setActionCommand(FLEXIBLE);
        jrbMinInterval.setActionCommand(MIN_INTERVAL);
        jrbMonitor.setActionCommand(MONITOR);

        ButtonGroup group = new ButtonGroup();
        group.add(jrbEphemeris);
        group.add(jrbFixed);
        group.add(jrbFlexible);
        group.add(jrbMinInterval);
        group.add(jrbMonitor);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String actionCommand = actionEvent.getActionCommand();
        ITimingConstraint timingConstraint = null;

        if (actionCommand.equals(EPHEMERIS)) {
            XEphemerisTimingConstraint ephemerisTimingConstraint = new XEphemerisTimingConstraint();
            ephemerisTimingConstraint.setStart(new Date().getTime());
            ephemerisTimingConstraint.setEnd(new Date().getTime());
            timingConstraint = ephemerisTimingConstraint;
        } else if (actionCommand.equals(FIXED)) {
            XFixedTimingConstraint fixedTimingConstraint = new XFixedTimingConstraint();
            fixedTimingConstraint.setFixedTime(new Date().getTime());
            timingConstraint = fixedTimingConstraint;
        } else if (actionCommand.equals(FLEXIBLE)) {
            XFlexibleTimingConstraint flexibleTimingConstraint = new XFlexibleTimingConstraint();
            flexibleTimingConstraint.setActivationDate(new Date().getTime());
            flexibleTimingConstraint.setExpiryDate(new Date().getTime());
            timingConstraint = flexibleTimingConstraint;
        } else if (actionCommand.equals(MIN_INTERVAL)) {
            XMinimumIntervalTimingConstraint minimumIntervalTimingConstraint = new XMinimumIntervalTimingConstraint();
            minimumIntervalTimingConstraint.setStart(new Date().getTime());
            minimumIntervalTimingConstraint.setEnd(new Date().getTime());
            minimumIntervalTimingConstraint.setMaximumRepeats(5); //default number for new instance
            timingConstraint = minimumIntervalTimingConstraint;
        } else if (actionCommand.equals(MONITOR)) {
            XMonitorTimingConstraint monitorTimingConstraint = new XMonitorTimingConstraint();
            monitorTimingConstraint.setStartDate(new Date().getTime());
            monitorTimingConstraint.setEndDate(new Date().getTime());
            timingConstraint = monitorTimingConstraint;
        }

        try {
            timingConstraintEditorPanel.setTimingConstraint(timingConstraint);
        } catch (Exception ex) {
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
        jrbEphemeris = new javax.swing.JRadioButton();
        jrbFixed = new javax.swing.JRadioButton();
        jrbFlexible = new javax.swing.JRadioButton();
        jrbMinInterval = new javax.swing.JRadioButton();
        jrbMonitor = new javax.swing.JRadioButton();
        timingConstraintEditorPanel = new ngat.beans.guibeans.TimingConstraintEditorPanel(true);
        jbtnCreate = new javax.swing.JButton();
        jbtnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Timing Constraint Creation");
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Select constraint type"));

        jrbEphemeris.setText("Phased Timing Constraint (single group)");

        jrbFixed.setText("Fixed Timing Constraint (single group)");

        jrbFlexible.setText("Flexible Timing Constraint (single group)");
        jrbFlexible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbFlexibleActionPerformed(evt);
            }
        });

        jrbMinInterval.setText("Minimum Interval Timing Constraint (repeating group)");

        jrbMonitor.setText("Monitor Timing Constraint (repeating group)");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jrbEphemeris)
                    .add(jrbFixed)
                    .add(jrbFlexible)
                    .add(jrbMinInterval)
                    .add(jrbMonitor))
                .addContainerGap(258, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jrbEphemeris)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jrbFixed)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jrbFlexible)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jrbMinInterval)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jrbMonitor)
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
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(jbtnCreate)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jbtnCancel)
                .addContainerGap())
            .add(timingConstraintEditorPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(timingConstraintEditorPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jbtnCreate)
                    .add(jbtnCancel)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jbtnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCreateActionPerformed
// TODO add your handling code here:
    if (timingConstraintEditorPanel.containsValidTimingConstraint()) {
        this.setVisible(false);
        this.dispose();
    } else {
        JOptionPane.showMessageDialog(this, "Incorrectly formatted timing constraint");
    }
}//GEN-LAST:event_jbtnCreateActionPerformed

private void jbtnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelActionPerformed
    try {
        setTimingConstraint(null);
    } catch (Exception ex) {
        ex.printStackTrace();
        logger.error(ex);
    }
    this.setVisible(false);
    this.dispose();
}//GEN-LAST:event_jbtnCancelActionPerformed

private void jrbFlexibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbFlexibleActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_jrbFlexibleActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NewTimingConstraintDialog dialog = new NewTimingConstraintDialog(false, true);
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jbtnCancel;
    private javax.swing.JButton jbtnCreate;
    private javax.swing.JRadioButton jrbEphemeris;
    private javax.swing.JRadioButton jrbFixed;
    private javax.swing.JRadioButton jrbFlexible;
    private javax.swing.JRadioButton jrbMinInterval;
    private javax.swing.JRadioButton jrbMonitor;
    private ngat.beans.guibeans.TimingConstraintEditorPanel timingConstraintEditorPanel;
    // End of variables declaration//GEN-END:variables

}
