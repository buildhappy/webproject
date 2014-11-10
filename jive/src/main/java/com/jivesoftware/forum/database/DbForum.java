/**
 * $RCSfile: DbForum.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:52 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import java.util.*;
import java.util.Date;
import java.sql.*;
import java.io.*;

import com.jivesoftware.forum.*;
import com.jivesoftware.util.*;


/**
 * Database implementation of the Forum interface. It loads and stores forum
 * information from a a database.
 *
 * @see Forum
 */
public class DbForum implements Forum, Cacheable {

    /** DATABASE QUERIES **/
    private static final String ALL_THREADS =
        "SELECT threadID from jiveThread WHERE forumID=?";
    private static final String ADD_THREAD =
        "UPDATE jiveThread set forumID=? WHERE threadID=?";
    private static final String MOVE_MESSAGES =
        "UPDATE jiveMessage set forumID=? WHERE threadID=?";
    protected static final String DELETE_THREAD =
        "DELETE FROM jiveThread WHERE threadID=?";
    private static final String DELETE_THREAD_PROPERTIES =
        "DELETE FROM jiveThreadProp WHERE threadID=?";
    private static final String LOAD_PROPERTIES =
        "SELECT name, propValue FROM jiveForumProp WHERE forumID=?";
    private static final String DELETE_PROPERTY =
        "DELETE FROM jiveForumProp WHERE forumID=? AND name=?";
    private static final String DELETE_PROPERTIES =
        "DELETE FROM jiveForumProp WHERE forumID=?";
    private static final String INSERT_PROPERTY =
        "INSERT INTO jiveForumProp(forumID,name,propValue) VALUES(?,?,?)";
    private static final String LOAD_FORUM =
        "SELECT forumID, name, description, modDefaultThreadVal, " +
        "modDefaultMsgVal, modMinThreadVal, modMinMsgVal, modifiedDate, " +
        "creationDate FROM jiveForum WHERE forumID=?";
    private static final String ADD_FORUM =
        "INSERT INTO jiveForum(forumID, name, description, modDefaultThreadVal, " +
        "modDefaultMsgVal, modMinThreadVal, modMinMsgVal, modifiedDate, creationDate)" +
        " VALUES (?,?,?,?,?,?,?,?,?)";
    private static final String SAVE_FORUM =
        "UPDATE jiveForum SET name=?, description=?, modDefaultThreadVal=?, " +
        "modDefaultMsgVal=?, modMinThreadVal=?, modMinMsgVal=?, " +
        "modifiedDate=?, creationDate=? WHERE forumID=?";
    private static final String UPDATE_FORUM_MODIFIED_DATE =
        "UPDATE jiveForum SET modifiedDate=? WHERE forumID=?";
    private static final String POPULAR_THREADS =
        "SELECT threadID, count(1) AS msgCount FROM jiveMessage WHERE " +
        "modifiedDate > ? AND forumID=? GROUP BY threadID ORDER BY msgCount DESC";
    private static final String POPULAR_THREADS_ORACLE =
        "SELECT /*+ INDEX (jiveMessage jiveMessage_mDate_idx) */ threadID, " +
        "count(1) AS msgCount FROM jiveMessage WHERE modifiedDate > ? " +
        "AND forumID=? GROUP BY threadID ORDER BY msgCount DESC";
     /*
        // Note, the above query includes hints for Oracle, which are necessary
        // so that modified date index will always be used. This is a great
        // tradeoff when the time window you're looking at is not excessively
        // large. MySQL also handles the query very quickly. If your own db
        // needs a hint, you may want to edit the sql logic to add it.
    */

    /**
     * Controls whether extended properties should be lazily loaded (not loaded
     * until requested). If the properties are infrequently used, this provides
     * a great speedup in initial object loading time. However, if your
     * application does use extended properties all the time, you may wish to
     * turn lazy loading off, as it's actually faster in total db lookup time
     * to load everything at once.
     */
    private static final boolean LAZY_PROP_LOADING = true;

    /**
     * Number of threadID's per cache block.
     */
    public static final int THREAD_BLOCK_SIZE = 200;
    /**
     * Number of messageID's per cache block.
     */
    public static final int MESSAGE_BLOCK_SIZE = 100;

    // Constant for an empty bock. This is returned in the case that there are
    // no results when trying to load a thread or message block.
    private static final long[] EMPTY_BLOCK = new long[0];

    // A ResultFilter is used to filter and sort the values that are returned
    // from the threads() and messages() methods. We use a default
    private static final ResultFilter DEFAULT_THREAD_FILTER =
            ResultFilter.createDefaultThreadFilter();
    private static final ResultFilter DEFAULT_MESSAGE_FILTER =
            ResultFilter.createDefaultMessageFilter();

    /**
     * Cache for lists of thread id's. The default size is 16K, which should
     * let us hold about 2000 thread id's in memory at once. If you have a lot
     * of memory and very large forums, you may wish to make the size of this
     * cache considerably larger.
     */
    protected Cache threadListCache = new Cache(16*1024, JiveGlobals.HOUR * 6);

