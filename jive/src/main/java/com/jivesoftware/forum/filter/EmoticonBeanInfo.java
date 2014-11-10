/**
 * $RCSfile: EmoticonBeanInfo.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:03 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.filter;

import com.jivesoftware.forum.util.*;

/**
 * BeanInfo class for the Emoticon filter.
 */
public class EmoticonBeanInfo extends JiveBeanInfo {

    public static final String [] PROPERTY_NAMES = {
        "imageURL",
        "filteringSubject",
        "filteringBody"
    };

    public EmoticonBeanInfo() {
        super();
    }

    public Class getBeanClass() {
        return com.jivesoftware.forum.filter.Emoticon.class;
    }

    public String [] getPropertyNames() {
        return PROPERTY_NAMES;
    }

    public String getName() {
        return "Emoticon";
    }
}
