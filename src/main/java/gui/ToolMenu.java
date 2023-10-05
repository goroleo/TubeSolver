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

import static gui.MainFrame.toolPanel;

/**
 * The popup menu for the toolbar / tool panel.
 */
public class ToolMenu extends LPopupMenu {

///////////////////////////////////////////////////////////////////////////
//
//               * Fields / variables *
//
///////////////////////////////////////////////////////////////////////////

    /**
     * This menu caption.
     */
    private final JMenuItem tools;

    /**
     * Docked position menu item.
     */
    private final JMenu pos;

    /**
     * Position menu item: docked to top.
     */
    private final JMenuItem top;

    /**
     * Position menu item: docked to bottom.
     */
    private final JMenuItem bottom;

    /**
     * Position menu item: docked to left.
     */
    private final JMenuItem left;

    /**
     * Position menu item: docked to right.
     */
    private final JMenuItem right;

    /**
     * Alignment menu item.
     */
    private final JMenu align;

    /**
     * Alignment menu item: align to begin (top / left).
     */
    private final JMenuItem begin;

    /**
     * Alignment menu item: align to center.
     */
    private final JMenuItem center;

    /**
     * Alignment menu item: align to end (bottom / right).
     */
    private final JMenuItem end;

///////////////////////////////////////////////////////////////////////////
//
//               * Routines *
//
///////////////////////////////////////////////////////////////////////////

    /**
     * The constructor. Creates the Tool menu and adds menu items.
     */
    public ToolMenu() {
        super();

        tools = addMenuItem(null, ResStrings.getString("strToolbar"));
        tools.setFont(tools.getFont().deriveFont(Font.BOLD, 13f));

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

        // alignment menu
        align = addMenu(null, ResStrings.getString("strAlignment"));
        {
            begin = addMenuItem(align, ResStrings.getString("strBegin"));
            begin.addActionListener((ActionEvent e) -> alignClick(0));
            center = addMenuItem(align, ResStrings.getString("strCenter"));
            center.addActionListener((ActionEvent e) -> alignClick(1));
            end = addMenuItem(align, ResStrings.getString("strEnd"));
            end.addActionListener((ActionEvent e) -> alignClick(2));
        }
    }

    @Override
    public void show(Component invoker, int x, int y) {
        updatePosIcons();
        updateAlignIcons();
        updateLanguage();
        super.show(invoker, x, y);
    }

    /**
     * Handles the click on position items.
     */
    private void positionClick(int number) {
        toolPanel.setDockedTo(number);
        updatePosIcons();
    }

    /**
     * Handles the click on alignment items.
     */
    private void alignClick(int number) {
        toolPanel.setAlignment(number);
        updateAlignIcons();
    }

    /**
     * Gets the current docked position value and shows icon at the proper item.
     */
    private void updatePosIcons() {
        switch (toolPanel.getDockedTo()) {
            case 0:
                top.setIcon(Options.cbIconSelected);
                bottom.setIcon(null);
                left.setIcon(null);
                right.setIcon(null);
                break;
            case 1:
                top.setIcon(null);
                bottom.setIcon(Options.cbIconSelected);
                left.setIcon(null);
                right.setIcon(null);
                break;
            case 2:
                top.setIcon(null);
                bottom.setIcon(null);
                left.setIcon(Options.cbIconSelected);
                right.setIcon(null);
                break;
            case 3:
                top.setIcon(null);
                bottom.setIcon(null);
                left.setIcon(null);
                right.setIcon(Options.cbIconSelected);
                break;
        }
    }

    /**
     * Gets the current alignment value and shows icon at the proper item.
     */
    private void updateAlignIcons() {
        switch (toolPanel.getAlignment()) {
            case 0:
                begin.setIcon(Options.cbIconSelected);
                center.setIcon(null);
                end.setIcon(null);
                break;
            case 1:
                begin.setIcon(null);
                center.setIcon(Options.cbIconSelected);
                end.setIcon(null);
                break;
            case 2:
                begin.setIcon(null);
                center.setIcon(null);
                end.setIcon(Options.cbIconSelected);
                break;
        }
    }

    /**
     * Updates menu captions if the application's language has been changed.
     */
    public void updateLanguage() {
        tools.setText(ResStrings.getString("strToolbar"));
        pos.setText(ResStrings.getString("strPosition"));
        top.setText(ResStrings.getString("strTop"));
        bottom.setText(ResStrings.getString("strBottom"));
        left.setText(ResStrings.getString("strLeft"));
        right.setText(ResStrings.getString("strRight"));
        align.setText(ResStrings.getString("strAlignment"));
        begin.setText(ResStrings.getString("strBegin"));
        center.setText(ResStrings.getString("strCenter"));
        end.setText(ResStrings.getString("strEnd"));
    }
}
