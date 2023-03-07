/*
 * Copyright (c) 2022 legoru / goroleo <legoru@me.com>
 *
 * This software is distributed under the <b>MIT License.</b>
 * The full text of the License you can read here:
 * https://choosealicense.com/licenses/mit/
 *
 * Use this as you want! ))
 */
package gui;

import core.Options;
import core.ResStrings;
import lib.lMenus.LPopupMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import static gui.MainFrame.palPan;

/**
 * The popup menu for the Palette Panel and Color Buttons.
 * @see PalettePanel
 * @see ColorButton
 */
public class PaletteMenu extends LPopupMenu {

///////////////////////////////////////////////////////////////////////////
//
//               * Fields / variables *
//
///////////////////////////////////////////////////////////////////////////

    /** This menu caption. */
    private final JMenuItem pal;

    /** Docked position menu item. */
    private final JMenu pos;

    /** Position menu item: docked to top. */
    private final JMenuItem top;

    /** Position menu item: docked to bottom. */
    private final JMenuItem bottom;

    /** Position menu item: docked to left. */
    private final JMenuItem left;

    /** Position menu item: docked to right. */
    private final JMenuItem right;

    /** Lines menu item (number of rows or cols at the Palette panel). */
    private final JMenu lines;

    /** lines menu item: 1. */
    private final JMenuItem lines1;

    /** lines menu item: 2. */
    private final JMenuItem lines2;

    /** lines menu item: 3. */
    private final JMenuItem lines3;

    /** Menu separator. */
    private final JSeparator sep1;

    /** Color change menu item. */
    private final JMenuItem change;

    /** Default palette menu item. */
    private final JMenuItem def;

    /** Corresponding ColorButton to change its color. */
    private ColorButton correspButton;

    /** The Image to get the Icon of the corresponding Button's color. */
    private final BufferedImage icon = new BufferedImage(16, 16, 1);

///////////////////////////////////////////////////////////////////////////
//
//               * Routines *
//
///////////////////////////////////////////////////////////////////////////

    /**
     * The constructor. Creates the Palette menu and adds menu items.
     */
    @SuppressWarnings("MagicConstant")
    public PaletteMenu() {
        super();

        pal = addMenuItem(null, ResStrings.getString("strPalette"));
        pal.setFont(pal.getFont().deriveFont(1, 13f));
        addSeparator(null);

        // position menu
        pos = addMenu(null, ResStrings.getString("strPosition"));
        {
            top = addMenuItem(pos, ResStrings.getString("strTop"));
            top.addActionListener((ActionEvent e) -> positionClick(0));

            bottom = addMenuItem(pos, ResStrings.getString("strBottom"));
            bottom.addActionListener((ActionEvent e) -> positionClick(1));

            left = addMenuItem(pos, ResStrings.getString("strLeft"));
            left.addActionListener((ActionEvent e) -> positionClick(2));

            right = addMenuItem(pos, ResStrings.getString("strRight"));
            right.addActionListener((ActionEvent e) -> positionClick(3));
        }

        // number of lines menu
        lines = addMenu(null, ResStrings.getString("strRows"));
        {
            lines1 = addMenuItem(lines, "1");
            lines1.addActionListener((ActionEvent e) -> linesClick(1));
            lines2 = addMenuItem(lines, "2");
            lines2.addActionListener((ActionEvent e) -> linesClick(2));
            lines3 = addMenuItem(lines, "3");
            lines3.addActionListener((ActionEvent e) -> linesClick(3));
        }

        // separator
        sep1 = addSeparator(null);

        {
            // change color
            change = addMenuItem(null, ResStrings.getString("strChangeColor"));
            change.addActionListener((ActionEvent e) -> clrChangeClick());
        }

        // separator
        addSeparator(null);

        {
            // set default palette
            def = addMenuItem(null, ResStrings.getString("strDefaultPalette"));
            def.addActionListener((ActionEvent e) -> defaultClick());
        }
    }

