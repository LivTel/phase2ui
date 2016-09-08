/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.panel;

import ngat.phase2.ISequenceComponent;

/**
 *
 * @author nrc
 */
public interface SequenceComponentPanel  {

    /**
     * Return the ISequenceComponent associated with this panel.
     * @return
     */
    public ISequenceComponent getSequenceComponent();
    public boolean isValidData();
}
