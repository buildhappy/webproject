/**
 * DbAuthorization.java
 * November 17, 2000
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import com.jivesoftware.forum.Authorization;
import java.io.Serializable;

/**
 * Database implementation of the Authorization interface.
 */
public final class DbAuthorization implements Authorization, Serializable {

    private long userID;

    /**
     * Constucts a new DbAuthorization with the specified userID.
     *
     * @param userID the userID to create an authorization token with.
     */
    protected DbAuthorization(long userID) {
        this.userID = userID;
    }

    //FROM THE AUTHORIZATION INTERFACE//

    public long getUserID() {
        return userID;
    }

    public boolean isAnonymous() {
        return userID == -1;
    }
}