/*
 *
 * Created on April 17, 2009, 2:07 PM
 */

package ngat.oss.client.gui.panel.instrumentConfig;

import ngat.oss.client.gui.panel.interfaces.IInstrumentConfigPanel;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import ngat.oss.client.gui.reference.CONST;
import ngat.phase2.IDetectorConfig;
import ngat.phase2.IInstrumentConfig;
import ngat.phase2.XDetectorConfig;
import ngat.phase2.XFilterDef;
import ngat.phase2.XFilterSpec;
import ngat.phase2.XImagerInstrumentConfig;
import ngat.phase2.XInstrumentConfig;
import ngat.phase2.XPolarimeterInstrumentConfig;
import ngat.phase2.XMoptopInstrumentConfig;
import org.apache.log4j.Logger;

/**
 * Panel to edit polarimeter instruments configs (for Ringo3, MOPTOP etc).
 * @author  nrc,cjm
 */
public class PolarimeterInstrumentConfigEditorPanel extends javax.swing.JPanel implements IInstrumentConfigPanel 
{
    static Logger logger = Logger.getLogger(ImagerInstrumentConfigEditorPanel.class);
    private boolean enabled;
    private long originalID;

    public PolarimeterInstrumentConfigEditorPanel(XPolarimeterInstrumentConfig polarimeterInstrumentConfig, boolean isNewInstrumentConfig) {
        
        this.originalID = polarimeterInstrumentConfig.getID();
        initComponents();
        populateComponents(polarimeterInstrumentConfig, isNewInstrumentConfig);
    }

    public PolarimeterInstrumentConfigEditorPanel(XMoptopInstrumentConfig moptopInstrumentConfig, boolean isNewInstrumentConfig) {
        
        this.originalID = moptopInstrumentConfig.getID();
        initComponents();
        populateComponents(moptopInstrumentConfig, isNewInstrumentConfig);
    }

    private void populateComponents(XInstrumentConfig instrumentConfig, boolean isNewInstrumentConfig) 
    {
        String instrumentName = null;

        if (instrumentConfig == null) {
            return;
        }

        //deleted 12/11/12:
        /*
        if (isNewInstrumentConfig) {
            jtfInstrumentName.setText(CONST.RINGO2);
            JOptionPane.showMessageDialog(this, "DEFAULTING TO RINGO2");
        } else {
        */

        //not new, get values from polarimeterInstrumentConfig
        jtfInstrumentConfigName.setText(instrumentConfig.getName());

        //added 12/11/12 :

        if (instrumentConfig.getInstrumentName() == null) 
        {
            //default to MOPTOP
            jcbInstrumentName.setSelectedItem(CONST.MOPTOP);
            instrumentName = CONST.MOPTOP;
        } 
        else 
        {
            jcbInstrumentName.setSelectedItem(instrumentConfig.getInstrumentName());
            instrumentName = instrumentConfig.getInstrumentName();
        }
        
        
        if(instrumentConfig instanceof XPolarimeterInstrumentConfig)
        {
            XPolarimeterInstrumentConfig polarimeterInstrumentConfig = (XPolarimeterInstrumentConfig)instrumentConfig;
            
            int gain = polarimeterInstrumentConfig.getGain();
            String gainStr = String.valueOf(gain);
            jcbGain.setSelectedItem(gainStr);
            jcbGain.setVisible(true);
            jGainPanel.setVisible(true);
            jcbRotorSpeed.setVisible(false);
            jRotorSpeedPanel.setVisible(false);
            jcbFilterWheel1.setVisible(false);
            jFilterWheelPanel1.setVisible(false);
        }
        else if(instrumentConfig instanceof XMoptopInstrumentConfig)
        { 
            XMoptopInstrumentConfig moptopInstrumentConfig = (XMoptopInstrumentConfig)instrumentConfig;
            int rotorSpeed = moptopInstrumentConfig.getRotorSpeed();
            jcbRotorSpeed.setSelectedIndex(rotorSpeed);
            jcbGain.setVisible(false);
            jGainPanel.setVisible(false);
            jcbRotorSpeed.setVisible(true);
            jRotorSpeedPanel.setVisible(true);
            setupFilterLists(instrumentName);
            try
            {
                selectFiltersForInstrumentConfig(moptopInstrumentConfig);
            } 
            catch (Exception ex)
            {
                ex.printStackTrace();
                logger.error(ex);
                JOptionPane.showMessageDialog(this, ex);
                return;
            }
            jcbFilterWheel1.setVisible(true);
            jFilterWheelPanel1.setVisible(true);
        }
    }
    
