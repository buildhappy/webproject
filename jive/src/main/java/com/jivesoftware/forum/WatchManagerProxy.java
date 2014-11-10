/**
 * $RCSfile: WatchManagerProxy.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:48 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import java.util.Iterator;

/**
 * Protection proxy for the WatchManager interface.
 */
public class WatchManagerProxy implements WatchManager {

    protected WatchManager watchManager;
    protected Authorization authorization;
    protected ForumPermissions permissions;

    public WatchManagerProxy(WatchManager watchManager,
            Authorization authorization, ForumPermissions permissions)
    {
        this.watchManager = watchManager;
        this.authorization = authorization;
        this.permissions = permissions;
    }

    public int getDeleteDays() {
        return watchManager.getDeleteDays();
    }

    public void setDeleteDays(int deleteDays) throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            watchManager.setDeleteDays(deleteDays);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public boolean isEmailNotifyEnabled() throws UnauthorizedException {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            return watchManager.isEmailNotifyEnabled();
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void setEmailNotifyEnabled(boolean enabled) throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            watchManager.setEmailNotifyEnabled(enabled);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public String getEmailBody() throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            return watchManager.getEmailBody();
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void setEmailBody(String body) throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            watchManager.setEmailBody(body);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public String getEmailSubject() throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            return watchManager.getEmailSubject();
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void setEmailSubject(String subject) throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            watchManager.setEmailSubject(subject);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public String getEmailFromName() throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            return watchManager.getEmailFromName();
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void setEmailFromName(String fromName) throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            watchManager.setEmailFromName(fromName);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public String getEmailFromEmail() throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            return watchManager.getEmailFromEmail();
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void setEmailFromEmail(String fromEmail) throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN)) {
            watchManager.setEmailFromEmail(fromEmail);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void createWatch(User user, ForumThread thread, int watchType)
            throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
            authorization.getUserID() == user.getID())
        {
            watchManager.createWatch(user, thread, watchType);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void deleteWatch(User user, ForumThread thread, int watchType)
            throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
            authorization.getUserID() == user.getID())
        {
            watchManager.deleteWatch(user, thread, watchType);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public boolean isWatchExpirable(User user, ForumThread thread, int watchType)
            throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
            authorization.getUserID() == user.getID())
        {
            return watchManager.isWatchExpirable(user, thread, watchType);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void setWatchExpirable(User user, ForumThread thread, int watchType,
            boolean expirable) throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
            authorization.getUserID() == user.getID())
        {
            watchManager.setWatchExpirable(user, thread, watchType, expirable);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public Iterator getWatchedForumThreads(Forum forum, User user, int watchType)
            throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
            authorization.getUserID() == user.getID())
        {
            return watchManager.getWatchedForumThreads(forum, user, watchType);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public Iterator getWatchedForumThreads(User user, int watchType)
            throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
            authorization.getUserID() == user.getID())
        {
            return watchManager.getWatchedForumThreads(user, watchType);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public int getWatchCount(User user, int watchType)
            throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
            authorization.getUserID() == user.getID())
        {
            return watchManager.getWatchCount(user, watchType);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public int getWatchCount(Forum forum, User user, int watchType)
            throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
            authorization.getUserID() == user.getID())
        {
            return watchManager.getWatchCount(forum, user, watchType);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public boolean isWatchedThread(User user, ForumThread thread, int watchType)
            throws UnauthorizedException
    {
        if (permissions.get(ForumPermissions.SYSTEM_ADMIN) ||
            authorization.getUserID() == user.getID())
        {
            return watchManager.isWatchedThread(user, thread, watchType);
        }
        else {
            throw new UnauthorizedException();
        }
    }
}