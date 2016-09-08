/*
 * MultiColourPhotomSequencePanel.java
 *
 * Created on June 11, 2009, 12:36 PM
 */
package ngat.oss.client.gui.panel.wizards;

import ngat.oss.client.gui.panel.*;
import java.util.Iterator;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import ngat.oss.client.gui.dialog.NewInstrumentConfigDialog;
import ngat.oss.client.gui.dialog.NewTargetDialog;
import ngat.oss.client.Phase2ModelClient;
import ngat.oss.client.gui.model.ArmSequenceTableModel;
import ngat.oss.client.gui.dialog.NewArcDialog;
import ngat.oss.client.gui.dialog.NewFrodoArmConfigDialog;
import ngat.oss.client.gui.dialog.NewExposureDialog;
import ngat.oss.client.gui.dialog.NewFrodoArmLampFlatDialog;
import ngat.oss.client.gui.render.TargetRenderer;
import ngat.oss.client.gui.reference.CONST;
import ngat.oss.client.gui.reference.Session;
import ngat.oss.client.gui.wrapper.WizardPanelTableLineEntry;
import ngat.oss.client.gui.wrapper.ExposureWrapper;
import ngat.oss.exception.Phase2Exception;
import ngat.phase2.IAcquisitionConfig;
import ngat.phase2.IAutoguiderConfig;
import ngat.phase2.IExecutiveAction;
import ngat.phase2.IInstrumentConfig;
import ngat.phase2.IProgram;
import ngat.phase2.IRotatorConfig;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.ITarget;
import ngat.phase2.XAcquisitionConfig;
import ngat.phase2.XArc;
import ngat.phase2.XAutoguiderConfig;
import ngat.phase2.XBranchComponent;
import ngat.phase2.XDualBeamSpectrographInstrumentConfig;
import ngat.phase2.XExecutiveComponent;
import ngat.phase2.XInstrumentConfigSelector;
import ngat.phase2.XIteratorComponent;
import ngat.phase2.XIteratorRepeatCountCondition;
import ngat.phase2.XLampFlat;
import ngat.phase2.XMultipleExposure;
import ngat.phase2.XRotatorConfig;
import ngat.phase2.XSlew;
import ngat.phase2.XTarget;
import org.apache.log4j.Logger;

/**
 *
 * @author  nrc
 */
public class WizardPanelDualBeamSpectrographSequence extends javax.swing.JPanel {

    static Logger logger = Logger.getLogger(WizardPanelDualBeamSpectrographSequence.class);
    
    private final String ALLOW_ALT_TEXT = "Allow alternative acquisition instrument";
    private final String DO_NOT_ALLOW_ALT_TEXT = "DO NOT allow alternative acquisition instrument";
        
    private IProgram program;
    private List targetList;
    
    private ButtonGroup bgAutoguide;
    //private ButtonGroup bgAcquire;
    
    private static final String CMD_NO = "AUTOGUIDE_NO";
    private static final String CMD_YES = "AUTOGUIDE_YES";
    private static final String CMD_AUTO = "AUTOGUIDE_AUTO";

    private static final String AUTOMATIC = "Automatic (Cardinal)";
    private static final String MANUAL = "Manual";
    
    public static final int UNSET_ROTATOR_TYPE = -1;

    private boolean rotatorValueNeedsSetting = true;
    
    /** Creates new form MultiColourPhotomSequencePanel */
    public WizardPanelDualBeamSpectrographSequence(IProgram program) {
        this.program = program;
        initComponents();
        populateComponents();
        setUpComponents();

        //temporary hide
        jbtnLampRed.setVisible(false);
        jbtnLampBlue.setVisible(false);
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

        } catch (Phase2Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return;
        }

