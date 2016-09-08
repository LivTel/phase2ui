/*
 * NewTimingConstraintDialog.java
 *
 * Created on April 30, 2009, 10:38 AM
 */
package ngat.oss.client.gui.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import ngat.oss.client.gui.panel.interfaces.IObservingConstraintPanel;
import ngat.oss.client.gui.panel.observingConstraint.AirMassObservingConstraintPanel;
import ngat.oss.client.gui.panel.observingConstraint.HourAngleObservingConstraintPanel;
import ngat.oss.client.gui.panel.observingConstraint.PhotometricityObservingConstraintPanel;
import ngat.oss.client.gui.panel.observingConstraint.SeeingObservingConstraintPanel;
import ngat.oss.client.gui.panel.observingConstraint.SkyBrightnessObservingConstraintPanel;
import ngat.phase2.IObservingConstraint;
import ngat.phase2.XAirmassConstraint;
import ngat.phase2.XHourAngleConstraint;
import ngat.phase2.XPhotometricityConstraint;
import ngat.phase2.XSeeingConstraint;
import ngat.phase2.XSkyBrightnessConstraint;
import org.apache.log4j.Logger;

/**
 *
 * @author  nrc
 */
public class NewObservingConstraintDialog extends javax.swing.JDialog implements ActionListener {

    static Logger logger = Logger.getLogger(NewObservingConstraintDialog.class);

    private static final String AIRMASS = "Airmass";
    private static final String HOUR_ANGLE = "HourAngle";
    private static final String LUNAR_DISTANCE = "LunarDistance";
    private static final String LUNAR_ELEVATION = "LunarElevation";
    private static final String PHASE = "Phase";
    private static final String PHOTOMETRICITY = "Photometricity";
    private static final String SEEING = "Seeing";
    private static final String SKY_BRIGHTNESS = "SkyBrightness";
    private static final String SOLAR_ELEVATION = "SolarElevation";
    
    private List panelObservingConstraints;
    private JPanel viewPanel = new JPanel();
    
    /** Creates new form NewTimingConstraintDialog */
    public NewObservingConstraintDialog(List panelObservingConstraints, boolean modal) {
        
        this.setModal(modal);
        this.panelObservingConstraints = panelObservingConstraints;
        
        initComponents();
        initComponents2();
        centerFrame();
        addListeners();
    }
    /*
    public NewObservingConstraintDialog(java.awt.Frame parent, List panelObservingConstraints, boolean modal) {
        super(parent, modal);

        this.panelObservingConstraints = panelObservingConstraints;
        
        initComponents();
        initComponents2();
        centerFrame();
        addListeners();
    }
    */