    /**
     * Setup the filter wheel combo-box/panel based on the specified filter wheel name.
     * @param instrumentName The name of the instrument to configure the filter wheel combo-box filters for. 
     *        Only CONST.MOPTOP is supported at present.
     * @see CONST.MOPTOP
     * @see CONST.MOPTOP_FW_ITEMS
     * @see #jcbFilterWheel1
     * @see #jFilterWheelPanel1
     */
    private void setupFilterLists(String instrumentName) 
    {
        if (instrumentName == null) 
        {
            return;
        }
        if (instrumentName.equalsIgnoreCase(CONST.MOPTOP)) 
        {
            //Filter wheel 1
            jcbFilterWheel1.removeAllItems();
            jcbFilterWheel1.addItem(CONST.MOPTOP_FW_ITEMS[0]);
            jcbFilterWheel1.addItem(CONST.MOPTOP_FW_ITEMS[1]);
            jcbFilterWheel1.addItem(CONST.MOPTOP_FW_ITEMS[2]);
            jcbFilterWheel1.addItem(CONST.MOPTOP_FW_ITEMS[3]);
            jcbFilterWheel1.addItem(CONST.MOPTOP_FW_ITEMS[4]);
            jcbFilterWheel1.setSelectedIndex(0);
            
            //show FW1
            jFilterWheelPanel1.setVisible(true);
            
        } 
    }

    private void selectFiltersForInstrumentConfig(XMoptopInstrumentConfig moptopInstrumentConfig) throws Exception 
    {

        XFilterSpec filterSpec = moptopInstrumentConfig.getFilterSpec();

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
        while (filterListIterator.hasNext()) {
            XFilterDef filter = (XFilterDef) filterListIterator.next();
            selectFilter(filter, moptopInstrumentConfig.getInstrumentName());
        }
        
    }

    private boolean selectFilter(XFilterDef filter, String instrumentName) 
    {
        String filterName = filter.getFilterName();
        
        if (instrumentName.equalsIgnoreCase(CONST.MOPTOP))
        {
            if (filterListContains(CONST.MOPTOP_FW_ITEMS, filterName)) 
            {
                jcbFilterWheel1.setSelectedItem(filterName);
                return true;
            }
        } 
        return false;
    }

    //given a filter list, does it contain the filter named?
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
    
