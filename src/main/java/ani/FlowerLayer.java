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
 * This layer draws and unlimited rotates 2-colors flower.
 */
@SuppressWarnings("FieldCanBeLocal")
public class FlowerLayer extends JComponent implements Runnable {

    // An original image represented as a buffer 400x400 points
    private final int[] imgBuf = new int[400 * 400];

    /**
     * The image displayed every single frame of animation.
     */
    private BufferedImage imgFrame;

    /**
     * The current rotation angle.
     */
    private double angleCurrent = 0;

    /**
     * The increments of the rotation angle for the next animation frame.
     */
    private final double angleIncrement = 1d / 180d * Math.PI;

    /**
     * Delay between two frames.
     */
    private final long delay = 10;

    /**
     * The X coordinate of the rotation point.
     */
    private double rotationX = 250;

    /**
     * The Y coordinate of the rotation point.
     */
    private double rotationY = 250;

    /**
     * The current alpha-channel value to appear the image.
     */
    private double masterAlpha;

    /**
     * The increment of the alpha-channel value for the next frame.
     */
    private final double alphaIncrement = 0.05d;

    /**
     * Working is true when animation is in process
     */
    private boolean working = false;

    /**
     * Creates the flower layer.
     */
    public FlowerLayer() {
        prepareFlower();
        setSize(500, 350);
    }

    @Override
    public final void setSize(int width, int height) {
        super.setSize(width, height);
        if (imgFrame == null
                || imgFrame.getWidth() != width
                || imgFrame.getHeight() != height) {
            imgFrame = new BufferedImage(width, height, 2);
        }
    }

    /**
     * Sets the rotation point coordinates.
     *
     * @param x horizontal coordinate
     * @param y vertical coordinate
     */
    public void setRotationPoint(int x, int y) {
        rotationX = x;
        rotationY = y;
        drawCurrentFrame();
        repaint();
    }

    /**
     * Starts the animation thread.
     */
    public void start() {
        masterAlpha = 0.0d;
        clearFrame();
        working = true;
        Thread t = new Thread(this);
        t.start();
    }

    /**
     * Stops the animation thread.
     */
    public void stop() {
        working = false;
    }

    /**
     * Mixes two colors. Please note (w1 + w2) must be 1 or less.
     *
     * @param clr1 first color
     * @param clr2 second color
     * @param w1   first color's weight fraction (from 0 to 1)
     * @param w2   second color's weight fraction (from 0 to 1)
     * @return mixed color
     */
    private int mixColors(int clr1, int clr2, double w1, double w2) {
        int a = (int) (w1 * ((clr1 >> 24) & 0xff)
                + w2 * ((clr2 >> 24) & 0xff));    // alpha component
        int r = (int) (w1 * ((clr1 >> 16) & 0xff)
                + w2 * ((clr2 >> 16) & 0xff));    // red component
        int g = (int) (w1 * ((clr1 >> 8) & 0xff)
                + w2 * ((clr2 >> 8) & 0xff));     // green component
        int b = (int) (w1 * (clr1 & 0xff)
                + w2 * (clr2 & 0xff));            // blue component
        return (b & 0xff) | ((g & 0xff) << 8) | ((r & 0xff) << 16) | ((a & 0xff) << 24);
    }

