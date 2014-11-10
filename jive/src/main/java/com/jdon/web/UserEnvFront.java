package com.jdon.web;

import javax.servlet.http.*;
import com.jivesoftware.forum.*;
import com.jivesoftware.forum.util.*;

import java.text.*;
import java.util.*;

public class UserEnvFront {

  public static DisplayUtil du=DisplayUtil.getInstance();



  // Formatter for short dates
  public static final SimpleDateFormat shortDateFormatter =
            new SimpleDateFormat("yyyy-MM", JiveGlobals.getLocale());


  private ForumFactory forumFactory;
  public ForumFactory getForumFactory(){
      return forumFactory;
  }

  private User pageUser;
  public User getPageUser(){
      return pageUser;
  }

  public void registeUserInit(Authorization authToken){
      try{
          this.forumFactory = ForumFactory.getInstance(authToken);
           this.pageUser=this.forumFactory.getUserManager().getUser(authToken.getUserID());
        }catch(Exception ex){
        }
  }


  public User getUser(long userID){
  // Try to load a user (try by userID first, then try username)
    UserManager userManager = forumFactory.getUserManager();
    User user = null;
    try {
        user = userManager.getUser(userID);
		return user;
    }
    catch (UserNotFoundException unfe) {
        return null;
    }

  }

}

