/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.listeners;

import ngat.phase2.IInstrumentConfig;
import ngat.phase2.IProgram;

/**
 *
 * @author nrc
 */
public interface InstrumentConfigListUpdateListener {

    public void receiveInstrumentConfigAdded(IInstrumentConfig instrumentConfig);
    public void receiveInstrumentConfigDeleted(IInstrumentConfig instrumentConfig);
    public void receiveInstrumentConfigEdited(IInstrumentConfig target);
}