        //directly set the rotator index to be sure, and update the related variable
        jcbRotator.setSelectedIndex(0); //CARDINAL
        this.rotatorValueNeedsSetting = false;
    }

    public boolean rotatorValueNeedsSetting() {
        return rotatorValueNeedsSetting;
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

    private void setUpComponents() {
        jcbRotator.setSelectedItem(AUTOMATIC);

        //is standard tick box only visible to super users
        jcbIsStandard.setVisible(Session.getInstance().getUser().isSuperUser());
    }

    public ISequenceComponent getObservationSequence() {
        if (!validObservation()) {
            return null;
        }

        //ROOT ITERATOR
        XIteratorComponent rootComponent = new XIteratorComponent("Root", new XIteratorRepeatCountCondition(1));

        //1. SLEW

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

        //2. FOCAL PLANE
        //IAcquisitionConfig instrumentChange = new XAcquisitionConfig(IAcquisitionConfig.INSTRUMENT_CHANGE, CONST.RATCAM, null);
        //19/2/13
        //IAcquisitionConfig instrumentChange = new XAcquisitionConfig(IAcquisitionConfig.INSTRUMENT_CHANGE, CONST.IO_O, null);
        //7/1/14
        IAcquisitionConfig instrumentChange = new XAcquisitionConfig(IAcquisitionConfig.INSTRUMENT_CHANGE, CONST.IO_O, null, false, IAcquisitionConfig.PRECISION_NORMAL);
        rootComponent.addElement(new XExecutiveComponent("INSTRUMENT_CHANGE", instrumentChange));
                
        
        // 3. FINE TUNE - NORMAL PRECISION
        
        boolean allowAlternative = jcbAllowAlternative.getSelectedItem().equals(ALLOW_ALT_TEXT);
                
        IAcquisitionConfig acquisitionConfig;
        if (jcbAcquisition.getSelectedItem().equals("WCS")) {
            //acquisitionConfig = new XAcquisitionConfig(IAcquisitionConfig.WCS_FIT, CONST.FRODO, CONST.RATCAM);
            //19/2/13
            acquisitionConfig = new XAcquisitionConfig(IAcquisitionConfig.WCS_FIT, CONST.FRODO, CONST.IO_O, allowAlternative, IAcquisitionConfig.PRECISION_NORMAL);
        } else if (jcbAcquisition.getSelectedItem().equals("BRIGHTEST")) {
            //acquisitionConfig = new XAcquisitionConfig(IAcquisitionConfig.BRIGHTEST, CONST.FRODO, CONST.RATCAM);
            //19/2/13
            acquisitionConfig = new XAcquisitionConfig(IAcquisitionConfig.BRIGHTEST, CONST.FRODO, CONST.IO_O, allowAlternative, IAcquisitionConfig.PRECISION_NORMAL);
        } else {
            return null;
        }
        rootComponent.addElement(new XExecutiveComponent("ACQUISITION", acquisitionConfig));

        
        //4. AUTOGUIDE (+ INSTR_CFG if autoguiding)
        boolean autoguiderIsOn = false;
        IAutoguiderConfig autoguiderConfigSpecified = getAutoguiderConfig();
        if (autoguiderConfigSpecified.getAutoguiderCommand() != IAutoguiderConfig.OFF) {
            //get an instrument config (wrapped in an XExecutiveComponent) that is inserted in a branch by the user
            //we can then insert it before the autoguide cmd (see bug number 1592)
            XExecutiveComponent configSelectorExecutiveComponent = getInstrumentConfigInBranch();
            if (configSelectorExecutiveComponent == null) {
                logger.error("No instrument config specified in a branch, unable to insert instr cfg before autoguide cmd.");
            } else {
                rootComponent.addElement(configSelectorExecutiveComponent);
            }
            rootComponent.addElement(new XExecutiveComponent(getAutoguiderModeAsString(), autoguiderConfigSpecified));
            autoguiderIsOn = true;
        }
        
        //5 FRODO BRANCH ITERATORS
        
        //5.1 RED
        XBranchComponent branchComponent = new XBranchComponent(CONST.FRODO);
        XIteratorComponent redArmIteratorComponent =
                new XIteratorComponent(CONST.FRODO_RED, new XIteratorRepeatCountCondition(1));
        
        //iterate through red table, adding to redInstrumentSequenceComponent
        ArmSequenceTableModel redArmSequenceTableModel = (ArmSequenceTableModel) jtblRedSeqTable.getModel();
        List redArmEntries = redArmSequenceTableModel.getData();
        Iterator ri = redArmEntries.iterator();
        XExecutiveComponent executiveComponent;
        while (ri.hasNext()) {
            //take each ArmSeqTableLineEntry
            WizardPanelTableLineEntry armSeqTableLineEntry = (WizardPanelTableLineEntry) ri.next();
            try {
                executiveComponent = getExecutiveComponent(armSeqTableLineEntry);
                redArmIteratorComponent.addElement(executiveComponent);
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error(ex);
            }
        }

        branchComponent.addChildComponent(redArmIteratorComponent);

        //5.2 BLUE
        XIteratorComponent blueArmIteratorComponent =
                new XIteratorComponent(CONST.FRODO_BLUE, new XIteratorRepeatCountCondition(1));

        //iterate through red table, adding to redInstrumentSequenceComponent
        ArmSequenceTableModel blueArmSequenceTableModel = (ArmSequenceTableModel) jtblBlueSeqTable.getModel();
        List blueArmEntries = blueArmSequenceTableModel.getData();
        Iterator bi = blueArmEntries.iterator();
        while (bi.hasNext()) {
            //take each ArmSeqTableLineEntry
            WizardPanelTableLineEntry armSeqTableLineEntry = (WizardPanelTableLineEntry) bi.next();
            try {
                executiveComponent = getExecutiveComponent(armSeqTableLineEntry);
                blueArmIteratorComponent.addElement(executiveComponent);
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error(ex);
            }
        }

        branchComponent.addChildComponent(blueArmIteratorComponent);

        //add branch to root
        rootComponent.addElement(branchComponent);
        
        //6. AUTOGUIDE off, if on
        if (autoguiderIsOn) {
            rootComponent.addElement(new XExecutiveComponent("AG_OFF", new XAutoguiderConfig(IAutoguiderConfig.OFF, "AG_OFF")));
        }

        return rootComponent;
    }

    //want to get a copy of an instrument config entered by the user in a branch
    private XExecutiveComponent getInstrumentConfigInBranch() {
        //try red arm table first
        ArmSequenceTableModel redArmSequenceTableModel = (ArmSequenceTableModel) jtblRedSeqTable.getModel();
        List redArmEntries = redArmSequenceTableModel.getData();
        Iterator ri = redArmEntries.iterator();
        XExecutiveComponent executiveComponent;
        while (ri.hasNext()) {
            //take each ArmSeqTableLineEntry
            WizardPanelTableLineEntry armSeqTableLineEntry = (WizardPanelTableLineEntry) ri.next();
            try {
                executiveComponent = getExecutiveComponent(armSeqTableLineEntry);
                IExecutiveAction executiveAction = executiveComponent.getExecutiveAction();
                if (executiveAction.getClass().equals(XInstrumentConfigSelector.class)) {
                    return executiveComponent;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error(ex);
            }
        }

        //none found in red arm table, try blue arm table.
        ArmSequenceTableModel blueArmSequenceTableModel = (ArmSequenceTableModel) jtblBlueSeqTable.getModel();
        List blueArmEntries = blueArmSequenceTableModel.getData();
        Iterator bi = blueArmEntries.iterator();
        while (bi.hasNext()) {
            //take each ArmSeqTableLineEntry
            WizardPanelTableLineEntry armSeqTableLineEntry = (WizardPanelTableLineEntry) bi.next();
            try {
                executiveComponent = getExecutiveComponent(armSeqTableLineEntry);
                IExecutiveAction executiveAction = executiveComponent.getExecutiveAction();
                if (executiveAction.getClass().equals(XInstrumentConfigSelector.class)) {
                    return executiveComponent;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error(ex);
            }
        }
        return null;
    }

    private XExecutiveComponent getExecutiveComponent(WizardPanelTableLineEntry armSeqTableLineEntry) throws Exception {
        XExecutiveComponent executiveComponent;
        int lineEntryType = armSeqTableLineEntry.getLineEntryType();
        switch (lineEntryType) {
            case WizardPanelTableLineEntry.LINE_TYPE_FRODO_CONFIG:
                XDualBeamSpectrographInstrumentConfig dualBeamSpectrographInstrumentConfig = armSeqTableLineEntry.getDualBeamSpectrographInstrumentConfig();
                XInstrumentConfigSelector instrumentConfigSelector = new XInstrumentConfigSelector(dualBeamSpectrographInstrumentConfig);
                executiveComponent = new XExecutiveComponent("CONFIG", instrumentConfigSelector);
                break;
            case WizardPanelTableLineEntry.LINE_TYPE_EXPOSE:
                int repeatCount = armSeqTableLineEntry.getExposureCount();
                double exposureTime = armSeqTableLineEntry.getExposureTime() * 1000; //mS
                XMultipleExposure multipleExposure = new XMultipleExposure(exposureTime, repeatCount);
                multipleExposure.setStandard(jcbIsStandard.isSelected()); //is standard from gui control.
                executiveComponent = new XExecutiveComponent("EXPOSURE", multipleExposure);
                break;
            case WizardPanelTableLineEntry.LINE_TYPE_LAMP_FLAT:
                XLampFlat lampFlat = armSeqTableLineEntry.getLampFlat();
                executiveComponent = new XExecutiveComponent("LAMP_FLAT", lampFlat);
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
        int mode;
        double angleDegrees;
        String alignedToInstrument = "";

        String selectedRot = (String) jcbRotator.getSelectedItem();
        if (selectedRot.equalsIgnoreCase(MANUAL)) {
            mode = WizardPanelPhotometryPolarimetrySequence.UNSET_ROTATOR_TYPE;
        } else if (selectedRot.equalsIgnoreCase(AUTOMATIC)) {
            mode = IRotatorConfig.CARDINAL;
            //alignedToInstrument = CONST.RATCAM;
            alignedToInstrument = CONST.IO_O;
        } else {
            JOptionPane.showMessageDialog(this, "unknown rotator mode :" + selectedRot);
            return null;
        }
        
        /*
        try {
            double angleDegrees;
            angleDegrees = Double.parseDouble(jtfRotAngle.getText());
            return new XRotatorConfig(mode, UnitConverter.convertDegsToRads(angleDegrees));
        } catch (NumberFormatException e) {
            return null;
        }
        */
        
        XRotatorConfig rotatorConfig = new XRotatorConfig();
        rotatorConfig.setRotatorMode(mode); //may be unset, i.e. PhotometricSequenceWizardPanel.UNSET_ROTATOR_TYPE
        rotatorConfig.setInstrumentName(alignedToInstrument);
        return rotatorConfig;
    }
    
    private String getRotatorModeAsString() {
        String selectedRot = (String) jcbRotator.getSelectedItem();
        return selectedRot;
    }
    
    private IAutoguiderConfig getAutoguiderConfig() {
        int mode;
        String name;
        
        String actionCmd = bgAutoguide.getSelection().getActionCommand();
        if (actionCmd.equals(WizardPanelDualBeamSpectrographSequence.CMD_YES)) {
            mode = IAutoguiderConfig.ON;
        } else if (actionCmd.equals(WizardPanelDualBeamSpectrographSequence.CMD_NO)) {
            mode = IAutoguiderConfig.OFF;
        } else if (actionCmd.equals(WizardPanelDualBeamSpectrographSequence.CMD_AUTO)) {
            mode = IAutoguiderConfig.ON_IF_AVAILABLE;
        } else {
            return null;
        }
        name = "autoguider1";
        
        XAutoguiderConfig autoguiderConfig = new XAutoguiderConfig(mode, name);
        return autoguiderConfig;
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
        jtblRedSeqTable = new javax.swing.JTable();
        jbtnDeleteRed = new javax.swing.JButton();
        jbtnUpRed = new javax.swing.JButton();
        jbtnDownRed = new javax.swing.JButton();
        jbtnConfigRed = new javax.swing.JButton();
        jbtnLampRed = new javax.swing.JButton();
        jbtnExposeRed = new javax.swing.JButton();
        jbtnArcRed = new javax.swing.JButton();
        jpObsTablePanel1 = new javax.swing.JPanel();
        jbtnDeleteBlue = new javax.swing.JButton();
        jbtnUpBlue = new javax.swing.JButton();
        jbtnDownBlue = new javax.swing.JButton();
        jbtnConfigBlue = new javax.swing.JButton();
        jbtnLampBlue = new javax.swing.JButton();
        jbtnExposeBlue = new javax.swing.JButton();
        jbtnArcBlue = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtblBlueSeqTable = new javax.swing.JTable();
        jpObsSeqStartPanel1 = new javax.swing.JPanel();
        jbtnCreateNewInstConfig = new javax.swing.JButton();
        jcbIsStandard = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jcbTarget = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jbtnCreateNewTarget = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jcbAcquisition = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jrbAutoguideYes = new javax.swing.JRadioButton();
        jrbAutoguideNo = new javax.swing.JRadioButton();
        jrbAutoguideAuto = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel8 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jcbRotator = new javax.swing.JComboBox();
        jcbAllowAlternative = new javax.swing.JComboBox();

        jpObsTablePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Red Arm"));

        jtblRedSeqTable.setModel(new ArmSequenceTableModel());
        jtblRedSeqTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jtblRedSeqTable);

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

        jbtnConfigRed.setText("Config");
        jbtnConfigRed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnConfigRedActionPerformed(evt);
            }
        });

        jbtnLampRed.setText("Lamp");
        jbtnLampRed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnLampRedActionPerformed(evt);
            }
        });

        jbtnExposeRed.setText("Expose");
        jbtnExposeRed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnExposeRedActionPerformed(evt);
            }
        });

        jbtnArcRed.setText("Arc");
        jbtnArcRed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnArcRedActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jpObsTablePanelLayout = new org.jdesktop.layout.GroupLayout(jpObsTablePanel);
        jpObsTablePanel.setLayout(jpObsTablePanelLayout);
        jpObsTablePanelLayout.setHorizontalGroup(
            jpObsTablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpObsTablePanelLayout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 264, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jpObsTablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jbtnConfigRed)
                    .add(jbtnDownRed)
                    .add(jbtnDeleteRed)
                    .add(jbtnUpRed)
                    .add(jbtnExposeRed)
                    .add(jbtnArcRed)
                    .add(jbtnLampRed))
                .add(98, 98, 98))
        );

        jpObsTablePanelLayout.linkSize(new java.awt.Component[] {jbtnDeleteRed, jbtnDownRed, jbtnUpRed}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jpObsTablePanelLayout.linkSize(new java.awt.Component[] {jbtnArcRed, jbtnConfigRed, jbtnExposeRed, jbtnLampRed}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jpObsTablePanelLayout.setVerticalGroup(
            jpObsTablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpObsTablePanelLayout.createSequentialGroup()
                .add(jpObsTablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jpObsTablePanelLayout.createSequentialGroup()
                        .add(jbtnConfigRed)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jbtnExposeRed)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jbtnArcRed)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jbtnLampRed)
                        .add(49, 49, 49)
                        .add(jbtnUpRed)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jbtnDownRed)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jbtnDeleteRed))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 278, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpObsTablePanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Blue Arm"));

        jbtnDeleteBlue.setText("Delete");
        jbtnDeleteBlue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDeleteBlueActionPerformed(evt);
            }
        });

        jbtnUpBlue.setText("Up");
        jbtnUpBlue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnUpBlueActionPerformed(evt);
            }
        });

        jbtnDownBlue.setText("Down");
        jbtnDownBlue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDownBlueActionPerformed(evt);
            }
        });

        jbtnConfigBlue.setText("Config");
        jbtnConfigBlue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnConfigBlueActionPerformed(evt);
            }
        });

        jbtnLampBlue.setText("Lamp");
        jbtnLampBlue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnLampBlueActionPerformed(evt);
            }
        });

        jbtnExposeBlue.setText("Expose");
        jbtnExposeBlue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnExposeBlueActionPerformed(evt);
            }
        });

        jbtnArcBlue.setText("Arc");
        jbtnArcBlue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnArcBlueActionPerformed(evt);
            }
        });

        jtblBlueSeqTable.setModel(new ArmSequenceTableModel());
        jtblBlueSeqTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(jtblBlueSeqTable);

        org.jdesktop.layout.GroupLayout jpObsTablePanel1Layout = new org.jdesktop.layout.GroupLayout(jpObsTablePanel1);
        jpObsTablePanel1.setLayout(jpObsTablePanel1Layout);
        jpObsTablePanel1Layout.setHorizontalGroup(
            jpObsTablePanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpObsTablePanel1Layout.createSequentialGroup()
                .add(2, 2, 2)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 264, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jpObsTablePanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jbtnConfigBlue)
                    .add(jbtnDownBlue)
                    .add(jbtnDeleteBlue)
                    .add(jbtnUpBlue)
                    .add(jbtnArcBlue)
                    .add(jbtnExposeBlue)
                    .add(jbtnLampBlue))
                .addContainerGap())
        );

        jpObsTablePanel1Layout.linkSize(new java.awt.Component[] {jbtnArcBlue, jbtnConfigBlue, jbtnDeleteBlue, jbtnDownBlue, jbtnExposeBlue, jbtnLampBlue, jbtnUpBlue}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jpObsTablePanel1Layout.setVerticalGroup(
            jpObsTablePanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpObsTablePanel1Layout.createSequentialGroup()
                .add(jpObsTablePanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jpObsTablePanel1Layout.createSequentialGroup()
                        .add(jbtnConfigBlue)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jbtnExposeBlue)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jbtnArcBlue)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jbtnLampBlue)
                        .add(47, 47, 47)
                        .add(jbtnUpBlue)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jbtnDownBlue)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jbtnDeleteBlue))
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 278, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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
                .add(49, 49, 49)
                .add(jbtnCreateNewInstConfig)
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jpObsSeqStartPanel1Layout.setVerticalGroup(
            jpObsSeqStartPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpObsSeqStartPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jbtnCreateNewInstConfig)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jcbIsStandard.setText("Is Standard");

        jLabel2.setText("Select Target:");

        jLabel4.setText("Rotator Setting:");

        jLabel3.setText("or");

        jbtnCreateNewTarget.setText("Create New Target");
        jbtnCreateNewTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCreateNewTargetActionPerformed(evt);
            }
        });

        jLabel7.setText("Acquisition:");

        jcbAcquisition.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "WCS", "BRIGHTEST" }));

        jLabel6.setText("Autoguide:");

        jrbAutoguideYes.setText("Yes");

        jrbAutoguideNo.setText("Off");

        jrbAutoguideAuto.setSelected(true);
        jrbAutoguideAuto.setText("On if available");

        jLabel1.setText("Initial Configuration");

        jSeparator1.setBackground(java.awt.SystemColor.windowBorder);
        jSeparator1.setForeground(new java.awt.Color(0, 0, 0));

        jLabel8.setText("Arm Configuration");

        jSeparator2.setBackground(java.awt.SystemColor.windowBorder);
        jSeparator2.setForeground(new java.awt.Color(0, 0, 0));

        jcbRotator.setModel(new javax.swing.DefaultComboBoxModel(new String[] { AUTOMATIC, MANUAL }));
        jcbRotator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbRotatorActionPerformed(evt);
            }
        });

        jcbAllowAlternative.setModel(new javax.swing.DefaultComboBoxModel(new String[] { ALLOW_ALT_TEXT, DO_NOT_ALLOW_ALT_TEXT }));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(11, 11, 11)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(jLabel6)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(jrbAutoguideYes)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jrbAutoguideNo)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jrbAutoguideAuto))
                                    .add(layout.createSequentialGroup()
                                        .add(11, 11, 11)
                                        .add(jLabel3)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jbtnCreateNewTarget))
                                    .add(layout.createSequentialGroup()
                                        .add(jLabel4)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jcbRotator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 223, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                .add(293, 293, 293)
                                .add(jcbIsStandard))
                            .add(layout.createSequentialGroup()
                                .add(jLabel7)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jcbAcquisition, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 159, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(32, 32, 32)
                                .add(jcbAllowAlternative, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 267, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(33, 33, 33)
                                .add(jpObsSeqStartPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(layout.createSequentialGroup()
                        .add(13, 13, 13)
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jcbTarget, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 646, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(jpObsTablePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 371, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jpObsTablePanel1, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                            .add(jLabel1)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jSeparator1))
                        .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                            .add(jLabel8)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 638, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSeparator1)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jcbTarget, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(1, 1, 1)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel3)
                            .add(jbtnCreateNewTarget))
                        .add(12, 12, 12)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel4)
                            .add(jcbRotator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(11, 11, 11)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel7)
                            .add(jcbAcquisition, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jcbAllowAlternative, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel6)
                            .add(jrbAutoguideYes)
                            .add(jrbAutoguideNo)
                            .add(jrbAutoguideAuto)))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(24, 24, 24)
                        .add(jpObsSeqStartPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jcbIsStandard)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel8)
                    .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jpObsTablePanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jpObsTablePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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

