/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.wrapper;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class ValidationResults {
    static Logger logger = Logger.getLogger(ValidationResults.class);

    private volatile ArrayList results = new ArrayList();

    public ValidationResults() {}

    public void addValidationResult(ValidationResult validationResult) {
        results.add(validationResult);
    }

    /**
     * 
     * @param validationResults to be added to this object's results list
     * @return 
     */
    public void addValidationResults(ValidationResults validationResults) {
        
        Iterator vri = validationResults.listResults().iterator();
        while (vri.hasNext()) {
            ValidationResult validationResult = (ValidationResult) vri.next();
            results.add(validationResult);
        }
    }
    
    public ArrayList listResults() {
        return results;
    }

    public int getFailureCount() {
        int failureCount = 0;
        Iterator i = results.iterator();
        while (i.hasNext()) {
            ValidationResult validationResult = (ValidationResult) i.next();
            if (validationResult.getResultType() == validationResult.FAILURE) {
                failureCount ++;
            }
        }
        return failureCount;
    }

    public int getWarningCount() {
        int warningCount = 0;
        Iterator i = results.iterator();
        while (i.hasNext()) {
            ValidationResult validationResult = (ValidationResult) i.next();
            if (validationResult.getResultType() == validationResult.WARNING) {
                warningCount ++;
            }
        }
        return warningCount;
    }

    public boolean hadFailures() {
        return getFailureCount() > 0;
    }

    public boolean hadWarnings() {
        return getWarningCount() > 0;
    }

    
    public String getValidationResultsAsString() {

        String summary = "";
        
        Iterator i = results.iterator();
        String msgType;
        String testingObjectName = "";
        boolean isFirstLine = true;
        while (i.hasNext()) {
            ValidationResult validationResult = (ValidationResult) i.next();
            if (validationResult.getResultType() == validationResult.FAILURE) {
                msgType = "FAILURE";
            } else if (validationResult.getResultType() == validationResult.WARNING) {
                msgType = "WARNING";
            } else if (validationResult.getResultType() == validationResult.MESSAGE) {
                msgType = "MESSAGE";
            } else {
                msgType = "UNKNOWN";
            }
            
            if (validationResult.getTestingObject() != testingObjectName) {
                if (isFirstLine) {
                    isFirstLine = false;
                } else {
                    summary += "\n";
                }
                summary += validationResult.getTestingObject();
                testingObjectName = validationResult.getTestingObject();
            }
            summary += "\n " + msgType + ": " + validationResult.getMessage();
        }

        //summary of results:
        if (!this.hadWarnings() && !this.hadFailures()) {
            summary += "\nThe validation completed without any warnings or failures.";
        }

        if (this.hadWarnings() && !this.hadFailures()) {
            summary += "\nThe validation completed with " + this.getWarningCount();
            if (this.getWarningCount() == 1) {
                summary += " warning.";
            } else {
                summary += " warnings.";
            }
        }

        if (this.hadFailures() && !this.hadWarnings()) {
            summary += "\nThe validation completed with " + this.getFailureCount();
             if (this.getFailureCount() == 1) {
                summary += " failure.";
            } else {
                summary += " failures.";
            }
        }

        if (this.hadFailures() && this.hadWarnings()) {
            summary += "\nThe validation completed with " + this.getFailureCount();
            if (this.getFailureCount() == 1) {
                summary += " failure.";
            } else {
                summary += " failures.";
            }

            if (this.getWarningCount() == 1) {
                summary += " A warning was also found.";
            } else {
                summary += " A further " + this.getWarningCount() + " warnings were found.";
            }
        }

        return summary;
    }

    public String toString() {
        String s = this.getClass().getName();
        s += "[results=";
        Iterator warningsIterator = results.iterator();
        while (warningsIterator.hasNext()) {
            ValidationResult result = (ValidationResult) warningsIterator.next();
            s += result + ",";
        }
        
        s += "]";
        return s;
    }

}
