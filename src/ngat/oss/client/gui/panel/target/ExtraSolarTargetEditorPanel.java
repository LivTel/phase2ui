/*
 *
 * Created on April 17, 2009, 2:07 PM
 */

package ngat.oss.client.gui.panel.target;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import ngat.astrometry.ReferenceFrame;
import ngat.oss.client.gui.reference.Session;
import ngat.oss.client.gui.util.LimitedCharactersDocument;
import ngat.phase2.ITarget;
import ngat.phase2.XExtraSolarTarget;
import ngat.phase2.util.UnitConverter;

/**
 *
 * @author  nrc
 */
public class ExtraSolarTargetEditorPanel extends javax.swing.JPanel {

    private boolean enabled;
    private XExtraSolarTarget originalExtraSolarTarget;

    /** Creates new form */
    public ExtraSolarTargetEditorPanel(XExtraSolarTarget extraSolarTarget, boolean isNewTarget) {
        this.originalExtraSolarTarget = extraSolarTarget;

        initComponents();
     
        jtfTargetName.setDocument(new LimitedCharactersDocument(LimitedCharactersDocument.STRICT_LIMITATION));

        populateComponents(extraSolarTarget, isNewTarget);
    }

    private void populateComponents(XExtraSolarTarget extraSolarTarget, boolean isNewTarget) {
        
        //nrc, uncommented Dec 16.
        //hide the proper motion components dependent upon the user type.
        jpPMRAPanel.setVisible(Session.getInstance().getUser().isSuperUser());
        jpPMDECPanel.setVisible(Session.getInstance().getUser().isSuperUser());
        
        if (Session.getInstance().getUser().isSuperUser()) {
            double pmRa = UnitConverter.convertRadsToSecs(extraSolarTarget.getPmRA());
            double pmDec = UnitConverter.convertRadsToArcSecs(extraSolarTarget.getPmDec());

            DecimalFormat df = new DecimalFormat("#.#######");
            df.setRoundingMode(RoundingMode.CEILING);

            String pmRaStr = df.format(pmRa);
            String pmDecStr = df.format(pmDec);
        
            jtfPmRA.setText(pmRaStr);
            jtfPmDEC.setText(pmDecStr);
        }
        
        jtfTargetName.setText(extraSolarTarget.getName());
        repRa.setRa(extraSolarTarget.getRa());
        depDec.setDec(extraSolarTarget.getDec());
        
        nepParallax.setNumber(UnitConverter.convertRadsToArcSecs(extraSolarTarget.getParallax()));

        nepRadVel.setNumber(extraSolarTarget.getRadialVelocity());
        
        int frame = extraSolarTarget.getFrame();
        
        //jcbRefFrame items list = FK5, FK4, ICRF
        switch (frame) {
            case ReferenceFrame.FK5:
                jcbRefFrame.setSelectedIndex(0);
                break;
            case ReferenceFrame.FK4:
                jcbRefFrame.setSelectedIndex(1);
                break;
                /*
            case ReferenceFrame.ICRF:
                jcbRefFrame.setSelectedIndex(2);
                break;
                 */
        }

        if (isNewTarget) {
            jtfEpoch.setText("2000");
        } else {
            jtfEpoch.setText(String.valueOf(extraSolarTarget.getEpoch()));
        }
    }
     
    public ITarget getTarget() throws Exception {
        
        XExtraSolarTarget extraSolarTarget = new XExtraSolarTarget();
        
        String name;
        double ra, dec, pmRa, pmDec, parallax, radialVelocity, epoch;
        int frame;        
        
        //set to zero
        pmRa = 0;
        pmDec = 0;
        
        ra = repRa.getRa();
        dec = depDec.getDec();
        
        if (Session.getInstance().getUser().isSuperUser()) {
            pmRa = UnitConverter.convertSecsToRads(Double.parseDouble(jtfPmRA.getText()));
            pmDec = UnitConverter.convertArcsecsToRads(Double.parseDouble(jtfPmDEC.getText()));
        }
        
        parallax = UnitConverter.convertArcsecsToRads(nepParallax.getNumber());
        
        radialVelocity = nepRadVel.getNumber();
        epoch = Double.parseDouble(jtfEpoch.getText());
        name = jtfTargetName.getText();
        
        int selectedIndex = jcbRefFrame.getSelectedIndex();
        
        //jcbRefFrame items list = FK5, FK4, ICRF
        switch (selectedIndex) {
            case 0:
                frame = ReferenceFrame.FK5;
                break;
            case 1:
                frame = ReferenceFrame.FK4;
                break;
                /*
            case 2:
                frame = ReferenceFrame.ICRF;
                break;
                 */
            default:
                frame = ReferenceFrame.FK5;
                break;
        }
        
        extraSolarTarget.setID(originalExtraSolarTarget.getID());
        extraSolarTarget.setName(name);
        extraSolarTarget.setRa(ra);
        extraSolarTarget.setDec(dec);
        
        if (Session.getInstance().getUser().isSuperUser()) {
            extraSolarTarget.setPmRA(pmRa);
            extraSolarTarget.setPmDec(pmDec);
        }
        
        extraSolarTarget.setParallax(parallax);
        extraSolarTarget.setRadialVelocity(radialVelocity);
        extraSolarTarget.setEpoch(epoch);
        extraSolarTarget.setFrame(frame);
        
        System.err.println("return extraSolarTarget:" + extraSolarTarget);
        
        return extraSolarTarget;
    }

