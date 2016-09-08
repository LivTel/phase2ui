/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.panel.interfaces;

import ngat.phase2.IObservingConstraint;

/**
 *
 * @author nrc
 */
public interface IObservingConstraintPanel {
    
    public boolean shouldBeDeleted();
    public void setEditable(boolean enabled);
    public boolean isEditable();

    public boolean containsValidObservingConstraint();
    public IObservingConstraint getObservingConstraint();

}
