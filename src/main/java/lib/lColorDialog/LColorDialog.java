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

import core.Options;
import core.ResStrings;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import static lib.lColorDialog.ColorPanel.currentColor;

/**
 *
 * Color dialog Window. it creates the window and place CoorPanel into the
 * window.
 *
 */
public class LColorDialog extends JDialog {

    private final JFrame owner;
    
    private final ColorPanel cPan = new ColorPanel(this, true);

    @SuppressWarnings("unused")
    public LColorDialog(JFrame owner) {
        this(owner, Color.white);
    }

    @SuppressWarnings("unused")
    public LColorDialog(Color clr) {
        this(null, clr);
    }
    
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
        calculateSize();
        calculatePos();
        setLayout(null);
        setBackground(new Color(0x28, 0x28, 0x28));
        setForeground(Color.white);
        getContentPane().add(cPan);
    }
    
    private void calculateSize() {
        setResizable(true);
        Dimension dim = new Dimension();
        dim.width = cPan.getWidth();
        dim.height = cPan.getHeight();
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
        Rectangle r = getGraphicsConfiguration().getBounds();
        if (Options.ccdPositionX >= 0 && Options.ccdPositionY >= 0 
                && Options.ccdPositionX + getWidth() <= r.width 
                && Options.ccdPositionY + getHeight() <= r.height )  {
            setLocation(Options.ccdPositionX, Options.ccdPositionY);
        } else if (owner != null) {
            setLocationRelativeTo(owner);
        } else {
            r.x = r.x + (r.width - getWidth())/2;
            r.y = r.y + (r.height - getHeight())/2;
            setLocation(r.x, r.y);
        }
    }
    
    @SuppressWarnings("MagicConstant")
    private void addListeners() {
        
        // CLOSE WINDOW click
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cPan.refuseAndClose();
            }
        });

        // ESCAPE pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> cPan.refuseAndClose(),
                KeyStroke.getKeyStroke(0x1B, 0), // VK_ESCAPE
                2); // WHEN_IN_FOCUSED_WINDOW

        // CTRL+ENTER pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> cPan.confirmAndClose(),
                KeyStroke.getKeyStroke('\n', 2), // VK_ENTER + MASK_CTRL
                2); // WHEN_IN_FOCUSED_WINDOW
    }

    @Override
    public void setBackground(Color bg) {
        getContentPane().setBackground(bg);
    }
    
    @Override
    public void setForeground(Color fg) {
        getContentPane().setForeground(fg);
    }

    @SuppressWarnings("unused")
    public void setColorScheme(int scheme) {
        cPan.setColorScheme(scheme);
    }

    @SuppressWarnings("unused")
    public int getColorValue() {
        return cPan.getColor().getRGB();
    }

    @SuppressWarnings("unused")
    public Color getColor() {
        return cPan.getColor();
    }

    @SuppressWarnings("unused")
    public void setColor(int rgb) {
        cPan.setColor(new Color(rgb));
    }

    @SuppressWarnings("unused")
    public void setColor(Color clr) {
        cPan.setColor(clr);
    }

    @SuppressWarnings("unused")
    public void setPrevColor(int rgb) {
        cPan.setPrevColor(new Color(rgb));
    }

    @SuppressWarnings("unused")
    public void setPrevColor(Color clr) {
        cPan.setPrevColor(clr);
    }

    @SuppressWarnings("unused")
    public void setColors(int rgb) {
        setColors(new Color(rgb));
    }

    public void setColors(Color clr) {
        cPan.setPrevColor(clr);
        cPan.setColor(clr);
    }

    public void addColorListener(ColorListener toAdd) {
        currentColor.addListener(toAdd);
    }

    @SuppressWarnings("unused")
    public void removeColorListener(ColorListener toRemove) {
        currentColor.removeListener(toRemove);
    }

    public Color chooseColor() {
        setVisible(true);
        return currentColor.getColor();
    }

    public void saveOptions() {
        Options.ccdPositionX = getX();
        Options.ccdPositionY = getY();
    }

}
