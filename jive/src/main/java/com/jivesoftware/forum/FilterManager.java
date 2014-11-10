/**
 * $RCSfile: FilterManager.java,v $
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
 * Manages message filters at either the global or forum level. Every message
 * object returned to the Jive forum API user is wrapped in a number of
 * filters; first, all global filters, and then the filters for the message's
 * forum.
 *
 * The default implementation of this interface stores filter configuration
 * properties in a file called <tt>filter_config.xml</tt> under the
 * </tt>jiveHome</tt> directory.
 *
 * @see ForumMessageFilter
 */
public interface FilterManager {

    /**
     * Returns the ForumMessageFilter at the specified index.
     *
     * @return the filter at the specified index.
     * @throws UnauthorizedException if not an admin.
     */
    public ForumMessageFilter getFilter(int index) throws UnauthorizedException;

    /**
     * Returns the count of currently active filters for the forum.
     *
     * @return a count of the currently active filters.
     * @throws UnauthorizedException if not an admin.
     */
    public int getFilterCount() throws UnauthorizedException;

    /**
     * Adds a new ForumMessageFilter to the end of the filter list.
     *
     * @param filter ForumMessageFilter to add to the filter list.
     * @throws UnauthorizedException if not an admin.
     */
    public void addFilter(ForumMessageFilter filter) throws UnauthorizedException;

    /**
     * Inserts a new ForumMessageFilter at specified index in the filter list.
     *
     * @param filter ForumMessageFilter to add to the filter list.
     * @param index position in filter list to insert new filter.
     * @throws UnauthorizedException if not an admin.
     */
    public void addFilter(ForumMessageFilter filter, int index)
            throws UnauthorizedException;

    /**
     * Removes a ForumMessageFilter at the specified index in the filter list.
     *
     * @param index position in filter list to remove filter from.
     * @throws UnauthorizedException if not an admin.
     */
    public void removeFilter(int index) throws UnauthorizedException;

    /**
     * Saves all ForumMessageFilters to the persistent store. This method
     * should be called after setting any properties on invidual filters
     * that are being managed by this filter manager. Warning: calling this
     * method will expire the entire message cache so that cache consistency
     * is maintained.
     *
     * @throws UnauthorizedException if not an admin.
     */
    public void saveFilters() throws UnauthorizedException;

    /**
     * Applies all defined filters to the specified message. This is useful
     * for cases such as message preview feature, when the user desires to see
     * what a message might look like after having filters applied, but before
     * the message is actually added to a forum.
     *
     * @param message the message to apply the filters to.
     * @return a message with all filters applied.
     */
    public ForumMessage applyFilters(ForumMessage message);

    /**
     * Applies only filters that are cacheable to the specified message. This
     * method can be generally ignored since Jive uses it internally to
     * implement filter output caching.
     *
     * @param message the message to apply cacheable filters to.
     * @return a message with cacheable filters applied.
     */
    public ForumMessage applyCacheableFilters(ForumMessage message);

    /**
     * Applies only filters that are uncacheable to the specified message. This
     * method can be generally ignored since Jive uses it internally to
     * implement filter output caching.
     *
     * @param message the message to apply uncacheable filters to.
     * @return a message with uncacheable filters applied.
     */
    public ForumMessage applyUncacheableFilters(ForumMessage message);

    /**
     * Returns true if there are any uncacheable filters currently installed.
     *
     * @return true if there are any uncacheable filters.
     */
    public boolean hasUncacheableFilters();
}