/*
 * Copyright (c) 2022 legoru / goroleo <legoru@me.com>
 *
 * This software is distributed under the <b>MIT License.</b>
 * The full text of the License you can read here:
 * https://choosealicense.com/licenses/mit/
 *
 * Use this as you want! ))
 */
package ani;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * An animation layer on which an image smoothly fades in and out without changing its size.
 * The opacity of an image is controlled by changing the alpha channel of its pixels.<br>
 * This layer can reveal an image from current to maximum opacity (see doShow),
 * hide an image from current opacity to zero (see doHide), and pulsate an image, i.e.
 * show and hide it without stopping (see doPulse).
 */
public class ShadeLayer extends JComponent implements Runnable {

// -----------------------------------------------------
//     Animation settings
//

    /**
     * If true, the opacity of the image will change smoothly. Otherwise, the image will
     * draw (or hide) without an animation.
     */
    public Boolean useAnimation = true;

    /**
     * An increment of the Alpha-channel value when the image appears.
     */
    private final Double alphaIncUp = 0.06d;

    /**
     * An increment of the Alpha-channel value when the image disappears.
     */
    private final Double alphaIncDown = -0.1d;

    /**
     * Delay between two frames, in milliseconds.
     */
    private final int delayStd = 30;

    /**
     * Delay when the picture is fully appeared.
     */
    private final int delayAtUp = 100;

    /**
     * Delay when the picture disappeared.
     */
    private final int delayAtDown = 0;


// -----------------------------------------------------
//     Images
//

    /**
     * An original image.
     */
    private BufferedImage imgOrig;

    /**
     * The image for every single frame.
     */
    private BufferedImage imgFrame;

// -----------------------------------------------------
//     Stopping settings
//

    /**
     * If true, the cycle is working, we don't need to restart it.
     */
    private boolean working = false;

    /**
     * if true, the cycle will stop when the picture appears or disappears.
     */
    private boolean limited = false;

    /**
     * An Alpha-channel value where the cycle has to stop.
     */
    private double alphaLimit;

// -----------------------------------------------------
//     Values for the current frame
//
    /**
     * Current alpha value.
     */
    private Double alpha;

    /**
     * Current alpha increment.
     */
    private Double alphaInc;

// -----------------------------------------------------
//     Routines
//

    /**
     * Creates the Shade Layer.
     *
     * @param bi an image to animate.
     */
    public ShadeLayer(BufferedImage bi) {
        restoreAlpha();
        setImage(bi);
    }

    /**
     * Sets the new image to the Shade Layer.
     *
     * @param bi an image to animate.
     */
    public void setImage(BufferedImage bi) {
        imgOrig = bi;
        if (imgFrame == null || getWidth() != bi.getWidth() || getHeight() != bi.getHeight()) {
            imgFrame = new BufferedImage(bi.getWidth(), bi.getHeight(), 2);
            setBounds(0, 0, bi.getWidth(), bi.getHeight());
        }
        drawCurrentFrame();
    }

    /**
     * Starts the animation.
     */
    private void start() {
        if (!working) {
            working = true;
            Thread t = new Thread(this);
            t.start();
        }
    }

    /**
     * Runs the animation in pulse mode, i.e. shows and hides an image without stopping.
     * If useAnimation is false, it does nothing.
     */
    public void doPulse() {
        if (useAnimation) {
            limited = false;
            start();
        }
    }

    /**
     * Smoothly shows the image, from current opacity to maximum opacity.
     * But if useAnimation is false, just draws the image.
     */
    public void doShow() {
        limited = true;
        alphaLimit = 1.0d;
        alphaInc = alphaIncUp;

        if (useAnimation) {
            start();
        } else {
            alpha = alphaLimit;
            drawCurrentFrame();
            repaint();
        }
    }

    /**
     * Smoothly hides the image, from current opacity to zero.
     * But if useAnimation is false, just makes the image invisible.
     */
    public void doHide() {
        limited = true;
        alphaLimit = 0.0d;
        alphaInc = alphaIncDown;

        if (useAnimation) {
            start();
        } else {
            alpha = alphaLimit;
            drawCurrentFrame();
            repaint();
        }
    }

    /**
     * Sets the alpha channel value to the initial.
     */
    public void restoreAlpha() {
        alpha = 0.0d;
        alphaInc = alphaIncUp;
    }

    /**
     * Draws the frame using current alpha value.
     */
    private void drawCurrentFrame() {
        int pix, newAlpha;
        for (int x = 0; x < imgOrig.getWidth(); x++) {
            for (int y = 0; y < imgOrig.getHeight(); y++) {
                pix = imgOrig.getRGB(x, y);
                newAlpha = (int) Math.round(((pix >> 24) & 0xff) * alpha);
                pix = (pix & 0xffffff) | ((newAlpha & 0xff) << 24);
                imgFrame.setRGB(x, y, pix);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(imgFrame, 0, 0, null);
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        while (!(limited && alpha == alphaLimit)) {

            int delay = delayStd;
            alpha += alphaInc;

            if (alpha > 1.0d) {
                alpha = 1.0d;
                alphaInc = alphaIncDown;
                delay = limited ? 0 : delayAtUp;
            } else if (alpha < 0.0d) {
                alpha = 0.0d;
                alphaInc = alphaIncUp;
                delay = limited ? 0 : delayAtDown;
            }

            drawCurrentFrame();
            repaint();

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // do nothing
            }
        }
        working = false; // thread is done 
        onThreadFinished();
    }

    /**
     * This routine calls when the animation has done.
     */
    public void onThreadFinished() {
        // the routine to override it
    }

}
