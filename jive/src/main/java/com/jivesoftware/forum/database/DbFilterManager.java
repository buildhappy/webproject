/**
 * $RCSfile: DbFilterManager.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:51 $
 *
 * Copyright (C) 1999-2002 CoolServlets, Inc. All rights reserved.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import com.jivesoftware.util.*;
import com.jivesoftware.forum.*;
import com.jivesoftware.forum.util.*;

import java.util.*;
import java.io.*;

import org.jdom.*;
import org.jdom.output.*;

/**
 * Manages filters for a given context (global filters, or filters local to
 * a single forum). Filters are persisted to a a file called
 * <tt>jive_filters.xml</tt>
 *
 * @see FilterManager
 * @author Matt Tucker
 */
public class DbFilterManager implements FilterManager {

    /**
     * The list of the class names of the filters that are available for
     * installation by default. These values are automatically added into
     * the <tt>jive_filters.xml</tt> file when it is first created. Further
     * classes can be defined by editing the <tt>jiveFilters.filterClasses</tt>
     * property.
     */
    public static final String [] DEFAULT_FILTER_CLASSES = new String [] {
            "com.jivesoftware.forum.filter.HTMLFilter",
            "com.jivesoftware.forum.filter.Newline",
            "com.jivesoftware.forum.filter.TextStyle",
            "com.jivesoftware.forum.filter.URLConverter",
            "com.jivesoftware.forum.filter.Profanity",
            "com.jivesoftware.forum.filter.JavaCodeHighlighter",
            "com.jivesoftware.forum.filter.WordBreak",
            "com.jivesoftware.forum.filter.QuoteFilter",
            "com.jivesoftware.forum.filter.Emoticon",
            "com.jivesoftware.forum.filter.ImageFilter"
        };

    private static XMLProperties properties = null;
    private static ForumMessageFilter [] availableFilters = null;

    private ForumMessageFilter [] filters;
    private int uncacheableIndex = -1;

    private DbForumFactory factory;
    String context = null;

    /**
     * Creates a new filter manager.
     *
     * @param forumID the forumID to manage filters on, or -1 to manage
     *      global filters.
     * @param factory a forum factory to use for various tasks.
     */
    public DbFilterManager(long forumID, DbForumFactory factory) {
        this.factory = factory;
        String name = null;
        if (forumID == -1) {
            name = "global";
        }
        else {
            name = "forum" + forumID;
        }
        // Make sure properties are loaded.
        loadProperties();

        // Now load up filters for this manager.
        context = name + ".";
        // See if a record for this context exists yet. If not, create one.
        String fCount = properties.getProperty(context + "filterCount");
        if (fCount == null) {
            fCount = "0";
        }
        int filterCount = 0;
        try {
            filterCount = Integer.parseInt(fCount);
        }
        catch (NumberFormatException nfe) { }

        // Load up all filters
        filters = new ForumMessageFilter[filterCount];
        for (int i=0; i<filterCount; i++) {
            try {
                String filterContext = context + "filter" + i + ".";
                String className = properties.getProperty(filterContext + "className");
                filters[i] = (ForumMessageFilter)Class.forName(className).newInstance();
                // If this filter isn't cacheable, then no further filters can
                // be cached.
                if (!filters[i].isCacheable()) {
                     uncacheableIndex = i;
                }
                // Load filter properties.
                String [] propNames = properties.getChildrenProperties(
                        filterContext + "properties");
                Map filterProps = new HashMap();
                for (int j=0; j<propNames.length; j++) {
                    // Get the bean property name, which is everything after
                    // the last '.' in the xml property name.
                    filterProps.put(propNames[j], properties.getProperty(
                            filterContext + "properties." + propNames[j]));
                }
                // Set properties on the bean
                BeanUtils.setProperties(filters[i], filterProps);
            }
            catch (Exception e) {
                System.err.println("Error loading filter " + i + " for context "
                        + context);
                e.printStackTrace();
            }
        }
    }

    public ForumMessageFilter getFilter(int index) {
        if (index < 0 || index > filters.length-1) {
            throw new IllegalArgumentException("Index " + index + " is not valid.");
        }
        return filters[index];
    }

