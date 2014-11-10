/**
 * $RCSfile: DbDataImport.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:51 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.jar.*;

//import com.jivesoftware.crimson.parser.*;

import org.xml.sax.*;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.*;
import org.apache.crimson.parser.XMLReaderImpl;

import com.jivesoftware.forum.*;

/**
 * Imports Jive data that is stored in the Jive XML format. The class can
 * handle plain XML files or compressed JAR files. All XML must strictly
 * comply with the Jive DTD.
 */
public class DbDataImport {

    public static final String XML_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss.SS z";

    public static final String XML_VERSION = "1.1";

    // A SAX parser
    private XMLReader parser = null;

    // ForumFactory object -- all Jive objects come from this
    private ForumFactory forumFactory = null;

    // UserManager and GroupManager objects to create users & groups
    private UserManager userManager = null;
    private GroupManager groupManager = null;

    // The current forum being imported
    private Forum forum;
    // The current thread being imported
    private ForumThread thread;

    // Indicates if the current thread has a root message.
    private boolean threadHasRootMessage = false;

    // A simple date formatter that will convert Date objects to the Jive
    // date format. For example: 1978/01/17 21:17:33.83 CST
    private static SimpleDateFormat dateFormatter =
        new SimpleDateFormat(XML_DATE_FORMAT);

    // Load Jive permission types. We map each permission name to its int value.
    private static HashMap jivePermissions = new HashMap();
    static {
        jivePermissions.put("CREATE_MESSAGE",
            new Integer(ForumPermissions.CREATE_MESSAGE));
        jivePermissions.put("CREATE_THREAD",
            new Integer(ForumPermissions.CREATE_THREAD));
        jivePermissions.put("FORUM_ADMIN",
            new Integer(ForumPermissions.FORUM_ADMIN));
        jivePermissions.put("GROUP_ADMIN",
            new Integer(ForumPermissions.GROUP_ADMIN));
        jivePermissions.put("MODERATE_THREADS",
            new Integer(ForumPermissions.MODERATE_THREADS));
        jivePermissions.put("READ",
            new Integer(ForumPermissions.READ));
        jivePermissions.put("SYSTEM_ADMIN",
            new Integer(ForumPermissions.SYSTEM_ADMIN));
        jivePermissions.put("USER_ADMIN",
            new Integer(ForumPermissions.USER_ADMIN));
        jivePermissions.put("MODERATE_MESSAGES",
            new Integer(ForumPermissions.MODERATE_MESSAGES));
    }

    // Parsing mode definitions
    private int mode = 0;
    private final static int USERNAME      = 1;
    private final static int PASSWORD      = 2;
    private final static int EMAIL         = 3;
    private final static int NAME          = 4;
    private final static int DESCRIPTION   = 5;
    private final static int CREATION_DATE = 6;
    private final static int MODIFIED_DATE = 7;
    private final static int SUBJECT       = 8;
    private final static int BODY          = 9;

    /**
     * Parses a date string and returns a Date object. If the the date cannot
     * be parsed, a new Date is returned.
     */
    private static Date parseDate(String dateText) {
        try {
            return dateFormatter.parse(dateText);
        }
        catch (ParseException pe) {
            return new Date();
        }
    }


    /**
     * Initializes Jive data importer.
     *
     * @param dbForumFactory
     */
    public DbDataImport(DbForumFactory dbForumFactory) throws UnauthorizedException,
            Exception
    {
        this.forumFactory = dbForumFactory;
        // Get user and group managers
        this.userManager = forumFactory.getUserManager();
        this.groupManager = forumFactory.getGroupManager();
        parser = new XMLReaderImpl();
    }

    /**
     * Imports Jive data.
     *
     * @param in An InputStream object
     */
    public void doImport(Reader in) throws IOException, Exception {
        try {
            parser.setContentHandler(new JiveHandler());
            parser.parse(new InputSource(in));
        }
        catch (SAXException saxe) {
            if (saxe instanceof SAXParseException) {
                SAXParseException saxpe = (SAXParseException)saxe;
                int line = saxpe.getLineNumber();
                int col = saxpe.getColumnNumber();
                String publicID = saxpe.getPublicId();
                String message = "XML parsing exception (" + publicID +
                    ") line " + line + ":" + col;
                throw new Exception(message);
            }
        }
    }

    //
    private static void print(String msg) {
        // TODO: make message stream settable
        System.err.println(msg);
    }

