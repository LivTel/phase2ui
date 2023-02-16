/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ImagerInstrumentConfigEditorPanel2.java
 *
 * Created on Oct 26, 2010, 2:44:23 PM
 */

package ngat.oss.client.gui.panel.instrumentConfig;

import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import ngat.oss.client.gui.panel.interfaces.IInstrumentConfigPanel;
import ngat.oss.client.gui.reference.CONST;
import ngat.oss.client.gui.reference.DefaultObjectFactory;
import ngat.oss.client.gui.reference.Session;
import ngat.phase2.IDetectorConfig;
import ngat.phase2.IInstrumentConfig;
import ngat.phase2.XDetectorConfig;
import ngat.phase2.XFilterDef;
import ngat.phase2.XFilterSpec;
import ngat.phase2.XImagerInstrumentConfig;
import ngat.phase2.XRaptorInstrumentConfig;
import org.apache.log4j.Logger;

/**
 * Imager instruments configuration editor panel.
 * @author nrc
 */
public class ImagerInstrumentConfigEditorPanel extends javax.swing.JPanel implements IInstrumentConfigPanel 
{
    /**
     * The logger for this panel. 
     */
    static Logger logger = Logger.getLogger(ImagerInstrumentConfigEditorPanel.class);

    private boolean enabled;
    /**
     * A copy of the passed in imager instrument config.
     */
    private XImagerInstrumentConfig originalImagerInstrumentConfig;

    /**
     * Constructor.
     * @param imagerInstrumentConfig The imager instrument config to modify.
     * @param isNewInstrumentConfig  A boolean, if true the instrument config is a new one, otherwise it already exists.
     * @see #originalImagerInstrumentConfig
     * @see #initComponents
     * @see #populateComponents
     */
    public ImagerInstrumentConfigEditorPanel(XImagerInstrumentConfig imagerInstrumentConfig, boolean isNewInstrumentConfig) 
    {
        this.originalImagerInstrumentConfig = imagerInstrumentConfig;
        initComponents();
        
        //jcbInstrumentName.setModel(new javax.swing.DefaultComboBoxModel(CONST.IMAGER_INSTRUMENTS));
        if (Session.getInstance().getUser().isSuperUser()) 
        {
            jcbInstrumentName.setModel(new javax.swing.DefaultComboBoxModel(CONST.IMAGER_INSTRUMENTS));
        } 
        else 
        {
            jcbInstrumentName.setModel(new javax.swing.DefaultComboBoxModel(CONST.IMAGER_INSTRUMENTS_EXCEPT_RAPTOR));
        }
        populateComponents(imagerInstrumentConfig, isNewInstrumentConfig);
    }

    /**
     * Populate the panel based on the passed in imager config.
     * @param imagerInstrumentConfig The imager instrument config to populate the panel with.
     * @param isNewInstrumentConfig A boolean, if true the instrument config is a new one, otherwise it already exists.
     * @see #populateForNewInstrumentConfig
     * @see #populateForExistingInstrumentConfig
     */
    private void populateComponents(XImagerInstrumentConfig imagerInstrumentConfig, boolean isNewInstrumentConfig) 
    {
        if (imagerInstrumentConfig == null) 
        {
            return;
        }
        //jcbInstrumentName.setModel(new javax.swing.DefaultComboBoxModel(CONST.IMAGER_INSTRUMENTS));
        if (Session.getInstance().getUser().isSuperUser()) 
        {
            jcbInstrumentName.setModel(new javax.swing.DefaultComboBoxModel(CONST.IMAGER_INSTRUMENTS));
        } 
        else
        {
            jcbInstrumentName.setModel(new javax.swing.DefaultComboBoxModel(CONST.IMAGER_INSTRUMENTS_EXCEPT_RAPTOR));
        }
        
        if (isNewInstrumentConfig) 
        {
            populateForNewInstrumentConfig(imagerInstrumentConfig);
        }
        else 
        {
            populateForExistingInstrumentConfig(imagerInstrumentConfig);
        }
    }

