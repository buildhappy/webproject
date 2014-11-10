/**
 * $RCSfile: DbDataExport.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:49 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import java.util.*;
import java.text.*;
import java.io.*;

import com.jivesoftware.forum.*;
import com.jivesoftware.util.*;

/**
 * Writes Jive data to an XML file. Files are always written to
 * <tt>jiveHome/data</tt>.
 */
public class DbDataExport {

    /**
     * Standard Jive XML date format.
     */
    private static final String XML_DATE_FORMAT = DbDataImport.XML_DATE_FORMAT;

    /**
     * XML version
     */
    private static final String XML_VERSION = DbDataImport.XML_VERSION;

    /**
     * A simple date formatter that will convert Date objects to the Jive
     * date format. For example: 1978/01/17 21:17:33.83 CST
     */
    private static SimpleDateFormat dateFormatter =
            new SimpleDateFormat(XML_DATE_FORMAT);

    private static SimpleDateFormat fileDateFormatter =
        new SimpleDateFormat("yyyy-MM-dd");

    private DbForumFactory factory = null;

    /**
     * Jive permission types
     */
    private static int [] permTypes = new int [] {
        ForumPermissions.READ,
        ForumPermissions.FORUM_ADMIN,
        ForumPermissions.MODERATE_THREADS,
        ForumPermissions.CREATE_THREAD,
        ForumPermissions.CREATE_MESSAGE,
        ForumPermissions.MODERATE_MESSAGES
    };

    /**
     * Jive permission names
     */
    private static String [] permNames = new String [] {
        "READ", "FORUM_ADMIN", "MODERATE_THREADS", "CREATE_THREAD", "CREATE_MESSAGE",
        "MODERATE_MESSAGES"
    };

    /**
     * Creates a new data export instance that can be used to write out Jive
     * XML data.
     */
    public DbDataExport(DbForumFactory factory) {
        this.factory = factory;
    }

