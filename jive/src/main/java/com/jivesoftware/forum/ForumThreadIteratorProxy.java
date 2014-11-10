package com.jivesoftware.forum;

public class ForumThreadIteratorProxy extends ForumThreadIterator {

    ForumThreadIterator iterator;
    Authorization authorization;
    ForumPermissions permissions;

    public ForumThreadIteratorProxy(ForumThreadIterator iterator, Authorization
            authorization, ForumPermissions permissions)
    {
        this.iterator = iterator;
        this.authorization = authorization;
        this.permissions = permissions;
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Object next() {
        return new ForumThreadProxy((ForumThread)iterator.next(), authorization,
            permissions);
    }

    public boolean hasPrevious() {
        return iterator.hasPrevious();
    }

    public Object previous() {
        return new ForumThreadProxy((ForumThread)iterator.previous(), authorization,
            permissions);
    }

    public void setIndex(ForumThread thread) {
        iterator.setIndex(thread);
    }
}