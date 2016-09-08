/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.util;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author nrc
 */
public class Trimmer {

    public static String trim(String originalString) {
        ArrayList allowedCharacters = new ArrayList();
        char[] originalStringAsCharArray = originalString.toCharArray();
        for (int i=0; i< originalStringAsCharArray.length; i++) {
            int c = originalStringAsCharArray[i];
            if (c > 32 && c < 127) {
                allowedCharacters.add(new Character((char) c));
            } else {
                //System.err.println("dumping: " + (char)c);
            }
        }

        int p=0;
        char[] allowedCharsArray = new char[allowedCharacters.size()];
        Iterator aci = allowedCharacters.iterator();
        while (aci.hasNext()) {
            Character c = (Character) aci.next();
            allowedCharsArray[p] = c.charValue();
            p++;
        }
        return new String(allowedCharsArray);
    }

}