private void jbtnConfigRedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnConfigRedActionPerformed
    NewFrodoArmConfigDialog dialog = new NewFrodoArmConfigDialog(true, CONST.FRODO_RED, program);
    dialog.setVisible(true);
    //blocks
    XDualBeamSpectrographInstrumentConfig dualBeamSpectrographInstrumentConfig = dialog.getConfig();
    
    dialog.setVisible(false);
    dialog.dispose();

    if (dualBeamSpectrographInstrumentConfig == null) {
        return;
    }
    WizardPanelTableLineEntry armSeqTableLineEntry = new WizardPanelTableLineEntry(dualBeamSpectrographInstrumentConfig);

    ArmSequenceTableModel redArmSequenceTableModel = (ArmSequenceTableModel) jtblRedSeqTable.getModel();
    redArmSequenceTableModel.addTableLineEntry(armSeqTableLineEntry);
    jtblRedSeqTable.updateUI();
}//GEN-LAST:event_jbtnConfigRedActionPerformed

private void jbtnExposeRedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExposeRedActionPerformed
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

    ArmSequenceTableModel redArmSequenceTableModel = (ArmSequenceTableModel) jtblRedSeqTable.getModel();
    redArmSequenceTableModel.addTableLineEntry(armSeqTableLineEntry);
    jtblRedSeqTable.updateUI();
}//GEN-LAST:event_jbtnExposeRedActionPerformed

