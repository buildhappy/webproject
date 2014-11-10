/**
 * $RCSfile: GroupCache.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:01 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import com.jivesoftware.util.*;
import com.jivesoftware.forum.*;

public class GroupCache extends DatabaseCache {

    private Cache groupIDCache = new Cache(128*1024, 6*JiveGlobals.HOUR);

    public GroupCache(LongCache cache, DbForumFactory forumFactory) {
        super(cache, forumFactory);
    }

    public DbGroup get(long groupID) throws GroupNotFoundException {
        if (!isEnabled()) {
            return new DbGroup(groupID, factory);
        }
        //Cache is enabled.
        DbGroup group = (DbGroup)cache.get(groupID);
        if (group == null) {
            group = new DbGroup(groupID, factory);
            cache.add(groupID, group);
        }
        return group;
    }

    public DbGroup get(String name) throws GroupNotFoundException {
        if (!isEnabled()) {
            return new DbGroup(name, null, factory);
        }
        //Cache is enabled.
        CacheableLong groupIDLong = (CacheableLong)groupIDCache.get(name);
        //if id wan't found in cache, load it up and put it there.
        if (groupIDLong == null) {
            Group group = new DbGroup(name, null, factory);
            groupIDLong = new CacheableLong(group.getID());
            groupIDCache.add(name, groupIDLong);
        }
        return get(groupIDLong.getLong());
    }

    public void remove(long key) {
        try {
            Group group = get(key);
            String name = group.getName();
            groupIDCache.remove(name);
        }
        catch (Exception e) { }
        super.remove(key);
    }
}