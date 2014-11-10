/**
 * $RCSfile: DbForumMessage.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:54 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

import com.jivesoftware.forum.*;
import com.jivesoftware.util.Cacheable;
import com.jivesoftware.util.CacheSizes;
import com.jivesoftware.util.StringUtils;

/**
 * Database implementation of the ForumMessage interface. It stores messages
 * in the jiveMessage database table, and message properties in JiveMessageProp
 * table.<p>
 *
 * Messages are not inserted into the database until they are added
 * to a thread. Therefore, it is best to set all properties of the message
 * before adding it to a thread in order to avoid extra database operations.<p>
 *
 * One key feature of the Jive message architecture is the ability to apply
 * dynamic filters to messages (specified at the global and forum level).
 * However, computing the filtered value of a message can be fairly expensive.
 * Therefore,
 */
public final class DbForumMessage implements ForumMessage, Cacheable {

    /** DATABASE QUERIES **/
    private static final String LOAD_PROPERTIES =
        "SELECT name, propValue FROM jiveMessageProp WHERE messageID=?";
    private static final String INSERT_PROPERTY =
        "INSERT INTO jiveMessageProp(messageID,name,propValue) VALUES(?,?,?)";
    private static final String DELETE_PROPERTY =
        "DELETE FROM jiveMessageProp WHERE messageID=? AND name=?";
    private static final String DELETE_PROPERTIES =
        "DELETE FROM jiveMessageProp WHERE messageID=?";
    private static final String LOAD_MESSAGE =
        "SELECT threadID, forumID, userID, subject, body, modValue, rewardPoints, "+
        "creationDate, modifiedDate FROM jiveMessage WHERE messageID=?";
    private static final String INSERT_MESSAGE =
        "INSERT INTO jiveMessage(messageID, parentMessageID, threadID, forumID, " +
        "userID, subject, body, modValue, rewardPoints, creationDate, modifiedDate) " +
        "VALUES(?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SAVE_MESSAGE =
        "UPDATE jiveMessage SET userID=?, subject=?, body=?, modValue=?, " +
        "rewardPoints=?, creationDate=?, modifiedDate=? WHERE messageID=?";
    private static final String INSERT_MODERATION_ENTRY =
        "INSERT INTO jiveModeration(objectID, objectType, userID, modDate, " +
        "modValue) VALUES(?,?,?,?,?)";

    /**
     * Controls whether extended properties should be lazily loaded (not loaded
     * until requested). If the properties are infrequently used, this provides
     * a great speedup in initial object loading time. However, if your
     * application does use extended properties all the time, you may wish to
     * turn lazy loading off, as it's actually faster in total db lookup time
     * to load everything at once.
     *
     * NOTE: lazy loading turned off by default for now since new filter
     * caching code always calls properties. This may be optimized in the
     * future so that lazy property loading makes sense again.
     */
    private static final boolean LAZY_PROP_LOADING = false;

    private long id = -1;
    private java.util.Date creationDate;
    private java.util.Date modifiedDate;
    private String subject = "";
    private String body = "";
    private long userID;
    // Default the moderation point value to Integer.MIN_VALUE. Then, if the
    // moderation value gets changed before being inserted into the db, we'll know.
    private int moderationValue = Integer.MIN_VALUE;
    private int rewardPoints = 0;
    private Map properties = null;

    // Variables to hold filtered values.
    protected String filteredSubject = null;
    protected String filteredBody = null;
    protected Map filteredProperties = null;

    private DbForumFactory factory;
    protected long threadID = -1;
    protected long forumID = -1;

    /**
     * Indicates if the object is ready to be saved or not. An object is not
     * ready to be saved if it has just been created and has not yet been added
     * to its container. For example, a message added to a thread, etc.
     */
    private boolean isReadyToSave = false;

