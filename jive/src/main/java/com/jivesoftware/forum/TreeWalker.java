/**
 * $RCSfile: TreeWalker.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:46 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import java.util.Iterator;

/**
 * Allows hierarchical navigation of a Thread. It closely follows the
 * TreeModel interface in Swing in the hopes of being easier to use.
 *
 * @see ForumThread
 */
public interface TreeWalker {

    /**
     * Returns the root of the tree. Returns null only if the tree has no nodes.
     *
     * @return the root of the tree
     */
    public ForumMessage getRoot();

    /**
     * Returns the parent of the <code>child</code> ForumMessage.
     *
     * @throws ForumMessageNotFoundException if child does not have a parent,
     *      or if the parent message could not be loaded for any other reason.
     * @return the parent message of child.
     */
    public ForumMessage getParent(ForumMessage child)
            throws ForumMessageNotFoundException;

    /**
     * Returns the child of parent at index index in the parent's child array.
     * The index must be a valid one, that is: <code>index</code> >= 0, and
     * <code>index</code> < getChildCount(parent). If the index is not valid,
     * or if the child could not be loaded for any other reason, a
     * ForumMessageNotFoundException will be thrown.
     *
     * @param parent the parent message.
     * @param index the index of the child.
     * @throws ForumMessageNotFoundException if the index was invalid or the
     *      child could not be loaded for any other reason.
     * @return the child of parent at index.
     */
    public ForumMessage getChild(ForumMessage parent, int index)
            throws ForumMessageNotFoundException;

    /**
     * Returns an Iterator for all the child messages of the parent. This
     * method only considers direct descendants of the parent message and not
     * sub-children. To get an iterator for the full hierarchy of children for
     * a parent message, use the recursiveChildren(ForumMessage) method.
     *
     * @param parent the parent message.
     * @return an Iterator for the children of parent.
     */
    public Iterator children(ForumMessage parent);

    /**
     * Returns an Iterator for all child messages (and sub-children, etc) of
     * the parent. Messages will be returned depth-first. For example, consider
     * the following message tree:
     *
     * <pre>
     *   1
     *   |-- 3
     *   |-- |-- 4
     *   |-- |-- |-- 7
     *   |-- |-- |-- |-- 10
     *   |-- |-- 6
     *   |-- |-- 8
     *   |-- 5
     * </pre>
     *
     * Calling recursiveChildren(3) on the tree above would return the sequence
     * 4, 7, 10, 6, 8. This method is a powerful way to show all children of
     * a message, especially in combination with the
     * {@link #getMessageDepth(ForumMessage)} method.
     *
     * @param parent the parent message.
     * @return an Iterator for all the recursive children of the parent.
     */
    public Iterator recursiveChildren(ForumMessage parent);

    /**
     * Returns the depth of a message in the message tree hierarchy. The root
     * message is always at depth 0. For example, consider the following
     * message tree:
     *
     * <pre>
     *   1
     *   |-- 3
     *   |-- |-- 4
     *   |-- |-- |-- 7
     * </pre>
     *
     * The depth of message 4 is 2, the depth of message 7 is 3, etc. This
     * method is useful in combination with the {@link #recursiveChildren(ForumMessage)}
     * Iterator to build a UI of hierarchical messages.
     *
     * @param message the message to determine the depth of.
     * @return the depth of the message in the message tree hiearchy.
     */
    public int getMessageDepth(ForumMessage message);

    /**
     * Returns the number of children of parent. Returns 0 if the node is a
     * leaf or if it has no children.
     *
     * @param parent a node in the tree, obtained from this data source.
     * @return the number of children of the node parent.
     */
    public int getChildCount(ForumMessage parent);

    /**
     * Returns the total number of recursive children of a parent. Returns 0
     * if there are no children. This method is not intended to aid in
     * navigation of a Thread but is included as an added utility.
     *
     * @return the recursive count of the children of <code>parent</code>.
     */
    public int getRecursiveChildCount(ForumMessage parent);

    /**
     * Returns the index of child in parent. For example, take the following
     * tree:
     *
     * <pre>
     *   4
     *   |-- 2
     *   |-- |-- 1
     *   |-- |-- 6
     *   |-- |-- 8
     *   |-- 5
     * </pre>
     *
     * In this example, getIndexOfChild(4, 2) would return 0,
     * getIndexOfChild(4, 5) would return 1, and
     * getIndexOfChild(2, 8) would return 2.
     *
     * @param parent the parent message.
     * @param child the child message to get the index for.
     * @return the index of child in the list of siblings under parent.
     */
    public int getIndexOfChild(ForumMessage parent, ForumMessage child);

    /**
     * Returns true if node is a leaf. A node is a leaf when it has no children
     * messages.
     *
     * @param node a node in the tree, obtained from this data source
     * @return true if node is a leaf
     */
    public boolean isLeaf(ForumMessage node);
}