private void jbtnLampRedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnLampRedActionPerformed
    NewFrodoArmLampFlatDialog dialog = new NewFrodoArmLampFlatDialog(true);
    dialog.setVisible(true);
    //blocks
    XLampFlat lampFlat = dialog.getLampFlat();
    dialog.setVisible(false);
    dialog.dispose();

    if (lampFlat == null) {
        return;
    }
    WizardPanelTableLineEntry armSeqTableLineEntry = new WizardPanelTableLineEntry(lampFlat);

    ArmSequenceTableModel redArmSequenceTableModel = (ArmSequenceTableModel) jtblRedSeqTable.getModel();
    redArmSequenceTableModel.addTableLineEntry(armSeqTableLineEntry);
    jtblRedSeqTable.updateUI();
}//GEN-LAST:event_jbtnLampRedActionPerformed

private void jbtnArcRedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnArcRedActionPerformed
    NewArcDialog dialog = new NewArcDialog(true, false);
    dialog.setVisible(true);
    //blocks
    XArc arc = dialog.getArc();

    dialog.setVisible(false);
    dialog.dispose();

    if (arc == null) {
        return;
    }
    WizardPanelTableLineEntry armSeqTableLineEntry = new WizardPanelTableLineEntry(arc);

    ArmSequenceTableModel redArmSequenceTableModel = (ArmSequenceTableModel) jtblRedSeqTable.getModel();
    redArmSequenceTableModel.addTableLineEntry(armSeqTableLineEntry);
    jtblRedSeqTable.updateUI();
}//GEN-LAST:event_jbtnArcRedActionPerformed

