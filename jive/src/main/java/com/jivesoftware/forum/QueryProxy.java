/**
 * $RCSfile: QueryProxy.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:45 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import java.util.*;

/**
 * Protection proxy for the query class.
 */
public class QueryProxy implements Query {

    /**
     * Query object that we are proxying.
     */
    private Query query;
    private Authorization authorization;
    private ForumPermissions permissions;

    public QueryProxy(Query query, Authorization authorization,
            ForumPermissions permissions)
    {
        this.query = query;
        this.authorization = authorization;
        this.permissions = permissions;
    }

    public String getQueryString() {
        return query.getQueryString();
    }

    public void setQueryString(String queryString) {
        query.setQueryString(queryString);
    }

    public Date getBeforeDate() {
        return query.getBeforeDate();
    }

    public void setBeforeDate(Date beforeDate) {
        query.setBeforeDate(beforeDate);
    }

    public Date getAfterDate() {
        return query.getAfterDate();
    }

    public void setAfterDate(Date afterDate) {
        query.setAfterDate(afterDate);
    }

    public void filterOnUser(User user) {
        query.filterOnUser(user);
    }

    public User getFilteredUser() {
        return query.getFilteredUser();
    }

    public ForumThread getFilteredThread() {
        return query.getFilteredThread();
    }

    public void filterOnThread(ForumThread thread) {
        query.filterOnThread(thread);
    }

    public int resultCount() {
        return query.resultCount();
    }

    public Iterator results() {
        Iterator iterator = query.results();
        return new IteratorProxy(JiveGlobals.MESSAGE, iterator, authorization,
                permissions);
    }

    public Iterator results(int startIndex, int numResults) {
        Iterator iterator = query.results(startIndex, numResults);
        return new IteratorProxy(JiveGlobals.MESSAGE, iterator, authorization,
                permissions);
    }
}
