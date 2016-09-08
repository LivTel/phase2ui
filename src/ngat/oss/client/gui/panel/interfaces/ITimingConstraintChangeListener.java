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
public interface ITimingConstraintChangeListener {

    public void receiveTimingConstraintChange(ITimingConstraint newTimingConstraint);
}
