/**
 * $RCSfile: JNDIDataSourceProvider.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:01 $
 *
 * Copyright (C) 1999-2001 CoolServlets Inc. All rights reserved.
 * ===================================================================
 * The Jive Software License (based on Apache Software License, Version 1.1)
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by
 *        Jive Software (http://www.jivesoftware.com)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Jive" and "CoolServlets" must not be used to
 *    endorse or promote products derived from this software without
 *    prior written permission. For written permission, please
 *    contact webmaster@coolservlets.com.
 *
 * 5. Products derived from this software may not be called "Jive",
 *    nor may "Jive" appear in their name, without prior written
 *    permission of CoolServlets.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL COOLSERVLETS INC OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 * This software consists of voluntary contributions made by many
 * individuals on behalf of Jive Software. For more information
 * on Jive Software please visit http://www.jivesoftware.com.
 */

package com.jivesoftware.forum.database;

import java.sql.*;
import javax.sql.*;
import java.util.*;
import javax.naming.*;
import com.jivesoftware.forum.*;

/**
 * An implementation of ConnectionProvider that utilizes a JDBC 2.0 DataSource
 * made available via JNDI. This is useful for application servers where a pooled
 * data connection is already provided so Jive can share the pool with the
 * other applications.<p>
 *
 * The JNDI location of the DataSource stored as the Jive property
 * <code>database.JNDIProvider.name</code>. This can be overridden by setting
 * the provider's <code>name</code> property if required.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 *
 * @see ConnectionProvider
 */
public class JNDIDataSourceProvider implements ConnectionProvider {

    private Hashtable properties;
    private DataSource dataSource;

    private static final boolean DEBUG = false;

    /**
     * Keys of JNDI properties to query PropertyManager for.
     */
    private static final String[] jndiPropertyKeys = {
        Context.APPLET ,
        Context.AUTHORITATIVE ,
        Context.BATCHSIZE ,
        Context.DNS_URL ,
        Context.INITIAL_CONTEXT_FACTORY ,
        Context.LANGUAGE ,
        Context.OBJECT_FACTORIES ,
        Context.PROVIDER_URL ,
        Context.REFERRAL ,
        Context.SECURITY_AUTHENTICATION ,
        Context.SECURITY_CREDENTIALS ,
        Context.SECURITY_PRINCIPAL ,
        Context.SECURITY_PROTOCOL ,
        Context.STATE_FACTORIES ,
        Context.URL_PKG_PREFIXES
    };

    /**
     * Initialize.
     */
    public JNDIDataSourceProvider() {
        properties = new Properties();
        setProperty("name",JiveGlobals.getJiveProperty("database.JNDIProvider.name"));
    }

    public String getName() {
        return "JNDI DataSource Connection Provider";
    }

    public String getDescription() {
        return "Connection Provider for Jive to lookup pooled "
        + "DataSource from JNDI location. Requires 'name' "
        + "property with JNDI location. This can be set in "
        + "the properties file as 'JNDIDataSource.name'";
    }

    public String getAuthor() {
        return "Joe Walnes - joe@truemesh.com";
    }

    public int getMajorVersion() {
        return 2;
    }

    public int getMinorVersion() {
        return 0;
    }

    public boolean isPooled() {
        return true;
    }

    public void start() {
        debug("start()");
        String name = getProperty("name");
        if (name==null || name.length()==0) {
            error("No name specified for DataSource JNDI lookup - 'name' " +
            "Property should be set.", null);
            return;
        }
        try {
            Properties contextProperties = new Properties();
            for (int i=0; i<jndiPropertyKeys.length; i++) {
                String k = jndiPropertyKeys[i];
                String v = JiveGlobals.getJiveProperty(k);
                if (v != null) {
                    contextProperties.setProperty(k,v);
                }
            }
            Context context = new InitialContext(contextProperties);
            dataSource = (DataSource) context.lookup( name );
        }
        catch (Exception e) {
            error("Could not lookup DataSource at '" + name + "'",e);
        }
    }

    public void restart() {
        debug("restart()");
        destroy();
        start();
    }

    public void destroy() {
        debug("destroy()");
        String name = getProperty("name");
        if (name!=null && name.length()>0) {
            JiveGlobals.setJiveProperty("database.JNDIProvider.name", name);
        }
    }

    public Connection getConnection() {
        debug("getConnection()");
        if (dataSource==null) {
            error("DataSource has not yet been looked up",null);
            return null;
        }
        try {
            return dataSource.getConnection();
        }
        catch (SQLException e) {
            error("Could not retrieve Connection from DataSource",e);
            return null;
        }
    }

    public String getProperty(String name) {
        debug("getProperty('"+name+"+')");
        return (String)properties.get(name);
    }

    public void setProperty(String name, String value) {
        debug("setProperty('"+name+"+','"+value+"')");
        properties.put(name,value);
    }

    public Iterator propertyNames() {
        debug("propertyNames()");
        return properties.keySet().iterator();
    }

    public String getPropertyDescription(String name) {
        debug("getPropertyDescription('"+name+"')");
        if (name.equals("name")) {
            return "JNDI name to lookup. eg: java:comp/env/jdbc/MyDataSource";
        }
        else {
            return null;
        }
    }

   /**
    * Log an error.
    *
    * @param msg Description of error
    * @param e Exception to printStackTrace (may be null)
    */
    private final void error(String msg, Exception e) {
        System.err.println("Error: " + msg);
        if (e != null) {
            e.printStackTrace();
        }
    }

    /**
     * Display messages for debugging
     */
    private final void debug(String msg) {
        if (DEBUG) {
            System.err.println("DEBUG: " + msg);
        }
    }
}