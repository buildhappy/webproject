/**
 * $RCSfile: Timer.java,v $
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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.util.Date;

/**
 * A simple replacement for the java.util.Timer class in JDK 1.3.
 * <P>
 * Functionally this class performs similarly to the java.util.Timer
 * class in JDK 1.3 although it probably is not as robust. The main
 * difference is that it uses a simple List for its task queue that
 * it traverses linearly, so it may not scale as well as the 1.3
 * version.
 * <P>
 * Refer to the JDK 1.3 documentation for a full description of how
 * this works.
 *
 * @author	Joseph Mocker
 */
public class Timer {

   private TimerEngine timer = null;
   protected List tasks = null;
   private Object lock = new Object();

   /**
    * Create a new Timer.
    */
   public Timer() {
      this(false);
   }

   /**
    * Create a new Timer. Additionally specify if the underlying
    * timer thread is a daemon thread or not.
    *
    * @param isDaemon true if timer thread should run as a daemon.
    */
   public Timer(boolean isDaemon) {
      tasks = new ArrayList();

      timer = new TimerEngine();
      timer.setDaemon(isDaemon);

      timer.start();
   }

   /**
    * Schedule a single-execution task after a delay.
    *
    * @param task task to be scheduled.
    * @param delay delay in milliseconds before task is to be executed.
    */
   public void schedule(TimerTask task, long delay) {
      schedule(task, System.currentTimeMillis() + delay, 0);
   }

   /**
    * Schedule a single-execution task at a specific time.
    *
    * @param task task to be scheduled.
    * @param time time task is to be executed.
    */
   public void schedule(TimerTask task, Date time) {
      schedule(task, time.getTime() - System.currentTimeMillis(), 0);
   }

   /**
    * Schedule a fixed-delay task beginning at a specific time.
    *
    * @param task task to be scheduled.
    * @param firstTime time the task is to be first executed.
    * @param period time in milliseconds betweek successive task executions.
    */
   public void schedule(TimerTask task, Date firstTime, long period) {
      schedule(task, firstTime.getTime() - System.currentTimeMillis(), period);
   }

    /**
     * Schedule a fixed-delay task after a delay.
     *
     * @param task task to be scheduled.
     * @param delay delay in milliseconds before task is to be executed.
     * @param period time in milliseconds betweek successive task executions.
     */
    public void schedule(TimerTask task, long delay, long period) {
        // Don't allow anything to be scheduled if someone cancelled us
        if (timer.isCancelled()) {
	    throw new IllegalStateException();
        }
        // Try to schedule the task, it will throw an exception
        // if we aren't allowed to.
        task.schedule(delay, period, true);
        synchronized(lock) {
            tasks.add(task);
        }
        timer.wakeUp();
   }

   /**
    * Schedule a fixed-rate task beginning at a specific time.
    *
    * @param task task to be scheduled.
    * @param firstTime time the task is to be first executed.
    * @param period time in milliseconds betweek successive task executions.
    */
    public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {
        scheduleAtFixedRate(task, firstTime.getTime()-System.currentTimeMillis(),
                period);
    }

   /**
    * Schedule a fixed-rate task after a delay.
    *
    * @param task task to be scheduled.
    * @param delay delay in milliseconds before task is to be executed.
    * @param period time in milliseconds betweek successive task executions.
    */
    public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
        // Don't allow anything to be scheduled if someone cancelled us
        if (timer.isCancelled()) {
            throw new IllegalStateException();
        }
        // Try to schedule the task. It will throw an exception if we
        // aren't allowed to.
        task.schedule(delay, period, false);
        synchronized(lock) {
            tasks.add(task);
        }
        timer.wakeUp();
    }

   /**
    * Terminates this timer.
    */
   public void cancel() {
      timer.cancel();
   }


   /**
    * Execution and Scheduling engine for the Timer object.
    * <P>
    * This class performs the scheduling and execution of all the
    * TimerTasks that have been added to the parent Timer object.
    *
    * @author	Joseph Mocker
    */
    protected class TimerEngine extends Thread {
        private boolean cancelled = false;

        public void run() {
            while(!cancelled) {
                // Iterate through the scheduled tasks, executing the
                // tasks which are ready to run, and find the
                // non-runnable task that should be executed next
                TimerTask current = null;
                synchronized(lock) {
                    for (Iterator i = tasks.iterator(); i.hasNext(); ) {
                        TimerTask task = (TimerTask)i.next();

                        // If the scheduledTime is 0 or it has been cancelled,
                        // just ignore this task.
                        if (task.scheduledExecutionTime() == 0 || task.isCancelled()) {
                            // Remove the task from the queue.
                            i.remove();
                            continue;
                        }

                        // If the scheduledTime is in the past, execute the task
                        // and calculate the next scheduledTime.
                        if (task.scheduledExecutionTime() <= System.currentTimeMillis())
                        {
                            task.run();
                            task.reschedule();
                            if (task.scheduledExecutionTime() == 0) {
                                continue;
                            }
                        }

                        // Decide if this task might be the next one to execute.
                        // the task with the earliest scheduledTime is the next
                        // one to execute.
                        if (current == null) {
                            current = task;
                        }
                        else if (task.scheduledExecutionTime() <
                                current.scheduledExecutionTime())
                        {
                            current = task;
                        }
                    }
                }

                // We ran through the pending tasks, so now we'll just
                // wait either until we should execute the next task
                // or some external event notifies us. If there are no runnable
                // tasks, just wait forever until we're notified.
                synchronized (this) {
                    try {
                        if (current != null) {
                            long timeout = current.scheduledExecutionTime() -
                                System.currentTimeMillis();

                            if (timeout > 0) {
                                wait(timeout);
                            }
                        }
                        else {
                            // Nothing to do, wait forever.
                            wait();
                        }
                    }
                    catch (InterruptedException e) { /* ignore */ }
                }
            }
        }

        protected synchronized void wakeUp() {
            notifyAll();
        }

        protected synchronized void cancel() {
            cancelled = true;
            wakeUp();
        }

        protected synchronized boolean isCancelled() {
            return cancelled;
        }
    }
}