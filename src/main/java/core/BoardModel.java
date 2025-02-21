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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.CRC32;

import static gui.Palette.usedColors;

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
     * Hash is a CRC32 value to compare this board to others. Call calculateHash()
     * before using this value.
     *
     * @see #calculateHash()
     */
    public long hash;

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
     * @see TubeModel#getState()
     */
    public boolean isSolved() {
        for (TubeModel aTube : this) {
            if (aTube.getState() == 1 // STATE_REGULAR
                    || aTube.getState() == 2) { // STATE_FILLED
                return false;
            }
        }
        return true;
    }

    /**
     * Calculates the hash value of this board to compare it to others.
     * Using CRC32 algorithm.
     */
    public void calculateHash() {
        int s = this.size();

        // store and sort tubes
        int[] stored = new int[s];
        for (int i = 0; i < s; i++) {
            stored[i] = this.get(i).storeColors();
        }
        Arrays.sort(stored);

        // calculate crc32 checksum
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(bytes);
        try {
            for (int i = 0; i < s; i++) {
                data.write((stored[i] >> 24) & 0xff);
                data.write((stored[i] >> 16) & 0xff);
                data.write((stored[i] >> 8) & 0xff);
                data.write(stored[i] & 0xff);
            }
            data.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CRC32 crc = new CRC32();
        crc.update(bytes.toByteArray());
        hash = crc.getValue();
    }

    /*
     * Is this board equal to another tubes board? The routine stores both
     * boards to integer arrays then sort and compares them.
     *
     * @param tm another tubes board
     * @return true or false
     *
     * !! unused !! Use hash value instead

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

*/

    /**
     * Creates and adds new move to the move's list.
     *
     * @param idxFrom index of the donor tube
     * @param idxTo   index of the recipient tube
     * @return new ColorMove
     * @see ColorMoveItem
     */
    public ColorMoveItem addNewMove(int idxFrom, int idxTo) {
        if (moves == null) {
            moves = new ArrayList<>();
        }

        ColorMoveItem cm = new ColorMoveItem();
        cm.bmBefore = this;
        cm.color = get(idxFrom).getCurrentColor();
        cm.idxFrom = idxFrom;
        cm.idxTo = idxTo;

        moves.add(cm);
        currentMove = cm;
        return cm;
    }

    /**
     * Creates and adds new move to the move's list.
     *
     * @param ctFrom donor tube
     * @param ctTo   recipient tube
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
     * tube to another. Donor tube must be not empty and not closed.
     *
     * @param ctFrom donor tube
     * @param ctTo   recipient tube
     * @return true or false
     */
    public boolean canMakeMove(TubeModel ctFrom, TubeModel ctTo) {
        return (ctFrom.getState() == 1 // STATE_REGULAR
                || ctFrom.getState() == 2) // STATE_FILLED
                && ctTo.canPutColor(ctFrom.getCurrentColor());
    }

    /**
     * This routine checks the possibility to transfer a color cell from one
     * tube to another.
     *
     * @param idxFrom index of the donor tube
     * @param idxTo   index of the recipient tube
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
     * @see TubeModel#getState()
     */
    public void fillAvailableColors() {
        usedColors.clearColorCounts();
        for (TubeModel ct : this) {
            if (ct.getState() == 1 // STATE_REGULAR
                    || ct.getState() == 2) {      // STATE_FILLED
                usedColors.incColorCount(ct.getCurrentColor(), ct.colorsToGet());
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

        calculateHash();

        int dColorsToGet; // donor's ColorsToGet
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
        boolean emptyTubeProcessed = false; // true if one of empty tube has processed already

        fillAvailableColors();

        for (TubeModel ctRecipient : this) {

            if (ctRecipient.getState() == 1 // STATE_REGULAR
                    || (ctRecipient.getState() == 0 // STATE_EMPTY
                    && !emptyTubeProcessed)) { // one of empty tubes is passed already

                for (TubeModel ctDonor : this) {

                    if (ctDonor != ctRecipient) {

                        if (canMakeMove(ctDonor, ctRecipient)) {

                            cm = addNewMove(ctDonor, ctRecipient);
                            if (this != root) {
                                cm.parent = this.parentMove;
                            } else {
                                cm.parent = null;
                            }

                            dColorsToGet = ctDonor.colorsToGet();
                            rColorsToGet = ctRecipient.colorsToGet();

                            cm.count = Math.min(
                                    // empty cells at Recipient
                                    (4 - ctRecipient.getCount()),
                                    // number of donor's cells of this color
                                    dColorsToGet);

                            // -----------------------------------
                            // Rank the move! 
                            // -----------------------------------
                            cm.rank = Math.min(
                                    // number of empty cells at Recipient
                                    (4 - ctRecipient.getCount()),
                                    // number of available cells of this color at whole the board
                                    usedColors.getColorCount(cm.color));

                            if (ctRecipient.getCount() > 0 && ctRecipient.getCount() == rColorsToGet) {
                                // if the whole tube of the Recipient is filled by this color
                                cm.rank += 3;
                            }

                            if (dColorsToGet == ctDonor.getCount()) {
                                // if the whole tube of the Donor is filled by this color
                                cm.rank += 2;
                            }

                            if (dColorsToGet > 4 - ctRecipient.getCount()) {
                                // if the Donor tube is not completely emptied after the move
                                cm.rank -= 4;
                            }

                            if (dColorsToGet + rColorsToGet == 4) {
                                // if the donor and recipient will give a filled closed tube,
                                // the fewer colors movement will be the higher rank.
                                cm.rank += 4 - cm.count;
                            }

                            // -----------------------------------
                            // end of ranking
                            // -----------------------------------
                            result++;
                        } // canMakeMove
                    }
                } // process the next donor

                if (ctRecipient.isEmpty() && !emptyTubeProcessed) {
                    emptyTubeProcessed = true;
                }
            }
        } // process the next recipient

        if (result > 0) {
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
     * @param ctFrom Donor tube
     * @param ctTo   Recipient tube
     * @return number of colors that were be moved
     */
    public int moveColor(TubeModel ctFrom, TubeModel ctTo) {
        int result = 0;
        if (canMakeMove(ctFrom, ctTo)) {
            int cnt = Math.min(ctFrom.colorsToGet(), 4 - ctTo.getCount());
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
     * @param idxFrom index of the Donor tube
     * @param idxTo   index of the Recipient tube
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
                if (tubeModel.getColor(i) != 0) {
                    str.append(Integer.toHexString(tubeModel.getColor(i)));
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
