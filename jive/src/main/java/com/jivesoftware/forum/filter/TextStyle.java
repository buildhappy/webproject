/**
 * $RCSfile: TextStyle.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:06 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.filter;

import com.jivesoftware.forum.*;
import com.jivesoftware.util.*;
import java.util.*;
import java.io.*;

/**
 * A ForumMessageFilter that replaces [b][/b] and [i][/i] tags with their HTML
 * tag equivalents. This filter should only be run after any HTML stripping
 * filters are.
 */
public class TextStyle extends ForumMessageFilter {

    private boolean boldEnabled = true;
    private boolean italicEnabled = true;
    private boolean underlineEnabled = true;
    private boolean preformatEnabled = true;
    private boolean filteringSubject = false;
    private boolean filteringBody = true;

    /**
     * Clones a new filter that will have the same properties and that
     * will wrap around the specified message.
     *
     * @param message the ForumMessage to wrap the new filter around.
     */
    public ForumMessageFilter clone(ForumMessage message){
        TextStyle filter = new TextStyle();
        // Set all properties on the new filter.
        filter.boldEnabled = boldEnabled;
        filter.italicEnabled = italicEnabled;
        filter.underlineEnabled = underlineEnabled;
        filter.preformatEnabled = preformatEnabled;
        filter.filteringSubject = filteringSubject;
        filter.filteringBody = filteringBody;

        filter.message = message;
        return filter;
    }

    public boolean isCacheable() {
        return true;
    }

    //FROM THE FORUMMESSAGE INTERFACE//

    /**
     * <b>Overloaded</b> to return the subject of the message with custom font
     * stylizing tags translated to HTML.
     *
     * @return the subject of the message with FontStyle tokens replaced by HTML.
     */
    public String getSubject() {
        if (filteringSubject) {
            return convertTags(message.getSubject());
        }
        else {
            return message.getSubject();
        }
    }

    /**
     * <b>Overloaded</b> to return the body of the message with custom font
     * stylizing tags translated to HTML.
     *
     * @return the subject of the message with FontStyle tokens replaced by HTML.
     */
    public String getBody() {
        if (filteringBody) {
            return convertTags(message.getBody());
        }
        else {
            return message.getBody();
        }
    }

    //FILTER PROPERTIES//

    /**
     * Returns true if translation of [b][/b] tags to the HTML bold tag is
     * enabled.
     *
     * @return true if translation of [b][/b] tags is enabled.
     */
    public boolean getBoldEnabled() {
        return boldEnabled;
    }

    /**
     * Toggles translation of [b][/b] tags to the HTML bold tag.
     *
     * @param boldEnabled toggles translation of [b][/b] tags.
     */
    public void setBoldEnabled(boolean boldEnabled) {
        this.boldEnabled = boldEnabled;
    }

    /**
     * Returns true if translation of [i][/i] tags to the HTML italic tag is
     * enabled.
     *
     * @return true if translation of [i][/i] tags is enabled.
     */
    public boolean getItalicEnabled() {
        return italicEnabled;
    }

    /**
     * Toggles translation of [i][/i] tags to the HTML italic tag.
     *
     * @param italicEnabled toggles translation of [i][/i] tags.
     */
    public void setItalicEnabled(boolean italicEnabled) {
        this.italicEnabled = italicEnabled;
    }

    /**
     * Returns true if translation of [u][/u] tags to the HTML underline tag
     * is enabled.
     *
     * @return true if translation of [i][/i] tags is enabled.
     */
    public boolean getUnderlineEnabled() {
        return underlineEnabled;
    }

    /**
     * Toggles translation of [u][/u] tags to the HTML underline tag.
     *
     * @param underlineEnabled toggles translation of [u][/u] tags.
     */
    public void setUnderlineEnabled(boolean underlineEnabled) {
        this.underlineEnabled = underlineEnabled;
    }

    /**
     * Returns true if translation of [pre][/pre] tags to the HTML preformat
     * tag is enabled.
     *
     * @return true if translation of [pre][/pre] tags is enabled.
     */
    public boolean getPreformatEnabled() {
        return preformatEnabled;
    }

