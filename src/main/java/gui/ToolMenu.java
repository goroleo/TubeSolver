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

import static gui.MainFrame.toolPan;

public class ToolMenu extends LPopupMenu {

    private final JMenu pos;
    private final JMenuItem top;
    private final JMenuItem bottom;
    private final JMenuItem left;
    private final JMenuItem right;

    private final JMenu align;
    private final JMenuItem begin;
    private final JMenuItem center;
    private final JMenuItem end;

    public ToolMenu() {
        super();

        JMenuItem ct = addMenuItem(null, ResStrings.getString("strToolbar"));
        ct.setFont(ct.getFont().deriveFont(1));
        ct.setFont(ct.getFont().deriveFont(13f));
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
        align = addMenu(null, ResStrings.getString("strAlignment"));
        {
            begin = addMenuItem(align, ResStrings.getString("strBegin"));
            begin.addActionListener((ActionEvent e) -> alignClick(0));
            center = addMenuItem(align,ResStrings.getString("strCenter"));
            center.addActionListener((ActionEvent e) -> alignClick(1));
            end = addMenuItem(align, ResStrings.getString("strEnd"));
            end.addActionListener((ActionEvent e) -> alignClick(2));
        }
    }

    private void positionClick(int number) {
        toolPan.setDockedTo(number);
        updatePosIcons();
    }

    private void alignClick(int number) {
        toolPan.setAlignment(number);
        updateAlignIcons();
    }

    private void updatePosIcons() {
        top.setIcon(null);
        bottom.setIcon(null);
        left.setIcon(null);
        right.setIcon(null);

        switch (toolPan.getDockedTo()) {
            case 0:
                top.setIcon(Options.cbIconSelected);
                break;
            case 1:
                bottom.setIcon(Options.cbIconSelected);
                break;
            case 2:
                left.setIcon(Options.cbIconSelected);
                break;
            case 3:
                right.setIcon(Options.cbIconSelected);
                break;
        }
    }

    private void updateAlignIcons() {
        begin.setIcon(null);
        center.setIcon(null);
        end.setIcon(null);
        switch (toolPan.getAlignment()) {
            case 0:
                begin.setIcon(Options.cbIconSelected);
                break;
            case 1:
                center.setIcon(Options.cbIconSelected);
                break;
            case 2:
                end.setIcon(Options.cbIconSelected);
                break;
        }
    }

    @Override
    public void show(Component invoker, int x, int y) {
        updatePosIcons();
        updateAlignIcons();
        super.show(invoker, x, y);
    }

}
