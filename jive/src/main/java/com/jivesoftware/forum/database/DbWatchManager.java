/**
 * $RCSfile: DbWatchManager.java,v $
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

import com.jivesoftware.forum.*;
import com.jivesoftware.forum.util.*;
import com.jivesoftware.util.*;

/**
 * Database implementation of the WatchManager interface. The following Jive
 * properties control the behavior of this class:<ul>
 *
 *  <li><tt>watches.emailNotifyEnabled</tt> -- set to <tt>true</tt> or </tt> false to
 *      enable or disable email watch notifications. Default is false.
 *  <li><tt>watches.cacheSize</tt> -- the size of the watch cache in bytes. Default
 *      value is 512K.
 *  <li><tt>watches.deleteDays</tt> -- the number of days a thread can be
 *      inactive before a watch on that thread is automatically deleted. Default
 *      is 30 days.
 * </ul>
 *
 * @see EmailWatchUpdateTask
 */
public class DbWatchManager implements WatchManager {

    /** DATABASE QUERIES **/
    private static final String CREATE_WATCH =
        "INSERT INTO jiveWatch(userID,forumID,threadID,watchType,expirable) " +
        "VALUES (?,?,?,?,1)";
    private static final String DELETE_WATCH =
        "DELETE FROM jiveWatch WHERE userID=? AND threadID=? AND watchType=?";
    private static final String DELETE_THREAD_WATCHES =
        "DELETE FROM jiveWatch WHERE threadID=?";
    private static final String ALL_WATCHES =
        "SELECT jiveWatch.threadID, jiveWatch.expirable, jiveThread.forumID, " +
        "modifiedDate FROM jiveWatch, jiveThread WHERE jiveWatch.userID=? AND " +
        "jiveWatch.watchType=? AND jiveWatch.threadID=jiveThread.threadID " +
        "ORDER BY modifiedDate DESC";
    private static final String ALL_WATCHES_BY_FORUM =
        "SELECT jiveWatch.threadID, jiveThread.forumID, modifiedDate FROM " +
        "jiveWatch, jiveThread WHERE jiveWatch.userID=? AND jiveWatch.watchType=? " +
        "AND jiveWatch.threadID=jiveThread.threadID ORDER BY jiveThread.forumID, " +
        "modifiedDate DESC";
    private static final String GET_EXPIRED_WATCHES =
        "SELECT jiveWatch.threadID FROM jiveWatch, jiveThread WHERE " +
        "jiveWatch.threadID=jiveThread.threadID AND jiveThread.modifiedDate < ? " +
        "AND jiveWatch.expirable=1";
    private static final String EXPIRE_OLD_WATCHES =
        "DELETE FROM jiveWatch WHERE threadID IN (SELECT jiveWatch.threadID FROM " +
        "jiveWatch, jiveThread WHERE jiveWatch.threadID=jiveThread.threadID AND " +
        "jiveThread.modifiedDate < ? AND jiveWatch.expirable=1)";
    private static final String UPDATE_WATCH_EXPIRATION =
        "UPDATE jiveWatch SET expirable=? WHERE userID=? AND threadID=? AND watchType=?";

    // Jive property values.
    private boolean emailNotifyEnabled = false;
    private int deleteDays;
    private String fromName;
    private String fromEmail;
    private String subject;
    private String body;

    private Cache watchCache;
    private DbForumFactory factory;

    /**
     * Creates a new DbWatchManager.
     */
    public DbWatchManager(DbForumFactory factory) {
        this.factory = factory;

        // Enable or disable email notifications
        String emailNotify = JiveGlobals.getJiveProperty("watches.emailNotifyEnabled");
        if ("true".equals(emailNotify)) {
            emailNotifyEnabled = true;
        }

        // Set number of days that watches on unmodified threads will live.
        // Default is 30 days.
        deleteDays = 30;
        String days = JiveGlobals.getJiveProperty("watches.deleteDays");
        if (days != null) {
            try {
                deleteDays = Integer.parseInt(days);
            } catch (Exception e) { }
        }

        // Load email watch properties.
        fromName = JiveGlobals.getJiveProperty("watches.email.fromName");
        fromEmail = JiveGlobals.getJiveProperty("watches.email.fromEmail");
        subject = JiveGlobals.getJiveProperty("watches.email.subject");
        body = JiveGlobals.getJiveProperty("watches.email.body");

        //If any properties fail to load, print error.
        if (fromName==null || fromEmail==null || subject==null || body==null) {
            System.err.println("Error! One or more email watch properties could " +
            "not be loaded. Email watch updates cannot be sent until the " +
            "problem is resolved.");
        }

        // Create watch cache with correct size.
        int cacheSize = 512*1024;   //default is 512K
        String size = JiveGlobals.getJiveProperty("watches.cacheSize");
        if (size != null) {
            try {
                cacheSize = Integer.parseInt(size);
            } catch (Exception e) { }
        }
        watchCache = new Cache(cacheSize, 1*JiveGlobals.HOUR);

        // Add a scheduled task to delete old watches.
        long period = 12*JiveGlobals.HOUR;
        TaskEngine.scheduleTask(new DeleteWatchesTask(),period,period);
    }