private void jbtnConfigBlueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnConfigBlueActionPerformed
    NewFrodoArmConfigDialog dialog = new NewFrodoArmConfigDialog(true, CONST.FRODO_BLUE, program);
    dialog.setVisible(true);
    //blocks
    XDualBeamSpectrographInstrumentConfig dualBeamSpectrographInstrumentConfig = dialog.getConfig();

    dialog.setVisible(false);
    dialog.dispose();

    if (dualBeamSpectrographInstrumentConfig == null) {
        return;
    }
    WizardPanelTableLineEntry armSeqTableLineEntry = new WizardPanelTableLineEntry(dualBeamSpectrographInstrumentConfig);

    ArmSequenceTableModel blueArmSequenceTableModel = (ArmSequenceTableModel) jtblBlueSeqTable.getModel();
    blueArmSequenceTableModel.addTableLineEntry(armSeqTableLineEntry);
    jtblBlueSeqTable.updateUI();
}//GEN-LAST:event_jbtnConfigBlueActionPerformed

private void jbtnExposeBlueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExposeBlueActionPerformed
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

    ArmSequenceTableModel blueArmSequenceTableModel = (ArmSequenceTableModel) jtblBlueSeqTable.getModel();
    blueArmSequenceTableModel.addTableLineEntry(armSeqTableLineEntry);
    jtblBlueSeqTable.updateUI();
}//GEN-LAST:event_jbtnExposeBlueActionPerformed

