/**
 * $RCSfile: DbForumFactory.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:53 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import com.jivesoftware.util.*;
import com.jivesoftware.forum.*;
import com.jivesoftware.forum.util.*;
import com.jivesoftware.forum.filter.*;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.Constructor;

/**
 * Database implementation of the ForumFactory interface.
 */
public class DbForumFactory extends ForumFactory {

    /** DATABASE QUERIES **/
    private static final String GET_FORUMS = "SELECT forumID FROM jiveForum";
    private static final String FORUM_COUNT = "SELECT count(1) FROM jiveForum";
    private static final String DELETE_FORUM = "DELETE FROM jiveForum WHERE forumID=?";
    private static final String GET_FORUM_ID =
        "SELECT forumID FROM jiveForum WHERE name=?";
    private static final String DELETE_FORUM_PROPERTIES =
        "DELETE FROM jiveForumProp WHERE forumID=?";
    private static final String POPULAR_FORUMS =
        "SELECT forumID, count(1) AS msgCount FROM jiveMessage " +
        "WHERE modifiedDate > ? GROUP BY forumID ORDER BY msgCount DESC";
    private static final String POPULAR_THREADS =
        "SELECT threadID, count(1) AS msgCount FROM jiveMessage " +
        "WHERE modifiedDate > ? GROUP BY threadID ORDER BY msgCount DESC";
    private static final String MOVE_THREADS_TO_FORUM =
        "UPDATE jiveThread SET forumID=? WHERE forumID=?";
    private static final String MOVE_MESSAGES_TO_FORUM =
        "UPDATE jiveMessage SET forumID=? WHERE forumID=?";

    /**
     * The cache manager is the global entry point for all the various object
     * caches in Jive.
     */
    public DatabaseCacheManager cacheManager;

    protected UserManager userManager;
    protected DbGroupManager groupManager;
    private DbSearchManager searchManager;
    private DbFilterManager filterManager;
    protected DbPermissionsManager permissionsManager;
    protected DbWatchManager watchManager;
    protected DbRewardManager rewardManager;
    /**
     * Store GatewayManager objects for each forum. We can't attach the objects
     * to instances of DbForum since those objects go in and out of cache and
     * GatewayManager objects must remain active over time. The only added
     * complexity with storing the managers globally is that we must keep the
     * map up to date as forums are created and deleted.
     */
    protected LongHashMap gatewayManagers;

    /**
     * Keep a cache of user message counts. This greatly helps for skins that
     * show the number of messages a user has posted next to each of their
     * messages. Each userManager implementation is responsible for actually
     * using this cache at its option.
     */
    public LongCache userMessageCountCache = new LongCache(64*1024, 6*JiveGlobals.HOUR);

    // Values for the popular forums/threads feature that can be overidden
    // with Jive property settings. Defaults are specified.
    private int popularForumsNumber = 4;       // 10 forums
    private int popularForumsWindow = 1;        // 1 day
    private int popularThreadsNumber = 4;      // 10 threads
    private int popularThreadsWindow = 1;       // 1 day

    protected int forumCount = -1;
    protected long [] forums = null;
    protected long [] popularForums = null;
    protected long [] popularThreads = null;

