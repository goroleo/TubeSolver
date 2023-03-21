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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 * This is an animation layer when the image appears from the center. The animation
 * shows the image resizing (from zero to normal) and its opacity changing
 * (from zero to one).
 */
public class ScaleLayer extends JComponent implements Runnable {

    /**
     * An original image.
     */
    private BufferedImage imgOrig;

    /**
     * An image that stretched to the proper size.
     */
    private BufferedImage imgStretched;

    /**
     * An image of the every single frame.
     */
    private BufferedImage imgFrame;

    /**
     * Delay between two frames in milliseconds.
     */
    private final long delay = 20;

    /**
     * The opacity of the image (from 0 to 1) of the current frame.
     */
    private double masterAlpha;

    /**
     * The increment of the opacity between two frames.
     */
    private final double alphaIncrement = 0.06d;

    /**
     * If <b>true</b> then the thread is working, and we don't need to start it again.
     */
    private boolean working = false;

    /**
     * Creates the Scale Layer.
     * @param bi image to scale.
     */
    public ScaleLayer(BufferedImage bi) {
        setImage(bi);
    }

    /**
     * Sets the image to scale.
     * @param bi new buffered image.
     */
    public void setImage(BufferedImage bi) {
        imgOrig = bi;
        if (bi != null) {
            setSize(bi.getWidth(), bi.getHeight());
        }
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);

        if (width > imgOrig.getWidth()) {
            imgStretched = new BufferedImage(width, imgOrig.getHeight(), 2);
            stretchImage();
        } else {
            imgStretched = imgOrig;
        }
        if (imgFrame == null
                || (imgFrame.getWidth() != width)
                || (imgFrame.getHeight() != height)) {
            imgFrame = new BufferedImage(width, height, 2);
        }
    }

    /**
     * Clears the frame before the thread starts.
     */
    public void clearFrame() {
        if (imgFrame != null) {
            Graphics2D g = (Graphics2D) imgFrame.getGraphics();
            g.setBackground(new Color(0, 0, 0, 0));
            g.clearRect(0, 0, imgFrame.getWidth(), imgFrame.getHeight());
        }
        repaint();
    }

    /**
     * Stretches the image width.
     */
    private void stretchImage() {
        Graphics2D g = (Graphics2D) imgStretched.getGraphics();
        int dw = getWidth() - imgOrig.getWidth();
        int cx = imgOrig.getWidth() / 2;

        g.setBackground(new Color(0, 0, 0, 0));
        g.clearRect(0, 0, imgStretched.getWidth(), imgStretched.getHeight());

        g.drawImage(imgOrig, 0, 0, cx, imgStretched.getHeight(),
                0, 0, cx, imgOrig.getHeight(), null);
        g.drawImage(imgOrig, cx + dw, 0, imgStretched.getWidth(), imgStretched.getHeight(),
                cx, 0, imgOrig.getWidth(), imgOrig.getHeight(), null);

        for (int x = 0; x < dw; x++) {
            for (int y = 0; y < imgStretched.getHeight(); y++) {
                imgStretched.setRGB(cx + x, y, imgOrig.getRGB(cx, y));
            }
        }
    }

    /**
     * Starts the animation.
     */
    public void start() {
        masterAlpha = 0.0d;
        clearFrame();
        working = true;
        Thread t = new Thread(this);
        t.start();
    }

    /**
     * Stops the animation.
     */
    public void stop() {
        working = false;
    }

    /**
     * Draws the frame using current masterAlpha value.
     */
    private void drawCurrentFrame() {
        int w = imgStretched.getWidth();
        int h = imgStretched.getHeight();
        int dw = (int) (w * masterAlpha);
        int dh = (int) (h * masterAlpha);

        int alpha, pix;

        int startX = (getWidth() - dw) / 2;
        if (startX < 0) {
            startX = 0;
        }
        int startY = (getHeight() - dh) / 2;
        if (startY < 0) {
            startY = 0;
        }

        for (int x = 0; x < dw; x++) {
            double dx = (double) x * (w - 1) / (dw - 1);
            for (int y = 0; y < dh; y++) {
                double dy = (double) y * (h - 1) / (dh - 1);
                pix = imgStretched.getRGB((int) dx, (int) dy);
                alpha = (int) (((pix >> 24) & 0xff) * masterAlpha);
                imgFrame.setRGB(x + startX, y + startY,
                        ((alpha & 0xff) << 24) | (pix & 0xffffff));
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
        while (working) {

            if (masterAlpha < 1) {
                masterAlpha += alphaIncrement;
                if (masterAlpha > 1) {
                    masterAlpha = 1.0d;
                    working = false;
                }
            }

            drawCurrentFrame();
            repaint();

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // do nothing
            }
        }
        onThreadFinished();
    }

    /**
     * This routine calls when the animation has done.
     */
    public void onThreadFinished() {
        // the routine to override it
    }

}
