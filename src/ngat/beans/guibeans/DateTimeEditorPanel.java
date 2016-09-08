package ngat.beans.guibeans;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;
import javax.swing.border.TitledBorder;

/**
 *
 * @author nrc
 */
public class DateTimeEditorPanel extends javax.swing.JPanel {

    /**
     * Default date format.
     */
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * Parsing date format.
     */
    public static SimpleDateFormat PARSE_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");
    public static final SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");
    /**
     * Format to set/get content.
     */
    protected SimpleDateFormat sdf;
    /**
     * Currently set time.
     */
    //protected long time;
    private boolean enabled;

    /**
     * Construct a DateField .
     */
    public DateTimeEditorPanel() {
        this(DEFAULT_DATE_FORMAT);
    }

    /**
     * Construct a DateField .
     */
    public DateTimeEditorPanel(boolean newDate) {
        this(DEFAULT_DATE_FORMAT, newDate);
    }

    /**
     * Construct a DateField .
     */
    public DateTimeEditorPanel(long time) {
        this(DEFAULT_DATE_FORMAT, time, false);
    }

    /**
     * Construct a DateField .
     */
    public DateTimeEditorPanel(SimpleDateFormat sdf) {
        this(sdf, System.currentTimeMillis(), false);
    }

    /**
     * Construct a DateField .
     */
    public DateTimeEditorPanel(SimpleDateFormat sdf, boolean newDate) {
        this(sdf, System.currentTimeMillis(), newDate);
    }

    /**
     * Construct a DateField .
     */
    public DateTimeEditorPanel(SimpleDateFormat sdf, long time, boolean newDate) {

        this.sdf = sdf;
        sdf.setLenient(false);
        sdf.setTimeZone(UTC);
        sdf.setDateFormatSymbols(new DateFormatSymbols(Locale.UK));
        PARSE_DATE_FORMAT.setTimeZone(UTC);
        PARSE_DATE_FORMAT.setDateFormatSymbols(new DateFormatSymbols(Locale.UK));
        initComponents();
        setTime(time, newDate);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        //super.setEnabled(enabled);
        this.enabled = enabled;

        jcbHour.setEnabled(enabled);
        jcbMins.setEnabled(enabled);
        jcbSecs.setEnabled(enabled);
        yearField.setEnabled(enabled);
        monthField.setEnabled(enabled);
        dayField.setEnabled(enabled);

        this.validate();
        this.repaint();
    }

    /**
     * Clear the fields.
     */
    public void clear() {
        yearField.setText("");
        monthField.setText("");
        dayField.setText("");
        jcbHour.setSelectedItem("00");
        jcbMins.setSelectedItem("00");
        jcbSecs.setSelectedItem("00");
    }

    public void setTitle(String title) {
        TitledBorder titledBorder = (TitledBorder) this.getBorder();
        titledBorder.setTitle(title + " (YYYY/MM/DD HH:MM:SS)");
    }

    /**
     * Returns the formatted time.
     */
    public String getFormattedTime() {
        return yearField.getText().trim() + "-"
                + monthField.getText().trim() + "-"
                + dayField.getText().trim() + " "
                + ((String) jcbHour.getSelectedItem()).trim() + ":"
                + ((String) jcbMins.getSelectedItem()).trim() + ":"
                + ((String) jcbSecs.getSelectedItem()).trim();
    }

    /**
     * Returns the time in the fields.
     */
    public long getTime() throws ParseException {
        Date date = sdf.parse(getFormattedTime());
        if (date != null) {
            return date.getTime();
        }
        throw new ParseException("No valid date could be parsed", 0);
    }

    /**
     * Causes the speciifed date to be displayed.
     *
     * @param atime The date/time to display.
     */
    public void setTime(long atime, boolean isNewDate) {

        // Format the date using curent formatter.

        String fmt = PARSE_DATE_FORMAT.format(new Date(atime));

        // Now extract the pieces. yyyy/mm/dd/hh/mm

        StringTokenizer st = new StringTokenizer(fmt, "/");

        if (st.countTokens() == 6) {
            String year = st.nextToken();
            String month = st.nextToken();
            String day = st.nextToken();
            yearField.setText(year);
            monthField.setText(month);
            dayField.setText(day);

            if (!isNewDate) {
                String hour = st.nextToken();
                String min = st.nextToken();
                String sec = st.nextToken();
                jcbHour.setSelectedItem(hour);
                jcbMins.setSelectedItem(min);
                jcbSecs.setSelectedItem(sec);
            } else {
                jcbHour.setSelectedItem("00");
                jcbMins.setSelectedItem("00");
                jcbSecs.setSelectedItem("00");
            }

        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel5 = new javax.swing.JLabel();
        jcbHour = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jcbMins = new javax.swing.JComboBox();
        jcbSecs = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        yearField = new javax.swing.JTextField(4);
        monthField = new javax.swing.JTextField(2);
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        dayField = new javax.swing.JTextField(2);

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Title", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 10))); // NOI18N

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 12));

        jcbHour.setFont(new java.awt.Font("Lucida Grande", 0, 10));
        jcbHour.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));

        jLabel1.setText(":"); // NOI18N

        jLabel3.setText(":"); // NOI18N

        jcbMins.setFont(new java.awt.Font("Lucida Grande", 0, 10));
        jcbMins.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));

        jcbSecs.setFont(new java.awt.Font("Lucida Grande", 0, 10));
        jcbSecs.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));

        jLabel2.setText("(UTC)"); // NOI18N

        jLabel4.setText("/"); // NOI18N

        jLabel6.setText("/"); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(480, Short.MAX_VALUE)
                .add(jLabel5))
            .add(layout.createSequentialGroup()
                .add(yearField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(1, 1, 1)
                .add(jLabel4)
                .add(5, 5, 5)
                .add(monthField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel6)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dayField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jcbHour, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jcbMins, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jcbSecs, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addContainerGap(52, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(yearField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jcbHour, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(monthField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1)
                    .add(dayField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3)
                    .add(jLabel6)
                    .add(jLabel2)
                    .add(jcbMins, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jcbSecs, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .add(99, 99, 99)
                .add(jLabel5))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField dayField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JComboBox jcbHour;
    private javax.swing.JComboBox jcbMins;
    private javax.swing.JComboBox jcbSecs;
    private javax.swing.JTextField monthField;
    private javax.swing.JTextField yearField;
    // End of variables declaration//GEN-END:variables
}
