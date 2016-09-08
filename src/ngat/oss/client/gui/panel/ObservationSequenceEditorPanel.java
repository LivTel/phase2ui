/*
 * MultiColourPhotomSequencePanel.java
 *
 * Created on June 11, 2009, 12:36 PM
 */
package ngat.oss.client.gui.panel;

import ngat.oss.client.gui.panel.sequencepanels.DefocusPanel;
import ngat.oss.client.gui.panel.sequencepanels.InstrumentConfigSelectorPanel;
import ngat.oss.client.gui.panel.sequencepanels.FineTunePanel;
import ngat.oss.client.gui.panel.sequencepanels.DualBeamSpecInstrumentConfigSelectorPanel;
import ngat.oss.client.gui.panel.sequencepanels.PositionOffsetPanel;
import ngat.oss.client.gui.panel.sequencepanels.BranchPanel;
import ngat.oss.client.gui.panel.sequencepanels.FocalPlanePanel;
import ngat.oss.client.gui.panel.sequencepanels.ExposurePanel;
import ngat.oss.client.gui.panel.sequencepanels.CalibrationPanel;
import ngat.oss.client.gui.panel.sequencepanels.IteratorPanel;
import ngat.oss.client.gui.panel.sequencepanels.SlewPanel;
import ngat.oss.client.gui.panel.sequencepanels.AutoguidePanel;
import ngat.oss.client.gui.tree.sequencetree.ObservationSequenceTreePanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import ngat.oss.client.gui.frame.ValidationFrame;
import ngat.oss.client.gui.listeners.SequenceComponentSelectionListener;
import ngat.oss.client.gui.panel.sequencepanels.RotatorPanel;
import ngat.oss.client.gui.reference.CONST;
import ngat.oss.client.gui.util.FrameCenterer;
import ngat.phase2.IAcquisitionConfig;
import ngat.phase2.IExecutiveAction;
import ngat.phase2.IExposure;
import ngat.phase2.IGroup;
import ngat.phase2.IProgram;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.XAcquisitionConfig;
import ngat.phase2.XAutoguiderConfig;
import ngat.phase2.XBeamSteeringConfig;
import ngat.phase2.XBranchComponent;
import ngat.phase2.XCalibration;
import ngat.phase2.XExecutiveComponent;
import ngat.phase2.XFocusControl;
import ngat.phase2.XFocusOffset;
import ngat.phase2.XInstrumentConfigSelector;
import ngat.phase2.XIteratorComponent;
import ngat.phase2.XPositionOffset;
import ngat.phase2.XProgram;
import ngat.phase2.XRotatorConfig;
import ngat.phase2.XSlew;
import ngat.phase2.XTipTiltAbsoluteOffset;
import org.apache.log4j.Logger;

/**
 *
 * @author  nrc
 */
public class ObservationSequenceEditorPanel extends javax.swing.JPanel implements SequenceComponentSelectionListener {

    static Logger logger = Logger.getLogger(ObservationSequenceEditorPanel.class);

    private static final String REPLACE = "Replace";
    private static final String ADD = "Add";
    private static final String OFF = "Off";

    private IProgram program;
    private IGroup group;
    private ObservationSequenceTreePanel observationSequenceTreePanel;
    private String buttonMode = ADD; //start state
    private static final Dimension SIZE = new Dimension(864, 596);

    boolean showDebugButton = false;

    /**
     * 
     * @param group 
     * @param program
     * @param isNewSequence Whether the sequence should be blank
     */
    public ObservationSequenceEditorPanel(IGroup group, IProgram program, boolean isNewSequence) {
        this.program = program;
        this.group = group;
        
        initComponents();
        if (isNewSequence) {
            enableNewComponentButtons(true);
            populateComponents(null);
        } else {
            populateComponents(group);
        }
               
        setSize(SIZE);
       
        setButtonMode(OFF);
    }

