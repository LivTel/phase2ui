/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.panel.sequencepanels.sub;

import ngat.phase2.IExposure;

/**
 *
 * @author nrc
 */
public interface IExposurePanel {
    public IExposure getExposure();
    public boolean containsValidData();
}
