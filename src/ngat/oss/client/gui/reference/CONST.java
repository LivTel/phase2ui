/*
 * CONST.java
 *
 * Created on 06 December 2007, 15:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ngat.oss.client.gui.reference;

import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import ngat.phase2.XVersion;

/**
 *
 * @author nrc
 */
public class CONST {

    public static final XVersion CURRENT_VERSION_NUMBER = new XVersion(2, 0, 0, 0);
    
    public static final String CURRENT_REVISION_LETTER = ".A";
    
    public static final String APP_NAME = "Liverpool Telescope Phase2 UI";

    public static final Dimension MAIN_FRAME_SIZE = new Dimension(1024, 720);

    public static final String USERS_TREE_ROOT_NAME="Users";
    public static final String TAGS_TREE_ROOT_NAME="Tags";
    public static final String PROGRAMMES_TREE_ROOT_NAME="Programmes";

    public static final String USER_PROGRAMMES_POSTFIX = "Programmes";
    public static final String USER_ACCESS_PERMISSIONS_POSTFIX = "Permissions";

    public static ImageIcon PROPOSAL_ICON    = new ImageIcon("resources/jlfgr/toolbarButtonGraphics/general/History24.gif");
    public static ImageIcon OBSERVATION_ICON = new ImageIcon("resources/jlfgr/toolbarButtonGraphics/general/Find24.gif");
    public static ImageIcon GROUP_ICON       = new ImageIcon("resources/jlfgr/toolbarButtonGraphics/general/Copy24.gif");
    
    public static ImageIcon FLEXIBLE_GROUP_ACTION_ICON  = new ImageIcon("flex-group.gif");
    public static ImageIcon MONITOR_GROUP_ACTION_ICON   = new ImageIcon("monitor-group.gif");
    public static ImageIcon FIXED_GROUP_ACTION_ICON     = new ImageIcon("fixed-group.gif");
    public static ImageIcon EPHEM_GROUP_ACTION_ICON     = new ImageIcon("ephem-group.gif");
    public static ImageIcon REPEAT_GROUP_ACTION_ICON    = new ImageIcon("repeat-group.gif");

    public static final String LOTUS = "LOTUS";
    public static final String SPRAT = "SPRAT";
    public static final String RISE = "RISE";
   
    public static final String RINGO3 = "RINGO3";
    public static final String MOPTOP = "MOPTOP";
    
    public static final String FRODO = "FRODO";
    public static final String FRODO_RED = "FRODO_RED";
    public static final String FRODO_BLUE = "FRODO_BLUE";

    public static final String IO = "IO";
    public static final String IO_I = "IO:I";
    public static final String IO_O = "IO:O";
    
    //left in to allow users to be warned that it has gone
    public static final String RATCAM = "RATCam";
    
    //left in to allow users to be warned that it has gone
    public static final String IO_THOR = "IO:THOR";

    //public static final String SUPIRCAM = "SupIRCam";

    public static final String LIRIC = "LIRIC";
    
    //instrument lists:
    
    //instrument lists for instrument configs
    public static final String[] ALL_INSTRUMENTS_FOR_CONFIGS = new String[]{SPRAT, RISE, MOPTOP, IO_O, LIRIC, FRODO_RED, FRODO_BLUE }; //NB: contains frodo_blue and frodo red (only used in instrument configs)
    //public static final String[] ALL_INSTRUMENTS_FOR_CONFIGS_EXCEPT_LIRIC = new String[]{SPRAT, RISE, MOPTOP, IO_O, FRODO_RED, FRODO_BLUE }; //temp, for non su users //NB: contains frodo_blue and frodo red (only used in instrument configs)
    
    //instrument lists for acquisition, acquiring
    public static final String[] ACQUISITION_ACQUIRING_INSTRUMENTS = new String[]{IO_O, SPRAT};
    
    //instrument lists for acquisition, science
    public static final String[] ACQUISITION_SCIENCE_INSTRUMENTS = new String[]{FRODO, IO_O, LIRIC, MOPTOP, RISE, SPRAT};
    //public static final String[] ACQUISITION_SCIENCE_INSTRUMENTS_EXCEPT_LIRIC = new String[]{ FRODO, IO_O, MOPTOP, RISE, SPRAT }; //temp, for non su users
    
