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
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import lib.lButtons.LPictureButton;

/**
 * This is the main panel to place all other color change controls inside. All
 * controls interact each other through this panel.
 */
public class ColorPanel extends JComponent {

    /**
     * Dialog controls: color box.
     * @see ColorBox
     */
    private static ColorBox cBox;
    
    /**
     * Dialog controls: color line.
     * @see ColorLine
     */
    private static ColorLine cLine;
    
    /**
     * Dialog controls: color labels.
     * @see ColorLabels
     */
    private static ColorLabels cLabels;

    /**
     * Dialog controls: Current color and previous color.
     * @see ColorCurrent
     */
    private static ColorCurrent cCur;

    /**
     * Dialog controls: OK button.
     * The color panel has an option to hide buttons, using <b>showButtons</b> 
     * parameter on the constructor.
     */
    private static LPictureButton btnOk;

    /**
     * Dialog controls: CANCEL button.
     * The color panel has an option to hide buttons, using <b>showButtons</b> 
     * parameter on the constructor.
     */
    private static LPictureButton btnCancel;

    /**
     * Dialog colors: current color.
     * All of the color controls can change this color, and all other controls 
     * are listen these cnanges.
     * @see ColorChanger
     * @see ColorCurrent
     */
    public static ColorChanger currentColor;
    
    /**
     * Dialog colors: previous color.
     * This color draws into lnColorCurrent panel.
     * @see ColorCurrent
     */
    public static int lnPrevColor = 0xffffffff;

    /**
     * Parent dialog frame used to access it from other controls (i.e. from 
     * buttons).
     */
    public JDialog dlgFrame;

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
     * @see ColorBox
     * @see ColorLine
     */
    public static Cursor cursorCross;           // Cross mouse cursor for Box and Line

    /**
     * Shared images: Circle cursor for lnColorBox and lnColorLine. <br>
     * All shared images are load from this panel, and all other controls no 
     * need to have their own instances (and their own loader) of shared images.
     * @see ColorBox
     * @see ColorLine
     */
    public static BufferedImage cursorCircle;   // Circle cursor for Box and Line

    /**
     * Shared images: normal state Icon for RadioButtons. Used in lnColorLabels.<br>
     * All shared images are load from this panel, and all other controls no 
     * need to have their own instances (and their own loader) of shared images.
     * @see ColorLabels
     */
    public static ImageIcon rbIcon;             // Icon for RadioButtons

    /**
     * Shared images: Selected Icon for RadioButtons. Used in lnColorLabels.<br>
     * All shared images are load from this panel, and all other controls no 
     * need to have their own instances (and their own loader) of shared images.
     * @see ColorLabels
     */
    public static ImageIcon rbIconSelected;     // Icon_Selected for RadioButtons  

    /**
     * Constructor. <br>
     * @param ownerFrame the parent dialog frame used to access it from other 
     * controls (i.e. from buttons). 
     * @param showButtons true or false
     */
    public ColorPanel(JDialog ownerFrame, boolean showButtons) {
        dlgFrame = ownerFrame;
        setBackground(null);
        setForeground(null);

        setSize(470, showButtons ? 345 : 300);

        currentColor = new ColorChanger();

        loadResources();

        cBox = new ColorBox();
        cBox.setLocation(15, 10);
        currentColor.addListener(cBox);
        add(cBox);

        cLine = new ColorLine();
        cLine.setLocation(295, 10);
        currentColor.addListener(cLine);
        add(cLine);

        cCur = new ColorCurrent();
        cCur.setLocation(338, 10);
        currentColor.addListener(cCur);
        add(cCur);

        cLabels = new ColorLabels();
        cLabels.setLocation(341, 85);
        // TextFields will be added to listeners on their own constructors.
        add(cLabels);

        if (showButtons) {

            btnOk = addButton(ResStrings.getString("strOk"), 236, 300);
            btnOk.addActionListener((ActionEvent e) -> confirmAndClose());
            btnOk.setDefaultCapable(true);
            add(btnOk);

            btnCancel = addButton(ResStrings.getString("strCancel"), 346, 300);
            btnCancel.addActionListener((ActionEvent e) -> refuseAndClose());
            btnCancel.setDefaultCapable(false);
            add(btnCancel);
        }
//        dlgFrame.getRootPane().setDefaultButton(btnOk);
    }

    public static void setDialogMode(int newMode) {
        cBox.updateDialogMode(newMode);
        cLine.updateDialogMode(newMode);
        cLabels.updateDialogMode(newMode);
    }

    public void setColorScheme(int scheme) {
        cBox.updateColorScheme(scheme);
        cLine.updateColorScheme(scheme);
        cLabels.updateColorScheme(scheme);
    }

    public void addColorListener(ColorListener toAdd) {
        currentColor.addListener(toAdd);
    }

    public void removeColorListener(ColorListener toRemove) {
        currentColor.removeListener(toRemove);
    }

    public Color getColor() {
        return currentColor.getColor();
    }

    public void setColor(Color clr) {
        currentColor.setRGB(this, clr.getRGB());
    }

    public Color getPrevColor() {
        return new Color(lnPrevColor);
    }

    public void setPrevColor(Color clr) {
        lnPrevColor = clr.getRGB();
        cCur.repaint();
    }

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
    * @see #dlgFrame
    */
    public void confirmAndClose() {
        EventQueue.invokeLater(() -> dlgFrame.dispose());
        ((LColorDialog) dlgFrame).saveOptions();
    }

    /** 
    * All color changes will be cancelled, so the previous color will be set as 
    * a current. Call this routine when Cancel button click.
    * @see #dlgFrame
    */
    public void refuseAndClose() {
        currentColor.setRGB(this, lnPrevColor);
        EventQueue.invokeLater(() -> dlgFrame.dispose());
        ((LColorDialog) dlgFrame).saveOptions();
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
