/**
 * $RCSfile: ForumMessageNotFoundException.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:43 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

/**
 * Thrown if a nonexistant ForumMessage was attempting to be loaded.
 */
public class ForumMessageNotFoundException extends Exception {

    public ForumMessageNotFoundException() {
        super();
    }

    public ForumMessageNotFoundException(String msg) {
        super( msg );
    }
}