    //instrument lists for everything else, e.g. rotator alignment and slew
    public static final String[] ALL_INSTRUMENTS_FOR_ROTATOR = new String[]{SPRAT, RISE, MOPTOP, IO_O, LIRIC}; //NB: contains frodo, not an arm instrument
    //public static final String[] ALL_INSTRUMENTS_FOR_ROTATOR = new String[]{SPRAT, RISE, RINGO3, MOPTOP, IO_O, IO_I, FRODO, LOTUS}; //NB: contains frodo, not an arm instrument
    //public static final String[] ALL_INSTRUMENTS_FOR_ROTATOR_EXCEPT_LIRIC = new String[]{SPRAT, RISE, IO_O, MOPTOP}; //temp, for non su users //NB: contains frodo, not an arm instrument
    
    //instrument lists for imager instrument config editor panel and photometry and polarimetry wizard
    public static final String[] IMAGER_INSTRUMENTS = new String[]{IO_O, LIRIC, RISE};
    //public static final String[] IMAGER_INSTRUMENTS_EXCEPT_LIRIC = new String[]{IO_O, RISE};
    
    public static final String[] BEAM_STEERING_INSTRUMENTS = new String[]{IO_O}; // will include IO_I but not yet.
    public static final String[] POLARIMETER_INSTRUMENTS = new String[]{MOPTOP}; 
    public static final String[] DUAL_BEAM_SPECTROGRAPH_INSTRUMENTS = new String[]{FRODO};
    
    
    //filter list
    public static final String CLEAR = "clear";
    public static final String SDSS_U = "SDSS-U";
    public static final String BESSELL_B = "Bessell-B";
    public static final String BESSELL_V = "Bessell-V";
    public static final String SDSS_G = "SDSS-G";
    public static final String SDSS_R = "SDSS-R";
    public static final String SDSS_I = "SDSS-I";
    public static final String SDSS_Z = "SDSS-Z";
    public static final String H_ALPHA_100 = "H-Alpha-100";
    public static final String H_ALPHA_6566 = "H-Alpha-6566";
    public static final String H_ALPHA_6634 = "H-Alpha-6634";
    public static final String H_ALPHA_6705 = "H-Alpha-6705";
    public static final String H_ALPHA_6755 = "H-Alpha-6755";
    public static final String H_ALPHA_6822 = "H-Alpha-6822"; // removed from filter wheel 30/6/15. Put back on 22/10/15. Removed Jan 2024.
    public static final String CN = "CN";
    //public static final String GG475_KG3 = "GG475+KG3"; //New broadband filter for Gaia observing (but taken out in April 2015)
    //public static final String GG475_KG3_Pol = "GG475+KG3+Pol"; // removed from filter wheel 22/10/15
    
    public static final String OG515 = "og515+k5";
    public static final String RED = "Red";
    public static final String BLUE = "Blue";
    public static final String ND1_5 = "ND1.5";
    public static final String ND3_0 = "ND3";
    public static final String H = "H";
    public static final String MOP_R = "MOP-R";
    public static final String MOP_V = "MOP-V";
    public static final String MOP_B = "MOP-B";
    public static final String MOP_I = "MOP-I";
    public static final String MOP_L = "MOP-L";
    public static final String FELH1500 = "FELH1500";
    public static final String BARR_H = "Barr-H";
    public static final String BARR_J = "Barr-J";
    public static final String LIR_CLEAR = "Clear";
    public static final String BESSELL_R = "Bessell-R";

