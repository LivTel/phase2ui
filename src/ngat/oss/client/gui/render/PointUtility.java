/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.render;

import java.awt.Polygon;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author nrc
 */
public class PointUtility {

    public static int[] translate(int[] ordinates, int translation, int offset) {
        int[] newOordinates = new int[ordinates.length];

        for (int i=0; i<ordinates.length; i++) {
            int ordinate = ordinates[i];
            ordinate = ordinate + (offset * translation);
            newOordinates[i] = ordinate;
        }

        return newOordinates;
    }

    public static void debugDisplayPolygons(List polygons) {
        Iterator i = polygons.iterator();
        while (i.hasNext()) {
            String s = "Polygon[";
            Polygon p = (Polygon) i.next();
            int npoints = p.npoints;
            int[] xpoints = p.xpoints;
            int[] ypoints = p.ypoints;
            for (int j=0; j<npoints; j++) {
                int x = xpoints[j];
                int y = ypoints[j];
                s += "(x=" +x + ",y=" + y + "),";
            }
            s += "]";
            System.err.println(s);
        }
    }
}