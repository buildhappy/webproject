/**
 * $RCSfile: RewardManagerProxy.java,v $
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
 * Protection proxy for RewardManager objects.
 */
public class RewardManagerProxy implements RewardManager{

    protected RewardManager rewardManager;
    protected Authorization authorization;
    protected ForumPermissions permissions;

    public RewardManagerProxy(RewardManager rewardManager,
            Authorization authorization, ForumPermissions permissions)
    {
        this.rewardManager = rewardManager;
        this.authorization = authorization;
        this.permissions = permissions;
    }

    public int getMaxPoints() {
        return rewardManager.getMaxPoints();
    }

    public void setMaxPoints(int numPoints) throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            rewardManager.setMaxPoints(numPoints);
        }
    }

    public void transferPoints(ForumThread thread, int numPoints)
            throws UnauthorizedException, RewardException
    {
        User user = thread.getRootMessage().getUser();
        if ( permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
            (user != null && user.getID() == authorization.getUserID()) )
        {
            rewardManager.transferPoints(thread, numPoints);
        }
    }

    public void rewardPoints(ForumMessage message, int numPoints)
            throws UnauthorizedException, RewardException
    {
        User user = message.getForumThread().getRootMessage().getUser();
        if ( permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
            (user != null && user.getID() == authorization.getUserID()) )
        {
            rewardManager.rewardPoints(message, numPoints);
        }
    }

    public int getPoints(ForumThread thread) {
        return rewardManager.getPoints(thread);
    }

    public int getPoints(ForumMessage message) {
        return rewardManager.getPoints(message);
    }

    public int getCurrentPoints(User user) {
        return rewardManager.getCurrentPoints(user);
    }

    public Iterator userRewardThreads(User user) {
        return new IteratorProxy(JiveGlobals.THREAD,
                rewardManager.userRewardThreads(user), authorization, permissions);
    }

    public int getTotalPointsRewarded(User user) {
        return rewardManager.getTotalPointsRewarded(user);
    }

    public int getTotalPointsEarned(User user) {
        return rewardManager.getTotalPointsEarned(user);
    }

    public void addPoints(User user, int numPoints)
            throws UnauthorizedException, RewardException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            rewardManager.addPoints(user, numPoints);
        }
    }
}