    private void populateComponents(final IGroup group) {
        
        observationSequenceTreePanel = new ObservationSequenceTreePanel(group, ObservationSequenceEditorPanel.this);
        observationSequenceTreePanel.setPreferredSize(new Dimension(640, 250));

        observationSequenceTreePanel.setOpaque(true);
        observationSequenceTreePanel.expandAll(true);
        jplTreePanelContainer.add(observationSequenceTreePanel, BorderLayout.CENTER);
        jplTreePanelContainer.validate();
        jplTreePanelContainer.repaint();
    }

    private void enableNewComponentButtons(boolean enable) {

        //pointing, acquisition and guidance buttons
       jbtnSlew.setEnabled(enable);
       jbtnFocalPlane.setEnabled(enable);
       jbtnPositionOffset.setEnabled(enable);
       jbtnFineTune.setEnabled(enable);
       jbtnRotator.setEnabled(enable);
       jbtnAutoguide.setEnabled(enable);

       //observation / instrument control
       jbtnInstrumentConfig.setEnabled(enable);
       jbtnDefocus.setEnabled(enable);
       jbtnExpose.setEnabled(enable);
       jbtnCalibration.setEnabled(enable);

       //control flow / group structure
       jbtnBranch.setEnabled(enable);
       jbtnIterator.setEnabled(enable);

    }

    private void enableSequenceEditButtons(boolean enable) {
        jbtnEdit.setEnabled(enable);
        jbtnDelete.setEnabled(enable);
        jbtnUp.setEnabled(enable);
        jbtnDown.setEnabled(enable);
    }

    private void enableTreePanel(boolean enabled) {
        observationSequenceTreePanel.setEnabled(enabled);
    }
    
    //receives selections of components
    //SequenceComponentSelectionListener
    public void receiveComponentSelected(ISequenceComponent sequenceComponent) {

        //enable all side panel buttons
        enableNewComponentButtons(true);
        enableSequenceEditButtons(true);

        //used to chance state of gui dependent upon selected component

        if (sequenceComponent.getComponentName().equalsIgnoreCase("Root")) {
            //disable editing buttons
            enableSequenceEditButtons(false);
            return;
        }
       
        if (sequenceComponent instanceof XBranchComponent) {
            //disable edit button
            jbtnEdit.setEnabled(false);
            XBranchComponent sequenceComponentAsBranchComponent = (XBranchComponent) sequenceComponent;
            if (sequenceComponentAsBranchComponent.canAddMoreChildren()) {
                //disable new component buttons except iterator button
                enableNewComponentButtons(false);
                jbtnIterator.setEnabled(true);
            } else {
                //disable all new component buttons
                enableNewComponentButtons(false);
            }
            return;
            
        } else if (sequenceComponent instanceof XIteratorComponent) {
            XIteratorComponent sequenceComponentAsIteratorComponent = (XIteratorComponent)sequenceComponent;
            String componentName = sequenceComponentAsIteratorComponent.getComponentName();
            if (componentName.equals(CONST.FRODO_BLUE) || componentName.equals(CONST.FRODO_RED)) {
                //disable edit buttons
                enableSequenceEditButtons(false);
            } 
        } 
        
    }
    
    /**
     * Returns error message if error, else returns null
     * @return error message or null
     */
    public String validateObservationSequence() {
        String errorMessage = null;
        try {
            observationSequenceTreePanel.getObservationSequence();
            return null;
        } catch (Exception ex) {
           errorMessage = ex.getMessage();
           return errorMessage;
        }
    }

    public ISequenceComponent getObservationSequence() {
        try {
            ISequenceComponent sequenceComponent = observationSequenceTreePanel.getObservationSequence();

            return sequenceComponent;
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            return null;
        }
    }

