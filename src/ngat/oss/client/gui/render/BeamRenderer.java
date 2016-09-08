/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.render;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.Iterator;
import java.util.List;
import ngat.phase2.IBeamSteeringConfig;
import ngat.phase2.IOpticalSlideConfig;

/**
 *
 * @author nrc
 */
public class BeamRenderer {

    IBeamSteeringConfig beamSteeringConfig;

    public BeamRenderer(IBeamSteeringConfig beamSteeringConfig) {
        this.beamSteeringConfig = beamSteeringConfig;
    }

    public void drawComponents(Graphics g) {
        drawAG(g);
        drawUpperOpticalSlide(g);
        drawLowerOpticalSlide(g);
        drawBeams(g);
    }

    private void drawAG(Graphics g) {
        //draw AG
        Polygon ag = BeamStearingElementsFactory.getInstance().getAG();
        g.setColor(Color.black);
        g.fillPolygon(ag);

        //AG incoming Beam
        Polygon agBeam = BeamStearingElementsFactory.getInstance().getBeam_AG();
        g.setColor(Color.lightGray);
        g.fillPolygon(agBeam);

        //draw AG incoming beam arrow
        Polygon beamArrowAG = BeamStearingElementsFactory.getInstance().getBeamArrow_AG();
        g.setColor(Color.lightGray);
        g.fillPolygon(beamArrowAG);
    }


    private void drawUpperOpticalSlide(Graphics g) {

        IOpticalSlideConfig topOpticalSlideConfig = beamSteeringConfig.getUpperSlideConfig();
        /*
        if (topOpticalSlideConfig.getPosition() == IOpticalSlideConfig.POSITION_CLEAR) {
            drawDichroic(g, Color.blue, true, BeamStearingElementsFactory.TOP_SLIDE, true, BeamStearingElementsFactory.DONT_CARE, 0, 0); //space
            drawDichroic(g, Color.black, true, BeamStearingElementsFactory.TOP_SLIDE, false, BeamStearingElementsFactory.DONT_CARE, -1, 1); //mirror
            drawDichroic(g, Color.black, false, BeamStearingElementsFactory.TOP_SLIDE, false, BeamStearingElementsFactory.DONT_CARE, -2, 2); //dichroic
        } else if (topOpticalSlideConfig.getPosition() == IOpticalSlideConfig.POSITION_AL_MIRROR) {
            drawDichroic(g, Color.blue, true, BeamStearingElementsFactory.TOP_SLIDE, true, BeamStearingElementsFactory.DONT_CARE, +1, -1); //space
            drawDichroic(g, Color.black, true, BeamStearingElementsFactory.TOP_SLIDE, false, BeamStearingElementsFactory.DONT_CARE, 0, 0); //mirror
            drawDichroic(g, Color.black, false, BeamStearingElementsFactory.TOP_SLIDE, false, BeamStearingElementsFactory.DONT_CARE, -1, 1); //dichroic
        } else if (topOpticalSlideConfig.getPosition() == IOpticalSlideConfig.POSITION_DI_RB) {
            drawDichroic(g, Color.blue, true, BeamStearingElementsFactory.TOP_SLIDE, true, BeamStearingElementsFactory.DONT_CARE, +2, -2); //space
            drawDichroic(g, Color.black, true, BeamStearingElementsFactory.TOP_SLIDE, false, BeamStearingElementsFactory.DONT_CARE, +1, -1); //mirror
            drawDichroic(g, Color.black, false, BeamStearingElementsFactory.TOP_SLIDE, false, BeamStearingElementsFactory.DONT_CARE, 0, 0); //dichroic
        }
         * */
    }

