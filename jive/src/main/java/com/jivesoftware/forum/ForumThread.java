/**
 * $RCSfile: ForumThread.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:43 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import java.util.Date;
import java.util.Iterator;

/**
 * A ForumThread is a container for a hierarchy of ForumMessages.<p>
 *
 * Intimately tied to the concept of a ForumThread is a root message. A
 * root message must be supplied when creating a thread. Subsequently, all
 * further messages posted to the thread are children of the root message.<p>
 *
 * To get a handle on a ForumThread object, the <code>getThread(long)</code> method
 * should be called from a Forum object. To create a thread, use the
 * <create>ForumFactory.createThread(ForumMessage)</code> method. After creating
 * a thread, you must attach it to a Forum by calling <code>Forum.addThread()</code>.
 * To delete a ForumThread, call the <code>Forum.deleteThread(long)</code> method.<p>
 *
 * There are two options for navigating through the messages in a thread.
 * <ul>
 *   <li>A TreeWalker -- this provides a hierarchical view of the messages in
 *      in the thread. For most skins, this will be the most appropriate
 *      navigation method.
 *   <li>An Iterator -- this provides a flat view of the messages in the thread.
 *      This view of thread is useful for skins that want to provide
 *      functionality such as listing all the messages in the order
 *      they were created, etc.
 * </ul>
 *
 * Because a root message must be passed in when creating a thread, you must
 * first create that message before creating the thread. The following code
 * snippet demonstrates:
 * <pre>
 * //Assume that a forum object and user object are already defined.
 * ForumMessage rootMessage = myForum.createMessage(myUser);
 * rootMessage.setSubject("A subject");
 * rootMessage.setBody("A body");
 * ForumThread myThread = myForum.createThread(rootMessage);
 * </pre>
 */
public interface ForumThread {

    /**
     * Returns the unique id of the thread.
     */
    public long getID();

    /**
     * Returns the subject of the root message of the thread. This is a
     * convenience method equivalent to <code>getRootMessage().getSubject()</code>.
     *
     * @return the name of the thread, which is the subject of the root message.
     */
    public String getName();

    /**
     * Returns the Date that the thread was created.
     *
     * @return the Date the thread was created.
     */
    public Date getCreationDate();

    /**
     * Sets the creation date of the thread. In most cases, the creation date
     * will default to when the thread was entered into the system. However,
     * the creation date needs to be set manually when importing data.
     * In other words, skin authors should ignore this method since it only
     * intended for system maintenance.
     *
     * @param creationDate the date the thread was created.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public void setCreationDate(Date creationDate) throws UnauthorizedException;

    /**
     * Returns the Date that the thread was last modified. In other words, the
     * date of the most recent message in the thread.
     */
    public Date getModifiedDate();

    /**
     * Sets the date the thread was last modified. In most cases, last modifed
     * will default to when the thread data was last changed. However,
     * the last modified date needs to be set manually when importing data.
     * In other words, skin authors should ignore this method since it only
     * intended for system maintenance.
     *
     * @param modifiedDate the date the thread was modified.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public void setModifiedDate(Date modifiedDate) throws UnauthorizedException;

    /**
     * Returns the number of moderation points the thread has. The default
     * moderation value is inherited from the forum.<p>
     *
     * If the moderation value is less than the forum's minimum, then the
     * thread will not be displayed by default.
     *
     * @return the number of moderation points the thread has.
     */
    public int getModerationValue();

    /**
     * Sets the number of moderation points the message has. If the moderation
     * value is less than the forum's minimum, then the thread will not be
     * displayed by default.<p>
     *
     * The authorization token of the user must be passed into this method as
     * a paramater for auditing purposes.
     *
     * @param value the number of moderation points for the thread.
     * @param auth the Authorization token of the user that is making the
     *      moderation decision.
     * @throws UnauthorizedException if does not have ADMIN or MODERATE_THREADS
     *      permissions.
     */
    public void setModerationValue(int value, Authorization auth)
            throws UnauthorizedException;

