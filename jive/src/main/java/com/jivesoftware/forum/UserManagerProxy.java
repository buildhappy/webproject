/**
 * $RCSfile: UserManagerProxy.java,v $
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
 * Protection proxy for the UserManager class. It restricts access to
 * protected methods by throwing UnauthorizedExceptions when necessary.
 *
 * @see UserManager
 */
public class UserManagerProxy implements UserManager {

    private UserManager userManager;
    private Authorization authorization;
    private ForumPermissions permissions;

    /**
     * Creates a new UserManagerProxy.
     */
    public UserManagerProxy(UserManager userManager, Authorization
            authorization, ForumPermissions permissions)
    {
        this.userManager = userManager;
        this.authorization = authorization;
        this.permissions = permissions;
    }

    public User createUser(String username, String password, String email)
            throws UserAlreadyExistsException
    {
        return userManager.createUser(username, password, email);
    }

    public User createUser(String username, String password, String name,
            String email, boolean nameVisible, boolean emailVisible,
            Map properties) throws UserAlreadyExistsException
    {
         return userManager.createUser(username, password, name, email,
                nameVisible, emailVisible, properties);
    }

    public User getUser(long userID) throws UserNotFoundException {
        User user = userManager.getUser(userID);
        ForumPermissions userPermissions = user.getPermissions(authorization);
        ForumPermissions newPermissions =
                new ForumPermissions(permissions, userPermissions);
        return new UserProxy(user, authorization, newPermissions);
    }

    public User getUser(String username) throws UserNotFoundException {
        User user = userManager.getUser(username);
        ForumPermissions userPermissions = user.getPermissions(authorization);
        ForumPermissions newPermissions =
                new ForumPermissions(permissions, userPermissions);
        return new UserProxy(user, authorization, newPermissions);
    }

    public long getUserID(String username) throws UserNotFoundException {
        return userManager.getUserID(username);
    }

    public void deleteUser(User user) throws UnauthorizedException {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            userManager.deleteUser(user);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public int getUserCount() {
        return userManager.getUserCount();
    }

    public Iterator users() {
        Iterator iterator = userManager.users();
        return new IteratorProxy(JiveGlobals.USER, iterator, authorization,
                permissions);
    }

    public Iterator users(int startIndex, int numResults) {
        Iterator iterator = userManager.users(startIndex, numResults);
        return new IteratorProxy(JiveGlobals.USER, iterator, authorization,
                permissions);
    }

    public int userMessageCount(User user) {
        return userManager.userMessageCount(user);
    }

    public Iterator userMessages(User user,int start, int count) {
        Iterator iterator = userManager.userMessages(user,start,count);
        return new IteratorProxy(JiveGlobals.MESSAGE, iterator, authorization,
                permissions);
    }
}