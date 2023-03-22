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

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This class is an extension of the LPictureButton class for buttons, that do not have text
 * labels, but tools pictures only. In addition to the state images (see LPictureButton), an
 * image is added to be displayed on the button (like an Icon). <br>
 * <br>The file with an Icon image must be placed in the /img/ folder also. It must be named as:
 * <br>[button name]_[suffix].png.<br>
 * <br>The constructor receives the button name and loads all images for the button states as
 * described for LPictureButton. And then it loads the icon image file. An icon will be recolored
 * depending on the button state using the text color of LPictureButton.
 *
 * @see LPictureButton
 */
@SuppressWarnings("unused")
public class LToolButton extends LPictureButton {

    private BufferedImage iconImg;

    /**
     * Creates the tool button without an icon.
     *
     * @param owner      parent frame
     * @param buttonName button name to load the state pictures from proper files.
     */
    public LToolButton(Container owner, String buttonName) {
        super(owner, buttonName);
    }

    /**
     * Creates the tool button.
     *
     * @param owner      parent frame
     * @param buttonName button name to load the state pictures from proper files.
     * @param iconSuffix the suffix of the file name stores the icon image.
     */
    public LToolButton(Container owner, String buttonName, String iconSuffix) {
        super(owner, buttonName);
        setIcon(new ImageIcon(loadImage(getImageFileName(iconSuffix))));
    }

    @Override
    public void setIcon(Icon icon) {
        super.setIcon(icon);
        iconImg = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), 2);
    }

    /**
     * Repaints an icon by the specified color.
     *
     * @param clr color to repaint an icon.
     */
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
        updateIcon(g.getColor());
        g.drawImage(iconImg, 0, 0, null);
    }

}
