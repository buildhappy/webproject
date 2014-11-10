/**
 * $RCSfile: Forum.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:41 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

//import com.jivesoftware.forum.gateway.GatewayManager;

import java.util.Date;
import java.util.Iterator;
import java.util.Enumeration;

/**
 * A top level container for discussions. It contains a list of threads, each
 * of which contains a tree of messages.
 */
public interface Forum {

    /**
     * Returns the unique id of the forum.
     *
     * @return the unique id of the forum.
     */
    public long getID();

    /**
     * Returns the name of the forum. Every forum name in the system must be
     * unique. This restriction allows one to lookup a forum by name
     * as well as by ID.
     *
     * @return the name of the forum.
     * @see ForumFactory#getForum(String)
     */
    public String getName();

    /**
     * Sets the name of a the forum. Every forum name in the system must be
     * unique. However, this restriction allows one to lookup a forum by name
     * as well as by ID.<p>
     *
     * An exception will be thrown if a forum with the same name as the new
     * name already exists.
     *
     * @param name the name of the forum.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     * @throws ForumAlreadyExistsException if a forum with the specified name
     *      already exists.
     */
    public void setName(String name) throws UnauthorizedException,
            ForumAlreadyExistsException;

    /**
     * Returns the description of the forum. A description should normally be
     * no longer than thirty words so that it can be easily displayed in a list
     * of forums on a web page.
     *
     * @return the description of the forum.
     */
    public String getDescription();

    /**
     * Sets the description of the forum. A description should normally be
     * no longer than thirty words so that it can be easily displayed in a list
     * of forums on a web page.
     *
     * @param description the description of the forum.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public void setDescription(String description) throws UnauthorizedException;

    /**
     * Returns the Date that the forum was created.
     *
     * @return the Date the forum was created.
     */
    public Date getCreationDate();

    /**
     * Sets the creation date of the forum. In most cases, the creation date
     * will default to when the forum was entered into the system. However,
     * the creation date needs to be set manually when importing data.
     * In other words, skin authors should ignore this method since it only
     * intended for system maintenance.
     *
     * @param creationDate the date the forum was created.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public void setCreationDate(Date creationDate) throws UnauthorizedException;

    /**
     * Returns the Date that the forum was last modified. In other words, the
     * date of the most recent message or thread in the forum.
     *
     * @return the Date the forum was last modified.
     */
    public Date getModifiedDate();

    /**
     * Sets the date the forum was last modified. In most cases, last modifed
     * will default to when the forum data was last changed. However,
     * the last modified date needs to be set manually when importing data.
     * In other words, skin authors should ignore this method since it only
     * intended for system maintenance.
     *
     * @param modifiedDate the date the forum was modified.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public void setModifiedDate(Date modifiedDate) throws UnauthorizedException;

    /**
     * Returns the default number of moderation points for threads created in
     * the forum. The default moderation points value can be used in
     * conjunction with the minimum thread moderation points value to
     * effectively turn moderation on or off. Consider a default value of 0 and
     * a minimum value of 1 -- this will mean that new threads will
     * be created below the display threshold of 1, and must be assigned a
     * moderation point before being visible.<p>
     *
     * By default, the minimum and default thread moderation value is 1.
     *
     * @return the default number of moderation points assigned to threads.
     */
    public int getModerationDefaultThreadValue();

    /**
     * Sets the default number of moderation points for threads created in the
     * forum. The default moderation points value can be used in
     * conjunction with the minimum thread moderation points value to
     * effectively turn moderation on or off. Consider a default value of 0 and
     * a minimum value of 1 -- this will mean that new threads will
     * be created below the display threshold of 1, and must be assigned a
     * moderation point before being visible.<p>
     *
     * By default, the minimum and default thread moderation value is 1.
     *
     * @param value default number of moderation points for threads.
     * @throws UnauthorizedException if does not have ADMIN or MODERATE_THREADS
     *      permissions.
     */
    public void setModerationDefaultThreadValue(int value) throws UnauthorizedException;

    /**
     * Returns the default number of moderation points for messages created in
     * the forum. The default moderation points value can be used in
     * conjunction with the minimum message moderation points value to
     * effectively turn moderation on or off. Consider a default value of 0 and
     * a minimum value of 1 -- this will mean that new messages will
     * be created below the display threshold of 1, and must be assigned a
     * moderation point before being visible.<p>
     *
     * By default, the minimum and default message moderation value is 1.
     *
     * @return the default number of moderation points assigned to messages.
     */
    public int getModerationDefaultMessageValue();

    /**
     * Sets the default number of moderation points for threads created in the
     * forum. The default moderation points value can be used in
     * conjunction with the minimum thread moderation points value to
     * effectively turn moderation on or off. Consider a default value of 0 and
     * a minimum value of 1 -- this will mean that new messages will
     * be created below the display threshold of 1, and must be assigned a
     * moderation point before being visible.<p>
     *
     * By default, the minimum and default message moderation value is 1.
     *
     * @param value default number of moderation points for messages.
     * @throws UnauthorizedException if does not have ADMIN or MODERATE_MESSAGES
     *      permissions.
     */
    public void setModerationDefaultMessageValue(int value) throws UnauthorizedException;