    private void showComponentPanel(final JPanel displayPanel) {

        final JPanel detailPanel = jpComponentDetailPanel;
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                detailPanel.removeAll();
                if (displayPanel != null) {
                    detailPanel.add(displayPanel, BorderLayout.CENTER);
                }
                detailPanel.validate();
                detailPanel.repaint();
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

        jpComponentDetailPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jbtnAddItem = new javax.swing.JButton();
        jbtnCancel = new javax.swing.JButton();
        jplTreePanelContainer = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jbtnCalibration = new javax.swing.JButton();
        jbtnExpose = new javax.swing.JButton();
        jbtnAutoguide = new javax.swing.JButton();
        jbtnDefocus = new javax.swing.JButton();
        jbtnInstrumentConfig = new javax.swing.JButton();
        jbtnFineTune = new javax.swing.JButton();
        jbtnRotator = new javax.swing.JButton();
        jbtnSlew = new javax.swing.JButton();
        jbtnPositionOffset = new javax.swing.JButton();
        jbtnFocalPlane = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jbtnIterator = new javax.swing.JButton();
        jbtnBranch = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jbtnDelete = new javax.swing.JButton();
        jbtnDown = new javax.swing.JButton();
        jbtnUp = new javax.swing.JButton();
        jbtnEdit = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jbtnValidate = new javax.swing.JButton();

        jpComponentDetailPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jpComponentDetailPanel.setLayout(new java.awt.CardLayout());

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jbtnAddItem.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jbtnAddItem.setText("Add");
        jbtnAddItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAddItemActionPerformed(evt);
            }
        });

        jbtnCancel.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jbtnCancel.setText("Cancel");
        jbtnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCancelActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(204, 204, 204)
                .add(jbtnAddItem, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 119, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jbtnCancel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 119, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(231, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jbtnAddItem)
                .add(jbtnCancel))
        );

        jplTreePanelContainer.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jplTreePanelContainer.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "All Instruments", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 10))); // NOI18N

        jbtnCalibration.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jbtnCalibration.setText("Calibration");
        jbtnCalibration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCalibrationActionPerformed(evt);
            }
        });

        jbtnExpose.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jbtnExpose.setText("Expose");
        jbtnExpose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnExposeActionPerformed(evt);
            }
        });

        jbtnAutoguide.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jbtnAutoguide.setText("Autoguide");
        jbtnAutoguide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAutoguideActionPerformed(evt);
            }
        });

        jbtnDefocus.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jbtnDefocus.setText("Defocus");
        jbtnDefocus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDefocusActionPerformed(evt);
            }
        });

        jbtnInstrumentConfig.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jbtnInstrumentConfig.setText("Instrument Config");
        jbtnInstrumentConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnInstrumentConfigActionPerformed(evt);
            }
        });

        jbtnFineTune.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jbtnFineTune.setText("Fine Tune");
        jbtnFineTune.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnFineTuneActionPerformed(evt);
            }
        });

        jbtnRotator.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jbtnRotator.setText("Rotator");
        jbtnRotator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnRotatorActionPerformed(evt);
            }
        });

        jbtnSlew.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jbtnSlew.setText("Slew");
        jbtnSlew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSlewActionPerformed(evt);
            }
        });

        jbtnPositionOffset.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jbtnPositionOffset.setText("Position Offset");
        jbtnPositionOffset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnPositionOffsetActionPerformed(evt);
            }
        });

        jbtnFocalPlane.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jbtnFocalPlane.setText("Focal Plane");
        jbtnFocalPlane.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnFocalPlaneActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jbtnFocalPlane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jbtnCalibration, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jbtnPositionOffset, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jbtnExpose, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jbtnAutoguide, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jbtnDefocus, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jbtnInstrumentConfig, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                    .add(jbtnFineTune, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jbtnRotator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jbtnSlew, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel4Layout.linkSize(new java.awt.Component[] {jbtnAutoguide, jbtnCalibration, jbtnDefocus, jbtnExpose, jbtnFineTune, jbtnFocalPlane, jbtnInstrumentConfig, jbtnPositionOffset, jbtnRotator, jbtnSlew}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jbtnSlew, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jbtnFocalPlane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jbtnFineTune, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jbtnPositionOffset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jbtnRotator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jbtnAutoguide, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jbtnInstrumentConfig, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jbtnDefocus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jbtnExpose, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jbtnCalibration, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel4Layout.linkSize(new java.awt.Component[] {jbtnAutoguide, jbtnFineTune, jbtnFocalPlane, jbtnPositionOffset, jbtnRotator, jbtnSlew}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jPanel4Layout.linkSize(new java.awt.Component[] {jbtnCalibration, jbtnDefocus, jbtnExpose, jbtnInstrumentConfig}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Flow Control", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 10))); // NOI18N

        jbtnIterator.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jbtnIterator.setText("Iterator");
        jbtnIterator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnIteratorActionPerformed(evt);
            }
        });

        jbtnBranch.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jbtnBranch.setText("Branch");
        jbtnBranch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnBranchActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jbtnBranch, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jbtnIterator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jbtnIterator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jbtnBranch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Sequence Editing", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 10))); // NOI18N

        jbtnDelete.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jbtnDelete.setText("Delete");
        jbtnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDeleteActionPerformed(evt);
            }
        });

        jbtnDown.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jbtnDown.setText("Down");
        jbtnDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDownActionPerformed(evt);
            }
        });

        jbtnUp.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jbtnUp.setText("Up");
        jbtnUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnUpActionPerformed(evt);
            }
        });

        jbtnEdit.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jbtnEdit.setText("Edit");
        jbtnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnEditActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jbtnUp, 0, 0, Short.MAX_VALUE)
                    .add(jbtnEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 64, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jbtnDown, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .add(jbtnDelete, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel6Layout.linkSize(new java.awt.Component[] {jbtnDelete, jbtnDown, jbtnEdit, jbtnUp}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(jbtnEdit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jbtnUp, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(jbtnDelete, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jbtnDown, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6Layout.linkSize(new java.awt.Component[] {jbtnDelete, jbtnDown, jbtnEdit, jbtnUp}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Validation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 10))); // NOI18N

        jbtnValidate.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jbtnValidate.setText("Validate");
        jbtnValidate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnValidateActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jbtnValidate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(jbtnValidate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel6, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(106, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jplTreePanelContainer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE)
                    .add(jpComponentDetailPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(6, 6, 6)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(jpComponentDetailPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 233, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jplTreePanelContainer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void jbtnRotatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnRotatorActionPerformed
    showComponentPanel(new RotatorPanel());
    setButtonMode(ADD);
}//GEN-LAST:event_jbtnRotatorActionPerformed

private void jbtnAutoguideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAutoguideActionPerformed

    showComponentPanel(new AutoguidePanel());
    setButtonMode(ADD);
}//GEN-LAST:event_jbtnAutoguideActionPerformed

private void jbtnDefocusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDefocusActionPerformed

    showComponentPanel(new DefocusPanel());
    setButtonMode(ADD);
}//GEN-LAST:event_jbtnDefocusActionPerformed

private void jbtnInstrumentConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnInstrumentConfigActionPerformed

    XIteratorComponent selectedBranchIterator = observationSequenceTreePanel.getSelectedBranchIterator();
    
    if (selectedBranchIterator == null) {
        //we're not in a branch, show a standard InstrumentConfigSelectorPanel
        showComponentPanel(new InstrumentConfigSelectorPanel(program));
        setButtonMode(ADD);
    } else {
        String branchName = selectedBranchIterator.getComponentName();
        if (branchName.equalsIgnoreCase(CONST.FRODO_BLUE)) {
            showComponentPanel(new DualBeamSpecInstrumentConfigSelectorPanel(CONST.FRODO_BLUE, program));
            setButtonMode(ADD);
        } else if (branchName.equalsIgnoreCase(CONST.FRODO_RED)) {
            showComponentPanel(new DualBeamSpecInstrumentConfigSelectorPanel(CONST.FRODO_RED, program));
            setButtonMode(ADD);
        } else {
            JOptionPane.showMessageDialog(this, "Non-implemented instrument config selection for :" + branchName);
        }
    }

}//GEN-LAST:event_jbtnInstrumentConfigActionPerformed

private void jbtnExposeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExposeActionPerformed

    showComponentPanel(new ExposurePanel());
    setButtonMode(ADD);
}//GEN-LAST:event_jbtnExposeActionPerformed

private void jbtnFocalPlaneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnFocalPlaneActionPerformed
    showComponentPanel(new FocalPlanePanel());
    setButtonMode(ADD);
}//GEN-LAST:event_jbtnFocalPlaneActionPerformed

