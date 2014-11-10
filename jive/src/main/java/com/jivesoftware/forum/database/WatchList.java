/**
 * $RCSfile: WatchList.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:03 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import java.util.*;
import com.jivesoftware.forum.*;
import com.jivesoftware.util.*;

/**
 * Stores the list of all watched threads for a user. The class also provides
 * a method to determine if a particular thread is being watched. Binary
 * searching is used to make this as efficient as possible.
 */
public class WatchList implements Cacheable {

    private long [] threadsByDate;
   // private long [] forumsByDate;
    private boolean [] expirableByDate;
    private long [] sThreadsByDate;

    private long [] threadsByForum;
    private long [] forumsByForum;

    private DbForumFactory factory;

    /**
     * Creates a new watch list.
     *
     * @param threadsByDate the thread ID's of all watches sorted in order that
     *      the threads were last modified.
     * @param expirableByDate the boolean values indicating if each watch is
     *      expirable corresponding to <tt>threadsByDate</tt>.
     * @param forumsByDate the forum ID's corresponding to <tt>threadsByDate</tt>.
     * @param threadsByForum the thread ID's of all watches sorted in order that
     *      the threads were last modified, but also grouped by forumID.
     * @param forumsByForum the forum ID's corresponding to <tt>threadsByForum</tt>.
     * @param factory a forum factory.
     */
    public WatchList(long [] threadsByDate, boolean [] expirableByDate,
            long [] forumsByDate, long [] threadsByForum, long [] forumsByForum,
            DbForumFactory factory)
    {
        this.threadsByDate = threadsByDate;
        this.expirableByDate = expirableByDate;
       // this.forumsByDate = forumsByDate;
        this.factory = factory;

        this.threadsByForum = threadsByForum;
        this.forumsByForum = forumsByForum;

        // Make sorted thread list so that we can do binary searching. This
        // makes the isThreadWatched method much faster.
        this.sThreadsByDate = new long[threadsByDate.length];
        for (int i=0; i<sThreadsByDate.length; i++) {
            sThreadsByDate[i] = threadsByDate[i];
        }
        //Now, sort threads and expirable lists.
        sort(sThreadsByDate, 0, sThreadsByDate.length - 1);
    }

    /**
     * Returns an Iterator for all threads being watched in a particular forum.
     */
    public Iterator watchedThreads(Forum forum) {
        long forumID = forum.getID();
        // Linear search array for start of forum.
        int start = 0;
        int max = forumsByForum.length;
        while (start < max && forumsByForum[start] != forumID) {
            start++;
        }
        // Now, find end
        int end = start;
        while (end < max && forumsByForum[end] == forumID) {
            end++;
        }
        // Define array of threadIDs.
        long [] threads = new long[end-start];
        for (int i=0; i<threads.length; i++) {
            threads[i] = threadsByForum[start+i];
        }
        return new DatabaseObjectIterator(JiveGlobals.THREAD, threads, forum);
    }

    /**
     * Returns an Iterator for all the threads being watched.
     */
    public Iterator watchedThreads() {
        return new DatabaseObjectIterator(JiveGlobals.THREAD, threadsByDate, factory);
        //return new MixedForumThreadIterator(threadsByDate, forumsByDate, factory, auth);
    }

    public int getWatchCount() {
        return threadsByDate.length;
    }

    public int getWatchCount(Forum forum) {
        long forumID = forum.getID();
        // Find where forumID starts.
        int start = 0;
        int max = forumsByForum.length;
        while (start < max && forumsByForum[start] != forumID) {
            start++;
        }
        // Now, find end
        int end = start;
        while (end < max && forumsByForum[end] == forumID) {
            end++;
        }
        // Count is end - start.
        return end-start;
    }

    /**
     * Returns true if thread with <tt>threadID</tt> is being watched.
     */
    public boolean isWatchedThread(long threadID) {
        return (Arrays.binarySearch(sThreadsByDate, threadID) >= 0);
    }

    /**
     * Returns true if the specified watch is expirable. If the thread isn't
     * being watched at all the method will return false.
     */
    public boolean isWatchExpirable(long threadID) {
        int index =  Arrays.binarySearch(sThreadsByDate, threadID);
        if (index < 0) {
            return false;
        }
        else {
            return expirableByDate[index];
        }
    }

    public int getSize() {
        //Approximate the size of the object in bytes by calculating the size
        //of each field.
        int size = 0;
        size += CacheSizes.sizeOfObject();                  //overhead of object
        size += CacheSizes.sizeOfObject();                  //factory ref
        int arrayLength = this.threadsByDate.length;
        size += CacheSizes.sizeOfLong() * arrayLength * 4;  //arrays

        return size;
    }

    /**
     * Sorts a long [] using the QuickSort algorithm. We must use this instead
     * of Array.sort(long []) since we call a special swamp method that keeps
     * two arrays in sync at the same time.
     */
    private final void sort(long[] a, int lo0, int hi0) {
        int lo = lo0;
        int hi = hi0;
        long mid;
        if ( hi0 > lo0) {
            // Partition will be middle element. This will at least work
            // well on forward and backward sorted data.
            mid = a[ ( lo0 + hi0 ) / 2 ];
            while( lo <= hi ) {
                while( ( lo < hi0 ) && ( a[lo] < mid ) )  ++lo;
                while( ( hi > lo0 ) && ( a[hi] > mid ) )  --hi;
                if( lo <= hi ) {
                    swap(lo, hi);
                    ++lo;
                    --hi;
                }
    	    }
    	    if( lo0 < hi ) {
                sort( a, lo0, hi );
    	    }
            if( lo < hi0 ) {
    		sort( a, lo, hi0 );
            }
        }
    }

    /**
     * Swamps two elements in the threads and expirable arrays.
     */
    private void swap(int i, int j) {
        long temp = sThreadsByDate[i];
        sThreadsByDate[i] = sThreadsByDate[j];
        sThreadsByDate[j] = temp;

        // Now do a swap on expirable array too.
        boolean t = expirableByDate[i];
        expirableByDate[i] = expirableByDate[j];
        expirableByDate[j] = t;
    }
}