    /**
     * Returns the minimum number of moderation points necessary for threads to
     * be visible in the forum. The default moderation points value can be used in
     * conjunction with the minimum thread moderation points value to
     * effectively turn moderation on or off. Consider a default value of 0 and
     * a minimum value of 1 -- this will mean that new threads will
     * be created below the display threshold of 1, and must be assigned a
     * moderation point before being visible.<p>
     *
     * By default, the minimum and default thread moderation value is 1.
     *
     * @return the minimum number of moderation points for threads to be visible.
     */
    public int getModerationMinThreadValue();

    /**
     * Sets the default number of moderation points necessary for threads to be
     * visible in the forum. The default moderation points value can be used in
     * conjunction with the minimum thread moderation points value to
     * effectively turn moderation on or off. Consider a default value of 0 and
     * a minimum value of 1 -- this will mean that new messages will
     * be created below the display threshold of 1, and must be assigned a
     * moderation point before being visible.<p>
     *
     * By default, the minimum and default message moderation value is 1.
     *
     * @param value the minimum number of moderation points for threads to be
     *      visible.
     * @throws UnauthorizedException if does not have ADMIN or MODERATE_THREADS
     *      permissions.
     */
    public void setModerationMinThreadValue(int value) throws UnauthorizedException;

    /**
     * Returns the minimum number of moderation points necessary for messages to
     * be visible in the forum. The default moderation points value can be used in
     * conjunction with the minimum thread moderation points value to
     * effectively turn moderation on or off. Consider a default value of 0 and
     * a minimum value of 1 -- this will mean that new threads will
     * be created below the display threshold of 1, and must be assigned a
     * moderation point before being visible.<p>
     *
     * By default, the minimum and default thread moderation value is 1.
     *
     * @return the minimum number of moderation points for threads to be visible.
     */
    public int getModerationMinMessageValue();

    /**
     * Sets the default number of moderation points necessary for messages to be
     * visible in the forum. The default moderation points value can be used in
     * conjunction with the minimum thread moderation points value to
     * effectively turn moderation on or off. Consider a default value of 0 and
     * a minimum value of 1 -- this will mean that new messages will
     * be created below the display threshold of 1, and must be assigned a
     * moderation point before being visible.<p>
     *
     * By default, the minimum and default message moderation value is 1.
     *
     * @param value the minimum number of moderation points for messages to be
     *      visible.
     * @throws UnauthorizedException if does not have ADMIN or MODERATE_MESSAGES
     *      permissions.
     */
    public void setModerationMinMessageValue(int value) throws UnauthorizedException;

    /**
     * Returns an extended property of the forum. Each forum can have an
     * arbitrary number of extended properties. This allows for enhanced
     * functionality that is not part of the base interface.
     *
     * @param name the name of the property to get.
     * @param the value of the property specified by name.
     */
    public String getProperty(String name);

    /**
     * Sets an extended property of the forum. Each forum can have an
     * arbitrary number of extended properties. This allows for enhanced
     * functionality that is not part of the base interface.<p>
     *
     * If the property referenced by <code>name</code> already exists, its
     * value will be updated.
     *
     * @param name the name of the property to set.
     * @param value the new value for the property.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public void setProperty(String name, String value) throws UnauthorizedException;

    /**
     * Deletes an extended property. If the property specified by
     * <code>name</code> does not exist, this method will do nothing.
     *
     * @param name the name of the property to delete.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public void deleteProperty(String name) throws UnauthorizedException;

    /**
     * Returns an Iterator for the names of the forum properties.
     *
     * @return an Iterator for the names of the forum properties.
     */
    public Iterator propertyNames();

    /**
     * Returns the thread specified by id. The method will return null
     * if the thread is not in the forum.
     *
     * @param threadID the id of the thread to get.
     * @return the ForumThread corresponding to <code>threadID</code>
     * @throws ForumThreadNotFoundException if the specified thread cannot be
     *      loaded.
     */
    public ForumThread getThread(long threadID)
            throws ForumThreadNotFoundException;

    /**
     * Adds a thread to the forum.
     *
     * @param thread the thread to add to the forum.
     * @throws UnauthorizedException if does not have CREATE_THREAD permissions.
     */
    public void addThread(ForumThread thread) throws UnauthorizedException;

    /**
     * Deletes a thread and all of its messages. Once a thread is
     * deleted, the thread object should no longer be used. The search index and
     * other resources that referenced the thread and its messages will also be
     * updated appropriately.
     *
     * @param thread the thread to delete.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public void deleteThread(ForumThread thread) throws UnauthorizedException;

    /**
     * Moves a thread from one forum to another. For this to work, the thread
     * must exist in the forum that this method is invoked on, and the user
     * calling this method must have ADMIN permissions for the forum this method
     * is invoked on and <code>newForum</code>.<p>
     *
     * The main purpose of this method is to allow admins to move non-topical
     * threads into a more appropriate forum.
     *
     * @param thread the thread to move to another forum.
     * @param newForum the forum to move the thread to.
     * @throws UnauthorizedException if does not have ADMIN permissions for the
     *      this forum and <code>newForum</code>.
     * @throws IllegalArgumentException if <code>thread</code> does not belong
     *      to the forum that this method was invoked on.
     */
    public void moveThread(ForumThread thread, Forum newForum)
            throws UnauthorizedException, IllegalArgumentException;

