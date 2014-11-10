/**
 * $RCSfile: ResultFilter.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:46 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import java.util.*;

/**
 * Filters and sorts lists of threads and messages. This allows for a very
 * rich set of possible queries that can be run on forum data. Some examples
 * are: "Show all messages posted in the forum during the last year by a
 * certain user" or "Show all threads in the forum, sorted by their modification
 * date".<p>
 *
 * The class also supports pagination of results with the setStartIndex(int)
 * and setNumResults(int) methods. If the start index is not set, it will
 * begin at index 0 (the start of results). If the number of results is not set,
 * it will be unbounded and return as many results as available.<p>
 *
 * By default, result filters will obey the moderation rules as they are set
 * for each forum. You can override this behavior by setting a moderation range.
 * <p>
 *
 * Factory methods to create common queries are provided for convenience.
 *
 * @see Forum#threads(ResultFilter)
 * @see Forum#messages(ResultFilter)
 * @see ForumThread#messages(ResultFilter)
 * @see JiveGlobals
 */
public class ResultFilter {

    /**
     * Descending sort, i.e. 3, 2, 1...
     */
    public static final int DESCENDING = 0;

    /**
     * Ascending sort, i.e. 3, 4, 5...
     */
    public static final int ASCENDING = 1;

    /**
     * An integer value that represents NULL. The actual value is
     * Integer.MAX_VALUE - 123 (an arbitrary number that has a very low
     * probability of actually being selected by a user as a valid value).
     */
    public static final int NULL_INT = Integer.MAX_VALUE - 123;

    /**
     * Creates a default thread ResultFilter: no filtering with results sorted
     * on the thread modification date.
     */
    public static ResultFilter createDefaultThreadFilter() {
        ResultFilter resultFilter = new ResultFilter();
        return resultFilter;
    }

    /**
     * Creates a default message ResultFilter: no filtering with results sorted
     * on the message creation date.
     */
    public static ResultFilter createDefaultMessageFilter() {
        ResultFilter resultFilter = new ResultFilter();
        resultFilter.setSortField(JiveGlobals.CREATION_DATE);
        resultFilter.setSortOrder(ASCENDING);
        return resultFilter;
    }

    private int sortField = JiveGlobals.MODIFIED_DATE;
    private int sortOrder = DESCENDING;
    private String sortPropertyName = null;

    /**
     * The starting index for results. Default is 0.
     */
    private int startIndex = 0;

    /**
     * Number of results to return. Default is NULL_INT which means an unlimited
     * number of results.
     */
    private int numResults = NULL_INT;

    private long userID = NULL_INT;
    private List propertyNames = new ArrayList();
    private List propertyValues = new ArrayList();
    private Date creationDateRangeMin = null;
    private Date creationDateRangeMax = null;
    private Date modifiedDateRangeMin = null;
    private Date modifiedDateRangeMax = null;

    private int moderationRangeMin = NULL_INT;
    private int moderationRangeMax = NULL_INT;

    /**
     * Returns the userID that results will be filtered on. The method will
     * return NULL_INT if no user to filter on has been specified. The method
     * will return -1 if filtering is to take place on all "anonymous" users.
     *
     * @return the userID that results will be filtered on.
     */
    public long getUserID() {
        return userID;
    }

    /**
     * Sets the userID that results will be filtered on. If you'd like to filter
     * on "anonymous" users, pass in an id of -1. By default, no filtering on
     * userID's will take place. If you'd like to change so that no filtering
     * is performed, pass in ResultFilter.NULL_INT.
     *
     * @param userID the user ID to filter on.
     */
    public void setUserID(long userID) {
        this.userID = userID;
    }

    /**
     * Adds a property to the list of properties that will be filtered on.
     * For a message or thread to pass the property filter:<ul>
     *      <li> The message or thread must have a property with the same name
     *              as the filter.
     *      <li> The property value in the thread or message must exactly match
     *              the property value of the filter.
     * </ul>
     *
     * For example, say that we have a message with extended properties "color"
     * and "size" with the values "green" and "big". If we create a result
     * filter and specify that we should filter on "color=green" and
     * "size=small", the hypothetical message will be filtered out since the
     * size property values don't match.
     *
     * @param name the name of the property to filter on.
     * @param value the value of the property that results must match.
     */
    public void addProperty(String name, String value) {
        propertyNames.add(name);
        propertyValues.add(value);
    }

    /**
     * Returns the number of properties that results will be filtered on.
     *
     * @return the number of properties that results will be filtered on.
     */
    public int getPropertyCount() {
        return propertyNames.size();
    }

