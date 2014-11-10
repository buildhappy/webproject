/**
 * $RCSfile: Authorization.java,v $
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
 * Proves that a user has successfully logged in. The existence of an
 * Authorization object indicates that a person has logged in correctly
 * and has authentication to act as the user associated with the
 * authentication. An instance of this object can be obtained from an
 * AuthorizationFactory and must be passed in to to get an intstance of
 * ForumFactory.
 * <p>
 * In the case of using the core forum services through a web interface, the
 * expected behavior is to have a user login and then store the Authorization
 * object in their session. In some app servers, all objects put in the session
 * must be seriablizable. The default Authorization implementation obeys this
 * rule, but ensure that custom Authorization classes do as well.
 *
 * @see AuthorizationFactory
 * @see ForumFactory
 */
public interface Authorization {

    /**
     * Returns the userID associated with this Authorization.
     *
     * @return the userID associated with this Authorization.
     */
    public long getUserID();

    /**
     * Returns true if this Authorization is the Anonymous authorization token.
     *
     * @return true if this token is the anonymous Authorization.
     */
    public boolean isAnonymous();
}