    /**
     * Creates a new DbForumFactory.
     */
    public DbForumFactory() {
        cacheManager = new DatabaseCacheManager(this);
        groupManager = new DbGroupManager(this);
        searchManager = new DbSearchManager();
        filterManager = new DbFilterManager(-1, this);
        permissionsManager = new DbPermissionsManager(-1, this);
        watchManager = new DbWatchManager(this);
        rewardManager = new DbRewardManager(this);

        // Load up a UserManager implementation. If the correct Jive property
        // is set, it will be a custom UserManager class.
        String uManagerProp = JiveGlobals.getJiveProperty("UserManager.className");
        String className = "com.jivesoftware.forum.database.DbUserManager";
        if (uManagerProp != null) {
            className = uManagerProp;
        }
        try {
            Class c = Class.forName(className);
            // Attempt to instantiate the UserManager implementation with a
            // DbForumFactory as a paramater.
            Class [] params = new Class [] { this.getClass() };
            Constructor constructor = c.getConstructor(params);
            // Intantiate the gateway object. We assume that
            userManager = (UserManager)constructor.newInstance(
                    new Object[] { this });
        }
        catch (Exception e) {
            System.err.println("Exception creating UserManager!");
            e.printStackTrace();
        }

        // Load up gateway managers.
//        gatewayManagers = new LongHashMap();
        //for (Iterator iter = forums(); iter.hasNext(); ) {
            //Forum forum = (Forum)iter.next();
            //GatewayManager gManager = new GatewayManager(this, forum);
            //gatewayManagers.put(forum.getID(), gManager);
        //}

        // Load custom popular threads/forums values if they exist.
        try {
            String number = JiveGlobals.getJiveProperty("popularForums.number");
            if (number != null) {
                popularForumsNumber = Integer.parseInt(number);
            }
        } catch (Exception e) { }
        try {
            String window = JiveGlobals.getJiveProperty("popularForums.timeWindow");
            if (window != null) {
                popularForumsWindow = Integer.parseInt(window);
            }
        } catch (Exception e) { }
        try {
            String number = JiveGlobals.getJiveProperty("popularThreads.number");
            if (number != null) {
                popularThreadsNumber = Integer.parseInt(number);
            }
        } catch (Exception e) { }
        try {
            String window = JiveGlobals.getJiveProperty("popularThreads.timeWindow");
            if (window != null) {
                popularThreadsWindow = Integer.parseInt(window);
            }
        } catch (Exception e) { }
    }

    //FROM THE FORUMFACTORY INTERFACE//

    public Forum createForum(String name, String description)
            throws UnauthorizedException, ForumAlreadyExistsException
    {
        Forum newForum = null;
        try {
            Forum existingForum = getForum(name);

            // The forum already exists since now exception, so:
            throw new ForumAlreadyExistsException();
        }
        catch (ForumNotFoundException fnfe) {
            // The forum doesn't already exist so we can create a new one
            newForum = new DbForum(name, description, this);
        }
        // Create a new GatewayManager for the forum.
//        gatewayManagers.put(newForum.getID(), new GatewayManager(this, newForum));

        // Now, delete the forum list since we created a new one.
        this.forums = null;
        this.forumCount = -1;
        return newForum;
    }

    public ForumThread createThread(ForumMessage rootMessage)
        throws UnauthorizedException
    {
        return new DbForumThread(rootMessage, this);
    }

    public ForumMessage createMessage() {
        return new DbForumMessage(null, this);
    }

    public ForumMessage createMessage(User user) throws UnauthorizedException {
        return new DbForumMessage(user, this);
    }

    public void deleteForum(Forum forum) throws UnauthorizedException {
        // Delete all messages and threads in the forum. Disable the search
        // component if it's on so that speed is reasonable. This means that
        // the index should be rebuilt after deleting a forum, but this
        // probably isn't unresonable.
        boolean searchEnabled = searchManager.isSearchEnabled();
        searchManager.setSearchEnabled(false);
        ResultFilter ignoreModerationFilter = ResultFilter.createDefaultThreadFilter();
        ignoreModerationFilter.setModerationRangeMin(Integer.MIN_VALUE);
        Iterator threads = forum.threads(ignoreModerationFilter);
        while (threads.hasNext()) {
            ForumThread thread = (ForumThread)threads.next();
            forum.deleteThread(thread);
        }
        // Restore searching to its previous state.
        searchManager.setSearchEnabled(searchEnabled);

        // Now, delete all filters associated with the forum. We delete in
        // reverse order since filter indexes will change if we don't delete
        // the last filter entry.
        FilterManager filterManager = forum.getFilterManager();
        int filterCount = filterManager.getFilterCount();
        for (int i=filterCount-1; i>=0; i--) {
            filterManager.removeFilter(i);
        }

        // Delete all gateways.
//        forum.getGatewayManager().deleteContext();
        //gatewayManagers.removeKey(forum.getID());

        // Delete all permissions
        PermissionsManager permManager = forum.getPermissionsManager();
        permManager.removeAllUserPermissions();
        permManager.removeAllGroupPermissions();

        // Finally, delete the forum itself and all its properties.
        boolean abortTransaction = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getTransactionConnection();
            // Properties
            pstmt = con.prepareStatement(DELETE_FORUM_PROPERTIES);
            pstmt.setLong(1,forum.getID());
            pstmt.execute();
            pstmt.close();

            pstmt = con.prepareStatement(DELETE_FORUM);
            pstmt.setLong(1,forum.getID());
            pstmt.execute();
        }
        catch( Exception sqle ) {
            sqle.printStackTrace();
            abortTransaction = true;
        }
        finally {
            try {  pstmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            ConnectionManager.closeTransactionConnection(con, abortTransaction);
        }

        // Remove from cache
        cacheManager.forumCache.remove(forum.getID());

        // Finally, delete the list of forums
        this.forumCount = -1;
        this.forums = null;
    }

