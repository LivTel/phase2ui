/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.wrapper;

/**
 *
 * @author nrc
 */
public class ValidationResult {

    public static final int WARNING = 0;
    public static final int FAILURE = 1;
    public static final int MESSAGE = 2;
    
    private String testingObject;
    private int resultType; //WARNING | FAILURE | MESSAGE
    private String message;

    public ValidationResult(String testingObject, int resultType, String message) {
        this.testingObject = testingObject;
        this.resultType = resultType;
        this.message = message;
    }

    public int getResultType() {
        return resultType;
    }

    public void setResultType(int resultType) {
        this.resultType = resultType;
    }

    public String getTestingObject() {
        return testingObject;
    }

    public void setTestingObject(String testingObject) {
        this.testingObject = testingObject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        String s = this.getClass().getName();
        s += "[";
        s += "testingObject=" + testingObject + ",";
        switch (resultType) {
            case ValidationResult.FAILURE:
                s += "resultType=FAILURE,";
                break;
            case ValidationResult.WARNING:
                s += "resultType=WARNING,";
                break;
            case ValidationResult.MESSAGE:
                s += "resultType=MESSAGE,";
                break;
        }

        s += "message=" + message + "]";
        return s;
    }
}