private void jbtnAddItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddItemActionPerformed

    if (jpComponentDetailPanel.getComponents().length == 0) {
        return;
    }

    JPanel componentPanel = (JPanel) jpComponentDetailPanel.getComponent(0);
    try {
        if (!(componentPanel instanceof SequenceComponentPanel)) {
            throw new Exception("panel in jpComponentDetailPanel is not a SequenceComponentPanel");
        }
    } catch (Exception ex) {
        ex.printStackTrace();
        logger.error(ex);
        return;
    }

    SequenceComponentPanel sequenceComponentPanel = (SequenceComponentPanel) componentPanel;
    ISequenceComponent sequenceComponent = sequenceComponentPanel.getSequenceComponent();

    if (buttonMode.equals(ADD)) {
        observationSequenceTreePanel.addSequenceComponent(sequenceComponent);

        showComponentPanel(null);
        setButtonMode(OFF);
    } else {
        //buttonMode.equals(REPLACE);
        observationSequenceTreePanel.replaceSelectedSequenceComponent(sequenceComponent);
        
        showComponentPanel(null);
        setButtonMode(OFF);
    }
}//GEN-LAST:event_jbtnAddItemActionPerformed

private void setButtonMode(String buttonMode) {
    this.buttonMode = buttonMode;
    
    jbtnAddItem.setText(buttonMode);
    if (buttonMode.equals(REPLACE)) {
        jbtnAddItem.setVisible(true);
        jbtnCancel.setVisible(true);
        enableNewComponentButtons(false);
        enableSequenceEditButtons(false);
        enableTreePanel(false);

    } else if (buttonMode.equals(ADD)) { //ADD
        jbtnAddItem.setVisible(true);
        jbtnCancel.setVisible(true);
        enableNewComponentButtons(true);
        enableSequenceEditButtons(true);
        enableTreePanel(true);
        
    } else {
        //OFF
        jbtnAddItem.setVisible(false);
        jbtnCancel.setVisible(false);
        enableNewComponentButtons(true);
        enableSequenceEditButtons(true);
        enableTreePanel(true);
        
    }
}

