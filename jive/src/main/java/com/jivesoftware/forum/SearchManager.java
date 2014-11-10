/**
 * $RCSfile: SearchManager.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:46 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import java.util.Date;

/**
 * Manages the search feature of Jive. It defines properties of the Jive indexer
 * for searching. Indexing can either be done real-time by calling
 * indexMessage() every time a new message is created, or by running the indexer
 * in timed update mode. The latter mode is probably preferable since it allows
 * for faster message posting.<p>
 *
 * The automated updating mode can be adjusted by setting how often batch
 * indexing is done. You can adjust this interval to suit the needs of your
 * forums. Frequent updates mean that messages will be searchable more quickly.
 * Less frequent updates use fewer system resources.<p>
 *
 * If you are not using the Jive search system at all (for example, if you have
 * your own search technology that you'd rather use), you should disable the
 * search feature entirely. Doing this may speed up many operations throughout
 * Jive.<p>
 *
 * The following Jive properties correspond to the search manager settings:<ul>
 *
 *      <li> <tt>search.enabled</tt> -- <tt>true</tt> or <tt>false</tt> to
 *              indicated if search should be enabled or disabled. When
 *              disabled, add and delete operations will be ignored, and auto-
 *              indexing is turned off. If this propety is not set, search
 *              is enabled by default.
 *      <li> <tt>search.autoIndexEnabled</tt> -- <tt>true</tt> or <tt>false</tt>
 *              to indicate if auto-indexing of messages is enabled. When
 *              enabled, new messages will automatically be added to the index
 *              at the specified time interval. If this property is not set,
 *              auto indexing is enabled by default.
 *      <li> <tt>search.autoIndexInterval</tt> -- value in minutes between
 *              automatic updates of the search index if auto indexing is
 *              enabled. If this property is not set, the default is 10 minutes.
 *      <li> <tt>search.lastIndexed</tt> -- a timestamp in milleseconds that
 *              indicates the last time the search index was updated. This value
 *              is automatically maintained by this class as the index is
 *              modified.
 * </ul>
 *
 * @see ForumFactory#getSearchManager()
 */
public interface SearchManager {

    /**
     * Returns true if the search feature is turned on. When search is disabled,
     * other methods serve as no-ops and auto indexing will not occur.
     *
     * @return true if the search is enabled.
     */
    public boolean isSearchEnabled();

    /**
     * Enables or disables the search feature. When search is disabled,
     * other methods serve as no-ops and auto indexing will not occur.
     *
     * @param searchEnabled true to enable the search feature, false to disable.
     */
    public void setSearchEnabled(boolean searchEnabled);

    /**
     * Returns true if the search manager is currently busy with an search
     * indexing task. When busy, calls to index operations such as
     * {@link #rebuildIndex()} will do nothing. If you'd like to query the status
     * of the indexing operation while the manager is busy, use the
     * {@link #getPercentComplete()} method.
     *
     * @return true if the search manager is currently busy with an index
     *      maintenance task.
     */
    public boolean isBusy();

    /**
     * Returns the percent complete that an indexing operation is if an
     * indexing operation is currently active (i.e., {@link #isBusy()} returns
     * true). Valid percentages returned are from 0 to 100. If no indexing
     * operation is active, this method will return -1.
     *
     * @return the percent complete that the currently active index operation is.
     */
    public int getPercentComplete();

    /**
     * Returns true if auto indexing is turned on. When auto indexing is on, it
     * will update the search index at the interval specified by the
     * setUpdateInterval method.
     *
     * @return true if auto indexing is turned on.
     */
    public boolean isAutoIndexEnabled();

    /**
     * Enables or disables auto indexing. When auto indexing is on, it
     * will update the search index at the interval specified by the
     * setUpdateInterval method.
     *
     * @param value true to turn auto indexing on, false to turn it off.
     */
    public void setAutoIndexEnabled(boolean value);

    /**
     * Returns the number of minutes that the indexer waits between each update.
     *
     * @return the number of hours between automatic index updates.
     */
    public int getAutoIndexInterval();

    /**
     * Sets the amount of time that indexer should wait between updating the
     * index.
     *
     * @param minutes the number of minutes between automatic index updates.
     */
    public void setAutoIndexInterval(int minutes);

    /**
     * Returns the date that the last update to the index was made.
     */
    public Date getLastIndexedDate();

    /**
     * Adds an individual message to the index. This method is useful for doing
     * real-time indexing. However, for maximum posting speed this method can
     * be ignored. In that case, the automatic indexer will pick up all new
     * messages at the next index interval.
     *
     * @param message the me
     * ssage to add to the index.
     */
    public void addToIndex(ForumMessage message);

    /**
     * Removes an individual message from the index.
     *
     * @param message the message to remove from the index.
     */
    public void removeFromIndex(ForumMessage message);

    /**
     * Manually update the index to include all new messages since the last
     * update.
     */
    public void updateIndex();

    /**
     * Manually rebuild the entire index. This operation can be slow if the
     * index is large.
     */
    public void rebuildIndex();

    /**
     * Optimizes the underlying search index for maximum speed. This operation
     * may be CPU intensive so should be performed relatively infrequently.
     * Rebuilding an index should automatically optimize it.
     */
    public void optimize();
}