/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.model;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import ngat.phase2.IInstrumentConfig;
import ngat.phase2.IInstrumentConfig;

/**
 *
 * @author nrc
 */
public class InstrumentConfigListModel implements ListModel {

    private List instrumentConfigList;
    
    public InstrumentConfigListModel() {
        this.instrumentConfigList = new ArrayList();
    }
    
    public InstrumentConfigListModel(List instrumentConfigList) {
        this.instrumentConfigList = instrumentConfigList;
    }
    
    public int getSize() {
        return instrumentConfigList.size();
    }

    public IInstrumentConfig getInstrumentConfig(int i) {
        if (i == -1) {
            return null;
        }
        return (IInstrumentConfig)instrumentConfigList.get(i); 
    }
    
    public Object getElementAt(int i) {
        IInstrumentConfig instrumentConfig = getInstrumentConfig(i);
        
        String description = "";
        description += instrumentConfig.getName();// + " (id=" + target.getID() + ")";
        
        return description;
    }

    public List getInstrumentConfigList() {
        return instrumentConfigList;
    }
    
    public void addListDataListener(ListDataListener arg0) {
        
    }

    public void removeListDataListener(ListDataListener arg0) {
        
    }

}
