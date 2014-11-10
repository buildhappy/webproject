/**
 * $RCSfile: DbSearchManager.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:58 $
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
import java.io.*;

import org.apache.lucene.document.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;

import com.jivesoftware.forum.*;
import com.jivesoftware.forum.util.*;
import com.jivesoftware.util.*;
import com.jivesoftware.util.TimerTask;
import org.apache.lucene.analysis.cn.*;

/**
 * Database implementation of SearchManager using the Lucene search package.
 * Search indexes are stored in the "search" subdirectory of <tt>jiveHome</tt>.
 */
class DbSearchManager implements SearchManager, Runnable {

    /** DATABASE QUERIES **/
    private static final String MESSAGES_BEFORE_DATE =
        "SELECT jiveMessage.messageID, jiveMessage.userID, jiveMessage.threadID, " +
                "jiveMessage.forumID, subject, body, jiveMessage.creationDate " +
          "FROM jiveMessage, jiveForum " +
         "WHERE jiveForum.forumID=jiveMessage.forumID " +
           "AND messageID >= ? AND messageID < ? " +
           "AND jiveMessage.modifiedDate < ? " +
           "AND jiveMessage.modValue >= jiveForum.modMinThreadVal";
    private static final String MESSAGES_BEFORE_DATE_COUNT =
        "SELECT count(1) " +
          "FROM jiveMessage, jiveForum " +
         "WHERE jiveForum.forumID=jiveMessage.forumID " +
           "AND jiveMessage.modValue >= jiveForum.modMinThreadVal " +
           "AND jiveMessage.modifiedDate < ?";
    private static final String MESSAGES_SINCE_DATE =
        "SELECT jiveMessage.messageID, jiveMessage.userID, jiveMessage.threadID, " +
                "jiveMessage.forumID, subject, body, jiveMessage.creationDate " +
          "FROM jiveMessage, jiveForum " +
         "WHERE jiveForum.forumID=jiveMessage.forumID " +
           "AND jiveMessage.modValue >= jiveForum.modMinThreadVal " +
           "AND jiveMessage.modifiedDate > ? AND jiveMessage.modifiedDate < ?";
    private static final String MESSAGES_SINCE_DATE_COUNT =
        "SELECT count(1) " +
          "FROM jiveMessage, jiveForum " +
         "WHERE jiveForum.forumID=jiveMessage.forumID " +
           "AND jiveMessage.modValue >= jiveForum.modMinThreadVal " +
           "AND jiveMessage.modifiedDate > ? AND jiveMessage.modifiedDate < ?";
    private static final String MESSAGE_IDS_SINCE_DATE =
        "SELECT jiveMessage.messageID " +
          "FROM jiveMessage, jiveForum " +
         "WHERE jiveForum.forumID=jiveMessage.forumID " +
           "AND jiveMessage.modValue >= jiveForum.modMinThreadVal " +
           "AND jiveMessage.modifiedDate > ? AND jiveMessage.modifiedDate < ?";
    private static final String HIGHEST_MESSAGE_ID =
            "SELECT MAX(messageID) FROM jiveMessage";

    /**
     * The number of messages to retrieve at once during index rebuilds.
     */
    private static final int BLOCK_SIZE = 500;

    /**
     * Path to where index is stored.
     */
    private static String indexPath = null;

    /**
     * The analyzer governs how words are tokenized. The standard anaylyzer
     * does a decent job in most cases, but could be replaced under certain
     * circumstances. We make the analyzer protected so that the DbQuery class
     * can also reference it (indexing and searching should use the same
     * analyzer).
     */
    protected static Analyzer analyzer = new ChineseAnalyzer();

    /**
     * Indicates whether search is on or off at a global level. If off, index
     * add and delete operations will be ignored, and auto-indexing will be
     * disabled.
     */
    private boolean searchEnabled = true;

    /**
     * Indicates whether auto-indexing should be on or off. When on, an update
     * will be run at the "updateInterval".
     */
    private boolean autoIndexEnabled = true;

    /**
     * Maintains the amount of time in minutes should elapse before the next
     * index auto-update.
     */
    private int autoIndexInterval;

