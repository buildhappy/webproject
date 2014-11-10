package com.jivesoftware.forum;

import java.util.Iterator;

public class PermissionsManagerProxy implements PermissionsManager {

    private PermissionsManager permManager;
    private Authorization authorization;
    private ForumPermissions permissions;

    public PermissionsManagerProxy(PermissionsManager permManager,
            Authorization authorization, ForumPermissions permissions)
    {
        this.permManager = permManager;
        this.authorization = authorization;
        this.permissions = permissions;
    }

     public void addUserPermission(User user, int permissionType)
            throws UnauthorizedException
    {
        //Only system admins can set forum admin perms.
        if (permissionType == ForumPermissions.FORUM_ADMIN) {
            if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
                permManager.addUserPermission(user, permissionType);
            }
            else {
                throw new UnauthorizedException();
            }
        }
        //Otherwise, system or forum admins can set this permission.
        if (permissions.isSystemOrForumAdmin()) {
            permManager.addUserPermission(user, permissionType);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void addAnonymousUserPermission(int permissionType)
            throws UnauthorizedException
    {
         //Only system admins can set forum admin perms.
        if (permissionType == ForumPermissions.FORUM_ADMIN) {
            if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
                permManager.addAnonymousUserPermission(permissionType);
            }
            else {
                throw new UnauthorizedException();
            }
        }
        //Otherwise, system or forum admins can set this permission.
        if (permissions.isSystemOrForumAdmin()) {
            permManager.addAnonymousUserPermission(permissionType);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void addRegisteredUserPermission(int permissionType)
            throws UnauthorizedException
    {
        //Only system admins can set forum admin perms.
        if (permissionType == ForumPermissions.FORUM_ADMIN) {
            if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
                permManager.addRegisteredUserPermission(permissionType);
            }
            else {
                throw new UnauthorizedException();
            }
        }
        //Otherwise, system or forum admins can set this permission.
        if (permissions.isSystemOrForumAdmin()) {
            permManager.addRegisteredUserPermission(permissionType);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void removeUserPermission(User user, int permissionType)
            throws UnauthorizedException
    {
        //Only system admins can set forum admin perms.
        if (permissionType == ForumPermissions.FORUM_ADMIN) {
            if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
                permManager.removeUserPermission(user, permissionType);
            }
            else {
                throw new UnauthorizedException();
            }
        }
        //Otherwise, system or forum admins can set this permission.
        if (permissions.isSystemOrForumAdmin()) {
            permManager.removeUserPermission(user, permissionType);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void removeAnonymousUserPermission(int permissionType)
            throws UnauthorizedException
    {
        //Only system admins can set forum admin perms.
        if (permissionType == ForumPermissions.FORUM_ADMIN) {
            if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
                permManager.removeAnonymousUserPermission(permissionType);
            }
            else {
                throw new UnauthorizedException();
            }
        }
        //Otherwise, system or forum admins can set this permission.
        if (permissions.isSystemOrForumAdmin()) {
            permManager.removeAnonymousUserPermission(permissionType);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void removeRegisteredUserPermission(int permissionType)
            throws UnauthorizedException
    {
        //Only system admins can set forum admin perms.
        if (permissionType == ForumPermissions.FORUM_ADMIN) {
            if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
                permManager.removeRegisteredUserPermission(permissionType);
            }
            else {
                throw new UnauthorizedException();
            }
        }
        //Otherwise, system or forum admins can set this permission.
        if (permissions.isSystemOrForumAdmin()) {
            permManager.removeRegisteredUserPermission(permissionType);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void removeAllUserPermissions() throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            permManager.removeAllUserPermissions();
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public boolean userHasPermission(User user, int permissionType)
    {
        return permManager.userHasPermission(user, permissionType);
    }

    public boolean anonymousUserHasPermission(int permissionType)
    {
        return permManager.anonymousUserHasPermission(permissionType);
    }

    public boolean registeredUserHasPermission(int permissionType)
    {
        return permManager.registeredUserHasPermission(permissionType);
    }

    public Iterator usersWithPermission(int permissionType)
    {
        return permManager.usersWithPermission(permissionType);
    }

    public int usersWithPermissionCount(int permissionType)
    {
        return permManager.usersWithPermissionCount(permissionType);
    }

    public void addGroupPermission(Group group, int permissionType)
            throws UnauthorizedException
    {
       //Only system admins can set forum admin perms.
        if (permissionType == ForumPermissions.FORUM_ADMIN) {
            if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
                permManager.addGroupPermission(group, permissionType);
            }
            else {
                throw new UnauthorizedException();
            }
        }
        //Otherwise, system or forum admins can set this permission.
        if (permissions.isSystemOrForumAdmin()) {
            permManager.addGroupPermission(group, permissionType);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void removeGroupPermission(Group group, int permissionType)
            throws UnauthorizedException
    {
        //Only system admins can set forum admin perms.
        if (permissionType == ForumPermissions.FORUM_ADMIN) {
            if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
                permManager.removeGroupPermission(group, permissionType);
            }
            else {
                throw new UnauthorizedException();
            }
        }
        //Otherwise, system or forum admins can set this permission.
        if (permissions.isSystemOrForumAdmin()) {
            permManager.removeGroupPermission(group, permissionType);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void removeAllGroupPermissions() throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            permManager.removeAllGroupPermissions();
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public Iterator groupsWithPermission(int permissionType)
    {
        return permManager.groupsWithPermission(permissionType);
    }

    public int groupsWithPermissionCount(int permissionType)
    {
        return permManager.groupsWithPermissionCount(permissionType);
    }

}