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
 * The layer to replace one image <i>(imgFirst)</i> to another <i>(imgSecond)</i>. One picture smoothly
 * dissolves and at the same time shifts away from its original place, and the second one smoothly appears
 * and moves towards the place of the first one. You can also exclude movement, then the first picture
 * will simply be replaced by the second.<br>
 * Both of images fading and appearing using Alpha-channel. And the Alphas change according to an
 * exponential function, not linear.<br>
 * Also, both of images can be <i>null</i>
 */
@SuppressWarnings("FieldCanBeLocal")
public class SlideLayer extends JComponent implements Runnable {

// -----------------------------------------------------
//     Animation settings
//
    /**
     * Delay (in milliseconds) before start to calculate and draw the next frame.
     */
    private final int delay = 12;

    /**
     * Size of step for movement in every frame. Must be more than 0 and less than 1
     */
    private final double pathStep = 0.055d;

    /**
     * Exponent degree for the <i>ImgFirst</i> fading.<br>
     * NB: Images fading and appearing using Alpha-channel. The Alphas change according to an exponential function, not linear.
     * And you can play with these exponent degrees but be aware: <i>(alpha1 + alpha2)</i> must always be 1 or less.
     */
    private double expDegree1 = 1.0d;

    /**
     * Exponent degree for the <i>ImgSecond</i> appearing.<br>
     * NB: Images fading and appearing using Alpha-channel. The Alphas change according to an exponential function, not linear.
     * And you can play with these exponent degrees but be aware: <i>(alpha1 + alpha2)</i> must always be 1 or less.
     */
    private double expDegree2 = 0.5d;

    /**
     * Enables and disables filling animation. If <b>true</b> then animation is enabled.
     */
    @SuppressWarnings("CanBeFinal")
    public Boolean useAnimation = true;

// -----------------------------------------------------
//     Images
//

    /**
     * The first (initial) picture that will be replaced.
     */
    private BufferedImage imgFirst;

    /**
     * The second picture that replaces the first.
     */
    private BufferedImage imgSecond;

    /**
     * Current frame's picture, calculating for every frame.
     */
    private BufferedImage imgFrame;

    /**
     * Width of the picture
     */
    private int w;

    /**
     * Height of the picture
     */
    private int h;

// -----------------------------------------------------
//     Movement variables
//

    /**
     * Start position of the <i>imgFirst</i>. 0 means it stands right in the frame.<br>
     * H - horizontal direction, V - vertical direction.
     */
    private final int startH1 = 0, startV1 = 0;

    /**
     * End position of the <i>imgFirst</i>, where it goes to.<br>
     * H - horizontal direction, V - vertical direction.<br>
     * Positive horizontal value means move an image to right / negative moves to left.<br>
     * Positive vertical value means move an image to bottom / negative moves to top.<br>
     * 0 means it stands right in the frame.
     */
    private int endH1, endV1;

    /**
     * Start position of the <i>imgSecond</i>, where it comes from.<br>
     * H - horizontal direction, V - vertical direction.<br>
     * Positive horizontal value means move an image from right / negative moves from left.<br>
     * Positive vertical value means move an image from bottom / negative moves from top.<br>
     * 0 means it stands right in the frame.
     */
    private int startH2, startV2;

    /**
     * End position of the <i>imgSecond</i>. 0 means it stands right in the frame.<br>
     * H - horizontal direction, V - vertical direction.
     */
    private final int endH2 = 0, endV2 = 0;

    /**
     * Current frame's precision for <i>imgFirst</i>.<br>
     * H - horizontal direction, V - vertical direction.
     */
    private int curH1, curV1;

    /**
     * Current frame's precision for <i>imgSecond</i>.<br>
     * H - horizontal direction, V - vertical direction.
     */
    private int curH2, curV2;

// -----------------------------------------------------
//     Other variables
//

    /**
     * Current frame's alpha for <i>ImgFirst</i>.
     *
     * @see #expDegree1
     */
    private double alpha1;

    /**
     * Current frame's alpha for <i>imgSecond</i>.
     *
     * @see #expDegree2
     */
    private double alpha2;

    /**
     * If <b>true</b> then the thread is working, and we don't need to start it again.
     */
    private boolean working = false;

    Thread t;


// -----------------------------------------------------
//     Routines
//

    /**
     * Creates a layer.
     *
     * @param firstImg the first image
     */
    public SlideLayer(BufferedImage firstImg) {

        setFirstImage(firstImg);
        // set the moving of the imgStart
        endH1 = 0; // no move in horizontal direction
        endV1 = 0; // no move in horizontal direction

        // set the moving of the imgEnd
        startH2 = 0; // no move in horizontal direction
        startV2 = 0; // no move in vertical direction
    }

    /**
     * Setter for the first image
     *
     * @param bi the first image
     */
    public final void setFirstImage(BufferedImage bi) {
        imgFirst = bi;
        if (bi != null) {
            setSize(bi.getWidth(), bi.getHeight());
        }
        restoreAlphas();
        restoreStartPosition();
        calculateCurrentFrame();
        repaint();
    }

    /**
     * Setter for the second image
     *
     * @param bi the second image
     */
    public void setSecondImage(BufferedImage bi) {
        imgSecond = new BufferedImage(w, h, 2); // 2 - TYPE_INT_ARGB. less imports use ))
        if (bi != null) {
            int newW = Math.min(bi.getWidth(), w);
            int newH = Math.min(bi.getHeight(), h);
            for (int x = 0; x < newW; x++) {
                for (int y = 0; y < newH; y++) {
                    imgSecond.setRGB(x, y, bi.getRGB(x, y));
                }
            }
        }
    }

    /**
     * Sets the Second image to be the First image. Then we can set another second image and run.
     */
    public void setSecondImageToFirst() {
        setFirstImage(imgSecond);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);