    /**
     * Cache for lists of message id's. The default size is 8K, which should
     * let us hold about 100 message id's in memory at once. If you have a lot
     * of memory and very large forums, you may wish to make the size of this
     * cache considerably larger.
     */
    protected Cache messageListCache = new Cache(8*1024, JiveGlobals.HOUR * 6);

    /**
     * Cache for thread counts. Default size is 256 bytes.
     */
    protected Cache threadCountCache = new Cache(256, JiveGlobals.HOUR * 6);

    /**
     * Cache for message counts. Default size is 256 bytes.
     */
    protected Cache messageCountCache = new Cache(256, JiveGlobals.HOUR * 6);

    private long id = -1;
    private String name;
    private String description;
    private java.util.Date creationDate;
    private java.util.Date modifiedDate;
    private int modDefaultThreadValue = 1;
    private int modDefaultMessageValue = 1;
    private int modMinThreadValue = 1 ;
    private int modMinMessageValue = 1;
    private Map properties;

    private DbForumFactory factory;
    private long[] popularThreads = null;
    private DbFilterManager filterManager;

    /**
     * Creates a new forum with the specified name and description.
     *
     * @param name the name of the forum.
     * @param description the description of the forum.
     * @param factory the DbForumFactory the forum is a part of.
     */
    protected DbForum(String name, String description, DbForumFactory factory) {
        this.id = SequenceManager.nextID(JiveGlobals.FORUM);
        this.name = name;
        this.description = description;
        long now = System.currentTimeMillis();
        creationDate = new java.util.Date(now);
        modifiedDate = new java.util.Date(now);
        this.factory = factory;
        insertIntoDb();
        properties = new Hashtable();
        init();
    }

    /**
     * Loads a forum with the specified id.
     */
    protected DbForum(long id, DbForumFactory factory)
            throws ForumNotFoundException
    {
        this.id = id;
        this.factory = factory;
        loadFromDb();
        init();
    }

    private void init() {
        filterManager = new DbFilterManager(this.id, factory);
    }

    //FROM THE FORUM INTERFACE//

    public long getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws ForumAlreadyExistsException {
        // If the new name is the same as the current name, do nothing.
        if (this.name.equals(name)) {
            return;
        }
        // If a forum with the new name already exists, throw an exception.
        try {
            // Check to make sure that's it's not just a case change. If we
            // don't do this check, the db lookup may give us a false-positive
            // that the name is already in use.
            if (!this.name.toLowerCase().equals(name.toLowerCase())) {
                Forum forum = factory.getForum(name);
                // If we get here, the forum must already exist, so throw exception.
                throw new ForumAlreadyExistsException();
            }
        }
        catch (ForumNotFoundException e) { }

        // This is a special case since names of forums and id's are linked
        // and cached. Before changing the name of the forum to the new name,
        // we need to clear from cache to remove the old name to id mapping.
        factory.cacheManager.forumCache.remove(id);

        this.name = name;
        saveToDb();

        // Finally, expire from cache again to revflect name change.
        factory.cacheManager.forumCache.remove(id);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        saveToDb();
        factory.cacheManager.forumCache.remove(id);
    }

    public java.util.Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(java.util.Date creationDate) {
       this.creationDate = creationDate;
       saveToDb();
       factory.cacheManager.forumCache.remove(id);
    }

    public java.util.Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(java.util.Date modifiedDate)
            throws UnauthorizedException
    {
        this.modifiedDate = modifiedDate;
        saveToDb();
        factory.cacheManager.forumCache.remove(id);
    }

    public int getModerationDefaultThreadValue() {
        return modDefaultThreadValue;
    }

    public void setModerationDefaultThreadValue(int value) {
        this.modDefaultThreadValue = value;
        saveToDb();
    }

    public int getModerationDefaultMessageValue() {
        return modDefaultMessageValue;
    }

    public void setModerationDefaultMessageValue(int value) {
        this.modDefaultMessageValue = value;
        saveToDb();
        factory.cacheManager.forumCache.remove(id);
    }

    public int getModerationMinThreadValue() {
        return modMinThreadValue;
    }

    public void setModerationMinThreadValue(int value) {
        this.modMinThreadValue = value;
        saveToDb();
        factory.cacheManager.forumCache.remove(id);
    }

    public int getModerationMinMessageValue() {
        return modMinMessageValue;
    }

    public void setModerationMinMessageValue(int value) {
        this.modMinMessageValue = value;
        saveToDb();
        factory.cacheManager.forumCache.remove(id);
    }

    public String getProperty(String name) {
        if (LAZY_PROP_LOADING) {
            if (properties == null) {
                loadPropertiesFromDb();
            }
        }
        return (String)properties.get(name);
    }

    public void setProperty(String name, String value) {
        if (LAZY_PROP_LOADING) {
            if (properties == null) {
                loadPropertiesFromDb();
            }
        }
        properties.put(name, value);
        savePropertiesToDb();
        factory.cacheManager.forumCache.remove(id);
    }

