/**
 * $RCSfile: ForumMessageBlockIterator.java,v $
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
 * A iterator for blocks of message ID's.
 */
public class ForumMessageBlockIterator implements Iterator {

    private long [] block;

    private int blockID;
    private int blockStart;

    private String query;

    private int startIndex;
    private int currentIndex;
    private int endIndex;

    private long forumID;
    private long threadID;

    private DbForumFactory factory;

    private Object previousElement = null;
    private Object nextElement = null;
    private boolean nextFlag = false;
    private boolean previousFlag = false;

    /**
     * Constructs a new ForumMessageBlockIterator.
     *
     * @param elements the array of id's to iterate through.
     */
    protected ForumMessageBlockIterator(long [] block, String query,
            int startIndex, int endIndex, long forumID, DbForumFactory factory,
            long threadID)
    {
        this.block = block;
        this.blockID = startIndex / DbForum.MESSAGE_BLOCK_SIZE;
        this.blockStart = blockID * DbForum.MESSAGE_BLOCK_SIZE;
        this.query = query;
        this.currentIndex = startIndex - 1;
        this.startIndex = startIndex;
        this.endIndex = endIndex;

        this.forumID = forumID;
        this.factory = factory;
        this.threadID = threadID;
    }

    /**
     * Returns true if there are more messages in the list.
     *
     * @return true if the iterator has more elements.
     */
    public boolean hasNext() {
        nextFlag = true;
        //If we are at the end of the list, there can't be any more elements
        //to iterate through.
        if (currentIndex+1 >= endIndex && nextElement == null) {
            return false;
        }
        //Otherwise, see if nextElement is null. If so, try to load the next
        //element to make sure it exists.
        if (nextElement == null) {
            nextElement = getNextElement();
            if (nextElement == null) {
                return false;
            }
        }
        return true;
    }

    public boolean hasPrevious() {
        previousFlag = true;
        if (currentIndex-1 < startIndex && previousElement == null) {
            return false;
        }
        //Otherwise, see if nextElement is null. If so, try to load the next
        //element to make sure it exists.
        if (previousElement == null) {
            previousElement = getPreviousElement();
            if (previousElement == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the next element.
     *
     * @return the next element.
     * @throws NoSuchElementException if there are no more elements.
     */
    public Object next() throws java.util.NoSuchElementException {
        nextFlag = false;
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
        previousFlag = true;
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
/*
    public void setIndex(ForumThread thread) {
        //Get the forum object for loading thread blocks
        try {
            DbForum forum = factory.cacheManager.forumCache.get(forumID);
            //Scan through all blocks looking for thread.
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
                //If in first block of threads
                if (startIndex < blockEnd) {
                    //If we are in the first block, j should start at the
                    //start index instead of the beginning of the block.
                    for (int j=startIndex%DbForum.THREAD_BLOCK_SIZE;
                            j<currentBlock.length; j++, i++)
                    {
                        if (currentBlock[j] == threadID) {
                            this.currentIndex = i;
                            return;
                        }
                    }
                }
                //Otherwise, not in first block so start looking at beginning
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
    }
*/
    /**
     * Not supported for security reasons.
     */
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the next available element, or null if there are no more
     * elements to return.
     *
     * @return the next available element.
     */
    public Object getNextElement() {
        if (previousElement != null && previousFlag) {
            previousElement = null;
            getNextElement();
        }

        Object element = null;
        while (currentIndex+1 < endIndex && element == null) {
            currentIndex++;
            element = getElement(currentIndex);
        }
        return element;
    }

    public Object getPreviousElement() {
        if (nextElement != null && nextFlag) {
            nextElement = null;
            getPreviousElement();
        }
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
        // See if element isn't in the current block
        if (index < blockStart || index >= blockStart + DbForum.MESSAGE_BLOCK_SIZE) {
            // Then load up the appropriate block. We either load the block
            // from a forum or thread, depending on what the original query was
            // for.
            if(threadID == -1) {
                try {
                    DbForum forum = factory.cacheManager.forumCache.get(forumID);
                    this.block = forum.getMessageBlock(query, index);
                    this.blockID = index / DbForum.MESSAGE_BLOCK_SIZE;
                    this.blockStart = blockID * DbForum.MESSAGE_BLOCK_SIZE;
                }
                catch (ForumNotFoundException fnfe) {
                    // If we get this exception, it probably means the entire
                    // forum has been deleted, which means we should return null.
                    return null;
                }
            }
            // Must have been a thread query, so load next block from thread.
            else {
                try {
                    DbForumThread thread = factory.cacheManager.threadCache.get(
                            threadID);
                    this.block = thread.getMessageBlock(query, index);
                    this.blockID = index / DbForum.MESSAGE_BLOCK_SIZE;
                    this.blockStart = blockID * DbForum.MESSAGE_BLOCK_SIZE;
                }
                catch (Exception e) {
                    return null;
                }
            }

        }
        Object element = null;
        // Compute the relative index of the element, which is the index in the
        // current message block.
        int relativeIndex = index % DbForum.MESSAGE_BLOCK_SIZE;
        // Make sure index isn't too large
        if (relativeIndex < block.length) {
            try {
                // Load the actual message object
                ForumMessage message =  factory.cacheManager.messageCache.get(
                        block[relativeIndex]);
                // Now, get the message from its thread so that filters are
                // applied to the message. This may seem a bit convuluted, but is
                // necessary.
                element = message.getForumThread().getMessage(block[relativeIndex]);
            }
            catch (ForumMessageNotFoundException tnfe) { }
        }
        return element;
    }
}