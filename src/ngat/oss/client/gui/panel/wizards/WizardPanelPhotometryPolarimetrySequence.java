/*
 * MultiColourPhotomSequencePanel.java
 *
 * Created on June 11, 2009, 12:36 PM
 */
package ngat.oss.client.gui.panel.wizards;

import ngat.oss.client.gui.panel.*;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import ngat.oss.client.gui.dialog.NewInstrumentConfigDialog;
import ngat.oss.client.gui.dialog.NewTargetDialog;
import ngat.oss.client.Phase2ModelClient;
import ngat.oss.client.gui.model.PhotomSeqWizardTableModel;
import ngat.oss.client.gui.render.InstrumentConfigRenderer;
import ngat.oss.client.gui.render.TargetRenderer;
import ngat.oss.client.gui.reference.CONST;
import ngat.oss.client.gui.reference.Session;
import ngat.oss.client.gui.wrapper.PhotometricSeqWizardTableLineEntry;
import ngat.oss.exception.Phase2Exception;
import ngat.phase2.IAcquisitionConfig;
import ngat.phase2.IAutoguiderConfig;
import ngat.phase2.IExposure;
import ngat.phase2.IInstrumentConfig;
import ngat.phase2.IProgram;
import ngat.phase2.IRotatorConfig;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.ITarget;
import ngat.phase2.XAcquisitionConfig;
import ngat.phase2.XAutoguiderConfig;
import ngat.phase2.XBias;
import ngat.phase2.XDark;
import ngat.phase2.XExecutiveComponent;
import ngat.phase2.XFocusOffset;
import ngat.phase2.XInstrumentConfig;
import ngat.phase2.XInstrumentConfigSelector;
import ngat.phase2.XIteratorComponent;
import ngat.phase2.XIteratorRepeatCountCondition;
import ngat.phase2.XLiricInstrumentConfig;
import ngat.phase2.XMultipleExposure;
import ngat.phase2.XPeriodExposure;
import ngat.phase2.XRotatorConfig;
import ngat.phase2.XSlew;
import ngat.phase2.XTarget;
import ngat.phase2.util.Rounder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author  nrc
 */
public class WizardPanelPhotometryPolarimetrySequence extends javax.swing.JPanel {

    static Logger logger = Logger.getLogger(WizardPanelPhotometryPolarimetrySequence.class);

    private IProgram program;
    private List targetList;
    private List instrumentConfigList;
    
    private ButtonGroup bgAutoguide;
    //private ButtonGroup bgAcquire;
    
    private static final String CMD_NO = "AUTOGUIDE_NO";
    private static final String CMD_YES = "AUTOGUIDE_YES";
    private static final String CMD_AUTO = "AUTOGUIDE_AUTO";

    private static final String AUTOMATIC = "Automatic (Cardinal)";
    private static final String MANUAL = "Manual";

    public static final int UNSET_ROTATOR_TYPE = -1;

    private boolean rotatorValueNeedsSetting = true;
    private String panelUsageType; // one of SelectSequenceTypePanel.PHOTOMETRY_WIZARD, SelectSequenceTypePanel.POLARIMETRY_WIZARD

    /** Creates new form MultiColourPhotomSequencePanel */
    public WizardPanelPhotometryPolarimetrySequence(IProgram program, String panelUsageType) {
        this.program = program;
        this.panelUsageType = panelUsageType;

        initComponents();
        populateAndSetUpComponents();
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

        /*
        //old code:
        try {
            double angleDegrees;
            angleDegrees = Double.parseDouble(jtfRotAngle.getText());
            return new XRotatorConfig(mode, UnitConverter.convertDegsToRads(angleDegrees));
        } catch (NumberFormatException e) {
            return null;
        }
        */

        if (panelUsageType.equals(SelectSequenceTypePanel.MULTI_COLOUR_PHOTOMETRY_WIZARD)) {
            return getRotatorConfigForPhotometry();
        } else if  (panelUsageType.equals(SelectSequenceTypePanel.POLARIMETRY_WIZARD)) {
            return getRotatorConfigForPolarimetry();
        } else {
            logger.error("unknown panel usage type: " + panelUsageType);
            return null;
        }
        
    }

    private IRotatorConfig getRotatorConfigForPhotometry() {
        int mode;
        String alignedToInstrument = "";
        String selectedRot = (String) jcbRotator.getSelectedItem();
        if (selectedRot.equalsIgnoreCase(MANUAL)) {
            mode = WizardPanelPhotometryPolarimetrySequence.UNSET_ROTATOR_TYPE;
        } else if (selectedRot.equalsIgnoreCase(AUTOMATIC)) {
            mode = IRotatorConfig.CARDINAL;
            alignedToInstrument = (String) jcbAlignedToInstrumentName.getSelectedItem();
        } else {
            JOptionPane.showMessageDialog(this, "unknown rotator mode :" + selectedRot);
            return null;
        }
        
        XRotatorConfig rotatorConfig = new XRotatorConfig();
        rotatorConfig.setRotatorMode(mode); //may be unset, i.e. PhotometricSequenceWizardPanel.UNSET_ROTATOR_TYPE
        rotatorConfig.setInstrumentName(alignedToInstrument);
        return rotatorConfig;
    }

