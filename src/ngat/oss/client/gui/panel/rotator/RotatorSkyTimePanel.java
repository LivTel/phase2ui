package ngat.oss.client.gui.panel.rotator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ngat.astrometry.AstroFormatter;
import ngat.astrometry.AstroLib;
import ngat.astrometry.AstrometryCalculator;
import ngat.astrometry.AstrometrySiteCalculator;
import ngat.astrometry.BasicAstrometryCalculator;
import ngat.astrometry.BasicAstrometrySiteCalculator;
import ngat.astrometry.BasicCardinalPointingCalculator;
import ngat.astrometry.BasicSite;
import ngat.astrometry.CardinalPointingCalculator;
import ngat.astrometry.Coordinates;
import ngat.astrometry.ISite;
import ngat.astrometry.SolarCalculator;
import ngat.astrometry.TargetTrackCalculator;
import ngat.astrometry.TargetTrackCalculatorFactory;
import ngat.astrometry.approximate.AlternativeTargetTrackCalculatorFactory;
import ngat.astrometry.approximate.BasicAstroLibImpl;
import ngat.oss.client.gui.reference.CONST;
import ngat.phase2.XExtraSolarTarget;
import ngat.util.CommandTokenizer;
import ngat.util.ConfigurationProperties;
import org.apache.log4j.Logger;

/**
 * @author eng
 *
 */
public class RotatorSkyTimePanel extends JPanel {

    static Logger logger = Logger.getLogger(RotatorSkyTimePanel.class);

    public static SimpleDateFormat ddf = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat fdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
    public static final SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");
    // public static final Color TARGET_SET = new Color(228,230,133);
    public static final Color TARGET_SET = Color.yellow.darker();
    // public static final Color SUNRISE = new Color(255,203,101);
    public static final Color SUNRISE = Color.yellow;
    // public static final Color INFEASIBLE = new Color(232,162,138);
    public static final Color INFEASIBLE = Color.red;
    // public static final Color FEASIBLE = new Color(160,217,119);
    public static final Color FEASIBLE = Color.green;
    /** A list of RotatorPositionSelectionListeners to update when user moves mouse over panel.*/
    private List listeners;
    private AstroLib astroLib;
    private CardinalPointingCalculator cpc;
    private AstrometrySiteCalculator astro;
    private SolarCalculator sunTrack;
    private ISite site;
    private double instrumentOffsetAngle;
    private TargetTrackCalculator track;
    private long start, end, duration;
    private boolean isLocked = false;

