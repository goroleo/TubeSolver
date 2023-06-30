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

/**
 * Color tube has 4 cells of colors. The tube can be as filled by different or
 * same colors, as it can be empty. Colors can be <b>extracted</b> from the tube,
 * and <b>put</b> into again. This class describes what the tube has to do.
 * This is a logical model of the tube without any visualization.
 *
 * @see #putColor
 * @see #extractColor
 * @see #state
 */
public class TubeModel {

    /**
     * Count of the filled colors.
     */
    private int count = 0;

    /**
     * Color cells. Array of color numbers which are in this tube now. Note
     * color number 0 is an empty cell.
     */
    private final byte[] colors = new byte[4];

    /**
     * The state of the tube. The states can be as follows: <ul>
     * <li><b>STATE_EMPTY</b> (0). The tube has no colors. <br>
     * <li><b>STATE_REGULAR</b> (1). The tube has different colors and their count
     * less than 4. So this tube can both put and extract a color.
     * <li><b>STATE_FILLED</b> (2). The whole tube is filled with different colors.
     * It can extract a color, but there is no place to put them more.
     * <li><b>STATE_CLOSED</b> (3). The whole tube is filled with one color. So
     * this tube is no longer in the game.<br></ul>
     * I don't want to add neither another class no list of constants for the
     * <b>state</b>. I just use an integer value.
     */
    private int state = 0;

    /**
     * Current color of this tube. Current color is always the top color of the tube.
     */
    private byte currentColor = 0;

    /**
     * Returns the count of the filled colors.
     *
     * @return colors count
     */
    public int getCount() {
        return count;
    }

    /**
     * Returns the state of the tube. The states can be as follows: <ul>
     * <li><b>STATE_EMPTY</b> (0). The tube has no colors. <br>
     * <li><b>STATE_REGULAR</b> (1). The tube has different colors and their count
     * less than 4. So this tube can both put and extract a color.
     * <li><b>STATE_FILLED</b> (2). The whole tube is filled with different colors.
     * It can extract a color, but there is no place to put them more.
     * <li><b>STATE_CLOSED</b> (3). The whole tube is filled with one color. So
     * this tube is no longer in the game.<br></ul>
     * I don't want to add neither another class no list of constants for the
     * <b>state</b>. I just use an integer value.
     *
     * @return current state of the tube
     */
    public int getState() {
        return state;
    }

    /**
     * Returns the count of the filled colors.
     *
     * @return colors count
     */
    public byte getCurrentColor() {
        return currentColor;
    }

    /**
     * Gets color cell. Array of color numbers which are in this tube now. Note
     * color number 0 is an empty cell.
     * @param idx index must be from 0 to colors count
     * @return color number of the palette
     */
    public byte getColor(int idx) {
        if (idx >= 0 && idx < count)
            return colors[idx];
        else return 0;
    }

    /**
     * Is this tube empty?
     *
     * @return true if the tube hasn't any colors
     */
    public boolean isEmpty() {
        return count == 0;
    }

