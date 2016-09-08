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
public class CreateTagAccountsTableModel extends DefaultTableModel {

    static Logger logger = Logger.getLogger(CreateTagAccountsTableModel.class);

    private List semesterSelectionList; //array of semesternames v boolean (checked box)
    
    private AccountModelClient accountModelClient;

    public CreateTagAccountsTableModel(AccountModelClient accountModelClient) {
        this.accountModelClient = accountModelClient;
        setUpRows();
    }
    
    private void setUpRows() {

        semesterSelectionList = new ArrayList();
        
        try {
            //want to show table rows from the first now semester until, say 10 semesters into the future.
            //want to highlight all the semesters of now, plus say the next 2
            ISemesterPeriod nowSemesterPeriod = accountModelClient.getSemesterPeriodOfDate(new Date().getTime());
            ISemester firstNowSemester = nowSemesterPeriod.getFirstSemester();
            long firstNowSemesterID = firstNowSemester.getID();
            
            int lastSemesterInTableID = ((int)firstNowSemesterID) + 10;
            
            for (int s = (int)firstNowSemesterID; s < lastSemesterInTableID; s++) {
                ISemester semester = accountModelClient.getSemester(s);
                
                SemesterSelectionTableLine semesterSelectionTableLine = new SemesterSelectionTableLine();
                semesterSelectionTableLine.setSemester(semester);
                
                semesterSelectionTableLine.setSelected(true);
                
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

