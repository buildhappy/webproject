/**
 * $RCSfile: DummyFilterBeanInfo.java,v $
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
import com.jivesoftware.forum.*;

import java.beans.*;
import java.util.*;

/**
 * BeanInfo class for the Dummy filter.
 */
public class DummyFilterBeanInfo extends JiveBeanInfo {

    private static final String [] PROPERTY_NAMES = new String[0];

    public DummyFilterBeanInfo() {
        super();
    }

    public Class getBeanClass() {
        return com.jivesoftware.forum.filter.DummyFilter.class;
    }

    public String [] getPropertyNames() {
        return PROPERTY_NAMES;
    }

    public String getName() {
        return "Dummy Filter";
    }
}