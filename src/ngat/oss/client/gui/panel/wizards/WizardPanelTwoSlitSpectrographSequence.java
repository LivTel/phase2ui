/*
 * MultiColourPhotomSequencePanel.java
 *
 * Created on June 11, 2009, 12:36 PM
 */
package ngat.oss.client.gui.panel.wizards;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import ngat.oss.client.Phase2ModelClient;
import ngat.oss.client.gui.dialog.NewExposureDialog;
import ngat.oss.client.gui.dialog.NewInstrumentConfigDialog;
import ngat.oss.client.gui.dialog.NewTargetDialog;
import ngat.oss.client.gui.model.LOTUSSequenceTableModel;
import ngat.oss.client.gui.reference.CONST;
import ngat.oss.client.gui.reference.Session;
import ngat.oss.client.gui.render.InstrumentConfigRenderer;
import ngat.oss.client.gui.render.TargetRenderer;
import ngat.oss.client.gui.wrapper.ExposureWrapper;
import ngat.oss.client.gui.wrapper.WizardPanelTableLineEntry;
import ngat.oss.exception.Phase2Exception;
import ngat.phase2.IAcquisitionConfig;
import ngat.phase2.IAutoguiderConfig;
import ngat.phase2.IExecutiveAction;
import ngat.phase2.IGroup;
import ngat.phase2.IInstrumentConfig;
import ngat.phase2.IProgram;
import ngat.phase2.IRotatorConfig;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.ITarget;
import ngat.phase2.XAcquisitionConfig;
import ngat.phase2.XAutoguiderConfig;
import ngat.phase2.XBlueTwoSlitSpectrographInstrumentConfig;
import ngat.phase2.XExecutiveComponent;
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
 * @author nrc
 */
public class WizardPanelTwoSlitSpectrographSequence extends javax.swing.JPanel {

    static Logger logger = Logger.getLogger(WizardPanelTwoSlitSpectrographSequence.class);

    private final String ALLOW_ALT_TEXT = "Allow alternative acquisition instrument";
    private final String DO_NOT_ALLOW_ALT_TEXT = "DO NOT allow alternative acquisition instrument";

    private static final String CMD_NO = "AUTOGUIDE_NO";
    private static final String CMD_YES = "AUTOGUIDE_YES";
    private static final String CMD_AUTO = "AUTOGUIDE_AUTO";

    private IGroup group;
    private IProgram program;
    private List targetList;
    private List lotusInstConfigList;

    private ButtonGroup bgAutoguide;
    //private ButtonGroup bgAcquire;

    private static final String AUTOMATIC = "Automatic (Vertical)";
    private static final String ROT_MOUNT_11 = "ROT. Mount 11";

    public static final int UNSET_ROTATOR_TYPE = -1;

    /**
     * Creates new form MultiColourPhotomSequencePanel
     */
    public WizardPanelTwoSlitSpectrographSequence(IGroup group, IProgram program) {
        this.group = group;
        this.program = program;
        initComponents();
        populateComponents();
        setUpComponents();
    }

