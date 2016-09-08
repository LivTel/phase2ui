/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.panel;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author nrc
 */
public class AccountDeleteButtonCellRenderer implements TableCellRenderer {


    /** Creates new form IncDecPanel */
    public AccountDeleteButtonCellRenderer() {
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        
        AccountDeleteButtonPanel accountDeleteButtonPanel = new AccountDeleteButtonPanel(null, value, null);
        return accountDeleteButtonPanel;
    }

}
