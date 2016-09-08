/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.wrapper;

/**
 *
 * @author nrc
 */
public class Dichroic implements IOpticalSlideElement {

    private String name, firstColour, secondColour;
    private int position;

    public Dichroic( int position, String name, String firstColour, String secondColour) {
        this.name = name;
        this.firstColour = firstColour;
        this.secondColour = secondColour;
        this.position = position;
    }

    public String getFirstColour() {
        return firstColour;
    }

    public void setFirstColour(String firstColour) {
        this.firstColour = firstColour;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //fullfill the interface
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getSecondColour() {
        return secondColour;
    }

    public void setSecondColour(String secondColour) {
        this.secondColour = secondColour;
    }

    
    public String toString() {
        String s = this.getClass().getName();
        s += "[";
        s += "position=" + position + ", ";
        s += "name=" + name + ", ";
        s += "firstColour=" + firstColour + ", ";
        s += "secondColour=" + secondColour ;
        s += "]";
        return s;
    }

}
