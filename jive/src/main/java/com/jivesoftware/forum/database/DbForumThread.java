/**
 * $RCSfile: DbForumThread.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:55 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.jivesoftware.forum.*;
import com.jivesoftware.util.*;

/**
 * Database implementation of the ForumThread interface. It stores thread
 * data in the jiveThread table, and thread properties in jiveThreadProp.
 *
 * @see ForumThread
 */
public class DbForumThread implements ForumThread, Cacheable {

    /** DATABASE QUERIES **/
    private static final String SET_MESSAGE_PARENT =
        "UPDATE jiveMessage SET parentID=? WHERE messageID=?";
    private static final String CHANGE_MESSAGE_THREAD =
        "UPDATE jiveMessage SET threadID=? WHERE messageID=?";
    private static final String UPDATE_THREAD_MODIFIED_DATE =
        "UPDATE jiveThread SET modifiedDate=? WHERE threadID=?";
    private static final String DELETE_MESSAGE =
        "DELETE FROM jiveMessage WHERE messageID=?";
    private static final String DELETE_MESSAGE_PROPERTIES =
        "DELETE FROM jiveMessageProp WHERE messageID=?";
    private static final String LOAD_THREAD =
        "SELECT forumID, rootMessageID, modValue, rewardPoints, creationDate, " +
        "modifiedDate FROM jiveThread WHERE threadID=?";
    private static final String INSERT_THREAD =
        "INSERT INTO jiveThread(threadID,forumID,rootMessageID,modValue, " +
        "rewardPoints,creationDate,modifiedDate) VALUES(?,?,?,?,?,?,?)";
    private static final String SAVE_THREAD =
        "UPDATE jiveThread SET rootMessageID=?, modValue=?, rewardPoints=?, " +
        "creationDate=?, modifiedDate=? WHERE threadID=?";
    private static final String LOAD_PROPERTIES =
        "SELECT name, propValue FROM jiveThreadProp WHERE threadID=?";
    private static final String INSERT_PROPERTY =
        "INSERT INTO jiveThreadProp(threadID,name,propValue) VALUES(?,?,?)";
    private static final String DELETE_PROPERTY =
        "DELETE FROM jiveThreadProp WHERE threadID=? AND name=?";
    private static final String DELETE_PROPERTIES =
        "DELETE FROM jiveThreadProp WHERE threadID=?";
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
     */
    private static final boolean LAZY_PROP_LOADING = true;

    // Constant for an empty bock. This is returned in the case that there are
    // no results when trying to load a message block.
    private static final long[] EMPTY_BLOCK = new long[0];

    private long id = -1;
    private long rootMessageID;
    private java.util.Date creationDate;
    private java.util.Date modifiedDate;
    private DbTreeWalker treeWalker = null;
    //Default the moderation point value to Integer.MIN_VALUE. Then, if the
    //moderation value gets changed before being inserted into the db, we'll know.
    private int moderationValue = Integer.MIN_VALUE;
    private int rewardPoints = 0;
    private Map properties;
    protected long forumID = -1;

    /**
     * Cache for lists of message id's. The default size is 2K, which should
     * let us hold about 250 message id's in memory at once. If you have a lot
     * of memory and very large forums, you may wish to make the size of this
     * cache a bit larger.
     */
    protected Cache messageListCache = new Cache(2048, JiveGlobals.HOUR * 6);

    /**
     * Cache for message counts. Default size is 256 bytes.
     */
    protected Cache messageCountCache = new Cache(256, JiveGlobals.HOUR * 6);

    // Default filter for messages, called with messages() method.
    private static final ResultFilter DEFAULT_MESSAGE_FILTER =
            ResultFilter.createDefaultMessageFilter();

    /**
     * Temporarily store a rootMessage object pointer when the thread is being
     * initially created. After creation, the object will remain null.
     */
    private ForumMessage rootMessage = null;