    //return the observing constraint from the gui panel
    public IObservingConstraint getObservingConstraint() {
        Component[] components = viewPanel.getComponents();
        if (components.length == 0) {
            return null;
        }
        IObservingConstraintPanel observingConstraintPanel = (IObservingConstraintPanel)components[0];
        return observingConstraintPanel.getObservingConstraint();
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
                        NewObservingConstraintDialog.this.setLocation(x, y);
                    }
                });
    }

    private void addListeners() {
        this.addWindowListener(new java.awt.event.WindowAdapter() {

            public void windowClosing(java.awt.event.WindowEvent e) {
                NewObservingConstraintDialog.this.setVisible(false);
                NewObservingConstraintDialog.this.dispose();
            }
        });

        jrbSkyBrightness.addActionListener(this);
        jrbAirmass.addActionListener(this);
        jrbHourAngle.addActionListener(this);
        jrbPhotometricity.addActionListener(this);
        jrbSeeing.addActionListener(this);
        
    }

    private void initComponents2() {

        //hide buttons of observing constraints already within group
        Iterator i = panelObservingConstraints.iterator();
        while (i.hasNext()) {
            IObservingConstraint foundPanelObservingConstraint = (IObservingConstraint) i.next();

            if (foundPanelObservingConstraint instanceof XSkyBrightnessConstraint) {
                jrbSkyBrightness.setEnabled(false);
             } else if (foundPanelObservingConstraint instanceof XAirmassConstraint) {
               jrbAirmass.setEnabled(false);
            } else if (foundPanelObservingConstraint instanceof XHourAngleConstraint) {
               jrbHourAngle.setEnabled(false);
            } else if (foundPanelObservingConstraint instanceof XPhotometricityConstraint) {
                jrbPhotometricity.setEnabled(false);
            } else if (foundPanelObservingConstraint instanceof XSeeingConstraint) {
                jrbSeeing.setEnabled(false);
            }
        }

        jrbSkyBrightness.setActionCommand(SKY_BRIGHTNESS);
        jrbAirmass.setActionCommand(AIRMASS);
        jrbHourAngle.setActionCommand(HOUR_ANGLE);
        jrbPhotometricity.setActionCommand(PHOTOMETRICITY);
        jrbSeeing.setActionCommand(SEEING);
        
        ButtonGroup group = new ButtonGroup();
        group.add(jrbSkyBrightness);
        group.add(jrbAirmass);
        group.add(jrbHourAngle);
        group.add(jrbPhotometricity);
        group.add(jrbSeeing);
        
        viewPanel.setLayout(new BoxLayout(viewPanel, BoxLayout.Y_AXIS));
        jScrollPane.setViewportView(viewPanel);
    }

    public void actionPerformed(ActionEvent actionEvent) {

        //JRadioButton clicked

        String actionCommand = actionEvent.getActionCommand();
        IObservingConstraint observingConstraint;
        
        if (actionCommand.equals(AIRMASS)) {
            observingConstraint = new XAirmassConstraint();
        } else if (actionCommand.equals(HOUR_ANGLE)) {
            observingConstraint = new XHourAngleConstraint();
        } else if (actionCommand.equals(PHOTOMETRICITY)) {
            observingConstraint = new XPhotometricityConstraint();
        } else if (actionCommand.equals(SEEING)) {
            observingConstraint = new XSeeingConstraint();
        } else if (actionCommand.equals(SKY_BRIGHTNESS)) {
            observingConstraint = new XSkyBrightnessConstraint();
        } else {
            System.out.println("UNRECOGNISED COMMAND");
            return;
        }

        try {
            updateView(observingConstraint);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
        }
    }

    private void updateView(IObservingConstraint observingConstraint) throws Exception {

        if (observingConstraint == null) {
            //System.out.println("observingConstraint == null");
            viewPanel.removeAll();
            jScrollPane.validate();
            jScrollPane.repaint();
            return;
        }

        if (observingConstraint instanceof XAirmassConstraint) {
            setObservingConstraintPanelTitle("Airmass Observing Constraint");
            XAirmassConstraint constraint = (XAirmassConstraint) observingConstraint;
            AirMassObservingConstraintPanel constraintPanel = new AirMassObservingConstraintPanel(constraint, true);
            viewPanel.removeAll();
            viewPanel.add(constraintPanel);

        } else if (observingConstraint instanceof XHourAngleConstraint) {
            setObservingConstraintPanelTitle("Hour Angle Observing Constraint");
            XHourAngleConstraint constraint = (XHourAngleConstraint) observingConstraint;
            HourAngleObservingConstraintPanel constraintPanel = new HourAngleObservingConstraintPanel(constraint, true);
            viewPanel.removeAll();
            viewPanel.add(constraintPanel);

        } else if (observingConstraint instanceof XPhotometricityConstraint) {
            setObservingConstraintPanelTitle("Photometricity Constraint");
            XPhotometricityConstraint constraint = (XPhotometricityConstraint) observingConstraint;
            PhotometricityObservingConstraintPanel constraintPanel = new PhotometricityObservingConstraintPanel(constraint, true);
            viewPanel.removeAll();
            viewPanel.add(constraintPanel);

        } else if (observingConstraint instanceof XSeeingConstraint) {
            setObservingConstraintPanelTitle("Seeing Constraint");
            XSeeingConstraint constraint = (XSeeingConstraint) observingConstraint;
            SeeingObservingConstraintPanel constraintPanel = new SeeingObservingConstraintPanel(constraint, true);
            viewPanel.removeAll();
            viewPanel.add(constraintPanel);

        } else if (observingConstraint instanceof XSkyBrightnessConstraint) {
            setObservingConstraintPanelTitle("Sky Brightness Constraint");
            XSkyBrightnessConstraint constraint = (XSkyBrightnessConstraint) observingConstraint;
            SkyBrightnessObservingConstraintPanel constraintPanel = new SkyBrightnessObservingConstraintPanel(constraint, true);
            viewPanel.removeAll();
            viewPanel.add(constraintPanel);

        } else {
            setObservingConstraintPanelTitle("Unknown Timing Constraint");
            throw new Exception("Unknown timing constraint type");
        }

        jScrollPane.validate();
        jScrollPane.repaint();
    }

    private void setObservingConstraintPanelTitle(String title) {
        TitledBorder titledBorder = (TitledBorder) jplObservingConstraintContainerPanel.getBorder();
        titledBorder.setTitle(title);
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
        jrbSkyBrightness = new javax.swing.JRadioButton();
        jrbHourAngle = new javax.swing.JRadioButton();
        jrbPhotometricity = new javax.swing.JRadioButton();
        jrbSeeing = new javax.swing.JRadioButton();
        jrbAirmass = new javax.swing.JRadioButton();
        jbtnCreate = new javax.swing.JButton();
        jbtnCancel = new javax.swing.JButton();
        jplObservingConstraintContainerPanel = new javax.swing.JPanel();
        jScrollPane = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Observing Constraint Creation");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Select constraint type"));

        jrbSkyBrightness.setText("Sky Brightness");

        jrbHourAngle.setText("Hour Angle");

        jrbPhotometricity.setText("Photometricity");

        jrbSeeing.setText("Seeing");

        jrbAirmass.setText("Airmass Constraint");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jrbSkyBrightness)
                    .add(jrbAirmass)
                    .add(jrbHourAngle)
                    .add(jrbPhotometricity)
                    .add(jrbSeeing))
                .addContainerGap(371, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jrbSkyBrightness)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jrbAirmass)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jrbHourAngle)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jrbPhotometricity)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jrbSeeing)
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

        jplObservingConstraintContainerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Observing Constraint"));

        org.jdesktop.layout.GroupLayout jplObservingConstraintContainerPanelLayout = new org.jdesktop.layout.GroupLayout(jplObservingConstraintContainerPanel);
        jplObservingConstraintContainerPanel.setLayout(jplObservingConstraintContainerPanelLayout);
        jplObservingConstraintContainerPanelLayout.setHorizontalGroup(
            jplObservingConstraintContainerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
        );
        jplObservingConstraintContainerPanelLayout.setVerticalGroup(
            jplObservingConstraintContainerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jbtnCreate)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jbtnCancel)
                .addContainerGap())
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jplObservingConstraintContainerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jplObservingConstraintContainerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(3, 3, 3)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jbtnCreate)
                    .add(jbtnCancel)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jbtnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCreateActionPerformed
// TODO add your handling code here:
    Component[] components = viewPanel.getComponents();
    IObservingConstraintPanel observingConstraintPanel = (IObservingConstraintPanel)components[0];
    
    if (observingConstraintPanel.containsValidObservingConstraint()) {
        this.setVisible(false);
        this.dispose();
    } else {
        JOptionPane.showMessageDialog(this, "Please make sure the observing constraint fields are correctly populated");
    }
}//GEN-LAST:event_jbtnCreateActionPerformed

private void jbtnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelActionPerformed
    try {
        viewPanel.removeAll();
    } catch (Exception ex) {
        ex.printStackTrace();
        logger.error(ex);
    }
    this.setVisible(false);
    this.dispose();
}//GEN-LAST:event_jbtnCancelActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NewObservingConstraintDialog dialog = new NewObservingConstraintDialog(null, true);
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JButton jbtnCancel;
    private javax.swing.JButton jbtnCreate;
    private javax.swing.JPanel jplObservingConstraintContainerPanel;
    private javax.swing.JRadioButton jrbAirmass;
    private javax.swing.JRadioButton jrbHourAngle;
    private javax.swing.JRadioButton jrbPhotometricity;
    private javax.swing.JRadioButton jrbSeeing;
    private javax.swing.JRadioButton jrbSkyBrightness;
    // End of variables declaration//GEN-END:variables

}
