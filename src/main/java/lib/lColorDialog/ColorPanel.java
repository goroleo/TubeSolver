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

import core.ResStrings;
import lib.lButtons.LPictureButton;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * This is the main panel to place all other color change controls inside. All
 * controls interact each other through this panel.
 */
public class ColorPanel extends JComponent {

    /**
     * Dialog controls: color box.
     *
     * @see ColorBox
     */
    private final ColorBox cBox;

    /**
     * Dialog controls: color line.
     *
     * @see ColorLine
     */
    private final ColorLine cLine;

    /**
     * Dialog controls: color labels.
     *
     * @see ColorLabels
     */
    private final ColorLabels cLabels;

    /**
     * Dialog controls: Current color and previous color.
     *
     * @see ColorCurrent
     */
    private final ColorCurrent cCur;

    /**
     * Dialog colors: current color.
     * All the color controls can change this color, and all other controls
     * are listen these changes.
     */
    public static ColorChanger current;

    /**
     * Dialog colors: previous color.
     * This color draws into lnColorCurrent panel.
     *
     * @see ColorCurrent
     */
    public static int previousColor = 0xffffffff;

    /**
     * <b>dialogMode</b> is an integer value from 0 to 5<br>
     * <i>HSB/HSV (or HSL) color model:</i><ul>
     * <li>0. <b>H</b>ue
     * <li>1. <b>S</b>aturation
     * <li>2. <b>B</b>rightness (<b>L</b>ightness when HSL)</ul>
     * <i>RGB color model:</i><ul>
     * <li>3. <b>R</b>ed
     * <li>4. <b>G</b>reen
     * <li>5. <b>B</b>lue</ul>
     */
    private int dialogMode = 0; // 0-5 means Hue, Sat, Bri, R, G, B

    /**
     * colorScheme is an integer value. 0 means HSB/HSV, 1 means HSL
     */
    private int colorScheme = 0;


    /**
     * Parent dialog frame used to access it from other controls (i.e. from
     * buttons).
     */
    public final JDialog dlgFrame;

    /**
     * Shared images: Dialog frame icon. <br>
     * All shared images are load from this panel, and all other controls no
     * need to have their own instances (and their own loader) of shared images.
     */
    public static BufferedImage lnFrameIcon;    // Dialog icon )) 

    /**
     * Shared images: Cross cursor for ColorBox and ColorLine. <br>
     * All shared images are load from this panel, and all other controls no
     * need to have their own instances (and their own loader) of shared images.
     *
     * @see ColorBox
     * @see ColorLine
     */
    public static Cursor cursorCross;           // Cross mouse cursor for Box and Line

    /**
     * Shared images: Circle cursor for lnColorBox and lnColorLine. <br>
     * All shared images are load from this panel, and all other controls no
     * need to have their own instances (and their own loader) of shared images.
     *
     * @see ColorBox
     * @see ColorLine
     */
    public static BufferedImage cursorCircle;   // Circle cursor for Box and Line

    /**
     * Shared images: normal state Icon for RadioButtons. Used in lnColorLabels.<br>
     * All shared images are load from this panel, and all other controls no
     * need to have their own instances (and their own loader) of shared images.
     *
     * @see ColorLabels
     */
    public static ImageIcon rbIcon;             // Icon for RadioButtons

    /**
     * Shared images: Selected Icon for RadioButtons. Used in lnColorLabels.<br>
     * All shared images are load from this panel, and all other controls no
     * need to have their own instances (and their own loader) of shared images.
     *
     * @see ColorLabels
     */
    public static ImageIcon rbIconSelected;     // Icon_Selected for RadioButtons  

