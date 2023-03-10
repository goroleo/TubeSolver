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
 * This class draws a vertical color line.<br>
 * It has 3 layers: Ground layer with color box; Cursor layer where the "circle"
 * cursor which indicates the current color; Mouse layer with a mouse "cross"
 * cursor and mouse listeners.
 *
 * @see ColorBox
 */
public class ColorLine extends JComponent implements ColorListener {

    /**
     * This is an additional layer to draws circle cursor that indicates
     * position of the current color.
     */
    private final CursorLayer cursorLayer; // for access this layer from another classes

    /**
     * This is a buffered image redraws when the current color will change.
     */
    private final BufferedImage buffer = new BufferedImage(W, 256, BufferedImage.TYPE_INT_ARGB);

    /**
     * <b>curY</b> is the Y coordinate of the circle cursor that
     * indicates position of the current color in the Line. <br>
     * <b>curX = 9 always!</b>
     */
    private int curY = 0; // circle cursor Y position     X=9 always!

    /**
     * The width of the color line
     */
    private static final int W = 19; // width of color line

    /**
     * Creates the class which draws ColorLine. <br>
     * It has 256 points height and <b>W</b> width plus 10 extra points for each
     * side (left, right, top, bottom). <br>
     *
     * @see ColorBox
     */
    public ColorLine() {
        setSize(W + 20, 276);
        MouseLayer mouseLayer = new MouseLayer();
        this.add(mouseLayer);
        cursorLayer = new CursorLayer();
        this.add(cursorLayer);
    }

    /**
     * Override routine for repaint this component. It just draws the buffered
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
        switch (cPanel.getDialogMode()) {
            case 0: // Hue
                float h = 1.0f - (curY / 255.0f);
                if (cPanel.getColorScheme() == 0) {
                    current.setHSBhue(this, h);
                } else {
                    current.setHSLhue(this, h);
                }
                break;
            case 1: // Saturation
                float s = 1.0f - (curY / 255.0f);
                if (cPanel.getColorScheme() == 0) {
                    current.setHSBsat(this, s);
                } else {
                    current.setHSLsat(this, s);
                }
                break;
            case 2: // Brightness
                float b = 1.0f - (curY / 255.0f);
                if (cPanel.getColorScheme() == 0) {
                    current.setHSBbri(this, b);
                } else {
                    current.setHSLlight(this, b);
                }
                break;
            case 3: // Red
                int r = 255 - curY;
                current.setRed(this, r);
                break;
            case 4: // Green
                int g = 255 - curY;
                current.setGreen(this, g);
                break;
            case 5: // Blue
                int b1 = 255 - curY;
                current.setBlue(this, b1);
        }
    }

    /**
     * This calculates position of the circle cursor and draw it. <br>
     * Note that <b>curX</b> value is never change in this control, it equals 9
     * always!
     */
    public void updateCursorPos() {
        switch (cPanel.getDialogMode()) {
            case 0: // Hue
                if (cPanel.getColorScheme() == 0) {
                    curY = Math.round((1.0f - current.getHSBhue()) * 255.0f);
                } else {
                    curY = Math.round((1.0f - current.getHSLhue()) * 255.0f);
                }
                break;
            case 1: // Saturation
                if (cPanel.getColorScheme() == 0) {
                    curY = Math.round((1.0f - current.getHSBsat()) * 255.0f);
                } else {
                    curY = Math.round((1.0f - current.getHSLsat()) * 255.0f);
                }
                break;
            case 2: // Brightness
                if (cPanel.getColorScheme() == 0) {
                    curY = Math.round((1.0f - current.getHSBbri()) * 255.0f);
                } else {
                    curY = Math.round((1.0f - current.getHSLlight()) * 255.0f);
                }
                break;
            case 3: // Red
                curY = 255 - current.getRed();
                break;
            case 4: // Green
                curY = 255 - current.getGreen();
                break;
            case 5: // Blue
                curY = 255 - current.getBlue();
        }
        cursorLayer.repaint();
    }

