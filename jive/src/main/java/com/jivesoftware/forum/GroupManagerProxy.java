/**
 * $RCSfile: GroupManagerProxy.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:44 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import java.util.Iterator;

/**
 * Protection proxy for the GroupManager class. It restricts access to
 * protected methods by throwing UnauthorizedExceptions when necessary.
 *
 * @see GroupManager
 */
public class GroupManagerProxy implements GroupManager {

    private GroupManager groupManager;
    private Authorization authorization;
    private ForumPermissions permissions;

    /**
     * Creates a new GroupManagerProxy.
     */
    public GroupManagerProxy(GroupManager groupManager, Authorization
            authorization, ForumPermissions permissions)
    {
        this.groupManager = groupManager;
        this.authorization = authorization;
        this.permissions = permissions;
    }

    public Group createGroup(String name) throws UnauthorizedException,
            GroupAlreadyExistsException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            Group group = groupManager.createGroup(name);
            return new GroupProxy(group, authorization, permissions);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public Group getGroup(long groupID) throws GroupNotFoundException {
        Group group = groupManager.getGroup(groupID);
        ForumPermissions groupPermissions = group.getPermissions(authorization);
        ForumPermissions newPermissions =
                new ForumPermissions(permissions, groupPermissions);
        return new GroupProxy(group, authorization, newPermissions);
    }

    public Group getGroup(String name) throws GroupNotFoundException {
        Group group = groupManager.getGroup(name);
        ForumPermissions groupPermissions = group.getPermissions(authorization);
        ForumPermissions newPermissions =
                new ForumPermissions(permissions, groupPermissions);
        return new GroupProxy(group, authorization, newPermissions);
    }

    public void deleteGroup(Group group) throws UnauthorizedException {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            groupManager.deleteGroup(group);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public int getGroupCount() {
        return groupManager.getGroupCount();
    }

    public Iterator groups() {
        Iterator iterator = groupManager.groups();
        return new IteratorProxy(JiveGlobals.GROUP, iterator, authorization,
                permissions);
    }

    public Iterator groups(int startIndex, int numResults) {
        Iterator iterator = groupManager.groups(startIndex, numResults);
        return new IteratorProxy(JiveGlobals.GROUP, iterator, authorization,
                permissions);
    }
}