    public int getDeleteDays() {
        return deleteDays;
    }

    public void setDeleteDays(int deleteDays) {
        this.deleteDays = deleteDays;
        JiveGlobals.setJiveProperty("watches.deleteDays", Integer.toString(deleteDays));
    }

    public boolean isEmailNotifyEnabled() {
        return emailNotifyEnabled;
    }

    public void setEmailNotifyEnabled(boolean enabled) {
        this.emailNotifyEnabled = enabled;
        JiveGlobals.setJiveProperty("watches.emailNotifyEnabled", ""+emailNotifyEnabled);
    }

    public String getEmailBody()
    {
        return body;
    }

    public void setEmailBody(String body)
    {
        this.body = body;
        JiveGlobals.setJiveProperty("watches.email.body", body);
    }

    public String getEmailSubject()
    {
        return subject;
    }

    public void setEmailSubject(String subject)
    {
        this.subject = subject;
        JiveGlobals.setJiveProperty("watches.email.subject", subject);
    }

    public String getEmailFromName()
    {
        return fromName;
    }

    public void setEmailFromName(String fromName)
    {
        this.fromName = fromName;
        JiveGlobals.setJiveProperty("watches.email.fromName", fromName);
    }

    public String getEmailFromEmail()
    {
        return fromEmail;
    }

    public void setEmailFromEmail(String fromEmail)
    {
        this.fromEmail = fromEmail;
        JiveGlobals.setJiveProperty("watches.email.fromEmail", fromEmail);
    }

    public void createWatch(User user, ForumThread thread, int watchType) {
        //If the user is already watching the thread, do nothing.
        if (getWatchList(user, watchType).isWatchedThread(thread.getID())) {
            return;
        }
        //Otherwise, add a new watch.
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(CREATE_WATCH);
            pstmt.setLong(1, user.getID());
            pstmt.setLong(2, thread.getForum().getID());
            pstmt.setLong(3, thread.getID());
            pstmt.setInt(4, watchType);
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
        //Remove user's watch list from cache.
        StringBuffer key = new StringBuffer(Long.toString(user.getID()));
        key.append("-").append(watchType);
        watchCache.remove(key.toString());
    }

