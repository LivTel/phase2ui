/*
 * MultiColourPhotomSequencePanel.java
 *
 * Created on June 11, 2009, 12:36 PM
 */
package ngat.oss.client.gui.panel.wizards;

import java.util.Iterator;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import ngat.oss.client.Phase2ModelClient;
import ngat.oss.client.gui.dialog.NewArcDialog;
import ngat.oss.client.gui.dialog.NewExposureDialog;
import ngat.oss.client.gui.dialog.NewInstrumentConfigDialog;
import ngat.oss.client.gui.dialog.PickSPRATConfigDialog;
import ngat.oss.client.gui.dialog.NewTargetDialog;
import ngat.oss.client.gui.model.SPRATSequenceTableModel;
import ngat.oss.client.gui.reference.CONST;
import ngat.oss.client.gui.reference.Session;
import ngat.oss.client.gui.render.TargetRenderer;
import ngat.oss.client.gui.wrapper.ExposureWrapper;
import ngat.oss.client.gui.wrapper.WizardPanelTableLineEntry;
import ngat.oss.exception.Phase2Exception;
import ngat.phase2.IAcquisitionConfig;
import ngat.phase2.IAutoguiderConfig;
import ngat.phase2.IGroup;
import ngat.phase2.IInstrumentConfig;
import ngat.phase2.IProgram;
import ngat.phase2.IRotatorConfig;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.ITarget;
import ngat.phase2.XAcquisitionConfig;
import ngat.phase2.XArc;
import ngat.phase2.XAutoguiderConfig;
import ngat.phase2.XDetectorConfig;
import ngat.phase2.XExecutiveComponent;
import ngat.phase2.XImagingSpectrographInstrumentConfig;
import ngat.phase2.XInstrumentConfigSelector;
import ngat.phase2.XIteratorComponent;
import ngat.phase2.XIteratorRepeatCountCondition;
import ngat.phase2.XMultipleExposure;
import ngat.phase2.XRotatorConfig;
import ngat.phase2.XSlew;
import ngat.phase2.XTarget;
import org.apache.log4j.Logger;

/**
 *
 * @author  nrc
 */
public class WizardPanelImagingSpectrographSequence extends javax.swing.JPanel {

    static Logger logger = Logger.getLogger(WizardPanelImagingSpectrographSequence.class);
    
    private final String ALLOW_ALT_TEXT = "Allow alternative acquisition instrument";
    private final String DO_NOT_ALLOW_ALT_TEXT = "DO NOT allow alternative acquisition instrument";
    
    private IGroup group;
    private IProgram program;
    private List targetList;
    
    private ButtonGroup bgAutoguide;
    //private ButtonGroup bgAcquire;
    
    private static final String CMD_NO = "AUTOGUIDE_NO";
    private static final String CMD_YES = "AUTOGUIDE_YES";
    private static final String CMD_AUTO = "AUTOGUIDE_AUTO";

    private static final String AUTOMATIC = "Automatic (Vertical)";
    private static final String ROT_MOUNT_11 = "ROT. Mount 11";
    
    public static final int UNSET_ROTATOR_TYPE = -1;

    /** Creates new form MultiColourPhotomSequencePanel */
    public WizardPanelImagingSpectrographSequence(IGroup group, IProgram program) {
        this.group = group;
        this.program = program;
        initComponents();
        populateComponents();
        setUpComponents();
    }

    private void populateComponents() {

        //populate target and instrumentConfig lists
        try {
            populateTargetList();

        } catch (Phase2Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return;
        }

    }

    private void setUpComponents() {

        //is standard tick box only visible to super users
        jcbIsStandard.setVisible(Session.getInstance().getUser().isSuperUser());
    }
    
