/**
 * $RCSfile: User.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:47 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import java.util.*;

/**
 * The User interface provides information about and services for users
 * of the forum system. Users can be identified by a unique id or username.
 * Users can also be organized into Groups for easier management of
 * permissions at the forum level.
 * <p>
 * The name and email field will normally be required fields when creating
 * user accounts for most implementations of forums. However, some users may
 * wish to keep that information private. Therefore, there are two flags to
 * set if the name and email fields should be made visible to other users. If
 * the flags are set to deny access, getName() and getEmail() will throw
 * UnauthorizedExceptions to users that don't have ADMIN permissions.
 * <p>
 * Security for User objects is provide by UserProxy protection proxy objects.
 *
 * @see Group
 */
public interface User {

    /**
     * Returns the user's id. All ids must be unique in the system.
     *
     * @return the user's id.
     */
    public long getID();

    /**
     * Returns the user's username. All usernames must be unique in the system.
     *
     * @return the username of the user.
     */
    public String getUsername();

    /**
     * Returns the user's name. The user's name does not have to be to be
     * unique in the system. Some users may opt to not let others see their
     * name for privacy reasons. In that case, the user can set nameVisible to
     * false. In that case, a call to this method will return null.
     *
     * @return the name of the user.
     */
    public String getName();

    /**
     * Sets the user's name. The user's name does not have to be to be
     * unique in the system.
     *
     * @param name new name for the user.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public void setName(String name) throws UnauthorizedException;

    /**
     * Returns true if the user has chosen to make her name visible to other
     * users. If the name is not visible, calling getName() will throw an
     * UnauthorizedException.
     *
     * @return true if the name is visible to other users.
     */
    public boolean isNameVisible();

    /**
     * Sets whether a user's name is visible to other users. If the field
     * is set to not be visible, calling getName() will throw an
     * UnauthorizedException.
     *
     * @param visible boolean value to determin if the name should be visible.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public void setNameVisible(boolean visible) throws UnauthorizedException;

    /**
     * Sets the users's password. The password should be passed in as
     * plain text. The way the password is stored is implementation dependent.
     * However, it is recommended to at least hash passwords with an
     * algorithm such as MD5.
     *
     * @param password new password for the user.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public void setPassword(String password) throws UnauthorizedException;

    /**
     * Returns the user's password in hashed form. This method is only intended
     * for system administration functions and can be ignored by skin writers.
     *
     * @return the hashed password.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public String getPasswordHash() throws UnauthorizedException;

    /**
     * Sets the user's password in hashed form. This method is only intended
     * for system administration functions and can be ignored by skin writers.
     *
     * @param hashedPassword the hashedPassword for the user.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public void setPasswordHash(String passwordHash) throws UnauthorizedException;

    /**
     * Returns the user's email address. Email should be considered to be
     * a required field of a user account since it is critical to many
     * user operations performing. If the user sets emailVisible to false,
     * this method will always return null.
     *
     * @return the email address of the user.
     */
    public String getEmail();

    /**
     * Sets the user's email address. Email should be considered to be
     * a required field of a user account since it is critical to many
     * user operations performing.
     *
     * @param email new email address for the user.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public void setEmail(String email) throws UnauthorizedException;

    /**
     * Returns true if the user has chosen to make her email visible to other
     * users. If the email field is not visible, calling getEmail() will throw
     * an UnauthorizedException.
     *
     * @return true if the name is visible to other users.
     */
    public boolean isEmailVisible();

    /**
     * Sets whether a user's email is visible to other users. If the field
     * is set to not be visible, calling getEmail() will throw an
     * UnauthorizedException.
     *
     * @param visible boolean value to determin if the name should be visible.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public void setEmailVisible(boolean visible) throws UnauthorizedException;

    /**
     * Returns the Date that the user was created.
     *
     * @return the Date the user was created.
     */
    public Date getCreationDate();

    /**
     * Sets the creation date of the user. In most cases, the creation date
     * will default to when the user was entered into the system. However,
     * the date needs to be set manually when importing data. In other words,
     * skin authors should ignore this method since it only intended for system
     * maintenance.
     *
     * @param creationDate the date the user was created.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public void setCreationDate(Date creationDate) throws UnauthorizedException;

    /**
     * Returns the Date that the user was last modified.
     *
     * @return the Date the user record was last modified.
     */
    public Date getModifiedDate();

    /**
     * Sets the date the user was last modified. Skin authors should ignore
     * this method since it only intended for system maintenance.
     *
     * @param modifiedDate the date the user was modified.
     * @throws UnauthorizedException if does not have ADMIN permissions.
     */
    public void setModifiedDate(Date modifiedDate) throws UnauthorizedException;

    /**
     * Returns an extended property of the user. Each user can have an
     * arbitrary number of extended properties. This lets particular skins
     * or filters provide enhanced functionality that is not part of the base
     * interface.
     *
     * @param name the name of the property to get.
     * @return the value of the property
     */
    public String getProperty(String name);

    /**
     * Sets an extended property of the user. Each user can have an
     * arbitrary number of extended properties. This lets particular skins
     * or filters provide enhanced functionality that is not part of the base
     * interface. Property names and values must be valid Strings. If
     * <tt>null</tt> or an empty length String is used, a NullPointerException
     * will be thrown.
     *
     * @param name the name of the property to set.
     * @param value the new value for the property.
     * @throws UnauthorizedException if not allowed to edit messages.
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
     * Returns an Iterator for all the names of the extended user properties.
     *
     * @return an Iterator for the property names.
     */
    public Iterator propertyNames();

    /**
     * Returns the number of reward points that the user currently has.
     *
     * @return the number of reward points that the user has.
     * @see RewardManager
     */
    public int getRewardPoints();

    /**
     * Sets the number of reward points that the user has.
     *
     * @param points the new number of reward points for the user.
     * @throws UnauthorizedException if not a system admin.
     * @throws Exception if the setting the reward points fails.
     * @see RewardManager
     */
    public void setRewardPoints(int points) throws UnauthorizedException, Exception;

    /**
     * Returns the permissions for the user that correspond to the
     * passed-in Authorization.
     *
     * @param authorization the auth token to look up permissions with.
     */
    public abstract ForumPermissions getPermissions(Authorization authorization);

    /**
     * Returns true if the handle on the object has the permission specified.
     * A list of possible permissions can be found in the ForumPermissions
     * class. Certain methods of this class are restricted to certain
     * permissions as specified in the method comments.
     *
     * @see ForumPermissions
     */
    public boolean hasPermission(int type);
}