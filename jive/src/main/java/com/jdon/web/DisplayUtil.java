package com.jdon.web;

import javax.servlet.http.*;
import com.jivesoftware.forum.*;
import com.jivesoftware.forum.util.*;

import java.text.*;
import java.util.*;

public class DisplayUtil {

// default number of threads to show
  static final int DEFAULT_THREAD_RANGE = 15;
  // default number of messages to show
  static final int DEFAULT_MESSAGE_RANGE = 15;

  // Returns the number of threads per forum listing to display for the user
  public static int getThreadRange(HttpServletRequest request,
                        HttpServletResponse response, User user)
        {
    int threadRange = ParamUtils.getIntParameter(request,"thRange",-1);
    if (threadRange > -1) {
      return threadRange;
    } else {
      threadRange = DEFAULT_THREAD_RANGE;
      if (user != null) {
        try {
          threadRange = Integer.parseInt(user.getProperty("jiveThreadRange"));
          } catch (Exception e) {}
      }
      else {
        try {
          threadRange = Integer.parseInt(SkinUtils.retrieve(request,response,"jiveThreadRange").trim());
          } catch (Exception e) {}
      }
      return threadRange;
    }
  }

  // Returns the number of messages to display.
  public static int getMessageRange(HttpServletRequest request,
                        HttpServletResponse response, User user)
  {
    int messageRange = ParamUtils.getIntParameter(request,"msRange",-1);
    if (messageRange > -1) {
      return messageRange;
    } else {
      messageRange = DEFAULT_MESSAGE_RANGE;
      if (user != null) {
        try {
          messageRange = Integer.parseInt(user.getProperty("jiveMessageRange"));
          } catch (Exception e) {}
      }
      else {
        try {
          messageRange = Integer.parseInt(SkinUtils.retrieve(request,response,"jiveMessageRange").trim());
          } catch (Exception e) {}
      }
      return messageRange;
    }
  }

  // Returns the newest message in a forum:
  public static ForumMessage getLastPost(Forum forum) {
    if (forum.getMessageCount() == 0) {
      return null;
    }
    ResultFilter filter = new ResultFilter();
    filter.setSortOrder(ResultFilter.DESCENDING);
    filter.setNumResults(1);
    Iterator messages = forum.messages(filter);
    ForumMessage lastPost = null;
    if (messages.hasNext()) {
      lastPost = (ForumMessage)messages.next();
    }
    return lastPost;
  }

  // Returns the newest message in a thread:
  public static ForumMessage getLastPost(ForumThread thread) {
    ResultFilter filter = new ResultFilter();
    filter.setSortOrder(ResultFilter.DESCENDING);
    filter.setSortField(JiveGlobals.CREATION_DATE);
    filter.setNumResults(1);
    Iterator messages = thread.messages(filter);
    ForumMessage lastPost = null;
    if (messages.hasNext()) {
      lastPost = (ForumMessage)messages.next();
    }
    return lastPost;
  }

