/*
 * Copyright (c) 2023 legoru / goroleo <legoru@me.com>
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
 * An animated layer that can blur the specified image. This class used quick and easy Laplace Blur
 * by Fedor Tukmakov <a href="https://github.com/impfromliga">@impfromliga</a>.<br>
 * After blurring, this layer will be hidden by changing its opacity.
 */
public class BlurLayer extends JComponent implements Runnable {

    /**
     * The current value of opaque (from 0 to 0xff)
     */
    private int opaque = 0xff;

    /**
     * Step value of opaque for the next frame
     */
    private final int opaqueStep = 7;

    /**
     * Time delay after every frame
     */
    private final int blurCount = 50;

    /**
     * The current blur value
     */
    private int blurStep;

    /**
     * Time delay after every frame
     */
    private final int delay = 7;

    /**
     * An animation direction: blurring or hiding
     */
    private boolean appearing = true;

    /**
     * A buffer to store pixels' info
     */
    private int[] buf;

    /**
     * Layer's width.
     */
    private int w;

    /**
     * Layer's height.
     */
    private int h;

    /**
     * An image to draw every frame.
     */
    private BufferedImage frame;

    /**
     * Creates of the blur layer.
     */
    public BlurLayer() {
    }

    /**
     * Sets the original image.
     *
     * @param bi an original image
     */
    public final void setImage(BufferedImage bi) {
        w = bi.getWidth();
        h = bi.getHeight();
        setSize(bi.getWidth(), bi.getHeight());
        if (frame == null || frame.getWidth() != w || frame.getHeight() != h) {
            frame = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            buf = new int[w * h];
        }
        frame.getGraphics().drawImage(bi, 0, 0, null);
        repaint();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                buf[x + y * w] = bi.getRGB(x, y);
            }
        }
    }

    /**
     * Starts the blurring thread.
     */
    public void startBlur() {
        appearing = true;
        blurStep = 0;
        Thread t = new Thread(this);
        t.start();
    }

    /**
     * Starts the thread to hide the layer.
     */
    public void startHide() {
        appearing = false;
        opaque = 0xff;
        Thread t = new Thread(this);
        t.start();
    }

    /**
     * Blurs the image stored in the buffer.
     * Big Thanks to Fedor Tukmakov <a href="https://github.com/impfromliga">@impfromliga</a> for his Laplace Blur, quick and easy!
     * <a href="https://github.com/impfromliga/LaplaceBlur">Code</a>.
     * <a href="https://habr.com/ru/articles/427077/">Description</a> (in Russian).
     */
    private void blurFrame() {

        int x01 = 0x010101, x7f = 0x7f7f7f, op = 0xff000000;

        // 1st pass: horizontal from left to right
        for (int idx = h * w; idx > 0; idx -= w) {
            for (int t = 0, i = idx - w, n = w; n > 0; n--) {
                buf[i] = t = (x01 + t >> 1 & x7f) + (buf[i] >> 1 & x7f);
                i++;
            }
        }

        // 2nd pass: horizontal from right to left
        for (int idx = h * w; idx > 0; idx -= w) {
            for (int t = 0, i = idx - 1, n = w; n > 0; n--) {
                buf[i] = t = (x01 + t >> 1 & x7f) + (buf[i] >> 1 & x7f);
                i--;
            }
        }

        // 3d pass: vertical from bottom to top
        for (int idx = w; idx > 0; idx--) {
            for (int t = 0, i = w * h - idx, n = h; n > 0; n--) {
                buf[i] = t = (x01 + t >> 1 & x7f) + (buf[i] >> 1 & x7f);
                i -= w;
            }
        }

        // 4th pass: vertical from top to bottom
        for (int idx = w; idx > 0; idx--) {  //
            for (int t = 0, i = idx - 1, n = h; n > 0; n--) {
                buf[i] = t = (x01 + t >> 1 & x7f) + (buf[i] >> 1 & x7f);
                i += w;
            }
        }

        // sets pixels from buffer into frame image
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                frame.setRGB(x, y, (buf[x + y * w] & 0xffffff) | op);
            }
        }
    }

    /**
     * Applies a new opaque to the image stored in the buffer.
     */
    private void opaqueFrame() {
        int op = opaque << 24;
        // sets pixels from buffer into frame and apply new opaque
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                frame.setRGB(x, y, (buf[x + y * w] & 0xffffff) | op);
            }
        }
    }

    public void paintComponent(Graphics g) {
        g.drawImage(frame, 0, 0, null);
    }

    @Override
    public void run() {

        boolean working = true;

        while (working) {

            if (appearing) {
                blurFrame();
                blurStep++;
                if (blurStep > blurCount) {
                    working = false;
                }
            } else {
                opaqueFrame();
                opaque -= opaqueStep;
                if (opaque < 0) {
                    working = false;
                }
            }

            repaint();

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // do nothing
            }

        }

        onThreadFinished(appearing);
    }

    /**
     * This routine is called after the animation has done.
     *
     * @param appearing the direction of the animation performed: appearing (blurring) if <i>true</i>, or hiding if <i>false</i>.
     */
    public void onThreadFinished(boolean appearing) {
        // the routine to override it
    }

}
