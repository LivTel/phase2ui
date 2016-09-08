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
import ngat.phase2.ITarget;

/**
 *
 * @author nrc
 */
public class TargetListModel implements ListModel {

    private List targetList;
    
    public TargetListModel() {
        this.targetList = new ArrayList();
    }
    
    public TargetListModel(List targetList) {
        this.targetList = targetList;
    }
    
    public int getSize() {
        return targetList.size();
    }

    public ITarget getTarget(int i) {
        if (i == -1) {
            return null;
        }
        return (ITarget)targetList.get(i); 
    }
    
    public Object getElementAt(int i) {
        ITarget target = getTarget(i);
        if (target == null) {
            return "null";
        }
        String description = "";
        description += target.getName();// + " (id=" + target.getID() + ")";
        
        return description;
    }

    public List getTargetList() {
        return targetList;
    }
    
    public void removeTarget(int targetIndex) {
        targetList.remove(targetIndex);
    }
    
    public void addListDataListener(ListDataListener arg0) {
        
    }

    public void removeListDataListener(ListDataListener arg0) {
        
    }

}
