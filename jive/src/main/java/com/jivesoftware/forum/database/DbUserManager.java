/**
 * $RCSfile: DbUserManager.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:59 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import java.util.*;
import java.sql.*;
import com.jivesoftware.util.*;
import com.jivesoftware.forum.*;

/**
 * Database implementation of the UserManager interface. It uses the DbUser
 * class along with the jiveUser database table to store and manipulate user
 * information.<p>
 *
 * In some cases, you may wish to plug in your own user system implementation.
 * In that case, you should set the Jive property <tt>UserManager.className</tt>
 * with the name of your UserManager class. Your class must have a public
 * constructor that takes a DbForumFactory object as an argument. The class
 * must also create and return User object implementations as necessary.
 *
 * This UserManager implementation uses two global caches to vastly improve speed:<ul>
 *   <li> DbForumFactory.userMessageCountCache
 *   <li> DbForumFactory.cacheManager.userCache </ul><p>
 *
 * If making your own UserManager implementation, it's highly recommended that
 * you also use these caches. However, if you do, your User implementation
 * must implement Cacheable to be put into cache. Other classes inside of the
 * database implementation know about the caches and expire them as necessary.
 */
public class DbUserManager implements UserManager {

    /** DATABASE QUERIES **/
    private static final String USER_MESSAGE_COUNT =
        "SELECT count(1) FROM jiveMessage WHERE jiveMessage.userID=?";
    private static final String USER_COUNT = "SELECT count(1) FROM jiveUser";
    private static final String ALL_USER_MESSAGES =
        "SELECT messageID FROM jiveMessage WHERE userID=? ORDER BY modifiedDate DESC";
    private static final String DELETE_USER_MESSAGES =
        "UPDATE jiveMessage set userID=NULL WHERE userID=?";
    private static final String DELETE_USER_PERMS =
        "DELETE FROM jiveUserPerm WHERE userID=?";
    private static final String DELETE_USER_GROUPS =
        "DELETE FROM jiveGroupUser WHERE userID=?";
    private static final String DELETE_USER_PROPS =
        "DELETE FROM jiveUserProp WHERE userID=?";
    private static final String DELETE_USER_WATCHES =
        "DELETE FROM jiveWatch WHERE userID=?";
    private static final String DELETE_USER =
        "DELETE FROM jiveUser WHERE userID=?";
    private static final String ALL_USERS =
        "SELECT userID from jiveUser";

    private DbForumFactory factory;
    private UserCache userCache;

    /**
     * Creates a new UserManager.
     */
    public DbUserManager(DbForumFactory factory) {
        this.factory = factory;
        this.userCache = factory.cacheManager.userCache;
    }

    //FROM THE USERMANAGER INTERFACE//

    public User createUser(String username, String password, String email)
            throws UserAlreadyExistsException
    {
        User newUser = null;
        try {
            User existingUser = getUser(username);

            //The user already exists since now exception, so:
            throw new UserAlreadyExistsException();
        }
        catch (UserNotFoundException unfe) {
            //The user doesn't already exist so we can create a new user
            newUser = new DbUser(username, password, email, factory);
        }
        return newUser;
    }

    public User createUser(String username, String password, String name,
            String email, boolean nameVisible, boolean emailVisible,
            Map properties) throws UserAlreadyExistsException
    {
        User newUser = null;
        try {
            User existingUser = getUser(username);

            //The user already exists since now exception, so:
            throw new UserAlreadyExistsException();
        }
        catch (UserNotFoundException unfe) {
            //The user doesn't already exist so we can create a new user
            newUser = new DbUser(username, password, name, email,
                    nameVisible, emailVisible, properties, factory);
        }
        return newUser;
    }

    public User getUser(long userID) throws UserNotFoundException {
        if (!userCache.isEnabled()) {
            return new DbUser(userID, factory);
        }
        //Cache is enabled.
        User user = (User)userCache.get(userID);
        if (user == null) {
            user = new DbUser(userID, factory);
            userCache.add(userID, user);
        }
        return user;
    }

    public User getUser(String username) throws UserNotFoundException {
        // Do a special check to see if chache is disabled. This will let us
        // avoid more db hits than necessary if it is.
        if (!userCache.isEnabled()) {
            return new DbUser(username, factory);
        }
        // Cache is enabled.
        return getUser(getUserID(username));
    }

    public long getUserID(String username) throws UserNotFoundException {
        if (!userCache.isEnabled()) {
            return new DbUser(username, factory).getID();
        }
        // Cache is enabled.
        CacheableLong userIDLong = (CacheableLong)userCache.userIDCache.get(username);
        // If ID wan't found in cache, load it up and put it there.
        if (userIDLong == null) {
            User user = new DbUser(username, factory);
            userIDLong = new CacheableLong(user.getID());
            userCache.userIDCache.add(username, userIDLong);
        }
        return userIDLong.getLong();
    }

