/**
 * $RCSfile: ProfanityBeanInfo.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:05 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.filter;

import com.jivesoftware.forum.util.JiveBeanInfo;

/**
 * BeanInfo class for the Profanity filter.
 */
public class ProfanityBeanInfo extends JiveBeanInfo {

    public static final String [] PROPERTY_NAMES = {
        "wordList",
        "ignoringCase"
    };

    public ProfanityBeanInfo() {
        super();
    }

    public Class getBeanClass() {
        return com.jivesoftware.forum.filter.Profanity.class;
    }

    public String [] getPropertyNames() {
        return PROPERTY_NAMES;
    }

    public String getName() {
        return "Profanity";
    }
}