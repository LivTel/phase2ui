/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.panel.interfaces;

import ngat.phase2.ITimingConstraint;

/**
 *
 * @author nrc
 */
public interface ITimingConstraintPanel {
    
    public ITimingConstraint getTimingConstraint();
    public boolean containsValidTimingConstraint();
}
