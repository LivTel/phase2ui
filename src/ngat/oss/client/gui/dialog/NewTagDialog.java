/*
 * NewTimingConstraintDialog.java
 *
 * Created on April 30, 2009, 10:38 AM
 */
package ngat.oss.client.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import ngat.oss.client.AccountModelClient;
import ngat.oss.client.Phase2ModelClient;
import ngat.oss.client.gui.frame.MainFrame;
import ngat.oss.client.gui.model.CreateProposalAccountsTableModel;
import ngat.oss.client.gui.model.CreateTagAccountsTableModel;
import ngat.oss.client.gui.reference.Session;
import ngat.oss.client.gui.util.LimitedCharactersDocument;
import ngat.oss.client.gui.wrapper.SemesterSelectionTableLine;
import ngat.oss.exception.Phase2Exception;
import ngat.oss.reference.Const;
import ngat.phase2.ISemester;
import ngat.phase2.XAccount;
import ngat.phase2.XTag;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class NewTagDialog extends javax.swing.JDialog implements ActionListener {

    static Logger logger = Logger.getLogger(NewTagDialog.class);
    private CreateTagAccountsTableModel createTagAccountsTableModel;
    private AccountModelClient accountModelClient = new AccountModelClient(Const.TAG_ACCOUNT_SERVICE);

    //private ITimingConstraint timingConstraint;
    /**
     * Creates new form NewTimingConstraintDialog
     */
    public NewTagDialog(boolean modal) {
        this.setModal(modal);
        initComponents();
        centerFrame();
        addListeners();
        try {
            setUpAccountCreationTable();
        } catch (Phase2Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred trying to ready the accounts options: " + ex.getMessage());
            ex.printStackTrace();
            logger.error(ex);
            JOptionPane.showMessageDialog(this, "An error occurred, no accounts will be created.");
        }
        jtfName.setDocument(new LimitedCharactersDocument(LimitedCharactersDocument.STRICT_LIMITATION));
    }

    private void setUpAccountCreationTable() throws Phase2Exception {
        //add the JTable here - created from the xProposal object

        createTagAccountsTableModel = new CreateTagAccountsTableModel(accountModelClient);

        JTable tagAccountsTable = new JTable(createTagAccountsTableModel);
        tagAccountsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tagAccountsTable.setPreferredScrollableViewportSize(new Dimension(500, 450));
        tagAccountsTable.setFillsViewportHeight(true);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(tagAccountsTable);

        //Add the scroll pane to this panel.

        jplTableContainerPanel.setLayout(new BorderLayout());
        jplTableContainerPanel.add(scrollPane, BorderLayout.CENTER);

        /*
         //set the renderers and editors on the particular columns
         TableColumnModel tableColumnModel = proposalAccountsTable.getColumnModel();

         //semester name column
         //ignore

         //consumed column
         TableColumn selectColumn = tableColumnModel.getColumn(SemesterSelectionTableLine.SELECTED_COL);
        
         //example of setting cell renderer
        
         AccountIncDecPanelCellRenderer consumedColumnCellRenderer = new AccountIncDecPanelCellRenderer(accountModelClient, TransactionWrapper.CONSUMED);
         consumedColumn.setCellRenderer(consumedColumnCellRenderer);
        
         CreateProposalAccountsCellRenderer createProposalAccountsCellRenderer = new CreateProposalAccountsCellRenderer();
         selectColumn.setCellRenderer(createProposalAccountsCellRenderer);
        
         CreateProposalAccountsCellEditor createProposalAccountsCellEditor = new CreateProposalAccountsCellEditor(accountModelClient, createProposalAccountsTableModel);
         selectColumn.setCellEditor(createProposalAccountsCellEditor);
         */

        tagAccountsTable.validate();
        tagAccountsTable.repaint();

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
                        NewTagDialog.this.setLocation(x, y);
                    }
                });
    }

    private void addListeners() {
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                NewTagDialog.this.setVisible(false);
                NewTagDialog.this.dispose();
            }
        });
    }

    public void actionPerformed(ActionEvent actionEvent) {
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jbtnCreate = new javax.swing.JButton();
        jbtnCancel = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jtfName = new javax.swing.JTextField();
        jplTableContainerPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Create New TAG");

        jbtnCreate.setForeground(new java.awt.Color(255, 0, 0));
        jbtnCreate.setText("Create");
        jbtnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCreateActionPerformed(evt);
            }
        });

        jbtnCancel.setText("Cancel");
        jbtnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCancelActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 10)); // NOI18N
        jLabel1.setText("TAG");

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jLabel2.setText("Name");

        jtfName.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N

        jplTableContainerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        org.jdesktop.layout.GroupLayout jplTableContainerPanelLayout = new org.jdesktop.layout.GroupLayout(jplTableContainerPanel);
        jplTableContainerPanel.setLayout(jplTableContainerPanelLayout);
        jplTableContainerPanelLayout.setHorizontalGroup(
            jplTableContainerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );
        jplTableContainerPanelLayout.setVerticalGroup(
            jplTableContainerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 326, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jbtnCreate)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jbtnCancel))
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jLabel1)
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                    .add(jLabel2)
                                    .add(56, 56, 56)
                                    .add(jtfName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE))))
                        .addContainerGap(17, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(jplTableContainerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .add(26, 26, 26)
                        .add(jLabel2))
                    .add(jtfName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(jplTableContainerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jbtnCreate)
                    .add(jbtnCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jbtnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCreateActionPerformed

    if (jtfName.getText().trim().length() < 1) {
        JOptionPane.showMessageDialog(this, "Please enter a name.");
        return;
    }

    XTag xTag = new XTag();
    try {
        String name = jtfName.getText().trim();
        xTag.setName(name);
        Phase2ModelClient phase2ModelClient = Phase2ModelClient.getInstance();
        long tid = phase2ModelClient.addTag(xTag);
        xTag.setID(tid);
        
        createAccounts(xTag);
    } catch (Phase2Exception ex) {
        ex.printStackTrace();
        logger.error(ex);
        JOptionPane.showMessageDialog(this, ex.getMessage());
        JOptionPane.showMessageDialog(this, "The TAG was NOT added");
        return;
    }
    JOptionPane.showMessageDialog(this, "The TAG has been successfully added");

    this.setVisible(false);
    this.dispose();

    MainFrame.getInstance().receiveNewTreeObject(xTag);

    showTagAccountsDialog(xTag);
}//GEN-LAST:event_jbtnCreateActionPerformed

    private void createAccounts(XTag xTag) throws Phase2Exception {

        boolean atLeastOneAccountWasCreated = false;

        for (int r = 0; r < createTagAccountsTableModel.getRowCount(); r++) {

            SemesterSelectionTableLine semesterSelectionTableLine = createTagAccountsTableModel.getSemesterSelectionTableLine(r);
            ISemester semester = semesterSelectionTableLine.getSemester();
            boolean selected = semesterSelectionTableLine.isSelected();

            if (selected) {
                atLeastOneAccountWasCreated = true;

                XAccount account = new XAccount();
                account.setChargeable(true);
                accountModelClient.addAccount(xTag.getID(), semester.getID(), account);
            }

            //System.err.println(semesterSelectionTableLine);
        }

        if (atLeastOneAccountWasCreated) {
            JOptionPane.showMessageDialog(this, "The accounts were successfully created");
        } else {
            JOptionPane.showMessageDialog(this, "No accounts were created for the proposal");
        }

        this.setVisible(false);
        this.dispose();

        MainFrame.getInstance().receiveNewTreeObject(xTag);
    }

    private void showTagAccountsDialog(XTag xTag) {
        boolean allowAccountsEditing = Session.getInstance().getUser().isSuperUser();
        AccountsDialog dialog = new AccountsDialog(true, xTag, allowAccountsEditing);
        dialog.setVisible(true);

        //blocks

        dialog.setVisible(false);
        dialog.dispose();
    }

private void jbtnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelActionPerformed
    this.setVisible(false);
    this.dispose();

}//GEN-LAST:event_jbtnCancelActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton jbtnCancel;
    private javax.swing.JButton jbtnCreate;
    private javax.swing.JPanel jplTableContainerPanel;
    private javax.swing.JTextField jtfName;
    // End of variables declaration//GEN-END:variables
}
