/**
 * $RCSfile: SkinUtils.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:08 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.util;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.jivesoftware.forum.*;
import com.jivesoftware.util.*;

/**
 * A collection of utility methods for use in Jive Skins. Because these
 * methods make skin development much easier, skin authors should study them
 * carefully.<p>
 *
 * Three major areas of funtionality are provided:<p><ol>
 *  <li> Methods that simplify Authorization tasks:
 *    <ul>
 *      <li>{@link #login(HttpServletRequest, HttpServletResponse, String, String, boolean)}
 *      <li>{@link #getUserAuthorization(HttpServletRequest, HttpServletResponse)}
 *      <li>{@link #removeUserAuthorization(HttpServletRequest, HttpServletResponse)}
 *    </ul>
 *    <p>
 *  <li> Methods that get and set Session and cookie values.
 *    <ul>
 *      <li>{@link #getCookie(HttpServletRequest, String)}
 *      <li>{@link #remove(HttpServletRequest, HttpServletResponse, String)}
 *      <li>{@link #retrieve(HttpServletRequest, HttpServletResponse, String)}
 *      <li>{@link #store(HttpServletRequest, HttpServletResponse, String, String)}
 *      <li>{@link #store(HttpServletRequest, HttpServletResponse, String, String, int)}
 *      <li>{@link #store(HttpServletRequest, HttpServletResponse, String, String, int boolean)}
 *    </ul>
 *    <p>
 *  <li> Date methods.
 *    <ul>
 *      <li>{@link #dateToText(HttpServletRequest, HttpServletResponse, User, Date)}
 *      <li>{@link #formatDate(HttpServletRequest, HttpServletResponse, User, Date)}
 *      <li>{@link #getLastVisited(HttpServletRequest, HttpServletResponse)}
 *    </ul>
 *  <li> Other methods.
 *    <ul>
 *      <li>{@link #quoteOriginal(String, String, int)}
 *      <li>{@link #getResourceBundle(String, Locale)}
 *    </ul>
 * </ol>
 *
 */
public class SkinUtils {

    /** Name of the authentication token (stored in the user's session) */
    private static final String JIVE_AUTH_TOKEN = "jive.authorization.token";

    /** Name of the cookie used to store user info for auto-login purposes */
    private static final String JIVE_AUTOLOGIN_COOKIE = "jive.authorization.autologin";

    /** Name of the last visited token (stored in the user's session) */
    private static final String JIVE_LASTVISITED_TOKEN = "jive.user.lastvisited";

    // Default cookie time to live (in seconds).
    private static final int MAX_COOKIE_AGE = (int)(JiveGlobals.WEEK / 1000) * 8;

    // Days of the week
    private static final String[] DAYS_OF_WEEK =
            {"Sun","Mon","Tues","Wed","Thurs","Fri","Sat"};
    // Months of the year
    private static final String[] MONTHS_OF_YEAR =
            {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug",
             "Sep","Oct","Nov","Dec"};

    // "Tweakable" parameters for the cookie password encoding. NOTE: changing
    // these and recompiling this class will essentially invalidate old cookies.
    private final static int  ENCODE_XORMASK = 0x5A;
    private final static char ENCODE_DELIMETER = '\002';
    private final static char ENCODE_CHAR_OFFSET1 = 'A';
    private final static char ENCODE_CHAR_OFFSET2 = 'h';

    // A cache of DateFormat objects:
    private static HashMap dateFormatCache = new HashMap();

    // A reuseable global calendar object
    private static Calendar globalCal = Calendar.getInstance();


