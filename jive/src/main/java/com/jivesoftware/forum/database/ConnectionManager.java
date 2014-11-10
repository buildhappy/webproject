/**
 * $RCSfile: ConnectionManager.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:48 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import java.sql.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import com.jivesoftware.forum.*;

/**
 * Central manager of database connections. All methods are static so that they
 * can be easily accessed throughout the classes in the database package.<p>
 *
 * This class also provides a set of utility methods that abstract out
 * operations that may not work on all databases such as setting the max number
 * or rows that a query should return.
 *
 * @see ConnectionProvider
 */
public class ConnectionManager {

    private static ConnectionProvider connectionProvider;
    private static Object providerLock = new Object();

    // True if connection profiling is turned on. Always false by default.
    private static boolean profilingEnabled = false;

    // True if the database support transactions.
    protected static boolean supportsTransactions;
    // True if the database requires large text fields to be streamed.
    protected static boolean streamLargeText;
    // True if the database supports the Statement.setMaxRows() method.
    protected static boolean supportsMaxRows;
    // True if the database supports the Statement.setFetchSize() method.
    protected static boolean supportsFetchSize;
    // True if the database supports correlated subqueries.
    protected static boolean supportsSubqueries;

    private static DatabaseType databaseType = DatabaseType.OTHER;

