/**
 * $RCSfile: UserProxy.java,v $
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
import java.util.Date;

/**
 * Protection proxy for User objects.
 *
 * @see User
 */
public class UserProxy implements User {

    private User user;
    private Authorization authorization;
    private ForumPermissions permissions;

    /**
     * Create a new UserProxy.
     */
    public UserProxy(User user, Authorization authorization,
            ForumPermissions permissions)
    {
        this.user = user;
        this.authorization = authorization;
        this.permissions = permissions;
    }

    public long getID() {
        return user.getID();
    }

    public String getUsername() {
        return user.getUsername();
    }

    public String getName(){
        if (isNameVisible() || permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
                permissions.get(ForumPermissions.USER_ADMIN))
        {
            return user.getName();
        }
        else {
            return null;
        }
    }

    public void setName(String name) throws UnauthorizedException {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
               permissions.get(ForumPermissions.USER_ADMIN))
        {
            user.setName(name);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public boolean isNameVisible() {
        return user.isNameVisible();
    }

    public void setNameVisible(boolean visible) throws UnauthorizedException {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
               permissions.get(ForumPermissions.USER_ADMIN))
        {
            user.setNameVisible(visible);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void setPassword(String password) throws UnauthorizedException {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
               permissions.get(ForumPermissions.USER_ADMIN))
        {
            user.setPassword(password);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public String getPasswordHash() throws UnauthorizedException {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN))
        {
            return user.getPasswordHash();
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void setPasswordHash(String passwordHash)
            throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN))
        {
            user.setPasswordHash(passwordHash);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public String getEmail() {
        if (isEmailVisible() ||  permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
                permissions.get(ForumPermissions.USER_ADMIN))
        {
            return user.getEmail();
        }
        else {
            return null;
        }
    }

    public void setEmail(String email) throws UnauthorizedException {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
               permissions.get(ForumPermissions.USER_ADMIN))
        {
            user.setEmail(email);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public boolean isEmailVisible() {
        return user.isEmailVisible();
    }

    public void setEmailVisible(boolean visible) throws UnauthorizedException {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
               permissions.get(ForumPermissions.USER_ADMIN))
        {
            user.setEmailVisible(visible);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public Date getCreationDate() {
        return user.getCreationDate();
    }

    public void setCreationDate(Date creationDate)
            throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            user.setCreationDate(creationDate);
        }
        else throw new UnauthorizedException();
    }

    public Date getModifiedDate() {
        return user.getModifiedDate();
    }

    public void setModifiedDate(Date modifiedDate)
            throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            user.setModifiedDate(modifiedDate);
        }
        else throw new UnauthorizedException();
    }

    public String getProperty(String name) {
        return user.getProperty(name);
    }

    public void setProperty(String name, String value)
            throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
               permissions.get(ForumPermissions.USER_ADMIN))
        {
            user.setProperty(name, value);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void deleteProperty(String name)
            throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
               permissions.get(ForumPermissions.USER_ADMIN))
        {
            user.deleteProperty(name);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public Iterator propertyNames() {
        return user.propertyNames();
    }

    public int getRewardPoints() {
        return user.getRewardPoints();
    }

    public void setRewardPoints(int points) throws UnauthorizedException, Exception
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            user.setRewardPoints(points);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public ForumPermissions getPermissions(Authorization authorization) {
        return user.getPermissions(authorization);
    }

    public boolean hasPermission(int type) {
        return permissions.get(type);
    }

    /**
     * Converts the object to a String by returning the usernamename.
     * This functionality is primarily for Java applications that might be
     * accessing CoolForum objects through a GUI.
     */
    public String toString() {
        return user.toString();
    }
}