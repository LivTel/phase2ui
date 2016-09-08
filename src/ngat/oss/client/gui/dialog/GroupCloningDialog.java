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
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import ngat.oss.client.Phase2ModelClient;
import ngat.oss.client.gui.frame.MainFrame;
import ngat.oss.client.gui.render.TargetRenderer;
import ngat.oss.client.gui.util.LimitedCharactersDocument;
import ngat.oss.client.gui.util.SequenceWalker;
import ngat.oss.exception.Phase2Exception;
import ngat.phase2.IGroup;
import ngat.phase2.IProgram;
import ngat.phase2.IProposal;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.ISlew;
import ngat.phase2.ITarget;
import ngat.phase2.ITimingConstraint;
import ngat.phase2.XBranchComponent;
import ngat.phase2.XExecutiveComponent;
import ngat.phase2.XGroup;
import ngat.phase2.XIteratorComponent;
import ngat.phase2.XSlew;
import org.apache.log4j.Logger;

/**
 *
 * @author  nrc
 */
public class GroupCloningDialog extends javax.swing.JDialog implements ActionListener {

    static Logger logger = Logger.getLogger(GroupCloningDialog.class);

    private IProgram program = null;
    private IProposal proposal = null;
    private IGroup originalGroup = null;
    
    private SequenceWalker sequenceWalker = null;
    private ISequenceComponent observationSequence = null;
    private List targetList;
    private List proposalsList;

    private int cloneTypeRequired = 0;
    private static final int CLONE_NO_OBS_SEQ = 1;
    private static final int CLONE_NO_FIDDLING = 2;
    private static final int CLONE_FIDDLE_SLEW = 3;
    
    private static final String NEW_CLONE_SUFFIX = "_CLONE";

    public GroupCloningDialog(IProgram program,  IProposal proposal, IGroup group) {
        setModal(true);
        this.program = program;
        this.proposal = proposal;
        this.originalGroup = group;
        try {
            observationSequence = Phase2ModelClient.getInstance().getObservationSequenceOfGroup(group.getID());
            sequenceWalker = new SequenceWalker(observationSequence);
        } catch (Phase2Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
        }

        initComponents();
        
        jtfClonedGroupName.setDocument(new LimitedCharactersDocument(LimitedCharactersDocument.STRICT_LIMITATION));
        jtfClonedGroupName.requestFocus();

        this.setSize(new Dimension(896, 354));
        centerFrame();
        
        this.setTitle("Cloning group: " + group.getName());
        populateComponents();
    }

    private void populateComponents() {
        if (observationSequence == null) {
            jtpMessages.setText("The group has no observation sequence, click OK to clone the other properties of the group.");
            cloneTypeRequired = CLONE_NO_OBS_SEQ;
            jplTargetPanel.setVisible(false);
        } else {
            List slewsList = sequenceWalker.getSlews();
            if (slewsList.size() > 1) {
                jtpMessages.setText("The group has more than one Slew component in the observation sequence, click OK to clone the group, however manual editing of the group will be required if you wish to alter the targets used.");
                cloneTypeRequired = CLONE_NO_FIDDLING;
                jplTargetPanel.setVisible(false);
            } else {
                jtpMessages.setText("The group has a single Slew component, use the interface below to change the Target in that Slew if so required.");
                //clone after fiddling slew here
                cloneTypeRequired = CLONE_FIDDLE_SLEW;
                jplTargetPanel.setVisible(true);
                try {
                    populateTargetList();
                } catch (Phase2Exception ex) {
                    ex.printStackTrace();
                    logger.error(ex);
                }
                selectTargetOfSlewInPanel(slewsList);
            }
        }

        jtfClonedGroupName.setText(originalGroup.getName() + NEW_CLONE_SUFFIX);

        try {
            populateAndSelectProposalList();
        } catch (Phase2Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
        };
    }

