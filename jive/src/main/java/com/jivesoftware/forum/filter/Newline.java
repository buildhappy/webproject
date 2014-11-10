/**
 * $RCSfile: Newline.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:05 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.filter;

import java.util.*;

import com.jivesoftware.forum.*;
import com.jivesoftware.util.*;

/**
 * A ForumMessageFilter that converts newline characters into HTML &lt;br&gt; tags.
 * This filter should only be run after any HTML stripping filters.
 * This filter should be run before the Java code filter, as "[" and "]" are escaped
 * to work around the TextStyle filter.
 */
public class Newline extends ForumMessageFilter {

    private static final char[] BR_TAG = "<BR>".toCharArray();

    /**
     * Clones a new filter that will have the same properties and that
     * will wrap around the specified message.
     *
     * @param message the ForumMessage to wrap the new filter around.
     */
    public ForumMessageFilter clone(ForumMessage message) {
        Newline filter = new Newline();
        filter.message = message;
        return filter;
    }

    public boolean isCacheable() {
        return true;
    }

    /**
     * Returns the body of the message with newline characters converted to
     * HTML &ltbr&gt tags.
     *
     * @return the body with newline characters converted to HTML.
     */
    public String getBody() {
        if (message.getBody() == null) {
            return null;
        }
        // return convertNewlines(message.getBody());
        return convertNewlinesAroundCode(message.getBody());
    }

    /**
     * Replaces newline characters with the HTML equivalent.
     * This method works around the code filter, allowing
     * copy and paste actions to be successfully performed.
     *
     * @param input the text to be converted.
     * @return the input string with newline characters replaced with HTML
     *      newline tags..
     */
    private static String convertNewlinesAroundCode(String input) {
        char[] chars = input.toCharArray();
        int[][] table;
        int cur = 0;
        int len = chars.length;
        StringBuffer buf = new StringBuffer(len);

        table = recordCodeIndeces(input);

        if (table.length>0) {    // there are some code sections
            // while there are more characters to filter
            for (int i=0; i<len; i++) {
                // If we've found a Unix newline, add BR tag.
                // filter only positions outside of code sections
                if (chars[i]=='\n' && notInCodeSection(i,table)) {
                    buf.append(chars, cur, i-cur).append(BR_TAG);
                    cur = i+1;
                }
                // If we've found a Windows newline, add BR tag.
                // filter only positions outside of code sections
                else if (chars[i]=='\r' && i<len-1 && chars[i+1]=='\n' && notInCodeSection(i,table)) {
                    buf.append(chars, cur, i-cur).append(BR_TAG);
                    i++;
                    cur = i+1;
                }
            }
        }
        else {    // there are no code sections
            // filter normally
            for (int i=0; i<len; i++) {
                // If we've found a Unix newline, add BR tag.
                if (chars[i]=='\n') {
                    buf.append(chars, cur, i-cur).append(BR_TAG);
                    cur = i+1;
                }
                // If we've found a Windows newline, add BR tag.
                else if (chars[i]=='\r' && i<len-1 && chars[i+1]=='\n') {
                    buf.append(chars, cur, i-cur).append(BR_TAG);
                    i++;
                    cur = i+1;
                }
            }
        }
        // Add whatever chars are left to buffer.
        buf.append(chars, cur, len-cur);
        return buf.toString();
    }

    /**
     * Finds out whether a given index position resides within
     * any boundaries of a set of index boundaries.
     *
     * @param index the index position to be tested.
     * @param boundaries the table containing a set of boundaries.
     * @return true if index resides within at least one boundary,
     *  false otherwise.
     */
    private static boolean notInCodeSection(int index, int[][] boundaries) {
        if (boundaries == null) {
            return true;
        }
        else {
            boolean notInCodeSection = true;
            int i = 0;
            while (i<boundaries.length) {
                // if in a code section
                if (index>=boundaries[i][0] && index<=boundaries[i][1]) {
                    return false;
                }
                else {
                    i++;
                    continue;
                }
            }
            return notInCodeSection;
        }
    }

    /**
     * Records the index positions of [code][/code] sections
     *
     * @param input the text to be filtered.
     * @return a table containing the index positions of code sections
     */
    private static int[][] recordCodeIndeces(String input) {
        int[][] codeIndeces;
        int count;
        int oldend;
        int i, j;

        count = 0;
        oldend = 0;
        i = j = 0;

        // first figure out how many pairs of [code][/code] there are
        while (((i=input.indexOf("[code]", oldend)) >= 0) && ((j=input.indexOf("[/code]", i+6)) >= 0)) {
            count++;
            oldend = j+7;
        }

        oldend = 0;
        codeIndeces = new int[count][2];
        i = j = 0;
        int x = 0;
        // int y = 0;

        // record code index positions
        while (((i=input.indexOf("[code]", oldend)) >= 0) && ((j=input.indexOf("[/code]", i+6)) >= 0)) {
            codeIndeces[x][0] = i;
            // y++;
            codeIndeces[x][1] = j;
            x++;
            // y--;
            oldend = j+7;
        }
        return codeIndeces;
    }

    /**
     * Replaces newline characters with the HTML equivalent.
     * This method does not work around code sections
     * @param input the text to be converted.
     * @return the input string with newline characters replaced with HTML
     *      newline tags..
     */
    private static String convertNewlines(String input) {
        char [] chars = input.toCharArray();
        int cur = 0;
        int len = chars.length;
        StringBuffer buf = new StringBuffer(len);

        // Loop through each character lookin for newlines.
        for (int i=0; i<len; i++) {
            // If we've found a Unix newline, add BR tag.
            if (chars[i]=='\n') {
                buf.append(chars, cur, i-cur).append(BR_TAG);
                cur = i+1;
            }
            // If we've found a Windows newline, add BR tag.
            else if (chars[i]=='\r' && i<len-1 && chars[i+1]=='\n') {
                buf.append(chars, cur, i-cur).append(BR_TAG);
                i++;
                cur = i+1;
            }
        }
        // Add whatever chars are left to buffer.
        buf.append(chars, cur, len-cur);
        return buf.toString();
    }
}