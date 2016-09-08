/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ngat.oss.client.gui.wrapper;

import ngat.phase2.ISemester;

/**
 *
 * @author nrc
 */
public class SemesterSelectionTableLine {

    public static final int SEM_NAME_COL = 0;
    public static final int SELECTED_COL = 1;
    
    public static final Class[] COL_CLASSES = new Class[]{java.lang.String.class, java.lang.Boolean.class};
    public static final String[] COL_NAMES = new String[]{"Semester", "Create account?"};
    
    ISemester semester;
    boolean selected;

    public ISemester getSemester() {
        return semester;
    }

    public void setSemester(ISemester semester) {
        this.semester = semester;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setValueAt(Object value, int col) {
        
        if (col ==1) {
            Boolean boolVal = (Boolean)value;
            selected = boolVal.booleanValue();
        }
        return;
    }

    public Object getValueAt(int col) {
        switch(col) {
            case SEM_NAME_COL:
                return semester.getName();
            case SELECTED_COL:
                return new Boolean(selected);
        }
        return null;
    }
    
    public static String getColumnName(int col) {
        return COL_NAMES[col];
    }

    public static int getColumnCount() {
        return 2;
    }
    
    public String toString() {
        String s = this.getClass().getName();
        s += "[semester=" + semester + ", selected=" + selected + "]";
        return s;
    }
}
