/*
 * Copyright (c) 2022 legoru / goroleo <legoru@me.com>
 * 
 * This software is distributed under the <b>MIT License.</b>
 * The full text of the License you can read here: 
 * https://choosealicense.com/licenses/mit/
 * 
 * Use this as you want! ))
 */

package lib.lColorDialog;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import static lib.lColorDialog.ColorPanel.currentColor;

/**
 *
 * This class draws a vertical color line.<br>
 * It has 3 layers: Ground layer with color box; Cursor layer where the "circle"
 * cursor which indicates the current color; Mouse layer with a mouse "cross"
 * cursor and mouse listeners.
 *
 * @see ColorBox
 *
 */
public class ColorLine extends JComponent implements ColorListener {

    /**
     * This is an additional layer to draws circle cursor that indicates
     * position of the current color.
     */
    private final LineCursorLayer cursorLayer; // for access this layer from another classes

    /**
     * This is a buffered image redraws when the current color will change.
     */
    private final BufferedImage LineImg = new BufferedImage(W, 256, BufferedImage.TYPE_INT_ARGB);

    /**
     * <b>curX</b> and <b>curY</b> are coordinates of the circle cursor that
     * indicates position of the current color in the Box. <br>
     * <b>curX = 9 always!</b>
     */
    @SuppressWarnings({"CanBeFinal"})
    private static int curX = 9, curY = 0; // circle cursor position     X=9 always!

    /**
     * The width of the color line
     */
    private static final int W = 19; // width of color line

    /**
     * <b>dialogMode</b> is an integer value from 0 to 5 that means:
     * <b>Hue</b>(0), <b>Saturation</b>(1), <b>Brightness</b>(2), <b>Red</b>(3),
     * <b>Green</b>(4), <b>Blue</b>(5).<br><br>
     */
    private int dialogMode = 0; // 0-5 means Hue, Sat, Bri, R, G, B 

    /**
     * <bcolorScheme</b> is an integer value. 0 means HSB/HSV, 1 means HSL:
     */
    private int colorScheme = 0; // 0-HSV/HSB, 1-HSL

    /**
     * <b>Creates the class which draws ColorLine.</b> <br>
     * It has 256 points height and <b>W</b> width plus 10 extra points for each
     * side (left, right, top, bottom). <br>
     * No params need for this constructor.
     *
     * @see ColorBox
     */
    public ColorLine() {
        setSize(W + 20, 276);
        LineMouseLayer mouseLayer = new LineMouseLayer();
        this.add(mouseLayer);
        cursorLayer = new LineCursorLayer();
        drawLine();
        this.add(cursorLayer);
    }