    @Override
    public void show(Component invoker, int x, int y) {
        if (invoker instanceof ColorButton) {
            correspButton = (ColorButton) invoker;
            setColorIcon(correspButton.getColor());
            showColorChange(true);
        } else {
            correspButton = null;
            showColorChange(false);
        }
        updatePosIcons();
        updateLanguage();
        super.show(invoker, x, y);
    }

    /**
     * Handles the click on position items.
     */
    private void positionClick(int number) {
        palPan.setDockedTo(number);
    }

    /**
     * Handles the click on lines items.
     */
    private void linesClick(int number) {
        if (palPan.getDockedTo() < 2) {
            palPan.setRows(number);
        } else {
            palPan.setColumns(number);
        }
        palPan.reDock();
    }

    /**
     * Handles the click on Color Change item.
     */
    private void clrChangeClick() {
        if (correspButton != null) {
            palPan.changeColor(correspButton);
        }
    }

    /**
     * Handles the click on Default Palette item.
     */
    private void defaultClick() {
        palPan.setDefaultPalette();
    }

    /**
     * Shows or hides Color change menu item.
     * @param doShow set true to show the item, false to hide it.
     */
    public void showColorChange(boolean doShow) {
        sep1.setVisible(doShow);
        change.setVisible(doShow);
    }

    /**
     * Shows the corresponding color icon at the Color Change menu item.
     * @param clr the color
     */
    public void setColorIcon(Color clr) {
        Graphics g = icon.getGraphics();
        g.setColor(clr);
        g.fillRect(0, 0, 16, 16);
        change.setIcon(new ImageIcon(icon));
    }

    /**
     * Gets the current docked position value and shows icon at the proper item.
     */
    private void updatePosIcons() {
        switch (palPan.getDockedTo()) {
            case 0:
                top.setIcon(Options.cbIconSelected);
                bottom.setIcon(null);
                left.setIcon(null);
                right.setIcon(null);
                updateLinesIcons(palPan.getRows());
                break;
            case 1:
                top.setIcon(null);
                bottom.setIcon(Options.cbIconSelected);
                left.setIcon(null);
                right.setIcon(null);
                updateLinesIcons(palPan.getRows());
                break;
            case 2:
                top.setIcon(null);
                bottom.setIcon(null);
                left.setIcon(Options.cbIconSelected);
                right.setIcon(null);
                updateLinesIcons(palPan.getCols());
                break;
            case 3:
                top.setIcon(null);
                bottom.setIcon(null);
                left.setIcon(null);
                right.setIcon(Options.cbIconSelected);
                updateLinesIcons(palPan.getCols());
                break;
        }
    }

    /**
     * Gets the current lines (rows, cols) value and shows icon at the proper item.
     * @param number number of lines
     */
    private void updateLinesIcons(int number) {
        switch (number) {
            case 1:
                lines1.setIcon(Options.cbIconSelected);
                lines2.setIcon(null);
                lines3.setIcon(null);
                break;
            case 2:
                lines1.setIcon(null);
                lines2.setIcon(Options.cbIconSelected);
                lines3.setIcon(null);
                break;
            case 3:
                lines1.setIcon(null);
                lines2.setIcon(null);
                lines3.setIcon(Options.cbIconSelected);
                break;
        }
    }

    /**
     * Updates menu captions if the application's language has been changed.
     */
    public void updateLanguage() {
        pal.setText(ResStrings.getString("strPalette"));
        pos.setText(ResStrings.getString("strPosition"));
        top.setText(ResStrings.getString("strTop"));
        bottom.setText(ResStrings.getString("strBottom"));
        left.setText(ResStrings.getString("strLeft"));
        right.setText(ResStrings.getString("strRight"));
        lines.setText(ResStrings.getString(
                (palPan.getDockedTo() < 2) ? "strRows" : "strCols"));
        change.setText(ResStrings.getString("strChangeColor"));
        def.setText(ResStrings.getString("strDefaultPalette"));
    }
}
