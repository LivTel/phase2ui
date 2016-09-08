/*
 * NewTimingConstraintDialog.java
 *
 * Created on April 30, 2009, 10:38 AM
 */
package ngat.oss.client.gui.dialog;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import ngat.astrometry.Position;
import ngat.phase2.ITarget;
import ngat.phase2.XEphemerisTarget;
import ngat.phase2.XEphemerisTrackNode;
import org.apache.log4j.Logger;

/**
 *
 * @author  nrc
 */
public class InputEphemerisTargetDialog extends javax.swing.JDialog {

    static Logger logger = Logger.getLogger(InputEphemerisTargetDialog.class);

    private static final String LINE_SEPERATOR = String.valueOf((char)10);
    private static final String CSV = "CSV";
    private static final String SSV = "SSV";

    private static final String COMMA = ",";
    private static final String SPACE = " ";

    private static InputEphemerisTargetDialog instance;
    private XEphemerisTarget target;
    private ButtonGroup valuesInputTypeButtonGroup;

    public static InputEphemerisTargetDialog getInstance() {
        if (instance == null) {
            instance = new InputEphemerisTargetDialog();
        }
        return instance;
    }
    
    /** Creates new form EditTargetDialog */
    private  InputEphemerisTargetDialog() {
        this.setModal(true);
        initComponents();
        initComponents2();
        centerFrame();
        addListeners();
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        jtaEphemerisPasteArea.setText("");
    }

    private void initComponents2() {
        valuesInputTypeButtonGroup = new ButtonGroup();
        valuesInputTypeButtonGroup.add(jrbCSV);
        valuesInputTypeButtonGroup.add(jrbSSV);

        jrbCSV.setActionCommand(CSV); //csv
        jrbSSV.setActionCommand(SSV); //space seperated values
    }

    public ITarget getTarget() {
       return target;
    }

    private XEphemerisTarget buildTarget(String inputLineType) throws Exception, TooSlowEphemerisTargetException {
        
        XEphemerisTarget ephemerisTarget = new XEphemerisTarget();
        
        String jtaEphemerisPasteAreaText = jtaEphemerisPasteArea.getText();
        
        StringTokenizer lineTokenizer = new StringTokenizer(jtaEphemerisPasteAreaText, LINE_SEPERATOR);

        if (inputLineType.equals(CSV)) {
            //comma seperated values;
            while (lineTokenizer.hasMoreElements()) {
                String lineString = (String) lineTokenizer.nextElement();
                // for each lineString convert to a XEphemerisTrackNode
                // e.g. 2009-Jun-29 00:00, , ,03 13 07.21,+17 12 14.8, 104.6093, 30.21466,

                StringTokenizer stringTokenizer = new StringTokenizer(lineString, COMMA);
                int tokenCount = stringTokenizer.countTokens();
                //logger.info("tokenCount=" + tokenCount);
                
                double previousRa = 0, previousDec = 0;
                
                while (stringTokenizer.hasMoreElements()) {
                    try {
                        String dateTimeString = (String) stringTokenizer.nextElement();
                        //logger.info("dateTimeString=" + dateTimeString);
                        if (tokenCount ==7) {
                            //it appears that sometimes the elements are ignored if there is no space between the tokens
                            String blank1 = (String) stringTokenizer.nextElement();
                            //logger.info("blank1=" + blank1);
                            String blank2 = (String) stringTokenizer.nextElement();
                            //logger.info("blank2=" + blank2);
                        }
                        String raString = (String) stringTokenizer.nextElement();
                        //logger.info("raString=" + raString);
                        String decString = (String) stringTokenizer.nextElement();
                        //logger.info("decString=" + decString);
                        String dRAString = (String) stringTokenizer.nextElement();
                        //logger.info("dRAString=" + dRAString);
                        String dDECString = (String) stringTokenizer.nextElement();
                        //logger.info("dDECString=" + dDECString);

                        long time = getTime(dateTimeString);
                        double ra = getRa(raString);
                        double dec = getDec(decString);
                        double raDot = getRaDot(dRAString);
                        double decDot = getDecDot(dDECString);

                        if (ra == previousRa) {
                            throw new TooSlowEphemerisTargetException("Two track nodes in the target have the same RA. The target may be too slow for the system.");
                        }
                        if (dec == previousDec) {
                            throw new TooSlowEphemerisTargetException("Two track nodes in the target have the same RA. The target may be too slow for the system.");
                        }
                        
                        XEphemerisTrackNode ephemerisTrackNode = new XEphemerisTrackNode(time, ra, dec, raDot, decDot);
                        ephemerisTarget.getEphemerisTrack().add(ephemerisTrackNode);
                        previousRa = ra;
                        previousDec = dec;
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error(e);
                        String errString = e.getMessage() + ", line= " + lineString;
                        //logger.error(errString);
                        JOptionPane.showMessageDialog(this, errString);
                        throw e;
                    }
                }
            }
        } else {
            //space seperated values
            while (lineTokenizer.hasMoreElements()) {
                String lineString = (String) lineTokenizer.nextElement();
                // for each lineString convert to a XEphemerisTrackNode
                // e.g. 2009-Jul-30 00:00     08 35 45.57 +01 38 09.9 4.145303  -1.02806
                StringTokenizer stringTokenizer = new StringTokenizer(lineString, SPACE);

                while (stringTokenizer.hasMoreElements()) {
                    try {
                        String dateString = (String) stringTokenizer.nextElement();
                        String timeString = (String) stringTokenizer.nextElement();

                        String raHString = (String) stringTokenizer.nextElement();
                        String raMString = (String) stringTokenizer.nextElement();
                        String raSString = (String) stringTokenizer.nextElement();

                        String decDString = (String) stringTokenizer.nextElement();
                        String decMString = (String) stringTokenizer.nextElement();
                        String decSString = (String) stringTokenizer.nextElement();
                        String dRAString = (String) stringTokenizer.nextElement();
                        String dDECString = (String) stringTokenizer.nextElement();

                        long time = getTime(dateString + SPACE + timeString);
                        double ra = getRa(raHString + SPACE + raMString + SPACE + raSString);
                        double dec = getDec(decDString + SPACE + decMString + SPACE + decSString);
                        double raDot = getRaDot(dRAString);
                        double decDot = getDecDot(dDECString);

                        XEphemerisTrackNode ephemerisTrackNode = new XEphemerisTrackNode(time, ra, dec, raDot, decDot);
                        ephemerisTarget.getEphemerisTrack().add(ephemerisTrackNode);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error(e);
                        String errString = e.getMessage() + ", line= " + lineString;
                        logger.error(errString);
                        JOptionPane.showMessageDialog(this, errString);
                        throw e;
                    }
                }
            }
        }
        
        return ephemerisTarget;
    }
    