    private void populateAndSelectProposalList() throws Phase2Exception {

        jcbProposals.removeAllItems();
        proposalsList = Phase2ModelClient.getInstance().listProposalsOfProgramme(program.getID());
        Iterator pi = proposalsList.iterator();
        int index = 0;
        int indexToSelect = 0;
        while (pi.hasNext()) {
            IProposal proposalInProgramme = (IProposal) pi.next();
            if (proposalInProgramme.getID() == proposal.getID()) {
                indexToSelect = index;
            }
            jcbProposals.addItem(proposalInProgramme.getName());
            index ++;
        }

        jcbProposals.setSelectedIndex(indexToSelect);
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

    private void selectTargetOfSlewInPanel(List slews) {
        //slews should only have one member
        ISlew slew = (ISlew) slews.get(0);
        setTarget(slew.getTarget());
    }

    private void setTarget(ITarget target) {

        //select the given target in the controls
        //targetList has been populated by populateTargetList([String])
        Iterator i = targetList.iterator();

        while (i.hasNext()) {
            ITarget foundTarget = (ITarget) i.next();
            if (foundTarget.getID() == target.getID()) {
                jcbTarget.setSelectedItem(TargetRenderer.getShortDescription(foundTarget));
            }
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
    
    private boolean cloneGroup() {
        
        //required fields: name, priority, active, timingConstraint, observingConstraints
        String name;
        int priority;
        boolean groupIsEnabled;
        ITimingConstraint timingConstraint;
        List observingConstraints;

        name = jtfClonedGroupName.getText().trim();
        XGroup newGroup;
        
        try {
            IProposal selectedProposal = getProposalSelected();
            
            if (name.length() == 0) {
                JOptionPane.showMessageDialog(this, "Please set a name for the group.", "Submission failed", JOptionPane.OK_OPTION );
                return false;
            }
            if (Phase2ModelClient.getInstance().groupExists(name, selectedProposal.getID())) {
                JOptionPane.showMessageDialog(this, "Unable to clone the group, as a group called '" + name + "' already exists on proposal '" + selectedProposal.getName() + "'");
                return false;
            }

            //set priority to 1. unused feature atm.
            priority = originalGroup.getPriority();
            groupIsEnabled = originalGroup.isActive();
            timingConstraint = originalGroup.getTimingConstraint();

            observingConstraints = originalGroup.listObservingConstraints();

            boolean urgent = originalGroup.isUrgent();

            newGroup = new XGroup();
            newGroup.setName(name);
            newGroup.setPriority(priority);
            newGroup.setActive(groupIsEnabled);
            newGroup.setTimingConstraint(timingConstraint);
            newGroup.setObservingConstraints(observingConstraints);
            newGroup.setUrgent(urgent);

            long gid = Phase2ModelClient.getInstance().addGroup(selectedProposal.getID(), newGroup);
            newGroup.setID(gid);

            //now the obs sequence
            switch(cloneTypeRequired) {
                case CLONE_NO_OBS_SEQ:
                    //we're done
                    break;
                case CLONE_NO_FIDDLING:
                    //just add the obs sequence as it stands
                    if (observationSequence != null) {
                        Phase2ModelClient.getInstance().addObservationSequence(gid, observationSequence);
                    }
                    break;
                case CLONE_FIDDLE_SLEW:
                    cloneSequenceReplaceSlew(gid, observationSequence);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            return false;
        }
        //was (before 12/3/12):
        //MainFrame.getInstance().reloadRootNode();

        //now (12/3/12) (next two lines)
        MainFrame.getInstance().receiveNewTreeObject(newGroup);
        MainFrame.getInstance().reloadSelectedNodeParent();
        
        return true;
    }

    private IProposal getProposalSelected() {
        IProposal proposalSelected = (IProposal) this.proposalsList.get(jcbProposals.getSelectedIndex());
        return proposalSelected;
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
                        GroupCloningDialog.this.setLocation(x, y);
                    }
                });
    }


    public void actionPerformed(ActionEvent actionEvent) {
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jbtnClose = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtpMessages = new javax.swing.JTextPane();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jtfClonedGroupName = new javax.swing.JTextField();
        jplTargetPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jbtnCreateNewTarget = new javax.swing.JButton();
        jcbTarget = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jbtnSubmit = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jcbProposals = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Accounts");
        setResizable(false);

        jbtnClose.setText("Cancel");
        jbtnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCloseActionPerformed(evt);
            }
        });

        jtpMessages.setEditable(false);
        jScrollPane2.setViewportView(jtpMessages);

        jLabel1.setText("Messages:");

        jLabel2.setText("Please enter a new name for the cloned group:");

        jLabel3.setText("Use Target:");

        jbtnCreateNewTarget.setText("Create New Target");
        jbtnCreateNewTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCreateNewTargetActionPerformed(evt);
            }
        });

        jcbTarget.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbTargetItemStateChanged(evt);
            }
        });
        jcbTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbTargetActionPerformed(evt);
            }
        });

        jLabel4.setText("or");

        org.jdesktop.layout.GroupLayout jplTargetPanelLayout = new org.jdesktop.layout.GroupLayout(jplTargetPanel);
        jplTargetPanel.setLayout(jplTargetPanelLayout);
        jplTargetPanelLayout.setHorizontalGroup(
            jplTargetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jplTargetPanelLayout.createSequentialGroup()
                .add(jLabel3)
                .add(jplTargetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jplTargetPanelLayout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jbtnCreateNewTarget)
                        .addContainerGap(549, Short.MAX_VALUE))
                    .add(jplTargetPanelLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jcbTarget, 0, 739, Short.MAX_VALUE))))
        );
        jplTargetPanelLayout.setVerticalGroup(
            jplTargetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jplTargetPanelLayout.createSequentialGroup()
                .add(jplTargetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(jcbTarget, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jplTargetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jbtnCreateNewTarget)
                    .add(jLabel4))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jbtnSubmit.setText("Submit");
        jbtnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSubmitActionPerformed(evt);
            }
        });

        jLabel5.setText("Please select the Proposal to clone the group to:");

        jcbProposals.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbProposals.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbProposalsActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(20, 20, 20)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 953, Short.MAX_VALUE)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(layout.createSequentialGroup()
                            .add(jbtnSubmit)
                            .add(18, 18, 18)
                            .add(jbtnClose))
                        .add(jplTargetPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jLabel1)
                        .add(layout.createSequentialGroup()
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jLabel2)
                                .add(layout.createSequentialGroup()
                                    .add(24, 24, 24)
                                    .add(jtfClonedGroupName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 294, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .add(31, 31, 31)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(layout.createSequentialGroup()
                                    .add(21, 21, 21)
                                    .add(jcbProposals, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 187, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(jLabel5)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jtfClonedGroupName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jcbProposals, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(jplTargetPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jbtnSubmit)
                    .add(jbtnClose))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed

    this.setVisible(false);
    this.dispose();

}//GEN-LAST:event_jbtnCloseActionPerformed

private void jcbTargetItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbTargetItemStateChanged

}//GEN-LAST:event_jcbTargetItemStateChanged

private void jcbTargetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbTargetActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_jcbTargetActionPerformed

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

private void jbtnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSubmitActionPerformed
    
    if (cloneGroup()) {
        this.setVisible(false);
        this.dispose();
    }
}//GEN-LAST:event_jbtnSubmitActionPerformed