    private IRotatorConfig getRotatorConfigForPolarimetry() {
        XRotatorConfig rotatorConfig = new XRotatorConfig();
        rotatorConfig.setRotatorMode(IRotatorConfig.MOUNT); //may be unset, i.e. PhotometricSequenceWizardPanel.UNSET_ROTATOR_TYPE
        rotatorConfig.setRotatorAngle(0);
        return rotatorConfig;
    }

    private IAutoguiderConfig getAutoguiderConfig() {
        int mode;
        String name;
        
        String actionCmd = bgAutoguide.getSelection().getActionCommand();
        if (actionCmd.equals(WizardPanelPhotometryPolarimetrySequence.CMD_YES)) {
            mode = IAutoguiderConfig.ON;
        } else if (actionCmd.equals(WizardPanelPhotometryPolarimetrySequence.CMD_NO)) {
            mode = IAutoguiderConfig.OFF;
        } else if (actionCmd.equals(WizardPanelPhotometryPolarimetrySequence.CMD_AUTO)) {
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

    public boolean rotatorValueNeedsSetting() {
        return rotatorValueNeedsSetting;
    }
    
    private void populateAndSetUpComponents() {
        //jcbSupirCamExposureTime.setVisible(false);

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

            if (panelUsageType.equals(SelectSequenceTypePanel.MULTI_COLOUR_PHOTOMETRY_WIZARD)) {
                setUpForPhotometry();
            } else if  (panelUsageType.equals(SelectSequenceTypePanel.POLARIMETRY_WIZARD)) {
                setUpForPolarimetry();
            }

        } catch (Phase2Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return;
        }
        
        //is standard tick box only visible to super users
        jcbIsStandard.setVisible(Session.getInstance().getUser().isSuperUser());

    }

    private void setUpForPhotometry() {

        setComponentsForPhotometry();
        
        //jcbObsInstrument.setModel(new javax.swing.DefaultComboBoxModel(CONST.IMAGER_INSTRUMENTS));
        //jcbAlignedToInstrumentName.setModel(new javax.swing.DefaultComboBoxModel(CONST.IMAGER_INSTRUMENTS));
            
        
        if (Session.getInstance().getUser().isSuperUser()) {
            jcbObsInstrument.setModel(new javax.swing.DefaultComboBoxModel(CONST.IMAGER_INSTRUMENTS));
            jcbAlignedToInstrumentName.setModel(new javax.swing.DefaultComboBoxModel(CONST.IMAGER_INSTRUMENTS));
        } else {
            jcbObsInstrument.setModel(new javax.swing.DefaultComboBoxModel(CONST.IMAGER_INSTRUMENTS));
            jcbAlignedToInstrumentName.setModel(new javax.swing.DefaultComboBoxModel(CONST.IMAGER_INSTRUMENTS));
        }
        
        
        //jcbObsInstrument.setModel(new javax.swing.DefaultComboBoxModel(CONST.IMAGER_INSTRUMENTS));

        jcbObsInstrument.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbObsInstrumentActionPerformed(evt);
            }
        });

        //populate instrument config list dependent upon selected instrument name
        String instrumentName = getInstrumentName();
        populateInstrumentConfigList(instrumentName);

        //directly set the rotator index to be sure, and update the related variable
        this.rotatorValueNeedsSetting = false;

        jcbRotator.setSelectedItem(AUTOMATIC);
    }
    
    private void setComponentsForPhotometry() {
        jtfExposureCount.setVisible(true);
        jlblMultiplier.setVisible(true);
        jtfExposureTime.setVisible(true);
        //jcbSupirCamExposureTime.setVisible(false);
    }

    private void setUpForPolarimetry() {

        setComponentsForPolarimetry(); 

        //hide all the rotator and instrument selection combos
        //deleted 12/11/12:
        //jlblInstrument.setVisible(false);
        jlblMultiplier.setVisible(false);
        jplRotatorContainerPanel.setVisible(false);
        
        //deleted 12/11/12:
        //jcbObsInstrument.setVisible(false);
        
        jcbObsInstrument.setModel(new javax.swing.DefaultComboBoxModel(CONST.POLARIMETER_INSTRUMENTS));
        jcbObsInstrument.setSelectedItem(CONST.MOPTOP);
        
        //added 12/11/12:
        jcbObsInstrument.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbObsInstrumentActionPerformed(evt);
            }
        });
        //deleted 12/11/12:
        //populateInstrumentConfigList(CONST.RINGO2);
        
        //added 12/11/12:
        //populate instrument config list dependent upon selected instrument name
        String instrumentName = getInstrumentName();
        populateInstrumentConfigList(instrumentName);
        
        // /
        
        //directly set the rotator index to be sure, and update the related variable
        this.rotatorValueNeedsSetting = false;

    }

    private void setComponentsForPolarimetry() {
        jtfExposureCount.setVisible(false);
        jlblMultiplier.setVisible(false);
        jtfExposureTime.setVisible(true);
        //jcbSupirCamExposureTime.setVisible(false);
    }
    
    private void populateTargetList() throws Phase2Exception {
        jcbTarget.removeAllItems();
        targetList = Phase2ModelClient.getInstance().listTargets(program.getID());
        Iterator ti = targetList.iterator();
        while (ti.hasNext()) {
            ITarget target = (ITarget) ti.next();
            String targetDescription = TargetRenderer.getShortDescription(target);
            jcbTarget.addItem(targetDescription);
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

    //populate instrumentConfigList and jcbObsInstrumentConfig object with all instr cfgs of programme that are relevant to instrument: instrumentName
    private void populateInstrumentConfigList(String instrumentName) {
        jcbObsInstrumentConfig.removeAllItems();
        //clear the form held list
        instrumentConfigList = new Vector();
        List allInstrumentConfigsList;
        try {
            //get all the configs
            allInstrumentConfigsList = Phase2ModelClient.getInstance().listInstrumentConfigs(program.getID());
            Iterator ici = allInstrumentConfigsList.iterator();
            while (ici.hasNext()) {
                XInstrumentConfig instrumentConfig = (XInstrumentConfig)ici.next();
                if (instrumentConfig.getInstrumentName() == null) {
                    instrumentConfig.setInstrumentName("NULL");
                }
                if (instrumentConfig.getInstrumentName().equalsIgnoreCase(instrumentName)) {
                    //put in cbo and form held list if the instrument is right
                    jcbObsInstrumentConfig.addItem(InstrumentConfigRenderer.getShortDescription(instrumentConfig));
                    instrumentConfigList.add(instrumentConfig);
                }
            }
        } catch (Phase2Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
        }
    }

    private int findIndexOfInstrumentConfig(IInstrumentConfig instrumentConfig) {
        //find index of instrumentConfig in  instrumentConfigList
        Iterator i = instrumentConfigList.iterator();
        int index = 0;
        while (i.hasNext()) {
            IInstrumentConfig instrumentConfigFound = (IInstrumentConfig) i.next();
            if (instrumentConfigFound.getName().equals(instrumentConfig.getName())) {
                return index;
            }
            index ++;
        }
        return -1;
    }


    private boolean validObservationSequence() {
        try {
            Integer.parseInt(jtfExposureCount.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a numeric value for exposure count");
            return false;
        }

        try {
            Double.parseDouble(jtfExposureTime.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a numeric value for exposure time");
            return false;
        }

        try {
            double defocusAmount = Double.parseDouble(jtfDefocusAmount.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a numeric value for defocus");
            return false;
        }

        try {
            double defocusAmount = Double.parseDouble(jtfDefocusAmount.getText());
            if ((defocusAmount > 10) || (defocusAmount < -10)) {
                throw new Exception("defocusAmount entered is outside legal range");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please enter a value for defocus between +10 and -10 mm");
            return false;
        }

        return true;
    }


    /*************************************/
    // OBSERVATION SEQUENCE EXTRACTION
    /*************************************/
    public ISequenceComponent getObservationSequence() 
    {
        if (!validObservationSequence()) {
            return null;
        }

        //set up root iterator
        XIteratorComponent rootComponent = new XIteratorComponent("Root", new XIteratorRepeatCountCondition(1));

        //SLEW

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

        //SETUP

        //set up autoguiderConfig off object and get autoguider config specified by user
        boolean useAutoguider;
        XAutoguiderConfig autoguiderConfigOff = new XAutoguiderConfig(IAutoguiderConfig.OFF, "AG_OFF");
        IAutoguiderConfig autoguiderConfigSpecified = getAutoguiderConfig();
        if (autoguiderConfigSpecified.getAutoguiderCommand() == IAutoguiderConfig.OFF) {
            useAutoguider = false;
        } else {
            useAutoguider = true;
        }

        //DEFOCUS
        double defocusAmount = Double.parseDouble(jtfDefocusAmount.getText());
        defocusAmount = Rounder.round(defocusAmount, 3); //round to 3 decimal places
        boolean addDefocusCommand = (defocusAmount != 0);
        if (addDefocusCommand) {
            rootComponent.addElement(new XExecutiveComponent("DEFOCUS", new XFocusOffset(false, defocusAmount)));
        }

        PhotomSeqWizardTableModel obsSequenceTableModel = (PhotomSeqWizardTableModel) jtblObsSeqWizardTable.getModel();
        ArrayList repeatConfigExposeList = obsSequenceTableModel.getData();
        Iterator i = repeatConfigExposeList.iterator();
        String exposureNamePrefix = "OBS_";
        String configNamePrefix = "CFG_";

        int lineCount = 1;
        boolean autoguiderIsOn = false;
        boolean isFirstIteration = true;
        XDark dark = null;
        XBias bias = null;
        int coaddExposureLength = 100;
        IInstrumentConfig previousInstrumentConfig = null;

        //START ITERATION -------
        while (i.hasNext()) {
            PhotometricSeqWizardTableLineEntry obsSeqTableLineEntry = (PhotometricSeqWizardTableLineEntry) i.next();

            //get the line details
            IExposure exposure = obsSeqTableLineEntry.getExposure();
            double exposureTime;
            if (exposure instanceof XMultipleExposure) {
                exposureTime = ((XMultipleExposure)exposure).getExposureTime();
            } else {
                exposureTime = ((XPeriodExposure)exposure).getExposureTime();
            }
            String instrumentName = obsSeqTableLineEntry.getInstrumentName();
            IInstrumentConfig instrumentConfig = obsSeqTableLineEntry.getInstrumentConfig();

            boolean instrumentIsSupirCam = instrumentName.equalsIgnoreCase("SUPIRCAM");
            boolean instrumentIsLiric = instrumentName.equalsIgnoreCase("LIRIC");
            boolean instrumentHasChanged;
            if (isFirstIteration) {
                instrumentHasChanged = true;
            } else {
                instrumentHasChanged = !instrumentConfig.getInstrumentName().equals(previousInstrumentConfig.getInstrumentName());
            }
            // If LIRIC, we need to retrieve the coaddExposureLength to create a DARK
            if(instrumentIsLiric)
            {
                XLiricInstrumentConfig liricConfig = (XLiricInstrumentConfig)instrumentConfig;
                
                coaddExposureLength = liricConfig.getCoaddExposureLength();
            }
            //ACQUISITION CONFIG
            if (isFirstIteration) {
                IAcquisitionConfig acquisitionConfig= new XAcquisitionConfig(IAcquisitionConfig.INSTRUMENT_CHANGE, instrumentName, null, false, IAcquisitionConfig.PRECISION_NORMAL);
                rootComponent.addElement(new XExecutiveComponent("ACQ-INST_CHANGED", acquisitionConfig));
            } else if (instrumentHasChanged) {
                //instrument has changed
                if (autoguiderIsOn) {
                    rootComponent.addElement(new XExecutiveComponent("AG_OFF", autoguiderConfigOff));
                    autoguiderIsOn = false;
                }
                IAcquisitionConfig acquisitionConfig= new XAcquisitionConfig(IAcquisitionConfig.INSTRUMENT_CHANGE, instrumentName, null, false, IAcquisitionConfig.PRECISION_NORMAL);
                rootComponent.addElement(new XExecutiveComponent("ACQ-INST_CHANGED", acquisitionConfig));
            }

            //AUTOGUIDER
            if (instrumentHasChanged) {
                if (useAutoguider && !instrumentIsSupirCam) {
                    rootComponent.addElement(new XExecutiveComponent(getAutoguiderModeAsString(), autoguiderConfigSpecified));
                    autoguiderIsOn = true;
                }
            }

            //CONFIG INSTRUMENT 
            // - if we haven't done one before or 
            // - if the instrumentConfig is different to the last one or
            // - if the instrument is LIRIC (the bias at the end of the LIRIC Multrun resets the .fmt file for the detector
            //   so a reconfig is always needed aafter a Liric Bias is taken)
            if ((previousInstrumentConfig == null) || !instrumentConfig.equals(previousInstrumentConfig) || instrumentIsLiric) 
            {
                XInstrumentConfigSelector instrumentConfigSelector = new XInstrumentConfigSelector(instrumentConfig);
                rootComponent.addElement(new XExecutiveComponent(configNamePrefix + lineCount, instrumentConfigSelector));
                previousInstrumentConfig = instrumentConfig;
            }

            //DARK - if we're using supircam
            if (instrumentIsSupirCam)
            {
                dark = new XDark();
                dark.setName("DARK");
                dark.setExposureTime(exposureTime); //dark time is in mS
                rootComponent.addElement(new XExecutiveComponent("DARK", dark));
            }
            
            //EXPOSE
            if (exposure instanceof XMultipleExposure) {
                XMultipleExposure multipleExposure = (XMultipleExposure) exposure;
                multipleExposure.setName(exposureNamePrefix + lineCount);
                multipleExposure.setStandard(jcbIsStandard.isSelected());
                rootComponent.addElement(new XExecutiveComponent(multipleExposure.getName(), exposure));
            } else {
                XPeriodExposure periodExposure = (XPeriodExposure) exposure;
                periodExposure.setName(exposureNamePrefix + lineCount);
                periodExposure.setStandard(jcbIsStandard.isSelected());
                rootComponent.addElement(new XExecutiveComponent(periodExposure.getName(), exposure));
            }

            //DARK - if we're using liric
            // We must have the DARK after the exposure with LIRIC, 
            // as the DARK command internally moves the filter to AuMirror
            if (instrumentIsLiric) 
            {
                dark = new XDark();
                dark.setName("DARK");
                dark.setExposureTime(coaddExposureLength); //dark time is one coadd in mS
                rootComponent.addElement(new XExecutiveComponent("DARK", dark));
            }

            //DARK - if we're using supircam
            if (instrumentIsSupirCam) {
                //add the dark again
                rootComponent.addElement(new XExecutiveComponent("DARK", dark));
            }
            
            // BIAS - if we're using liric
            if (instrumentIsLiric) 
            {
                bias = new XBias();
                bias.setName("BIAS");
                rootComponent.addElement(new XExecutiveComponent("BIAS", bias));
            }

            lineCount ++;
            isFirstIteration = false;
        }

        //END OF ITERATION -------

        //AUTOGUIDER - off if it's on
        if (autoguiderIsOn) {
            //add an autoguider off command
            rootComponent.addElement(new XExecutiveComponent("AG_OFF", autoguiderConfigOff));
            autoguiderIsOn = false;
        }

        return rootComponent;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpObsSeqStartPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jcbTarget = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jbtnCreateNewTarget = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jrbAutoguideYes = new javax.swing.JRadioButton();
        jrbAutoguideNo = new javax.swing.JRadioButton();
        jrbAutoguideAuto = new javax.swing.JRadioButton();
        jcbIsStandard = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        jtfDefocusAmount = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jplRotatorContainerPanel = new javax.swing.JPanel();
        jlblRotator = new javax.swing.JLabel();
        jcbRotator = new javax.swing.JComboBox();
        jlblAlignedTo = new javax.swing.JLabel();
        jcbAlignedToInstrumentName = new javax.swing.JComboBox();
        jpAddObs = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jcbObsInstrumentConfig = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        jbtnCreateNewInstrumentConfig = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jtfExposureCount = new javax.swing.JTextField();
        jlblMultiplier = new javax.swing.JLabel();
        jbAddExposure = new javax.swing.JButton();
        jplExposureCountContainer = new javax.swing.JPanel();
        jplExposureCountContainer.setLayout(new BorderLayout());
        jtfExposureTime = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jlblInstrument = new javax.swing.JLabel();
        jcbObsInstrument = new javax.swing.JComboBox();
        jpObsTablePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtblObsSeqWizardTable = new javax.swing.JTable();
        jbtnDelRow = new javax.swing.JButton();
        jbtnUp = new javax.swing.JButton();
        jbtnDown = new javax.swing.JButton();

        jpObsSeqStartPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Start Configuration"));

        jLabel2.setText("Select Target:");

        jcbTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbTargetActionPerformed(evt);
            }
        });

        jLabel3.setText("or");

        jbtnCreateNewTarget.setText("New");
        jbtnCreateNewTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCreateNewTargetActionPerformed(evt);
            }
        });

        jLabel6.setText("Autoguide:");

        jrbAutoguideYes.setText("Yes");

        jrbAutoguideNo.setText("Off");

        jrbAutoguideAuto.setSelected(true);
        jrbAutoguideAuto.setText("On if available");

        jcbIsStandard.setText("Is Standard");

        jLabel7.setText("Defocus:");

        jtfDefocusAmount.setText("0.0");

        jLabel8.setText("mm (non-cumulative)");

        jlblRotator.setText("Rotator Setting:");

        jcbRotator.setModel(new javax.swing.DefaultComboBoxModel(new String[] { AUTOMATIC, MANUAL }));
        jcbRotator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbRotatorActionPerformed(evt);
            }
        });

        jlblAlignedTo.setText("Aligned to:");

        jcbAlignedToInstrumentName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbAlignedToInstrumentNameActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jplRotatorContainerPanelLayout = new org.jdesktop.layout.GroupLayout(jplRotatorContainerPanel);
        jplRotatorContainerPanel.setLayout(jplRotatorContainerPanelLayout);
        jplRotatorContainerPanelLayout.setHorizontalGroup(
            jplRotatorContainerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jplRotatorContainerPanelLayout.createSequentialGroup()
                .add(jlblRotator)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jcbRotator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 223, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(47, 47, 47)
                .add(jlblAlignedTo)
                .add(18, 18, 18)
                .add(jcbAlignedToInstrumentName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 183, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(132, Short.MAX_VALUE))
        );
        jplRotatorContainerPanelLayout.setVerticalGroup(
            jplRotatorContainerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jplRotatorContainerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jlblRotator)
                .add(jcbRotator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jlblAlignedTo)
                .add(jcbAlignedToInstrumentName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout jpObsSeqStartPanelLayout = new org.jdesktop.layout.GroupLayout(jpObsSeqStartPanel);
        jpObsSeqStartPanel.setLayout(jpObsSeqStartPanelLayout);
        jpObsSeqStartPanelLayout.setHorizontalGroup(
            jpObsSeqStartPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpObsSeqStartPanelLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(jpObsSeqStartPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jpObsSeqStartPanelLayout.createSequentialGroup()
                        .add(jpObsSeqStartPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(jLabel6)
                            .add(jLabel7))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jpObsSeqStartPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jpObsSeqStartPanelLayout.createSequentialGroup()
                                .add(jcbTarget, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 509, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jLabel3)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jbtnCreateNewTarget))
                            .add(jpObsSeqStartPanelLayout.createSequentialGroup()
                                .add(jtfDefocusAmount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 67, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel8))
                            .add(jpObsSeqStartPanelLayout.createSequentialGroup()
                                .add(jrbAutoguideYes)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jrbAutoguideNo)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jrbAutoguideAuto)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jcbIsStandard)
                                .add(48, 48, 48))))
                    .add(jplRotatorContainerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpObsSeqStartPanelLayout.setVerticalGroup(
            jpObsSeqStartPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpObsSeqStartPanelLayout.createSequentialGroup()
                .add(jpObsSeqStartPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jcbTarget, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jbtnCreateNewTarget)
                    .add(jLabel3))
                .add(4, 4, 4)
                .add(jplRotatorContainerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jpObsSeqStartPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jpObsSeqStartPanelLayout.createSequentialGroup()
                        .add(jLabel6)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jLabel7))
                    .add(jpObsSeqStartPanelLayout.createSequentialGroup()
                        .add(jpObsSeqStartPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jrbAutoguideYes)
                            .add(jrbAutoguideNo)
                            .add(jrbAutoguideAuto)
                            .add(jcbIsStandard))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jpObsSeqStartPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jtfDefocusAmount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel8)))))
        );

        jpAddObs.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Add Configuration / Exposure"));

        jLabel11.setText("Instrument Config:");

        jLabel12.setText("or");

        jbtnCreateNewInstrumentConfig.setText("New");
        jbtnCreateNewInstrumentConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCreateNewInstrumentConfigActionPerformed(evt);
            }
        });

        jLabel13.setText("Exposure:");

        jtfExposureCount.setText("1");

        jlblMultiplier.setText("x");

        jbAddExposure.setText("Add to Observation List");
        jbAddExposure.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbAddExposureActionPerformed(evt);
            }
        });

        jtfExposureTime.setText("01");

        jLabel14.setText("seconds");

        org.jdesktop.layout.GroupLayout jplExposureCountContainerLayout = new org.jdesktop.layout.GroupLayout(jplExposureCountContainer);
        jplExposureCountContainer.setLayout(jplExposureCountContainerLayout);
        jplExposureCountContainerLayout.setHorizontalGroup(
            jplExposureCountContainerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jplExposureCountContainerLayout.createSequentialGroup()
                .add(jtfExposureTime, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel14)
                .add(20, 20, 20))
        );
        jplExposureCountContainerLayout.setVerticalGroup(
            jplExposureCountContainerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jplExposureCountContainerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jtfExposureTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jLabel14))
        );

        jlblInstrument.setText("Instrument:");

        jcbObsInstrument.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbObsInstrumentActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jlblInstrument)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jcbObsInstrument, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 159, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jcbObsInstrument, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jlblInstrument))
        );

        org.jdesktop.layout.GroupLayout jpAddObsLayout = new org.jdesktop.layout.GroupLayout(jpAddObs);
        jpAddObs.setLayout(jpAddObsLayout);
        jpAddObsLayout.setHorizontalGroup(
            jpAddObsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpAddObsLayout.createSequentialGroup()
                .addContainerGap()
                .add(jpAddObsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jpAddObsLayout.createSequentialGroup()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(605, 605, 605))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jpAddObsLayout.createSequentialGroup()
                        .add(jpAddObsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jpAddObsLayout.createSequentialGroup()
                                .add(jLabel13)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jtfExposureCount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 53, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jlblMultiplier)
                                .add(9, 9, 9)
                                .add(jplExposureCountContainer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jbAddExposure))
                            .add(jpAddObsLayout.createSequentialGroup()
                                .add(jLabel11)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jcbObsInstrumentConfig, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel12)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jbtnCreateNewInstrumentConfig)))
                        .add(249, 249, 249))))
        );
        jpAddObsLayout.setVerticalGroup(
            jpAddObsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpAddObsLayout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jpAddObsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jpAddObsLayout.createSequentialGroup()
                        .add(8, 8, 8)
                        .add(jLabel11))
                    .add(jpAddObsLayout.createSequentialGroup()
                        .add(1, 1, 1)
                        .add(jpAddObsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(jcbObsInstrumentConfig, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel12)
                            .add(jbtnCreateNewInstrumentConfig))))
                .add(jpAddObsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jpAddObsLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 24, Short.MAX_VALUE)
                        .add(jbAddExposure))
                    .add(jpAddObsLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jpAddObsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(jLabel13)
                            .add(jtfExposureCount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jlblMultiplier)
                            .add(jplExposureCountContainer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jpObsTablePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Observation List"));

        jtblObsSeqWizardTable.setModel(new ngat.oss.client.gui.model.PhotomSeqWizardTableModel());
        jtblObsSeqWizardTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jtblObsSeqWizardTable);

        jbtnDelRow.setText("Delete");
        jbtnDelRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDelRowActionPerformed(evt);
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

        org.jdesktop.layout.GroupLayout jpObsTablePanelLayout = new org.jdesktop.layout.GroupLayout(jpObsTablePanel);
        jpObsTablePanel.setLayout(jpObsTablePanelLayout);
        jpObsTablePanelLayout.setHorizontalGroup(
            jpObsTablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jpObsTablePanelLayout.createSequentialGroup()
                .add(jScrollPane1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jpObsTablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jbtnDown)
                    .add(jbtnUp)
                    .add(jbtnDelRow)))
        );

        jpObsTablePanelLayout.linkSize(new java.awt.Component[] {jbtnDown, jbtnUp}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jpObsTablePanelLayout.setVerticalGroup(
            jpObsTablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jpObsTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jbtnUp)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jbtnDown)
                .add(24, 24, 24)
                .add(jbtnDelRow))
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jpObsTablePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jpAddObs, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jpObsSeqStartPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jpObsSeqStartPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jpAddObs, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jpObsTablePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void jbAddExposureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbAddExposureActionPerformed

    if (validObservationSequence()) {
        String instrumentName = getInstrumentName();

        int iConfigIndex = jcbObsInstrumentConfig.getSelectedIndex();
        if (iConfigIndex == -1) {
            return;
        }

        PhotomSeqWizardTableModel obsSequenceTableModel = (PhotomSeqWizardTableModel) jtblObsSeqWizardTable.getModel();

        IInstrumentConfig instrumentConfig = (IInstrumentConfig) instrumentConfigList.get(iConfigIndex);
        IExposure exposure;
        /*
        if (instrumentConfig.getInstrumentName().equals(CONST.RINGO2)) {
            double exposureTime = Double.parseDouble(jtfExposureTime.getText().trim());
            exposureTime *= 1000;
            boolean isStandard = jcbIsStandard.isSelected();
            XPeriodExposure periodExposure = new XPeriodExposure(exposureTime, isStandard);
            exposure = periodExposure;
        } else */
        if ((instrumentConfig.getInstrumentName().equals(CONST.RINGO3))||
                (instrumentConfig.getInstrumentName().equals(CONST.MOPTOP))) 
        {
            double exposureTime = Double.parseDouble(jtfExposureTime.getText().trim());
            exposureTime *= 1000;
            boolean isStandard = jcbIsStandard.isSelected();
            XPeriodExposure periodExposure = new XPeriodExposure(exposureTime, isStandard);
            exposure = periodExposure;
        } 
        else 
        {
            double exposureTime;
            
            exposureTime = Double.parseDouble(jtfExposureTime.getText().trim());
            exposureTime *= 1000;
            int exposureCount = Integer.parseInt(jtfExposureCount.getText().trim());
            boolean isStandard = jcbIsStandard.isSelected();
            XMultipleExposure multipleExposure = new XMultipleExposure(exposureTime, exposureCount, isStandard);
            exposure = multipleExposure;
        }
        PhotometricSeqWizardTableLineEntry obsSeqTableLineEntry = new PhotometricSeqWizardTableLineEntry(instrumentName, instrumentConfig, exposure);
        obsSequenceTableModel.addTableLineEntry(obsSeqTableLineEntry);
        jtblObsSeqWizardTable.updateUI();
    }
}//GEN-LAST:event_jbAddExposureActionPerformed

