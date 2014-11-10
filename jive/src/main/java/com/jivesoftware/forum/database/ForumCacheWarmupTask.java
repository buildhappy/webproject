/**
 * $RCSfile: ForumCacheWarmupTask.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:00 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import com.jivesoftware.forum.util.*;
import com.jivesoftware.forum.*;
import java.util.*;

/**
 * A task that warms up the cache of a forum by loading up the first block
 * of thread ID's, and most of the threads in that block.
 */
public class ForumCacheWarmupTask implements Runnable {

    private static ResultFilter filter;
    static {
        filter = ResultFilter.createDefaultThreadFilter();
        filter.setNumResults(DbForum.THREAD_BLOCK_SIZE);
    }

    private DbForum forum;

    /**
     * Create a new cache warmup task. The forum that should have its cache
     * warmed up must be specified.
     *
     * @param forum the forum to do cache warmup on.
     */
    public ForumCacheWarmupTask(DbForum forum) {
        super();
        this.forum = forum;
    }

    public void run() {
        String query = forum.getThreadListSQL(filter, false);
        long [] threadList = forum.getThreadBlock(query, 0);

        //It's possible that the forum is empty. If that's the case, abort.
        if (threadList.length == 0) {
            return;
        }

        /**
         * Start 1/4th of the way through the list, loading backwards, then
         * load the rest. This behavior is specifically designed to speed
         * loading the initial page a forum right after a forum object
         * has been expired. In that case, the skin's thread iterator will work
         * forwards loading data into cache, while this task will work
         * backwards (assuming this cache loading task is done in a timely
         * manner). At best, this technique will speed cache loading. At worst,
         * it doesn't help or hurt matters.
         */
        int index = threadList.length/4;

        //Loop from start index to beginning.
        int i = 0;
        for (i=index; i>=0; i--) {
            try {
                ForumThread thread = forum.getThread(threadList[i]);
                ForumMessage message = thread.getRootMessage();
                //Get the user associated with the message.
                message.getUser();
            }
            catch (ForumThreadNotFoundException e) { }
        }

        //Now, the rest of the list. We'll skip every other element to
        //increase the speed of this process. That will mean fewer objects are
        //in cache, but will still double the speed of page loads that only
        //have half the content cached.
        for (i=index+1; i<threadList.length; i+=2) {
            try {
                ForumThread thread = forum.getThread(threadList[i]);
                ForumMessage message = thread.getRootMessage();
                //Get the user associated with the message.
                message.getUser();
            }
            catch (ForumThreadNotFoundException e) { }
        }
    }
}