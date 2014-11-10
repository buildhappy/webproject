/**
 * $RCSfile: DbRewardManager.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:57 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import com.jivesoftware.forum.*;
import com.jivesoftware.util.*;
import java.sql.*;
import java.util.Iterator;

/**
 * Database implementation of the RewardManager interface. A user's point total
 * is stored as part of their user database record. Points values are also
 * maintained in the thread and message tables. Although storing values in all
 * three places isn't ideal in a relational sense, it's very necessary to make
 * speed tolerable.<p>
 *
 * For auditing purposes, points awards are recorded in a special database table
 * which tracks the ID of the user receiving the points, the amount of points
 * awarded, and a timestamp. Optionally, the thread and message IDs are stored.
 * This schema allows us to track normal point awards as well as when
 * administrators assigning arbitrary point values.<p>
 *
 * To customize the max number of reward points that can be set for a thread,
 * adjust the Jive property <tt>rewards.maxPoints</tt>.
 */
public class DbRewardManager implements RewardManager {

    /** DATABASE QUERIES **/
    private static final String CREATE_REWARD_ENTRY =
        "INSERT INTO jiveReward(userID, creationDate, rewardPoints, " +
        "messageID, threadID) VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE_MESSAGE_ENTRY =
        "UPDATE jiveReward SET messageID=NULL, threadID=NULL WHERE messageID=?";
    private static final String TOTAL_POINTS_EARNED =
        "SELECT sum(rewardPoints) FROM jiveReward WHERE userID=?";
    private static final String TOTAL_POINTS_REWARDED =
        "SELECT sum(rewardPoints) FROM jiveReward, jiveMessage WHERE " +
        "jiveReward.messageID=jiveMessage.messageID AND jiveMessage.userID=?";
    private static final String USER_REWARD_THREADS =
        "SELECT jiveThread.threadID FROM jiveThread, jiveMessage WHERE userID=? " +
        "AND jiveMessage.threadID=jiveThread.threadID AND " +
        "jiveMessage.parentMessageID IS NULL AND jiveThread.rewardPoints > 0 " +
        "ORDER BY jiveThread.modifiedDate DESC";


    private int maxRewardPoints = Integer.MAX_VALUE;
    private DbForumFactory factory;

    public DbRewardManager(DbForumFactory factory) {
        this.factory = factory;

        String maxPoints = JiveGlobals.getJiveProperty("rewards.maxPoints");
        if (maxPoints != null) {
            try {
                maxRewardPoints = Integer.parseInt(maxPoints);
            } catch (Exception e) { }
        }
    }

    public int getMaxPoints() {
        return maxRewardPoints;
    }

    public void setMaxPoints(int numPoints) {
        this.maxRewardPoints = numPoints;
        JiveGlobals.setJiveProperty("rewards.maxPoints", Integer.toString(numPoints));
    }

    public synchronized void transferPoints(ForumThread thread, int numPoints)
            throws RewardException
    {
        //Won't work if thread was created anonymously.
        if (thread.getRootMessage().isAnonymous()) {
             throw new RewardException("Can't transfer points to anonymous threads.");
        }
        User user = thread.getRootMessage().getUser();
        int userPoints = getCurrentPoints(user);
        //Can't transfer more points than the user has.
        if (numPoints > userPoints) {
            throw new RewardException("User has insufficient points for transfer.");
        }
        //The number of points in the thread after the transfer must fall
        //between 0 and maxRewardPoints
        int currentThreadPoints = getPoints(thread);
        int newThreadPoints = currentThreadPoints + numPoints;
        if (newThreadPoints > maxRewardPoints || newThreadPoints < 0) {
            throw new RewardException("Reward points value not in valid range: " +
                0 + "-" + maxRewardPoints + "." );
        }
        //All checks passed, so perform point switch.
        Connection con = null;
        boolean abortTransaction = false;
        try {
            con = ConnectionManager.getTransactionConnection();

            DbForumThread dbForumThread = factory.cacheManager.threadCache.get(
                    thread.getID());
            dbForumThread.setRewardPoints(newThreadPoints, con);

            user.setRewardPoints(userPoints - numPoints);
        }
        catch( Exception e ) {
            e.printStackTrace();
            abortTransaction = true;
        }
        finally {
            ConnectionManager.closeTransactionConnection(con, abortTransaction);
        }
    }

