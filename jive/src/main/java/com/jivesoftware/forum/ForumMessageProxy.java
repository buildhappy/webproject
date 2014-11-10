/**
 * $RCSfile: ForumMessageProxy.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:43 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import java.util.*;

/**
 * A protection proxy for ForumMessage objects.
 */
public class ForumMessageProxy implements ForumMessage {

    private ForumMessage message;
    private Authorization authorization;
    private ForumPermissions permissions;

    /**
     * Creates a new ForumMessageProxy to protect the supplied message with
     * the specified permissions
     */
    public ForumMessageProxy(ForumMessage message, Authorization authorization,
            ForumPermissions permissions)
    {
        this.message = message;
        this.authorization = authorization;
        this.permissions = permissions;
    }

    //FROM THE FORUMMESSAGE INTERFACE//

    public long getID() {
        return message.getID();
    }

    public Date getCreationDate() {
        return message.getCreationDate();
    }

    public void setCreationDate(Date creationDate)
            throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin()) {
            this.message.setCreationDate(creationDate);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public Date getModifiedDate() {
        return message.getModifiedDate();
    }

    public void setModifiedDate(Date modifiedDate)
            throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin()) {
            this.message.setModifiedDate(modifiedDate);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public String getSubject() {
        return message.getSubject();
    }

    public String getUnfilteredSubject() {
        return message.getUnfilteredSubject();
    }

    public void setSubject(String subject) throws UnauthorizedException  {
        if (isAllowedToEdit()) {
            this.message.setSubject(subject);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public String getBody() {
        return message.getBody();
    }

    public String getUnfilteredBody() {
        return message.getUnfilteredBody();
    }

    public void setBody(String body) throws UnauthorizedException {
        if (isAllowedToEdit()) {
            this.message.setBody(body);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public User getUser() {
        User user = message.getUser();
        if (user == null) {
            return null;
        }
        ForumPermissions userPermissions = user.getPermissions(authorization);
        ForumPermissions newPermissions =
                new ForumPermissions(permissions, userPermissions);
        return new UserProxy(user, authorization, newPermissions);
    }

    public int getModerationValue() {
        return message.getModerationValue();
    }

    public void setModerationValue(int value, Authorization auth)
            throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin() ||
                permissions.get(ForumPermissions.MODERATE_MESSAGES))
        {
            message.setModerationValue(value, auth);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public String getProperty(String name) {
        return message.getProperty(name);
    }

    public String getUnfilteredProperty(String name) {
        return message.getUnfilteredProperty(name);
    }

    public void setProperty(String name, String value)
            throws UnauthorizedException
    {
        if (isAllowedToEdit()) {
            message.setProperty(name, value);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void deleteProperty(String name)
            throws UnauthorizedException
    {
        if (isAllowedToEdit()) {
            message.deleteProperty(name);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public Iterator propertyNames() {
        return message.propertyNames();
    }

    public boolean isAnonymous() {
        return message.isAnonymous();
    }

    public ForumThread getForumThread() {
        return message.getForumThread();
    }

    public boolean hasPermission(int type) {
        return permissions.get(type);
    }

    //OTHER METHODS//

    /**
     * Converts the object to a String by returning the subject of the message.
     * This functionality is primarily for Java applications that might be
     * accessing CoolForum objects through a GUI.
     */
    public String toString() {
        return message.toString();
    }

    /**
     * Allow access to the underlying message object under two conditions:
     * system or forum admin, or the message hasn't been added to a thread yet.
     */
    public ForumMessage getProxiedForumMessage()
            throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin() || getForumThread() == null) {
            return message;
        }
        else {
            throw new UnauthorizedException();
        }
    }

    /**
     * Returns true if editing of this message is allowed under the current
     * permissions. See the ForumMessage class for details about message editing
     * policies.
     */
    private boolean isAllowedToEdit() {
        // Admins or moderators can always edit messages.
        if (permissions.isSystemOrForumAdmin() ||
                permissions.get(ForumPermissions.MODERATE_THREADS) ||
                permissions.get(ForumPermissions.MODERATE_MESSAGES))
        {
            return true;
        }
        // Authors of the message can always edit.
        if (!isAnonymous() && getUser().getID() == authorization.getUserID()) {
            return true;
        }
        // Anonymous messages can only be edited before being added to a thread.
        if (getUser() == null && getForumThread() == null) {
            return true;
        }
        // If all previous checks, not allowed to edit.
        return false;
    }
}