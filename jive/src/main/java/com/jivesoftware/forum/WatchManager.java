/**
 * $RCSfile: WatchManager.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:47 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import java.util.*;

/**
 * Manages user watches on threads. A watch is a way for a user to track
 * updates to a thread. Users add watches on individual threads and can
 * specifiy whether or not they want to be notified by email (or
 * some other way) each time the thread is updated.<p>
 *
 * Two kinds of watches are available: <ul>
 *      <li> "Normal" watches are passive watches. This means the user doesn't
 *          get explicit notifcations of thread updates, but will see that
 *          has been updated the next time they visit the forum.
 *      <li> Email update watches send an email to the user whenever the thread
 *          is updated. Typically, this email message will include a link back
 *          to the online forums, but messages are highly configurable. Email
 *          notifications are disabled by default, but can be enabled by setting
 *          the Jive property <tt>watches.emailNotifyEnabled</tt> to <tt>true</tt>.
 * </ul>
 *
 * Email watch updates require a number of Jive properties to be set:<ul>
 *  <li> <tt>watches.email.fromName</tt> -- the name that will be used
 *      for watch update emails, e.g. Webmaster.
 *  <li> <tt>watches.email.fromEmail</tt> -- the email address that will
 *      be used for watch update emails, e.g. webmaster@yoursite.com.
 *  <li> <tt>watches.email.subject</tt> -- the subject that will be used
 *      for watch update emails.
 *  <li> <tt>watches.email.body</tt> -- the body that will be used for
 *      watch update emails.
 * </ul>
 *
 * The subject and body of email update messages can be fully customized with
 * the help of special tokens. Valid tokens are:
 * <ul>
 * {username}, {email}, {name}, {userID} {threadID}, {threadName},
 * {threadModifiedDate}, {threadCreationDate}, {forumID}, {forumName}
 * </ul>
 *
 * For example, a subject of:<br>
 * <tt>Hello {name}! Thread "{threadName}" has been updated in forum "{forumName}".</tt>
 * <p>
 *
 * Would be transformed into something like:<br>
 * <tt>Hello Joe Smith! Thread "New Monitor" has been updated in forum
 * "Computers".</tt><p>
 *
 * Because the number of watches could build up and overload the system over time,
 * watches are automatically deleted after the threads they are attached to are
 * inactive for a certain number of days. By default, this value is 30 days.
 * Individual watches can be toggled to be undeletable so that it is possible
 * to circumvent this behavior.
 */
public interface WatchManager {

    /**
     * The normal watch type. Notification is passive which means the user will
     * see that a thread has been updated next time they view it.
     */
    public static final int NORMAL_WATCH = 0;

    /**
     * A watch that notifies its user via email that the thread has been modified.
     */
    public static final int EMAIL_NOTIFY_WATCH = 1;

    /**
     * Sets the number of days that a watched thread can remain inactive
     * before watches on that thread are deleted. This value corresponds to
     * the <tt>watches.deleteDays</tt> Jive property. The default value is
     * 30 days.
     *
     * @param deleteDays the number days a watch can be inactive before being
     *      automatically deleted.
     * @throws UnauthorizedException if not a system admin.
     */
    public void setDeleteDays(int deleteDays) throws UnauthorizedException;

    /**
     * Returns the number of days that a watched thread can remain inactive
     * before watches on that thread are deleted. This value corresponds to
     * the <tt>watches.deleteDays</tt> Jive property. The default value is
     * 30 days.
     *
     * @return the number of days a thread can be inactive before watches on it
     *      are deleted.
     */
    public int getDeleteDays();

    /**
     * Returns true if email notifications are enabled. By default, they are
     * disabled unless the Jive property <tt>watches.emailNotifyEnabled</tt>
     * is set to <tt>true</tt>.
     *
     * @return true if email notifications are enabled.
     * @throws UnauthorizedException if not a system admin.
     */
    public boolean isEmailNotifyEnabled() throws UnauthorizedException;

    /**
     * Sets thread watching on or off. This methods saves its value to the
     * jive.properties file as "watches.enabled". The only effect of disabling
     * watches is that calling the notifyWatches method will do nothing.
     *
     * @param enabled true to turn watches on, false to turn watches off.
     * @throws UnauthorizedExeption if not a system admin.
     */
    public void setEmailNotifyEnabled(boolean enabled) throws UnauthorizedException;

    /**
     * Returns the body of the email messages that get sent during email watch
     * updates. This value is stored as the Jive property
     * <tt>watches.email.body</tt>.
     *
     * @return the value of the body of email sent during watch updates.
     */
    public String getEmailBody() throws UnauthorizedException;

    /**
     * Sets the body of the email messages that get sent during email watch
     * updates. This value is stored as the Jive property
     * <tt>watches.email.body</tt>.
     *
     * @param body the body of email message updates.
     */
    public void setEmailBody(String body) throws UnauthorizedException;

    /**
     * Returns the subject of the email messages that get sent during email
     * watch updates. This value is stored as the Jive property
     * <tt>watches.email.subject</tt>.
     *
     * @return the value of the body of email sent during watch updates.
     */
    public String getEmailSubject() throws UnauthorizedException;

    /**
     * Sets the subject of the email messages that get sent during email watch
     * updates. This value is stored as the Jive property
     * <tt>watches.email.subject</tt>.
     *
     * @param subject the subject of email message updates.
     */
    public void setEmailSubject(String subject) throws UnauthorizedException;

