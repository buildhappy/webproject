/**
 * $RCSfile: DatabaseCache.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:48 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import com.jivesoftware.util.LongCache;

/**
 * A base class that defines the basic functionality needed to wrap the general
 * purpose CoolServlets cache classes to caches for specific Jive database
 * objects.
 *
 * @see LongCache
 */
public class DatabaseCache {

    protected LongCache cache;
    protected DbForumFactory factory;

    /**
     * Creates a new database cache object.
     *
     * @param cache a cache object to wrap.
     * @param factory a DbForumFactory to be used to perform Jive operations.
     */
    public DatabaseCache(LongCache cache, DbForumFactory factory) {
        this.cache = cache;
        this.factory = factory;
    }

    /**
     * Pass-thru method for LongCache.remove(long).
     *
     * @see LongCache#remove(long)
     */
    public void remove(long key) {
        cache.remove(key);
    }

    /**
     * Pass-thru method for LongCache.getCacheHits().
     *
     * @see LongCache#getCacheHits()
     */
    public long getCacheHits() {
        return cache.getCacheHits();
    }

    /**
     * Pass-thru for LongCache.getCacheMisses().
     *
     * @see LongCache#getCacheMisses()
     */
    public long getCacheMisses() {
        return cache.getCacheMisses();
    }

    public int getSize() {
        return cache.getSize();
    }

    public void setMaxSize(int maxSize) {
        cache.setMaxSize(maxSize);
    }

    public int getMaxSize() {
        return cache.getMaxSize();
    }

    public int getNumElements() {
        return cache.getNumElements();
    }

    public void clear() {
        cache.clear();
    }

    public boolean isEnabled() {
        return factory.cacheManager.isCacheEnabled();
    }
}