    public void deleteProperty(String name) {
        if (LAZY_PROP_LOADING) {
            if (properties == null) {
                loadPropertiesFromDb();
            }
        }
        properties.remove(name);
        deletePropertyFromDb(name);
        factory.cacheManager.forumCache.remove(id);
    }

    public Iterator propertyNames() {
        if (LAZY_PROP_LOADING) {
            if (properties == null) {
                loadPropertiesFromDb();
            }
        }
        return Collections.unmodifiableSet(properties.keySet()).iterator();
    }

    public void addThread(ForumThread thread) {
        boolean abortTransaction = false;
        // Add thread to forum table.
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getTransactionConnection();
            try {
                pstmt = con.prepareStatement(ADD_THREAD);
                pstmt.setLong(1,id);
                pstmt.setLong(2,thread.getID());
                pstmt.executeUpdate();
            }
            finally {
                try {  pstmt.close();   }
                catch (Exception e) { e.printStackTrace(); }
            }

            // Now, insert the thread into the database. Depending on if this
            // method was called internally or not, the thread object might
            // be wrapped by a proxy or not.
            if (thread instanceof ForumThreadProxy) {
                ForumThreadProxy proxyThread = (ForumThreadProxy)thread;
                DbForumThread dbThread = (DbForumThread)proxyThread.getProxiedForumThread();
                dbThread.insertIntoDb(this, con);
            }
            else {
                DbForumThread dbThread = (DbForumThread)thread;
                dbThread.insertIntoDb(this, con);
            }

            // Check the moderation value of the thread. If the value is above
            // the visible threshold for the forum, then update the modified
            // date of the forum. Otherwise, we'll wait to update the modified
            // date to when the thread is moderated to be visible.
            if (thread.getModerationValue() >= getModerationMinThreadValue()) {
                updateModifiedDate(thread.getModifiedDate().getTime(), con);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            abortTransaction = true;
        }
        finally {
            ConnectionManager.closeTransactionConnection(con, abortTransaction);
        }

        // Thread count has changed, so remove the forum from cache.
        factory.cacheManager.forumCache.remove(this.id);

        // Expire the userMessageCountCache if the root message was not posted
        // anonymously.
        ForumMessage message = thread.getRootMessage();
        if (!message.isAnonymous()) {
            factory.userMessageCountCache.remove(message.getUser().getID());
        }
    }

    public ForumThread getThread(long threadID)
            throws ForumThreadNotFoundException
    {
        return factory.getThread(threadID, this);
    }

    public void deleteThread(ForumThread thread) {
        boolean abortTransaction = false;
        Connection con = null;
        try {
            con = ConnectionManager.getTransactionConnection();
            DbForumThread dbThread = (DbForumThread)getThread(thread.getID());
            deleteThread(dbThread, con);
        }
        catch (Exception e) {
            e.printStackTrace();
            abortTransaction = true;
        }
        finally {
            ConnectionManager.closeTransactionConnection(con, abortTransaction);
        }

        // Now, delete thread and forum from cache
        factory.cacheManager.threadCache.remove(thread.getID());
        factory.cacheManager.forumCache.remove(this.id);
    }