    /**
     * Returns an Authorization token for the user. The session is first checked
     * and if the token is not found, the Jive cookie is checked. If the cookie
     * is found,
     *
     * @param request the HttpServletRequest object, known as "request" in a
     *      JSP page.
     * @param response The HttpServletResponse object, known as "response" in
     *      a JSP page.
     * @return A users's authorization token if they're already authenticated,
     *      otherwise <code>null</code>.
     */
    public static Authorization getUserAuthorization
            (HttpServletRequest request, HttpServletResponse response)
    {
        HttpSession session = request.getSession();

        // Check 1: check for the Jive authentication token in the user's session.
        Authorization authToken = (Authorization)session.getAttribute(JIVE_AUTH_TOKEN);
        if (authToken != null) {
            return authToken;
        }

        // Check 2: check the jive cookie for username and password
        Cookie cookie = getCookie(request, JIVE_AUTOLOGIN_COOKIE);
        if (cookie != null) {
            try {
                // at this point, we found a cookie so grab the username and
                // password from it, create an authorization token and store
                // that in the session
                String[] values = decodePasswordCookie(cookie.getValue());
                String username = values[0];
                String password = values[1];
                // Try to validate the user based on the info from the cookie.
                // Catch any exceptions
                authToken = AuthorizationFactory.getAuthorization(username,password);
            }
            catch (Exception e) {}

            // put that token in the user's session:
            if (authToken != null) {
                session.setAttribute(JIVE_AUTH_TOKEN, authToken);
            }

            // return the authorization token
            return authToken;
        }
        return null;
    }

    /**
     * Validates the user and optionally enables auto-login by creating an
     * auto-login cookie.
     *
     * @param request the HttpServletRequest object, known as "request" in a JSP page.
     * @param response the HttpServletResponse object, known as "response" in a JSP page.
     * @param username the username.
     * @param password the password.
     * @param autoLogin if <code>true</code> create a cookie that enables auto-login.
     * @throws UserNotFoundException
     * @throws UnauthorizedException
     */
    public static Authorization login(HttpServletRequest request,
            HttpServletResponse response, String username, String password,
            boolean autoLogin) throws UserNotFoundException, UnauthorizedException
    {
        HttpSession session = request.getSession();
        Authorization authToken = AuthorizationFactory.getAuthorization(username, password);
        session.setAttribute(JIVE_AUTH_TOKEN, authToken);

        // If auto-login is enabled, create the auto-login cookie
        if (autoLogin) {
            saveCookie(response,JIVE_AUTOLOGIN_COOKIE,
                    encodePasswordCookie(username,password));
        }
        return authToken;
    }
    public static Authorization setUserAuthorization(HttpServletRequest request,
            HttpServletResponse response, String username, String password,
            boolean autoLogin) throws UserNotFoundException, UnauthorizedException
    {
        return login(request, response, username, password, autoLogin);
    }


    /**
     *  Removes a user's token from the session and invalidates the auto-login
     *  cookie (if one exists).
     *
     *  @param request the HttpServletRequest object; "request" in JSP pages.
     *  @param response the HttpServletResponse object; "response" in JSP pages.
     */
    public static void logout(HttpServletRequest request,
            HttpServletResponse response)
    {
        HttpSession session = request.getSession();
        session.removeAttribute(JIVE_AUTH_TOKEN);
        deleteCookie(request, response, JIVE_AUTOLOGIN_COOKIE);
    }
    public static void removeUserAuthorization(HttpServletRequest request,
            HttpServletResponse response)
    {
        logout(request,response);
    }