private void jbtnUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnUpActionPerformed
        try {
        observationSequenceTreePanel.moveSelectedSequenceComponent(ObservationSequenceTreePanel.UP);
    } catch (Exception e) {
        e.printStackTrace();
        logger.error(e);
        JOptionPane.showMessageDialog(this, e);
    }
}//GEN-LAST:event_jbtnUpActionPerformed

private void jbtnDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDownActionPerformed
    try {
        observationSequenceTreePanel.moveSelectedSequenceComponent(ObservationSequenceTreePanel.DOWN);
    } catch (Exception e) {
        e.printStackTrace();
        logger.error(e);
        JOptionPane.showMessageDialog(this, e);
    }
}//GEN-LAST:event_jbtnDownActionPerformed

private void jbtnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDeleteActionPerformed

    int n = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to delete the selected component?",
        "Delete?",
        JOptionPane.YES_NO_OPTION);
        
    if (n == JOptionPane.YES_OPTION) {
        try {
            observationSequenceTreePanel.deleteSelectedSequenceComponent();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
        }
    }
}//GEN-LAST:event_jbtnDeleteActionPerformed

private void jbtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEditActionPerformed

    DefaultMutableTreeNode selectedNode = observationSequenceTreePanel.getSelectedNode();
    if (selectedNode == null) {
        return;
    }

    //edit node
    Object selectedObject = selectedNode.getUserObject();
    
    if (selectedObject.getClass().equals(String.class)) {
        //it's the root selected.
        return;
    }

    JPanel editPanel = getPanelForComponent((ISequenceComponent) selectedObject);
    if (editPanel == null) {
        return;
    }
    
    setButtonMode(REPLACE);

    showComponentPanel(editPanel);
}//GEN-LAST:event_jbtnEditActionPerformed

