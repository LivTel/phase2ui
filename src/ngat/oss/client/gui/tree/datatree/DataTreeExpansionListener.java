/*
 * MainTreeSelectionListener.java
 *
 * Created on 03 December 2007, 16:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ngat.oss.client.gui.tree.datatree;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class DataTreeExpansionListener implements TreeExpansionListener {
    
    static Logger logger = Logger.getLogger(DataTreeExpansionListener.class);
    
    private JTree tree;
    
    /** Creates a new instance of MainTreeSelectionListener */
    public DataTreeExpansionListener(JTree tree) {
        this.tree = tree;
    }

    public void treeExpanded(TreeExpansionEvent expansionEvent) {
    }

    public void treeCollapsed(TreeExpansionEvent arg0) {
        
    }
}