  // Prints out a group of links to paginate through thread listings, ie:
  // "X page(s) [ 1 2 3 4 5 ... 30 | > ]"
  public static String getForumPaginator(String hrefname, int topicCount,
      int numPages, int start, int range)
  {
    StringBuffer buf = new StringBuffer();

    // "X page(s) in this forum":
    /*buf.append("<b>").append(numPages).append("</b> page").append((numPages!=1)?"s":"");
    buf.append(" in this forum");*/

    // "X page(s) in this forum": //to chinese
    buf.append(" 当前有");
    buf.append("<b>").append(numPages).append("</b> 页");


    // "["
    buf.append(" [ ");

    // Print out a left arrow if necessary
    if (start > 0) {

      buf.append("<a href=\"");
      buf.append(hrefname);
      buf.append("&start=");
      buf.append((start-range));
      buf.append("&thRange=");
      buf.append(range);
      buf.append("\" class=\"forum\" title=\"Previous page\">");
      buf.append("<img src=\"images/prev.gif\" width=\"10\" height=\"10\" hspace=\"2\" border=\"0\">");
      buf.append("</a>");
      //buf.append(" | ");
      buf.append("<img src=\"images/blank.gif\" width=\"5\" height=\"1\" border=\"0\">");
    }

    // Calculate the starting point & end points (the range of pages to display)
    int currentPage = (start/range)+1;
    int lo = currentPage - 5;
    if (lo <= 0) {
      lo = 1;
    }
    int hi = currentPage + 5;

    // print out a link to the first page if we're beyond that page
    if (lo > 2) {
       buf.append("<a href=\"");
      buf.append(hrefname);
      buf.append("\" class=\"forum\" title=\"Go to the first topic page\">1</a> ... ");
    }

    // Print the page numbers before the current page
    while (lo < currentPage) {
      buf.append("<a href=\"");
      buf.append(hrefname);
	  buf.append("&start=");
      buf.append(((lo-1)*range));
      buf.append("&thRange=");
      buf.append(range);
      buf.append("\" class=\"forum\"><b>");
      buf.append(lo);
      buf.append("</b></a>&nbsp;");
      lo++;
    }

    // Print the current page
    buf.append("<b><span style=\"background-color:#ffffff;color:#000000;\">");
    buf.append(currentPage);
    buf.append("</span></b>");

    currentPage++;

    // Print page numbers after the current page
    while ((currentPage <= hi) && (currentPage<=numPages)) {
      buf.append("&nbsp;");
      buf.append("<a href=\"");
      buf.append(hrefname);
	  buf.append("&start=");
      buf.append(((currentPage-1)*range));
      buf.append("&thRange=");
      buf.append(range);
      buf.append("\" class=\"forum\"><b>");
      buf.append(currentPage);
      buf.append("</b></a>");
      currentPage++;
    }

    // Show a next arrow if necesary
    if (topicCount > (start+range)) {
      int numRemaining = (int)(topicCount-(start+range));
      //buf.append(" | ");
      buf.append("<img src=\"images/blank.gif\" width=\"5\" height=\"1\" border=\"0\">");
      buf.append("<a href=\"");
      buf.append(hrefname);
      buf.append("&start=");
      buf.append((start+range));
      buf.append("&thRange=");
      buf.append(range);
      buf.append("\" class=\"forum\" title=\"Next page\">");
      buf.append("<img src=\"images/next.gif\" width=\"10\" height=\"10\" hspace=\"2\" border=\"0\">");
      buf.append("</a>");
    }

    // "]"
    buf.append(" ]");
    return buf.toString();
  }

