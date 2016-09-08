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
import ngat.oss.client.gui.wrapper.SemesterTagsTableLineEntry;
import ngat.oss.reference.AccountTypes;
import ngat.phase2.IAccount;
import ngat.phase2.ISemester;
import ngat.phase2.util.SemesterAccountEntry;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class SemesterTagsTableModel extends DefaultTableModel {

    static Logger logger = Logger.getLogger(SemesterTagsTableModel.class);

    private List semesterTagsTableLineEntries = new ArrayList();
    private List objectsRepresented = new ArrayList();

    public SemesterTagsTableModel() {
        //do nothing
    }

    public SemesterTagsTableModel(ISemester semester, AccountModelClient accountModelClient) {
        try {

            List accountEntriesList= accountModelClient.listAccountEntriesOfSemester(semester.getID());
            Iterator aeli = accountEntriesList.iterator();
            while (aeli.hasNext()) {
                SemesterAccountEntry semesterAccountEntry = (SemesterAccountEntry) aeli.next();
                IAccount account = semesterAccountEntry.getAccount();
                SemesterTagsTableLineEntry semesterTagsTableLineEntry = new SemesterTagsTableLineEntry(semester, semesterAccountEntry);
                semesterTagsTableLineEntries.add(semesterTagsTableLineEntry);
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
        if (semesterTagsTableLineEntries == null) {
            return 0;
        }
        return semesterTagsTableLineEntries.size();
    }

    public int getColumnCount() {
        return SemesterTagsTableLineEntry.getColumnCount();
    }

    public String getColumnName(int col) {
        return SemesterTagsTableLineEntry.getColumnName(col);
    }

    public boolean isCellEditable(int row, int col) {
        switch(col) {
            case SemesterTagsTableLineEntry.ALLOCATED_COLUMN:
                return true;
            case SemesterTagsTableLineEntry.CONSUMED_COLUMN:
                return true;
            default:
                return false;
        }
    }

    public Object getValueAt(int row, int col) {
        if (semesterTagsTableLineEntries == null) {
            return null;
        }
        SemesterTagsTableLineEntry semesterTagsTableLineEntry = (SemesterTagsTableLineEntry) semesterTagsTableLineEntries.get(row);
        Object value =  semesterTagsTableLineEntry.getvalueAt(col);
        return value;
    }

    public void setValueAt(Object value, int row , int col) {
        SemesterTagsTableLineEntry semesterTagsTableLineEntry = (SemesterTagsTableLineEntry) semesterTagsTableLineEntries.get(row);
        semesterTagsTableLineEntry.setValueAt(value, col);
        //super.setValueAt(value, row, col);
    }

    public String toString() {
        String s = this.getClass().getName();
        s += "[";
        Iterator i = semesterTagsTableLineEntries.iterator();
        boolean addedEntry = false;
        while (i.hasNext()) {
            SemesterTagsTableLineEntry itemAccountsTableLineEntry = (SemesterTagsTableLineEntry) i.next();
            s += itemAccountsTableLineEntry + " | ";
            addedEntry = true;
        }
        if (addedEntry) {
            s = s.substring(0, s.length() - 3);
        }
        s += "]";
        return s;
    }

    /*
    private List semesterTagsTableLineEntries = new ArrayList();
    private List objectsRepresented = new ArrayList();
    private ISemester semester;
    private AccountModelClient accountModelClient;

    public SemesterTagsTableModel() {
        //do nothing
    }
    
    public SemesterTagsTableModel(ISemester semester, AccountModelClient accountModelClient) {
        this.semester = semester;
        this.accountModelClient = accountModelClient;

        try {
            List accountEntriesList= accountModelClient.listAccountEntriesOfSemester(semester.getID());
            Iterator aeli = accountEntriesList.iterator();
            while (aeli.hasNext()) {
                SemesterAccountEntry semesterAccountEntry = (SemesterAccountEntry) aeli.next();
                IAccount account = semesterAccountEntry.getAccount();
                //only allow top level account allocations to be shown
                SemesterTagsTableLineEntry semesterTagsTableLineEntry = new SemesterTagsTableLineEntry(semester, semesterAccountEntry);
                semesterTagsTableLineEntries.add(semesterTagsTableLineEntry);
                objectsRepresented.add(semesterAccountEntry.getColumn1());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            JOptionPane.showMessageDialog(MainFrame.getInstance(), ex);
        }
 
    }

    public List getObjectsRepresented() {
        //a list of tag names in the table
       return  objectsRepresented;
    }

    public int getRowCount() {
        if (semesterTagsTableLineEntries == null) {
            return 0;
        }
        return semesterTagsTableLineEntries.size();
    }

    public int getColumnCount() {
        return SemesterTagsTableLineEntry.getColumnCount();
    }

    public String getColumnName(int col) {
        return SemesterTagsTableLineEntry.getColumnName(col);
    }

    public Object getValueAt(int row, int col) {
        if (semesterTagsTableLineEntries == null) {
            return null;
        }
        SemesterTagsTableLineEntry semesterTagsTableLineEntry = (SemesterTagsTableLineEntry) semesterTagsTableLineEntries.get(row);
        return semesterTagsTableLineEntry.getvalueAt(col);
    }
    
    public String toString() {
        String s = this.getClass().getName();
        s += "[";
        Iterator i = semesterTagsTableLineEntries.iterator();
        boolean addedEntry = false;
        while (i.hasNext()) {
            SemesterTagsTableLineEntry semesterTagsTableLineEntry = (SemesterTagsTableLineEntry) i.next();
            s += semesterTagsTableLineEntry + " | ";
            addedEntry = true;
        }
        if (addedEntry) {
            s = s.substring(0, s.length() - 3);
        }
        s += "]";
        return s;
    }
    
     */
}

