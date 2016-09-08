/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.wrapper;

/**
 *
 * @author nrc
 */
public class ExposureWrapper {
    private int exposureCount;
    private double exposureTime;

    public ExposureWrapper(int exposureCount, double exposureTime) {
        this.exposureCount = exposureCount;
        this.exposureTime = exposureTime;
    }

    public int getExposureCount() {
        return exposureCount;
    }

    public void setExposureCount(int exposureCount) {
        this.exposureCount = exposureCount;
    }

    public double getExposureTime() {
        return exposureTime;
    }

    public void setExposureTime(double exposureTime) {
        this.exposureTime = exposureTime;
    }

    
}