  // Prints out a group of links to paginate through message listings, ie:
  // "This topic has X replies on Y pages [ 1 .. 7 8 9 .. 33 | > ]"
  public static String getThreadPaginator(long forumID, long threadID,
      int numMessages, int numReplies, int numPages, int start, int range)
  {
        StringBuffer buf = new StringBuffer();

        // "This topic has X replies on Y pages"
        buf.append("这个主题有 <b>").append(numReplies).append("</b> ");
        if (numReplies == 1) {
            buf.append("回复");
        }
        else {
            buf.append("回复");
        }
        buf.append(" ／ <b>").append(numPages).append("</b> ");
        if (numPages == 1) {
            buf.append("页");
        }
        else {
            buf.append("页");
        }

        // Only show the pages if there is greater than one page
        if (numPages > 1) {

            // "["
            buf.append(" [ ");

            // Print out a "<<" if necessary
            if (start > 0) {
                buf.append("<a href=\"thread.jsp?forum=").append(forumID);
                buf.append("&thread=").append(threadID);
                buf.append("&start=").append(start-range);
                buf.append("&msRange=").append(range);
                buf.append("\" title=\"Previous Page\" ");
                buf.append("style=\"color:#ffffff;\">&laquo;</a> | ");
            }

            int currentPage = (start/range)+1;
    	    int lo = currentPage - 3;
    	    if (lo <= 0) {
	    	    lo = 1;
    	    }
	        int hi = currentPage + 5;

            // Add a link back to the first page
            if (lo > 1) {
                buf.append("<a href=\"thread.jsp?forum=").append(forumID);
                buf.append("&thread=").append(threadID);
                buf.append("&msRange=").append(range);
                buf.append("\" style=\"color:#ffffff;\"");
                buf.append("title=\"Back to first page\">");
                buf.append("1").append("</a> <b>...</b> ");
            }

            // Print out low page numbers
    	    while (lo < currentPage) {
                buf.append("<a href=\"thread.jsp?forum=").append(forumID);
                buf.append("&thread=").append(threadID);
                buf.append("&start=").append((lo-1)*range);
                buf.append("&msRange=").append(range);
                buf.append("\" style=\"color:#ffffff;\">");
                buf.append(lo).append("</a>&nbsp;");
                lo++;
            }

            // Current page
            buf.append("<b><span style=\"color:#000000;background-color=#ffffff;\">");
            buf.append(currentPage);
            buf.append("</span></b>");

            // Print out high page numbers
            while ((currentPage < hi) && (currentPage<numPages)) {
                buf.append("&nbsp;<a href=\"thread.jsp?forum=").append(forumID);
                buf.append("&thread=").append(threadID);
                buf.append("&start=").append((currentPage)*range);
                buf.append("&msRange=").append(range).append("\"");
                buf.append(" style=\"color:#ffffff;\">");
                buf.append(currentPage+1).append("</a>");
                currentPage++;
            }

            // put ending page at the end, ie: " 2 3 4 ... 33"
            if (numPages > currentPage) {
                buf.append(" <b>...</b>  <a href=\"thread.jsp?forum=").append(forumID);
                buf.append("&thread=").append(threadID);
                buf.append("&start=").append((numPages-1)*range);
                buf.append("&msRange=").append(range);
                buf.append("\" style=\"color:#ffffff;\">");
                buf.append(numPages).append("</a>&nbsp;");
            }

            if (numMessages > (start+range)) {
                int numRemaining = (int)(numMessages-(start+range));
                buf.append(" | <a href=\"thread.jsp?forum=").append(forumID);
                buf.append("&thread=").append(threadID);
                buf.append("&start=").append(start+range);
                buf.append("&msRange=").append(range).append("\"");
                buf.append(" title=\"Next page\" style=\"color:#ffffff;\">&raquo;</a>");
            }

            // "]"
            buf.append(" ] ");
        }
        return buf.toString();
  }

  // Returns a property formatted name and email address string,
  // like <a href="mailto:foo">Foo Bar</a>
  public String getNameAndEmailDisplay(String name, String email) {
    if ("null".equals(name)) {
      name = null;
    }
    if ("null".equals(email)) {
      name = null;
        }
        if (name == null && email == null) {
          return null;
        }
        StringBuffer buf = new StringBuffer(40);
        // name is not null, so try to make a like like:
        // <a href="mailto:foo@bar.com">Foo Bar</a>
        if (name != null) {
          if (email != null) {
            buf.append("<a href=\"mailto:").append(email).append("\">");
            buf.append(name);
            buf.append("</a>");
          }
          else {
            buf.append(name);
          }
        }
        // Else, name is null so just try to use the email:
        // <a href="mailto:foo@bar.com">foo@bar.com</a>
        else if (email != null) {
          String display = email;
            if (email.length() > 15) {
                display = email.substring(0,15) + "..";
            }
            buf.append("<a href=\"mailto:").append(email).append("\">");
            buf.append(display);
            buf.append("</a>");
        }

        if (buf.length() == 0) {
          return null;
        }
        else {
          return buf.toString();
        }
  }

   private static DisplayUtil du=new DisplayUtil();
   public static DisplayUtil getInstance() {
                           return du;
   }


}