    public void moveThread(ForumThread thread, Forum forum)
            throws UnauthorizedException
    {
        // Ensure that thread belongs to this forum
        if (thread.getForum().getID() != this.id) {
            throw new IllegalArgumentException("The thread does not belong to this forum.");
        }

        // Read all messageIDs of the thread into an array so that we can expire
        // each of them later.
        ResultFilter ignoreModerationFilter = ResultFilter.createDefaultMessageFilter();
        ignoreModerationFilter.setModerationRangeMin(Integer.MIN_VALUE+1);
        LongList messageIDList = new LongList();
        Iterator iter = thread.messages(ignoreModerationFilter);
        while (iter.hasNext()) {
            long messageID = ((ForumMessage)iter.next()).getID();
            messageIDList.add(messageID);
        }

        // Modify the SQL record. Only the thread table has information about
        // forumID, so we only need to modify that record. The message records
        // underneath the thread can be left alone.
        boolean abortTransaction = false;
        Connection con = null;
        try {
            con = ConnectionManager.getTransactionConnection();
            PreparedStatement pstmt = null;
            try {
                pstmt = con.prepareStatement(ADD_THREAD);
                pstmt.setLong(1,forum.getID());
                pstmt.setLong(2,thread.getID());
                pstmt.executeUpdate();
                pstmt.close();

                // Move all messages in thread to new forum.
                pstmt = con.prepareStatement(MOVE_MESSAGES);
                pstmt.setLong(1,forum.getID());
                pstmt.setLong(2,thread.getID());
                pstmt.executeUpdate();
            }
            finally {
                try {  pstmt.close(); }
                catch (Exception e) { e.printStackTrace(); }
            }
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
            abortTransaction = true;
            return;
        }
        finally {
            ConnectionManager.closeTransactionConnection(con, abortTransaction);
        }

        DatabaseCacheManager cacheManager = factory.cacheManager;
        SearchManager searchManager = factory.getSearchManager();

        // Remove both forums from cache.
        cacheManager.forumCache.remove(this.id);
        cacheManager.forumCache.remove(forum.getID());

        // Update the last modified date of both forums to the most recently
        // updated thread (this may have changed during the move thread operation).
        ResultFilter newestThreadFilter = ResultFilter.createDefaultThreadFilter();
        newestThreadFilter.setNumResults(1);
        Iterator threadIter = threads(newestThreadFilter);
        if (threadIter.hasNext()) {
            ForumThread newestThread = (ForumThread)threadIter.next();
            if (newestThread != null) {
                setModifiedDate(newestThread.getModifiedDate());
            }
        }
        // Updated modified date of other forum.
        newestThreadFilter = ResultFilter.createDefaultThreadFilter();
        newestThreadFilter.setNumResults(1);
        threadIter = forum.threads(newestThreadFilter);
        if (threadIter.hasNext()) {
            ForumThread newestThread = (ForumThread)threadIter.next();
            if (newestThread != null) {
                forum.setModifiedDate(newestThread.getModifiedDate());
            }
        }

        // Remove thread from cache.
        cacheManager.threadCache.remove(thread.getID());

        // Loop through all messages in thread and delete from cache, reset
        // entry in the search index to new thread.
        long [] messageIDArray = messageIDList.toArray();
        for (int i=0; i<messageIDArray.length; i++) {
            long messageID = messageIDArray[i];
            cacheManager.messageCache.remove(messageID);
            try {
                ForumMessage message = thread.getMessage(messageID);
                searchManager.removeFromIndex(message);
                searchManager.addToIndex(message);
            }
            catch (ForumMessageNotFoundException e)  { }
        }
    }

    public com.jivesoftware.forum.ForumThreadIterator threads() {
        return threads(DEFAULT_THREAD_FILTER);
    }

    public ForumThreadIterator threads(ResultFilter resultFilter) {
        String query = getThreadListSQL(resultFilter, false);
        long [] threadBlock = getThreadBlock(query.toString(), resultFilter.getStartIndex());
        int startIndex = resultFilter.getStartIndex();
        int endIndex;
        // If number of results is set to inifinite, set endIndex to the total
        // number of threads in the forum.
        if (resultFilter.getNumResults() == ResultFilter.NULL_INT) {
            endIndex = (int)getThreadCount(resultFilter);
        }
        else {
            endIndex = resultFilter.getNumResults() + startIndex;
        }
        return new ForumThreadBlockIterator(threadBlock, query.toString(),
                startIndex, endIndex, this.id, factory);
    }

