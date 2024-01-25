/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.render;

import ngat.phase2.IInstrumentConfig;
import ngat.phase2.XBlueTwoSlitSpectrographInstrumentConfig;
import ngat.phase2.XDualBeamSpectrographInstrumentConfig;
import ngat.phase2.XImagerInstrumentConfig;
import ngat.phase2.XImagingSpectrographInstrumentConfig;
import ngat.phase2.XPolarimeterInstrumentConfig;
import ngat.phase2.XMoptopInstrumentConfig;
import ngat.phase2.XSpectrographInstrumentConfig;
import ngat.phase2.XTipTiltImagerInstrumentConfig;
import ngat.phase2.XLiricInstrumentConfig;

/**
 * Routines to describe instrument configs.
 * @author nrc,cjm
 */
public class InstrumentConfigRenderer 
{

    /**
     * Get a short description of an instrument config.
     * @param instrumentConfig The instrument configuration to describe.
     * @return A string containing the description.
     * @see #getInstrumentConfigTypeDescription
     * @see #getSummaryOfInstrumentConfig
     */
    public static String getShortDescription(IInstrumentConfig instrumentConfig) 
    {
        String s = instrumentConfig.getName();
        s += " [" + getInstrumentConfigTypeDescription(instrumentConfig) + "]";
        s += " [" + getSummaryOfInstrumentConfig(instrumentConfig) + "]";
        return s;
    }

    /**
     * Get a description of what type the instrument config is.
     * @param instrumentConfig The instrument config to describe the type of.
     * @return A string describing the instrument type.
     */
    public static String getInstrumentConfigTypeDescription(IInstrumentConfig instrumentConfig) 
    {
      if (instrumentConfig instanceof XDualBeamSpectrographInstrumentConfig) 
      {
          return "Dual-Beam Spec. configuration";
      } 
      else  if (instrumentConfig instanceof XImagerInstrumentConfig) 
      {
          if (instrumentConfig instanceof XTipTiltImagerInstrumentConfig) 
          {
            return "Tip-Tilt Imager Configuration";
          } 
          else if (instrumentConfig instanceof XLiricInstrumentConfig) 
          {
            return "LIRIC Imager Configuration";
          } 
          else 
          {
            return "Imager configuration";
          }
      } 
      else  if (instrumentConfig instanceof XPolarimeterInstrumentConfig) 
      {
          return "Polarimeter configuration";
      } 
      else  if (instrumentConfig instanceof XMoptopInstrumentConfig) 
      {
          return "MOPTOP Polarimeter configuration";
      } 
      else  if (instrumentConfig instanceof XSpectrographInstrumentConfig) 
      {
          return "Spectrograph configuration";
      } 
      else  if (instrumentConfig instanceof XImagingSpectrographInstrumentConfig) 
      {
          return "Imaging-Spec. configuration";
      } 
      else  if (instrumentConfig instanceof XBlueTwoSlitSpectrographInstrumentConfig) 
      {
          return "Blue Two Slit Spec. configuration";
      } 
      else 
      {
          return "UNKNOWN";
      }
    }

