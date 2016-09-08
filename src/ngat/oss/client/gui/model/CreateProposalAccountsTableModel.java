/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ngat.oss.client.gui.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import ngat.oss.client.AccountModelClient;
import ngat.oss.client.gui.frame.MainFrame;
import ngat.oss.client.gui.wrapper.SemesterSelectionTableLine;
import ngat.oss.reference.Const;
import ngat.phase2.IProposal;
import ngat.phase2.ISemester;
import ngat.phase2.ISemesterPeriod;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class CreateProposalAccountsTableModel extends DefaultTableModel {

    static Logger logger = Logger.getLogger(CreateProposalAccountsTableModel.class);

    //list of SemesterSelection objects representing the semester and whether to create the account for that semester
    private List semesterSelectionList; //array of semesternames v boolean (checked box)

    private IProposal proposal;
    private AccountModelClient accountModelClient = new AccountModelClient(Const.PROPOSAL_ACCOUNT_SERVICE);

    public CreateProposalAccountsTableModel() {
        //do nothing
    }

    public CreateProposalAccountsTableModel(IProposal proposal) {
        this.proposal = proposal;

        setUpRows();
    }

    private void setUpRows() {

        semesterSelectionList = new ArrayList();

        try {
            ISemesterPeriod nowSemesterPeriod = accountModelClient.getSemesterPeriodOfDate(new Date().getTime());
            ISemester firstNowSemester = nowSemesterPeriod.getFirstSemester();

            ISemesterPeriod activationSemesterPeriod = accountModelClient.getSemesterPeriodOfDate(proposal.getActivationDate());
            ISemesterPeriod expirySemesterPeriod = accountModelClient.getSemesterPeriodOfDate(proposal.getExpiryDate());

            ISemester proposalStartSemester, proposalEndSemester;

            proposalStartSemester = activationSemesterPeriod.getFirstSemester();

            if (expirySemesterPeriod.isOverlap()) {
                proposalEndSemester = expirySemesterPeriod.getSecondSemester();
            } else {
                proposalEndSemester = expirySemesterPeriod.getFirstSemester();
            }

            //loop from the now semester (earliest) until now semester (earliest) + erm, 10 semesters, say
            for (int s = (int) firstNowSemester.getID(); s < (int) firstNowSemester.getID() + 10; s++) {

                ISemester semester = accountModelClient.getSemester(s);

                SemesterSelectionTableLine semesterSelectionTableLine = new SemesterSelectionTableLine();
                semesterSelectionTableLine.setSemester(semester);

                //if the semester is within the lifetime of the proposal, select it, otherwise don't
                if ((semester != null) && (proposalStartSemester != null) & (proposalEndSemester != null)) {
                    if ((semester.getID() >= proposalStartSemester.getID()) && (semester.getID() <= proposalEndSemester.getID())) {
                        semesterSelectionTableLine.setSelected(true);
                    } else {
                        semesterSelectionTableLine.setSelected(false);
                    }
                }

                //add the semester selection to the list
                semesterSelectionList.add(semesterSelectionTableLine);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            JOptionPane.showMessageDialog(MainFrame.getInstance(), ex);
        }
    }

    public Class getColumnClass(int col) {
        return SemesterSelectionTableLine.COL_CLASSES[col];
    }

    public int getColumnCount() {
        return SemesterSelectionTableLine.getColumnCount();
    }

    public int getRowCount() {
        if (semesterSelectionList == null) {
            return 0;
        }
        return semesterSelectionList.size();
    }

    public String getColumnName(int col) {
        return SemesterSelectionTableLine.getColumnName(col);
    }

    public boolean isCellEditable(int row, int col) {
        return (col == SemesterSelectionTableLine.SELECTED_COL);
    }

    public Object getValueAt(int row, int col) {
        if (semesterSelectionList == null) {
            return null;
        }
        SemesterSelectionTableLine semesterSelectionTableLine = getSemesterSelectionTableLine(row);
        return semesterSelectionTableLine.getValueAt(col);
    }

    public SemesterSelectionTableLine getSemesterSelectionTableLine(int row) {
        if (semesterSelectionList == null) {
            return null;
        }
        SemesterSelectionTableLine semesterSelectionTableLine = (SemesterSelectionTableLine) semesterSelectionList.get(row);
        return semesterSelectionTableLine;
    }

    public void setValueAt(Object value, int row, int col) {

        SemesterSelectionTableLine semesterSelectionTableLine = (SemesterSelectionTableLine) semesterSelectionList.get(row);
        semesterSelectionTableLine.setValueAt(value, col);
    }

    public String toString() {
        String s = this.getClass().getName();
        s += "[";
        Iterator i = semesterSelectionList.iterator();
        boolean addedEntry = false;
        while (i.hasNext()) {
            SemesterSelectionTableLine semesterSelectionTableLine = (SemesterSelectionTableLine) i.next();
            s += semesterSelectionTableLine + " | ";
            addedEntry = true;
        }
        if (addedEntry) {
            s = s.substring(0, s.length() - 3);
        }
        s += "]";
        return s;
    }
    
}
