/*
 * MultiColourPhotomSequencePanel.java
 *
 * Created on June 11, 2009, 12:36 PM
 */
package ngat.oss.client.gui.panel.wizards;

import java.awt.BorderLayout;
import java.awt.Component;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import ngat.astrometry.BasicSite;
import ngat.astrometry.TargetTrackCalculator;
import ngat.astrometry.TargetTrackCalculatorFactory;
import ngat.astrometry.approximate.AlternativeTargetTrackCalculatorFactory;
import ngat.astrometry.approximate.BasicAstroLibImpl;

import ngat.oss.client.gui.panel.rotator.RotatorPositionSelectionListener;
import ngat.oss.client.gui.panel.rotator.RotatorSkyTimePanel;
import ngat.oss.client.gui.panel.rotator.RotatorMountTimePanel;
import ngat.oss.client.gui.reference.CONST;
import ngat.oss.client.gui.reference.Session;
import ngat.phase2.IExecutiveAction;
import ngat.phase2.IRotatorConfig;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.ITarget;
import ngat.phase2.XExecutiveComponent;
import ngat.phase2.XIteratorComponent;
import ngat.phase2.XRotatorConfig;
import ngat.phase2.XSlew;
import ngat.sms.models.standard.StandardChargeAccountingModel;
import org.apache.log4j.Logger;

/**
 *
 * @author  nrc
 */
public class WizardPanelUpdateSequenceRotator extends javax.swing.JPanel implements RotatorPositionSelectionListener {

    static Logger logger = Logger.getLogger(WizardPanelUpdateSequenceRotator.class);
    
    private ISequenceComponent originalSequenceComponent;
    private ITarget originalTarget;
    private double estimatedExecutionTime = 0;
    private static final String MOUNT = "MOUNT";
    private static final String SKY = "SKY";

    private ButtonGroup rotatorButtonGroup = new ButtonGroup();
    private ButtonGroup instrumentButtonGroup = new ButtonGroup();

    public static final int MOUNT_OUT_OF_BOUNDS = -1;
    public static final int SKY_OUT_OF_BOUNDS = -2;
    public static final int ANGLE_OK = 1;
    public static final int ANGLE_NOT_A_NUMBER = -3;
            
    public WizardPanelUpdateSequenceRotator(ISequenceComponent sequenceComponent) {
        if (sequenceComponent != null) {
            this.originalSequenceComponent = sequenceComponent;
            this.originalTarget = getTargetFromOriginalWizardSequence();
        }
        
        initComponents();
        populateComponents();
    }

    public ISequenceComponent getObservationSequence() {
        //create the rotator cfg first
        int mode;
        double rotatorAngle;

        String actionCmd = rotatorButtonGroup.getSelection().getActionCommand();
        if (actionCmd.equals(SKY)) {
            mode =IRotatorConfig.SKY;
        } else if (actionCmd.equals(MOUNT)) {
            mode =IRotatorConfig.MOUNT;
        } else {
            logger.error("unknown actionCommand " + actionCmd);
            return null;
        }

        //get rotator angle, convert it to radians
        rotatorAngle = Double.parseDouble(jtfAngleOfPointer.getText());
        rotatorAngle = Math.toRadians(rotatorAngle);

        //the instrument name
        String instrumentName = (String) jcbInstrumentName.getSelectedItem();

        //create new rotator component
        XRotatorConfig newRotatorConfig = new XRotatorConfig(mode, rotatorAngle, instrumentName);

        //copy the original sequence component
        XIteratorComponent newIterator = (XIteratorComponent) originalSequenceComponent;
        
        //copy original sequence list, add new slew complonent in place of one in it
        List originalSequenceList = originalSequenceComponent.listChildComponents();
        List newList = new Vector();
        Iterator originalSequenceIterator = originalSequenceList.iterator();
        while(originalSequenceIterator.hasNext()) {
            ISequenceComponent sequenceComponent = (ISequenceComponent) originalSequenceIterator.next();
            if (sequenceComponent instanceof XExecutiveComponent) {
                XExecutiveComponent executiveComponent = (XExecutiveComponent) sequenceComponent;
                IExecutiveAction executiveAction = executiveComponent.getExecutiveAction();
                if (executiveAction instanceof XSlew) {
                    //get the info about the current slew
                    XSlew originalSlew = (XSlew) executiveAction;
                    boolean usesNonSiderealTracking = originalSlew.usesNonSiderealTracking();
                    ITarget target = originalSlew.getTarget();
                    //create the new slew object
                    XSlew newSlew = new XSlew(target, newRotatorConfig, usesNonSiderealTracking);
                    //copy the executiveComponent wrapper
                    XExecutiveComponent newExecutiveComponent = executiveComponent;
                    //set the action in the wrapper to the new slew
                    newExecutiveComponent.setAction(newSlew);
                    //add the executive component to the list
                    newList.add(newExecutiveComponent);
                } else {
                    //add the sequenceComponent to the list
                    newList.add(sequenceComponent);
                }
            } else {
                //add the sequenceComponent to the list
                newList.add(sequenceComponent);
            }
        }

        newIterator.setSequence(newList);
        ISequenceComponent newSequence = (ISequenceComponent)newIterator;
        return newSequence;
    }

