/**
 * $RCSfile: SplashScreen.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:46 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.io.*;
import java.net.URL;

/**
 * Creates a Jive splash screen, which is a small window that will appear
 * when users double click on the jive.jar file. It's assumed that an image
 * named splash.gif is locate at the root of the JAR file that this class
 * is being run from (jive.jar).
 */
public class SplashScreen extends Window {

    private Image image;

    /**
     * Creates a new SpashScreen.
     */
    public SplashScreen() throws Exception {
        super(new Frame());

        // Load the image from the JAR file. We use a MediaTracker so that we
        // can wait until the image is fully loaded before continuing.
        MediaTracker tracker = new MediaTracker(this);
        image = Toolkit.getDefaultToolkit().createImage(
                getClass().getResource("/splash.gif"));
        if (image != null) {
            tracker.addImage(image, 0);
            try {
                tracker.waitForID(0);
            }
            catch ( InterruptedException e) {  }
        }

        // Set the width and height of the window to be the size of the iamge.
        setSize(image.getWidth(this), image.getHeight(this));

        // Center the window on the screen.
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frame = getSize();
        setLocation((screen.width-frame.width)/2,(screen.height-frame.height)/2);

        setVisible(true);

        // Add a listener so that the splash screen will exit whenever it is
        // clicked on.
        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });

        // Play audio
        AudioClip clip = Applet.newAudioClip(getClass().getResource("/splash.mid"));
        clip.play();
    }

    public void paint(Graphics g) {
        // Draw the image.
        g.drawImage(image, 0, 0, getBackground(), this);
    }

    public static void main(String[] args) throws Exception {
        new SplashScreen();
    }
}