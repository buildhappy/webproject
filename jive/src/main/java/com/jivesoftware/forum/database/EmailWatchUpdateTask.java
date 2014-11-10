/**
 * $RCSfile: EmailWatchUpdateTask.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:00 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import java.sql.*;

import com.jivesoftware.util.*;
import com.jivesoftware.forum.util.*;
import com.jivesoftware.forum.*;

/**
 * A task that sends out email notifications about a thread watch update.
 */
public class EmailWatchUpdateTask implements Runnable {

    /** DATABASE QUERIES **/
    private static final String GET_WATCHES =
        "SELECT userID from jiveWatch WHERE forumID=? AND threadID=? and " +
        "watchType=" + WatchManager.EMAIL_NOTIFY_WATCH;

    private DbWatchManager watchManager;
    private DbForumFactory factory;
    private ForumThread thread;

    /**
     * Creates a new email watch update task.
     */
    public EmailWatchUpdateTask(DbWatchManager watchManager,
            DbForumFactory factory, ForumThread thread)
    {
        this.watchManager = watchManager;
        this.factory = factory;
        this.thread = thread;
    }

    /**
     * Executes the task by loading a list of watches and sending emails.
     */
    public void run() {
        // First, load all watches from the db for this thread. We only need
        // to pay attention to email watches at this point.
        LongList users = new LongList();
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(GET_WATCHES);
            pstmt.setLong(1, thread.getForum().getID());
            pstmt.setLong(2, thread.getID());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                users.add(rs.getLong(1));
            }
        }
        catch( SQLException sqle ) {
            sqle.printStackTrace();
        }
        finally {
            try {  pstmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }

        //Now, create an email task
        EmailTask emailTask = new EmailTask();

        for (int i=0; i<users.size(); i++) {
            try {
                User user = factory.userManager.getUser(users.get(i));
                String toName = user.getName();
                String toEmail = user.getEmail();
                String fromEmail = watchManager.getEmailFromEmail();
                String fromName = watchManager.getEmailFromName();
                String subject = replaceTokens(watchManager.getEmailSubject(),
                        thread, user);
                String body = replaceTokens(watchManager.getEmailBody(),
                        thread, user);
                emailTask.addMessage(toName, toEmail, fromName, fromEmail,
                        subject, body);
            }
            catch (UserNotFoundException e) { }
        }

        //Send all emails using the currently running worker.
        emailTask.run();
    }

    /**
     * Replaces tokens with the correct values.
     */
    private String replaceTokens(String string, ForumThread thread, User user) {
        string = StringUtils.replace(string, "{username}", user.getUsername());
        string = StringUtils.replace(string, "{email}", user.getEmail());
        String name = user.getName();
        if (name != null) {
            string = StringUtils.replace(string, "{name}", name);
        }
        //If name is null, replace {name} with username
        else {
            string = StringUtils.replace(string, "{name}", user.getUsername());
        }
        string = StringUtils.replace(string, "{userID}",
                Long.toString(user.getID()));

        string = StringUtils.replace(string, "{threadID}",
                Long.toString(thread.getID()));
        string = StringUtils.replace(string, "{threadName}", thread.getName());
        string = StringUtils.replace(string, "{threadModifiedDate}",
                JiveGlobals.formatDateTime(thread.getModifiedDate()));
        string = StringUtils.replace(string, "{threadCreationDate}",
                JiveGlobals.formatDateTime(thread.getCreationDate()));

        Forum forum = thread.getForum();
        string = StringUtils.replace(string, "{forumID}",
                Long.toString(forum.getID()));
        string = StringUtils.replace(string, "{forumName}", forum.getName());

        return string;
    }
}