    public IInstrumentConfig getInstrumentConfig() throws Exception 
    {
        String selectedInstrumentName = (String)jcbInstrumentName.getSelectedItem();

        if (selectedInstrumentName.equalsIgnoreCase(CONST.RINGO3)) 
        {
    
            XPolarimeterInstrumentConfig polarimeterInstrumentConfig = new XPolarimeterInstrumentConfig();
        
            polarimeterInstrumentConfig.setID(originalID);
            String name = jtfInstrumentConfigName.getText();
        
            String instrumentName = (String)jcbInstrumentName.getSelectedItem();
        
            int gain = Integer.parseInt((String)jcbGain.getSelectedItem());

            polarimeterInstrumentConfig.setGain(gain);

            XDetectorConfig detectorConfig = new XDetectorConfig();
            detectorConfig.setXBin(1);  //default
            detectorConfig.setYBin(1);  //default
            polarimeterInstrumentConfig.setDetectorConfig(detectorConfig);
            polarimeterInstrumentConfig.setName(name);
            polarimeterInstrumentConfig.setInstrumentName(instrumentName);
       
            return polarimeterInstrumentConfig;
        }
        else if (selectedInstrumentName.equalsIgnoreCase(CONST.MOPTOP)) 
        {
            XMoptopInstrumentConfig moptopInstrumentConfig = new XMoptopInstrumentConfig();
        
            moptopInstrumentConfig.setID(originalID);
            String name = jtfInstrumentConfigName.getText();
        
            String instrumentName = (String)jcbInstrumentName.getSelectedItem();
        
            // set rotorspeed
            int rotorSpeed = XMoptopInstrumentConfig.ROTOR_SPEED_UNKNOWN;
           
            if(jcbRotorSpeed.getSelectedItem().equals(CONST.MOPTOP_ROTOR_SPEEDS[1]))
                rotorSpeed = XMoptopInstrumentConfig.ROTOR_SPEED_SLOW;
            else if(jcbRotorSpeed.getSelectedItem().equals(CONST.MOPTOP_ROTOR_SPEEDS[2]))
                rotorSpeed = XMoptopInstrumentConfig.ROTOR_SPEED_FAST;
            else
                rotorSpeed = XMoptopInstrumentConfig.ROTOR_SPEED_UNKNOWN;
            moptopInstrumentConfig.setRotorSpeed(rotorSpeed);
            
            // set filter
            XFilterSpec filterSpec = new XFilterSpec();
            //filter 1    
            String filter1 = (String)jcbFilterWheel1.getSelectedItem();
            filterSpec.addFilter(new XFilterDef(filter1));
            moptopInstrumentConfig.setFilterSpec(filterSpec);

            // setup detector (binning)
            XDetectorConfig detectorConfig = new XDetectorConfig();
            detectorConfig.setXBin(2);  //default
            detectorConfig.setYBin(2);  //default
            moptopInstrumentConfig.setDetectorConfig(detectorConfig);
            moptopInstrumentConfig.setName(name);
            moptopInstrumentConfig.setInstrumentName(instrumentName);
       
            return moptopInstrumentConfig;
        }
        throw new Exception(this.getClass().getName()+"getInstrumentConfig:Unknown Polarimeter Type:"+selectedInstrumentName);
    }
    
    public boolean containsValidInstrumentConfig() 
    {
        // check instrument config name is not null
        if (jtfInstrumentConfigName.getText().trim().length() ==0) 
        {
            return false;
        }
        return true;
    }
    
    public boolean isEnabled() 
    {
        return enabled;
    }

