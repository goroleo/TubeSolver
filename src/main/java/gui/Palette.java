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
import java.util.Properties;
import java.util.ArrayList;

public class Palette extends ArrayList<Color> {

    public static Color backgroundColor;
    public static Color dialogColor;
    public static UsedColors usedColors;
    public static Properties paletteProps;

    public Palette() {
        paletteProps = new Properties();

        if (!loadPalette()) {
            defaultPalette();
        }
        usedColors = new UsedColors(this.size()-1);
    }

    public Color getColor(int index) {
        return this.get(index);
    }

    public int getColorValue(int index) {
        return this.get(index).getRGB();
    }


    public final boolean loadPalette() {

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
        add(Palette.backgroundColor);
        hexColor = paletteProps.getProperty("dlg", "#ff1c1127");
        dialogColor = Options.colorFromHex(hexColor);
        for (int i = 0; i < colorsCount; i++) {
            hexColor = paletteProps.getProperty("color" + (i+1));
            add(Options.colorFromHex(hexColor));
        }
        return true;
    }

    public void savePalette() {
        String hexColor;
        paletteProps.setProperty("colors", Integer.toString(size()-1));
        paletteProps.setProperty("bg", "0x" + Integer.toHexString(backgroundColor.getRGB()));
        paletteProps.setProperty("dlg", "0x" + Integer.toHexString(dialogColor.getRGB()));
        for (int i = 0; i < size(); i++) {
            hexColor = Integer.toHexString(getColorValue(i));
            paletteProps.setProperty("color" + (i), "0x" + hexColor);
        }
        TubesIO.savePalette(paletteProps);
    }

    public final void defaultPalette() {
        this.clear();

        backgroundColor = new Color(0x282828);
        dialogColor = new Color(0x1c1127);

        this.add(backgroundColor);
        this.add(Options.colorFromHex("#ff38ff4d")); // 1
        this.add(Options.colorFromHex("#ff27deff")); // 2
        this.add(Options.colorFromHex("#ff8b5331")); // 3
        this.add(Options.colorFromHex("#ff92ad21")); // 4
        this.add(Options.colorFromHex("#ff757778")); // 5
        this.add(Options.colorFromHex("#ff006f00")); // 6
        this.add(Options.colorFromHex("#ff3632de")); // 7 
        this.add(Options.colorFromHex("#fff98f0f")); // 8
        this.add(Options.colorFromHex("#ffa6158c")); // 9
        this.add(Options.colorFromHex("#ffea1a0f")); // 10
        this.add(Options.colorFromHex("#fffd68b3")); // 11
        this.add(Options.colorFromHex("#fffff03e")); // 12
    }

}
