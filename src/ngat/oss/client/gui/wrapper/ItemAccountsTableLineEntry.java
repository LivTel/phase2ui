/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ngat.oss.client.gui.wrapper;

/**
 *
 * @author nrc
 */
public class ItemAccountsTableLineEntry {

    public static final String[] COL_NAMES = new String[]{"Semester", "Allocated", "Consumed", "Graphic", "Delete"};

    public static final int SEMESTER_COLUMN = 0;
    public static final int ALLOCATED_COLUMN = 1;
    public static final int CONSUMED_COLUMN = 2;
    public static final int GRAPHIC_COLUMN = 3;
    public static final int DELETE_COLUMN = 4;

    private SemesterAccountWrapper semesterAccountWrapper;

    public ItemAccountsTableLineEntry(SemesterAccountWrapper semesterAccountWrapper) {
        this.semesterAccountWrapper = semesterAccountWrapper;
    }

    public static int getColumnCount() {
        return COL_NAMES.length;
    }

    public static String getColumnName(int col) {
        return COL_NAMES[col];
    }

    public Object getvalueAt(int col) {
       
        switch (col) {
            case SEMESTER_COLUMN:
                return semesterAccountWrapper.getSemester().getName();
            case ALLOCATED_COLUMN:
                return semesterAccountWrapper; //passes a wrapper for the semester and account to the IncDecPanelCellRenderer
            case CONSUMED_COLUMN:
                return semesterAccountWrapper; //passes a wrapper for the semester and account to the IncDecPanelCellRenderer
            case GRAPHIC_COLUMN:
                return semesterAccountWrapper.getAccount(); //passes the account object to the BarChartCellRenderer
            case DELETE_COLUMN:
                return semesterAccountWrapper; //passes the account object to the AccountDeleteButtonCellRenderer;
        }
        return null;
    }

    public void setValueAt(Object value, int col) {

        switch (col) {
            case ALLOCATED_COLUMN:
                semesterAccountWrapper = (SemesterAccountWrapper) value;
                break;
            case CONSUMED_COLUMN:
                semesterAccountWrapper = (SemesterAccountWrapper) value;
                break;
            default:
                //System.err.println(this.getClass().getName() + " setValueAt(" + value + "," + col + "); NOT HANDLED");

        }
    }
}
