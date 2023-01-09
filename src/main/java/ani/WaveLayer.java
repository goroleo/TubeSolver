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
 * WaveLayer is a layer with some picture through which waves of opacity pass. As the wave passes,
 * the picture is partially "muted" (becomes transparent), and then restored again.
 * The wavefront can pass through the image in different directions: vertical, horizontal or diagonal.
 * I called those directions as <i>axis</i>.<br>
 * The wave consists of four parts:<ul>
 * <li><b>Prow</b> (nose, head). The opacity of the picture increases from minimum to maximum value.<br>
 * <li><b>Body</b>. The opacity is maximum.<br>
 * <li><b>Tail</b>. The opacity of the picture decreases from the maximum to the minimum value.<br>
 * <li><b>Space</b>. The opacity is minimum.</ul>
 * <i>See description of wave shape values at JavaDoc of <b>body</b>.</i>
 *
 * @see #body
 * @see #axis
 * @see #forward
 */
public class WaveLayer extends JComponent implements Runnable {

// -----------------------------------------------------
//     Animation settings
//
    /**
     * Delay (in milliseconds) before start to calculate and draw next frame.
     */
    private final int delay = 35;

    /**
     * Path Increment is the part of the picture which the wave pass in one frame. Can be from 0 to 1.
     */
    private final float pathInc = 0.015f;

    /**
     * Alpha Increment is the alpha-channel value for appearing and disappearing whole the picture.
     * Master alpha-channel value increases (or decreases) whole the picture transparency in every single frame.
     */
    private final float alphaInc = 0.08f;

// -----------------------------------------------------
//     Wave direction settings
//

    /**
     * Axis to run the color wave: <ul>
     * <li><b>0</b> - vertical axis;
     * <li><b>1</b> - horizontal;<br>
     * <li><b>2</b> - diagonal 1 (SW-NE, BottomLeft to TopRight);<br>
     * <li><b>3</b> - diagonal 2 (NW-SE, TopLeft to BottomRight).</ul>
     */
    private int axis = 2;

    /**
     * Depending on the axis, the Forward direction is from left to right preferably. If <b>true</b> the direction
     * is: <ul>
     * <li>from Left to Right,
     * <li>from Top-Left to Bottom-Right,
     * <li>from Bottom-Left To Top-Right,
     * <li>or (in vertical direction) from Top to Bottom.</ul>
     */
    private boolean forward = true; // else backward :)

    /**
     * Direction field is deprecated and not used 'cause we have two previous variables: <br>
     * 0 (Top-to-Bottom): axis = vertical, forward = true<br>
     * 1 (Bottom-to-Top): axis = vertical, forward = false <br>
     * 2 (Left-to-Right): axis = horizontal, forward = true<br>
     * 3 (Right-to-Left): axis = horizontal, forward = false<br>
     * 4 (TopLeft-to-BottomRight): axis = diagonal2, forward = true<br>
     * 5 (TopRight-to-BottomLeft): axis = diagonal1, forward = false<br>
     * 6 (BottomLeft-to-TopRight): axis = diagonal1, forward = true<br>
     * 7 (BottomRight-to-TopLeft): axis = diagonal2, forward = false
     *
     * @see #axis
     * @see #forward
     */
    @Deprecated
    private int direction = 0;   // deprecated

// -----------------------------------------------------
//     Wave shape settings
//

    /**
     * The first part of the wave shape. The opacity of the picture increases from minimum to maximum value.<br>
     * <i>See description of wave shape values at JavaDoc of <b>body</b>.</i>
     *
     * @see #body
     */
    private float prow = 0.15f;
    /**
     * The second part of the wave shape. The opacity is maximum value.<br><br>
     * The wave shape values are given as fractions of the total path length that the wave must pass through
     * the picture. For horizontal direction, the full path is the width of the image. For vertical, this is
     * the height. For a diagonal directions, this is the size of the diagonal. The length of the full path is
     * taken as one. The values of prow, body, tail and space can be any positive. They can be both greater
     * and less than 1. They also can be zero, in which case this part of the wave will not be shown.
     */
    private float body = 2.4f;