    public void mergeForums(Forum forum1, Forum forum2)
            throws UnauthorizedException
    {
        // First, remove all permissions to forum2 so that people no longer
        // post in it or see it.
        PermissionsManager permManager = forum2.getPermissionsManager();
        permManager.removeAllUserPermissions();
        permManager.removeAllGroupPermissions();

        // Read all messageIDs of the thread into an array so that we can expire
        // each of them later. This may be quite large for huge forums, but there
        // isn't really a better way around this.
        ResultFilter ignoreModerationFilter = ResultFilter.createDefaultMessageFilter();
        ignoreModerationFilter.setModerationRangeMin(Integer.MIN_VALUE);
        LongList messageIDList = new LongList();
        Iterator iter = forum2.messages(ignoreModerationFilter);
        while (iter.hasNext()) {
            long messageID = ((ForumMessage)iter.next()).getID();
            messageIDList.add(messageID);
        }

        // Perform db operations.
        boolean abortTransaction = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getTransactionConnection();
            // Move all threads to new forum
            pstmt = con.prepareStatement(MOVE_THREADS_TO_FORUM);
            pstmt.setLong(1,forum1.getID());
            pstmt.setLong(2,forum2.getID());
            pstmt.executeUpdate();
            pstmt.close();

            // Move all messages to new forum.
            pstmt = con.prepareStatement(MOVE_MESSAGES_TO_FORUM);
            pstmt.setLong(1,forum1.getID());
            pstmt.setLong(2,forum2.getID());
            pstmt.executeUpdate();
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
            abortTransaction = true;
            return;
        }
        finally {
            try {  pstmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            ConnectionManager.closeTransactionConnection(con, abortTransaction);
        }

        // Delete forum2
        deleteForum(forum2);

        // Erase both forums from cache.
        cacheManager.forumCache.remove(forum1.getID());
        cacheManager.forumCache.remove(forum2.getID());

        // Clear out entire message and thread caches. This is drastic, but is
        // pretty necessary to make everything work correctly. Merging forums
        // shouldn't be something that is done all the time, so we should be ok.
        cacheManager.messageCache.clear();
        cacheManager.threadCache.clear();

        // Update the last modified date of forum1 to the most recently
        // updated thread (this may have changed during forum merge).
        ResultFilter newestThreadFilter = ResultFilter.createDefaultThreadFilter();
        newestThreadFilter.setNumResults(1);
        Iterator threadIter = forum1.threads(newestThreadFilter);
        if (threadIter.hasNext()) {
            ForumThread newestThread = (ForumThread)threadIter.next();
            if (newestThread != null) {
                forum1.setModifiedDate(newestThread.getModifiedDate());
            }
        }

        // Loop through all messages from forum2 and delete from cache, then
        // reset entry in the search index.
        long [] messageIDArray = messageIDList.toArray();
        int i;
        for (i=0; i<messageIDArray.length; i++) {
            long messageID = messageIDArray[i];
            try {
                ForumMessage message = cacheManager.messageCache.get(messageID);
                searchManager.removeFromIndex(message);
                searchManager.addToIndex(message);
            }
            catch (Exception e)  {
                System.err.println("Failed to re-index message " + messageIDArray[i]);
                e.printStackTrace();
            }
        }
    }

