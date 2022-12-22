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

public class GameMoves extends ArrayList<Integer> {

    // tubeFrom = (move >> 24) & 0xff;
    // tubeTo = (move >> 16) & 0xff;
    // mCount = (move >> 8) & 0xff;
    // color = (move) & 0xff;

    public int getStoredMove(int index) {
        return get(index);
    }

    public void setStoredMove(int index, int storedMove) {
        this.set(index, storedMove);
    }

    public int getTubeFrom(int index) {
        return (get(index) >> 24) & 0xff;
    }

    public int getTubeTo(int index) {
        return (get(index) >> 16) & 0xff;
    }

    public int getMoveCount(int index) {
        return (get(index) >> 8) & 0xff;
    }

    public byte getColor(int index) {
        return (byte) (get(index) & 0xff);
    }

    public void addMove(int idxFrom, int idxTo, int count, byte color) {
        int temp = ((idxFrom & 0xff) << 24)
                + ((idxTo & 0xff) << 16)
                + ((count & 0xff) << 8)
                + (((int) color) & 0xff);
        this.add(temp);
    }

    public void addMove(int storedMove) {
        this.add(storedMove);
    }

    public void addMove(int index, int storedMove) {
        this.add(index, storedMove);
    }

    public void addMove(int index, int idxFrom, int idxTo, int count, byte color) {
        int temp = ((idxFrom & 0xff) << 24)
                + ((idxTo & 0xff) << 16)
                + ((count & 0xff) << 8)
                + (((int) color) & 0xff);
        this.add(index, temp);
    }

}
