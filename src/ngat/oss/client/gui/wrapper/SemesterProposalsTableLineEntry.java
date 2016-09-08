/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.wrapper;

import ngat.phase2.ISemester;
import ngat.phase2.util.SemesterAccountEntry;

/**
 *
 * @author nrc
 */
public class SemesterProposalsTableLineEntry {

    public static final String[] COL_NAMES = new String[]{"Proposal", "Programme", "PI", "Allocated", "Consumed", "Graphic"};

    public static final int PROPOSAL_COLUMN = 0;
    public static final int PROGRAMME_COLUMN = 1;
    public static final int PI_COLUMN = 2;
    public static final int ALLOCATED_COLUMN = 3;
    public static final int CONSUMED_COLUMN = 4;
    public static final int GRAPHIC_COLUMN = 5;

    private ISemester semester;
    private SemesterAccountEntry semesterAccountEntry;

    public SemesterProposalsTableLineEntry(ISemester semester, SemesterAccountEntry semesterAccountEntry) {
        this.semester = semester;
        this.semesterAccountEntry = semesterAccountEntry;
    }

    public static int getColumnCount() {
        return COL_NAMES.length;
    }

    public static String getColumnName(int col) {
        return COL_NAMES[col];
    }

    public ISemester getSemester() {
        return semester;
    }

    public SemesterAccountEntry getSemesterAccountEntry() {
        return semesterAccountEntry;
    }

    public Object getValueAt(int col) {

        switch (col) {
            case PROPOSAL_COLUMN:
                return semesterAccountEntry.getColumn1();
            case PROGRAMME_COLUMN:
                return semesterAccountEntry.getColumn2();
            case PI_COLUMN:
                return semesterAccountEntry.getColumn3();
            case ALLOCATED_COLUMN:
                return new SemesterAccountWrapper(semester, semesterAccountEntry.getAccount());//passes SemesterAccountWrapper to the related renderer
            case CONSUMED_COLUMN:
                return new SemesterAccountWrapper(semester, semesterAccountEntry.getAccount());//passes SemesterAccountWrapper to the related renderer
            case GRAPHIC_COLUMN:
                return semesterAccountEntry.getAccount();
        }
        return null;
    }

    public void setValueAt(Object value, int col) {

        SemesterAccountWrapper semesterAccountWrapper;
        switch (col) {
            case PROPOSAL_COLUMN:
                semesterAccountEntry.setColumn1((String) value);
                break;
            case PROGRAMME_COLUMN:
                semesterAccountEntry.setColumn2((String) value);
                break;
            case PI_COLUMN:
                semesterAccountEntry.setColumn3((String) value);
                break;
            case ALLOCATED_COLUMN:
                semesterAccountWrapper = (SemesterAccountWrapper) value;
                semesterAccountEntry.setAccount(semesterAccountWrapper.getAccount());
                break;
            case CONSUMED_COLUMN:
                semesterAccountWrapper = (SemesterAccountWrapper) value;
                semesterAccountEntry.setAccount(semesterAccountWrapper.getAccount());
                break;
         }
    }
    
    /*
    public String toString() {
        String s = "";
        s += this.getClass().getName();
        s += "[" + this.
    }
    */
}