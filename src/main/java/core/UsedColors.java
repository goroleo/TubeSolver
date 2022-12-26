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

import java.util.Arrays;
import java.util.Random;

/**
 * Class UsedColors counts colors usage on the board. It uses for filling tubes,
 * fot solving tubes, etc.
 * <br>
 * Please note, the Color Number 0 is the background color at Palette and the
 * transparent color at ColorTube. So it doesn't use in this class.
 */
public class UsedColors {

    /**
     * Colors' counts array. Its size is a palette size without the background
     * color.
     */
    private final int[] buf;

    /**
     * Class constructor.
     *
     * @param size size of the counts array
     */
    public UsedColors(int size) {
        buf = new int[size];
    }

    /**
     * Increases color's count by one.
     *
     * @param clr number of the palette's color
     */
    public void incColorCount(byte clr) {
        buf[clr - 1] += 1;
    }

    /**
     * Increases the number of colors by a specific value.
     *
     * @param clr number of the palette's color.
     * @param count value to increase color's use.
     */
    public void incColorCount(byte clr, int count) {
        buf[clr - 1] += count;
    }

    /**
     * Decreases color's count by one.
     *
     * @param clr number of the palette's color
     */
    public void decColorCount(byte clr) {
        buf[clr - 1] -= 1;
    }

    /**
     * Set the count value for a specific color.
     *
     * @param clr number of the palette's color
     * @param value new count
     */
    public void setColorCount(byte clr, int value) {
        buf[clr - 1] = value;
    }

    /**
     * Returns the specific color's count
     *
     * @param clr number of the palette's color
     * @return count of the specific color
     */
    public int getColorCount(byte clr) {
        return buf[clr - 1];
    }

    /**
     * Clears the specific color's count and set it to 0.
     *
     * @param clr number of the palette's color
     */
    public void clearColorCount(byte clr) {
        buf[clr - 1] = 0;
    }

    /**
     * Clears all colors' count.
     */
    public void clearAllColorCounts() {
        Arrays.fill(buf, 0);
    }

    /**
     * Returns the number of colors used on the board at least once.
     *
     * @return number of used colors.
     */
    public int getAllUsedColors() {
        int c = 0;
        for (int r : buf) {
            if (r > 0) {
                c++;
            }
        }
        return c;
    }

    /**
     * Returns the number of colors that have been fully applied, all 4 times.
     *
     * @return number of filled colors.
     */
    public int getAllFilledColors() {
        int c = 0;
        for (int r : buf) {
            if (r == 4) {
                c++;
            }
        }
        return c;
    }

    /**
     * Returns the count of all colors not used at the board.
     * @return count of all available colors.
     */
    public int getAvailableColors() {
        int c = 0;
        for (int r : buf) {
            c += (4 - r);
        }
        return c;
    }

    /**
     * Returns the random unused color.
     * @return Palette's color number
     */
    public byte getRandomColor() {
        Random random = new Random(System.currentTimeMillis());
        int clrIndex = random.nextInt(getAvailableColors());

        int temp = 0;
        int i = 0;

        do {
            temp += (4 - buf[i]);
            i++;
        } while (temp < clrIndex + 1);

        return (byte) i;
    }

}
