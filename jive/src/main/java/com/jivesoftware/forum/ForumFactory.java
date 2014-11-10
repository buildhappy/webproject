/**
 * $RCSfile: ForumFactory.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:42 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import java.lang.reflect.*;
import java.util.*;

/**
 * A ForumFactory provides access to and management of Forums. It is the point
 * of entry for the entire Jive system.
 * <p>
 * A concrete instance of ForumFactory can be obtained by calling the getInstance()
 * method with an Authorization token. The Authorization token determines with
 * what permissions the rest of the objects in the system will be accessed with.
 * <p>
 * Usually the first steps of any program interacting with the Jive system are:
 * <ul>
 *   <li> Obtain an authorization token by calling
 *    AuthorizationFactory.getInstance().getAuthorization(username, password);
 *   <li> Use that authorization to get a ForumFactory instance.
 *   <li> Use the forum factory to access forums and other Jive content.
 * </ul>
 * It is also possible to access Jive content with anonymous permissions. See
 * the AuthorizationFactory class for more information.
 * <p>
 * ForumFactory is an abstract class so that the actual implementation is
 * pluggable. For example, the default Jive implementation uses a database
 * backend. You can optionally plug in your own backend that might use the
 * filesystem, for example. When first creating the forum factory, Jive will
 * look for the Jive property "ForumFactory.className". If it fails to find
 * that property, it will use the default class.
 *
 * @see AuthorizationFactory
 */
public abstract class ForumFactory {

    private static Object initLock = new Object();
    private static String className = "com.jivesoftware.forum.database.DbForumFactory";
    private static ForumFactory factory = null;