    /**
     * The third part of the wave shape. The opacity of the picture decreases from the maximum to the minimum value.<br>
     * <i>See description of wave shape values at JavaDoc of <b>body</b>.</i>
     *
     * @see #body
     */
    private float tail = 0.15f;

    /**
     * The last part of the wave shape. The opacity is minimum.<br>
     * <i>See description of wave shape values at JavaDoc of <b>body</b>.</i>
     *
     * @see #body
     */
    private float space = 0.0f;

// -----------------------------------------------------
//     Opacity settings
//

    /**
     * Minimum opacity value (0 - fully transparent, 1 - fully opacity).
     */
    private final float minOpacity = 0.5f;

    /**
     * Maximum opacity value (0 - fully transparent, 1 - fully opacity).
     */
    private final float maxOpacity = 1.0f;

// -----------------------------------------------------
//     Variables / fields
//

    /**
     * An original image.
     */
    private BufferedImage imgOrig;

    /**
     * The image for the every single frame.
     */
    private BufferedImage imgFrame;

    /**
     * Temporal image to replace the original in the future, when the cycle will stop.
     */
    private BufferedImage imgTemp;

    /**
     * The width of the image.
     */
    private int w;

    /**
     * The height of the image.
     */
    private int h;

    /**
     * The size of the image's diagonal (in pixels).
     */
    private float diagSize;

    /**
     * Size of the wave shape (in pixels).
     */
    private float shapeSize;

    /**
     * prowLimit, bodyLimit, tailLimit - proportional parts of the whole wave shape. Used to determine which
     * part of the shape the current point falls into.
     */
    private float prowLimit, bodyLimit, tailLimit;

    /**
     * If <b>true</b> then the thread is working, and we don't need to start it again.
     */
    private boolean working = false;

    /**
     * Appear or disappear the image
     */
    private boolean disappear = false;

    /**
     * If <b>true</b> then the cycle will work until the external break.
     */
    private Boolean unlimited = true;

    /**
     * The switcher to replace the original image with the temporal, when the cycle will stop.
     */
    private boolean changeWhenHide = false;

// -----------------------------------------------------
//     Cycle variables
//

    /**
     * The current wave position
     */
    private float curPos = 0.0f;

    /**
     * Current master value of the Alpha-channel
     */
    private float masterAlpha = 0.0f;


// -----------------------------------------------------
//     Routines
//

    public WaveLayer(BufferedImage bi) {
        super();
        setImage(bi);
        calculateShapeSize();
    }

    public void setImage(BufferedImage bi) {
        imgOrig = bi;
        if (imgFrame == null || w != bi.getWidth() || h != bi.getHeight()) {
            w = bi.getWidth();
            h = bi.getHeight();
            diagSize = (float) Math.sqrt(w * w + h * h);
            calculateShapeSize();
            imgFrame = new BufferedImage(w, h, 2);
            setBounds(0, 0, w, h);
        }
        updateCurrentFrame();
    }

    /**
     * This routine will set the new image to this layer, not right now, but when the cycle will stop and
     * the previous picture will disappear.
     *
     * @param bi new image
     */
    public void setImageWhenHide(BufferedImage bi) {
        if (!working & masterAlpha == 0) {
            setImage(bi);
            changeWhenHide = false;
        } else {
            imgTemp = bi;
            changeWhenHide = true;
        }
    }

    /**
     * Direction is deprecated and not used 'cause we have <i>axis</i> and <i>forward</i> variables.
     * @param d new direction
     */
    @Deprecated
    public void setDirection(int d) {
        if (d >= 0 && d <= 7) {
            direction = d;
            switch (d) {
                case 0: // (Top-to-Bottom)
                    axis = 0; // vertical
                    forward = true;
                    break;
                case 1: // (Bottom-to-Top)
                    axis = 0; // vertical
                    forward = false;
                    break;
                case 2: // (Left-to-Right)
                    axis = 1; // horizontal
                    forward = true;
                    break;
                case 3: // (Right-to-Left)
                    axis = 1; // horizontal
                    forward = false;
                    break;
                case 4: // (TopLeft-to-BottomRight)
                    axis = 3; // diagonal2
                    forward = true;
                    break;
                case 5: // (TopRight-to-BottomLeft)
                    axis = 2; // diagonal1
                    forward = false;
                    break;
                case 6: // (BottomLeft-to-TopRight)
                    axis = 2; // diagonal1
                    forward = true;
                    break;
                case 7: // (BottomRight-to-TopLeft)
                    axis = 3; // diagonal2
                    forward = false;
                    break;
            }
        }
    }

