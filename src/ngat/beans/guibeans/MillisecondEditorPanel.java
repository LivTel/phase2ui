/*
 * MillisecondEditorPanel.java
 *
 * Created on 20 December 2007, 14:56
 */
package ngat.beans.guibeans;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import ngat.phase2.util.TimeWrapper;
import org.apache.log4j.Logger;


/**
 *
 * @author  nrc
 */
public class MillisecondEditorPanel extends JPanel {

    static Logger logger = Logger.getLogger(MillisecondEditorPanel.class);

    private static final int RECALC_MS = 0;
    private static final int RECALC_DHMS = 1;
    
    private boolean enabled;
    private boolean isInErrorState = true;

    private KeyListener msKeyListener;
    private KeyListener dhmsKeyListener;

    /** Creates new form MillisecondEditorPanel */
    public MillisecondEditorPanel() {
        initComponents();
        setUpListeners();
        setUpRestrictionDocuments();
    }

    /** Creates new form MillisecondEditorPanel */
    public MillisecondEditorPanel(String title) {
        initComponents();
        this.setTitle(title);
        setUpListeners();
        setUpRestrictionDocuments();
    }

    /** Creates new form MillisecondEditorPanel */
    public MillisecondEditorPanel(long mS) {
        initComponents();
        this.setTime(mS);
        setUpListeners();
        setUpRestrictionDocuments();
    }

    /** Creates new form MillisecondEditorPanel */
    public MillisecondEditorPanel(String title, long mS) {
        initComponents();
        this.setTitle(title);
        this.setTime(mS);
        recalcAndDisplay(RECALC_DHMS);
        setUpListeners();
        setUpRestrictionDocuments();
    }

    private void setUpRestrictionDocuments() {
       
    }
    
    private void setUpListeners() {
        msKeyListener = new KeyAdapter() {
            public void keyReleased(KeyEvent keyEvent) {
                recalcAndDisplay(RECALC_DHMS);
              }
        };
        dhmsKeyListener = new KeyAdapter() {
            public void keyReleased(KeyEvent keyEvent) {
                recalcAndDisplay(RECALC_MS);
              }
        };

        jtfDDD.addKeyListener(dhmsKeyListener);
        jtfHH.addKeyListener(dhmsKeyListener);
        jtfMM.addKeyListener(dhmsKeyListener);
        jtfSS.addKeyListener(dhmsKeyListener);
        jtfmS.addKeyListener(dhmsKeyListener);
        
        jtfMsValue.addKeyListener(msKeyListener);
    }

