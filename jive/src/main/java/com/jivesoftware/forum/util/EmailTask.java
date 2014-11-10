/**
 * $RCSfile: EmailTask.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:07 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.util;

import java.io.*;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

import com.jivesoftware.forum.*;

/**
 * A task to send email.<p>
 *
 * This class will hold a list of messages and will send them all at once when
 * the <code>run</code> method is called.<p>
 *
 * This class has a few factory methods you can use to return message objects
 * or to add messages into a queue to be sent. Using these methods, you can
 * send emails in the following couple of ways:<p>
 * <pre>
 *   TaskEngine taskEngine = new TaskEngine();
 *   EmailTask emailTask = new EmailTask();
 *   emailTask.addMessage(
 *     "Joe Bloe", "jbloe@place.org",
 *     "Jane Doe", "jane@doe.com",
 *     "Hello...",
 *     "This is the body of the email..."
 *   );
 *   engine.addTask(emailTask, Thread.NORM_PRIORITY);
 * </pre>
 * or
 * <pre>
 *   TaskEngine taskEngine = new TaskEngine();
 *   EmailTask emailTask = new EmailTask();
 *   Message message = emailTask.createMessage();
 *   // call setters on the message object
 *   // .
 *   // .
 *   // .
 *   engine.addTask(emailTask, Thread.NORM_PRIORITY);
 * </pre><p>
 *
 * This class is configured with a set of Jive properties:<ul>
 *      <li><tt>mail.smtp.host</tt> -- the host name of your mail server, i.e.
 *          mail.yourhost.com
 *      <li><tt>mail.smtp.port</tt> -- an optional property to change the smtp
 *          port used from the default of 25.
 * </ul>
 *
 * @see TaskEngine
 */
public class EmailTask implements Runnable {

    // Session used by the javamail classes
    private static Session session;

    /**
     * Reads mail properties from the jive.properties file and creates a
     * JavaMail session that will be used to send all mail.
     */
    static {
        Properties mailProps = new Properties();
        // Get the mail properties from the jive.properties file and add
        // its values to the mailProps object
        String host = JiveGlobals.getJiveProperty("mail.smtp.host");
        String port = JiveGlobals.getJiveProperty("mail.smtp.port");
        String debug = JiveGlobals.getJiveProperty("mail.debug");
        if (host != null) {
            mailProps.setProperty("mail.smtp.host", host);
        }
        // check the port for errors (if specified)
        if (port != null && !port.equals("")) {
            try {
                int num = Integer.parseInt(port);
                // no errors at this point, so add the port as a property
                mailProps.setProperty("mail.smtp.port", port);
            }
            catch (Exception e) {}
        }
        // optional mail debug (output is written to standard out)
        if ("true".equals(debug)) {
            mailProps.setProperty("mail.debug","true");
        }
        // Create the mail session
        session = Session.getDefaultInstance(mailProps, null);
    }

    // List of messages
    private List messages = null;

    /**
     * Creates a new EmailTask.
     */
    public EmailTask() {
        messages = new LinkedList();
    }

    /**
     * Runs the task, which sends all email messages that have been queued.
     */
    public void run() {
        try {
            Iterator messageIterator = messages.iterator();
            while (messageIterator.hasNext()) {
                try {
                    Message message = (Message)messageIterator.next();
                    Transport.send(message);
                }
                catch (MessagingException me) {
                    me.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Factory method to add a JavaMail message object to the internal list
     * of messages.
     *
     * @param message a message to send.
     */
    public void addMessage(Message message) {
        messages.add(message);
    }

    /**
     * Factory method to add a message by specifying its fields.<p>
     *
     * To use more advanced message features, use the
     * <code>addMessage(Message message)</code> method.<p>
     *
     * If parts of the message are invalid (ie, the toEmail is null) the message
     * won't be sent.
     *
     * @param toName the name of the recipient of this email.
     * @param toEmail the email address of the recipient of this email.
     * @param fromName the name of the sender of this email.
     * @param fromEmail the email address of the sender of this email.
     * @param subject the subject of the email.
     * @param body the body of the email.
     */
    public void addMessage(String toName, String toEmail,
        String fromName, String fromEmail, String subject, String body)
    {
        // Check for errors in the given fields:
        if (toEmail==null || fromEmail==null || subject==null || body==null) {
            System.err.println("Error sending email in EmailTask.java: "
                +"Invalid fields.");
        }
        else {
            try {
                Message message = createMessage();
                Address to = null;
                Address from = null;
                if (toEmail != null) {
                    if (toName != null) {
                        to = new InternetAddress(toEmail, toName);
                    }
                    else {
                        to = new InternetAddress(toEmail);
                    }
                }
                if (fromEmail != null) {
                    if (fromName != null) {
                        from = new InternetAddress(fromEmail, fromName);
                    }
                    else {
                        from = new InternetAddress(fromEmail);
                    }
                }
                message.setRecipient(Message.RecipientType.TO, to);
                message.setFrom(from);
                message.setSubject(subject);
                message.setContent(body,"text/plain");
                addMessage(message);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Factory method to return a blank JavaMail message. You should use the
     * object returned and set desired message properties. When done, pass the
     * object to the addMessage(Message) method.
     *
     * @return A new JavaMail message.
     */
    public Message createMessage() {
        return new MimeMessage(session);
    }
}