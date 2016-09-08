/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.wrapper;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author nrc
 */
public class SlideArrangementsContainer {

    private ArrayList slideArrangementList;

    //container for slide arrangements
    public SlideArrangementsContainer() {
        slideArrangementList = new ArrayList();
    }

    public void addSlideArrangement(SlideArrangement slideArrangement) {
        slideArrangementList.add(slideArrangement);
    }

    public SlideArrangement getSlideArrangement(int slideIndex) {
        Iterator sali = slideArrangementList.iterator();
        while (sali.hasNext()) {
            SlideArrangement slideArrangement = (SlideArrangement)sali.next();
            if (slideArrangement.getSlideIndex() == slideIndex) {
                //got the right slide, now get the right element on that slide
                return slideArrangement;
            }
        }
        return null;
    }

    public IOpticalSlideElement getSlideElement(int slideIndex, int elementPosition) {

        SlideArrangement slideArrangement = getSlideArrangement(slideIndex);
        if (slideArrangement != null) {
            return slideArrangement.getElementAt(elementPosition);
        }
        return null;
    }

    public String toString() {
        String s = this.getClass().getName() + "[";
        Iterator i = slideArrangementList.iterator();
        while (i.hasNext()) {
            SlideArrangement sa = (SlideArrangement) i.next();
            s += sa + ",";
        }
        s += "]";
        return s;
    }
}