    /**
     * Override routine for repaint this component. It just draws the buffered
     * image, made before.
     */
    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(LineImg, 10, 10, null);
    }

    /**
     * If the user choose another dialog mode than we need to redraw the
     * buffered image and update the circle cursor position. The mode is changed
     * by RadioButtons in class lnColorLabels.
     *
     * @param aMode is the new dialog mode
     * @see ColorLabels
     */
    public void updateDialogMode(int aMode) {
        if (dialogMode != aMode) {
            dialogMode = aMode;
            drawLine();
            updateCursorPos();
        }
    }

    public void updateColorScheme(int scheme) {
        if (colorScheme != scheme) {
            colorScheme = scheme;
            drawLine();
            updateCursorPos();
        }
    }

    /**
     * This is a listener for an external color change made by any other
     * control.
     *
     * @see ColorChanger
     * @see ColorListener
     */
    @Override
    public void updateColor() {
        drawLine();
        updateCursorPos();
    }

    /**
     * This sets a color from the current cursor position and then broadcast it
     * to all other components.
     *
     * @see ColorChanger
     */
    private void setCurrentColor() {
        switch (dialogMode) {
            case 0: // Hue
                float h = 1.0f - (curY / 255.0f);
                if (colorScheme == 0) {
                    currentColor.setHSBhue(this, h);
                } else {
                    currentColor.setHSLhue(this, h);
                }
                break;
            case 1: // Saturation
                float s = 1.0f - (curY / 255.0f);
                if (colorScheme == 0) {
                    currentColor.setHSBsat(this, s);
                } else {
                    currentColor.setHSLsat(this, s);
                }
                break;
            case 2: // Brightness
                float b = 1.0f - (curY / 255.0f);
                if (colorScheme == 0) {
                    currentColor.setHSBbri(this, b);
                } else {
                    currentColor.setHSLlight(this, b);
                }
                break;
            case 3: // Red
                int r = 255 - curY;
                currentColor.setRed(this, r);
                break;
            case 4: // Green
                int g = 255 - curY;
                currentColor.setGreen(this, g);
                break;
            case 5: // Blue
                int b1 = 255 - curY;
                currentColor.setBlue(this, b1);
        }
    }

    /**
     * This calculates position of the circle cursor and draw it. <br>
     * Note that <b>curX</b> value is never change in this control, it equals 9
     * always!
     */
    public void updateCursorPos() {
        switch (dialogMode) {
            case 0: // Hue
                if (colorScheme == 0) {
                    curY = Math.round((1.0f - currentColor.getHSBhue()) * 255.0f);
                } else {
                    curY = Math.round((1.0f - currentColor.getHSLhue()) * 255.0f);
                }
                break;
            case 1: // Saturation
                if (colorScheme == 0) {
                    curY = Math.round((1.0f - currentColor.getHSBsat()) * 255.0f);
                } else {
                    curY = Math.round((1.0f - currentColor.getHSLsat()) * 255.0f);
                }
                break;
            case 2: // Brightness
                if (colorScheme == 0) {
                    curY = Math.round((1.0f - currentColor.getHSBbri()) * 255.0f);
                } else {
                    curY = Math.round((1.0f - currentColor.getHSLlight()) * 255.0f);
                }
                break;
            case 3: // Red
                curY = 255 - currentColor.getRed();
                break;
            case 4: // Green
                curY = 255 - currentColor.getGreen();
                break;
            case 5: // Blue
                curY = 255 - currentColor.getBlue();
        }
        cursorLayer.repaint();
    }

    /**
     * The main routine which draws the Line to the buffer and then repaint the
     * component. The drawing routine depends on the dialog mode.
     *
     * @see #dialogMode
     */
    private void drawLine() {
        float h, s, b;
        int r, g, b1;

        switch (dialogMode) {

            case 0: // Hue
                if (colorScheme == 0)
                {
                    s = 1.0f;                             // This is the Photoshop's style -  
                    b = 1.0f;                             // HUE line is never change due to current color.

//                  But I personally prefer to change it this way:
//                  s = currentColor.getHSBsat();         
//                  b = currentColor.getHSBbri();     

                    for (int y = 0; y < 256; y++) {
                        h = 1.0f - y / 255.0f;
                        for (int x = 0; x < W; x++) {
                            LineImg.setRGB(x, y, ColorChanger.HSBtoColor(h, s, b));
                        }
                    }
                } else {
                    s = 1.0f;                             // This is the Photoshop's style -  
                    b = 0.5f;                             // HUE line is never change due to current color.

//                  But I personally prefer to change it this way:
//                  s = currentColor.getHSLsat();         
//                  b = currentColor.getHSLlight();       

                    for (int y = 0; y < 256; y++) {
                        h = 1.0f - y / 255.0f;
                        for (int x = 0; x < W; x++) {
                            LineImg.setRGB(x, y, ColorChanger.HSLtoColor(h, s, b));
                        }
                    }
                }
                break;

            case 1: // Saturation
                if (colorScheme == 0) {
                    h = currentColor.getHSBhue();
                    b = currentColor.getHSBbri();
                    for (int y = 0; y < 256; y++) {
                        s = 1.0f - y / 255.0f;
                        for (int x = 0; x < W; x++) {
                            LineImg.setRGB(x, y, ColorChanger.HSBtoColor(h, s, b));
                        }
                    }
                } else {
                    h = currentColor.getHSLhue();
                    b = currentColor.getHSLlight();
                    for (int y = 0; y < 256; y++) {
                        s = 1.0f - y / 255.0f;
                        for (int x = 0; x < W; x++) {
                            LineImg.setRGB(x, y, ColorChanger.HSLtoColor(h, s, b));
                        }
                    }
                }
                break;

            case 2: // Brightness
                if (colorScheme == 0) {
                    h = currentColor.getHSBhue();
                    s = currentColor.getHSBsat();
                    for (int y = 0; y < 256; y++) {
                        b = 1.0f - y / 255.0f;
                        for (int x = 0; x < W; x++) {
                            LineImg.setRGB(x, y, ColorChanger.HSBtoColor(h, s, b));
                        }
                    }
                } else {
                    h = currentColor.getHSLhue();
                    s = currentColor.getHSLsat();
                    for (int y = 0; y < 256; y++) {
                        b = 1.0f - y / 255.0f;
                        for (int x = 0; x < W; x++) {
                            LineImg.setRGB(x, y, ColorChanger.HSLtoColor(h, s, b));
                        }
                    }
                }
                break;

            case 3: // Red
                g = currentColor.getGreen();
                b1 = currentColor.getBlue();
                for (int y = 0; y < 256; y++) {
                    r = 255 - y;
                    int cval = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b1 & 0xff);
                    for (int x = 0; x < W; x++) {
                        LineImg.setRGB(x, y, cval);
                    }
                }
                break;

            case 4: // Green
                r = currentColor.getRed();
                b1 = currentColor.getBlue();
                for (int y = 0; y < 256; y++) {
                    g = 255 - y;
                    int cval = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b1 & 0xff);
                    for (int x = 0; x < W; x++) {
                        LineImg.setRGB(x, y, cval);
                    }
                }
                break;

            case 5: // Blue
                r = currentColor.getRed();
                g = currentColor.getGreen();
                for (int y = 0; y < 256; y++) {
                    b1 = 255 - y;
                    int cval = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b1 & 0xff);
                    for (int x = 0; x < W; x++) {
                        LineImg.setRGB(x, y, cval);
                    }
                }
        }
        repaint();
    }

    /**
     * Graphics layer of the "circle" cursor.
     */
    private static class LineCursorLayer extends JComponent {

        public LineCursorLayer() {
            setSize(W + 20, 276);
        }

        // Draw circle cursor at current cursor position
        @Override
        public void paintComponent(Graphics g) {
            g.drawImage(ColorPanel.cursorCircle, curX, curY, null);
        }

    } // class LineCursorLayer

    /**
     * This is a layer of the mouse "cross" cursor and mouse listeners.
     */
    private class LineMouseLayer extends JComponent {

        public LineMouseLayer() {
            setBounds(10, 10, W, 256);
            this.setCursor(ColorPanel.cursorCross);

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    curY = e.getY();
                    if (curY < 0) {
                        curY = 0;
                    } else if (curY > 255) {
                        curY = 255;
                    }
                    cursorLayer.repaint();
                    setCurrentColor();
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    requestFocusInWindow();              // Grab active focus from any text fields.
                    curY = e.getY();
                    if (curY < 0) {
                        curY = 0;
                    } else if (curY > 255) {
                        curY = 255;
                    }
                    cursorLayer.repaint();
                    // We have to wait until textfields will give us their focus,
                    // and then we'll update all other components
                    EventQueue.invokeLater(ColorLine.this::setCurrentColor);
                }
            });

        } // constructor LineMouseLayer

    } // class LineMouseLayer

} // class ColorLine
