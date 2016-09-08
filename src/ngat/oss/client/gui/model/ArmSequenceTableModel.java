/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.model;

import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.table.AbstractTableModel;
import ngat.oss.client.gui.wrapper.WizardPanelTableLineEntry;


/**
 *
 * @author nrc
 */
public class ArmSequenceTableModel extends AbstractTableModel {

    private ArrayList sequenceList = new ArrayList();

    public ArmSequenceTableModel() {
    }

    public void addTableLineEntry(WizardPanelTableLineEntry armSeqTableLineEntry) {
        sequenceList.add(armSeqTableLineEntry);
    }
    
    public ArrayList getData() {
        return sequenceList;
    }

    public int getResultSetSize() {
        return sequenceList.size();
    }

    public int getRowCount() {
        return sequenceList.size();
    }

    public int getColumnCount() {
        return WizardPanelTableLineEntry.getColumnCount();
    }

    public String getColumnName(int col) {
        return WizardPanelTableLineEntry.getColumnName(col);
    }

    public int moveRowUp(int row) {
        if (row == 0) {
            return row;
        }
        WizardPanelTableLineEntry armSeqTableLineEntry = (WizardPanelTableLineEntry) sequenceList.get(row);
        sequenceList.remove(row);
        sequenceList.add(row - 1, armSeqTableLineEntry);
        return row - 1;
    }
    
    public int moveRowDown(int row) {
        if (row == (sequenceList.size() - 1)) {
            return row;
        }
        WizardPanelTableLineEntry armSeqTableLineEntry = (WizardPanelTableLineEntry) sequenceList.get(row);
        sequenceList.remove(row);
        sequenceList.add(row + 1, armSeqTableLineEntry);
        return row + 1;
    }
    
    public void deleteRow(int row) {
        if (sequenceList.size() == 0) {
            return;
        }
        if (row > (sequenceList.size() - 1)) {
            return;
        }
        WizardPanelTableLineEntry armSeqTableLineEntry = (WizardPanelTableLineEntry) sequenceList.get(row);
        sequenceList.remove(row);
    }
    
    public Object getValueAt(int row, int col) {
        WizardPanelTableLineEntry armSeqTableLineEntry = (WizardPanelTableLineEntry) sequenceList.get(row);
        return armSeqTableLineEntry.getvalueAt(col);
    }
    
    public String toString() {
        String s = this.getClass().getName();
        s += "[";
        Iterator i = sequenceList.iterator();
        boolean addedEntry = false;
        while (i.hasNext()) {
            WizardPanelTableLineEntry armSeqTableLineEntry = (WizardPanelTableLineEntry) i.next();
            s += armSeqTableLineEntry + " | ";
            addedEntry = true;
        }
        if (addedEntry) {
            s = s.substring(0, s.length() - 3);
        }
        s += "]";
        return s;
    }
}
