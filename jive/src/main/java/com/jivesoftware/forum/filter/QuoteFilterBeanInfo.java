/**
 * $RCSfile: QuoteFilterBeanInfo.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:06 $
 *
 * Copyright (C) 1999-2002 CoolServlets, Inc. All rights reserved.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.filter;

import java.beans.*;
import com.jivesoftware.forum.util.JiveBeanInfo;

/**
 * BeanInfo class for the QuoteFilter filter.
 */
public class QuoteFilterBeanInfo extends JiveBeanInfo {

    public static final String [] PROPERTY_NAMES = {
        "quoteChar",
        "quoteColors",
        "reformatTextEnabled"
    };

    public QuoteFilterBeanInfo() {
        super();
    }

    public Class getBeanClass() {
        return com.jivesoftware.forum.filter.QuoteFilter.class;
    }

    public String [] getPropertyNames() {
        return PROPERTY_NAMES;
    }

    public String getName() {
        return "QuoteFilter";
    }
}