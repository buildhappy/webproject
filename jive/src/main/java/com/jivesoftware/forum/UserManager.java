/**
 * $RCSfile: UserManager.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:47 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import java.util.Iterator;
import java.util.Map;

/**
 * Centralized management of users in the Jive system including creating,
 * retrieving, and deleting User objects. A number of methods to retrieve
 * content based on users are also provided. For more advanced content queries,
 * please see the ResultFilter class, which allows a userID to be set for
 * forum content queries like "show me all threads created by user XYZ over
 * the last 30 days".
 *
 * @see User
 * @see ResultFilter
 */
public interface UserManager {

    /**
     * Factory method for creating a new User with all required values: a
     * password, email address, and unique username.
     *
     * @param username the new and unique username for the account.
     * @param password the password for the account as plain text.
     * @param email the email address for the account.
     * @return a new User.
     * @throws UserAlreadyExistsException if the username already exists in
     *      the system.
     */
    public User createUser(String username, String password, String email)
            throws UserAlreadyExistsException;

    /**
     * Factory method for creating a new User with all required AND optional
     * values.
     *
     * @param username the new and unique username for the account.
     * @param password the password for the account as plain text.
     * @param name the name for the account.
     * @param email the email address for the account.
     * @param nameVisible true if the user's name should be visible to others.
     * @param emailVisible true if the user's email address should be visible
     *      to others.
     * @param properties the user's extended properties.
     * @return a new User.
     * @throws UserAlreadyExistsException if the username already exists in
     *      the system.
     */
    public User createUser(String username, String password, String name,
            String email, boolean nameVisible, boolean emailVisible,
            Map properties) throws UserAlreadyExistsException;

    /**
     * Returns a User specified by their ID.
     *
     * @param userid the id of the User to lookup.
     * @return the User specified by <tt>userID</tt>.
     * @throws UserNotFoundException if the user does not exist.
     */
    public User getUser(long userID) throws UserNotFoundException;

    /**
     * Returns the User specified by username.
     *
     * @throws UserNotFoundException if the user does not exist.
     * @param username the username of the user.
     * @return the User that matches <tt>username</tt>.
     */
    public User getUser(String username) throws UserNotFoundException;

    /**
     * Returns the userID specified by the username. This method is only
     * useful in specialized cases, as its generally easier to call
     * <tt>getUser(username).getID()</tt> instead of this method.
     *
     * @throws UserNotFoundException if the user does not exist.
     * @param username the username of the usedr.
     * @return the userID that matches <tt>username</tt>.
     */
    public long getUserID(String username) throws UserNotFoundException;

    /**
     * Deletes a user. To maintain data consistency, deleting a user will
     * cause all messages created by the user to switch to anonymous postings.
     *
     * @param user the user to delete.
     * @throws UnauthorizedException
     */
    public void deleteUser(User user) throws UnauthorizedException;

    /**
     * Returns the numer of users in the system.
     *
     * @return the total number of users.
     */
    public int getUserCount();

    /**
     * Retruns an iterator for all users in the system.
     *
     * @return an Iterator for all users.
     */
    public Iterator users();

    /**
     * Returns an iterator for all users starting at <tt>startIndex</tt> with the
     * given number of results. This is useful to support pagination in a GUI
     * where you may only want to display a certain number of results per page.
     * It is possible that the number of results returned will be less than
     * that specified by numResults if numResults is greater than the number
     * of records left in the system to display.
     *
     * @param startIndex the beginning index to start the results at.
     * @param numResults the total number of results to return.
     * @return an Iterator for all users in the specified range.
     */
    public Iterator users(int startIndex, int numResults);

    /**
     * Returns the total number of messages a user has posted in all forums.
     * Warning: forums that the current user doesn't have read access for will
     * also be included in the total, so you should use caution if that information
     * should be private.<p>
     *
     * In order to get a count of user messages in an individual forum, please
     * use the Forum.getMessageCount(ResultFilter) method.
     *
     * @param user the user to tally results for.
     * @return the number of messages the user has posted.
     */
    public int userMessageCount(User user);

    /**
     * Returns an iterator for all messages posted by a user. Only messages
     * that the caller of this method has permission to read will be returned.
     * Therefore, it's possible that the number of messages returned in this
     * iterator will be less than the number returned by userMessageCount(user).
     */
    public Iterator userMessages(User user,int start, int count);
}
