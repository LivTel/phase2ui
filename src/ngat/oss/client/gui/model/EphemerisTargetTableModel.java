/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.model;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TimeZone;
import javax.swing.table.AbstractTableModel;
import ngat.astrometry.Position;
import ngat.phase2.XEphemerisTarget;
import ngat.phase2.XEphemerisTrackNode;

/**
 *
 * @author nrc
 */
public class EphemerisTargetTableModel extends AbstractTableModel {

    private ArrayList trackNodesList = new ArrayList();
    
    public EphemerisTargetTableModel(XEphemerisTarget ephemerisTarget) {
        if (ephemerisTarget == null) {
            return;
        }
        SortedSet ephemerisTrack = ephemerisTarget.getEphemerisTrack();
        if (ephemerisTrack == null) {
            return;
        }
        Iterator i = ephemerisTrack.iterator();
        while (i.hasNext()) {
            XEphemerisTrackNode ephemerisTrackNode = (XEphemerisTrackNode) i.next();
            trackNodesList.add(ephemerisTrackNode);
        }
    }
    
    public int getRowCount() {
        return trackNodesList.size();
    }

    public String getColumnName(int col) {
        switch (col) {
            case 0:
                return "Date/Time";
            case 1:
                return "RA";
            case 2:
                return "Dec";
            case 3:
                return "dRA (arcsec/hr)";
            case 4:
                return "dDec (arcsec/hr)";
        }
        return "unknown";
    }
    
    public int getColumnCount() {
        return 5;
    }

    public Object getValueAt(int row, int col) {
         XEphemerisTrackNode ephemerisTrackNode = (XEphemerisTrackNode) trackNodesList.get(row);
         switch (col) {
             case 0:
                 return getTimeString(ephemerisTrackNode.time);
             case 1:
                 return getRAString(ephemerisTrackNode.ra);
             case 2:
                 return getDECString(ephemerisTrackNode.dec);
             case 3:
                 return String.valueOf(ephemerisTrackNode.raDot);
             case 4:
                 return String.valueOf(ephemerisTrackNode.decDot);
         }
         return null;
    }

    private String getTimeString(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy H:mm:ss");
        dateFormat.setDateFormatSymbols(new DateFormatSymbols(Locale.UK)); //make sure months are spelt in English
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(new Date(time));
    }
    
    private String getRAString(double ra) {
        return Position.formatHMSString(ra, ":");
    }
    
    private String getDECString(double dec) {
        return Position.formatDMSString(dec, ":");
    }
    
}
