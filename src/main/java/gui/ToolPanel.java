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
import dlg.OptionsDlg;
import dlg.PaletteDlg;
import dlg.StartDlg;
import lib.lButtons.LToolButton;
import lib.lOpenSaveDialog.LOpenSaveDialog;
import run.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The toolbar with action buttons: new, load, save, options, etc. The toolbar can be horizontal or vertical.
 * It can be docked to any edge of the main frame (left, right, top, bottom).
 */
public class ToolPanel extends JPanel {

///////////////////////////////////////////////////////////////////////////
//
//               * Fields / variables *
//
///////////////////////////////////////////////////////////////////////////

    /**
     * The space size between buttons and also between the button and the border of toolbar.
     * The <b>X</b> identifier means the coordinate in the same direction as the toolbar laying.
     */
    private final int spaceX = 10;

    /**
     * The space size between buttons and also between the button and the border of toolbar.
     * The <b>Y</b> identifier means the coordinate in the opposite direction as the toolbar laying.
     */
    private final int spaceY = 10;

    /**
     * The space size for the separator.
     */
    private final int sepX = 20;

    /**
     * Maximum buttons count. Used for memory allocation.
     */
    private final int buttonsMaxCount = 20;

    /**
     * The buttons array. I didn't want to name every button separately, so all of them are in the array.
     */
    private final LToolButton[] buttons = new LToolButton[buttonsMaxCount];

    /**
     * The count of buttons in the <i>buttons[]</i> array.
     */
    private int btnCount = 0;

    /**
     * Which edge of the MainFrame the toolbar is docked to: <ul>
     * <li>0 - top
     * <li>1 - bottom
     * <li>2 - left
     * <li>3 - right </ul>
     */
    private int docked = 2;

    /**
     * The alignment of buttons inside the toolbar: <ul>
     * <li>0 - begin of the toolbar (left/top)
     * <li>1 - center of the toolbar
     * <li>2 - end of the toolbar (right/bottom) </ul>
     */
    private int align = 0;

    /**
     * The popup menu.
     */
    private static final ToolMenu menu = new ToolMenu();

    /**
     * The current game mode used to show and hide specific buttons.
     */
    private int curMode = -1;

///////////////////////////////////////////////////////////////////////////
//
//               * Main routines *
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Create the toolbar with action buttons. Create and add buttons to the toolbar. Add listeners.
     * Update buttons position. Update toolbar position and direction.
     */
    public ToolPanel() {
        LToolButton tb;
        setLayout(null);
        setBackground(Palette.dialogColor);

        // buttons[0] "Start new game"
        tb = addNewButton("new", ResStrings.getString("strStartGame"));
        tb.addActionListener((ActionEvent e) -> newGameClick());

        // buttons[1] separator
        addSeparator();

        // buttons[2] "Load a game from file"
        tb = addNewButton("load", ResStrings.getString("strLoadGame"));
        tb.addActionListener((ActionEvent e) -> loadClick());

        // buttons[3] "Save the game to file"
        tb = addNewButton("save", ResStrings.getString("strSaveGame"));
        tb.addActionListener((ActionEvent e) -> saveClick());

        // buttons[4] separator
        addSeparator();

        // buttons[5] "Refresh" - @PLAY_MODE
        tb = addNewButton("refresh", ResStrings.getString("strRefresh"));
        tb.addActionListener((ActionEvent e) -> refreshClick());

        // buttons[6] "Solve" - @PLAY_MODE
        tb = addNewButton("solve", ResStrings.getString("strSolve"));
        tb.addActionListener((ActionEvent e) -> solveClick());

        // buttons[7] separator
        addSeparator();

        // buttons[8] "Undo the last move" - @PLAY_MODE
        tb = addNewButton("undo", ResStrings.getString("strUndoMove"));
        tb.addActionListener((ActionEvent e) -> undoClick());

        // buttons[9] "Restart the game from the beginning" - @PLAY_MODE
        tb = addNewButton("replay", ResStrings.getString("strStartAgain"));
        tb.addActionListener((ActionEvent e) -> restartClick());

        // buttons[10] "Clear all tubes" - @FILL_MODE
        tb = addNewButton("cleartubes", ResStrings.getString("strClearAllTubes"));
        tb.addActionListener((ActionEvent e) -> clearTubesClick());

        // buttons[11] "Auto (random) fill the rest colors" - @FILL_MODE
        tb = addNewButton("replay", ResStrings.getString("strAutoFill"));
        tb.addActionListener((ActionEvent e) -> autoFillClick());

        // buttons[12] separator
        addSeparator();

        // buttons[13] "Change palette"
        tb = addNewButton("palette", ResStrings.getString("strChangePalette"));
        tb.addActionListener((ActionEvent e) -> paletteClick());

        // buttons[14] "Application options"
        tb = addNewButton("options", ResStrings.getString("strOptions"));
        tb.addActionListener((ActionEvent e) -> optionsClick());

        // buttons[15] separator
        addSeparator();

        // buttons[16] "Exit - close the application"
        tb = addNewButton("exit", ResStrings.getString("strExit"));
        tb.addActionListener((ActionEvent e) -> exitClick());

        updateButtons(0);
        addPopupMenu(this);

        if (Options.menuDockedTo >= 0 && Options.menuDockedTo <= 3
                && Options.menuAlign >= 0 && Options.menuAlign <= 1) {
            docked = Options.menuDockedTo;
            align = Options.menuAlign;
        }
    }

