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

import core.BoardModel;
import core.Options;
import run.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * This is a set of color tubes, or the Game Board.
 */
public class BoardPanel extends JComponent {

///////////////////////////////////////////////////////////////////////////
//
//               * Fields / variables *
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Color tubes array.
     */
    private final ArrayList<ColorTube> tubes = new ArrayList<>();
    /**
     * The horizontal space between two tubes.
     */
    private int spaceX = 15;
    /**
     * The vertical space between two tubes.
     */
    private int spaceY = 15;

    /**
     * The number of rows to display color tubes.
     */
    private int rows;

    /**
     * The number of columns to display color tubes.
     */
    private int cols;

    /**
     * Which edge of the client area the panel will be docked to: <ul>
     * <li>0 - center;
     * <li>1 - top;
     * <li>2 - bottom;
     * <li>3 - left;
     * <li>4 - right.</ul>
     */
    private int docked = 0;

    /**
     * The logical model of the board.
     */
    private final BoardModel model;

    /**
     * The tube that will lose the color (Donor).
     */
    private ColorTube tubeFrom = null;

    /**
     * The tube that will get a new color (Recipient).
     */
    private ColorTube tubeTo = null;

    /**
     * The popup menu with Color Buttons actions.
     */
    private static final BoardMenu menu = new BoardMenu();

///////////////////////////////////////////////////////////////////////////
//
//               * Routines *
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new Board Panel.
     */
    public BoardPanel() {
        rows = 2;
        model = new BoardModel();
        addPopup(this);
    }

    /**
     * Adds a new ColorTube to the board.
     * @return a new color tube.
     */
    public ColorTube addNewTube() {
        ColorTube tube = new ColorTube() {
            @Override
            public void doClick() {
                clickTube(this);
            }

            @Override
            public boolean canShowArrow() {
                return BoardPanel.this.canShowArrow(this);
            }

            @Override
            public boolean canHideArrow() {
                return BoardPanel.this.canHideArrow(this);
            }
        };

        tubes.add(tube);
        model.addNewTube(tube.getModel());
        add(tube);
        addPopup(tube);

        return tube;
    }

    /**
     * Adds some number of color tubes to the board.
     * @param filled number of filled tubes.
     * @param empty number of empty tubes.
     */
    public void addNewTubes(int filled, int empty) {
        for (int i = 0; i < filled; i++) {
            ColorTube tube = addNewTube();
            tube.setFrame(0);
            tube.setActive(true);
        }
        for (int i = 0; i < empty; i++) {
            ColorTube tube = addNewTube();
            tube.setActive(false);
            tube.setClosed(true);
        }
        calculateColumns();
    }

    public void clearBoard() {
        removeAll();
        model.clear();
        tubes.clear();
    }