    private long getTime(String timeString) throws ParseException {
        try {
            timeString = timeString.trim();

            //time in format     2009-Jun-29 00:00
            SimpleDateFormat horizonsDateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm");
            horizonsDateFormat.setDateFormatSymbols(new DateFormatSymbols(Locale.UK)); //make sure months are spelt in English
            SimpleTimeZone utc = new SimpleTimeZone(0, "UTC");
            horizonsDateFormat.setTimeZone(utc);

            return horizonsDateFormat.parse(timeString).getTime();
        } catch (ParseException e) {
            logger.error(e + " getTime(" + timeString + ") FAILED");
            e.printStackTrace();
            logger.error(e);
            throw e;
        }
    }
    
    private double getRa(String raString) throws ParseException {
        //ra in format    03 13 07.21
        try {
            return Position.parseHMS(raString, " ");
        } catch (ParseException e) {
            logger.error(e + " getRa( " + raString + ") FAILED");
            throw e;
        }
    }
    
    private double getDec(String decString) throws ParseException {
        //dec in format    +17 12 14.8
        try {
            return Position.parseDMS(decString, " ");
        } catch (ParseException e) {
            logger.error(e + " getDec( " + decString + ") FAILED");
            throw e;
        }
    }
    
    private double getRaDot(String raDotString) throws NumberFormatException {
        try {
            return Double.parseDouble(raDotString);
        } catch (NumberFormatException e) {
            logger.error(e + " getRaDot( " + raDotString + ") FAILED");
            throw e;
        }
    }
    
    private double getDecDot(String decDotString) throws NumberFormatException {
        try {
            return Double.parseDouble(decDotString);
        } catch (NumberFormatException e) {
            logger.error(e + " getDecDot( " + decDotString + ") FAILED");
            throw e;
        }
    }
    
    private void centerFrame() {
        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        double hd = screenDimension.getHeight();
        double wd = screenDimension.getWidth();

        double yd = (hd - this.getBounds().getHeight()) / 2;
        double xd = (wd - this.getBounds().getWidth()) / 2;

        final int x = (int) xd;
        final int y = (int) yd;

        EventQueue.invokeLater(
                new Runnable() {

                    public void run() {
                        InputEphemerisTargetDialog.this.setLocation(x, y);
                    }
                });
    }

