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
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import static gui.MainFrame.palette;

/**
 * This layer draws and rotates a drum (a torus) composed of the palette colors. <br>
 * The drum (torus) consists of color sectors. Each sector has its own color
 * corresponding to the color from the palette. The number of drum sectors
 * matches the number of colors in the palette.
 * Sectors are separated by gaps.
 * All values here are in relative terms.
 * For example each sector size is 1.00. Spacing size is 0.08 of the sector size.<br>
 * The transparency fan will draw above the drum. It rotates in the opposite direction.
 */

public class WheelLayer extends JComponent implements Runnable {

    // radian constants
    final double PI = Math.PI;
    final double TWO_PI = 2 * PI;

    /**
     * The inner radius of the drum (tor).
     */
    private final int r_Min = 50;

    /**
     * The outer radius of the drum (tor).
     */
    private final int r_Max = 70;

    /**
     * Every single frame image
     */
    private final BufferedImage imgFrame
            = new BufferedImage(r_Max * 2, r_Max * 2, BufferedImage.TYPE_INT_ARGB);

    /**
     * Graphics instance to access the frame image
     */
    private final Graphics2D g2d
            = (Graphics2D) imgFrame.getGraphics();

    // -------------------------------------------------------------------
    //                 Values to render a drum (a torus) of colors.
    //
    // The drum (tor) consists of color sectors. Each sector has its own color 
    // corresponding to the color from the palette. The number of drum sectors 
    // matches the number of colors in the palette. 
    // Sectors are separated by gaps.    
    // All values here are in relative terms. 
    // For example each sector size is 1.00. Spacing size is 0.08 of the sector size.
    //

    /**
     * The spacing (gap) between drum's sectors in relative terms of the sector angle.
     */
    private final double spacing = 0.08;

    /**
     * The angle size of sector, in radians.
     */
    private final double colorsSectorAngle
            = TWO_PI / (palette.size() - 1) / (1 + spacing);

    /**
     * The angle size of spacing (gap), in radians
     */
    private final double colorsSpaceAngle
            = colorsSectorAngle * spacing;

    /**
     * Bezier angle to create a drum's curve.
     */
    private final double bezierAngle
            = Math.atan(Math.tan(colorsSectorAngle / 4) * 4 / 3);

    /**
     * The outer radius for Bezier points.
     */
    private final double br_Max
            = r_Max / Math.cos(bezierAngle);

    /**
     * The inner radius for Bezier points.
     */
    private final double br_Min
            = r_Min / Math.cos(bezierAngle);

    /**
     * The array of sector points <b>X</b> coordinates.
     */
    private final double[] cooX = new double[4];

    /**
     * The array of sector points <b>Y</b> coordinates.
     */
    private final double[] cooY = new double[4];

    // -------------------------------------------------------------------
    //                Values to render a transparency fan.
    //

    /**
     * The number of the fan blades (sectors).
     */
    private final int funSectorsNumber = 3;

    /**
     * The angle size of each fun blade, in radians
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final double funSectorAngle = TWO_PI / funSectorsNumber;  // radians

    /**
     * Maximum of fun's alpha-channel (from 0 to 1)
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final double maxOpacity = 1.0;

    /**
     * Minimum of fun's alpha-channel (from 0 to 1)
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final double minOpacity = 0.4;

    // -------------------------------------------------------------------
    //                        Rotation values
    //

    /**
     * Delay before start creating a new frame, in milliseconds
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final long delay = 30;

    /**
     * An angle speed of the Drum Of Colors, in radians.
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final double drumAngleIncrement = PI * 0.02;

    /**
     * An angle speed of the Transparency Fan, in radians.
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final double funAngleIncrement = PI * 0.03;

    /**
     * The current angle of the Drum, to create the current frame.
     */
    private double drumAngle;

    /**
     * The current angle of the Fun, to create the current frame.
     */
    private double funAngle;

    /**
     * This variable uses for an external interrupt of rotation cycle.
     */
    private boolean doStop = false;

    // -------------------------------------------------------------------
    //                        Routines
    //