private void jbtnLampBlueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnLampBlueActionPerformed
    NewFrodoArmLampFlatDialog dialog = new NewFrodoArmLampFlatDialog(true);
    dialog.setVisible(true);
    //blocks
    XLampFlat lampFlat = dialog.getLampFlat();

    dialog.setVisible(false);
    dialog.dispose();

    if (lampFlat == null) {
        return;
    }
    WizardPanelTableLineEntry armSeqTableLineEntry = new WizardPanelTableLineEntry(lampFlat);

    ArmSequenceTableModel blueArmSequenceTableModel = (ArmSequenceTableModel) jtblBlueSeqTable.getModel();
    blueArmSequenceTableModel.addTableLineEntry(armSeqTableLineEntry);
    jtblBlueSeqTable.updateUI();
}//GEN-LAST:event_jbtnLampBlueActionPerformed

private void jbtnArcBlueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnArcBlueActionPerformed
    NewArcDialog dialog = new NewArcDialog(true, false);
    dialog.setVisible(true);
    //blocks
    XArc arc = dialog.getArc();

    dialog.setVisible(false);
    dialog.dispose();

    if (arc == null) {
        return;
    }
    WizardPanelTableLineEntry armSeqTableLineEntry = new WizardPanelTableLineEntry(arc);

    ArmSequenceTableModel blueArmSequenceTableModel = (ArmSequenceTableModel) jtblBlueSeqTable.getModel();
    blueArmSequenceTableModel.addTableLineEntry(armSeqTableLineEntry);
    jtblBlueSeqTable.updateUI();
}//GEN-LAST:event_jbtnArcBlueActionPerformed