    private void populateComponents() {

        bgAutoguide = new ButtonGroup();
        bgAutoguide.add(jrbAutoguideNo);
        bgAutoguide.add(jrbAutoguideYes);
        bgAutoguide.add(jrbAutoguideAuto);

        jrbAutoguideNo.setActionCommand(CMD_NO);
        jrbAutoguideYes.setActionCommand(CMD_YES);
        jrbAutoguideAuto.setActionCommand(CMD_AUTO);

        //populate target and instrumentConfig lists
        try {
            populateTargetList();
            populateConfigList();
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

    private void populateConfigList() throws Phase2Exception {

        lotusInstConfigList = new ArrayList();
        jcbInstrumentConfigs.removeAllItems();
        List instConfigList = Phase2ModelClient.getInstance().listInstrumentConfigs(program.getID());
        Iterator ci = instConfigList.iterator();
        while (ci.hasNext()) {
            IInstrumentConfig config = (IInstrumentConfig) ci.next();
            if (config != null) {
                if (config.getInstrumentName() != null) {
                    if (config.getInstrumentName().equals(CONST.LOTUS)) {
                        jcbInstrumentConfigs.addItem(InstrumentConfigRenderer.getShortDescription(config));
                        lotusInstConfigList.add(config);
                    }
                }
            }
        }
    }

    private void selectTargetInJCB(ITarget target) {

        Iterator i = targetList.iterator();
        int index = 0;
        while (i.hasNext()) {
            ITarget targetFound = (ITarget) i.next();
            if (targetFound.getName().equals(target.getName())) {
                jcbTarget.setSelectedIndex(index);
                return;
            }
            index++;
        }

    }

    private void selectInstrumentConfigInJCB(IInstrumentConfig config) {

        Iterator i = lotusInstConfigList.iterator();
        int index = 0;
        while (i.hasNext()) {
            IInstrumentConfig configFound = (IInstrumentConfig) i.next();
            if (configFound.getName().equals(config.getName())) {
                jcbInstrumentConfigs.setSelectedIndex(index);
                return;
            }
            index++;
        }
    }

    
    //get the instrument config selected in the JCB.
    private IInstrumentConfig getInstrumentConfigInJCB() {

        //find the index of the selected instrument config in the JCB, get the object of that index in the instConfigList
        return (IInstrumentConfig) lotusInstConfigList.get(jcbInstrumentConfigs.getSelectedIndex());
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
        IAcquisitionConfig instrumentChange = new XAcquisitionConfig(IAcquisitionConfig.INSTRUMENT_CHANGE, CONST.IO_O, null, false, IAcquisitionConfig.PRECISION_NORMAL);
        rootComponent.addElement(new XExecutiveComponent("INSTRUMENT_CHANGE", instrumentChange));

        // 3. FINE TUNE - NORMAL PRECISION
        //boolean allowAlternative = jcbAllowAlternative.getSelectedItem().equals(ALLOW_ALT_TEXT);
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

        fineTuneNormalPrecision = new XAcquisitionConfig(mode, CONST.LOTUS, CONST.IO_O, allowAlternative, IAcquisitionConfig.PRECISION_NORMAL);
        rootComponent.addElement(new XExecutiveComponent("FINE_TUNE_NORMAL", fineTuneNormalPrecision));

        // 4. INSTRUMENT CONFIG
        IInstrumentConfig instrumentConfig = getInstrumentConfigInJCB();
        XInstrumentConfigSelector instrumentConfigSelector = new XInstrumentConfigSelector(instrumentConfig);
        rootComponent.addElement(new XExecutiveComponent("CONFIG", instrumentConfigSelector));

        // 5. AUTOGUIDE
        boolean autoguiderIsOn = false;
        IAutoguiderConfig autoguiderConfigSpecified = getAutoguiderConfig();
        if (autoguiderConfigSpecified.getAutoguiderCommand() != IAutoguiderConfig.OFF) {
            rootComponent.addElement(new XExecutiveComponent(getAutoguiderModeAsString(), autoguiderConfigSpecified));
            autoguiderIsOn = true;
        }

        // 5. FINE TUNE - HIGH PRECISION
        /*
         IAcquisitionConfig fineTuneHighPrecision;
         fineTuneHighPrecision = new XAcquisitionConfig(mode, CONST.SPRAT, CONST.SPRAT, allowAlternative, IAcquisitionConfig.PRECISION_HIGH);
         rootComponent.addElement(new XExecutiveComponent("FINE_TUNE_HIGH", fineTuneHighPrecision));
         */
        // 6. CONFIG FOR SLIT IMAGING (put slit IN beam, grism OUT)
        /*
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
        
         */
        // 6. Config / expose / arc elements added by user (taken from JTable on form)
        LOTUSSequenceTableModel sequenceTableModel = (LOTUSSequenceTableModel) userSeqTable.getModel();
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

        // 9. AG off
        //AUTOGUIDE off, if on
        if (autoguiderIsOn) {
            rootComponent.addElement(new XExecutiveComponent("AG_OFF", new XAutoguiderConfig(IAutoguiderConfig.OFF, "AG_OFF")));
        }

        return rootComponent;
    }

    private IAutoguiderConfig getAutoguiderConfig() {
        int mode;
        String name;

        String actionCmd = bgAutoguide.getSelection().getActionCommand();
        if (actionCmd.equals(CMD_YES)) {
            mode = IAutoguiderConfig.ON;
        } else if (actionCmd.equals(CMD_NO)) {
            mode = IAutoguiderConfig.OFF;
        } else if (actionCmd.equals(CMD_AUTO)) {
            mode = IAutoguiderConfig.ON_IF_AVAILABLE;
        } else {
            return null;
        }
        name = "autoguider1";

        XAutoguiderConfig autoguiderConfig = new XAutoguiderConfig(mode, name);
        return autoguiderConfig;
    }

    //extract executive component from table line entry
    private XExecutiveComponent getExecutiveComponent(WizardPanelTableLineEntry armSeqTableLineEntry) throws Exception {
        XExecutiveComponent executiveComponent;
        int lineEntryType = armSeqTableLineEntry.getLineEntryType();
        switch (lineEntryType) {
            case WizardPanelTableLineEntry.LINE_TYPE_LOTUS_CONFIG:
                XBlueTwoSlitSpectrographInstrumentConfig blueTwoSlitSpectrographInstrumentConfig = armSeqTableLineEntry.getBlueTwoSlitSpectrographInstrumentConfig();
                XInstrumentConfigSelector instrumentConfigSelector = new XInstrumentConfigSelector(blueTwoSlitSpectrographInstrumentConfig);
                executiveComponent = new XExecutiveComponent("CONFIG", instrumentConfigSelector);
                break;
            case WizardPanelTableLineEntry.LINE_TYPE_EXPOSE:
                int repeatCount = armSeqTableLineEntry.getExposureCount();
                double exposureTime = armSeqTableLineEntry.getExposureTime() * 1000; //mS
                XMultipleExposure multipleExposure = new XMultipleExposure(exposureTime, repeatCount);
                multipleExposure.setStandard(jcbIsStandard.isSelected()); //is standard from gui control.
                executiveComponent = new XExecutiveComponent("EXPOSURE", multipleExposure);
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
        rotatorConfig.setInstrumentName(CONST.LOTUS);
        rotatorConfig.setRotatorAngle(Math.toRadians(11));

        return rotatorConfig;
    }

    private String getAutoguiderModeAsString() {
        String actionCmd = bgAutoguide.getSelection().getActionCommand();
        return actionCmd;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpObsTablePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        userSeqTable = new javax.swing.JTable();
        jbtnDelete = new javax.swing.JButton();
        jbtnUp = new javax.swing.JButton();
        jbtnDown = new javax.swing.JButton();
        jbtnExpose = new javax.swing.JButton();
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
        jLabel6 = new javax.swing.JLabel();
        jrbAutoguideYes = new javax.swing.JRadioButton();
        jrbAutoguideNo = new javax.swing.JRadioButton();
        jrbAutoguideAuto = new javax.swing.JRadioButton();
        jbtnCreateNewInstrumentConfig = new javax.swing.JButton();
        jcbInstrumentConfigs = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();

        jpObsTablePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(222, 222, 222)));

        userSeqTable.setModel(new LOTUSSequenceTableModel());
        userSeqTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(userSeqTable);

        jbtnDelete.setText("Delete");
        jbtnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDeleteActionPerformed(evt);
            }
        });

        jbtnUp.setText("Up");
        jbtnUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnUpActionPerformed(evt);
            }
        });

        jbtnDown.setText("Down");
        jbtnDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDownActionPerformed(evt);
            }
        });

        jbtnExpose.setText("Expose");
        jbtnExpose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnExposeActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jpObsTablePanelLayout = new org.jdesktop.layout.GroupLayout(jpObsTablePanel);
        jpObsTablePanel.setLayout(jpObsTablePanelLayout);
        jpObsTablePanelLayout.setHorizontalGroup(
            jpObsTablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpObsTablePanelLayout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 814, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(jpObsTablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jpObsTablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jbtnDown)
                        .add(jbtnDelete)
                        .add(jbtnUp))
                    .add(jbtnExpose))
                .add(16, 16, 16))
        );

        jpObsTablePanelLayout.linkSize(new java.awt.Component[] {jbtnDelete, jbtnDown, jbtnUp}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jpObsTablePanelLayout.setVerticalGroup(
            jpObsTablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jpObsTablePanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jpObsTablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 246, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jpObsTablePanelLayout.createSequentialGroup()
                        .add(14, 14, 14)
                        .add(jbtnExpose)
                        .add(72, 72, 72)
                        .add(jbtnUp)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jbtnDown)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jbtnDelete)))
                .add(46, 46, 46))
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

        jLabel8.setText("Config / Expose elements");

        jLabel5.setText("Instrument = IO:O");

        jcbIsStandard.setText("Is Standard");

        jLabel6.setText("Autoguide:");

        jrbAutoguideYes.setText("Yes");

        jrbAutoguideNo.setText("Off");

        jrbAutoguideAuto.setSelected(true);
        jrbAutoguideAuto.setText("On if available");

        jbtnCreateNewInstrumentConfig.setText("Create New Instrument config");
        jbtnCreateNewInstrumentConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCreateNewInstrumentConfigActionPerformed(evt);
            }
        });

        jcbInstrumentConfigs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbInstrumentConfigsActionPerformed(evt);
            }
        });

        jLabel4.setText("or");

        jLabel9.setText("Select Instrument config:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(12, 12, 12)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(layout.createSequentialGroup()
                        .add(15, 15, 15)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jcbTarget, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 545, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(layout.createSequentialGroup()
                                .add(jLabel4)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jbtnCreateNewInstrumentConfig))
                            .add(layout.createSequentialGroup()
                                .add(jLabel9)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jcbInstrumentConfigs, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 545, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(layout.createSequentialGroup()
                                .add(jLabel3)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jbtnCreateNewTarget)))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(15, 15, 15)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel7)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jLabel5)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jcbAcquisition, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 159, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(86, 520, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(jLabel6)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jrbAutoguideYes)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jrbAutoguideNo)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jrbAutoguideAuto)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jcbIsStandard)
                                .add(103, 103, 103))))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jpObsTablePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jLabel8)
                        .addContainerGap(741, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
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
                    .add(jLabel9)
                    .add(jcbInstrumentConfigs, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(jbtnCreateNewInstrumentConfig))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(jcbAcquisition, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel6)
                        .add(jrbAutoguideYes)
                        .add(jrbAutoguideNo)
                        .add(jrbAutoguideAuto))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jcbIsStandard))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel8)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jpObsTablePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 298, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
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
        selectTargetInJCB(target);

    } catch (Phase2Exception ex) {
        ex.printStackTrace();
        logger.error(ex);
        JOptionPane.showMessageDialog(this, ex.getMessage());
    }
}//GEN-LAST:event_jbtnCreateNewTargetActionPerformed