    private void populateTargetList() throws Phase2Exception {
        jcbTarget.removeAllItems();
        targetList = Phase2ModelClient.getInstance().listTargets(program.getID());
        Iterator ti = targetList.iterator();
        while (ti.hasNext()) {
            ITarget target = (ITarget) ti.next();
            jcbTarget.addItem(TargetRenderer.getShortDescription(target));
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

    public ISequenceComponent getObservationSequence() {
        if (!validObservation()) {
            return null;
        }

        //ROOT ITERATOR
        XIteratorComponent rootComponent = new XIteratorComponent("Root", new XIteratorRepeatCountCondition(1));

        // 1. SLEW

        //get target
        XTarget target = (XTarget) getTarget();
        if (target == null) {
            return null;
        }
        
        // get rotatorConfig
        IRotatorConfig rotatorConfig = getRotatorConfig();
        if (rotatorConfig == null) {
            return null;
        }

        //set up tracking type
        //default this to false atm.
        boolean usesNonSiderealTracking = false;

        XSlew slew = new XSlew(target, rotatorConfig, usesNonSiderealTracking);
        rootComponent.addElement(new XExecutiveComponent(target.getName(), slew));

        
        // 2. FOCAL PLANE
        
        IAcquisitionConfig instrumentChange = new XAcquisitionConfig(IAcquisitionConfig.INSTRUMENT_CHANGE, CONST.SPRAT, null, false, IAcquisitionConfig.PRECISION_NORMAL);
        rootComponent.addElement(new XExecutiveComponent("INSTRUMENT_CHANGE", instrumentChange));
        
        
        // 3. FINE TUNE - NORMAL PRECISION
        
        //only allow SPRAT as acquisition instrument
        boolean allowAlternative = false;
        
        IAcquisitionConfig fineTuneNormalPrecision;
        int mode;
        if (jcbAcquisition.getSelectedItem().equals("WCS")) {
            mode = IAcquisitionConfig.WCS_FIT;
        } else if (jcbAcquisition.getSelectedItem().equals("BRIGHTEST")) {
            mode = IAcquisitionConfig.BRIGHTEST;
        } else {
            mode = IAcquisitionConfig.WCS_FIT;
        }
        
        fineTuneNormalPrecision = new XAcquisitionConfig(mode, CONST.SPRAT, CONST.SPRAT, allowAlternative, IAcquisitionConfig.PRECISION_NORMAL);
        rootComponent.addElement(new XExecutiveComponent("FINE_TUNE_NORMAL", fineTuneNormalPrecision));

        
        // 4. AG ON
        
        XAutoguiderConfig autoguiderConfig = new XAutoguiderConfig(IAutoguiderConfig.ON, "autoguider1");
        rootComponent.addElement(new XExecutiveComponent(CMD_YES, autoguiderConfig));
        
        
        // 5. FINE TUNE - HIGH PRECISION
        
        IAcquisitionConfig fineTuneHighPrecision;
        fineTuneHighPrecision = new XAcquisitionConfig(mode, CONST.SPRAT, CONST.SPRAT, allowAlternative, IAcquisitionConfig.PRECISION_HIGH);
        rootComponent.addElement(new XExecutiveComponent("FINE_TUNE_HIGH", fineTuneHighPrecision));
        
        
        // 6. CONFIG FOR SLIT IMAGING (put slit IN beam, grism OUT)
        String configId = "slit_imaging_config_" + group.getID();
        XImagingSpectrographInstrumentConfig slitImagingConfig = new XImagingSpectrographInstrumentConfig(configId);
        slitImagingConfig.setSlitPosition(XImagingSpectrographInstrumentConfig.SLIT_DEPLOYED);
        slitImagingConfig.setGrismPosition(XImagingSpectrographInstrumentConfig.GRISM_OUT);
        slitImagingConfig.setInstrumentName(CONST.SPRAT);
        
            XDetectorConfig detectorConfig = new  XDetectorConfig();
            detectorConfig.setXBin(1);
            detectorConfig.setYBin(1);
            detectorConfig.setWindows(null);
        slitImagingConfig.setDetectorConfig(detectorConfig);
        
        //new instrument config needs adding to database, it then needs grabbing and putting back into the sequence inside an XInstrumentConfigSelector
        //otherwise the instrument config selector component doesn't grab hold of it later
        Phase2ModelClient phase2ModelClient = Phase2ModelClient.getInstance();
        IInstrumentConfig instrumentConfigInDB = null;
        try {
            long cfgId = phase2ModelClient.addInstrumentConfig(program.getID(), slitImagingConfig);
            instrumentConfigInDB =  phase2ModelClient.getInstrumentConfig(cfgId);    
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        XInstrumentConfigSelector instrumentConfigSelector = new XInstrumentConfigSelector(instrumentConfigInDB);
        rootComponent.addElement(new XExecutiveComponent("CONFIG", instrumentConfigSelector));
        
        // 7. EXPOSE 1 (10 seconds if WCS fit, 1 second if brightest)
        double exposureTime = 0;
        String acqName = (String)jcbAcquisition.getSelectedItem();
        if (acqName.equalsIgnoreCase("WCS")) {
            exposureTime = 10000;
        } else {
            exposureTime = 1000;
        }
        int repeatCount = 1;
        
        XMultipleExposure multipleExposure = new XMultipleExposure(exposureTime, repeatCount);
        multipleExposure.setStandard(jcbIsStandard.isSelected()); //is standard from gui control.
        rootComponent.addElement(new XExecutiveComponent("EXPOSURE", multipleExposure));
        
        
        // 8. Config / expose / arc elements added by user (taken from JTable on form)
        
        SPRATSequenceTableModel sequenceTableModel = (SPRATSequenceTableModel) userSeqTable.getModel();
        List userEntries = sequenceTableModel.getData();
        Iterator ri = userEntries.iterator();
        XExecutiveComponent executiveComponent;
        while (ri.hasNext()) {
            WizardPanelTableLineEntry seqTableLineEntry = (WizardPanelTableLineEntry) ri.next();
            try {
                executiveComponent = getExecutiveComponent(seqTableLineEntry);
                rootComponent.addElement(executiveComponent);
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error(ex);
            }
        }

        //9. AUTOGUIDE off - it is on by default
        
        rootComponent.addElement(new XExecutiveComponent("AG_OFF", new XAutoguiderConfig(IAutoguiderConfig.OFF, "AG_OFF")));
        
        return rootComponent;
    }

    //extract executive component from table line entry
    private XExecutiveComponent getExecutiveComponent(WizardPanelTableLineEntry armSeqTableLineEntry) throws Exception {
        XExecutiveComponent executiveComponent;
        int lineEntryType = armSeqTableLineEntry.getLineEntryType();
        switch (lineEntryType) {
            case WizardPanelTableLineEntry.LINE_TYPE_SPRAT_CONFIG:
                XImagingSpectrographInstrumentConfig imagingSpectrographInstrumentConfig = armSeqTableLineEntry.getImagingSpectrographInstrumentConfig();
                XInstrumentConfigSelector instrumentConfigSelector = new XInstrumentConfigSelector(imagingSpectrographInstrumentConfig);
                executiveComponent = new XExecutiveComponent("CONFIG", instrumentConfigSelector);
                break;
            case WizardPanelTableLineEntry.LINE_TYPE_EXPOSE:
                int repeatCount = armSeqTableLineEntry.getExposureCount();
                double exposureTime = armSeqTableLineEntry.getExposureTime() * 1000; //mS
                XMultipleExposure multipleExposure = new XMultipleExposure(exposureTime, repeatCount);
                multipleExposure.setStandard(jcbIsStandard.isSelected()); //is standard from gui control.
                executiveComponent = new XExecutiveComponent("EXPOSURE", multipleExposure);
                break;
            case WizardPanelTableLineEntry.LINE_TYPE_ARC:
                XArc arc = armSeqTableLineEntry.getArc();
                executiveComponent = new XExecutiveComponent("ARC", arc);
                break;
            default:
                throw new Exception("Unknown ArmSeqTableLineEntry type: " + lineEntryType);
        }
        return executiveComponent;
    }

    
    private boolean validObservation() {
        return true;
    }
   
    private ITarget getTarget() {
        int targetIndex = jcbTarget.getSelectedIndex();
        if (targetIndex == -1) {
            return null;
        }
        
        ITarget target = (ITarget) targetList.get(targetIndex);
        return target;
    }
    
    private IRotatorConfig getRotatorConfig() {
    
        XRotatorConfig rotatorConfig = new XRotatorConfig();
        rotatorConfig.setRotatorMode(IRotatorConfig.MOUNT);
        rotatorConfig.setInstrumentName(CONST.SPRAT);
        rotatorConfig.setRotatorAngle(Math.toRadians(11));
        
        return rotatorConfig;
    }
    
    private String getAutoguiderModeAsString() {
        String actionCmd = bgAutoguide.getSelection().getActionCommand();
        return actionCmd;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpObsTablePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        userSeqTable = new javax.swing.JTable();
        jbtnDeleteRed = new javax.swing.JButton();
        jbtnUpRed = new javax.swing.JButton();
        jbtnDownRed = new javax.swing.JButton();
        jbtnConfig = new javax.swing.JButton();
        jbtnExpose = new javax.swing.JButton();
        jbtnArc = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jcbTarget = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jbtnCreateNewTarget = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jcbAcquisition = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jcbIsStandard = new javax.swing.JCheckBox();
        jpObsSeqStartPanel1 = new javax.swing.JPanel();
        jbtnCreateNewInstConfig = new javax.swing.JButton();

        jpObsTablePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(222, 222, 222)));

        userSeqTable.setModel(new SPRATSequenceTableModel());
        userSeqTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(userSeqTable);

        jbtnDeleteRed.setText("Delete");
        jbtnDeleteRed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDeleteRedActionPerformed(evt);
            }
        });

        jbtnUpRed.setText("Up");
        jbtnUpRed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnUpRedActionPerformed(evt);
            }
        });

        jbtnDownRed.setText("Down");
        jbtnDownRed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDownRedActionPerformed(evt);
            }
        });

        jbtnConfig.setText("Config");
        jbtnConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnConfigActionPerformed(evt);
            }
        });

        jbtnExpose.setText("Expose");
        jbtnExpose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnExposeActionPerformed(evt);
            }
        });

        jbtnArc.setText("Arc");
        jbtnArc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnArcActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jpObsTablePanelLayout = new org.jdesktop.layout.GroupLayout(jpObsTablePanel);
        jpObsTablePanel.setLayout(jpObsTablePanelLayout);
        jpObsTablePanelLayout.setHorizontalGroup(
            jpObsTablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpObsTablePanelLayout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 739, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(jpObsTablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jbtnConfig)
                    .add(jbtnDownRed)
                    .add(jbtnDeleteRed)
                    .add(jbtnUpRed)
                    .add(jbtnExpose)
                    .add(jbtnArc))
                .addContainerGap())
        );

        jpObsTablePanelLayout.linkSize(new java.awt.Component[] {jbtnDeleteRed, jbtnDownRed, jbtnUpRed}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jpObsTablePanelLayout.linkSize(new java.awt.Component[] {jbtnArc, jbtnConfig, jbtnExpose}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jpObsTablePanelLayout.setVerticalGroup(
            jpObsTablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpObsTablePanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jpObsTablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jpObsTablePanelLayout.createSequentialGroup()
                        .add(jbtnConfig)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jbtnExpose)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jbtnArc)
                        .add(78, 78, 78)
                        .add(jbtnUpRed)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jbtnDownRed)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jbtnDeleteRed))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 302, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        jLabel2.setText("Select Target:");

        jLabel3.setText("or");

        jbtnCreateNewTarget.setText("Create New Target");
        jbtnCreateNewTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCreateNewTargetActionPerformed(evt);
            }
        });

        jLabel7.setText("Acquisition:");

        jcbAcquisition.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "WCS", "BRIGHTEST" }));
        jcbAcquisition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbAcquisitionActionPerformed(evt);
            }
        });

        jLabel1.setText("Initial Configuration");

        jLabel8.setText("Config / Expose / Arc elements");

        jLabel5.setText("Instrument = SPRAT");

        jcbIsStandard.setText("Is Standard");

        jpObsSeqStartPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Create Instrument Config"));

        jbtnCreateNewInstConfig.setText("Create");
        jbtnCreateNewInstConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCreateNewInstConfigActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jpObsSeqStartPanel1Layout = new org.jdesktop.layout.GroupLayout(jpObsSeqStartPanel1);
        jpObsSeqStartPanel1.setLayout(jpObsSeqStartPanel1Layout);
        jpObsSeqStartPanel1Layout.setHorizontalGroup(
            jpObsSeqStartPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpObsSeqStartPanel1Layout.createSequentialGroup()
                .add(54, 54, 54)
                .add(jbtnCreateNewInstConfig)
                .addContainerGap(70, Short.MAX_VALUE))
        );
        jpObsSeqStartPanel1Layout.setVerticalGroup(
            jpObsSeqStartPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jpObsSeqStartPanel1Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jbtnCreateNewInstConfig)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(12, 12, 12)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(15, 15, 15)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jcbTarget, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 463, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(jLabel3)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jbtnCreateNewTarget))
                                    .add(layout.createSequentialGroup()
                                        .add(jLabel7)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(jLabel5)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(jcbAcquisition, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 159, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                        .add(jcbIsStandard)
                                        .add(98, 98, 98))
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                        .add(jpObsSeqStartPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(32, 32, 32))))))
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .add(41, 41, 41))))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jpObsTablePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel8)
                .add(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel2)
                            .add(jcbTarget, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel3)
                            .add(jbtnCreateNewTarget))
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel7)
                            .add(jcbAcquisition, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel5))
                        .add(42, 42, 42)
                        .add(jLabel8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jpObsTablePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(42, 42, 42)
                        .add(jpObsSeqStartPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jcbIsStandard)))
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

