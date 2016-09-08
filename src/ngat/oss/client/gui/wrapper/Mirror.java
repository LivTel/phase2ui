/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.wrapper;

/**
 *
 * @author nrc
 */
public class Mirror implements IOpticalSlideElement {

    private int position;
    private String name;

    public Mirror(int position, String name) {
        this.position = position;
        this.name = name;
    }

    public int getPosition() {
        return position;
    }
    
    public String toString() {
        String s = this.getClass().getName();
        s += "[position=" + position + ", name=" + name + "]";
        return s;
    }

    public String getName() {
        return name;
    }
    
}