    public int getFilterCount() {
        return filters.length;
    }

    public void addFilter(ForumMessageFilter filter) {
        addFilter(filter, filters.length);
    }

    public void addFilter(ForumMessageFilter filter, int index) {
        ArrayList newFilters = new ArrayList(filters.length+1);
        for (int i=0; i<filters.length; i++) {
            newFilters.add(filters[i]);
        }
        newFilters.add(index, filter);
        ForumMessageFilter[] newArray = new ForumMessageFilter[newFilters.size()];
        for (int i=0; i<newArray.length; i++) {
            newArray[i] = (ForumMessageFilter)newFilters.get(i);
        }
        // Loop through the array to find the uncacheable index.
        uncacheableIndex = -1;
        for (int i=0; i<newArray.length; i++) {
            if (!newArray[i].isCacheable()) {
                uncacheableIndex = i;
                break;
            }
        }
        // Finally, overwrite filters with the new array
        filters = newArray;
        saveFilters();
    }

    public synchronized void saveFilters() {
        // Delete all filters from context.
        properties.deleteProperty(context.substring(0, context.length()-1));
        if (filters.length > 0) {
            properties.setProperty(context + "filterCount",
                    Integer.toString(filters.length));
        }
        // Now write them out again.
        for (int i=0; i<filters.length; i++) {
            String filterContext = context + "filter" + i + ".";
            // Write out class name.
            properties.setProperty(filterContext+"className",
                    filters[i].getClass().getName());
            // Write out all properties.
            Map filterProps = BeanUtils.getProperties(filters[i]);
            for (Iterator iter = filterProps.keySet().iterator(); iter.hasNext(); ) {
                String name = (String)iter.next();
                String value = (String)filterProps.get(name);
                properties.setProperty(filterContext + "properties." + name, value);
            }
        }

        // Finally, clear the message cache. This is necessary to erase cached
        // filter results.
        factory.cacheManager.messageCache.clear();
    }

    public void removeFilter(int index)  {
        ArrayList newFilters = new ArrayList(filters.length);
        for (int i=0; i<filters.length; i++) {
            newFilters.add(filters[i]);
        }
        newFilters.remove(index);
        ForumMessageFilter[] newArray = new ForumMessageFilter[newFilters.size()];
        for (int i=0; i<newArray.length; i++) {
            newArray[i] = (ForumMessageFilter)newFilters.get(i);
        }
        // Loop through the array to find the uncacheable index.
        uncacheableIndex = -1;
        for (int i=0; i<newArray.length; i++) {
            if (!newArray[i].isCacheable()) {
                uncacheableIndex = i;
                break;
            }
        }
        // Finally, overwrite filters with the new array
        filters = newArray;
        saveFilters();
    }

    public ForumMessage applyFilters(ForumMessage message) {
        // Loop through cacheable filters and apply them
        for (int i=0; i < filters.length; i++) {
            if (filters[i] != null) {
                message = filters[i].clone(message);
            }
        }
        return message;
    }

    public ForumMessage applyCacheableFilters(ForumMessage message) {
        if (uncacheableIndex == -1) {
            return applyFilters(message);
        }
        else {
            // Loop through cacheable filters and apply them
            for (int i=0; i < uncacheableIndex; i++) {
                if (filters[i] != null) {
                    message = filters[i].clone(message);
                }
            }
            return message;
        }
    }

    public ForumMessage applyUncacheableFilters(ForumMessage message) {
        if (uncacheableIndex == -1) {
            return message;
        }
        else {
            // Loop through uncacheable filters and apply them
            for (int i=uncacheableIndex; i < filters.length; i++) {
                if (filters[i] != null) {
                    message = filters[i].clone(message);
                }
            }
            return message;
        }
    }

    public boolean hasUncacheableFilters() {
        return uncacheableIndex != -1;
    }