    static {
        // Add a shutdown hook to the VM if we're running JDK 1.3. When the
        // thread is executed, it will call the destroy() method of the
        // current connection provider. This is necessary for some connection
        // providers -- especially those for in-VM Java databases.
        Runtime runtime = Runtime.getRuntime();
        Class c = runtime.getClass();
        try {
            Method m = c.getMethod("addShutdownHook", new Class[] { Thread.class } );
            m.invoke(runtime, new Object[] { new ShutdownThread() });
        }
        catch (NoSuchMethodException nsme) {
            // Ignore -- the user might not be running JDK 1.3.
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a database connection from the currently active connection
     * provider.
     */
    public static Connection getConnection() throws SQLException {
        if (connectionProvider == null) {
            synchronized (providerLock) {
                if (connectionProvider == null) {
                    // Attempt to load the connection provider classname as
                    // a Jive property.
                    String className =
                        JiveGlobals.getJiveProperty("connectionProvider.className");
                    if (className != null) {
                        // Attempt to load the class.
                        try {
                            Class conClass = Class.forName(className);
                            setConnectionProvider((ConnectionProvider)conClass.newInstance());
                        }
                        catch(Exception e) {
                            e.printStackTrace();
                            System.err.println("Warning: failed to create the " +
                                "connection provider specified by connection" +
                                "Provider.className. Using the default pool.");
                            setConnectionProvider(new DefaultConnectionProvider());
                        }
                    }
                    else {
                        setConnectionProvider(new DefaultConnectionProvider());
                    }
                }
            }
        }
        Connection con = connectionProvider.getConnection();
        if (con == null) {
            System.err.println("WARNING: ConnectionManager.getConnection() " +
                "failed to obtain a connection.");
        }
        // See if profiling is enabled. If yes, wrap the connection with a
        // profiled connection.
        //if (profilingEnabled) {
            //return new ProfiledConnection(con);
        //}
        //else {
            return con;
        //}
    }

    /**
     * Returns a Connection from the currently active connection provider that
     * is ready to participate in transactions (auto commit is set to false).
     */
    public static Connection getTransactionConnection() throws SQLException {
        Connection con = getConnection();
        if (supportsTransactions) {
            con.setAutoCommit(false);
        }
        return con;
    }

    /**
     * Closes a Connection. However, it first rolls back the transaction or
     * commits it depending on the value of <code>abortTransaction</code>.
     */
    public static void closeTransactionConnection(Connection con,
            boolean abortTransaction)
    {
        // Rollback or commit the transaction
        if (supportsTransactions) {
            try {
                if (abortTransaction) {
                    con.rollback();
                }
                else {
                    con.commit();
                }
            }
            catch (Exception e) { e.printStackTrace(); }
        }
        try {
            // Reset the connection to auto-commit mode.
            if (supportsTransactions) {
                con.setAutoCommit(true);
            }
            // Finally, close the db connection.
            con.close();
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Returns the current connection provider. The only case in which this
     * method should be called is if more information about the current
     * connection provider is needed. Database connections should always be
     * obtained by calling the getConnection method of this class.
     */
    public static ConnectionProvider getConnectionProvider() {
        return connectionProvider;
    }

    /**
     * Sets the connection provider. The old provider (if it exists) is shut
     * down before the new one is started. A connection provider <b>should
     * not</b> be started before being passed to the connection manager
     * because the manager will call the start() method automatically.
     *
     * @param provider the ConnectionProvider that the manager should obtain
     *      connections from.
     */
    public static void setConnectionProvider(ConnectionProvider provider) {
        synchronized (providerLock) {
            if (connectionProvider != null) {
                connectionProvider.destroy();
                connectionProvider = null;
            }
            connectionProvider = provider;
            connectionProvider.start();
            // Now, get a connection to determine meta data.
            Connection con = null;
            try {
                con = connectionProvider.getConnection();
                setMetaData(con);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                try {  con.close();  } catch (Exception e) { }
            }
        }
        // Remember what connection provider we want to use for restarts.
        JiveGlobals.setJiveProperty("connectionProvider.className",
                provider.getClass().getName());
    }

    /**
     * Retrives a large text column from a result set, automatically performing
     * streaming if the JDBC driver requires it. This is necessary because
     * different JDBC drivers have different capabilities and methods for
     * retrieving large text values.
     *
     * @param rs the ResultSet to retrieve the text field from.
     * @param columnIndex the column in the ResultSet of the text field.
     * @return the String value of the text field.
     */
    public static String getLargeTextField(ResultSet rs, int columnIndex)
            throws SQLException
    {
        if (streamLargeText) {
            Reader bodyReader = null;
            String value = null;
            try {
                bodyReader = rs.getCharacterStream(columnIndex);
                if (bodyReader == null) {
                    return null;
                }
                char [] buf = new char[256];
                int len;
                StringWriter out = new StringWriter(256);
                while ((len = bodyReader.read(buf)) >= 0) {
                    out.write(buf, 0, len);
                }
                value = out.toString();
                out.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                throw new SQLException("Failed to load text field");
            }
            finally {
                try { bodyReader.close(); }
                catch(Exception e) { }
            }
            return value;
        }
        else {
            return rs.getString(columnIndex);
        }
    }

    /**
     * Sets a large text column in a result set, automatically performing
     * streaming if the JDBC driver requires it. This is necessary because
     * different JDBC drivers have different capabilities and methods for
     * setting large text values.
     *
     * @param pstmt the PreparedStatement to set the text field in.
     * @param parameterIndex the index corresponding to the text field.
     * @param value the String to set.
     */
    public static void setLargeTextField(PreparedStatement pstmt,
            int parameterIndex, String value) throws SQLException
    {
        if (streamLargeText) {
            Reader bodyReader = null;
            try {
                bodyReader = new StringReader(value);
                pstmt.setCharacterStream(parameterIndex, bodyReader, value.length());
            }
            catch (Exception e) {
                e.printStackTrace();
                throw new SQLException("Failed to set text field.");
            }
            // Leave bodyReader open so that the db can read from it. It *should*
            // be garbage collected after it's done without needing to call close.
        }
        else {
            pstmt.setString(parameterIndex, value);
        }
    }

    /**
     * Sets the max number of rows that should be returned from executing a
     * statement. The operation is automatically bypassed if Jive knows that the
     * the JDBC driver or database doesn't support it.
     *
     * @param stmt the Statement to set the max number of rows for.
     * @param maxRows the max number of rows to return.
     */
    public static void setMaxRows(Statement stmt, int maxRows)
            throws SQLException
    {
        if (supportsMaxRows) {
            try {
                stmt.setMaxRows(maxRows);
            }
            catch (Throwable t) {
                // Ignore. Exception may happen if the driver doesn't support
                // this operation and we didn't set meta-data correctly.
                // However, it is a good idea to update the meta-data so that
                // we don't have to incur the cost of catching an exception
                // each time.
            }
        }
    }

    /**
     * Sets the number of rows that the JDBC driver should buffer at a time.
     * The operation is automatically bypassed if Jive knows that the
     * the JDBC driver or database doesn't support it.
     *
     * @param rs the ResultSet to set the fetch size for.
     * @param fetchSize the fetchSize.
     */
    public static void setFetchSize(ResultSet rs, int fetchSize)
            throws SQLException
    {
        if (supportsFetchSize) {
            try {
                rs.setFetchSize(fetchSize);
            }
            catch (Throwable t) {
                // Ignore. Exception may happen if the driver doesn't support
                // this operation and we didn't set meta-data correctly.
                // However, it is a good idea to update the meta-data so that
                // we don't have to incur the cost of catching an exception
                // each time.
            }
        }
    }

    /**
     * Uses a connection from the database to set meta data information about
     * what different JDBC drivers and databases support.
     */
    private static void setMetaData(Connection con) throws SQLException {
         DatabaseMetaData metaData = con.getMetaData();
        // Supports transactions?
        supportsTransactions = metaData.supportsTransactions();
        // Supports subqueries?
        supportsSubqueries = metaData.supportsCorrelatedSubqueries();

        // Set defaults for other meta properties
        streamLargeText = false;
        supportsMaxRows = true;
        supportsFetchSize = true;

        // Get the database name so that we can perform meta data settings.
        String dbName = metaData.getDatabaseProductName().toLowerCase();
        String driverName = metaData.getDriverName().toLowerCase();

        // Oracle properties.
        if (dbName.indexOf("oracle") != -1) {
            databaseType = DatabaseType.ORACLE;
            streamLargeText = true;
            // The i-net AUGURO JDBC driver
            if (driverName.indexOf("auguro") != -1) {
                streamLargeText = false;
                supportsFetchSize = true;
                supportsMaxRows = false;
            }
        }
        // Postgres properties
        else if (dbName.indexOf("postgres") != -1) {
            supportsFetchSize = false;
        }
        // Interbase properties
        else if (dbName.indexOf("interbase") != -1) {
            supportsFetchSize = false;
            supportsMaxRows = false;
        }
        // SQLServer, JDBC driver i-net UNA properties
        else if (dbName.indexOf("sql server") != -1 &&
            driverName.indexOf("una") != -1)
        {
            supportsFetchSize = true;
            supportsMaxRows = false;
        }
        // MySQL properties
        else if (dbName.indexOf("mysql") != -1) {
            databaseType = DatabaseType.MYSQL;
        }
    }

    /**
     * Returns the database type. The possible types are constants of the
     * DatabaseType class. Any database that doesn't have its own constant
     * falls into the "Other" category.
     *
     * @return the database type.
     */
    public static DatabaseType getDatabaseType() {
        return databaseType;
    }

    /**
     * Returns true if connection profiling is turned on. You can collect
     * profiling statistics by using the static methods of the ProfiledConnection
     * class.
     *
     * @return true if connection profiling is enabled.
     */
    public static boolean isProfilingEnabled() {
        return profilingEnabled;
    }

    /**
     * Turns connection profiling on or off. You can collect profiling
     * statistics by using the static methods of the ProfiledConnection
     * class.
     *
     * @param profilingEnabled true to enable profiling; false to disable.
     */
    public static void setProfilingEnabled(boolean enable) {
        // If enabling profiling, call the start method on ProfiledConnection
        if (!profilingEnabled && enable) {
//            ProfiledConnection.start();
        }
        // Otherwise, if turning off, call stop method.
        else if(profilingEnabled && !enable) {
//            ProfiledConnection.stop();
        }
        profilingEnabled = enable;
     }

    /**
     * A class that identifies the type of the database that Jive is connected
     * to. In most cases, we don't want to make any database specific calls
     * and have no need to know the type of database we're using. However,
     * there are certain cases where it's critical to know the database for
     * performance reasons.
     */
    public static class DatabaseType {

        public static final DatabaseType ORACLE = new DatabaseType();
        public static final DatabaseType MYSQL = new DatabaseType();
        public static final DatabaseType OTHER = new DatabaseType();

        private DatabaseType() {
            /* do nothing */
        }
    }

    /**
     * Shuts down the current connection provider. It should be called when
     * the VM is exiting so that any necessary cleanup can be done.
     */
    private static class ShutdownThread extends Thread {
        public void run() {
            ConnectionProvider provider = ConnectionManager.getConnectionProvider();
            if (provider != null) {
                provider.destroy();
            }
        }
    }
}