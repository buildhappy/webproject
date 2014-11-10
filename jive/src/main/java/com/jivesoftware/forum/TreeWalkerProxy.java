/**
 * $RCSfile: TreeWalkerProxy.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:47 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import java.util.Iterator;

/**
 * Protection proxy for TreeWalker ojbects
 */
public class TreeWalkerProxy implements TreeWalker{

    private TreeWalker treeWalker;
    private Authorization authorization;
    private ForumPermissions permissions;

    public TreeWalkerProxy(TreeWalker treeWalker, Authorization authorization,
            ForumPermissions permissions)
    {
        this.treeWalker = treeWalker;
        this.authorization = authorization;
        this.permissions = permissions;
    }

    public ForumMessage getRoot() {
        ForumMessage message = treeWalker.getRoot();
        return new ForumMessageProxy(message, authorization, permissions);
    }

    public ForumMessage getParent(ForumMessage child)
            throws ForumMessageNotFoundException
    {
        ForumMessage message = treeWalker.getParent(child);
        return new ForumMessageProxy(message, authorization, permissions);
    }

    public ForumMessage getChild(ForumMessage parent, int index)
            throws ForumMessageNotFoundException
    {
        ForumMessage message = treeWalker.getChild(parent, index);
        return new ForumMessageProxy(message, authorization, permissions);
    }

    public Iterator children(ForumMessage parent) {
        return new IteratorProxy(JiveGlobals.MESSAGE, treeWalker.children(parent),
                authorization, permissions);
    }

    public Iterator recursiveChildren(ForumMessage parent) {
        return new IteratorProxy(JiveGlobals.MESSAGE,
                treeWalker.recursiveChildren(parent), authorization, permissions);
    }

    public int getMessageDepth(ForumMessage message) {
        return treeWalker.getMessageDepth(message);
    }

    public int getChildCount(ForumMessage parent) {
        return treeWalker.getChildCount(parent);
    }

    public int getRecursiveChildCount(ForumMessage parent) {
        return treeWalker.getRecursiveChildCount(parent);
    }

    public int getIndexOfChild(ForumMessage parent, ForumMessage child) {
        return treeWalker.getIndexOfChild(parent, child);
    }

    public boolean isLeaf(ForumMessage node) {
        return treeWalker.isLeaf(node);
    }

}