private void jbtnUpRedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnUpRedActionPerformed
    int selectedRow = jtblRedSeqTable.getSelectedRow();
    if (selectedRow < 0) {
        return;
    }
   
    ArmSequenceTableModel redArmSequenceTableModel = (ArmSequenceTableModel) jtblRedSeqTable.getModel();
    int newRow = redArmSequenceTableModel.moveRowUp(selectedRow);
    jtblRedSeqTable.getSelectionModel().setSelectionInterval(newRow, newRow);
    jtblRedSeqTable.updateUI();
}//GEN-LAST:event_jbtnUpRedActionPerformed

private void jbtnDownRedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDownRedActionPerformed
    int selectedRow = jtblRedSeqTable.getSelectedRow();
    if (selectedRow < 0) {
        return;
    }

    ArmSequenceTableModel redArmSequenceTableModel = (ArmSequenceTableModel) jtblRedSeqTable.getModel();
    int newRow = redArmSequenceTableModel.moveRowDown(selectedRow);
    jtblRedSeqTable.getSelectionModel().setSelectionInterval(newRow, newRow);
    jtblRedSeqTable.updateUI();
}//GEN-LAST:event_jbtnDownRedActionPerformed

private void jbtnDeleteRedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDeleteRedActionPerformed
    int selectedRow = jtblRedSeqTable.getSelectedRow();
    if (selectedRow < 0) {
        return;
    }

    ArmSequenceTableModel redArmSequenceTableModel = (ArmSequenceTableModel) jtblRedSeqTable.getModel();
    redArmSequenceTableModel.deleteRow(selectedRow);
    jtblRedSeqTable.updateUI();
}//GEN-LAST:event_jbtnDeleteRedActionPerformed

