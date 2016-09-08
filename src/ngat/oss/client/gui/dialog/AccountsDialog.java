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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import ngat.oss.client.AccountModelClient;
import ngat.oss.client.gui.model.ItemAccountsTableModel;
import ngat.oss.client.gui.panel.AccountDeleteButtonCellEditor;
import ngat.oss.client.gui.panel.AccountDeleteButtonCellRenderer;
import ngat.oss.client.gui.panel.BarChartCellRenderer;
import ngat.oss.client.gui.panel.AccountIncDecPanelCellEditor;
import ngat.oss.client.gui.panel.AccountIncDecPanelCellRenderer;
import ngat.oss.client.gui.wrapper.ItemAccountsTableLineEntry;
import ngat.oss.client.gui.wrapper.TransactionWrapper;
import ngat.oss.exception.Phase2Exception;
import ngat.oss.reference.AccountTypes;
import ngat.oss.reference.Const;
import ngat.phase2.IAccount;
import ngat.phase2.IProposal;
import ngat.phase2.ISemester;
import ngat.phase2.ITag;
import ngat.phase2.XAccount;
import org.apache.log4j.Logger;

/**
 *
 * @author  nrc
 */
public class AccountsDialog extends javax.swing.JDialog implements ActionListener {

    static Logger logger = Logger.getLogger(AccountsDialog.class);

    private AccountModelClient accountModelClient;
    private long tagOrProposalID;
    private IProposal proposal = null;
    private ITag tag = null;
    private String objectType = "";
    private boolean allowEditing;

    public AccountsDialog(boolean modal, ITag tag, boolean allowEditing) {
        setModal(modal);
        this.tag = tag;
        this.tagOrProposalID = tag.getID();
        this.objectType = "TAG";
        this.allowEditing = allowEditing;

        this.setSize(800, 640);

        //instantiate the TAG account model client
        accountModelClient = new AccountModelClient(Const.TAG_ACCOUNT_SERVICE);
        ItemAccountsTableModel itemAccountsTableModel = new ItemAccountsTableModel(tag, accountModelClient);
        initComponents();
        initComponents2(itemAccountsTableModel);
        this.setSize(new Dimension(700, 640));
        centerFrame();
        addListeners();
        this.setTitle("Accounts of TAG: " + tag.getName());
    }

    public AccountsDialog(boolean modal, IProposal proposal, boolean allowEditing) {
        setModal(modal);
        this.proposal = proposal;
        this.tagOrProposalID = proposal.getID();
        this.objectType = "Proposal";
        this.allowEditing = allowEditing;
        
        this.setSize(800, 640);
        
        //instantiate the PROPOSAL account model client
        accountModelClient = new AccountModelClient(Const.PROPOSAL_ACCOUNT_SERVICE);
        ItemAccountsTableModel itemAccountsTableModel = new ItemAccountsTableModel(proposal, accountModelClient);
        initComponents();
        initComponents2(itemAccountsTableModel);
        this.setSize(new Dimension(700, 640));
        centerFrame();
        addListeners();
        this.setTitle("Accounts of Proposal: " + proposal.getName());
    }

