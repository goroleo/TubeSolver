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

/**
 * The popup menu for the Tubes Board Panel.
 * @see BoardPanel
 * @see ColorTube
 */
public class BoardMenu extends LPopupMenu {

///////////////////////////////////////////////////////////////////////////
//
//               * Fields / variables *
//
///////////////////////////////////////////////////////////////////////////

    /** This menu caption. */
    private final JMenuItem board;

    /** Docked position menu item. */
    private final JMenu pos;

    /** Position menu item: docked to the center of the main frame. */
    private final JMenuItem center;

    /** Position menu item: docked to top. */
    private final JMenuItem top;

    /** Position menu item: docked to bottom. */
    private final JMenuItem bottom;

    /** Position menu item: docked to left. */
    private final JMenuItem left;

    /** Position menu item: docked to right. */
    private final JMenuItem right;

    /** Lines menu item (number of rows of color tubes). */
    private final JMenu lines;

    /** lines menu item: 1 line. */
    private final JMenuItem lines1;

    /** lines menu item: 2 lines. */
    private final JMenuItem lines2;

    /** lines menu item: 3 lines. */
    private final JMenuItem lines3;

    /** Clear current tube menu item. Used at FILL_MODE.
     * @see #correspTube
     */
    public final JMenuItem clear;

    /** Clear All tubes menu item. Used at FILL_MODE. */
    private final JMenuItem clearAll;

    /** Undo one move menu item. Used at PLAY_MODE and ASSIST_MODE. */
    private final JMenuItem undo;

    /** Undo all moves (start the game again) menu item. Used at PLAY_MODE and ASSIST_MODE. */
    private final JMenuItem start;

    /** Menu separator. */
    private final JSeparator sep2;

    /** Solve the game menu item. Used at PLAY_MODE. */
    private final JMenuItem solve;

    /** A pointer to corresponding tube. Used at FILL_MODE to Clear this tube.
     * @see #clear
     */
    private ColorTube correspTube;

///////////////////////////////////////////////////////////////////////////
//
//               * Routines *
//
///////////////////////////////////////////////////////////////////////////

    /** The constructor of the Board menu. Creates the menu and adds items.  */
    @SuppressWarnings("MagicConstant")
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

    /** Handles the click on position items. */
    private void positionClick(int number) {
        MainFrame.tubesPan.setDockedTo(number);
        updatePosIcons();
    }

    /** Handles the click on lines items. */
    private void linesClick(int number) {
        MainFrame.tubesPan.setRows(number);
        MainFrame.tubesPan.reDock();
        updateLinesIcons();
    }

    /** Handles the click on Clear this tube item. */
    private void clearClick() {
        if (correspTube != null) {
            Main.frame.clearTube(correspTube);
        }
    }

    /** Handles the click on Clear All tubes item. */
    private void clearAllClick() {
        Main.frame.clearAllTubes();
    }

    /** Handles the click on Undo one move item. */
    private void undoClick() {
        MainFrame.tubesPan.undoMoveColor();
    }

    /** Handles the click on Undo All Moves (Start the game again) item. */
    private void startClick() {
        MainFrame.tubesPan.startAgain();
    }

    /** Handles the click on Solve item. */
    private void solveClick() { Main.frame.startSolve();  }

    /** Gets the current docked position value and shows icon at the proper item. */
    private void updatePosIcons() {
        switch (MainFrame.tubesPan.getDockedTo()) {
            case 0:
                center.setIcon(Options.cbIconSelected);
                top.setIcon(null);
                bottom.setIcon(null);
                left.setIcon(null);
                right.setIcon(null);
                break;
            case 1:
                center.setIcon(null);
                top.setIcon(Options.cbIconSelected);
                bottom.setIcon(null);
                left.setIcon(null);
                right.setIcon(null);
                break;
            case 2:
                center.setIcon(null);
                top.setIcon(null);
                bottom.setIcon(Options.cbIconSelected);
                left.setIcon(null);
                right.setIcon(null);
                break;
            case 3:
                center.setIcon(null);
                top.setIcon(null);
                bottom.setIcon(null);
                left.setIcon(Options.cbIconSelected);
                right.setIcon(null);
                break;
            case 4:
                center.setIcon(null);
                top.setIcon(null);
                bottom.setIcon(null);
                left.setIcon(null);
                right.setIcon(Options.cbIconSelected);
                break;
        }
    }

    /** Gets the current rows value and shows icon at the proper item.  */
    private void updateLinesIcons() {
        switch (MainFrame.tubesPan.getRows()) {
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

    /** Updates menu captions if the application's language has been changed. */
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
