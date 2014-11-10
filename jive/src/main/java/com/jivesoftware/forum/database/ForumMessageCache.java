/**
 * $RCSfile: ForumMessageCache.java,v $
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

public class ForumMessageCache extends DatabaseCache {

    public ForumMessageCache(LongCache cache, DbForumFactory forumFactory) {
        super(cache, forumFactory);
    }

    public DbForumMessage get(long messageID) throws ForumMessageNotFoundException {
        if (!isEnabled()) {
            return new DbForumMessage(messageID, factory);
        }
        //Cache is enabled.
        DbForumMessage message = (DbForumMessage)cache.get(messageID);
        if (message == null) {
            message = new DbForumMessage(messageID, factory);
            cache.add(messageID, message);
        }
        return message;
    }

    public void remove(long key) {
        DbForumMessage message = (DbForumMessage)cache.get(key);
        if (message != null) {
            try {
                // Expire appropriate thread and forum result filter caches
                DbForumThread thread = factory.cacheManager.threadCache.get(
                        message.threadID);
                thread.messageCountCache.clear();
                thread.messageListCache.clear();
                DbForum forum = factory.cacheManager.forumCache.get(
                        message.forumID);
                forum.messageCountCache.clear();
                forum.messageListCache.clear();
            }
            catch (Exception e) { /* ignore */ }
        }
        // Expire actual message.
        super.remove(key);
    }
}