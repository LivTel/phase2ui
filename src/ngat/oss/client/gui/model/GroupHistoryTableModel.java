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
import ngat.oss.client.HistoryModelClient;
import ngat.oss.client.gui.frame.MainFrame;
import ngat.oss.client.gui.wrapper.GroupHistoryTableLineEntry;
import ngat.phase2.IHistoryItem;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class GroupHistoryTableModel extends AbstractTableModel {

    static Logger logger = Logger.getLogger(GroupHistoryTableModel.class);

    private List groupHistoryList = new ArrayList();

    private long groupId;

    public GroupHistoryTableModel(long groupId) {
        this.groupId = groupId;
        HistoryModelClient historyModelClient  = HistoryModelClient.getInstance();
        List historyItemsList = null;
        try {
            historyItemsList = historyModelClient.listHistoryItems(groupId);
            Iterator hili = historyItemsList.iterator();
            while (hili.hasNext()) {
                IHistoryItem historyItem = (IHistoryItem) hili.next();

                GroupHistoryTableLineEntry groupHistoryTableLineEntry = new GroupHistoryTableLineEntry(historyItem);
                groupHistoryList.add(groupHistoryTableLineEntry);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            JOptionPane.showMessageDialog(MainFrame.getInstance(), ex);
        }
    }

    private void addTableLineEntry(GroupHistoryTableLineEntry groupHistoryTableLineEntry) {
        groupHistoryList.add(groupHistoryTableLineEntry);
    }
    
    public int getRowCount() {
        return groupHistoryList.size();
    }

    public int getColumnCount() {
        return GroupHistoryTableLineEntry.getColumnCount();
    }

    public String getColumnName(int col) {
        return GroupHistoryTableLineEntry.getColumnName(col);
    }

    public Object getValueAt(int row, int col) {
        GroupHistoryTableLineEntry groupHistoryTableLineEntry = (GroupHistoryTableLineEntry) groupHistoryList.get(row);
        return groupHistoryTableLineEntry.getvalueAt(col);
    }
    
    public String toString() {
        String s = this.getClass().getName();
        s += "[";
        Iterator i = groupHistoryList.iterator();
        boolean addedEntry = false;
        while (i.hasNext()) {
            GroupHistoryTableLineEntry groupHistoryTableLineEntry = (GroupHistoryTableLineEntry) i.next();
            s += groupHistoryTableLineEntry + " | ";
            addedEntry = true;
        }
        if (addedEntry) {
            s = s.substring(0, s.length() - 3);
        }
        s += "]";
        return s;
    }
}
