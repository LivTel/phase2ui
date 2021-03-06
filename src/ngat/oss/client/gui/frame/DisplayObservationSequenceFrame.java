/*
 * LoginFrame.java
 *
 * Created on 19 November 2007, 12:26
 */
package ngat.oss.client.gui.frame;

import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.tree.TreeSelectionModel;
import ngat.oss.client.Phase2ModelClient;
import ngat.oss.client.gui.tree.sequencetree.ObservationSequenceTreeRenderer;
import ngat.oss.client.gui.tree.sequencetree.ObservationComponentSelector;
import ngat.oss.client.gui.tree.sequencetree.ObservationSequenceTreeModel;
import ngat.oss.client.gui.reference.CONST;
import ngat.oss.client.gui.reference.Session;
import ngat.oss.client.gui.util.FrameCenterer;
import ngat.oss.client.gui.util.validation.SequenceValidator;
import ngat.oss.client.gui.util.validation.ValidationProcess;
import ngat.oss.client.gui.wrapper.ValidationResults;
import ngat.oss.exception.Phase2Exception;
import ngat.oss.test.JiBXTester;
import ngat.phase2.IGroup;
import ngat.phase2.ISequenceComponent;
import ngat.sms.models.standard.StandardChargeAccountingModel;
import org.apache.log4j.Logger;

/**
 *
 * @author  nrc
 */
public class DisplayObservationSequenceFrame extends javax.swing.JFrame {

    static Logger logger = Logger.getLogger(DisplayObservationSequenceFrame.class);

    private IGroup group;
    private boolean isFirstLine = true;
    ISequenceComponent observationSequence;
    
    /** Creates new form LoginFrame */
    public DisplayObservationSequenceFrame(IGroup group) {
        this.group = group;

        try {
            Phase2ModelClient phase2ModelClient = Phase2ModelClient.getInstance();
            observationSequence = phase2ModelClient.getObservationSequenceOfGroup(group.getID());
            //System.err.println(observationSequence);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            JOptionPane.showMessageDialog(this, ex.getMessage());
            this.setVisible(false);
            this.dispose();
            return;
        }
        
        initComponents();
        populateComponents();
        addListeners();
        setSize(CONST.MAIN_FRAME_SIZE);
        new FrameCenterer(this).start();
        
        try {
            SequenceValidator sequenceValidator = new SequenceValidator(observationSequence, group);
            ValidationResults validationResult = sequenceValidator.getValidationResults(true, true);
            
            jtpMessages.setText(validationResult.getValidationResultsAsString());
            jlblFailed.setVisible(validationResult.getFailureCount() > 0);

        }  catch (Phase2Exception ex) {
           ex.printStackTrace();
           logger.error(ex);
           JOptionPane.showMessageDialog(this, ex);
        }

        StandardChargeAccountingModel standardChargeAccountingModel = new StandardChargeAccountingModel();

        double estimatedExecutionTime = 0;
        try {
            estimatedExecutionTime = standardChargeAccountingModel.calculateCost(observationSequence);
            jtfEstimatedExecutionTime.setText(String.valueOf(estimatedExecutionTime / 1000));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            jtfEstimatedExecutionTime.setText("ERROR");
        }
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jtObsSequenceTree = new javax.swing.JTree();
        jbtnCancel = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtpMessages = new javax.swing.JTextPane();
        jlblFailed = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jtfEstimatedExecutionTime = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jbtnDumpXML = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Observation Sequence Editing");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                WindowClosing(evt);
            }
        });

        jtObsSequenceTree.setModel(null);
        jScrollPane1.setViewportView(jtObsSequenceTree);

        jbtnCancel.setText("Close");
        jbtnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCancelActionPerformed(evt);
            }
        });

        jLabel1.setText("Observation Sequence:");

        jLabel2.setText("Validation Results:");

        jtpMessages.setEditable(false);
        jScrollPane2.setViewportView(jtpMessages);

        jlblFailed.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jlblFailed.setForeground(new java.awt.Color(255, 0, 0));
        jlblFailed.setText("FAILED");

        jLabel3.setText("Estimated Execution Time:");

        jtfEstimatedExecutionTime.setEditable(false);

        jLabel4.setText("(sec)");

        jbtnDumpXML.setText("Dump xml");
        jbtnDumpXML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDumpXMLActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jtfEstimatedExecutionTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 77, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jbtnDumpXML)
                        .add(21, 21, 21))
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(layout.createSequentialGroup()
                                .add(jLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jlblFailed))
                            .add(jbtnCancel))
                        .add(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(11, 11, 11)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 291, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(jtfEstimatedExecutionTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4)
                    .add(jbtnDumpXML))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(jlblFailed))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jbtnCancel)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void WindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_WindowClosing
        // TODO add your handling code here:
    }//GEN-LAST:event_WindowClosing

    private void jbtnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelActionPerformed
        this.setVisible(false);
        this.dispose();
}//GEN-LAST:event_jbtnCancelActionPerformed

    private void jbtnDumpXMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDumpXMLActionPerformed
        try {
            JiBXTester.showJibxOfObject(observationSequence);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jbtnDumpXMLActionPerformed
    
    private void addListeners() {
    }
   
    private void populateComponents() {
        
        this.setTitle("Displaying Observation Sequence of Group: " + group.getName());

        jbtnDumpXML.setVisible(Session.getInstance().getUser().isSuperUser());
        loadTreeModel();
    }
    
    private void loadTreeModel() {
        //instantiate a new DynamicUserTreeModel and place it in the Session
        //by default it will use the IUser in the Session to load its data
        ObservationSequenceTreeModel obsSeqTreeModel = new ObservationSequenceTreeModel(observationSequence);
       
        //set the model of the overview tree to be this model
        jtObsSequenceTree.setModel(obsSeqTreeModel);
        
        //set the selection to single item only selection
        int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
        jtObsSequenceTree.getSelectionModel().setSelectionMode(mode);
        //instantiate the MainTreeRenderer and add it as a renderer for the tree
        
        //HOOK THIS UP:
        jtObsSequenceTree.setCellRenderer(new ObservationSequenceTreeRenderer());
        
        //add listeners to tree events here if needed in future
        jtObsSequenceTree.addTreeSelectionListener(new ObservationComponentSelector(jtObsSequenceTree,  this));
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbtnCancel;
    private javax.swing.JButton jbtnDumpXML;
    private javax.swing.JLabel jlblFailed;
    private javax.swing.JTree jtObsSequenceTree;
    private javax.swing.JTextField jtfEstimatedExecutionTime;
    private javax.swing.JTextPane jtpMessages;
    // End of variables declaration//GEN-END:variables
    
}
