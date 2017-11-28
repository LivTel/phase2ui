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
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import ngat.oss.client.AccessModelClient;
import ngat.oss.client.AccountModelClient;
import ngat.oss.client.Phase2ModelClient;
import ngat.oss.client.gui.frame.MainFrame;
import ngat.oss.client.gui.model.CreateProposalAccountsTableModel;
import ngat.oss.client.gui.reference.Session;
import ngat.oss.client.gui.util.LimitedCharactersDocument;
import ngat.oss.client.gui.wrapper.SemesterSelectionTableLine;
import ngat.oss.exception.Phase2Exception;
import ngat.oss.reference.Const;
import ngat.phase2.IAccessPermission;
import ngat.phase2.IProgram;
import ngat.phase2.IProposal;
import ngat.phase2.ISemester;
import ngat.phase2.ISemesterPeriod;
import ngat.phase2.ITag;
import ngat.phase2.IUser;
import ngat.phase2.XAccessPermission;
import ngat.phase2.XAccount;
import ngat.phase2.XProposal;
import ngat.phase2.util.Rounder;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class NewProposalDialog extends javax.swing.JDialog implements ActionListener {

    static Logger logger = Logger.getLogger(NewProposalDialog.class);
    private AccountModelClient accountModelClient;
    private IProgram program = null;
    private ITag tag = null;
    private XProposal xProposal = new XProposal();
    private int guiState;
    private static final int CREATING_PROPOSAL = 1;
    private static final int CREATING_ACCOUNTS = 2;
    private static final String NO_PI_SET = "----";
    private static final Dimension SIZE = new Dimension(612, 580);
    
    ISemesterPeriod proposalActivationSemesterPeriod, proposalExpirySemesterPeriod, currentSemesterPeriod;
    ArrayList allUsersList = new ArrayList();
    CreateProposalAccountsTableModel createProposalAccountsTableModel;

    public NewProposalDialog(boolean modal, IProgram program) {
        
        accountModelClient = new AccountModelClient(Const.PROPOSAL_ACCOUNT_SERVICE);
        
        this.program = program;
        this.setModal(modal);

        guiState = CREATING_PROPOSAL;

        initComponents();
        initComponents2();
        centerFrame();
        addListeners();
    }

    public NewProposalDialog(boolean modal, ITag tag) {
        
        accountModelClient = new AccountModelClient(Const.TAG_ACCOUNT_SERVICE);
        
        this.tag = tag;
        this.setModal(modal);

        guiState = CREATING_PROPOSAL;

        initComponents();
        initComponents2();
        centerFrame();
        addListeners();
    }

    private void initComponents2() {

        this.setSize(SIZE);

        jtfName.setDocument(new LimitedCharactersDocument(LimitedCharactersDocument.STRICT_LIMITATION));

        if (tag != null) {
            //created from tag, set tag name in drop down
            jcbTag.removeAllItems();
            jcbTag.addItem(tag.getName());
            jcbTag.setEditable(false);
            populateProgrammeList();
        } else if (program != null) {
            //created from programme, set programme name in drop down
            jcbProgramme.removeAllItems();
            jcbProgramme.addItem(program.getName());
            jcbProgramme.setEditable(false);
            populateTagList();
        }

        populateUserList();

        setDateControlsToDefaults();

        //set the priority offset panel visibility dependent upon user type
        jplPriorityOffsetPanel.setVisible(Session.getInstance().getUser().isSuperUser());

        jplProposalAccountsHeaderPanel.setVisible(false);
    }

    private void setDateControlsToDefaults() {
        
        try {
            long startDate, endDate;

            //get today's semester period
            ISemesterPeriod semesterPeriod = accountModelClient.getSemesterPeriodOfDate(new Date().getTime());
            if (!semesterPeriod.isOverlap()) {
                //not overlapping at the mo.
                //so set the start time to the start of the semester following this one.
                long firstSemesterId = semesterPeriod.getFirstSemester().getID();
                ISemester secondSemester = accountModelClient.getSemester(firstSemesterId + 1);
                startDate = secondSemester.getStartDate();
                //and set the end time to the end of the semester following this one.
                endDate = secondSemester.getEndDate() - 1000; //less 1 second
            } else {
                //overlapping at the mo
                //so set the start date to the start of this current semester (which will be the second semester in the overlapping period).
                ISemester secondSemester = semesterPeriod.getSecondSemester();
                startDate = secondSemester.getStartDate();
                //and set the end time to the second semester in the overlapping period.
                endDate = secondSemester.getEndDate() - 1000; //less 1 second
            }

            //set the controls
            dtepActivation.setTime(startDate, false);
            dtepExpiry.setTime(endDate, false);

            /*
             //get current semester
             ISemester semester = accountModelClient.getSemesterOfDate(new Date().getTime());
             //get it's id and add 1
             long nextSemesterId = semester.getID() + 1;
             ISemester nextSemester = accountModelClient.getSemester(nextSemesterId);
             //get start and end dates
             long startDate = nextSemester.getStartDate();
             long endDate = nextSemester.getEndDate() - 1000; //one second earlier by default
             //set default date times
             dtepActivation.setTime(startDate, false);
             dtepExpiry.setTime(endDate, false);
             */
        } catch (RemoteException ex) {
            ex.printStackTrace();
            logger.error(ex);
        }
    }

    private void populateProgrammeList() {
        jcbProgramme.removeAllItems();
        try {
            List progList = Phase2ModelClient.getInstance().listProgrammes();
            Iterator i = progList.iterator();
            while (i.hasNext()) {
                IProgram program = (IProgram) i.next();
                jcbProgramme.addItem(program.getName());
            }
        } catch (Phase2Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            JOptionPane.showMessageDialog(this, "cannot populate programme list");
            this.setVisible(false);
            this.dispose();
        }
    }

    private void populateTagList() {
        jcbTag.removeAllItems();
        try {
            List tagList = Phase2ModelClient.getInstance().listTags();
            Iterator i = tagList.iterator();
            while (i.hasNext()) {
                ITag tag = (ITag) i.next();
                jcbTag.addItem(tag.getName());
            }
        } catch (Phase2Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            JOptionPane.showMessageDialog(this, "cannot populate TAG list");
            this.setVisible(false);
            this.dispose();
        }
    }

    private void populateUserList() {
        List childrenList;
        try {
            jcbPI.removeAllItems();

            jcbPI.addItem(NO_PI_SET);
            allUsersList.add(null);

            childrenList = AccessModelClient.getInstance().listUsers();
            Iterator cli = childrenList.iterator();
            while (cli.hasNext()) {
                IUser user = (IUser) cli.next();
                jcbPI.addItem(user.getName());
                allUsersList.add(user);
            }
        } catch (Phase2Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
        }

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
                        NewProposalDialog.this.setLocation(x, y);
                    }
                });
    }

    private void addListeners() {
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                NewProposalDialog.this.setVisible(false);
                NewProposalDialog.this.dispose();
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

        jPanel1 = new javax.swing.JPanel();
        jbtnCreate = new javax.swing.JButton();
        jbtnCancel = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jplProposalHeaderPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jtfName = new javax.swing.JTextField();
        jcbRank = new javax.swing.JComboBox();
        jcbProgramme = new javax.swing.JComboBox();
        jtfTitle = new javax.swing.JTextField();
        jplPriorityOffsetPanel = new javax.swing.JPanel();
        jtfPriorityOffset = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jcbAllowFixed = new javax.swing.JCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtaScience = new javax.swing.JTextArea();
        jcbTag = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jcbEnabled = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        dtepActivation = new ngat.beans.guibeans.DateTimeEditorPanel();
        dtepExpiry = new ngat.beans.guibeans.DateTimeEditorPanel();
        jcbAllowUrgent = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jcbPI = new javax.swing.JComboBox();
        jtfProposalCode = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jplProposalAccountsHeaderPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jplTableContainerPanel = new javax.swing.JPanel();

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Create New Proposal");

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

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jplProposalHeaderPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 10)); // NOI18N
        jLabel1.setText("PROPOSAL");

        jtfName.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N

        jcbRank.setEditable(true);
        jcbRank.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "A", "B", "C", "Z" }));

        jtfTitle.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N

        jplPriorityOffsetPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jtfPriorityOffset.setText("0.00");

        jLabel12.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jLabel12.setText("Priority Offset:");

        org.jdesktop.layout.GroupLayout jplPriorityOffsetPanelLayout = new org.jdesktop.layout.GroupLayout(jplPriorityOffsetPanel);
        jplPriorityOffsetPanel.setLayout(jplPriorityOffsetPanelLayout);
        jplPriorityOffsetPanelLayout.setHorizontalGroup(
            jplPriorityOffsetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jplPriorityOffsetPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel12)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jtfPriorityOffset))
        );
        jplPriorityOffsetPanelLayout.setVerticalGroup(
            jplPriorityOffsetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jplPriorityOffsetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel12)
                .add(jtfPriorityOffset, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jLabel11.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jLabel11.setText("Programme:");

        jcbAllowFixed.setText("Allow Fixed Groups");

        jtaScience.setColumns(20);
        jtaScience.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jtaScience.setRows(5);
        jScrollPane2.setViewportView(jtaScience);

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jLabel2.setText("Name");

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jLabel4.setText("Rank");

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jLabel3.setText("Title");

        jcbEnabled.setSelected(true);
        jcbEnabled.setText("Enabled");

        jLabel8.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jLabel8.setText("Science Abstract");

        dtepActivation.setTitle("Activation");

        dtepExpiry.setTitle("Expiry");

        jcbAllowUrgent.setText("Allow Urgent Groups");

        jLabel10.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jLabel10.setText("Tag:");

        jLabel9.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jLabel9.setText("PI:");

        jLabel13.setText("CODE:");

        org.jdesktop.layout.GroupLayout jplProposalHeaderPanelLayout = new org.jdesktop.layout.GroupLayout(jplProposalHeaderPanel);
        jplProposalHeaderPanel.setLayout(jplProposalHeaderPanelLayout);
        jplProposalHeaderPanelLayout.setHorizontalGroup(
            jplProposalHeaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jplProposalHeaderPanelLayout.createSequentialGroup()
                .add(jplProposalHeaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel8)
                    .add(jLabel1)
                    .add(jplProposalHeaderPanelLayout.createSequentialGroup()
                        .add(jplProposalHeaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(jLabel4)
                            .add(jplProposalHeaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(jLabel10)
                                .add(jLabel3)))
                        .add(29, 29, 29)
                        .add(jplProposalHeaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jplProposalHeaderPanelLayout.createSequentialGroup()
                                .add(jcbTag, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 141, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(59, 59, 59)
                                .add(jLabel11)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jcbProgramme, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 180, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jplProposalHeaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, jtfTitle)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, jtfName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 436, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jplProposalHeaderPanelLayout.createSequentialGroup()
                                .add(jcbRank, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(25, 25, 25)
                                .add(jLabel9)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jcbPI, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 177, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(jplPriorityOffsetPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(jplProposalHeaderPanelLayout.createSequentialGroup()
                        .add(jcbEnabled)
                        .add(18, 18, 18)
                        .add(jcbAllowUrgent)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jcbAllowFixed))
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 502, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jplProposalHeaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, dtepActivation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, dtepExpiry, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jplProposalHeaderPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel13)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jtfProposalCode, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 117, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jplProposalHeaderPanelLayout.setVerticalGroup(
            jplProposalHeaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jplProposalHeaderPanelLayout.createSequentialGroup()
                .add(jLabel1)
                .add(4, 4, 4)
                .add(jplProposalHeaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jtfName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jplProposalHeaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(jtfTitle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jplProposalHeaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel10)
                    .add(jLabel11)
                    .add(jcbTag, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jcbProgramme, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jplProposalHeaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jplProposalHeaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel4)
                        .add(jcbRank, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel9)
                        .add(jcbPI, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jplPriorityOffsetPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(10, 10, 10)
                .add(dtepActivation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(10, 10, 10)
                .add(dtepExpiry, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jplProposalHeaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jtfProposalCode, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel13))
                .add(11, 11, 11)
                .add(jLabel8)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 113, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jplProposalHeaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jcbEnabled)
                    .add(jcbAllowUrgent)
                    .add(jcbAllowFixed))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jplProposalAccountsHeaderPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel5.setFont(new java.awt.Font("Lucida Grande", 1, 10)); // NOI18N
        jLabel5.setText("PROPOSAL ACCOUNTS");

        org.jdesktop.layout.GroupLayout jplTableContainerPanelLayout = new org.jdesktop.layout.GroupLayout(jplTableContainerPanel);
        jplTableContainerPanel.setLayout(jplTableContainerPanelLayout);
        jplTableContainerPanelLayout.setHorizontalGroup(
            jplTableContainerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );
        jplTableContainerPanelLayout.setVerticalGroup(
            jplTableContainerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout jplProposalAccountsHeaderPanelLayout = new org.jdesktop.layout.GroupLayout(jplProposalAccountsHeaderPanel);
        jplProposalAccountsHeaderPanel.setLayout(jplProposalAccountsHeaderPanelLayout);
        jplProposalAccountsHeaderPanelLayout.setHorizontalGroup(
            jplProposalAccountsHeaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jplProposalAccountsHeaderPanelLayout.createSequentialGroup()
                .add(jplProposalAccountsHeaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jplProposalAccountsHeaderPanelLayout.createSequentialGroup()
                        .add(jLabel5)
                        .add(0, 421, Short.MAX_VALUE))
                    .add(jplProposalAccountsHeaderPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jplTableContainerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jplProposalAccountsHeaderPanelLayout.setVerticalGroup(
            jplProposalAccountsHeaderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jplProposalAccountsHeaderPanelLayout.createSequentialGroup()
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jplTableContainerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jplProposalHeaderPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 13, Short.MAX_VALUE)
                .add(jplProposalAccountsHeaderPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jplProposalAccountsHeaderPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jplProposalHeaderPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(65, 65, 65))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jbtnCreate)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jbtnCancel))
            .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 518, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jbtnCreate)
                    .add(jbtnCancel)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jbtnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCreateActionPerformed

    if (guiState == CREATING_PROPOSAL) {
        if (createProposal()) {
            try {
                setUpToCreateAccounts();
            } catch (Phase2Exception ex) {
                JOptionPane.showMessageDialog(this, "An error occurred trying to ready the proposal accounts options: " + ex.getMessage());
                ex.printStackTrace();
                logger.error(ex);
            }
        } else {
            //no change to state model.
        }
    } else {
        // i.e. guiState == CREATING_ACCOUNTS
        try {
            createAccounts();
            showProposalAccountsDialog();
        } catch (Phase2Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred trying to create the proposal accounts: " + ex.getMessage());
            ex.printStackTrace();
            logger.error(ex);
        }
    }

}//GEN-LAST:event_jbtnCreateActionPerformed

    private void showProposalAccountsDialog() {
        
        System.err.println("showProposalAccountsDialog()");
        
        boolean allowAccountsEditing = Session.getInstance().getUser().isSuperUser();
        AccountsDialog dialog = new AccountsDialog(true, xProposal, allowAccountsEditing);
        dialog.setVisible(true);

        //blocks

        dialog.setVisible(false);
        dialog.dispose();
    }

    private boolean createProposal() {

        String proposalName = jtfName.getText().trim();
        String title = jtfTitle.getText().trim();
        String piName = (String) jcbPI.getSelectedItem();
        String scienceAbstract;
        String code;
        int priority;

        boolean allowUrgent, allowFixed, proposalIsEnabled;

        if (proposalName.length() == 0) {
            JOptionPane.showMessageDialog(this, "Please set a name for the proposal.");
            return false;
        }
        if (title.length() == 0) {
            JOptionPane.showMessageDialog(this, "Please set a title for the proposal.");
            return false;
        }

        try {
            if (proposalExists(proposalName)) {
                JOptionPane.showMessageDialog(this, "A proposal with that name already exists, please enter a different name.");
                return false;
            }
        } catch (Phase2Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred.");
            ex.printStackTrace();
            logger.error(ex);
            return false;
        }

        long activationDate, expiryDate;
        try {
            activationDate = dtepActivation.getTime();
            expiryDate = dtepExpiry.getTime();
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Please enter dates in a valid format. Proposal wasn't submitted.");
            return false;
        }

        if (activationDate >= expiryDate) {
            JOptionPane.showMessageDialog(this, "Please make sure the activation date is before the expiry date.");
            return false;
        }

        String priorityOffsetString = jtfPriorityOffset.getText();
        double priorityOffset = 0;
        try {
            priorityOffset = Double.parseDouble(priorityOffsetString);
            priorityOffset = Rounder.round(priorityOffset, 2);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric value for priority offset.");
            return false;
        }

        if ((priorityOffset > 7d) | (priorityOffset < -7d)) {
            JOptionPane.showMessageDialog(this, "Please enter a priority offset between the values +7 and -7.");
            return false;
        }

        //check that a semester exists for the given activation and expiry dates
    /*
         try {
         AccountModelClient accountModelClient = new AccountModelClient(Const.PROPOSAL_ACCOUNT_SERVICE);
        
         ISemester activationSemester = accountModelClient.getSemesterOfDate(activationDate);
         ISemester expirySemester = accountModelClient.getSemesterOfDate(expiryDate);
         if (activationSemester == null) {
         JOptionPane.showMessageDialog(this, "The activation date entered is outside the current permitted semester range (00A to 19B). Proposal not submitted.");
         return false;
         }
         if (expirySemester == null) {
         JOptionPane.showMessageDialog(this, "The expiry date entered is outside the current permitted semester range (00A to 19B). Proposal not submitted.");
         return false;
         }
         } catch (Exception e) {
         JOptionPane.showMessageDialog(this, "An error occurred accessing the Accounting system, proposal not submitted");
         return false;
         }
         */

        switch (jcbRank.getSelectedIndex()) {
            case 0:
                priority = IProposal.PRIORITY_A;
                break;
            case 1:
                priority = IProposal.PRIORITY_B;
                break;
            case 2:
                priority = IProposal.PRIORITY_C;
                break;
            case 3:
                priority = IProposal.PRIORITY_Z;
                break;
            default:
                priority = IProposal.PRIORITY_Z;
        }

        code = jtfProposalCode.getText();

        scienceAbstract = jtaScience.getText();

        allowUrgent = jcbAllowUrgent.isSelected();
        allowFixed = jcbAllowFixed.isSelected();
        proposalIsEnabled = jcbEnabled.isSelected();

        long key = -1;

        xProposal.setName(proposalName);
        xProposal.setTitle(title);
        xProposal.setPriority(priority);
        xProposal.setScienceAbstract(scienceAbstract);
        xProposal.setAllowUrgentGroups(allowUrgent);
        xProposal.setAllowFixedGroups(allowFixed);
        xProposal.setEnabled(proposalIsEnabled);
        xProposal.setPriorityOffset(priorityOffset);
        xProposal.setTypeCode(code);
        long tagId, progId;

        try {
            if (tag != null) {
                tagId = tag.getID();
            } else {
                tagId = getTagId((String) jcbTag.getSelectedItem());
            }

            if (program != null) {
                progId = program.getID();
            } else {
                progId = getProgId((String) jcbProgramme.getSelectedItem());
            }

            xProposal.setActivationDate(activationDate);
            xProposal.setExpiryDate(expiryDate);
            long pid = Phase2ModelClient.getInstance().addProposal(tagId, progId, xProposal);
            xProposal.setID(pid);

            if (!piName.equals(NO_PI_SET)) {
                IUser piUser = (IUser) allUsersList.get(jcbPI.getSelectedIndex());

                XAccessPermission accessPermission = new XAccessPermission();
                accessPermission.setProposalID(pid);
                accessPermission.setUserID(piUser.getID());
                accessPermission.setUserRole(IAccessPermission.PRINCIPLE_INVESTIGATOR_ROLE);
                try {
                    long aid = AccessModelClient.getInstance().addPermission(accessPermission);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    logger.error(ex);
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                    JOptionPane.showMessageDialog(this, "creating the access permission failed");
                    return true; //still created the proposal
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            JOptionPane.showMessageDialog(this, ex.getMessage());
            JOptionPane.showMessageDialog(this, "creating new proposal failed");

            //it's in xProposal and is referenceable fro here on
            return false;
        }

        JOptionPane.showMessageDialog(this, "New proposal was created");
        return true;
    }

    private void setUpToCreateAccounts() throws Phase2Exception {
        guiState = CREATING_ACCOUNTS;

        setUpSemesters();
        setUpAccountCreationTable();

        //swap the panels so that we're creating accounts
        jplProposalHeaderPanel.setVisible(false);
        jplProposalAccountsHeaderPanel.setVisible(true);
        
        jbtnCreate.doClick();
    }

    private void setUpSemesters() throws Phase2Exception {
        //set up semester objects
        AccountModelClient proposalAccountModelClient = new AccountModelClient(Const.PROPOSAL_ACCOUNT_SERVICE);

        proposalActivationSemesterPeriod = proposalAccountModelClient.getSemesterPeriodOfDate(xProposal.getActivationDate());
        proposalExpirySemesterPeriod = proposalAccountModelClient.getSemesterPeriodOfDate(xProposal.getActivationDate());
        currentSemesterPeriod = proposalAccountModelClient.getSemesterPeriodOfDate(new Date().getTime());

    }

    private void setUpAccountCreationTable() throws Phase2Exception {
        //add the JTable here - created from the xProposal object
        
        createProposalAccountsTableModel = new CreateProposalAccountsTableModel(xProposal);
        
        JTable proposalAccountsTable = new JTable(createProposalAccountsTableModel);
        proposalAccountsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        proposalAccountsTable.setPreferredScrollableViewportSize(new Dimension(500, 450));
        proposalAccountsTable.setFillsViewportHeight(true);
  
        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(proposalAccountsTable);
 
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
        
        proposalAccountsTable.validate();
        proposalAccountsTable.repaint();
        
    }

    private void createAccounts() throws Phase2Exception {

        boolean atLeastOneAccountWasCreated = false;
        
        for (int r=0; r<createProposalAccountsTableModel.getRowCount(); r++) {
            
            SemesterSelectionTableLine semesterSelectionTableLine = createProposalAccountsTableModel.getSemesterSelectionTableLine(r);
            ISemester semester = semesterSelectionTableLine.getSemester();
            boolean selected = semesterSelectionTableLine.isSelected();

            if (selected) {    
                atLeastOneAccountWasCreated = true;
                
                XAccount account = new XAccount();
                account.setChargeable(true);
                accountModelClient.addAccount(xProposal.getID(), semester.getID(), account);
            }
            
            //System.err.println(semesterSelectionTableLine);
        }
                
        /*
         String accountsCreationSelection = accountOptionsButtonGroup.getSelection().getActionCommand();
         AccountModelClient accountModelClient = new AccountModelClient(Const.PROPOSAL_ACCOUNT_SERVICE);

         boolean accountsWereCreated = false;

         //semesters are: activationSemester, expirySemester, currentSemester, nextSemester;
         if (accountsCreationSelection.equalsIgnoreCase(OPTION_1)) {
         //no accounts
         } else if (accountsCreationSelection.equalsIgnoreCase(OPTION_2)) {
         //accounts of current lifetime of proposal
         accountModelClient.addAllAccountsBetweenSemesters(xProposal.getID(), activationSemester.getID(), expirySemester.getID());
         accountsWereCreated = true;
         } else if (accountsCreationSelection.equalsIgnoreCase(OPTION_3)) {
         //from next semester to end of proposal
         accountModelClient.addAllAccountsBetweenSemesters(xProposal.getID(), nextSemester.getID(), expirySemester.getID());
         accountsWereCreated = true;
         } else if (accountsCreationSelection.equalsIgnoreCase(OPTION_4)) {
         //this semester only
         accountModelClient.addAllAccountsBetweenSemesters(xProposal.getID(), currentSemester.getID(), currentSemester.getID());
         accountsWereCreated = true;
         } else if (accountsCreationSelection.equalsIgnoreCase(OPTION_5)) {
         //next semester only
         accountModelClient.addAllAccountsBetweenSemesters(xProposal.getID(), nextSemester.getID(), nextSemester.getID());
         accountsWereCreated = true;
         } else if (accountsCreationSelection.equalsIgnoreCase(OPTION_6)) {
         //all semesters (now to 19B)
         accountModelClient.addAllAccountsBetweenSemesters(xProposal.getID(), currentSemester.getID(), 40);
         accountsWereCreated = true;
         }
         */
         if (atLeastOneAccountWasCreated) {
            JOptionPane.showMessageDialog(this, "The accounts were successfully created");
         } else {
            JOptionPane.showMessageDialog(this, "No accounts were created for the proposal");
         }

         this.setVisible(false);
         this.dispose();

         MainFrame.getInstance().receiveNewTreeObject(xProposal);
         
    }

    private boolean proposalExists(String proposalName) throws Phase2Exception {
        return Phase2ModelClient.getInstance().findProposal(proposalName) != null;
    }

    private long getTagId(String tagName) throws Phase2Exception {
        ITag tag = Phase2ModelClient.getInstance().findTag(tagName);
        return tag.getID();
    }

    private long getProgId(String progName) throws Phase2Exception {
        IProgram program = Phase2ModelClient.getInstance().findProgram(progName);
        return program.getID();
    }

private void jbtnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelActionPerformed
    this.setVisible(false);
    this.dispose();

}//GEN-LAST:event_jbtnCancelActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ngat.beans.guibeans.DateTimeEditorPanel dtepActivation;
    private ngat.beans.guibeans.DateTimeEditorPanel dtepExpiry;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbtnCancel;
    private javax.swing.JButton jbtnCreate;
    private javax.swing.JCheckBox jcbAllowFixed;
    private javax.swing.JCheckBox jcbAllowUrgent;
    private javax.swing.JCheckBox jcbEnabled;
    private javax.swing.JComboBox jcbPI;
    private javax.swing.JComboBox jcbProgramme;
    private javax.swing.JComboBox jcbRank;
    private javax.swing.JComboBox jcbTag;
    private javax.swing.JPanel jplPriorityOffsetPanel;
    private javax.swing.JPanel jplProposalAccountsHeaderPanel;
    private javax.swing.JPanel jplProposalHeaderPanel;
    private javax.swing.JPanel jplTableContainerPanel;
    private javax.swing.JTextArea jtaScience;
    private javax.swing.JTextField jtfName;
    private javax.swing.JTextField jtfPriorityOffset;
    private javax.swing.JTextField jtfProposalCode;
    private javax.swing.JTextField jtfTitle;
    // End of variables declaration//GEN-END:variables
}
