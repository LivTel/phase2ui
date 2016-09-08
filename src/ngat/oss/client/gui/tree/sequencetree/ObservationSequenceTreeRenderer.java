/*
 * MainTreeRenderer.java
 *
 * Created on 06 December 2007, 15:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ngat.oss.client.gui.tree.sequencetree;

import ngat.oss.client.gui.render.*;
import java.awt.Component;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author nrc
 */
public class ObservationSequenceTreeRenderer extends DefaultTreeCellRenderer {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm z");
                
    /** Creates a new instance of MainTreeRenderer */
    public ObservationSequenceTreeRenderer() {
        super();
        dateFormat.setDateFormatSymbols(new DateFormatSymbols(Locale.UK)); //make sure months are spelt in English
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    
    public Component getTreeCellRendererComponent(
                                            JTree   tree,
                                            Object  value,
                                            boolean selected,
                                            boolean expanded,
                                            boolean leaf,
                                            int     row,
                                            boolean hasFocus) {
        
        super.getTreeCellRendererComponent(
                tree, value, selected,
                expanded, leaf, row,
                hasFocus);

        String text = "unknown";

        //if the renderer is being used on a JTree which has a DefaultTreeModel, we need to make the value the wrapped user data
        if (value instanceof DefaultMutableTreeNode) {
            value = ((DefaultMutableTreeNode)value).getUserObject();
        } else {
            //it's an ISequenceComponent anyhow
        }

        text=ObservationSequenceComponentRenderer.getInstance().getTextRendering(value);

        setText(text);
        
        return this;
    }

    

    
}
