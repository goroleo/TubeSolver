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

import core.Options;
import core.TubesIO;
import core.UsedColors;

import java.awt.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Palette of the game colors
 */
public class Palette extends ArrayList<Color> {

    /**
     * Background color for the application
     */
    public static Color backgroundColor;

    /**
     * Background color for dialogs
     */
    public static Color dialogColor;

    /**
     * Counters of used colors at the board
     */
    public static UsedColors usedColors;
    private final Properties paletteProps;

    public Palette() {
        paletteProps = new Properties();

        if (!loadPalette()) {
            setDefaultPalette();
        }
        usedColors = new UsedColors(this.size() - 1);
    }

    /**
     * Gets color from the palette
     *
     * @param index - color index (note that color number 0 is a background color)
     * @return color from the palette
     */
    public Color getColor(int index) {
        return this.get(index);
    }

    /**
     * Loads the current palette from the user work folder
     */
    private boolean loadPalette() {

        if (!TubesIO.loadPalette(paletteProps)) {
            return false;
        }

        String hexColor;
        int colorsCount = Integer.parseInt(paletteProps.getProperty("colors", "12"));
        if (colorsCount != 12) {
            return false;
        }
        clear();
        hexColor = paletteProps.getProperty("bg", "#ff282828");
        backgroundColor = Options.colorFromHex(hexColor);
        add(Palette.backgroundColor); // adds color number 0
        hexColor = paletteProps.getProperty("dlg", "#ff1c1127");
        dialogColor = Options.colorFromHex(hexColor);
        for (int i = 0; i < colorsCount; i++) {
            hexColor = paletteProps.getProperty("color" + (i + 1));
            add(Options.colorFromHex(hexColor));
        }
        return true;
    }

    /**
     * Saves the current palette to the user work folder
     */
    public void savePalette() {
        String hexColor;
        paletteProps.setProperty("colors", Integer.toString(size() - 1));
        paletteProps.setProperty("bg", "0x" + Integer.toHexString(backgroundColor.getRGB()));
        paletteProps.setProperty("dlg", "0x" + Integer.toHexString(dialogColor.getRGB()));
        for (int i = 1; i < size(); i++) {
            hexColor = Integer.toHexString(getColor(i).getRGB());
            paletteProps.setProperty("color" + i, "0x" + hexColor);
        }
        TubesIO.savePalette(paletteProps);
    }

    /**
     * Sets the default palette colors
     */
    public void setDefaultPalette() {
        this.clear();

        backgroundColor = new Color(0x282828);
        dialogColor = new Color(0x1c1127);

        this.add(backgroundColor);
        this.add(Options.colorFromHex("#ff38ff4d")); // 1
        this.add(Options.colorFromHex("#ff1dd3f4")); // 2
        this.add(Options.colorFromHex("#ff884822")); // 3
        this.add(Options.colorFromHex("#ff8eaf00")); // 4
        this.add(Options.colorFromHex("#ff737f8c")); // 5
        this.add(Options.colorFromHex("#ff067606")); // 6
        this.add(Options.colorFromHex("#ff3632de")); // 7 
        this.add(Options.colorFromHex("#fff36d00")); // 8
        this.add(Options.colorFromHex("#ffaf008f")); // 9
        this.add(Options.colorFromHex("#ffe60f04")); // 10
        this.add(Options.colorFromHex("#ffff7abc")); // 11
        this.add(Options.colorFromHex("#ffffeb04")); // 12
    }

}
