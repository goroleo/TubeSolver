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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import java.awt.Container;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.JToolTip;

@SuppressWarnings("unused")
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
    private BufferedImage imgCurrent = null;

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

    public LPictureButton() {
        this(null, null);
    }

    public LPictureButton(Container owner) {
        this(owner, null);
    }

    public LPictureButton(String name) {
        this(null, name);
    }

    public LPictureButton(Container owner, String name) {
        super();
        parent = owner;

        toolTip = new JToolTip();

        this.setLayout(null);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setFocusable(true);

        setButtonName(name);
        loadImages();
        if (imgEnabled != null) {
            setSize(imgEnabled.getWidth(), imgEnabled.getHeight());
        }
    }

    public void setButtonName(String name) {
        if (name != null && name.length() != 0) {
            buttonName = name;
        }
        repaint();
    }

    public void setColorEnabled(Color clr) {
        clrEnabled = clr;
        repaint();
    }

    public void setColorDisabled(Color clr) {
        clrDisabled = clr;
        repaint();
    }

    public void setColorHover(Color clr) {
        clrHover = clr;
        repaint();
    }

    public void setColorPressed(Color clr) {
        clrPressed = clr;
        repaint();
    }

    public void setToolTipBackground(Color clr) {
        clrToolTipBg = clr;
        toolTip.setBackground(clrToolTipBg);
    }

    public void setToolTipForeground(Color clr) {
        clrToolTipFg = clr;
        toolTip.setForeground(clrToolTipFg);
    }

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

    public String getImageFileName(String suffix) {
        return buttonName + "_" + suffix + ".png";
    }

    public URL getImageURL(String fName) {
        return Objects.requireNonNullElse(parent, this).getClass().getResource("/img/" + fName);
    }

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