        // updating imgFrame's size
        if (imgFrame == null || w != width || h != height) {
            imgFrame = new BufferedImage(width, height, 2);
            w = width;
            h = height;
        }
    }

    /**
     * Restore opaque / transparency of images to start the cycle again.
     */
    public void restoreAlphas() {
        // imgFirst is visible
        alpha1 = 1.0d;
        // imgSecond is hide
        alpha2 = 0.0d;
    }

    /**
     * Restore start position of both images.
     */
    public void restoreStartPosition() {
        curH1 = startH1;
        curH2 = startH2;
        curV1 = startV1;
        curV2 = startV2;
    }

    /**
     * Set the end position of the First image, where it goes to.<br>
     * Positive horizontal value means move an image to right / negative moves to left.
     * Positive vertical value means move an image to bottom / negative moves to top.
     * 0 means it stands right in the frame.
     *
     * @param imgFirstHoriz horizontal position
     * @param imgFirstVert  vertical position
     */
    public void setEndPosOfFirstImg(int imgFirstHoriz, int imgFirstVert) {
        endH1 = imgFirstHoriz;
        endV1 = imgFirstVert;
    }

    /**
     * Set the start position of the second image, where it comes from.<br>
     * Positive horizontal value means move an image from right / negative moves from left.
     * Positive vertical value means move an image from bottom / negative moves from top.
     * 0 means it stands right in the frame.
     *
     * @param imgSecondHoriz horizontal position
     * @param imgSecondVert  vertical position
     */
    public void setStartPosOfSecondImg(int imgSecondHoriz, int imgSecondVert) {
        startH2 = imgSecondHoriz;
        startV2 = imgSecondVert;
    }

    /**
     * Images fading and appearing using Alpha-channel. The Alphas change according to an exponential function,
     * not linear. And you can play with these exponent degrees but be aware: (alpha1 + alpha2) must always
     * be 1 or less.
     *
     * @param deg1 Exponential degree for the first image
     * @param deg2 Exponential degree for the second image
     */
    public void setExpDegrees(double deg1, double deg2) {
        expDegree1 = deg1;
        expDegree2 = deg2;
    }

    /**
     * Starts the animation thread. If <i>useAnimation</i> is not set, then just draw the Second image instead the First.
     */
    public void start() {
        if (useAnimation) {
            if (!working) {
                working = true;
                t = new Thread(this);
                t.start();
            }
        } else {
            alpha1 = 0.0;
            alpha2 = 1.0;
            curH1 = endH1;
            curH2 = endH2;
            curV1 = endV1;
            curV2 = endV2;
            calculateCurrentFrame();
            repaint();
        }
    }

    /**
     * Calculates the animation frame using current movement and transparency values.
     */
    private void calculateCurrentFrame() {

        // pixels from the First and Second images
        int pix1, pix2;
        // recalculated images' coordinates
        int dx, dy;
        // components for the resulting color (Reg, Green, Blue, Alpha)
        int r, g, b, a;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {

                // getting pixel from the First image
                dx = x - curH1;
                dy = y - curV1;
                if (imgFirst != null
                        && dx >= 0 && dx < w
                        && dy >= 0 && dy < h) {
                    pix1 = imgFirst.getRGB(dx, dy);
                } else {
                    pix1 = 0;
                }

                // getting pixel from the Second image
                dx = x - curH2;
                dy = y - curV2;
                if (imgSecond != null
                        && dx >= 0 && dx < w
                        && dy >= 0 && dy < h) {
                    pix2 = imgSecond.getRGB(dx, dy);
                } else {
                    pix2 = 0;
                }

                // calculating components for resulting color 
                a = (int) Math.round(alpha1 * ((pix1 >> 24) & 0xff)
                        + alpha2 * ((pix2 >> 24) & 0xff));
                r = (int) Math.round(alpha1 * ((pix1 >> 16) & 0xff)
                        + alpha2 * ((pix2 >> 16) & 0xff));
                g = (int) Math.round(alpha1 * ((pix1 >> 8) & 0xff)
                        + alpha2 * ((pix2 >> 8) & 0xff));
                b = (int) Math.round(alpha1 * (pix1 & 0xff)
                        + alpha2 * (pix2 & 0xff));

                // setting a pixel to imgFrame
                pix1 = ((a & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                imgFrame.setRGB(x, y, pix1);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(imgFrame, 0, 0, null);
    }

    @Override
    public void run() {
        double curPos = 0;

        while (curPos < 1.0) {

            alpha1 = Math.exp(-expDegree1 * curPos) * (1 - curPos);
            alpha2 = Math.exp(-expDegree2 * (1 - curPos)) * curPos;

            curH1 = startH1 + (int) Math.round((endH1 - startH1) * curPos);
            curH2 = startH2 + (int) Math.round((endH2 - startH2) * curPos);
            curV1 = startV1 + (int) Math.round((endV1 - startV1) * curPos);
            curV2 = startV2 + (int) Math.round((endV2 - startV2) * curPos);

            calculateCurrentFrame();
            repaint();

            curPos += pathStep;

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // do nothing
            }
        }

        // we have to repaint the final frame
        // 'cause the cycle can stop before doing it :)
        alpha1 = 0.0;
        alpha2 = 1.0;
        curH1 = endH1;
        curV1 = endV1;
        curH2 = endH2;
        curV2 = endV2;
        calculateCurrentFrame();
        repaint();

        working = false; // thread is done 
        onThreadFinished();
    }

    /**
     * Calling this routine when the thread was finish its work. This is a routine to override if you need it.
     */
    @SuppressWarnings("EmptyMethod")
    public void onThreadFinished() {
        // routine to override it
    }
}
