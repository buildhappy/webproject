/**
 * $RCSfile: DbGroupManager.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:56 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import java.util.Iterator;
import java.util.ArrayList;
import java.sql.*;
import com.jivesoftware.forum.*;
import com.jivesoftware.util.*;

/**
 * Database implementation of the GroupManager interface.
 */
public class DbGroupManager implements GroupManager {

    /** DATABASE QUERIES **/
    private static final String USER_GROUPS =
        "SELECT groupID from jiveGroupUser WHERE userID=?";
    private static final String USER_MESSAGE_COUNT =
        "SELECT count(1) FROM jiveMessage,jiveForum,jiveThread WHERE " +
        "jiveMessage.userID=? AND jiveForum.forumID=? AND " +
        "jiveThread.forumID=jiveForum.forumID AND " +
        "jiveMessage.threadID=jiveThread.threadID";
    private static final String ALL_USER_MESSAGES =
        "SELECT messageID FROM jiveMessage WHERE userID=?";
    private static final String DELETE_USER_MESSAGES =
        "UPDATE jiveMessage set userID=-1 WHERE userID=?";
    private static final String DELETE_USER_PERMS =
        "DELETE FROM jiveUserPerm WHERE userID=?";
    private static final String DELETE_USER_GROUPS =
        "DELETE FROM jiveGroupUser WHERE userID=?";
    private static final String DELETE_USER_PROPS =
        "DELETE FROM jiveUserProp WHERE userID=?";
    private static final String GROUP_COUNT = "SELECT count(1) FROM jiveGroup";
    private static final String DELETE_GROUP_USERS =
        "DELETE FROM jiveGroupUser WHERE groupID=?";
    private static final String DELETE_GROUP =
        "DELETE FROM jiveGroup WHERE groupID=?";
    private static final String ALL_GROUPS = "SELECT groupID FROM jiveGroup";

    private DbForumFactory factory;

    /**
     * Creates a new GroupManager.
     */
    public DbGroupManager(DbForumFactory factory) {
        this.factory = factory;
    }

    //FROM THE GROUPMANAGER INTERFACE//

    public Group createGroup(String name) throws UnauthorizedException,
            GroupAlreadyExistsException
    {
        Group newGroup = null;
        try {
            Group existingGroup = getGroup(name);

            //The group already exists since now exception, so:
            throw new GroupAlreadyExistsException();
        }
        catch (GroupNotFoundException unfe) {
            //The group doesn't already exist so we can create a new group
            newGroup = new DbGroup(name, factory);
        }
        return newGroup;
    }

    public Group getGroup(long groupID) throws GroupNotFoundException {
        return factory.cacheManager.groupCache.get(groupID);
    }

    public Group getGroup(String name) throws GroupNotFoundException {
        return factory.cacheManager.groupCache.get(name);
    }

    public void deleteGroup(Group group) throws UnauthorizedException {
        long groupID = group.getID();
        long [] members = new long[group.getMemberCount()];
        Iterator iter = group.members();
        for (int i=0; i<members.length; i++) {
            User user = (User)iter.next();
            members[i] = user.getID();
        }

        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            //mark all message by user as anonymous
            pstmt = con.prepareStatement(DELETE_GROUP_USERS);
            pstmt.setLong(1,groupID);
            pstmt.execute();
            pstmt.close();
            //remove all permissions given to user
            pstmt = con.prepareStatement(DELETE_GROUP);
            pstmt.setLong(1,groupID);
            pstmt.execute();
            pstmt.close();
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
        }
        finally {
            try {  pstmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }

        //Finally, expire all relevant caches
        DatabaseCacheManager cacheManager = factory.getCacheManager();
        cacheManager.groupCache.remove(groupID);
        //Removing a group can change the permissions of all the users in that
        //group. Therefore, remove each user from the user perms cache.
        for (int i=0; i<members.length; i++) {
            cacheManager.userPermsCache.removeUserPerm(members[i]);
        }
    }

    public int getGroupCount() {
        int count = 0;
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(GROUP_COUNT);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
        }
        finally {
            try {  pstmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
        return count;
    }

    public Iterator groups() {
        LongList groups = new LongList();
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(ALL_GROUPS);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                groups.add(rs.getLong(1));
            }
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
        }
        finally {
            try {  pstmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
        return new DatabaseObjectIterator(JiveGlobals.GROUP, groups.toArray(),
                this);
    }

    public Iterator groups(int startIndex, int numResults) {
        LongList groups = new LongList();
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(ALL_GROUPS);
            ResultSet rs = pstmt.executeQuery();
            //Move to start of index
            for (int i=0; i<startIndex; i++) {
                rs.next();
            }
            //Now read in desired number of results
            for (int i=0; i<numResults; i++) {
                if (rs.next()) {
                    groups.add(rs.getLong(1));
                }
                else {
                    break;
                }
            }
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
        }
        finally {
            try {  pstmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
        return new DatabaseObjectIterator(JiveGlobals.GROUP, groups.toArray(),
                this);
    }

    /**
     * Returns an array of all the groups that the user belongs to.
     */
    protected long[] getUserGroups(long userID) {
        Connection con = null;
        PreparedStatement pstmt = null;
        long [] groups = new long[0];
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(USER_GROUPS);
            pstmt.setLong(1,userID);
            ResultSet rs = pstmt.executeQuery();
            ArrayList groupList = new ArrayList();
            while (rs.next()) {
                groupList.add(new Long(rs.getLong("groupID")));
            }
            groups = new long[groupList.size()];
            for (int i=0; i<groups.length; i++) {
                groups[i] = ((Long)groupList.get(i)).longValue();
            }
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
        }
        finally {
            try {  pstmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
        return groups;
    }
}
