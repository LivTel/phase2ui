/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.wrapper;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author nrc
 */
public class BookedPeriod implements Comparable {

    private boolean gotStartTime, gotEndTime;
    private long startTime, endTime, groupID, slack;
    //private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy H:mm:ss");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm z");


    public BookedPeriod() {

        dateFormat.setDateFormatSymbols(new DateFormatSymbols(Locale.UK)); //make sure months are spelt in English
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        this.gotStartTime = false;
        this.gotEndTime = false;
    }

    public BookedPeriod(long startTime, long endTime) {
        this();
        
        this.startTime = startTime;
        this.endTime = endTime;

        this.gotStartTime = true;
        this.gotEndTime = true;
    }

    public BookedPeriod(long groupID, long slack, long startTime, long endTime) {
        this(startTime, endTime);
        this.groupID = groupID;
        this.slack = slack;
    }

    public boolean isPopulated() {
        return (gotStartTime && gotEndTime);
    }

    public long getEndTime() {
        return endTime;
    }

    public String getEndTimeFormattedString() {
   
        return dateFormat.format(new Date(endTime));
    }

    public long getStartTime() {
        return startTime;
    }

    public String getStartTimeFormattedString() {
        return dateFormat.format(new Date(startTime));
    }

    public long getSlack() {
        return slack;
    }

    public long getGroupID() {
        return groupID;
    }

    public boolean containsTime(long time) {
        //System.err.println("containsTime("  + time + ") [startTime=" + startTime + " | endTime=" + endTime + "]");
        boolean containsTime = ((startTime <= time) && (time <= endTime));
        //System.err.println("... returning " + containsTime );
        return  containsTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
        this.gotEndTime = true;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
        this.gotStartTime = true;
    }

    public String getFormattedString() {

        return getStartTimeFormattedString() + " <----> " + getEndTimeFormattedString();
    }

    public int compareTo(Object arg0) {
        BookedPeriod otherBookedPeriod = (BookedPeriod) arg0;
        if (this.startTime < otherBookedPeriod.startTime) {
            return -1;
        } else if (this.startTime > otherBookedPeriod.startTime) {
            return 1;
        } else {
            return 0;
        }
    }

    public String toString() {
        return this.getClass().getName()  + "[" +dateFormat.format(new Date(startTime)) + "<-->" + dateFormat.format(new Date(endTime)) + "]";
    }
}
