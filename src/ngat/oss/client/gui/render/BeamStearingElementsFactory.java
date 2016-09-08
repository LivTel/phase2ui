/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.render;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nrc
 */
public class BeamStearingElementsFactory {

    private static BeamStearingElementsFactory instance = null;
    public static final int TOP = 0;
    public static final int BOTTOM = 1;
    public static final int DONT_CARE = 2;
    public static final int TOP_SLIDE = 3;
    public static final int BOTTOM_SLIDE = 4;

    private BeamStearingElementsFactory() {
    }

    public static BeamStearingElementsFactory getInstance() {

        if (instance == null) {
            instance = new BeamStearingElementsFactory();
        }
        return instance;
    }

    public Polygon getIFilter(int filterNumber, boolean inBeam) {

        int xpoints[] = new int[]{195, 200, 200, 195};
        int ypoints[] = new int[]{80, 80, 120, 120};
        int npoints = 4;

        int xoffset = 5;
        int yoffset = 50;
        if (filterNumber == 2) {
            //offset in x if it's filter 2
            xpoints = PointUtility.translate(xpoints, 1, xoffset);
        }

        if (!inBeam) {
            //offset in y if it's out of the beam
            ypoints = PointUtility.translate(ypoints, 1, yoffset);
        }

        return new Polygon(xpoints, ypoints, npoints);
    }

    //return a list of polygons representing the dichroic
    public List getDichroic(int dichroicType, boolean isClear, int topBottom, int xTranslation, int yTranslation) {

        //topBottom = TOP | BOTTOM (ignored for I dichroics)
        //slidePos = 0,1,2,3,4 (0 highest, 4 lowest)

        int[] xpoints, ypoints;

        int topRightX = 0, topRightY = 0;
        int offset = 30;
        ArrayList polygons = new ArrayList();

        if (dichroicType == TOP_SLIDE) {
            topRightX = 380;
            topRightY = 88;
            if (isClear) {
                xpoints = PointUtility.translate(new int[]{topRightX, topRightX + 3}, xTranslation, offset);
                ypoints = PointUtility.translate(new int[]{topRightY, topRightY + 4}, yTranslation, offset);
                polygons.add(new Polygon(xpoints, ypoints, 2));
                xpoints = PointUtility.translate(new int[]{topRightX - 26, topRightX - 29}, xTranslation, offset);
                ypoints = PointUtility.translate(new int[]{topRightY + 33, topRightY + 29}, yTranslation, offset);
                polygons.add(new Polygon(xpoints, ypoints, 2));
            } else {
                xpoints = PointUtility.translate(new int[]{topRightX, topRightX + 3, topRightX - 26, topRightX - 29}, xTranslation, offset);
                ypoints = PointUtility.translate(new int[]{topRightY, topRightY + 4, topRightY + 33, topRightY + 29}, yTranslation, offset);
                polygons.add(new Polygon(xpoints, ypoints, 4));
            }
        } else if (dichroicType == BOTTOM_SLIDE) {
            switch (topBottom) {
                case TOP:
                    //beam centralised coordinates of top O dichroic:
                    topRightX = 282;
                    topRightY = 204;
                    break;
                case BOTTOM:
                    //beam centralised coordinates of bottom O dichroic:
                    topRightX = 287;
                    topRightY = 208;
                    break;
            }
            //translate points to required location:
            xpoints = PointUtility.translate(new int[]{topRightX, topRightX + 3, topRightX - 26, topRightX - 29}, xTranslation, offset);
            ypoints = PointUtility.translate(new int[]{topRightY, topRightY + 4, topRightY + 33, topRightY + 29}, yTranslation, offset);

            polygons.add(new Polygon(xpoints, ypoints, 4));
        }

        return polygons;
    }

    public Polygon getAG() {
        int xpoints[] = new int[]{476, 513, 513, 508, 508, 481, 481, 476};
        int ypoints[] = new int[]{223, 223, 233, 233, 263, 263, 233, 233};
        int npoints = 8;

        return new Polygon(xpoints, ypoints, npoints);
    }

    public Polygon getBeamArrow_IO() {
        int xpoints[] = new int[]{349, 386, 368};
        int ypoints[] = new int[]{9, 9, 41};
        int npoints = 3;

        return new Polygon(xpoints, ypoints, npoints);
    }

    public Polygon getBeamArrow_AG() {
        int xpoints[] = new int[]{476, 513, 495};
        int ypoints[] = new int[]{9, 9, 41};
        int npoints = 3;

        return new Polygon(xpoints, ypoints, npoints);
    }

    public Polygon getBeam_AG() {
        int xpoints[] = new int[]{486, 486, 503, 503};
        int ypoints[] = new int[]{002, 221, 221, 002};
        int npoints = 4;

        return new Polygon(xpoints, ypoints, npoints);
    }

    public Polygon getBeam_Incoming_to_TopSlide() {
        int xpoints[] = new int[]{376, 376, 359, 359};
        int ypoints[] = new int[]{002, 214, 231, 002};


        int npoints = 4;

        return new Polygon(xpoints, ypoints, npoints);
    }

    public Polygon getBeam_Incoming_to_TopMirror() {
        int xpoints[] = new int[]{376, 376, 359, 359};
        int ypoints[] = new int[]{    2,  90, 107,    2};
        int npoints = 4;

        return new Polygon(xpoints, ypoints, npoints);
    }

    public Polygon getBeam_TopMirror_to_I() {
        int xpoints[] = new int[]{376, 359, 178, 178};
        int ypoints[] = new int[]{  90, 107, 107,  90};
        int npoints = 4;

        return new Polygon(xpoints, ypoints, npoints);
    }

    public Polygon getBeam_TopSlide_to_TipTilt() {
        int xpoints[] = new int[]{376, 376, 359, 359};
        int ypoints[] = new int[]{101, 214, 231, 118};
        int npoints = 4;

        return new Polygon(xpoints, ypoints, npoints);
    }

    public Polygon getBeam_TipTilt_to_BottomSlide() {
        int xpoints[] = new int[]{376, 290, 273, 359};
        int ypoints[] = new int[]{214, 214, 231, 231};
        int npoints = 4;

        return new Polygon(xpoints, ypoints, npoints);
    }

    public Polygon getBeam_TipTilt_to_THOR() {
        int xpoints[] = new int[]{376, 359, 177, 177};
        int ypoints[] = new int[]{214, 231, 231, 214};
        int npoints = 4;

        return new Polygon(xpoints, ypoints, npoints);
    }

    public Polygon getBeam_BottomSlide_to_THOR() {
        int xpoints[] = new int[]{253, 177, 177, 270};
        int ypoints[] = new int[]{231, 231, 214, 214};
        int npoints = 4;

        return new Polygon(xpoints, ypoints, npoints);
    }

    public Polygon getBeam_BottomSlide_to_FilterWheel() {
        int xpoints[] = new int[]{273, 273, 290, 290};
        int ypoints[] = new int[]{231, 290, 290, 214};
        int npoints = 4;

        return new Polygon(xpoints, ypoints, npoints);
    }

    public Polygon getBeam_FilterWheel_to_O() {
        int xpoints[] = new int[]{273, 273, 290, 290};
        int ypoints[] = new int[]{297, 308, 308, 297};
        int npoints = 4;

        return new Polygon(xpoints, ypoints, npoints);
    }
}