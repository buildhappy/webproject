/**
 * $RCSfile: WordBreakBeanInfo.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:06 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.filter;

import com.jivesoftware.forum.util.*;
import com.jivesoftware.forum.*;

import java.beans.*;
import java.util.*;

/**
 * BeanInfo class for the Word Break filter.
 */
public class WordBreakBeanInfo extends JiveBeanInfo {

    private static final String [] PROPERTY_NAMES = {
        "maxSubjectWordLength",
        "maxBodyWordLength",
        "filteringSubject",
        "filteringBody"
    };

    public WordBreakBeanInfo() {
        super();
    }

    public Class getBeanClass() {
        return com.jivesoftware.forum.filter.WordBreak.class;
    }

    public String [] getPropertyNames() {
        return PROPERTY_NAMES;
    }

    public String getName() {
        return "WordBreak";
    }
}