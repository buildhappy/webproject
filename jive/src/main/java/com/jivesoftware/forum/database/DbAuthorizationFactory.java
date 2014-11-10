/**
 * $RCSfile: DbAuthorizationFactory.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:49 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import com.jivesoftware.forum.Authorization;
import com.jivesoftware.forum.AuthorizationFactory;
import com.jivesoftware.forum.UnauthorizedException;
import com.jivesoftware.util.StringUtils;

import java.sql.*;

/**
 * A subclass of AuthorizationFactory for the default Jive implementation. It makes an
 * SQL query to the Jive user table to see if the supplied username and password
 * match a user record. If they do, the appropaite Authorization token is
 * returned. If no matching User record is found an UnauthorizedException is
 * thrown.<p>
 *
 * Because each call to createAuthorization(String, String) makes a database
 * connection, Authorization tokens should be cached whenever possible. When
 * using a servlet or JSP skins, a good method is to cache the token in the
 * session. The SkinUtils.getUserAuthorization() methods automatically handles
 * this logic.<p>
 *
 * If you wish to integrate Jive with your own user system, you'll need to
 * either modify the class or provide your own implementation of the
 * AuthorizationFactory interface.
 */
public class DbAuthorizationFactory extends AuthorizationFactory {

    /** DATABASE QUERIES **/
    private static final String AUTHORIZE =
        "SELECT userID FROM jiveUser WHERE username=? AND passwordHash=?";

    /**
     * The same token can be used for all anonymous users, so cache it.
     */
    private static final Authorization anonymousAuth = new DbAuthorization(-1);

    /**
     * Creates Authorization tokens for users. This method is implemented by
     * concrete subclasses of AuthorizationFactory.
     *
     * @param username the username to create an Authorization with.
     * @param password the password to create an Authorization with.
     * @return an Authorization token if the username and password are correct.
     * @throws UnauthorizedException if the username and password do not match
     *      any existing user.
     */
    public Authorization createAuthorization(String username, String password)
            throws UnauthorizedException
    {
        if (username == null || password == null) {
            throw new UnauthorizedException();
        }
        //Jive stores all passwords in hashed form. So, hash the plain text
        //password for comparison.
        password = StringUtils.hash(password);
        long userID = 0;
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = ConnectionManager.getConnection();
            pstmt = con.prepareStatement(AUTHORIZE);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            //If the query had no results, the username and password
            //did not match a user record. Therefore, throw an exception.
            if (!rs.next()) {
                throw new UnauthorizedException();
            }
            userID = rs.getLong(1);
        }
        catch( SQLException sqle ) {
            System.err.println("Exception in DbAuthorizationFactory:" + sqle);
            sqle.printStackTrace();
            throw new UnauthorizedException();
        }
        finally {
            try {  pstmt.close(); }
            catch (Exception e) { e.printStackTrace(); }
            try {  con.close();   }
            catch (Exception e) { e.printStackTrace(); }
        }
        //Got this far, so the user must be authorized.
        return new DbAuthorization(userID);
    }

    /**
     * Creates anonymous Authorization tokens.
     *
     * @return an anonymous Authorization token.
     */
    public Authorization createAnonymousAuthorization() {
        return anonymousAuth;
    }
}
