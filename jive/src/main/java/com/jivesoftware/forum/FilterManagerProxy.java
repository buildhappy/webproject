/**
 * $RCSfile: FilterManagerProxy.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:41 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

/**
 * A protection proxy for FilterManagers. A proxy has a set of permissions that
 * are specified at creation time of the proxy. Subsequently, those permissions
 * are use to restrict access to protected methods. If a user does
 * not have the right to execute a particular method, an UnauthorizedException
 * is thrown.
 *
 * @see FilterManager
 */
public class FilterManagerProxy implements FilterManager {

    private FilterManager filterManager;
    private Authorization authorization;
    private ForumPermissions permissions;

    /**
     * Creates a new filterManagerProxy object.
     *
     * @param filterManager the filter manager to protect by proxy
     * @param authorization the user's authorization token
     * @param permissions the permissions to use with this proxy.
     */
    public FilterManagerProxy(FilterManager filterManager,
            Authorization authorization, ForumPermissions permissions)
    {
        this.filterManager = filterManager;
        this.authorization = authorization;
        this.permissions = permissions;
    }

    //FROM THE FORUM INTERFACE//

    public ForumMessageFilter getFilter(int index)
            throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin()) {
            return filterManager.getFilter(index);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public int getFilterCount() throws UnauthorizedException {
        if (permissions.isSystemOrForumAdmin()) {
            return filterManager.getFilterCount();
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void addFilter(ForumMessageFilter filter)
            throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin()) {
            filterManager.addFilter(filter);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void addFilter(ForumMessageFilter filter, int index)
            throws UnauthorizedException
    {
        if (permissions.isSystemOrForumAdmin()) {
            filterManager.addFilter(filter, index);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void removeFilter(int index) throws UnauthorizedException {
        if (permissions.isSystemOrForumAdmin()) {
            filterManager.removeFilter(index);
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public void saveFilters() throws UnauthorizedException {
        if (permissions.isSystemOrForumAdmin()) {
            filterManager.saveFilters();
        }
        else {
            throw new UnauthorizedException();
        }
    }

    public ForumMessage applyFilters(ForumMessage message) {
        return filterManager.applyFilters(message);
    }

    public ForumMessage applyCacheableFilters(ForumMessage message) {
        return filterManager.applyCacheableFilters(message);
    }

    public ForumMessage applyUncacheableFilters(ForumMessage message) {
        return filterManager.applyUncacheableFilters(message);
    }

    public boolean hasUncacheableFilters() {
        return filterManager.hasUncacheableFilters();
    }
}