private void jbtnConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnConfigActionPerformed
            
    PickSPRATConfigDialog dialog = new PickSPRATConfigDialog(true, program);
    
    dialog.setVisible(true);
    //blocks
    XImagingSpectrographInstrumentConfig imagingSpectrographInstrumentConfig = dialog.getConfig();
    
    dialog.setVisible(false);
    dialog.dispose();

    if (imagingSpectrographInstrumentConfig == null) {
        return;
    }
    WizardPanelTableLineEntry seqTableLineEntry = new WizardPanelTableLineEntry(imagingSpectrographInstrumentConfig);

    SPRATSequenceTableModel spratSequenceTableModel = (SPRATSequenceTableModel) userSeqTable.getModel();
    spratSequenceTableModel.addTableLineEntry(seqTableLineEntry);
    
    
    //refresh the UI
    userSeqTable.updateUI();
}//GEN-LAST:event_jbtnConfigActionPerformed

private void jbtnExposeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExposeActionPerformed
    
    NewExposureDialog dialog = new NewExposureDialog(true);
    dialog.setVisible(true);
    //blocks
    ExposureWrapper frodoExposureWrapper = dialog.getExposure();

    dialog.setVisible(false);
    dialog.dispose();
    if (frodoExposureWrapper == null) {
        return;
    }
    WizardPanelTableLineEntry armSeqTableLineEntry = new WizardPanelTableLineEntry(frodoExposureWrapper.getExposureTime(), frodoExposureWrapper.getExposureCount());

    SPRATSequenceTableModel spratSequenceTableModel = (SPRATSequenceTableModel) userSeqTable.getModel();
    spratSequenceTableModel.addTableLineEntry(armSeqTableLineEntry);
    userSeqTable.updateUI();
}//GEN-LAST:event_jbtnExposeActionPerformed