    //public getters and setters ****
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        
        jtfDDD.setEnabled(enabled);
        jtfHH.setEnabled(enabled);
        jtfMM.setEnabled(enabled);
        jtfMsValue.setEnabled(enabled);
        jtfSS.setEnabled(enabled);
        jtfmS.setEnabled(enabled);
    }
    
    public String getTitle() {
        TitledBorder border = (TitledBorder) this.getBorder();
        return border.getTitle();
    }

    public void setTitle(String title) {
        TitledBorder border = (TitledBorder) this.getBorder();
        border.setTitle(title);
    }

    public void setTime(long mS) {
        jtfMsValue.setText("" + mS);
        recalcAndDisplay(RECALC_DHMS);
    }

    public long getTime() {
        try {
            return new Long(jtfMsValue.getText()).longValue();
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            logger.error(ex);
            return -1;
        }
    }

    //required abstract methods **
    //returns whether the component is in an error state
    public boolean isIsInErrorState() {
        return isInErrorState;
    }

    //******************************
    private void recalcAndDisplay(int field) {

        //populateEmptyFields();
        clearFields(field);
        if (field == RECALC_DHMS) {
            try {
                String msValueString = jtfMsValue.getText();

                if (msValueString.length() == 0) {
                    msValueString = "0";
                }

                long ms = Long.parseLong(msValueString);
                TimeWrapper timeWrapper = new TimeWrapper(ms);

                jtfDDD.setText("" + timeWrapper.getD());
                jtfSS.setText("" + timeWrapper.getS());
                jtfMM.setText("" + timeWrapper.getM());
                jtfHH.setText("" + timeWrapper.getH());
                jtfmS.setText("" + timeWrapper.getMs());
            } catch (NumberFormatException e) {
                setErrorState(true);
                return;
            }
        } else if (field == RECALC_MS) {
            try {
                String daysStr = jtfDDD.getText();
                if (daysStr.length() == 0) {
                    daysStr = "0";
                }
                String hoursStr = jtfHH.getText();
                if (hoursStr.length() == 0) {
                    hoursStr = "0";
                }
                String minsStr = jtfMM.getText();
                if (minsStr.length() == 0) {
                    minsStr = "0";
                }
                String secsStr = jtfSS.getText();
                if (secsStr.length() == 0) {
                    secsStr = "0";
                }
                String msStr = jtfmS.getText();
                if (msStr.length() == 0) {
                    msStr = "0";
                }
                int days = Integer.parseInt(daysStr);
                int hours = Integer.parseInt(hoursStr);
                int minutes = Integer.parseInt(minsStr);
                int seconds = Integer.parseInt(secsStr);
                int milliseconds = Integer.parseInt(msStr);

                if ((hours > 23) || (minutes > 59) || (seconds > 59)) {
                    throw new Exception("silly numeric value");
                }
                
                TimeWrapper timeWrapper = new TimeWrapper(days, hours, minutes, seconds, milliseconds);

                jtfMsValue.setText(String.valueOf(timeWrapper.getTimeInMillis()));
            } catch (Exception e) {
                setErrorState(true);
                return;
            }
        }
        setErrorState(false);
    }

    private void clearFields(int field) {
        switch (field) {
            case RECALC_DHMS:
                jtfDDD.setText("");
                jtfSS.setText("");
                jtfMM.setText("");
                jtfHH.setText("");
                jtfmS.setText("");
                break;
            case RECALC_MS:
                jtfMsValue.setText("");
                break;
        }
    }

    

    private void setErrorState(boolean isInError) {
        this.isInErrorState = isInError;
        jlError.setVisible(isInError);
    }

    /*
     *put a '0' in any textboxes left empty
     */
    private void populateEmptyFields() {
        populateIfEmpty(jtfDDD);
        populateIfEmpty(jtfHH);
        populateIfEmpty(jtfMM);
        populateIfEmpty(jtfmS);
        populateIfEmpty(jtfMsValue);
        populateIfEmpty(jtfSS);
    }

    private void populateIfEmpty(JTextField textField) {
        if (textField.getText().equals("")) {
            textField.setText("0");
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jtfMsValue = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jtfDDD = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtfHH = new javax.swing.JTextField();
        jtfMM = new javax.swing.JTextField();
        jtfSS = new javax.swing.JTextField();
        jtfmS = new javax.swing.JTextField();
        jlError = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "title", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 12))); // NOI18N
        setPreferredSize(new java.awt.Dimension(357, 103));

        jtfMsValue.setFont(new java.awt.Font("Dialog", 1, 10));
        jtfMsValue.setText("0");

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel1.setText("mS");

        jtfDDD.setColumns(3);
        jtfDDD.setFont(new java.awt.Font("Dialog", 1, 10));
        jtfDDD.setText("000");

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel2.setText("mS");

        jtfHH.setColumns(2);
        jtfHH.setFont(new java.awt.Font("Dialog", 1, 10));
        jtfHH.setText("00");

        jtfMM.setColumns(2);
        jtfMM.setFont(new java.awt.Font("Dialog", 1, 10));
        jtfMM.setText("00");

        jtfSS.setColumns(2);
        jtfSS.setFont(new java.awt.Font("Dialog", 1, 10));
        jtfSS.setText("00");

        jtfmS.setColumns(3);
        jtfmS.setFont(new java.awt.Font("Dialog", 1, 10));
        jtfmS.setText("000");

        jlError.setFont(new java.awt.Font("Dialog", 1, 10));
        jlError.setForeground(new java.awt.Color(255, 51, 51));
        jlError.setText("ERROR");
        jlError.setVisible(false);

        jLabel3.setText("<---->");

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jLabel4.setText("DDD");

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel5.setText("HH");

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel6.setText("MM");

        jLabel7.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel7.setText("SS");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jtfMsValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 122, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(2, 2, 2)
                .add(jLabel1)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel3)
                    .add(jlError))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jtfDDD, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jtfHH, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .add(1, 1, 1)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jtfMM, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6))
                .add(3, 3, 3)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jtfSS, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel2)
                    .add(jtfmS, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(1, 1, 1)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jtfMsValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel1)
                            .add(jLabel3))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jlError)
                            .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jtfDDD, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jtfHH, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jtfMM, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jtfSS, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(layout.createSequentialGroup()
                        .add(jtfmS, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(31, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String[] a) {
        final MillisecondEditorPanel periodBean = new MillisecondEditorPanel("some period", 125363);
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.getContentPane().add(periodBean, BorderLayout.CENTER);
        frame.show();
        frame.setSize(new Dimension(200, 200));
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jlError;
    private javax.swing.JTextField jtfDDD;
    private javax.swing.JTextField jtfHH;
    private javax.swing.JTextField jtfMM;
    private javax.swing.JTextField jtfMsValue;
    private javax.swing.JTextField jtfSS;
    private javax.swing.JTextField jtfmS;
    // End of variables declaration//GEN-END:variables
}