    /**
     * Returns a concrete ForumFactory instance. Permissions corresponding
     * to the Authorization will be used. If getting the factory fails, null
     * will be returned.
     *
     * @param authorization the auth token for the user.
     * @return a concrete ForumFactory instance.
     */
    public static ForumFactory getInstance(Authorization authorization) {
        //If no valid authorization passed in, return null.
        if (authorization == null) {
            return null;
        }
        if (factory == null) {
            synchronized(initLock) {
                if (factory == null) {
                    // Note, the software license expressely forbids
                    // tampering with this check.
                    //LicenseManager.validateLicense("Jive Forums Basic", "2.0");

                    String classNameProp =
                        JiveGlobals.getJiveProperty("ForumFactory.className");
                    if (classNameProp != null) {
                        className = classNameProp;
                    }
                    try {
                        //Load the class and create an instance.
                        Class c = Class.forName(className);
                        factory = (ForumFactory)c.newInstance();
                    }
                    catch (Exception e) {
                        System.err.println("Failed to load ForumFactory class "
                            + className + ". Jive cannot function normally.");
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        //Now, create a forum factory proxy.
        return new ForumFactoryProxy(authorization, factory,
                factory.getPermissions(authorization));
    }

    /**
     * Creates a new forum. This method should always be used instead of
     * trying to instantiate a forum directly.
     *
     * @param name the name of the forum.
     * @param description the description of the forum.
     * @throws UnauthorizedException if not allowed to create a Forum.
     * @throws ForumAlreadExistsException if the forum name already exists.
     */
    public abstract Forum createForum(String name, String description)
            throws UnauthorizedException, ForumAlreadyExistsException;

    /**
     * Factory method to create a new thread. A root message must supplied
     * when creating the thread. Therefore, the following pattern should
     * be applied to add a new thread to a forum:<ul>
     *      <li>Create a new message, and set each of its properties such as
     *          the subject and body.
     *      <li>Create a new thread using the root message just created.
     *      <li>Set any properties on the new thread.
     *      <li>Add the thread to the forum by calling the addThread(ForumThread)
     *          method.</ul>
     *
     * Note that creating the ForumThread object is only one step of the process.
     * You must also add the thread to the forum with the addThread(ForumThread)
     * method.
     *
     * @param forum the forum to create the thread in.
     * @param rootMessage the root message of the thread.
     * @return a new ForumThread object.
     * @throws UnauthorizedException if does not have CREATE_THREAD permissions.
     */
    public abstract ForumThread createThread(ForumMessage rootMessage) throws
            UnauthorizedException;

    /**
     * Factory method to create a message with an anonymous author.
     */
    public abstract ForumMessage createMessage();

    /**
     * Factory method to create a message.
     *
     * @param user the author of the message.
     * @throws UnauthorizedException if does not have  permissions.
     */
    public abstract ForumMessage createMessage(User user)
            throws UnauthorizedException;

    /**
     * Returns the forum with the specified forumID.
     *
     * @param forumID the id of the forum to return.
     * @return the Forum specified by forumID.
     * @throws UnauthorizedException if not allowed to read the forum.
     * @throws ForumNotFoundException if the requested forum does not exist.
     */
    public abstract Forum getForum(long forumID)
            throws ForumNotFoundException, UnauthorizedException;

    /**
     * Returns the Forum with the specified name.
     *
     * @param name the name of the forum to return.
     * @return the forum with the specified name.
     * @throws ForumNotFoundException if the requested forum does not exist.
     * @throws UnauthorizedException if not allowed to read the forum.
     */
    public abstract Forum getForum(String name)
            throws ForumNotFoundException, UnauthorizedException;

    /**
     * Returns the total number of forums. This number might not agree
     * with the number of forums returned by ForumFactory.forums() since that
     * method return an Iterator of forums that a user has READ access for.
     *
     * @return the total number of forums.
     */
    public abstract int getForumCount();

    /**
     * Returns an Iterator of Forum objects for all the forums in the system
     * that the user has READ access for. Read access can be granted in the
     * following ways:
     * <ul>
     *   <li> Anonymous read permission is enabled for the forum; anyone can
     *        read it.
     *   <li> The "all users" read permission is set so that any registered
     *        user can read the forum.
     *   <li> The user belongs to a group that has been granted read permission.
     *   <li> The user has been specifically granted read permission.
     *   <li> The current user is a system admin or admin of this forum. This
     *        allows automatic read permission.
     * </ul>
     *
     * @return an Iterator of Forum objects for all forums in the system that
     *    the user has read permission for.
     */
    public abstract Iterator forums();

    /**
     * Creates a query object to search through all forums. Note: only forums
     * that the user calling this method has read access for will be included
     * in the search.
     *
     * @return a Query object that can be used to search all forums.
     */
    public abstract Query createQuery();

    /**
     * Creates a query object to search through the specified list of forums.
     *
     * @return a Query object that can be used to search the specified forums.
     */
    public abstract Query createQuery(Forum [] forums);

    /**
     * Returns an Iterator for the most popular forums in the system. Popular
     * forums are defined as those that have the most new messages over a
     * period of time (e.g. the last 24 hours).
     *
     * A number of configurable Jive properties control how the popular forums
     * feature works:<ul>
     *      <li> <code>popularForums.number</code> -- The maximum number of
     *          forums that will be returned in the list of popular forums.
     *          Default is 4.
     *      <li> <code>popularForums.timeWindow</code> -- The number of hours
     *          to consider new messages in a thread from. Default is 24 hours,
     *          which is suitable for sites with moderate to high amounts of
     *          traffic. Sites with less traffic may wish to set the window
     *          to a number of days. </ul>
     *
     * @return an Iterator for the most popular forums.
     */
    public abstract Iterator popularForums();

    /**
     * Returns an Iterator for the most threads in the system. Popular
     * threads are defined as those that have the most new messages over a
     * period of time (e.g. the last 24 hours).
     *
     * A number of configurable Jive properties control how the popular threads
     * feature works:<ul>
     *      <li> <code>popularThreads.number</code> -- The number of
     *          threads that will be returned in the list of popular threads.
     *          Default is 4.
     *      <li> <code>popularThreads.timeWindow</code> -- The number of hours
     *          to consider new messages in a thread from. Default is 24 hours,
     *          which is suitable for sites with moderate to high amounts of
     *          traffic. Sites with less traffic may wish to set the window
     *          to a number of days. </ul>
     *
     * @return an Iterator for the most popular forums.
     */
    public abstract Iterator popularThreads();

    /**
     * Deletes a forum and all of its content. This method is not always
     * guaranteed to be safe to call. For example, if multiple clients have
     * handles on a forum, and that forum is subsequently deleted, the behavior
     * of the forum objects that the clients have handles on is unspecified and
     * may result in errors.
     *
     * @param forum the forum to delete.
     * @throws UnauthorizedException if not allowed to delete a forum.
     */
    public abstract void deleteForum(Forum forum)
            throws UnauthorizedException;

    /**
     * Merges the content of two forums by copying the threads from
     * <tt>forum2</tt> to <tt>forum1</tt>. <tt>forum2</tt> will be deleted after
     * all content is merged.<p>
     *
     * You may only execute this operation if you are a system administrator.
     *
     * @param forum1 the Forum to merge threads into.
     * @param forum2 the Forum to move all threads from.
     */
    public abstract void mergeForums(Forum forum1, Forum forum2)
            throws UnauthorizedException;

    /**
     * Returns a UserManager that can be used to manage users.
     *
     * @return a user manager.
     * @see UserManager
     */
    public abstract UserManager getUserManager();

    /**
     * Returns a GroupManager that can be used to manage groups.
     *
     * @return a group manager.
     * @see GroupManager
     */
    public abstract GroupManager getGroupManager();

    /**
     * Returns the search manger which can be used to manage the index used
     * by Jive to perform searches.
     *
     * @throws UnauthorizedException if not a system administator.
     * @return a search manager.
     */
    public abstract SearchManager getSearchManager()
            throws UnauthorizedException;

    /**
     * Returns a filter manager that can be used to manage system wide filters.
     *
     * @return a FilterManager to manage system wide filters.
     */
    public abstract FilterManager getFilterManager();

    /**
     * Returns a watch manager that can be used to manage thread watches.
     *
     * @return a WatchManager to manage thread watches.
     */
    public abstract WatchManager getWatchManager();

    /**
     * Returns a reward manager that can be used to manage reward points.
     *
     * @return a RewardManager to manage reward points.
     */
    public abstract RewardManager getRewardManager();

    /**
     * Returns a permissions manager that can be used to set system wide
     * permissions. Only system admins can perform this function.
     *
     * @return a PermissionsManager to manage the system permissions.
     * @throws UnauthorizedException if not a system admin.
     */
    public abstract PermissionsManager getPermissionsManager()
            throws UnauthorizedException;

    /**
     * Returns an array of ForumMessageFilter objects that represent all the
     * currently available filters in the system. This list is useful when
     * using the FilterManager class.
     *
     * @return an array of all available message filters in the system.
     * @throws UnauthorizedException if not a system admin.
     */
    public abstract ForumMessageFilter [] getAvailableFilters()
            throws UnauthorizedException;

    /**
     * Installs a new class into the list of available filters for the system.
     * Exceptions are thrown if you're not a system admin, the class can't be
     * loaded from the classpath, or the class isn't an instance of
     * ForumMessageFilter.
     *
     * @param className the fully qualified name of the class to add to the list
     *      of available filters in the system.
     * @throws UnauthorizedException if not a system admin
     * @throws ClassNotFoundException if the class could not be loaded.
     * @throws IllegalArgumentException if the class is not a filter or could
     *      not be instantiated.
     */
    public abstract void addFilterClass(String className)
            throws UnauthorizedException, ClassNotFoundException,
            IllegalArgumentException;

    /**
     * Returns the permissions for the factory that correspond to the
     * passed-in Authorization.
     *
     * @param authorization the auth token for the user.
     * @return the permissions for this object.
     */
    public abstract ForumPermissions getPermissions(Authorization authorization);

    /**
     * Returns true if the handle on the object has the permission specified.
     * A list of possible permissions can be found in the ForumPermissions
     * class. Certain methods of this class are restricted to certain
     * permissions as specified in the method comments.
     *
     * @param type the type of permission to check for.
     * @see ForumPermissions
     */
    public abstract boolean hasPermission(int type);
}