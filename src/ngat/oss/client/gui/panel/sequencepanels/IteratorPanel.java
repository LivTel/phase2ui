/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ExposurePanel.java
 *
 * Created on Sep 9, 2009, 12:08:25 PM
 */

package ngat.oss.client.gui.panel.sequencepanels;

import ngat.oss.client.gui.panel.*;
import javax.swing.JOptionPane;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.XIteratorComponent;
import ngat.phase2.XIteratorRepeatCountCondition;

/**
 *
 * @author nrc
 */
public class IteratorPanel extends javax.swing.JPanel implements SequenceComponentPanel {

    /** Creates new form ExposurePanel */
    public IteratorPanel() {
        initComponents();
    }

    public IteratorPanel(XIteratorComponent iteratorComponent) {
        initComponents();
        populateComponents(iteratorComponent);
    }

    private void populateComponents(XIteratorComponent iteratorComponent) {
        String name = iteratorComponent.getComponentName();
        XIteratorRepeatCountCondition iteratorRepeatCountCondition =  (XIteratorRepeatCountCondition)iteratorComponent.getCondition();
        int repeatCount = iteratorRepeatCountCondition.getCount();

        jtfName.setText(name);
        jtfRepeatCount.setText(String.valueOf(repeatCount));
    }

    public ISequenceComponent getSequenceComponent() {

        String name = jtfName.getText();
        int repeatCount = Integer.parseInt(jtfRepeatCount.getText());

        XIteratorRepeatCountCondition iteratorRepeatCountCondition = new XIteratorRepeatCountCondition(repeatCount);
        XIteratorComponent iteratorComponent = new XIteratorComponent(name, iteratorRepeatCountCondition);
       
        return iteratorComponent;
    }
    
    public boolean isValidData() {
        try {
            int repeatCount = Integer.parseInt(jtfRepeatCount.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a numeric value for repeat count");
            return false;
        }
        return true;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel13 = new javax.swing.JLabel();
        jtfRepeatCount = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jtfName = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();

        jLabel13.setText("Repeat count:");

        jtfRepeatCount.setText("1");

        jLabel14.setText("Description:");

        jLabel15.setText("Iterator");
        jLabel15.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel15)
                .add(23, 23, 23)
                .add(jLabel13)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jtfRepeatCount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 53, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(36, 36, 36)
                .add(jLabel14)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jtfName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jtfRepeatCount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel13)
                    .add(jLabel14)
                    .add(jtfName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel15))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JTextField jtfName;
    private javax.swing.JTextField jtfRepeatCount;
    // End of variables declaration//GEN-END:variables

}
