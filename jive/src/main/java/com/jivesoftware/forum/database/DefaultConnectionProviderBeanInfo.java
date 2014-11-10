/**
 * $RCSfile: DefaultConnectionProviderBeanInfo.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:00 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import com.jivesoftware.forum.util.*;

/**
 * BeanInfo class for the DefaultConnectionProvider class.
 */
public class DefaultConnectionProviderBeanInfo extends JiveBeanInfo {

    public static final String [] PROPERTY_NAMES = {
        "driver",
        "serverURL",
        "username",
        "password",
        "minConnections",
        "maxConnections",
        "connectionTimeout"
    };

    public DefaultConnectionProviderBeanInfo() {
        super();
    }

    public Class getBeanClass() {
        return com.jivesoftware.forum.database.DefaultConnectionProvider.class;
    }

    public String [] getPropertyNames() {
        return PROPERTY_NAMES;
    }

    public String getName() {
        return "DefaultConnectionProvider";
    }
}