/**
 * $RCSfile: ForumMessageFilter.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:43 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import java.util.*;
import java.util.Date;

/**
 * A filter acts as a layer between a forum skin and a real ForumMessage
 * object. It provides extended functionality by dynamically reformatting
 * ForumMessage contents.<p>
 *
 * Each filter can override the getBody(), getSubject(), and getProperty(String)
 * methods from the ForumMessage interface. Each filter must
 * also specify whether it is cacheable. It is recommended that
 * each filter provide through the <i>BeanInfo</i> interface a
 * <code>displayName</code>, <code>shortDescription</code>, <code>version</code>
 * and <code>author</code>. These values can then be internationalized as
 * necessary using ResourceBundles. For example, say you create a filter
 * <tt>com.foo.SampleFilter</tt>. You should also create a class named
 * <tt>com.foo.SampleFilterBeanInfo</tt> that extends the <tt>JiveBeanInfo</tt>
 * class. Then, create a resource bundle named <tt>bean_SampleFilter.properties</tt>
 * and other property files for various locales as desired.<p>
 *
 * Filters have 0 or more properties that can be configured by an administrator.
 * Each property may control any aspect of how the filter works -- for example,
 * a property named "color" could control which color to format text in messages.
 * All properties are configurable through the standard JavaBeans conventions.
 * You should specify exactly which properties can be controlled by an
 * administrator by using the <i>BeanInfo</i> interface and <tt>JiveBeanInfo</tt>
 * class described above.<p>
 *
 * An unlimited number of ForumMessageFilters can be specified to be applied
 * per Forum. Global filters can also be specified, which will apply to all
 * messages (and before local filters are applied). Object-oriented designers
 * will recognize this class as a Decorator for ForumMessages.
 *
 * @see ForumMessage
 */
public abstract class ForumMessageFilter implements ForumMessage {

    /**
     * The underlying message the filter is applied to.
     */
    protected ForumMessage message = null;

    /**
     * Clones a new filter that will have the same properties and that
     * will wrap around the specified message.
     *
     * @param message the ForumMessage to wrap the new filter around.
     */
    public abstract ForumMessageFilter clone(ForumMessage message);

    /**
     * Returns true if the filter is cacheable. The filter is cacheable if and
     * only if the output of the filter is constant over time and is only based
     * on the values of the message being filtered. Filters that are cacheable
     * are generally preferred, since not cacheing can dramatically affect
     * performance.<p>
     *
     * For example, a filter that replaces the tag <code>[date]</code> in the
     * body of a message is not cacheable because the output of the filter
     * varies from day to day.<p>
     *
     * It should be noted that filters must NOT internally cache their output
     * since they may be participating with other filters that are not cacheable.
     * All caching will be performed by the Jive system.
     *
     * @return true if the filter is cacheable.
     */
    public abstract boolean isCacheable();

    //FROM THE FORUMMESSAGE INTERFACE//

    public final long getID() {
        return message.getID();
    }

    public final Date getCreationDate() {
        return message.getCreationDate();
    }

    public final void setCreationDate(Date creationDate)
            throws UnauthorizedException
    {
        message.setCreationDate(creationDate);
    }

    public final Date getModifiedDate() {
        return message.getModifiedDate();
    }

    public final void setModifiedDate(Date modifiedDate)
            throws UnauthorizedException
    {
        message.setModifiedDate(modifiedDate);
    }

    public String getSubject() {
        return message.getSubject();
    }

    public final String getUnfilteredSubject() {
        return message.getUnfilteredSubject();
    }

    public final void setSubject(String subject) throws UnauthorizedException {
        message.setSubject(subject);
    }

    public String getBody() {
        return message.getBody();
    }

    public final String getUnfilteredBody() {
        return message.getUnfilteredBody();
    }

    public final void setBody(String body) throws UnauthorizedException {
        this.message.setBody(body);
    }

    public final User getUser() {
        return message.getUser();
    }

    public final int getModerationValue() {
        return message.getModerationValue();
    }

    public final void setModerationValue(int value, Authorization auth)
            throws UnauthorizedException
    {
        message.setModerationValue(value, auth);
    }

    public String getProperty(String name) {
        return message.getProperty(name);
    }

    public final String getUnfilteredProperty(String name) {
        return message.getUnfilteredProperty(name);
    }

    public final void setProperty(String name, String value)
            throws UnauthorizedException
    {
        message.setProperty(name, value);
    }

    public final void deleteProperty(String name) throws UnauthorizedException {
        message.deleteProperty(name);
    }

    public final Iterator propertyNames() {
        return message.propertyNames();
    }

    public final boolean isAnonymous() {
        return message.isAnonymous();
    }

    public final ForumThread getForumThread() {
        return message.getForumThread();
    }

    public final boolean hasPermission(int type) {
        return message.hasPermission(type);
    }

    //OTHER METHODS//

    public String toString() {
        return message.toString();
    }
}