    public boolean containsValidTarget() {
        if (jtfTargetName.getText().trim().length() == 0)
            return false;
        /*
        if (jtfPmRA.getText().trim().length() == 0)
            return false;
        if (jtfPmDEC.getText().trim().length() == 0)
            return false;
        */
        if (jtfEpoch.getText().trim().length() == 0)
            return false;

        try {
            nepParallax.getNumber();
            nepRadVel.getNumber();
            repRa.getRa();
            depDec.getDec();
        } catch (NumberFormatException nfe) {
            return false;
        } catch (ParseException ex) {
            return false;
        }

        return true;
    }
    
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        
        if (Session.getInstance().getUser().isSuperUser()) {
            jtfPmDEC.setEnabled(enabled);
            jtfPmRA.setEnabled(enabled);
        } else {
            jtfPmDEC.setEnabled(false);
            jtfPmRA.setEnabled(false);
        }
        
        depDec.setEnabled(enabled);
        
        jcbRefFrame.setEnabled(enabled);
        jtfEpoch.setEnabled(enabled);
        nepRadVel.setEnabled(enabled);
        nepParallax.setEnabled(enabled);
        
        repRa.setEnabled(enabled);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        repRa = new ngat.beans.guibeans.RaEditorPanel();
        depDec = new ngat.beans.guibeans.DecEditorPanel();
        nepParallax = new ngat.beans.guibeans.NumberEditorPanel();
        nepRadVel = new ngat.beans.guibeans.NumberEditorPanel();
        jPanel2 = new javax.swing.JPanel();
        jcbRefFrame = new javax.swing.JComboBox();
        jpPMRAPanel = new javax.swing.JPanel();
        jtfPmRA = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jpPMDECPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jtfPmDEC = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jtfEpoch = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jtfTargetName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();

        repRa.setEnabled(true);

        depDec.setEnabled(true);

        nepParallax.setBorder(javax.swing.BorderFactory.createTitledBorder("Parallax (arcsec)"));
        nepParallax.setEnabled(true);

        nepRadVel.setBorder(javax.swing.BorderFactory.createTitledBorder("Radial Velocity (km/s)"));
        nepRadVel.setEnabled(true);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Reference Frame"));

        jcbRefFrame.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "FK5", "FK4" }));
        jcbRefFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbRefFrameActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jcbRefFrame, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 132, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jcbRefFrame, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        jpPMRAPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Proper Motion RA", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, java.awt.Color.black));

        jtfPmRA.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jtfPmRA.setText("     ");

        jLabel1.setText("sec/yr");

        org.jdesktop.layout.GroupLayout jpPMRAPanelLayout = new org.jdesktop.layout.GroupLayout(jpPMRAPanel);
        jpPMRAPanel.setLayout(jpPMRAPanelLayout);
        jpPMRAPanelLayout.setHorizontalGroup(
            jpPMRAPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jpPMRAPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jtfPmRA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel1)
                .addContainerGap())
        );
        jpPMRAPanelLayout.setVerticalGroup(
            jpPMRAPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpPMRAPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel1)
                .add(jtfPmRA, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jpPMDECPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Proper Motion DEC", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, java.awt.Color.black));

        jLabel2.setText("arcsec/yr");

        jtfPmDEC.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jtfPmDEC.setText("     ");

        org.jdesktop.layout.GroupLayout jpPMDECPanelLayout = new org.jdesktop.layout.GroupLayout(jpPMDECPanel);
        jpPMDECPanel.setLayout(jpPMDECPanelLayout);
        jpPMDECPanelLayout.setHorizontalGroup(
            jpPMDECPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpPMDECPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jtfPmDEC, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpPMDECPanelLayout.setVerticalGroup(
            jpPMDECPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpPMDECPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jtfPmDEC, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jLabel2))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Epoch"));

        jtfEpoch.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jtfEpoch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 73, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jtfEpoch, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Target name"));

        jLabel3.setText("(no spaces please)");

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(11, 11, 11)
                .add(jtfTargetName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 196, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jtfTargetName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jLabel3))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(8, 8, 8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jpPMRAPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jpPMDECPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(repRa, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(depDec, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(layout.createSequentialGroup()
                                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                            .add(layout.createSequentialGroup()
                                .add(nepParallax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .add(3, 3, 3)))
                        .add(nepRadVel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(188, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(repRa, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 71, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(depDec, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 71, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jpPMRAPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jpPMDECPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(nepParallax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, Short.MAX_VALUE)
                        .add(nepRadVel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap(24, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jcbRefFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbRefFrameActionPerformed
        String selectedItemString = (String)jcbRefFrame.getSelectedItem();

        if (selectedItemString.equalsIgnoreCase("FK5")) {
            jtfEpoch.setText("2000");
        } else if (selectedItemString.equalsIgnoreCase("FK4")) {
            jtfEpoch.setText("1950");
        } /*
        else if (selectedItemString.equalsIgnoreCase("ICRF")) {
            jtfEpoch.setText("2000");
        } */
    }//GEN-LAST:event_jcbRefFrameActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ngat.beans.guibeans.DecEditorPanel depDec;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JComboBox jcbRefFrame;
    private javax.swing.JPanel jpPMDECPanel;
    private javax.swing.JPanel jpPMRAPanel;
    private javax.swing.JTextField jtfEpoch;
    private javax.swing.JTextField jtfPmDEC;
    private javax.swing.JTextField jtfPmRA;
    private javax.swing.JTextField jtfTargetName;
    private ngat.beans.guibeans.NumberEditorPanel nepParallax;
    private ngat.beans.guibeans.NumberEditorPanel nepRadVel;
    private ngat.beans.guibeans.RaEditorPanel repRa;
    // End of variables declaration//GEN-END:variables

}
