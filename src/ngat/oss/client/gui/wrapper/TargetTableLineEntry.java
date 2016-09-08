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
import ngat.oss.client.gui.render.TargetRenderer;
import ngat.phase2.ITarget;

/**
 *
 * @author nrc
 */
public class TargetTableLineEntry {

    public static final String[] COL_NAMES = new String[]{"Name", "Type", "Summary"};
    ITarget target;

    public TargetTableLineEntry(ITarget target) {
        this.target = target;
    }

    public ITarget getTarget() {
        return target;
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
                return target.getName();
            case 1:
                return TargetRenderer.getTargetTypeDescription(target);
            case 2:
               return TargetRenderer.getSummaryOfTarget(target);
        }
        return null;
    }

}
