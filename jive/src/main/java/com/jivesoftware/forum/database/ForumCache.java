/**
 * $RCSfile: ForumCache.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:00 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import com.jivesoftware.util.*;
import com.jivesoftware.forum.util.*;
import com.jivesoftware.forum.*;

/**
 * Cache for DbForum objects.
 */
public class ForumCache extends DatabaseCache {

    protected Cache forumIDCache = new Cache(128*1024, 6*JiveGlobals.HOUR);

    public ForumCache(LongCache cache, DbForumFactory forumFactory) {
        super(cache, forumFactory);
    }

    public DbForum get(long forumID) throws ForumNotFoundException {
        if (!isEnabled()) {
            return new DbForum(forumID, factory);
        }
        // Cache is enabled.
        DbForum forum = (DbForum)cache.get(forumID);
        if (forum == null) {
            forum = new DbForum(forumID, factory);
            cache.add(forumID, forum);
            // -- CACHE WARMUP DISABLED. SEEMS TO DO MORE HARM THAN GOOD. -- //
            // Finally, schedule a warmup of the cache for the forum.
            //TaskEngine.addTask(new ForumCacheWarmupTask(forum),
            //    Thread.NORM_PRIORITY);
        }
        return forum;
    }

    public Forum get(String name) throws ForumNotFoundException {
        // If cache is not enabled, do a new lookup of object
        if (!isEnabled()) {
            long forumID = factory.getForumID(name);
            return new DbForum(forumID, factory);
        }
        // Cache is enabled.
        CacheableLong forumIDLong = (CacheableLong)forumIDCache.get(name);

        // If id wan't found in cache, load it up and put it there.
        if (forumIDLong == null) {
            long forumID = factory.getForumID(name);
            forumIDLong = new CacheableLong(forumID);
            forumIDCache.add(name, forumIDLong);
        }
        return get(forumIDLong.getLong());
    }

    public void remove(long key) {
        DbForum forum = (DbForum)cache.get(key);
        if (forum == null) {
            return;
        }

        // Find the name of the forum and remove it from the cache.
        try {
            String name = forum.getName();
            forumIDCache.remove(name);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        forum.reset();

        // Now, expire relevant objects out of the factory.
        factory.popularForums = null;
        factory.popularThreads = null;
    }
}