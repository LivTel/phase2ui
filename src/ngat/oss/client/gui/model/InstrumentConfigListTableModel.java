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

import ngat.oss.client.gui.wrapper.InstrumentConfigTableLineEntry;
import ngat.phase2.IInstrumentConfig;
import ngat.phase2.IProgram;
import org.apache.log4j.Logger;


/**
 *
 * @author nrc
 */
public class InstrumentConfigListTableModel extends AbstractTableModel {

    static Logger logger = Logger.getLogger(InstrumentConfigListTableModel.class);

    private List instrumentConfigTableLineEntrysList = new ArrayList();

    private IProgram program;

    public InstrumentConfigListTableModel(IProgram program) {
        this.program = program;
        Phase2ModelClient phase2ModelModelClient  = Phase2ModelClient.getInstance();
        List instrumentConfigList = null;
        try {
            instrumentConfigList = phase2ModelModelClient.listInstrumentConfigs(program.getID());
            Iterator hili = instrumentConfigList.iterator();
            while (hili.hasNext()) {
                IInstrumentConfig instrumentConfig = (IInstrumentConfig) hili.next();

                InstrumentConfigTableLineEntry instrumentConfigTableLineEntry = new InstrumentConfigTableLineEntry(instrumentConfig);
                instrumentConfigTableLineEntrysList.add(instrumentConfigTableLineEntry);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            JOptionPane.showMessageDialog(MainFrame.getInstance(), ex);
        }
    }

    private void addTableLineEntry(InstrumentConfigTableLineEntry instrumentConfigTableLineEntry) {
        instrumentConfigTableLineEntrysList.add(instrumentConfigTableLineEntry);
    }
    
    public int getRowCount() {
        return instrumentConfigTableLineEntrysList.size();
    }

    public int getColumnCount() {
        return InstrumentConfigTableLineEntry.getColumnCount();
    }

    public String getColumnName(int col) {
        return InstrumentConfigTableLineEntry.getColumnName(col);
    }

    public Object getValueAt(int row, int col) {
        InstrumentConfigTableLineEntry instrumentConfigTableLineEntry = (InstrumentConfigTableLineEntry) instrumentConfigTableLineEntrysList.get(row);
        return instrumentConfigTableLineEntry.getvalueAt(col);
    }

    public IInstrumentConfig getInstrumentConfigInRow(int row) {
        if (row < 0) {
            return null;
        }
        InstrumentConfigTableLineEntry instrumentConfigTableLineEntry = (InstrumentConfigTableLineEntry) instrumentConfigTableLineEntrysList.get(row);
        return instrumentConfigTableLineEntry.getInstrumentConfig();
    }

    public String toString() {
        String s = this.getClass().getName();
        s += "[";
        Iterator i = instrumentConfigTableLineEntrysList.iterator();
        boolean addedEntry = false;
        while (i.hasNext()) {
            InstrumentConfigTableLineEntry instrumentConfigTableLineEntry = (InstrumentConfigTableLineEntry) i.next();
            s += instrumentConfigTableLineEntry + " | ";
            addedEntry = true;
        }
        if (addedEntry) {
            s = s.substring(0, s.length() - 3);
        }
        s += "]";
        return s;
    }
}
