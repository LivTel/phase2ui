/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.wrapper;

import ngat.phase2.IAccount;
import ngat.phase2.ISemester;

/**
 *
 * @author nrc
 */
public class SemesterAccountWrapper {

    private ISemester semester;
    private IAccount account;

    public SemesterAccountWrapper(ISemester semester, IAccount account) {
        this.semester = semester;
        this.account = account;
    }

    public ISemester getSemester() {
        return semester;
    }

    public IAccount getAccount() {
        return account;
    }

    public void setAccount(IAccount account) {
        this.account = account;
    }

    public String toString() {
        String s = this.getClass().getName() + ":[semester=" +semester + ", account=" + account + "]";
        return s;
    }
}