    /**
     * This routine creates and adds the new action button.
     *
     * @param imgFSuffix the suffix of the image file name stored at <i>resources</i> folder.
     * @param hint       the button's tooltip text
     * @return a new button
     */
    public LToolButton addNewButton(String imgFSuffix, String hint) {
        LToolButton btn = new LToolButton(this, "btnTool32", imgFSuffix);
        btn.setColorEnabled(new Color(0xb8cfe5));
        btn.setColorDisabled(new Color(0x737c85));
        btn.setColorHover(new Color(0x222222));
        btn.setColorPressed(new Color(0xaaaaaa));
        btn.setFocusable(false);

        btn.setToolTipBackground(Palette.dialogColor);
        btn.setToolTipForeground(Color.WHITE);
        btn.setToolTipBorder(new Color(0xb8cfe5));
        btn.setToolTipText(hint);

        addPopupMenu(btn);

        buttons[btnCount] = btn;
        add(btn);
        btnCount++;
        return btn;
    }

    /**
     * Adds the separator between two buttons
     */
    public void addSeparator() {
        buttons[btnCount] = null;
        btnCount++;
    }

    /**
     * Adds the popup menu to the Toolbar and all the buttons.
     *
     * @param comp the Swing component to add the popup menu
     */
    public void addPopupMenu(JComponent comp) {
        comp.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    menu.show(e.getComponent(),
                            e.getX(), e.getY());
                }
            }
        });
    }

    /**
     * Show and hide specific buttons depends on the current game mode
     */
    public void updateButtons() {
        updateButtons(curMode);
    }

    /**
     * Show and hide specific buttons depends on the current game mode
     *
     * @param gameMode current game mode
     * @see MainFrame#gameMode
     */
    public void updateButtons(int gameMode) {
        switch (gameMode) {
            case MainFrame.FILL_MODE:
                buttons[3].setEnabled(true);  // 3 - save button
                buttons[5].setVisible(false); // 5 - refresh board
                buttons[6].setVisible(false); // 6 - solve button
                buttons[8].setVisible(false); // 8 - undo move
                buttons[9].setVisible(false); // 9 - replay / start again
                buttons[10].setVisible(true); // 10 - clear all tubes 
                buttons[10].setEnabled(Palette.usedColors.getAllUsedColors() > 0);
                buttons[11].setVisible(true); // 11 - auto fill button
                break;
            case MainFrame.PLAY_MODE:
            case MainFrame.ASSIST_MODE:
                buttons[3].setEnabled(true); // 3 - save button
                buttons[5].setVisible(true); // 5 - refresh board
                buttons[6].setVisible(true); // 6 - solve button
                buttons[6].setEnabled(gameMode != MainFrame.ASSIST_MODE);
                buttons[8].setVisible(true); // 8 - undo move
                buttons[8].setEnabled(MainFrame.movesDone > 0);
                buttons[9].setVisible(true); // 9 - replay / start again
                buttons[9].setEnabled(MainFrame.movesDone > 0);
                buttons[10].setVisible(false); // 10 - clear all tubes 
                buttons[11].setVisible(false); // 11 - auto fill button
                break;
            default:
                buttons[3].setEnabled(false); // 3 - save button
                buttons[5].setVisible(false); // 5 - refresh board
                buttons[6].setVisible(false); // 6 - solve button
                buttons[8].setVisible(false); // 8 - undo move
                buttons[9].setVisible(false); // 9 - replay / start again
                buttons[10].setVisible(false); // 10 - clear all tubes 
                buttons[11].setVisible(false); // 11 - auto fill button
        }
        if (curMode != gameMode) {
            updateAllButtonsPos();
            curMode = gameMode;
        }
    }

