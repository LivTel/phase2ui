/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.listeners;

import ngat.phase2.ITarget;

/**
 *
 * @author nrc
 */
public interface TargetListUpdateListener {

    public void receiveTargetAdded(ITarget target);
    public void receiveTargetDeleted(ITarget target);
    public void receiveTargetEdited(ITarget target);
}