    private void addListeners() {
        this.addWindowListener(new java.awt.event.WindowAdapter() {

            public void windowClosing(java.awt.event.WindowEvent e) {
                InputEphemerisTargetDialog.this.setVisible(false);
                InputEphemerisTargetDialog.this.dispose();
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jbtnSubmit = new javax.swing.JButton();
        jbtnCancel = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtaEphemerisPasteArea = new javax.swing.JTextArea();
        jrbCSV = new javax.swing.JRadioButton();
        jrbSSV = new javax.swing.JRadioButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtpHelpPane = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Create Ephemeris Target");

        jbtnSubmit.setForeground(new java.awt.Color(255, 0, 0));
        jbtnSubmit.setText("Submit");
        jbtnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSubmitActionPerformed(evt);
            }
        });

        jbtnCancel.setText("Cancel");
        jbtnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCancelActionPerformed(evt);
            }
        });

        jLabel1.setText("Paste the Ephemeris output lines into this text area:");

        jtaEphemerisPasteArea.setColumns(20);
        jtaEphemerisPasteArea.setFont(new java.awt.Font("Arial", 0, 10));
        jtaEphemerisPasteArea.setRows(5);
        jScrollPane1.setViewportView(jtaEphemerisPasteArea);

        jrbCSV.setText("Comma Separated");
        jrbCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbCSVActionPerformed(evt);
            }
        });

        jrbSSV.setSelected(true);
        jrbSSV.setText("Space Separated");

        jtpHelpPane.setEditable(false);
        jtpHelpPane.setText("The format for entering the ephemeris target data is either of the following:\n\n 2009-Jul-30 00:00     08 35 45.57 +01 38 09.9 4.145303  -1.02806\n 2009-Jul-31 00:00     08 35 52.21 +01 37 45.0 4.143384  -1.04952\n 2009-Aug-01 00:00     08 35 58.84 +01 37 19.7 4.140392  -1.07069\n 2009-Aug-02 00:00     08 36 05.46 +01 36 53.8 4.136340  -1.09158\n 2009-Aug-03 00:00     08 36 12.08 +01 36 27.4 4.131236  -1.11218\n 2009-Aug-04 00:00     08 36 18.69 +01 36 00.6 4.125087  -1.13250\n 2009-Aug-05 00:00     08 36 25.29 +01 35 33.2 4.117899  -1.15254\n 2009-Aug-06 00:00     08 36 31.87 +01 35 05.4 4.109676  -1.17229\n 2009-Aug-07 00:00     08 36 38.45 +01 34 37.1 4.100419  -1.19176\n\nOR\n\n 2009-Nov-04 21:00, , ,04 32 01.37,+24 55 28.3, 2077.870, 525.5907,\n 2009-Nov-04 21:15, , ,04 32 39.10,+24 57 39.9, 2028.686, 522.1528,\n 2009-Nov-04 21:30, , ,04 33 15.94,+24 59 50.4, 1979.719, 517.3850,\n 2009-Nov-04 21:45, , ,04 33 51.89,+25 01 59.5, 1931.170, 511.2841,\n 2009-Nov-04 22:00, , ,04 34 26.97,+25 04 06.9, 1883.238, 503.8527,\n 2009-Nov-04 22:15, , ,04 35 01.18,+25 06 12.3, 1836.125, 495.0990,\n 2009-Nov-04 22:30, , ,04 35 34.54,+25 08 15.3, 1790.027, 485.0375,\n 2009-Nov-04 22:45, , ,04 36 07.08,+25 10 15.6, 1745.143, 473.6886,\n 2009-Nov-04 23:00, , ,04 36 38.81,+25 12 13.0, 1701.665, 461.0789,\n 2009-Nov-04 23:15, , ,04 37 09.76,+25 14 07.0, 1659.782, 447.2412,\n 2009-Nov-04 23:30, , ,04 37 39.96,+25 15 57.4, 1619.677, 432.2145,\n");
        jScrollPane2.setViewportView(jtpHelpPane);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 784, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jbtnSubmit)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jbtnCancel))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(3, 3, 3)
                        .add(jLabel1))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jrbCSV)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jrbSSV))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 784, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jrbCSV)
                    .add(jrbSSV))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 342, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jbtnCancel)
                    .add(jbtnSubmit))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jbtnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSubmitActionPerformed
// TODO add your handling code here:
    if (containsValidTarget()) {
        try {
            this.target = buildTarget(valuesInputTypeButtonGroup.getSelection().getActionCommand());
            this.setVisible(false);
            this.dispose();
        } catch (TooSlowEphemerisTargetException tsete) {
            JOptionPane.showMessageDialog(this, tsete.getMessage());
            this.target = null;
            return;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to build target. " + e.getMessage());
            this.target = null;
            return;
        }
    } else {
        JOptionPane.showMessageDialog(this, "Please make sure the target fields are correctly populated");
    }
}//GEN-LAST:event_jbtnSubmitActionPerformed

private boolean containsValidTarget() {
    
    
    return true;
}

private void jbtnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelActionPerformed
    try {
        this.target = null;
    } catch (Exception ex) {
        ex.printStackTrace();
        logger.error(ex);
    }
    this.setVisible(false);
    this.dispose();
}//GEN-LAST:event_jbtnCancelActionPerformed

private void jrbCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbCSVActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_jrbCSVActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jbtnCancel;
    private javax.swing.JButton jbtnSubmit;
    private javax.swing.JRadioButton jrbCSV;
    private javax.swing.JRadioButton jrbSSV;
    private javax.swing.JTextArea jtaEphemerisPasteArea;
    private javax.swing.JTextPane jtpHelpPane;
    // End of variables declaration//GEN-END:variables
}


class TooSlowEphemerisTargetException extends Exception {
    
    public TooSlowEphemerisTargetException(String message) {
        super(message);
    }
    
}