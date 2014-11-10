/**
 * $RCSfile: Group.java,v $
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
 * Organizes Users into a group for easier permissions management at the
 * Forum level. In this way, Groups essentially serve the same purpose that
 * they do in Unix or Windows.<p>
 *
 * For example, CREATE_THREAD permissions can be set per forum. A forum
 * administrator may wish to create a "Thread Posters" group that has
 * CREATE_THREAD permissions in the forum. Then, users can be added to that
 * group and will automatically receive CREATE_THREAD permissions in that forum.
 * <p>
 *
 * Security for Group objects is provide by GroupProxy protection proxy objects.
 *
 * @see User
 */
public interface Group {

    /**
     * Returns the id of the group.
     *
     * @return the id of the group.
     */
    public long getID();

    /**
     * Returns the name of the group. For example, 'XYZ Admins'.
     *
     * @return the name of the group.
     */
    public String getName();

    /**
     * Sets the name of the group. For example, 'XYZ Admins'.<p>
     *
     * This method is restricted to those with group administration permission.
     *
     * @param name the name for the group.
     * @throws UnauthorizedException if does not have group admin permissions.
     */
    public void setName(String name) throws UnauthorizedException;

    /**
     * Returns the description of the group. The description often summarizes
     * a group's function, such as 'Administrators of the XYZ forum'.
     *
     * @return the description of the group.
     */
    public String getDescription();

    /**
     * Sets the description of the group.
     *
     * The description often summarizes a group's function, such as
     * 'Administrators of the XYZ forum'.<p>
     *
     * This method is restricted to those with group administration permission.
     *
     * @param name the description of the group.
     * @throws UnauthorizedException if does not have group admin permissions.
     */
    public void setDescription(String description) throws UnauthorizedException;

    /**
     * Returns the Date that the group was created.
     *
     * @return the Date the group was created.
     */
    public Date getCreationDate();

    /**
     * Sets the creation date of group user. In most cases, the creation date
     * will default to when the group was entered into the system. However,
     * the date needs to be set manually when importing data. In other words,
     * skin authors should ignore this method since it only intended for system
     * maintenance.
     *
     * @param creationDate the date the group was created.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public void setCreationDate(Date creationDate) throws UnauthorizedException;

    /**
     * Returns the Date that the group was last modified.
     *
     * @return the Date the group record was last modified.
     */
    public Date getModifiedDate();

    /**
     * Sets the date the group was last modified. Skin authors should ignore
     * this method since it only intended for system maintenance.
     *
     * @param modifiedDate the date the group was modified.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public void setModifiedDate(Date modifiedDate) throws UnauthorizedException;

    /**
     * Returns an extended property of the group. Each group can have an
     * arbitrary number of extended properties. This lets particular skins
     * or filters provide enhanced functionality that is not part of the base
     * interface.
     *
     * @param name the name of the property to get.
     * @return the value of the property
     */
    public String getProperty(String name);

    /**
     * Sets an extended property of the group. Each group can have an
     * arbitrary number of extended properties. This lets particular skins
     * or filters provide enhanced functionality that is not part of the base
     * interface.
     *
     * @param name the name of the property to set.
     * @param value the new value for the property.
     * @throws UnauthorizedException if not allowed to change the group.
     */
    public void setProperty(String name, String value)
            throws UnauthorizedException;

    /**
     * Deletes an extended property. If the property specified by
     * <code>name</code> does not exist, this method will do nothing.
     *
     * @param name the name of the property to delete.
     * @throws UnauthorizedException if not allowed to edit messages.
     */
    public void deleteProperty(String name) throws UnauthorizedException;

    /**
     * Returns an Iterator for all the names of the extended group properties.
     *
     * @return an Iterator for the property names.
     */
    public Iterator propertyNames();

    /**
     * Grants administrator privileges of the group to a user.<p>
     *
     * This method is restricted to those with group administration permission.
     *
     * @param user the User to grant adminstrative privileges to.
     * @throws UnauthorizedException if does not have group admin permissions.
     */
    public void addAdministrator(User user) throws UnauthorizedException;

    /**
     * Revokes administrator privileges of the group to a user.<p>
     *
     * This method is restricted to those with group administration permission.
     *
     * @param user the User to grant adminstrative privileges to.
     * @throws UnauthorizedException if does not have group admin permissions.
     */
    public void removeAdministrator(User user) throws UnauthorizedException;

    /**
     * Adds a member to the group.<p>
     *
     * This method is restricted to those with group administration permission.
     *
     * @param user the User to add to the group.
     * @throws UnauthorizedException if does not have group admin permissions.
     */
    public void addMember(User user) throws UnauthorizedException;

    /**
     * Removes a member from the group. If the User is not in the group, this
     * method does nothing.<p>
     *
     * This method is restricted to those with group administration permission.
     *
     * @param user the User to remove from the group.
     * @throws UnauthorizedException if does not have group admin permissions.
     */
    public void removeMember(User user) throws UnauthorizedException;

    /**
     * Returns true if the User has group administrator permissions. Group
     * administrators are also considered to be members.
     *
     * @return true if the User is an administrator of the group.
     */
    public boolean isAdministrator(User user);

    /**
     * Returns true if if the User is a member of the group. Group
     * administrators are also considered to be members.
     *
     * @return true if the User is a member of the group.
     */
    public boolean isMember(User user);

    /**
     * Returns the number of group administrators.
     *
     * @return the number of group administrators.
     */
    public int getAdministratorCount();

    /**
     * Returns the number of group members.
     *
     * @return the number of group members.
     */
    public int getMemberCount();

    /**
     * An iterator for all the users that are members of the group.
     *
     * @return an Iterator for all members of the group.
     */
    public Iterator members();

    /**
     * An iterator for all the users that are administrators of the group.
     *
     * @return an Iterator for all administrators of the group.
     */
    public Iterator administrators();

    /**
     * Returns the permissions for the group that correspond to the
     * passed-in Authorization.
     *
     * @param authorization the auth token to lookup permissions for.
     */
    public abstract ForumPermissions getPermissions(Authorization authorization);

    /**
     * Returns true if the handle on the object has the permission specified.
     * A list of possible permissions can be found in the ForumPermissions
     * class. Certain methods of this class are restricted to certain
     * permissions as specified in the method comments.
     *
     * @param type a permission type.
     * @return true if the specified permission is valid.
     * @see ForumPermissions
     */
    public boolean hasPermission(int type);
}
