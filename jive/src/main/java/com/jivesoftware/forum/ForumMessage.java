/**
 * $RCSfile: ForumMessage.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:42 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import java.util.*;

/**
 * A ForumMessage encapsulates message data. Each message belongs to a thread,
 * and relates to other messages in a thread in a tree relationship. This system
 * allows messages to represent threaded conversations. For example:
 *
 * <pre>
 *   [thread]
 *        |- [message]
 *        |- [message]
 *                 |- [message]
 *                 |- [message]
 *                          |- [message]
 *        |- [message]
 * </pre><p>
 *
 * Each message has a subject and body. Messages are authored by a user
 * in the system or can be anonymous. An ID is given to each message so that
 * it can be tracked uniquely. Because is possible that one might want to store
 * more information with each message besides a subject and body,
 * each message can have an arbitrary number of properties. For example, a
 * property "IPAddress" could be stored with each message that records the IP
 * address of the person posting the message for security reasons.<p>
 *
 * The creation date, and the date the message was last modified are maintained
 * for each message. These values are normally maintained automatically by
 * Jive and do not need to be set explicitly except in special circumstances.<p>
 *
 * Message editing has a number of rules:<ul>
 *  <li>System or forum administrators can edit any message at any time.
 *  <li>The message author can edit a message at any time.
 *  <li>Anonymous messages can be edited by anonyone before being added to
 *      a thread. After being added to a thread, only system or forum admins
 *      can edit them.</ul>
 *  Of course, these message editing rules can be made more strict by a skin.
 *  For example, you may choose to never allow anyone to edit messages, or only
 *  messages to be edited for the first fifteen minutes after they are posted.<p>
 *
 * For added functionality, any number of filters can be applied to a message.
 * Filters dynamically format the subject and body of a message. Methods are
 * also provided to bypass filters.
 *
 * @see ForumMessageFilter
 */
public interface ForumMessage {

    /**
     * Returns the id of the message.
     *
     * @return the unique id of the message.
     */
    public long getID();

    /**
     * Returns the date the message was created.
     *
     * @return the date the message was created.
     */
    public Date getCreationDate();

    /**
     * Sets the creation date of the message. In most cases, the creation date
     * will default to when the message was entered into the system. However,
     * the creation date needs to be set manually when importing messages.
     * In other words, skin authors should ignore this method since it only
     * intended for system maintenance.
     *
     * @param creationDate the date the message was created.
     *
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public void setCreationDate(Date creationDate) throws UnauthorizedException;

    /**
     * Returns the date the message was last modified. When a message is first
     * created, the date returned by this method is identical to the creation
     * date. The modified date is updated every time a message property is
     * updated, such as the message body.
     *
     * @return the date the message was last modified.
     */
    public Date getModifiedDate();

    /**
     * Sets the date the message was last modified. In most cases, last modifed
     * will default to when the message data was last changed. However,
     * the last modified date needs to be set manually when importing messages.
     * In other words, skin authors should ignore this method since it only
     * intended for system maintenance.
     *
     * @param modifiedDate the date the message was modified.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public void setModifiedDate(Date modifiedDate) throws UnauthorizedException;

    /**
     * Returns the message subject. If message filters are active, the
     * subject returned will be a filtered one. Because filters often provide
     * security functionality, this method is the preferred way to get the
     * subject of a message.
     *
     * @return the subject of the message.
     */
    public String getSubject();

    /**
     * Returns the message subject, bypassing any active filters. Because
     * filters often provide security, this method should be used with caution.
     * In particular, you should avoid showing unfiltered data in an environment
     * where embedded HTML might be interpreted.<p>
     *
     * Unfiltered content is necessary for a few reasons. One is when saving
     * Jive content to another persistence mechanism such as an XML format.
     * Another is when you need to skip filter formatting, such as when a user
     * is responding to another user's message.
     *
     * @return the subject of the message.
     */
    public String getUnfilteredSubject();

    /**
     * Sets the subject of the message.
     *
     * @param subject the subject of the message.
     * @throws UnauthorizedException if not allowed to edit the message.
     */
    public void setSubject(String subject) throws UnauthorizedException;

    /**
     * Returns the message body. If message filters are active, the body
     * returned will be a filtered one. Because filters often provide security
     * functionality such as stripping out HTML and Javascript, this method is
     * the preferred way to get the body of a message.
     *
     * @return the body of the message.
     */
    public String getBody();

    /**
     * Returns the message body, bypassing any active filters. Because filters
     * often provide security, this method should be used with caution. In
     * particular, you should avoid showing unfiltered data in an environment
     * where embedded HTML might be interpreted.<p>
     *
     * Unfiltered content is necessary for a few reasons. One is when saving
     * Jive content to another persistence mechanism such as an XML format.
     * Another is when you need to skip filter formatting, such as when a user
     * is responding to another user's message.
     *
     * @return the body of the message.
     */
    public String getUnfilteredBody();

