/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.panel.interfaces;

import ngat.phase2.IInstrumentConfig;

/**
 *
 * @author nrc
 */
public interface IInstrumentConfigPanel {

    public IInstrumentConfig getInstrumentConfig() throws Exception;
    public boolean containsValidInstrumentConfig();
    
}