    /**
     * Draws the flower at the original image.
     */
    private void prepareFlower() {

        int x0 = 200, y0 = 200; // center point

        // pixel 
        double dx, dy;     // coordinates relative to the center
        double r, a;       // polar coordinates: radius and angle 
        int clr;           // color for every pixel 

        int sectors = 18;

        // angles
        // secAngle - angle for one sector 
        double secAngle = 2 * Math.PI / sectors;

        // one color's part of the sector (with 2 transit areas)
        double clrAngle = secAngle * 0.78;

        // size of the area (in angles) where one color transits to another
        double shadeAngle = (clrAngle * 2 - secAngle) / 2;

        int clr1 = 0xfffd68b3; // color1 0xAARRGGBB
        int clr2 = 0xfffff003; // color2 0xAARRGGBB
        double alpha1, alpha2; // transparency of color1 and color2; 

        double maxRad1 = 190.0d, maxRad2 = 200.0d; // radius for color1 and color2
        double shRad1 = 190.0d, shRad2 = 200.0d; // shade radius - where transparency is increasing
        double maxRad, shRad; // max radius and shade radius of the colors' transit areas

        for (int y = 0; y < 400; y++) {
            // coordinates relative to the center
            dy = y0 - y;

            for (int x = 0; x < 400; x++) {

                // coordinates relative to the center
                dx = x - x0;

                // Polar coordinates:
                // point's radius
                r = Math.sqrt(dx * dx + dy * dy);
                // point's angle 
                a = Math.atan2(dy, dx);
                // we need positive value for the angle
                if (a < 0) {
                    a += 2 * Math.PI;
                }

                // inside every sector 
                a = (a % secAngle);

                if (a < shadeAngle) {
                    // color 2 -> color 1
                    alpha1 = a / shadeAngle;
                    alpha2 = 1.0 - alpha1;
                    maxRad = maxRad1 * alpha1 + maxRad2 * alpha2;
                    shRad = shRad1 * alpha1 + shRad2 * alpha2;
                    clr = mixColors(clr1, clr2, alpha1, alpha2);

                } else if (a < clrAngle - shadeAngle) {
                    // body of the color 1 
                    // alpha1 = 1; - don't need to assign
                    // alpha2 = 0; - don't need to assign
                    maxRad = maxRad1;
                    shRad = shRad1;
                    clr = clr1;

                } else if (a < clrAngle) {
                    // color 1 -> color 2
                    alpha1 = (clrAngle - a) / shadeAngle;
                    alpha2 = 1.0 - alpha1;
                    maxRad = maxRad1 * alpha1 + maxRad2 * alpha2;
                    shRad = shRad1 * alpha1 + shRad2 * alpha2;
                    clr = mixColors(clr1, clr2, alpha1, alpha2);

                } else {
                    // (a < secAngle)
                    // body of the color 2
                    // alpha1 = 0; - don't need to assign
                    // alpha2 = 1; - don't need to assign
                    maxRad = maxRad2;
                    shRad = shRad2;
                    clr = clr2;
                }

                // transparency depends on radius
                if (r >= (maxRad - shRad)
                        && r < maxRad) {
                    // transparency from (maxRad - shRad) to (maxRad)
                    int alpha = (int) (((maxRad - r) / shRad) // current shade coordinate
                            * ((clr >> 24) & 0xff));              // alpha channel from the pix
                    clr = (clr & 0xffffff) | ((alpha & 0xff) << 24);

                } else if (r >= maxRad) {
                    // pixel is outside the radius
                    clr = 0;
                }

                imgBuf[x + y * 400] = clr;
            }
        }
    }

    /**
     * Clears frame before the animation start.
     */
    private void clearFrame() {
        if (imgFrame != null) {
            Graphics2D g = (Graphics2D) imgFrame.getGraphics();
            g.setBackground(new Color(0, 0, 0, 0));
            g.clearRect(0, 0, imgFrame.getWidth(), imgFrame.getHeight());
        }
        repaint();
    }

    /**
     * Draws the frame using the current rotation angle.
     */
    private void drawCurrentFrame() {
        int w = imgFrame.getWidth();
        int h = imgFrame.getHeight();
        double cx = 400 / 2.0d; // center of the original image
        double cy = 400 / 2.0d; // center of the original image
        int alpha;

        double dx, dy;     // coordinates relative to the center
        double ox, oy;     // coordinates of the original image
        double r, a;       // polar coordinates: radius and angle 
        int clr;           // color for every pixel 

        for (int x = 0; x < w; x++) {

            if (x == rotationX) {
                dx = 0;
            } else if (x < rotationX) {
                dx = (x - rotationX) / rotationX * cx;
            } else {
                dx = (x - rotationX) / (w - rotationX) * cx;
            }

            for (int y = 0; y < h; y++) {

                // We have a point (x, y) of the imgFrame. 
                // first we need to calculate corresponding coordinates (dx, dy) 
                // of the original image.
                // BUT: rotation point of the imgFrame can be moved from the center,
                // so we have two different scale values (both for horizontal and 
                // vertical coordinates): 
                //   a) from the beginning of the image to the rotation center
                //   b) from the rotation center to the image's end.

                if (y == rotationY) {
                    dy = 0;
                } else if (y < rotationY) {
                    dy = (rotationY - y) / rotationY * cy;
                } else {
                    dy = (rotationY - y) / (h - rotationY) * cy;
                }

                // calculating corresponding polar coordinates of the original image 
                // radius
                r = Math.sqrt(dx * dx + dy * dy);
                // angle
                a = Math.atan2(dy, dx);

                // doing rotate 
                a += angleCurrent;

                // calculate new coordinates after rotate
                ox = r * Math.cos(a) + cx;
                oy = r * Math.sin(a) + cy;

                // getting pixel color from the new coordinates
                if (ox < 0 || ox >= 400
                        || oy < 0 || oy >= 400) {
                    clr = 0;
                } else {
                    clr = imgBuf[(int) ox + (int) oy * 400];
                }

                // apply the opacity
                if (masterAlpha < 1) {
                    alpha = (clr >> 24) & 0xff;
                    if (alpha != 0) {
                        alpha = ((int) (masterAlpha * alpha)) & 0xff;
                        clr = (alpha << 24) | (clr & 0xffffff);
                    }
                }

                // setting color to imgFrame
                imgFrame.setRGB(x, y, clr);
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
                }
            }

            angleCurrent += angleIncrement;
            if (angleCurrent > Math.PI) {
                angleCurrent -= Math.PI;
            }

            drawCurrentFrame();
            repaint();

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // do nothing
            }
        }
    }
}
