/**
 * $RCSfile: Query.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:45 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import java.util.Date;
import java.util.Iterator;

/**
 * Encapsulates a search for content in a forum. Use the factory method
 * forum.createQuery() to get a handle on a Query object. From there, set
 * the properties that you're interested in searching on. For example, to
 * search a forum for "Jive is cool", you would use the following code:<p>
 *
 * <pre>
 * Query query = forum.createQuery();
 * query.setQueryString("Jive is cool");
 * Iterator iter = query.results();
 * while (iter.hasNext()) {
 *     ForumMessage message = (ForumMessage)iter.nextElement();
 *     //print results...
 * }
 * </pre><p>
 *
 * All search properties are optional. You can mix and match them depending on
 * what kind of query you'd like to perform.<p>
 *
 * You can also use the filter methods to restrict searches to messages
 * from a particular user, messages between a date range, or messages in a
 * particular thread.
 */
public interface Query {

    /**
     * Returns the query string for the Query object. If the query string has
     * not been set, this method will return null.
     *
     * @return the Query query string.
     */
    public String getQueryString();

    /**
     * Sets the query string for the Query object.
     *
     * @param queryString a new query string.
     */
    public void setQueryString(String queryString);

    /**
     * Returns the latest date for search results. For example, the "before date"
     * can be used to search for messages modified more than 1 month ago.
     * <p>
     * If the "before date" has not been set, this method will return null.
     *
     * @return the upder date boundary for search results.
     */
    public Date getBeforeDate();

    /**
     * Sets the latest date for search results. For example, the "before date"
     * can be used to search for messages modified more than 1 month ago.
     *
     * @param beforeDate an upper date boundary for search results.
     */
    public void setBeforeDate(Date beforeDate);

    /**
     * Returns the earliest date for search results. For example, the "after date"
     * can be used to search for messages modified within the last week.<p>
     *
     * If the "after date" has not been set, this method will return null.
     *
     * @return the lower date boundary for search results.
     */
    public Date getAfterDate();

    /**
     * Sets the earliest date for search results. For example, the "after date"
     * can be used to search for messages modified within the last week.
     *
     * @param afterDate a lower date boundary for search results.
     */
    public void setAfterDate(Date afterDate);

    /**
     * Returns the user that query results are restricted to. If the query
     * is not restricted to messages posted by a certain user, this method will
     * return null.
     *
     * @return the message that results are restricted to.
     */
    public User getFilteredUser();

    /**
     * Restricts the query results to messages posted by a specified user. Note:
     * this method is not intended to show all messages posted by a user.
     * Rather, it lets you filter out search results; for example, all messages
     * matching the query "Jive rocks", but filtering out all results that
     * aren't posted by some particular user. If you just want to see all
     * messages posted by a user regardless of their actual content, use the
     * ResultFilter class for a particular forum, or UserManager.userMessages(User)
     * for a user's messages among all forums.
     *
     * @param user a User to restrict query results to.
     * @see ResultFilter
     * @see UserManager
     */
    public void filterOnUser(User user);

    /**
     * Returns the thread that query results are restricted to. If the query
     * is not restricted to messages in a certain thread, this method will
     * return null.
     *
     * @return the thread that results are restricted to.
     */
    public ForumThread getFilteredThread();

    /**
     * Restricts the querty results to messages posted in a specified thread.
     *
     * @param thread the ForumThread to restrict query results to.
     */
    public void filterOnThread(ForumThread thread);

    /**
     * Returns the total number of results of the query.
     *
     * @return the number of results of the query.
     */
    public int resultCount();

    /**
     * Returns the results of the Query as an Iterator of ForumMessage objects.
     *
     * @return the result of the query as an Iterator.
     */
    public Iterator results();

    /**
     * Returns the results of the Query as an Iterator of ForumMessage objects.
     * The startIndex and numResults paramaters are used to look at a certain
     * range of the results. For example, the first twenty results, the second
     * twenty results, etc. This is useful for user interface with multiple
     * pages of results.<p>
     *
     * If startIndex or numResults does not fall within the range of results,
     * the number of messages returned may be smaller than expected. For
     * example, suppose a query has a total of 17 results. If startIndex
     * is 0 and numResults is 25, only 17 results can be returned.
     *
     * @param startIndex the index in the results that the iterator will start at.
     * @param numResuls the max number of results that should be returned.
     * @return the result of the query as an Iterator.
     */
    public Iterator results(int startIndex, int numResults);
}