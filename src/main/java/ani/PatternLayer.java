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
 * This is a simple layer that fills its entire size with the specified pattern image. If the layer is resized, it redraws itself.
 */
public class PatternLayer extends JComponent {

    /**
     * Pattern image.
     */
    private BufferedImage img;

    /**
     * @param bi pattern image
     */
    public PatternLayer(BufferedImage bi) {
        setImage(bi);
    }

    /**
     * @param bi pattern image
     */
    public void setImage(BufferedImage bi) {
        img = bi;
        repaint();
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        if (img != null) {
            int x, y = 0;
            do {
                x = 0;
                do {
                    g.drawImage(img, x, y, null);
                    x += img.getWidth();
                } while (x < getWidth());

                y += img.getHeight();
            } while (y < getHeight());
        }
    }

}
