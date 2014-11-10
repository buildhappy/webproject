package com.jivesoftware.forum.filter;

import com.jivesoftware.forum.*;
import java.util.regex.*;

/**
* A ForumMessageFilter that replaces  to <img src=url></img>
*/


public class ImageFilter extends ForumMessageFilter {
private boolean filteringSubject = false;
private boolean filteringBody = true;

//clone\u65B9\u6CD5\uFF0C\u7528\u4E8E\u4EA7\u751F\u4E00\u4E2A\u5B9E\u4F8B
public ForumMessageFilter clone(ForumMessage message) {
ImageFilter filter = new ImageFilter();
filter.filteringSubject = filteringSubject;
filter.filteringBody = filteringBody;
filter.message = message;
return filter;
}

public String getSubject() {
return message.getSubject();
}

//\u83B7\u5F97\u8FC7\u6EE4\u540E\u7684body
public String getBody(){
return convertTags(message.getBody());
}

public boolean isCacheable() {
return true;
}

//\u6B64\u65B9\u6CD5\u7528\u4E8E\u89E3\u6790\u5C06UBB\u7684img\u8868\u8FBE\u65B9\u6CD5\u8F6C\u6362\u6210html\u6837\u5F0F\uFF0C\u4F7F\u7528regx\u5305
public static String convertTags(String str){
if (str == null || str.length() == 0) {
return str;
}
String patt = "(\\[img\\])([^\\[]+)(\\[/img\\])";
Pattern p = Pattern.compile(patt);
Matcher m = p.matcher(str);
StringBuffer sb = new StringBuffer();
int i=0;
boolean result = m.find();
while(result) {
i++;
m.appendReplacement(sb, "<img src=\""+m.group(2)+"\">");
result = m.find();
}
m.appendTail(sb);
return sb.toString();
}

}
