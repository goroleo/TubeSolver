/*
 * Copyright (c) 2021 legoru / goroleo <legoru@me.com>
 * 
 * This software is distributed under the <b>MIT License.</b>
 * The full text of the License you can read here: 
 * https://choosealicense.com/licenses/mit/
 * 
 * Use this as you want! ))
 */
package core;

import static gui.Palette.usedColors;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The logical model of the board with some color tubes.
 */
public class BoardModel extends ArrayList<TubeModel> {

    /**
     * The list of available moves from this tubes' board / configuration.
     */
    public ArrayList<ColorMoveItem> moves;

    /**
     * A parent tubes' configuration
     */
    public BoardModel parent = null;

    /**
     * The very first initial configuration.
     */
    public BoardModel root = null;

    /**
     * The most ranked move at the current configuration.
     */
    public ColorMoveItem currentMove;

    /**
     * The move that resulted in the current configuration.
     */
    public ColorMoveItem parentMove;

    /**
     * Create the new tube object and add it to the list
     */
    public void addNewTube() {
        TubeModel tube = new TubeModel();
        addNewTube(tube);
    }

    /**
     * Adds an existing tube created before
     *
     * @param tube created before
     */
    public void addNewTube(TubeModel tube) {
        this.add(tube);
    }

    /**
     * Removes all the elements from this list. The list will be empty after
     * this call returns. Also clears moves list and all fields pointers.
     */
    @Override
    public void clear() {
        if (moves != null) {
            moves.clear();
        }
        currentMove = null;
        parentMove = null;
        super.clear();
    }

    /**
     * Is this tubes board solved already?
     *
     * @return false if any tube state is REGULAR or FILLED
     * @see TubeModel#state
     */
    public boolean isSolved() {
        for (TubeModel aTube : this) {
            if (aTube.state == 1 // STATE_REGULAR
                    || aTube.state == 2) { // STATE_FILLED
                return false;
            }
        }
        return true;
    }

    /**
     * Is this board equal to another tubes board? The routine stores both
     * boards to integer arrays then sort and compares them.
     *
     * @param tm another tubes board
     * @return true or false
     */
    public boolean equalsTo(BoardModel tm) {
        boolean result = this.size() == tm.size();

        if (result) {
            int s = this.size();

            int[] cts1 = new int[s];
            int[] cts2 = new int[s];

            for (int i = 0; i < s; i++) {
                cts1[i] = this.get(i).storeColors();
                cts2[i] = tm.get(i).storeColors();
            }
            Arrays.sort(cts1);
            Arrays.sort(cts2);

            int i = 0;
            do {
                if (cts1[i] != cts2[i]) {
                    result = false;
                }
                i++;
            } while (result && i < s);

        }
        return result;
    }

    /**
     * Creates and adds new move to the move's list.
     *
     * @param idxFrom index of the donator tube
     * @param idxTo index of the recipient tube
     * @return new ColorMove
     * @see ColorMoveItem
     */
    public ColorMoveItem addNewMove(int idxFrom, int idxTo) {
        if (moves == null) {
            moves = new ArrayList<>();
        }

        ColorMoveItem cm = new ColorMoveItem();
        cm.bmBefore = this;
        cm.color = get(idxFrom).currentColor;
        cm.idxFrom = idxFrom;
        cm.idxTo = idxTo;

        moves.add(cm);
        currentMove = cm;
        return cm;
    }

    /**
     * Creates and adds new move to the move's list.
     *
     * @param ctFrom donator tube
     * @param ctTo recipient tube
     * @return new ColorMove
     * @see ColorMoveItem
     */
    public ColorMoveItem addNewMove(TubeModel ctFrom, TubeModel ctTo) {
        return addNewMove(indexOf(ctFrom), indexOf(ctTo));
    }

    /**
     * Deletes the move from the move's list
     *
     * @param idx Index of the move
     */
    public void deleteMove(int idx) {
        ColorMoveItem cm = moves.remove(idx);
        cm.bmAfter = null;
        cm.bmBefore = null;
        if (idx != 0) {
            currentMove = moves.get(idx - 1);
        } else {
            currentMove = null;
//            calculated = false;
        }
    }

    /**
     * Deletes the move from the move's list
     *
     * @param cm the move
     * @see ColorMoveItem
     */
    public void deleteMove(ColorMoveItem cm) {
        deleteMove(moves.indexOf(cm));
    }

    /**
     * If this board / configuration remains any moves.
     *
     * @return true or false
     */
    public boolean hasMoves() {
        return moves != null && !moves.isEmpty();
    }

    /**
     * This routine checks the possibility to transfer a color cell from one
     * tube to another. Donator tube must be not empty and not closed.
     *
     * @param ctFrom donator tube
     * @param ctTo recipient tube
     * @return true or false
     */
    public boolean canMakeMove(TubeModel ctFrom, TubeModel ctTo) {
        return (ctFrom.state == 1 // STATE_REGULAR
                || ctFrom.state == 2) // STATE_FILLED
                && ctTo.canPutColor(ctFrom.currentColor);
    }

    /**
     * This routine checks the possibility to transfer a color cell from one
     * tube to another.
     *
     * @param idxFrom index of the donator tube
     * @param idxTo index of the recipient tube
     * @return true or false
     */
    public boolean canMakeMove(int idxFrom, int idxTo) {
        return canMakeMove(get(idxFrom), get(idxTo));
    }

