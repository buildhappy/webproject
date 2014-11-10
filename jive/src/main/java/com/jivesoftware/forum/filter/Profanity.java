/**
 * $RCSfile: Profanity.java,v $
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

/**
 * A ForumMessageFilter that filters out user-specified profanity.
 */
public class Profanity extends ForumMessageFilter {

    /**
     * Array of all the bad words to filter.
     */
    private String [] words = null;

    /**
     * Comma delimited list of words to filter.
     */
    private String wordList = null;

    /**
     * Indicates if case of words should be ignored.
     */
    private boolean ignoringCase = true;

    /**
     * Clones a new filter that will have the same properties and that
     * will wrap around the specified message.
     *
     * @param message the ForumMessage to wrap the new filter around.
     */
    public ForumMessageFilter clone(ForumMessage message){
        Profanity filter = new Profanity();
        // Set all properties on the new filter.
        filter.wordList = wordList;
        filter.ignoringCase = ignoringCase;
        filter.applyProperties();

        filter.message = message;
        return filter;
    }

    public boolean isCacheable() {
        return true;
    }

    /**
     * Returns ture if the filter will ignore case when filtering out
     * profanity. For example, when "ignore case" is turned on and "dog" is
     * in your word filter list, then "Dog" and "DOG" will also be filtered.
     *
     * @return true if case will be ignored when filtering words.
     */
    public boolean isIgnoringCase() {
        return ignoringCase;
    }

    /**
     * Toggles the filter to ignore case when filtering words or not. For
     * example, when "ignore case" is turned on and "dog" is in your word
     * filter list, then "Dog" and "DOG" will also be filtered.
     *
     * @param ignoringCase true if case should be ignored when filtering.
     */
    public void setIgnoringCase(boolean ignoringCase) {
        this.ignoringCase = ignoringCase;
    }

    /**
     * Returns the comma delimited list of words that will be filtered.
     *
     * @return the list of words to be filtered.
     */
    public String getWordList() {
        return wordList;
    }

    /**
     * Sets the list of words to be filtered. Each word must seperated by a
     * comma.
     *
     * @param the comma delimited list of words to filter.
     */
    public void setWordList(String wordList) {
        this.wordList = wordList;
    }

    //FROM THE FORUMMESSAGE INTERFACE//

    /**
     * <b>Overloaded</b> to return the subject of the message with profanity
     * filtered out.
     */
    public String getSubject() {
        return filterProfanity(message.getSubject());
    }

    /**
     * <b>Overloaded</b> to return the body of the message with profanity
     * filtered out.
     */
    public String getBody() {
        return filterProfanity(message.getBody());
    }

    /**
     * Applies new property values so the filter is ready for futher processing.
     */
    private void applyProperties() {
        if (wordList == null || wordList.equals("")) {
            words = null;
            return;
        }
        StringTokenizer tokens = new StringTokenizer(wordList,",");
        String [] newWords = new String[tokens.countTokens()];
        for (int i=0; i<newWords.length; i++) {
            if (ignoringCase) {
                newWords[i] = tokens.nextToken().toLowerCase().trim();
            }
            else {
                newWords[i] = tokens.nextToken().trim();
            }
        }
        words = newWords;
    }

    /**
     * Filters out bad words.
     */
    private String filterProfanity(String str) {
        // Check to see if the string is null or zero-length
        if (str == null || "".equals(str) || wordList == null) {
            return str;
        }
        String lower;
        if (ignoringCase) {
            lower = str.toLowerCase();
        }
        else {
            lower = str;
        }
        for (int i=0; i<words.length; i++) {
            str = replace(str, lower, words[i], cleanWord(words[i].length()));
        }
        return str;
    }

    /**
     * Generates a string of characters of specified length. For example:
     * !@%$ or %!@$%!@@ or *****
     */
    private String cleanWord(int length) {
        char[] newWord = new char[length];
        for (int i=0; i<newWord.length; i++) {
            newWord[i] = '*';
        }
        return new String(newWord);
    }

    /**
     * Replaces all instances of oldString with newString in the String line.
     */
    private String replace(String line, String lowerCaseLine,
            String oldString, String newString )
    {
        int i=0;
        if ( ( i=lowerCaseLine.indexOf( oldString, i ) ) >= 0 ) {
            int oLength = oldString.length();
            int nLength = newString.length();
            StringBuffer buf = new StringBuffer(line.length()+15);
            buf.append(line.substring(0,i)).append(newString);
            i += oLength;
            int j = i;
            while( ( i=lowerCaseLine.indexOf( oldString, i ) ) > 0 ) {
                buf.append(line.substring(j,i)).append(newString);
                i += oLength;
                j = i;
            }
            buf.append(line.substring(j));
            return buf.toString();
        }
        return line;
    }
}