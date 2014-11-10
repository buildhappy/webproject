/**
 * $RCSfile: ConnectionAdapter.java,v $
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
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * An adapter for the connection interface. This is necessary so that other
 * classes that we create that implement the Connection interface will
 * compile with JDK 1.3 or JDK 1.4
 */
public class ConnectionAdapter implements Connection {

    protected Connection connection;

    public ConnectionAdapter(Connection connection) {
        this.connection = connection;
    }

    public void close() throws SQLException {
        connection.close();
    }

    public String toString() {
        return connection.toString();
    }

    public void setHoldability(int holdability) throws SQLException {
        connection.setHoldability(holdability);
    }
    public int getHoldability() throws SQLException{
    	return connection.getHoldability();
    }
    public Savepoint setSavepoint() throws SQLException{
        return connection.setSavepoint();
    }
    public Savepoint setSavepoint(String name) throws SQLException{
        return connection.setSavepoint(name)	;
    }
    public void rollback(Savepoint savepoint)
              throws SQLException{
              connection.rollback(savepoint);
              }
    public void releaseSavepoint(Savepoint savepoint)
                      throws SQLException{
               connection.releaseSavepoint(savepoint);
                      }

    public Statement createStatement(int resultSetType,
                                 int resultSetConcurrency,
                                 int resultSetHoldability)
                          throws SQLException{
                return connection.createStatement(resultSetType,resultSetConcurrency,resultSetHoldability);
                          }
     public PreparedStatement prepareStatement(String sql,
                                          int resultSetType,
                                          int resultSetConcurrency,
                                          int resultSetHoldability)
                                   throws SQLException{
                   return connection.prepareStatement(sql,resultSetType,resultSetConcurrency,resultSetHoldability);
                                   }

     public CallableStatement prepareCall(String sql,
                                     int resultSetType,
                                     int resultSetConcurrency,
                                     int resultSetHoldability)
                              throws SQLException {
                   return connection.prepareCall(sql,resultSetType,resultSetConcurrency,resultSetHoldability);
                              }

     public PreparedStatement prepareStatement(String sql,
                                          int resultSetConcurrency)
                                   throws SQLException{
                   return connection.prepareStatement(sql,resultSetConcurrency);
                                   }


      public PreparedStatement prepareStatement(String sql,
                                          int[] columnIndexes)
                                   throws SQLException{
                   return connection.prepareStatement(sql,columnIndexes);
                                   	}

      public PreparedStatement prepareStatement(String sql,
                                          String[] columnNames)
                                   throws SQLException{
                     return connection.prepareStatement(sql,columnNames);
                                   	}


    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        return connection.prepareCall(sql);
    }

    public String nativeSQL(String sql) throws SQLException {
        return connection.nativeSQL(sql);
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }

    public boolean getAutoCommit() throws SQLException {
        return connection.getAutoCommit();
    }

    public void commit() throws SQLException {
        connection.commit();
    }

    public void rollback() throws SQLException {
        connection.rollback();
    }

    public boolean isClosed() throws SQLException {
        return connection.isClosed();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return connection.getMetaData();
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        connection.setReadOnly(readOnly);
    }

    public boolean isReadOnly() throws SQLException {
        return connection.isReadOnly();
    }

    public void setCatalog(String catalog) throws SQLException {
        connection.setCatalog(catalog);
    }

    public String getCatalog() throws SQLException {
        return connection.getCatalog();
    }

    public void setTransactionIsolation(int level) throws SQLException {
        connection.setTransactionIsolation(level);
    }

    public int getTransactionIsolation() throws SQLException {
        return connection.getTransactionIsolation();
    }

    public SQLWarning getWarnings() throws SQLException {
        return connection.getWarnings();
    }

    public void clearWarnings() throws SQLException {
        connection.clearWarnings();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency)
            throws SQLException
    {
        return connection.createStatement(resultSetType, resultSetConcurrency);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType,
            int resultSetConcurrency) throws SQLException
    {
        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    public CallableStatement prepareCall(String sql, int resultSetType,
            int resultSetConcurrency) throws SQLException
    {
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    public Map getTypeMap() throws SQLException {
        return connection.getTypeMap();
    }

    public void setTypeMap(Map map) throws SQLException {
        connection.setTypeMap(map);
    }

	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void abort(Executor executor) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Blob createBlob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Clob createClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public NClob createNClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLXML createSQLXML() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Properties getClientInfo() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getClientInfo(String name) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNetworkTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getSchema() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isValid(int timeout) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void setClientInfo(Properties properties)
			throws SQLClientInfoException {
		// TODO Auto-generated method stub
		
	}

	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {
		// TODO Auto-generated method stub
		
	}

	public void setNetworkTimeout(Executor executor, int milliseconds)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setSchema(String schema) throws SQLException {
		// TODO Auto-generated method stub
		
	}
}