    /**
     * Returns the name of the property at the specified index in the list of
     * properties to be filtered on. If the index is invalid, null will be
     * returned.
     *
     * @return the name of the property at the specified index in the property
     *      filter list.
     */
    public String getPropertyName(int index) {
        if (index >= 0 && index < propertyNames.size()) {
            return (String)propertyNames.get(index);
        }
        else {
            return null;
        }
    }

    /**
     * Returns the value of the property at the specified index in the list of
     * properties to be filtered on. If the index is invalid, null will be
     * returned.
     *
     * @return the value of the property at the specified index in the property
     *      filter list.
     */
    public String getPropertyValue(int index) {
        if (index >= 0 && index < propertyValues.size()) {
            return (String)propertyValues.get(index);
        }
        else {
            return null;
        }
    }

    /**
     * Returns the creation date that represents the lower boundary for messages
     * or threads to be filtered on. If this value has not been set, the method
     * will return null.
     *
     * @return a Date representing the lower bound for creation dates to filter
     *      on.
     */
    public Date getCreationDateRangeMin() {
        return creationDateRangeMin;
    }

    /**
     * Sets the date that represents the lower boundary for messages or threads to
     * be selected by the result filter. If this value is not set the results filter will
     * be unbounded for the earliest creation date selected.
     *
     * @param creationDateRangeMin Date representing the filter lowest value of
     *      the creation date to be selected.
     */
    public void setCreationDateRangeMin(Date creationDateRangeMin) {
        this.creationDateRangeMin = creationDateRangeMin;
    }

    /**
     * Returns a date that represents the upper boundry for messages or threads to
     * be selected by the result filter. If this value is not set it will return null
     * and the results filter will be unbounded for the latest creation date selected.
     *
     * @return a Date representing the filter highest value of the creation date to be
     * selected.
     */
    public Date getCreationDateRangeMax() {
        return creationDateRangeMax;
    }

    /**
     * Sets a date that represents the upper boundry for messages or threads to
     * be selected by the result filter. If this value is not set the results filter will
     * be unbounded for the latest creation date selected.
     *
     * @param creationDateRangeMax Date representing the filter lowest value of
     * the creation date range.
     */
    public void setCreationDateRangeMax(Date creationDateRangeMax) {
        this.creationDateRangeMax = creationDateRangeMax;
    }

    /**
     * Returns a date that represents the lower boundry for messages or threads to
     * be selected by the result filter. If this value is not set it will return null
     * and the results filter will be unbounded for the earliest modified date selected.
     *
     * @return a Date representing the filter lowest value of the modified date range.
     */
    public Date getModifiedDateRangeMin() {
        return modifiedDateRangeMin;
    }

    /**
     * Sets a date that represents the lower boundry for messages or threads to
     * be selected by the result filter. If this value is not set the results filter will
     * be unbounded for the earliest modified date selected.
     *
     * @param modifiedDateRangeMin Date representing the filter lowest value of
     * the modified date to be selected.
     */
    public void setModifiedDateRangeMin(Date modifiedDateRangeMin) {
        this.modifiedDateRangeMin = modifiedDateRangeMin;
    }

    /**
     * Returns a date that represents the upper boundry for messages or threads to
     * be selected by the result filter. If this value is not set it will return null
     * and the results filter will be unbounded for the latest modified date selected.
     *
     * @return a Date representing the filter highest value of the modified date to be
     * selected.
     */
    public Date getModifiedDateRangeMax() {
        return modifiedDateRangeMax;
    }

    /**
     * Sets a date that represents the upper boundry for messages or threads to
     * be selected by the result filter. If this value is not set the results filter will
     * be unbounded for the latest modified date selected.
     *
     * @param modifiedDateRangeMax Date representing the filter lowest value of
     * the modified date range.
     */
    public void setModifiedDateRangeMax(Date modifiedDateRangeMax) {
        this.modifiedDateRangeMax = modifiedDateRangeMax;
    }

    /*
     * Returns the currently selected sort field. Default is
     * JiveGlobals.MODIFIED_DATE.
     *
     * @return current sort field.
     */
    public int getSortField() {
        return sortField;
    }

    /**
     * Sets the sort field to use. Default is JiveGlobals.MODIFIED_DATE .
     * If the sortField is set to JiveGlobals.EXTENDED_PROPERTY, the name of
     * the property must be set by a subsequent call to
     * setSortPropertyName(String).
     *
     * @param sortField the field that will be used for sorting.
     */
    public void setSortField(int sortField) {
        this.sortField = sortField;
    }