private void jbtnExposeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExposeActionPerformed

    NewExposureDialog dialog = new NewExposureDialog(true);
    dialog.setVisible(true);
    //blocks
    ExposureWrapper exposureWrapper = dialog.getExposure();

    dialog.setVisible(false);
    dialog.dispose();
    if (exposureWrapper == null) {
        return;
    }
    WizardPanelTableLineEntry seqTableLineEntry = new WizardPanelTableLineEntry(exposureWrapper.getExposureTime(), exposureWrapper.getExposureCount());

    LOTUSSequenceTableModel sequenceTableModel = (LOTUSSequenceTableModel) userSeqTable.getModel();
    sequenceTableModel.addTableLineEntry(seqTableLineEntry);
    userSeqTable.updateUI();
}//GEN-LAST:event_jbtnExposeActionPerformed

private void jbtnUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnUpActionPerformed

    int selectedRow = userSeqTable.getSelectedRow();
    if (selectedRow < 0) {
        return;
    }

    LOTUSSequenceTableModel lOTUSSequenceTableModel = (LOTUSSequenceTableModel) userSeqTable.getModel();
    int newRow = lOTUSSequenceTableModel.moveRowUp(selectedRow);
    userSeqTable.getSelectionModel().setSelectionInterval(newRow, newRow);
    userSeqTable.updateUI();
}//GEN-LAST:event_jbtnUpActionPerformed