    //filter wheel lists
    public static final String[] RATCAM_FW1_ITEMS = new String[]{CLEAR, SDSS_I, H_ALPHA_100, SDSS_Z, SDSS_R};
    public static final String[] RATCAM_FW2_ITEMS = new String[]{CLEAR, BESSELL_V, SDSS_U, SDSS_G, BESSELL_B};
    public static final String[] RISE_FW_ITEMS = new String[]{OG515};
    //public static final String[] O_FW_ITEMS = new String[]{SDSS_U, BESSELL_B, BESSELL_V, SDSS_G, SDSS_R, SDSS_I, SDSS_Z, H_ALPHA_6566, H_ALPHA_6634, H_ALPHA_6705, H_ALPHA_6755, GG475_KG3};
    //public static final String[] O_FW_ITEMS = new String[]{BESSELL_B, BESSELL_V, SDSS_U, SDSS_G, SDSS_R, SDSS_I, SDSS_Z, H_ALPHA_6566, H_ALPHA_6634, H_ALPHA_6705, H_ALPHA_6755, GG475_KG3_Pol};
    //public static final String[] O_FW_ITEMS = new String[]{BESSELL_B, BESSELL_V, SDSS_U, SDSS_G, SDSS_R, SDSS_I, SDSS_Z, H_ALPHA_6566, H_ALPHA_6634, H_ALPHA_6705, H_ALPHA_6755, H_ALPHA_6822}; //22/10/15
    public static final String[] O_FW_ITEMS = new String[]{BESSELL_B, BESSELL_V, SDSS_U, SDSS_G, SDSS_R, SDSS_I, SDSS_Z, H_ALPHA_6566, H_ALPHA_6634, H_ALPHA_6705, H_ALPHA_6755, CN}; // 25/01/24
    public static final String[] I_FW_ITEMS = new String[]{H};
    public static final String[] LIRIC_FW_ITEMS = new String[]{FELH1500, BARR_J, BESSELL_R};
    public static final String[] MOPTOP_FW_ITEMS = new String[]{MOP_R,MOP_V,MOP_B,MOP_I,MOP_L};
    
    //IO:O neutral density slides
    public static final String[] O_UPPER_ND_ITEMS = new String[]{CLEAR, ND3_0};
    public static final String[] O_LOWER_ND_ITEMS = new String[]{CLEAR, ND1_5}; 

    //Binning Lists
     public static final String BIN_1 = "1x1";
     public static final String BIN_2 = "2x2";
     public static final String BIN_4 = "4x4";

     public static final String[] RATCAM_BINNING_OPTIONS = new String[]{BIN_1, BIN_2};
     public static final String[] RISE_BINNING_OPTIONS = new String[]{BIN_1, BIN_2};
     public static final String[] IO_O_BINNING_OPTIONS = new String[]{BIN_2};
     public static final String[] SU_IO_O_BINNING_OPTIONS = new String[]{BIN_1, BIN_2};
     public static final String[] IO_I_BINNING_OPTIONS = new String[]{BIN_1};  //1x1 only for now
     public static final String[] LIRIC_BINNING_OPTIONS = new String[]{BIN_1}; 
     public static final String[] SPRAT_BINNING_OPTIONS = new String[]{BIN_1, BIN_2};
     public static final String[] LOTUS_BINNING_OPTIONS = new String[]{BIN_4};
     
     //Windows
     //public static final String[] IO_THOR_WINDOW_OPTIONS =new String[] { "8x8", "16x16", "32x32", "64x64", "128x128", "256x256", "512x512", "1024x1024" };

    //Optical Slides
    public static final String CLEAR_NAME = "Clear"; //the name of the clear slides in the io_optical_elements.xml document. - MUST BE SET CORRECTLY
    public static final String UV_AL_MIRROR_NAME = "UvAlMirror"; //the name of the UV enhanced aluminium mirror slide in the io_optical_elements.xml document. - MUST BE SET CORRECTLY
    public static final String GOLD_MIRROR_NAME = "AuMirror"; //the name of the gold mirror slide in the io_optical_elements.xml document. - MUST BE SET CORRECTLY

    //I dichroics
    public static final String IO_CLEAR = "CLEAR";
     
    //O dichroics
    public static final String DICHROIC = "DICHROIC";
    public static final String RED_BLUE_DICHROIC = "RED_BLUE_DICHROIC";
    public static final String BLUE_RED_DICHROIC = "BLUE_RED_DICHROIC";
    
    //I and O DICHROIC elements
    public static final String MIRROR = "MIRROR";

    //gains
    public static final String[] RINGO_GAINS = new String[]{"100", "20", "5"}; 
    //public static final String[] THOR_GAINS = new String[]{"5", "20", "100"};

    // Moptop rotor speeds
    public static final String[] MOPTOP_ROTOR_SPEEDS = new String[]{"Slow", "Fast"}; 
    public static final int MOPTOP_ROTOR_SPEED_INDEX_SLOW = 0;
    public static final int MOPTOP_ROTOR_SPEED_INDEX_FAST = 1;