    /**
     * Adds the popup menu to the board panel components.
     * We need to add it to every color tube and to the board itself.
     * @param comp a component to add the menu.
     */
    public final void addPopup(JComponent comp) {
        if (comp == null) {
            comp = this;
        }
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

    public ColorTube getTube(int number) {
        if (number >= 0 && number < getTubesCount()) {
            return tubes.get(number);
        } else {
            return null;
        }
    }

    public int getTubeNumber(ColorTube tube) {
        return tubes.indexOf(tube);
    }

    public int getTubesCount() {
        return tubes.size();
    }

    public void clearTube(ColorTube tube) {
        tube.clear();
    }

    public void clearTubes() {
        for (int i = 0; i < getTubesCount(); i++) {
            clearTube(getTube(i));
        }
    }

    public BoardModel getModel() {
        return model;
    }

///////////////////////////////////////////////////////////////////////////
//
//               * Size and position routines *
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Gets number of rows to display color tubes.
     * @return rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Set rows to display color tubes.
     * @param newRows a new Rows value
     */
    public void setRows(int newRows) {
        rows = newRows;
        calculateColumns();
        calculateSize();
        updateTubesPos();
        reDock();
    }

    /**
     * Calculates how many columns are needed to display all color tubes on the board.
     */
    private void calculateColumns() {
        cols = getTubesCount() / rows;
        if (cols * rows < getTubesCount()) {
            cols++;
        }
    }

    /**
     * Calculates and sets the size of the Board to display color tubes.
     */
    private void calculateSize() {
        if (!tubes.isEmpty()) {
            setSize(spaceX + cols * (spaceX + tubes.get(0).getWidth()),
                    spaceY + rows * (spaceY + tubes.get(0).getHeight()));
        }
    }

    /**
     * Calculates and sets position of color tube depending on the current columns and rows values.
     * @param number of the color tube to set its location.
     */
    private void updateTubePos(int number) {
        int col = number % cols;
        int row = number / cols;
        int x = spaceX + col * (getTube(number).getWidth() + spaceX);
        int y = spaceY + row * (getTube(number).getHeight() + spaceY);
        if (row == rows - 1) { // last row
            int lastRowCols = getTubesCount() - (cols * (rows - 1));
            if (lastRowCols != cols) {
                switch (docked) {
                    case 0: // center
                    case 1: // top
                    case 2: // bottom
                        x = x + (cols - lastRowCols) * (getTube(number).getWidth() + spaceX) / 2;
                        break;
                    case 4: // right
                        x = x + (cols - lastRowCols) * (getTube(number).getWidth() + spaceX);
                        break;
                    default: // left
                        // nothing to do
                        break;
                }
            }
        }
        getTube(number).setLocation(x, y);
    }

    /**
     * Calculates and sets position of all color tubes depending on the current columns and rows values.
     */
    private void updateTubesPos() {
        for (int i = 0; i < getTubesCount(); i++) {
            updateTubePos(i);
        }
    }

    /**
     * Gets the edge of the MainFrame the panel is docked to: <ul>
     * <li>0 - center;
     * <li>1 - top;
     * <li>2 - bottom;
     * <li>3 - left;
     * <li>4 - right.</ul>
     *
     * @return current dockedTo value
     */
    public int getDockedTo() {
        return docked;
    }

    /**
     * Sets the edge of the MainFrame the panel is docked to.
     *
     * @param newDocked a new docked value.
     * @see #docked
     */
    public void setDockedTo(int newDocked) {
        if (newDocked >= 0 && newDocked < 5) {
            docked = newDocked;
            reDock();
        }
    }

    /**
     * Relocates the panel depending on the current docked value.
     */
    public void reDock() {
        Rectangle r = Main.frame.getTubesArea();
        switch (docked) {
            case 0: // center
                r.x += (r.width - getWidth()) / 2;
                r.y += (r.height - getHeight()) / 2;
                break;
            case 1: // top
                r.x += (r.width - getWidth()) / 2;
                break;
            case 2: // bottom
                r.x += (r.width - getWidth()) / 2;
                r.y += r.height - getHeight();
                break;
            case 3: // left
                r.y += (r.height - getHeight()) / 2;
                break;
            case 4: // right
                r.x += r.width - getWidth();
                r.y += (r.height - getHeight()) / 2;
                break;
        }
        updateTubesPos();
        setLocation(r.x, r.y);
    }

    public void restoreLocation() {
        if (Options.boardDockedTo >= 0
                && Options.boardDockedTo <= 4
                && Options.boardLines > 0
                && Options.boardLines < 4) {
            docked = Options.boardDockedTo;
            setRows(Options.boardLines);
        } else {
            setRows(2);
        }
    }

    public ColorTube getTubeFrom() {
        return tubeFrom;
    }

    public void setTubeFrom(ColorTube tube) {
        if (tube != tubeFrom) {
            if (tubeFrom != null) {
                tubeFrom.setFrame(ColorTube.FRAME_NO_COLOR);
                tubeFrom = null;
            }
        }
        if (tube != null && canGetColor(tube)) {
            tube.setFrame(ColorTube.FRAME_GREEN);
            tube.showFrame();
            tubeFrom = tube;
        }
    }

    public ColorTube getTubeTo() {
        return tubeTo;
    }

    public void setTubeTo(ColorTube tube) {
        if (tube != tubeTo) {
            if (tubeTo != null) {
                if (!tubeTo.isClosed())
                    tubeTo.setFrame(ColorTube.FRAME_NO_COLOR);
                tubeTo = null;
            }
        }
        if (tube != null && tube.canPutColor(0)) {
            tube.setFrame(ColorTube.FRAME_YELLOW);
            tube.showFrame();
            tubeTo = tube;
        }
    }

    public void clickTube(ColorTube tube) {
        // overrides by MainFrame
    }

    public boolean canShowArrow(ColorTube tube) {
        // overrides by MainFrame
        return true;
    }

    public boolean canHideArrow(ColorTube tube) {
        // overrides by MainFrame
        return true;
    }

    public void updateColor(int colorNumber) {
        for (int i = 0; i < getTubesCount(); i++) {
            if (getTube(i).getModel().hasColor((byte) colorNumber)) {
                getTube(i).repaintColors();
            }
        }
    }

    public void updateColors() {
        for (int i = 0; i < getTubesCount(); i++) {
            getTube(i).repaintColors();
        }
    }

    public boolean canGetColor(ColorTube tubeFrom) {
        boolean result = false;
        int fromIdx = getTubeNumber(tubeFrom);
        int i = 0;

        while (!result && i < getTubesCount()) {
            if (i != fromIdx) {
                result = model.canMakeMove(fromIdx, i);
            }
            i++;
        }
        return result;
    }

    public int moveColor(ColorTube tubeFrom, ColorTube tubeTo) {
        int result = 0;
        if (model.canMakeMove(getTubeNumber(tubeFrom), getTubeNumber(tubeTo))) {
            byte clr = tubeFrom.getCurrentColor();
            int cnt = Math.min(tubeFrom.getModel().colorsToGet(), 4 - tubeTo.getColorsCount());
            result = cnt;
            do {
                tubeFrom.extractColor();
                tubeTo.putColor(clr);
                cnt--;
            } while (cnt > 0);
        }

        return result;
    }

    public void undoMoveColor() {
        if (MainFrame.gameMoves.isEmpty()) {
            return;
        }

        int moveNumber = MainFrame.movesDone - 1;

        int idxFrom = MainFrame.gameMoves.getTubeFrom(moveNumber);
        int idxTo = MainFrame.gameMoves.getTubeTo(moveNumber);
        int mCount = MainFrame.gameMoves.getMoveCount(moveNumber);
        byte mColor = MainFrame.gameMoves.getColor(moveNumber);

        while (mCount > 0) {
            getTube(idxTo).extractColor();
            getTube(idxFrom).putColor(mColor);
            mCount--;
        }

        MainFrame.movesDone--;
        MainFrame.toolPan.updateButtons();
        if (MainFrame.gameMode != MainFrame.ASSIST_MODE) {
            MainFrame.gameMoves.remove(moveNumber);
        } else {
            Main.frame.hideMove();
            Main.frame.showMove();
        }
    }

    public void startAgain() {

        if (MainFrame.gameMoves.isEmpty()) {
            return;
        }

        int[] storedTubes = new int[getTubesCount()];
        for (int i = 0; i < getTubesCount(); i++) {
            storedTubes[i] = model.get(i).storeColors();
        }

        int movesCount = 0;

        if (MainFrame.gameMode == MainFrame.PLAY_MODE) {
            movesCount = MainFrame.gameMoves.size();
        } else if (MainFrame.gameMode == MainFrame.ASSIST_MODE) {
            movesCount = MainFrame.movesDone;
        }

        for (int i = movesCount; i > 0; i--) {
            int idxFrom = MainFrame.gameMoves.getTubeFrom(i - 1);
            int idxTo = MainFrame.gameMoves.getTubeTo(i - 1);
            int mCount = MainFrame.gameMoves.getMoveCount(i - 1);
            byte mColor = MainFrame.gameMoves.getColor(i - 1);

            while (mCount > 0) {
                model.get(idxTo).extractColor();
                model.get(idxFrom).putColor(mColor);
                mCount--;
            }
            if (MainFrame.gameMode == MainFrame.PLAY_MODE) {
                MainFrame.gameMoves.remove(i - 1);
            }
        }
        MainFrame.movesDone = 0;
        MainFrame.toolPan.updateButtons();
        for (int i = 0;
             i < getTubesCount();
             i++) {
            int newTube = model.get(i).storeColors();
            model.get(i).assignColors(storedTubes[i]);

            getTube(i).clear();
            getTube(i).restoreColors(newTube);
        }
        if (MainFrame.gameMode == MainFrame.PLAY_MODE) {
            Main.frame.startPlayMode();
        } else if (MainFrame.gameMode == MainFrame.ASSIST_MODE) {
            Main.frame.startAssistMode();
        }

    }

    public boolean isSolved() {
        return model.isSolved();
    }

    /**
     * Set spaces between color tubes
     *
     * @param spaceX horizontal space between tubes
     * @param spaceY vertical spaces between tubes
     */
    public void setSpaces(int spaceX, int spaceY) {
        this.spaceX = spaceX;
        this.spaceY = spaceY;
        reDock();
    }

    public void saveOptions() {
        Options.boardDockedTo = docked;
        Options.boardLines = rows;
    }
}