    /**
     * Creates a new DbForumMessage object. If the User object is null, we'll
     * assume that the message has an anonymous author.
     */
    protected DbForumMessage(User user, DbForumFactory factory) {
        this.factory = factory;
        if (user != null) {
            this.userID = user.getID();
        }
        else {
            this.userID = -1;
        }

        this.id = SequenceManager.nextID(JiveGlobals.MESSAGE);
        long now = System.currentTimeMillis();
        creationDate = new java.util.Date(now);
        modifiedDate = new java.util.Date(now);
        properties = new Hashtable();
    }

    /**
     * Loads the specified DbForumMessage by its message id.
     */
    protected DbForumMessage(long id, DbForumFactory factory)
            throws ForumMessageNotFoundException
    {
        this.id = id;
        this.factory = factory;
        loadFromDb();
        isReadyToSave = true;
    }

    //FROM THE FORUMMESSAGE INTERFACE//

    public long getID() {
        return id;
    }

    public java.util.Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(java.util.Date creationDate)
            throws UnauthorizedException
    {
        this.creationDate = creationDate;
        // Only save to the db if the object is read
        if (!isReadyToSave) {
            return;
        }
        saveToDb();
        // Remove message from cache
        factory.cacheManager.messageCache.remove(this.id);
    }

    public java.util.Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(java.util.Date modifiedDate)
            throws UnauthorizedException
    {
        this.modifiedDate = modifiedDate;
        // Only save to the db if the object is read
        if (!isReadyToSave) {
            return;
        }
        saveToDb();
        // Remove message from cache
        factory.cacheManager.messageCache.remove(this.id);
    }

    public String getSubject() {
        // Return cached filter value if it exists.
        if (filteredSubject != null) {
            return filteredSubject;
        }
        // Otherwise, return normal value.
        else {
            return subject;
        }
    }

    public String getUnfilteredSubject() {
        return subject;
    }

    public void setSubject(String subject) throws UnauthorizedException {
        this.subject = subject;
        // Only save to the db if the object is read
        if (!isReadyToSave) {
            return;
        }
        // Update modifiedDate to the current time.
        modifiedDate.setTime(System.currentTimeMillis());
        saveToDb();
        // Remove message from cache
        factory.cacheManager.messageCache.remove(this.id);
    }

    public String getBody() {
        // Return cached filter value if it exists.
        if (filteredBody != null) {
            return filteredBody;
        }
        // Otherwise, return normal value.
        else {
            return body;
        }
    }

    public String getUnfilteredBody() {
        return body;
    }

    public void setBody(String body) throws UnauthorizedException {
        this.body = body;
        // Only save to the db if the object is read
        if (!isReadyToSave) {
            return;
        }
        // Update modifiedDate to the current time.
        modifiedDate.setTime(System.currentTimeMillis());
        saveToDb();
        // Remove message from cache
        factory.cacheManager.messageCache.remove(this.id);
    }

    public User getUser() {
        if (userID == -1) {
            return null;
        }
        User user = null;
        try {
            user = factory.getUserManager().getUser(userID);
        }
        catch (UserNotFoundException unfe) {
            unfe.printStackTrace();
        }
        return user;
    }

    public int getModerationValue() {
        return moderationValue;
    }

