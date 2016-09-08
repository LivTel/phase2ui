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

import java.awt.BorderLayout;
import java.awt.Component;
import ngat.oss.client.gui.panel.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import ngat.oss.client.gui.panel.sequencepanels.sub.IExposurePanel;
import ngat.oss.client.gui.panel.sequencepanels.sub.MovieExposurePanel;
import ngat.oss.client.gui.panel.sequencepanels.sub.MultipleExposurePanel;
import ngat.oss.client.gui.panel.sequencepanels.sub.PeriodExposurePanel;
import ngat.oss.client.gui.panel.sequencepanels.sub.MovieRunAtExposurePanel;
import ngat.phase2.IExecutiveAction;
import ngat.phase2.IExposure;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.XExecutiveComponent;
import ngat.phase2.XMultipleExposure;
import ngat.phase2.XPeriodExposure;
import ngat.phase2.XPeriodRunAtExposure;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class ExposurePanel extends javax.swing.JPanel implements SequenceComponentPanel, ActionListener {

    static Logger logger = Logger.getLogger(ExposurePanel.class);

    private static final String CMD_MULTRUN = "MULTRUN";
    private static final String CMD_MOVIE = "MOVIE";
    private static final String CMD_MOVIE_RUN_AT = "MOVIE_RUN_AT";
    private static final String CMD_DURATION = "DURATION";
    
    /** Creates new form ExposurePanel */
    public ExposurePanel() {
        initComponents();
        initComponents2();
        jrbMultrun.setSelected(true);

        //default exposure
        XMultipleExposure multipleExposure = new XMultipleExposure();
        multipleExposure.setRepeatCount(1);
        populateComponents(multipleExposure);
    }

    public ExposurePanel(IExposure exposure) {
        initComponents();
        initComponents2();
        populateComponents(exposure);
    }

    private void initComponents2() {
        
        ButtonGroup group = new ButtonGroup();
        group.add(jrbMultrun);
        group.add(jrbMovie);
        group.add(jrbMovieRunAt);
        group.add(jrbDuration);
        
        jrbMultrun.setActionCommand(CMD_MULTRUN);
        jrbMovie.setActionCommand(CMD_MOVIE);
        jrbMovieRunAt.setActionCommand(CMD_MOVIE_RUN_AT);
        jrbDuration.setActionCommand(CMD_DURATION);
        
        jrbMultrun.addActionListener(this);
        jrbMovie.addActionListener(this);
        jrbMovieRunAt.addActionListener(this);
        jrbDuration.addActionListener(this);
        
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String actionCommand = actionEvent.getActionCommand();

        if (actionCommand.equals(CMD_MULTRUN)) {
            populateComponents(new XMultipleExposure());
            
        } else if (actionCommand.equals(CMD_MOVIE)) {
            //make the run-at time 0 - this means that a MovieExposurePanel will be rendered, not an run-at panel
            XPeriodRunAtExposure movieExposure = new XPeriodRunAtExposure();
            movieExposure.setRunAtTime(0);
            populateComponents(movieExposure);
            
        } else if (actionCommand.equals(CMD_MOVIE_RUN_AT)) {
            //make the run-at time now
            XPeriodRunAtExposure movieRunAtExposure = new XPeriodRunAtExposure();
            movieRunAtExposure.setRunAtTime(new Date().getTime());
            populateComponents(movieRunAtExposure);
            
        } else if (actionCommand.equals(CMD_DURATION)) {
            populateComponents(new XPeriodExposure());
            
        } else {
            return;
        }

    }

    private void populateComponents(IExposure exposure) {

        if (exposure instanceof XMultipleExposure) {
            XMultipleExposure multipleExposure = (XMultipleExposure) exposure;
            jrbMultrun.setSelected(true);

            MultipleExposurePanel multipleExposurePanel = new MultipleExposurePanel(multipleExposure);
            showExposurePanel(multipleExposurePanel);
            
        } else if (exposure instanceof XPeriodExposure) {
            jrbDuration.setSelected(true);

            XPeriodExposure periodExposure = (XPeriodExposure) exposure;
            PeriodExposurePanel periodExposurePanel = new PeriodExposurePanel(periodExposure);
            showExposurePanel(periodExposurePanel);

        } else if (exposure instanceof XPeriodRunAtExposure) {
            
            XPeriodRunAtExposure periodRunAtExposure = (XPeriodRunAtExposure) exposure;
            
            boolean hasNoRunAt = (periodRunAtExposure.getRunAtTime() == 0); // was set to be 0 on "movie" button selection
            
            if (hasNoRunAt) {
                jrbMovie.setSelected(true);
                showExposurePanel(new MovieExposurePanel(periodRunAtExposure));
            } else {
                //has a run-at component
                jrbMovieRunAt.setSelected(true);
                showExposurePanel(new MovieRunAtExposurePanel(periodRunAtExposure));
            }

        } else {
            try {
                throw new Exception("Unknown exposure type:" + exposure.getClass().getName());
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error(ex);
            }
        }
    }

    public ISequenceComponent getSequenceComponent() {

        IExposurePanel exposurePanel = (IExposurePanel) jplContainerPanel.getComponent(0);

        //Mult-run
        if (exposurePanel instanceof MultipleExposurePanel) {
            MultipleExposurePanel multipleExposurePanel = (MultipleExposurePanel) exposurePanel;
            return new XExecutiveComponent("MULTIPLE_EXPOSURE",(IExecutiveAction) multipleExposurePanel.getExposure());
        
        //Movie
        } else if (exposurePanel instanceof MovieExposurePanel) {
            MovieExposurePanel movieExposurePanel = (MovieExposurePanel) exposurePanel;
            return new XExecutiveComponent("MOVIE_EXPOSURE",(IExecutiveAction) movieExposurePanel.getExposure());
        
        //Movie run-at
        } else if (exposurePanel instanceof MovieRunAtExposurePanel) {
            MovieRunAtExposurePanel movieRunAtExposurePanel = (MovieRunAtExposurePanel) exposurePanel;
            return new XExecutiveComponent("MOVIE_RUN_AT_EXPOSURE",(IExecutiveAction) movieRunAtExposurePanel.getExposure());
        
        //Duration
        } else if (exposurePanel instanceof PeriodExposurePanel) {
            PeriodExposurePanel periodExposurePanel = (PeriodExposurePanel) exposurePanel;
            return new XExecutiveComponent("PERIOD_EXPOSURE",(IExecutiveAction) periodExposurePanel.getExposure());
            
        } else {
            try {
                throw new Exception("Unknown radio-button condition");
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error(ex);
                return null;
            }
        }
        /*
        if (jrbMultrun.isSelected()) {
            MultipleExposurePanel multipleExposurePanel = (MultipleExposurePanel) jplContainerPanel.getComponent(0);
            return new XExecutiveComponent("MULTIPLE_EXPOSURE",(IExecutiveAction) multipleExposurePanel.getExposure());
        } else if (jrbDuration.isSelected()) {
            PeriodExposurePanel periodExposurePanel = (PeriodExposurePanel) jplContainerPanel.getComponent(0);
            return new XExecutiveComponent("PERIOD_EXPOSURE",(IExecutiveAction) periodExposurePanel.getExposure());
        } else if (jrbDurationRunAt.isSelected()) {
            PeriodExposurePanel periodExposurePanel = (PeriodExposurePanel) jplContainerPanel.getComponent(0);
            return new XExecutiveComponent("PERIOD_EXPOSURE",(IExecutiveAction) periodExposurePanel.getExposure());
        } else {
            try {
                throw new Exception("Unknown radio-button condition");
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error(ex);
                return null;
            }
        }
         */
    }
    
    public boolean isValidData() {
        IExposurePanel exposurePanel = (IExposurePanel) jplContainerPanel.getComponent(0);
        return exposurePanel.containsValidData();
    }

    private void showExposurePanel(final IExposurePanel displayPanel) {

        final JPanel containerPanel = jplContainerPanel;
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                containerPanel.removeAll();
                if (displayPanel != null) {
                    containerPanel.add((Component) displayPanel, BorderLayout.CENTER);
                }
                containerPanel.validate();
                containerPanel.repaint();
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

        jLabel13 = new javax.swing.JLabel();
        jrbMultrun = new javax.swing.JRadioButton();
        jrbDuration = new javax.swing.JRadioButton();
        jplContainerPanel = new javax.swing.JPanel();
        jrbMovieRunAt = new javax.swing.JRadioButton();
        jrbMovie = new javax.swing.JRadioButton();

        jLabel13.setText("Exposure");
        jLabel13.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jrbMultrun.setText("Mult-run");

        jrbDuration.setText("Duration (usually only used for RINGO exposures)");

        jplContainerPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jplContainerPanel.setLayout(new java.awt.CardLayout());

        jrbMovieRunAt.setText("Movie Run-at");

        jrbMovie.setText("Movie");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jplContainerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jLabel13)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jrbMultrun)
                            .add(jrbDuration)
                            .add(layout.createSequentialGroup()
                                .add(jrbMovie)
                                .add(86, 86, 86)
                                .add(jrbMovieRunAt)))
                        .addContainerGap(68, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel13)
                    .add(jrbMultrun))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jrbMovie)
                    .add(jrbMovieRunAt))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jrbDuration)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jplContainerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 181, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel13;
    private javax.swing.JPanel jplContainerPanel;
    private javax.swing.JRadioButton jrbDuration;
    private javax.swing.JRadioButton jrbMovie;
    private javax.swing.JRadioButton jrbMovieRunAt;
    private javax.swing.JRadioButton jrbMultrun;
    // End of variables declaration//GEN-END:variables

}
