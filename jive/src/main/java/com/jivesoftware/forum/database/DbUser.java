/**
 * $RCSfile: DbUser.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:59 $
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
import java.text.*;
import java.sql.*;
import java.security.*;

/**
 * Database implementation of the User interface. Additionally, it filters all
 * HTML tags from fields before returning them for security purposes.<p>
 *
 * The DbUser keeps track of a user's reward points. Optionally, you
 * can specify a initial number of reward points to assign a newly created user
 * by setting the Jive property <tt>rewards.initialPoints</tt>. If no value is
 * set, the default will be 0 points.
 */
public class DbUser implements User, Cacheable {

    /** DATABASE QUERIES **/
    private static final String LOAD_PROPERTIES =
        "SELECT name, propValue FROM jiveUserProp WHERE userID=?";
    private static final String DELETE_PROPERTY =
        "DELETE FROM jiveUserProp WHERE userID=? AND name=?";
    private static final String DELETE_PROPERTIES =
        "DELETE FROM jiveUserProp WHERE userID=?";
    private static final String INSERT_PROPERTY =
        "INSERT INTO jiveUserProp(userID,name,propValue) VALUES(?,?,?)";
    private static final String LOAD_USER_BY_USERNAME =
        "SELECT userID,username,passwordHash,name,nameVisible,email,emailVisible,"+
        "rewardPoints,creationDate,modifiedDate FROM jiveUser WHERE username=?";
    private static final String LOAD_USER_BY_ID =
        "SELECT userID,username,passwordHash,name,nameVisible,email,emailVisible,"+
        "rewardPoints,creationDate,modifiedDate FROM jiveUser WHERE userID=?";
    private static final String INSERT_USER =
        "INSERT INTO jiveUser(userID,username,passwordHash,name,nameVisible," +
        "email,emailVisible,rewardPoints, creationDate,modifiedDate) " +
        "VALUES(?,?,?,?,?,?,?,?,?,?)";
    private static final String SAVE_USER =
        "UPDATE jiveUser SET passwordHash=?,name=?,nameVisible=?,email=?," +
        "emailVisible=?,rewardPoints=?,creationDate=?,modifiedDate=? WHERE " +
        "userID=?";
    private static final String SAVE_REWARD_POINTS =
        "UPDATE jiveUser SET rewardPoints=? WHERE userID=?";

    /**
     * Controls whether extended properties should be lazily loaded (not loaded
     * until requested). If the properties are infrequently used, this provides
     * a great speedup in initial object loading time. However, if your
     * application does use extended properties all the time, you may wish to
     * turn lazy loading off, as it's actually faster in total db lookup time
     * to load everything at once.
     */
    private static final boolean LAZY_PROP_LOADING = true;

    private static final ForumPermissions USER_ADMIN_PERMS =
            new ForumPermissions(false,false,false,true,false,false,false,false,false);

    private static int initialRewardPoints;
    static {
        // Load the default reward points to assign new users. Default is 0.
        initialRewardPoints = 0;
        // Load a user defined value if specified.
        try {
            String points = JiveGlobals.getJiveProperty("rewards.initialPoints");
            if (points != null) {
                initialRewardPoints = Integer.parseInt(points);
            }
        }
        catch(Exception e) { }
    }

    /**
     * User id of -2 means no user id has been set yet. -1 is reserved for
     * "anonymous user" and 0 is reserved for "all users".
     */
    private long id = -2;
    private String username = null;
    private String passwordHash;
    private String name = "";
    private boolean nameVisible = true;
    private String email;
    private boolean emailVisible = true;
    private int rewardPoints;
    private java.util.Date creationDate;
    private java.util.Date modifiedDate;
    private Map properties;

    private DbForumFactory factory;

    /**
     * Create a new DbUser with all required fields.
     *
     * @param username the username for the user.
     * @param password a password for the user.
     * @param email the email address for the user.
     */
    protected DbUser(String username, String password, String email,
            DbForumFactory factory)
    {
        this.id = SequenceManager.nextID(JiveGlobals.USER);
        this.username = username;
        // Compute hash of password.
        this.passwordHash = StringUtils.hash(password);
        this.email = email;

        this.factory = factory;

        // Set the reward points to the default.
        this.rewardPoints = initialRewardPoints;

        long now = System.currentTimeMillis();
        creationDate = new java.util.Date(now);
        modifiedDate = new java.util.Date(now);
        properties = new Hashtable();
        insertIntoDb();
    }

