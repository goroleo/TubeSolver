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
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

import static lib.lColorDialog.ColorPanel.current;

/**
 *
 * Color dialog Window. it creates the window and place ColorPanel into the
 * window.
 *
 */
public class LColorDialog extends JDialog {

    private final JFrame owner;

    /**
     * @see ColorPanel
     */
    public static ColorPanel cPanel;

    /**
     * Creates the color chooser dialog
     * @param owner owner frame to center the dialog
     * @param clr initial color
     */
    public LColorDialog(JFrame owner, Color clr) {
        super(owner, ResStrings.getString("strChangeColorTitle"), true);
        this.owner = owner;
        initFrame();
        addListeners();
        setColors(clr);
    }

    private void initFrame() {
        setDefaultCloseOperation(0); // DO_NOTHING_ON_CLOSE
        setIconImage(ColorPanel.lnFrameIcon);

        cPanel = new ColorPanel(this, true);

        calculateSize();
        calculatePos();
        setLayout(null);
        setBackground(new Color(0x28, 0x28, 0x28));
        setForeground(Color.white);
        getContentPane().add(cPanel);
    }
    
    private void calculateSize() {
        setResizable(true);
        Dimension dim = new Dimension();
        dim.width = cPanel.getWidth();
        dim.height = cPanel.getHeight();
        setPreferredSize(dim);
        pack();
        int realW = getContentPane().getWidth();
        int realH = getContentPane().getHeight();
        dim.width += (dim.width - realW);
        dim.height += (dim.height - realH);
        setPreferredSize(dim);
        pack();
        setResizable(false);
    }

    private void calculatePos() {
        if (owner != null) {
            setLocationRelativeTo(owner);
        } else {
            Rectangle r = getGraphicsConfiguration().getBounds();
            r.x = r.x + (r.width - getWidth())/2;
            r.y = r.y + (r.height - getHeight())/2;
            setLocation(r.x, r.y);
        }
    }
    
    private void addListeners() {
        
        // CLOSE WINDOW click
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cPanel.refuseAndClose();
            }
        });

        // ESCAPE pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> cPanel.refuseAndClose(),
                KeyStroke.getKeyStroke(0x1B, 0), // VK_ESCAPE
                JComponent.WHEN_IN_FOCUSED_WINDOW); // WHEN_IN_FOCUSED_WINDOW

        // CTRL+ENTER pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> cPanel.confirmAndClose(),
                KeyStroke.getKeyStroke('\n', InputEvent.CTRL_DOWN_MASK), // VK_ENTER + MASK_CTRL
                JComponent.WHEN_IN_FOCUSED_WINDOW); // WHEN_IN_FOCUSED_WINDOW
    }

    @Override
    public void setBackground(Color bg) {
        getContentPane().setBackground(bg);
    }
    
    @Override
    public void setForeground(Color fg) {
        getContentPane().setForeground(fg);
    }

    /**
     * @return dialog mode
     * @see ColorPanel#getDialogMode
     */
    public int getDialogMode() {
        return cPanel.getDialogMode();
    }

    /**
     * @param newMode dialog mode
     * @see ColorPanel#setDialogMode
     */
    public void setDialogMode(int newMode) {
        cPanel.setDialogMode(newMode);
    }

    /* unused
    /**
     * Gets the color scheme
     * @return color scheme
     * @see ColorPanel#getColorScheme()
    public int getColorScheme() {
        return cPanel.getColorScheme();
    }
     */

    /* unused
    /**
     * Sets the color scheme
     * @param scheme color scheme
     * @see ColorPanel#getColorScheme()
    public void setColorScheme(int scheme) {
        cPanel.setColorScheme(scheme);
    }
     */

    /* unused
    /**
     * Gets chosen color at RGB integer value: 0xAARRGGBB.
     * @return RGB integer value.
    public int getColorValue() {
        return cPanel.getColor().getRGB();
    }
     */

    /**
     * Gets chosen color.
     * @return color
     */
    public Color getColor() {
        return cPanel.getColor();
    }

    /* unused
    /**
     * Sets the initial color.
     * @param rgb color integer value: 0xAARRGGBB.
    public void setColor(int rgb) {
        cPanel.setColor(new Color(rgb));
    }
     */

    /* unused
    /**
     * Sets the initial color.
     * @param clr color
    public void setColor(Color clr) {
        cPanel.setColor(clr);
    }
     */

    /* unused
    /**
     * Sets color to shows as previous color
     * @param rgb color integer value: 0xAARRGGBB.
    public void setPrevColor(int rgb) {
        cPanel.setPrevColor(new Color(rgb));
    }
     */

    /* unused
    /**
     * Sets color to shows as previous color
     * @param clr color
    public void setPrevColor(Color clr) {
        cPanel.setPrevColor(clr);
    }
     */

    /* unused
    /**
     * Sets the both color, initial and previous
     * @param rgb color integer value: 0xAARRGGBB.
    public void setColors(int rgb) {
        setColors(new Color(rgb));
    }
     */

    /**
     * Sets the both color, initial and previous
     * @param clr color
     */
    public void setColors(Color clr) {
        cPanel.setPrevColor(clr);
        cPanel.setColor(clr);
    }

    /**
     * Adds en external color's change listener
     * @param toAdd listener
     */
    public void addColorListener(ColorListener toAdd) {
        current.addListener(toAdd);
    }

    /**
     * Removes an external listener
     * @param toRemove listener
     */
    @SuppressWarnings("unused")
    public void removeColorListener(ColorListener toRemove) {
        current.removeListener(toRemove);
    }

    /**
     * shows the color choose dialog
     * @return chosen color
     */
    public Color chooseColor() {
        setVisible(true);
        return current.getColor();
    }

}