    /**
     * Returns the "from name" field for email messages that get sent during
     * email watch updates. This value is stored as the Jive property
     * <tt>watches.email.fromName</tt>.
     *
     * @return the value of the body of email sent during watch updates.
     */
    public String getEmailFromName() throws UnauthorizedException;

    /**
     * Sets the "from name" field for email messages that get sent during
     * email watch updates. This value is stored as the Jive property
     * <tt>watches.email.fromName</tt>.
     *
     * @param fromName the "from name" that should be used for email message
     *      updates.
     */
    public void setEmailFromName(String fromName) throws UnauthorizedException;

    /**
     * Returns the "from email" field for email messages that get sent during
     * email watch updates. This value is stored as the Jive property
     * <tt>watches.email.fromEmail</tt>.
     *
     * @return the value of the body of email sent during watch updates.
     */
    public String getEmailFromEmail() throws UnauthorizedException;

    /**
     * Sets the "from email" field for email messages that get sent during
     * email watch updates. This value is stored as the Jive property
     * <tt>watches.email.fromEmail</tt>.
     *
     * @param fromEmail the "from email" that should be used for email message
     *      updates.
     */
    public void setEmailFromEmail(String fromEmail) throws UnauthorizedException;

    /**
     * Creates a watch on the thread. If the thread is already being watched,
     * this method does nothing.
     *
     * @param user the User watching the thread.
     * @param thread the ForumThread to be watched.
     * @param watchType the type of watch this is.
     * @throws UnauthorizedException if not a system admin or the user.
     */
    public void createWatch(User user, ForumThread thread, int watchType)
            throws UnauthorizedException;

    /**
     * Deletes a watch from a thread.
     *
     * @param user the User watching the thread.
     * @param thread the ForumThread that will no longer we watched.
     * @throws UnauthorizedException if not a system admin or the user.
     */
    public void deleteWatch(User user, ForumThread thread, int watchType)
            throws UnauthorizedException;

    /**
     * Returns true if a watch is automatically expirable by the system. Watches
     * are always automatically expirable when first created, so they must be
     * explicitly set to the opposite if desired. Leaving watches as expirable
     * has to benefits. First, it makes watch management easier for users since
     * watched threads that have been inactive for long periods of time will
     * automatically drop off the end of their watches list. Second, expiring
     * old watches also lightens the load on the system.<p>
     *
     * If the specified watch does not exist, calling this method will always
     * return false.
     *
     * @param user the User watching the thread.
     * @param thread the thread the watch is set on.
     * @param watchType the type of the watch.
     * @return true if the specified watch is expirable.
     * @throws UnauthorizedException if not a system admin or the user.
     */
    public boolean isWatchExpirable(User user, ForumThread thread, int watchType)
            throws UnauthorizedException;

    /**
     * Sets a watch to be automatically expirable by the system or not. Watches
     * are always automatically expirable when first created, so they must be
     * explicitly set to the opposite if desired. Leaving watches as expirable
     * has to benefits. First, it makes watch management easier for users since
     * watched threads that have been inactive for long periods of time will
     * automatically drop off the end of their watches list. Second, expiring
     * old watches also lightens the load on the system.<p>
     *
     * If the specified watch does not exist, calling this method will do nothing.
     *
     * @param user the User watching the thread.
     * @param thread the thread the watch is set on.
     * @param watchType the type of the watch.
     * @param expirable true if the watch should be expirable; false otherwise.
     * @throws UnauthorizedException if not a system admin or the user.
     */
    public void setWatchExpirable(User user, ForumThread thread, int watchType,
            boolean expirable) throws UnauthorizedException;

    /**
     * Returns a list of all threads this user is watching (across all forums).
     * Threads will be given in the order that they were last modified.
     *
     * @param user the User watching threads.
     * @param watchType the type of watch.
     * @return a list of threads this user is watching.
     * @throws UnauthorizedException if not a system admin or the user.
     */
    public Iterator getWatchedForumThreads(User user, int watchType)
            throws UnauthorizedException;

    /**
     * Returns a list of threads this user is watching in the specified forum.
     * Threads will be given in the order that they were last modified.
     *
     * @param user the User watching threads.
     * @param forum the Forum we want the watched threads for.
     * @param watchType the type of watch.
     * @return a list of threads the user is watching in the spcified forum.
     * @throws UnauthorizedException if not a system admin or the user.
     */
    public Iterator getWatchedForumThreads(Forum forum, User user, int watchType)
            throws UnauthorizedException;

    /**
     * Returns a count of all watches that a user has.
     *
     * @param user the User watching threads.
     * @param watchType the type of watch.
     * @return a count of all the watches that a user has.
     * @throws UnauthorizedException if not a system admin or the user.
     */
    public int getWatchCount(User user, int watchType)
            throws UnauthorizedException;

    /**
     * Returns a count of all the watches a user has in a particular forum.
     *
     * @param user the User watching threads.
     * @param forum the Forum we want the watched threads for.
     * @param watchType the type of watch.
     * @return a count of the watches a user is has in the spcified forum.
     * @throws UnauthorizedException if not a system admin or the user.
     */
    public int getWatchCount(Forum forum, User user, int watchType)
            throws UnauthorizedException;

    /**
     * Returns true if the user is watching the thread, false otherwise.
     *
     * @param user the User watching the thread.
     * @param thread the ForumThread the user is watching.
     * @return true if the thread is being watched by the user, false otherwise.
     * @throws UnauthorizedException if not a system admin or the user.
     */
    public boolean isWatchedThread(User user, ForumThread thread, int watchType)
            throws UnauthorizedException;
}