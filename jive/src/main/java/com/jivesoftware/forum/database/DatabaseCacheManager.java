/**
 * $RCSfile: DatabaseCacheManager.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:49 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import com.jivesoftware.util.*;
import com.jivesoftware.forum.*;

/**
 * Central cache management of all caches used by Jive. Cache sizes are stored
 * as the following Jive following Jive property values: <ul>
 *
 *      <li> <tt>cache.userCache.size</tt>
 *      <li> <tt>cache.groupCache.size</tt>
 *      <li> <tt>cache.forumCache.size</tt>
 *      <li> <tt>cache.threadCache.size</tt>
 *      <li> <tt>cache.messageCache.size</tt> </ul>
 *
 * All values should be in bytes. Cache can also be globally enabled or disabled.
 * This value is stored as the <tt>cache.enabled</tt> Jive property.
 */
public class DatabaseCacheManager {

    public static final int USER_CACHE = 0;
    public static final int GROUP_CACHE = 1;
    public static final int FORUM_CACHE = 2;
    public static final int THREAD_CACHE = 3;
    public static final int MESSAGE_CACHE = 4;
    public static final int USER_PERMS_CACHE = 5;

    public UserCache userCache;
    public GroupCache groupCache;
    public ForumCache forumCache;
    public ForumThreadCache threadCache;
    public ForumMessageCache messageCache;
    public UserPermissionsCache userPermsCache;

    private boolean cacheEnabled = true;

    private DbForumFactory factory;

    /**
     * Creates a new cache manager.
     */
    public DatabaseCacheManager(DbForumFactory factory) {
        this.factory = factory;

        // See if cache is supposed to be enabled.
        String enabled = JiveGlobals.getJiveProperty("cache.enabled");
        if (enabled != null) {
            try { cacheEnabled = Boolean.valueOf(enabled).booleanValue(); }
            catch (Exception e) { }
        }

        // Default cache sizes (small)
        int forumCacheSize = 512*1024;        // 1/2 MB
        int threadCacheSize = 512*1024;       // 1/2 MB
        int messageCacheSize = 1152*1024;     // 5/4 MB
        int userCacheSize = 512*1024;         // 1/2 MB
        int userPermCacheSize = 256*1024;     // 1/4 MB
        int groupCacheSize = 128*1024;        // 1/8 MB

        //Now, see if properties were set.
        String fCacheSize = JiveGlobals.getJiveProperty("cache.forumCache.size");
        if (fCacheSize != null) {
            try {  forumCacheSize = Integer.parseInt(fCacheSize);  }
            catch (Exception e) { }
        }
        String tCacheSize = JiveGlobals.getJiveProperty("cache.threadCache.size");
        if (tCacheSize != null) {
            try {  threadCacheSize = Integer.parseInt(tCacheSize);  }
            catch (Exception e) { }
        }
        String mCacheSize = JiveGlobals.getJiveProperty("cache.messageCache.size");
        if (mCacheSize != null) {
            try {  messageCacheSize = Integer.parseInt(mCacheSize);  }
            catch (Exception e) { }
        }
        String uCacheSize = JiveGlobals.getJiveProperty("cache.userCache.size");
        if (uCacheSize != null) {
            try {  userCacheSize = Integer.parseInt(uCacheSize);  }
            catch (Exception e) { }
        }
        String upCacheSize = JiveGlobals.getJiveProperty("cache.userPermCache.size");
        if (upCacheSize != null) {
            try {  userPermCacheSize = Integer.parseInt(upCacheSize);  }
            catch (Exception e) { }
        }
        String gCacheSize = JiveGlobals.getJiveProperty("cache.groupCache.size");
        if (gCacheSize != null) {
            try {  groupCacheSize = Integer.parseInt(gCacheSize);  }
            catch (Exception e) { }
        }

        int MINUTE = 1000*60;
        int HOUR = MINUTE*60;

        //Initialize all cache structures
        forumCache = new ForumCache(new LongCache(forumCacheSize, 6*HOUR), factory);
        threadCache = new ForumThreadCache(new LongCache(threadCacheSize, 6*HOUR), factory);
        messageCache = new ForumMessageCache(new LongCache(messageCacheSize, 6*HOUR), factory);
        userCache = new UserCache(new LongCache(userCacheSize, 6*HOUR), factory);
        groupCache = new GroupCache(new LongCache(groupCacheSize, 6*HOUR), factory);
        //The user permissions cache is a special one. It's actually a Cache
        //of Cache objects. Each of the cache objects in the main cache
        //corresponds to a particular forum, and is used to cache the
        //permissions that a user has for a forum. In order to handle this
        //requirement, we use a special subclass of Cache.
        userPermsCache = new UserPermissionsCache(
                new UserPermsCache(userPermCacheSize, 24*HOUR), factory
        );
    }

    public void clear(int cacheType) {
        getCache(cacheType).clear();
    }

    public long getHits(int cacheType) {
        return getCache(cacheType).getCacheHits();
    }

    public long getMisses(int cacheType) {
        return getCache(cacheType).getCacheMisses();
    }

    public int getSize(int cacheType) {
         return getCache(cacheType).getSize();
    }

    public int getMaxSize(int cacheType) {
        return getCache(cacheType).getMaxSize();
    }

    public void setMaxSize(int cacheType, int size) {
        getCache(cacheType).setMaxSize(size);
        // Save the size of the cache as a jive property
        switch (cacheType) {
            case FORUM_CACHE:
                JiveGlobals.setJiveProperty("cache.forumCache.size",String.valueOf(size));
                break;
            case THREAD_CACHE:
                JiveGlobals.setJiveProperty("cache.threadCache.size",String.valueOf(size));
                break;
            case MESSAGE_CACHE:
                JiveGlobals.setJiveProperty("cache.messageCache.size",String.valueOf(size));
                break;
            case USER_CACHE:
                JiveGlobals.setJiveProperty("cache.userCache.size",String.valueOf(size));
                break;
            case USER_PERMS_CACHE:
                JiveGlobals.setJiveProperty("cache.userPermsCache.size",String.valueOf(size));
                break;
            case GROUP_CACHE:
                JiveGlobals.setJiveProperty("cache.groupCache.size",String.valueOf(size));
                break;
            default:
                throw new IllegalArgumentException("Invalid cache type: " + cacheType);
        }
    }

    public int getNumElements(int cacheType) {
        return getCache(cacheType).getNumElements();
    }

    private DatabaseCache getCache(int cacheType) {
        switch (cacheType) {
            case FORUM_CACHE:
                return forumCache;
            case THREAD_CACHE:
                return threadCache;
            case MESSAGE_CACHE:
                return messageCache;
            case USER_CACHE:
                return userCache;
            case USER_PERMS_CACHE:
                return userPermsCache;
            case GROUP_CACHE:
                return groupCache;
            default:
                throw new IllegalArgumentException("Invalid cache type: " + cacheType);
        }
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        // If we're setting cacheEnabled to false, clear all caches
        if (cacheEnabled == false) {
            // Iterate through each of the five caches.
            for (int i=USER_CACHE; i<USER_PERMS_CACHE; i++) {
                clear(i);
            }
        }
        this.cacheEnabled = cacheEnabled;
        JiveGlobals.setJiveProperty("cache.enabled", String.valueOf(cacheEnabled));
    }
}