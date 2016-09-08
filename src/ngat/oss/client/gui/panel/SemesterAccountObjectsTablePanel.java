/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SemesterAccountObjectsTablePanel.java
 *
 * Created on Dec 6, 2010, 12:31:52 PM
 */

package ngat.oss.client.gui.panel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import ngat.jibxsoap.StringTypeParameter;
import ngat.oss.client.AccountModelClient;
import ngat.oss.client.Phase2ModelClient;
import ngat.oss.client.gui.model.SemesterProposalsTableModel;
import ngat.oss.client.gui.model.SemesterTagsTableModel;
import ngat.oss.client.gui.wrapper.SemesterProposalsTableLineEntry;
import ngat.oss.client.gui.wrapper.SemesterTagsTableLineEntry;
import ngat.oss.client.gui.wrapper.TransactionWrapper;
import ngat.oss.exception.Phase2Exception;
import ngat.oss.reference.AccountTypes;
import ngat.phase2.IAccount;
import ngat.phase2.IProposal;
import ngat.phase2.ISemester;
import ngat.phase2.ITag;
import ngat.phase2.XAccount;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class SemesterAccountObjectsTablePanel extends javax.swing.JPanel {

    static Logger logger = Logger.getLogger(SemesterAccountObjectsTablePanel.class);

    private Class objectClass; //IProposal | ITag
    private ISemester semester;
    private AccountModelClient accountModelClient;

    public SemesterAccountObjectsTablePanel(Class objectClass, ISemester semester, AccountModelClient accountModelClient) {
        this.objectClass = objectClass;
        this.semester = semester;
        this.accountModelClient = accountModelClient;
        
        initComponents();
        try {
            populateComponents();
        } catch (Phase2Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            JOptionPane.showMessageDialog(this, "An error occurred populating the tables.");
        }
    }

    private void populateComponents() throws Phase2Exception {

        if (objectClass.equals(IProposal.class)) {
            SemesterProposalsTableModel semesterProposalsTableModel = new SemesterProposalsTableModel(semester, accountModelClient);
            semesterObjectsTable.setModel(semesterProposalsTableModel);

            TableColumnModel tableColumnModel =  semesterObjectsTable.getColumnModel();

            TableColumn allocatedColumn = tableColumnModel.getColumn(SemesterProposalsTableLineEntry.ALLOCATED_COLUMN);
            AccountIncDecPanelCellRenderer allocatedColumnCellRenderer = new AccountIncDecPanelCellRenderer(accountModelClient, TransactionWrapper.ALLOCATED_BALANCE_TYPE);
            AccountIncDecPanelCellEditor allocatedColumnCellEditor = new AccountIncDecPanelCellEditor(accountModelClient, semesterProposalsTableModel, TransactionWrapper.ALLOCATED_BALANCE_TYPE);
            allocatedColumn.setCellRenderer(allocatedColumnCellRenderer);
            allocatedColumn.setCellEditor(allocatedColumnCellEditor);

            TableColumn consumedColumn = tableColumnModel.getColumn(SemesterProposalsTableLineEntry.CONSUMED_COLUMN);
            AccountIncDecPanelCellRenderer consumedColumnCellRenderer = new AccountIncDecPanelCellRenderer(accountModelClient, TransactionWrapper.CONSUMED_BALANCE_TYPE);
            AccountIncDecPanelCellEditor consumedColumnCellEditor = new AccountIncDecPanelCellEditor(accountModelClient, semesterProposalsTableModel, TransactionWrapper.CONSUMED_BALANCE_TYPE);
            consumedColumn.setCellRenderer(consumedColumnCellRenderer);
            consumedColumn.setCellEditor(consumedColumnCellEditor);

            TableColumn graphicColumn = tableColumnModel.getColumn(SemesterProposalsTableLineEntry.GRAPHIC_COLUMN);
            BarChartCellRenderer graphicColumnCellRenderer = new BarChartCellRenderer();
            graphicColumn.setCellRenderer(graphicColumnCellRenderer);

            //get the tableRepresentedObjects which contains a list of proposal names already in the table
            List tableRepresentedObjects = semesterProposalsTableModel.getObjectsRepresented();
            //now list in jcbNewAccountOwners objects not in that table
            List allProposalNames = Phase2ModelClient.getInstance().listProposalNames(false);
            populateJcbNewAccountOwners(tableRepresentedObjects, allProposalNames);

        } else if (objectClass.equals(ITag.class)) {
            SemesterTagsTableModel semesterTagsTableModel = new SemesterTagsTableModel(semester, accountModelClient);
            semesterObjectsTable.setModel(semesterTagsTableModel);

            TableColumnModel tableColumnModel =  semesterObjectsTable.getColumnModel();

            TableColumn allocatedColumn = tableColumnModel.getColumn(SemesterTagsTableLineEntry.ALLOCATED_COLUMN);
            AccountIncDecPanelCellRenderer allocatedColumnCellRenderer = new AccountIncDecPanelCellRenderer(accountModelClient, TransactionWrapper.ALLOCATED_BALANCE_TYPE);
            AccountIncDecPanelCellEditor allocatedColumnCellEditor = new AccountIncDecPanelCellEditor(accountModelClient, semesterTagsTableModel, TransactionWrapper.ALLOCATED_BALANCE_TYPE);
            allocatedColumn.setCellRenderer(allocatedColumnCellRenderer);
            allocatedColumn.setCellEditor(allocatedColumnCellEditor);

            TableColumn consumedColumn = tableColumnModel.getColumn(SemesterTagsTableLineEntry.CONSUMED_COLUMN);
            AccountIncDecPanelCellRenderer consumedColumnCellRenderer = new AccountIncDecPanelCellRenderer(accountModelClient, TransactionWrapper.CONSUMED_BALANCE_TYPE);
            AccountIncDecPanelCellEditor consumedColumnCellEditor = new AccountIncDecPanelCellEditor(accountModelClient, semesterTagsTableModel, TransactionWrapper.CONSUMED_BALANCE_TYPE);
            consumedColumn.setCellRenderer(consumedColumnCellRenderer);
            consumedColumn.setCellEditor(consumedColumnCellEditor);

            TableColumn graphicColumn = tableColumnModel.getColumn(SemesterTagsTableLineEntry.GRAPHIC_COLUMN);
            BarChartCellRenderer graphicColumnCellRenderer = new BarChartCellRenderer();
            graphicColumn.setCellRenderer(graphicColumnCellRenderer);

            //get the tableRepresentedObjects which contains a list of proposal names already in the table
            List tableRepresentedObjects = semesterTagsTableModel.getObjectsRepresented();
            //now list in jcbNewAccountOwners objects not in that table
            List allTags = Phase2ModelClient.getInstance().listTags();
            List allTagNames = new ArrayList();
            Iterator ati = allTags.iterator();
            while (ati.hasNext()) {
                ITag tag = (ITag) ati.next();
                allTagNames.add(tag.getName());
            }

            populateJcbNewAccountOwners(tableRepresentedObjects, allTagNames);
        }

        semesterObjectsTable.validate();
        semesterObjectsTable.repaint();
    }

    private void populateJcbNewAccountOwners(List alreadyRepresentedNames, List allNames) {
        ArrayList namesToShow = new ArrayList();
        Iterator allNamesIterator = allNames.iterator();
        while (allNamesIterator.hasNext()) {
            String name;
            Object nextObject = allNamesIterator.next();
            if (nextObject instanceof StringTypeParameter) {
                StringTypeParameter param = (StringTypeParameter) nextObject;
                name = param.getStringValue();
            } else {
                name = (String) nextObject;
            }
            if(!listContains(alreadyRepresentedNames, name)) {
                namesToShow.add(name);
            }
        }
        jcbNewAccountOwners.removeAllItems();
        Iterator ntsi = namesToShow.iterator();
        while (ntsi.hasNext()) {
            String name = (String) ntsi.next();
            jcbNewAccountOwners.addItem(name);
        }
    }

    private boolean listContains(List names, String name) {
        Iterator ni = names.iterator();
        while (ni.hasNext()) {
            String foundName = (String) ni.next();
            if (foundName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void reloadTable() {

        semesterObjectsTable.setEnabled(false);        

        try {
            populateComponents();
            semesterObjectsTable.setEnabled(true);
        } catch (Phase2Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            JOptionPane.showMessageDialog(this, "An error occurred populating the tables.");
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

        jScrollPane1 = new javax.swing.JScrollPane();
        semesterObjectsTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jcbNewAccountOwners = new javax.swing.JComboBox();
        jbtnCreateAccount = new javax.swing.JButton();
        jtfAlloc = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        semesterObjectsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        semesterObjectsTable.setRowHeight(25);
        jScrollPane1.setViewportView(semesterObjectsTable);

        jLabel1.setText("Create account for:");

        jbtnCreateAccount.setText("Create");
        jbtnCreateAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCreateAccountActionPerformed(evt);
            }
        });

        jLabel2.setText("Allocation:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(jcbNewAccountOwners, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 197, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(45, 45, 45)
                        .add(jLabel1)))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(7, 7, 7)
                        .add(jtfAlloc, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 102, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(9, 9, 9)
                        .add(jbtnCreateAccount))
                    .add(layout.createSequentialGroup()
                        .add(22, 22, 22)
                        .add(jLabel2)))
                .addContainerGap(244, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 333, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(32, 32, 32)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jcbNewAccountOwners, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jtfAlloc, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jbtnCreateAccount)))
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel2)
                            .add(jLabel1))))
                .addContainerGap(20, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnCreateAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCreateAccountActionPerformed
        //Class objectClass, ISemester semester, AccountModelClient accountModelClient

        String ownerName = (String) jcbNewAccountOwners.getSelectedItem();
        Phase2ModelClient phase2ModelClient = Phase2ModelClient.getInstance();
        String objectTypeName = "";
        double allocation;
        long tagOrProposalID;
        try {
            allocation = Double.parseDouble(jtfAlloc.getText());
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Please enter a valid allocation value.");
            return;
        }
        if (objectClass.equals(IProposal.class)) {
            objectTypeName = "proposal";
            try {
                IProposal proposal = phase2ModelClient.findProposal(ownerName);
                if (proposal != null) {
                    tagOrProposalID = proposal.getID();
                } else {
                    JOptionPane.showMessageDialog(this, "An error occurred in contacting the Phase2 server, proposal=null");
                    return;
                }
            } catch (Phase2Exception ex) {
                ex.printStackTrace();
                logger.error(ex);
                JOptionPane.showMessageDialog(this, "An error occurred in contacting the Phase2 server");
                return;
            }
        } else {
            objectTypeName = "tag";
            try {
                ITag tag = phase2ModelClient.findTag(ownerName);
                if (tag != null) {
                    tagOrProposalID = tag.getID();
                } else {
                    JOptionPane.showMessageDialog(this, "An error occurred in contacting the Phase2 server, tag=null");
                    return;
                }
            } catch (Phase2Exception ex) {
                ex.printStackTrace();
                logger.error(ex);
                JOptionPane.showMessageDialog(this, "An error occurred in contacting the Phase2 server");
                return;
            }
        }

        //check account for this semester for this tag or proposal doesn't exist already
        IAccount foundAccount;
        try {
            foundAccount = accountModelClient.findAccount(tagOrProposalID, semester.getID());
            if (foundAccount != null) {
                JOptionPane.showMessageDialog(this, "An account for this " + objectTypeName + " for semester " + semester.getName() + " already exists. Account not created.");
                return;
            }
        } catch (Phase2Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred in accessing the database. Account not created.");
            ex.printStackTrace();
            return;
        }
        
        XAccount account = new XAccount(AccountTypes.ALLOCATION_NAME); //only adding a total allocation account now-a-days
        account.setChargeable(true);
        account.setAllocated(allocation);
        account.setConsumed(0);

        try {
            long newAccountId = accountModelClient.addAccount(tagOrProposalID, semester.getID(), account);
            //System.err.println("newAccountId=" + newAccountId);
            JOptionPane.showMessageDialog(this, "The account was successfully added, click OK.");
            reloadTable();
        } catch (Phase2Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
        }
        
    }//GEN-LAST:event_jbtnCreateAccountActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtnCreateAccount;
    private javax.swing.JComboBox jcbNewAccountOwners;
    private javax.swing.JTextField jtfAlloc;
    private javax.swing.JTable semesterObjectsTable;
    // End of variables declaration//GEN-END:variables

}
