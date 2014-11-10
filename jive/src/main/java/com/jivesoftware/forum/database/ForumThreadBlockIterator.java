/**
 * $RCSfile: ForumThreadBlockIterator.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:01 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import com.jivesoftware.forum.*;
import java.util.*;

/**
 * Database implementation of the ForumThreadIterator abstract class.
 *
 * @see ForumThreadIterator
 */
public class ForumThreadBlockIterator extends ForumThreadIterator {

    private long [] threadBlock;

    private int blockID;
    private int blockStart;

    private String query;

    private int startIndex;
    private int currentIndex;
    private int endIndex;

    private long forumID;
    private DbForumFactory factory;

    private Object previousElement = null;
    private Object nextElement = null;

    /**
     * Constructs a new ForumThreadBlockIterator.
     *
     * @param threadBlock the starting threadBlock of elements to iterate
     *      through.
     * @param query the SQL query corresponding to this iteration.
     * @param startIndex the starting index of the iteration.
     * @param endIndex the ending index of the iteration.
     * @param forumID the forumID the threads are a part of.
     * @param factory a ForumFactory to load data from.
     */
    protected ForumThreadBlockIterator(long [] threadBlock, String query,
            int startIndex, int endIndex, long forumID, DbForumFactory factory)
    {
        this.threadBlock = threadBlock;
        this.blockID = startIndex / DbForum.THREAD_BLOCK_SIZE;
        this.blockStart = blockID * DbForum.THREAD_BLOCK_SIZE;
        this.query = query;
        this.currentIndex = startIndex - 1;
        this.startIndex = startIndex;
        this.endIndex = endIndex;

        this.forumID = forumID;
        this.factory = factory;
    }

    public boolean hasNext() {
        // If we are at the end of the list there are no more elements.
        if (currentIndex == endIndex) {
            return false;
        }
        // Otherwise, see if nextElement is null. If so, try to load the next
        // element to make sure it exists. If nextElement isn't null, that
        // means we've already checked to make sure it exists.
        if (nextElement == null) {
            nextElement = getNextElement();
            // If getting the next element failed, return false.
            if (nextElement == null) {
                return false;
            }
        }
        return true;
    }

    public boolean hasPrevious() {
        // If we are at the start of the list there are no previous elements.
        if (currentIndex == startIndex) {
            return false;
        }
        // Otherwise, see if previousElement is null. If so, try to load the
        // previous element to make sure it exists.
        if (previousElement == null) {
            previousElement = getPreviousElement();
            // If getting the previous element failed, return false.
            if (previousElement == null) {
                return false;
            }
        }
        return true;
    }

    public Object next() throws java.util.NoSuchElementException {
        Object element = null;
        if (nextElement != null) {
            element = nextElement;
            this.nextElement = null;
        }
        else {
            element = getNextElement();
            if (element == null) {
                throw new java.util.NoSuchElementException();
            }
        }
        return element;
    }

    public Object previous() {
        Object element = null;
        if (previousElement != null) {
            element = previousElement;
            previousElement = null;
        }
        else {
            element = getPreviousElement();
            if (element == null) {
                throw new java.util.NoSuchElementException();
            }
        }
        return element;
    }

    public void setIndex(ForumThread thread) {
        // Set nextElement and previousElement to null since we may be moving
        // the index.
        nextElement = null;
        previousElement = null;

        // Get the forum object for loading thread blocks
        try {
            DbForum forum = factory.cacheManager.forumCache.get(forumID);
            // Scan through all blocks looking for thread.
            long threadID = thread.getID();
            long [] currentBlock;
            for (int i=startIndex; i<endIndex; i++) {
                currentBlock = forum.getThreadBlock(query, i);
                if (currentBlock.length == 0) {
                    throw new NoSuchElementException("Thread with id " +
                        threadID + " is not a valid index in the iteration.");
                }
                int blockID = i / DbForum.THREAD_BLOCK_SIZE;
                int blockEnd = blockID * DbForum.THREAD_BLOCK_SIZE + DbForum.THREAD_BLOCK_SIZE;
                // If in first block of threads
                if (startIndex < blockEnd) {
                    // If we are in the first block, j should start at the
                    // start index instead of the beginning of the block.
                    for (int j=startIndex%DbForum.THREAD_BLOCK_SIZE;
                            j<currentBlock.length; j++, i++)
                    {
                        if (currentBlock[j] == threadID) {
                            this.currentIndex = i;
                            return;
                        }
                    }
                }
                // Otherwise, not in first block so start looking at beginning
                else {
                    for (int j=0; j<currentBlock.length; j++, i++) {
                        if (currentBlock[j] == threadID) {
                            this.currentIndex = i;
                            return;
                        }
                    }
                }
            }
        }
        catch (ForumNotFoundException e) { }
        // Got this far, so we never found the the element.
        throw new NoSuchElementException("Thread with id " + thread.getID() +
                " is not a valid index in the iteration.");
    }

    /**
     * Returns the next element, or null if there are no more
     * elements to return.
     *
     * @return the next element.
     */
    private Object getNextElement() {
        previousElement = null;
        Object element = null;
        while (currentIndex+1 < endIndex && element == null) {
            currentIndex++;
            element = getElement(currentIndex);
        }
        return element;
    }

    /**
     * Returns the previous element, or null if there are no more elements
     * to return.
     *
     * @return the previous element.
     */
    private Object getPreviousElement() {
        nextElement = null;

        Object element = null;
        while (currentIndex >= startIndex && element == null) {
            currentIndex--;
            element = getElement(currentIndex);
        }
        return element;
    }

    private Object getElement(int index) {
        if (index < 0) {
            return null;
        }
        // See if element isn't in the current thread block
        if (index < blockStart || index >= blockStart + DbForum.THREAD_BLOCK_SIZE) {
            // Then load up the appropriate block
            try {
                DbForum forum = factory.cacheManager.forumCache.get(forumID);
                this.threadBlock = forum.getThreadBlock(query, index);
                this.blockID = index / DbForum.THREAD_BLOCK_SIZE;
                this.blockStart = blockID * DbForum.THREAD_BLOCK_SIZE;
            }
            catch (ForumNotFoundException fnfe) {
                // If we get this exception, it probably means the entire forum
                // has been deleted, which means we should return null.
                return null;
            }
        }
        Object element = null;
        // Compute the relative index of the element, which is the index in the
        // current thread block.
        int relativeIndex = index % DbForum.THREAD_BLOCK_SIZE;
        // Make sure index isn't too large
        if (relativeIndex < threadBlock.length) {
            try {
                // Load the actual thread object
                element = factory.cacheManager.threadCache.get(
                        threadBlock[relativeIndex]);
            }
            catch (ForumThreadNotFoundException tnfe) { }
        }
        return element;
    }
}