    /**
     * Create a new DbUser with all required AND optional fields. Using this
     * constructor can result in significantly fewer database operations than
     * calling the other create constructor and then using setter methods.
     *
     * @param username the username for the user.
     * @param password a password for the user.
     * @param email the email address for the user.
     */
    protected DbUser(String username, String password, String name, String email,
            boolean nameVisisble, boolean emailVisible, Map properties,
            DbForumFactory factory)
    {
        this.id = SequenceManager.nextID(JiveGlobals.USER);
        this.username = username;
        // Compute hash of password.
        this.passwordHash = StringUtils.hash(password);
        this.email = email;
        if (name != null) {
            this.name = name;
        }
        this.nameVisible = nameVisible;
        this.emailVisible = emailVisible;

        this.rewardPoints = initialRewardPoints;

        this.factory = factory;

        long now = System.currentTimeMillis();
        creationDate = new java.util.Date(now);
        modifiedDate = new java.util.Date(now);

        if (properties != null) {
            // Make sure that the none of the properties are null values.
            Iterator iter = properties.keySet().iterator();
            while (iter.hasNext()) {
                String pName = (String)iter.next();
                if (pName == null || "".equals(pName)) {
                    throw new NullPointerException(
                            "Cannot set property with empty or null value."
                    );
                }
            }
            iter = properties.values().iterator();
            while (iter.hasNext()) {
                String pValue = (String)iter.next();
                if (pValue == null || "".equals(pValue)) {
                    throw new NullPointerException(
                            "Cannot set property with empty or null value."
                    );
                }
            }
            // No null found, set the properties.
            this.properties = properties;
        }
        else {
            this.properties = new Hashtable();
        }
        insertIntoDb();
    }

    /**
     * Load a DbUser object specified by userID.
     *
     * @param userID the userID of the user to load.
     */
    protected DbUser(long userID, DbForumFactory factory)
            throws UserNotFoundException
    {
        if (userID < 1) {
            throw new UserNotFoundException();
        }
        this.id = userID;
        this.factory = factory;
        loadFromDb();
    }

    /**
     * Load a DbUser object specified by username.
     *
     * @param username the username of the user to load.
     */
    protected DbUser(String username, DbForumFactory factory)
            throws UserNotFoundException
    {
        if (username == null) {
            throw new UserNotFoundException();
        }
        this.username = username;
        this.factory = factory;
        loadFromDb();
    }

    //FROM THE USER INTERFACE//

    public long getID() {
        return id;
    }

    public String getUsername() {
        return StringUtils.escapeHTMLTags(username);
    }

    public String getName() {
        return StringUtils.escapeHTMLTags(name);
    }

    public void setName(String name) throws UnauthorizedException {
        this.name = name;
        // Update modified date
        modifiedDate.setTime(System.currentTimeMillis());
        saveToDb();
        // Remove user from cache
        factory.cacheManager.userCache.remove(this.id);
    }

    public boolean isNameVisible() {
        return nameVisible;
    }

    public void setNameVisible(boolean visible) throws UnauthorizedException {
        this.nameVisible = visible;
        // Update modified date
        modifiedDate.setTime(System.currentTimeMillis());
        saveToDb();
        // Remove user from cache
        factory.cacheManager.userCache.remove(this.id);
    }

    public void setPassword(String password) throws UnauthorizedException {
        // Compute hash of password.
        this.passwordHash = StringUtils.hash(password);
        // Update modified date
        modifiedDate.setTime(System.currentTimeMillis());
        saveToDb();
        // Remove user from cache
        factory.cacheManager.userCache.remove(this.id);
    }

    public String getPasswordHash() throws UnauthorizedException {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        // Update modified date
        modifiedDate.setTime(System.currentTimeMillis());
        saveToDb();
        // Remove user from cache
        factory.cacheManager.userCache.remove(this.id);
    }

