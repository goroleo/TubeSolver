/*
 * Copyright (c) 2022 legoru / goroleo <legoru@me.com>
 * 
 * This software is distributed under the <b>MIT License.</b>
 * The full text of the License you can read here: 
 * https://choosealicense.com/licenses/mit/
 * 
 * Use this as you want! ))
 */
package core;

import java.util.ArrayList;

/**
 * This is an array to store all completed or prepared moves for the current game.
 * Unlike ColorMoveItem, this array cannot be used to calculate and solve the game.
 * This is just the store of statistics of passed or future moves. <br>
 * Each integer value of the move consists of 4 bytes (from high to low):<ul>
 *     <li> <b>tubeFrom</b> - number of the tube that lost the color cell(s).
 *     <li> <b>tubeTo</b> - number of the tube that get the color cell(s).
 *     <li> <b>movesCount</b> - how many colored cells were moved during this move.
 *     <li> <b>color</b> - color number (from the palette) of moved cell(s).
 * </ul>
 */
@SuppressWarnings("unused")
public class GameMoves extends ArrayList<Integer> {

    /*
       * tubeFrom = (move >> 24) & 0xff;
       * tubeTo = (move >> 16) & 0xff;
       * movesCount = (move >> 8) & 0xff;
       * color = (move) & 0xff;
     */

    /**
     * Gets the move by its index.
     * @param index index of the move
     * @return integer value stored the move data
     */
    public int getStoredMove(int index) {
        return get(index);
    }

    /**
     * Sets the move by its index.
     * @param index index of the move
     * @param storedMove an integer value stored the move data
     */
    public void setStoredMove(int index, int storedMove) {
        this.set(index, storedMove);
    }

    /**
     * Gets the tubeFrom value from the move with specific index.
     * @param index index of the move
     * @return tubeFrom
     */
    public int getTubeFrom(int index) {
        return (get(index) >> 24) & 0xff;
    }

    /**
     * Gets the tubeTo value from the move with specific index.
     * @param index index of the move
     * @return tubeTo
     */
    public int getTubeTo(int index) {
        return (get(index) >> 16) & 0xff;
    }

    /**
     * Gets the moveCount value from the move with specific index.
     * @param index index of the move
     * @return moveCount
     */
    public int getMoveCount(int index) {
        return (get(index) >> 8) & 0xff;
    }

    /**
     * Gets the Color value from the move with specific index.
     * @param index index of the move
     * @return color number
     */
    public byte getColor(int index) {
        return (byte) (get(index) & 0xff);
    }

    /**
     * Adds a game move to the end of the moves array.
     * @param idxFrom index (number) of the tubeFrom.
     * @param idxTo index (number) of the tubeTo.
     * @param count movesCount - how many colored cells were moved during this move.
     * @param color color number (from the palette) of moved cell(s).
     */
    public void addMove(int idxFrom, int idxTo, int count, byte color) {
        this.add(((idxFrom & 0xff) << 24)
                + ((idxTo & 0xff) << 16)
                + ((count & 0xff) << 8)
                + (((int) color) & 0xff));
    }

    /**
     * Adds a game move to the specified place of the moves array.
     * @param index an array index to place this move before it.
     * @param idxFrom index (number) of the tubeFrom.
     * @param idxTo index (number) of the tubeTo.
     * @param count movesCount - how many colored cells were moved during this move.
     * @param color color number (from the palette) of moved cell(s).
     */
    public void addMove(int index, int idxFrom, int idxTo, int count, byte color) {
        this.add(index, ((idxFrom & 0xff) << 24)
                + ((idxTo & 0xff) << 16)
                + ((count & 0xff) << 8)
                + (((int) color) & 0xff));
    }

    /**
     * Adds a game move to the end of the moves array.
     * @param storedMove an integer value that already has the stored move
     */
    public void addMove(int storedMove) {
        this.add(storedMove);
    }

    /**
     * Adds a game move to the specified place of the moves array.
     * @param index an array index to place this move before it.
     * @param storedMove an integer value that already has the stored move
     */
    public void addMove(int index, int storedMove) {
        this.add(index, storedMove);
    }

}