    public void deleteWatch(User user, ForumThread thread, int watchType) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(DELETE_WATCH);
            pstmt.setLong(1, user.getID());
            pstmt.setLong(2, thread.getID());
            pstmt.setInt(3, watchType);
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
        //Remove user's watch list from cache.
        StringBuffer key = new StringBuffer(Long.toString(user.getID()));
        key.append("-").append(watchType);
        watchCache.remove(key.toString());
    }

    public boolean isWatchExpirable(User user, ForumThread thread,
            int watchType)
    {
        return getWatchList(user, watchType).isWatchExpirable(thread.getID());
    }

    public void setWatchExpirable(User user, ForumThread thread, int watchType,
            boolean expirable) throws UnauthorizedException
    {
        WatchList wList = getWatchList(user, watchType);
        long threadID = thread.getID();
        boolean isWatched = wList.isWatchedThread(threadID);
        boolean isExpirable = wList.isWatchExpirable(threadID);
        //Only change the entry if it exists and if it's a change from the
        //current value.
        if (isWatched && (isExpirable != expirable)) {
            Connection con = null;
            PreparedStatement pstmt = null;
            try {
                con = ConnectionManager.getConnection();
                pstmt = con.prepareStatement(UPDATE_WATCH_EXPIRATION);
                pstmt.setInt(1, expirable?1:0);
                pstmt.setLong(2, user.getID());
                pstmt.setLong(3, thread.getID());
                pstmt.setInt(4, watchType);
                pstmt.executeUpdate();
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
            //Remove user's watch list from cache.
            StringBuffer key = new StringBuffer(Long.toString(user.getID()));
            key.append("-").append(watchType);
            watchCache.remove(key.toString());
        }
    }

    public Iterator getWatchedForumThreads(Forum forum, User user, int watchType)
    {
        return getWatchList(user, watchType).watchedThreads(forum);
    }

    public Iterator getWatchedForumThreads(User user, int watchType) {
        return getWatchList(user, watchType).watchedThreads();
    }

    public int getWatchCount(Forum forum, User user, int watchType) {
        return getWatchList(user, watchType).getWatchCount(forum);
    }

    public int getWatchCount(User user, int watchType) {
        return getWatchList(user, watchType).getWatchCount();
    }

    public boolean isWatchedThread(User user, ForumThread thread, int watchType) {
        return getWatchList(user, watchType).isWatchedThread(thread.getID());
    }

    /**
     * Notifies watches that a thread has been updated. If there are email
     * watches, email notifications are sent out.
     */
    protected void notifyWatches(ForumThread thread) {
        //If watches are turned on.
        if (!emailNotifyEnabled) {
            return;
        }
        //Create a new email watch update task that will send out emails to
        //any users that have watches on the updated thread.
        EmailWatchUpdateTask task = new EmailWatchUpdateTask(this, factory, thread);
        TaskEngine.addTask(task);
    }

    /**
     * Deletes all watches on a thread. Typically, this method is called when
     * deleting a thread from the system. A Connection is passed in so that
     * this method can participate in a transaction.
     *
     * @param thread the ForumThread to remove all watches from.
     * @param con a database connection to perform work with.
     */
    protected void deleteThreadWatches(ForumThread thread, Connection con)
            throws SQLException
    {
        int numRows;
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(DELETE_THREAD_WATCHES);
            pstmt.setLong(1,thread.getID());
            numRows = pstmt.executeUpdate();
        }
        finally {
            try {  pstmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
        }
        //If any watches were deleted, expire the watch cache. This is
        //potentially a "bad" thing if there is a huge number of active users
        //looking at their watches, but is a good trade-off for now compared to
        //trying to expire individual cache entries.
        if (numRows > 0) {
            watchCache.clear();
        }
    }

    private WatchList getWatchList(User user, int watchType) {
        //Remove user's watch list from cache.
        StringBuffer key = new StringBuffer(Long.toString(user.getID()));
        key.append("-").append(watchType);

        WatchList watchList = (WatchList)watchCache.get(key.toString());
        if (watchList != null) {
            return watchList;
        }

        LongList threadsByDate = new LongList();
        BooleanList expirableByDate = new BooleanList();
        LongList forumsByDate = new LongList();
        LongList threadsByForum = new LongList();
        LongList forumsByForum = new LongList();

        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(ALL_WATCHES);
            pstmt.setLong(1, user.getID());
            pstmt.setInt(2, watchType);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                threadsByDate.add(rs.getLong(1));
                expirableByDate.add(rs.getBoolean(2));
                forumsByDate.add(rs.getLong(3));
            }

            pstmt.close();
            pstmt = con.prepareStatement(ALL_WATCHES_BY_FORUM);
            pstmt.setLong(1, user.getID());
            pstmt.setInt(2, watchType);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                threadsByForum.add(rs.getLong(1));
                forumsByForum.add(rs.getLong(2));
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

        watchList = new WatchList(
                threadsByDate.toArray(),
                expirableByDate.toArray(),
                forumsByDate.toArray(),
                threadsByForum.toArray(),
                forumsByForum.toArray(),
                factory
            );
        //Add watch list to cache for next time, then return it.
        watchCache.add(key.toString(), watchList);
        return watchList;
    }

    /**
     * A task the deletes all watches that are "delatable" and that are attached
     * to threads older than a certain value.
     *
     * The class does not deal with watch cache expiration since that would
     * require deletion of the entire cache. Instead, we just let the watch
     * objects expire naturally.
     */
    class DeleteWatchesTask implements Runnable {

        public void run() {
            long now = System.currentTimeMillis();
            long deleteDays = JiveGlobals.DAY * getDeleteDays();
            long oldestDate = now - deleteDays;

            Connection con = null;
            PreparedStatement pstmt = null;
            boolean abortTransaction = false;
            try {
                con = ConnectionManager.getTransactionConnection();
                //Simple case: database supports sub-selects. This query can be
                //slow if many records are affected, but will at least work.
                if (ConnectionManager.supportsSubqueries) {
                    pstmt = con.prepareStatement(EXPIRE_OLD_WATCHES);
                    pstmt.setLong(1, oldestDate);
                    pstmt.execute();
                }
                //Complicated case: db doesn't support sub-selects. So, build
                //up the "IN" clause ourselves. We'll submit them 100 records
                //at a time.
                else {
                    pstmt = con.prepareStatement(GET_EXPIRED_WATCHES);
                    pstmt.setLong(1, oldestDate);
                    ResultSet rs = pstmt.executeQuery();
                    LongList expiredThreads = new LongList();
                    while (rs.next()) {
                        expiredThreads.add(rs.getLong(1));
                    }
                    pstmt.close();

                    int length = expiredThreads.size();
                    //Do nothing if there are no records to delete.
                    if (length == 0) {
                        return;
                    }

                    int numBlocks = length / 100;
                    for (int i=0; i <= numBlocks; i++) {
                        int blockNum = i*100;
                        //Build up "IN" SQL statement list.
                        StringBuffer buf = new StringBuffer();
                        buf.append(expiredThreads.get(blockNum));
                        int j=1;
                        while (j < 100 && blockNum+j < length) {
                            buf.append(",").append(expiredThreads.get(blockNum+j));
                            j++;
                        }
                        //Do db delete.
                        String sql = "DELETE FROM jiveWatch WHERE threadID IN (" +
                                buf.toString() + ")";
                        Statement stmt = con.createStatement();
                        stmt.execute(sql);
                        stmt.close();
                     }
                }
            }
            catch( SQLException sqle ) {
                abortTransaction = true;
                sqle.printStackTrace();
            }
            finally {
                try {
                    if (ConnectionManager.supportsSubqueries) { pstmt.close(); }
                }
                catch (Exception e) { e.printStackTrace(); }
                try {
                    ConnectionManager.closeTransactionConnection(con, abortTransaction);
                }
                catch (Exception e) { e.printStackTrace(); }
            }
        }
    }
}