/**
 * $RCSfile: GroupProxy.java,v $
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
import java.util.Date;

/**
 * Protection proxy for the Group interface. It restricts access of certain
 * methods to those that have the proper permissions to administer this object.
 */
public class GroupProxy implements Group {

    private Group group;
    private Authorization authorization;
    private ForumPermissions permissions;

    public GroupProxy(Group group, Authorization authorization,
            ForumPermissions permissions)
    {
        this.group = group;
        this.authorization = authorization;
        this.permissions = permissions;
    }

    public long getID() {
        return group.getID();
    }

    public String getName() {
        return group.getName();
    }

    public void setName(String name) throws UnauthorizedException {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
                permissions.get(ForumPermissions.GROUP_ADMIN))
        {
            group.setName(name);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public String getDescription() {
        return group.getDescription();
    }

    public void setDescription(String description) throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
                permissions.get(ForumPermissions.GROUP_ADMIN))
        {
            group.setDescription(description);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public Date getCreationDate() {
        return group.getCreationDate();
    }

    public void setCreationDate(Date creationDate)
            throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            group.setCreationDate(creationDate);
        }
        else throw new UnauthorizedException();
    }

    public Date getModifiedDate() {
        return group.getModifiedDate();
    }

    public void setModifiedDate(Date modifiedDate)
            throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            group.setModifiedDate(modifiedDate);
        }
        else throw new UnauthorizedException();
    }

    public String getProperty(String name) {
        return group.getProperty(name);
    }

    public void setProperty(String name, String value)
            throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
                permissions.get(ForumPermissions.GROUP_ADMIN))
        {
            group.setProperty(name, value);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void deleteProperty(String name)
            throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
                permissions.get(ForumPermissions.GROUP_ADMIN))
        {
            group.deleteProperty(name);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public Iterator propertyNames() {
        return group.propertyNames();
    }

    public void addAdministrator(User user) throws UnauthorizedException {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
                permissions.get(ForumPermissions.GROUP_ADMIN))
        {
            group.addAdministrator(user);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void removeAdministrator(User user) throws UnauthorizedException {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
                permissions.get(ForumPermissions.GROUP_ADMIN))
        {
            group.removeAdministrator(user);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void addMember(User user) throws UnauthorizedException {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
                permissions.get(ForumPermissions.GROUP_ADMIN))
        {
            group.addMember(user);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void removeMember(User user) throws UnauthorizedException {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
                permissions.get(ForumPermissions.GROUP_ADMIN))
        {
            group.removeMember(user);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public boolean isAdministrator(User user) {
        return group.isAdministrator(user);
    }

    public boolean isMember(User user) {
        return group.isMember(user);
    }

    public int getAdministratorCount() {
        return group.getAdministratorCount();
    }

    public int getMemberCount() {
        return group.getMemberCount();
    }

    public Iterator members() {
        return group.members();
    }

    public Iterator administrators() {
        return group.administrators();
    }

    public ForumPermissions getPermissions(Authorization authorization) {
        return group.getPermissions(authorization);
    }

    public boolean hasPermission(int type) {
        return permissions.get(type);
    }
}
