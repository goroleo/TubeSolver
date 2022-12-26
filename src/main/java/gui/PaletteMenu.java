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

public class PaletteMenu extends LPopupMenu {

    private final JMenu pos;
    private final JMenuItem top;
    private final JMenuItem bottom;
    private final JMenuItem left;
    private final JMenuItem right;

    private final JMenu lines;
    private final JMenuItem lines1;
    private final JMenuItem lines2;
    private final JMenuItem lines3;

    private final JSeparator sep1;
    private final JMenuItem change;

    private final JMenuItem def;

    private ColorButton correspButton;
    
    private final BufferedImage icon = new BufferedImage(16, 16, 1);

    public PaletteMenu() {
        super();

        JMenuItem pal = addMenuItem(null, ResStrings.getString("strPalette"));
        pal.setFont(pal.getFont().deriveFont(1));
        pal.setFont(pal.getFont().deriveFont(13f));
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
            // save palette
            def = addMenuItem(null, ResStrings.getString("strDefaultPalette"));
            def.addActionListener((ActionEvent e) -> defaultClick());
        }
    }

    private void positionClick(int number) {
        palPan.setDockedTo(number);
        updateIcons();
    }

    private void linesClick(int number) {
        if (palPan.getDockedTo() < 2) {
            palPan.setRows(number);
        } else {
            palPan.setColumns(number);
        }
        palPan.reDock();
        updateLinesIcons(number);
    }

    private void clrChangeClick() {
        if (correspButton != null) {
            palPan.changeColor(correspButton);
        }
    }

    private void defaultClick() {
        palPan.setDefaultPalette();
    }

    public void showColorChange(boolean doShow) {
        sep1.setVisible(doShow);
        change.setVisible(doShow);
    }

    public void setColorIcon(Color clr) {
        Graphics g = icon.getGraphics();
        g.setColor(clr);
        g.fillRect(0, 0, 16, 16);
        change.setIcon(new ImageIcon(icon));
    }

    private void updateIcons() {
        top.setIcon(null);
        bottom.setIcon(null);
        left.setIcon(null);
        right.setIcon(null);

        switch (palPan.getDockedTo()) {
            case 0:
                top.setIcon(Options.cbIconSelected);
                lines.setText(ResStrings.getString("strRows"));
                updateLinesIcons(palPan.getRows());
                break;
            case 1:
                bottom.setIcon(Options.cbIconSelected);
                lines.setText(ResStrings.getString("strRows"));
                updateLinesIcons(palPan.getRows());
                break;
            case 2:
                left.setIcon(Options.cbIconSelected);
                lines.setText(ResStrings.getString("strCols"));
                updateLinesIcons(palPan.getCols());
                break;
            case 3:
                right.setIcon(Options.cbIconSelected);
                lines.setText(ResStrings.getString("strCols"));
                updateLinesIcons(palPan.getCols());
                break;
        }
    }

    private void updateLinesIcons(int number) {
        lines1.setIcon(null);
        lines2.setIcon(null);
        lines3.setIcon(null);
        switch (number) {
            case 1:
                lines1.setIcon(Options.cbIconSelected);
                break;
            case 2:
                lines2.setIcon(Options.cbIconSelected);
                break;
            case 3:
                lines3.setIcon(Options.cbIconSelected);
                break;
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
        updateIcons();
        super.show(invoker, x, y);
    }

}
