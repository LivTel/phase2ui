/*
 * NewClass.java
 *
 * Created on 05 December 2007, 10:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ngat.oss.client.gui.reference;

import java.net.URL;
import ngat.phase2.IUser;

/**
 *
 * @author nrc
 */
public class Session {
    
    private static Session instance;
    private IUser user;
    private URL url;
    
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }
    
    /** Creates a new instance of NewClass */
    private Session() {
    }
    
    public void setUser(IUser user) {
        this.user = user;
    }
    
    public IUser getUser() {
        return user;
    }
    
    public void setServiceURL(URL url) {
        this.url = url;
    }
    
    public String getServiceHostName() {
        return url.getHost();
    }
    
}