private JPanel getPanelForComponent(ISequenceComponent sequenceComponent) {
    if (sequenceComponent instanceof XBranchComponent) {
        //can't edit branch components
        return null;
    } else if (sequenceComponent instanceof XIteratorComponent) {
        return new IteratorPanel((XIteratorComponent)sequenceComponent);
    } else if (sequenceComponent instanceof XExecutiveComponent) {
        return getPanelForExecutiveComponent((XExecutiveComponent) sequenceComponent);
    } else {
        return null;
    }
}

private JPanel getPanelForExecutiveComponent(XExecutiveComponent executiveComponent) {

    IExecutiveAction executiveAction = executiveComponent.getExecutiveAction();

    if (executiveAction instanceof XAcquisitionConfig) {
        XAcquisitionConfig acquisitionConfig = (XAcquisitionConfig) executiveAction;
        if (acquisitionConfig.getMode() == IAcquisitionConfig.INSTRUMENT_CHANGE) {
            return new FocalPlanePanel((XAcquisitionConfig)executiveAction);
        } else {
            return new FineTunePanel((XAcquisitionConfig)executiveAction);
        }
    } else if (executiveAction instanceof XAutoguiderConfig) {
        return new AutoguidePanel((XAutoguiderConfig)executiveAction);

    } else if (executiveAction instanceof XCalibration) {
        return new CalibrationPanel((XCalibration)executiveAction);

    } else if (executiveAction instanceof IExposure) {
        //catch both types of exposure
        return new ExposurePanel((IExposure)executiveAction);

    } else if (executiveAction instanceof XFocusOffset) {
        return new DefocusPanel((XFocusOffset)executiveAction);

    } else if (executiveAction instanceof XInstrumentConfigSelector) {

        XIteratorComponent selectedBranchIterator = observationSequenceTreePanel.getSelectedBranchIterator();
        XInstrumentConfigSelector instrumentConfigSelector = (XInstrumentConfigSelector)executiveAction;

        if (selectedBranchIterator == null) {
             //we're not in a branch, show a standard InstrumentConfigSelectorPanel
            return new InstrumentConfigSelectorPanel(instrumentConfigSelector, program);
        } else {
             //we're in a branch, show a FrodospecInstrumentConfigSelectorPanel for the XInstrumentConfigSelector
            String branchName = selectedBranchIterator.getComponentName();
            if (branchName.equalsIgnoreCase(CONST.FRODO_BLUE)) {
                return new DualBeamSpecInstrumentConfigSelectorPanel(instrumentConfigSelector, program);
            } else if (branchName.equalsIgnoreCase(CONST.FRODO_RED)) {
                return new DualBeamSpecInstrumentConfigSelectorPanel(instrumentConfigSelector, program);
            } else {
                return null;
            }
        }

    } else if (executiveAction instanceof XPositionOffset) {
        return new PositionOffsetPanel((XPositionOffset)executiveAction);

    } else  if (executiveAction instanceof XRotatorConfig) {
        return new RotatorPanel((XRotatorConfig)executiveAction);

    } else  if (executiveAction instanceof XSlew) {
        return new SlewPanel((XSlew)executiveAction, program);

    } else  if (executiveAction instanceof XFocusControl) {
        //return new FocusControlPanel((XFocusControl)executiveAction);
        //whilst BSS is off the telescope
        return null;
    } else  if (executiveAction instanceof XBeamSteeringConfig) {
        //return new BeamSteeringPanel((XBeamSteeringConfig)executiveAction);
        //whilst BSS is off the telescope
        return null;
    } else  if (executiveAction instanceof XTipTiltAbsoluteOffset) {
        //return new TipTiltAbsoluteOffsetPanel((XTipTiltAbsoluteOffset)executiveAction);
        //whilst BSS is off the telescope
        return null;
    } else {
        try {
            throw new Exception("Class type " + executiveAction.getClass() + " not supported");
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
        }
        return null;
    }
}

