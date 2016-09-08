/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.panel;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import ngat.oss.client.AccountModelClient;
import ngat.oss.client.gui.wrapper.SemesterAccountWrapper;

/**
 *
 * @author nrc
 */
public class AccountIncDecPanelCellRenderer implements TableCellRenderer {

    private AccountModelClient accountModelClient;

    private int allocatedOrConsumedColumn; //one of TransactionWrapper.ALLOCATED | CONSUMED

    /** Creates new form AccountIncDecPanel */
    public AccountIncDecPanelCellRenderer(AccountModelClient accountModelClient, int allocatedOrConsumedColumn) {
        this.accountModelClient = accountModelClient;
        this.allocatedOrConsumedColumn = allocatedOrConsumedColumn;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

        //System.err.println(this.getClass().getName() + ".getTableCellRendererComponent(table, value=" + value + ", isSelected=" + isSelected + ", hasFocus=" + hasFocus + ", row=" + row + ", col=" + col);
        
        SemesterAccountWrapper semesterAccountWrapper = (SemesterAccountWrapper) value;
        return new AccountIncDecPanel(accountModelClient, allocatedOrConsumedColumn, semesterAccountWrapper);
    }

}