private void jbtnUpBlueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnUpBlueActionPerformed
    int selectedRow = jtblBlueSeqTable.getSelectedRow();
    if (selectedRow < 0) {
        return;
    }

    ArmSequenceTableModel blueArmSequenceTableModel = (ArmSequenceTableModel) jtblBlueSeqTable.getModel();
    int newRow = blueArmSequenceTableModel.moveRowUp(selectedRow);
    jtblBlueSeqTable.getSelectionModel().setSelectionInterval(newRow, newRow);
    jtblBlueSeqTable.updateUI();
}//GEN-LAST:event_jbtnUpBlueActionPerformed

private void jbtnDownBlueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDownBlueActionPerformed
    int selectedRow = jtblBlueSeqTable.getSelectedRow();
    if (selectedRow < 0) {
        return;
    }

    ArmSequenceTableModel blueArmSequenceTableModel = (ArmSequenceTableModel) jtblBlueSeqTable.getModel();
    int newRow = blueArmSequenceTableModel.moveRowDown(selectedRow);
    jtblBlueSeqTable.getSelectionModel().setSelectionInterval(newRow, newRow);
    jtblBlueSeqTable.updateUI();
}//GEN-LAST:event_jbtnDownBlueActionPerformed

private void jbtnDeleteBlueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDeleteBlueActionPerformed
    int selectedRow = jtblBlueSeqTable.getSelectedRow();
    if (selectedRow < 0) {
        return;
    }

    ArmSequenceTableModel blueArmSequenceTableModel = (ArmSequenceTableModel) jtblBlueSeqTable.getModel();
    blueArmSequenceTableModel.deleteRow(selectedRow);
    jtblBlueSeqTable.updateUI();
}//GEN-LAST:event_jbtnDeleteBlueActionPerformed

private void jbtnCreateNewInstConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCreateNewInstConfigActionPerformed
    NewInstrumentConfigDialog dialog = new NewInstrumentConfigDialog(true, CONST.FRODO);
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

private void jcbRotatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbRotatorActionPerformed
    String selectedRot = (String) jcbRotator.getSelectedItem();
    if (selectedRot.equalsIgnoreCase(MANUAL)) {
        rotatorValueNeedsSetting = true;
    } else if (selectedRot.equalsIgnoreCase(AUTOMATIC)) {
        rotatorValueNeedsSetting = false;
    } else {
        JOptionPane.showMessageDialog(this, "unknown rotator mode :" + selectedRot);
        rotatorValueNeedsSetting = true;
    }
}//GEN-LAST:event_jcbRotatorActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton jbtnArcBlue;
    private javax.swing.JButton jbtnArcRed;
    private javax.swing.JButton jbtnConfigBlue;
    private javax.swing.JButton jbtnConfigRed;
    private javax.swing.JButton jbtnCreateNewInstConfig;
    private javax.swing.JButton jbtnCreateNewTarget;
    private javax.swing.JButton jbtnDeleteBlue;
    private javax.swing.JButton jbtnDeleteRed;
    private javax.swing.JButton jbtnDownBlue;
    private javax.swing.JButton jbtnDownRed;
    private javax.swing.JButton jbtnExposeBlue;
    private javax.swing.JButton jbtnExposeRed;
    private javax.swing.JButton jbtnLampBlue;
    private javax.swing.JButton jbtnLampRed;
    private javax.swing.JButton jbtnUpBlue;
    private javax.swing.JButton jbtnUpRed;
    private javax.swing.JComboBox jcbAcquisition;
    private javax.swing.JComboBox jcbAllowAlternative;
    private javax.swing.JCheckBox jcbIsStandard;
    private javax.swing.JComboBox jcbRotator;
    private javax.swing.JComboBox jcbTarget;
    private javax.swing.JPanel jpObsSeqStartPanel1;
    private javax.swing.JPanel jpObsTablePanel;
    private javax.swing.JPanel jpObsTablePanel1;
    private javax.swing.JRadioButton jrbAutoguideAuto;
    private javax.swing.JRadioButton jrbAutoguideNo;
    private javax.swing.JRadioButton jrbAutoguideYes;
    private javax.swing.JTable jtblBlueSeqTable;
    private javax.swing.JTable jtblRedSeqTable;
    // End of variables declaration//GEN-END:variables
}
