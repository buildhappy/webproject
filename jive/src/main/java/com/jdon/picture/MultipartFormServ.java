package com.jdon.picture;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class MultipartFormServ extends HttpServlet {
  static final private String CONTENT_TYPE = "text/html";

  private static MultipartFormHandle mf = MultipartFormHandle.getInstance();

  private String dirName;
  private ServletContext context;

  //Initialize global variables
  public void init() throws ServletException {

    // read the uploadDir from the servlet parameters
    dirName = mf.getUploaddir();
    if (dirName == null) {
      throw new ServletException("Please supply uploadDir parameter");
    }

  }
  //Process the HTTP Get request
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType(CONTENT_TYPE);
    doGet(request, response);
  }
  //Process the HTTP Post request
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
       mf.init(dirName,request);

       String forward=mf.getForwardProgram();
      if (forward.equals(""))
      {
         errorMessage("no forward program", response);
         return;
      }
      String param=mf.getForwardProgramParam();
      mf.clear();
//      errorMessage(forward+"?"+param, response);

      getServletConfig().getServletContext().getRequestDispatcher("/"+forward+"?"+param).forward(request, response);

    }catch (Exception Ex) {
          throw new ServletException(Ex.getMessage());
    }
  }
  //Clean up resources
  public void destroy() {

  }

  private void errorMessage(String s, HttpServletResponse response)
         throws IOException
   {
       PrintWriter out = response.getWriter();
       out.println("<html>");
       out.println("<head>");
       out.println("<title> error message</title>");
       out.println("</head>");
       out.println("<body>");
       out.println(s + "</center>");
       out.println("</body>");
       out.println("</html>");
       out.flush();
       out.close();
    }
}