///////////////////////////////////////////////////////////////////////////
//
//               * Actions! *
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Click the New Game button
     */
    public void newGameClick() {
        Main.frame.saveTempGame();
        StartDlg dlg = new StartDlg(Main.frame);
        dlg.setVisible(true);
    }

    /**
     * Click the Load Game button
     */
    public void loadClick() {
        LOpenSaveDialog os = new LOpenSaveDialog(Main.frame);
        String fileName = os.showOpenDialog();
        if (!"".equals(fileName)) {
            if (Main.frame.loadGame(fileName)) {
                switch (MainFrame.gameMode) {
                    case MainFrame.PLAY_MODE:
                        Main.frame.startPlayMode();
                        break;
                    case MainFrame.ASSIST_MODE:
                        Main.frame.startAssistMode();
                        break;
                    case MainFrame.FILL_MODE:
                        Main.frame.resumeManualFillMode();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Click the Save Game button
     */
    public void saveClick() {
        Main.frame.saveGameAs();
    }

    /**
     * Click the Undo Move button
     */
    public void undoClick() {
        MainFrame.tubesPan.undoMoveColor();
    }

    /**
     * Click the Restart Game button
     */
    public void restartClick() {
        MainFrame.tubesPan.startAgain();
    }

    /**
     * Click the Refresh Board button
     */
    public void refreshClick() {
        if (MainFrame.palPan != null) {
            MainFrame.palPan.updateColors();
        }
        if (MainFrame.tubesPan != null) {
            MainFrame.tubesPan.updateColors();
        }
    }

    /**
     * Click the Solve Game button
     */
    public void solveClick() {
        Main.frame.doSolve();
    }

    /**
     * Click the Change Palette button
     */
    public void paletteClick() {
        PaletteDlg pd = new PaletteDlg(Main.frame);
        pd.setVisible(true);
    }

    /**
     * Click the Clear All Tubes button
     */
    public void clearTubesClick() {
        MainFrame.tubesPan.clearTubes();
    }

    /**
     * Click the Auto Fill button
     */
    public void autoFillClick() {
        Main.frame.autoFillTheRest();
    }

    /**
     * Click the Options button
     */
    public void optionsClick() {
        OptionsDlg od = new OptionsDlg(Main.frame);
        od.setVisible(true);
    }

    /**
     * Click the Exit Game button
     */
    public void exitClick() {
        Main.frame.closeFrame();
    }

///////////////////////////////////////////////////////////////////////////
//
//               * Docked and Alignment *
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Gets the edge of the MainFrame the toolbar is docked to.
     *
     * @return current dockedTo value
     * @see #docked
     */
    public int getDockedTo() {
        return docked;
    }

    /**
     * Sets the edge of the MainFrame the toolbar is docked to.
     *
     * @param newDock the new docked value
     * @see #docked
     */
    public void setDockedTo(int newDock) {
        docked = newDock;
        resize();
        Main.frame.updatePanelsPos();
    }

    /**
     * Gets the alignment of the toolbar's buttons.
     *
     * @return current align value
     * @see #align
     */
    public int getAlignment() {
        return align;
    }

    /**
     * Sets the alignment of the toolbar's buttons.
     *
     * @param newAlign new align value
     * @see #align
     */
    public void setAlignment(int newAlign) {
        align = newAlign;
        updateAllButtonsPos();
    }

///////////////////////////////////////////////////////////////////////////
//
//               * Size and position routines *
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Resizes the toolbar. The toolbar is a one row buttons panel at the whole length of the main frame,
     * depends on edge it docked to. So resizing the main frame is the reason to resize the toolbar.
     */
    public void resize() {
        if (docked < 2) { // horizontal
            setSize(Main.frame.getClientArea().width,
                    getToolbarY());
        } else { // vertical
            setSize(getToolbarY(),
                    Main.frame.getClientArea().height);
        }
        relocation();
    }

    /**
     * Relocation the toolbar.
     */
    public void relocation() {
        switch (docked) {
            case 1: // bottom
                setLocation(0, Main.frame.getClientArea().height - getToolbarY());
                break;
            case 3: // right
                setLocation(Main.frame.getClientArea().width - getToolbarY(), 0);
                break;
            default: // top, left
                setLocation(0, 0);
        }
        updateAllButtonsPos();
    }

    /**
     * Gets the length of all showed buttons inside the toolbar.
     *
     * @return the length
     */
    public int getButtonsLength() {
        return getButtonX(btnCount);
    }

    /**
     * Gets the Y size of the toolbar. The <b>Y</b> identifier means the coordinate in the opposite direction as the toolbar laying.
     *
     * @return toolbar height (if horizontal) of width (if vertical).
     */
    public int getToolbarY() {
        int btnSize;
        if (btnCount == 0) {
            btnSize = 0;
        } else if (docked < 2) { // horizontal
            btnSize = buttons[0].getHeight();
        } else { // vertical
            btnSize = buttons[0].getWidth();
        }
        return spaceY * 2 + btnSize;
    }

    /**
     * Gets the X coordinate of the specified toolbar's button. The <b>X</b> identifier means the coordinate in the same direction as the toolbar laying.
     *
     * @param btnNum number of the button. If (btnNum == btnCount) this returns the whole length of the buttons.
     * @return X coordinate of the toolbar's button
     */
    private int getButtonX(int btnNum) {
        boolean wasSkipped = false;
        if (btnNum > btnCount) {
            return 0;
        }
        int result = spaceX;
        for (int i = 0; i < btnNum; i++) {
            if (buttons[i] != null && buttons[i].isVisible()) {
                result += buttons[i].getWidth();
                wasSkipped = false;
            } else if (buttons[i] == null && !wasSkipped) {
                result += sepX;
                wasSkipped = true;
            }
        }
        if (btnNum == btnCount) {
            result += spaceX;
        }
        return result;
    }

    /**
     * Update position of all toolbar buttons.
     */
    private void updateAllButtonsPos() {
        if (Main.frame != null) {
            int startX;

            int panLength = (docked < 2)
                    ? Main.frame.getClientArea().width
                    : Main.frame.getClientArea().height;

            switch (align) {
                case 1: // center
                    startX = (panLength - getButtonsLength()) / 2;
                    break;
                case 2: // end (right / bottom)
                    startX = panLength - getButtonsLength();
                    break;
                default: // begin (top / left)
                    startX = 0;
                    break;
            }

            for (int i = 0; i < buttons.length; i++) {
                if (buttons[i] != null) {
                    if (docked < 2) { // horizontal
                        buttons[i].setLocation(getButtonX(i) + startX, spaceY);
                    } else { // vertical
                        buttons[i].setLocation(spaceY, getButtonX(i) + startX);
                    }
                }
            }
        }
    }

///////////////////////////////////////////////////////////////////////////
//
//               * Other routines *
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Updates button tooltips if the application's language has been changed.
     */
    public void updateLanguage() {
        buttons[0].setToolTipText(ResStrings.getString("strStartGame"));
        buttons[2].setToolTipText(ResStrings.getString("strLoadGame"));
        buttons[3].setToolTipText(ResStrings.getString("strSaveGame"));
        buttons[5].setToolTipText(ResStrings.getString("strRefresh"));
        buttons[6].setToolTipText(ResStrings.getString("strSolve"));
        buttons[8].setToolTipText(ResStrings.getString("strUndoMove"));
        buttons[9].setToolTipText(ResStrings.getString("strStartAgain"));
        buttons[10].setToolTipText(ResStrings.getString("strClearAllTubes"));
        buttons[11].setToolTipText(ResStrings.getString("strAutoFill"));
        buttons[13].setToolTipText(ResStrings.getString("strChangePalette"));
        buttons[14].setToolTipText(ResStrings.getString("strOptions"));
        buttons[16].setToolTipText(ResStrings.getString("strExit"));
    }

    /**
     * Saves Toolbar properties, docked and alignment.
     */
    public void saveOptions() {
        Options.menuDockedTo = docked;
        Options.menuAlign = align;
    }

}