private void jcbProposalsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbProposalsActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_jcbProposalsActionPerformed

private ITarget getSelectedTarget() {
    int targetIndex = jcbTarget.getSelectedIndex();
    if (targetIndex == -1) {
        return null;
    }

    ITarget target = (ITarget) targetList.get(targetIndex);
    return target;
}

private void cloneSequenceReplaceSlew(long groupId, ISequenceComponent observationSequence) {
    //take the obs sequence, replace the single slew that is in it with the slew represented on the gui
    //then add the created observation sequence to the originalGroup using:
    //  Phase2ModelClient.getInstance().addObservationSequence(gid, observationSequence);

    //can I walk through the obs seq, if find slew, replace with new slew
    ITarget target = getSelectedTarget();

    ISequenceComponent clone = new SequencePoker().replaceSingleTargetInSequence(observationSequence, target);

    //bang it into the database
    try {
        Phase2ModelClient.getInstance().addObservationSequence(groupId, clone);
    } catch (Phase2Exception ex) {
        ex.printStackTrace();
        logger.error(ex);
    }

}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnCreateNewTarget;
    private javax.swing.JButton jbtnSubmit;
    private javax.swing.JComboBox jcbProposals;
    private javax.swing.JComboBox jcbTarget;
    private javax.swing.JPanel jplTargetPanel;
    private javax.swing.JTextField jtfClonedGroupName;
    private javax.swing.JTextPane jtpMessages;
    // End of variables declaration//GEN-END:variables
}

class SequencePoker {

    public ISequenceComponent replaceSingleTargetInSequence(ISequenceComponent rootComponent, ITarget target) {

        XIteratorComponent rootComponentAsIterator = (XIteratorComponent) rootComponent;
        //copy the header values of rootComponent into clonedRoot i.e. don't copy children in
        XIteratorComponent clonedRoot = new XIteratorComponent(rootComponentAsIterator.getComponentName(), rootComponentAsIterator.getCondition());//(XIteratorComponent) rootComponent;
        
        //iterate through rootComponent, adding children to clonedRoot as needed
        if (rootComponent != null) {
            List childrenList = rootComponent.listChildComponents();
            if (childrenList != null) {
                Iterator childrenIterator = childrenList.iterator();
                while (childrenIterator.hasNext()) {
                    ISequenceComponent childComponent = (ISequenceComponent) childrenIterator.next();

                    //only add it if it's not a slew, if it is a slew, replace the target and then add it
                    if (childComponent instanceof XExecutiveComponent) {
                        XExecutiveComponent childAsExecutiveComponent = (XExecutiveComponent) childComponent;
                        if (childAsExecutiveComponent.getExecutiveAction() instanceof ISlew) {

                            ISlew foundSlew = (ISlew) childAsExecutiveComponent.getExecutiveAction();
                            XSlew newSlew = new XSlew(target, foundSlew.getRotatorConfig(), foundSlew.usesNonSiderealTracking());
                            childAsExecutiveComponent.setAction(newSlew);
                            clonedRoot.addElement(childAsExecutiveComponent); //add it to the cloned root
                            addChildrenOfComponentToList(childComponent, clonedRoot);

                        } else {
                            clonedRoot.addElement(childComponent); //add it to the cloned root
                            addChildrenOfComponentToList(childComponent, clonedRoot);
                        }
                    } else {
                        clonedRoot.addElement(childComponent); //add it to the cloned root
                        addChildrenOfComponentToList(childComponent, clonedRoot);
                    }

                    
                }
            }
        }

        return (ISequenceComponent)clonedRoot;
    }


    private void addChildrenOfComponentToList(ISequenceComponent component, ISequenceComponent parent) {
        
        if ((component instanceof XIteratorComponent) | (component instanceof XBranchComponent)) {
            List childrenList = component.listChildComponents();
            if (childrenList != null) {
                Iterator childrenIterator = childrenList.iterator();
                while (childrenIterator.hasNext()) {
                    ISequenceComponent childComponent = (ISequenceComponent) childrenIterator.next();
                    addChildrenOfComponentToList(childComponent, parent);
                }
            }
        }
    }

}