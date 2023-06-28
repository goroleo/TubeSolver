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
    private final static ArrayList<ColorTube> tubes = new ArrayList<>();

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

    /**
     * Gets the tube by its number from the tubes list.
     *
     * @param number tube's number
     * @return color tube
     */
    public ColorTube getTube(int number) {
        if (number >= 0 && number < getTubesCount()) {
            return tubes.get(number);
        } else {
            return null;
        }
    }

    /**
     * Gets the number of the specified tube.
     *
     * @param tube color tube
     * @return tube's number
     */
    public int getTubeNumber(ColorTube tube) {
        return tubes.indexOf(tube);
    }

    /**
     * Gets the count of color tubes
     *
     * @return tubes' count
     */
    public int getTubesCount() {
        return tubes.size();
    }

    /**
     * Gets the logical BoradModel of the panel
     *
     * @return model
     */
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
     * Set spaces between color tubes.
     *
     * @param spaceX horizontal space between tubes
     * @param spaceY vertical spaces between tubes
     */
    public void setSpaces(int spaceX, int spaceY) {
        this.spaceX = spaceX;
        this.spaceY = spaceY;
        reDock();
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
    private void setTubeLocation(int number) {
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
            setTubeLocation(i);
        }
    }

    /**
     * Gets the edge of the MainFrame the panel is docked to
     * @return current dockedTo value
     * @see #docked
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

    /**
     * Restores the location of this panel which was previously saved in the application options.
     */
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

    /**
     * Saves current lines and position to the application options.
     */
    public void saveOptions() {
        Options.boardDockedTo = docked;
        Options.boardLines = rows;
    }

///////////////////////////////////////////////////////////////////////////
//
//               * Game and Color routines *
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Determines if the game is solved already?
     * @return true if the board is solved, false otherwise.
     */
    public boolean isSolved() {
        return model.isSolved();
    }

    /**
     * Clears whole the board, deletes all tubes.
     */
    public void emptyBoard() {
        removeAll();
        model.clear();
        tubes.clear();
    }

    /**
     * Gets the current Donor tube, from which the color cell will be got.
     * @return the donor tube or <i>null</i> if the donor is not stated.
     */
    public ColorTube getTubeFrom() {
        return tubeFrom;
    }

    /**
     * Sets the Donor tube, from which the color cell will be got.
     * @param tube the new donor tube, or <i>null</i> if you want to clear the donor.
     */
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

    /**
     * Gets the current Recipient tube, it which the color cell will be put.
     * @return the recipient tube or <i>null</i> if the recipient is not stated.
     */
    public ColorTube getTubeTo() {
        return tubeTo;
    }

    /**
     * Sets the current Recipient tube, it which the color cell will be put.
     * @param tube the new recipient tube, or <i>null</i> if you want to clear the recipient.
     */
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

    /**
     * Handles the mouse click event on the specified tube. This procedure is intended to override it.
     *
     * @param tube specified tube
     */
    public void clickTube(ColorTube tube) {
        // overrides by MainFrame
    }

    /**
     * Determines if this tube's arrow can be shown. This procedure is intended to override it.
     *
     * @param tube specified color tube
     * @return true if this tube's arrow can be shown, false otherwise
     */
    public boolean canShowArrow(ColorTube tube) {
        // overrides by MainFrame
        return true;
    }

    /**
     * Determines if this tube's arrow can be hidden. This procedure is intended to override it.
     *
     * @param tube specified color tube
     * @return true if this tube's arrow can be shown, false otherwise
     */
    public boolean canHideArrow(ColorTube tube) {
        // overrides by MainFrame
        return true;
    }

    /**
     * Repaints one color at the Board. I.e. repaints all tubes that have this color.
     *
     * @param colorNumber number of the color in palette.
     */
    public void updateColor(int colorNumber) {
        for (int i = 0; i < getTubesCount(); i++) {
            if (getTube(i).getModel().hasColor((byte) colorNumber)) {
                getTube(i).repaintColors();
            }
        }
    }

    /**
     * Repaints all tubes  at the Board.
     */
    public void updateColors() {
        for (int i = 0; i < getTubesCount(); i++) {
            getTube(i).repaintColors();
        }
    }

    /**
     * Clears all colors from the specified tube.
     * @param tube specified color tube
     */
    public void clearTube(ColorTube tube) {
        tube.clear();
    }

    /**
     * Clears all colors from all tubes.
     */
    public void clearTubes() {
        for (int i = 0; i < getTubesCount(); i++) {
            clearTube(getTube(i));
        }
    }

    /**
     * Determines whether a color can be taken from the specific tube. The routine
     * gets the Current Color from the tube and tries to find another tube that has
     * a place to put that color in.
     *
     * @param tube specified tube
     * @return true if the Current Color of this tube has another tube to place it in, false otherwise.
     * @see ColorTube#getCurrentColor()
     */
    public boolean canGetColor(ColorTube tube) {
        boolean result = false;
        int fromIdx = getTubeNumber(tube);
        int i = 0;
        while (!result && i < getTubesCount()) {
            if (i != fromIdx) {
                result = model.canMakeMove(fromIdx, i);
            }
            i++;
        }
        return result;
    }

    /**
     * Doing the move!
     *
     * @param tubeFrom Donor color tube.
     * @param tubeTo Recipient color tube.
     * @return count ot the transferred color cells.
     */
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

    /**
     * Undoes the one last move.
     */
    public void undoMoveColor() {
        if (MainFrame.gameMoves.isEmpty()) {
            return;
        }

        // get last move
        MainFrame.movesDone--;
        int idxFrom = MainFrame.gameMoves.getTubeFrom(MainFrame.movesDone);
        int idxTo = MainFrame.gameMoves.getTubeTo(MainFrame.movesDone);
        int mCount = MainFrame.gameMoves.getMoveCount(MainFrame.movesDone);
        byte mColor = MainFrame.gameMoves.getColor(MainFrame.movesDone);

        // undo move
        while (mCount > 0) {
            getTube(idxTo).extractColor();
            getTube(idxFrom).putColor(mColor);
            mCount--;
        }

        // update board
        MainFrame.toolPan.updateButtons();
        if (MainFrame.gameMode != MainFrame.ASSIST_MODE) {
            MainFrame.gameMoves.remove(MainFrame.movesDone);
        } else {
            Main.frame.hideMove();
            Main.frame.showMove();
        }
    }

    /**
     * Undoes all moves of the current game. Starts the game again.
     */
    public void startAgain() {

        if (MainFrame.gameMoves.isEmpty()) {
            return;
        }

        // how much moves will be cancelled
        int movesCount = 0;
        if (MainFrame.gameMode == MainFrame.PLAY_MODE) {
            movesCount = MainFrame.gameMoves.size();
        } else if (MainFrame.gameMode == MainFrame.ASSIST_MODE) {
            movesCount = MainFrame.movesDone;
        }

        // undo moves at the board model
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

        // restore initial colors from the board model to ColorTubes
        for (int i = 0; i < getTubesCount(); i++) {
            getTube(i).restoreColors();
        }

        // update board configuration
        MainFrame.movesDone = 0;
        MainFrame.toolPan.updateButtons();
        if (MainFrame.gameMode == MainFrame.ASSIST_MODE) {
            Main.frame.hideMove();
            Main.frame.showMove();
        }
    }
}
