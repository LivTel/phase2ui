/*
 * MainTreeSelectionListener.java
 *
 * Created on 03 December 2007, 16:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ngat.oss.client.gui.listeners;

import ngat.oss.client.gui.tree.datatree.DataTreeSelectionListener;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import ngat.phase2.IGroup;
import ngat.oss.client.gui.frame.MainFrame;
import ngat.oss.client.gui.panel.ObservationSequenceButtonPanel;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class ControlPanelSelector implements TreeSelectionListener {
    
    static Logger logger = Logger.getLogger(DataTreeSelectionListener.class);
    
    private JTree tree;
    
    /** Creates a new instance of MainTreeSelectionListener */
    public ControlPanelSelector(JTree tree) {
        this.tree = tree;
    }

    /*
     * value selected in tree has changed, update detailPanel
    */
    public void valueChanged(TreeSelectionEvent e) {
        
        TreePath path = tree.getSelectionPath();
        if (path == null)
            return;

        MainFrame.getInstance().getControlPanel().removeAll();

        //System.err.println(path.getLastPathComponent().getClass().getName());

        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object userObject = treeNode.getUserObject();
        
        if (userObject instanceof IGroup) {
            //get group
            IGroup group = (IGroup) userObject;
            MainFrame.getInstance().showControlPanel(new ObservationSequenceButtonPanel(group));
        }
    }
}