    /**
     * This routine counts all colors that are available in this configuration.
     * The colors that are at the top of each tube. <br>
     * The resulting array is used to rank the available moves.
     *
     * @see TubeModel#state
     */
    public void fillAvailableColors() {
        usedColors.clearColorCounts();
        for (TubeModel ct : this) {
            if (ct.state == 1 // STATE_REGULAR
                    || ct.state == 2) {      // STATE_FILLED
                usedColors.incColorCount(ct.currentColor, ct.colorsToGet());
            }
        }
    }

    /**
     * The main routine of the class. It calculates all available moves and
     * ranks them.
     *
     * @return Number of moves that were found.
     * @see ColorMoveItem
     */
    public int calculateMoves() {

        int dColorsToGet; // donator's ColorsToGet
        int rColorsToGet; // recipient's ColorsToGet

        if (hasMoves()) {
            return moves.size();
        }

        ColorMoveItem cm;
        int result = 0;

       /*
        * We can have more than one empty tube at the current board. 
        * Each empty tube repeats the already calculated moves again, 
        * moves are unnecessarily duplicated.
        * We don't need to increase unnecessarily the number of available 
        * moves and then calculate their consequences. Therefore, we will 
        * not calculate the second and subsequent empty tubes.
        */
        boolean emptyTubeProcessed = false; // true if one of empty tube was processed already

        fillAvailableColors();

        for (TubeModel ctRecipient : this) {

            if (ctRecipient.state == 1 // STATE_REGULAR
                    || (ctRecipient.state == 0 // STATE_EMPTY
                    && !emptyTubeProcessed)) { // one of empty tubes is passed already

                for (TubeModel ctDonator : this) {

                    if (ctDonator != ctRecipient) {

                        if (canMakeMove(ctDonator, ctRecipient)) {

                            cm = addNewMove(ctDonator, ctRecipient);
                            if (this != root) {
                                cm.parent = this.parentMove;
                            } else {
                                cm.parent = null;
                            }

                            dColorsToGet = ctDonator.colorsToGet();
                            rColorsToGet = ctRecipient.colorsToGet();

                            cm.count = Math.min(
                                    // empty cells at Recipient
                                    (4 - ctRecipient.count),
                                    // number of donator's cells of this color
                                    dColorsToGet);

                            // -----------------------------------
                            // Rank the move! 
                            // -----------------------------------
                            cm.rank = Math.min(
                                    // empty cells at Recipient
                                    (4 - ctRecipient.count),
                                    // number of available cells of this color at whole the board
                                    usedColors.getColorCount(cm.color));

                            if (ctRecipient.count > 0 && ctRecipient.count == rColorsToGet) {
                                // if the whole tube of the Recipient is filled by this color
                                cm.rank += 3;
                            }

                            if (dColorsToGet == ctDonator.count) {
                                // if the whole tube of the Donator is filled by this color
                                cm.rank += 2;
                            }

                            if (dColorsToGet > 4 - ctRecipient.count) {
                                // if the Donator tube is not completely emptied after the move
                                cm.rank -= 4;
                            }

                            if (dColorsToGet + rColorsToGet == 4) {
                                // if the donator and recipient will give a filled closed tube, 
                                // the fewer colors movement will be the higher rank.
                                cm.rank += 4 - cm.count;
                            }

                            // -----------------------------------
                            // end of ranking
                            // -----------------------------------
                            result++;
                        } // canMakeMove
                    }
                } // process the next donator 

                if (!emptyTubeProcessed) {
                    emptyTubeProcessed = ctRecipient.isEmpty();
                }
            }
        }

        if (result > 0) {
//            calculated = true;
            moves.sort(ColorMoveItem.RankComparator); // sort moves by rank! 
            currentMove = moves.get(moves.size() - 1);
        } else {
            currentMove = null;
        }

        return result;
    }

    /**
     * Doing move
     *
     * @param ctFrom Donator tube
     * @param ctTo Recipient tube
     * @return number of colors that were be moved
     */
    public int moveColor(TubeModel ctFrom, TubeModel ctTo) {
        int result = 0;
        if (canMakeMove(ctFrom, ctTo)) {
            int cnt = Math.min(ctFrom.colorsToGet(), 4 - ctTo.count);
            result = cnt;
            do {
                ctTo.putColor(ctFrom.extractColor());
                cnt--;
            } while (cnt > 0);
        }
        return result;
    }

    /**
     * Doing move
     *
     * @param idxFrom index of the Donator tube
     * @param idxTo index of the Recipient tube
     * @return number of colors that were be moved
     */
    public int moveColor(int idxFrom, int idxTo) {
        return moveColor(get(idxFrom), get(idxTo));
    }

    /**
     * Outs the current board to string
     *
     * @return string to output
     */
    @Override
    public String toString() {
        StringBuilder str;
        int s = this.size();

        str = new StringBuilder("  ");
        for (int j = 0; j < s; j++) {
            str.append(Integer.toHexString(j)).append("   ");
        }
        str.append("\n");

        for (int i = 3; i >= 0; i--) {
            str = new StringBuilder("| ");
            for (TubeModel tubeModel : this) {
                if (tubeModel.colors[i] != 0) {
                    str.append(Integer.toHexString(tubeModel.colors[i]));
                } else {
                    str.append(" ");
                }
                str.append(" | ");
            }
            str.append("\n");
        }
        return str.toString();
    }

}
