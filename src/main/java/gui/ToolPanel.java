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
import dlg.MessageDlg;
import dlg.OptionsDlg;
import dlg.PaletteDlg;
import dlg.StartDlg;
import lib.lButtons.LToolButton;
import lib.lOpenSaveDialog.LOpenSaveDialog;
import run.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The toolbar with action buttons: new, load, save, options, etc. The toolbar can be horizontal or vertical.
 * It can be docked to any edge of the main frame (left, right, top, bottom).
 */
@SuppressWarnings("FieldCanBeLocal")
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
        tb = addNewButton("new", ResStrings.getString("strStartGame") + " (Ctlr+N)");
        tb.addActionListener((ActionEvent e) -> newGameClick());
        registerKeyboardAction(
                (ActionEvent e) -> newGameClick(),
                KeyStroke.getKeyStroke('N', InputEvent.CTRL_DOWN_MASK), // VK_N + MASK_CTRL
                JComponent.WHEN_IN_FOCUSED_WINDOW); // WHEN_IN_FOCUSED_WINDOW

        // buttons[1] separator
        addSeparator();

        // buttons[2] "Load a game from file"
        tb = addNewButton("load", ResStrings.getString("strLoadGame") + " (Ctlr+L)");
        tb.addActionListener((ActionEvent e) -> loadClick());
        registerKeyboardAction(
                (ActionEvent e) -> loadClick(),
                KeyStroke.getKeyStroke('L', InputEvent.CTRL_DOWN_MASK), // VK_L + MASK_CTRL
                JComponent.WHEN_IN_FOCUSED_WINDOW); // WHEN_IN_FOCUSED_WINDOW

        // buttons[3] "Save the game to file"
        tb = addNewButton("save", ResStrings.getString("strSaveGame") + " (Ctlr+S)");
        tb.addActionListener((ActionEvent e) -> saveClick());
        registerKeyboardAction(
                (ActionEvent e) -> saveClick(),
                KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK), // VK_O + MASK_CTRL
                JComponent.WHEN_IN_FOCUSED_WINDOW); // WHEN_IN_FOCUSED_WINDOW

        // buttons[4] separator
        addSeparator();

        // buttons[5] "Refresh" - @PLAY_MODE
        tb = addNewButton("refresh", ResStrings.getString("strRefresh") + " (F5)");
        tb.addActionListener((ActionEvent e) -> refreshClick());
        registerKeyboardAction(
                (ActionEvent e) -> refreshClick(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0), // VK_F5
                JComponent.WHEN_IN_FOCUSED_WINDOW); // WHEN_IN_FOCUSED_WINDOW

        // buttons[6] "Solve" - @PLAY_MODE
        tb = addNewButton("solve", ResStrings.getString("strSolve") + " (F9)");
        tb.addActionListener((ActionEvent e) -> solveClick());
        registerKeyboardAction(
                (ActionEvent e) -> solveClick(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, 0), // VK_F9
                JComponent.WHEN_IN_FOCUSED_WINDOW); // WHEN_IN_FOCUSED_WINDOW

        // buttons[7] separator
        addSeparator();

        // buttons[8] "Undo the last move" - @PLAY_MODE
        tb = addNewButton("undo", ResStrings.getString("strUndoMove") + " (Ctlr+Z)");
        tb.addActionListener((ActionEvent e) -> undoClick());
        registerKeyboardAction(
                (ActionEvent e) -> undoClick(),
                KeyStroke.getKeyStroke('Z', InputEvent.CTRL_DOWN_MASK), // VK_O + MASK_CTRL
                JComponent.WHEN_IN_FOCUSED_WINDOW); // WHEN_IN_FOCUSED_WINDOW

        // buttons[9] "Restart the game from the beginning" - @PLAY_MODE
        tb = addNewButton("replay", ResStrings.getString("strStartAgain") + " (F2)");
        tb.addActionListener((ActionEvent e) -> restartClick());
        registerKeyboardAction(
                (ActionEvent e) -> restartClick(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0), // VK_F2
                JComponent.WHEN_IN_FOCUSED_WINDOW); // WHEN_IN_FOCUSED_WINDOW

        // buttons[10] "Clear all tubes" - @FILL_MODE
        tb = addNewButton("cleartubes", ResStrings.getString("strClearAllTubes"));
        tb.addActionListener((ActionEvent e) -> clearTubesClick());

        // buttons[11] "Auto (random) fill the rest colors" - @FILL_MODE
        tb = addNewButton("replay", ResStrings.getString("strAutoFill"));
        tb.addActionListener((ActionEvent e) -> autoFillClick());

        // buttons[12] separator
        addSeparator();

        // buttons[13] "Change palette"
        tb = addNewButton("palette", ResStrings.getString("strChangePalette") + " (Ctrl+P)");
        tb.addActionListener((ActionEvent e) -> paletteClick());
        registerKeyboardAction(
                (ActionEvent e) -> paletteClick(),
                KeyStroke.getKeyStroke('P', InputEvent.CTRL_DOWN_MASK), // VK_O + MASK_CTRL
                JComponent.WHEN_IN_FOCUSED_WINDOW); // WHEN_IN_FOCUSED_WINDOW

        // buttons[14] "Application options"
        tb = addNewButton("options", ResStrings.getString("strOptions") + " (Ctrl+O)");
        tb.addActionListener((ActionEvent e) -> optionsClick());
        registerKeyboardAction(
                (ActionEvent e) -> optionsClick(),
                KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK), // VK_O + MASK_CTRL
                JComponent.WHEN_IN_FOCUSED_WINDOW); // WHEN_IN_FOCUSED_WINDOW

        // buttons[15] separator
        addSeparator();

        // buttons[16] "Exit - close the application"
        tb = addNewButton("exit", ResStrings.getString("strExit") + " (Ctlr+X)");
        tb.addActionListener((ActionEvent e) -> exitClick());
        registerKeyboardAction(
                (ActionEvent e) -> exitClick(),
                KeyStroke.getKeyStroke('X', InputEvent.CTRL_DOWN_MASK), // VK_O + MASK_CTRL
                JComponent.WHEN_IN_FOCUSED_WINDOW); // WHEN_IN_FOCUSED_WINDOW

        updateButtons(0);
        addPopupMenu(this);

        if (Options.menuDockedTo >= 0 && Options.menuDockedTo <= 3
                && Options.menuAlignment >= 0 && Options.menuAlignment <= 1) {
            docked = Options.menuDockedTo;
            align = Options.menuAlignment;
        }
    }

    /**
     * This routine creates and adds the new action button.
     *
     * @param iconSuffix the suffix of the image file name stored at <i>/resources/img/</i> folder.
     * @param hint       the button's tooltip text
     * @return a new button
     * @see lib.lButtons.LToolButton
     */
    public LToolButton addNewButton(String iconSuffix, String hint) {
        LToolButton btn = new LToolButton(this, "btnTool32", iconSuffix);
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

        if (gameMode == MainFrame.BUSY_MODE) {
            for (int i = 0; i < btnCount; i++)
                if (buttons[i] != null)
                    buttons[i].setEnabled(false);
        } else {

            // these buttons are always visible & enabled
            buttons[0].setVisible(true); // 0 - new game
            buttons[0].setEnabled(true);
            buttons[2].setVisible(true); // 2 - load game
            buttons[2].setEnabled(true);
            buttons[13].setVisible(true); // 13 - options
            buttons[13].setEnabled(true);
            buttons[14].setVisible(true); // 14 - change palette
            buttons[14].setEnabled(true);
            buttons[16].setVisible(true); // 16 - exit
            buttons[16].setEnabled(true);

            if (gameMode == MainFrame.FILL_MODE) {
                buttons[3].setEnabled(true);  // 3 - save button
                buttons[5].setVisible(false); // 5 - refresh board
                buttons[6].setVisible(false); // 6 - solve button
                buttons[8].setVisible(false); // 8 - undo move
                buttons[9].setVisible(false); // 9 - replay / start again
                buttons[10].setVisible(true); // 10 - clear all tubes
                buttons[10].setEnabled(Palette.usedColors.getAllUsedColors() > 0);
                buttons[11].setVisible(true); // 11 - auto fill button
                buttons[11].setEnabled(true); // 11 - auto fill button

            } else if (gameMode == MainFrame.PLAY_MODE
                    || gameMode == MainFrame.ASSIST_MODE) {
                buttons[3].setEnabled(true); // 3 - save button
                buttons[5].setVisible(true); // 5 - refresh board
                buttons[5].setEnabled(true);
                buttons[6].setVisible(true); // 6 - solve button
                buttons[6].setEnabled(gameMode != MainFrame.ASSIST_MODE);
                buttons[8].setVisible(true); // 8 - undo move
                buttons[8].setEnabled(MainFrame.movesDone > 0);
                buttons[9].setVisible(true); // 9 - replay / start again
                buttons[9].setEnabled(MainFrame.movesDone > 0);
                buttons[10].setVisible(false); // 10 - clear all tubes
                buttons[11].setVisible(false); // 11 - auto fill button

            } else { // end game
                buttons[3].setEnabled(false); // 3 - save button
                buttons[5].setVisible(false); // 5 - refresh board
                buttons[6].setVisible(false); // 6 - solve button
                buttons[8].setVisible(false); // 8 - undo move
                buttons[9].setVisible(false); // 9 - replay / start again
                buttons[10].setVisible(false); // 10 - clear all tubes
                buttons[11].setVisible(false); // 11 - auto fill button
            }
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
     * Click the New Game button // buttons[0]
     */
    public void newGameClick() {
        if (buttons[0].isEnabled() && buttons[0].isVisible()) {
            if (MainFrame.gameMode != MainFrame.END_GAME)
                Main.frame.saveTempGame();
            StartDlg dlg = new StartDlg(Main.frame);
            dlg.setVisible(true);
        }
    }

    /**
     * Click the Load Game button // buttons [2]
     */
    public void loadClick() {
        if (buttons[2].isEnabled() && buttons[2].isVisible()) {
            Main.frame.setGameMode(MainFrame.BUSY_MODE);
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
                            Main.frame.resumeFillMode();
                            break;
                        default:
                            break;
                    }
                } else {
                    MessageDlg msgDlg = new MessageDlg(Main.frame,
                            ResStrings.getString("strCannotLoad"),
                            MessageDlg.BTN_OK);
                    msgDlg.setButtonsLayout(MessageDlg.BTN_LAYOUT_RIGHT);
                    msgDlg.setVisible(true);
                    Main.frame.setGameMode(MainFrame.prevMode);
                }
            } else {
                Main.frame.setGameMode(MainFrame.prevMode);
            }
        }
    }

    /**
     * Click the Save Game button // buttons[3]
     */
    public void saveClick() {
        if (buttons[3].isEnabled() && buttons[3].isVisible()) {
            Main.frame.saveGameAs();
        }
    }

    /**
     * Click the Refresh Board button // buttons[5]
     */
    public void refreshClick() {
        if (buttons[5].isEnabled() && buttons[5].isVisible()) {
            if (MainFrame.palettePanel != null) {
                MainFrame.palettePanel.updateColors();
            }
            if (MainFrame.tubesPanel != null) {
                MainFrame.tubesPanel.updateColors();
            }
        }
    }

    /**
     * Click the Solve Game button // buttons[6]
     */
    public void solveClick() {
        if (buttons[6].isEnabled() && buttons[6].isVisible()) {
            Main.frame.startSolve();
        }
    }

    /**
     * Click the Undo Move button // buttons[8]
     */
    public void undoClick() {
        if (buttons[8].isEnabled() && buttons[8].isVisible()) {
            MainFrame.tubesPanel.undoMoveColor();
        }
    }

    /**
     * Click the Restart Game button // buttons[9]
     */
    public void restartClick() {
        if (buttons[9].isEnabled() && buttons[9].isVisible()) {
            MainFrame.tubesPanel.startAgain();
        }
    }

    /**
     * Click the Clear All Tubes button // buttons[10]
     */
    public void clearTubesClick() {
        if (buttons[10].isEnabled() && buttons[10].isVisible()) {
            MainFrame.tubesPanel.clearTubes();
        }
    }

    /**
     * Click the Auto Fill button // buttons[11]
     */
    public void autoFillClick() {
        if (buttons[11].isEnabled() && buttons[11].isVisible()) {
            Main.frame.autoFillTheRest();
        }
    }

    /**
     * Click the Change Palette button // buttons[13]
     */
    public void paletteClick() {
        if (buttons[13].isEnabled() && buttons[13].isVisible()) {
            PaletteDlg pd = new PaletteDlg(Main.frame);
            pd.setVisible(true);
        }
    }

    /**
     * Click the Options button // buttons[14]
     */
    public void optionsClick() {
        if (buttons[14].isEnabled() && buttons[14].isVisible()) {
            OptionsDlg od = new OptionsDlg(Main.frame);
            od.setVisible(true);
        }
    }

    /**
     * Click the Exit Game button // buttons[16]
     */
    public void exitClick() {
        if (buttons[16].isEnabled() && buttons[16].isVisible()) {
            Main.frame.closeFrame();
        }
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

            int panelLength = (docked < 2)
                    ? Main.frame.getClientArea().width
                    : Main.frame.getClientArea().height;

            switch (align) {
                case 1: // center
                    startX = (panelLength - getButtonsLength()) / 2;
                    break;
                case 2: // end (right / bottom)
                    startX = panelLength - getButtonsLength();
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
                        //noinspection SuspiciousNameCombination
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
        buttons[0].setToolTipText(ResStrings.getString("strStartGame") + " (Ctlr+N)");
        buttons[2].setToolTipText(ResStrings.getString("strLoadGame") + " (Ctlr+L)");
        buttons[3].setToolTipText(ResStrings.getString("strSaveGame") + " (Ctlr+S)");
        buttons[5].setToolTipText(ResStrings.getString("strRefresh") + " (F5)");
        buttons[6].setToolTipText(ResStrings.getString("strSolve") + " (F9)");
        buttons[8].setToolTipText(ResStrings.getString("strUndoMove") + " (Ctlr+Z)");
        buttons[9].setToolTipText(ResStrings.getString("strStartAgain") + " (F2)");
        buttons[10].setToolTipText(ResStrings.getString("strClearAllTubes"));
        buttons[11].setToolTipText(ResStrings.getString("strAutoFill"));
        buttons[13].setToolTipText(ResStrings.getString("strChangePalette") + " (Ctlr+P)");
        buttons[14].setToolTipText(ResStrings.getString("strOptions") + " (Ctlr+O)");
        buttons[16].setToolTipText(ResStrings.getString("strExit") + " (Ctlr+X)");
    }

    /**
     * Saves Toolbar properties, docked and alignment.
     */
    public void saveOptions() {
        Options.menuDockedTo = docked;
        Options.menuAlignment = align;
    }

}