    /**
     * Maintains the time that the last index took place.
     */
    private Date lastIndexed;

    /**
     * True if the indexer is busy. False otherwise.
     */
    private boolean busy = false;

    // Values used to calculate the current percent complete.
    int currentCount = 0;
    int totalCount = -1;
    private Object lock = new Object();

    /**
     * The scheduled task for auto-indexing.
     */
    private TimerTask timerTask = null;

    /**
     * Creates a new DbSearchIndexer. It attempts to load properties for
     * the update interval and when the last index occured from the Jive
     * properties then starts the indexing thread.
     */
    public DbSearchManager() {
        // Default to performing updates ever 10 minutes.
        autoIndexInterval = 10;
        // If the update interval property exists, use that
        String interval = JiveGlobals.getJiveProperty("search.autoIndexInterval");
        try {
            autoIndexInterval = Integer.parseInt(interval);
        }
        catch (Exception e) { /* ignore */ }

        // Determine if search should be turned on.
        String enabled = JiveGlobals.getJiveProperty("search.enabled");
        try {
            searchEnabled = Boolean.valueOf(enabled).booleanValue();
        }
        catch (Exception e) { }

        // Determine if auto-indexing should be turned on.
        String index = JiveGlobals.getJiveProperty("search.autoIndexEnabled");
        try {
            autoIndexEnabled = Boolean.valueOf(index).booleanValue();
        }
        catch (Exception e) { }

        // Attempt to get the last updated time from the Jive properties
        String lastInd = JiveGlobals.getJiveProperty("search.lastIndexed");
        try {
            lastIndexed = new Date(Long.parseLong(lastInd));
        }
        catch (Exception e) {
            // Something went wrong. Therefore, set lastIndexed far into the
            // past so that we'll do a full re-index. If you've time travelled
            // backwards too far with this code, this could be a problem.
            lastIndexed = new Date(0);
        }
        // If autoIndexing is turned on, schedule indexing task.
        if (autoIndexEnabled) {
            timerTask = TaskEngine.scheduleTask(
                    this,autoIndexInterval*JiveGlobals.MINUTE,
                    autoIndexInterval*JiveGlobals.MINUTE);
        }
    }

    public int getAutoIndexInterval() {
        return autoIndexInterval;
    }

    public void setAutoIndexInterval(int minutes) {
        this.autoIndexInterval = minutes;
        JiveGlobals.setJiveProperty("search.autoIndexInterval",
                "" + autoIndexInterval);
        // Restart indexer with new schedule.
        if (timerTask != null) {
            timerTask.cancel();
        }
        timerTask = TaskEngine.scheduleTask(
                this,autoIndexInterval*JiveGlobals.MINUTE,
                autoIndexInterval*JiveGlobals.MINUTE);
    }

    public Date getLastIndexedDate() {
        return lastIndexed;
    }

    public boolean isSearchEnabled() {
        return searchEnabled;
    }

    public void setSearchEnabled(boolean searchEnabled) {
        this.searchEnabled = searchEnabled;
        JiveGlobals.setJiveProperty("search.enabled", String.valueOf(searchEnabled));
    }

    public boolean isBusy() {
        return busy;
    }

    public synchronized int getPercentComplete() {
        if (!busy) {
            return -1;
        }
        // If we're busy but the total count hasn't been set yet, return 0.
        if (totalCount == -1 || totalCount == 0) {
            return 0;
        }
        else {
            return (int)(((double)currentCount/(double)totalCount)*100.0);
        }
    }

    public boolean isAutoIndexEnabled() {
        return autoIndexEnabled;
    }

    public void setAutoIndexEnabled(boolean enabled) {
        // If we are switching from off to on, enable indexer
        if (!autoIndexEnabled && enabled) {
            if (timerTask != null) {
                timerTask.cancel();
            }
            timerTask = TaskEngine.scheduleTask(
                    this,autoIndexInterval*JiveGlobals.MINUTE,
                    autoIndexInterval*JiveGlobals.MINUTE);
        }
        // Otherwise, if switching from on to off, disable indexer.
        else if (autoIndexEnabled && !enabled) {
            if (timerTask != null) {
                timerTask.cancel();
            }
        }
        autoIndexEnabled = enabled;
        JiveGlobals.setJiveProperty("search.autoIndexEnabled", "" + enabled);
    }

