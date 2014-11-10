/**
 * $RCSfile: FullStringTokenizer.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:10 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.util;

public class FullStringTokenizer {

    private String string;
    private String delimiter;

    private int index = 0;
    private int length = 0;
    private int delimiterLength;

    public FullStringTokenizer(String string, String delimiter) {
        this.string = string;
        this.delimiter = delimiter;
        delimiterLength = delimiter.length();
        length = string.length();
    }

   /**
    * Determine if there are more tokens available
    */
    public boolean hasMoreTokens() {
        return (index < length);
    }

   /**
    * Get the next token
    */
    public String nextToken() {
        String s = this.string;
        int nextToken = s.indexOf(delimiter, index);
        //Done finding tokens
        if (nextToken < 0) {
            // fixed to return whatever is left in the string (for the case
            // of "[tok]data[tok]data" <- no ending token)
            String tok = s.substring(index,length);
            index = length;
            return tok;
        }
        else if (nextToken == index) {
            //We found an empty token
            index += delimiterLength;
            return "";
        }
        else {
            String result = s.substring(index, nextToken);
            index = nextToken + delimiterLength;
            return result;
        }
    }

    /**
     * Returns true of the string being tokenized ends on a delimiter.<p>
     *
     * For instance, if the string is "a/b/c/d/" and the delimiter is "/"
     * then this method will return true. A string of "a/b/c/d" returns false.
     */
    public boolean endsOnDelimiter() {
        return ((string.lastIndexOf(delimiter)+delimiter.length())==string.length());
    }

    /**
     * Test program.
     */
    public static void main(String [] args) {
        String testString = "||this is|| a test||||of the system||||";
        FullStringTokenizer tokens = new FullStringTokenizer(testString, "||");
        while(tokens.hasMoreTokens()) {
            System.out.println("> " + tokens.nextToken());
        }
    }
}