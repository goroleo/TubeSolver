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

import run.Main;
import core.BoardModel;
import core.Options;
import dlg.MessageDlg;
import dlg.SolveDlg;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.JComponent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import core.ResStrings;

public class BoardPanel extends JComponent {

    private final ArrayList<ColorTube> tubes = new ArrayList<>();
    private int spaceX = 15;
    private int spaceY = 15;
    private int rows;
    private int cols;
    private int docked = 0;

    private final BoardModel model;
    private ColorTube tubeFrom = null;
    private ColorTube tubeTo = null;

    private static final BoardMenu menu = new BoardMenu();

    public BoardPanel() {
        rows = 2;
        model = new BoardModel();
        addPopup(null);
    }

    public void updateLocation() {
        if (Options.boardDockedTo >= 0
                && Options.boardDockedTo <= 4
                && Options.boardLines > 0
                && Options.boardLines < 4) {
            docked = Options.boardDockedTo;
            setRows(Options.boardLines);
        } else {
            setRows(2);
        }
        calculateColumns();
        calculateSize();
        updateTubesPos();
        reDock();
    }

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

    public void addNewTubes(int count) {
        for (int i = 0; i < count; i++) {
            addNewTube();
        }
        calculateColumns();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        for (int i = 0; i < getTubesCount(); i++) {
            this.getTube(i).setVisible(visible);
            this.getTube(i).paintImmediately(getTube(i).getBounds());
        }
    }

    public BoardModel getModel() {
        return model;
    }

    public void addNewTubes(int countFilled, int countEmpty) {
        for (int i = 0; i < countFilled; i++) {
            ColorTube tube = addNewTube();
            tube.setShade(0);
            tube.setActive(true);
        }
        for (int i = 0; i < countEmpty; i++) {
            ColorTube tube = addNewTube();
            tube.setActive(false);
            tube.setClosed(true);
        }
        calculateColumns();
    }

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

    public void clear() {
        removeAll();
        tubes.clear();
    }

    public int getTubesCount() {
        return tubes.size();
    }

    public int getTubeNumber(ColorTube tube) {
        return tubes.indexOf(tube);
    }