    public synchronized void addToIndex(ForumMessage message) {
        if (!searchEnabled) {
            return;
        }
        IndexWriter writer = null;
        try {
            writer = getWriter(false);

            long messageID = message.getID();
            long userID = -1;
            if (!message.isAnonymous()) {
                userID = message.getUser().getID();
            }
            long threadID = message.getForumThread().getID();
            long forumID = message.getForumThread().getForum().getID();
            String subject = message.getUnfilteredSubject();
            String body = message.getUnfilteredBody();

            addMessageToIndex(messageID, userID, threadID, forumID,
                    subject, body, message.getCreationDate(), writer);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        finally{
            try { writer.close(); }
            catch (Exception e) { }
        }
    }

    public synchronized void removeFromIndex(ForumMessage message) {
        if (!searchEnabled) {
            return;
        }
        try {
            long [] toDelete = new long [] { message.getID() };
            deleteMessagesFromIndex(toDelete);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public synchronized void updateIndex() {
        if (!searchEnabled) {
            return;
        }
        // Add a task to the task engine to update the index.
        TaskEngine.addTask(new IndexTask(false));
    }

    public synchronized void rebuildIndex() {
        if (!searchEnabled) {
            return;
        }
        // Add a task to the task engine to update the index.
        TaskEngine.addTask(new IndexTask(true));
    }

    public synchronized void optimize() {
        if (busy) {
            return;
        }
        try {
            IndexWriter writer = getWriter(false);
            if (writer != null) {
                writer.optimize();
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    //OTHER METHODS//

    /**
     * Auto-indexing logic. It will automatically be scheduled to run at the
     * desired interval if auto-indexing is turned on.
     */
    public synchronized void run() {
        // Do nothing if searching is disabled.
        if (!searchEnabled) {
            return;
        }
        // Add a task to the task engine to update the index.
        TaskEngine.addTask(new IndexTask(false));
    }

   /**
     * Indexes an indivual message. The writer is assumed to be open when
     * passed in and will remain open after the method is done executing.
     */
    protected final void addMessageToIndex(long messageID, long userID,
            long threadID, long forumID, String subject, String body,
            java.util.Date creationDate, IndexWriter writer) throws IOException
    {
        if (writer == null) {
            return;
        }

        Document doc = new Document();
        doc.add(Field.Keyword("messageID",Long.toString(messageID)));
        doc.add(new Field("userID", Long.toString(userID), false, true, false));
        doc.add(new Field("threadID", Long.toString(threadID), false, true, false));
        doc.add(new Field("forumID", Long.toString(forumID), false, true, false));
        doc.add(Field.UnStored("subject", subject));
        doc.add(Field.UnStored("body", body));
        doc.add(new Field("creationDate", DateField.dateToString(creationDate),
                false, true, false));

        writer.addDocument(doc);
    }

    /**
     * Deletes a message from the index.
     */
    private final void deleteMessagesFromIndex(long [] messages)
            throws IOException
    {
        if (messages == null) {
            return;
        }
        IndexReader reader = getReader();
        if (reader == null) {
            //Reader will be null if the search index doesn't exist.
            return;
        }
        try {
            Term messageIDTerm;
            for (int i=0; i<messages.length; i++) {
                messageIDTerm = new Term("messageID", Long.toString(messages[i]));
                try {
                    reader.delete(messageIDTerm);
                }
                //Bug in current version of Lucene. It will throw a null pointer
                //if the term to be deleted isn't found in the index.
                catch (Exception e) { }
            }
        }
        finally {
            try {
                reader.close();
            }
            catch (Exception e) { }
        }
    }

    /**
     * Rebuilds the search index from scratch. It deletes the entire index
     * and word tables and then indexes every message up to the end time.
     */
    private final void rebuildIndex(Date end) {
        Connection con = null;
        PreparedStatement pstmt = null;
        IndexWriter writer = null;
        String endDate = StringUtils.dateToMillis(end);
        try {
            writer = getWriter(true);
            con = ConnectionManager.getConnection();
            // Get a count of the messages that will be added to index.
            pstmt = con.prepareStatement(MESSAGES_BEFORE_DATE_COUNT);
            pstmt.setString(1, endDate);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            // Set the total count so that we can keep track of percentage
            // complete.
            totalCount = rs.getInt(1);
            pstmt.close();

            // Some JDBC drivers don't stream results, but read all records
            // returned by a query into memory. Most notably, the current
            // MySQL JDBC drivers have this problem. As a workaround, we read
            // message data in blocks (default 500).

            // First, get the highest messageID in the database.
            pstmt = con.prepareStatement(HIGHEST_MESSAGE_ID);
            rs = pstmt.executeQuery();
            rs.next();
            long sentinal = (long)(rs.getLong(1) / BLOCK_SIZE);
            pstmt.close();

            // Now, use the messageIDs to select blocks of message data to add
            // to the index at a time.
            pstmt = con.prepareStatement(MESSAGES_BEFORE_DATE);
            for (int i = 0; i <= sentinal; i++) {
                pstmt.setLong(1, BLOCK_SIZE * i);
                pstmt.setLong(2, BLOCK_SIZE * (i + 1));
                pstmt.setString(3, endDate);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    // Increment current count to track percentage complete.
                    currentCount++;
                    long messageID = rs.getLong(1);
                    long userID = rs.getLong(2);
                    long threadID = rs.getLong(3);
                    long forumID = rs.getLong(4);
                    String subject = rs.getString(5);
                    String body = rs.getString(6);
                    Date creationDate =
                            new Date(Long.parseLong(rs.getString(7).trim()));
                    addMessageToIndex(messageID, userID, threadID, forumID, subject,
                            body, creationDate, writer);
                }
                rs.close();
                writer.optimize();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try { pstmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try { con.close();  }
            catch (Exception e) { e.printStackTrace(); }
            try { writer.close(); }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    /**
     * Updates the index. It first deletes any messages in the index between
     * the start and end times, and then adds all messages to the index that
     * are between the start and end times.
     */
    protected final void updateIndex(Date start, Date end) {
        Connection con = null;
        PreparedStatement pstmt = null;
        IndexWriter writer = null;
        LongList messages = new LongList();
        try {
            con = ConnectionManager.getConnection();
            // For a clean update, we need to make sure that we first delete
            // any index entries that were made since we last updated. This
            // might happen if a process was calling indexMessage() between runs
            // of this method. For this reason, the two types of indexing (manual
            // and automatic) should not be intermixed. However, we still perform
            // this deletion to be safe.
            pstmt = con.prepareStatement(MESSAGE_IDS_SINCE_DATE);
            pstmt.setString(1, StringUtils.dateToMillis(start));
            pstmt.setString(2, StringUtils.dateToMillis(end));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                messages.add(rs.getLong(1));
            }
            pstmt.close();
            // Now, delete those messages
            deleteMessagesFromIndex(messages.toArray());

            // Get a count of the messages that will be added to index.
            pstmt = con.prepareStatement(MESSAGES_SINCE_DATE_COUNT);
            pstmt.setString(1, StringUtils.dateToMillis(start));
            pstmt.setString(2, StringUtils.dateToMillis(end));
            rs = pstmt.executeQuery();
            rs.next();
            // Set the total count so that we can keep track of percentage
            // complete.
            totalCount = rs.getInt(1);
            pstmt.close();

            // Add the messages to the index.
            writer = getWriter(false);
            pstmt = con.prepareStatement(MESSAGES_SINCE_DATE);
            pstmt.setString(1, StringUtils.dateToMillis(start));
            pstmt.setString(2, StringUtils.dateToMillis(end));
            rs = pstmt.executeQuery();
            while (rs.next()) {
                // Increment current count to track percentage complete.
                currentCount++;
                long messageID = rs.getLong(1);
                long userID = rs.getLong(2);
                long threadID = rs.getLong(3);
                long forumID = rs.getLong(4);
                String subject = rs.getString(5);
                String body = rs.getString(6);
                Date creationDate =
                    new Date(Long.parseLong(rs.getString(7).trim()));
                addMessageToIndex(messageID, userID, threadID, forumID, subject,
                        body, creationDate, writer);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {  pstmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
            try {  writer.close(); }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    /**
     * Returns a Lucene IndexWriter. The create param indicates whether an
     * existing index should be used if it's found there.
     */
    private static IndexWriter getWriter(boolean create) throws IOException {
        if (indexPath == null) {
            // Get path of where search index should be. It should be
            // the search subdirectory of [jiveHome].
            String jiveHome = JiveGlobals.getJiveHome();
            if (jiveHome == null) {
                System.err.println("ERROR: the jiveHome property is not set.");
                throw new IOException("Unable to open index for searching " +
                        "because jiveHome was not set.");
            }
            indexPath =  jiveHome + File.separator + "search";
        }

        IndexWriter writer = null;

        // If create is true, we always want to create a new index.
        if (create) {
            try {
                writer = new IndexWriter(indexPath, analyzer, true);
            }
            catch (Exception e) {
                System.err.println("ERROR: Failed to create a new index writer.");
                e.printStackTrace();
            }
        }
        // Otherwise, use an existing index if it exists.
        else {
            if (indexExists(indexPath)) {
                try {
                    writer = new IndexWriter(indexPath, analyzer, false);
                }
                catch (Exception e) {
                    System.err.println("ERROR: Failed to open an index writer.");
                    e.printStackTrace();
                }
            }
            else {
                try {
                    writer = new IndexWriter(indexPath, analyzer, true);
                }
                catch (Exception e) {
                    System.err.println("ERROR: Failed to create a new index writer.");
                    e.printStackTrace();
                }
            }
        }

        return writer;
    }

    /**
     * Returns a Lucene IndexReader.
     */
    private static IndexReader getReader() throws IOException {
        if (indexPath == null) {
            // Get path of where search index should be. It should be
            // the search subdirectory of [jiveHome].
            String jiveHome = JiveGlobals.getJiveHome();
            if (jiveHome == null) {
                System.err.println("ERROR: the jiveHome property is not set.");
                throw new IOException("Unable to open index for searching " +
                        "because jiveHome was not set.");
            }
            indexPath = jiveHome + File.separator + "search";
        }

        if (indexExists(indexPath)) {
            IndexReader reader = IndexReader.open(indexPath);
            return reader;
        }
        else {
            return null;
        }
    }

    /**
     * Returns true if the search index exists at the specified path. We do
     * this by checking if a file called "segments" exists in the index
     * directory. This isn't an ideal way to check for an existing index, but
     * will have to suffice until a suitable method is added to the Lucene API.
     *
     * @param indexPath the path to check for the search index at.
     */
    private static boolean indexExists(String indexPath) {
        // Lucene always creates a file called "segments" -- if it exists, we
        // assume that the search index exists.
        File segments = new File(indexPath + File.separator + "segments");
        return segments.exists();
    }

    /**
     * Performs indexing tasks using the Jive Task Engine.
     */
    private class IndexTask implements Runnable {

        private boolean rebuildOperation;

        public IndexTask(boolean rebuildOperation) {
            this.rebuildOperation = rebuildOperation;
        }

        public void run() {
            synchronized(lock) {
                // If another index operation is already occuring, do nothing.
                if (busy) {
                    return;
                }
                busy = true;
            }
            try {
                Date now = new Date();
                // Do a rebuild if we were told to do so, or if the index
                // has never been built before.
                if (rebuildOperation || lastIndexed.getTime() == 0) {
                    rebuildIndex(now);
                }
                else {
                    updateIndex(lastIndexed, now);
                }
                lastIndexed = now;
                // Save the last indexed time as a Jive property.
                JiveGlobals.setJiveProperty("search.lastIndexed", "" + now.getTime());
            }
            finally {
                // Reset counts
                currentCount = 0;
                totalCount = -1;
                // Reset state of the search manager to idle.
                busy = false;
            }
        }
    }
}