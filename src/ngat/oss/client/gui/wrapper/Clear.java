/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.wrapper;

/**
 *
 * @author nrc
 */
public class Clear implements IOpticalSlideElement {

    private static final String CLEAR = "Clear";

    private int position;

    public Clear(int position) {
        this.position = position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return CLEAR;
    }

    public int getPosition() {
        return position;
    }

    public String toString() {
        String s = this.getClass().getName();
        s += "[position=" + position + ",";
        s += "name=" + CLEAR + "]";
        return s;
    }


}
