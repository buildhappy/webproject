/**
 * $RCSfile: JiveServlet.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:07 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.util;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;

import com.jivesoftware.forum.*;
import com.jivesoftware.forum.database.*;

/**
 * A servlet that is used to initialize and destroy Jive resources. This servlet
 * should be registered to be loaded at appserver startup. Below is a sample
 * entry in a Servlet 2.2's webapp web.xml file:<p>
 * <pre>
 *  &lt;servlet
 *      servlet-name="JiveServlet"
 *      servlet-class="com.jivesoftware.forum.util.JiveServlet"
 *  &gt;
 *      &lt;load-on-startup/&gt;
 *  &lt;/servlet&gt;
 * </pre>
 */
public class JiveServlet extends HttpServlet {

    /**
     * This method will set the "jiveHome" property if its passed in as an
     * init parameter to this servlet.
     */
    public void init(ServletConfig config) throws ServletException {
        // Get jiveHome as a web app parameter.
        String jiveHome = config.getServletContext().getInitParameter("jiveHome");
        // If that failed, see if it's set as a servlet param.
        if (jiveHome == null) {
            jiveHome = config.getInitParameter("jiveHome");
        }

        // If we found jiveHome, set it in JiveGlobals.
        if (jiveHome != null) {
            jiveHome = jiveHome.trim();
            // Remove trailing slashes.
            while (jiveHome.endsWith("/") || jiveHome.endsWith("\\")) {
                jiveHome = jiveHome.substring(0, jiveHome.length()-1);
            }
            JiveGlobals.jiveHome = jiveHome;
        }
    }

    public void service(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            out.println("<html><head><title>Jive Forums</title></head>");
            out.println("<body><font face=\"Arial, Helvetica\">");
            out.println("<b>Jive Forums</b><br>");
            out.println("<font size=\"-1\"> Version: " + JiveGlobals.getJiveVersion());
            out.println("</font></font></body></html>");
            out.close();
        }
        catch (IOException ioe) { }
    }
}