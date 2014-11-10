/**
 * $RCSfile: JiveGlobals.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:45 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import com.jivesoftware.forum.util.XMLProperties;

import java.util.*;
import java.io.*;
import java.text.*;
import java.net.*;

/**
 * Contains constant values representing various objects in Jive as well as
 * other settings such as the global locale and Jive version number.<p>
 *
 * The class also controls Jive properties. Jive properties are only meant to
 * be set and retrevied by core Jive classes.
 * <p>
 * All properties are stored in the file <tt>jive_config.xml</tt> which is
 * located in the <tt>jiveHome</tt> directory. The location of that
 * directory should be specified one of two ways:<ol>
 *   <li>Indicate its value in the <tt>jive_init.properties</tt> file. This
 *       is a standard properties file so the property should be something
 *       like:<br>
 *       <tt>jiveHome=c:\\some\\directory\\jiveHome</tt> (Windows) <br>
 *       or <br>
 *       <tt>jiveHome=/home/some/directory/jiveHome</tt> (Unix)
 *       <p>
 *       The file must be in your classpath so that it can be loaded by Java's
 *       classloader.
 *   <li>Use another class in your VM to set the
 *      <tt>PropertyManager.jiveHome</tt> variable. This must be done before
 *      the rest of Jive starts up, for example: in a servlet that is set to run
 *      as soon as the appserver starts up.
 * </ol>
 * <p>
 * All Jive property names must be in the form <code>prop.name</code> - parts of
 * the name must be seperated by ".". The value can be any valid String,
 * including Strings with line breaks.
 */
public class JiveGlobals {

    // Constant values

    public static final int FORUM = 0;
    public static final int THREAD = 1;
    public static final int MESSAGE = 2;
    public static final int USER = 3;
    public static final int GROUP = 4;

    public static final int THREAD_NAME = 5;
    public static final int MESSAGE_SUBJECT = 6;
    public static final int MESSAGE_BODY = 7;
    public static final int CREATION_DATE = 8;
    public static final int MODIFIED_DATE = 9;
    public static final int EXTENDED_PROPERTY = 10;

    public static final int ANONYMOUS = 11;
    public static final int REGISTERED_USERS = 12;

    // Time values in milliseconds

    public static final long SECOND = 1000;
    public static final long MINUTE = 60 * SECOND;
    public static final long HOUR   = 60 * MINUTE;
    public static final long DAY    = 24 * HOUR;
    public static final long WEEK   = 7 * DAY;

    /**
     * The Major version number of Jive. i.e. 1.x.x
     */
    public static final int JIVE_MAJOR_VERSION = 2;

    /**
     * The Minor version number of Jive. i.e. x.1.x.
     */
    public static final int JIVE_MINOR_VERSION = 1;

    /**
     * The revision version number of Jive. i.e. x.x.1.
     */
    public static final int JIVE_REVISION_VERSION = 1;

    private static final String JIVE_CONFIG_FILENAME = "jive_config.xml";

    /**
     * Location of the jiveHome directory. All configuration files should be
     * located here. This value can be set explicitly by an outside class or
     * this class will attempt to load it from the <tt>jive_init.properties</tt>
     * file.
     */
    public static String jiveHome = null;

    /**
     * XML properties to actually get and set the Jive properties.
     */
    private static XMLProperties properties = null;

    private static Locale locale = null;
    private static TimeZone timeZone = null;
    private static DateFormat dateFormat = null;
    private static DateFormat dateTimeFormat = null;

    /**
     * Returns the version number of Jive as a String. i.e. major.minor.revision
     */
    public static String getJiveVersion() {
        return "<a href=http://www.jdon.com>Jive(J��)</a>)";
    }

    /**
     * Returns the global Locale used by Jive. A locale specifies language
     * and country codes, and is used for internationalization. The default
     * locale is Locale.US
     *
     * @return the global locale used by Jive.
     */
    public static Locale getLocale() {
        if (locale == null) {
            loadLocale();
        }
        return locale;
    }