    static ForumMessageFilter [] getAvailableFilters() {
        if (availableFilters == null) {
            // Load filter classes
            String [] classNames = properties.getChildrenProperties("filterClasses");
            List filterList = new ArrayList();
            for (int i=0; i<classNames.length; i++) {
                try {
                    String className = properties.getProperty(
                            "filterClasses." + classNames[i]);
                    Class filterClass = Class.forName(className);
                    // Attempt a cast. If it fails, we'll skip this class.
                    ForumMessageFilter filter =
                            (ForumMessageFilter)filterClass.newInstance();
                    filterList.add(filter);
                }
                catch (Exception e) {  }
            }
            availableFilters = new ForumMessageFilter[filterList.size()];
            for (int i=0; i<availableFilters.length; i++) {
                availableFilters[i] = (ForumMessageFilter)filterList.get(i);
            }
        }
        return availableFilters;
    }

    public static synchronized void addFilterClass(String className)
            throws ClassNotFoundException, IllegalArgumentException
    {
        // Try to load class. If found in classpath and is a filter, add it to
        // the list
        Class newClass = Class.forName(className);
        try {
            Object newFilter = newClass.newInstance();
            if (!(newFilter instanceof ForumMessageFilter)) {
                throw new IllegalArgumentException("Class is not a ForumMessageFilter");
            }
            ForumMessageFilter [] newFilters =
                    new ForumMessageFilter[availableFilters.length + 1];
            for (int i=0; i<newFilters.length-1; i++) {
                newFilters[i] = availableFilters[i];
            }
            newFilters[newFilters.length -1] = (ForumMessageFilter)newFilter;
            availableFilters = newFilters;
            // Write out new classes names.
            properties.deleteProperty("filterClasses");
            for (int i=0; i<availableFilters.length; i++) {
                String cName = availableFilters[i].getClass().getName();
                properties.setProperty("filterClasses.filter" + i, cName);
            }
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        catch (InstantiationException e2) {
            throw new IllegalArgumentException(e2.getMessage());
        }
    }

    /**
     * Loads a property manager for filter persistence if it isn't already
     * loaded. If an XML file for the filters isn't already created, it will
     * attempt to make a file with default value.
     */
    private static void loadProperties() {
        if (properties == null) {
            String filterXML = JiveGlobals.getJiveHome() + File.separator +
                "jive_filters.xml";
            // Make sure the file actually exists. If it doesn't, a new file
            // will be created.
            File file = new File(filterXML);
            // If it doesn't exists we have to create it.
            if (!file.exists()) {
                Document doc = new Document(new Element("jiveFilters"));
                Element filterClasses = new Element("filterClasses");
                doc.getRootElement().addContent(filterClasses);
                // Add in default list of available filter classes.
                for (int i=0; i<DEFAULT_FILTER_CLASSES.length; i++) {
                    Element newClass = new Element("filter" + i);
                    newClass.setText(DEFAULT_FILTER_CLASSES[i]);
                    filterClasses.addContent(newClass);
                }
                // Set default global filters -- HTML and Newline
                Element defaultFilters = new Element("global");
                doc.getRootElement().addContent(defaultFilters);
                defaultFilters.addContent(new Element("filterCount").addContent("3"));
                Element filter0 = new Element("filter0");
                filter0.addContent(new Element("className").addContent(
                        "com.jivesoftware.forum.filter.HTMLFilter"));
                Element filter1 = new Element("filter1");
                filter1.addContent(new Element("className").addContent(
                        "com.jivesoftware.forum.filter.TextStyle"));
                Element filter2 = new Element("filter2");
                filter2.addContent(new Element("className").addContent(
                        "com.jivesoftware.forum.filter.Newline"));
                defaultFilters.addContent(filter0);
                defaultFilters.addContent(filter1);
                defaultFilters.addContent(filter2);
                // Now, write out to the file.
                OutputStream out = null;
                try {
                    // Use JDOM's XMLOutputter to do the writing and formatting.
                    XMLOutputter outputter = new XMLOutputter();
                    out = new BufferedOutputStream(new FileOutputStream(file));
                    outputter.output(doc, out);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    try {  out.close();  }
                    catch (Exception e) { }
                }
            }
            // Finally, create xml properties with the file.
            properties = new XMLProperties(filterXML);
        }
    }
}