    /**
     * Constructor. <br>
     *
     * @param ownerFrame  the parent dialog frame used to access it from other
     *                    controls (i.e. from buttons).
     * @param showButtons true or false
     */
    public ColorPanel(JDialog ownerFrame, boolean showButtons) {
        dlgFrame = ownerFrame;
        setBackground(null);
        setForeground(null);

        setSize(470, showButtons ? 345 : 300);

        current = new ColorChanger();

        loadResources();

        cBox = new ColorBox();
        cBox.setLocation(15, 10);
        current.addListener(cBox);
        add(cBox);

        cLine = new ColorLine();
        cLine.setLocation(295, 10);
        current.addListener(cLine);
        add(cLine);

        cCur = new ColorCurrent();
        cCur.setLocation(338, 10);
        current.addListener(cCur);
        add(cCur);

        cLabels = new ColorLabels();
        cLabels.setLocation(341, 85);
        // TextFields will be added to listeners on their own constructors.
        add(cLabels);

        if (showButtons) {

            LPictureButton btnOk = addButton(ResStrings.getString("strOk"), 236, 300);
            btnOk.addActionListener((ActionEvent e) -> confirmAndClose());
            btnOk.setDefaultCapable(true);
            add(btnOk);

            LPictureButton btnCancel = addButton(ResStrings.getString("strCancel"), 346, 300);
            btnCancel.addActionListener((ActionEvent e) -> refuseAndClose());
            btnCancel.setDefaultCapable(false);
            add(btnCancel);
        }
    }

    /**
     * @return dialog mode
     * @see ColorPanel#dialogMode
     */
    public int getDialogMode() {
        return dialogMode;
    }

    /**
     * Sets the mode of the dialog
     * @param newMode a new mode of the dialog
     * @see ColorPanel#dialogMode
     */
    public void setDialogMode(int newMode) {
        dialogMode = newMode;
        cBox.updateColor();
        cLine.updateColor();
        cLabels.updateDialogMode();
    }

    /**
     * @return color scheme
     * @see ColorPanel#colorScheme
     */
    public int getColorScheme() {
        return colorScheme;
    }

    /**
     * Sets color scheme
     * @param scheme new color scheme
     * @see ColorPanel#colorScheme
     */
    public void setColorScheme(int scheme) {
        if (scheme == 0 || scheme == 1) {
            colorScheme = scheme;
            cBox.updateColor();
            cLine.updateColor();
            cLabels.updateColorScheme();
        }
    }

    /**
     * Gets the current color chosen by user
     * @return current color
     */
    public Color getColor() {
        return current.getColor();
    }

    /**
     * Sets the current color of the dialog
     * @param clr new color
     */
    public void setColor(Color clr) {
        current.setRGB(this, clr.getRGB());
    }

    /**
     * Sets the previous color of the dialog
     * @param clr previous color
     */
    public void setPrevColor(Color clr) {
        previousColor = clr.getRGB();
        cCur.repaint();
    }

    @SuppressWarnings("SameParameterValue")
    private LPictureButton addButton(String txt, int x, int y) {
        LPictureButton btn = new LPictureButton(this);
        btn.setText(txt);
        btn.setLocation(x, y);
        btn.setBackground(null);
        btn.setForeground(null);
        btn.setFocusable(true);
        return btn;
    }

    /**
     * The routine will close the frame. Current color is the chosen color already.
     * Call this routine when Ok button click.
     *
     * @see #dlgFrame
     */
    public void confirmAndClose() {
        EventQueue.invokeLater(dlgFrame::dispose);
    }

    /**
     * All color changes will be cancelled, so the previous color will be set as
     * a current. Call this routine when Cancel button click.
     *
     * @see #dlgFrame
     */
    public void refuseAndClose() {
        current.setRGB(this, previousColor);
        EventQueue.invokeLater(dlgFrame::dispose);
    }


/////////////////////////////////////////////////////////
//         
//         Resources loader     
//         
/////////////////////////////////////////////////////////
    private void loadResources() {
        lnFrameIcon = createBufImage("lncolordialog_icon.png");
        rbIcon = createIconImage("radiobutton_icon_standard.png");
        rbIconSelected = createIconImage("radiobutton_icon_selected.png");
        cursorCircle = createBufImage("colorbox_cursor_circle.png");
        cursorCross = Toolkit.getDefaultToolkit().createCustomCursor(
                createBufImage("colorbox_cursor_cross.png"),
                new Point(10, 10), "");
    }

    private BufferedImage createBufImage(String FName) {
        java.net.URL url = this.getClass().getResource("/img/" + FName);
        if (url == null) {
            return null;
        }
        try {
            return ImageIO.read(url);
        } catch (IOException ex) {
            return null;
        }
    }

    private ImageIcon createIconImage(String FName) {
        java.net.URL url = this.getClass().getResource("/img/" + FName);
        if (url != null) {
            return new ImageIcon(url);
        } else {
            return null;
        }
    }

}