    // Liric Nudgematic Offset Sizes
    // These constants match those in ngat/phase2/LiricConfig.java
    // and ngat/phase2/XLiricInstrumentConfig.java
    public static final String[] LIRIC_NUDGEMATIC_OFFSET_SIZE_LIST = new String[]{"None", "Small", "Large"}; 
    public static final int LIRIC_NUDGEMATIC_OFFSET_SIZE_NONE = 0;
    public static final int LIRIC_NUDGEMATIC_OFFSET_SIZE_SMALL = 1;
    public static final int LIRIC_NUDGEMATIC_OFFSET_SIZE_LARGE = 2;
    
    public static final double INSTRUMENT_OFFSET = Math.toRadians(104);

    public static final String IO_SCHEMATIC_FILE_PATH = "/home/eng/bssgui/resources/IO_Small.png";
    public static final String IO_SCHEMATIC_URL_PATH = "http://telescope.livjm.ac.uk/pics/IO_Small.png";

    public static final String DEFAULT_OPTICAL_CONFIG_LOCAL_FILE_LOCATION = "/home/eng/bssgui/resources/io_optical_elements.xml";
    public static final String DEFAULT_OPTICAL_CONFIG_WEB_FILE_LOCATION = "http://161.72.57.4/launch_oss/configs/io_optical_elements.xml";

    
    /**
     * All instruments in IMAGER_INSTRUMENTS + POLARIMETER_INSTRUMENTS + DUAL_BEAM_SPECTROGRAPH_INSTRUMENTS
     * @return complete list of instruments
     */
    /*
    public static String[] getAllInstruments() {

        String[] allInstruments;

        int lengthOfAllInstrumentsArray = IMAGER_INSTRUMENTS.length + POLARIMETER_INSTRUMENTS.length + DUAL_BEAM_SPECTROGRAPH_INSTRUMENTS.length; // + TIP_TILT_INSTRUMENTS.length;
        allInstruments = new String[lengthOfAllInstrumentsArray];
       
        int insertPointer = 0;
        System.arraycopy(IMAGER_INSTRUMENTS,  0, allInstruments, insertPointer, IMAGER_INSTRUMENTS.length);

        insertPointer += IMAGER_INSTRUMENTS.length;
        System.arraycopy(POLARIMETER_INSTRUMENTS,  0 , allInstruments, insertPointer, POLARIMETER_INSTRUMENTS.length);

        insertPointer += POLARIMETER_INSTRUMENTS.length;
        System.arraycopy(DUAL_BEAM_SPECTROGRAPH_INSTRUMENTS,  0 , allInstruments, insertPointer, DUAL_BEAM_SPECTROGRAPH_INSTRUMENTS.length);

        // new new don't include THOR, it's there already
        
        //new (include THOR):
        //insertPointer += DUAL_BEAM_SPECTROGRAPH_INSTRUMENTS.length;
        //System.arraycopy(TIP_TILT_INSTRUMENTS,  0 , allInstruments, insertPointer, TIP_TILT_INSTRUMENTS.length);
        
        return allInstruments;
    }
    */
    public static double getOffsetAngleOfInstrument(String instrumentName) throws Exception {
        
        double instrumentOffsetAngle = Math.toRadians(0); //by default
        
        if (instrumentName.equalsIgnoreCase(CONST.RISE)) {
            instrumentOffsetAngle = Math.toRadians(-44.4);
          
        } else if (instrumentName.equalsIgnoreCase(CONST.RINGO3)) {
            instrumentOffsetAngle = Math.toRadians(-87.8);
            
        } else if (instrumentName.equalsIgnoreCase(CONST.MOPTOP)) {
            instrumentOffsetAngle = Math.toRadians(-177.162);
            
        } else if (instrumentName.equalsIgnoreCase(CONST.IO_O)) {
            instrumentOffsetAngle = Math.toRadians(0);
            
        } else if (instrumentName.equalsIgnoreCase(CONST.FRODO)) {
            instrumentOffsetAngle = Math.toRadians(0);
        
        } else if (instrumentName.equalsIgnoreCase(CONST.IO_I)) {
            instrumentOffsetAngle = Math.toRadians(0);
            
        } else if (instrumentName.equalsIgnoreCase(CONST.SPRAT)) {
            instrumentOffsetAngle = Math.toRadians(+91.72);
            
        } else {
            throw new Exception("unknown instrument name");
        }
        return instrumentOffsetAngle;
    }
    
}