    /**
     * Returns an extended property of the thread. Each thread can have an
     * arbitrary number of extended properties. This allows for enhanced
     * functionality that is not part of the base interface.
     *
     * @param name the name of the property to get.
     * @param the value of the property specified by name.
     */
    public String getProperty(String name);

    /**
     * Sets an extended property of the thread. Each thread can have an
     * arbitrary number of extended properties. This allows for enhanced
     * functionality that is not part of the base interface.<p>
     *
     * The user that created the thread (author of rootMessage), or system or
     * forum admins can always call this method. Additionally, anyone (including
     * anonymous users) can call this method before the thread has been
     * added to a Forum.
     *
     * @param name the name of the property to set.
     * @param value the new value for the property.
     * @throws UnauthorizedException if not allowed to set extended properties.
     */
    public void setProperty(String name, String value) throws UnauthorizedException;

    /**
     * Deletes an extended property. If the property specified by
     * <code>name</code> does not exist, this method will do nothing.<p>
     *
     * The user that created the thread (author of rootMessage), or system or
     * forum admins can always call this method. Additionally, anyone (including
     * anonymous users) can call this method before the thread has been
     * added to a Forum.
     *
     * @param name the name of the property to delete.
     * @throws UnauthorizedException if not allowed to delete extended properties.
     */
    public void deleteProperty(String name) throws UnauthorizedException;

    /**
     * Returns an Iterator for the names of the thread properties.
     *
     * @return an Iterator for the names of the thread properties.
     */
    public Iterator propertyNames();

    /**
     * Returns the parent Forum of the thread.
     */
    public Forum getForum();

    /**
     * Returns a message from the thread based on its id.
     *
     * @param messageID the ID of the message to get from the thread.
     */
    public ForumMessage getMessage(long messageID)
            throws ForumMessageNotFoundException;

    /**
     * Returns the root message of a thread. The root message is a special
     * first message that is intimately tied to the thread for most forumViews.
     * All other messages in the thread are children of the root message.
     */
    public ForumMessage getRootMessage();

    /**
     * Returns the number of messages in the thread. This includes the root
     * message. So, to find the number of replies to the root message,
     * subtract one from the answer of this method.
     */
    public int getMessageCount();

    /**
     * Returns the number of messages in the thread based on the specified
     * result filter. This includes the root message. So, to find the number of
     * replies to the root message, subtract one from the answer of this method.
     *
     * @param resultFilter a filter to limit the message count query, such as
     *      date or moderation value range.
     */
    public int getMessageCount(ResultFilter resultFilter);

    /**
     * Adds a new message to the thread.
     *
     * @param parentMessage some message in the thread that will be parent
     * @param newMessage message to add to the thread under the parent
     * @throws UnauthorizedException if does not have CREATE_MESSAGE permissions.
     */
    public void addMessage(ForumMessage parentMessage, ForumMessage newMessage)
            throws UnauthorizedException;

    /**
     * Deletes a message in the thread. Deleting a message also deletes all
     * of its children messages. The search index and other resources that
     * referenced the message and its children will also be updated
     * appropriately.
     *
     * @throws IllegalArgumentException if the message does not belong to the
     *   thread.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public void deleteMessage(ForumMessage message)
            throws UnauthorizedException;

    /**
     * Returns a TreeWalker for the thread. A TreeWalker is used
     * to navigate through the tree of messages in a hierarchical manner.
     */
    public TreeWalker treeWalker();

    /**
     * Return an Iterator for all the messages in a thread.
     */
    public Iterator messages();

    /**
     * Returns a Iterator for all the messages in the thread that match the
     * criteria specified by the ResultFilter.
     *
     * @param resultFilter a ResultFilter object to perform filtering and
     *      sorting with.
     * @return an Iterator for the messages in the thread that match the
     *      ResultFilter.
     */
    public Iterator messages(ResultFilter resultFilter);

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
