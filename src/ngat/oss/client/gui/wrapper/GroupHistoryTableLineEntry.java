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
import ngat.phase2.IHistoryItem;

/**
 *
 * @author nrc
 */
public class GroupHistoryTableLineEntry {

    public static final String[] COL_NAMES = new String[]{"Scheduled Time", "Completed Time", "Status", "Error Message"};
    IHistoryItem historyItem;

    public GroupHistoryTableLineEntry(IHistoryItem historyItem) {
        this.historyItem = historyItem;
    }

    public static int getColumnCount() {
        return COL_NAMES.length;
    }

    public static String getColumnName(int col) {
        return COL_NAMES[col];
    }

    private String getTimeString(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy H:mm:ss");
        dateFormat.setDateFormatSymbols(new DateFormatSymbols(Locale.UK)); //make sure months are spelt in English
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(new Date(time));
    }

    public Object getvalueAt(int col) {
       
        switch (col) {
            case 0:
                return getTimeString(historyItem.getScheduledTime());
            case 1:
                return getTimeString(historyItem.getCompletionTime());
            case 2:
                int completionStatus = historyItem.getCompletionStatus();
                switch (completionStatus) {
                    case IHistoryItem.EXECUTION_FAILED:
                        return "Failed";
                    case IHistoryItem.EXECUTION_PARTIAL:
                        return "Partial Success";
                    case IHistoryItem.EXECUTION_SUCCESSFUL:
                        return "Success";
                }
                return historyItem.getCompletionStatus();
            case 3:
                if (historyItem.getCompletionStatus() == IHistoryItem.EXECUTION_FAILED)
                    return historyItem.getErrorMessage();
                else
                    return "";
        }
        return null;
    }
}
