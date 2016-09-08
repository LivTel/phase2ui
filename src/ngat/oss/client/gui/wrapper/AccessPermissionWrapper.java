/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngat.oss.client.gui.wrapper;

import ngat.phase2.IAccessPermission;
import ngat.phase2.IProposal;
import ngat.phase2.IUser;

/**
 *
 * @author nrc
 */
public class AccessPermissionWrapper {

    IAccessPermission accessPermission;
    IUser user;
    IProposal proposal;

    public AccessPermissionWrapper(IAccessPermission accessPermission, IUser user, IProposal proposal) {
        this.accessPermission = accessPermission;
        this.user = user;
        this.proposal = proposal;
    }

    public IAccessPermission getAccessPermission() {
        return accessPermission;
    }

    public void setAccessPermission(IAccessPermission accessPermission) {
        this.accessPermission = accessPermission;
    }

    public IProposal getProposal() {
        return proposal;
    }

    public void setProposal(IProposal proposal) {
        this.proposal = proposal;
    }

    
}