    /**
     * Toggles translation of [pre][/pre] tags to the HTML underline tag.
     *
     * @param preformatEnabled toggles translation of [pre][/pre] tags.
     */
    public void setPreformatEnabled(boolean preformatEnabled) {
        this.preformatEnabled = preformatEnabled;
    }

    /**
     * Returns true if filtering on the subject is enabled.
     *
     * @return true if filtering on the subject is enabled.
     */
    public boolean isFilteringSubject() {
        return filteringSubject;
    }

    /**
     * Enables or disables filtering on the subject.
     *
     * @param filteringSubject toggle value for filtering on subject.
     */
    public void setFilteringSubject(boolean filteringSubject) {
        this.filteringSubject = filteringSubject;
    }

    /**
     * Returns true if filtering on the body is enabled.
     *
     * @return true if filtering on the body is enabled.
     */
    public boolean isFilteringBody() {
        return filteringBody;
    }

    /**
     * Enables or disables filtering on the body.
     *
     * @param filteringBody toggle value for filtering on body.
     */
    public void setFilteringBody(boolean filteringBody) {
        this.filteringBody = filteringBody;
    }

   /**
    * This method takes a string which may contain [b][/b] and [i][/i] tags
    * and converts them to their HTML equivalents.
    *
    * @param input the text to be converted.
    * @return the input string with the [b][/b] and [i][/i] tags converted to
    *   their HTML equivalents.
    */
    private String convertTags(String input) {
        // Check if the string is null or zero length -- if so, return what was sent in.
        if (input == null || input.length() == 0) {
            return input;
        }
        else {
            // To figure out how many times we've made text replacements, we
            // need to pass around Integer count objects.
            int[] boldStartCount = new int[1];
            int[] italicsStartCount = new int[1];
            int[] boldEndCount = new int[1];
            int[] italicsEndCount = new int[1];
            int[] underlineStartCount = new int[1];
            int[] underlineEndCount = new int[1];
            int[] preformatStartCount = new int[1];
            int[] preformatEndCount = new int[1];
            if (boldEnabled) {
                input = StringUtils.replaceIgnoreCase(input, "[b]", "<b>", boldStartCount);
                input = StringUtils.replaceIgnoreCase(input, "[/b]", "</b>", boldEndCount);
                int bStartCount = boldStartCount[0];
                int bEndCount = boldEndCount[0];
                while (bStartCount > bEndCount) {
                    input = input.concat("</b>");
                    bEndCount++;
                }
            }
            if (italicEnabled) {
                input = StringUtils.replaceIgnoreCase(input, "[i]", "<i>", italicsStartCount);
                input = StringUtils.replaceIgnoreCase(input, "[/i]", "</i>", italicsEndCount);
                int iStartCount = italicsStartCount[0];
                int iEndCount = italicsEndCount[0];
                while (iStartCount > iEndCount) {
                    input = input.concat("</i>");
                    iEndCount++;
                }
            }
            if (underlineEnabled) {
                input = StringUtils.replaceIgnoreCase(input, "[u]", "<u>", underlineStartCount);
                input = StringUtils.replaceIgnoreCase(input, "[/u]", "</u>", underlineEndCount);
                int uStartCount = underlineStartCount[0];
                int uEndCount = underlineEndCount[0];
                while (uStartCount > uEndCount) {
                    input = input.concat("</u>");
                    uEndCount++;
                }
            }
            if (preformatEnabled) {
                input = StringUtils.replaceIgnoreCase(input, "[pre]", "<pre>", preformatStartCount);
                input = StringUtils.replaceIgnoreCase(input, "[/pre]", "</pre>", preformatEndCount);
                int preStartCount = preformatStartCount[0];
                int preEndCount = preformatEndCount[0];
                while (preStartCount > preEndCount) {
                    input = input.concat("</pre>");
                    preEndCount++;
                }
            }
        }
        return input;
    }
}