    public int validateRotatorValue() {
        try {
            double rotAngle = Double.parseDouble(jtfAngleOfPointer.getText());
            if (rotatorButtonGroup.getSelection().getActionCommand().equals(MOUNT)) {
                if ((rotAngle >90) || (rotAngle < -90)) {
                    return MOUNT_OUT_OF_BOUNDS;
                }
            } else {
                if ((rotAngle >360) || (rotAngle < 0)) {
                    return SKY_OUT_OF_BOUNDS;
                }
            }
            return ANGLE_OK;
        } catch (NumberFormatException e) {
            return ANGLE_NOT_A_NUMBER;
        }
    }

    private void populateComponents() {
        
        jcbInstrumentName.setModel(new javax.swing.DefaultComboBoxModel(CONST.ALL_INSTRUMENTS_FOR_ROTATOR));
        
        /*
        if (Session.getInstance().getUser().isSuperUser()) {
            jcbInstrumentName.setModel(new javax.swing.DefaultComboBoxModel(CONST.ALL_INSTRUMENTS_FOR_ROTATOR));
        } else {
            jcbInstrumentName.setModel(new javax.swing.DefaultComboBoxModel(CONST.ALL_INSTRUMENTS_FOR_ROTATOR_EXCEPT_IOI));
        }
        */
        
        rotatorButtonGroup.add(jrbMountAngle);
        rotatorButtonGroup.add(jrbSkyAngle);
        jrbMountAngle.setActionCommand(MOUNT);
        jrbSkyAngle.setActionCommand(SKY);

        StandardChargeAccountingModel standardChargeAccountingModel = new StandardChargeAccountingModel();

        try {
            //calc estimatedExecutionTime
            estimatedExecutionTime = standardChargeAccountingModel.calculateCost(originalSequenceComponent);
            jtfEstimatedExecutionTime.setText(String.valueOf(ngat.phase2.util.Rounder.round(estimatedExecutionTime / 1000, 2)));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            jtfEstimatedExecutionTime.setText("ERROR");
        }

        jlblLocked.setVisible(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        startDateTimeEditorPanel = new ngat.beans.guibeans.DateTimeEditorPanel();
        jLabel3 = new javax.swing.JLabel();
        jtfEstimatedExecutionTime = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jbtnCreateChart = new javax.swing.JButton();
        jplChartContainerPanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jtfAngleOfPointer = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jtfDateTimeOfPointer = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jlblLocked = new javax.swing.JLabel();
        jplInstrument = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jcbInstrumentName = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jrbSkyAngle = new javax.swing.JRadioButton();
        jrbMountAngle = new javax.swing.JRadioButton();

        startDateTimeEditorPanel.setTitle("Start date-time");

        jLabel3.setText("The execution time of the group is estimated to be");

        jtfEstimatedExecutionTime.setEditable(false);

        jLabel4.setText("(sec)");

        jLabel1.setText("Please select the date and time to start the graph from (the duration of the graph will be 24 hours):");

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel2.setText("1)");

        jLabel5.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel5.setText("2)");

        jbtnCreateChart.setText("Create Chart");
        jbtnCreateChart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCreateChartActionPerformed(evt);
            }
        });

        jplChartContainerPanel.setLayout(new java.awt.BorderLayout());

        jLabel9.setText("Angle:");

        jtfAngleOfPointer.setBackground(new java.awt.Color(153, 255, 255));
        jtfAngleOfPointer.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jtfAngleOfPointer.setText("0.0");

        jLabel10.setText("(degrees)");

        jLabel11.setText("Date-time (of mouse pointer):");

        jtfDateTimeOfPointer.setEditable(false);

        jLabel12.setText("(Click on the chart to set the values on the form, click again to unlock it and allow further editing)");

        jlblLocked.setText("LOCKED");

        jLabel13.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel13.setText("4)");

        jLabel14.setText("Please select the instrument to align to:");

        jcbInstrumentName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbInstrumentNameActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jplInstrumentLayout = new org.jdesktop.layout.GroupLayout(jplInstrument);
        jplInstrument.setLayout(jplInstrumentLayout);
        jplInstrumentLayout.setHorizontalGroup(
            jplInstrumentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jplInstrumentLayout.createSequentialGroup()
                .add(jLabel13)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jplInstrumentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jcbInstrumentName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 183, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel14))
                .add(22, 22, 22))
        );
        jplInstrumentLayout.setVerticalGroup(
            jplInstrumentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jplInstrumentLayout.createSequentialGroup()
                .add(jplInstrumentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel13)
                    .add(jLabel14))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jcbInstrumentName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jLabel6.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jLabel6.setText("3)");

        jLabel7.setText("Please select the type of angle you want to use:");

        jrbSkyAngle.setSelected(true);
        jrbSkyAngle.setText("Sky angle");
        jrbSkyAngle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbSkyAngleActionPerformed(evt);
            }
        });

        jrbMountAngle.setText("Mount angle");
        jrbMountAngle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbMountAngleActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jLabel6)
                .add(18, 18, 18)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(jrbSkyAngle)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jrbMountAngle))
                    .add(jLabel7)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(jLabel7))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jrbSkyAngle)
                    .add(jrbMountAngle)))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jLabel9)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jtfAngleOfPointer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel10)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jLabel11)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jtfDateTimeOfPointer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 237, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jLabel2)
                        .add(18, 18, 18)
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jtfEstimatedExecutionTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 101, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(35, 35, 35)
                        .add(jLabel4))
                    .add(layout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(jplInstrument, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(layout.createSequentialGroup()
                                .add(jLabel5)
                                .add(18, 18, 18)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(24, 24, 24)
                                        .add(startDateTimeEditorPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(jLabel1)))))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jbtnCreateChart))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(697, 697, 697)
                                .add(jlblLocked))
                            .add(jLabel12)
                            .add(jplChartContainerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 748, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(15, 15, 15)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jLabel3)
                    .add(jtfEstimatedExecutionTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(startDateTimeEditorPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 53, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(9, 9, 9)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(26, 26, 26)
                        .add(jbtnCreateChart))
                    .add(jplInstrument, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel12)
                    .add(jlblLocked))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jplChartContainerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 321, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel9)
                    .add(jtfAngleOfPointer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel10)
                    .add(jLabel11)
                    .add(jtfDateTimeOfPointer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(20, 20, 20))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnCreateChartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCreateChartActionPerformed

        //undo locks
        this.receiveLockingEvent(false);

        jplChartContainerPanel.removeAll();

        JPanel timePanel = getTimePanel(rotatorButtonGroup.getSelection().getActionCommand());
        if (timePanel == null) {
            return;
        }
        jplChartContainerPanel.add((Component) timePanel, BorderLayout.CENTER);
        jplChartContainerPanel.repaint();
    }//GEN-LAST:event_jbtnCreateChartActionPerformed

    private void jrbMountAngleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbMountAngleActionPerformed
        String actionCommand = rotatorButtonGroup.getSelection().getActionCommand();
        jplInstrument.setVisible(actionCommand.equalsIgnoreCase(SKY));
    }//GEN-LAST:event_jrbMountAngleActionPerformed

    private void jrbSkyAngleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbSkyAngleActionPerformed
        String actionCommand = rotatorButtonGroup.getSelection().getActionCommand();
        jplInstrument.setVisible(actionCommand.equalsIgnoreCase(SKY));
    }//GEN-LAST:event_jrbSkyAngleActionPerformed

    private void jcbInstrumentNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbInstrumentNameActionPerformed

}//GEN-LAST:event_jcbInstrumentNameActionPerformed

    private JPanel getTimePanel(String actionCommand) {
        TargetTrackCalculatorFactory targetTrackCalculatorFactory = new AlternativeTargetTrackCalculatorFactory();
        BasicSite site = new BasicSite("obs", Math.toRadians(28.7624), Math.toRadians(-17.8792));

        TargetTrackCalculator targetTrackCalculator = targetTrackCalculatorFactory.getTrackCalculator(originalTarget, site);

        String instrumentName = (String) jcbInstrumentName.getSelectedItem();
        double instrumentOffsetAngle; 
        try {
            instrumentOffsetAngle = CONST.getOffsetAngleOfInstrument(instrumentName);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return null;
        }
        
        if (actionCommand.equals(SKY)) {
            RotatorSkyTimePanel rotatorSkyTimePanel = new RotatorSkyTimePanel(site, new BasicAstroLibImpl(), targetTrackCalculatorFactory, instrumentOffsetAngle);
            try {
                long start = startDateTimeEditorPanel.getTime();
                long end = start + 86400 * 1000L; //start + 24 hours

                rotatorSkyTimePanel.addRotatorPositionSelectionListener(this);
                
                rotatorSkyTimePanel.setSize(jplChartContainerPanel.getSize());
                rotatorSkyTimePanel.setPreferredSize(jplChartContainerPanel.getSize());
                rotatorSkyTimePanel.update(targetTrackCalculator, start, end, (long) estimatedExecutionTime);
                return rotatorSkyTimePanel;
            } catch (ParseException ex) {
                ex.printStackTrace();
                logger.error(ex);
                return null;
            }
        } else if (actionCommand.equals(MOUNT)) {
            RotatorMountTimePanel rotatorMountTimePanel = new RotatorMountTimePanel(site, new BasicAstroLibImpl(), targetTrackCalculatorFactory, instrumentOffsetAngle);

            try {
                long start = startDateTimeEditorPanel.getTime();
                long end = start + 86400 * 1000L; //start + 24 hours

                rotatorMountTimePanel.addRotatorPositionSelectionListener(this);

                rotatorMountTimePanel.setSize(jplChartContainerPanel.getSize());
                rotatorMountTimePanel.setPreferredSize(jplChartContainerPanel.getSize());
                rotatorMountTimePanel.update(targetTrackCalculator, start, end, (long) estimatedExecutionTime);
                return rotatorMountTimePanel;
            } catch (ParseException ex) {
                ex.printStackTrace();
                logger.error(ex);
                return null;
            }
        } else {
            return null;
        }
    }

    private void debugShowPanel(JPanel panel) {
        //debug show panel in JFrame
        JFrame f = new JFrame("Mount angle display");
        JComponent comp = (JComponent) f.getContentPane();
        comp.setLayout(new BorderLayout());

        f.getContentPane().add(panel);
        f.pack();
        f.setBounds(200, 200, 880, 440);
        f.setVisible(true);
    }

    //RotatorPositionSelectionListener method
    public void rotatorSelection(long time, double rotator) {
        jtfAngleOfPointer.setText(String.valueOf(rotator));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm, z");
        dateFormat.setDateFormatSymbols(new DateFormatSymbols(Locale.UK)); //make sure months are spelt in English
        SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");
        dateFormat.setTimeZone(UTC);
        String dateFormatted = dateFormat.format(new Date(time)).toString();
        jtfDateTimeOfPointer.setText(dateFormatted);
    }

    //RotatorPositionSelectionListener method
    public void receiveLockingEvent(boolean locked) {
        jlblLocked.setVisible(locked);
    }

    private ITarget getTargetFromOriginalWizardSequence() {
        List childComponents = originalSequenceComponent.listChildComponents();
        Iterator i = childComponents.iterator();
        while (i.hasNext()) {
            ISequenceComponent sequenceComponent = (ISequenceComponent) i.next();
            if (sequenceComponent instanceof XExecutiveComponent) {
                IExecutiveAction action = ((XExecutiveComponent) sequenceComponent).getExecutiveAction();
                if (action instanceof XSlew) {
                    XSlew slew = (XSlew) action;
                    return slew.getTarget();
                }
            }
        }
        
        logger.error("unable to find target in observation sequence: " + originalSequenceComponent);
        return null;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jbtnCreateChart;
    private javax.swing.JComboBox jcbInstrumentName;
    private javax.swing.JLabel jlblLocked;
    private javax.swing.JPanel jplChartContainerPanel;
    private javax.swing.JPanel jplInstrument;
    private javax.swing.JRadioButton jrbMountAngle;
    private javax.swing.JRadioButton jrbSkyAngle;
    private javax.swing.JTextField jtfAngleOfPointer;
    private javax.swing.JTextField jtfDateTimeOfPointer;
    private javax.swing.JTextField jtfEstimatedExecutionTime;
    private ngat.beans.guibeans.DateTimeEditorPanel startDateTimeEditorPanel;
    // End of variables declaration//GEN-END:variables


}
