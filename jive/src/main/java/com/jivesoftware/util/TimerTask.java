/**
 * $RCSfile: TimerTask.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:16 $
 *
 * Copyright (C) 1999-2001 CoolServlets Inc. All rights reserved.
 * ===================================================================
 * The Jive Software License (based on Apache Software License, Version 1.1)
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by
 *        Jive Software (http://www.jivesoftware.com)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Jive" and "CoolServlets" must not be used to
 *    endorse or promote products derived from this software without
 *    prior written permission. For written permission, please
 *    contact webmaster@coolservlets.com.
 *
 * 5. Products derived from this software may not be called "Jive",
 *    nor may "Jive" appear in their name, without prior written
 *    permission of CoolServlets.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL COOLSERVLETS INC OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 * This software consists of voluntary contributions made by many
 * individuals on behalf of Jive Software. For more information
 * on Jive Software please visit http://www.jivesoftware.com.
 */

package com.jivesoftware.util;

/**
 * A replacement for the java.util.TimerTask in JDK 1.3
 * <P>
 * Functionally this class performs similarly to the java.util.TimerTask
 * class in JDK 1.3.
 * <P>
 * Refer to the JDK 1.3 documentation for a full description.
 *
 * @author	Joseph Mocker
 */
public abstract class TimerTask implements Runnable {

    private boolean _scheduled = false;
    private boolean _cancelled = false;

    private long _scheduledExecutionTime = 0;
    private long _delay = 0;
    private long _period = 0;
    private boolean _fixedDelay = false;

    /**
     * Create a new TimerTask
     */
    protected TimerTask() {
        // Hmm, there is some reason why this constructor was defined
        // as protected in the 1.3 implementation.
    }

    /**
     * Cancel this task
     *
     * @return Roughly speaking returns true if there is a pending execution.
     *		See JDK 1.3 for more information.
     */
    public boolean cancel() {
        // if we've already been cancelled, return false per javadoc.
        if (_cancelled) return false;

        _cancelled = true;

        // if we've never been scheduled, return false per javadoc.
        if (!_scheduled) return false;

        // if we have a pending execution, return true per javadoc.
        // otherwise return false.
        return  (_scheduledExecutionTime != 0);
    }

    /**
     * The action to be performed by this task
     */
    public abstract void run();

    /**
     * Return the scheduled execution time of the most recent actual
     * execution of this task.
     */
    public long scheduledExecutionTime() {
        return _scheduledExecutionTime;
    }

   /**
    * Return true if this task has been cancelled.
    * <P>
    * <I>This method is an artifact of the implementation.</I>
    */
   protected final boolean isCancelled() {
      return _cancelled;
   }

    /**
     * Schedule this task for execution.
     * <P>
     * <I>This method is an artifact of the implementation.</I>
     *
     * @param delay initial delay until first execution. must be >= 0
     * @param period time in milliseconds between executions.
     * @param fixedDelay true if this should be a fixed-delay task,
     *                   false if this should be a fixed-rate task.
     */
    protected final void schedule(long delay, long period, boolean fixedDelay) {
        if (delay < 0 || period < 0) {
            throw new IllegalArgumentException();
        }

        if (_scheduled) {
            throw new IllegalStateException();
        }

        _scheduled = true;
        _delay = delay;
        _period = period;
        _fixedDelay = fixedDelay;

        _scheduledExecutionTime = System.currentTimeMillis() + _delay;
    }

    /**
     * Schedule this task for another execution.
     * <P>
     * <I>This method is an artifact of the implementation</I>
     */
    protected final void reschedule() {
        if (_period == 0) {
            _scheduledExecutionTime = 0;
        }
        else if (_fixedDelay) {
            _scheduledExecutionTime = System.currentTimeMillis() + _period;
        }
        else {
            long now = System.currentTimeMillis();
	    long delta = now - _scheduledExecutionTime;
            _scheduledExecutionTime = now + _period - delta;
        }
    }
}