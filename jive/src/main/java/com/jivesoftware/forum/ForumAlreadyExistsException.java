/**
 * $RCSfile: ForumAlreadyExistsException.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:41 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

/**
 * Thrown when a forum is attempted to be created with the same name as an
 * existing forum.
 */
public class ForumAlreadyExistsException extends Exception {

    public ForumAlreadyExistsException() {
        super();
    }

    public ForumAlreadyExistsException(String msg) {
        super(msg);
    }
}