    /**
     * Indicates if the object is ready to be saved or not. An object is not
     * ready to be saved if it has just been created and has not yet been added
     * to its container. For example, a message added to a thread, etc.
     */
    private boolean isReadyToSave = false;

    private DbForumFactory factory;

    /**
     * Creates a new DbForumThread. The supplied message object is used to
     * derive the name of the thread (subject of message), as well as the
     * creation date and modified date of thread.
     *
     * @param rootMessage the root message of the thread.
     */
    protected DbForumThread(ForumMessage rootMessage,
            DbForumFactory factory) throws UnauthorizedException
    {
        this.id = SequenceManager.nextID(JiveGlobals.THREAD);
        this.factory = factory;
        this.rootMessage = rootMessage;
        this.rootMessageID = rootMessage.getID();
        // Set the creation and modified dates to be the same as those of
        // root message.
        long rootMessageTime = rootMessage.getCreationDate().getTime();
        this.creationDate = new java.util.Date(rootMessageTime);
        this.modifiedDate = new java.util.Date(rootMessageTime);
        properties = new Hashtable();
    }

    /**
     * Loads a DbForumThread from the database based on its id.
     *
     * @param id in unique id of the ForumThread to load.
     * @param factory a ForumFactory to use for loading.
     */
    protected DbForumThread(long id, DbForumFactory factory)
            throws ForumThreadNotFoundException
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

    public String getName() {
        String name = null;
        try {
            name = getMessage(rootMessageID).getSubject();
        }
        catch (Exception e) { }
        return name;
    }

    public java.util.Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(java.util.Date creationDate)
            throws UnauthorizedException
    {
        this.creationDate = creationDate;
        // Only save to the db if the object is ready
        if (!isReadyToSave) {
            return;
        }
        saveToDb();
        // Remove thread from cache.
        factory.cacheManager.threadCache.remove(this.id);
    }

    public java.util.Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(java.util.Date modifiedDate)
            throws UnauthorizedException
    {
        this.modifiedDate = modifiedDate;
        // Only save to the db if the object is ready
        if (!isReadyToSave) {
            return;
        }
        saveToDb();
        // Remove thread from cache.
        factory.cacheManager.threadCache.remove(this.id);
    }

    public int getModerationValue() {
        return moderationValue;
    }