private void jbtnPositionOffsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPositionOffsetActionPerformed
    showComponentPanel(new PositionOffsetPanel());
    setButtonMode(ADD);
}//GEN-LAST:event_jbtnPositionOffsetActionPerformed

private void jbtnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelActionPerformed
    setButtonMode(OFF);
    enableNewComponentButtons(true);
    enableSequenceEditButtons(true);
    showComponentPanel(new JPanel());
}//GEN-LAST:event_jbtnCancelActionPerformed

private void jbtnCalibrationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCalibrationActionPerformed
    showComponentPanel(new CalibrationPanel());
    setButtonMode(ADD);
}//GEN-LAST:event_jbtnCalibrationActionPerformed

private void jbtnIteratorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnIteratorActionPerformed
    showComponentPanel(new IteratorPanel());
    setButtonMode(ADD);
}//GEN-LAST:event_jbtnIteratorActionPerformed

private void jbtnBranchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnBranchActionPerformed
    showComponentPanel(new BranchPanel());
    setButtonMode(ADD);
}//GEN-LAST:event_jbtnBranchActionPerformed

private void jbtnSlewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSlewActionPerformed
    showComponentPanel(new SlewPanel(program));
    setButtonMode(ADD);
}//GEN-LAST:event_jbtnSlewActionPerformed

private void jbtnFineTuneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnFineTuneActionPerformed
    showComponentPanel(new FineTunePanel());
    setButtonMode(ADD);
}//GEN-LAST:event_jbtnFineTuneActionPerformed

private void jbtnValidateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnValidateActionPerformed
    new ValidationFrame(group, getObservationSequence()).setVisible(true);
}//GEN-LAST:event_jbtnValidateActionPerformed

    public static void main(String[] args) {
        XProgram program = new XProgram("Testing");
        program.setID(2);
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());

        ObservationSequenceEditorPanel photomSeqEditorPanel = new ObservationSequenceEditorPanel(null, program, true);
        frame.setSize(SIZE);
        frame.setResizable(true);
        frame.getContentPane().add(photomSeqEditorPanel);

        new FrameCenterer(frame).start();
        frame.setVisible(true);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JButton jbtnAddItem;
    private javax.swing.JButton jbtnAutoguide;
    private javax.swing.JButton jbtnBranch;
    private javax.swing.JButton jbtnCalibration;
    private javax.swing.JButton jbtnCancel;
    private javax.swing.JButton jbtnDefocus;
    private javax.swing.JButton jbtnDelete;
    private javax.swing.JButton jbtnDown;
    private javax.swing.JButton jbtnEdit;
    private javax.swing.JButton jbtnExpose;
    private javax.swing.JButton jbtnFineTune;
    private javax.swing.JButton jbtnFocalPlane;
    private javax.swing.JButton jbtnInstrumentConfig;
    private javax.swing.JButton jbtnIterator;
    private javax.swing.JButton jbtnPositionOffset;
    private javax.swing.JButton jbtnRotator;
    private javax.swing.JButton jbtnSlew;
    private javax.swing.JButton jbtnUp;
    private javax.swing.JButton jbtnValidate;
    private javax.swing.JPanel jpComponentDetailPanel;
    private javax.swing.JPanel jplTreePanelContainer;
    // End of variables declaration//GEN-END:variables

}