private void jbtnDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDownActionPerformed

    int selectedRow = userSeqTable.getSelectedRow();
    if (selectedRow < 0) {
        return;
    }

    LOTUSSequenceTableModel sequenceTableModel = (LOTUSSequenceTableModel) userSeqTable.getModel();
    int newRow = sequenceTableModel.moveRowDown(selectedRow);
    userSeqTable.getSelectionModel().setSelectionInterval(newRow, newRow);
    userSeqTable.updateUI();
}//GEN-LAST:event_jbtnDownActionPerformed

private void jbtnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDeleteActionPerformed

    int selectedRow = userSeqTable.getSelectedRow();
    if (selectedRow < 0) {
        return;
    }

    LOTUSSequenceTableModel lOTUSSequenceTableModel = (LOTUSSequenceTableModel) userSeqTable.getModel();
    lOTUSSequenceTableModel.deleteRow(selectedRow);
    userSeqTable.updateUI();
}//GEN-LAST:event_jbtnDeleteActionPerformed

    //if the user changes the acquisition type, change the slit imaging exposure length in the JTable.
    private void jcbAcquisitionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbAcquisitionActionPerformed

    }//GEN-LAST:event_jcbAcquisitionActionPerformed

    private void jbtnCreateNewInstrumentConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCreateNewInstrumentConfigActionPerformed
        NewInstrumentConfigDialog dialog = new NewInstrumentConfigDialog(true, CONST.LOTUS);
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
            //add it to the database
            Phase2ModelClient.getInstance().addInstrumentConfig(program.getID(), instrumentConfig);
            //populate the combo box
            populateConfigList();
            //select the instrument config
            selectInstrumentConfigInJCB(instrumentConfig);
        } catch (Phase2Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }//GEN-LAST:event_jbtnCreateNewInstrumentConfigActionPerformed

    private void jcbInstrumentConfigsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbInstrumentConfigsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jcbInstrumentConfigsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtnCreateNewInstrumentConfig;
    private javax.swing.JButton jbtnCreateNewTarget;
    private javax.swing.JButton jbtnDelete;
    private javax.swing.JButton jbtnDown;
    private javax.swing.JButton jbtnExpose;
    private javax.swing.JButton jbtnUp;
    private javax.swing.JComboBox jcbAcquisition;
    private javax.swing.JComboBox jcbInstrumentConfigs;
    private javax.swing.JCheckBox jcbIsStandard;
    private javax.swing.JComboBox jcbTarget;
    private javax.swing.JPanel jpObsTablePanel;
    private javax.swing.JRadioButton jrbAutoguideAuto;
    private javax.swing.JRadioButton jrbAutoguideNo;
    private javax.swing.JRadioButton jrbAutoguideYes;
    private javax.swing.JTable userSeqTable;
    // End of variables declaration//GEN-END:variables
}
