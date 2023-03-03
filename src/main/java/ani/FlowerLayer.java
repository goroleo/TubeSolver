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
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

public class FlowerLayer extends JComponent implements Runnable {

    private final BufferedImage imgOrig = new BufferedImage(400, 400, 2);

    private BufferedImage imgFrame;

    private double curAngle = 0;
    private final double dAngle = 1d / 180d * Math.PI;
    private final double endAngle = 20d / 180d * Math.PI;

    private final long delay = 30;

    private final double origCenterX = imgOrig.getWidth() / 2.0d;
    private final double origCenterY = imgOrig.getHeight() / 2.0d;

    private double rotationX = 250;
    private double rotationY = 250;
    private double masterAlpha;
    private final double deltaAlpha = 0.05d;
    boolean working = false;

    public FlowerLayer() {
        prepareFlower();

        setBackground(null);
        setForeground(null);
        setSize(500, 350);

        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        Graphics2D gOrig = (Graphics2D) imgOrig.getGraphics();
        gOrig.setRenderingHints(rh);
    }

    @Override
    public final void setSize(int width, int height) {
        super.setSize(width, height);
        imgFrame = new BufferedImage(width, height, 2);
    }

    public void setRotationPoint(int x, int y) {
        rotationX = x;
        rotationY = y;
        drawCurrentFrame();
        repaint();
    }

    public void start() {
        masterAlpha = 0.0d;
        clearFrame();
        working = true;
        Thread t = new Thread(this);
        t.start();
    }

    public void stop() {
        working = false;
    }

    public int mixColors(int clr1, int clr2, double alpha1, double alpha2) {
        int R = (int) (alpha1 * (clr1 & 0xff)
                + alpha2 * (clr2 & 0xff));
        int G = (int) (alpha1 * ((clr1 >> 8) & 0xff)
                + alpha2 * ((clr2 >> 8) & 0xff));
        int B = (int) (alpha1 * ((clr1 >> 16) & 0xff)
                + alpha2 * ((clr2 >> 16) & 0xff));
        int A = (int) (alpha1 * ((clr1 >> 24) & 0xff)
                + alpha2 * ((clr2 >> 24) & 0xff));
        return (R & 0xff) | ((G & 0xff) << 8) | ((B & 0xff) << 16) | ((A & 0xff) << 24);
    }

    public final void prepareFlower() {

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

        Color clr1 = new Color(0xfd68b3); // color1
        Color clr2 = new Color(0xfff003); // color2
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
                    clr = mixColors(clr1.getRGB(), clr2.getRGB(), alpha1, alpha2);

                } else if (a < clrAngle - shadeAngle) {
                    // body of the color 1 
                    // alpha1 = 1; - don't need to assign
                    // alpha2 = 0; - don't need to assign
                    maxRad = maxRad1;
                    shRad = shRad1;
                    clr = clr1.getRGB();

                } else if (a < clrAngle) {
                    // color 1 -> color 2
                    alpha1 = (clrAngle - a) / shadeAngle;
                    alpha2 = 1.0 - alpha1;
                    maxRad = maxRad1 * alpha1 + maxRad2 * alpha2;
                    shRad = shRad1 * alpha1 + shRad2 * alpha2;
                    clr = mixColors(clr1.getRGB(), clr2.getRGB(), alpha1, alpha2);

                } else {
                    // (a < secAngle)
                    // body of the color 2
                    // alpha1 = 0; - don't need to assign
                    // alpha2 = 1; - don't need to assign
                    maxRad = maxRad2;
                    shRad = shRad2;
                    clr = clr2.getRGB();
                }

                // transparency depends on radius
                if (r >= (maxRad - shRad)
                        // main body, pixel color is calculated above, do nothing
                        && r < maxRad) {
                    // transparency from (maxRad - shRad) to (maxRad)
                    int alpha = (int) (((maxRad - r) / shRad) // current shade coordinate
                            * ((clr >> 24) & 0xff));              // alpha channel from the pix
                    clr = (clr & 0xffffff) | ((alpha & 0xff) << 24);

                } else {
                    // (r >= maxRad)
                    // pixel is outside the radius
                    clr = 0;
                }

                imgOrig.setRGB(x, y, clr);
            }
        }
    }

    public void clearFrame() {
        if (imgFrame != null) {
            Graphics2D g = (Graphics2D) imgFrame.getGraphics();
            g.setBackground(new Color(0, 0, 0, 0));
            g.clearRect(0, 0, imgFrame.getWidth(), imgFrame.getHeight());
        }
        repaint();
    }

    public void drawCurrentFrame() {
        int w = imgFrame.getWidth();
        int h = imgFrame.getHeight();
        int alpha;

        double dx, dy;     // coordinates relative to the center
        double ox, oy;     // coordinates relative to the center
        double r, a;       // polar coordinates: radius and angle 
        int clr;           // color for every pixel 

        for (int x = 0; x < w; x++) {
            
            if (x == rotationX) {
                dx = 0;
            } else if (x < rotationX) {
                dx = (x - rotationX) / rotationX * origCenterX;
            } else {
                dx = (x - rotationX) / (w - rotationX) * origCenterX;
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
                    dy = (rotationY - y) / rotationY * origCenterY;
                } else {
                    dy = (rotationY - y) / (h - rotationY) * origCenterY;
                }

                // calculating corresponding polar coordinates of the original image 
                // radius
                r = Math.sqrt(dx * dx + dy * dy);
                // angle
                a = Math.atan2(dy, dx);

                // doing rotate 
                a += curAngle;

                // calculate new coordinates after rotate
                ox = r * Math.cos(a) + origCenterX;
                oy = r * Math.sin(a) + origCenterY;

                // getting pixel color from the new coordinates
                if (ox < 0 || ox >= imgOrig.getWidth()
                        || oy < 0 || oy >= imgOrig.getHeight()) {
                    clr = 0;
                } else {
                    clr = imgOrig.getRGB((int) ox, (int) oy);
                }

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

    @Override
    public void run() {
        while (working) {

            if (masterAlpha < 1) {
                masterAlpha += deltaAlpha;
                if (masterAlpha > 1) {
                    masterAlpha = 1.0d;
                }
            }

            curAngle += dAngle;
            if (curAngle > endAngle) {
                curAngle -= endAngle;
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
