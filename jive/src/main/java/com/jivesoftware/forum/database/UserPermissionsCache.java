/**
 * $RCSfile: UserPermissionsCache.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:03 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import com.jivesoftware.util.*;
import com.jivesoftware.forum.*;

public class UserPermissionsCache extends DatabaseCache {

    public UserPermissionsCache(UserPermsCache cache, DbForumFactory forumFactory) {
        super(cache, forumFactory);
    }

    public void add(long forumID, long userID, ForumPermissions perms) {
        if (!isEnabled()) {
            return;
        }
        LongCache userPermsCache = (LongCache)cache.get(forumID);
        userPermsCache.add(userID, perms);
    }

    public ForumPermissions get(long forumID, long userID) {
        if (!isEnabled()) {
            return null;
        }
        //Get the user perm cache for this forum
        LongCache userPermsCache = (LongCache)cache.get(forumID);

        ForumPermissions permissions = (ForumPermissions)userPermsCache.get(userID);
        if (permissions != null) {
            return permissions;
        }
        else {
            return null;
        }
    }

    public LongCache get(long forumID) {
        if (!isEnabled()) {
            return null;
        }
        return (LongCache)cache.get(forumID);
    }

    public void removeUserPerm(long userID) {
        Object [] values = cache.values();
        for (int i=0; i<values.length; i++) {
            LongCache cache = (LongCache)values[i];
            cache.remove(userID);
        }
        //when cache becomes distributed, we'd send out an expire message
        //here to all other jive servers.
    }

    public void removeUserPerm(long userID, long forumID) {
        LongCache userPermsCache = (LongCache)this.cache.get(forumID);
        userPermsCache.remove(userID);

        //when cache becomes distributed, we'd send out an expire message
        //here to all other jive servers.
    }

    public void clear() {
        //Clear all user caches and then this cache.
        Object [] values = cache.values();
        for (int i=0; i<values.length; i++) {
            ((LongCache)values[i]).clear();
        }
        //Now clear global cache.
        cache.clear();
    }
}


/**
 * Special purpose Cache to hold all of the different user permission cache
 * objects. The main feature is that new caches are automatically created so
 * that calling get() never returns null.
 */
class UserPermsCache extends LongCache {

    public UserPermsCache(int size, long expireTime) {
        super(size, expireTime);
    }

    public synchronized Cacheable get(long key) {
        LongCache subCache = (LongCache)super.get(key);
        if (subCache == null) {
            //cache has expired, or is not there, so put a new one in there.
            //Cache objects only need to last as long as a user's session
            //does. Half an hour is a reasonable amount of time for this.
            subCache = new LongCache(2*1024, 30*1000*60);
            add(key, subCache);
        }
        return subCache;
    }

    public synchronized void remove(long key) {
        LongCacheObject cacheObject = (LongCacheObject)cachedObjectsHash.get(key);
        //If the object is not in cache, stop trying to remove it.
        if (cacheObject == null) {
            return;
        }
        //remove from the hash map
        cachedObjectsHash.removeKey(key);
        //remove from the cache order list
        cacheObject.lastAccessedListNode.remove();
        cacheObject.ageListNode.remove();
        //remove references to linked list nodes
        cacheObject.ageListNode = null;
        cacheObject.lastAccessedListNode = null;
        //removed the object, so subtract its size from the total.
        size -= cacheObject.size;

        //Finally, clear the sub-cache to make sure memory is released.
        ((LongCache)cacheObject.object).clear();
    }

    /**
     * Returns the current size in bytes of the cache. The base getSize() method
     * does not work correctly because the sub-caches are empty when we first
     * add them rather than the normal cache assumption that objects are near
     * the size that they will always be.
     *
     * @return the size of the cache in bytes.
     */
    public int getSize() {
        int size = 0;
        Object [] values = values();
        for (int i=0; i<values.length; i++) {
            size += ((LongCache)values[i]).getSize();
        }
        return size;
    }
}