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
import dlg.PaletteDlg;
import dlg.StartDlg;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import core.ResStrings;
import lib.lButtons.LToolButton;
import lib.lOpenSaveDialog.LOpenSaveDialog;
import run.Main;

public class ToolPanel extends JPanel {

    private final int dimX = 10;
    private final int dimY = 10;
    private final int skipX = 20; // size for separator
    private final int buttonsMax = 20;
    private static final ToolMenu menu = new ToolMenu();
    private final LToolButton[] buttons = new LToolButton[buttonsMax];
    private int btnCount = 0;
    private int align = 0; // 0 - begin (left/top), 1 - center, 2 - end (right/bottom);
    private int docked = 2; // 0 - top, 1 - bottom, 2 - left, 3 - right

    private int curMode = -1;

    public ToolPanel() {
        LToolButton tb;
        setLayout(null);
        setBackground(Palette.dialogColor);

        tb = addNewButton("new", ResStrings.getString("strStartGame"));  // buttons[0]
        tb.addActionListener((ActionEvent e) -> newGameClick());
        addSeparator();                                         // buttons[1]

        tb = addNewButton("load", ResStrings.getString("strLoadGame"));  // buttons[2]
        tb.addActionListener((ActionEvent e) -> loadClick());

        tb = addNewButton("save", ResStrings.getString("strSaveGame"));  // buttons[3]
        tb.addActionListener((ActionEvent e) -> saveClick());

        addSeparator();                                         // buttons[4]
        tb = addNewButton("refresh", ResStrings.getString("strRefresh")); // buttons[5]
        tb.addActionListener((ActionEvent e) -> refreshClick());

        tb = addNewButton("solve", ResStrings.getString("strSolve"));                    // buttons[6]
        tb.setEnabled(true);
        tb.addActionListener((ActionEvent e) -> solveClick());

        addSeparator();                                          // buttons[7]
        tb = addNewButton("undo", ResStrings.getString("strUndoMove"));                  // buttons[8]
        tb.setEnabled(true);
        tb.addActionListener((ActionEvent e) -> undoClick());

        tb = addNewButton("replay", ResStrings.getString("strStartAgain"));              // buttons[9]
        tb.setEnabled(true);
        tb.addActionListener((ActionEvent e) -> restartClick());

        tb = addNewButton("cleartubes", ResStrings.getString("strClearAllTubes"));         // buttons[10]
        tb.setEnabled(true);
        tb.addActionListener((ActionEvent e) -> clearTubesClick());

        tb = addNewButton("replay", ResStrings.getString("strAutoFill"));   // buttons[11]
        tb.setEnabled(true);
        tb.addActionListener((ActionEvent e) -> autoFillClick());

        addSeparator();                                          // buttons[12]
        tb = addNewButton("palette", ResStrings.getString("strChangePalette"));          // buttons[13]
        tb.setEnabled(true);
        tb.addActionListener((ActionEvent e) -> paletteClick());

        tb = addNewButton("options", ResStrings.getString("strOptions"));                 // buttons[14]
        tb.setEnabled(false);
        tb.addActionListener((ActionEvent e) -> optionsClick());

        addSeparator();                                          // buttons[15]
        tb = addNewButton("exit",  ResStrings.getString("strExit"));                  // buttons[16]
        tb.addActionListener((ActionEvent e) -> exitClick());

        updateButtons(0);

        addMouseListener(new MouseAdapter() {
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

        if (Options.menuDockedTo >= 0 && Options.menuDockedTo <= 3
                && Options.menuAlign >= 0 && Options.menuAlign <= 1) {
            docked = Options.menuDockedTo;
            align = Options.menuAlign;
        }

    }

    public LToolButton addNewButton(String imgFName, String hint) {
        LToolButton btn = new LToolButton(this, "btnTool32", imgFName);
        btn.setColorEnabled(new Color(0xb8cfe5));
        btn.setColorDisabled(new Color(0x737c85)); // 737c85 545d65
        btn.setColorHover(new Color(0x222222));
        btn.setColorPressed(new Color(0xaaaaaa));
        btn.setFocusable(false);

        btn.setToolTipBackground(Palette.dialogColor);
        btn.setToolTipForeground(Color.WHITE);
        btn.setToolTipBorder(new Color(0xb8cfe5));
        btn.setToolTipText(hint);

        buttons[btnCount] = btn;

        add(btn);
        btnCount++;
        return btn;
    }

    public void addSeparator() {
        buttons[btnCount] = null;
        btnCount++;
    }

    public void updateButtons() {
        updateButtons(curMode);
    }

    public void updateButtons(int gameMode) {
        switch (gameMode) {
            case MainFrame.FILL_MODE:
                buttons[3].setEnabled(true);  // 3 - save button
                buttons[5].setVisible(false); // 5 - refresh board
                buttons[6].setVisible(false); // 6 - solve button
                buttons[8].setVisible(false); // 8 - undo move
                buttons[9].setVisible(false); // 9 -replay / start again
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
            updateButtonsPos();
            curMode = gameMode;
        }
    }

//////////////// 
//////////////// actions!
////////////////     
    public void newGameClick() {
        Main.frame.saveGame();
        StartDlg dlg = new StartDlg(Main.frame);
        dlg.setVisible(true);
    }

    public void loadClick() {
        LOpenSaveDialog os = new LOpenSaveDialog(Main.frame);
        String fileName = os.showOpenDialog();
        if (!"".equals(fileName)) {
            if (Main.frame.loadGame(fileName)) {
                switch (MainFrame.gameMode) {
                    case MainFrame.PLAY_MODE:
                        Main.frame.resumePlayMode();
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
            }
        }
    }

    public void saveClick() {
        LOpenSaveDialog os = new LOpenSaveDialog(Main.frame);
        String fileName = os.showSaveDialog();
        if (!"".equals(fileName)) {
            Main.frame.saveGame(fileName);
        }
    }

    public void undoClick() {
        MainFrame.tubesPan.undoMoveColor();
    }

    public void restartClick() {
        MainFrame.tubesPan.startAgain();
    }

    public void refreshClick() {
        if (MainFrame.palPan != null) {
            MainFrame.palPan.updateColors();
        }
        if (MainFrame.tubesPan != null) {
            MainFrame.tubesPan.updateColors();
        }
    }

    public void solveClick() {
        if (MainFrame.tubesPan.doSolve()) {
            Main.frame.startAssistMode();
        } else {
            Main.frame.endAssistMode();
            Main.frame.resumePlayMode();
        }
    }

    public void paletteClick() {
        PaletteDlg pd = new PaletteDlg(Main.frame);
        pd.setVisible(true);
    }

    public void clearTubesClick() {
        MainFrame.tubesPan.clearTubes();
    }

    public void autoFillClick() {
        Main.frame.autoFillTheRest();
    }

    public void optionsClick() {
        // TODO Options.
    }

    public void exitClick() {
        Main.frame.closeFrame();
    }

//////////////// 
//////////////// dock and align
////////////////     
    public int getDockedTo() {
        return docked;
    }

    public void setDockedTo(int newDock) {
        docked = newDock;
        resize();
        Main.frame.updatePanelsPos();
    }

    public int getAlignment() {
        return align;
    }

    public void setAlignment(int newAlign) {
        align = newAlign;
        updateButtonsPos();
    }

//////////////// 
//////////////// size and position routines 
////////////////     
    public void resize() {
        if (docked < 2) { // horizontal
            setSize(Main.frame.getClientArea().width,
                    getButtonsHeight());
        } else { // vertical
            setSize(getButtonsHeight(),
                    Main.frame.getClientArea().height);
        }
        relocation();
    }

    public void relocation() {
        switch (docked) {
            case 1: // bottom
                setLocation(0, Main.frame.getClientArea().height - getButtonsHeight());
                break;
            case 3: // right
                setLocation(Main.frame.getClientArea().width - getButtonsHeight(), 0);
                break;
            default: // top, left
                setLocation(0, 0);
        }
        updateButtonsPos();
    }

    public int getButtonsWidth() {
        return getButtonX(btnCount);
    }

    public int getButtonsHeight() {
        int btnSize = (btnCount > 0) ? buttons[0].getWidth() : 0;
        return dimY * 2 + btnSize;
    }

    private int getButtonX(int btnNum) {
        boolean wasSkipped = false;
        if (btnNum > btnCount) {
            return 0;
        }
        int result = dimX;
        for (int i = 0; i < btnNum; i++) {
            if (buttons[i] != null && buttons[i].isVisible()) {
                result += buttons[i].getWidth();
                wasSkipped = false;
            } else if (buttons[i] == null && !wasSkipped) {
                result += skipX;
                wasSkipped = true;
            }
        }
        if (btnNum == btnCount) {
            result += dimX;
        }
        return result;
    }

    private void updateButtonsPos() {
        if (Main.frame != null) {
            int startX;

            int panLength = (docked < 2)
                    ? Main.frame.getClientArea().width
                    : Main.frame.getClientArea().height;

            switch (align) {
                case 1: // center
                    startX = (panLength - getButtonsWidth()) / 2;
                    break;
                case 2: // end (right / bottom)
                    startX = panLength - getButtonsWidth();
                    break;
                default: // begin (top / left)
                    startX = 0;
                    break;
            }

            for (int i = 0; i < buttons.length; i++) {
                if (buttons[i] != null) {
                    if (docked < 2) { // horizontal
                        buttons[i].setLocation(getButtonX(i) + startX, dimY);
                    } else { // vertical
                        buttons[i].setLocation(dimY, getButtonX(i) + startX);
                    }
                }
            }
        }
    }

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

    public void saveOptions() {
        Options.menuDockedTo = docked;
        Options.menuAlign = align;
    }

}
