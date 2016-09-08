/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.wrapper;

import ngat.phase2.IAccount;
import ngat.phase2.IProposal;
import ngat.phase2.ISemester;

/**
 *
 * @author nrc
 */
public class SemesterAccountProposalWrapper {

    private ISemester semester;
    private IAccount account;
    private IProposal proposal;

    public SemesterAccountProposalWrapper(ISemester semester, IAccount account, IProposal proposal) {
        this.semester = semester;
        this.account = account;
        this.proposal = proposal;
    }

    public IProposal getProposal() {
        return proposal;
    }

    public ISemester getSemester() {
        return semester;
    }

    public IAccount getAccount() {
        return account;
    }

}
