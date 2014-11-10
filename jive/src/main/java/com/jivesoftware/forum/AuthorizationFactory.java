/**
 * $RCSfile: AuthorizationFactory.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:41 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

/**
 * An abstract class that defines a framework for providing authorization
 * services in Jive. The static getAuthorization(String,String) and
 * getAnonymousAuthorization() methods should be called directly from
 * applications using Jive in order to obtain Authorization tokens.<p>
 *
 * Users of Jive that wish to change the AuthorizationFactory implementation
 * used to generate tokens can set the <code>AuthorizationFactory.className</code>
 * Jive property. For example, if you have altered Jive to use LDAP for user
 * information, you'd want to write a custom implementation of
 * AuthorizationFactory to make LDAP authorization queries. After changing the
 * <code>AuthorizationFactory.className</code> Jive property, you must restart
 * your application server.
 */
public abstract class AuthorizationFactory {

    /**
     * The default class to instantiate is database implementation.
     */
    private static String className =
        "com.jivesoftware.forum.database.DbAuthorizationFactory";

    private static AuthorizationFactory factory = null;

    /**
     * Returns the Authorization token associated with the specified username
     * and password. If the username and password do not match the record of
     * any user in the system, the method throws an UnauthorizedException.<p>
     *
     * When using most implementations of this class, authorization tokens
     * should be cached. A convenient place to store a token is often in the
     * HttpSession.
     *
     * @param username the username to create an Authorization with.
     * @param password the password to create an Authorization with.
     * @return an Authorization token if the username and password are correct.
     * @throws UnauthorizedException if the username and password do not match
     *      any existing user.
     */
    public static Authorization getAuthorization(String username,
            String password) throws UnauthorizedException
    {
        loadAuthorizationFactory();
        return factory.createAuthorization(username, password);
    }

    /**
     * Returns the anonymous user Authorization.
     *
     * @return an anonymous Authorization token.
     */
    public static Authorization getAnonymousAuthorization() {
        loadAuthorizationFactory();
        return factory.createAnonymousAuthorization();
    }

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
    protected abstract Authorization createAuthorization(String username,
            String password) throws UnauthorizedException;

    /**
     * Creates anonymous Authorization tokens. This method is implemented by
     * concrete subclasses AuthorizationFactory.
     *
     * @return an anonymous Authorization token.
     */
    protected abstract Authorization createAnonymousAuthorization();

    /**
     * Loads a concrete AuthorizationFactory that can be used generate
     * Authorization tokens for authorized users.<p>
     *
     * By default, the implementation used will be an instance of
     * DbAuthorizationFactory -- the standard database implementation that uses
     * the Jive user table. A different factory can be specified by setting the
     * Jive property "AuthorizationFactory.className". However, you must
     * restart Jive for any change to take effect.
     */
    private static void loadAuthorizationFactory() {
        if (factory == null) {
            //Use className as a convenient object to get a lock on.
            synchronized(className) {
                if (factory == null) {
                    //See if the classname has been set as a Jive property.
                    String classNameProp = JiveGlobals.getJiveProperty(
                            "AuthorizationFactory.className");
                    if (classNameProp != null) {
                        className = classNameProp;
                    }
                    try {
                        Class c = Class.forName(className);
                        factory = (AuthorizationFactory)c.newInstance();
                    }
                    catch (Exception e) {
                        System.err.println("Exception loading class: " + e);
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
