/**
 * $RCSfile: GroupNotFoundException.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:44 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

public class GroupNotFoundException extends Exception {

    public GroupNotFoundException() {
        super();
    }

    public GroupNotFoundException( String msg ) {
        super(msg);
    }
}