    /**
     * Sets the body of the message.
     *
     * @param body the body of the message.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public void setBody(String body) throws UnauthorizedException;

    /**
     * Returns the User that authored the message. If the message was created
     * anonymously, the Anonymous User object will be returned.
     *
     * @return the author of the message.
     */
    public User getUser();

    /**
     * Returns the number of moderation points the message has. The default
     * moderation value is inherited from the forum.<p>
     *
     * If the moderation value is less than the forum's minimum, then the
     * message will not be displayed by default.
     *
     * @return the number of moderation points the message has.
     */
    public int getModerationValue();

    /**
     * Sets the number of moderation points the message has. If the moderation
     * value is less than the forum's minimum, then a number of things will
     * happen:<ul>
     *      <li>The message will not be visible in the thread by default.
     *      <li>The thread and forum of the message will not have their modified
     *          date's updated.
     *      <li>The message will not be exported to installed gateways.
     *      <li>Watch updates will not be triggered.</ul><p>
     *
     * When calling this method results in a message to go from below the
     * forum's minimum moderation threshold to above, then the actions listed
     * above will all be executed. Any change of the moderation value also
     * results in the modified date of the message being updated.<p>
     *
     * Only administrators and moderators can call this method. The two types
     * of moderators are thread moderators and message moderators. Either type
     * of moderator can call this method since a thread moderator is also
     * automatically a message moderator.<p>
     *
     * The authorization token of the user must be passed into this method as
     * a paramater for moderation auditing purposes.
     *
     * @param value the number of moderation points for the message.
     * @param auth the Authorization token of the user that is making the
     *      moderation decision.
     * @throws UnauthorizedException if does not have ADMIN, MODERATE_MESSAGES
     *      or MODERATE_THREADS permissions.
     *
     * @see com.jivesoftware.forum.gateway.Gateway
     * @see WatchManager
     */
    public void setModerationValue(int value, Authorization auth)
            throws UnauthorizedException;

    /**
     * Returns an extended property of the message. Each message can have an
     * arbitrary number of extended properties. This lets particular skins
     * or filters provide enhanced functionality that is not part of the base
     * interface.<p>
     *
     * For security reasons, you should enable an HTML filter in case properties
     * contain malicious HTML code.
     *
     * @param name the name of the property to get.
     * @return the value of the property.
     */
    public String getProperty(String name);

    /**
     * Returns an extended property of the message, bypassing any filters.
     * Each message can have an arbitrary number of extended properties. This
     * lets particular skins or filters provide enhanced functionality that is
     * not part of the base interface.<p>
     *
     * Because properties are not filtered before being returned, this method
     * should be used with caution. In particular, you should avoid showing
     * unfiltered data in an environment where embedded HTML might be
     * interpreted.
     *
     * @param name the name of the property to get.
     * @return the value of the property.
     */
    public String getUnfilteredProperty(String name);

    /**
     * Sets an extended property of the message. Each message can have an
     * arbitrary number of extended properties. This lets particular skins
     * or filters provide enhanced functionality that is not part of the base
     * interface.
     *
     * @param name the name of the property to set.
     * @param value the new value for the property.
     * @throws UnauthorizedException if not allowed to edit the message.
     */
    public void setProperty(String name, String value) throws UnauthorizedException;

    /**
     * Deletes an extended property. If the property specified by
     * <code>name</code> does not exist, this method will do nothing.
     *
     * @param name the name of the property to delete.
     * @throws UnauthorizedException if not allowed to edit the message.
     */
    public void deleteProperty(String name) throws UnauthorizedException;

    /**
     * Returns an Iterator for all the names of the message properties.
     *
     * @return an Iterator for the names of all message properties.
     */
    public Iterator propertyNames();

    /**
     * Returns true if the message was posted anonymously. This is a convenience
     * method for: <tt>message.getUser() != null</tt>.
     *
     * @return true if the message was posted anonymously.
     */
    public boolean isAnonymous();

    /**
     * Returns the thread the message belongs to.
     *
     * @return the thread the message belongs to.
     */
    public ForumThread getForumThread();

    /**
     * Returns true if the handle on the object has the permission specified.
     * A list of possible permissions can be found in the ForumPermissions
     * class. Certain methods of this class are restricted to certain
     * permissions as specified in the method comments.
     *
     * @param type a permission type.
     * @return true if the specified permission is valid.
     * @see ForumPermissions
     */
    public boolean hasPermission(int type);
}

