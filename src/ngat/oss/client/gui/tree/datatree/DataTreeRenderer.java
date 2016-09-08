/*
 * MainTreeRenderer.java
 *
 * Created on 06 December 2007, 15:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ngat.oss.client.gui.tree.datatree;

import java.awt.Component;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import ngat.oss.client.gui.reference.CONST;
import ngat.oss.client.gui.render.AccessPermissionRenderer;
import ngat.oss.client.gui.wrapper.AccessPermissionWrapper;
import ngat.phase2.IAccessPermission;
import ngat.phase2.IGroup;
import ngat.phase2.IProgram;
import ngat.phase2.IProposal;
import ngat.phase2.ITag;
import ngat.phase2.IUser;
import org.apache.log4j.Logger;

/**
 *
 * @author nrc
 */
public class DataTreeRenderer extends DefaultTreeCellRenderer {

    static Logger logger = Logger.getLogger(DataTreeRenderer.class);

    private static final String ICON_PATH_ROOT = "oss_client_images/";
    private static final String AP_PATH = ICON_PATH_ROOT +  "AP.jpg";
    private static final String GROUP_PATH = ICON_PATH_ROOT +  "Group.jpg";
    private static final String OS_PATH = ICON_PATH_ROOT +  "ObsSeq.jpg";
    private static final String PROG_PATH = ICON_PATH_ROOT +  "Prog.jpg";
    private static final String PROP_PATH = ICON_PATH_ROOT +  "Proposal.jpg";
    private static final String TAG_PATH = ICON_PATH_ROOT +  "TAG.jpg";
    private static final String USER_PATH = ICON_PATH_ROOT +  "User.jpg";
    private static final String COLL_PATH = ICON_PATH_ROOT +  "Collection.jpg";

    /** Creates a new instance of MainTreeRenderer */
    public DataTreeRenderer() {
        super();
    }
    
    public Component getTreeCellRendererComponent(
                                            JTree   tree,
                                            Object  value,
                                            boolean selected,
                                            boolean expanded,
                                            boolean leaf,
                                            int     row,
                                            boolean hasFocus) {
        
        super.getTreeCellRendererComponent(
                tree, value, selected,
                expanded, leaf, row,
                hasFocus);

        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
        Object userObject = treeNode.getUserObject();
        String iconPath = null;

        if (userObject instanceof String) {
            setText(String.valueOf(userObject));
            if (isACollectionRoot(userObject)) {
                iconPath = COLL_PATH;
            } else {
                iconPath = USER_PATH; //don't think this is actually needed
            }
        } else if (userObject instanceof IUser) {
            IUser user = (IUser) userObject;
            setText(user.getName());
            iconPath = USER_PATH;
        } else if (userObject instanceof IProgram) {
            IProgram programme = (IProgram) userObject;
            setText(programme.getName());
            iconPath = PROG_PATH;
       } else if (userObject instanceof ITag) {
            ITag tag = (ITag) userObject;
            setText(tag.getName());
            iconPath = TAG_PATH;
        } else  if (userObject instanceof AccessPermissionWrapper) {
            AccessPermissionWrapper accessPermissionWrapper = (AccessPermissionWrapper) userObject;
            setText(AccessPermissionRenderer.getRenderedAccessPermission(accessPermissionWrapper));
            iconPath = AP_PATH;
        } else if (userObject instanceof IProposal) {
            IProposal proposal = (IProposal) userObject;
            setText(proposal.getName());
            setIcon(CONST.PROPOSAL_ICON);
            iconPath = PROP_PATH;
        } else if (userObject instanceof IGroup) {
            IGroup group = (IGroup) userObject;
            setText(group.getName());
            setIcon(CONST.GROUP_ICON);
            iconPath = GROUP_PATH;
        } else {
            logger.info("not rendering object of type " + value.getClass().getName());
            return this;
        }

        ImageIcon imageIcon = getImageIcon(iconPath);

        //now the icon
        setIcon(imageIcon);
        
        return this;
    }

    private ImageIcon getImageIcon(String iconPath) {
        /*
        http://java.sun.com/j2se/1.5.0/docs/guide/javaws/developersguide/faq.html#211
        */
        
        URL imageURL;
        ImageIcon imageIcon;
        ClassLoader classLoader;

        //first class loader
        classLoader = ClassLoader.getSystemClassLoader();
        imageURL = classLoader.getResource(iconPath);
        if (imageURL != null) {
            imageIcon = new ImageIcon(imageURL);
            if (imageIcon != null) {
                return imageIcon;
            }
        }

        //second class loader
        classLoader = getClass().getClassLoader();
        imageURL = classLoader.getResource(iconPath);
        if (imageURL != null) {
            imageIcon = new ImageIcon(imageURL);
            if (imageIcon != null) {
                return imageIcon;
            }
        }

        //third class loader
        classLoader = ClassLoader.getSystemClassLoader();
        imageURL = classLoader.getResource(iconPath);
        if (imageURL != null) {
            imageIcon = new ImageIcon(imageURL);
            if (imageIcon != null) {
                return imageIcon;
            }
        }

        //fourth class loader
        classLoader = Thread.currentThread().getContextClassLoader();
        imageURL = classLoader.getResource(iconPath);
        if (imageURL != null) {
            imageIcon = new ImageIcon(imageURL);
            if (imageIcon != null) {
                return imageIcon;
            }
        }

        logger.error("unable to load image at url: " + iconPath);
        return null;
    }

    private boolean isACollectionRoot(Object userObject) {
        boolean isACollectionRoot = false;
        String userObjectAsString = (String) userObject;
        if (userObjectAsString.indexOf(CONST.USERS_TREE_ROOT_NAME) > -1) {
            isACollectionRoot = true;
        } else if (userObjectAsString.indexOf(CONST.PROGRAMMES_TREE_ROOT_NAME) > -1) {
            isACollectionRoot = true;
        } else if (userObjectAsString.indexOf(CONST.TAGS_TREE_ROOT_NAME) > -1) {
            isACollectionRoot = true;
        }
        return isACollectionRoot;
    }
}
