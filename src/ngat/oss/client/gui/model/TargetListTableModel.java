/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import ngat.oss.client.Phase2ModelClient;
import ngat.oss.client.gui.frame.MainFrame;
import ngat.oss.client.gui.wrapper.TargetTableLineEntry;
import ngat.phase2.IProgram;
import ngat.phase2.ITarget;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class TargetListTableModel extends AbstractTableModel {

    static Logger logger = Logger.getLogger(TargetListTableModel.class);

    private List targetTableLineEntrysList = new ArrayList();

    private IProgram program;

    public TargetListTableModel(IProgram program) {
        this.program = program;
        Phase2ModelClient phase2ModelModelClient  = Phase2ModelClient.getInstance();
        List targetList = null;
        try {
            targetList = phase2ModelModelClient.listTargets(program.getID());
            Iterator hili = targetList.iterator();
            while (hili.hasNext()) {
                ITarget target = (ITarget) hili.next();

                TargetTableLineEntry targetTableLineEntry = new TargetTableLineEntry(target);
                targetTableLineEntrysList.add(targetTableLineEntry);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            JOptionPane.showMessageDialog(MainFrame.getInstance(), ex);
        }
    }

    private void addTableLineEntry(TargetTableLineEntry targetTableLineEntry) {
        targetTableLineEntrysList.add(targetTableLineEntry);
    }
    
    public int getRowCount() {
        return targetTableLineEntrysList.size();
    }

    public int getColumnCount() {
        return TargetTableLineEntry.getColumnCount();
    }

    public String getColumnName(int col) {
        return TargetTableLineEntry.getColumnName(col);
    }

    public Object getValueAt(int row, int col) {
        TargetTableLineEntry targetTableLineEntry = (TargetTableLineEntry) targetTableLineEntrysList.get(row);
        return targetTableLineEntry.getvalueAt(col);
    }

    public ITarget getTargetInRow(int row) {
        if (row < 0) {
            return null;
        }
        TargetTableLineEntry targetTableLineEntry = (TargetTableLineEntry) targetTableLineEntrysList.get(row);
        return targetTableLineEntry.getTarget();
    }

    public String toString() {
        String s = this.getClass().getName();
        s += "[";
        Iterator i = targetTableLineEntrysList.iterator();
        boolean addedEntry = false;
        while (i.hasNext()) {
            TargetTableLineEntry targetTableLineEntry = (TargetTableLineEntry) i.next();
            s += targetTableLineEntry + " | ";
            addedEntry = true;
        }
        if (addedEntry) {
            s = s.substring(0, s.length() - 3);
        }
        s += "]";
        return s;
    }
}
