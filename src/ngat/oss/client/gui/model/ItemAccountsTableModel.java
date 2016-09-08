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
import ngat.oss.client.gui.reference.Session;
import ngat.oss.client.gui.wrapper.ItemAccountsTableLineEntry;
import ngat.oss.client.gui.wrapper.SemesterAccountWrapper;
import ngat.oss.reference.AccountTypes;
import ngat.oss.reference.Const;
import ngat.phase2.IAccount;
import ngat.phase2.IProposal;
import ngat.phase2.ISemester;
import ngat.phase2.ITag;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class ItemAccountsTableModel extends DefaultTableModel {

    static Logger logger = Logger.getLogger(ItemAccountsTableModel.class);

    private List itemAccountsList;
    private long ownerId;
    private AccountModelClient accountModelClient;

    public ItemAccountsTableModel() {
        //do nothing
    }
    
    public ItemAccountsTableModel(IProposal proposal, AccountModelClient accountModelClient) {
        this.ownerId = proposal.getID();
        this.accountModelClient = accountModelClient;
        setUpItemsList(proposal.getID(), Const.PROPOSAL_ACCOUNT_SERVICE, accountModelClient);
    }
    
    public ItemAccountsTableModel(ITag tag, AccountModelClient accountModelClient) {
        this.ownerId = tag.getID();
        this.accountModelClient = accountModelClient;
        setUpItemsList(tag.getID(), Const.TAG_ACCOUNT_SERVICE, accountModelClient);
    }

    private void setUpItemsList(long id, String itemType, AccountModelClient accountModelClient) {

        itemAccountsList = new ArrayList();

        try {
            List semestersList = accountModelClient.listSemestersForWhichOwnerHasAccounts(id);
            Iterator sli = semestersList.iterator();
            while (sli.hasNext()) {
                ISemester semester = (ISemester) sli.next();
                
                //now use findAccount (singular) and add the single account
                IAccount account = accountModelClient.findAccount(id, semester.getID());
                SemesterAccountWrapper semesterAccountWrapper = new SemesterAccountWrapper(semester, account);
                ItemAccountsTableLineEntry itemAccountsTableLineEntry = new ItemAccountsTableLineEntry(semesterAccountWrapper);
                itemAccountsList.add(itemAccountsTableLineEntry);
                    
                /*
                List accountsList = accountModelClient.listAccounts(id, semester.getID());
                Iterator ali = accountsList.iterator();
                while (ali.hasNext()) {
                    IAccount account = (IAccount) ali.next();    
                    SemesterAccountWrapper semesterAccountWrapper = new SemesterAccountWrapper(semester, account);
                    ItemAccountsTableLineEntry itemAccountsTableLineEntry = new ItemAccountsTableLineEntry(semesterAccountWrapper);
                    itemAccountsList.add(itemAccountsTableLineEntry);
                }
                */
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            JOptionPane.showMessageDialog(MainFrame.getInstance(), ex);
        }
    }

    /*
    private void addTableLineEntry(GroupHistoryTableLineEntry groupHistoryTableLineEntry) {
        groupHistoryList.add(groupHistoryTableLineEntry);
    }
    */

    public int getRowCount() {
        if (itemAccountsList == null) {
            return 0;
        }
        return itemAccountsList.size();
    }

    public int getColumnCount() {
        return ItemAccountsTableLineEntry.getColumnCount();
    }

    public String getColumnName(int col) {
        return ItemAccountsTableLineEntry.getColumnName(col);
    }

    public boolean isCellEditable(int row, int col) {
        if (!Session.getInstance().getUser().isSuperUser()) {
            return false;
        } else {
            switch (col) {
                case ItemAccountsTableLineEntry.ALLOCATED_COLUMN:
                    return true;
                case ItemAccountsTableLineEntry.CONSUMED_COLUMN:
                    return true;
                case ItemAccountsTableLineEntry.DELETE_COLUMN:
                    return true;
            }
        }
        return false;
    }

    public Object getValueAt(int row, int col) {
        if (itemAccountsList == null) {
            return null;
        }
        ItemAccountsTableLineEntry itemAccountsTableLineEntry = (ItemAccountsTableLineEntry) itemAccountsList.get(row);
        return itemAccountsTableLineEntry.getvalueAt(col);
    }

    public void setValueAt(Object value, int row, int col) {

        ItemAccountsTableLineEntry itemAccountsTableLineEntry = (ItemAccountsTableLineEntry) itemAccountsList.get(row);
        itemAccountsTableLineEntry.setValueAt(value, col);
    }

    public String toString() {
        String s = this.getClass().getName();
        s += "[";
        Iterator i = itemAccountsList.iterator();
        boolean addedEntry = false;
        while (i.hasNext()) {
            ItemAccountsTableLineEntry itemAccountsTableLineEntry = (ItemAccountsTableLineEntry) i.next();
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
