/**
 * $RCSfile: ForumFactoryProxy.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:42 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import java.util.Iterator;
import java.util.ArrayList;

/**
 * A protection proxy for ForumFactory. It ensures that only authorized users
 * are allowed to access restricted methods.
 */
public class ForumFactoryProxy extends ForumFactory {

    protected ForumFactory factory;
    protected Authorization authorization;
    protected ForumPermissions permissions;

    public ForumFactoryProxy(Authorization authorization, ForumFactory factory,
            ForumPermissions permissions)
    {
        this.factory = factory;
        this.authorization = authorization;
        this.permissions = permissions;
    }

    public Forum createForum(String name, String description)
            throws UnauthorizedException, ForumAlreadyExistsException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            Forum newForum = factory.createForum(name, description);
            return new ForumProxy(newForum, authorization, permissions);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public ForumThread createThread(ForumMessage rootMessage)
            throws UnauthorizedException
    {
        ForumThread thread = factory.createThread(rootMessage);
        return new ForumThreadProxy(thread, authorization, permissions);
    }

    public ForumMessage createMessage() {
        ForumMessage message = factory.createMessage();
        return new ForumMessageProxy(message, authorization, permissions);
    }

    public ForumMessage createMessage(User user)
            throws UnauthorizedException
    {
        //The user must be the actual user in order to post as that user.
        //Otherwise, throw an exception.
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
                user.getID() == authorization.getUserID())
        {
            ForumMessage message = factory.createMessage(user);
            return new ForumMessageProxy(message, authorization, permissions);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void deleteForum(Forum forum) throws UnauthorizedException {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            factory.deleteForum(forum);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void mergeForums(Forum forum1, Forum forum2)
            throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            factory.mergeForums(forum1, forum2);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public Forum getForum(long forumID) throws ForumNotFoundException,
            UnauthorizedException
    {
        Forum forum = factory.getForum(forumID);
        ForumPermissions forumPermissions = forum.getPermissions(authorization);
        //Create a new permissions object with the combination of the
        //permissions of this object and tempPermissions.
        ForumPermissions newPermissions =
                new ForumPermissions(permissions, forumPermissions);
        //Check and see if the user has READ permissions. If not, throw an
        //an UnauthorizedException.
        if (!(
            newPermissions.get(ForumPermissions.READ) ||
            newPermissions.get(ForumPermissions.FORUM_ADMIN) ||
            newPermissions.get(ForumPermissions.SYSTEM_ADMIN)
            ))
        {
            throw new UnauthorizedException();
        }
        return new ForumProxy(forum, authorization, newPermissions);
    }

    public Forum getForum(String name) throws ForumNotFoundException,
            UnauthorizedException
    {
        Forum forum = factory.getForum(name);
        ForumPermissions forumPermissions = forum.getPermissions(authorization);
        //Create a new permissions object with the combination of the
        //permissions of this object and tempPermissions.
        ForumPermissions newPermissions =
                new ForumPermissions(permissions, forumPermissions);
        //Check and see if the user has READ permissions. If not, throw an
        //an UnauthorizedException.
        if (!(
            newPermissions.get(ForumPermissions.READ) ||
            newPermissions.get(ForumPermissions.FORUM_ADMIN) ||
            newPermissions.get(ForumPermissions.SYSTEM_ADMIN)
            ))
        {
            throw new UnauthorizedException();
        }
        return new ForumProxy(forum, authorization, newPermissions);
    }

    public int getForumCount() {
        return factory.getForumCount();
    }

    public Iterator forums() {
        return new IteratorProxy(JiveGlobals.FORUM, factory.forums(),
                authorization, permissions);
    }

    public Query createQuery() {
        // Special implementation of this method so that we can determine the
        // actual list of forums that the user has permissions to search over.
        ArrayList forumList = new ArrayList();
        for (Iterator iter = forums(); iter.hasNext(); ) {
            forumList.add(iter.next());
        }
        Forum [] forums = new Forum[forumList.size()];
        for (int i=0; i<forums.length; i++) {
            forums[i] = (Forum)forumList.get(i);
        }
        return createQuery(forums);
    }

    public Query createQuery(Forum [] forums) {
        return new QueryProxy(factory.createQuery(forums), authorization,
                permissions);
    }

    public Iterator popularForums() {
        return new IteratorProxy(JiveGlobals.FORUM, factory.popularForums(),
                authorization, permissions);
    }

    public Iterator popularThreads() {
        return new IteratorProxy(JiveGlobals.THREAD, factory.popularThreads(),
                authorization, permissions);
    }

    public UserManager getUserManager() {
        UserManager userManager = factory.getUserManager();
        return new UserManagerProxy(userManager, authorization, permissions);
    }

    public GroupManager getGroupManager() {
        GroupManager groupManager = factory.getGroupManager();
        return new GroupManagerProxy(groupManager, authorization, permissions);
    }

    public SearchManager getSearchManager() throws UnauthorizedException {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            return factory.getSearchManager();
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public FilterManager getFilterManager() {
        return new FilterManagerProxy(factory.getFilterManager(), authorization,
                permissions);
    }

    public WatchManager getWatchManager() {
        return new WatchManagerProxy(factory.getWatchManager(), authorization,
                permissions);
    }

    public RewardManager getRewardManager() {
       return new RewardManagerProxy(factory.getRewardManager(), authorization,
                permissions);
    }

    public PermissionsManager getPermissionsManager()
            throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            return new PermissionsManagerProxy(factory.getPermissionsManager(),
                authorization, permissions);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public ForumMessageFilter [] getAvailableFilters()
            throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            return factory.getAvailableFilters();
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void addFilterClass(String className) throws UnauthorizedException,
            ClassNotFoundException, IllegalArgumentException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            factory.addFilterClass(className);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public ForumPermissions getPermissions(Authorization authorization) {
        return factory.getPermissions(authorization);
    }

    public boolean hasPermission(int type) {
        return permissions.get(type);
    }

    /**
     * Returns the forum factory class that the proxy wraps. In some cases,
     * this is necessary so that an outside class can get at methods that only
     * a particular forum factory sublclass contains. Because this is
     * potentially a dangerours operation, access to the underlying class is
     * restricted to those with SYSTEM_ADMIN permissions.
     *
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public ForumFactory getProxiedForumFactory()
            throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            return factory;
        }
        else {
            throw new UnauthorizedException();
        }
    }
}