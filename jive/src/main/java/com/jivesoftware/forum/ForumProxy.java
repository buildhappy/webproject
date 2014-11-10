/**
 * $RCSfile: ForumProxy.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:43 $
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
 * A protection proxy for Forums. A proxy has a set of permissions that are
 * specified at creation time of the proxy. Subsequently, those permissions
 * are use to restrict access to protected Forum methods. If a user does
 * not have the right to execute a particular method, an UnauthorizedException
 * is thrown.
 *
 * @see Forum
 * @see ForumPermissions
 * @see UnauthorizedException
 */
public class ForumProxy implements Forum {

    private Forum forum;
    private Authorization authorization;
    private ForumPermissions permissions;

    /**
     * Creates a new ForumProxy object.
     *
     * @param forum the forum to protect by proxy
     * @param authorization the user's authorization token
     * @param permissions the permissions to use with this proxy.
     */
    public ForumProxy(Forum forum, Authorization authorization,
            ForumPermissions permissions)
    {
        this.forum = forum;
        this.authorization = authorization;
        this.permissions = permissions;
    }

    //FROM THE FORUM INTERFACE//

    public long getID() {
        return forum.getID();
    }

    public String getName() {
        return forum.getName();
    }

    public void setName(String name) throws UnauthorizedException,
            ForumAlreadyExistsException
    {
        if (permissions.isSystemOrForumAdmin()) {
            forum.setName(name);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public String getDescription() {
        return forum.getDescription();
    }

    public void setDescription(String description)
            throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin()) {
            forum.setDescription(description);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public Date getCreationDate() {
        return forum.getCreationDate();
    }

    public void setCreationDate(Date creationDate)
            throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin()) {
            forum.setCreationDate(creationDate);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public Date getModifiedDate() {
        return forum.getModifiedDate();
    }

    public void setModifiedDate(Date modifiedDate)
            throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin()) {
            forum.setModifiedDate(modifiedDate);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public int getModerationDefaultThreadValue() {
        return forum.getModerationDefaultThreadValue();
    }

    public void setModerationDefaultThreadValue(int value)
            throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin() ||
                permissions.get(ForumPermissions.MODERATE_THREADS))
        {
            forum.setModerationDefaultThreadValue(value);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public int getModerationDefaultMessageValue() {
        return forum.getModerationDefaultMessageValue();
    }

    public void setModerationDefaultMessageValue(int value)
            throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin() ||
                permissions.get(ForumPermissions.MODERATE_MESSAGES))
        {
            forum.setModerationDefaultMessageValue(value);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public int getModerationMinThreadValue() {
        return forum.getModerationMinThreadValue();
    }

    public void setModerationMinThreadValue(int value)
            throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin() ||
                permissions.get(ForumPermissions.MODERATE_THREADS))
        {
            forum.setModerationMinThreadValue(value);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public int getModerationMinMessageValue() {
        return forum.getModerationMinMessageValue();
    }

    public void setModerationMinMessageValue(int value)
            throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin() ||
                permissions.get(ForumPermissions.MODERATE_MESSAGES))
        {
            forum.setModerationMinMessageValue(value);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public String getProperty(String name) {
        return forum.getProperty(name);
    }

    public void setProperty(String name, String value)
            throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin()) {
            forum.setProperty(name, value);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void deleteProperty(String name)
            throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin()) {
            forum.deleteProperty(name);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public Iterator propertyNames() {
        return forum.propertyNames();
    }

    public void deleteThread(ForumThread thread) throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin() ||
                permissions.get(ForumPermissions.MODERATE_THREADS))
        {
            forum.deleteThread(thread);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void moveThread(ForumThread thread, Forum newForum) throws
            UnauthorizedException, IllegalArgumentException
    {
        // If the user is an admin or moderator of both forums
        boolean forum1Perm = permissions.isSystemOrForumAdmin() ||
                permissions.get(ForumPermissions.MODERATE_THREADS);
        boolean forum2Perm = newForum.hasPermission(ForumPermissions.SYSTEM_ADMIN) ||
                newForum.hasPermission(ForumPermissions.FORUM_ADMIN) ||
                newForum.hasPermission(ForumPermissions.MODERATE_THREADS);
        if (forum1Perm && forum2Perm)
        {
            forum.moveThread(thread, newForum);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void addThread(ForumThread thread) throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin() ||
                permissions.get(ForumPermissions.CREATE_THREAD))
        {
            forum.addThread(thread);
        }
        else {
            throw new UnauthorizedException("Create thread permissions are " +
                "necessary to perform this task"
            );
        }
    }

    public ForumThread getThread(long threadID) throws ForumThreadNotFoundException
    {
        ForumThread thread = forum.getThread(threadID);
        // Apply protection proxy and return.
        return new ForumThreadProxy(thread, authorization, permissions);
    }

    public ForumThreadIterator threads() {
        ForumThreadIterator iterator = forum.threads();
        return new ForumThreadIteratorProxy(iterator, authorization, permissions);
    }

    public ForumThreadIterator threads(ResultFilter resultFilter) {
        ForumThreadIterator iterator = forum.threads(resultFilter);
        return new ForumThreadIteratorProxy(iterator, authorization, permissions);
    }

    public Iterator popularThreads() {
        Iterator iterator = forum.popularThreads();
        return new IteratorProxy(JiveGlobals.THREAD, iterator, authorization, permissions);
    }

    public Iterator messages() {
        Iterator iterator = forum.messages();
        return new IteratorProxy(JiveGlobals.MESSAGE, iterator, authorization, permissions);
    }

    public Iterator messages(ResultFilter resultFilter) {
        Iterator iterator = forum.messages(resultFilter);
        return new IteratorProxy(JiveGlobals.MESSAGE, iterator, authorization, permissions);
    }

    public int getThreadCount() {
        return forum.getThreadCount();
    }

    public int getThreadCount(ResultFilter resultFilter) {
        return forum.getThreadCount(resultFilter);
    }

    public int getMessageCount() {
        return forum.getMessageCount();
    }

    public int getMessageCount(ResultFilter resultFilter) {
        return forum.getMessageCount(resultFilter);
    }

    public Query createQuery() {
        return new QueryProxy(forum.createQuery(), authorization, permissions);
    }

    public FilterManager getFilterManager() {
        return new FilterManagerProxy(forum.getFilterManager(), authorization,
                permissions);
    }

    //public GatewayManager getGatewayManager() throws UnauthorizedException {
      //  if (permissions.isSystemOrForumAdmin()) {
            //return forum.getGatewayManager();
        //}
        //else {
            //throw new UnauthorizedException();
        //}
    //}

    public PermissionsManager getPermissionsManager()
            throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin()) {
            return new PermissionsManagerProxy(forum.getPermissionsManager(),
                authorization, permissions);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public ForumPermissions getPermissions(Authorization authorization) {
        return forum.getPermissions(authorization);
    }

    public boolean hasPermission(int type) {
        return permissions.get(type);
    }

    public String toString() {
        return forum.toString();
    }

    /**
     * Returns the forum class that the proxy wraps. In some cases,
     * this is necessary so that an outside class can get at methods that only
     * a particular forum sublclass contains. Because this is
     * potentially a dangerours operation, access to the underlying class is
     * restricted to those with SYSTEM_ADMIN permissions.
     *
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public Forum getProxiedForum()
            throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            return forum;
        }
        else {
            throw new UnauthorizedException();
        }
    }
}