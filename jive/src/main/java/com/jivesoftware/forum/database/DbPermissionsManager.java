/**
 * $RCSfile: DbPermissionsManager.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:56 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import com.jivesoftware.forum.*;
import com.jivesoftware.util.*;
import java.util.*;
import java.sql.*;

/**
 * Database implementation of the PermissionsManager interface.
 */
public class DbPermissionsManager implements PermissionsManager {

    /** DATABASE QUERIES **/

    private static final String ADD_USER_PERM =
        "INSERT INTO jiveUserPerm(forumID,userID,userType,permission) VALUES(?,?,?,?)";
    private static final String ADD_GROUP_PERM =
        "INSERT INTO jiveGroupPerm(forumID,groupID,permission) VALUES(?,?,?)";
    private static final String REMOVE_GROUP_PERM =
        "DELETE FROM jiveGroupPerm WHERE forumID=? AND groupID=? AND permission=?";

    private long forumID;
    private DbForumFactory factory;

    /**
     * Constructs a new DbPermissionsManager. The forumID indicates what forum
     * the permissions management will be for. If the forumID is -1, that means
     * system permissions will be being used.
     */
    public DbPermissionsManager(long forumID, DbForumFactory factory) {
        this.forumID = forumID;
        this.factory = factory;
    }

    //FROM THE PERMISSIONSMANAGER INTERFACE//

    public void addUserPermission(User user, int permissionType) {
        if (!userHasPermission(user, permissionType)) {
            addUserPermission(user.getID(), permissionType);
        }
    }

    public void addAnonymousUserPermission(int permissionType) {
        if (!anonymousUserHasPermission(permissionType)) {
            addUserPermission(-1, permissionType);
        }
    }

    public void addRegisteredUserPermission(int permissionType) {
        if (!registeredUserHasPermission(permissionType)) {
            addUserPermission(0, permissionType);
        }
    }

    public void removeUserPermission(User user, int permissionType) {
        removeUserPermission(user.getID(), permissionType);
    }