    public String getEmail() {
        return StringUtils.escapeHTMLTags(email);
    }

    public void setEmail(String email) throws UnauthorizedException {
        this.email = email;
        // Update modified date
        modifiedDate.setTime(System.currentTimeMillis());
        saveToDb();
        // Remove user from cache
        factory.cacheManager.userCache.remove(this.id);
    }

    public boolean isEmailVisible() {
        return emailVisible;
    }

    public void setEmailVisible(boolean visible) throws UnauthorizedException {
        this.emailVisible = visible;
        // Update modified date
        modifiedDate.setTime(System.currentTimeMillis());
        saveToDb();
        // Remove user from cache
        factory.cacheManager.userCache.remove(this.id);
    }

    public java.util.Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(java.util.Date creationDate)
            throws UnauthorizedException
    {
        this.creationDate = creationDate;
        saveToDb();
        // Remove user from cache
        factory.cacheManager.userCache.remove(this.id);
    }

    public java.util.Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(java.util.Date modifiedDate)
            throws UnauthorizedException
    {
        this.modifiedDate = modifiedDate;
        saveToDb();
        // Remove user from cache
        factory.cacheManager.userCache.remove(this.id);
    }

    public String getProperty(String name) {
        if (LAZY_PROP_LOADING && properties == null) {
            loadPropertiesFromDb();
        }
        return StringUtils.escapeHTMLTags((String)properties.get(name));
    }

    public void setProperty(String name, String value) {
        if (LAZY_PROP_LOADING && properties == null) {
            loadPropertiesFromDb();
        }
        // Make sure the property name and value aren't null.
        if (name == null || value == null || "".equals(name) || "".equals(value)) {
            throw new NullPointerException("Cannot set property with empty or null value.");
        }
        properties.put(name, value);
        savePropertiesToDb();
        // Remove user from cache
        factory.cacheManager.userCache.remove(this.id);
    }

    public void deleteProperty(String name) {
        if (LAZY_PROP_LOADING) {
            if (properties == null) {
                loadPropertiesFromDb();
            }
        }
        properties.remove(name);
        deletePropertyFromDb(name);
        // Remove user from cache
        factory.cacheManager.userCache.remove(this.id);
    }

    public Iterator propertyNames() {
        if (LAZY_PROP_LOADING) {
            if (properties == null) {
                loadPropertiesFromDb();
            }
        }
        return Collections.unmodifiableSet(properties.keySet()).iterator();
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints)
            throws SQLException
    {
        // Save old point value in case something goes wrong.
        int oldPoints = this.rewardPoints;
        this.rewardPoints = rewardPoints;
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(SAVE_REWARD_POINTS);
            pstmt.setInt(1, rewardPoints);
            pstmt.setLong(2, this.id);
            pstmt.executeUpdate();
        }
        catch (SQLException sqle) {
            // Switch back to old point value.
            this.rewardPoints = oldPoints;
            // Throw an exception so that the caller knows that the update
            // operation failed.
            throw sqle;
        }
        finally {
            try {  pstmt.close();   }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    public ForumPermissions getPermissions(Authorization authorization) {
        if (authorization.getUserID() == id) {
            return USER_ADMIN_PERMS;
        }
        else {
            return ForumPermissions.none();
        }
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
        size += CacheSizes.sizeOfString(username);      // username
        size += CacheSizes.sizeOfString(passwordHash);  // password
        size += CacheSizes.sizeOfString(name);          // name
        size += CacheSizes.sizeOfString(email);         // email
        size += CacheSizes.sizeOfBoolean();             // nameVisible
        size += CacheSizes.sizeOfBoolean();             // emailVisible
        size += CacheSizes.sizeOfMap(properties);       // properties
        size += CacheSizes.sizeOfDate();                // creationDate
        size += CacheSizes.sizeOfDate();                // modifiedDate
        size += CacheSizes.sizeOfInt();                 // reward points
        size += CacheSizes.sizeOfObject();              // factory

        return size;
    }

    //OTHER METHODS

    /**
     * Returns a String representation of the User object using the username.
     *
     * @return a String representation of the User object.
     */
    public String toString() {
        return username;
    }

    public int hashCode() {
        return (int)id;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object != null && object instanceof DbUser) {
            return id == ((DbUser)object).getID();
        }
        else {
            return false;
        }
    }

