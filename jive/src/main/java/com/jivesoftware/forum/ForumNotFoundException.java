/**
 * $RCSfile: ForumNotFoundException.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:43 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

public class ForumNotFoundException extends Exception {

    public ForumNotFoundException() {
        super();
    }

    public ForumNotFoundException(String msg) {
        super( msg );
    }
}
