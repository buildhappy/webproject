/**
 * $RCSfile: SequenceManager.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:02 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import java.sql.*;
import com.jivesoftware.util.StringUtils;

/**
 * Manages sequences of unique ID's that get stored in the database. Database
 * support for sequences varies widely; some don't support them at all. So,
 * we handle unique ID generation with a combination VM/database solution.<p>
 *
 * A special table in the database doles out blocks of unique ID's to each
 * virtual machine that interacts with Jive. This has the following consequences:
 * <ul>
 *  <li>There is no need to go to the database every time we want a new unique
 *      id.
 *  <li>Multiple app servers can interact with the same db without consequences
 *      in terms of id's.
 *  <li>The order of unique id's may not correspond to creation date
 *      (as they once did).
 * </ul>
 */
public class SequenceManager {

    private static final String LOAD_ID =
        "SELECT id FROM jiveID WHERE idType=?";
    private static final String UPDATE_ID =
        "UPDATE jiveID SET id=? WHERE idType=? AND id=?";

    /**
     * The number of ID's to checkout at a time. 15 should provide a good
     * balance between speed and not wasing too many ID's on appserver restarts.
     * Feel free to change this number if you believe your Jive setup warrants
     * it.
     */
    private static final int INCREMENT = 15;

    // Statically startup a sequence manager for each of the five sequence
    // counters.
    private static SequenceManager[] managers;
    static {
        managers = new SequenceManager[5];
        for (int i=0; i<managers.length; i++) {
            managers[i] = new SequenceManager(i);
        }
    }

    /**
     * Returns the next ID of the specified type.
     *
     * @param type the type of unique ID.
     * @return the next unique ID of the specified type.
     */
    public static long nextID(int type) {
        return managers[type].nextUniqueID();
    }

    public static long nextID() {
        return managers[0].nextUniqueID();
    }

    private int type;
    private long currentID;
    private long maxID;

    /**
     * Creates a new DbSequenceManager.
     */
    public SequenceManager(int type) {
        this.type = type;
        currentID = 0l;
        maxID = 0l;
    }

    /**
     * Returns the next available unique ID. Essentially this provides for the
     * functionality of an auto-increment database field.
     */
    public synchronized long nextUniqueID() {
        if (! (currentID < maxID)) {
            // Get next block -- make 5 attempts at maximum.
            getNextBlock(5);
        }
        long id = currentID;
        currentID++;
        return id;
    }

    /**
     * Performs a lookup to get the next availabe ID block. The algorithm is as
     * follows:<ol>
     *  <li> Select currentID from appropriate db row.
     *  <li> Increment id returned from db.
     *  <li> Update db row with new id where id=old_id.
     *  <li> If update fails another process checked out the block first; go
     *          back to step 1. Otherwise, done.
     * </ol>
     */
    private void getNextBlock(int count) {
        if (count == 0) {
            System.err.println("Failed at last attempt to obtain an ID, aborting...");
            return;
        }
        boolean success = false;
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            // Get the current ID from the database.
            pstmt = con.prepareStatement(LOAD_ID);
            pstmt.setInt(1, type);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                throw new SQLException("Loading the current ID failed. The " +
                    "jiveID table may not be correctly populated.");
            }
            long currentID = rs.getLong(1);
            pstmt.close();
            // Increment the id to define our block.
            long newID = currentID + INCREMENT;
            // The WHERE clause includes the last value of the id. This ensures
            // that an update will occur only if nobody else has performed an
            // update first.
            pstmt = con.prepareStatement(UPDATE_ID);
            pstmt.setLong(1, newID);
            pstmt.setInt(2, type);
            pstmt.setLong(3, currentID);
            // Check to see if the row was affected. If not, some other process
            // already changed the original id that we read. Therefore, this
            // round failed and we'll have to try again.
            success = pstmt.executeUpdate() == 1;
            if (success) {
                this.currentID = currentID;
                this.maxID = newID;
            }
        }
        catch( Exception sqle ) {
            sqle.printStackTrace();
        }
        finally {
            try {  pstmt.close();   }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
        if (!success) {
            System.err.println("WARNING: failed to obtain next ID block due to " +
                "thread contention. Trying again...");
            // Call this method again, but sleep briefly to try to avoid thread
            // contention.
            try {
                Thread.currentThread().sleep(75);
            } catch (InterruptedException ie) { }
            getNextBlock(count-1);
        }
    }
}