    /**
     * Set the direction of the wave pass. More information at the description of the <i>axis</i> and <i>forward</i> variables.
     * @param a new axis
     * @param f new forward
     * @see #axis
     * @see #forward
     */
    public void setDirection(int a, boolean f) {
        if (a >= 0 && a <= 3) {
            axis = a;
            forward = f;
        }
    }

    /**
     * Sets the shape of the wave.
     * <i>See description of wave shape values at JavaDoc of <b>body</b>.</i>
     *
     * @param p prow
     * @param b body
     * @param t tail
     * @param s space
     * @see #body
     */
    public void setShape(float p, float b, float t, float s) {
        prow = p;
        body = b;
        tail = t;
        space = s;
        calculateShapeSize();
    }

    /**
     * This routine calculates the size of the wave shape and sets the limits of the wave's parts for further calculations.
     *
     * @see #shapeSize
     * @see #bodyLimit
     */
    private void calculateShapeSize() {
        shapeSize = (prow + body + tail + space);

        prowLimit = prow / shapeSize;
        bodyLimit = body / shapeSize + prowLimit;
        tailLimit = tail / shapeSize + bodyLimit;

        switch (axis) {
            case 0: // vertical
                shapeSize = shapeSize * h;
                break;
            case 1: // horizontal
                shapeSize = shapeSize * w;
                break;
            case 2: // diagonal 1 (SW-NE, BottomLeft to TopRight)
            case 3: // diagonal 2 (NW-SE, TopLeft to BottomRight)
                shapeSize = shapeSize * diagSize;
        }
    }

    public void start() {
        if (!working) {
            working = true;
            Thread t = new Thread(this);
            t.start();
        } else {
            disappear = false;
        }
    }

    public void stop() {
        if (working) {
            disappear = true;
        }
    }

    public void startUnlimited() {
        curPos = -1.0f / (prow + body + tail + space);
        unlimited = true;
        disappear = false;
        start();
    }

    /**
     *
     */
    public void startShow() {
        curPos = -1.0f / (prow + body + tail + space);
        unlimited = false;
        disappear = false;
        start();
    }

    public void startHide() {
        curPos = tailLimit - 1.0f / (prow + body + tail + space);
        disappear = true;
        unlimited = false;
        start();
    }

    /**
     * This routine calculates the
     *
     * @param x the X coordinate of the image's pixel
     * @param y the Y coordinate of the image's pixel
     * @return
     */
    private float calculateAlpha(int x, int y) {
        float result;

        // Coordinate of the current point, depending on the wave propagation axis and its direction.
        float coordinate;

        // The part of the wave on which the current coordinate is located: prow, body, tail, space.
        float fraction;

        switch (axis) {
            case 0: // vertical
                coordinate = (forward) ? h - y : y;
                break;
            case 1: // horizontal
                coordinate = (forward) ? w - x : x;
                break;
            case 2: // diagonal 1 (SW-NE, BottomLeft - TopRight)
                if (forward) {
                    x = w - x;
                } else {
                    y = h - y;
                }
                coordinate = (x * w + y * h) / diagSize;
                break;
            default: // diagonal 2 (NW-SE, TopLeft - BottomRight)
                if (forward) {
                    x = w - x;
                    y = h - y;
                }
                coordinate = (x * w + y * h) / diagSize;
        }

        if (unlimited) {
            fraction = (((coordinate - shapeSize) + (curPos * shapeSize)) / shapeSize) % 1;
            if (fraction < 0) {
                fraction = fraction + 1;
            }
            if (fraction < prowLimit && prow > 0) {
                result = minOpacity + (maxOpacity - minOpacity) * fraction / prowLimit;
            } else if (fraction < bodyLimit && body > 0) {
                result = maxOpacity;
            } else if (fraction < tailLimit && tail > 0) {
                result = minOpacity + (maxOpacity - minOpacity) * (tailLimit - fraction) / (tailLimit - bodyLimit);
            } else if (space > 0) {
                result = minOpacity;
            } else {
                result = 0;
            }
            result = result * masterAlpha;
        } else {
            fraction = ((coordinate + (curPos * shapeSize)) / shapeSize);
            if (disappear) {
                if (fraction < bodyLimit) {
                    result = maxOpacity;
                } else if (fraction < tailLimit) {
                    result = (tailLimit - fraction) / (tailLimit - bodyLimit);
                } else {
                    result = 0;
                }
            } else {
                if (fraction < 0) {
                    result = 0;
                } else if (fraction < prowLimit) {
                    result = fraction / prowLimit;
                } else {
                    result = maxOpacity;
                }
            }
        }
        return result;
    }

