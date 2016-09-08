/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 *
 * @author nrc
 */
public class LimitedCharactersDocument extends PlainDocument {

    public static final int STRICT_LIMITATION = 1;
    public static final int NUMBERS_ONLY_LIMITATION = 2;
    public static final int NUMBERS_AND_DECPOINT_LIMITATION = 3;
    public int limitation;

    public LimitedCharactersDocument(int limitation) {
        this.limitation = limitation;
    }

    //characters are limited to
    /**
    A-Z a-z 0-9 <space> + - . _
    */
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {

         //System.err.println("insertString(offs=" + offs + ", str=" + str + ", a=" + a + ")");

        if (str == null) {
            //System.err.println("... str == null");
            return;
        }

        char[] allowedChars = new char[str.length()];
        int count = 0;
        boolean stringWasInserted = false;
        for (; count < str.length(); count++) {
            char ch = str.charAt(count);
            if (isAllowedCharacter(ch)) {
                allowedChars[count] = ch;
                stringWasInserted = true;
            }
        }

        if (stringWasInserted) {
            str = new String(allowedChars, 0, count);
            //System.err.println("insertString(offs=" + offs + ", str=" + str + ", a=" + a + ")");
            super.insertString(offs, str, a);
        }

        //System.err.println("getLength=" + this.getLength() + " , getEndPosition()=" + this.getEndPosition());

    }


    private boolean isAllowedCharacter(char c) {

        if (limitation == STRICT_LIMITATION) {
            int asciiCode = c;
            if ((asciiCode <= 90) && (asciiCode >=65)) {
                //A-Z
                return true;
            }
            if ((asciiCode <= 122) && (asciiCode >=97)) {
                //a-z
                return true;
            }
            if ((asciiCode <= 57) && (asciiCode >=48)) {
                //0-9
                return true;
            }
            if (asciiCode == 32) {
                //space
                return true;
            }
            if (asciiCode == 43) {
                //+
                return true;
            }
            if (asciiCode == 45) {
                //-
                return true;
            }
            if (asciiCode == 46) {
                //.
                return true;
            }
            if (asciiCode == 95) {
                //_
                return true;
            }
            return false;
        } else if (limitation == NUMBERS_ONLY_LIMITATION) {
            int asciiCode = c;
            if ((asciiCode <= 57) && (asciiCode >=48)) {
                //0-9
                return true;
            }
            return false;
        } else if (limitation == LimitedCharactersDocument.NUMBERS_AND_DECPOINT_LIMITATION) {
            int asciiCode = c;
            if ((asciiCode <= 57) && (asciiCode >=48)) {
                //0-9
                return true;
            } else if (asciiCode == 46) {
                //.
                return true;
            }
            return false;
        } else {
            //no limitation set
            return true;
        }
        
    }

    private String getStringValue(char[] chars) {
        String s = "";
        for (int i=0; i<chars.length; i++) {
            s += chars[i];
        }
        return s;
    }

}
