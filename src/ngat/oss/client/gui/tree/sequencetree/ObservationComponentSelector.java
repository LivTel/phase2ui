/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.tree.sequencetree;

import ngat.oss.client.gui.tree.datatree.DataTreeSelectionListener;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import ngat.oss.client.gui.frame.DisplayObservationSequenceFrame;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class ObservationComponentSelector implements TreeSelectionListener {
    
    static Logger logger = Logger.getLogger(DataTreeSelectionListener.class);
    
    private JTree tree;
    private DisplayObservationSequenceFrame parentFrame;
    
    /** Creates a new instance of MainTreeSelectionListener */
    public ObservationComponentSelector(JTree tree, DisplayObservationSequenceFrame parentFrame) {
        this.tree = tree;
        this.parentFrame = parentFrame;
    }

    /*
     * value selected in tree has changed
    */
    public void valueChanged(TreeSelectionEvent e) {
        
        //for debug:
        /*
        TreePath path = tree.getSelectionPath();
        if (path == null)
            return;
        
        ISequenceComponent nodeData = (ISequenceComponent)path.getLastPathComponent();
        String m = nodeData.toString();
        */
    }
}
