package com.jdon.web.control;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;


public class ContextSecurityFilter implements Filter {

    public static final String module = ContextSecurityFilter.class.getName();

    public FilterConfig config;

    public void init(FilterConfig config) {
        this.config = config;
    }

    public void setFilterConfig(FilterConfig config) {
        this.config = config;
    }

    public FilterConfig getFilterConfig() {
        return config;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper((HttpServletResponse) response);

        String allowedPath = config.getInitParameter("allowedPaths");
        String redirectPath = config.getInitParameter("redirectPath");
        String errorCode = config.getInitParameter("errorCode");

//        System.out.println("allowedPaths"+allowedPath);

        String[] allows= allowedPath.split(":");
        List allowList=new ArrayList();
        allowList.addAll(Arrays.asList(allows));

        allowList.add("/");    // No path is allowed.
        allowList.add("");      // No path is allowed.

        String requestPath = httpRequest.getServletPath();
        if (requestPath == null) requestPath = "";

        if (requestPath.lastIndexOf("/") > 0) {
          if (requestPath.indexOf("/") == 0)
            requestPath = "/" + requestPath.substring(1, requestPath.indexOf("/", 1));
          else
            requestPath = requestPath.substring(1, requestPath.indexOf("/"));
        }

        String requestInfo = httpRequest.getServletPath();
        if (requestInfo == null) requestInfo = "";

        if (requestInfo.lastIndexOf("/") >= 0) {
          requestInfo = requestInfo.substring(0, requestInfo.lastIndexOf("/")) + "/*";
        }

        StringBuffer contextUriBuffer = new StringBuffer();
        if (httpRequest.getContextPath() != null)
          contextUriBuffer.append(httpRequest.getContextPath());
        if (httpRequest.getServletPath() != null)
          contextUriBuffer.append(httpRequest.getServletPath());
        if (httpRequest.getPathInfo() != null)
          contextUriBuffer.append(httpRequest.getPathInfo());
        String contextUri = contextUriBuffer.toString();

//        System.out.println("requestPath="+requestPath+" requestInfo="+requestInfo+" httpRequest.getServletPath()="
//                           +httpRequest.getServletPath());

        if (!allowList.contains(requestPath) && !allowList.contains(requestInfo) &&
                    !allowList.contains(httpRequest.getServletPath())) {
            String filterMessage = "[Filtered request]: " + contextUri;
            if ((redirectPath == null) || redirectPath.equals("")){
              int error;
              try {
                error = Integer.parseInt(errorCode);
              } catch (NumberFormatException nfe) {
                error = 404;
              }
              filterMessage = filterMessage + " (" + error + ")";
              wrapper.sendError(error, contextUri);
            } else {
              filterMessage = filterMessage + " (" + redirectPath + ")";
              wrapper.sendRedirect(httpRequest.getContextPath() + redirectPath);
              //request.getRequestDispatcher(redirectPath).forward(request, response);
            }
            return;
        }

        chain.doFilter(request, response);
    }

    public void destroy() {
        config = null;
    }
}