    /**
     * The main routine which draws the Line to the buffer and then repaint the
     * component. The drawing routine depends on the dialog mode.
     *
     * @see ColorPanel#getDialogMode
     */
    private void drawLine() {
        float h, s, b;
        int r, g, b1;
        int clr;

        switch (cPanel.getDialogMode()) {

            case 0: // Hue
                if (cPanel.getColorScheme() == 0) {
                    s = 1.0f;                             // This is the Photoshop's style -  
                    b = 1.0f;                             // HUE line is never change due to current color.

                    // But I personally prefer to change it this way:
                    // (saturation) s = currentColor.getHSBsat();
                    // (brightness) b = currentColor.getHSBbri();

                    for (int y = 0; y < 256; y++) {
                        h = 1.0f - y / 255.0f;
                        clr = ColorChanger.HSBtoColor(h, s, b);
                        for (int x = 0; x < W; x++) {
                            buffer.setRGB(x, y, clr);
                        }
                    }
                } else {
                    s = 1.0f;                             // This is the Photoshop's style -  
                    b = 0.5f;                             // HUE line is never change due to current color.

                    // But I personally prefer to change it this way:
                    // (saturation) s = currentColor.getHSBsat();
                    // (lightness) b = currentColor.getHSLlight();

                    for (int y = 0; y < 256; y++) {
                        h = 1.0f - y / 255.0f;
                        clr = ColorChanger.HSLtoColor(h, s, b);
                        for (int x = 0; x < W; x++) {
                            buffer.setRGB(x, y, clr);
                        }
                    }
                }
                break;

            case 1: // Saturation
                if (cPanel.getColorScheme() == 0) {
                    h = current.getHSBhue();
                    b = current.getHSBbri();
                    for (int y = 0; y < 256; y++) {
                        s = 1.0f - y / 255.0f;
                        clr = ColorChanger.HSBtoColor(h, s, b);
                        for (int x = 0; x < W; x++) {
                            buffer.setRGB(x, y, clr);
                        }
                    }
                } else {
                    h = current.getHSLhue();
                    b = current.getHSLlight();
                    for (int y = 0; y < 256; y++) {
                        s = 1.0f - y / 255.0f;
                        clr = ColorChanger.HSLtoColor(h, s, b);
                        for (int x = 0; x < W; x++) {
                            buffer.setRGB(x, y, clr);
                        }
                    }
                }
                break;

            case 2: // Brightness
                if (cPanel.getColorScheme() == 0) {
                    h = current.getHSBhue();
                    s = current.getHSBsat();
                    for (int y = 0; y < 256; y++) {
                        b = 1.0f - y / 255.0f;
                        clr = ColorChanger.HSBtoColor(h, s, b);
                        for (int x = 0; x < W; x++) {
                            buffer.setRGB(x, y, clr);
                        }
                    }
                } else {
                    h = current.getHSLhue();
                    s = current.getHSLsat();
                    for (int y = 0; y < 256; y++) {
                        b = 1.0f - y / 255.0f;
                        clr = ColorChanger.HSLtoColor(h, s, b);
                        for (int x = 0; x < W; x++) {
                            buffer.setRGB(x, y, clr);
                        }
                    }
                }
                break;

            case 3: // Red
                g = current.getGreen();
                b1 = current.getBlue();
                for (int y = 0; y < 256; y++) {
                    r = 255 - y;
                    clr = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b1 & 0xff);
                    for (int x = 0; x < W; x++) {
                        buffer.setRGB(x, y, clr);
                    }
                }
                break;

            case 4: // Green
                r = current.getRed();
                b1 = current.getBlue();
                for (int y = 0; y < 256; y++) {
                    g = 255 - y;
                    clr = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b1 & 0xff);
                    for (int x = 0; x < W; x++) {
                        buffer.setRGB(x, y, clr);
                    }
                }
                break;

            case 5: // Blue
                r = current.getRed();
                g = current.getGreen();
                for (int y = 0; y < 256; y++) {
                    b1 = 255 - y;
                    clr = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b1 & 0xff);
                    for (int x = 0; x < W; x++) {
                        buffer.setRGB(x, y, clr);
                    }
                }
        }
        repaint();
    }

    /**
     * Graphics layer of the "circle" cursor.
     */
    private class CursorLayer extends JComponent {

        public CursorLayer() {
            setSize(W + 20, 276);
        }

        // Draw circle cursor at current cursor position
        @Override
        public void paintComponent(Graphics g) {
            g.drawImage(ColorPanel.cursorCircle, 9, curY, null);
        }

    } // class CursorLayer

    /**
     * This is a layer of the mouse "cross" cursor and mouse listeners.
     */
    private class MouseLayer extends JComponent {

        public MouseLayer() {
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
                    // We have to wait until text fields will give us their focus,
                    // and then we'll update all other components
                    EventQueue.invokeLater(ColorLine.this::setCurrentColor);
                }
            });

        } // constructor LineMouseLayer

    } // class LineMouseLayer

} // class ColorLine