    public RotatorSkyTimePanel(ISite site, AstroLib astroLib, TargetTrackCalculatorFactory tcf, double instrumentOffsetAngle) {
        super(true);
        this.site = site;
        this.astroLib = astroLib;
        this.instrumentOffsetAngle = instrumentOffsetAngle;
        
        AstrometryCalculator ast = new BasicAstrometryCalculator(astroLib);
        astro = new BasicAstrometrySiteCalculator(site, ast);
        cpc = new BasicCardinalPointingCalculator(site, astro, tcf);
        sunTrack = new SolarCalculator(astroLib);

        sdf.setTimeZone(UTC);
        fdf.setTimeZone(UTC);
        ddf.setTimeZone(UTC);

        listeners = new Vector<RotatorPositionSelectionListener>();

        addMouseMotionListener(new MouseMotionAdapter() {

            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                // System.err.println("Mouse at: ["+e.getX()+","+e.getY());
                //don't update listeners if the panel is locked
                if (!isLocked) {
                    int h = getSize().height - 80;
                    int w = getSize().width - 80;
                    long t = start + (long) ((double) (e.getX() - 40) * (double) (end - start) / (double) w);
                    double skyAngle = (double) (e.getY() - 40) * 360.0 / (double) h;
                    if ((skyAngle < 0) | (skyAngle > 360)) {
                        return;
                    }

                    // notify any lsiteners
                    Iterator<RotatorPositionSelectionListener> il = listeners.iterator();
                    while (il.hasNext()) {
                        try {
                            RotatorPositionSelectionListener l = il.next();
                            skyAngle = ngat.phase2.util.Rounder.round(skyAngle, 0);
                            l.rotatorSelection(t, skyAngle);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            logger.error(ex);
                        }
                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (isLocked) {
                    isLocked = false;
                    //repaint();
                } else {
                    isLocked = true;
                    //draw cross at mouse - not done yet
                }

                Iterator<RotatorPositionSelectionListener> il = listeners.iterator();
                while (il.hasNext()) {
                    try {
                        RotatorPositionSelectionListener l = il.next();
                        l.receiveLockingEvent(isLocked);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        logger.error(ex);
                    }
                }
            }
            //public void mousePressed(MouseEvent arg0) {
            //public void mouseReleased(MouseEvent arg0) {
            //public void mouseEntered(MouseEvent arg0) {
            //public void mouseExited(MouseEvent arg0) {
        });

    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.JComponent#paint(java.awt.Graphics)
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // draw a box inset by 25,25
        int h = getSize().height - 80;
        int w = getSize().width - 80;
        g.setColor(Color.black);
        g.drawRect(40, 40, w, h);

        g.setFont(new Font("serif", Font.PLAIN, 10));
        // draw time axis
        for (int i = 0; i < w; i += w / 10) {
            long t = start + (long) ((double) i * (double) (end - start) / (double) w);
            g.drawString(ddf.format(new Date(t)), i + 40, h + 55);
            g.drawString(sdf.format(new Date(t)), i + 40, h + 65);
        }

        // draw sky angle axis
        double isky = 0.0;
        while (isky <= 360.0) {
            // y = sky/360*h
            g.drawString(String.format("%4.1f", isky), 0, 40 + (int) (isky * (double) h / 360.0));
            isky += 45.0;
        }

        Color color = null;
        int dw = (int) (w / 100);
        int dh = (int) (h / 100);
        // loop along x axis (time)
        for (int i = 0; i < w; i += dw) {

            long t = start + (long) ((double) i * (double) (end - start) / (double) w);

            try {
                Coordinates c = track.getCoordinates(t);
                double minalt = astro.getMinimumAltitude(track, t, t + duration);
                boolean visible = minalt > Math.toRadians(CONST.DOME_LIMIT);

                Coordinates sun = sunTrack.getCoordinates(t);
                double sunlev = astro.getAltitude(sun, t);

                Coordinates targetTrack = track.getCoordinates(t);
                XExtraSolarTarget target = new XExtraSolarTarget();
                target.setRa(targetTrack.getRa());
                target.setDec(targetTrack.getDec());

                // loop along y axis (rot pos)
                for (int j = 0; j < h; j += dh) {

                    // double rot = Math.toRadians(-90.0 + (double) j * 180.0 /
                    // (double) h);

                    double sky = Math.toRadians((double) j * 360.0 / (double) h);

                    // check if this is a feasible mount angle
                    // double sky = cpc.getSkyAngle(rot, target, IOFF, t);
                    boolean feasible = cpc.isFeasibleSkyAngle(sky, target, CONST.INSTRUMENT_OFFSET - instrumentOffsetAngle, t, t + duration);

                    // System.err.printf("Check [%4d , %4d] : at %tT - %4.2f %s %s \n",
                    // i, j, t, Math.toDegrees(sky),
                    // (visible? "V/":"NV/"), (feasible?"F":"NF"));

                    // color selector
/*
                    if (sunlev > 0.0) {
                    color = SUNRISE;
                    } else {
                    if (!visible) {
                    color = TARGET_SET;
                    } else {
                    if (feasible)
                    color = FEASIBLE;
                    else
                    color = INFEASIBLE;
                    }
                    }*/

                    if (feasible) {
                        if (sunlev > 0.0) {
                            color = FEASIBLE.darker();
                        } else if (!visible) {
                            color = FEASIBLE.darker().darker();
                        } else {
                            color = FEASIBLE;
                        }

                    } else {
                        if (sunlev > 0.0) {
                            color = INFEASIBLE.darker();
                        } else if (!visible) {
                            color = INFEASIBLE.darker().darker();
                        } else {
                            color = INFEASIBLE;
                        }

                    }

                    g.setColor(color);
                    // plot a square at (i,j)
                    g.fillRect(40 + i, 40 + j, dw, dh);

                }
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error(ex);
            }
        }

        // draw cardinal points
        g.setColor(Color.black);
        g.drawLine(40, 40 + h / 4, 40 + w, 40 + h / 4);
        g.drawLine(40, 40 + h / 2, 40 + w, 40 + h / 2);
        g.drawLine(40, 40 + 3 * h / 4, 40 + w, 40 + 3 * h / 4);
    }

    public void addRotatorPositionSelectionListener(RotatorPositionSelectionListener rpl) {
        if (listeners.contains(rpl)) {
            return;
        }
        listeners.add(rpl);
    }

    public void update(TargetTrackCalculator track, long start, long end, long duration) {
        this.track = track;
        this.start = start;
        this.end = end;
        this.duration = duration;

        repaint();

    }

    public static void main(String args[]) {

        BasicSite site = new BasicSite("obs", Math.toRadians(28.7624), Math.toRadians(-17.8792));

        TargetTrackCalculatorFactory tcf = new AlternativeTargetTrackCalculatorFactory();
        RotatorSkyTimePanel rtp = new RotatorSkyTimePanel(site, new BasicAstroLibImpl(), tcf, 0);

        //TargetTrackCalculatorFactory tcf = new BasicTargetTrackCalculatorFactory();
        //RotatorSkyTimePanel rtp = new RotatorSkyTimePanel(site, new JAstroSlalib(),
        //	new BasicTargetTrackCalculatorFactory());

        try {

            ConfigurationProperties cfg = CommandTokenizer.use("--").parse(args);
            long t1 = (fdf.parse(cfg.getProperty("start"))).getTime();
            long t2 = (fdf.parse(cfg.getProperty("end"))).getTime();
            double ra = AstroFormatter.parseHMS(cfg.getProperty("ra"), ":");
            double dec = AstroFormatter.parseDMS(cfg.getProperty("dec"), ":");
            XExtraSolarTarget target = new XExtraSolarTarget();
            target.setRa(ra);
            target.setDec(dec);
            double duration = 3600 * 1000 * cfg.getDoubleValue("duration");

            JFrame f = new JFrame("Sky angle display");
            JComponent comp = (JComponent) f.getContentPane();
            comp.setLayout(new BorderLayout());

            f.getContentPane().add(rtp);
            f.pack();
            f.setBounds(200, 200, 880, 440);
            f.setVisible(true);

            TargetTrackCalculator track = tcf.getTrackCalculator(target, site);
            rtp.update(track, t1, t2, (long) duration);

            //System.err.printf("Target at: [%4.2f h, %4.2f ]", Math.toDegrees(ra) / 15, Math.toDegrees(dec));
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
        }
    }
}
