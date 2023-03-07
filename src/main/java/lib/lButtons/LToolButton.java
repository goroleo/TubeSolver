/*
 * Copyright (c) 2022 legoru / goroleo <legoru@me.com>
 * 
 * This software is distributed under the <b>MIT License.</b>
 * The full text of the License you can read here: 
 * https://choosealicense.com/licenses/mit/
 * 
 * Use this as you want! ))
 */

package lib.lButtons;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;

@SuppressWarnings("unused")
public class LToolButton extends LPictureButton {

    private BufferedImage iconImg;

    public LToolButton(Container owner, String name) {
        super(owner, name);
    }

    public LToolButton(Container owner, String name, String iconName) {
        super(owner, name);
        setIcon(new ImageIcon(loadImage(getImageFileName(iconName))));
    }

    @Override
    public void setIcon(Icon icon) {
        super.setIcon(icon);
        iconImg = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), 2);
    }

    private void updateIcon(Color clr) {
        BufferedImage icn = (BufferedImage) ((ImageIcon) this.getIcon()).getImage();
        for (int x = 0; x < icn.getWidth(); x++) {
            for (int y = 0; y < icn.getHeight(); y++) {
                int rgb = (icn.getRGB(x, y) & 0xFF000000) | (clr.getRGB() & 0xFFFFFF);
                iconImg.setRGB(x, y, rgb);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color clr = g.getColor();
        updateIcon(clr);
        g.drawImage(iconImg, 0, 0, null);
    }

}
