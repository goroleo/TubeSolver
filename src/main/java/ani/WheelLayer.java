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
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import static gui.MainFrame.pal;

public class WheelLayer extends JComponent implements Runnable {

    // radian constants
    final double PI = Math.PI;
    final double TWO_PI = 2 * PI;

    // Radius of the tor. r_Min is an inner radius, r_Max is an outer radius.
    private final int r_Min = 50;
    private final int r_Max = 70;

    // buffer to render frames 
    private final BufferedImage imgFrame = new BufferedImage(r_Max * 2, r_Max * 2, BufferedImage.TYPE_INT_ARGB);
    private final Graphics2D g2d = (Graphics2D) imgFrame.getGraphics();

    // -------------------------------------------------------------------
    //                 Values to render drum (tor) of colors. 
    //
    // The drum (tor) consists of color sectors. Each sector has its own color 
    // corresponding to the color from the palette. The number of drum sectors 
    // matches the number of colors in the palette. 
    // Sectors are separated by gaps.    
    // All values here are in relative terms. 
    // For example each sector size is 1.00. Spacing size is 0.08 of the sector size.
    // see WheelLayer.png
    // 
    // spacing (gap) between sectors 
    private final double spacing = 0.08;

    // angle size of sector, in radians 
    private final double colorsSectorAngle
            = TWO_PI / (pal.size() - 1) / (1 + spacing);

    // angle size of spacing (gap), in radians 
    private final double colorsSpaceAngle
            = colorsSectorAngle * spacing;

    // Bezier angle to create a curve 
    private final double bezierAngle = Math.atan(Math.tan(colorsSectorAngle / 4) * 4 / 3);

    // inner and outer radius for Bezier points
    private final double br_Max = r_Max / Math.cos(bezierAngle);
    private final double br_Min = r_Min / Math.cos(bezierAngle);

    // arrays of sector coordinates 
    private final double[] cooX = new double[4];
    private final double[] cooY = new double[4];

    // -------------------------------------------------------------------
    //                Values to render a transparency fan. 
    //
    // Alpha is an Alpha channel of pixels
    // 
    // number of fan blades (sectors) 
    private final int funSectorsNumber = 3;
    
    // angle size of blade, in radians 
    private final double funSectorAngle = TWO_PI / funSectorsNumber;  // radians

    // maximum of alpha (from 0 to 1)
    private final double maxOpacity = 1.0;
    // minimum of alpha (from 0 to 1)
    private final double minOpacity = 0.4;


    // -------------------------------------------------------------------
    //                        Rotation values
    // 
    // delay before start creating a new frame, in milliseconds 
    private final long delay = 30;                    

    // An angle speed of Drum Of Colors, in radians
    private final double drumAngleIncrement = PI * 0.02;         

    // An angle speed of Transparency Fan, in radians
    private final double funAngleIncrement = PI * 0.03;          
    
    // Current Drum angle 
    private double drumAngle;                               
    
    // Current Fan angle 
    private double funAngle;          
    
    // This uses for external interrupt
    private boolean doStop = false;

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

    public void start() {
        Thread t = new Thread(this);
        t.start();
    }

    public void stop() {
        doStop = true;
    }

    private void drawFrame() {

        // variables 
        double angle1, angle2; // sector angles
        double bezierAngle1, bezierAngle2; // bezier angles
        double sin_a1, sin_a2, sin_ba1, sin_ba2; // sinuses
        double cos_a1, cos_a2, cos_ba1, cos_ba2; // co-sinuses

        int pix; // pixel
        int oldAlpha, newAlpha; // alpha-channel values

        // clear image frame
        g2d.clearRect(0, 0, r_Max * 2, r_Max * 2);

        // draw DRUM or TOR: 
        for (int i = 0; i < pal.size() - 1; i++) {

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

            g2d.setPaint(pal.getColor(i + 1));
            GeneralPath poly = new GeneralPath();

            cooX[0] = (cos_a1 * r_Max + r_Max);
            cooY[0] = (sin_a1 * r_Max + r_Max);
            poly.moveTo(cooX[0], cooY[0]);

            cooX[1] = (cos_ba1 * br_Max + r_Max);
            cooY[1] = (sin_ba1 * br_Max + r_Max);

            cooX[2] = (cos_ba2 * br_Max + r_Max);
            cooY[2] = (sin_ba2 * br_Max + r_Max);

            cooX[3] = (cos_a2 * r_Max + r_Max);
            cooY[3] = (sin_a2 * r_Max + r_Max);
            poly.curveTo(cooX[1], cooY[1], cooX[2], cooY[2], cooX[3], cooY[3]);

            cooX[1] = (cos_a2 * r_Min + r_Max);
            cooY[1] = (sin_a2 * r_Min + r_Max);
            poly.lineTo(cooX[1], cooY[1]);

            cooX[1] = (cos_ba2 * br_Min + r_Max);
            cooY[1] = (sin_ba2 * br_Min + r_Max);

            cooX[2] = (cos_ba1 * br_Min + r_Max);
            cooY[2] = (sin_ba1 * br_Min + r_Max);

            cooX[3] = (cos_a1 * r_Min + r_Max);
            cooY[3] = (sin_a1 * r_Min + r_Max);
            poly.curveTo(cooX[1], cooY[1], cooX[2], cooY[2], cooX[3], cooY[3]);
            poly.lineTo(cooX[0], cooY[0]);
            poly.closePath();
            g2d.fill(poly);
        }

        // draw Transparency Fan above the Drum: 
        for (int i = 0; i < r_Max * 2; i++) {
            double x = i - r_Max;
            for (int j = 0; j < r_Max * 2; j++) {
                double y = r_Max - j;
                pix = imgFrame.getRGB(i, j);
                oldAlpha = (pix >> 24) & 0xff;

                if (oldAlpha != 0) {
                    double angle = Math.atan2(y, x) - funAngle;
                    if (angle < 0) {
                        angle += TWO_PI;
                    }
                    double range = (angle / funSectorAngle) % 1;
                    if (range < 0.9) {
                        range = range / 0.9;
                    } else {
                        range = (1 - range) / 0.1;
                    }

                    newAlpha = (int) ((range * (maxOpacity - minOpacity) + minOpacity) * oldAlpha);
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

            drawFrame();
            repaint();

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // do nothing
            }

        }
    }

}
