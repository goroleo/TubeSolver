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
import static gui.MainFrame.toolPan;
import core.ResStrings;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.border.Border;

public class ToolMenu extends JPopupMenu {

    private final JMenu pos;
    private final JMenuItem top;
    private final JMenuItem bottom;
    private final JMenuItem left;
    private final JMenuItem right;

    private final JMenu align;
    private final JMenuItem begin;
    private final JMenuItem center;
    private final JMenuItem end;

    private final Border border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.gray),
            BorderFactory.createEmptyBorder(2, 2, 2, 2));
    private final Border itemBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 0, 0, 0),
            BorderFactory.createEmptyBorder(3, 3, 3, 3));

    public ToolMenu() {

        setBackground(Palette.dialogColor);
        setForeground(Color.white);
        setBorder(border);

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

    private JMenu addMenu(JMenu parent, String text) {
        JMenu menu = new JMenu(text);
        menu.setBackground(Palette.dialogColor);
        menu.setForeground(Color.white);
        menu.setBorder(itemBorder);
        menu.setFont(menu.getFont().deriveFont(0));
        menu.getPopupMenu().setBackground(Palette.dialogColor);
        menu.getPopupMenu().setForeground(Color.white);
        menu.getPopupMenu().setBorder(border);
        if (parent != null) {
            parent.add(menu);
        } else {
            this.add(menu);
        }
        return menu;
    }

    private JMenuItem addMenuItem(JMenu parent, String text) {
        JMenuItem menu = new JMenuItem(text);
        menu.setBackground(Palette.dialogColor);
        menu.setForeground(Color.white);
        menu.setBorder(itemBorder);
        menu.setIcon(null);
        menu.setFont(menu.getFont().deriveFont(0));
        if (parent != null) {
            parent.add(menu);
        } else {
            this.add(menu);
        }
        return menu;
    }

    private JSeparator addSeparator(JMenu parent) {
        JSeparator sep = new JSeparator();
        sep.setBackground(Palette.dialogColor);
        sep.setForeground(Color.gray);
        if (parent != null) {
            parent.add(sep);
        } else {
            this.add(sep);
        }
        return sep;
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
