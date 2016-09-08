/*
 * UserTreeModel.java
 *
 * Created on 21 November 2007, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ngat.oss.client.gui.tree.sequencetree;

import java.util.List;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import ngat.phase2.ISequenceComponent;
import org.apache.log4j.Logger;
 
/**
 *
 * @author nrc  
 */
public class ObservationSequenceTreeModel implements TreeModel {
    
    static Logger logger = Logger.getLogger(ObservationSequenceTreeModel.class);
    
    private ISequenceComponent rootComponent;
    
    /** Creates a new instance of UserTreeModel */
    public ObservationSequenceTreeModel(ISequenceComponent observationSequence) {
        logger.info("instantiate ObservationSequenceTreeModel(" + observationSequence + ")");
        this.rootComponent = observationSequence;
    }
    
    /*
     *  implement
    */
    public Object getRoot() {
        //logger.info("getRoot()");
        //logger.info("returns " + rootComponent);
        return rootComponent;
    }
    
    /*
     * Returns n'th child of parent as an object
    */
    public Object getChild(Object parent, int n) {
        //logger.info("getChild(" + parent + ", " + n + ")");
        
        ISequenceComponent parentComponent = (ISequenceComponent)parent;
        List childList = parentComponent.listChildComponents();
        ISequenceComponent childComponent = (ISequenceComponent)childList.get(n);
        
        //logger.info("returns " + childComponent);
        return childComponent;
    }
    
    /*
     * Returns number of children of parent
     *  implement
    */
    public int getChildCount(Object parent) {
        //logger.info("getChildCount(" + parent.getClass().getName() + ")");
        
        ISequenceComponent parentComponent = (ISequenceComponent)parent;
        int childCount;
        List childList = parentComponent.listChildComponents();
        if (childList == null)
            childCount = 0;
        else
            childCount = childList.size();
        
        //logger.info("returns " + childCount);
        return childCount;
    }

    public boolean isLeaf(Object node) {
        //logger.info("isLeaf(" + node.getClass().getName()+ ")");
        
        boolean isLeaf = getChildCount(node) == 0;
        
        //logger.info("returns " + isLeaf);
        return isLeaf;
    }
    
    public void valueForPathChanged(TreePath path, Object newValue) {
         //logger.info("valueForPathChanged(" + path + "," + newValue + ")");
         //logger.info("... valueForPathChanged(" + path + "," + newValue + ") not implemented");
    }

    public int getIndexOfChild(Object parent, Object child) {
        //logger.info("getIndexOfChild(" + parent + "," + child + ")");
        //logger.info("... getIndexOfChild(" + parent + "," + child + ") not implemented");
        return 1;
    }

    public void addTreeModelListener(TreeModelListener l) {
        //logger.info("addTreeModelListener()");
        //logger.info("... addTreeModelListener() not implemented");
    }

    public void removeTreeModelListener(TreeModelListener l) {
        //logger.info("removeTreeModelListener()");
        //logger.info("... removeTreeModelListener() not implemented");
    }
}