private String getInstrumentName() {
    String instrumentName = (String) jcbObsInstrument.getSelectedItem();

    //deleted 12/11/12:
    /*
    if (instrumentName == null) {
        if (panelUsageType.equals(SelectSequenceTypePanel.POLARIMETRY_WIZARD)) {
            instrumentName = CONST.RINGO2;
            might be ringo3
        } else {
            JOptionPane.showMessageDialog(this, "An error occurred, the instrument combo box does not contain a valid instrument name.");
            return "UNKNOWN";
        }
    }
    */
    return instrumentName;
}

private void jcbObsInstrumentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbObsInstrumentActionPerformed

    String instrumentName = getInstrumentName();

    //boolean isSupirCam = getInstrumentName().equals(CONST.SUPIRCAM);
    //boolean isRingo2 = getInstrumentName().equals(CONST.RINGO2);

    /*
    if (isSupirCam) {
        jtfExposureCount.setVisible(true);
        jlblMultiplier.setVisible(true);
        jtfExposureTime.setVisible(false);
        jcbSupirCamExposureTime.setVisible(true);
    
    } else {
    */
    if (panelUsageType.equals(SelectSequenceTypePanel.MULTI_COLOUR_PHOTOMETRY_WIZARD)) {
        setComponentsForPhotometry();
    } else if  (panelUsageType.equals(SelectSequenceTypePanel.POLARIMETRY_WIZARD)) {
        setComponentsForPolarimetry();
    }
        
    //}

    //populate instrument config list dependent upon selected instrument name
    populateInstrumentConfigList(instrumentName);
}//GEN-LAST:event_jcbObsInstrumentActionPerformed

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

private void jbtnCreateNewInstrumentConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCreateNewInstrumentConfigActionPerformed

    String instrumentName = getInstrumentName();

    NewInstrumentConfigDialog dialog = new NewInstrumentConfigDialog(true, instrumentName);
    dialog.setVisible(true);

    //blocks

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
        //populate instrument config list dependent upon selected instrument name
        instrumentName = getInstrumentName();
        populateInstrumentConfigList(instrumentName);
        //select new instrument config in combo box
        jcbObsInstrumentConfig.setSelectedIndex(findIndexOfInstrumentConfig(instrumentConfig));
    } catch (Phase2Exception ex) {
        ex.printStackTrace();
        logger.error(ex);
        JOptionPane.showMessageDialog(this, ex.getMessage());
    }
}//GEN-LAST:event_jbtnCreateNewInstrumentConfigActionPerformed

private void jbtnUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnUpActionPerformed
    int selectedRow = jtblObsSeqWizardTable.getSelectedRow();
    if (selectedRow < 0) {
        return;
    }
    PhotomSeqWizardTableModel obsSequenceTableModel = (PhotomSeqWizardTableModel) jtblObsSeqWizardTable.getModel();
    int newRow = obsSequenceTableModel.moveRowUp(selectedRow);
    jtblObsSeqWizardTable.getSelectionModel().setSelectionInterval(newRow, newRow);
    jtblObsSeqWizardTable.updateUI();
}//GEN-LAST:event_jbtnUpActionPerformed

