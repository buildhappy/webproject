/**
 * $RCSfile: DummyFilter.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:03 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.filter;

import com.jivesoftware.forum.*;

/**
 * A very simple filter that pretends to be uncacheable. Only useful for testing.
 */
public class DummyFilter extends ForumMessageFilter {

    public ForumMessageFilter clone(ForumMessage message) {
        DummyFilter filter = new DummyFilter();
        filter.message = message;
        return filter;
    }

    public boolean isCacheable() {
        return false;
    }
}