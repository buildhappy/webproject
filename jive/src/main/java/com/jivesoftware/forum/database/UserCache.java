/**
 * $RCSfile: UserCache.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:02 $
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
 * User cache implementation. This cache should not be used to try load user
 * objects directly, as the <tt>get(long userID)</tt> method will simply return
 * <tt>null</tt> if the user object is not found in cache (this is different
 * behavior than the other database caches). Instead, a UserManager instance
 * should always be used to load user objects.<p>
 *
 * This class also maintains a userIDCache that UserManager implementations
 * may wish to exploit. Note that all User objects in this cache must implement
 * the Cacheable interface.
 */
public class UserCache extends DatabaseCache {

    /**
     * Keep a cache that maps usernames to userID's. UserManager implementations
     * should refer to this cache directly as needed.
     */
    public Cache userIDCache = new Cache(128*1024, 6*JiveGlobals.HOUR);

    public UserCache(LongCache cache, DbForumFactory factory) {
        super(cache, factory);
    }

    public void add(long userID, User user) {
        cache.add(userID, (Cacheable)user);
        // Add a userName to userID mapping to the ID cache.
        userIDCache.add(user.getUsername(), new CacheableLong(userID));
    }

    public User get(long userID) {
        return (User)cache.get(userID);
    }

    public void remove(long key) {
        try {
            User user = get(key);
            String username = user.getUsername();
            userIDCache.remove(username);
        }
        catch (Exception e) { }
        super.remove(key);
    }
}