    public ColorTube getTube(int number) {
        if (number >= 0 && number < tubes.size()) {
            return tubes.get(number);
        } else {
            return null;
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public void setRows(int newRows) {
        rows = newRows;
        calculateColumns();
        calculateSize();
        updateTubesPos();
        reDock();
    }

    public void setCols(int newCols) {
        cols = newCols;
        calculateRows();
        calculateSize();
        updateTubesPos();
        reDock();
    }

    private void calculateColumns() {
        cols = tubes.size() / rows;
        if (cols * rows < tubes.size()) {
            cols++;
        }
    }

    private void calculateRows() {
        rows = tubes.size() / cols;
        if (cols * rows < tubes.size()) {
            rows++;
        }
    }

    private void setTubePos(int number) {
        int col = number % cols;
        int row = number / cols;
        int x = spaceX + col * (getTube(number).getWidth() + spaceX);
        int y = spaceY + row * (getTube(number).getHeight() + spaceY);
        if (row == rows - 1) { // last row
            int lastRowCols = tubes.size() - (cols * (rows - 1));
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

    private void updateTubesPos() {
        for (int i = 0; i < tubes.size(); i++) {
            setTubePos(i);
        }
    }

    public void calculateSize() {
        if (!tubes.isEmpty()) {
            setSize(spaceX + cols * (spaceX + tubes.get(0).getWidth()),
                    spaceY + rows * (spaceY + tubes.get(0).getHeight()));
        }
    }

    public int getDockedTo() {
        return docked;
    }

    public void setDockedTo(int number) {
        docked = number;
        reDock();
    }

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

    
    public void clearTubes() {
        for (int i = 0; i < tubes.size(); i++) {
            getTube(i).clear();
        }
    }

    public void clearTube(int number) {
        clearTube(getTube(number));
    }

    public void clearTube(ColorTube tube) {
        tube.clear();
    }

    public ColorTube getTubeTo() {
        return tubeTo;
    }

    public ColorTube getTubeFrom() {
        return tubeFrom;
    }

    public void setTubeTo(ColorTube tube) {
        if (tube != tubeTo) {
            if (tubeTo != null) {
                tubeTo.setShade(ColorTube.SHADE_NO_COLOR);
                tubeTo = null;
            }
        }
        if (tube != null && tube.canPutColor(0)) {
            tube.setShade(ColorTube.SHADE_YELLOW);
            tube.showShade();
            tubeTo = tube;
        }
    }

    public void setTubeFrom(ColorTube tube) {
        if (tube != tubeFrom) {
            if (tubeFrom != null) {
                tubeFrom.setShade(ColorTube.SHADE_NO_COLOR);
                tubeFrom = null;
            }
        }
        if (tube != null && canGetColor(tube)) {
            tube.setShade(ColorTube.SHADE_GREEN);
            tube.showShade();
            tubeFrom = tube;
        }
    }

    public void setTubeTo(int number) {
        setTubeTo(getTube(number));
    }

    public void clickTube(ColorTube tube) {

    }

    public boolean canShowArrow(ColorTube tube) {
        return true;
    }

    public boolean canHideArrow(ColorTube tube) {
        return true;
    }

    public void updateColor(int colorNumber) {
        for (int i = 0; i < getTubesCount(); i++) {
            if (getTube(i).hasColor(colorNumber)) {
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
        int fromIdx = tubes.indexOf(tubeFrom);
        int i = 0;

        while (!result && i < tubes.size()) {
            if (i != fromIdx) {
                result = model.canMakeMove(fromIdx, i);
            }
            i++;
        }
        return result;
    }

    public int moveColor(ColorTube tubeFrom, ColorTube tubeTo) {
        int result = 0;
        if (model.canMakeMove(tubes.indexOf(tubeFrom), tubes.indexOf(tubeTo))) {
            byte clr = tubeFrom.getCurrentColor();
            int cnt = Math.min(tubeFrom.getModel().colorsToGet(), 4 - tubeTo.getColorsCount());
            result = cnt;
            do {
                tubeFrom.extractColor();
                tubeTo.putColor(clr);
                cnt--;
            } while (cnt > 0);
        }

        tubeFrom.updateState();
        tubeTo.updateState();

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

        if (mCount > 0) {
            while (mCount > 0) {
                getTube(idxTo).extractColor();
                getTube(idxFrom).putColor(mColor);
                mCount--;
            }
            getTube(idxTo).updateState();
            getTube(idxFrom).updateState();
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
//            int moveNumber = i - 1;
            int idxFrom = MainFrame.gameMoves.getTubeFrom(i - 1);
            int idxTo = MainFrame.gameMoves.getTubeTo(i - 1);
            int mCount = MainFrame.gameMoves.getMoveCount(i - 1);
            byte mColor = MainFrame.gameMoves.getColor(i - 1);

            if (mCount > 0) {
                while (mCount > 0) {
                    model.get(idxTo).extractColor();
                    model.get(idxFrom).putColor(mColor);
                    mCount--;
                }
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

    public boolean doSolve() {

        boolean result = false;

        MessageDlg msgDlg = new MessageDlg(Main.frame,
                ResStrings.getString("strFindSolution"),
                MessageDlg.BTN_YES_NO);
        msgDlg.setButtonsLayout(MessageDlg.BTN_LAYOUT_RIGHT);
        msgDlg.setVisible(true);

        if (msgDlg.modalResult > 0) {

            SolveDlg solveDlg = new SolveDlg(Main.frame, model);
            solveDlg.solve();

            switch (solveDlg.modalResult) {
                case -1: // window closed
                case 0: // escape-cancel pressed
                    msgDlg = new MessageDlg(Main.frame,
                            ResStrings.getString("strCancelSolution"),
                            MessageDlg.BTN_OK);
                    msgDlg.setButtonsLayout(MessageDlg.BTN_LAYOUT_CENTER);
                    msgDlg.setVisible(true);
                    break;
                case 1: // not solved 
                    msgDlg = new MessageDlg(Main.frame,
                            ResStrings.getString("strNotSolved"),
                            MessageDlg.BTN_OK);
                    msgDlg.setButtonsLayout(MessageDlg.BTN_LAYOUT_RIGHT);
                    msgDlg.setVisible(true);
                    break;
                case 2: // solved! 
                    msgDlg = new MessageDlg(Main.frame,
                            ResStrings.getString("strSolutionSuccess"),
                            MessageDlg.BTN_YES_NO);
                    msgDlg.setButtonsLayout(MessageDlg.BTN_LAYOUT_RIGHT);
                    msgDlg.setVisible(true);
                    result = msgDlg.modalResult > 0;
            }
        }

        return result;
    }

    public boolean isSolved() {
        return model.isSolved();
    }

    /**
     * Set spaces between color tubes
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
