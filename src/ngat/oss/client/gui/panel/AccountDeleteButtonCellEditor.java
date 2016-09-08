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
import ngat.oss.client.gui.dialog.AccountsDialog;
import ngat.oss.client.gui.reference.Session;

/**
 *
 * @author nrc
 */
public class AccountDeleteButtonCellEditor extends AbstractCellEditor implements TableCellEditor {

    private AccountModelClient accountModelClient;

    private AccountIncDecPanel incDecPanel;
    private AccountsDialog accountsDialog;

    /** Creates new form AccountIncDecPanel */
    public AccountDeleteButtonCellEditor(AccountModelClient accountModelClient, AccountsDialog accountsDialog) {
        this.accountModelClient = accountModelClient;
        this.accountsDialog = accountsDialog;
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
        AccountDeleteButtonPanel accountDeleteButtonPanel = new AccountDeleteButtonPanel(accountModelClient, value, accountsDialog);
        return accountDeleteButtonPanel;
    }

}