    public void deleteUser(User user) throws UnauthorizedException {
        long userID = user.getID();
        // Get array of all user's messages in the system so that
        // we can expire them from cache.
        LongList messages = new LongList();
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(ALL_USER_MESSAGES);
            pstmt.setLong(1, user.getID());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                messages.add(rs.getLong(1));
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

        con = null;
        pstmt = null;
        boolean abortTransaction = false;
        try {
            con = ConnectionManager.getTransactionConnection();
            // Mark all message by user as anonymous
            pstmt = con.prepareStatement(DELETE_USER_MESSAGES);
            pstmt.setLong(1,userID);
            pstmt.execute();
            pstmt.close();
            // Remove all permissions given to user
            pstmt = con.prepareStatement(DELETE_USER_PERMS);
            pstmt.setLong(1,userID);
            pstmt.execute();
            pstmt.close();
            // Remove user from all groups
            pstmt = con.prepareStatement(DELETE_USER_GROUPS);
            pstmt.setLong(1,userID);
            pstmt.execute();
            pstmt.close();
            // Delete all of the users's extended properties
            pstmt = con.prepareStatement(DELETE_USER_PROPS);
            pstmt.setLong(1,userID);
            pstmt.execute();
            pstmt.close();
            // Delete all of user's watches
            pstmt = con.prepareStatement(DELETE_USER_WATCHES);
            pstmt.setLong(1, userID);
            pstmt.execute();
            pstmt.close();
            // Delete the actual user entry
            pstmt = con.prepareStatement(DELETE_USER);
            pstmt.setLong(1,userID);
            pstmt.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
            abortTransaction = true;
        }
        finally {
            try {  pstmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            ConnectionManager.closeTransactionConnection(con, abortTransaction);
        }

        // Expire user's messages from cache
        long [] messagesArray = messages.toArray();
        for (int i=0; i<messagesArray.length; i++) {
            factory.cacheManager.messageCache.remove(messagesArray[i]);
        }
        // Expire user perms cache
        factory.cacheManager.userPermsCache.removeUserPerm(userID);
        // Expire user cache
        factory.cacheManager.userCache.remove(userID);
    }

    public int getUserCount() {
        int count = 0;
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(USER_COUNT);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
        }
        finally {
            try {  pstmt.close();   }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
        return count;
    }

    public Iterator users() {
        int userCount = getUserCount();
        long [] users = new long[userCount];
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(ALL_USERS);
            ResultSet rs = pstmt.executeQuery();
            for (int i=0; i<userCount; i++) {
                rs.next();
                users[i] = rs.getLong(1);
            }
        }
        catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        finally {
            try {  pstmt.close();   }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
        return new DatabaseObjectIterator(JiveGlobals.USER, users, this);
    }

    public Iterator users(int startIndex, int numResults) {
        LongList users = new LongList();
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(ALL_USERS);
            ResultSet rs = pstmt.executeQuery();
            // Move to start of index
            for (int i=0; i<startIndex; i++) {
                rs.next();
            }
            // Now read in desired number of results (or stop if we run out
            // of results).
            for (int i=0; i<numResults; i++) {
                if (rs.next()) {
                    users.add(rs.getLong(1));
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
        return new DatabaseObjectIterator(JiveGlobals.USER, users.toArray(), this);
    }

    public int userMessageCount(User user) {
        // Construct key for the cache entry which is "forumID-userID".
        long key = user.getID();
        // See if the value is in cache.
        CacheableInt msgCount = (CacheableInt)factory.userMessageCountCache.get(key);
        if (msgCount != null) {
            return msgCount.getInt();
        }
        // Not cached, so load from database.
        int count = 0;
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(USER_MESSAGE_COUNT);
            pstmt.setLong(1, user.getID());
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
        // Add to cache.
        msgCount = new CacheableInt(count);
        factory.userMessageCountCache.add(key, msgCount);
        return count;
    }

    public Iterator userMessages(User user,int start, int count) {
        LongList messages = new LongList();
        Connection con = null;
        PreparedStatement pstmt = null;
        int total =userMessageCount(user) ;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(ALL_USER_MESSAGES);
            pstmt.setLong(1, user.getID());
            ResultSet rs = pstmt.executeQuery();
            if (start >= 0 && start < total) {
                  rs.absolute(start+1);
                  do {
                     messages.add(rs.getLong(1));
                  } while (rs.next() && (--count > 0));
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
        return new DatabaseObjectIterator(JiveGlobals.MESSAGE, messages.toArray(),
                factory);
    }
}