    public void setModerationValue(int value, Authorization auth) {
        // If the new value is the same as the old value, return.
        if (moderationValue == value) {
            return;
        }
        long now = System.currentTimeMillis();
        // We modified the moderation value of the thread, so update the
        // modified date.
        this.modifiedDate.setTime(now);
        // If the thread is switching from hidden to visible, we should update
        // the modified date of forum it belongs to.
        try {
            Forum forum = factory.getForum(this.forumID);
            if (this.moderationValue < forum.getModerationMinThreadValue()
                    && value >= forum.getModerationMinThreadValue())
            {
                forum.setModifiedDate(modifiedDate);
            }
        }
        catch (ForumNotFoundException fnfe) { /* ignore */ }
        catch (UnauthorizedException ue) { /* ignore */ }
        // Now, set the moderation value.
        this.moderationValue = value;
        // Only save to the db if the object is ready
        if (!isReadyToSave) {
            return;
        }
        saveToDb();
        // Remove thread from cache
        factory.cacheManager.threadCache.remove(this.id);
        // Remove forum from cache
        factory.cacheManager.forumCache.remove(this.forumID);

        // Finally, make an entry into the jiveModeration table for moderation
        // auditing purposes.
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(INSERT_MODERATION_ENTRY);
            pstmt.setLong(1, id);
            pstmt.setInt(2, JiveGlobals.THREAD);
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
            if (properties == null) {
                loadPropertiesFromDb();
            }
        }
        return (String)properties.get(name);
    }

    public void setProperty(String name, String value)
            throws UnauthorizedException
    {
        if (LAZY_PROP_LOADING) {
            if (properties == null) {
                loadPropertiesFromDb();
            }
        }
        properties.put(name, value);
        // Only save to the db if the object is ready
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
        // Remove thread from cache.
        factory.cacheManager.threadCache.remove(this.id);
    }

    public void deleteProperty(String name)
            throws UnauthorizedException
    {
        if (LAZY_PROP_LOADING) {
            if (properties == null) {
                loadPropertiesFromDb();
            }
        }
        properties.remove(name);
        // Only save to the db if the object is ready
        if (!isReadyToSave) {
            return;
        }
        deletePropertyFromDb(name);
        // Remove thread from cache.
        factory.cacheManager.threadCache.remove(this.id);
    }

    public Iterator propertyNames() {
        if (LAZY_PROP_LOADING) {
            if (properties == null) {
                loadPropertiesFromDb();
            }
        }
        return Collections.unmodifiableSet(properties.keySet()).iterator();
    }

    public Forum getForum() {
        // If the thread hasn't been added to a forum yet, return null.
        if (forumID == -1) {
            return null;
        }
        try {
            return factory.getForum(forumID);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ForumMessage getMessage(long messageID)
            throws ForumMessageNotFoundException
    {
        return factory.getMessage(messageID, this.id, forumID);
    }

    public ForumMessage getRootMessage()  {
        ForumMessage rootMessage = null;
        try {
            rootMessage = getMessage(rootMessageID);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return rootMessage;
    }

    public int getMessageCount() {
        return getMessageCount(DEFAULT_MESSAGE_FILTER);
    }

    public int getMessageCount(ResultFilter resultFilter) {
        String query = getMessageListSQL(resultFilter, true);
        CacheableInt count = (CacheableInt)messageCountCache.get(query);
        // If already in cache, return the count.
        if (count != null) {
            return count.getInt();
        }
        // Otherwise, we have to load the count from the db.
        else {
            int messageCount = 0;
            Connection con = null;
            Statement stmt = null;
            try {
                con = ConnectionManager.getConnection();
                stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                rs.next();
                messageCount = rs.getInt(1);
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
            // Add the thread count to cache
            messageCountCache.add(query, new CacheableInt(messageCount));
            return messageCount;
        }
    }

    public void addMessage(ForumMessage parentMessage, ForumMessage newMessage)
            throws UnauthorizedException
    {
        // Get the underlying DbForumMessage object.
        DbForumMessage dbMessage = null;
        if (newMessage instanceof ForumMessageProxy) {
            ForumMessageProxy proxyMessage = (ForumMessageProxy)newMessage;
            dbMessage = (DbForumMessage)proxyMessage.getProxiedForumMessage();
        }
        else {
            dbMessage = (DbForumMessage)newMessage;
        }
        DbForum dbForum = null;

        boolean abortTransaction = false;
        Connection con = null;
        try {
            con = ConnectionManager.getTransactionConnection();

            // Insert the message into the database.
            dbMessage.insertIntoDb(this, parentMessage.getID(), con);

            // Check the moderation value of the message. If the value is above
            // the visible threshold for the forum, then update the modified
            // date of the thread and forum. Otherwise, we'll wait to update
            // the modified dates to when the message is moderated to be visible.
            dbForum = factory.cacheManager.forumCache.get(forumID);
            if (newMessage.getModerationValue() >=
                    dbForum.getModerationMinMessageValue())
            {
                long modifiedDate = newMessage.getModifiedDate().getTime();
                updateModifiedDate(modifiedDate, con);
                dbForum.updateModifiedDate(modifiedDate, con);
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
            abortTransaction = true;
        }
        finally {
            ConnectionManager.closeTransactionConnection(con, abortTransaction);
        }

        // Thread count has been modified, remove thread and forum from cache.
        factory.cacheManager.threadCache.remove(this.id);
        factory.cacheManager.forumCache.remove(this.forumID);

        // Expire the userMessageCountCache if this message was not posted
        // anonymously.
        if (!newMessage.isAnonymous()) {
            factory.userMessageCountCache.remove(newMessage.getUser().getID());
        }

        // If above the moderation threshold...
        if (newMessage.getModerationValue() >=
                    dbForum.getModerationMinMessageValue())
        {
            // Notify the watch manager that the thread has been updated.
            factory.watchManager.notifyWatches(this);
            // Notify the gateway manager of a new message.
//            dbForum.getGatewayManager().exportData(dbMessage);
        }
    }

    public void deleteMessage(ForumMessage message)
            throws UnauthorizedException
    {
        // If the message does not belong to this thread, don't perform delete.
        if (message.getForumThread().getID() != this.id) {
            throw new IllegalArgumentException("Message " + message.getID() +
                " could not be deleted. It belongs to thread " +
                message.getForumThread().getID() + ", and not thread " +
                this.id + ".");
        }

        // Now, make sure that the message being deleted isn't the root message
        // of this thread. If it is, the whole thread should just be deleted.
        if (message.getID() == getRootMessage().getID()) {
            try {
                DbForum forum = factory.cacheManager.forumCache.get(forumID);
                forum.deleteThread(this);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Otherwise, delete the message and all children.
        else {
            boolean abortTransaction = false;
            Connection con = null;
            try {
                con = ConnectionManager.getTransactionConnection();
                // Delete the message.
                deleteMessage(message,con);
            }
            catch( Exception e ) {
                e.printStackTrace();
                abortTransaction = true;
            }
            finally {
                ConnectionManager.closeTransactionConnection(con, abortTransaction);
            }
        }

        // Thread count has been modified, remove thread and forum from cache.
        factory.cacheManager.threadCache.remove(this.id);
        factory.cacheManager.forumCache.remove(this.forumID);
    }

    public TreeWalker treeWalker() {
        if (treeWalker == null) {
            treeWalker = new DbTreeWalker(this, factory);
        }
        return treeWalker;
    }

    public Iterator messages() {
        return messages(DEFAULT_MESSAGE_FILTER);
    }

    public Iterator messages(ResultFilter resultFilter) {
        String query = getMessageListSQL(resultFilter, false);
        long [] messageBlock = getMessageBlock(query.toString(),
                resultFilter.getStartIndex()
            );
        int startIndex = resultFilter.getStartIndex();
        int endIndex;
        // If number of results is set to inifinite, set endIndex to the total
        // number of threads in the forum.
        if (resultFilter.getNumResults() == ResultFilter.NULL_INT) {
            endIndex = (int)getMessageCount(resultFilter);
        }
        else {
            endIndex = resultFilter.getNumResults() + startIndex;
        }
        return new ForumMessageBlockIterator(messageBlock, query.toString(),
                startIndex, endIndex, this.forumID, factory, this.id);
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
        size += CacheSizes.sizeOfDate();                // creation date
        size += CacheSizes.sizeOfDate();                // modified date
        size += CacheSizes.sizeOfObject();              // ref to rootMessage
        size += CacheSizes.sizeOfObject();              // ref to forum
        size += CacheSizes.sizeOfObject();              // ref to factory
        size += CacheSizes.sizeOfBoolean();             // ready save var
        size += CacheSizes.sizeOfBoolean();             // deleting var

        size += messageListCache.getMaxSize();          // message list cache.
        size += messageCountCache.getMaxSize();         // message count cache.

        return size;
    }

    //OTHER METHODS//

    /**
     * Converts the object to a String by returning the name of the thread.
     * This functionality is primarily for Java applications that might be
     * accessing Jive objects through a GUI.
     */
    public String toString() {
        return getName();
    }

    public int hashCode() {
        return (int)id;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object != null && object instanceof DbForumThread) {
            return id == ((DbForumThread)object).getID();
        }
        else {
            return false;
        }
    }

    /**
     * Updates the modified date but doesn't require a security check since
     * it is a protected method.
     */
    protected void updateModifiedDate(long date, Connection con)
            throws SQLException
    {
        this.modifiedDate.setTime(date);
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(UPDATE_THREAD_MODIFIED_DATE);
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
     * Delete message method that accepts a Connection as argument -- useful
     * for transactions.
     */
    protected void deleteMessage(ForumMessage message, Connection con)
            throws SQLException, ForumMessageNotFoundException
    {
        // Skip null messages.
        if (message == null) {
            return;
        }

        // Recursively delete all children
        TreeWalker walker = treeWalker();
        int childCount = walker.getChildCount(message);
        for (int i=childCount-1; i >= 0; i--) {
            ForumMessage childMessage = walker.getChild(message, i);
            deleteMessage(childMessage, con);
        }

        // Fix any references to the message in the reward auditing table.
        factory.rewardManager.deleteMessageReference(message, con);

        PreparedStatement pstmt = null;
        try {
            // Delete any message properties.
            pstmt = con.prepareStatement(DELETE_MESSAGE_PROPERTIES);
            pstmt.setLong(1, message.getID());
            pstmt.execute();
            pstmt.close();

            // Delete the data from the jiveMessage table.
            pstmt = con.prepareStatement(DELETE_MESSAGE);
            pstmt.setLong(1, message.getID());
            pstmt.execute();
        }
        finally {
            try {  pstmt.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }

        // Now, delete from the cache.
        factory.cacheManager.messageCache.remove(message.getID());
        // Finally, delete it from the search index
        factory.getSearchManager().removeFromIndex(message);
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
     * Returns the SQL statement corresponding to a ResultFilter for messages.
     */
    private String getMessageListSQL(ResultFilter resultFilter, boolean countQuery) {
        int sortField = resultFilter.getSortField();
        // Make sure the sort field is valid.
        if (!countQuery && !( sortField == JiveGlobals.MODIFIED_DATE ||
                sortField == JiveGlobals.CREATION_DATE ||
                sortField == JiveGlobals.MESSAGE_SUBJECT ||
                sortField == JiveGlobals.MESSAGE_BODY ||
                ( sortField == JiveGlobals.EXTENDED_PROPERTY &&
                  resultFilter.getSortPropertyName() != null
                )
              )
            )
        {
            throw new IllegalArgumentException("The specified sort field is not valid.");
        }

        // We'll accumlate the query in a StringBuffer.
        StringBuffer query = new StringBuffer(160);
        if (!countQuery) {
            query.append("SELECT jiveMessage.messageID");
        }
        else {
            query.append("SELECT count(1)");
        }

        boolean filterUser = resultFilter.getUserID() != ResultFilter.NULL_INT;
        boolean filterCreationDate = resultFilter.getCreationDateRangeMin() != null ||
                                     resultFilter.getCreationDateRangeMax() != null;
        boolean filterModifiedDate = resultFilter.getModifiedDateRangeMin() != null ||
                                     resultFilter.getModifiedDateRangeMax() != null;
        int propertyCount = resultFilter.getPropertyCount();

        // SELECT -- need to add value that we sort on
        if(!countQuery) {
            switch(sortField) {
                case JiveGlobals.MESSAGE_SUBJECT:
                    query.append(", jiveMessage.subject");
                    break;
                case JiveGlobals.MODIFIED_DATE:
                    query.append(", jiveMessage.modifiedDate");
                    break;
                case JiveGlobals.CREATION_DATE:
                    query.append(", jiveMessage.creationDate");
                    break;
                case JiveGlobals.EXTENDED_PROPERTY:
                    query.append(", propTable.propValue");
                    break;
            }
        }

        // FROM -- values (in addition to jiveThread)
        query.append(" FROM jiveMessage");
        for (int i=0; i<propertyCount; i++) {
            query.append(", jiveMessageProp p").append(i);
        }
        if (!countQuery &&
                resultFilter.getSortField() == JiveGlobals.EXTENDED_PROPERTY)
        {
            query.append(", jiveMessageProp propTable");
        }

        // WHERE BLOCK
        query.append(" WHERE jiveMessage.threadID=").append(this.id);
        // User
        if (filterUser) {
            query.append(" AND jiveMessage.userID=").append(resultFilter.getUserID());
        }
        // Properties
        for (int i=0; i<propertyCount; i++) {
            query.append(" AND jiveMessage.messageID=p").append(i).append(".messageID");
            query.append(" AND p").append(i).append(".name='");
            query.append(resultFilter.getPropertyName(i));
            query.append("' AND p").append(i).append(".propValue='");
            query.append(resultFilter.getPropertyValue(i)).append("'");
        }
        // Sort on properties
        if (!countQuery && sortField == JiveGlobals.EXTENDED_PROPERTY) {
            query.append(" AND jiveMessage.messageID=propTable.messageID");
        }

        // Creation date range
        if (filterCreationDate) {
            if (resultFilter.getCreationDateRangeMin() != null) {
                query.append(" AND jiveMessage.creationDate >= '");
                query.append(StringUtils.dateToMillis(
                        resultFilter.getCreationDateRangeMin()));
                query.append("'");
            }
            if (resultFilter.getCreationDateRangeMax() != null) {
                query.append(" AND jiveMessage.creationDate <= '");
                query.append(StringUtils.dateToMillis(
                        resultFilter.getCreationDateRangeMax()));
                query.append("'");
            }
        }

        // Modified date range
        if (filterModifiedDate) {
            if (resultFilter.getModifiedDateRangeMin() != null) {
                query.append(" AND jiveMessage.modifiedDate >= '");
                query.append(StringUtils.dateToMillis(
                        resultFilter.getModifiedDateRangeMin()));
                query.append("'");
            }
            if (resultFilter.getModifiedDateRangeMax() != null) {
                query.append(" AND jiveMessage.modifiedDate <= '");
                query.append(StringUtils.dateToMillis(
                        resultFilter.getModifiedDateRangeMax()));
                query.append("'");
            }
        }

        // Moderation range
        int moderationRangeMin = resultFilter.getModerationRangeMin();
        // If the minimum moderation value was not set, it should default
        // to the minimum threshold for the forum.
        if (moderationRangeMin == ResultFilter.NULL_INT) {
            moderationRangeMin = getForum().getModerationMinMessageValue();
        }
        int moderationRangeMax = resultFilter.getModerationRangeMax();
        if (moderationRangeMin == moderationRangeMax) {
            query.append(" AND jiveMessage.modValue = ");
            query.append(moderationRangeMin);
        }
        else {
            // Ignore any min less than negative 1 million.
            if (moderationRangeMin > -1000000) {
                query.append(" AND jiveMessage.modValue >= ");
                query.append(moderationRangeMin);
            }
            // Use a max value if it's defined.
            if (moderationRangeMax != ResultFilter.NULL_INT) {
                query.append(" AND jiveMessage.modValue <= ");
                query.append(moderationRangeMax);
            }
        }

        // ORDER BY
        if (!countQuery) {
            query.append(" ORDER BY ");
            switch(sortField) {
                case JiveGlobals.MESSAGE_SUBJECT:
                    query.append("jiveMessage.subject");
                    break;
                case JiveGlobals.MODIFIED_DATE:
                    query.append("jiveMessage.modifiedDate");
                    break;
                case JiveGlobals.CREATION_DATE:
                    query.append("jiveMessage.creationDate");
                    break;
                case JiveGlobals.EXTENDED_PROPERTY:
                    query.append("propTable.propValue");
                    break;
            }
            if (resultFilter.getSortOrder() == ResultFilter.DESCENDING) {
                query.append(" DESC");
            }
            else {
                query.append(" ASC");
            }
        }

        return query.toString();
    }

    /**
     * Resets the object.
     */
    protected void reset() {
        // Reload the actual forum data.
        try {
            loadFromDb();
        } catch (Exception e) { }
        // Clear out the message list and message count cache.
        messageListCache.clear();
        messageCountCache.clear();
        // Clear tree
        this.treeWalker = null;
    }

    /**
     * Returns a block of messageID's from a query and performs transparent
     * caching of those blocks. The two parameters specify a database query
     * and a startIndex for the results in that query.
     *
     * @param query the SQL message list query to cache blocks from.
     * @param startIndex the startIndex in the list to get a block for.
     */
    protected long[] getMessageBlock(String query, int startIndex) {
        // First, discover what block number the results will be in.
        int blockID = startIndex / DbForum.MESSAGE_BLOCK_SIZE;
        int blockStart = blockID * DbForum.MESSAGE_BLOCK_SIZE;
        // Now, check cache to see if the block is already cached. The key is
        // simply the query plus the blockID.
        String key = query + blockID;
        CacheableLongArray longArray = (CacheableLongArray)messageListCache.get(key);
        // If already in cache, return the block.
        if (longArray != null) {
            /**
             * The actual block may be smaller than MESSAGE_BLOCK_SIZE. If that's
             * the case, it means two things:
             *  1) We're at the end boundary of all the results.
             *  2) If the start index is greater than the length of the current
             *     block, than there aren't really any results to return.
             */
            long [] messages = longArray.getLongArray();
            if (startIndex >= blockStart + messages.length) {
                //Return an empty array
                return EMPTY_BLOCK;
            }
            else {
                return messages;
            }
        }
        // Otherwise, we have to load up the block from the database.
        else {
            LongList messagesList = new LongList(DbForum.MESSAGE_BLOCK_SIZE);
            Connection con = null;
            Statement stmt = null;
            try {
                con = ConnectionManager.getConnection();
                stmt = con.createStatement();
                // Set the maxium number of rows to end at the end of this block.
                ConnectionManager.setMaxRows(stmt, DbForum.MESSAGE_BLOCK_SIZE * (blockID+1));
                ResultSet rs = stmt.executeQuery(query);
                // Grab MESSAGE_BLOCK_ROWS rows at a time.
                ConnectionManager.setFetchSize(rs, DbForum.MESSAGE_BLOCK_SIZE);
                // Many JDBC drivers don't implement scrollable cursors the real
                // way, but instead load all results into memory. Looping through
                // the results ourselves is more efficient.
                for (int i=0; i<blockStart; i++) {
                    rs.next();
                }
                // Keep reading results until the result set is exaughsted or
                // we come to the end of the block.
                int count = 0;
                while (rs.next() && count < DbForum.MESSAGE_BLOCK_SIZE) {
                    messagesList.add(rs.getLong(1));
                    count++;
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
            long [] messages = messagesList.toArray();
            // Add the message block to cache
            messageListCache.add(key, new CacheableLongArray(messages));
            /**
             * The actual block may be smaller than MESSAGE_BLOCK_SIZE. If that's
             * the case, it means two things:
             *  1) We're at the end boundary of all the results.
             *  2) If the start index is greater than the length of the current
             *     block, than there aren't really any results to return.
             */
            if (startIndex >= blockStart + messages.length) {
                // Return an empty array
                return EMPTY_BLOCK;
            }
            else {
                return messages;
            }
        }
    }

    /**
     * Loads a ForumThread from the database.
     */
    private void loadFromDb() throws ForumThreadNotFoundException {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();

            pstmt = con.prepareStatement(LOAD_THREAD);
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if( !rs.next() ) {
                throw new ForumThreadNotFoundException("Thread " + id +
                    " could not be loaded from the database.");
            }
            forumID = rs.getLong(1);
            rootMessageID = rs.getLong(2);
            moderationValue = rs.getInt(3);
            rewardPoints = rs.getInt(4);
            creationDate = new java.util.Date(Long.parseLong(rs.getString(5).trim()));
            modifiedDate =  new java.util.Date(Long.parseLong(rs.getString(6).trim()));

            // Now, load any extended thread properties
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
            sqle.printStackTrace();
            throw new ForumThreadNotFoundException("Thread " + id +
                " could not be loaded from the database.");
        }
        catch (NumberFormatException nfe) {
            System.err.println("WARNING: In DbForumThread.loadFromDb() -- there " +
                "was an error parsing the dates returned from the database. Ensure " +
                "that they're being stored correctly.");
        }
        finally {
            try {  pstmt.close();   }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    /**
     * Inserts a new forum thread into the database. A connection object must
     * be passed in. The connection must be open when passed in, and will
     * remain open when passed back. This method allows us to make insertions
     * be transactional.
     *
     * @param con an open Connection used to insert the thread to the db.
     */
    protected void insertIntoDb(DbForum forum, Connection con)
            throws SQLException, UnauthorizedException
    {
        this.forumID = forum.getID();
        // If the moderation value hasn't already been set, use the default.
        if (this.moderationValue == Integer.MIN_VALUE) {
            this.moderationValue = forum.getModerationDefaultThreadValue();
        }
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(INSERT_THREAD);
            pstmt.setLong(1, id);
            pstmt.setLong(2, forum.getID());
            pstmt.setLong(3, rootMessageID);
            pstmt.setInt(4, moderationValue);
            pstmt.setInt(5, rewardPoints);
            pstmt.setString(6, StringUtils.dateToMillis(creationDate));
            pstmt.setString(7, StringUtils.dateToMillis(modifiedDate));
            pstmt.executeUpdate();
        }
        finally {
            try {  pstmt.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }

        // Now, insert the message into the database. Depending on if this
        // method was called internally or not, the newMessage object might
        // be wrapped by a proxy or not.
        DbForumMessage dbMessage = null;
        if (rootMessage instanceof ForumMessageProxy) {
            ForumMessageProxy proxyMessage = (ForumMessageProxy)rootMessage;
            dbMessage = (DbForumMessage)proxyMessage.getProxiedForumMessage();
            dbMessage.insertIntoDb(this, -1, con);
        }
        else {
            dbMessage = (DbForumMessage)rootMessage;
            dbMessage.insertIntoDb(this, -1, con);
        }

        // Now, insert properties into database
        if (this.properties.size() > 0) {
            savePropertiesToDb(con, false);
        }

        // Notify gateway manager of new message being inserted if the message
        // is above the min moderation threshold for the forum. If it's below
        // the threshold, the message will simply get exported later if the
        // moderation value is set high enough.
        if (dbMessage.getModerationValue() >=
                    forum.getModerationMinMessageValue())
        {
//            forum.getGatewayManager().exportData(dbMessage);
        }

        // Set root message to null sice reference is no longer needed.
        rootMessage = null;

        // Since we're done inserting the object to the database, it is ready
        // for future insertions.
        isReadyToSave = true;
    }

    /**
     * Saves the ForumThread to the database.
     */
    private synchronized void saveToDb() {
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
     * A version of saveToDb that accepts a Connection. This is useful for
     * participating in transactions.
     */
    private synchronized void saveToDb(Connection con) throws SQLException
    {
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(SAVE_THREAD);
            pstmt.setLong(1, getRootMessage().getID());
            pstmt.setInt(2, moderationValue);
            pstmt.setInt(3, rewardPoints);
            pstmt.setString(4, StringUtils.dateToMillis(creationDate));
            pstmt.setString(5, StringUtils.dateToMillis(modifiedDate));
            pstmt.setLong(6, id);
            pstmt.executeUpdate();
        }
        finally {
            try {  pstmt.close();   }
            catch (Exception e) { e.printStackTrace(); }
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
}