    /**
     *
     */
    public void export(boolean IDFlag) throws IOException,
            UnauthorizedException
    {
        String fileName = JiveGlobals.getJiveHome() + File.separator + "data"
                + File.separator + "jive-" + fileDateFormatter.format(new Date())
                + ".xml";

        int buffer = 1024*256; // 256 K buffer for writing.
        Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName),"UTF-8"),buffer);

        try {
            // Write out the header XML
            out.write(getHeaderXML());

            // Export users
            out.write("<UserList>");
            exportUsers(out, IDFlag);
            out.write("</UserList>");

            // Export gropus
            out.write("<GroupList>");
            exportGroups(out, IDFlag);
            out.write("</GroupList>");

            // Export forums
            out.write("<ForumList>");
            exportForums(out, IDFlag);
            out.write("</ForumList>");

            // Export global permissions
            out.write(getPermissionsXML());

            // Ending tag
            out.write("</Jive>");
            out.flush();
        }
        finally {
            try {
                out.close();
            } catch (Exception e) {}
        }
    }

    private String getHeaderXML() {
        StringBuffer buf = new StringBuffer(200);
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buf.append("<!DOCTYPE Jive SYSTEM \"http://www.jivesoftware.com/jive.dtd\">");
        buf.append("<Jive xmlversion=\"").append(XML_VERSION).append("\" exportDate=\"" +
            dateFormatter.format(new Date()) + "\">"
        );
        return buf.toString();
    }

    private String getPermissionsXML() {
        StringBuffer buf = new StringBuffer(1024);
        PermissionsManager permManager = factory.getPermissionsManager();

        // Global user permissions
        buf.append("<UserPermissionList>");
        for( int i=0; i<permNames.length; i++ ) {
            Iterator userList = permManager.usersWithPermission(permTypes[i]);
            while( userList.hasNext() ) {
                User user = (User)userList.next();
                if( user != null ) {
                    buf.append("<UserPermission usertype=\"USER\" username=\"");
                    buf.append(user.getUsername());
                    buf.append("\" permission=\"").append(permNames[i]).append("\"/>");
                }
            }
            if( permManager.anonymousUserHasPermission(permTypes[i]) ) {
                buf.append("<UserPermission usertype=\"ANONYMOUS\"");
                buf.append(" permission=\"").append(permNames[i]).append("\"/>");
            }
            if( permManager.registeredUserHasPermission(permTypes[i]) ) {
                buf.append("<UserPermission usertype=\"REGISTERED_USERS\"");
                buf.append(" permission=\"").append(permNames[i]).append("\"/>");
            }
        }
        buf.append("</UserPermissionList>");

        // Global group permissions
        buf.append("<GroupPermissionList>");
        for( int i=0; i<permNames.length; i++ ) {
            Iterator groupList = permManager.groupsWithPermission(permTypes[i]);
            while (groupList.hasNext()) {
                Group group = (Group)groupList.next();
                if (group != null) {
                    buf.append("<GroupPermission groupname=\"" + group.getName() + "\"");
                    buf.append(" permission=\"").append(permNames[i]).append("\"/>");
                }
            }
        }
        buf.append("</GroupPermissionList>");
        return buf.toString();
    }

    private String getThreadXML(ForumThread thread, boolean IDFlag) {
        StringBuffer buf = new StringBuffer(4096);
        if (!IDFlag) {
            buf.append("<Thread>");
        }
        else {
            buf.append("<Thread id=\"").append(thread.getID()).append("\">");
        }
        buf.append("<CreationDate>");
        buf.append(dateFormatter.format(thread.getCreationDate()));
        buf.append("</CreationDate><ModifiedDate>");
        buf.append(dateFormatter.format(thread.getModifiedDate()));
        buf.append("</ModifiedDate>");
        // Properties
        Iterator propertyNames = thread.propertyNames();
        if( propertyNames.hasNext() ) {
            buf.append("<PropertyList>");
            while (propertyNames.hasNext()) {
                String propName = (String)propertyNames.next();
                String propValue = StringUtils.escapeForXML(
                        thread.getProperty(propName));
                if (propValue != null) {
                    propName = StringUtils.escapeForXML(propName);
                    buf.append("<Property name=\"").append(propName).append("\" ");
                    buf.append("value=\"").append(propValue).append("\"/>");
                }
            }
            buf.append("</PropertyList>");
        }
        // Write out all messages in thread.
        messageToXML(thread.getRootMessage(), IDFlag, buf);
        buf.append("</Thread>");
        return buf.toString();
    }

    private void messageToXML(ForumMessage message, boolean IDFlag, StringBuffer buf) {
        if (!IDFlag) {
            buf.append("<Message>");
        }
        else {
            buf.append("<Message id=\"").append(message.getID()).append("\">");
        }
        buf.append("<Subject>");
        buf.append(StringUtils.escapeForXML(message.getUnfilteredSubject()));
        buf.append("</Subject><Body>");
        buf.append(StringUtils.escapeForXML(message.getUnfilteredBody()));
        buf.append("</Body>");
        if( !message.isAnonymous() ) {
            buf.append("<Username>");
            buf.append(StringUtils.escapeForXML(message.getUser().getUsername()));
            buf.append("</Username>");
        }
        buf.append("<CreationDate>");
        buf.append(dateFormatter.format(message.getCreationDate()));
        buf.append("</CreationDate><ModifiedDate>");
        buf.append(dateFormatter.format(message.getModifiedDate()));
        buf.append("</ModifiedDate>");
        // Message properties
        Iterator propertyNames = message.propertyNames();
        if( propertyNames.hasNext() ) {
            buf.append("<PropertyList>");
            while (propertyNames.hasNext()) {
                String propName = (String)propertyNames.next();
                String propValue = StringUtils.escapeForXML(
                        message.getUnfilteredProperty(propName));
                if (propValue != null) {
                    propName = StringUtils.escapeForXML(propName);
                    buf.append("<Property name=\"").append(propName).append("\" ");
                    buf.append("value=\"").append(propValue).append("\"/>");
                }
            }
            buf.append("</PropertyList>");
        }

        // Write out children messages
        TreeWalker walker = message.getForumThread().treeWalker();
        int childCount = walker.getChildCount(message);
        if (childCount > 0) {
            buf.append("<MessageList>");
            for (int i=0; i<childCount; i++) {
                try {
                    ForumMessage childMessage = walker.getChild(message, i);
                    messageToXML(childMessage, IDFlag, buf);
                }
                catch( ForumMessageNotFoundException fmnfe ) {
                    fmnfe.printStackTrace();
                }
                catch( Exception e ) {
                    e.printStackTrace();
                }
            }
            buf.append("</MessageList>");
        }
        buf.append("</Message>");
    }

    private String getUserXML(User user, boolean IDFlag) throws UnauthorizedException {
        StringBuffer buf = new StringBuffer(512);
        if (!IDFlag) {
            buf.append("<User>");
        }
        else {
            buf.append("<User id=\"").append(user.getID()).append("\">");
        }
        buf.append("<Username>");
        buf.append(StringUtils.escapeForXML(user.getUsername()));
        buf.append("</Username><Password>");
        buf.append(StringUtils.escapeForXML(user.getPasswordHash()));
        boolean emailVisible = user.isEmailVisible();
        boolean nameVisible = user.isNameVisible();
        String name = user.getName();
        buf.append("</Password><Email visible=\"" + emailVisible + "\">");
        buf.append(StringUtils.escapeForXML(user.getEmail()));
        buf.append("</Email>");
        if (name != null) {
            buf.append("<Name visible=\"" + nameVisible + "\">");
            buf.append(StringUtils.escapeForXML(name));
            buf.append("</Name>");
        }
        // creation date / modified date
        buf.append("<CreationDate>").append(user.getCreationDate()).append("</CreationDate>");
        buf.append("<ModifiedDate>").append(user.getModifiedDate()).append("</ModifiedDate>");
        // Properties
        Iterator userProps = user.propertyNames();
        if (userProps.hasNext()) {
            buf.append("<PropertyList>");
            while (userProps.hasNext()) {
                String propName = (String)userProps.next();
                String propValue = StringUtils.escapeForXML(user.getProperty(propName));
                if (propValue != null) {
                    // XML escape these sequences
                    propName = StringUtils.escapeForXML(propName);
                    buf.append("<Property name=\"").append(propName).append("\" ");
                    buf.append("value=\"").append(propValue).append("\"/>");
                }
            }
            buf.append("</PropertyList>");
        }
        buf.append("</User>");
        return buf.toString();
    }

    private String getGroupXML(Group group, boolean IDFlag) throws UnauthorizedException {
        StringBuffer buf = new StringBuffer(512);
        if (!IDFlag) {
            buf.append("<Group>");
        }
        else {
            buf.append("<Group id=\"").append(group.getID()).append("\">");
        }

        buf.append("<Name>");
        buf.append(StringUtils.escapeForXML(group.getName()));
        buf.append("</Name><Description>");
        buf.append(StringUtils.escapeForXML(group.getDescription()));
        buf.append("</Description>");

        // creation date / modified date
        buf.append("<CreationDate>").append(group.getCreationDate()).append("</CreationDate>");
        buf.append("<ModifiedDate>").append(group.getModifiedDate()).append("</ModifiedDate>");
        // Properties
        Iterator groupProps = group.propertyNames();
        if (groupProps.hasNext()) {
            buf.append("<PropertyList>");
            while (groupProps.hasNext()) {
                String propName = (String)groupProps.next();
                String propValue = StringUtils.escapeForXML(group.getProperty(propName));
                if (propValue != null) {
                    // XML escape these sequences
                    propName = StringUtils.escapeForXML(propName);
                    buf.append("<Property name=\"").append(propName).append("\" ");
                    buf.append("value=\"").append(propValue).append("\"/>");
                }
            }
            buf.append("</PropertyList>");
        }
        // Admin list
        Iterator admins = group.administrators();
        if (admins.hasNext()) {
            buf.append("<AdministratorList>");
            while (admins.hasNext()) {
                String username = ((User)admins.next()).getUsername();
                buf.append("<Username>").append(username).append("</Username>");
            }
            buf.append("</AdministratorList>");
        }
        // Member list
        Iterator members = group.members();
        if (members.hasNext()) {
            buf.append("<MemberList>");
            while (members.hasNext()) {
                String username = ((User)members.next()).getUsername();
                buf.append("<Username>").append(username).append("</Username>");
            }
            buf.append("</MemberList>");
        }
        buf.append("</Group>");
        return buf.toString();
    }

    private void exportUsers(Writer out, boolean IDFlag) throws IOException,
            UnauthorizedException
    {
        UserManager userManager = factory.getUserManager();
        for (Iterator iter = userManager.users(); iter.hasNext(); ) {
            User user = (User)iter.next();
            out.write(getUserXML(user, IDFlag));
        }
    }

    private void exportGroups(Writer out, boolean IDFlag) throws IOException,
            UnauthorizedException
    {
        GroupManager groupManager = factory.getGroupManager();
        for (Iterator iter = groupManager.groups(); iter.hasNext(); ) {
            Group group = (Group)iter.next();
            out.write(getGroupXML(group, IDFlag));
        }
    }

    private void exportForums(Writer out, boolean IDFlag) throws IOException,
            UnauthorizedException
    {
        Iterator forums = factory.forums();
        while (forums.hasNext()) {
            Forum forum = (Forum)forums.next();
            String name = forum.getName();
            StringBuffer buf = new StringBuffer(4096);
            if (!IDFlag) {
                buf.append("<Forum>");
            }
            else {
                buf.append("<Forum id=\"").append(forum.getID()).append("\">");
            }
            buf.append("<Name>");
            buf.append(StringUtils.escapeForXML(name));
            buf.append("</Name><Description>");
            buf.append(StringUtils.escapeForXML(forum.getDescription()));
            buf.append("</Description><CreationDate>");
            buf.append(dateFormatter.format(forum.getCreationDate()));
            buf.append("</CreationDate><ModifiedDate>");
            buf.append(dateFormatter.format(forum.getModifiedDate()));
            buf.append("</ModifiedDate>");

            // export forum permissions
            PermissionsManager permManager = forum.getPermissionsManager();
            buf.append("<PermissionList>");

            // User List
            buf.append("<UserPermissionList>");
            for( int i=0; i<permNames.length; i++ ) {
            Iterator userList = permManager.usersWithPermission(permTypes[i]);
                while( userList.hasNext() ) {
                User user = (User)userList.next();
                if( user != null ) {
                    buf.append("<UserPermission usertype=\"USER\" username=\"");
                    buf.append(user.getUsername());
                    buf.append("\" permission=\"" + permNames[i] + "\"/>");
                }
                }
            if( permManager.anonymousUserHasPermission(permTypes[i]) ) {
                buf.append("<UserPermission usertype=\"ANONYMOUS\"");
                buf.append(" permission=\"" + permNames[i] + "\"/>");
            }
            if( permManager.registeredUserHasPermission(permTypes[i]) ) {
                buf.append("<UserPermission usertype=\"REGISTERED_USERS\"");
                buf.append(" permission=\"" + permNames[i] + "\"/>");
            }
            }
            buf.append("</UserPermissionList>");

            // Group List
            buf.append("<GroupPermissionList>");
            for( int i=0; i<permNames.length; i++ ) {
            Iterator groupList = permManager.groupsWithPermission(permTypes[i]);
            while( groupList.hasNext() ) {
                Group group = (Group)groupList.next();
                if( group != null ) {
                        buf.append("<GroupPermission groupname=\"" + group.getName() + "\"");
                        buf.append(" permission=\"" + permNames[i] + "\"/>");
                    }
                }
            }
            buf.append("</GroupPermissionList>");
            buf.append("</PermissionList>");

            // Properties
            Iterator propertyNames = forum.propertyNames();
            if( propertyNames.hasNext() ) {
                buf.append("<PropertyList>");
                while (propertyNames.hasNext()) {
                    String propName = (String)propertyNames.next();
                    String propValue = StringUtils.escapeForXML(
                        forum.getProperty(propName));
                    if (propValue != null) {
                        propName = StringUtils.escapeForXML(propName);
                        buf.append("<Property name=\"").append(propName).append("\" ");
                        buf.append("value=\"").append(propValue).append("\"/>");
                    }
                }
                buf.append("</PropertyList>");
            }

            // write out what we have so far
            out.write(buf.toString());

            // export threads
            out.write("<ThreadList>");
            exportThreads(forum, IDFlag, out);
            out.write("</ThreadList>");
            out.write("</Forum>");
        }
    }

    private void exportThreads(Forum forum, boolean IDFlag, Writer out) throws IOException {
        for (Iterator iter = forum.threads(); iter.hasNext(); ) {
            ForumThread thread = (ForumThread)iter.next();
            out.write(getThreadXML(thread, IDFlag));
        }
    }

    public static void main( String args[] ) {
        if( args.length < 3 ) {
            System.err.println( "USAGE: java DbDataExport [xml_uri] "
                + "[jive_username] [jive_password]" );
            System.exit(1);
        }/*
        try {
            authToken = AuthorizationFactory.getAuthorization(args[1],args[2]);
        }
        catch( UnauthorizedException e ) {
            System.err.println( "Error authenicating user " + args[1] + ". "
                + "Please make sure you're using the correct username and "
                + "password." );
            System.exit(1);
        }*/
        try {
            DbDataExport exporter = new DbDataExport(new DbForumFactory());
            System.out.println("\nBeginning export, writing to " + args[0] + "...");
            long time = System.currentTimeMillis();
            exporter.export(true);
            time = System.currentTimeMillis() - time;

            System.out.println("\nExport done, time = "
                + ((double)time/1000.0) + " seconds" );

        } catch( Exception e ) {
            e.printStackTrace();
        }
        System.exit(1);
    }
}