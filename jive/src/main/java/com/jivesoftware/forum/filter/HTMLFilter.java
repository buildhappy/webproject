/**
 * $RCSfile: HTMLFilter.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:03 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.filter;

import java.util.*;

import com.jivesoftware.forum.*;
import com.jivesoftware.util.*;

/**
 * A ForumMessageFilter that converts newline characters into HTML &lt;br&gt; tags.
 * This filter should only be run after any HTML stripping filters.
 */
public class HTMLFilter extends ForumMessageFilter {

    /**
     * Clones a new filter that will have the same properties and that
     * will wrap around the specified message.
     *
     * @param message the ForumMessage to wrap the new filter around.
     */
    public ForumMessageFilter clone(ForumMessage message){
        HTMLFilter filter = new HTMLFilter();
        filter.message = message;
        return filter;
    }

    public boolean isCacheable() {
        return true;
    }

    /**
     * <b>Overloaded</b> to return the subject of the message with HTML tags
     * escaped.
     */
    public String getSubject() {
        return StringUtils.escapeHTMLTags(message.getSubject());
    }

    /**
     * <b>Overloaded</b> to return the body of the message with HTML tags
     * escaped.
     */
    public String getBody() {
        return StringUtils.escapeHTMLTags(message.getBody());
    }
}