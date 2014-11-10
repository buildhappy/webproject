/**
 * $RCSfile: DbGroup.java,v $
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
 * Database implementation of the Group interface. Most operations on a group
 * require trips to the database, with the exception of the isMember(User)
 * method, which uses a cache to make the test fast.
 *
 * @see Group
 */
public class DbGroup implements Group, Cacheable {

    /** DATABASE QUERIES **/
    private static final String ADD_ADMIN =
        "INSERT INTO jiveGroupUser(groupID,userID,administrator) VALUES(?,?,1)";
    private static final String REMOVE_ADMIN =
        "DELETE FROM jiveGroupUser WHERE groupID=? AND userID=? AND administrator=1";
    private static final String ADD_USER =
        "INSERT INTO jiveGroupUser(groupID,userID,administrator) VALUES(?,?,0)";
    private static final String REMOVE_USER =
        "DELETE FROM jiveGroupUser WHERE groupID=? AND userID=? AND administrator=0";
    private static final String ADMIN_TEST =
        "SELECT userID FROM jiveGroupUser WHERE groupID=? AND userID=? AND " +
        "administrator=1";
    private static final String MEMBER_TEST =
        "SELECT userID FROM jiveGroupUser WHERE groupID=? AND userID=?";
    private static final String ADMIN_COUNT =
        "SELECT count(*) FROM jiveGroupUser WHERE groupID=? " +
        "AND administrator=1";
    private static final String MEMBER_COUNT =
        "SELECT DISTINCT count(userID) FROM jiveGroupUser " +
        "WHERE groupID=?";
    private static final String LOAD_ADMINS =
        "SELECT userID FROM jiveGroupUser WHERE administrator=1 AND groupID=?";
    private static final String LOAD_USERS =
        "SELECT userID FROM jiveGroupUser WHERE groupID=?";
    private static final String LOAD_GROUP_BY_ID =
        "SELECT * FROM jiveGroup WHERE groupID=?";
    private static final String LOAD_GROUP_BY_NAME =
        "SELECT * FROM jiveGroup WHERE name=?";
    private static final String INSERT_GROUP =
        "INSERT INTO jiveGroup(name,description,groupID,creationDate," +
        "modifiedDate) VALUES(?,?,?,?,?)";
    private static final String SAVE_GROUP =
        "UPDATE jiveGroup SET name=?, description=?, creationDate=?, " +
        "modifiedDate=? WHERE groupID=?";
    private static final String UPDATE_MODIFIED_DATE =
        "UPDATE jiveGroup SET modifiedDate=? WHERE groupID=?";
    private static final String LOAD_PROPERTIES =
        "SELECT name, propValue FROM jiveGroupProp WHERE groupID=?";
    private static final String DELETE_PROPERTY =
        "DELETE FROM jiveGroupProp WHERE groupID=? AND name=?";
    private static final String DELETE_PROPERTIES =
        "DELETE FROM jiveGroupProp WHERE groupID=?";
    private static final String INSERT_PROPERTY =
        "INSERT INTO jiveGroupProp(userID,name,propValue) VALUES(?,?,?)";

    private long id;
    private String name = null;
    private String description = "";
    private java.util.Date creationDate;
    private java.util.Date modifiedDate;
    private Map properties;

    private DbForumFactory factory;

    /**
     * Cache to make the isMember(User) test fast.
     */
    private LongCache memberCache = new LongCache(4*1024, 6*JiveGlobals.HOUR);

    /**
     * Creates a new group.
     *
     * @param the name of the group.
     * @param factory a ForumFactory that can be used to get user and group
     *    managers.
     */
    protected DbGroup(String name, DbForumFactory factory) {
        this.name = name;
        this.factory = factory;
        this.id = SequenceManager.nextID(JiveGlobals.GROUP);
        long now = System.currentTimeMillis();
        creationDate = new java.util.Date(now);
        modifiedDate = new java.util.Date(now);
        properties = new Hashtable();
        insertIntoDb();
    }

