/**
 * $RCSfile: DbTreeWalker.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:58 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import java.sql.*;
import java.util.Iterator;

import com.jivesoftware.forum.*;
import com.jivesoftware.util.*;

/**
 * Database implementation of the TreeWalker interface. It caches the tree
 * structure in memory for fastest access.<p>
 *
 * One important caveat is the following: Jive assumes that no child message
 * will have a creation date that is earlier in time than its parent message.
 * The assumption lets us make some big optimization gains and is safe as long
 * as all the data is created in Jive. The only possible problem is if data is
 * imported from other forum apps that don't follow this behavior (not very
 * likely).
 */
public class DbTreeWalker implements TreeWalker {

    /** DATABASE QUERIES **/
    private static final String GET_MESSAGES =
        "SELECT messageID, parentMessageID, creationDate FROM jiveMessage " +
        "WHERE threadID=? AND parentMessageID IS NOT NULL " +
        "ORDER BY creationDate ASC";

    /**
     * Create a result filter that will let us get the number of messages in
     * thread ignoring moderation.
     */
    private static ResultFilter IGNORE_MODERATION_FILTER;
    static {
        IGNORE_MODERATION_FILTER = ResultFilter.createDefaultMessageFilter();
        IGNORE_MODERATION_FILTER.setModerationRangeMin(Integer.MIN_VALUE+1);
    }

    private long threadID;
    private DbForumFactory factory;
    private LongTree tree;

    /**
     * Creates a new TreeWalker instance.
     */
    public DbTreeWalker(DbForumThread thread, DbForumFactory factory) {
        this.threadID = thread.getID();
        this.factory = factory;

        ForumMessage root = thread.getRootMessage();
        int numMessages = thread.getMessageCount(IGNORE_MODERATION_FILTER);
        // Create the tree, set the root.
        tree = new LongTree(root.getID(), numMessages);

        // Now, build the rest of the tree.
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(GET_MESSAGES);
            pstmt.setLong(1, thread.getID());
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                long messageID = rs.getLong(1);
                long parentMessageID = rs.getLong(2);
                tree.addChild(parentMessageID, messageID);
            }
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        finally {
            try {  pstmt.close();   }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    //FROM THE TREEWALKER INTERFACE//

    public ForumMessage getRoot() {
        ForumThread thread = null;
        try {
            thread = factory.cacheManager.threadCache.get(threadID);
        }
        catch (Exception e) {
            return null;
        }
        return thread.getRootMessage();
    }

    public int getChildCount(ForumMessage parent) {
        return tree.getChildCount(parent.getID());
    }

    public ForumMessage getParent(ForumMessage message)
            throws ForumMessageNotFoundException
    {
        long parentID = tree.getParent(message.getID());
        if (parentID == -1) {
            throw new ForumMessageNotFoundException();
        }
        else {
            ForumThread thread = null;
            try {
                thread = factory.cacheManager.threadCache.get(threadID);
            }
            catch (Exception e) {
                throw new ForumMessageNotFoundException();
            }
            return thread.getMessage(parentID);
        }
    }

    public ForumMessage getChild(ForumMessage message, int index)
            throws ForumMessageNotFoundException
    {
        long childID = tree.getChild(message.getID(), index);
        if (childID == -1) {
            throw new ForumMessageNotFoundException();
        }
        else {
            ForumThread thread = null;
            try {
                thread = factory.cacheManager.threadCache.get(threadID);
            }
            catch (Exception e) {
                throw new ForumMessageNotFoundException();
            }
            return thread.getMessage(childID);
        }
    }

    public Iterator children(ForumMessage parent) {
        long [] children = tree.getChildren(parent.getID());
        return new DatabaseObjectIterator(JiveGlobals.MESSAGE, children,
                parent.getForumThread());
    }

    public Iterator recursiveChildren(ForumMessage parent) {
        long [] messages = tree.getRecursiveChildren(parent.getID());
        return new DatabaseObjectIterator(JiveGlobals.MESSAGE, messages,
                parent.getForumThread());
    }

    public int getMessageDepth(ForumMessage message) {
        int depth = tree.getDepth(message.getID());
        if (depth == -1) {
            throw new IllegalArgumentException("Message " + message.getID() +
                    " does not belong to this thread.");
        }
        return depth;
    }

    public int getRecursiveChildCount(ForumMessage parent) {
        //Call the recursive method.
        return getRecursiveChildCount(parent.getID());
    }

    public int getIndexOfChild(ForumMessage parent, ForumMessage child) {
        return tree.getIndexOfChild(parent.getID(), child.getID());
    }

    public boolean isLeaf(ForumMessage message) {
        return tree.isLeaf(message.getID());
    }

    //OTHER METHODS//

    public int getSize() {
        int size = 0;
        size += CacheSizes.sizeOfObject();
        size += tree.getSize();
        size += CacheSizes.sizeOfLong();
        size += CacheSizes.sizeOfLong();
        size += CacheSizes.sizeOfObject();
        return size;
    }

    private int getRecursiveChildCount(long parentID) {
        int numChildren = 0;
        int num = tree.getChildCount(parentID);
        numChildren += num;
        for (int i=0; i<num; i++) {
            long childID = tree.getChild(parentID, i);
            if (childID != -1) {
                numChildren += getRecursiveChildCount(childID);
            }
        }
        return numChildren;
    }
}