    /**
     * Draws the every single frame.
     */
    private void updateCurrentFrame() {

        // A pixel from the original image
        int pix;

        // Value of the alpha channel calculated from the current pixel coordinates
        float alphaValue;

        // Value of the alpha channel that will be set to the pixel
        int pixAlpha;


        switch (axis) {

            case 0: // vertical
                for (int y = 0; y < h; y++) {
                    alphaValue = calculateAlpha(0, y);
                    for (int x = 0; x < w; x++) {
                        pix = imgOrig.getRGB(x, y);
                        pixAlpha = Math.round(((pix >> 24) & 0xff) * alphaValue);
                        pix = (pix & 0xffffff) | ((pixAlpha & 0xff) << 24);
                        imgFrame.setRGB(x, y, pix);
                    }
                }
                break;

            case 1: // horizontal
                for (int x = 0; x < w; x++) {
                    alphaValue = calculateAlpha(x, 0);
                    for (int y = 0; y < h; y++) {
                        pix = imgOrig.getRGB(x, y);
                        pixAlpha = Math.round(((pix >> 24) & 0xff) * alphaValue);
                        pix = (pix & 0xffffff) | ((pixAlpha & 0xff) << 24);
                        imgFrame.setRGB(x, y, pix);
                    }
                }
                break;

            default: // diagonal 1 & diagonal 2
                for (int x = 0; x < w; x++) {
                    for (int y = 0; y < h; y++) {
                        pix = imgOrig.getRGB(x, y);
                        pixAlpha = Math.round(((pix >> 24) & 0xff) * calculateAlpha(x, y));
                        pix = (pix & 0xffffff) | ((pixAlpha & 0xff) << 24);
                        imgFrame.setRGB(x, y, pix);
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
        boolean stop = false;

        while (!stop) {

            if (unlimited) {
                if (!disappear) {
                    if (masterAlpha < 1) {
                        masterAlpha = masterAlpha + alphaInc;
                        if (masterAlpha > 1) {
                            masterAlpha = 1.0f;
                        }
                    }
                } else {
                    if (masterAlpha > 0) {
                        masterAlpha = masterAlpha - alphaInc;
                        if (masterAlpha < 0) {
                            masterAlpha = 0.0f;
                        }
                    }
                    stop = masterAlpha == 0;
                }

            } else { // unlimited is false
                if (!disappear) {
                    stop = curPos >= prowLimit;
                    if (stop) {
                        masterAlpha = 1.0f;
                        unlimited = true;
                    }
                } else {
                    stop = curPos >= bodyLimit;
                    if (stop) {
                        masterAlpha = 0.0f;
                        unlimited = true;
                    }
                }
            }

            curPos = curPos + pathInc;
            if (curPos > 1) {
                curPos = curPos - 1.0f;
            }

            updateCurrentFrame();
            repaint();

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // do nothing
            }

            if (unlimited && stop && !disappear) {
                stop = false;
            }
        }
        working = false; // thread is done 

        if (changeWhenHide && masterAlpha == 0) {
            setImage(imgTemp);
            changeWhenHide = false;
        }
        onThreadFinished();
    }

    /**
     * Override this routine if you want to get to know when the cycle has done.
     */
    public void onThreadFinished() {
        // routine to override 
    }
}