    public Iterator popularThreads() {
        if (popularThreads == null) {
            // Determine the max number of threads that will be in the list.
            // Default is 4, but can be overriden by setting a Jive property.
            int threadNumber = 4;
            try {
                String number = JiveGlobals.getJiveProperty("popularThreads.number");
                if (number != null) {
                    threadNumber = Integer.parseInt(number);
                }
            }
            catch (Exception e) { }

            // Determine the number of days to consider for the list. Default is
            // 1, but can be overriden by setting a Jive property.
            int timeWindow = 1;
            try {
                String window = JiveGlobals.getJiveProperty("popularThreads.timeWindow");
                if (window != null) {
                    timeWindow = Integer.parseInt(window);
                }
            }
            catch (Exception e) { }

            LongList popThreads = new LongList(threadNumber);

            Calendar cal = Calendar.getInstance();
            cal.roll(Calendar.DAY_OF_YEAR, -timeWindow);

            Connection con = null;
            PreparedStatement pstmt = null;
            try {
                con = ConnectionManager.getConnection();
                // If Oracle, use the special SQL that includes Oracle hints.
                // Use the regular query for all other databases.
                if (ConnectionManager.getDatabaseType() ==
                        ConnectionManager.DatabaseType.ORACLE)
                {
                    pstmt = con.prepareStatement(POPULAR_THREADS_ORACLE);
                }
                else {
                    pstmt = con.prepareStatement(POPULAR_THREADS);
                }
                pstmt.setString(1, StringUtils.dateToMillis(cal.getTime()));
                pstmt.setLong(2, this.id);
                ResultSet rs = pstmt.executeQuery();
                for (int i=0; i < threadNumber; i++) {
                    if (!rs.next()) {
                        break;
                    }
                    popThreads.add(rs.getLong(1));
                }
                this.popularThreads = popThreads.toArray();
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
        return new DatabaseObjectIterator(JiveGlobals.THREAD, popularThreads,
                this);
    }

    public Iterator messages() {
        return messages(DEFAULT_MESSAGE_FILTER);
    }

    public Iterator messages(ResultFilter resultFilter) {
        String query = getMessageListSQL(resultFilter, false);
        long [] messageBlock = getMessageBlock(query.toString(), resultFilter.getStartIndex());
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
                startIndex, endIndex, this.id, factory, -1);
    }

    public int getThreadCount() {
        return getThreadCount(DEFAULT_THREAD_FILTER);
    }

    public int getThreadCount(ResultFilter resultFilter) {
        String query = getThreadListSQL(resultFilter, true);
        CacheableInt count = (CacheableInt)threadCountCache.get(query);
        // If already in cache, return the count.
        if (count != null) {
            return count.getInt();
        }
        // Otherwise, we have to load the count from the db.
        else {
            int threadCount = 0;
            Connection con = null;
            Statement stmt = null;
            try {
                con = ConnectionManager.getConnection();
                stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                rs.next();
                threadCount = rs.getInt(1);
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
            threadCountCache.add(query, new CacheableInt(threadCount));
            return threadCount;
        }
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

    public Query createQuery() {
        return new DbQuery(new Forum [] { this }, factory);
    }

    public FilterManager getFilterManager() {
        return filterManager;
    }

//    public GatewayManager getGatewayManager() {
        //GatewayManager manager = (GatewayManager)factory.gatewayManagers.get(this.id);
        // If there is no entry for the manager, add it.
        //if (manager == null) {
            //manager = new GatewayManager(factory, this);
            //factory.gatewayManagers.put(this.id, manager);
        //}
        //return manager;
    //}

    public PermissionsManager getPermissionsManager() {
        return new DbPermissionsManager(id, factory);
    }

    public ForumPermissions getPermissions(Authorization authorization) {
        long userID = authorization.getUserID();
        return factory.permissionsManager.getFinalUserPerms(id, userID);
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
        size += CacheSizes.sizeOfObject();              // factory
        size += CacheSizes.sizeOfLong();                // id
        size += CacheSizes.sizeOfString(name);          // name
        size += CacheSizes.sizeOfString(description);   // description
        size += CacheSizes.sizeOfDate();                // creation date
        size += CacheSizes.sizeOfDate();                // modified date
        size += CacheSizes.sizeOfMap(properties);       // properties
        size += threadListCache.getMaxSize();           // thread list cache
        size += messageListCache.getMaxSize();          // message list cache
        size += threadCountCache.getMaxSize();          // thread count cache
        size += messageCountCache.getMaxSize();         // message count cache
        size += CacheSizes.sizeOfObject() +
                CacheSizes.sizeOfLong() * 5;            // approx of pop threads
        size += CacheSizes.sizeOfInt() * 4;             // moderation values
        size += CacheSizes.sizeOfObject();              // filter manager

        return size;
    }

    //OTHER METHODS

    /**
     * Returns a String representation of the Forum object using the forum name.
     *
     * @return a String representation of the Forum object.
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
        if (object != null && object instanceof DbForum) {
            return id == ((DbForum)object).getID();
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
            pstmt = con.prepareStatement(UPDATE_FORUM_MODIFIED_DATE);
            pstmt.setString(1, StringUtils.dateToMillis(modifiedDate));
            pstmt.setLong(2, id);
            pstmt.executeUpdate();
        }
        finally {
            try {  pstmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    /**
     * Does the actual work of deleteting a thread. It accepts a Connection so
     * that it can participate in transactions.
     */
    protected void deleteThread(DbForumThread thread, Connection con)
            throws SQLException, ForumMessageNotFoundException
    {
        // Delete all messages from the thread. Deleting the root
        // message will delete all submessages.
        thread.deleteMessage(thread.getRootMessage(), con);

        // Delete any watches on the thread.
        factory.watchManager.deleteThreadWatches(thread, con);

        // Now delete thread db entry and thread properties.
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(DELETE_THREAD_PROPERTIES);
            pstmt.setLong(1,thread.getID());
            pstmt.execute();
            pstmt.close();

            pstmt = con.prepareStatement(DELETE_THREAD);
            pstmt.setLong(1,thread.getID());
            pstmt.execute();
        }
        finally {
            try {  pstmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    /**
     * Returns an array of all the thread id's in the forum. This method
     * is primarily useful for the data export class, which needs a full list
     * of all the id's for maximum efficiency.
     *
     * @return an array of all thread ID's in the forum.
     */
    protected long[] getAllThreads() {
        LongList threads = new LongList(getThreadCount());
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(ALL_THREADS);
            pstmt.setLong(1, this.id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                threads.add(rs.getLong(1));
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
        return threads.toArray();
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
        threadListCache.clear();
        threadCountCache.clear();
        messageListCache.clear();
        messageCountCache.clear();
        // Reset popular threads
        this.popularThreads = null;
    }

    /**
     * Returns the SQL statement corresponding to a ResultFilter for threads.
     */
    protected String getThreadListSQL(ResultFilter resultFilter, boolean countQuery) {
        int sortField = resultFilter.getSortField();

        // Make sure the sort field is valid.
        if (!countQuery && ! ( sortField == JiveGlobals.MODIFIED_DATE ||
                sortField == JiveGlobals.CREATION_DATE ||
                sortField == JiveGlobals.THREAD_NAME ||
                ( sortField == JiveGlobals.EXTENDED_PROPERTY &&
                  resultFilter.getSortPropertyName() != null
                )
              )
            )
        {
            throw new IllegalArgumentException("The specified sort field is not valid.");
        }

        StringBuffer query = new StringBuffer(80);
        if (!countQuery) {
            query.append("SELECT jiveThread.threadID");
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
        if (!countQuery) {
            switch(sortField) {
                case JiveGlobals.THREAD_NAME:
                    query.append(", jiveMessage.subject");
                    break;
                case JiveGlobals.MODIFIED_DATE:
                    query.append(", jiveThread.modifiedDate");
                    break;
                case JiveGlobals.CREATION_DATE:
                    query.append(", jiveThread.creationDate");
                    break;
                case JiveGlobals.EXTENDED_PROPERTY:
                    query.append(", propTable.propValue");
                    break;
            }
        }

        // FROM -- values (in addition to jiveThread)
        query.append(" FROM jiveThread");
        if (filterUser ||
                (!countQuery && resultFilter.getSortField() == JiveGlobals.THREAD_NAME))
        {
            query.append(", jiveMessage");
        }
        for (int i=0; i<propertyCount; i++) {
            query.append(", jiveThreadProp p").append(i);
        }
        if (!countQuery &&
                resultFilter.getSortField() == JiveGlobals.EXTENDED_PROPERTY)
        {
            query.append(", jiveThreadProp propTable");
        }

        // WHERE BLOCK
        query.append(" WHERE jiveThread.forumID=").append(this.id);
        // User
        if (filterUser || (!countQuery && sortField == JiveGlobals.THREAD_NAME)) {
            query.append(" AND jiveThread.threadID=jiveMessage.threadID");
            query.append(" AND jiveMessage.parentMessageID IS NULL");
        }
        if (filterUser) {
            query.append(" AND jiveMessage.userID=").append(resultFilter.getUserID());
        }
        // Properties
        for (int i=0; i<propertyCount; i++) {
            query.append(" AND jiveThread.threadID=p").append(i).append(".threadID");
            query.append(" AND p").append(i).append(".name='");
            query.append(resultFilter.getPropertyName(i));
            query.append("' AND p").append(i).append(".propValue='");
            query.append(resultFilter.getPropertyValue(i)).append("'");
        }
        // Sort on thread name
        if (!countQuery && sortField == JiveGlobals.THREAD_NAME) {
            query.append(" AND jiveThread.threadID=jiveMessage.threadID");
        }
        // Sort on properties
        if (!countQuery && sortField == JiveGlobals.EXTENDED_PROPERTY) {
            query.append(" AND jiveThread.threadID=propTable.threadID");
        }

        // Creation date range
        if (filterCreationDate) {
            if (resultFilter.getCreationDateRangeMin() != null) {
                query.append(" AND jiveThread.creationDate >= '");
                query.append(StringUtils.dateToMillis(
                        resultFilter.getCreationDateRangeMin()));
                query.append("'");
            }
            if (resultFilter.getCreationDateRangeMax() != null) {
                query.append(" AND jiveThread.creationDate <= '");
                query.append(StringUtils.dateToMillis(
                        resultFilter.getCreationDateRangeMax()));
                query.append("'");
            }
        }

        // Modified date range
        if (filterModifiedDate) {
            if (resultFilter.getModifiedDateRangeMin() != null) {
                query.append(" AND jiveThread.modifiedDate >= '");
                query.append(StringUtils.dateToMillis(
                        resultFilter.getModifiedDateRangeMin()));
                query.append("'");
            }
            if (resultFilter.getModifiedDateRangeMax() != null) {
                query.append(" AND jiveThread.modifiedDate <= '");
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
            moderationRangeMin = getModerationMinThreadValue();
        }
        int moderationRangeMax = resultFilter.getModerationRangeMax();
        if (moderationRangeMin == moderationRangeMax) {
            query.append(" AND jiveThread.modValue = ");
            query.append(moderationRangeMin);
        }
        else {
            // Ignore any min less than negative 1 million.
            if (moderationRangeMin > -1000000) {
                query.append(" AND jiveThread.modValue >= ");
                query.append(moderationRangeMin);
            }
            // Use a max value if it's defined.
            if (moderationRangeMax != ResultFilter.NULL_INT) {
                query.append(" AND jiveThread.modValue <= ");
                query.append(moderationRangeMax);
            }
        }

        // ORDER BY
        if (!countQuery) {
            query.append(" ORDER BY ");
            switch(sortField) {
                case JiveGlobals.THREAD_NAME:
                    query.append("jiveMessage.subject");
                    break;
                case JiveGlobals.MODIFIED_DATE:
                    query.append("jiveThread.modifiedDate");
                    break;
                case JiveGlobals.CREATION_DATE:
                    query.append("jiveThread.creationDate");
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
     * Returns the SQL statement corresponding to a ResultFilter for messages.
     */
    protected String getMessageListSQL(ResultFilter resultFilter, boolean countQuery) {
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
        StringBuffer query = new StringBuffer(80);
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
        if (!countQuery) {
            switch(sortField) {
                case JiveGlobals.MESSAGE_SUBJECT:
                    query.append(", subject");
                    break;
                case JiveGlobals.MODIFIED_DATE:
                    query.append(", modifiedDate");
                    break;
                case JiveGlobals.CREATION_DATE:
                    query.append(", creationDate");
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
        query.append(" WHERE forumID=").append(this.id);
        // User
        if (filterUser) {
            query.append(" AND userID=").append(resultFilter.getUserID());
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
                query.append(" AND creationDate >= '");
                query.append(StringUtils.dateToMillis(
                        resultFilter.getCreationDateRangeMin()));
                query.append("'");
            }
            if (resultFilter.getCreationDateRangeMax() != null) {
                query.append(" AND creationDate <= '");
                query.append(StringUtils.dateToMillis(
                        resultFilter.getCreationDateRangeMax()));
                query.append("'");
            }
        }

        // Modified date range
        if (filterModifiedDate) {
            if (resultFilter.getModifiedDateRangeMin() != null) {
                query.append(" AND modifiedDate >= '");
                query.append(StringUtils.dateToMillis(
                        resultFilter.getModifiedDateRangeMin()));
                query.append("'");
            }
            if (resultFilter.getModifiedDateRangeMax() != null) {
                query.append(" AND modifiedDate <= '");
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
            moderationRangeMin = getModerationMinMessageValue();
        }
        int moderationRangeMax = resultFilter.getModerationRangeMax();
        if (moderationRangeMin == moderationRangeMax) {
            query.append(" AND modValue = ");
            query.append(moderationRangeMin);
        }
        else {
            // Ignore any min less than negative 1 million.
            if (moderationRangeMin > -1000000) {
                query.append(" AND modValue >= ");
                query.append(moderationRangeMin);
            }
            // Use a max value if it's defined.
            if (moderationRangeMax != ResultFilter.NULL_INT) {
                query.append(" AND modValue <= ");
                query.append(moderationRangeMax);
            }
        }

        // ORDER BY
        if (!countQuery) {
            query.append(" ORDER BY ");
            switch(sortField) {
                case JiveGlobals.MESSAGE_SUBJECT:
                    query.append("subject");
                    break;
                case JiveGlobals.MODIFIED_DATE:
                    query.append("modifiedDate");
                    break;
                case JiveGlobals.CREATION_DATE:
                    query.append("creationDate");
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
     * Returns a block of threadID's from a query and performs transparent
     * caching of those blocks. The two parameters specify a database query
     * and a startIndex for the results in that query.
     *
     * @param query the SQL thread list query to cache blocks from.
     * @param startIndex the startIndex in the list to get a block for.
     */
    protected long[] getThreadBlock(String query, int startIndex) {
        // First, discover what block number the results will be in.
        int blockID = startIndex / THREAD_BLOCK_SIZE;
        int blockStart = blockID * THREAD_BLOCK_SIZE;
        // Now, check cache to see if the block is already cached. The key is
        // simply the query plus the blockID.
        String key = query + blockID;
        CacheableLongArray longArray = (CacheableLongArray)threadListCache.get(key);
        //If already in cache, return the block.
        if (longArray != null) {
            /**
             * The actual block may be smaller than THREAD_BLOCK_SIZE. If that's
             * the case, it means two things:
             *  1) We're at the end boundary of all the results.
             *  2) If the start index is greater than the length of the current
             *     block, than there aren't really any results to return.
             */
            long [] threads = longArray.getLongArray();
            if (startIndex >= blockStart + threads.length) {
                // Return an empty array
                return EMPTY_BLOCK;
            }
            else {
                return threads;
            }
        }
        // Otherwise, we have to load up the block from the database.
        else {
            LongList threadsList = new LongList(THREAD_BLOCK_SIZE);
            Connection con = null;
            Statement stmt = null;
            try {
                con = ConnectionManager.getConnection();
                stmt = con.createStatement();
                // Set the maxium number of rows to end at the end of this block.
                ConnectionManager.setMaxRows(stmt, THREAD_BLOCK_SIZE * (blockID+1));
                ResultSet rs = stmt.executeQuery(query);
                // Grab THREAD_BLOCK_ROWS rows at a time.
                ConnectionManager.setFetchSize(rs, THREAD_BLOCK_SIZE);
                // Many JDBC drivers don't implement scrollable cursors the real
                // way, but instead load all results into memory. Looping through
                // the results ourselves is more efficient.
                for (int i=0; i<blockStart; i++) {
                    rs.next();
                }
                // Keep reading results until the result set is exaughsted or
                // we come to the end of the block.
                int count = 0;
                while (rs.next() && count < THREAD_BLOCK_SIZE) {
                    threadsList.add(rs.getLong(1));
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
            long [] threads = threadsList.toArray();
            // Add the thread block to cache
            threadListCache.add(key, new CacheableLongArray(threads));
            /**
             * The actual block may be smaller than THREAD_BLOCK_SIZE. If that's
             * the case, it means two things:
             *  1) We're at the end boundary of all the results.
             *  2) If the start index is greater than the length of the current
             *     block, than there aren't really any results to return.
             */
            if (startIndex >= blockStart + threads.length) {
                // Return an empty array
                return EMPTY_BLOCK;
            }
            else {
                return threads;
            }
        }
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
        int blockID = startIndex / MESSAGE_BLOCK_SIZE;
        int blockStart = blockID * MESSAGE_BLOCK_SIZE;
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
                // Return an empty array
                return EMPTY_BLOCK;
            }
            else {
                return messages;
            }
        }
        // Otherwise, we have to load up the block from the database.
        else {
            LongList messagesList = new LongList(MESSAGE_BLOCK_SIZE);
            Connection con = null;
            Statement stmt = null;
            try {
                con = ConnectionManager.getConnection();
                stmt = con.createStatement();
                // Set the maxium number of rows to end at the end of this block.
                ConnectionManager.setMaxRows(stmt, MESSAGE_BLOCK_SIZE * (blockID+1));
                ResultSet rs = stmt.executeQuery(query);
                // Grab MESSAGE_BLOCK_ROWS rows at a time.
                ConnectionManager.setFetchSize(rs, MESSAGE_BLOCK_SIZE);
                // Many JDBC drivers don't implement scrollable cursors the real
                // way, but instead load all results into memory. Looping through
                // the results ourselves is more efficient.
                for (int i=0; i<blockStart; i++) {
                    rs.next();
                }
                // Keep reading results until the result set is exaughsted or
                // we come to the end of the block.
                int count = 0;
                while (rs.next() && count < MESSAGE_BLOCK_SIZE) {
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
                //Return an empty array
                return EMPTY_BLOCK;
            }
            else {
                return messages;
            }
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
                String name = rs.getString(1);
                String value = rs.getString(2);
                properties.put(name, value);
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
     * Saves all extended forum properties to the database.
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
            try {  pstmt.close(); }
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
            try {  pstmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    /**
     * Loads forum data from the database.
     */
    private void loadFromDb() throws ForumNotFoundException {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOAD_FORUM);
            pstmt.setLong(1,id);
            ResultSet rs = pstmt.executeQuery();
            if( !rs.next() ) {
                throw new ForumNotFoundException("Forum " + getID() +
                    " could not be loaded from the database.");
            }
            this.id = rs.getLong(1);
            this.name = rs.getString(2);
            this.description = rs.getString(3);
            this.modDefaultThreadValue = rs.getInt(4);
            this.modDefaultMessageValue = rs.getInt(5);
            this.modMinThreadValue = rs.getInt(6);
            this.modMinMessageValue = rs.getInt(7);
            this.modifiedDate =
                new java.util.Date(Long.parseLong(rs.getString(8).trim()));
            this.creationDate =
                new java.util.Date(Long.parseLong(rs.getString(9).trim()));

            // Now, load any extended forum properties
            if (!LAZY_PROP_LOADING) {
                pstmt.close();
                properties = new Hashtable();
                pstmt = con.prepareStatement(LOAD_PROPERTIES);
                pstmt.setLong(1, id);
                rs = pstmt.executeQuery();
                while(rs.next()) {
                    String name = rs.getString(1);
                    String value = rs.getString(2);
                    properties.put(name, value);
                }
            }
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
            throw new ForumNotFoundException("Forum " + getID() +
                " could not be loaded from the database.");
        }
        catch (NumberFormatException nfe) {
            System.err.println("WARNING: In DbForum.loadFromDb() -- there " +
                "was an error parsing the dates returned from the database. Ensure " +
                "that they're being stored correctly.");
        }
        finally {
            try {  pstmt.close(); }
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
            pstmt = con.prepareStatement(ADD_FORUM);
            pstmt.setLong(1,id);
            pstmt.setString(2,name);
            pstmt.setString(3,description);
            pstmt.setInt(4, modDefaultThreadValue);
            pstmt.setInt(5, modDefaultMessageValue);
            pstmt.setInt(6, modMinThreadValue);
            pstmt.setInt(7, modMinMessageValue);
            pstmt.setString(8, StringUtils.dateToMillis(creationDate));
            pstmt.setString(9, StringUtils.dateToMillis(modifiedDate));
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
     * Saves forum data to the database.
     */
    private synchronized void saveToDb() {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(SAVE_FORUM);
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setInt(3, modDefaultThreadValue);
            pstmt.setInt(4, modDefaultMessageValue);
            pstmt.setInt(5, modMinThreadValue);
            pstmt.setInt(6, modMinMessageValue);
            pstmt.setString(7, StringUtils.dateToMillis(creationDate));
            pstmt.setString(8, StringUtils.dateToMillis(modifiedDate));
            pstmt.setLong(9, id);
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
}