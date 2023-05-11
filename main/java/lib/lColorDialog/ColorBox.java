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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import static lib.lColorDialog.ColorPanel.current;
import static lib.lColorDialog.LColorDialog.cPanel;

/**
 * This class draws a color box.<br>
 * It has 3 layers: Ground layer with color box, Cursor layer where the "circle"
 * cursor which moved, Mouse layer with a mouse "cross" cursor and mouse
 * listeners.
 *
 * @see ColorLine
 */
public class ColorBox extends JComponent implements ColorListener {

    /**
     * This is an additional layer to draws circle cursor that indicates
     * position of the current color in the Box.
     */
    private final BoxCursorLayer cursorLayer;

    /**
     * This is a buffered image redraws when the current color will change.
     */
    private final BufferedImage buffer = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);

    /**
     * <b>curX</b> and <b>curY</b> are coordinates of the circle cursor that
     * indicates position of the current color in the Box.
     */
    private static int curX = 0, curY = 0; // current position of the "circle" cursor

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
        this.add(cursorLayer);
    }

    /**
     * Overrided routine for repaint this conponent. It just draws the buffered
     * image, made before.
     */
    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(buffer, 10, 10, null);
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
        drawBox();
        updateCursorPos();
    }

    /**
     * This sets a color from the current cursor position and then broadcast it.
     * to all other components.
     *
     * @see ColorChanger
     */
    private void setCurrentColor() {
        int r, g, b1;
        float h, s, b;

        switch (cPanel.getDialogMode()) {
            case 0: // Hue
                s = curX / 255.0f;
                b = 1.0f - (curY / 255.0f);
                if (cPanel.getColorScheme() == 0) {
                    h = current.getHSBhue();
                    current.setHSB(this, h, s, b);
                } else {
                    h = current.getHSLhue();
                    current.setHSL(this, h, s, b);
                }
                break;
            case 1: // Saturation
                h = curX / 255.0f;
                b = 1.0f - (curY / 255.0f);
                if (cPanel.getColorScheme() == 0) {
                    s = current.getHSBsat();
                    current.setHSB(this, h, s, b);
                } else {
                    s = current.getHSLsat();
                    current.setHSL(this, h, s, b);
                }
                break;
            case 2: // Brightness
                h = curX / 255.0f;
                s = 1.0f - (curY / 255.0f);
                if (cPanel.getColorScheme() == 0) {
                    b = current.getHSBbri();
                    current.setHSB(this, h, s, b);
                } else {
                    b = current.getHSLlight();
                    current.setHSL(this, h, s, b);
                }
                break;
            case 3: // Red
                r = current.getRed();
                b1 = curX;
                g = 255 - curY;
                current.setRGB(this, r, g, b1);
                break;
            case 4: // Green
                g = current.getGreen();
                b1 = curX;
                r = 255 - curY;
                current.setRGB(this, r, g, b1);
                break;
            case 5: // Blue
                b1 = current.getBlue();
                r = curX;
                g = 255 - curY;
                current.setRGB(this, r, g, b1);
        }
    }

    /**
     * This calculates position of the circle cursor and draw it.
     */
    public void updateCursorPos() {
        switch (cPanel.getDialogMode()) {
            case 0: // Hue
                if (cPanel.getColorScheme() == 0) {
                    curX = Math.round(current.getHSBsat() * 255.0f);  //s
                    curY = Math.round((1.0f - current.getHSBbri()) * 255.0f);
                } else {
                    curX = Math.round(current.getHSLsat() * 255.0f);  //s
                    curY = Math.round((1.0f - current.getHSLlight()) * 255.0f);
                }
                break;
            case 1: // Saturation
                if (cPanel.getColorScheme() == 0) {
                    curX = Math.round(current.getHSBhue() * 255.0f);
                    curY = Math.round((1.0f - current.getHSBbri()) * 255.0f);
                } else {
                    curX = Math.round(current.getHSLhue() * 255.0f);
                    curY = Math.round((1.0f - current.getHSLlight()) * 255.0f);
                }
                break;
            case 2: // Brightness
                if (cPanel.getColorScheme() == 0) {
                    curX = Math.round(current.getHSBhue() * 255.0f);
                    curY = Math.round((1.0f - current.getHSBsat()) * 255.0f);
                } else {
                    curX = Math.round(current.getHSLhue() * 255.0f);
                    curY = Math.round((1.0f - current.getHSLsat()) * 255.0f);
                }
                break;
            case 3: // Red
                curX = current.getBlue();
                curY = 255 - current.getGreen();
                break;
            case 4: // Green
                curX = current.getBlue();
                curY = 255 - current.getRed();
                break;
            case 5: // Blue
                curX = current.getRed();
                curY = 255 - current.getGreen();
        }
        cursorLayer.repaint();
    }

    /**
     * The main routine which draws the Box to the buffer and then repaint the
     * component. The drawing routine depends on the dialog mode.
     *
     * @see ColorPanel#getDialogMode
     */
    private void drawBox() {
        float h, s, b;
        int r, g, b1, clr;

        switch (cPanel.getDialogMode()) {
            case 0: // Hue
                if (cPanel.getColorScheme() == 0) {
                    h = current.getHSBhue();
                } else {
                    h = current.getHSLhue();
                }
                for (int x = 0; x < 256; x++) {
                    for (int y = 0; y < 256; y++) {
                        s = x / 255.0f;
                        b = 1.0f - y / 255.0f;
                        if (cPanel.getColorScheme() == 0) {
                            clr = ColorChanger.HSBtoColor(h, s, b);
                        } else {
                            clr = ColorChanger.HSLtoColor(h, s, b);
                        }
                        buffer.setRGB(x, y, clr);
                    }
                }
                break;

            case 1: // Saturation
                if (cPanel.getColorScheme() == 0) {
                    s = current.getHSBsat();
                } else {
                    s = current.getHSLsat();
                }
                for (int x = 0; x < 256; x++) {
                    for (int y = 0; y < 256; y++) {
                        h = x / 255.0f;
                        b = 1.0f - y / 255.0f;
                        if (cPanel.getColorScheme() == 0) {
                            clr = ColorChanger.HSBtoColor(h, s, b);
                        } else {
                            clr = ColorChanger.HSLtoColor(h, s, b);
                        }
                        buffer.setRGB(x, y, clr);
                    }
                }
                break;

            case 2: // Brightness
                if (cPanel.getColorScheme() == 0) {
                    b = current.getHSBbri();
                } else {
                    b = current.getHSLlight();
                }
                for (int x = 0; x < 256; x++) {
                    for (int y = 0; y < 256; y++) {
                        h = x / 255.0f;
                        s = 1.0f - y / 255.0f;
                        if (cPanel.getColorScheme() == 0) {
                            clr = ColorChanger.HSBtoColor(h, s, b);
                        } else {
                            clr = ColorChanger.HSLtoColor(h, s, b);
                        }
                        buffer.setRGB(x, y, clr);
                    }
                }
                break;

            case 3: // Red
                r = current.getRed();
                for (int x = 0; x < 256; x++) {
                    for (int y = 0; y < 256; y++) {
                        b1 = x;
                        g = 255 - y;
                        buffer.setRGB(x, y,
                                0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b1 & 0xff));
                    }
                }
                break;

            case 4: // Green
                g = current.getGreen();
                for (int x = 0; x < 256; x++) {
                    for (int y = 0; y < 256; y++) {
                        b1 = x;
                        r = 255 - y;
                        buffer.setRGB(x, y,
                                0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b1 & 0xff));
                    }
                }
                break;

            case 5: // Blue
                b1 = current.getBlue();
                for (int x = 0; x < 256; x++) {
                    for (int y = 0; y < 256; y++) {
                        g = 255 - y;
                        r = x;
                        buffer.setRGB(x, y,
                                0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b1 & 0xff));
                    }
                }
        } // switch dialogMode
        repaint();
    }

    /**
     * Graphics layer of the "circle" cursor.
     */
    private static class BoxCursorLayer extends JComponent {

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