    private void drawLowerOpticalSlide(Graphics g) {

        IOpticalSlideConfig bottomOpticalSlideConfig = beamSteeringConfig.getLowerSlideConfig();
        /*
        if (bottomOpticalSlideConfig.getPosition() == IOpticalSlideConfig.POSITION_DI_RB) {
            //red blue
            drawDichroic(g, Color.blue, true, BeamStearingElementsFactory.BOTTOM_SLIDE, false, BeamStearingElementsFactory.TOP, 0, 0);
            drawDichroic(g, Color.red, true, BeamStearingElementsFactory.BOTTOM_SLIDE, false, BeamStearingElementsFactory.BOTTOM, 0, 0);
            //mirror
            drawDichroic(g, Color.DARK_GRAY, true, BeamStearingElementsFactory.BOTTOM_SLIDE, false, BeamStearingElementsFactory.BOTTOM, 1, -1);
            //blue red
            drawDichroic(g, Color.blue, true, BeamStearingElementsFactory.BOTTOM_SLIDE, false, BeamStearingElementsFactory.BOTTOM, 2, -2);
            drawDichroic(g, Color.red, true, BeamStearingElementsFactory.BOTTOM_SLIDE, false, BeamStearingElementsFactory.TOP, 2, -2);
        } else if (bottomOpticalSlideConfig.getPosition() == IOpticalSlideConfig.POSITION_AL_MIRROR) {
            //red blue
            drawDichroic(g, Color.blue, true, BeamStearingElementsFactory.BOTTOM_SLIDE, false, BeamStearingElementsFactory.TOP, -1, 1);
            drawDichroic(g, Color.red, true, BeamStearingElementsFactory.BOTTOM_SLIDE, false, BeamStearingElementsFactory.BOTTOM, -1, 1);
            //mirror
            drawDichroic(g, Color.DARK_GRAY, true, BeamStearingElementsFactory.BOTTOM_SLIDE, false, BeamStearingElementsFactory.BOTTOM, 0, 0);
            //blue red
            drawDichroic(g, Color.blue, true, BeamStearingElementsFactory.BOTTOM_SLIDE, false, BeamStearingElementsFactory.BOTTOM, 1, -1);
            drawDichroic(g, Color.red, true, BeamStearingElementsFactory.BOTTOM_SLIDE, false, BeamStearingElementsFactory.TOP, 1, -1);
        } else if (bottomOpticalSlideConfig.getPosition() == IOpticalSlideConfig.POSITION_DI_BR) {
            //red blue
            drawDichroic(g, Color.blue, true, BeamStearingElementsFactory.BOTTOM_SLIDE, false, BeamStearingElementsFactory.TOP, -2, 2);
            drawDichroic(g, Color.red, true, BeamStearingElementsFactory.BOTTOM_SLIDE, false, BeamStearingElementsFactory.BOTTOM, -2, 2);
            //mirror
            drawDichroic(g, Color.DARK_GRAY, true, BeamStearingElementsFactory.BOTTOM_SLIDE, false, BeamStearingElementsFactory.BOTTOM, -1, 1);
            //blue red
            drawDichroic(g, Color.blue, true, BeamStearingElementsFactory.BOTTOM_SLIDE, false, BeamStearingElementsFactory.BOTTOM, 0, 0);
            drawDichroic(g, Color.red, true, BeamStearingElementsFactory.BOTTOM_SLIDE, false, BeamStearingElementsFactory.TOP, 0, 0);
        }
        */
    }

    private void drawDichroic(Graphics g, Color color, boolean fill, int dichroicType, boolean isClear, int topBottom, int xOffset, int yOffset) {
        g.setColor(color);
        List polygons = BeamStearingElementsFactory.getInstance().getDichroic(dichroicType, isClear, topBottom, xOffset, yOffset);
        //PointUtility.debugDisplayPolygons(polygons);
        Iterator i = polygons.iterator();
        while (i.hasNext()) {
            Polygon polygon = (Polygon) i.next();
            if (polygon.npoints == 2) {
                //draw a line instead, 2 pointed polygons don't render
                g.drawLine(polygon.xpoints[0], polygon.ypoints[0], polygon.xpoints[1], polygon.ypoints[1]);
            } else {
                //draw the polygon
                if (fill) {
                    g.fillPolygon(polygon);
                } else {
                    g.drawPolygon(polygon);
                }
            }
        }
    }

    