    private void initComponents2(ItemAccountsTableModel itemAccountsTableModel) {

        //semester list
        try {
            List semesters = accountModelClient.listSemestersFromDate(new Date().getTime());
            Iterator i = semesters.iterator();
            while (i.hasNext()) {
                ISemester semester = (ISemester) i.next();
                jcbSemester.addItem(semester.getName());
            }
        } catch (Phase2Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            JOptionPane.showMessageDialog(this, "A error occurred whilst accessing the Phase2 server.");
            return;
        }

        //table
        itemAccountsListTable.setModel(itemAccountsTableModel);

        //set the renderers and editors on the particular columns
        TableColumnModel tableColumnModel =  itemAccountsListTable.getColumnModel();

        //allocated column
        TableColumn allocatedColumn = tableColumnModel.getColumn(ItemAccountsTableLineEntry.ALLOCATED_COLUMN);
        AccountIncDecPanelCellRenderer allocatedColumnCellRenderer = new AccountIncDecPanelCellRenderer(accountModelClient, TransactionWrapper.ALLOCATED_BALANCE_TYPE);
        allocatedColumn.setCellRenderer(allocatedColumnCellRenderer);

            AccountIncDecPanelCellEditor allocatedColumnCellEditor = new AccountIncDecPanelCellEditor(accountModelClient, itemAccountsTableModel, TransactionWrapper.ALLOCATED_BALANCE_TYPE);
            allocatedColumn.setCellEditor(allocatedColumnCellEditor);

        //consumed column
        TableColumn consumedColumn = tableColumnModel.getColumn(ItemAccountsTableLineEntry.CONSUMED_COLUMN);
        AccountIncDecPanelCellRenderer consumedColumnCellRenderer = new AccountIncDecPanelCellRenderer(accountModelClient, TransactionWrapper.CONSUMED_BALANCE_TYPE);
        consumedColumn.setCellRenderer(consumedColumnCellRenderer);
        
            AccountIncDecPanelCellEditor consumedColumnCellEditor = new AccountIncDecPanelCellEditor(accountModelClient, itemAccountsTableModel, TransactionWrapper.CONSUMED_BALANCE_TYPE);
            consumedColumn.setCellEditor(consumedColumnCellEditor);

        //graphic column
        TableColumn graphicColumn = tableColumnModel.getColumn(ItemAccountsTableLineEntry.GRAPHIC_COLUMN);
        BarChartCellRenderer graphicColumnCellRenderer = new BarChartCellRenderer();
        graphicColumn.setCellRenderer(graphicColumnCellRenderer);

        //delete button colum
        TableColumn deleteButtonColumn = tableColumnModel.getColumn(ItemAccountsTableLineEntry.DELETE_COLUMN);
        AccountDeleteButtonCellRenderer accountDeleteButtonCellRenderer = new AccountDeleteButtonCellRenderer();
        deleteButtonColumn.setCellRenderer(accountDeleteButtonCellRenderer);

            AccountDeleteButtonCellEditor accountDeleteButtonCellEditor = new AccountDeleteButtonCellEditor(accountModelClient, this);
            deleteButtonColumn.setCellEditor(accountDeleteButtonCellEditor);

        itemAccountsListTable.validate();
        itemAccountsListTable.repaint(); 
    }

    
    public void reloadTable() {

        ItemAccountsTableModel itemAccountsTableModel;
        if (proposal != null) {
            itemAccountsTableModel = new ItemAccountsTableModel(proposal, accountModelClient);
        } else {
            itemAccountsTableModel = new ItemAccountsTableModel(tag, accountModelClient);
        }
        initComponents2(itemAccountsTableModel);
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
                        AccountsDialog.this.setLocation(x, y);
                    }
                });
    }

    private void addListeners() {
        this.addWindowListener(new java.awt.event.WindowAdapter() {

            public void windowClosing(java.awt.event.WindowEvent e) {
                AccountsDialog.this.setVisible(false);
                AccountsDialog.this.dispose();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        itemAccountsListTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jcbSemester = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jtfAllocation = new javax.swing.JTextField();
        jbtnCreate = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Accounts");
        setResizable(false);

        jbtnClose.setText("Close");
        jbtnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCloseActionPerformed(evt);
            }
        });

        itemAccountsListTable.setModel(new ItemAccountsTableModel());
        itemAccountsListTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        itemAccountsListTable.setRowHeight(25);
        jScrollPane1.setViewportView(itemAccountsListTable);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setText("Create a new account:");

        jLabel2.setText("Semester:");

        jLabel3.setText("Allocation:");

        jbtnCreate.setText("Create");
        jbtnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCreateActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(24, 24, 24)
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jcbSemester, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jtfAllocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 89, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(27, 27, 27)
                        .add(jbtnCreate))
                    .add(jLabel1))
                .addContainerGap(258, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jcbSemester, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3)
                    .add(jtfAllocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jbtnCreate))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jbtnClose))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jbtnClose)
                .add(17, 17, 17))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed

    
    this.setVisible(false);
    this.dispose();

}//GEN-LAST:event_jbtnCloseActionPerformed

private void jbtnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCreateActionPerformed

    double allocation;

    try {
        allocation = Double.parseDouble(jtfAllocation.getText());
    } catch (NumberFormatException nfe) {
        JOptionPane.showMessageDialog(this, "Please enter a valid allocation value.");
        return;
    }

    try {
        ISemester semesterSelected = findSemester((String) jcbSemester.getSelectedItem());

        if (semesterSelected == null) {
            JOptionPane.showMessageDialog(this, "Unable to locate related semester, the account was not added.");
            return;
        }

        IAccount foundAccount = accountModelClient.findAccount(tagOrProposalID, semesterSelected.getID());
        if (foundAccount != null) {
           JOptionPane.showMessageDialog(this, "An account for this " + objectType + " for semester " + semesterSelected.getName() + " already exists. Account not created.");
           return;
        }

        XAccount account = new XAccount(AccountTypes.ALLOCATION_NAME); //only adding a total allocation account now-a-days
        account.setChargeable(true);
        account.setAllocated(allocation);
        account.setConsumed(0);

        accountModelClient.addAccount(tagOrProposalID, semesterSelected.getID(), account);
        JOptionPane.showMessageDialog(this, "The account was successfully added, click OK.");
        reloadTable();
    } catch (Exception p2e) {
        JOptionPane.showMessageDialog(this, "An error occurred whilst accessing the Phase2 system, the account was not added.");
        return;
    }
}//GEN-LAST:event_jbtnCreateActionPerformed

    private ISemester findSemester(String semesterName) throws Phase2Exception {

            List semesters = accountModelClient.listSemestersFromDate(new Date().getTime());
            Iterator i = semesters.iterator();
            while (i.hasNext()) {
                ISemester semester = (ISemester) i.next();
                if (semester.getName().equalsIgnoreCase(semesterName)) {
                    return semester;
                }
            }

        return null;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable itemAccountsListTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnCreate;
    private javax.swing.JComboBox jcbSemester;
    private javax.swing.JTextField jtfAllocation;
    // End of variables declaration//GEN-END:variables
}