    /**
     * Loads a group from the database based on its id.
     *
     * @param id the id of the group to load.
     * @param factory a ForumFactory that can be used to get user and group
     *    managers.
     */
    protected DbGroup(long id, DbForumFactory factory)
            throws GroupNotFoundException
    {
        this.id = id;
        this.factory = factory;
        loadFromDb();
    }

    /**
     * Loads a group from the database based on its name. The implementation
     * of this method is rather hackish since it includes a fake parameter just
     * so that it can have a different method signature than the first
     * constructor. Even so, this methodology makes this class behave more like
     * our other classes, so we're gleefully leaving it this way. :)
     *
     * @param name the name of the group to load.
     * @param fake a fake paramater that can always be null.
     * @param factory a ForumFactory that can be used to get user and group
     *    managers.
     */
    protected DbGroup(String name, Object fake, DbForumFactory factory)
            throws GroupNotFoundException
    {
        this.name = name;
        this.factory = factory;
        loadFromDb();
    }

    // FROM THE USER INTERFACE //

    public long getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws UnauthorizedException {
        this.name = name;
        saveToDb();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description)
            throws UnauthorizedException
    {
        this.description = description;
        saveToDb();
    }

    public java.util.Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(java.util.Date creationDate)
            throws UnauthorizedException
    {
        this.creationDate = creationDate;
        saveToDb();
    }

    public java.util.Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(java.util.Date modifiedDate)
            throws UnauthorizedException
    {
        this.modifiedDate = modifiedDate;
        saveToDb();
    }

    public String getProperty(String name) {
        return (String)properties.get(name);
    }

    public void setProperty(String name, String value) {
        properties.put(name, value);
        savePropertiesToDb();
    }

    public void deleteProperty(String name) {
        properties.remove(name);
        deletePropertyFromDb(name);
    }

    public Iterator propertyNames() {
        return Collections.unmodifiableSet(properties.keySet()).iterator();
    }

    public void addAdministrator(User user) throws UnauthorizedException {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            // Update the modified date.
            updateModifiedDate(System.currentTimeMillis(), con);
            // Add the administrator.
            pstmt = con.prepareStatement(ADD_ADMIN);
            pstmt.setLong(1, id);
            pstmt.setLong(2, user.getID());
            pstmt.execute();
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

        // Now, remove the user from the user perm cache since being in the
        // group could affect their permissions.
        factory.cacheManager.userPermsCache.removeUserPerm(user.getID());
    }

    public void removeAdministrator(User user) throws UnauthorizedException {
        long userID = user.getID();
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            // Update the modified date.
            updateModifiedDate(System.currentTimeMillis(), con);
            // Remove the admin.
            pstmt = con.prepareStatement(REMOVE_ADMIN);
            pstmt.setLong(1, id);
            pstmt.setLong(2, userID);
            pstmt.execute();
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

        // Remove user from member cache
        memberCache.remove(userID);

        // Now, remove the user from the USER_PERM_CACHE since being in the
        // group could affect their permissions.
        factory.cacheManager.userPermsCache.removeUserPerm(userID);
    }

    public void addMember(User user) throws UnauthorizedException {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            // Update the modified date.
            updateModifiedDate(System.currentTimeMillis(), con);
            // Add the user.
            pstmt = con.prepareStatement(ADD_USER);
            pstmt.setLong(1, id);
            pstmt.setLong(2, user.getID());
            pstmt.execute();
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

        memberCache.add(user.getID(), new CacheableBoolean(true));

        // Now, remove the user from the USER_PERM_CACHE since being in the
        // group could affect their permissions.
        factory.cacheManager.userPermsCache.removeUserPerm(user.getID());
    }