    /**
     * Loads properties from the database.
     */
    private synchronized void loadPropertiesFromDb() {
        this.properties = new Hashtable();
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOAD_PROPERTIES);
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                properties.put(rs.getString(1), rs.getString(2));
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

    /**
     * Load the user data from the database.
     */
    private void loadFromDb() throws UserNotFoundException {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            if (username != null) {
                pstmt = con.prepareStatement(LOAD_USER_BY_USERNAME);
                pstmt.setString(1, username);
            }
            else {
                pstmt = con.prepareStatement(LOAD_USER_BY_ID);
                pstmt.setLong(1, id);
            }
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                if (username != null) {
                    throw new UserNotFoundException(
                        "Failed to read user " + username + " from database."
                    );
                }
                else {
                    throw new UserNotFoundException(
                        "Failed to read user with ID " + id + " from database."
                    );
                }
            }
            this.id = rs.getLong(1);
            this.username = rs.getString(2);
            this.passwordHash = rs.getString(3);
            this.name = rs.getString(4);
            this.nameVisible = (rs.getInt(5) == 1);
            this.email = rs.getString(6);
            this.emailVisible = (rs.getInt(7) == 1);
            this.rewardPoints = rs.getInt(8);
            // We trim() the dates before trying to parse them because some
            // databases pad with extra characters when returning the data.
            this.creationDate =
                new java.util.Date(Long.parseLong(rs.getString(9).trim()));
            this.modifiedDate =
                new java.util.Date(Long.parseLong(rs.getString(10).trim()));

            // Now, load any extended message properties
            if (!LAZY_PROP_LOADING) {
                pstmt.close();
                properties = new Hashtable();
                pstmt = con.prepareStatement(LOAD_PROPERTIES);
                pstmt.setLong(1, id);
                rs = pstmt.executeQuery();
                while(rs.next()) {
                    properties.put(rs.getString(1), rs.getString(2));
                }
            }
        }
        catch( SQLException sqle ) {
            throw new UserNotFoundException(
                "Failed to read user " + id + " from database.", sqle
            );
        }
        catch (NumberFormatException nfe) {
            System.err.println("WARNING: There was an error parsing the dates " +
                "returned from the database. Ensure that they're being stored " +
                "correctly.");
            throw new UserNotFoundException("User with id "
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
     * Inserts a new user record into the database.
     */
    private void insertIntoDb() {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(INSERT_USER);
            pstmt.setLong(1, id);
            pstmt.setString(2, username);
            pstmt.setString(3, passwordHash);
            pstmt.setString(4, name);
            pstmt.setInt(5, nameVisible?1:0);
            pstmt.setString(6, email);
            pstmt.setInt(7, emailVisible?1:0);
            pstmt.setInt(8, rewardPoints);
            pstmt.setString(9, StringUtils.dateToMillis(creationDate));
            pstmt.setString(10, StringUtils.dateToMillis(modifiedDate));
            pstmt.executeUpdate();

            // Now, insert properties if there are any.
            if (properties.size() > 0) {
                // Close the previously opened statement.
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

    /**
     * Save the user data to the database.
     */
    private void saveToDb() {
        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            saveToDb(con);
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
        }
        finally {
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    /**
     * Implementation of saveToDb that takes a Connection. This is useful for
     * participating in transactions.
     */
    private void saveToDb(Connection con) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(SAVE_USER);
            pstmt.setString(1, passwordHash);
            pstmt.setString(2, name);
            pstmt.setInt(3, nameVisible?1:0);
            pstmt.setString(4, email);
            pstmt.setInt(5, emailVisible?1:0);
            pstmt.setInt(6, rewardPoints);
            pstmt.setString(7, StringUtils.dateToMillis(creationDate));
            pstmt.setString(8, StringUtils.dateToMillis(modifiedDate));
            pstmt.setLong(9, id);
            pstmt.executeUpdate();
        }
        finally {
            try {  pstmt.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
    }
}