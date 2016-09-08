/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.panel;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import ngat.phase2.IAccount;

/**
 *
 * @author nrc
 */
public class BarChartCellRenderer implements TableCellRenderer {


    /** Creates new form IncDecPanel */
    public BarChartCellRenderer() {
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        
        IAccount account = (IAccount) value;
        return new BarChartPanel(account.getAllocated(), account.getConsumed());
    }

}
