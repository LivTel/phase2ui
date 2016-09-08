/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.render;

import java.util.SortedSet;
import ngat.astrometry.Position;
import ngat.phase2.ITarget;
import ngat.phase2.XEphemerisTarget;
import ngat.phase2.XEphemerisTrackNode;
import ngat.phase2.XExtraSolarTarget;
import ngat.phase2.XOrbitalElementsTarget;
import ngat.phase2.XSlaNamedPlanetTarget;

/**
 *
 * @author nrc
 */
public class TargetRenderer {

    public static String getShortDescription(ITarget target) {
        String s = target.getName();
        s += " [" + getTargetTypeDescription(target) + "]";

        if (target instanceof XEphemerisTarget) {
            XEphemerisTarget ephemerisTarget = (XEphemerisTarget) target;
            SortedSet trackNodes =  ephemerisTarget.getEphemerisTrack();
            if (trackNodes == null) {
                s+= " [No track nodes]";
            } else {
                s += " [" + trackNodes.size() + " track nodes]";
            }
            return s;

        } else  if (target instanceof XExtraSolarTarget) {
            XExtraSolarTarget extraSolarTarget = (XExtraSolarTarget) target;
            s += " [RA: " + Position.formatHMSString(extraSolarTarget.getRa(), ":");
            s += ", Dec: " + Position.formatDMSString(extraSolarTarget.getDec(), ":");
            s += "]";
            return s;

        } else  if (target instanceof XOrbitalElementsTarget) {
            return s;

        } else  if (target instanceof XSlaNamedPlanetTarget) {
            XSlaNamedPlanetTarget slaNamedPlanetTarget = (XSlaNamedPlanetTarget) target;
            return s + " [" + XSlaNamedPlanetTarget.getCatalogName(slaNamedPlanetTarget.getIndex()) + "]";

        } else {
            return "UNKNOWN";
        }


    }

    public static String getTargetTypeDescription(ITarget target) {
      if (target instanceof XEphemerisTarget) {
          return "Ephemeris";
      } else  if (target instanceof XExtraSolarTarget) {
          return "Extra-solar";
      } else  if (target instanceof XOrbitalElementsTarget) {
          return "Orbital-elements";
      } else  if (target instanceof XSlaNamedPlanetTarget) {
          return "Catalogue";
      } else {
          return "UNKNOWN";
      }
    }

    public static String getSummaryOfTarget(ITarget target) {

        String s = "";
        if (target instanceof XEphemerisTarget) {
            XEphemerisTarget ephemerisTarget = (XEphemerisTarget) target;
            SortedSet trackNodes =  ephemerisTarget.getEphemerisTrack();
            if (trackNodes == null) {
                return "Empty";
            }
            if (trackNodes.size() == 0) {
                return "Empty";
            }
            XEphemerisTrackNode ephemerisTrackNode = (XEphemerisTrackNode) trackNodes.first();

            s += "First track node = [RA: " + Position.formatHMSString(ephemerisTrackNode.ra, ":");
            s += ", Dec: " + Position.formatDMSString(ephemerisTrackNode.dec, ":");
            s += ", dRA (arcsec/hr): " + String.valueOf(ephemerisTrackNode.raDot);
            s += ", dDec (arcsec/hr): " + String.valueOf(ephemerisTrackNode.decDot);
            s += "]";
            return s;

        } else  if (target instanceof XExtraSolarTarget) {
            XExtraSolarTarget extraSolarTarget = (XExtraSolarTarget) target;
            s += "Position = [RA: " + Position.formatHMSString(extraSolarTarget.getRa(), ":");
            s += ", Dec: " + Position.formatDMSString(extraSolarTarget.getDec(), ":");
            s += "]";
            return s;

        } else  if (target instanceof XOrbitalElementsTarget) {
            return "Orbital-elements";

        } else  if (target instanceof XSlaNamedPlanetTarget) {
            XSlaNamedPlanetTarget slaNamedPlanetTarget = (XSlaNamedPlanetTarget) target;
            return "Name: " + XSlaNamedPlanetTarget.getCatalogName(slaNamedPlanetTarget.getIndex());

        } else {
            return "UNKNOWN";
        }
    }
}