    /**
     * Creates a WheelLayer.
     */
    public WheelLayer() {
        super();
        setBackground(null);
        setForeground(null);
        setSize(r_Max * 2, r_Max * 2);

        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);
        g2d.setBackground(new Color(255, 255, 255, 0));
    }

    /**
     * Starts a WheelLayer rotation.
     */
    public void start() {
        doStop = false;
        Thread t = new Thread(this);
        t.start();
    }

    /**
     * Stops a WheelLayer rotation.
     */
    public void stop() {
        doStop = true;
    }

    /**
     * Calculates and draws a current rotation frame.
     */
    private void drawCurrentFrame() {

        // variables 
        double angle1, angle2; // sector angles
        double bezierAngle1, bezierAngle2; // bezier angles
        double sin_a1, sin_a2, sin_ba1, sin_ba2; // sinuses
        double cos_a1, cos_a2, cos_ba1, cos_ba2; // co-sinuses

        int pix; // pixel
        int oldAlpha, newAlpha; // alpha-channel values

        // clear image frame
        g2d.clearRect(0, 0, r_Max * 2, r_Max * 2);

        // draw DRUM (TOR) of colors:
        for (int i = 1; i < palette.size(); i++) {

            angle1 = (colorsSectorAngle + colorsSpaceAngle) * i + drumAngle;
            sin_a1 = Math.sin(angle1);
            cos_a1 = Math.cos(angle1);

            angle2 = angle1 + colorsSectorAngle;
            sin_a2 = Math.sin(angle2);
            cos_a2 = Math.cos(angle2);

            bezierAngle1 = angle1 + bezierAngle;
            sin_ba1 = Math.sin(bezierAngle1);
            cos_ba1 = Math.cos(bezierAngle1);

            bezierAngle2 = angle2 - bezierAngle;
            sin_ba2 = Math.sin(bezierAngle2);
            cos_ba2 = Math.cos(bezierAngle2);

            g2d.setPaint(palette.getColor(i));
            GeneralPath poly = new GeneralPath();

            // first point at the outer curve
            cooX[0] = cos_a1 * r_Max + r_Max;
            cooY[0] = sin_a1 * r_Max + r_Max;
            poly.moveTo(cooX[0], cooY[0]);

            // Points to create the outer curve
            // first bezier point
            cooX[1] = cos_ba1 * br_Max + r_Max;
            cooY[1] = sin_ba1 * br_Max + r_Max;

            // second bezier point
            cooX[2] = cos_ba2 * br_Max + r_Max;
            cooY[2] = sin_ba2 * br_Max + r_Max;

            // second point at the outer curve
            cooX[3] = cos_a2 * r_Max + r_Max;
            cooY[3] = sin_a2 * r_Max + r_Max;

            // draw an outer curve
            poly.curveTo(cooX[1], cooY[1], cooX[2], cooY[2], cooX[3], cooY[3]);

            // second point at the inner curve
            cooX[1] = cos_a2 * r_Min + r_Max;
            cooY[1] = sin_a2 * r_Min + r_Max;
            // draw the sector edge
            poly.lineTo(cooX[1], cooY[1]);

            // Points to create the inner curve
            // second bezier point
            cooX[1] = cos_ba2 * br_Min + r_Max;
            cooY[1] = sin_ba2 * br_Min + r_Max;

            // first bezier point
            cooX[2] = cos_ba1 * br_Min + r_Max;
            cooY[2] = sin_ba1 * br_Min + r_Max;

            // first point at the inner curve
            cooX[3] = cos_a1 * r_Min + r_Max;
            cooY[3] = sin_a1 * r_Min + r_Max;

            // draw an inner curve
            poly.curveTo(cooX[1], cooY[1], cooX[2], cooY[2], cooX[3], cooY[3]);
            // draw the sector edge to the first point at the outer curve
            poly.lineTo(cooX[0], cooY[0]);

            poly.closePath();
            g2d.fill(poly);
        }

        // draw Transparency Fan above the Drum: 
        for (int i = 0; i < r_Max * 2; i++) {
            for (int j = 0; j < r_Max * 2; j++) {
                pix = imgFrame.getRGB(i, j);
                oldAlpha = (pix >> 24) & 0xff;

                if (oldAlpha != 0) {
                    double angle = Math.atan2((i - r_Max), (r_Max - j)) + funAngle;
                    if (angle < 0) {
                        angle += TWO_PI;
                    }
                    double fraction = (angle / funSectorAngle) % 1;
                    if (fraction < 0.9) {
                        fraction = fraction / 0.9;
                    } else {
                        fraction = (1 - fraction) / 0.1;
                    }

                    newAlpha = (int) ((fraction * (maxOpacity - minOpacity) + minOpacity) * oldAlpha);
                    pix = (pix & 0xffffff) | ((newAlpha & 0xff) << 24);
                    imgFrame.setRGB(i, j, pix);
                }
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
        while (!doStop) {
            drumAngle += drumAngleIncrement;
            if (drumAngle > PI) {
                drumAngle -= TWO_PI;
            }
            funAngle += funAngleIncrement;
            if (funAngle > PI) {
                funAngle -= TWO_PI;
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
