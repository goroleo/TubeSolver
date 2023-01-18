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
import run.Main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BoardMenu extends LPopupMenu {

    private final JMenuItem board;
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

    public final JMenuItem clear;
    private final JMenuItem clearAll;

    private final JMenuItem undo;
    private final JMenuItem start;

    private final JSeparator sep2;
    private final JMenuItem solve;

    private ColorTube correspTube;

    public BoardMenu() {

        super();

        board = addMenuItem(null, ResStrings.getString("strColorTubes"));
        board.setFont(board.getFont().deriveFont(1, 13f));
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
        Main.frame.doSolve();
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
        updateLanguage();
        super.show(invoker, x, y);
    }

    public void updateLanguage() {
        board.setText(ResStrings.getString("strColorTubes"));
        pos.setText(ResStrings.getString("strPosition"));
        center.setText(ResStrings.getString("strCenter"));
        top.setText(ResStrings.getString("strTop"));
        bottom.setText(ResStrings.getString("strBottom"));
        left.setText(ResStrings.getString("strLeft"));
        right.setText(ResStrings.getString("strRight"));
        lines.setText(ResStrings.getString("strRows"));
        clear.setText(ResStrings.getString("strClearTube"));
        clearAll.setText(ResStrings.getString("strClearAllTubes"));
        undo.setText(ResStrings.getString("strUndoMove"));
        start.setText(ResStrings.getString("strStartAgain"));
        solve.setText(ResStrings.getString("strSolve"));
    }
}