    /**
     * main method (for testing purposes only)
     */
    public static void main(String args[]) {

        // Get parameters
        if (args.length < 3) {
            print("USAGE: java DbDataImport <xml_data_file> <jive_username> "
                + "<jive_password>");
            return;
        }
        String xmlDataFile = args[0];
        String username = args[1];
        String password = args[2];

        // TODO: check all params

        // Login to Jive, create a jive ForumFactory
        Authorization authToken = null;
        ForumFactory factory = null;
        try {
            authToken = AuthorizationFactory.getAuthorization(args[1], args[2]);
            print("Logged into Jive as '" + username + "'");
            // Get the underlying forum factory implementation
            factory = ForumFactory.getInstance(authToken);
        }
        catch (UnauthorizedException ue) {
            print("ERROR: Jive authorization failed. Please "
                + "verify that your username and password are correct and "
                + "that a jive.properties file exists in your classpath.");
            ue.printStackTrace();
            return;
        }

        // Start an import, keep track of the time.
        long time = 0;
        try {
            DbForumFactory dbForumFactory = (DbForumFactory)(((ForumFactoryProxy)factory).getProxiedForumFactory());
            DbDataImport importer = new DbDataImport(dbForumFactory);
            Reader in = new FileReader(new File(args[0]));
            time = System.currentTimeMillis();
            importer.doImport(in);
            time = System.currentTimeMillis() - time;
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        print("Finished import.");
        print("--------------------------------");
        print("Total time: " + ((double)time/1000.0) + " seconds");
        return;
    }

    /**
     * Setup global SAX handlers ala the Flyweight design pattern. Each of
     * these objects will be re-used again and again to parse the subsets of
     * the XML document that they know how to handle. The only handlers that are
     * a bit tricky are MessageHandler and MessageListHandler because they can
     * be nested in the XML document. Therefore, those classes use an internal
     * stacks to remember where to pass back the flow of control after ending.
     */
    private ForumHandler forumHandler = new ForumHandler();
    private ForumListHandler forumListHandler = new ForumListHandler();
    private GroupHandler groupHandler = new GroupHandler();
    private GroupListHandler groupListHandler = new GroupListHandler();
    private GroupPermissionListHandler groupPermissionListHandler
        = new GroupPermissionListHandler();
    private MessageHandler messageHandler = new MessageHandler();
    private MessageListHandler messageListHandler = new MessageListHandler();
    private PermissionListHandler permissionListHandler
        = new PermissionListHandler();
    private PropertyListHandler propertyListHandler = new PropertyListHandler();
    private ThreadHandler threadHandler = new ThreadHandler();
    private ThreadListHandler threadListHandler = new ThreadListHandler();
    private UserHandler userHandler = new UserHandler();
    private UserListHandler userListHandler = new UserListHandler();
    private UserPermissionListHandler userPermissionListHandler
        = new UserPermissionListHandler();

    /**
     *
     */
    private final class JiveHandler extends DefaultHandler {

        public void startElement(String uri, String localName, String tag,
                Attributes attribs)
                throws SAXParseException
        {
            if (localName.equals("Jive")) {
                // Check attributes here such as the Jive XML version, etc.
            }
            else if (localName.equals("UserList")) {
                userListHandler.setParentHandler(this);
                parser.setContentHandler(userListHandler);
            }
            else if (localName.equals("GroupList")) {
                groupListHandler.setParentHandler(this);
                parser.setContentHandler(groupListHandler);
            }
            else if (localName.equals("ForumList")) {
                forumListHandler.setParentHandler(this);
                parser.setContentHandler(forumListHandler);
            }
            else if (localName.equals("UserPermissionList")) {
                userPermissionListHandler.setParentHandler(this);
                parser.setContentHandler(userPermissionListHandler);
            }
            else if (localName.equals("GroupPermissionList")) {
            }
        }
    }

    /**
     * Class to handle parsing of
     */
    private class UserListHandler extends DefaultHandler {

        private ContentHandler parentHandler;

        /**
         * Sets the parentHandler of the handler. Control will be passed back
         * to the parent when this handler is done parsing the XML it knows
         * how to handle.
         */
        public void setParentHandler(DefaultHandler parentHandler) {
            this.parentHandler = parentHandler;
        }

        public void startElement(String uri, String localName, String tag,
                Attributes attribs)
                throws SAXParseException
        {
            if (localName.equals("User")) {
                userHandler.setParentHandler(this);
                parser.setContentHandler(userHandler);
            }
        }
        public void endElement(String uri, String localName, String tag)
                throws SAXParseException
        {
            if (localName.equals("UserList")) {
                // Let parent resume handling SAX events
                parser.setContentHandler(parentHandler);
            }
        }
    }

    /**
     *
     */
    private class UserHandler extends DefaultHandler implements PropertyStore {

        private User newUser;

        private boolean isUsernameSet = false;
        private boolean isPasswordSet = false;
        private boolean isEmailSet = false;

        private StringBuffer username = new StringBuffer();
        private StringBuffer password = new StringBuffer();
        private StringBuffer email = new StringBuffer();
        private StringBuffer name = new StringBuffer();
        private StringBuffer creationDate = new StringBuffer();
        private StringBuffer modifiedDate = new StringBuffer();
        private boolean nameVisible = false;
        private boolean emailVisible = false;
        private Map properties = null;

        private boolean doCreateUser = false;
        private boolean userCreated = false;

        private ContentHandler parentHandler;

        /**
         * Sets the parentHandler of the handler. Control will be passed back
         * to the parent when this handler is done parsing the XML it knows
         * how to handle.
         */
        public void setParentHandler(DefaultHandler parentHandler) {
            this.parentHandler = parentHandler;

            //reset data
            newUser = null;
            isUsernameSet = false;
            isPasswordSet = false;
            isEmailSet = false;
            username.setLength(0);
            password.setLength(0);
            email.setLength(0);
            name.setLength(0);
            creationDate.setLength(0);
            modifiedDate.setLength(0);
            nameVisible = false;
            emailVisible = false;
            properties = new HashMap();
            doCreateUser = false;
            userCreated = false;
        }

        public void addProperty(String name, String value) {
            properties.put(name,value);
         }

        public void startElement(String uri, String localName, String tag,
                Attributes attribs)
                throws SAXParseException
        {
            if (localName.equals("Username")) {
                mode = USERNAME;
            }
            else if (localName.equals("Password")) {
                mode = PASSWORD;
            }
            else if (localName.equals("Email")) {
                mode = EMAIL;
                nameVisible = Boolean.valueOf(attribs.getValue(0)).booleanValue();
            }
            else if (localName.equals("Name")) {
                mode = NAME;
                emailVisible = Boolean.valueOf(attribs.getValue(0)).booleanValue();
            }
            else if (localName.equals("CreationDate")) {
                mode = CREATION_DATE;
            }
            else if (localName.equals("ModifiedDate")) {
                mode = MODIFIED_DATE;
            }
            else if (localName.equals("PropertyList")) {
                propertyListHandler.setParentHandler(this);
                propertyListHandler.setPropertyStore(this);
                parser.setContentHandler(propertyListHandler);
            }
        }

        public void characters(char[] buf, int start, int length)
                throws SAXParseException
        {
            switch (mode) {
                case USERNAME:
                    username.append(buf,start,length);
                    isUsernameSet = true;
                    break;
                case PASSWORD:
                    password.append(buf,start,length);
                    isPasswordSet = true;
                    break;
                case EMAIL:
                    email.append(buf,start,length);
                    isEmailSet = true;
                    break;
                case NAME:
                    name.append(buf,start,length);
                    break;
                case CREATION_DATE:
                    creationDate.append(buf,start,length);
                    break;
                case MODIFIED_DATE:
                    modifiedDate.append(buf,start,length);
                    break;
            }
            // check and see if we can create a message
            doCreateUser = (isUsernameSet && isPasswordSet && isEmailSet);
        }
        public void endElement(String uri, String localName, String tag)
                throws SAXParseException
        {
            // reset the mode now -- (fixes whitespace padding)
            mode = 0;

            if (localName.equals("User") && doCreateUser) {
                // create the user
                createUser();
                // Let parent resume handling SAX events
                parser.setContentHandler(parentHandler);
            }
        }

        private void createUser() {
            try {
                // Create the user. We pass in a map of properties at creation
                // time because this is the most efficient way to add
                // extended properties
                newUser = userManager.createUser(
                    username.toString(),
                    password.toString(),
                    name.toString(),
                    email.toString(),
                    nameVisible,emailVisible,properties
                );
                // Set the password hash -- the XML file gives us the password
                // hash, not the original password
                newUser.setPasswordHash(password.toString());
                // Set the creation date of the user
                try {
                    newUser.setCreationDate(dateFormatter.parse(creationDate.toString()));
                } catch(ParseException pe) {}
                // Set the modified date of the user
                try {
                    newUser.setModifiedDate(dateFormatter.parse(creationDate.toString()));
                } catch(ParseException pe) {}
            }
            catch (UserAlreadyExistsException uaee) {
                //print("User '" + username + "' already exists.");
            }
            catch (UnauthorizedException ue) {
                ue.printStackTrace();
            }
        }
    }

    /**
     *
     */
    private class GroupListHandler extends DefaultHandler {

        private ContentHandler parentHandler;

        /**
         * Sets the parentHandler of the handler. Control will be passed back
         * to the parent when this handler is done parsing the XML it knows
         * how to handle.
         */
        public void setParentHandler(DefaultHandler parentHandler) {
            this.parentHandler = parentHandler;
        }

        public void startElement(String uri, String localName, String tag,
                Attributes attribs)
                throws SAXParseException
        {
            if (localName.equals("Group")) {
                groupHandler.setParentHandler(this);
                parser.setContentHandler(groupHandler);
            }
        }
        public void endElement(String uri, String localName, String tag)
                throws SAXParseException
        {
            if (localName.equals("GroupList")) {
                // Let parent resume handling SAX events
                parser.setContentHandler(parentHandler);
            }
        }
    }

    /**
     *
     */
    private class GroupHandler extends DefaultHandler {

        private ContentHandler parentHandler;
        private Group group;

        private boolean isNameSet = false;
        private boolean isDescriptionSet = false;

        private StringBuffer username = new StringBuffer();
        private StringBuffer password = new StringBuffer();
        private StringBuffer email = new StringBuffer();
        private StringBuffer name = new StringBuffer();
        private StringBuffer creationDate = new StringBuffer();
        private StringBuffer modifiedDate = new StringBuffer();
        private boolean nameVisible = false;
        private boolean emailVisible = false;
        private Map properties = null;

        private boolean doCreateUser = false;
        private boolean userCreated = false;

        public void setParentHandler(DefaultHandler parentHandler) {
            this.parentHandler = parentHandler;

            //reset data
            group = null;
            isNameSet = false;
            isDescriptionSet = false;
            username.setLength(0);
            password.setLength(0);
            email.setLength(0);
            name.setLength(0);
            creationDate.setLength(0);
            modifiedDate.setLength(0);
            nameVisible = false;
            emailVisible = false;
            properties = new HashMap();
            doCreateUser = false;
            userCreated = false;
        }
/*
        public void addProperty(String name, String value) {
            properties.put(name,value);
         }

        public void startElement(String uri, String localName, String tag,
                Attributes attribs)
                throws SAXParseException
        {
            if (localName.equals("Username")) {
                mode = USERNAME;
            }
            else if (localName.equals("Password")) {
                mode = PASSWORD;
            }
            else if (localName.equals("Email")) {
                mode = EMAIL;
                nameVisible = Boolean.valueOf(attribs.getValue(0)).booleanValue();
            }
            else if (localName.equals("Name")) {
                mode = NAME;
                emailVisible = Boolean.valueOf(attribs.getValue(0)).booleanValue();
            }
            else if (localName.equals("CreationDate")) {
                mode = CREATION_DATE;
            }
            else if (localName.equals("ModifiedDate")) {
                mode = MODIFIED_DATE;
            }
            else if (localName.equals("PropertyList")) {
                propertyListHandler.setParentHandler(this);
                propertyListHandler.setPropertyStore(this);
                parser.setContentHandler(propertyListHandler);
            }
        }

        public void characters(char[] buf, int start, int length)
                throws SAXParseException
        {
            switch (mode) {
                case USERNAME:
                    username.append(buf,start,length);
                    isUsernameSet = true;
                    break;
                case PASSWORD:
                    password.append(buf,start,length);
                    isPasswordSet = true;
                    break;
                case EMAIL:
                    email.append(buf,start,length);
                    isEmailSet = true;
                    break;
                case NAME:
                    name.append(buf,start,length);
                    break;
                case CREATION_DATE:
                    creationDate.append(buf,start,length);
                    break;
                case MODIFIED_DATE:
                    modifiedDate.append(buf,start,length);
                    break;
            }
            // check and see if we can create a message
            doCreateUser = (isUsernameSet && isPasswordSet && isEmailSet);
        }
        public void endElement(String uri, String localName, String tag)
                throws SAXParseException
        {
            // reset the mode now -- (fixes whitespace padding)
            mode = 0;

            if (localName.equals("GROUP") && doCreateGroup) {
                // create the user
                createGroup();
                // Let parent resume handling SAX events
                parser.setContentHandler(parentHandler);
            }
        }

        /*private void createGroup() {
            try {
                // Create the user. We pass in a map of properties at creation
                // time because this is the most efficient way to add
                // extended properties
                group = groupManager.createGroup(name);
                group.
                );
                // Set the password hash -- the XML file gives us the password
                // hash, not the original password
                newUser.setPasswordHash(password.toString());
                // Set the creation date of the user
                try {
                    newUser.setCreationDate(dateFormatter.parse(creationDate.toString()));
                } catch(ParseException pe) {}
                // Set the modified date of the user
                try {
                    newUser.setModifiedDate(dateFormatter.parse(creationDate.toString()));
                } catch(ParseException pe) {}
            }
            catch (UserAlreadyExistsException uaee) {
                //print("User '" + username + "' already exists.");
            }
            catch (UnauthorizedException ue) {
                ue.printStackTrace();
            }
        }*/
    }

    /**
     *
     */
    private class ForumListHandler extends DefaultHandler {

        private ContentHandler parentHandler;

        /**
         * Sets the parentHandler of the handler. Control will be passed back
         * to the parent when this handler is done parsing the XML it knows
         * how to handle.
         */
        public void setParentHandler(DefaultHandler parentHandler) {
            this.parentHandler = parentHandler;
        }

        public void startElement(String uri, String localName, String tag,
                Attributes attribs)
                throws SAXParseException
        {
            if (localName.equals("Forum")) {
                forumHandler.setParentHandler(this);
                parser.setContentHandler(forumHandler);
            }
        }

        public void endElement(String uri, String localName, String tag)
                throws SAXParseException
        {
            if (localName.equals("ForumList")) {
                parser.setContentHandler(parentHandler);
            }
        }
    }

    /**
     *
     */
    private class ForumHandler extends DefaultHandler implements PropertyStore {

        private boolean doCreateForum = false;
        private boolean forumCreated = false;

        private boolean isNameSet = false;
        private boolean isDescriptionSet = false;
        private boolean isCreationDateSet = false;
        private boolean isModifiedDateSet = false;

        private StringBuffer name = new StringBuffer();
        private StringBuffer description = new StringBuffer();
        private StringBuffer creationDate = new StringBuffer();
        private StringBuffer modifiedDate = new StringBuffer();

        private ContentHandler parentHandler;

        /**
         * Sets the parentHandler of the handler. Control will be passed back
         * to the parent when this handler is done parsing the XML it knows
         * how to handle.
         */
        public void setParentHandler(DefaultHandler parentHandler) {
            this.parentHandler = parentHandler;

            // Reset values.
            forum = null;
            forumCreated = false;
            doCreateForum = false;
            isNameSet = false;
            isDescriptionSet = false;
            isCreationDateSet = false;
            isModifiedDateSet = false;

            name.setLength(0);
            description.setLength(0);
            creationDate.setLength(0);
            modifiedDate.setLength(0);
        }

        public void addProperty(String name, String value) {
            try {
                forum.setProperty(name,value);
            }
            catch (UnauthorizedException ue) {
                ue.printStackTrace();
            }
            catch (Exception e) {
                print("Exception adding forum property, name=" + name +
                    ", value=" + value);
                e.printStackTrace();
            }
        }

        public void startElement(String uri, String localName, String tag,
                Attributes attribs)
                throws SAXParseException
        {
            if (localName.equals("Name")) {
                mode = NAME;
                isNameSet = true;
            }
            else if (localName.equals("Description")) {
                mode = DESCRIPTION;
                isDescriptionSet = true;
            }
            else if (localName.equals("CreationDate")) {
                mode = CREATION_DATE;
                isCreationDateSet = true;
            }
            else if (localName.equals("ModifiedDate")) {
                mode = MODIFIED_DATE;
                isModifiedDateSet = true;
            }
            else if (localName.equals("PermissionList")) {
                permissionListHandler.setParentHandler(this);
                parser.setContentHandler(permissionListHandler);
            }
            else if (localName.equals("PropertyList")) {
                propertyListHandler.setParentHandler(this);
                propertyListHandler.setPropertyStore(this);
                parser.setContentHandler(propertyListHandler);
            }
            else if (localName.equals("ThreadList")) {
                threadListHandler.setParentHandler(this);
                parser.setContentHandler(threadListHandler);
            }
        }

        public void characters(char[] buf, int start, int length)
                throws SAXParseException
        {
            switch (mode) {
                case NAME:
                    name.append(buf, start, length);
                    break;
                case DESCRIPTION:
                    description.append(buf, start, length);
                    break;
                case CREATION_DATE:
                    creationDate.append(buf, start, length);
                    break;
                case MODIFIED_DATE:
                    modifiedDate.append(buf, start, length);
                    break;
            }

            doCreateForum = (isNameSet && isCreationDateSet && isModifiedDateSet);
            if (doCreateForum && !forumCreated) {
                createForum();
                forumCreated = true;
            }
        }

        public void endElement(String uri, String localName, String tag)
                throws SAXParseException
        {
            // reset the mode now -- (fixes whitespace padding)
            mode = 0;

            if (localName.equals("Forum")) {
                // Let parent resume handling SAX events
                parser.setContentHandler(parentHandler);
            }
        }

        private void createForum() {
            try {
                // TODO: check to see if name or description is null
                forum = forumFactory.createForum(name.toString(),description.toString());

                Date cDate = parseDate(creationDate.toString());
                Date mDate = parseDate(modifiedDate.toString());

                forum.setCreationDate(cDate);
                forum.setModifiedDate(mDate);
            }
            catch (ForumAlreadyExistsException faee) {
                print("Forum \"" + name + "\" already exists.");
            }
            catch (Exception e) {
                print("Error creating forum.");
                e.printStackTrace();
            }
        }
    }

    /**
     *
     */
    private class PermissionListHandler extends DefaultHandler {

        private ContentHandler parentHandler;

        /**
         * Sets the parentHandler of the handler. Control will be passed back
         * to the parent when this handler is done parsing the XML it knows
         * how to handle.
         */
        public void setParentHandler(DefaultHandler parentHandler) {
            this.parentHandler = parentHandler;
        }

        public void startElement(String uri, String localName, String tag,
                Attributes attribs)
                throws SAXParseException
        {
            if (localName.equals("UserPermissionList")) {
                userPermissionListHandler.setParentHandler(this);
                parser.setContentHandler(userPermissionListHandler);
            }
            else if (localName.equals("GroupPermissionList")) {
                groupPermissionListHandler.setParentHandler(this);
                parser.setContentHandler(groupPermissionListHandler);
            }
        }

        public void endElement(String uri, String localName, String tag)
                throws SAXParseException
        {
            parser.setContentHandler(parentHandler);
        }
    }

    /**
     *
     */
    private class UserPermissionListHandler extends DefaultHandler {

        private ContentHandler parentHandler;

        /**
         * Sets the parentHandler of the handler. Control will be passed back
         * to the parent when this handler is done parsing the XML it knows
         * how to handle.
         */
        public void setParentHandler(DefaultHandler parentHandler) {
            this.parentHandler = parentHandler;
        }

        public void startElement(String uri, String localName, String tag,
                Attributes attribs)
                throws SAXParseException
        {
            if (localName.equals("UserPermission")) {
                String usertype = attribs.getValue("usertype");
                String username = attribs.getValue("username");
                String permission = attribs.getValue("permission");
                // Add the permission to the jive system
                User user = null;
                //Now, grant the user permission
                try {
                    int permType = ((Integer)jivePermissions.get(permission)).intValue();
                    if (usertype.equals("ANONYMOUS")) {
                        forum.getPermissionsManager().addAnonymousUserPermission(permType);
                    }
                    else if (usertype.equals("REGISTERED_USERS")) {
                        forum.getPermissionsManager().addRegisteredUserPermission(permType);
                    }
                    else {
                        try {
                            user = userManager.getUser(username);
                            forum.getPermissionsManager().addUserPermission(user,permType);
                        }
                        catch (UserNotFoundException unfe) {
                            print("User '" + username + "' not found, won't add user permission");
                        }
                    }
                }
                catch (UnauthorizedException e) {
                    print("Can't grant user permission, no authorization");
                }
            }
        }
        public void endElement(String uri, String localName, String tag)
                throws SAXParseException
        {
            if (localName.equals("UserPermissionList")) {
                parser.setContentHandler(parentHandler);
            }
        }
    }

    /**
     *
     */
    private class GroupPermissionListHandler extends DefaultHandler {

        private ContentHandler parentHandler;

        /**
         * Sets the parentHandler of the handler. Control will be passed back
         * to the parent when this handler is done parsing the XML it knows
         * how to handle.
         */
        public void setParentHandler(DefaultHandler parentHandler) {
            this.parentHandler = parentHandler;
        }

        public void startElement(String uri, String localName, String tag,
                Attributes attribs)
                throws SAXParseException
        {
            if (localName.equals("GroupPermission")) {
                String groupname = attribs.getValue("groupname");
                String permission = attribs.getValue("permission");
                // Add the permission to the jive system
                Group group = null;
                try {
                    group = groupManager.getGroup(groupname);
                }
                catch( GroupNotFoundException e ) {
                }
                // try to grant the group permission
                try {
                    forum.getPermissionsManager().addGroupPermission(
                        group,
                        ((Integer)jivePermissions.get(permission)).intValue()
                    );
                }
                catch (UnauthorizedException ue) {
                    print("Can't grant group permission, no authorization");
                }
            }
        }

        public void endElement(String uri, String localName, String tag)
                throws SAXParseException
        {
            parser.setContentHandler(parentHandler);
        }
    }

    /**
     *
     */
    private class ThreadListHandler extends DefaultHandler {

        private ContentHandler parentHandler;

        /**
         * Sets the parentHandler of the handler. Control will be passed back
         * to the parent when this handler is done parsing the XML it knows
         * how to handle.
         */
        public void setParentHandler(DefaultHandler parentHandler) {
            this.parentHandler = parentHandler;
        }

        public void startElement(String uri, String localName, String tag,
                Attributes attribs)
                throws SAXParseException
        {
            if (localName.equals("Thread")) {
                threadHandler.setParentHandler(this);
                parser.setContentHandler(threadHandler);
            }
        }

        public void endElement(String uri, String localName, String tag)
                throws SAXParseException
        {
            parser.setContentHandler(parentHandler);
        }
    }

    /**
     *
     */
    private class ThreadHandler extends DefaultHandler {

        private StringBuffer creationDate = new StringBuffer();
        private StringBuffer modifiedDate = new StringBuffer();

        private ContentHandler parentHandler;

        /**
         * Sets the parentHandler of the handler. Control will be passed back
         * to the parent when this handler is done parsing the XML it knows
         * how to handle.
         */
        public void setParentHandler(DefaultHandler parentHandler) {
            this.parentHandler = parentHandler;

            thread = null;
            threadHasRootMessage = false;
            creationDate.setLength(0);
            modifiedDate.setLength(0);
        }

        public void addProperty( String name, String value ) {
            try {
                thread.setProperty(name,value);
            }
            catch (UnauthorizedException ue) {
                ue.printStackTrace();
            }
        }

        public void startElement(String uri, String localName, String tag,
                Attributes attribs)
                throws SAXParseException
        {
            if (localName.equals("CreationDate")) {
                mode = CREATION_DATE;
            }
            else if (localName.equals("ModifiedDate")) {
                mode = MODIFIED_DATE;
            }
            else if (localName.equals("Message")) {
                messageHandler.addParentHandler(this);
                parser.setContentHandler(messageHandler);
            }
        }

        public void characters(char[] buf, int start, int length)
                throws SAXParseException
        {
            switch (mode) {
                case CREATION_DATE:
                    creationDate.append(buf, start, length);
                    break;
                case MODIFIED_DATE:
                    modifiedDate.append(buf, start, length);
                    break;
            }
        }

        public void endElement(String uri, String localName, String tag)
                throws SAXParseException
        {
            // reset the mode now -- (fixes whitespace padding)
            mode = 0;

            if (localName.equals("Thread")) {
                //Set the remaining properties of the thread (at this point,
                //the thread should have been created in the message handler)
                if (thread != null) {
                    try {
                        Date cDate = parseDate(creationDate.toString());
                        Date mDate = parseDate(modifiedDate.toString());
                        thread.setCreationDate(cDate);
                        thread.setModifiedDate(mDate);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                parser.setContentHandler(parentHandler);
            }
        }
    }

    /**
     *
     */
    private class MessageHandler extends DefaultHandler implements PropertyStore {

        private ForumMessage message = null;
        private ForumMessage parentMessage = null;

        private boolean isSubjectSet = false;
        private boolean isBodySet = false;
        private boolean isCreationDateSet = false;
        private boolean isModifiedDateSet = false;

        private boolean doCreateMessage = false;
        private boolean messageCreated = false;

        private StringBuffer subject = new StringBuffer();
        private StringBuffer body = new StringBuffer();
        private StringBuffer username = new StringBuffer();
        private StringBuffer creationDate = new StringBuffer();
        private StringBuffer modifiedDate = new StringBuffer();
        private HashMap properties = new HashMap();

        private LinkedList parentHandlerList = new LinkedList();

        public void addParentHandler(DefaultHandler parentHandler) {
            parentHandlerList.addFirst(parentHandler);

            //Now, reset all fields.
            message = null;
            parentMessage = null;
            isSubjectSet = false;
            isBodySet = false;
            isCreationDateSet = false;
            isModifiedDateSet = false;

            doCreateMessage = false;
            messageCreated = false;

            subject.setLength(0);
            body.setLength(0);
            username.setLength(0);
            creationDate.setLength(0);
            modifiedDate.setLength(0);
            properties = new HashMap();
        }

        public void setParentMessage(ForumMessage message) {
            parentMessage = message;
        }

        public void addProperty(String name, String value) {
            if (message != null) {
                try {
                    message.setProperty(name,value);
                }
                catch (UnauthorizedException ue) {}
            }
            else {
                print("Message was null, can't add property.");
            }
        }

        public void startElement(String uri, String localName, String tag,
                Attributes attribs)
                throws SAXParseException
        {
            // Check for various elements under a <Message> tag
            if (localName.equals("Subject")) {
                mode = SUBJECT;
                isSubjectSet = true;
            }
            else if (localName.equals("Body")) {
                mode = BODY;
                isBodySet = true;
            }
            else if (localName.equals("Username")) {
                mode = USERNAME;
            }
            else if (localName.equals("CreationDate")) {
                mode = CREATION_DATE;
                isCreationDateSet = true;
            }
            else if (localName.equals("ModifiedDate")) {
                mode = MODIFIED_DATE;
                isModifiedDateSet = true;
            }
            else if (localName.equals("PropertyList")) {
                // get any extended message properties.
                propertyListHandler.setParentHandler(this);
                propertyListHandler.setPropertyStore(this);
                parser.setContentHandler(propertyListHandler);
            }
            else if (localName.equals("MessageList")) {
                // get child messages (if any)
                messageListHandler.addParentHandler(this);
                messageListHandler.addMessage(message);
                parser.setContentHandler(messageListHandler);
            }
        }

        public void characters(char[] buf, int start, int length)
                throws SAXParseException
        {
            switch (mode) {
                case SUBJECT:
                    subject.append(buf, start, length);
                    break;
                case BODY:
                    body.append(buf, start, length);
                    break;
                case USERNAME:
                    username.append(buf, start, length);
                    break;
                case CREATION_DATE:
                    creationDate.append(buf, start, length);
                    break;
                case MODIFIED_DATE:
                    modifiedDate.append(buf, start, length);
                    break;
            }
            // check and see if we can create a message
            doCreateMessage = (isSubjectSet && isBodySet && isCreationDateSet && isModifiedDateSet);
        }

        public void endElement(String uri, String localName, String tag)
                throws SAXParseException
        {
            // reset the mode now -- (fixes whitespace padding)
            mode = 0;

            // create a message, if not created already
            if (doCreateMessage && !messageCreated) {
                createMessage();
                messageCreated = true;
            }

            if (localName.equals("Message")) {
                //Pop a parentHandler off the stack and return flow of control
                //to it.
                DefaultHandler parentHandler = (DefaultHandler)parentHandlerList.removeFirst();
                parser.setContentHandler(parentHandler);
            }
        }

        private void createMessage() {
            boolean userNotFound = false;
            //first, get the message user. If the user fails to load, we'll
            //make this user an 'anonymous' user
            String uName = username.toString();
            User user = null;
            if (!uName.equals("")) {
                try {
                    user = userManager.getUser(uName);
                }
                catch (UserNotFoundException unfe) {
                    userNotFound = true;
                }
            }
            try {
                if (user != null) {
                    message = forumFactory.createMessage(user);
                }
                else {
                    message = forumFactory.createMessage();
                }
                // set the properties of the message
                message.setSubject(subject.toString());
                message.setBody(body.toString());

                Date cDate = parseDate(creationDate.toString());
                Date mDate = parseDate(modifiedDate.toString());
                message.setCreationDate(cDate);
                message.setModifiedDate(mDate);
                if(userNotFound) {
                    addProperty("username",uName);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            // set this message as the root message of the current
            // thread if the current thread doesn't have a root message
            if (!threadHasRootMessage) {
                try {
                    thread = forumFactory.createThread(message);
                    forum.addThread(thread);
                    threadHasRootMessage = true;
                }
                catch (UnauthorizedException ue) {
                    ue.printStackTrace();
                }
            }
            else {
                if (parentMessage != null) {
                    try {
                        thread.addMessage(parentMessage,message);
                    }
                    catch (UnauthorizedException ue) {}
                }
            }
        }
    }

    /**
     *
     */
    private class MessageListHandler extends DefaultHandler {

        private LinkedList messageList = new LinkedList();
        private LinkedList parentHandlerList = new LinkedList();

        public void addParentHandler(DefaultHandler parentHandler) {
            parentHandlerList.addFirst(parentHandler);
        }

        public void addMessage(ForumMessage message) {
            messageList.addFirst(message);
        }

        public void startElement(String uri, String localName, String tag, Attributes attribs)
                throws SAXParseException
        {
            if (localName.equals("Message")) {
                messageHandler.addParentHandler(this);
                ForumMessage parentMessage = (ForumMessage)messageList.getFirst();
                messageHandler.setParentMessage(parentMessage);
                parser.setContentHandler(messageHandler);
            }
        }
        public void endElement(String uri, String localName, String tag)
                throws SAXParseException
        {
            if (localName.equals("MessageList")) {
                //Pop a parentHandler off the stack and return flow of control
                //to it. We also pop a message of the messageList since it will
                //no longer need to be used.
                DefaultHandler parentHandler = (DefaultHandler)parentHandlerList.removeFirst();
                messageList.removeFirst();
                parser.setContentHandler(parentHandler);
            }
        }
    }

    /**
     *
     */
    private class PropertyListHandler extends DefaultHandler {

        private PropertyStore propertyStore;
        private ContentHandler parentHandler;

        /**
         * Sets the parent handler of the handler. Control will be passed back
         * to the parent when this handler is done parsing the XML it knows
         * how to handle.
         */
        public void setParentHandler(DefaultHandler parentHandler) {
            this.parentHandler = parentHandler;
        }

        /**
         * Sets the property store that properties will be sent to.
         */
        public void setPropertyStore(PropertyStore propertyStore) {
            this.propertyStore = propertyStore;
        }

        public void startElement(String uri, String localName, String tag, Attributes attribs)
                throws SAXParseException
        {
            if (localName.equals("Property")) {
                String propName = attribs.getValue("name");
                String propValue = attribs.getValue("value");
                propertyStore.addProperty(propName,propValue);
            }
        }

        public void endElement(String uri, String localName, String tag)
                throws SAXParseException
        {
            if (localName.equals("PropertyList")) {
                parser.setContentHandler(parentHandler);
            }
        }
    }

    /**
     *
     */
    private interface PropertyStore {
        public void addProperty( String name, String value );
    }

}
