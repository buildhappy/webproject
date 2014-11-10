/**
 * $RCSfile: URLConverterBeanInfo.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:06 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.filter;

import java.beans.*;
import com.jivesoftware.forum.util.JiveBeanInfo;

/**
 * BeanInfo class for the URLConverter filter.
 */
public class URLConverterBeanInfo extends JiveBeanInfo {

    public static final String [] PROPERTY_NAMES = {
        "filteringSubject",
        "filteringBody",
        "newWindowEnabled"
    };

    public URLConverterBeanInfo() {
        super();
    }

    public Class getBeanClass() {
        return com.jivesoftware.forum.filter.URLConverter.class;
    }

    public String [] getPropertyNames() {
        return PROPERTY_NAMES;
    }

    public String getName() {
        return "URLConverter";
    }
}