    public void setEnabled(boolean enabled) 
    {
        this.enabled = enabled;

        jcbGain.setEnabled(enabled);
        jcbRotorSpeed.setEnabled(enabled);
        jcbFilterWheel1.setEnabled(enabled);
        //deleted 12/11/12:
        //jtfInstrumentName.setEnabled(enabled);
        //added 12/11/12:
        jcbInstrumentName.setEnabled(enabled);
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
        jGainPanel = new javax.swing.JPanel();
        jcbGain = new javax.swing.JComboBox();
        jRotorSpeedPanel = new javax.swing.JPanel();
        jcbRotorSpeed = new javax.swing.JComboBox();
        jFilterWheelPanel1 = new javax.swing.JPanel();
        jcbFilterWheel1 = new javax.swing.JComboBox();

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Instrument Name"));

        jcbInstrumentName.setModel(new javax.swing.DefaultComboBoxModel(CONST.POLARIMETER_INSTRUMENTS));
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
                .add(jcbInstrumentName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 241, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jcbInstrumentName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Instrument Config Name"));

        jLabel3.setText("(no spaces please)");

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jtfInstrumentConfigName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 362, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3)
                .addContainerGap(39, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jtfInstrumentConfigName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jGainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Gain"));

        jcbGain.setModel(new javax.swing.DefaultComboBoxModel(CONST.RINGO_GAINS));
        jcbGain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbGainActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jGainPanelLayout = new org.jdesktop.layout.GroupLayout(jGainPanel);
        jGainPanel.setLayout(jGainPanelLayout);
        jGainPanelLayout.setHorizontalGroup(
            jGainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jGainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jcbGain, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 123, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(406, Short.MAX_VALUE))
        );
        jGainPanelLayout.setVerticalGroup(
            jGainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jGainPanelLayout.createSequentialGroup()
                .add(jcbGain, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jRotorSpeedPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Rotor Speed"));

        jcbRotorSpeed.setModel(new javax.swing.DefaultComboBoxModel(CONST.MOPTOP_ROTOR_SPEEDS));
        jcbRotorSpeed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbRotorSpeedActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jRotorSpeedPanelLayout = new org.jdesktop.layout.GroupLayout(jRotorSpeedPanel);
        jRotorSpeedPanel.setLayout(jRotorSpeedPanelLayout);
        jRotorSpeedPanelLayout.setHorizontalGroup(
            jRotorSpeedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jRotorSpeedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jcbRotorSpeed, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 123, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(406, Short.MAX_VALUE))
        );
        jRotorSpeedPanelLayout.setVerticalGroup(
            jRotorSpeedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jRotorSpeedPanelLayout.createSequentialGroup()
                .add(jcbRotorSpeed, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jFilterWheelPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Filter Wheel"));

        jcbFilterWheel1.setModel(new javax.swing.DefaultComboBoxModel(CONST.MOPTOP_FW_ITEMS));
        jcbFilterWheel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbFilterWheel1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jFilterWheelPanel1Layout = new org.jdesktop.layout.GroupLayout(jFilterWheelPanel1);
        jFilterWheelPanel1.setLayout(jFilterWheelPanel1Layout);
        jFilterWheelPanel1Layout.setHorizontalGroup(
            jFilterWheelPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jFilterWheelPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jcbFilterWheel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 123, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(406, Short.MAX_VALUE))
        );
        jFilterWheelPanel1Layout.setVerticalGroup(
            jFilterWheelPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jFilterWheelPanel1Layout.createSequentialGroup()
                .add(jcbFilterWheel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jGainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jRotorSpeedPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jFilterWheelPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jGainPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jRotorSpeedPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jFilterWheelPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jRotorSpeedPanel.getAccessibleContext().setAccessibleName("Rotor Speed");
    }// </editor-fold>//GEN-END:initComponents

private void jcbGainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbGainActionPerformed
    // No handling needed
}//GEN-LAST:event_jcbGainActionPerformed

    private void jcbInstrumentNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbInstrumentNameActionPerformed
        String selectedInstrumentName = (String)jcbInstrumentName.getSelectedItem();
        if (selectedInstrumentName == null) 
        {
            return;
        }
        if (selectedInstrumentName.equalsIgnoreCase(CONST.RINGO3)) 
        {
            //Ringo3 has Gain
            jcbGain.setVisible(true);
            jGainPanel.setVisible(true);
            // Ringo3 does not have rotor speed, filter wheel1
            jcbRotorSpeed.setVisible(false);
            jRotorSpeedPanel.setVisible(false);
            jcbFilterWheel1.setVisible(false);
            jFilterWheelPanel1.setVisible(false);
         } 
        else if (selectedInstrumentName.equalsIgnoreCase(CONST.MOPTOP)) 
        {
            // MOPTOP has no Gain
            jcbGain.setVisible(false);
            jGainPanel.setVisible(false);
            // MOPTOP has rotor speed, filter wheel1
            jcbRotorSpeed.setVisible(true);
            jRotorSpeedPanel.setVisible(true);
            jcbFilterWheel1.setVisible(true);
            jFilterWheelPanel1.setVisible(true);
         }
    }//GEN-LAST:event_jcbInstrumentNameActionPerformed

    private void jcbRotorSpeedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbRotorSpeedActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jcbRotorSpeedActionPerformed

    private void jcbFilterWheel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbFilterWheel1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jcbFilterWheel1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jFilterWheelPanel1;
    private javax.swing.JPanel jGainPanel;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jRotorSpeedPanel;
    private javax.swing.JComboBox jcbFilterWheel1;
    private javax.swing.JComboBox jcbGain;
    private javax.swing.JComboBox jcbInstrumentName;
    private javax.swing.JComboBox jcbRotorSpeed;
    private javax.swing.JTextField jtfInstrumentConfigName;
    // End of variables declaration//GEN-END:variables

}