    public void removeMember(User user) throws UnauthorizedException {
        long userID = user.getID();
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            // Update the modified date.
            updateModifiedDate(System.currentTimeMillis(), con);
            // Remove the user.
            pstmt = con.prepareStatement(REMOVE_USER);
            pstmt.setLong(1, id);
            pstmt.setLong(2, userID);
            pstmt.execute();

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

        // Remove user from member cache
        memberCache.remove(userID);

        // Now, remove the user from the USER_PERM_CACHE since being in the
        // group could affect their permissions.
        factory.cacheManager.userPermsCache.removeUserPerm(userID);
    }

    public boolean isAdministrator(User user) {
        boolean answer = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(ADMIN_TEST);
            pstmt.setLong(1, id);
            pstmt.setLong(2, user.getID());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                answer = true;
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
        return answer;
    }

    public boolean isMember(User user) {
        CacheableBoolean bool = null;
        bool = (CacheableBoolean)memberCache.get(user.getID());
        if (bool == null ) {
            bool = new CacheableBoolean(false);
            Connection con = null;
            PreparedStatement pstmt = null;
            try {
                con = ConnectionManager.getConnection();
                pstmt = con.prepareStatement(MEMBER_TEST);
                pstmt.setLong(1, id);
                pstmt.setLong(2, user.getID());
                ResultSet rs = pstmt.executeQuery();
                // If there is a result, then the user is a member of the group.
                if (rs.next()) {
                    bool = new CacheableBoolean(true);
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
            // Add to cache
            memberCache.add(user.getID(), bool);
        }
        return bool.getBoolean();
    }

    public int getAdministratorCount() {
        int count = 0;
        boolean answer = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(ADMIN_COUNT);
            pstmt.setLong(1, id);
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

    public int getMemberCount() {
        int count = 0;
        boolean answer = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(MEMBER_COUNT);
            pstmt.setLong(1, id);
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

    public Iterator members() {
        Connection con = null;
        PreparedStatement pstmt = null;
        long [] users = new long[getMemberCount()];
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOAD_USERS);
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            for(int i=0; i<users.length; i++) {
                rs.next();
                users[i] = rs.getLong(1);
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
        return new DatabaseObjectIterator(JiveGlobals.USER, users,
                factory.getUserManager());
    }

    public Iterator administrators() {
        Connection con = null;
        PreparedStatement pstmt = null;
        long [] admins = new long[getAdministratorCount()];
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOAD_ADMINS);
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            for(int i=0; i<admins.length; i++) {
                rs.next();
                admins[i] = rs.getLong(1);
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
        return new DatabaseObjectIterator(JiveGlobals.USER, admins,
                factory.getUserManager());
    }

    public ForumPermissions getPermissions(Authorization authorization) {
        long userID = authorization.getUserID();
        try {
            User user = factory.getUserManager().getUser(userID);
            if (isAdministrator(user)) {
                return new ForumPermissions(false, false, false, false,
                        true, false, false, false, false);
            }
        }
        catch (Exception e) { }

        return ForumPermissions.none();
    }

    public boolean hasPermission(int type) {
        return true;
    }

    //FROM THE CACHEABLE INTERFACE//

    public int getSize() {
        // Approximate the size of the object in bytes by calculating the size
        // of each field.
        int size = 0;
        size += CacheSizes.sizeOfObject();              // overhead of object
        size += CacheSizes.sizeOfLong();                // id
        size += CacheSizes.sizeOfString(name);          // name
        size += CacheSizes.sizeOfString(description);   // description
        size += CacheSizes.sizeOfMap(properties);       // properties
        size += CacheSizes.sizeOfObject();              // forum factory ref.
        size += memberCache.getMaxSize();               // member cache

        return size;
    }

    //OTHER METHODS

    /**
     * Returns a String representation of the Group object using the group name.
     *
     * @return a String representation of the Group object.
     */
    public String toString() {
        return name;
    }

    public int hashCode() {
        return (int)id;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object != null && object instanceof DbGroup) {
            return id == ((DbGroup)object).getID();
        }
        else {
            return false;
        }
    }

    /**
     * Updates the modified date. It accepts a Connection so that it can
     * participate in trasactions.
     */
    protected void updateModifiedDate(long date, Connection con)
            throws SQLException
    {
        this.modifiedDate.setTime(date);
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(UPDATE_MODIFIED_DATE);
            pstmt.setString(1, StringUtils.dateToMillis(modifiedDate));
            pstmt.setLong(2, id);
            pstmt.executeUpdate();
        }
        finally {
            try {  pstmt.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    /**
     * Load the group data from the database.
     */
    private synchronized void loadFromDb() throws GroupNotFoundException {
        String query;
        if (name == null) {
            query = LOAD_GROUP_BY_ID;
        }
        else {
            query = LOAD_GROUP_BY_NAME;
        }
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(query);
            if (name == null) {
                pstmt.setLong(1, id);
            }
            else {
                pstmt.setString(1, name);
            }
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                throw new GroupNotFoundException();
            }
            this.id = rs.getLong("groupID");
            this.name = rs.getString("name");
            this.description = rs.getString("description");
            // We trim() the dates before trying to parse them because some
            // databases pad with extra characters when returning the data.
            this.creationDate =
                new java.util.Date(Long.parseLong(rs.getString("creationDate").trim()));
            this.modifiedDate =
                new java.util.Date(Long.parseLong(rs.getString("modifiedDate").trim()));
            pstmt.close();

            // Load any extended message properties.
            properties = new Hashtable();
            pstmt = con.prepareStatement(LOAD_PROPERTIES);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                // Add in name, value as a new property.
                properties.put(rs.getString(1), rs.getString(2));
            }
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
            throw new GroupNotFoundException();
        }
        catch (NumberFormatException nfe) {
            System.err.println("WARNING: There was an error parsing the dates " +
                "returned from the database. Ensure that they're being stored " +
                "correctly.");
            throw new GroupNotFoundException( "Group with id "
                    + id + " could not be loaded from the database."
            );
        }
        finally {
            try {  pstmt.close();   }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    /**
     * Inserts a new record into the database.
     */
    private void insertIntoDb() {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(INSERT_GROUP);
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setLong(3, id);
            pstmt.setString(4, StringUtils.dateToMillis(creationDate));
            pstmt.setString(5, StringUtils.dateToMillis(modifiedDate));
            pstmt.executeUpdate();
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

    /**
     * Saves group data to the db.
     */
    private synchronized void saveToDb() {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(SAVE_GROUP);
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setString(3, StringUtils.dateToMillis(creationDate));
            pstmt.setString(4, StringUtils.dateToMillis(modifiedDate));
            pstmt.setLong(5, id);
            pstmt.executeUpdate();
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

    /**
     * Saves properties to the database.
     */
    private synchronized void savePropertiesToDb() {
        boolean abortTransaction = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getTransactionConnection();
            // Delete all old values.
            pstmt = con.prepareStatement(DELETE_PROPERTIES);
            pstmt.setLong(1, id);
            pstmt.execute();
            pstmt.close();
            // Now insert new values.
            pstmt = con.prepareStatement(INSERT_PROPERTY);
            Iterator iter = properties.keySet().iterator();
            while (iter.hasNext()) {
                String name = (String)iter.next();
                String value = (String)properties.get(name);
                pstmt.setLong(1, id);
                pstmt.setString(2, name);
                pstmt.setString(3, value);
                pstmt.executeUpdate();
            }
        }
        catch( SQLException sqle ) {
           sqle.printStackTrace();
           abortTransaction = true;
        }
        finally {
            try {  pstmt.close();   }
            catch (Exception e) { e.printStackTrace(); }
            ConnectionManager.closeTransactionConnection(con, abortTransaction);
        }
    }

    /**
     * Deletes a property from the db.
     */
    private synchronized void deletePropertyFromDb(String name) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
           con = ConnectionManager.getConnection();
           pstmt = con.prepareStatement(DELETE_PROPERTY);
           pstmt.setLong(1, id);
           pstmt.setString(2, name);
           pstmt.execute();
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
    }
}