    public void setModerationValue(int value, Authorization auth) {
        // If the new value is the same as the old value, return.
        if (moderationValue == value) {
            return;
        }

        int oldValue = moderationValue;
        moderationValue = value;
        // Only save to the db if the object is ready
        if (!isReadyToSave) {
            return;
        }
        saveToDb();

        long now = System.currentTimeMillis();
        // We modified the moderation value of the message, so update the
        // modified date.
        this.modifiedDate.setTime(now);
        // If the message is switching from hidden to visible, we should update
        // the modified date of the thread and forum it belongs to. We'll also
        // trigger a watch update and send out the message to gateways. These
        // last actions have the potential for problems since it's possible that
        // a message could switch from being below the moderation threshold to
        // above multiple times. However, it's probably ok to let there be
        // be multiple watch updates, and the gateway implementations should
        // ensure that no message gets exported twice.
        try {
            DbForum dbForum = factory.cacheManager.forumCache.get(this.forumID);
            if (oldValue < dbForum.getModerationMinMessageValue()
                    && value >= dbForum.getModerationMinMessageValue())
            {
                ForumThread thread = dbForum.getThread(this.threadID);
                thread.setModifiedDate(modifiedDate);
                dbForum.setModifiedDate(modifiedDate);

                // Notify the watch manager that the thread has been updated.
                factory.watchManager.notifyWatches(thread);
                // Notify the gateway manager that we're ready to be exported.
//                dbForum.getGatewayManager().exportData(this);
            }
        }
        catch (ForumNotFoundException fnfe) { /* ignore */ }
        catch (ForumThreadNotFoundException ftnfe) { /* ignore */ }
        catch (UnauthorizedException ue) { /* ignore */ }

        // Remove message from cache
        factory.cacheManager.messageCache.remove(this.id);
        // Also remove thread from cache
        factory.cacheManager.threadCache.remove(this.threadID);

        // Finally, make an entry into the jiveModeration table for moderation
        // auditing purposes.
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(INSERT_MODERATION_ENTRY);
            pstmt.setLong(1, id);
            pstmt.setInt(2, JiveGlobals.MESSAGE);
            if (auth.isAnonymous()) {
                pstmt.setNull(3, Types.NUMERIC);
            }
            else {
                pstmt.setLong(3, auth.getUserID());
            }
            pstmt.setString(4, StringUtils.dateToMillis(modifiedDate));
            pstmt.setInt(5, moderationValue);
            pstmt.executeUpdate();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {  pstmt.close();   }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    public String getProperty(String name) {
        if (LAZY_PROP_LOADING) {
            if (properties == null && isReadyToSave) {
                loadPropertiesFromDb();
            }
        }
        // Return the cached filter value if it exists.
        if (filteredProperties != null) {
            return (String)filteredProperties.get(name);
        }
        // Otherwise, return normal value.
        else {
            return (String)properties.get(name);
        }
    }

    public String getUnfilteredProperty(String name) {
        if (LAZY_PROP_LOADING) {
            if (properties == null && isReadyToSave) {
                loadPropertiesFromDb();
            }
        }
        return (String)properties.get(name);
    }

    public void setProperty(String name, String value) {
        if (LAZY_PROP_LOADING) {
            if (properties == null && isReadyToSave) {
                loadPropertiesFromDb();
            }
        }
        properties.put(name, value);
        // Only save to the db if the object is read
        if (!isReadyToSave) {
            return;
        }
        // Open a database connection and start a transaction to save properties.
        boolean abortTransaction = false;
        Connection con = null;
        try {
            con = ConnectionManager.getTransactionConnection();
            savePropertiesToDb(con, true);
        }
        catch( Exception e ) {
            e.printStackTrace();
            abortTransaction = true;
        }
        finally {
            ConnectionManager.closeTransactionConnection(con, abortTransaction);
        }
        // Remove message from cache
        factory.cacheManager.messageCache.remove(this.id);
    }

    public void deleteProperty(String name)
            throws UnauthorizedException
    {
        if (LAZY_PROP_LOADING) {
            if (properties == null && isReadyToSave) {
                loadPropertiesFromDb();
            }
        }
        properties.remove(name);
        // Only save to the db if the object is ready
        if (!isReadyToSave) {
            return;
        }
        deletePropertyFromDb(name);
        // Remove message from cache
        factory.cacheManager.messageCache.remove(this.id);
    }

    public Iterator propertyNames() {
        if (LAZY_PROP_LOADING) {
            if (properties == null && isReadyToSave) {
                loadPropertiesFromDb();
            }
        }
        return Collections.unmodifiableSet(properties.keySet()).iterator();
    }

    public boolean isAnonymous() {
        return (userID == -1);
    }

    public ForumThread getForumThread() {
        if (threadID == -1) {
            return null;
        }
        try {
            return factory.cacheManager.threadCache.get(threadID);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean hasPermission(int type) {
        return true;
    }

    //FROM CACHEABLE INTERFACE//

    public int getSize() {
        // Approximate the size of the object in bytes by calculating the size
        // of each field. Filtered variable sizes are approximated since they
        // are lazily loaded.
        int size = 0;
        size += CacheSizes.sizeOfObject();              // overhead of object
        size += CacheSizes.sizeOfLong();                // id
        size += CacheSizes.sizeOfString(subject) * 2;   // subject & filtered
        size += CacheSizes.sizeOfString(body) * 2;      // body & filtered
        size += CacheSizes.sizeOfDate();                // creation date
        size += CacheSizes.sizeOfDate();                // modified date
        size += CacheSizes.sizeOfLong();                // userID
        size += CacheSizes.sizeOfLong();                // threadID
        size += CacheSizes.sizeOfLong();                // forumID
        size += CacheSizes.sizeOfMap(properties) * 2;   // map object * filtered
        size += CacheSizes.sizeOfObject();              // ref to factory
        size += CacheSizes.sizeOfInt();                 // moderation points
        size += CacheSizes.sizeOfInt();                 // reward points

        return size;
    }

    //OTHER METHODS//

    /**
     * Returns a String representation of the message object using the subject.
     *
     * @return a String representation of the ForumMessage object.
     */
    public String toString() {
        return subject;
    }

    public int hashCode() {
        return (int)id;
    }


    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object != null && object instanceof DbForumMessage) {
            return id == ((DbForumMessage)object).getID();
        }
        else {
            return false;
        }
    }

    /**
     * Returns the number of reward points for the thread.
     */
    protected int getRewardPoints() {
        return rewardPoints;
    }

    /**
     * Sets the number of reward points for the thread.
     */
    protected void setRewardPoints(int rewardPoints, Connection con)
            throws SQLException
    {
        // Save old point value in case something goes wrong.
        int oldPoints = this.rewardPoints;
        this.rewardPoints = rewardPoints;
        try {
            saveToDb(con);
        }
        catch (SQLException sqle) {
            // Switch back to old point value.
            this.rewardPoints = oldPoints;
            throw sqle;
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
     * Saves properties to the database. Normally, this method will clear out
     * all old property values before saving new ones. However, the deletion
     * step will be skipped if specified. This is useful for the case that you
     * know that no current properties could possibly exist.
     *
     * @param con a database connection to be used for property saving.
     * @param deleteOldProps true if old properties should be deleted before
     *      saving new ones. This should be true in most cases.
     */
    private synchronized void savePropertiesToDb(Connection con,
            boolean deleteOldProps) throws SQLException
    {
        PreparedStatement pstmt = null;
        try {
            if (deleteOldProps) {
                // Delete all old values.
                pstmt = con.prepareStatement(DELETE_PROPERTIES);
                pstmt.setLong(1, id);
                pstmt.execute();
                pstmt.close();
            }
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
        finally {
            try {  pstmt.close();   }
            catch (Exception e) { e.printStackTrace(); }
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
     *  Loads message and user data from the database.
     */
    private void loadFromDb() throws ForumMessageNotFoundException {
        // Based on the id in the object, get the message data from the database.
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOAD_MESSAGE);
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                throw new ForumMessageNotFoundException("Message " + id +
                    " could not be loaded from the database.");
            }
            this.threadID = rs.getLong(1);
            this.forumID = rs.getLong(2);
            this.userID = rs.getLong(3);
            if (rs.wasNull()) {
                userID = -1;
            }
            this.subject = rs.getString(4);
            // The Oracle JDBC driver is lame and returns the String "null" for
            // null columns.
            if (rs.wasNull()) {
                this.subject = null;
            }
            // Use the connection manager to get the body field.
            this.body = ConnectionManager.getLargeTextField(rs, 5);
            this.moderationValue = rs.getInt(6);
            this.rewardPoints = rs.getInt(7);
            // We trim() the dates before trying to parse them because some
            // databases pad with extra characters when returning the data.
            this.creationDate =
                new java.util.Date(Long.parseLong(rs.getString(8).trim()));
            this.modifiedDate =
                new java.util.Date(Long.parseLong(rs.getString(9).trim()));

            // Load any extended message properties.
            if (!LAZY_PROP_LOADING) {
                pstmt.close();
                properties = new Hashtable();
                pstmt = con.prepareStatement(LOAD_PROPERTIES);
                pstmt.setLong(1, id);
                rs = pstmt.executeQuery();
                while(rs.next()) {
                    // Add in name, value as a new property.
                    properties.put(rs.getString(1), rs.getString(2));
                }
            }
         }
        catch( SQLException sqle ) {
            throw new ForumMessageNotFoundException( "Message with id "
                    + id + " could not be loaded from the database."
            );
        }
        catch (NumberFormatException nfe) {
            System.err.println("WARNING: In DbForumMessage.loadFromDb() -- there " +
                "was an error parsing the dates returned from the database. Ensure " +
                "that they're being stored correctly.");
            throw new ForumMessageNotFoundException( "Message with id "
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
     * Inserts a new message into the database. A connection object must
     * be passed in. The connection must be open when passed in, and will
     * remain open when passed back. This method allows us to make insertions
     * be transactional.
     *
     * @param con an open Connection used to insert the thread to the db.
     * @param thread the ForumThread the message is being added to.
     */
    protected void insertIntoDb(DbForumThread thread, long parentMessageID,
            Connection con) throws SQLException
    {
        // Set the message threadID, forumID
        this.threadID = thread.getID();
        // Get forumID and moderation value.
        Forum forum = thread.getForum();
        this.forumID = forum.getID();
        // If the moderation value hasn't already been set, use the default.
        if (moderationValue == Integer.MIN_VALUE) {
            moderationValue = forum.getModerationDefaultMessageValue();
        }

        // Now, insert the message to the db.
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(INSERT_MESSAGE);
            pstmt.setLong(1, id);
            if (parentMessageID == -1) {
                pstmt.setNull(2, Types.NUMERIC);
            }
            else {
                pstmt.setLong(2, parentMessageID);
            }
            pstmt.setLong(3, threadID);
            pstmt.setLong(4, thread.forumID);
            if (userID == -1) {
                pstmt.setNull(5, Types.NUMERIC);
            }
            else {
                pstmt.setLong(5, userID);
            }
            pstmt.setString(6, subject);
            // Use the connection manager to set the body field.
            ConnectionManager.setLargeTextField(pstmt, 7, body);
            pstmt.setInt(8, moderationValue);
            pstmt.setInt(9, rewardPoints);
            pstmt.setString(10, StringUtils.dateToMillis(creationDate));
            pstmt.setString(11, StringUtils.dateToMillis(modifiedDate));
            pstmt.executeUpdate();
        }
        finally {
            try {  pstmt.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }

        // We're done inserting the message, so now save any extended
        // properties to the database.
        if (this.properties.size() > 0) {
            savePropertiesToDb(con, false);
        }

        // Since we're done inserting the object to the database, it is ready
        // for future saves.
        isReadyToSave = true;
    }

    /**
     * Saves message data to the database.
     */
    private synchronized void saveToDb() {
        boolean abortTransaction = false;
        Connection con = null;
        try {
            con = ConnectionManager.getTransactionConnection();
            saveToDb(con);
        }
        catch( Exception e ) {
            e.printStackTrace();
            abortTransaction = true;
        }
        finally {
            ConnectionManager.closeTransactionConnection(con, abortTransaction);
        }
    }

    /**
     * Implementation of saveToDb that takes a Connection. This is useful for
     * participating in transactions.
     */
    private synchronized void saveToDb(Connection con) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(SAVE_MESSAGE);
            pstmt.setLong(1, userID);
            pstmt.setString(2, subject);
            // Use the connection manager to set the body field.
            ConnectionManager.setLargeTextField(pstmt, 3, body);
            pstmt.setInt(4, moderationValue);
            pstmt.setInt(5, rewardPoints);
            pstmt.setString(6, StringUtils.dateToMillis(creationDate));
            pstmt.setString(7, StringUtils.dateToMillis(modifiedDate));
            pstmt.setLong(8, id);
            pstmt.executeUpdate();
        }
        finally {
            try {  pstmt.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
    }
}