/**
 * $RCSfile: RewardManager.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:46 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import java.util.Iterator;

/**
 * Manages the reward points that users trade back and forth to encourage
 * answering of questions. The author of each thread can transfer a variable
 * number of points to any thread they create. Subsequently, they can award
 * those points to whoever they feel makes the best responses in that thread.
 * System administrators also have the ability to reward any number of points
 * to any user.<p>
 *
 * This manager provides methods to transfer points to threads from users, and
 * to reward the best posts in a thread with points. It also provies the ability
 * to check the number of points that individual threads, messages, and users
 * have.<p>
 *
 * The maximum number of points that can be transferred into a thread is
 * controlled by the Jive property <tt>rewards.maxPoints</tt>, which has a
 * default value of Integer.MAX_INT.
 */
public interface RewardManager {

    /**
     * Returns the maximum number of reward points that can be assigned to a
     * thread. By default, this value is essentially unbounded (Integer.MAX_INT).
     *
     * @return the max number of reward points that can be assigned to a thread.
     */
    public int getMaxPoints();

    /***
     * Sets the maximum number of reward points that can be assigned to a
     * thread. By default, this value is essentially unbounded (Integer.MAX_INT).
     *
     * @param numPoints the max number of reward points that can be assigned to
     *      a thread.
     */
    public void setMaxPoints(int numPoints) throws UnauthorizedException;

    /**
     * Transfers the specified number of points to the thread. Points will be
     * drawn from the user account that started the thread. All of the following
     * conditions must be met in order for this method to function:<ul>
     *
     *      <li> The thread must not have been created anonymously.
     *      <li> The user must have at least <tt>numPoints</tt> points in their
     *           account.
     *      <li> The current points in the thread plus <tt>numPoints</tt>
     *           must satisfy 0 <= (currentPoints + <tt>numPoints</tt>) <=
     *           getMaxRewardPoints().</ul>
     *
     * It is possible to use this method to transfer points back from a thread
     * to a user if <tt>numPoints</tt> is a negative value. It is highly
     * recommended that if you do allow points to be transferred back from a
     * thread to a user, you only do so if there are no responses in the thread.
     * This is a good compromise since it let's user's take back "mistaken"
     * reward points, but also prevents the "bait and switch" of users assigning
     * points to lure others into making responses and then taking away those
     * points for future use.
     *
     * @param thread the thread to transfer points to.
     * @param numPoints the number of points to transfer.
     * @throws Unauthorized exception if not the user that started the thread
     *      or an admin.
     */
    public void transferPoints(ForumThread thread, int numPoints)
            throws UnauthorizedException, RewardException;

    /**
     * Rewards a message with points from the thread that the message belongs
     * to. The user that created the message will also have the points added
     * to their account.
     *
     * All of the following conditions must be met (or a RewardException is
     * thrown):
     * <ul>
     *      <li> The message being awarded cannot be an anonymous posting.
     *      <li> User cannot assign points to themselves.
     *      <li> <tt>numPoints</tt> must be greater than 0 but less than the
     *          total number of available points in the thread.</ul>
     *
     * @param message the message receiving the reward.
     * @param numPoints the number of points to reward the message with.
     * @throws UnauthorizedException if not author of the thread the message
     *      is in, or an administrator.
     * @throws RewardException if one of the necessary conditions for this
     *      method to execute is not met.
     */
    public void rewardPoints(ForumMessage message, int numPoints)
            throws UnauthorizedException, RewardException;

    /**
     * Returns the number of reward points in a thread that are left to be
     * distributed. The number will never exceed the value of
     * getMaxRewardPoints().
     *
     * @return the number of points in a thread that are left to be distributed.
     */
    public int getPoints(ForumThread thread);

    /**
     * Returns the number of reward points that have been assigned to a message.
     * Assigning points to a message also gives those points to the user that
     * created a message.
     *
     * @return the number of points a message has been assigned.
     */
    public int getPoints(ForumMessage message);

    /**
     * Returns the current number of reward points a user has. Users gain points
     * when messages they authored are rewarded and lose points when they
     * transfer points to threads they create.
     *
     * @return the number of points a user has.
     */
    public int getCurrentPoints(User user);

    /**
     * Returns the threads (ordered by modified date) that the user has
     * assigned points to that haven't been fully rewarded out yet. This is
     * useful for showing a user the threads that they may need to go
     * back and look at to assign points in.
     *
     * @return a Iterator for the threads that a user has un-rewarded points in.
     */
    public Iterator userRewardThreads(User user);

    /**
     * Returns the total number of points that a user has ever rewarded.
     * Highlighting this value may be a good idea for communities that use the
     * reward system since it is a great way to encourage people to give out
     * points (instead of hoarding them).<p>
     *
     * Points are not considered rewarded until they are actually transferred
     * from a thread to
     *
     * @return the total number of points that a user has rewarded.
     */
    public int getTotalPointsRewarded(User user);

    /**
     * Returns the total number of points that a user has ever earned. This
     * value does not fluctate
     *
     * @return the total number of points that a user has ever earned.
     */
    public int getTotalPointsEarned(User user);

    /**
     * Allows administrators to add or substract an arbitrary number of reward
     * points to a user account. To remove points, simple pass in a negative
     * point value.
     *
     * @param user the user to give points to.
     * @param numPoints the number of points to give to the user, which can be
     *      negative.
     */
    public void addPoints(User user, int numPoints)
            throws UnauthorizedException, RewardException;
}