    /**
     * Returns the name of the extended property that will be sorted on.
     * Returns null if sorting will not be done on a property.
     *
     * @return the extended property that will be sorted on.
     */
    public String getSortPropertyName() {
        return this.sortPropertyName;
    }

    /**
     * Sets the property name to sort on. You must also call the setSortField(int)
     * method with JiveGlobals.EXTENDED_PROPERTY as an argument.
     *
     * @param propertyName the name of the extended property to sort on.
     */
    public void setSortPropertyName(String sortPropertyName) {
        this.sortPropertyName = sortPropertyName;
    }

    /**
     * Returns the sort order, which will be ResultFilter.ASCENDING for
     * ascending sorting, or ResultFilter.DESCENDING for descending sorting.
     * Descending sorting is: 3, 2, 1, etc. Ascending sorting is 1, 2, 3, etc.
     *
     * @retun the sort order.
     */
    public int getSortOrder() {
        return this.sortOrder;
    }

    /**
     * Sets the sort type. Valid arguments are ResultFilter.ASCENDING for
     * ascending sorting or ResultFilter.DESCENDING for descending sorting.
     * Descending sorting is: 3, 2, 1, etc. Ascending sorting is 1, 2, 3, etc.
     *
     * @param sortOrder the order that results will be sorted in.
     */
    public void setSortOrder(int sortOrder) {
        if (! (sortOrder == ResultFilter.ASCENDING ||
               sortOrder == ResultFilter.DESCENDING)
            )
        {
            throw new IllegalArgumentException();
        }
        this.sortOrder = sortOrder;
    }

    /**
     * Returns the max number of results that should be returned.
     * The default value for is NULL_INT, which means there will be no limit
     * on the number of results. This method can be used in combination with
     * setStartIndex(int) to perform pagination of results.
     *
     * @return the max number of results to return.
     * @see #setStartIndex(int)
     */
    public int getNumResults() {
        return numResults;
    }

    /**
     * Sets the limit on the number of results to be returned.
     *
     * @param numResults the number of results to return.
     */
    public void setNumResults(int numResults) {
        this.numResults = numResults;
    }

    /**
     * Returns the index of the first result to return.
     *
     * @return the index of the first result which should be returned.
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * Sets the index of the first result to return. For example, if the start
     * index is set to 20, the Iterator returned will start at the 20th result
     * in the query. This method can be used in combination with
     * setNumResults(int) to perform pagination of results.
     *
     * @param startIndex the index of the first result to return.
     */
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    /**
     * Returns the moderation value that represents the lower boundry for
     * messages or threads to be selected by the result filter. If this value
     * is not set it will return the lowest moderation value allowed and the
     * results filter will be unbounded for the lowest moderation value selected.
     *
     * For example if boolean moderation is being used, the possible values that
     * can be expected of a message are -1 for rejected messages, 0 unapproved
     * messages, and 1 for approved messages.  To show messages available
     * for the general public, you would pass this accessor a minimum value
     * of 1 and a maximum value of 1 or higher.  To show messages in need of
     * moderation you would pass this accessor a minimum value of 0 and a
     * maximum value of 0.  To show messages in need of moderation in the
     * context of threads that are already approved, you would pass this accessor
     * a minumum value of 0 and a maximum value of 1.
     *
     * @return an int representing the lowest value of the moderation value to be
     * selected by the filter.
     */
    public int getModerationRangeMin() {
        return moderationRangeMin;
    }

    /**
     * Sets the moderation value that represents the lower bound for messages
     * or threads to be selected by the result filter. If this value is not
     * set, the lower bound will default to minimum set for the forum. If you
     * would like the moderation range to have now lower bound, simply pass in
     * a sufficiently small number such as
     *
     * @param moderationRangeMin int representing the lowest value of
     *      the moderation value range to be selected by the result filter.
     */
    public void setModerationRangeMin(int moderationRangeMin) {
        this.moderationRangeMin = moderationRangeMin;
    }

    /**
     * Returns the moderation value that represents the upper bound for
     * messages or threads to be selected by the result filter. If an upper
     * bound has not been set, this method will return ResultFilter.NULL_INT.
     *
     * @return the upper bound for moderation values to filter on.
     */
    public int getModerationRangeMax() {
        return moderationRangeMax;
    }

    /**
     * Sets the upper bound for the moderation value that will pass the
     * result filter. If this value is not set, or is set to
     * ResultFilter.NULL_INT there will be no upper bound on moderation values.
     *
     * @param moderationRangeMax the max value of the moderation value range.
     */
    public void setModerationRangeMax(int moderationRangeMax) {
        this.moderationRangeMax = moderationRangeMax;
    }
}