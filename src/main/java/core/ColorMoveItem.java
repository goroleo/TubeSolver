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

import java.util.Comparator;

/**
 * This is a class which keeps the data about every game move at the board.<br>
 * Every game move puts one color from one tube <i>(Donator)</i> to another tube <i>(Recipient)</i>.
 * Every move knows the board configuration before the move and after it. Every move knows a parent
 * (previous) move. Every move has a rank.
 */
public class ColorMoveItem {

    /**
     * This move's number in moves array.
     */
    @Deprecated
    public int number;     // Deprecated. Use array index instead

    /**
     * Number (index) of the tube where we'll get the color(s). A.k.a. color's
     * Donator.
     */
    public int idxFrom;

    /**
     * Number (index) of the tube where we'll put the color(s). A.k.a. color's
     * Recipient.
     */
    public int idxTo;

    /**
     * The rank of the current move's preference
     */
    public int rank;

    /**
     * How many colors have been moved.
     */
    public int count;

    /**
     * Number (index) of the moved color
     */
    public byte color;

    /**
     * The previous move, the parent of this move.
     */
    public ColorMoveItem parent;

    /**
     * Tubes board configuration before this move
     */
    public BoardModel bmBefore;

    /**
     * Tubes board configuration after this move
     */
    public BoardModel bmAfter;

    /**
     * Compare moves by rank
     */
    public static final Comparator<ColorMoveItem> RankComparator
            = Comparator.comparingInt((ColorMoveItem cm) -> cm.rank);

    /**
     * Doing move and check what we'll get after it. <br>
     * The move will be named successful if we have new move(s) after the move.
     *
     * @return true if move was successful
     */
    public boolean doMove() {
        boolean result;
        BoardModel bmOneOfParents;

        // creating tubes' board after the move 
        bmAfter = new BoardModel();
        bmAfter.root = bmBefore.root;
        bmAfter.parent = bmBefore;
//      bmAfter.level = bmBefore.level + 1; // level is deprecated
        bmAfter.parentMove = this;

        // and fill tubes from tubes before
        for (int i = 0; i < bmBefore.size(); i++) {
            bmAfter.addNewTube();
            bmAfter.get(i).assignColors(bmBefore.get(i));
        }

        // doing move and get how many colors have been moved
        count = bmAfter.moveColor(idxFrom, idxTo);
        result = (count > 0);

        if (!bmAfter.isSolved()) { // if not solved

            // now check what we've got after the move
            if (result) {
                // can we continue with a new configuration?
                // has any moves at new tubes board?
                result = bmAfter.calculateMoves() > 0;
            }

            if (result) {
                // is it new configuration? wasn't there before? 
                // are we going in circles?
                bmOneOfParents = bmBefore;
                do {
                    result = !bmAfter.equalsTo(bmOneOfParents);
                    bmOneOfParents = bmOneOfParents.parent;
                } while (result && bmOneOfParents != null);
            }
        }

        if (!result) {
            bmAfter = null; // this is for trash collector
        }

        return result;
    }

    /**
     * Store move's fields into one integer value
     *
     * @return stored integer
     */
    public int storeMove() {
        return ((idxFrom & 0xff) << 24)
                + ((idxTo & 0xff) << 16)
                + ((count & 0xff) << 8)
                + (((int) color) & 0xff);
    }

}
