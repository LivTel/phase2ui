/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import ngat.oss.client.AccountModelClient;
import ngat.oss.client.gui.frame.MainFrame;
import ngat.oss.client.gui.wrapper.SemesterProposalsTableLineEntry;
import ngat.oss.reference.AccountTypes;
import ngat.phase2.IAccount;
import ngat.phase2.ISemester;
import ngat.phase2.util.SemesterAccountEntry;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class SemesterProposalsTableModel extends DefaultTableModel {

    static Logger logger = Logger.getLogger(SemesterProposalsTableModel.class);

    private List semesterProposalsTableLineEntries = new ArrayList();
    private List objectsRepresented = new ArrayList();
    
    public SemesterProposalsTableModel() {
        //do nothing
    }
    
    public SemesterProposalsTableModel(ISemester semester, AccountModelClient accountModelClient) {
        try {
            //debug
            /*
            String propName = "AccTestProp1";
            List accountsList = accountModelClient.listAccountsOfSemester(semester.getID());
            Iterator ali = accountsList.iterator();
            while (ali.hasNext()) {
                IAccount account = (IAccount) ali.next();
                long proposalId = accountModelClient.getAccountOwnerID(account.getID());
                IProposal proposal = Phase2ModelClient.getInstance().getProposal(proposalId);
                if (proposal == null) {
                    System.err.println("found Proposal[" + null + "] Account[" + account + "]");
                } else {
                    if (proposal.getName().equals(propName)) {
                        System.err.println("found Proposal[" + proposal + "] Account[" + account + "]");
                    }
                }
            }
            */
            
            List accountEntriesList= accountModelClient.listAccountEntriesOfSemester(semester.getID());
            Iterator aeli = accountEntriesList.iterator();
            while (aeli.hasNext()) {
                SemesterAccountEntry semesterAccountEntry = (SemesterAccountEntry) aeli.next();
                IAccount account = semesterAccountEntry.getAccount();
                
                SemesterProposalsTableLineEntry semesterProposalsTableLineEntry = new SemesterProposalsTableLineEntry(semester, semesterAccountEntry);
                semesterProposalsTableLineEntries.add(semesterProposalsTableLineEntry);
                objectsRepresented.add(semesterAccountEntry.getColumn1());
                
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            JOptionPane.showMessageDialog(MainFrame.getInstance(), ex);
        }
    }

    public void fireTableDataChanged() {
        super.fireTableDataChanged();
    }

    public List getObjectsRepresented() {
        //a list of proposal names in the table
       return  objectsRepresented;
    }

    public int getRowCount() {
        if (semesterProposalsTableLineEntries == null) {
            return 0;
        }
        return semesterProposalsTableLineEntries.size();
    }

    public int getColumnCount() {
        return SemesterProposalsTableLineEntry.getColumnCount();
    }

    public String getColumnName(int col) {
        return SemesterProposalsTableLineEntry.getColumnName(col);
    }

    public boolean isCellEditable(int row, int col) {
        switch(col) {
            case SemesterProposalsTableLineEntry.ALLOCATED_COLUMN:
                return true;
            case SemesterProposalsTableLineEntry.CONSUMED_COLUMN:
                return true;
            default:
                return false;
        }
    }

    public Object getValueAt(int row, int col) {
        if (semesterProposalsTableLineEntries == null) {
            return null;
        }
        SemesterProposalsTableLineEntry itemAccountsTableLineEntry = (SemesterProposalsTableLineEntry) semesterProposalsTableLineEntries.get(row);
        Object value =  itemAccountsTableLineEntry.getValueAt(col);
        return value;
    }

    public void setValueAt(Object value, int row , int col) {
        SemesterProposalsTableLineEntry itemAccountsTableLineEntry = (SemesterProposalsTableLineEntry) semesterProposalsTableLineEntries.get(row);
        
        itemAccountsTableLineEntry.setValueAt(value, col);
        //super.setValueAt(value, row, col);
    }

    public String toString() {
        String s = this.getClass().getName();
        s += "[";
        Iterator i = semesterProposalsTableLineEntries.iterator();
        boolean addedEntry = false;
        while (i.hasNext()) {
            SemesterProposalsTableLineEntry itemAccountsTableLineEntry = (SemesterProposalsTableLineEntry) i.next();
            s += itemAccountsTableLineEntry + " | ";
            addedEntry = true;
        }
        if (addedEntry) {
            s = s.substring(0, s.length() - 3);
        }
        s += "]";
        return s;
    }

}

