/**
 * $RCSfile: RewardException.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:46 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import java.io.*;

/**
 * Thrown when an error occurs during a failed operation in the RewardManager.
 */
public class RewardException extends Exception {

    private Throwable nestedThrowable = null;

    public RewardException() {
        super();
    }

    public RewardException(String msg) {
        super(msg);
    }

    public RewardException(Throwable nestedThrowable) {
        this.nestedThrowable = nestedThrowable;
    }

    public RewardException(String msg, Throwable nestedThrowable) {
        super(msg);
        this.nestedThrowable = nestedThrowable;
    }

    public void printStackTrace() {
        super.printStackTrace();
        if (nestedThrowable != null) {
            nestedThrowable.printStackTrace();
        }
    }

    public void printStackTrace(PrintStream ps) {
        super.printStackTrace(ps);
        if (nestedThrowable != null) {
            nestedThrowable.printStackTrace(ps);
        }
    }

    public void printStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);
        if (nestedThrowable != null) {
            nestedThrowable.printStackTrace(pw);
        }
    }
}