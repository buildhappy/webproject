/**
 * $RCSfile: IteratorProxy.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:44 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import java.util.*;

/**
 * Protection proxy for Iterators of forums, threads, messages, users, and
 * groups. The class is also smart enough to skip over elements in the Iterator
 * that the user doesn't have permission to read.
 */
public class IteratorProxy implements Iterator {

    private Iterator iterator;
    private Object nextElement = null;

    private Authorization authorization;
    private ForumPermissions permissions;
    private ProxyFactory proxyFactory;

    /**
     * Creates a new iterator proxy.
     *
     * @param type the type of object to be proxied. Must be a valid value from
     *      JiveGlobals.
     * @param iterator the Iterator to create proxies for.
     * @param authorization the authorization.
     * @param permissions the permissions that the new proxy will inherit.
     */
    public IteratorProxy(int type, Iterator iterator, Authorization authorization,
            ForumPermissions permissions)
    {
        this.iterator = iterator;
        this.authorization = authorization;
        this.permissions = permissions;

        // Load the appropriate proxy factory depending on the type of object
        // that we're iterating through. Each proxy factory is responsible
        // for checking that the user has permission to view the object, and
        // then wrapping it with an appropriate proxy.
        switch (type) {
            // FORUM
            case JiveGlobals.FORUM:
                // Create a class that wraps forums with proxies.
                proxyFactory = new ProxyFactory() {
                    public Object createProxy(Object obj, Authorization auth,
                            ForumPermissions perms)
                    {
                        Forum forum = (Forum)obj;
                        ForumPermissions forumPerms = forum.getPermissions(auth);
                        // Create a new permissions object with the combination
                        // of the permissions of this object and tempPermissions.
                        ForumPermissions newPerms = new ForumPermissions(
                                perms, forumPerms);
                        // Return the object if the user has permission.
                        if (newPerms.get(ForumPermissions.READ) ||
                            newPerms.get(ForumPermissions.FORUM_ADMIN) ||
                            newPerms.get(ForumPermissions.SYSTEM_ADMIN))
                        {
                            return new ForumProxy(forum, auth, newPerms);
                        }
                        // Otherwise return null.
                        else {
                            return null;
                        }
                    }
                };
                break;
            // THREAD
            case JiveGlobals.THREAD:
                // Create a class that wraps threads with proxies.
                proxyFactory = new ProxyFactory() {

                    // It's very possible that we're loading threads from a
                    // global permissions context rather than from inside a
                    // forum (for example, when we iterate over threads from
                    // multiple different forums). In that case, we need to
                    // load up the permissions for the forum and combine them
                    // with the global permissions before determining if the
                    // user has read access for the thread. As a way to speed
                    // up this check, we store the last forumID that permissions
                    // were loaded for.
                    long forumID = -1;
                    ForumPermissions forumPerms = null;

                    public Object createProxy(Object obj, Authorization auth,
                            ForumPermissions perms)
                    {
                        ForumThread thread = (ForumThread)obj;
                        // If we don't already have the forumPerms defined
                        // for the forum that the message belongs to, load them.
                        Forum forum = thread.getForum();
                        if (forum.getID() != forumID) {
                            forumPerms = new ForumPermissions(perms,
                                    forum.getPermissions(auth));
                        }
                        // Create a proxy
                        thread = new ForumThreadProxy(thread, auth, forumPerms);
                        // Check that the user has read permission for the thread.
                        if (thread.hasPermission(ForumPermissions.READ) ||
                            thread.hasPermission(ForumPermissions.FORUM_ADMIN) ||
                            thread.hasPermission(ForumPermissions.SYSTEM_ADMIN))
                        {
                            return thread;
                        }
                        else {
                            return null;
                        }
                    }
                };
                break;
            // MESSAGE
            case JiveGlobals.MESSAGE:
                // Create a class that wraps messages with proxies.
                proxyFactory = new ProxyFactory() {

                    // It's very possible that we're loading messages from a
                    // global permissions context rather than from inside a
                    // forum (for example, when we iterate over messages from
                    // multiple different forums). In that case, we need to
                    // load up the permissions for the forum and combine them
                    // with the global permissions before determining if the
                    // user has read access for the message. As a way to speed
                    // up this check, we store the last forumID that permissions
                    // were loaded for.
                    long forumID = -1;
                    ForumPermissions forumPerms = null;

                    public Object createProxy(Object obj, Authorization auth,
                            ForumPermissions perms)
                    {
                        ForumMessage message = (ForumMessage)obj;
                        // If we don't already have the forumPerms defined
                        // for the forum that the message belongs to, load them.
                        Forum forum = message.getForumThread().getForum();
                        if (forum.getID() != forumID) {
                            forumPerms = new ForumPermissions(perms,
                                    forum.getPermissions(auth));
                        }
                        // Create a proxy
                        message = new ForumMessageProxy(message, auth, forumPerms);
                        // Check that the user has read permission for the message.
                        if (message.hasPermission(ForumPermissions.READ) ||
                            message.hasPermission(ForumPermissions.FORUM_ADMIN) ||
                            message.hasPermission(ForumPermissions.SYSTEM_ADMIN))
                        {
                            return message;
                        }
                        else {
                            return null;
                        }
                    }
                };
                break;
            // USER
            case JiveGlobals.USER:
                // Create a class that wraps users with proxies.
                proxyFactory = new ProxyFactory() {
                    public Object createProxy(Object obj, Authorization auth,
                            ForumPermissions perms)
                    {
                        User user = (User)obj;
                        ForumPermissions userPerms =
                            user.getPermissions(auth);
                        ForumPermissions newPerms =
                            new ForumPermissions(perms, userPerms);
                        return new UserProxy(user, auth, newPerms);
                    }
                };
                break;
            // GROUP
            case JiveGlobals.GROUP:
                // Create a class that wraps groups with proxies.
                proxyFactory = new ProxyFactory() {
                    public Object createProxy(Object obj, Authorization auth,
                            ForumPermissions perms)
                    {
                        Group group = (Group)obj;
                        ForumPermissions groupPerms =
                            group.getPermissions(auth);
                        ForumPermissions newPerms =
                            new ForumPermissions(perms, groupPerms);
                        return new GroupProxy(group, auth, newPerms);
                    }
                };
                break;
            // Otherwise, an invalid value was passed in so throw an exception.
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Returns true if there are more elements in the iteration.
     *
     * @return true if the iterator has more elements.
     */
    public boolean hasNext() {
        // If we are at the end of the list, there can't be any more elements
        // to iterate through.
        if (!iterator.hasNext() && nextElement == null) {
            return false;
        }
        // Otherwise, see if nextElement is null. If so, try to load the next
        // element to make sure it exists.
        if (nextElement == null) {
            nextElement = getNextElement();
            if (nextElement == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the next element.
     *
     * @return the next element.
     * @throws NoSuchElementException if there are no more elements.
     */
    public Object next() throws java.util.NoSuchElementException {
        Object element = null;
        if (nextElement != null) {
            element = nextElement;
            nextElement = null;
        }
        else {
            element = getNextElement();
            if (element == null) {
                throw new java.util.NoSuchElementException();
            }
        }
        return element;
    }

    /**
     * Not supported for security reasons.
     */
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the next available element, or null if there
     * are no more elements.
     *
     * @return the next available element.
     */
    public Object getNextElement() {
        while (iterator.hasNext()) {
            Object element = proxyFactory.createProxy(iterator.next(),
                    authorization, permissions);
            if (element != null) {
                return element;
            }
        }
        return null;
    }
}

/**
 * An interface that defines a method to create proxy objects based on an
 * authorization and permissions.
 */
interface ProxyFactory {

    /**
     * Creates a new proxy for <tt>obj</tt> using the specified authorization
     * and permissions, or returns null if the user doesn't have permission to
     * read the object.
     *
     * @return a new proxy.
     */
    public Object createProxy(Object obj, Authorization auth, ForumPermissions perms);
}