    private void drawBeams(Graphics g) {

            /*
        //draw IO incoming beam arrow
        Polygon beamArrowIO = BeamStearingElementsFactory.getInstance().getBeamArrow_IO();
        g.setColor(Color.lightGray);
        g.fillPolygon(beamArrowIO);
        
        //if I dichroic is mirror, or dichroic, draw incoming -> I beams
        //if it's mirror, then exit
        if ((beamSteeringConfig.getUpperSlideConfig().getPosition() == IOpticalSlideConfig.POSITION_AL_MIRROR) || (beamSteeringConfig.getUpperSlideConfig().getPosition() == IOpticalSlideConfig.POSITION_DI_RB)) {
            //iDichroic not clear
            Polygon incomingToIMirror = BeamStearingElementsFactory.getInstance().getBeam_Incoming_to_TopMirror();
            g.setColor(Color.lightGray);
            g.fillPolygon(incomingToIMirror);

            Polygon iMirrorToI = BeamStearingElementsFactory.getInstance().getBeam_TopMirror_to_I();
            g.setColor(Color.lightGray);
            g.fillPolygon(iMirrorToI);

            if (beamSteeringConfig.getUpperSlideConfig().getPosition() == IOpticalSlideConfig.POSITION_AL_MIRROR) {
                return;
            }
        }

        //if I dichroic is clear, draw the beam incoming -> tip tilt
        //      else, draw the beam I dichroic to tip-tilt
        if (beamSteeringConfig.getUpperSlideConfig().getPosition() == IOpticalSlideConfig.POSITION_CLEAR) {
            Polygon incomingToTipTilt = BeamStearingElementsFactory.getInstance().getBeam_Incoming_to_TopMirror();
            g.setColor(Color.lightGray);
            g.fillPolygon(incomingToTipTilt);

            Polygon tipTiltToODichroic = BeamStearingElementsFactory.getInstance().getBeam_TipTilt_to_BottomSlide();
            g.setColor(Color.lightGray);
            g.fillPolygon(tipTiltToODichroic);
        } else if (beamSteeringConfig.getUpperSlideConfig().getPosition() == IOpticalSlideConfig.POSITION_DI_RB) {
            Polygon incomingToTipTilt = BeamStearingElementsFactory.getInstance().getBeam_TopSlide_to_TipTilt();
            g.setColor(Color.lightGray);
            g.fillPolygon(incomingToTipTilt);

            Polygon tipTiltToODichroic = BeamStearingElementsFactory.getInstance().getBeam_TipTilt_to_BottomSlide();
            g.setColor(Color.lightGray);
            g.fillPolygon(tipTiltToODichroic);
        } else {
            System.err.println("ILLOGICAL STATE");
        }

        //now the O dichroics and filterwheel
        if (beamSteeringConfig.getLowerSlideConfig().getPosition() == IOpticalSlideConfig.POSITION_DI_RB) {
            Polygon oDichroicToFilterWheel = BeamStearingElementsFactory.getInstance().getBeam_BottomSlide_to_FilterWheel();
            g.setColor(Color.red);
            g.fillPolygon(oDichroicToFilterWheel);

            Polygon filterWheelToO = BeamStearingElementsFactory.getInstance().getBeam_FilterWheel_to_O();
            g.fillPolygon(filterWheelToO);

            Polygon oDichroicToTHOR = BeamStearingElementsFactory.getInstance().getBeam_BottomSlide_to_THOR();
            g.setColor(Color.blue);
            g.fillPolygon(oDichroicToTHOR);

        } else if (beamSteeringConfig.getLowerSlideConfig().getPosition() == IOpticalSlideConfig.POSITION_DI_BR) {
            Polygon oDichroicToFilterWheel = BeamStearingElementsFactory.getInstance().getBeam_BottomSlide_to_FilterWheel();
            g.setColor(Color.blue);
            g.fillPolygon(oDichroicToFilterWheel);

            Polygon filterWheelToO = BeamStearingElementsFactory.getInstance().getBeam_FilterWheel_to_O();
            g.fillPolygon(filterWheelToO);

            Polygon oDichroicToTHOR = BeamStearingElementsFactory.getInstance().getBeam_BottomSlide_to_THOR();
            g.setColor(Color.red);
            g.fillPolygon(oDichroicToTHOR);

        } else if (beamSteeringConfig.getLowerSlideConfig().getPosition() == IOpticalSlideConfig.POSITION_AL_MIRROR) {
            Polygon oDichroicToFilterWheel = BeamStearingElementsFactory.getInstance().getBeam_BottomSlide_to_FilterWheel();
            g.setColor(Color.lightGray);
            g.fillPolygon(oDichroicToFilterWheel);

            Polygon filterWheelToO = BeamStearingElementsFactory.getInstance().getBeam_FilterWheel_to_O();
            g.fillPolygon(filterWheelToO);
            
        }
        */
    }
}


