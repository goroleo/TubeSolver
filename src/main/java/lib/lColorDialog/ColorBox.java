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
 * This class draws a color box.<br>
 * It has 3 layers: Ground layer with color box, Cursor layer where the "circle"
 * cursor which moved, Mouse layer with a mouse "cross" cursor and mouse
 * listeners.
 *
 * @see ColorLine
 *
 */
public class ColorBox extends JComponent implements ColorListener {

    /**
     * This is an additional layer to draws circle cursor that indicates
     * position of the current color in the Box.
     */
    private final BoxCursorLayer cursorLayer;

    /**
     * This is an buffered image redrawed when the current color will change.
     */
    private final BufferedImage boxImg = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);

    /**
     * <b>curX</b> and <b>curY</b> are coordinates of the circle cursor that
     * indicates position of the current color in the Box.
     */
    private static int curX = 0, curY = 0; // current position of the "circle" cursor

    /**
     * <b>dialogMode</b> is an integer value from 0 to 5 that means:
     * <b>Hue</b>(0), <b>Saturation</b>(1), <b>Brightness</b>(2), <b>Red</b>(3),
     * <b>Green</b>(4), <b>Blue</b>(5).<br><br>
     */
    private int dialogMode = 0; // 0-5 means Hue, Sat, Bri, R, G, B

    /**
     * <bcolorScheme</b> is an integer value. 0 means HSB/HSV, 1 means HSL:
     */
    private int colorScheme = 0; // 0-HSV/HSV, 1-HSL

    /**
     * <b>Creates the class which draws ColorBox.</b> <br>
     * It has 256x256 poins plus 10 extra points for each side (left, right,
     * top, bottom). These extra points are used to draw a circle cursor that
     * indicates the current color position.<br>
     * No params need for this constructor.
     *
     * @see ColorLine
     */
    public ColorBox() {
        setSize(276, 276);
        BoxMouseLayer mouseLayer = new BoxMouseLayer();
        this.add(mouseLayer);
        cursorLayer = new BoxCursorLayer();
        drawBox();
        this.add(cursorLayer);
    }

    /**
     * Overrided routine for repaint this conponent. It just draws the buffered
     * image, made before.
     */
    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(boxImg, 10, 10, null);
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
            drawBox();
            updateCursorPos();
        }
    }

    public void updateColorScheme(int scheme) {
        if (colorScheme != scheme) {
            colorScheme = scheme;
            drawBox();
            updateCursorPos();
        }
    }

    /**
     * This is a listener for an external color change made by any other
     * control.
     *
     * @param rgb is the color value that specifies color components
     * @see ColorChanger
     * @see ColorListener
     */
    @Override
    public void updateColor(int rgb) {
        drawBox();
        updateCursorPos();
    }

    /**
     * This sets a color from the current cursor position and than broadcast it
     * to all other components.
     *
     * @see ColorChanger
     */
    private void setCurrentColor() {
        int r, g, b1;
        float h, s, b;

        switch (dialogMode) {
            case 0: // Hue
                s = curX / 255.0f;
                b = 1.0f - (curY / 255.0f);
                if (colorScheme == 0) {
                    h = currentColor.getHSBhue();
                    currentColor.setHSB(this, h, s, b);
                } else {
                    h = currentColor.getHSLhue();
                    currentColor.setHSL(this, h, s, b);
                }
                break;
            case 1: // Saturation
                h = curX / 255.0f;
                b = 1.0f - (curY / 255.0f);
                if (colorScheme == 0) {
                    s = currentColor.getHSBsat();
                    currentColor.setHSB(this, h, s, b);
                } else {
                    s = currentColor.getHSLsat();
                    currentColor.setHSL(this, h, s, b);
                }
                break;
            case 2: // Brightness
                h = curX / 255.0f;
                s = 1.0f - (curY / 255.0f);
                if (colorScheme == 0) {
                    b = currentColor.getHSBbri();
                    currentColor.setHSB(this, h, s, b);
                } else {
                    b = currentColor.getHSLlight();
                    currentColor.setHSL(this, h, s, b);
                }
                break;
            case 3: // Red
                r = currentColor.getRed();
                b1 = curX;
                g = 255 - curY;
                currentColor.setRGB(this, r, g, b1);
                break;
            case 4: // Green
                g = currentColor.getGreen();
                b1 = curX;
                r = 255 - curY;
                currentColor.setRGB(this, r, g, b1);
                break;
            case 5: // Blue
                b1 = currentColor.getBlue();
                r = curX;
                g = 255 - curY;
                currentColor.setRGB(this, r, g, b1);
        }
    }

    /**
     * This calculates position of the circle cursor and draw it.
     */
    public void updateCursorPos() {
        switch (dialogMode) {
            case 0: // Hue
                if (colorScheme == 0) {
                    curX = Math.round(currentColor.getHSBsat() * 255.0f);  //s 
                    curY = Math.round((1.0f - currentColor.getHSBbri()) * 255.0f);
                } else {
                    curX = Math.round(currentColor.getHSLsat() * 255.0f);  //s 
                    curY = Math.round((1.0f - currentColor.getHSLlight()) * 255.0f);
                }
                break;
            case 1: // Saturation
                if (colorScheme == 0) {
                    curX = Math.round(currentColor.getHSBhue() * 255.0f);
                    curY = Math.round((1.0f - currentColor.getHSBbri()) * 255.0f);
                } else {
                    curX = Math.round(currentColor.getHSLhue() * 255.0f);
                    curY = Math.round((1.0f - currentColor.getHSLlight()) * 255.0f);
                }
                break;
            case 2: // Brightness
                if (colorScheme == 0) {
                    curX = Math.round(currentColor.getHSBhue() * 255.0f);
                    curY = Math.round((1.0f - currentColor.getHSBsat()) * 255.0f);
                } else {
                    curX = Math.round(currentColor.getHSLhue() * 255.0f);
                    curY = Math.round((1.0f - currentColor.getHSLsat()) * 255.0f);
                }
                break;
            case 3: // Red
                curX = currentColor.getBlue();
                curY = 255 - currentColor.getGreen();
                break;
            case 4: // Green
                curX = currentColor.getBlue();
                curY = 255 - currentColor.getRed();
                break;
            case 5: // Blue
                curX = currentColor.getRed();
                curY = 255 - currentColor.getGreen();
        }
        cursorLayer.repaint();
    }

    /**
     * The main routine which draws the Box to the buffer and then repaint the
     * component. The drawing routine depends on the dialog mode.
     *
     * @see #dialogMode
     */
    private void drawBox() {
        float h, s, b;
        int r, g, b1, clr;

        switch (dialogMode) {
            case 0: // Hue
                if (colorScheme == 0) {
                    h = currentColor.getHSBhue();
                } else {
                    h = currentColor.getHSLhue();
                }
                for (int x = 0; x < 256; x++) {
                    for (int y = 0; y < 256; y++) {
                        s = x / 255.0f;
                        b = 1.0f - y / 255.0f;
                        if (colorScheme == 0) {
                            clr = ColorChanger.HSBtoColor(h, s, b);
                        } else {
                            clr = ColorChanger.HSLtoColor(h, s, b);
                        }
                        boxImg.setRGB(x, y, clr);
                    }
                }
                break;

            case 1: // Saturation
                if (colorScheme == 0) {
                    s = currentColor.getHSBsat();
                } else {
                    s = currentColor.getHSLsat();
                }
                for (int x = 0; x < 256; x++) {
                    for (int y = 0; y < 256; y++) {
                        h = x / 255.0f;
                        b = 1.0f - y / 255.0f;
                        if (colorScheme == 0) {
                            clr = ColorChanger.HSBtoColor(h, s, b);
                        } else {
                            clr = ColorChanger.HSLtoColor(h, s, b);
                        }
                        boxImg.setRGB(x, y, clr);
                    }
                }
                break;

            case 2: // Brightness
                if (colorScheme == 0) {
                    b = currentColor.getHSBbri();
                } else {
                    b = currentColor.getHSLlight();
                }
                for (int x = 0; x < 256; x++) {
                    for (int y = 0; y < 256; y++) {
                        h = x / 255.0f;
                        s = 1.0f - y / 255.0f;
                        if (colorScheme == 0) {
                            clr = ColorChanger.HSBtoColor(h, s, b);
                        } else {
                            clr = ColorChanger.HSLtoColor(h, s, b);
                        }
                        boxImg.setRGB(x, y, clr);
                    }
                }
                break;

            case 3: // Red
                r = currentColor.getRed();
                for (int x = 0; x < 256; x++) {
                    for (int y = 0; y < 256; y++) {
                        b1 = x;
                        g = 255 - y;
                        boxImg.setRGB(x, y,
                                0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b1 & 0xff));
                    }
                }
                break;

            case 4: // Green
                g = currentColor.getGreen();
                for (int x = 0; x < 256; x++) {
                    for (int y = 0; y < 256; y++) {
                        b1 = x;
                        r = 255 - y;
                        boxImg.setRGB(x, y,
                                0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b1 & 0xff));
                    }
                }
                break;

            case 5: // Blue
                b1 = currentColor.getBlue();
                for (int x = 0; x < 256; x++) {
                    for (int y = 0; y < 256; y++) {
                        g = 255 - y;
                        r = x;
                        boxImg.setRGB(x, y,
                                0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b1 & 0xff));
                    }
                }
        } // switch dialogMode
        repaint();
    }

    /**
     * Graphics layer of the "circle" cursor.
     */
    private class BoxCursorLayer extends JComponent {

        public BoxCursorLayer() {
            setSize(276, 276);
        }

        @Override
        public void paintComponent(Graphics g) {
            g.drawImage(ColorPanel.cursorCircle, curX, curY, null);
        }

    } // class lnBoxCursorLayer

    /**
     * This is a layer of the mouse "cross" cursor and mouse listeners.
     */
    private class BoxMouseLayer extends JComponent {

        public BoxMouseLayer() {
            setBounds(10, 10, 256, 256);
            setCursor(ColorPanel.cursorCross);

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    curX = e.getX();
                    curY = e.getY();
                    if (curX < 0) {
                        curX = 0;
                    } else if (curX > 255) {
                        curX = 255;
                    }
                    if (curY < 0) {
                        curY = 0;
                    } else if (curY > 255) {
                        curY = 255;
                    }
                    cursorLayer.repaint();
                    setCurrentColor();
                }
            }); // MouseMotionListener

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    requestFocusInWindow();               // grab active focus from any text fields

                    curX = e.getX();
                    curY = e.getY();
                    if (curX < 0) {
                        curX = 0;
                    } else if (curX > 255) {
                        curX = 255;
                    }
                    if (curY < 0) {
                        curY = 0;
                    } else if (curY > 255) {
                        curY = 255;
                    }
                    cursorLayer.repaint();

                    // we have to wait until text fields will give us their focus,
                    // and then we'll update and repaint all other components
                    EventQueue.invokeLater(ColorBox.this::setCurrentColor);
                }
            }); // MouseListener

        } // constructor lnBoxMouseLayer

    } // class lnBoxMouseLayer

} // class lnColorBox