private void jbtnArcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnArcActionPerformed
    
    NewArcDialog dialog = new NewArcDialog(true, true);
    dialog.setVisible(true);
    //blocks
    XArc arc = dialog.getArc();

    dialog.setVisible(false);
    dialog.dispose();

    if (arc == null) {
        return;
    }
    WizardPanelTableLineEntry spratSequenceTableModel = new WizardPanelTableLineEntry(arc);

    SPRATSequenceTableModel wizardSequenceTableModel = (SPRATSequenceTableModel) userSeqTable.getModel();
    wizardSequenceTableModel.addTableLineEntry(spratSequenceTableModel);
    userSeqTable.updateUI();
}//GEN-LAST:event_jbtnArcActionPerformed

private void jbtnUpRedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnUpRedActionPerformed
    
    int selectedRow = userSeqTable.getSelectedRow();
    if (selectedRow < 0) {
        return;
    }
   
    SPRATSequenceTableModel spratSequenceTableModel = (SPRATSequenceTableModel) userSeqTable.getModel();
    int newRow = spratSequenceTableModel.moveRowUp(selectedRow);
    userSeqTable.getSelectionModel().setSelectionInterval(newRow, newRow);
    userSeqTable.updateUI();
}//GEN-LAST:event_jbtnUpRedActionPerformed