    /**
     * Returns a Iterator for all the threads in the forum. Threads will be
     * sorted on their modified date.
     *
     * @return an Iterator for the threads in the forum.
     */
    public ForumThreadIterator threads();

    /**
     * Returns a Iterator for all the threads in the forum that match the
     * criteria specified by the ResultFilter.
     *
     * @param resultFilter a ResultFilter object to perform filtering and
     *      sorting with.
     * @return an Iterator for the threads in the forum that match the
     *      ResultFilter.
     */
    public ForumThreadIterator threads(ResultFilter resultFilter);

    /**
     * Returns an Iterator for the most popular threads in the forum. Popular
     * threads are defined as those that have the most new messages over a
     * period of time (e.g. the last 24 hours). When two threads have
     * identical numbers of new messages, newer threads will be ordered first.<p>
     *
     * A number of configurable Jive properties control how the popular threads
     * feature works:<ul>
     *      <li> <code>popularThreads.numThreads</code> -- The number of
     *          messages that will be returned in the list of popular threads.
     *          Default is 4 threads.
     *      <li> <code>popularThreads.timeWindow</code> -- The number of hours
     *          to consider new messages in a thread from. Default is 24 hours,
     *          which is suitable for sites with moderate to high amounts of
     *          traffic. Sites with less traffic may wish to set the window
     *          to a number of days. </ul>
     *
     * @return an Iterator for the most popular threads in the forum.
     */
    public Iterator popularThreads();

    /**
     * Returns an Iterator for all the messages in the forum.
     *
     * @return an Iterator for the messages in the forum.
     */
    public Iterator messages();

    /**
     * Returns a Iterator for all the messages in the forum that match the
     * criteria specified by the ResultFilter.
     *
     * @param resultFilter a ResultFilter object to perform filtering and
     *      sorting with.
     * @return an Iterator for the messages in the forum that match the
     *      ResultFilter.
     */
    public Iterator messages(ResultFilter resultFilter);

    /**
     * Returns the number of threads in the forum.
     *
     * @return the number of threads in the forum.
     */
    public int getThreadCount();

    /**
     * Returns the number of threads in the forum based on the specified
     * ResultFilter. This is useful for determining such things as the number
     * of threads in a date range, etc.
     *
     * @param resultFilter a resultFilter to limit the query on.
     * @return the number of threads in the forum based on the filter.
     */
     public int getThreadCount(ResultFilter resultFilter);

    /**
     * Returns the number of messages in the forum.
     *
     * @return the number of messages in the forum.
     */
    public int getMessageCount();

    /**
     * Returns the number of messages in the forum based on the specified
     * ResultFilter. This is useful for determining such things as the number
     * of messages in a date range, etc.
     *
     * @param resultFilter a resultFilter to limit the query on.
     * @return the number of messages in the forum based on the filter.
     */
     public int getMessageCount(ResultFilter resultFilter);

    /**
     * Creates a query object to search the forum.
     *
     * @return a Query object that can be used to search the forum.
     */
    public Query createQuery();

    /**
     * Returns a filter manager that can be used to manage the filters for this
     * forum. Filter management is only for forum or system admins, excluding
     * the {@link FilterManager#applyFilters(ForumMessage)} method, which may
     * be useful to skin writers.
     *
     * @return a FilterManager to manage the filters for this forum.
     */
    public FilterManager getFilterManager();

    /**
     * Returns a gateway manager that can be used to manage the gateways for
     * this forum. Only system or forum admins can perform this function.
     *
     * @return a GatewayManager to manage the gateways for this forum.
     * @throws UnauthorizedException if not a forum or system admin.
     */
//    public GatewayManager getGatewayManager() throws UnauthorizedException;

    /**
     * Returns a permissions manager that can be used to set permissions for
     * this forum. Only system or forum admins can perform this function.
     *
     * @return a PermissionsManager to manage the permissions on this forum.
     * @throws UnauthorizedException is not a forum or system admin.
     */
    public PermissionsManager getPermissionsManager()
            throws UnauthorizedException;

    /**
     * Returns the permissions for the forum that correspond to the
     * passed-in Authorization.
     *
     * @param authorization the auth token to lookup permissions for.
     */
    public abstract ForumPermissions getPermissions(Authorization authorization);

    /**
     * Returns true if the handle on the object has the permission specified.
     * A list of possible permissions can be found in the ForumPermissions
     * class. Certain methods of this class are restricted to certain
     * permissions as specified in the method comments.
     *
     * @see ForumPermissions
     */
    public boolean hasPermission(int type);
}
