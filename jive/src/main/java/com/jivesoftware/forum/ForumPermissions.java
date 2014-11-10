/**
 * $RCSfile: ForumPermissions.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:43 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import com.jivesoftware.util.*;

/**
 * Defines a set of permissions for objects in the forum system that
 * users can be granted. Forum permissions are used by the protection
 * proxy objects defined for each major component of the system.
 */
public class ForumPermissions implements Cacheable {

    /**
     * Permission to read object.
     */
    public static final int READ = 0;

    /**
     * Permission to administer the entire sytem.
     */
    public static final int SYSTEM_ADMIN = 1;

    /**
     * Permission to administer a particular forum.
     */
    public static final int FORUM_ADMIN = 2;

    /**
     * Permission to administer a particular user.
     */
    public static final int USER_ADMIN = 3;

    /**
     * Permission to administer a particular group.
     */
    public static final int GROUP_ADMIN = 4;

    /**
     * Permission to moderate threads.
     */
    public static final int MODERATE_THREADS = 5;

    /**
     * Permission to create a new thread.
     */
    public static final int CREATE_THREAD = 6;

    /**
     * Permission to create a new message.
     */
    public static final int CREATE_MESSAGE = 7;

    /**
     * Permission to moderate messages.
     */
    public static final int MODERATE_MESSAGES = 8;

    /**
     * Holds the actual permission values.
     */
    private boolean [] values = new boolean[9];

    private static final ForumPermissions NO_PERMS =
        new ForumPermissions(false, false, false, false, false, false, false, false, false);
    private static final ForumPermissions FULL_PERMS =
        new ForumPermissions(true, true, true, true, true, true, true, true, true);
    private static final ForumPermissions READ_ONLY =
        new ForumPermissions(true, false, false, false, false, false, false, false, false);

    /**
     * Factory method to create full permissions.
     */
    public static ForumPermissions full() {
        return FULL_PERMS;
    }

    /**
     * Factory method to create an object with no permissions.
     */
    public static ForumPermissions none() {
        return NO_PERMS;
    }

    /**
     * Factory method to create an object with read-only permissions.
     */
    public static ForumPermissions readOnly() {
        return READ_ONLY;
    }

    /**
     * Create a new permissions object with the specified permissions.
     */
    public ForumPermissions(boolean read, boolean systemAdmin,
            boolean forumAdmin, boolean userAdmin, boolean groupAdmin,
            boolean moderateThreads, boolean createThread, boolean createMessage,
            boolean moderateMessages)
    {
        values[0] = read;
        values[1] = systemAdmin;
        values[2] = forumAdmin;
        values[3] = userAdmin;
        values[4] = groupAdmin;
        values[5] = moderateThreads;
        values[6] = createThread;
        values[7] = createMessage;
        values[8] = moderateMessages;
    }

    /**
     * Creates a new ForumPermission object by combining two permissions
     * objects. The higher permission of each permission type will be used.
     */
    public ForumPermissions(ForumPermissions perm1,
            ForumPermissions perm2)
    {
        values[0] = perm1.get(0) || perm2.get(0);
        values[1] = perm1.get(1) || perm2.get(1);
        values[2] = perm1.get(2) || perm2.get(2);
        values[3] = perm1.get(3) || perm2.get(3);
        values[4] = perm1.get(4) || perm2.get(4);
        values[5] = perm1.get(5) || perm2.get(5);
        values[6] = perm1.get(6) || perm2.get(6);
        values[7] = perm1.get(7) || perm2.get(7);
        values[8] = perm1.get(8) || perm2.get(8);
    }

    public ForumPermissions(boolean [] permissions) {
        this.values = permissions;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (int i=0; i<values.length-1; i++) {
            buf.append(values[i]).append(",");
        }
        buf.append(values[values.length -1]);
        return buf.toString();
    }

    /**
     * Returns true if the permission of a particular type is allowed.
     */
    public boolean get(int type) {
        if (type < 0 || type > 8) {
            return false;
        }
        return values[type];
    }

    /**
     * Returns true if the permissions include system or forum admin
     * permissions.
     */
    public boolean isSystemOrForumAdmin() {
        return (values[FORUM_ADMIN] || values[SYSTEM_ADMIN]);
    }

    //FROM THE CACHEABLE INTERFACE//

    public int getSize() {
        //Approximate the size of the object in bytes by calculating the size
        //of each field.
        int size = 0;
        size += CacheSizes.sizeOfObject();              //overhead of object
        size += CacheSizes.sizeOfObject();              //ref to array
        size += CacheSizes.sizeOfBoolean() * 9;         //boolean array vals
        return size;
    }
}