/**
 * $RCSfile: DatabaseObjectIterator.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:49 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import java.util.Iterator;
import com.jivesoftware.forum.*;

/**
 * An class that defines the logic to iterate through an array of long unique
 * ID's of Jive objects.<p>
 *
 * One feature of the class is the ability to recover from underlying
 * modifications to the dataset in some cases. Consider the following sequence
 * of events:
 * <ul>
 *      <li> Time 00: An Iterator for messages in a thread is obtained.
 *      <li> Time 01: 3 of the 8 messages in the thread are deleted.
 *      <li> Time 02: Iteration of messages begins.
 * </ul>
 *
 * In the above example, the underlying messages in the thread were changed
 * after the initial iterator was obtained. The logic in this class will
 * attempt to automatically compensate for these changes by skipping over items
 * that cannot be loaded. In the above example, that would translate to the
 * iterator returning 5 messages instead of 8.
 */
public class DatabaseObjectIterator implements Iterator {

    private long [] elements;
    private int currentIndex = -1;
    private Object nextElement = null;

    private DatabaseObjectFactory objectFactory;

    /**
     * Creates a new DatabaseObjectIterator. The type must be one of the
     * following: <ul>
     *      <li> JiveGlobals.FORUM
     *      <li> JiveGlobals.THREAD
     *      <li> JiveGlobals.MESSAGE
     *      <li> JiveGlobals.USER
     *      <li> JiveGlobals.GROUP
     * </ul>
     *
     * Additionally, each type of iterator requires an extra object of a
     * certain type to perform iteration. In respective order for each
     * <tt>type</tt>, <tt>extraObject</tt> should be a ForumFactory, Forum,
     * ForumThread, UserManager, or GroupManager. If <tt>type</tt> and <tt>
     * extraObject</tt> do not match, iteration construction will fail. <p>
     *
     * The implementation of this method takes the type and extraObject and
     * creates anonymous inner classes to handle loading of each Jive object.
     */
    public DatabaseObjectIterator(int type, long [] elements,
            final Object extraObject)
    {
        this.elements = elements;

        // Load the appropriate proxy factory depending on the type of object
        // that we're iterating through.
        switch (type) {
            // FORUM
            case JiveGlobals.FORUM:
                // Create an objectFactory to load forums.
                this.objectFactory = new DatabaseObjectFactory() {
                    ForumFactory factory = (ForumFactory)extraObject;

                    public Object loadObject(long id) {
                        try {
                            Forum forum = factory.getForum(id);
                            return forum;
                        }
                        catch (ForumNotFoundException mnfe) { }
                        catch (UnauthorizedException ue) { }
                        return null;
                    }
                };
                break;
            //THREAD
            case JiveGlobals.THREAD:
                // Create an objectFactory to load threads. There are two
                // possibilities -- the first is that extraObject is a
                // Forum that we can directly load threads from. The
                // second is that extraObject is a DbForumFactory. The latter
                // case can occur when we need to iterate through a set of
                // threads that come from multiple different forums.
                if (extraObject instanceof Forum) {
                    this.objectFactory = new DatabaseObjectFactory() {
                        Forum forum = (Forum)extraObject;

                        public Object loadObject(long id) {
                            try {
                                ForumThread thread = forum.getThread(id);
                                return thread;
                            }
                            catch (ForumThreadNotFoundException tnfe) { }
                            return null;
                        }
                    };
                }
                else {
                    this.objectFactory = new DatabaseObjectFactory() {
                        DbForumFactory factory = (DbForumFactory)extraObject;

                        public Object loadObject(long id) {
                            try {
                                return factory.cacheManager.threadCache.get(id);
                            }
                            catch (ForumThreadNotFoundException mnfe) { }
                            return null;
                        }
                    };
                }
                break;
            //MESSAGE
            case JiveGlobals.MESSAGE:
                // Create an objectFactory to load messages. There are two
                // possibilities -- the first is that extraObject is a
                // ForumThread that we can directly load messages from. The
                // second is that extraObject is a DbForumFactory. The latter
                // case can occur when we need to iterate through a set of
                // messages that come from multiple different threads.
                if (extraObject instanceof ForumThread) {
                    this.objectFactory = new DatabaseObjectFactory() {
                        ForumThread thread = (ForumThread)extraObject;

                        public Object loadObject(long id) {
                            try {
                                ForumMessage message = thread.getMessage(id);
                                return message;
                            }
                            catch (ForumMessageNotFoundException mnfe) { }
                            return null;
                        }
                    };
                }
                else {
                    this.objectFactory = new DatabaseObjectFactory() {
                        DbForumFactory factory = (DbForumFactory)extraObject;

                        public Object loadObject(long id) {
                            try {
                                ForumMessage message =
                                        factory.cacheManager.messageCache.get(id);
                                // Now, get the message from its thread so that
                                // filters are applied to the message.
                                message = message.getForumThread().getMessage(id);
                                return message;
                            }
                            catch (ForumMessageNotFoundException mnfe) { }
                            return null;
                        }
                    };
                }
                break;
            //USER
            case JiveGlobals.USER:
                // Create an objectFactory to load users.
                this.objectFactory = new DatabaseObjectFactory() {
                    UserManager userManager = (UserManager)extraObject;

                    public Object loadObject(long id) {
                        try {
                            User user = userManager.getUser(id);
                            return user;
                        }
                        catch (UserNotFoundException unfe) { }
                        return null;
                    }
                };
                break;
            //GROUP
            case JiveGlobals.GROUP:
                // Create an objectFactory to load groups.
                this.objectFactory = new DatabaseObjectFactory() {
                    GroupManager groupManager = (GroupManager)extraObject;

                    public Object loadObject(long id) {
                        try {
                            Group group = groupManager.getGroup(id);
                            return group;
                        }
                        catch (GroupNotFoundException gnfe) { }
                        return null;
                    }
                };
                break;
            // Otherwise, an invalid value was passed in so throw an exception.
            default:
                throw new IllegalArgumentException("Illegal type specified");
        }
    }

    /**
     * Returns true if there are more elements in the iteration.
     *
     * @return true if the iterator has more elements.
     */
    public boolean hasNext() {
        // If we are at the end of the list, there can't be any more elements
        // to iterate through.
        if (currentIndex+1 >= elements.length && nextElement == null) {
            return false;
        }
        // Otherwise, see if nextElement is null. If so, try to load the next
        // element to make sure it exists.
        if (nextElement == null) {
            nextElement = getNextElement();
            if (nextElement == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the next element.
     *
     * @return the next element.
     * @throws NoSuchElementException if there are no more elements.
     */
    public Object next() throws java.util.NoSuchElementException {
        Object element = null;
        if (nextElement != null) {
            element = nextElement;
            nextElement = null;
        }
        else {
            element = getNextElement();
            if (element == null) {
                throw new java.util.NoSuchElementException();
            }
        }
        return element;
    }

    /**
     * Not supported for security reasons.
     */
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the next available element, or null if there are no more
     * elements to return.
     *
     * @return the next available element.
     */
    public Object getNextElement() {
        while (currentIndex+1 < elements.length) {
            currentIndex++;
            Object element = objectFactory.loadObject(elements[currentIndex]);
            if (element != null) {
                return element;
            }
        }
        return null;
    }
}

/**
 * An interface for loading Jive database objects.
 */
interface DatabaseObjectFactory {

    /**
     * Returns the object associated with <code>id</code> or null if the
     * object could not be loaded.
     *
     * @param id the id of the object to load.
     * @return the object specified by <code>id</code> or null if it could not
     *      be loaded.
     */
    public Object loadObject(long id);
}