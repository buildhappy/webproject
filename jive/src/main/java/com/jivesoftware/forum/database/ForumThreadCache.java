/**
 * $RCSfile: ForumThreadCache.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:01 $
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
 * A cache for ForumThread objects.
 */
public class ForumThreadCache extends DatabaseCache {

    public ForumThreadCache(LongCache cache, DbForumFactory forumFactory) {
        super(cache, forumFactory);
    }

    public DbForumThread get(long threadID)
            throws ForumThreadNotFoundException
    {
        if (!isEnabled()) {
            return new DbForumThread(threadID, factory);
        }
        //Cache is enabled.
        DbForumThread thread = (DbForumThread)cache.get(threadID);
        if (thread == null) {
            thread = new DbForumThread(threadID, factory);
            cache.add(threadID, thread);
        }
        return thread;
    }

    public void remove(long key) {
        DbForumThread thread = (DbForumThread)cache.get(key);
        // Instead of removing the object from cache, simply "reset" it. This
        // makes sure that if references to the object are still being held,
        // they'll remain valid with the most up-to-date data.
        if (thread != null) {
            thread.reset();
        }
    }
}