    /**
     * Setup the imager instrument config for a new config.
     * @param imagerInstrumentConfig  The instrument config to populate the dialog from.
     * @see #jcbInstrumentName
     * @see #setupFilterLists
     * @see #limitInstrumentList
     * @see #setBinningOptions
     * @see #detectorConfigStandardPanel
     * @see #setNudgematicOffsetSize
     * @see #setCoaddExposureLength
     */
    private void populateForNewInstrumentConfig(XImagerInstrumentConfig imagerInstrumentConfig) 
    {
        String instrumentName = imagerInstrumentConfig.getInstrumentName();
        XDetectorConfig detectorConfig = DefaultObjectFactory.getDefaultDetectorConfig(instrumentName);

        if (instrumentName != null) 
        {
            jcbInstrumentName.setSelectedItem(instrumentName);
        } 
        else 
        {
            //default to IO:O
            jcbInstrumentName.setSelectedItem(CONST.IO_O);
        }
        
        //populate filter lists for instrument
        setupFilterLists(instrumentName);
        
        //if we've been passed an instrument name, limit the list down to that name
        if (instrumentName != null) 
        {
           limitInstrumentList(instrumentName);
        }
        
        //set the binning options available for that instrument
        setBinningOptions(instrumentName); //defaults to 1x1, used for IO:O
        
        //set the detector config
        try 
        {
            detectorConfigStandardPanel.setDetectorConfig(detectorConfig);
        }
        catch (Exception ex) 
        {
            ex.printStackTrace();
            logger.error(ex);
            JOptionPane.showMessageDialog(this, ex);
            return;
        }
        
        // set nudgematic offset size
        setNudgematicOffsetSize(imagerInstrumentConfig);
        // set coadd exposure length
        setCoaddExposureLength(imagerInstrumentConfig);
    }
    
    /**
     * Setup the imager instrument config for an existing config.
     * @param imagerInstrumentConfig  The instrument config to populate the dialog from.
     * @see #jcbInstrumentName
     * @see #setupFilterLists
     * @see #limitInstrumentList
     * @see #selectFiltersForInstrumentConfig
     * @see #setBinningOptions
     * @see #detectorConfigStandardPanel
     * @see #setNudgematicOffsetSize
     * @see #setCoaddExposureLength
     */
    private void populateForExistingInstrumentConfig(XImagerInstrumentConfig imagerInstrumentConfig) 
    {
        String instrumentName = imagerInstrumentConfig.getInstrumentName();
        //select instrument name in instrument list
        jcbInstrumentName.setSelectedItem(instrumentName);
        //populate filter lists for instrument and limit list to that instrument
            
        setupFilterLists(instrumentName);
        //close down the instrument list    
        limitInstrumentList(instrumentName);
                    
        try 
        {
            //select filters from loaded filter spec
            selectFiltersForInstrumentConfig(imagerInstrumentConfig);

            IDetectorConfig detectorConfig = imagerInstrumentConfig.getDetectorConfig();
            //populate binning lists for instrument
            detectorConfigStandardPanel.setDetectorConfig(detectorConfig); //change, this line added (was commented out) 20/6/11
            jtfInstrumentConfigName.setText(imagerInstrumentConfig.getName());
        } 
        catch (Exception ex) 
        {
            ex.printStackTrace();
            logger.error(ex);
            JOptionPane.showMessageDialog(this, ex);
            return;
        }
         // set nudgematic offset size
        setNudgematicOffsetSize(imagerInstrumentConfig);
        // set coadd exposure length
        setCoaddExposureLength(imagerInstrumentConfig);
    }
    
    /**
     * Set the allowed binning options based on the instrument name.
     * @param instrumentName The name of the instrument.
     * @see #detectorConfigStandardPanel
     */
    private void setBinningOptions(String instrumentName) 
    {
        if (instrumentName != null) 
        {
            if (instrumentName.equalsIgnoreCase(CONST.IO_O)) 
            {
                if (Session.getInstance().getUser().isSuperUser()) 
                {
                    detectorConfigStandardPanel.setBinningOptions(CONST.SU_IO_O_BINNING_OPTIONS);
                }
                else
                {
                    detectorConfigStandardPanel.setBinningOptions(CONST.IO_O_BINNING_OPTIONS);
                }
            } 
            else if (instrumentName.equalsIgnoreCase(CONST.IO_I)) 
            {
                detectorConfigStandardPanel.setBinningOptions(CONST.IO_I_BINNING_OPTIONS);
            }
            else if (instrumentName.equalsIgnoreCase(CONST.RISE)) 
            {
                detectorConfigStandardPanel.setBinningOptions(CONST.RISE_BINNING_OPTIONS);
            }
            else if (instrumentName.equalsIgnoreCase(CONST.RAPTOR)) 
            {
                detectorConfigStandardPanel.setBinningOptions(CONST.RAPTOR_BINNING_OPTIONS);
            }
         }
    }

    /**
     * limit the instrument so it only shows the instrument that panel received (and removes all other entries).
     * @param instrumentName The name of the instrument to limit the list to.
     */
    private void limitInstrumentList(String instrumentName) 
    {
        jcbInstrumentName.removeAllItems();
        jcbInstrumentName.addItem(instrumentName);
        jcbInstrumentName.setSelectedIndex(0);
    }