    public synchronized void rewardPoints(ForumMessage message, int numPoints)
            throws RewardException
    {
        //Message must belong to a thread.
        if (message.getForumThread() == null) {
            throw new RewardException("Message must belong to a valid thread.");
        }
        //Cannot award anonymous messages
        if (message.isAnonymous()) {
            throw new RewardException("Cannot reward anonymous post.");
        }
        User user = message.getUser();
        ForumThread thread = message.getForumThread();
        //Points award must be > 0, but no more than the total number of points
        //in the thread.
        if (numPoints <= 0 || numPoints > getPoints(thread)) {
            throw new RewardException("Points assignment must be greater than 1 " +
                "and no more than the thread has available.");
        }
        //Users cannot assign points to themselves.
        if (user.getID() == thread.getRootMessage().getUser().getID()) {
            throw new RewardException("Points cannot be assigned to thread poster.");
        }
        //All checks passed, so perform point switch.
        Connection con = null;
        PreparedStatement pstmt = null;
        boolean abortTransaction = false;
        try {
            con = ConnectionManager.getTransactionConnection();

            DbForumThread dbForumThread = factory.cacheManager.threadCache.get(
                    thread.getID());
            int newThreadPoints = dbForumThread.getRewardPoints() - numPoints;
            dbForumThread.setRewardPoints(newThreadPoints, con);

            int newUserPoints = user.getRewardPoints() + numPoints;
            user.setRewardPoints(newUserPoints);

            DbForumMessage dbMessage = factory.cacheManager.messageCache.get(
                    message.getID()
                );
            int newMessagePoints = dbMessage.getRewardPoints() + numPoints;
            dbMessage.setRewardPoints(newMessagePoints, con);

            //Make entry in reward tracking table.
            pstmt = con.prepareStatement(CREATE_REWARD_ENTRY);
            pstmt.setLong(1, user.getID());
            pstmt.setString(2, StringUtils.dateToMillis(new java.util.Date()));
            pstmt.setInt(3, numPoints);
            pstmt.setLong(4, message.getID());
            pstmt.setLong(5, thread.getID());
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
    }

    public int getPoints(ForumThread thread) {
        try {
            DbForumThread dbForumThread = factory.cacheManager.threadCache.get(
                    thread.getID());
            return dbForumThread.getRewardPoints();
        }
        catch (Exception e) { }
        return 0;
    }

    public int getPoints(ForumMessage message) {
        try {
            DbForumMessage dbMessage = factory.cacheManager.messageCache.get(
                    message.getID()
                );
            return dbMessage.getRewardPoints();
        }
        catch (Exception e) { }
        return 0;
    }

    public int getCurrentPoints(User user) {
        return user.getRewardPoints();
    }

    public int getTotalPointsRewarded(User user) {
        int points = 0;
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(TOTAL_POINTS_REWARDED);
            pstmt.setLong(1, user.getID());
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            points = rs.getInt(1);
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        finally {
            try {  pstmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close(); }
            catch (Exception e) { e.printStackTrace(); }
        }
        return points;
    }

    public int getTotalPointsEarned(User user) {
        int points = 0;
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(TOTAL_POINTS_EARNED);
            pstmt.setLong(1, user.getID());
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            points = rs.getInt(1);
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        finally {
            try {  pstmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close(); }
            catch (Exception e) { e.printStackTrace(); }
        }
        return points;
    }

    public Iterator userRewardThreads(User user) {
        LongList threads = new LongList();
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(USER_REWARD_THREADS);
            pstmt.setLong(1, user.getID());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                threads.add(rs.getLong(1));
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        finally {
            try {  pstmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close(); }
            catch (Exception e) { e.printStackTrace(); }
        }
        return new DatabaseObjectIterator(JiveGlobals.THREAD, threads.toArray(),
                factory);
    }

    public synchronized void addPoints(User user, int numPoints)
            throws RewardException
    {
        int totalPoints = user.getRewardPoints();
        totalPoints += numPoints;
        if (totalPoints < 0) {
            throw new RewardException("User cannot have less than 0 points.");
        }

        Connection con = null;
        PreparedStatement pstmt = null;
        boolean abortTransaction = false;
        try {
            con = ConnectionManager.getTransactionConnection();
            // Update reward points in user account.
            user.setRewardPoints(totalPoints);

            // Make entry in reward tracking table.
            pstmt = con.prepareStatement(CREATE_REWARD_ENTRY);
            pstmt.setLong(1, user.getID());
            pstmt.setString(2, StringUtils.dateToMillis(new java.util.Date()));
            pstmt.setInt(3, numPoints);
            pstmt.setNull(4, Types.NUMERIC);
            pstmt.setNull(5, Types.NUMERIC);
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
    }

    /**
     * Removes all references to the message from the reward auditing table.
     */
    protected void deleteMessageReference(ForumMessage message, Connection con)
        throws SQLException
    {
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(DELETE_MESSAGE_ENTRY);
            pstmt.setLong(1,message.getID());
            pstmt.executeUpdate();
        }
        finally {
            try {  pstmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
        }
    }
}