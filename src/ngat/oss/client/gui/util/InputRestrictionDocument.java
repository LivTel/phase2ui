package ngat.oss.client.gui.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class InputRestrictionDocument extends PlainDocument {
    private int sizeLimit;
    private boolean numericsOnly = false;
    private boolean signsOnly = false;
   
    public InputRestrictionDocument(int sizeLimit, boolean numericsOnly, boolean signsOnly) {
        super();
        this.sizeLimit = sizeLimit;
        this.numericsOnly = numericsOnly;
        this.signsOnly = signsOnly;
    }
    
    public void insertString(int offset, String  str, AttributeSet attr) throws BadLocationException {
        if (str == null) 
            return;
        
        boolean allNumerics = allNumerics(str);
        boolean allSigns = allSigns(str);
        boolean isWithinSize = (getLength() + str.length()) <= sizeLimit;
        
        boolean canInsert = true;
        
        if (numericsOnly && !allNumerics) {
            canInsert = false;
        }
        
        if (signsOnly && !allSigns) {
            canInsert = false;
        }
        
         if (!isWithinSize)
             canInsert = false;
        
        if (canInsert)
            super.insertString(offset, str, attr);
    }
    
    private boolean allSigns(String inStr) {
        char[] in = inStr.toCharArray();
        boolean nonSignFound = false;
        for (int i=0; i<in.length; i++ ) {
            int n =(int) in[i];
            if (!(n !=38 && n != 40)) {
                nonSignFound = true;
            }
        }
        return !nonSignFound;
    }
    
    private boolean allNumerics(String inStr) {
        char[] in = inStr.toCharArray();
        boolean nonNumericFound = false;
        for (int i=0; i<in.length; i++ ) {
            int n =(int) in[i];
            if (!(n >=48 && n <= 57)) {
                nonNumericFound = true;
            }
        }
        return !nonNumericFound;
    }
}