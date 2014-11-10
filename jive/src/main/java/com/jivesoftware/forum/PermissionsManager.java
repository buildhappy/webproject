/**
 * $RCSfile: PermissionsManager.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:45 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import java.util.Iterator;

/**
 * Interface for managing Permissions for users and groups. Each permissions
 * manager is attached to a permissions context, which will either be for an
 * individual forum or the entire system.<p>
 *
 * This class is only intended for administrative purposes, and should not be
 * used by skin authors to check the permissions for individual users. For
 * example, to check if a particular user has permissions to create threads
 * in a forum, the proper code snippet is as follows:
 *
 * <pre>
 * Forum forum = factory.getForum("some forum");
 * boolean canCreateThread = forum.hasPermission(ForumPermissions.CREATE_THREAD);
 * </pre>
 */
public interface PermissionsManager {

    /**
     * Grants a user a particular permission. Only forum or system
     * administrators can assign permissions to a forum, and only system admins
     * can assign forum administration permissions. If this permissions context
     * is at the system level, only system admins can call this method.
     *
     * @param user the User to grant a permission to.
     * @param permissionType the type of permission to grant the user.
     * @throws UnauthorizedException if does not have property admin permissions.
     */
    public void addUserPermission(User user, int permissionType)
            throws UnauthorizedException;

    /**
     * Grants a particular permission for anonymous users. Any registered user
     * automatically inherits any anonymous user permissions. Only forum or system
     * administrators can assign permissions to a forum, and only system admins
     * can assign forum administration permissions. If this permissions context
     * is at the system level, only system admins can call this method.
     *
     * @param permissionType the type of permission to grant.
     * @throws UnauthorizedException if does not have property admin permissions.
     */
    public void addAnonymousUserPermission(int permissionType)
            throws UnauthorizedException;

    /**
     * Grants a particular permission to all registered users. "Registered
     * Users" does not refer to the static current list of users. Instead, it
     * dynamically matches to any member of the user database. Only forum or
     * system administrators can assign permissions to a forum, and only system
     * admins can assign forum administration permissions. If this permissions
     * context is at the system level, only system admins can call this method.
     *
     * @param permissionType the type of permission to grant.
     * @throws UnauthorizedException if does not have property admin permissions.
     */
    public void addRegisteredUserPermission(int permissionType)
            throws UnauthorizedException;

    /**
     * Revokes a particular permission from a user. Only forum or system
     * administrators can assign permissions to a forum, and only system admins
     * can assign forum administration permissions. If this permissions context
     * is at the system level, only system admins can call this method.
     *
     * @param user the User to revoke a permission from.
     * @param permissionType the type of permission to revoke.
     * @throws UnauthorizedException if does not have property admin permissions.
     */
    public void removeUserPermission(User user, int permissionType)
            throws UnauthorizedException;

    /**
     * Revokes a particular permission from anonymous users. Only forum or system
     * administrators can assign permissions to a forum, and only system admins
     * can assign forum administration permissions. If this permissions context
     * is at the system level, only system admins can call this method.
     *
     * @param permissionType the type of permission to revoke.
     * @throws UnauthorizedException if does not have property admin permissions.
     */
    public void removeAnonymousUserPermission(int permissionType)
            throws UnauthorizedException;

    /**
     * Revokes a particular permission from all registered users. "Registered
     * Users" does not refer to the static current list of users. Instead, it
     * dynamically matches to any member of the user database. Only forum or system
     * administrators can assign permissions to a forum, and only system admins
     * can assign forum administration permissions. If this permissions context
     * is at the system level, only system admins can call this method.
     *
     * @param permissionType the type of permission to revoke.
     * @throws UnauthorizedException if does not have property admin permissions.
     */
    public void removeRegisteredUserPermission(int permissionType)
            throws UnauthorizedException;

    /**
     * Revokes all user permissions for this permission context. Only system
     * administrators can call this method.
     *
     * @throws UnauthorizedException if not a system admin.
     */
    public void removeAllUserPermissions() throws UnauthorizedException;

    /**
     * Returns true if the specified user has a particular permission.
     *
     * @param user the User to check permissions for.
     * @param permissionType the permission to check.
     * @return true if the user has the specified permission.
     */
    public boolean userHasPermission(User user, int permissionType);

    /**
     * Returns true if the anonymous users have a particular permission.
     *
     * @param permissionType the permission to check.
     * @return true if anonymous users have the specified permission.
     */
    public boolean anonymousUserHasPermission(int permissionType);

    /**
     * Returns true if registered users have a particular permission.
     * "Registered Users" does not refer to the static current list of users.
     * Instead, it dynamically matches to any member of the user database.
     *
     * @param permissionType the permission to check.
     * @return true if all registered users have the specified permission.
     */
    public boolean registeredUserHasPermission(int permissionType);

    /**
     * Returns all the userID's of users with a particular permission. This
     * list does not include the special "anonymous users" and
     * "registered users" permission types. This method is not the normal method
     * for determining if a user has a certain permission on an object in the
     * system; instead it is only useful for permission management. For example,
     * to check if a user has read access on a Forum, simply call
     * forum.hasPermission(ForumPermissions.READ), where forum is the Forum
     * you want to check perms on.
     *
     * @param permissionType the type of permission to check.
     * @return an Iterator of all the users with the specified permission.
     */
    public Iterator usersWithPermission(int permissionType);

    /**
     * Returns a count of the users that have a particular permission. This
     * list does not include the special "anonymous users" and
     * "registered users" permission types.
     *
     * @return a count of the users that have a particular permission.
     */
    public int usersWithPermissionCount(int permissionType);

    /**
     * Grants a group a particular permission. Only forum or system
     * administrators can assign permissions to a forum, and only system admins
     * can assign forum administration permissions. If this permissions context
     * is at the system level, only system admins can call this method.
     *
     * @param group the group to grant a permission to.
     * @param permissionType the type of permission to grant the group.
     * @throws UnauthorizedException if does not have property admin permissions.
     */
    public void addGroupPermission(Group group, int permissionType)
            throws UnauthorizedException;

    /**
     * Revokes a particular permission from a group. Only forum or system
     * administrators can assign permissions to a forum, and only system admins
     * can assign forum administration permissions. If this permissions context
     * is at the system level, only system admins can call this method.
     *
     * @param group the group to revoke a permission from.
     * @param permissionType the type of permission to revoke.
     * @throws UnauthorizedException if does not have property admin permissions.
     */
    public void removeGroupPermission(Group group, int permissionType)
            throws UnauthorizedException;

    /**
     * Revokes all group permissions for this permission context. Only system
     * admins can call this method.
     *
     * @throws UnauthorizedException if not a system admin.
     */
    public void removeAllGroupPermissions() throws UnauthorizedException;

    /**
     * Returns an Iterator of groups with a particular permission.
     *
     * @param permissionType the type of permission to check.
     * @return an Iterator of all the groups with the specified permission.
     */
    public Iterator groupsWithPermission(int permissionType);

    /**
     * Returns a count of the groups that have a particular permission.
     *
     * @return a count of the groups that have a particular permission.
     */
    public int groupsWithPermissionCount(int permissionType);

}