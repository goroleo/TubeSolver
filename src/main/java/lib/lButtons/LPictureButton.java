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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * This is extended JButton class with specified pictures for every button state.
 * Images from <b>/img/</b> folder of application resources are used to paint the button.<br>
 * <br>Every file with button images must be named as:
 * <br>[button name]_enabled.png - for the ordinary state.
 * <br>[button name]_disabled.png - for the disabled state.
 * <br>[button name]_focused.png - for the focused state.
 * <br>[button name]_hover.png - for the mouse over state.
 * <br>[button name]_pressed.png - for the pressed state.
 * <br>[button name]_default.png - for the window default button state.<br>
 * <br>If the expected file is not found, the file of the enabled state will be
 * used instead. So at least [button name]_enabled.png file must be in the
 * /resources/img/ folder.
 * At the constructor the [button name] is set, and all button's pictures will be loaded
 * from files as described above.
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class LPictureButton extends JButton {

// Button name 
    private String buttonName = "btnDialog";

// Button parent to use its images for draw  
    private final Container parent;

// Images:     
    private BufferedImage imgEnabled = null;
    private BufferedImage imgDisabled = null;
    private BufferedImage imgFocused = null;
    private BufferedImage imgHover = null;
    private BufferedImage imgPressed = null;
    private BufferedImage imgDefault = null;

// Text Colors:
    private Color clrEnabled = new Color(255, 255, 255);
    private Color clrDisabled = new Color(128, 128, 128);
    private Color clrHover = new Color(0, 0, 0);
    private Color clrPressed = new Color(0, 0, 0);
    private Color clrToolTipBg = Color.BLACK;
    private Color clrToolTipFg = Color.WHITE;
    private Color clrToolTipBorder = Color.GRAY;

// ToolTip
    private final JToolTip toolTip;

    /**
     * Creates the Picture button with the default name.
     * @param owner the parent frame
     */
    public LPictureButton(Container owner) {
        this(owner, null);
    }

    /**
     * Creates the Picture button with the specified name.
     * @param name button name to load the state pictures from proper files.
     */
    public LPictureButton(String name) {
        this(null, name);
    }

    /**
     * Creates the Picture button.
     * @param owner the parent frame
     * @param name button name to load the state pictures from proper files.
     */
    public LPictureButton(Container owner, String name) {
        super();
        parent = owner;

        toolTip = new JToolTip();
        toolTip.setBackground(clrToolTipBg);
        toolTip.setForeground(clrToolTipFg);
        toolTip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(clrToolTipBorder),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));

        this.setLayout(null);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setFocusable(true);

        setButtonName(name);
        if (imgEnabled != null) {
            setSize(imgEnabled.getWidth(), imgEnabled.getHeight());
        }
    }

    /**
     * Sets the new button name and reloads state pictures.
     * @param name button name to load the state pictures from proper files.
     */
    public void setButtonName(String name) {
        if (name != null && name.length() != 0) {
            buttonName = name;
        }
        loadImages();
        repaint();
    }

    /**
     * Sets the Text color for the enabled button state.
     * @param clr new text color.
     */
    public void setColorEnabled(Color clr) {
        clrEnabled = clr;
        repaint();
    }

    /**
     * Sets the Text color for the disabled button state.
     * @param clr new text color.
     */
    public void setColorDisabled(Color clr) {
        clrDisabled = clr;
        repaint();
    }

    /**
     * Sets the Text color for the mouse over button state.
     * @param clr new text color.
     */
    public void setColorHover(Color clr) {
        clrHover = clr;
        repaint();
    }

    /**
     * Sets the Text color for the pressed button state.
     * @param clr new text color.
     */
    public void setColorPressed(Color clr) {
        clrPressed = clr;
        repaint();
    }

    /**
     * Sets the background color of the ToolTip window.
     * @param clr new color.
     */
    public void setToolTipBackground(Color clr) {
        clrToolTipBg = clr;
        toolTip.setBackground(clrToolTipBg);
    }

    /**
     * Sets the text color of the ToolTip window.
     * @param clr new color.
     */
    public void setToolTipForeground(Color clr) {
        clrToolTipFg = clr;
        toolTip.setForeground(clrToolTipFg);
    }

    /**
     * Sets the border color of the ToolTip window.
     * @param clr new color.
     */
    public void setToolTipBorder(Color clr) {
        clrToolTipBorder = clr;
        toolTip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(clrToolTipBorder),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));
    }

    private void loadImages() {
        if (imgEnabled == null) {
            imgEnabled = loadImage(getImageFileName("enabled"));
        }
        if (imgDisabled == null) {
            imgDisabled = loadImage(getImageFileName("disabled"));
            if (imgDisabled == null) {
                imgDisabled = imgEnabled;
            }
        }
        if (imgFocused == null) {
            imgFocused = loadImage(getImageFileName("focused"));
            if (imgFocused == null) {
                imgFocused = imgEnabled;
            }
        }
        if (imgHover == null) {
            imgHover = loadImage(getImageFileName("hover"));
            if (imgHover == null) {
                imgHover = imgEnabled;
            }
        }
        if (imgPressed == null) {
            imgPressed = loadImage(getImageFileName("pressed"));
            if (imgPressed == null) {
                imgPressed = imgEnabled;
            }
        }
        if (imgDefault == null) {
            imgDefault = loadImage(getImageFileName("default"));
            if (imgDefault == null) {
                imgDefault = imgEnabled;
            }
        }
    }

    /**
     * Gets the file name of the button state pictures.
     * @param suffix the suffix after the button name.
     * @return file name
     */
    public String getImageFileName(String suffix) {
        return buttonName + "_" + suffix + ".png";
    }

    private URL getImageURL(String fName) {
        return this.getClass().getResource("/img/" + fName);
    }

    /**
     * Loads the button's state picture from the application resource.
     * @param fName file name.
     * @return image or null
     */
    public BufferedImage loadImage(String fName) {
        BufferedImage img = null;
        URL imgURL = getImageURL(fName);
        if (imgURL != null) {
            try {
                img = ImageIO.read(imgURL);
            } catch (IOException ex) {
                System.err.println("Error while loading file: " + "/img/" + fName);
            }
        } else {
            System.err.println("Couldn't find file: " + "/img/" + fName);
        }
        return img;
    }

    @Override
    public void paintComponent(Graphics g) {

        Font f = g.getFont();
        FontMetrics fm = g.getFontMetrics(f);

        int iconWidth = 0;
        if (getIcon() != null) {
            iconWidth = getIcon().getIconWidth() + getIconTextGap();
        }

        int textHeight = (int) (72.0 * f.getSize() / Toolkit.getDefaultToolkit().getScreenResolution());
        int textWidth = fm.stringWidth(getText()) + iconWidth;
        int x = ((getWidth() - 3) - textWidth) / 2 + 1;
        int y = ((getHeight() - 3) + textHeight) / 2 + 1;

        BufferedImage imgCurrent;
        if (!this.isEnabled()) {
            imgCurrent = imgDisabled;
            g.setColor(clrDisabled);
        } else if (this.model.isPressed()) {
            imgCurrent = imgPressed;
            g.setColor(clrPressed);
            x++;
            y++;
        } else if (this.model.isRollover()) {
            imgCurrent = imgHover;
            g.setColor(clrHover);
        } else if (this.hasFocus()) {
            imgCurrent = imgFocused;
            g.setColor(clrEnabled);
        } else if (this.isDefaultButton()) {
            imgCurrent = imgDefault;
            g.setColor(clrEnabled);
        } else {
            imgCurrent = imgEnabled;
            g.setColor(clrEnabled);
        }

        g.drawImage(imgCurrent, 0, 0, null);
        g.drawString(getText(), x, y);
    }

    @Override
    public JToolTip createToolTip() {
        return toolTip;
    }

}