private void jbtnDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDownActionPerformed
    int selectedRow = jtblObsSeqWizardTable.getSelectedRow();
    if (selectedRow < 0) {
        return;
    }
    PhotomSeqWizardTableModel obsSequenceTableModel = (PhotomSeqWizardTableModel) jtblObsSeqWizardTable.getModel();
    int newRow = obsSequenceTableModel.moveRowDown(selectedRow);
    jtblObsSeqWizardTable.getSelectionModel().setSelectionInterval(newRow, newRow);
    jtblObsSeqWizardTable.updateUI();
}//GEN-LAST:event_jbtnDownActionPerformed

private void jbtnDelRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDelRowActionPerformed
    int selectedRow = jtblObsSeqWizardTable.getSelectedRow();
    if (selectedRow < 0) {
        return;
    }
    PhotomSeqWizardTableModel obsSequenceTableModel = (PhotomSeqWizardTableModel) jtblObsSeqWizardTable.getModel();
    obsSequenceTableModel.deleteRow(selectedRow);
    jtblObsSeqWizardTable.updateUI();
}//GEN-LAST:event_jbtnDelRowActionPerformed

private void jcbTargetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbTargetActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jcbTargetActionPerformed

private void jcbRotatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbRotatorActionPerformed
    String selectedRot = (String) jcbRotator.getSelectedItem();
    if (selectedRot.equalsIgnoreCase(MANUAL)) {
        rotatorValueNeedsSetting = true;
        jlblAlignedTo.setVisible(false);
        jcbAlignedToInstrumentName.setVisible(false);
    } else if (selectedRot.equalsIgnoreCase(AUTOMATIC)) {
        rotatorValueNeedsSetting = false;
        jlblAlignedTo.setVisible(true);
        jcbAlignedToInstrumentName.setVisible(true);
    } else {
        JOptionPane.showMessageDialog(this, "unknown rotator mode :" + selectedRot);
        rotatorValueNeedsSetting = true;
    }
}//GEN-LAST:event_jcbRotatorActionPerformed

private void jcbAlignedToInstrumentNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbAlignedToInstrumentNameActionPerformed

}//GEN-LAST:event_jcbAlignedToInstrumentNameActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbAddExposure;
    private javax.swing.JButton jbtnCreateNewInstrumentConfig;
    private javax.swing.JButton jbtnCreateNewTarget;
    private javax.swing.JButton jbtnDelRow;
    private javax.swing.JButton jbtnDown;
    private javax.swing.JButton jbtnUp;
    private javax.swing.JComboBox jcbAlignedToInstrumentName;
    private javax.swing.JCheckBox jcbIsStandard;
    private javax.swing.JComboBox jcbObsInstrument;
    private javax.swing.JComboBox jcbObsInstrumentConfig;
    private javax.swing.JComboBox jcbRotator;
    private javax.swing.JComboBox jcbTarget;
    private javax.swing.JLabel jlblAlignedTo;
    private javax.swing.JLabel jlblInstrument;
    private javax.swing.JLabel jlblMultiplier;
    private javax.swing.JLabel jlblRotator;
    private javax.swing.JPanel jpAddObs;
    private javax.swing.JPanel jpObsSeqStartPanel;
    private javax.swing.JPanel jpObsTablePanel;
    private javax.swing.JPanel jplExposureCountContainer;
    private javax.swing.JPanel jplRotatorContainerPanel;
    private javax.swing.JRadioButton jrbAutoguideAuto;
    private javax.swing.JRadioButton jrbAutoguideNo;
    private javax.swing.JRadioButton jrbAutoguideYes;
    private javax.swing.JTable jtblObsSeqWizardTable;
    private javax.swing.JTextField jtfDefocusAmount;
    private javax.swing.JTextField jtfExposureCount;
    private javax.swing.JTextField jtfExposureTime;
    // End of variables declaration//GEN-END:variables
}