    public void removeAllUserPermissions() {
        Connection con = null;
        Statement stmt = null;
        try {
            con = ConnectionManager.getConnection();
            stmt = con.createStatement();
            StringBuffer sql = new StringBuffer("DELETE FROM jiveUserPerm WHERE ");
            if (forumID == -1) {
                sql.append("forumID IS NULL");
            }
            else {
                sql.append("forumID=").append(forumID);
            }
            stmt.execute(sql.toString());
            //Remove user permissions from cache since they've changed.
            factory.cacheManager.userPermsCache.remove(forumID);
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
        }
        finally {
            try {  stmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    public void removeAnonymousUserPermission(int permissionType) {
        removeUserPermission(-1, permissionType);
    }

    public void removeRegisteredUserPermission(int permissionType) {
        removeUserPermission(0, permissionType);
    }

    public boolean userHasPermission(User user, int permissionType) {
        return userHasPermission(user.getID(), permissionType);
    }

    public boolean anonymousUserHasPermission(int permissionType) {
        return userHasPermission(-1, permissionType);
    }

    public boolean registeredUserHasPermission(int permissionType) {
        return userHasPermission(0, permissionType);
    }

    public Iterator usersWithPermission(int permissionType) {
        LongList users = new LongList();
        Connection con = null;
        Statement stmt = null;
        try {
            con = ConnectionManager.getConnection();
            stmt = con.createStatement();
            StringBuffer query = new StringBuffer(83);
            query.append("SELECT DISTINCT userID FROM jiveUserPerm WHERE ");
            if (forumID == -1) {
                query.append("forumID IS NULL");
            }
            else {
                query.append("forumID=").append(forumID);
            }
            query.append(" AND permission=").append(permissionType);
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                users.add(rs.getInt(1));
            }
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
        }
        finally {
            try {  stmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
        return new DatabaseObjectIterator(JiveGlobals.USER, users.toArray(),
                factory.getUserManager());
    }

    public int usersWithPermissionCount(int permissionType) {
        int userCount = 0;
        Connection con = null;
        Statement stmt = null;
        try {
            con = ConnectionManager.getConnection();
            stmt = con.createStatement();
            StringBuffer query = new StringBuffer(83);
            query.append("SELECT count(userID) FROM jiveUserPerm WHERE ");
            if (forumID == -1) {
                query.append("forumID IS NULL");
            }
            else {
                query.append("forumID=").append(forumID);
            }
            query.append(" AND permission=").append(permissionType);
            ResultSet rs = stmt.executeQuery(query.toString());
            if (rs.next()) {
                userCount = rs.getInt(1);
            }
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
        }
        finally {
            try {  stmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
        return userCount;
    }

    public void addGroupPermission(Group group, int permissionType) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(ADD_GROUP_PERM);
            pstmt.setLong(1,forumID);
            pstmt.setLong(2,group.getID());
            pstmt.setInt(3,permissionType);
            pstmt.execute();
            //Remove user permissions from cache since they've changed. Because
            //of the way that user perm cache is handled, it is easiest to
            //simply remove all the user perm cache for the forum. This is ok
            //since it happens infrequently.
            factory.cacheManager.userPermsCache.remove(forumID);
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
    }

    public void removeGroupPermission(Group group, int permissionType) {
        Connection con = null;
        Statement stmt = null;
        try {
            con = ConnectionManager.getConnection();
            stmt = con.createStatement();
            StringBuffer sql = new StringBuffer("DELETE FROM jiveGroupPerm WHERE ");
            if (forumID == -1) {
                sql.append("forumID IS NULL ");
            }
            else {
                sql.append("forumID=").append(forumID).append(" ");
            }
            sql.append("AND groupID=").append(group.getID()).append(" ");
            sql.append("AND permission=").append(permissionType);
            stmt.execute(sql.toString());
            //Remove user permissions from cache since they've changed. Because
            //of the way that user perm cache is handled, it is easiest to
            //simply remove all the user perm cache for the forum. This is ok
            //since it happens infrequently.
            factory.cacheManager.userPermsCache.remove(forumID);
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
        }
        finally {
            try {  stmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    public void removeAllGroupPermissions() {
        Connection con = null;
        Statement stmt = null;
        try {
            con = ConnectionManager.getConnection();
            stmt = con.createStatement();
            StringBuffer sql = new StringBuffer("DELETE FROM jiveGroupPerm WHERE ");
            if (forumID == -1) {
                sql.append("forumID IS NULL");
            }
            else {
                sql.append("forumID=").append(forumID);
            }
            stmt.execute(sql.toString());
            //Remove user permissions from cache since they've changed.
            factory.cacheManager.userPermsCache.remove(forumID);
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
        }
        finally {
            try {  stmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    public Iterator groupsWithPermission(int permissionType) {
        LongList groups = new LongList();
        Connection con = null;
        Statement stmt = null;
        try {
            con = ConnectionManager.getConnection();
            stmt = con.createStatement();
            StringBuffer sql = new StringBuffer(
                "SELECT groupID FROM jiveGroupPerm WHERE "
            );
            sql.append("forumID=").append(forumID).append(" ");
            sql.append(" AND permission=").append(permissionType);
            ResultSet rs = stmt.executeQuery(sql.toString());
            while (rs.next()) {
                groups.add(rs.getLong(1));
            }
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
        }
        finally {
            try {  stmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
        return new DatabaseObjectIterator(JiveGlobals.GROUP, groups.toArray(),
                factory.getGroupManager());
    }

    public int groupsWithPermissionCount(int permissionType) {
        int groupCount = 0;
        Connection con = null;
        Statement stmt = null;
        try {
            con = ConnectionManager.getConnection();
            stmt = con.createStatement();
            StringBuffer sql = new StringBuffer(
                "SELECT count(groupID) FROM jiveGroupPerm WHERE "
            );
            sql.append("forumID=").append(forumID).append(" ");
            sql.append(" AND permission=").append(permissionType);
            ResultSet rs = stmt.executeQuery(sql.toString());
            if (rs.next()) {
                groupCount = rs.getInt(1);
            }
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
        }
        finally {
            try {  stmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
        return groupCount;
    }

    //OTHER METHODS//

    /**
     * Returns the ForumPermissions object that corresponds to the combined
     * permissions that a user has for a particular forum (or their global
     * permissions if the forumID is -1). This consists of:
     * <ul>
     *      <li> Anonymous and "all registered users" permissions.
     *      <li> Global permissions.
     *      <li> Permissions for groups that the user belongs to.
     *      <li> Specific permissions granted to the user.
     * </ul>
     */
    protected ForumPermissions getFinalUserPerms(long forumID, long userID) {
         UserPermissionsCache userPermsCache = factory.cacheManager.userPermsCache;
        // Simple case: if cache is turned on and the user is already cached,
        // we can simply return the cached permissions.
        ForumPermissions userPerms = userPermsCache.get(forumID, userID);
        if (userPerms != null) {
            return userPerms;
        }

        // Not so simple case: cache is not turned on or the user permissions
        // have not been cached yet.
        boolean isAnonymous = (userID == -1);
        boolean isUser = !isAnonymous;

        ForumPermissions finalPermissions = ForumPermissions.none();

        // Step 1 - Get permissions for the User. This includes anonymous
        // perms, "special user" perms, and the specific perms for the user.
        if (isUser) {
            ForumPermissions userPermissions = getUserPermissions(forumID, userID);
            // Combine permissions
            finalPermissions = new ForumPermissions(finalPermissions, userPermissions);
        }

        // Add in anonymous perms.
        ForumPermissions anonyPermissions = userPermsCache.get(forumID, -1);
        // Otherwise, do our own lookup.
        if (anonyPermissions == null) {
            anonyPermissions = getUserPermissions(forumID, -1);
            // Add to cache so it will be there next time.
            userPermsCache.add(forumID, -1, anonyPermissions);
        }
        // Combine permissions
        finalPermissions = new ForumPermissions(finalPermissions, anonyPermissions);

        // If they are a valid user, figure out "any user" permissions.
        if (isUser) {
            ForumPermissions specialUserPermissions = null;
            // Check for cache
            specialUserPermissions = userPermsCache.get(forumID, 0);

            // Otherwise, do our own lookup.
            if (specialUserPermissions == null) {
                specialUserPermissions = getUserPermissions(forumID, 0);
                // Add to cache so it will be there next time.
                userPermsCache.add(forumID, 0, specialUserPermissions);
            }
            // Combine permissions
            finalPermissions = new ForumPermissions(finalPermissions, specialUserPermissions);
        }

        // Step 2 -- get Permissions for all groups the user is in.
        long [] groups = factory.groupManager.getUserGroups(userID);
        for (int i=0; i<groups.length; i++) {
            ForumPermissions groupPermissions = getGroupPermissions(forumID, groups[i]);
            finalPermissions = new ForumPermissions(finalPermissions, groupPermissions);
        }

        // Finally, add user to cache so it will be there next time.
        if (isUser) {
            userPermsCache.add(forumID, userID, finalPermissions);
        }

        return finalPermissions;
    }

    /**
     * Returns the permissions that a particular user has for the forum.
     */
    protected static ForumPermissions getUserPermissions(long forumID, long userID) {
        Connection con = null;
        Statement stmt = null;
        //Initialize a permissions array with no permissions.
        boolean [] permissions = new boolean[9];
        for (int i=0; i<permissions.length; i++) {
            permissions[i] = false;
        }
        try {
            con = ConnectionManager.getConnection();
            stmt = con.createStatement();
            StringBuffer sql = new StringBuffer(
                "SELECT DISTINCT permission FROM jiveUserPerm WHERE "
            );
            if (forumID == -1) {
                sql.append("forumID IS NULL AND ");
            }
            else {
                sql.append("forumID=").append(forumID).append(" AND ");
            }
            if (userID == -1) {
                sql.append("userID IS NULL and userType=");
                sql.append(JiveGlobals.ANONYMOUS);
            }
            else if (userID == 0) {
                sql.append("userID IS NULL AND userType=");
                sql.append(JiveGlobals.REGISTERED_USERS);
            }
            else {
                sql.append("userID=").append(userID).append(" AND userType=");
                sql.append(JiveGlobals.USER);
            }
            ResultSet rs = stmt.executeQuery(sql.toString());
            while(rs.next()) {
                int newPerm = rs.getInt(1);
                permissions[newPerm] = true;
            }
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
        }
        finally {
            try {  stmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
        return new ForumPermissions(permissions);
    }

    /**
     * Returns the permissions that a particular group has for the forum.
     */
    protected static ForumPermissions getGroupPermissions(long forumID, long groupID) {
        Connection con = null;
        Statement stmt = null;
        //Initialize a permissions array with no permissions.
        boolean [] permissions = new boolean[9];
        for (int i=0; i<permissions.length; i++) {
            permissions[i] = false;
        }
        try {
            con = ConnectionManager.getConnection();
            stmt = con.createStatement();
            StringBuffer sql = new StringBuffer(
                "SELECT DISTINCT permission FROM jiveGroupPerm WHERE "
            );
            if (forumID == -1) {
                sql.append("forumID IS NULL AND ");
            }
            else {
                sql.append("forumID=").append(forumID).append(" AND ");
            }
            sql.append("groupID=").append(groupID);
            ResultSet rs = stmt.executeQuery(sql.toString());
            while(rs.next()) {
                int newPerm = rs.getInt(1);
                permissions[newPerm] = true;
            }
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
        }
        finally {
            try {  stmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
        return new ForumPermissions(permissions);
    }

    private void addUserPermission(long userID, int permissionType) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(ADD_USER_PERM);
            //Set the forum ID
            if (forumID == -1) {
                pstmt.setNull(1, Types.NUMERIC);
            }
            else {
                pstmt.setLong(1, forumID);
            }
            //Set the userID and type
            if (userID == -1) {
                pstmt.setNull(2, Types.NUMERIC);
                pstmt.setInt(3, JiveGlobals.ANONYMOUS);
            }
            else if (userID == 0) {
                pstmt.setNull(2, Types.NUMERIC);
                pstmt.setInt(3, JiveGlobals.REGISTERED_USERS);
            }
            else {
                pstmt.setLong(2, userID);
                pstmt.setInt(3, JiveGlobals.USER);
            }
            //Set the perm type
            pstmt.setInt(4, permissionType);
            pstmt.execute();

            //Now, expire cache. If forum ID is global, we need to do a global
            //expriation
            if (forumID == -1) {
                if (userID == -1 || userID == 0) {
                    factory.cacheManager.userPermsCache.clear();
                }
                else {
                    factory.cacheManager.userPermsCache.removeUserPerm(userID);
                }
            }
            //Otherwise, it's just per forum.
            else {
                if (userID == -1 || userID == 0) {
                    LongCache permsCache = factory.cacheManager.userPermsCache.get(forumID);
                    //If cache is turned on, the cache shouldn't be null.
                    if (permsCache != null) {
                        permsCache.clear();
                    }
                }
                else {
                    factory.cacheManager.userPermsCache.removeUserPerm(userID, forumID);
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
    }

    private void removeUserPermission(long userID, int permissionType) {
        Connection con = null;
        Statement stmt = null;
        try {
            con = ConnectionManager.getConnection();
            stmt = con.createStatement();
            StringBuffer sql = new StringBuffer("DELETE FROM jiveUserPerm WHERE ");
            if (forumID == -1) {
                sql.append("forumID IS NULL ");
            }
            else {
                sql.append("forumID=").append(forumID).append(" ");
            }
            if (userID == -1) {
                sql.append("AND userID IS NULL AND userType=");
                sql.append(JiveGlobals.ANONYMOUS).append(" ");
            }
            else if (userID == 0) {
                sql.append("AND userID IS NULL AND userType=");
                sql.append(JiveGlobals.REGISTERED_USERS).append(" ");
            }
            else {
                sql.append("AND userID=").append(userID).append(" ");
            }
            sql.append("AND permission=").append(permissionType);
            stmt.execute(sql.toString());

            //Now, expire cache. If forum ID is global, we need to do a global
            //expriation.
            UserPermissionsCache cache = factory.cacheManager.userPermsCache;
            if (cache.isEnabled()) {
                if (forumID == -1) {
                    if (userID == -1 || userID == 0) {
                        cache.clear();
                    }
                    else {
                        cache.removeUserPerm(userID);
                    }
                }
                //Otherwise, it's just per forum.
                else {
                    if (userID == -1 || userID == 0) {
                        cache.get(forumID).clear();
                    }
                    else {
                        cache.removeUserPerm(userID, forumID);
                    }
                }
            }
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
        }
        finally {
            try {  stmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    private boolean userHasPermission(long userID, int permissionType) {
        boolean hasPermission = false;
        Connection con = null;
        Statement stmt = null;
        try {
            con = ConnectionManager.getConnection();
            stmt = con.createStatement();
            StringBuffer sql = new StringBuffer(100);
            sql.append("SELECT userID FROM jiveUserPerm WHERE ");
            if (forumID == -1) {
                sql.append("forumID IS NULL ");
            }
            else {
                sql.append("forumID=").append(forumID).append(" ");
            }
            if (userID == -1) {
                sql.append("AND userID IS NULL AND userType=");
                sql.append(JiveGlobals.ANONYMOUS).append(" ");
            }
            else if (userID == 0) {
                sql.append("AND userID IS NULL and userType=");
                sql.append(JiveGlobals.REGISTERED_USERS).append(" ");
            }
            else {
                sql.append("AND userID=").append(userID).append(" ");
            }
            sql.append("AND permission=").append(permissionType);
            ResultSet rs = stmt.executeQuery(sql.toString());
            if (rs.next()) {
                hasPermission = true;
            }
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
        }
        finally {
            try {  stmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
        return hasPermission;
    }
}