    /**
     * Produce a string describing the main contents of the specified instrument configuration. 
     * @param instrumentConfig The instrument config to describe.
     * @return A string describing the contents of the instrument configuration.
     */
    public static String getSummaryOfInstrumentConfig(IInstrumentConfig instrumentConfig) 
    {

        String s = "";

        // XDualBeamSpectrographInstrumentConfig
        if (instrumentConfig instanceof XDualBeamSpectrographInstrumentConfig) 
        {
            XDualBeamSpectrographInstrumentConfig dualBeamSpectrographInstrumentConfig = (XDualBeamSpectrographInstrumentConfig) instrumentConfig;
            String resString =dualBeamSpectrographInstrumentConfig.toResolutionString(dualBeamSpectrographInstrumentConfig.getResolution());
            s += "Resolution=[" + resString + "]";
            return s;
        
        } 
        // XImagerInstrumentConfig
        else  if (instrumentConfig instanceof XImagerInstrumentConfig) 
        {
            XImagerInstrumentConfig imagerInstrumentConfig = (XImagerInstrumentConfig) instrumentConfig;
            if (imagerInstrumentConfig.getInstrumentName().equalsIgnoreCase("RISE")) 
            {
                return "-";
            }
            if (instrumentConfig instanceof XTipTiltImagerInstrumentConfig) 
            {
                XTipTiltImagerInstrumentConfig tiltImagerInstrumentConfig = (XTipTiltImagerInstrumentConfig) instrumentConfig;
                s += "Gain=[" + tiltImagerInstrumentConfig.getGain() + "]";
                return s;
            }
            s += "Filters=[" + imagerInstrumentConfig.getFilterSpec().getFiltersString() + "]";
            if (instrumentConfig instanceof XLiricInstrumentConfig) 
            {
                XLiricInstrumentConfig liricInstrumentConfig = (XLiricInstrumentConfig)imagerInstrumentConfig;
                s += ",Nudgematic Offset Size=[" + liricInstrumentConfig.nudgematicOffsetSizeToString()+ "]";
                s += ",Coadd Exposure Length=[" + liricInstrumentConfig.getCoaddExposureLength()+ "]";
            } 
            return s;
        } 
        // XPolarimeterInstrumentConfig
        else  if (instrumentConfig instanceof XPolarimeterInstrumentConfig) 
        {
            XPolarimeterInstrumentConfig polarimeterInstrumentConfig = (XPolarimeterInstrumentConfig) instrumentConfig;
            s += "Gain=[" + polarimeterInstrumentConfig.getGain() + "]";
            return s;
        } 
        // XMoptopInstrumentConfig
        else  if (instrumentConfig instanceof XMoptopInstrumentConfig) 
        {
            XMoptopInstrumentConfig moptopInstrumentConfig = (XMoptopInstrumentConfig) instrumentConfig;
            s += "Filter=[" + moptopInstrumentConfig.getFilterSpec().getFiltersString()+ "]";
            s += ", Rotor Speed=[" + moptopInstrumentConfig.rotorSpeedToString()+ "]";
            return s;
        } 
        // XSpectrographInstrumentConfig
        else  if (instrumentConfig instanceof XSpectrographInstrumentConfig) 
        {
            XSpectrographInstrumentConfig spectrographInstrumentConfig = (XSpectrographInstrumentConfig) instrumentConfig;
            return "Wavelength=[" + spectrographInstrumentConfig.getWavelength() + "]";
        } 
        // XImagingSpectrographInstrumentConfig
        else  if (instrumentConfig instanceof XImagingSpectrographInstrumentConfig) 
        {
            XImagingSpectrographInstrumentConfig imagingSpectrographInstrumentConfig = (XImagingSpectrographInstrumentConfig) instrumentConfig;
            int grismPos = imagingSpectrographInstrumentConfig.getGrismPosition();
            int grismRot = imagingSpectrographInstrumentConfig.getGrismRotation();
            int slitPos = imagingSpectrographInstrumentConfig.getSlitPosition();
            
            String description = "";
            switch (grismPos) 
            {
                case XImagingSpectrographInstrumentConfig.GRISM_IN:
                    description += "Grism position=[IN]";
                    break;
                case XImagingSpectrographInstrumentConfig.SLIT_STOWED:
                    description += "[Grism position=[STOWED]";
                    break;
                default:
                    break;
            }
            
            switch (grismRot) 
            {
                case XImagingSpectrographInstrumentConfig.GRISM_ROTATED:
                    description += ", Grism rotation=[BLUE_OPT]";
                    break;
                case XImagingSpectrographInstrumentConfig.GRISM_NOT_ROTATED:
                    description += ", Grism rotation=[RED_OPT]";
                    break;
                default:
                    break;
            }
            
            switch (slitPos) 
            {
                case XImagingSpectrographInstrumentConfig.SLIT_DEPLOYED:
                    description += ", Slit=[DEPLOYED]";
                    break;
                case XImagingSpectrographInstrumentConfig.SLIT_STOWED:
                    description += ", Slit=[STOWED]";
                    break;
                default:
                    break;
            }
            return description;
        
        } 
        // XBlueTwoSlitSpectrographInstrumentConfig
        else if (instrumentConfig instanceof XBlueTwoSlitSpectrographInstrumentConfig) 
        {
            XBlueTwoSlitSpectrographInstrumentConfig blueTwoSlitSpectrographInstrumentConfig = (XBlueTwoSlitSpectrographInstrumentConfig) instrumentConfig;
            int slitWidth = blueTwoSlitSpectrographInstrumentConfig.getSlitWidth();
            String description = "";
            switch (slitWidth) 
            {
                case XBlueTwoSlitSpectrographInstrumentConfig.SLIT_NARROW:
                    description += "Slit=[NARROW]";
                    break;
                case XBlueTwoSlitSpectrographInstrumentConfig.SLIT_WIDE:
                    description += "Slit=[WIDE]";
                    break;
                default:
                    break;
            }
            return description;
         
        } 
        else 
        {
            return "UNKNOWN";
        }
    }
}
