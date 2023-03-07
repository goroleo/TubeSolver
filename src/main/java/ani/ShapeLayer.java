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
 * The layer to fill some shape with the specified color. The shape is an image with areas
 * of opacity and transparency. All opaque areas will be filled with the desired color.
 */
public class ShapeLayer extends JComponent {

    /**
     * Shape image to fill with a specified color
     */
    private final BufferedImage shape;

    /**
     * Shape fill color (in integer).
     */
    private int rgb = 0;

    /**
     * Resulting image to draw
     */
    private final BufferedImage bi;

    /**
     * @param img shape image to fill it
     */
    public ShapeLayer(BufferedImage img) {
        shape = img;
        setSize(img.getWidth(), img.getHeight());
        bi = new BufferedImage(img.getWidth(), img.getHeight(), 2);
    }

    /**
     * @param img shape image to fill it
     * @param clr color to fill the shape
     */
    @SuppressWarnings("unused")
    public ShapeLayer(BufferedImage img, Color clr) {
        this(img);
        setColor(clr);
    }

    /**
     * Get current filled color
     *
     * @return color
     */
    public Color getColor() {
        return new Color(rgb);
    }

    /**
     * Get current filled color as an integer value
     *
     * @return color as integer
     */
    public int getColorValue() {
        return rgb;
    }

    /**
     * Set color to fill the shape
     *
     * @param clr color to fill the shape
     */
    public void setColor(Color clr) {
        setColor(clr.getRGB());
    }

    /**
     * Set color to fill the shape, and repaint component.
     *
     * @param clrValue color as integer
     */
    public void setColor(int clrValue) {
        if (rgb != clrValue) {
            rgb = clrValue;
            reShape();
            repaint();
        }
    }

    /**
     * This routine fills the shape with the specified color.
     */
    private void reShape() {
        for (int x = 0; x < shape.getWidth(); x++) {
            for (int y = 0; y < shape.getHeight(); y++) {
                bi.setRGB(x, y,
                        (shape.getRGB(x, y) & 0xff000000) | (rgb & 0xffffff));
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        if (bi != null) {
            g.drawImage(bi, 0, 0, null);
        }
    }
}
