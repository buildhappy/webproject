/**
 * $RCSfile: ForumThreadProxy.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:44 $
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
 * Protection proxy for ForumThread objects. It restricts access to protected
 * methods by throwing UnauthorizedExceptions if the user does not have
 * permission to access the class.
 */
public class ForumThreadProxy implements ForumThread {

    private ForumThread thread;
    private Authorization authorization;
    private ForumPermissions permissions;

    /**
     * Creates a new proxy.
     */
    public ForumThreadProxy(ForumThread thread, Authorization authorization,
            ForumPermissions permissions)
    {
        this.thread = thread;
        this.authorization = authorization;
        this.permissions = permissions;
    }

    public long getID() {
        return thread.getID();
    }

    public String getName() {
        return thread.getName();
    }

    public Date getCreationDate() {
        return thread.getCreationDate();
    }

    public void setCreationDate(Date creationDate)
            throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin()) {
            thread.setCreationDate(creationDate);
        }
        else throw new UnauthorizedException();
    }

    public Date getModifiedDate() {
        return thread.getModifiedDate();
    }

    public void setModifiedDate(Date modifiedDate)
            throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin()) {
            thread.setModifiedDate(modifiedDate);
        }
        else throw new UnauthorizedException();
    }

    public int getModerationValue() {
        return thread.getModerationValue();
    }

    public void setModerationValue(int value, Authorization auth)
            throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin() ||
                permissions.get(ForumPermissions.MODERATE_THREADS))
        {
            thread.setModerationValue(value, auth);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public String getProperty(String name) {
        return thread.getProperty(name);
    }

    public void setProperty(String name, String value)
            throws UnauthorizedException
    {
        if (isAllowedToEdit()) {
            thread.setProperty(name, value);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void deleteProperty(String name)
            throws UnauthorizedException
    {
        if (isAllowedToEdit()) {
            thread.deleteProperty(name);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public Iterator propertyNames() {
        return thread.propertyNames();
    }

    public Forum getForum() {
        Forum forum = thread.getForum();
        if (forum == null) {
            return null;
        }
        else {
            return new ForumProxy(forum, authorization, permissions);
        }
    }

    public int getMessageCount() {
        return thread.getMessageCount();
    }

    public int getMessageCount(ResultFilter resultFilter) {
        return thread.getMessageCount(resultFilter);
    }

    public ForumMessage getRootMessage() {
        ForumMessage message = thread.getRootMessage();
        return new ForumMessageProxy(message, authorization, permissions);
    }

    public void addMessage(ForumMessage parentMessage, ForumMessage newMessage)
            throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin() ||
                permissions.get(ForumPermissions.CREATE_MESSAGE))
        {
            thread.addMessage(parentMessage, newMessage);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void deleteMessage(ForumMessage message)
            throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin() ||
                permissions.get(ForumPermissions.MODERATE_THREADS) ||
                permissions.get(ForumPermissions.MODERATE_MESSAGES))
        {
            thread.deleteMessage(message);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public ForumMessage getMessage(long messageID)
            throws ForumMessageNotFoundException
    {
        ForumMessage message = thread.getMessage(messageID);
        // Apply the protection proxy and return message.
        return new ForumMessageProxy(message, authorization, permissions);
    }

    public TreeWalker treeWalker() {
        return new TreeWalkerProxy(thread.treeWalker(), authorization, permissions);
    }

    public Iterator messages() {
        Iterator iterator = thread.messages();
        return new IteratorProxy(JiveGlobals.MESSAGE, iterator, authorization, permissions);
    }

    public Iterator messages(ResultFilter resultFilter) {
        Iterator iterator = thread.messages(resultFilter);
        return new IteratorProxy(JiveGlobals.MESSAGE, iterator, authorization, permissions);
    }

    public boolean hasPermission(int type) {
        return permissions.get(type);
    }

    public String toString() {
        return thread.toString();
    }

    /**
     * Allow access to the underlying thread object under two conditions: system
     * or forum admin, or the thread hasn't been added to a forum yet.
     */
    public ForumThread getProxiedForumThread() throws UnauthorizedException {
        if (permissions.isSystemOrForumAdmin() || getForum() == null) {
            return thread;
        }
        else {
            throw new UnauthorizedException();
        }
    }

    /**
     * Returns true if editing of this thread is allowed under the current
     * permissions. See the ForumThread class for details about thread editing
     * policies.
     */
    private boolean isAllowedToEdit() {
        // Admins or moderators can always edit threads.
        if (permissions.isSystemOrForumAdmin() ||
                permissions.get(ForumPermissions.MODERATE_THREADS)) {
            return true;
        }
        ForumMessage rootMessage = getRootMessage();
        // Authors of the thread can always edit.
        if (!rootMessage.isAnonymous() && rootMessage.getUser().getID() ==
                authorization.getUserID())
        {
            return true;
        }
        // Anonymous threads (anonymous root message) can only be edited before
        // being added to a forum.
        if (rootMessage.getUser() == null && getForum() == null) {
            return true;
        }
        // If all previous checks, not allowed to edit.
        return false;
    }
}