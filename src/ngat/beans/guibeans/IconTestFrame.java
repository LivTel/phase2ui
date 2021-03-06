/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * IconTestFrame.java
 *
 * Created on May 7, 2010, 2:00:52 PM
 */

package ngat.beans.guibeans;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author nrc
 */
public class IconTestFrame extends javax.swing.JFrame {

    /** Creates new form IconTestFrame */
    public IconTestFrame() {
        initComponents();
        populateComponents();
    }

    private void populateComponents() {
        /*
        File file = new File("'images/Group_Big.jpg");
        if (file.exists()) {
            System.err.println("exists");
        } else {
            System.err.println("doesn't exist");
        }
         * */
        URL imageURL = getClass().getClassLoader().getResource("images/Group_Big.jpg");  //getClass().getResource("images/Group_Big.jpg");   - doesn't work, even tho Sun docs say it should.
        //URL imageURL = getClass().getResource(".");  //images/Group_Big.jpg
        //System.err.println("imageURL = " + imageURL);

        JLabel imageLabel = new JLabel(new ImageIcon(imageURL));
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(imageLabel, BorderLayout.CENTER);
        this.setSize(new Dimension(100, 100));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new IconTestFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
