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
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.border.Border;
import run.Main;

public class BoardMenu extends JPopupMenu {

    private final JMenu pos;
    private final JMenuItem center;
    private final JMenuItem top;
    private final JMenuItem bottom;
    private final JMenuItem left;
    private final JMenuItem right;

    private final JMenu lines;
    private final JMenuItem lines1;
    private final JMenuItem lines2;
    private final JMenuItem lines3;

//    private final JSeparator sep1;

    private final JMenuItem clear;
    private final JMenuItem clearAll;

    private final JMenuItem undo;
    private final JMenuItem start;

    private final JSeparator sep2;
    private final JMenuItem solve;

    private ColorTube correspTube;

    private final Border border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.gray),
            BorderFactory.createEmptyBorder(2, 2, 2, 2));
    private final Border itemBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 0, 0, 0),
            BorderFactory.createEmptyBorder(3, 3, 3, 3));

    public BoardMenu() {

        setBackground(Palette.dialogColor);
        setForeground(Color.white);
        setBorder(border);

        JMenuItem ct = addMenuItem(null, ResStrings.getString("strColorTubes"));
        ct.setFont(ct.getFont().deriveFont(1));
        ct.setFont(ct.getFont().deriveFont(13f));
        addSeparator(null);

        // position menu
        pos = addMenu(null, ResStrings.getString("strPosition"));
        {
            center = addMenuItem(pos, ResStrings.getString("strCenter"));
            center.addActionListener((ActionEvent e) -> positionClick(0));

            top = addMenuItem(pos, ResStrings.getString("strTop"));
            top.addActionListener((ActionEvent e) -> positionClick(1));

            bottom = addMenuItem(pos, ResStrings.getString("strBottom"));
            bottom.addActionListener((ActionEvent e) -> positionClick(2));

            left = addMenuItem(pos, ResStrings.getString("strLeft"));
            left.addActionListener((ActionEvent e) -> positionClick(3));

            right = addMenuItem(pos, ResStrings.getString("strRight"));
            right.addActionListener((ActionEvent e) -> positionClick(4));
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
        addSeparator(null);

        {
            clear = addMenuItem(null, ResStrings.getString("strClearTube"));
            clear.addActionListener((ActionEvent e) -> clearClick());

            clearAll = addMenuItem(null, ResStrings.getString("strClearAllTubes"));
            clearAll.addActionListener((ActionEvent e) -> clearAllClick());
        }

        {
            undo = addMenuItem(null, ResStrings.getString("strUndoMove"));
            undo.addActionListener((ActionEvent e) -> undoClick());

            start = addMenuItem(null, ResStrings.getString("strStartAgain"));
            start.addActionListener((ActionEvent e) -> startClick());

        }

        sep2 = addSeparator(null);
        solve = addMenuItem(null, ResStrings.getString("strSolve"));
        solve.addActionListener((ActionEvent e) -> solveClick());

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
        MainFrame.tubesPan.setDockedTo(number);
        updatePosIcons();
    }

    private void linesClick(int number) {
        MainFrame.tubesPan.setRows(number);
        MainFrame.tubesPan.reDock();
        updateLinesIcons();
    }

    private void clearClick() {
        if (correspTube != null) {
            MainFrame.tubesPan.clearTube(correspTube);
        }
    }

    private void clearAllClick() {
        MainFrame.tubesPan.clearTubes();
    }

    private void undoClick() {
        MainFrame.tubesPan.undoMoveColor();
    }

    private void startClick() {
        MainFrame.tubesPan.startAgain();
    }

    private void solveClick() {
        if (MainFrame.tubesPan.doSolve()) {
            Main.frame.startAssistMode();
        } else {
            Main.frame.endAssistMode();
            Main.frame.resumePlayMode();
        }
    }

    private void updatePosIcons() {
        center.setIcon(null);
        top.setIcon(null);
        bottom.setIcon(null);
        left.setIcon(null);
        right.setIcon(null);

        switch (MainFrame.tubesPan.getDockedTo()) {
            case 0:
                center.setIcon(Options.cbIconSelected);
                break;
            case 1:
                top.setIcon(Options.cbIconSelected);
                break;
            case 2:
                bottom.setIcon(Options.cbIconSelected);
                break;
            case 3:
                left.setIcon(Options.cbIconSelected);
                break;
            case 4:
                right.setIcon(Options.cbIconSelected);
                break;
        }
    }

    private void updateLinesIcons() {
        lines1.setIcon(null);
        lines2.setIcon(null);
        lines3.setIcon(null);
        switch (MainFrame.tubesPan.getRows()) {
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
        switch (MainFrame.gameMode) {
            case MainFrame.FILL_MODE:
                if (invoker instanceof ColorTube) {
                    correspTube = (ColorTube) invoker;
                    clear.setVisible(true);
                    clear.setEnabled(correspTube.getColorsCount() > 0);
                } else {
                    clear.setVisible(false);
                }
                clearAll.setVisible(true);
                undo.setVisible(false);
                start.setVisible(false);
                sep2.setVisible(false);
                solve.setVisible(false);
                break;
            case MainFrame.PLAY_MODE:
                clear.setVisible(false);
                clearAll.setVisible(false);
                undo.setVisible(true);
                undo.setEnabled(!MainFrame.gameMoves.isEmpty());
                start.setVisible(true);
                start.setEnabled(!MainFrame.gameMoves.isEmpty());
                sep2.setVisible(true);
                solve.setVisible(true);
                break;
            case MainFrame.ASSIST_MODE:
                clear.setVisible(false);
                clearAll.setVisible(false);
                undo.setVisible(true);
                undo.setEnabled(MainFrame.movesDone > 0);
                start.setVisible(true);
                start.setEnabled(MainFrame.movesDone > 0);
                sep2.setVisible(false);
                solve.setVisible(false);
                break;
            default:
                return; // don't show the menu
        }

        updatePosIcons();
        updateLinesIcons();
        super.show(invoker, x, y);
    }

}