    /**
     * Is this tube closed?
     *
     * @return true if all the tube's cells are fulled by one color, false otherwise.
     */
    public boolean isClosed() {
        if (count != 4) {
            return false;
        }
        int tempColor = colors[0];
        for (int i = 1; i < 4; i++) {
            if (colors[i] != tempColor) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculates current state of the tube and updates the <b>state</b>
     * variable. The possible states are described above.
     *
     * @see #state
     */
    public void updateState() {
        switch (count) {
            case 0:
                state = 0;             // STATE_EMPTY
                return;
            case 4:
                if (isClosed()) {
                    state = 3;         // STATE_CLOSED
                } else {
                    state = 2;         // STATE_FILLED
                }
                return;
            default:
                state = 1;             // STATE_REGULAR
        }
    }

    /**
     * Can this color be put into this tube.
     *
     * @param value color number
     * @return true if the tube can accept this color, false otherwise
     */
    public boolean canPutColor(byte value) {
        if (value == 0) {
            return false;
        }

        switch (state) {
            case 0:   // STATE_EMPTY
                return true;
            case 2:   // STATE_FILLED
            case 3:   // STATE_CLOSED 
                return false;
            default:  // STATE_REGULAR
                return value == currentColor;
        }
    }

    /**
     * Puts the color into the tube. Please note this routine don't check the
     * possibility of the operation that's why it can be also used for initial
     * filling the tube.
     *
     * @param value color number
     * @return true if the operation was successful, false otherwise
     */
    public boolean putColor(byte value) {
        if (count == 4 || value == 0) {
            return false;
        }

        colors[count] = value;
        currentColor = value;
        count++;

        // update tube's state
        if (count < 4) {
            state = 1; // STATE_REGULAR
        } else if (isClosed()) {
            state = 3; // STATE_CLOSED
        } else {
            state = 2; // STATE_FILLED
        }
        return true;
    }

    /**
     * Extracts one color from the tube. Only the top color can be extracted.
     * Please note this routine don't check the possibility of the operation.
     *
     * @return number of the color being extracted
     */
    public byte extractColor() {
        if (count == 0) {
            return 0;
        }

        count--;
        byte result = colors[count];
        colors[count] = 0;

        // update tube's state
        if (count == 0) {
            currentColor = 0;
            state = 0; // STATE_EMPTY;
        } else {
            currentColor = colors[count - 1];
            state = 1; // STATE_REGULAR;
        }
        return result;
    }

    /**
     * How much colors we can get from this tube at the one move.
     * If the tube has several cells with the same color following one by one,
     * then these colors can be got at once.
     *
     * @return at least 1
     */
    public int colorsToGet() {
        if (count == 0) {
            return 0;
        }

        int result = 0;
        int i = count;
        do {
            result++;
            i--;
        } while (i > 0 && currentColor == colors[i - 1]);

        return result;
    }

    /**
     * Clears the tube.
     */
    public void clear() {
        count = 0;
        currentColor = 0;
        state = 0; // STATE_EMPTY;
        for (int i = 0; i < 4; i++) {
            colors[i] = 0;
        }
    }

    /**
     * Is the tube have a specific color? Use this as the trigger to redraw the
     * tube when the color (or palette) was changed.
     *
     * @param value color number
     * @return true if the tube has this color, false otherwise
     */
    public boolean hasColor(byte value) {
        if (count == 0 || value == 0) {
            return false;
        }
        boolean result = false;
        for (int i = 0; i < count; i++) {
            if (colors[i] == value) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Stores the colors array into one integer variable - 4 bytes. One byte by one color.
     *
     * @return integer value of the stored colors.
     */
    public int storeColors() {
        int result = 0;
        for (int i = 4; i > 0; i--) {
            result = (result << 8) + colors[i - 1];
        }
        return result;
    }

    /**
     * Fills this tube colors' array from the stored integer variable.
     *
     * @param storedColors integer value of the stored colors.
     * @see #storeColors
     */
    public void assignColors(int storedColors) { // 
        count = 0;
        for (int i = 0; i < 4; i++) {
            colors[i] = (byte) storedColors;
            if (colors[i] != 0) {
                count++;
            }
            storedColors >>= 8;
        }
        if (count > 0) {
            currentColor = colors[count - 1];
        } else {
            currentColor = 0;
        }
        updateState();
    }

    /**
     * Fills this tube colors' array from another tube. Used for copying specified
     * tube to this tube.
     *
     * @param tmFrom the donor tube
     */
    public void assignColors(TubeModel tmFrom) {
        System.arraycopy(tmFrom.colors, 0, this.colors, 0, 4);
        this.currentColor = tmFrom.currentColor;
        this.count = tmFrom.count;
        this.state = tmFrom.state;
    }

    /**
     * Compares this tube with the another tube.
     *
     * @param tm another tube to compare.
     * @return true if both the tubes are consists of the same colors.
     */
    @SuppressWarnings("unused")
    public boolean equalsTo(TubeModel tm) {
        for (int i = 0; i < 4; i++) {
            if (this.colors[i] != tm.colors[i]) {
                return false;
            }
        }
        return true;
    }

}