private void jbtnDownRedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDownRedActionPerformed
    
    int selectedRow = userSeqTable.getSelectedRow();
    if (selectedRow < 0) {
        return;
    }

    SPRATSequenceTableModel spratSequenceTableModel = (SPRATSequenceTableModel) userSeqTable.getModel();
    int newRow = spratSequenceTableModel.moveRowDown(selectedRow);
    userSeqTable.getSelectionModel().setSelectionInterval(newRow, newRow);
    userSeqTable.updateUI();
}//GEN-LAST:event_jbtnDownRedActionPerformed

private void jbtnDeleteRedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDeleteRedActionPerformed
 
    int selectedRow = userSeqTable.getSelectedRow();
    if (selectedRow < 0) {
        return;
    }
    
    SPRATSequenceTableModel spratSequenceTableModel = (SPRATSequenceTableModel) userSeqTable.getModel();
    spratSequenceTableModel.deleteRow(selectedRow);
    userSeqTable.updateUI();
}//GEN-LAST:event_jbtnDeleteRedActionPerformed

private void jbtnCreateNewInstConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCreateNewInstConfigActionPerformed
    NewInstrumentConfigDialog dialog = new NewInstrumentConfigDialog(true, CONST.SPRAT);
    dialog.setVisible(true);

    IInstrumentConfig instrumentConfig = dialog.getInstrumentConfig();
    dialog.setVisible(false);
    dialog.dispose();
    if (instrumentConfig == null) {
        return;
    }
    if (dialog.wasKilled()) {
        return;
    }
    try {
        Phase2ModelClient.getInstance().addInstrumentConfig(program.getID(), instrumentConfig);
    } catch (Phase2Exception ex) {
        ex.printStackTrace();
        logger.error(ex);
        JOptionPane.showMessageDialog(this, ex.getMessage());
    }
}//GEN-LAST:event_jbtnCreateNewInstConfigActionPerformed

    //if the user changes the acquisition type, change the slit imaging exposure length in the JTable.
    private void jcbAcquisitionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbAcquisitionActionPerformed

    }//GEN-LAST:event_jcbAcquisitionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtnArc;
    private javax.swing.JButton jbtnConfig;
    private javax.swing.JButton jbtnCreateNewInstConfig;
    private javax.swing.JButton jbtnCreateNewTarget;
    private javax.swing.JButton jbtnDeleteRed;
    private javax.swing.JButton jbtnDownRed;
    private javax.swing.JButton jbtnExpose;
    private javax.swing.JButton jbtnUpRed;
    private javax.swing.JComboBox jcbAcquisition;
    private javax.swing.JCheckBox jcbIsStandard;
    private javax.swing.JComboBox jcbTarget;
    private javax.swing.JPanel jpObsSeqStartPanel1;
    private javax.swing.JPanel jpObsTablePanel;
    private javax.swing.JTable userSeqTable;
    // End of variables declaration//GEN-END:variables
}
