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

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 * This is a simple layer to draw a specified image. The layer just sets its size and just draws the image.
 * Without any animation.
 */
public class ImageLayer extends JComponent {

    private BufferedImage img;

    /**
     * @param bi image to draw
     */
    public ImageLayer(BufferedImage bi) {
        setImage(bi);
    }

    /**
     * @param bi image to draw
     */
    public final void setImage(BufferedImage bi) {
        img = bi;
        setSize(bi.getWidth(), bi.getHeight());
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        if (img != null) {
            g.drawImage(img, 0, 0, null);
        }
    }
}
