/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.wrapper;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author nrc
 */
public class SlideArrangement {

    private String name;
    private int slideIndex;
    private List opticalSlideElements; //IOpticalSlideElement i.e. Dichroics | Mirrors | Clears

    public SlideArrangement() {}

    public SlideArrangement(String name, int slideIndex) {
        this.name = name;
        this.slideIndex = slideIndex;
    }

    public IOpticalSlideElement getElementAt(int position) {
        Iterator i = opticalSlideElements.iterator();
        while (i.hasNext()) {
            IOpticalSlideElement element = (IOpticalSlideElement) i.next();
            if (element.getPosition() == position) {
                return element;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List getOpticalSlideElements() {
        return opticalSlideElements;
    }

    public void setOpticalSlideElements(List opticalSlideElements) {
        this.opticalSlideElements = opticalSlideElements;
    }

    public int getSlideIndex() {
        return slideIndex;
    }

    public void setslideIndex(int slideIndex) {
        this.slideIndex = slideIndex;
    }

    public String toString() {
        String s = this.getClass().getName() + "[";
        s += name + ", ";
        s += slideIndex + ", ";
        s += "elements=[" ;
        Iterator i = opticalSlideElements.iterator();
        while (i.hasNext()) {
            IOpticalSlideElement opticalSlideElement = (IOpticalSlideElement) i.next();
            s += opticalSlideElement.toString() + ",";
        }
        s += "]]";
        return s;
    }
}