    public Forum getForum(long forumID) throws ForumNotFoundException {
        return cacheManager.forumCache.get(forumID);
    }

    public Forum getForum(String name) throws ForumNotFoundException {
        return cacheManager.forumCache.get(name);
    }

    public int getForumCount() {
        if (forumCount != -1) {
            return forumCount;
        }
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(FORUM_COUNT);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            forumCount = rs.getInt(1);
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
        return forumCount;
    }

    public Iterator forums() {
        if (forums == null) {
            LongList forumList = new LongList();
            Connection con = null;
            PreparedStatement pstmt = null;
            try {
                con = ConnectionManager.getConnection();
                pstmt = con.prepareStatement(GET_FORUMS);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    forumList.add(rs.getLong(1));
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
            this.forums = forumList.toArray();
        }
        return new DatabaseObjectIterator(JiveGlobals.FORUM, forums, this);
    }

    public Query createQuery() {
        return null;
        // Note, this a fake method only implemented to meet the requirements
        // of extending the abstract ForumFactory class. It's not possible to
        // directly implement this method since we need authorization
        // information to determine which forums a user has READ access for.
        // The ForumFactoryProxy instead automatically calls the
        // createQuery(Forum []) method with the proper set of forums.
    }

    public Query createQuery(Forum [] forums) {
        return new DbQuery(forums, this);
    }

    public Iterator popularForums() {
        if (popularForums == null) {
            LongList popForums = new LongList(popularForumsNumber);

            Calendar cal = Calendar.getInstance();
            cal.roll(Calendar.DAY_OF_YEAR, -popularForumsWindow);

            Connection con = null;
            PreparedStatement pstmt = null;
            try {
                con = ConnectionManager.getConnection();
                pstmt = con.prepareStatement(POPULAR_FORUMS);
                pstmt.setString(1, StringUtils.dateToMillis(cal.getTime()));
                ResultSet rs = pstmt.executeQuery();
                for (int i=0; i < popularForumsNumber; i++) {
                    if (!rs.next()) {
                        break;
                    }
                    popForums.add(rs.getLong(1));
                }
                this.popularForums = popForums.toArray();
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
        return new DatabaseObjectIterator(JiveGlobals.FORUM, popularForums, this);
    }

    public Iterator popularThreads() {
        if (popularThreads == null) {
            LongList threadIDs = new LongList(popularThreadsNumber);

            Calendar cal = Calendar.getInstance();
            cal.roll(Calendar.DAY_OF_YEAR, -popularThreadsWindow);

            Connection con = null;
            PreparedStatement pstmt = null;
            try {
                con = ConnectionManager.getConnection();
                pstmt = con.prepareStatement(POPULAR_THREADS);
                pstmt.setString(1, StringUtils.dateToMillis(cal.getTime()));
                ResultSet rs = pstmt.executeQuery();
                for (int i=0; i < popularThreadsNumber; i++) {
                    if (!rs.next()) {
                        break;
                    }
                    threadIDs.add(rs.getLong(1));
                }
                popularThreads = threadIDs.toArray();
            }
            catch( Exception e ) {
                e.printStackTrace();
            }
            finally {
                try {  pstmt.close(); }
                catch (Exception e) { e.printStackTrace(); }
                try {  con.close();   }
                catch (Exception e) { e.printStackTrace(); }
            }
        }
        return new DatabaseObjectIterator(JiveGlobals.THREAD, popularThreads, this);
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }

    public SearchManager getSearchManager() {
        return searchManager;
    }

    public FilterManager getFilterManager() {
        return filterManager;
    }

    public WatchManager getWatchManager() {
        return watchManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public PermissionsManager getPermissionsManager() {
        return permissionsManager;
    }

    public ForumMessageFilter [] getAvailableFilters()
    {
        return DbFilterManager.getAvailableFilters();
    }

    public void addFilterClass(String className) throws ClassNotFoundException,
            IllegalArgumentException
    {
        DbFilterManager.addFilterClass(className);
    }

    public ForumPermissions getPermissions(Authorization authorization) {
        long userID = authorization.getUserID();

        return permissionsManager.getFinalUserPerms(-1, userID);
    }

    public boolean hasPermission(int type) {
        return true;
    }

    //OTHER METHODS//

    /**
     * Returns the cache manager object.
     */
    public DatabaseCacheManager getCacheManager() {
        return cacheManager;
    }

    /**
     * Returns the id of the forum with the specified name.
     *
     * @param name the name of the forum to lookup.
     */
    protected static long getForumID(String name) throws ForumNotFoundException {
        Connection con = null;
        PreparedStatement pstmt = null;
        long forumID = -1;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(GET_FORUM_ID);
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                throw new ForumNotFoundException("Forum with name " + name +
                        "does not exist.");
            }
            forumID = rs.getLong(1);
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
        return forumID;
    }

    /**
     * Returns a thread specified by its id. An exception will be thrown if the
     * thread could not be loaded or if the thread is not in the specified
     * forum. If cache is turned on, it will be used.
     *
     * @param threadID the thread to load.
     * @param forum the forum that the thread belongs to.
     * @throws ForumThreadNotFoundException if the specified thread could not
     *      be loaded.
     */
    protected DbForumThread getThread(long threadID, DbForum forum) throws
            ForumThreadNotFoundException
    {
        DbForumThread thread = cacheManager.threadCache.get(threadID);
        // Make sure the thread is part of the forum.
        if (!(thread.forumID ==  forum.getID())) {
            throw new ForumThreadNotFoundException();
        }
        return thread;
    }

    /**
     * Returns a message based on its id. An exception will be thrown if the
     * message could not be loaded or if the message is not in the specified
     * thread. If cache is turned on, it will be used. All filters will be
     * applied to the message before it is returned.
     *
     * @param messageID the ID of the message to get.
     * @param threadID the ID of the thread the message belongs to.
     * @param forumID the ID of the forum that the message belong to.
     * @return the message specified by <tt>messageID</tt> with all filters
     *      applied.
     */
    protected ForumMessage getMessage(long messageID, long threadID, long forumID)
            throws ForumMessageNotFoundException
    {
        DbForumMessage message = cacheManager.messageCache.get(messageID);
        // Do a security check to make sure the message comes from the thread.
        if (message.threadID != threadID) {
            throw new ForumMessageNotFoundException();
        }
        ForumMessage filterMessage = null;
        // See if the filter values are not already cached.
        if (message.filteredSubject == null) {
            // Apply global filters
            filterMessage = filterManager.applyCacheableFilters(message);
            // Apply forum specific filters if there were no uncacheable filters
            // at the global level.
            if (!filterManager.hasUncacheableFilters()) {
                try {
                    FilterManager fManager = getForum(forumID).getFilterManager();
                    filterMessage =  fManager.applyCacheableFilters(filterMessage);
                }
                catch (Exception e) { }
            }
            // Now, cache those values.
            message.filteredSubject = filterMessage.getSubject();
            message.filteredBody = filterMessage.getBody();
            Hashtable filteredProperties = new Hashtable();
            for (Iterator i=filterMessage.propertyNames(); i.hasNext(); ) {
                String name = (String)i.next();
                filteredProperties.put(name, filterMessage.getProperty(name));
            }
            message.filteredProperties = filteredProperties;
        }
        // Apply uncacheable filters.
        if (filterManager.hasUncacheableFilters()) {
            // Apply global uncachable filters and then all filters for the forum.
            filterMessage = filterManager.applyUncacheableFilters(message);
            try {
                Forum forum = getForum(forumID);
                filterMessage = forum.getFilterManager().applyFilters(filterMessage);
            }
            catch (Exception e) { }
        }
        else {
            // Apply any forum specific uncacheable filters.
            try {
                Forum forum = getForum(forumID);
                filterMessage = forum.getFilterManager().applyUncacheableFilters(message);
            }
            catch (Exception e) { }
        }
        return filterMessage;
    }
}