    /**
     * Invalidates the specified cookie.
     */
    public static void deleteCookie(HttpServletRequest request,
            HttpServletResponse response, String cookieName)
    {
        // invalidate the cookie
        Cookie cookie = new Cookie(cookieName, "");
        // delete the cookie when the user closes their webbrowser
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     *  Persists a value for the length of the user's session.
     *
     *  @see SkinUtils#store(HttpServletRequest,HttpServletResponse,String,String,int) store
     */
    public static void store(HttpServletRequest request, HttpServletResponse response,
            String id, String value)
    {
        // By default, we'll just store the value in the session (saveTime
        // is zero)
        store(request, response, id, value, 0);
    }

    /**
     *  This method should be used in a skin to store an arbritary value.
     *  For example, we could persist the name of a user so that on a form page
     *  where they enter their name, that field could be auto-filled in with
     *  the stored value.
     *  <p>
     *  To indicate that the data should only be persisted for a session, pass
     *  in 0 as the <code>timeToLive</code>. Otherwise, the value will be
     *  saved for one month.
     *
     *  @param request The HttpServletRequest object, known as "request" on a
     *      JSP page.
     *  @param response The HttpServletRequest object, known as "response" on a
     *      JSP page.
     *  @param id The name or identifier of the data you want to persist.
     *  @param value The value you wish to store.
     *  @param saveTime The length (in seconds) this value will persist. Any
     *      value of 0 or less indicates this data should only persist for
     *      a session.
     */
    public static void store(HttpServletRequest request,
            HttpServletResponse response, String id, String value, int saveTime)
    {
        // If the id is null, return
        if (id == null) {
            return;
        }

        // Get the session object
        HttpSession session = request.getSession();

        // Store the value in the session
        session.setAttribute(id, value);

        // if the timeToLive param is > 0, store to a cookie
        if (saveTime > 0) {
            saveCookie(response, id, value, saveTime);
        }
    }

    /**
     *  Retrieves a user stored value. Values are set using the
     *  <code>store(...)</code> methods. If <code>remove</code> is true, the
     *  value is also removed from persistence.
     *
     *  @param request The HttpServletRequest object, known as "request" on
     *      a JSP page.
     *  @param response The HttpServletRequest object, known as "response" on
     *      a JSP page.
     *  @param id The id or name of the stored value.
     */
    public static String retrieve(HttpServletRequest request,
            HttpServletResponse response, String id)
    {
        // First, check the session.
        HttpSession session = request.getSession();
        String value = (String)session.getAttribute(id);

        // if it's not found, check the cookies
        if (value == null) {
            Cookie cookie = getCookie(request, id);
            if (cookie != null) {
                value = cookie.getValue();
            }
            if (id != null && value != null) {
                session.setAttribute(id, value);
            }
        }
        return value;
    }

    /**
     * Deletes a user stored value. Values are set using the
     * <code>store(...)</code>
     * methods.
     *
     * @param request the HttpServletRequest object, known as "request" on
     *      a JSP page.
     * @param response the HttpServletRequest object, known as "response" on
     *      a JSP page.
     * @param id the id or name of the stored value you wish to remove from persistence.
     */
    public static void delete(HttpServletRequest request,
            HttpServletResponse response, String id)
    {
        // First, remove it from the session:
        HttpSession session = request.getSession();
        session.removeAttribute(id);

        // Invalidate the cookie by setting a null expired cookie in its place
        deleteCookie(request, response, id);
    }
    public static void remove(HttpServletRequest request,
            HttpServletResponse response, String id)
    {
        delete(request,response,id);
    }

    /**
     *
     *
     * @param request the HttpServletRequest object, known as "request" on
     *      a JSP page.
     * @param response the HttpServletRequest object, known as "response" on
     *      a JSP page.
     */
    public static long getLastVisited(HttpServletRequest request,
            HttpServletResponse response)
    {
        // Get session object
        HttpSession session = request.getSession();
        // The current instant in time. We use the CacheTimer field because this
        // method will be called often and this will scale better than calling
        // System.currentTimeMillis().
        long now = CacheTimer.currentTime;

        // First, try to retrieve the value from the session
        String lastTime = (String)session.getAttribute(JIVE_LASTVISITED_TOKEN);

        // Found a value in the session, so return it
        if (lastTime != null) {
            try {
                long time = Long.parseLong(lastTime);
                // update the last visited cookie time to now, but don't update
                // the last visited time in the session:
                saveCookie(response, JIVE_LASTVISITED_TOKEN, Long.toString(now));
                // return the time value
                return time;
            }
            catch(NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // getting to this point means no time value was found in the session,
        // so look for it in the cookie:
        long time = now;
        Cookie cookie = getCookie(request, JIVE_LASTVISITED_TOKEN);
        if (cookie != null) {
            lastTime = cookie.getValue();
        }
        if (lastTime != null) {
            try {
                time = Long.parseLong(lastTime);
            } catch (NumberFormatException e) {}
        }

        // set the value in the session & cookie
        session.setAttribute(JIVE_LASTVISITED_TOKEN, Long.toString(time));
        saveCookie(response, JIVE_LASTVISITED_TOKEN, Long.toString(now));

        // return the time
        return time;
    }

    /**
     * Returns true if the forum has been modified since the specified time.
     *
     * @param forum the forum to check.
     * @param time the time to reference the forum against.
     * @return true if the forum has been modified since the specified time.
     */
    public static boolean isNew(Forum forum, long time)
    {
        return (forum.getModifiedDate().getTime() > time);
    }

    /**
     * Returns true if the forum thread has been modified since the specified
     * time.
     *
     * @param thread the thread to check.
     * @param time the time to reference the thread against.
     * @return true if the thread has been modified since the specified time.
     */
    public static boolean isNew(ForumThread thread, long time)
    {
        return (thread.getModifiedDate().getTime() > time);
    }

    /**
     * Returns true if the forum message has been modified since the specified
     * time.
     *
     * @param message the message to check.
     * @param time the time to reference the message against.
     * @return true if the message has been modified since the specified time.
     */
    public static boolean isNew(ForumMessage message, long time)
    {
        return (message.getModifiedDate().getTime() > time);
    }

    /**
     * Returns the specified cookie, or <code>null</code> if the cookie
     * does not exist.
     *
     * @param request The HttpServletRequest object, known as "request" in a
     *      JSP page.
     * @param name the name of the cookie.
     * @return the Cookie object if it exists, otherwise <code>null</code>.
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie cookies[] = request.getCookies();
        // Return null if there are no cookies or the name is invalid.
        if(cookies == null || name == null || name.length() == 0) {
            return null;
        }
        // Otherwise, we  do a linear scan for the cookie.
        for (int i = 0; i < cookies.length; i++) {
            if(cookies[i].getName().equals(name) ) {
                return cookies[i];
            }
        }
        return null;
    }

    /**
     * Returns the specified resource bundle, which is a properties file
     * that aids in localization of skins. This method is handy since it
     * uses the class loader that other Jive classes are loaded from (hence,
     * it can load bundles that are stored in jive.jar).
     *
     * @param baseName the name of the resource bundle to load.
     * @param locale the desired Locale.
     * @return the specified resource bundle, if it exists.
     */
    public static ResourceBundle getResourceBundle(String baseName,
            Locale locale)
    {
        return ResourceBundle.getBundle(baseName, locale);
    }

    /**
     * Stores a value in a cookie. This cookie will persist for 30 days.
     *
     * @param request The HttpServletResponse object, known as "response" in a
     *      JSP page.
     * @param name a name to identify the cookie
     * @param value the value to store in the cookie
     */
    public static void saveCookie(HttpServletResponse response, String name,
            String value)
    {
        // Save the cookie value for 1 month
        saveCookie(response, name, value, 60*60*24*30);
    }

    /**
     * Stores a value in a cookie. This cookie will persist for the amount
     * specified in the <tt>saveTime</tt> parameter.
     *
     * @param request The HttpServletResponse object, known as "response" in a
     *      JSP page.
     * @param name a name to identify the cookie
     * @param value the value to store in the cookie
     * @param saveTime the time (in seconds) this cookie should live
     */
    public static void saveCookie(HttpServletResponse response, String name,
            String value, int saveTime)
    {
        // Check to make sure the new value is not null (appservers like Tomcat
        // 4 blow up if the value is null).
        if (value == null) {
            value = "";
        }
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(saveTime);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * Formats the unfiltered body of a message to make it appear in the "quote
     * original" format. This is simply the body of the message with the
     * delimiter appended to the beginning of each line. The delimiter
     * is most often "> " by convention. A desired length for each line in the
     * returned String can be specified to aid in formatting.<p>
     *
     * This method uses message.getUnfilteredBody() in order to get the body of
     * the message. This usually yields better results for the formatting
     * required by this method. However, it also has the potential of being
     * a security risk if malicious HTML code is embedded in the body. Therefore,
     * you should always filter HTML from the result of this method before
     * showing it in an environment where HTML is interpreted. If you are
     * showing the results of this method in an HTML &lt;textarea&gt;, there is
     * no need to worry about malicious HTML.
     *
     * @param message the message to quote.
     * @param delimiter a String that will start each line of the quoted
     *      message. For example, "> ";
     * @param lineLength the desired length of each line in the quoted message.
     * @return the unfiltered body of the message in the "quote original" format.
     */
    public static String quoteOriginal(String body, String delimiter,
            int lineLength)
    {
        if (body == null || body.length() == 0) {
            return "";
        }
        if (body.trim().length() == 0) {
            return "";
        }
        int length = body.length();
        //Create a StringBuffer to hold the quoted body; approximate size.
        StringBuffer buf = new StringBuffer(body.length());
        //i maintains the current position in the String.
        for (int i=0; i<length; ) {
            String partialString =
                StringUtils.chopAtWord(
                    body.substring(i),
                    lineLength
                );
            i += partialString.length()+1;
            buf.append(delimiter).append(partialString.trim()).append("\n");
        }
        return buf.toString();
    }

    /**
     * Formats a date for a user, according to their locale and time zone
     * preferences.
     *
     * @param request the servlet request object.
     * @param response the servlet response object.
     * @param user the User that the date is being formatted for.
     * @param date the Date object we're comparing the current time with.
     */
    public static String formatDate(HttpServletRequest request,
            HttpServletResponse response, User user, Date date)
    {


        Locale locale = JiveGlobals.getLocale();
        TimeZone timeZone = getTimeZone(request, response, user);
        // See if the date is today.
        // Check the cache of DateFormat objects:
        String key = locale.toString() + timeZone.getID();
        if (dateFormatCache.containsKey(key)) {
            return ((SimpleDateFormat)dateFormatCache.get(key)).format(date);
        }
        else {
            // Cache miss
//            DateFormat formatter
//                = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT,locale);
//            formatter.setTimeZone(timeZone);
            SimpleDateFormat formatter=new SimpleDateFormat("yyyy��MM��dd�� HH:mm:ss");
            dateFormatCache.put(key,formatter);
            return formatter.format(date);
        }
    }

    /**
     * Returns an English sentence which describes the <code>Date</code>
     * parameter relative to the current time. For instance, if the passed
     * in date was 39 seconds ago, this method returns:<p>
     *     "Less than 1 min ago"<p>
     * Similiarly, a date 1 ago would be:<p>
     *      "Yesterday at 3:53 PM"<p>
     *
     * The method obeys user preferences for time zone, if they exist.
     *
     * @param request the servlet request object.
     * @param response the servlet response object.
     * @param user the User that the date is being formatted for, or null
     *      if an anonymmous user.
     * @param date the Date object we're comparing the current time with.
     */
    public static String dateToText(HttpServletRequest request,
            HttpServletResponse response, User user, Date date)
    {
        if (date == null) {
            return "";
        }
        // Time difference between now and the parameter object's time. Using
        // the cache timer means the resolution is no better than a seconds,
        // for for our purposes, we don't need better.
        long delta = com.jivesoftware.util.CacheTimer.currentTime - date.getTime();

        // Within the last hour
        if ((delta / JiveGlobals.HOUR) < 1) {
            long minutes = (delta/JiveGlobals.MINUTE);
            if (minutes == 0) {
                return "Less than 1 min ago";
            }
            else if (minutes == 1) {
                return "1 minute ago";
            }
            else {
                return (minutes + " minutes ago");
            }
        }

        // Sometime today
        if ((delta / JiveGlobals.DAY) < 1) {
            long hours = (delta/JiveGlobals.HOUR);
            if(hours <= 1) {
                return "1 hour ago";
            }
            else {
                return (hours + " hours ago");
            }
        }

        int hour = -1;
        int minute = -1;
        int am_pm = -1;
        int day_of_week = -1;
        int day_of_month = -1;
        int month = -1;
        int day_of_the_week = -1;

        synchronized (globalCal) {
            globalCal.setTime(date);
            globalCal.setTimeZone(getTimeZone(request, response, user));
            hour = globalCal.get(Calendar.HOUR);
            minute = globalCal.get(Calendar.MINUTE);
            am_pm = globalCal.get(Calendar.AM_PM);
            day_of_the_week = globalCal.get(Calendar.DAY_OF_WEEK);
            day_of_month = globalCal.get(Calendar.DAY_OF_MONTH);
            month = globalCal.get(Calendar.MONTH);
            day_of_week = globalCal.get(Calendar.DAY_OF_WEEK);
        }

        // Within the last week
        if ((delta / JiveGlobals.WEEK) < 1) {
            long days = (delta/JiveGlobals.DAY);
            if (days <= 1) {
                StringBuffer buf = new StringBuffer("Yesterday, ");
                if (am_pm == 1 && hour == 0) {
                    buf.append(12).append(":");
                } else {
                    buf.append(hour).append(":");
                }
                if (minute < 10) {
                    buf.append("0").append(minute);
                } else {
                    buf.append(minute);
                }
                buf.append(" ");
                if (am_pm == 0) {
                    buf.append("AM");
                } else {
                    buf.append("PM");
                }
                return buf.toString();
            }
            else {
                StringBuffer buf = new StringBuffer();
                buf.append(DAYS_OF_WEEK[day_of_the_week-1]);
                buf.append(", ").append(MONTHS_OF_YEAR[month]);
                buf.append(" ").append(day_of_month).append(" ");
                if (am_pm == 1 && hour == 0) {
                    buf.append(12).append(":");
                } else {
                    buf.append(hour).append(":");
                }
                if (minute < 10) {
                    buf.append("0").append(minute);
                } else {
                    buf.append(minute);
                }
                buf.append(" ");
                if (am_pm == 0) {
                    buf.append("AM");
                } else {
                    buf.append("PM");
                }
                return buf.toString();
            }
        }

        // More than a week ago.
        else {
            return formatDate(request, response, user, date);
        }
    }

    /**
     * Returns the time zone for a user according to their time zone
     * preferences (if they exist). Timezones preferences for users are stored
     * in the user property "jive.timeZoneID", while timezones for anonymous
     * users are stored in the cookie/session attribute "jive.timeZoneID". If
     * no preferences are found, the default Jive timezone is used
     * (JiveGlobals.getTimeZone()).
     *
     * @param request the servlet request object.
     * @param response the servlet response object.
     * @param user the User that the date is being formatted for, or null if
     *      an anonymous user.
     * @param date the Date object we're comparing the current time with.
     */
    public static TimeZone getTimeZone(HttpServletRequest request,
            HttpServletResponse response, User user)
    {
        TimeZone timeZone = JiveGlobals.getTimeZone();
        String timeZoneID = null;
        if (user != null) {
            timeZoneID = user.getProperty("jiveTimeZoneID");
        }
        else {
            HttpSession session = request.getSession();
            timeZoneID = (String)session.getAttribute("jiveTimeZoneID");
            if (timeZoneID == null) {
                Cookie cookie = getCookie(request, "jiveTimeZoneID");
                if (cookie != null) {
                    timeZoneID = cookie.getValue();
                    session.setAttribute("jiveTimeZoneID",timeZoneID);
                }
                else {
                    session.setAttribute("jiveTimeZoneID",timeZone.getID());
                }
            }
        }
        if (timeZoneID != null) {
            timeZone = TimeZone.getTimeZone(timeZoneID);
        }
        return timeZone;
    }

    private static String [][] timeZoneList = null;
    private static Object timeZoneLock = new Object();

    /**
     * Returns a list of all available time zone's as a String [][]. The first
     * entry in each list item is the timeZoneID, and the second is the
     * display name.<p>
     *
     * Normally, there are many ID's that correspond to a single display name.
     * However, the list has been paired down so that a display name only
     * appears once. Normally, the time zones will be returned in order:
     * -12 GMT,..., +0GMT,... +12GMT..., etc.
     *
     * @return a list of time zones, as a tuple of the zime zone ID, and its
     *      display name.
     */
    public static String [][] getTimeZoneList() {
        synchronized (timeZoneLock) {
            if (timeZoneList == null) {
                Date now = new Date();

                String[] timeZoneIDs = TimeZone.getAvailableIDs();
                ArrayList timeZones = new ArrayList(timeZoneIDs.length);
                Map uniqueNames = new HashMap(timeZoneIDs.length/2);
                Locale jiveLocale = JiveGlobals.getLocale();

                // Loop through all time zones, and create a list of those that
                // have unique names.
                for (int i=0; i<timeZoneIDs.length; i++) {
                    TimeZone zone = TimeZone.getTimeZone(timeZoneIDs[i]);
                    String zoneName = getTimeZoneName(zone, now, jiveLocale);

                    // If we don't already have this name, add it.
                    if (!uniqueNames.containsKey(zoneName)) {
                        uniqueNames.put(zoneName, null);
                        timeZones.add(zone);
                    }
                }
                // Now, create String[][] using the unique zones.
                timeZoneList = new String[uniqueNames.size()][2];
                for (int i=0; i<timeZoneList.length; i++) {
                    TimeZone zone = (TimeZone)timeZones.get(i);
                    timeZoneList[i][0] = zone.getID();
                    timeZoneList[i][1] = getTimeZoneName(zone, now, jiveLocale);
                }
            }
        }
        return timeZoneList;
    }

    /**
     * Returns the display name for a time zone. The display name is the name
     * specified by the Java TimeZone class, with the addition of the GMT offset
     * for human readability.
     *
     * @param zone the time zone to get the name for.
     * @param now the current date.
     * @param locale the locale to use.
     * @return the display name for the time zone.
     */
    private static String getTimeZoneName(TimeZone zone, Date now, Locale locale) {
        StringBuffer buf = new StringBuffer();
        // Add in the GMT part to the name. First, figure out the offset.
        int offset = zone.getRawOffset();
        if (zone.inDaylightTime(now) && zone.useDaylightTime()) {
            offset += JiveGlobals.HOUR;
        }
        String gmt;
        if (offset < 0) {
            buf.append("(GMT-");
        }
        else {
            buf.append("(GMT+");
        }
        offset = Math.abs(offset);
        int hours = offset/(int)JiveGlobals.HOUR;
        int minutes = (offset % (int)JiveGlobals.HOUR)/(int)JiveGlobals.MINUTE;
        if (hours < 10) {
            buf.append("0").append(hours).append(":");
        }
        else {
            buf.append(hours).append(":");
        }
        if (minutes < 10) {
            buf.append("0").append(minutes);
        }
        else {
            buf.append(minutes);
        }
        buf.append(") ").append(zone.getDisplayName(true, TimeZone.LONG, locale));
        return buf.toString();
    }

    /**
     * Builds a cookie string containing a username and password.<p>
     *
     * Note: with open source this is not really secure, but it prevents users
     * from snooping the cookie file of others and by changing the XOR mask and
     * character offsets, you can easily tweak results.
     *
     * @param username The username.
     * @param password The password.
     * @return String encoding the input parameters, an empty string if one of
     *      the arguments equals <code>null</code>.
     */
    private static String encodePasswordCookie(String username, String password)
    {
        StringBuffer buf = new StringBuffer();
        if (username != null && password != null) {
            byte[] bytes = (username + ENCODE_DELIMETER + password).getBytes();
            int b;

            for (int n = 0; n < bytes.length; n++) {
                b = bytes[n] ^ (ENCODE_XORMASK + n);
                buf.append((char)(ENCODE_CHAR_OFFSET1 + (b & 0x0F)));
                buf.append((char)(ENCODE_CHAR_OFFSET2 + ((b >> 4) & 0x0F)));
            }
        }
        return buf.toString();
    }

    /**
     * Unrafels a cookie string containing a username and password.
     * @param value The cookie value.
     * @return String[] containing the username at index 0 and the password at
     *      index 1, or <code>{ null, null }</code> if cookieVal equals
     *      <code>null</code> or the empty string.
     */
    private static String[] decodePasswordCookie( String cookieVal ) {

        // check that the cookie value isn't null or zero-length
        if( cookieVal == null || cookieVal.length() <= 0 ) {
            return null;
        }

        // unrafel the cookie value
        char[] chars = cookieVal.toCharArray();
        byte[] bytes = new byte[chars.length / 2];
        int b;
        for (int n = 0, m = 0; n < bytes.length; n++) {
            b = chars[m++] - ENCODE_CHAR_OFFSET1;
            b |= (chars[m++] - ENCODE_CHAR_OFFSET2) << 4;
            bytes[n] = (byte)(b ^ (ENCODE_XORMASK + n));
        }
        cookieVal = new String(bytes);
        int	pos = cookieVal.indexOf(ENCODE_DELIMETER);
        String username = (pos < 0) ? "" : cookieVal.substring(0, pos);
        String password = (pos < 0) ? "" : cookieVal.substring(pos + 1);

        return new String[] {username, password};
    }
}