    /**
     * Set up the filter wheel combo boxes (and their visibility) based on the instrument name.
     * @param instrumentName The name of the instrument.
     * @see #jcbFilterWheel1
     * @see #jcbFilterWheel2
     * @see #jcbUpperNDSlide
     * @see #jcbLowerNDSlide
     */
    private void setupFilterLists(String instrumentName) 
    {
        if (instrumentName == null) 
        {
            return;
        }
        
        if (instrumentName.equalsIgnoreCase(CONST.IO_O)) 
        {
            //Filter wheel 1
            jcbFilterWheel1.removeAllItems();
            jcbFilterWheel1.addItem(CONST.O_FW_ITEMS[0]);
            jcbFilterWheel1.addItem(CONST.O_FW_ITEMS[1]);
            jcbFilterWheel1.addItem(CONST.O_FW_ITEMS[2]);
            jcbFilterWheel1.addItem(CONST.O_FW_ITEMS[3]);
            jcbFilterWheel1.addItem(CONST.O_FW_ITEMS[4]);
            jcbFilterWheel1.addItem(CONST.O_FW_ITEMS[5]);
            jcbFilterWheel1.addItem(CONST.O_FW_ITEMS[6]);
            jcbFilterWheel1.addItem(CONST.O_FW_ITEMS[7]);
            jcbFilterWheel1.addItem(CONST.O_FW_ITEMS[8]);
            jcbFilterWheel1.addItem(CONST.O_FW_ITEMS[9]);
            jcbFilterWheel1.addItem(CONST.O_FW_ITEMS[10]);
            jcbFilterWheel1.addItem(CONST.O_FW_ITEMS[11]);
            jcbFilterWheel1.setSelectedIndex(0);
            //FIlter wheel 2
            jcbFilterWheel2.removeAllItems();
            //Upper ND slide
            jcbUpperNDSlide.removeAllItems();
            jcbUpperNDSlide.addItem(CONST.O_UPPER_ND_ITEMS[0]);
            jcbUpperNDSlide.addItem(CONST.O_UPPER_ND_ITEMS[1]);
            jcbUpperNDSlide.setSelectedIndex(0);
            //Lower ND slide
            jcbLowerNDSlide.removeAllItems();
            jcbLowerNDSlide.addItem(CONST.O_LOWER_ND_ITEMS[0]);
            jcbLowerNDSlide.addItem(CONST.O_LOWER_ND_ITEMS[1]);
            jcbLowerNDSlide.setSelectedIndex(0);
            //show FW1, ND1, ND2
            jpFilterWheel1.setVisible(true);
            jpFilterWheel2.setVisible(false);
            jpUpperNDSlide.setVisible(true);
            jpLowerNDSlide.setVisible(true);
        }
        else if (instrumentName.equalsIgnoreCase(CONST.IO_I)) 
        {
            //Filter wheel 1
            jcbFilterWheel1.removeAllItems();
            jcbFilterWheel1.addItem(CONST.I_FW_ITEMS[0]);
            jcbFilterWheel1.setSelectedIndex(0);
            //Filter wheel 1
            jcbFilterWheel2.removeAllItems();
            //Upper ND slide
            jcbUpperNDSlide.removeAllItems();
            //Lower ND slide
            jcbLowerNDSlide.removeAllItems();
            //show FW1
            jpFilterWheel1.setVisible(true);
            jpFilterWheel2.setVisible(false);
            jpUpperNDSlide.setVisible(false);
            jpLowerNDSlide.setVisible(false);
        }
        else if (instrumentName.equalsIgnoreCase(CONST.RISE)) 
        {    
            //Filter wheel 1
            jcbFilterWheel1.removeAllItems();
            jcbFilterWheel1.addItem(CONST.RISE_FW_ITEMS[0]);
            jcbFilterWheel1.setSelectedIndex(0);
            //Filter wheel 2
            jcbFilterWheel2.removeAllItems();
            //Upper ND slide
            jcbUpperNDSlide.removeAllItems();
            //Lower ND slide
            jcbLowerNDSlide.removeAllItems();
            //show FW1
            jpFilterWheel1.setVisible(true);
            jpFilterWheel2.setVisible(false);
            jpUpperNDSlide.setVisible(false);
            jpLowerNDSlide.setVisible(false);
        }
        else if (instrumentName.equalsIgnoreCase(CONST.RAPTOR)) 
        {
            //Filter wheel 1
            jcbFilterWheel1.removeAllItems();
            for(int i = 0; i < CONST.RAPTOR_FW_ITEMS.length; i++)
            {
                jcbFilterWheel1.addItem(CONST.RAPTOR_FW_ITEMS[i]);
            }
            jcbFilterWheel1.setSelectedIndex(0);
            //Filter wheel 1
            jcbFilterWheel2.removeAllItems();
            //Upper ND slide
            jcbUpperNDSlide.removeAllItems();
            //Lower ND slide
            jcbLowerNDSlide.removeAllItems();
            //show FW1
            jpFilterWheel1.setVisible(true);
            jpFilterWheel2.setVisible(false);
            jpUpperNDSlide.setVisible(false);
            jpLowerNDSlide.setVisible(false);
        }
    }