    /**
     * Sets the global locale used by Jive. A locale specifies language
     * and country codes, and is used for internationalization. The default
     * locale is Locale.US
     *
     * @param locale the global Locale for Jive.
     */
    public static void setLocale(Locale newLocale) {
        locale = newLocale;
        // Save values to Jive properties.
        setJiveProperty("locale.country", locale.getCountry());
        setJiveProperty("locale.language", locale.getLanguage());
        // Reset the date formatter objects
        dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                DateFormat.MEDIUM, locale);
        dateFormat.setTimeZone(timeZone);
        dateTimeFormat.setTimeZone(timeZone);
    }

    /**
     * Returns the global TimeZone used by Jive. The default is the VM's
     * default time zone.
     *
     * @return the global time zone used by Jive.
     */
    public static TimeZone getTimeZone() {
        if (timeZone == null) {
            loadLocale();
        }
        return timeZone;
    }

    /**
     * Sets the global time zone used by Jive. The default time zone is the VM's
     * time zone.
     */
    public static void setTimeZone(TimeZone newTimeZone) {
        timeZone = newTimeZone;
        dateFormat.setTimeZone(timeZone);
        dateTimeFormat.setTimeZone(timeZone);
        setJiveProperty("locale.timeZone", timeZone.getID());
    }

    /**
     * Formats a Date object to return a date using the global locale.
     */
    public static String formatDate(Date date) {
        if (dateFormat == null) {
            loadLocale();
        }
        return dateFormat.format(date);
    }

    /**
     * Formats a Date object to return a date and time using the global locale.
     */
    public static String formatDateTime(Date date) {
        if (dateTimeFormat == null) {
            loadLocale();
        }
        return dateTimeFormat.format(date);
    }

    /**
     * Returns the location of the <code>jiveHome</code> directory.
     *
     * @return the location of the jiveHome dir.
     */
    public static String getJiveHome() {
        if (jiveHome == null) {
            jiveHome = new InitPropLoader().getJiveHome();
            // If jiveHome is still null, try loading it as a system property
            if (jiveHome == null) {
                jiveHome = System.getProperty("jiveHome");
            }
        }
        return jiveHome;
    }


    /**
     * Indicates whether or not we have read access to the <code>jiveHome</code>
     * directory.
     *
     * @return true if we have read access to jiveHome, false otherwise.
     */
    public static boolean isJiveHomeReadable() {
        return (new File(getJiveHome())).canRead();
    }

    /**
     * Indicates whether or not we have write access to the
     * <code>jiveHome</code> directory.
     *
     * @return true if we have write access to jiveHome, false otherwise.
     */
    public static boolean isJiveHomeWritable() {
        return (new File(getJiveHome())).canWrite();
    }

    /**
     * Returns a Jive property.
     *
     * @param name the name of the property to return.
     * @return the property value specified by name.
     */
    public static String getJiveProperty(String name) {
        loadProperties();
        return properties.getProperty(name);
    }

    /**
     * Sets a Jive property. If the property doesn't already exists, a new
     * one will be created.
     *
     * @param name the name of the property being set.
     * @param value the value of the property being set.
     */
    public static void setJiveProperty(String name, String value) {
        loadProperties();
        properties.setProperty(name, value);
    }

    /**
     * Deletes a Jive property. If the property doesn't exist, the method
     * does nothing.
     *
     * @param name the name of the property to delete.
     */
    public static void deleteJiveProperty(String name) {
        loadProperties();
        properties.deleteProperty(name);
    }

    /**
     * Loads properties if necessary. Property loading must be done lazily so
     * that we give outside classes a chance to set <tt>jiveHome</tt>.
     */
    private synchronized static void loadProperties() {
            // If jiveHome is still null, no outside process has set it and
            // we have to attempt to load the value from jive_init.properties,
            // which must be in the classpath.
            if (jiveHome == null) {
                jiveHome = new InitPropLoader().getJiveHome();
            }
            //Create a manager with the full path to the xml config file.
            properties = new XMLProperties(jiveHome + File.separator +
                    JIVE_CONFIG_FILENAME);

    }

    /**
     * Load locale and timezone information.
     */
    private synchronized static void loadLocale() {
        String language = getJiveProperty("locale.language");
        if (language == null) {
            language = "";
        }
        String country = getJiveProperty("locale.country");
        if (country == null) {
            country = "";
        }
        // If no locale info is specified, default to Locale.US
        if (language.equals("") && country.equals("")) {
            locale = Locale.US;
        }
        else {
            locale = new Locale(language, country);
        }
        String timeZoneID = getJiveProperty("locale.timeZone");
        if (timeZoneID == null) {
            timeZone = TimeZone.getDefault();
        }
        else {
            timeZone = TimeZone.getTimeZone(timeZoneID);
        }
        dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                DateFormat.MEDIUM, locale);
        dateFormat.setTimeZone(timeZone);
        dateTimeFormat.setTimeZone(timeZone);
    }
}

/**
 * A very small class to load the jive_init.properties file. The class is
 * needed since loading files from the classpath in a static context often
 * fails.
 */
class InitPropLoader {

    public String getJiveHome() {
        String jiveHome = null;
        Properties initProps = new Properties();
        InputStream in = null;
        try {
            in = getClass().getResourceAsStream("/jive_init.properties");
            initProps.load(in);
        }
        catch (Exception e) {
            System.err.println("Error reading Jive properties "
                + "in JiveGlobals");
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null) { in.close(); }
            } catch (Exception e) {}
        }
        if (initProps != null) {
            jiveHome = initProps.getProperty("jiveHome");
            if (jiveHome != null) {
                jiveHome = jiveHome.trim();
                //Remove trailing slashes.
                while (jiveHome.endsWith("/") || jiveHome.endsWith("\\")) {
                    jiveHome = jiveHome.substring(0, jiveHome.length()-1);
                }
            }
        }
        return jiveHome;
    }
}