/*
 * $RCSfile: UnicodeFilterWriter.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:16 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.util;

import java.io.*;

/**
 * Class for writing non-null unicode characters to character streams.<p>
 *
 * Example use:<p>
 *
 * <code>Writer out = new UnicodeWriter(new FileWriter("blah.txt"));</code>
 */
public class UnicodeFilterWriter extends Writer {

    // Underlying writer
    private Writer out;

    /**
     * Create a new UnicodeWriter
     *
     * @param out A Writer.
     */
    public UnicodeFilterWriter( Writer out ) {
        super(out);
        this.out = out;
    }

    /**
     * Check each character in the character array for the null character. If
     * the null character is found ignore it (don't write to underlying character
     * stream).
     *
     * @param cbuf The character array
     * @param offset Offset from which to start writing characters
     * @param length Number of characters to write
     */
    public void write(char[] cbuf, int offset, int length) throws IOException {
        synchronized( lock ) {
            int loopEnd = (offset+length);
            for( int i=offset; i<loopEnd; i++ ) {
                switch(cbuf[i]) {
                    // null character case
                    case '\u0000':
                        break;
                    case '\u0018':
                        break;
                    // everything else
                    default:
                        out.write(cbuf[i]);
                        break;
                }
            }
        }
    }

    /**
     * Flush the stream
     */
    public void flush() throws IOException {
	    synchronized (lock) {
	        out.flush();
	    }
    }

    /**
     * Close the stream
     */
    public void close() throws IOException {
	    synchronized (lock) {
	        if (out == null) {
		        return;
            }
	        out.close();
    	    out = null;
	    }
    }

}