    /**
     * Return the maximum binning allowed for the specified instrument.
     * @param instrumentName The name of the instrument.
     * @return The maximum binning allowed for the specified instrument.
     */
    private int getBinningLimitOfInstrument(String instrumentName) 
    {
        if (instrumentName != null) 
        {
            /*
            if (instrumentName.equalsIgnoreCase(CONST.RATCAM)) {
                 return 2;
            */  
            if (instrumentName.equalsIgnoreCase(CONST.IO_O)) 
            {
                return 1;
            } 
            else if (instrumentName.equalsIgnoreCase(CONST.RISE)) 
            {
                return 2;
            } 
            else if (instrumentName.equalsIgnoreCase(CONST.IO_I)) 
            {
                return 1;
            }
            else if (instrumentName.equalsIgnoreCase(CONST.RAPTOR)) 
            {
                return 1;
            }
         }
        return 1;//default
    }

    /**
     * Use the filterSpec set in the specified config, to configure the filter wheel combo boxes.
     * @param imagerInstrumentConfig The instrument config to use.
     * @throws Exception Thrown if the instrument config filterSpec is null.
     * @see #jcbFilterWheel1
     * @see #jcbFilterWheel2
     * @see #selectFilter
     */
    private void selectFiltersForInstrumentConfig(XImagerInstrumentConfig imagerInstrumentConfig) throws Exception 
    {

        XFilterSpec filterSpec = imagerInstrumentConfig.getFilterSpec();

        if (filterSpec == null) 
        {
            logger.error("FilterSpec is null");
            throw new Exception("FilterSpec is null");
        }

        //first filter
        List filterList = filterSpec.getFilterList();

        if (filterList == null) 
        {
            //likely a clear filter or two clear filters.
            //jcb's will have been set up dependant upon instrument name already
            if (jcbFilterWheel1.getItemCount() > 0) 
            {
                jcbFilterWheel1.setSelectedIndex(0);
            }
            if (jcbFilterWheel2.getItemCount() > 0) 
            {
                jcbFilterWheel2.setSelectedIndex(0);
            }
            return;
        }

        //iterate through filters, selecting each found on the appropriate wheel.
        /*
        Iterator filterListIterator = filterList.iterator();
        XFilterDef filter = (XFilterDef) filterListIterator.next();
        selectFilter(filter, imagerInstrumentConfig.getInstrumentName());

        if (filterListIterator.hasNext()) {
            //2 filters, do second filter
            filter = (XFilterDef) filterListIterator.next();
            selectFilter(filter, imagerInstrumentConfig.getInstrumentName());
        }
        */
        //more than two filter wheels now (including the Neutral Density filters)
        Iterator filterListIterator = filterList.iterator();
        while (filterListIterator.hasNext()) 
        {
            XFilterDef filter = (XFilterDef) filterListIterator.next();
            selectFilter(filter, imagerInstrumentConfig.getInstrumentName());
        }
        
    }

    /**
     * Select the specified filter in a filter wheel combo box, based on the instrument name.
     * @param filter An instance of XFilterDef containing the specification of one filter.
     * @param instrumentName The name of the instrument we are configuring for.
     * @return Returns true if we manage to configure the filter successfully, false if not.
     * @see #jcbFilterWheel1
     * @see #jcbUpperNDSlide
     * @see #jcbLowerNDSlide
     */
    private boolean selectFilter(XFilterDef filter, String instrumentName) 
    {

        String filterName = filter.getFilterName();
        
        if (instrumentName.equalsIgnoreCase(CONST.RISE))
        {
            if (filterListContains(CONST.RISE_FW_ITEMS, filterName)) 
            {
                jcbFilterWheel1.setSelectedItem(filterName);
                return true;
            }
        } 
        else if (instrumentName.equalsIgnoreCase(CONST.IO_O)) 
        {
            if (filterListContains(CONST.O_FW_ITEMS, filterName)) 
            {
                jcbFilterWheel1.setSelectedItem(filterName);
                return true;
            }
            if (filterListContains(CONST.O_UPPER_ND_ITEMS, filterName)) 
            {
                jcbUpperNDSlide.setSelectedItem(filterName);
                return true;
            }
            if (filterListContains(CONST.O_LOWER_ND_ITEMS, filterName)) 
            {
                jcbLowerNDSlide.setSelectedItem(filterName);
                return true;
            }
        } 
        else if (instrumentName.equalsIgnoreCase(CONST.IO_I)) 
        {
            if (filterListContains(CONST.I_FW_ITEMS, filterName)) 
            {
                jcbFilterWheel1.setSelectedItem(filterName);
                return true;
            }
        }
        else if (instrumentName.equalsIgnoreCase(CONST.RAPTOR)) 
        {
            if (filterListContains(CONST.RAPTOR_FW_ITEMS, filterName)) 
            {
                jcbFilterWheel1.setSelectedItem(filterName);
                return true;
            }
        }
        return false;
    }

