/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.panel;

import java.awt.Component;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import ngat.oss.client.AccountModelClient;
import ngat.oss.client.gui.reference.Session;
import ngat.oss.client.gui.wrapper.SemesterAccountWrapper;

/**
 *
 * @author nrc
 */
public class AccountIncDecPanelCellEditor extends AbstractCellEditor implements TableCellEditor {

    private AccountModelClient accountModelClient;

    private int accountColumn; //one of TransactionWrapper.ALLOCATED | CONSUMED
    
    private AccountIncDecPanel incDecPanel;
    private DefaultTableModel tableModel;

    /** Creates new form AccountIncDecPanel */
    public AccountIncDecPanelCellEditor(AccountModelClient accountModelClient, DefaultTableModel tableModel, int accountColumn) {
        this.accountModelClient = accountModelClient;
        this.accountColumn = accountColumn;
        this.tableModel = tableModel;
    }
    
    public Object getCellEditorValue() {
        Object value;
        if (incDecPanel != null) {
            value = incDecPanel.getSemesterAccountWrapper();
        } else {
            value = null;
        }
        //System.err.println("getCellEditorValue() returns " + value);
        return value;
    }

    //required so that the table inserts the cell editor when a mouse click in a relavent cell occurs
    public boolean isCellEditable(EventObject eventObject) {
        return (Session.getInstance().getUser().isSuperUser());
    }

    public boolean shouldSelectCell(EventObject eventObject) {
        return (Session.getInstance().getUser().isSuperUser());
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        SemesterAccountWrapper semesterAccountWrapper = (SemesterAccountWrapper) value;
        incDecPanel = new AccountIncDecPanel(accountModelClient, accountColumn, tableModel, table, this, semesterAccountWrapper);
        return incDecPanel;
    }

}
