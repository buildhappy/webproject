/**
 * $RCSfile: GroupManager.java,v $
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
 * Manages groups.
 */
public interface GroupManager {

    /**
     * Factory method for creating a new Group. A unique name is the only
     * required field.
     *
     * @param name the new and unique name for the group.
     * @return a new Group.
     * @throws GroupAlreadyExistsException if the group name already exists in
     *      the system.
     */
    public Group createGroup(String name) throws UnauthorizedException,
            GroupAlreadyExistsException;

    /**
     * Gets a Group by ID.
     *
     * throws GroupNotFoundException if the group does not exist.
     */
    public Group getGroup(long groupID) throws GroupNotFoundException;

    /**
     * Gets a Group by name.
     *
     * throws GroupNotFoundException if the group does not exist.
     */
    public Group getGroup(String name) throws GroupNotFoundException;

    /**
     * Deletes a Group.
     *
     * @param group the group to delete.
     * @throws UnauthorizedException
     */
    public void deleteGroup(Group group) throws UnauthorizedException;

    /**
     * Returns the number of groups in the system.
     */
    public int getGroupCount();

    /**
     * Returns an iterator for all groups in the system.
     *
     * @return an Iterator for all groups.
     */
    public Iterator groups();

    /**
     * Returns an iterator for all groups starting at startIndex with the
     * given number of results. This is useful to support pagination in a GUI
     * where you may only want to display a certain number of results per page.
     * It is possible that the number of results returned will be less than
     * that specified by numResults if numResults is greater than the number
     * of records left in the system to display.
     *
     * @param startIndex the beginning index to start the results at.
     * @param numResults the total number of results to return.
     * @return an Iterator for all groups in the specified range.
     */
    public Iterator groups(int startIndex, int numResults);

}