    /**
     * given a filter list, does it contain the filter named?
     */
    private boolean filterListContains(String[] filterList, String filter) 
    {

        for (int i=0; i< filterList.length; i++) 
        {
            if (filterList[i].equalsIgnoreCase(filter)) 
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Setup the nudgematic offset list combo box (and it's visibility) based on the specified imager instrument config. 
     * @param imagerInstrumentConfig The instrument config used for configuration.
     * @see #jcbNudgematicOffsetSize
     * @see #jpNudgematicPanel
     */
    private void setNudgematicOffsetSize(XImagerInstrumentConfig imagerInstrumentConfig)
    {
        // Only raptor has a nudgematic
        if(imagerInstrumentConfig instanceof XRaptorInstrumentConfig)
        {
            XRaptorInstrumentConfig raptorInstrumentConfig = (XRaptorInstrumentConfig)imagerInstrumentConfig;
            
            int nudgematicOffsetSize = raptorInstrumentConfig.getNudgematicOffsetSize();
            if(nudgematicOffsetSize == XRaptorInstrumentConfig.NUDGEMATIC_OFFSET_SIZE_NONE)
                jcbNudgematicOffsetSize.setSelectedItem(CONST.RAPTOR_NUDGEMATIC_OFFSET_SIZE_LIST[CONST.RAPTOR_NUDGEMATIC_OFFSET_SIZE_NONE]);
            else if(nudgematicOffsetSize == XRaptorInstrumentConfig.NUDGEMATIC_OFFSET_SIZE_SMALL)
                jcbNudgematicOffsetSize.setSelectedItem(CONST.RAPTOR_NUDGEMATIC_OFFSET_SIZE_LIST[CONST.RAPTOR_NUDGEMATIC_OFFSET_SIZE_SMALL]);
            else if(nudgematicOffsetSize == XRaptorInstrumentConfig.NUDGEMATIC_OFFSET_SIZE_LARGE)
                jcbNudgematicOffsetSize.setSelectedItem(CONST.RAPTOR_NUDGEMATIC_OFFSET_SIZE_LIST[CONST.RAPTOR_NUDGEMATIC_OFFSET_SIZE_LARGE]);
            jpNudgematicPanel.setVisible(true);
        }
        else
        {
            // this is not raptor, hide the nudgematic offset size combo box.
            jpNudgematicPanel.setVisible(false);
        }
    }
    
    /**
     * Setup the coadd exposure length combo box (and it's visibility) based on the specified imager instrument config.
     * @param imagerInstrumentConfig The instrument config used for configuration.
     * @see #jcbCoaddExposureLength
     * @see #jpCoaddPanel
     */
    private void setCoaddExposureLength(XImagerInstrumentConfig imagerInstrumentConfig)
    {
        // Only raptor has a coadd exposure length
        if(imagerInstrumentConfig instanceof XRaptorInstrumentConfig)
        {
            XRaptorInstrumentConfig raptorInstrumentConfig = (XRaptorInstrumentConfig)imagerInstrumentConfig;
            
            int coaddExposureLength = raptorInstrumentConfig.getCoaddExposureLength();
            jcbCoaddExposureLength.setSelectedItem(new String(""+coaddExposureLength));
            jpCoaddPanel.setVisible(true);
        }
        else
        {
            // this is not raptor, hide the coadd exposure length combo box.
            jpCoaddPanel.setVisible(false);
        }
        
    }
    
    /**
     * Construct and return an imager instrument config based on the settings in this dialog.
     * @return AN instance of ImagerInstrumentConfig (or it's subclass RaptorInstrumentConfig if necessary).
     * @throws Exception Thrown if an error occurs.
     */
    public IInstrumentConfig getInstrumentConfig() throws Exception 
    {
        XImagerInstrumentConfig imagerInstrumentConfig = null;
        XRaptorInstrumentConfig raptorInstrumentConfig = null;
        String instrumentName;
        String name;

        instrumentName = (String)jcbInstrumentName.getSelectedItem();
        // The returned instrument config will be an instance of XRaptorInstrumentConfig if the instrumentName is RAPTOR
        // and an instance of XImagerInstrumentConfig otherwise
        if (instrumentName.equalsIgnoreCase(CONST.RAPTOR))
        {
            raptorInstrumentConfig = new XRaptorInstrumentConfig();
            imagerInstrumentConfig = raptorInstrumentConfig;
        }
        else
        {
            imagerInstrumentConfig = new XImagerInstrumentConfig();
        }
        imagerInstrumentConfig.setID(originalImagerInstrumentConfig.getID());
        name = jtfInstrumentConfigName.getText();

        //need name field to have been validated

        //add filters + ND slide options dependent upon instrument
        XFilterSpec filterSpec = new XFilterSpec();
        
        if (instrumentName.equalsIgnoreCase(CONST.RISE)) 
        {
            //blank filter spec
        /*
        } else if (instrumentName.equalsIgnoreCase(CONST.RATCAM)) {
            //(in this order)
            //filter 1
            String filter1 = (String)jcbFilterWheel1.getSelectedItem();
            filterSpec.addFilter(new XFilterDef(filter1));
            //filter 2
            String filter2 = (String)jcbFilterWheel2.getSelectedItem();
            filterSpec.addFilter(new XFilterDef(filter2));
        */
        } 
        else if (instrumentName.equalsIgnoreCase(CONST.IO_O)) 
        {
            //(in this order)
            //filter 1    
            String filter1 = (String)jcbFilterWheel1.getSelectedItem();
            filterSpec.addFilter(new XFilterDef(filter1));
            //lower slide
            String lowerSlide = (String)jcbLowerNDSlide.getSelectedItem();
            filterSpec.addFilter(new XFilterDef(lowerSlide));
            //upper slide
            String upperSlide = (String)jcbUpperNDSlide.getSelectedItem();
            filterSpec.addFilter(new XFilterDef(upperSlide));
            
        }
        else if (instrumentName.equalsIgnoreCase(CONST.IO_I)) 
        {
            String filter1 = (String)jcbFilterWheel1.getSelectedItem();
            filterSpec.addFilter(new XFilterDef(filter1));
        }
        else if (instrumentName.equalsIgnoreCase(CONST.RAPTOR)) 
        {
            String filter1 = (String)jcbFilterWheel1.getSelectedItem();
            filterSpec.addFilter(new XFilterDef(filter1));
        }
        
        imagerInstrumentConfig.setFilterSpec(filterSpec);
        
        //get the detector (binning) config
        XDetectorConfig detectorConfig = (XDetectorConfig) detectorConfigStandardPanel.getDetectorConfig();
        imagerInstrumentConfig.setDetectorConfig(detectorConfig);
        
        // nudgematic offset size
        if(raptorInstrumentConfig != null)
        {
            int nudgematicOffsetSize = XRaptorInstrumentConfig.NUDGEMATIC_OFFSET_SIZE_NONE;
           
            if(jcbNudgematicOffsetSize.getSelectedItem().equals(CONST.RAPTOR_NUDGEMATIC_OFFSET_SIZE_LIST[CONST.RAPTOR_NUDGEMATIC_OFFSET_SIZE_NONE]))
                nudgematicOffsetSize = XRaptorInstrumentConfig.NUDGEMATIC_OFFSET_SIZE_NONE;
            else if(jcbNudgematicOffsetSize.getSelectedItem().equals(CONST.RAPTOR_NUDGEMATIC_OFFSET_SIZE_LIST[CONST.RAPTOR_NUDGEMATIC_OFFSET_SIZE_SMALL]))
                nudgematicOffsetSize = XRaptorInstrumentConfig.NUDGEMATIC_OFFSET_SIZE_SMALL;
            else if(jcbNudgematicOffsetSize.getSelectedItem().equals(CONST.RAPTOR_NUDGEMATIC_OFFSET_SIZE_LIST[CONST.RAPTOR_NUDGEMATIC_OFFSET_SIZE_LARGE]))
                nudgematicOffsetSize = XRaptorInstrumentConfig.NUDGEMATIC_OFFSET_SIZE_LARGE;
            else
                nudgematicOffsetSize = XRaptorInstrumentConfig.NUDGEMATIC_OFFSET_SIZE_NONE;
            raptorInstrumentConfig.setNudgematicOffsetSize(nudgematicOffsetSize);
        }
        // coadd exposure length
        if(raptorInstrumentConfig != null)
        {
            String coaddExpsoureLengthString = null;
            int coaddExposureLength;
            
            try
            {
                coaddExpsoureLengthString = (String)jcbCoaddExposureLength.getSelectedItem();
                coaddExposureLength = Integer.parseInt(coaddExpsoureLengthString);
            }
            catch(Exception e)
            {
                throw new IllegalArgumentException("Coadd Exposure Length is not a valid integer:"+coaddExpsoureLengthString);
            }
            raptorInstrumentConfig.setCoaddExposureLength(coaddExposureLength);
        }
       
        imagerInstrumentConfig.setName(name);
        imagerInstrumentConfig.setInstrumentName(instrumentName);
 
        return imagerInstrumentConfig;
    }

    public boolean containsValidInstrumentConfig() {

        if (jtfInstrumentConfigName.getText().trim().length() ==0) {
            return false;
        }
        /*
        if (!detectorConfigEditorPanel.containsValidDetectorConfig()) {
            return false;
        }
        */
        return true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        //detectorConfigEditorPanel.setEnabled(enabled);
        jcbInstrumentName.setEnabled(enabled);
        jbtnRemoveFilter.setEnabled(enabled);
        jtfInstrumentConfigName.setEnabled(enabled);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jcbInstrumentName = new javax.swing.JComboBox();
        jPanel6 = new javax.swing.JPanel();
        jtfInstrumentConfigName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jbtnRemoveFilter = new javax.swing.JButton();
        jpFilterWheel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jcbFilterWheel1 = new javax.swing.JComboBox();
        jpUpperNDSlide = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jcbUpperNDSlide = new javax.swing.JComboBox();
        jpLowerNDSlide = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jcbLowerNDSlide = new javax.swing.JComboBox();
        jpFilterWheel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jcbFilterWheel2 = new javax.swing.JComboBox();
        detectorConfigStandardPanel = new ngat.beans.guibeans.DetectorConfigStandardPanel();
        jpNudgematicPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jcbNudgematicOffsetSize = new javax.swing.JComboBox();
        jpCoaddPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jcbCoaddExposureLength = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Instrument Name"));

        jcbInstrumentName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbInstrumentNameActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jcbInstrumentName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 123, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(387, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jcbInstrumentName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Instrument Config Name"));

        jtfInstrumentConfigName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtfInstrumentConfigNameActionPerformed(evt);
            }
        });

        jLabel3.setText("(no spaces please)");

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jtfInstrumentConfigName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 362, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3)
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jtfInstrumentConfigName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Filter Specification"));

        jbtnRemoveFilter.setText(">");
        jbtnRemoveFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnRemoveFilterActionPerformed(evt);
            }
        });

        jpFilterWheel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Filter Wheel 1");

        org.jdesktop.layout.GroupLayout jpFilterWheel1Layout = new org.jdesktop.layout.GroupLayout(jpFilterWheel1);
        jpFilterWheel1.setLayout(jpFilterWheel1Layout);
        jpFilterWheel1Layout.setHorizontalGroup(
            jpFilterWheel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpFilterWheel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jpFilterWheel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jpFilterWheel1Layout.createSequentialGroup()
                        .add(jLabel1)
                        .add(0, 107, Short.MAX_VALUE))
                    .add(jcbFilterWheel1, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jpFilterWheel1Layout.setVerticalGroup(
            jpFilterWheel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpFilterWheel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jcbFilterWheel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jpUpperNDSlide.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setText("Upper ND slide");

        org.jdesktop.layout.GroupLayout jpUpperNDSlideLayout = new org.jdesktop.layout.GroupLayout(jpUpperNDSlide);
        jpUpperNDSlide.setLayout(jpUpperNDSlideLayout);
        jpUpperNDSlideLayout.setHorizontalGroup(
            jpUpperNDSlideLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpUpperNDSlideLayout.createSequentialGroup()
                .addContainerGap()
                .add(jpUpperNDSlideLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jpUpperNDSlideLayout.createSequentialGroup()
                        .add(jLabel5)
                        .add(0, 98, Short.MAX_VALUE))
                    .add(jcbUpperNDSlide, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jpUpperNDSlideLayout.setVerticalGroup(
            jpUpperNDSlideLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpUpperNDSlideLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel5)
                .add(12, 12, 12)
                .add(jcbUpperNDSlide, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jpLowerNDSlide.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel6.setText("Lower ND slide");

        org.jdesktop.layout.GroupLayout jpLowerNDSlideLayout = new org.jdesktop.layout.GroupLayout(jpLowerNDSlide);
        jpLowerNDSlide.setLayout(jpLowerNDSlideLayout);
        jpLowerNDSlideLayout.setHorizontalGroup(
            jpLowerNDSlideLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpLowerNDSlideLayout.createSequentialGroup()
                .addContainerGap()
                .add(jpLowerNDSlideLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jcbLowerNDSlide, 0, 201, Short.MAX_VALUE)
                    .add(jLabel6))
                .addContainerGap())
        );
        jpLowerNDSlideLayout.setVerticalGroup(
            jpLowerNDSlideLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpLowerNDSlideLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel6)
                .add(12, 12, 12)
                .add(jcbLowerNDSlide, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jpFilterWheel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Filter Wheel 2");

        org.jdesktop.layout.GroupLayout jpFilterWheel2Layout = new org.jdesktop.layout.GroupLayout(jpFilterWheel2);
        jpFilterWheel2.setLayout(jpFilterWheel2Layout);
        jpFilterWheel2Layout.setHorizontalGroup(
            jpFilterWheel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpFilterWheel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jpFilterWheel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jpFilterWheel2Layout.createSequentialGroup()
                        .add(jLabel2)
                        .add(0, 107, Short.MAX_VALUE))
                    .add(jcbFilterWheel2, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jpFilterWheel2Layout.setVerticalGroup(
            jpFilterWheel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpFilterWheel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jcbFilterWheel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(460, 460, 460)
                        .add(jbtnRemoveFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jpFilterWheel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jpUpperNDSlide, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jpFilterWheel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jpLowerNDSlide, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {jpFilterWheel1, jpFilterWheel2, jpLowerNDSlide, jpUpperNDSlide}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jpFilterWheel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jpFilterWheel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(12, 12, 12)
                        .add(jpUpperNDSlide, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jpLowerNDSlide, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jbtnRemoveFilter)
                .add(53, 53, 53))
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {jpFilterWheel1, jpFilterWheel2, jpLowerNDSlide, jpUpperNDSlide}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jpNudgematicPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Nudgematic"));
        jpNudgematicPanel.setName("jpNudgematicPanel"); // NOI18N

        jLabel4.setText("Offset Size");

        jcbNudgematicOffsetSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "Small", "Large" }));
        jcbNudgematicOffsetSize.setName("jcbNudgematicOffsetSize"); // NOI18N

        org.jdesktop.layout.GroupLayout jpNudgematicPanelLayout = new org.jdesktop.layout.GroupLayout(jpNudgematicPanel);
        jpNudgematicPanel.setLayout(jpNudgematicPanelLayout);
        jpNudgematicPanelLayout.setHorizontalGroup(
            jpNudgematicPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpNudgematicPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jcbNudgematicOffsetSize, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpNudgematicPanelLayout.setVerticalGroup(
            jpNudgematicPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpNudgematicPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel4)
                .add(jcbNudgematicOffsetSize, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jpCoaddPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Coadd"));

        jLabel7.setText("Exposure Length");

        jcbCoaddExposureLength.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "100", "1000" }));

        jLabel8.setText("milliseconds");

        org.jdesktop.layout.GroupLayout jpCoaddPanelLayout = new org.jdesktop.layout.GroupLayout(jpCoaddPanel);
        jpCoaddPanel.setLayout(jpCoaddPanelLayout);
        jpCoaddPanelLayout.setHorizontalGroup(
            jpCoaddPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpCoaddPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jcbCoaddExposureLength, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel8)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpCoaddPanelLayout.setVerticalGroup(
            jpCoaddPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpCoaddPanelLayout.createSequentialGroup()
                .add(jpCoaddPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(jcbCoaddExposureLength, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(0, 0, Short.MAX_VALUE))
            .add(jpCoaddPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel8)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, detectorConfigStandardPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jpNudgematicPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jpCoaddPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(detectorConfigStandardPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 216, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jpNudgematicPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jpCoaddPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(83, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jcbInstrumentNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbInstrumentNameActionPerformed
        String selectedInstrumentName = (String)jcbInstrumentName.getSelectedItem();
        if (selectedInstrumentName == null) {
            return;
        }
        setupFilterLists(selectedInstrumentName);
        setBinningOptions(selectedInstrumentName);
        if (selectedInstrumentName.equalsIgnoreCase(CONST.RAPTOR))
        {
            jpNudgematicPanel.setVisible(true);
            jpCoaddPanel.setVisible(true);
        }
        else
        {
            jpNudgematicPanel.setVisible(false);
            jpCoaddPanel.setVisible(false);        
        }
}//GEN-LAST:event_jcbInstrumentNameActionPerformed

    private void jbtnRemoveFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnRemoveFilterActionPerformed

}//GEN-LAST:event_jbtnRemoveFilterActionPerformed

    private void jtfInstrumentConfigNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtfInstrumentConfigNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtfInstrumentConfigNameActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ngat.beans.guibeans.DetectorConfigStandardPanel detectorConfigStandardPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JButton jbtnRemoveFilter;
    private javax.swing.JComboBox jcbCoaddExposureLength;
    private javax.swing.JComboBox jcbFilterWheel1;
    private javax.swing.JComboBox jcbFilterWheel2;
    private javax.swing.JComboBox jcbInstrumentName;
    private javax.swing.JComboBox jcbLowerNDSlide;
    private javax.swing.JComboBox jcbNudgematicOffsetSize;
    private javax.swing.JComboBox jcbUpperNDSlide;
    private javax.swing.JPanel jpCoaddPanel;
    private javax.swing.JPanel jpFilterWheel1;
    private javax.swing.JPanel jpFilterWheel2;
    private javax.swing.JPanel jpLowerNDSlide;
    private javax.swing.JPanel jpNudgematicPanel;
    private javax.swing.JPanel jpUpperNDSlide;
    private javax.swing.JTextField jtfInstrumentConfigName;
    // End of variables declaration//GEN-END:variables

}
