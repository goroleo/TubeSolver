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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Properties;

/**
 * This class loads, stores and saves all application options and settings. Also, it is
 * the set of additional & auxiliary routines of the application.
 */
public class Options {

// --------- Application options -------------

// --------- language -------------
    /**
     * A current language code, the three-letters ISO 639-2 language code.
     *
     * @see ResStrings#getLangCode(int)
     */
    public static String langCode = Locale.getDefault().getISO3Language();

// --------- auto save options -------------
    /**
     * A feature to suggest to save the game after (auto/manual) filling the board.
     */
    public static boolean saveGameAfterFill = true;

    /**
     * A feature to suggest to save the game after finding the game solution.
     */
    public static boolean saveGameAfterSolve = false;

    /**
     * A feature to suggest to save the game before closing the application.
     */
    public static boolean saveGameBeforeClose = false;

// --------- main frame position -------------
    /**
     * Stores the maximized state of the main frame.
     */
    public static boolean mainMaximized = false;

    /**
     * Stores the horizontal position of the main frame.
     */
    public static int mainPositionX = -1;

    /**
     * Stores the vertical position of the main frame.
     */
    public static int mainPositionY = -1;

    /**
     * Stores the horizontal size (width) of the main frame.
     */
    public static int mainSizeX = -1;

    /**
     * Stores the vertical size (height) of the main frame.
     */
    public static int mainSizeY = -1;

// --------- main frame: palette panel -------------
    /**
     * Stores the number of lines of the palette panel.
     */
    public static int palLines = 0;

    /**
     * Stores the docked state number of the palette panel.
     *
     * @see gui.PalettePanel#getDockedTo()
     */
    public static int palDockedTo = -1;

// --------- main frame: color tubes panel -------------
    /**
     * Stores the number of lines of the color tubes (game board) panel.
     */
    public static int boardLines = 0;

    /**
     * Stores the docked state number of the board panel.
     *
     * @see gui.BoardPanel#getDockedTo()
     */
    public static int boardDockedTo = -1;

// --------- main frame: tools panel -------------
    /**
     * Stores the alignment of the tools panel.
     *
     * @see gui.ToolPanel#getAlignment()
     */
    public static int menuAlignment = -1;

    /**
     * Stores the docked state number of the tools panel.
     *
     * @see gui.ToolPanel#getDockedTo()
     */
    public static int menuDockedTo = -1;

// --------- create new dialog options -------------
    /**
     * Stores the last used number of filled tubes.
     */
    public static int cndFilledTubes = 0;

    /**
     * Stores the last used number of empty tubes.
     */
    public static int cndEmptyTubes = 0;

// --------- color picker dialog options -------------
    /**
     * Stores the horizontal position of the color picker dialog.
     */
    public static int ccdPositionX = -1;

    /**
     * Stores the vertical position of the color picker dialog.
     */
    public static int ccdPositionY = -1;

    /**
     * Stores the color picker dialog mode.
     *
     * @see lib.lColorDialog.ColorPanel#getDialogMode()
     */
    public static int ccdDialogMode = 0;

// --------- open/save dialog position & options -------------
    /**
     * Stores the horizontal position of the open/save dialog.
     */
    public static int osdPositionX = -1; // dialog X position

    /**
     * Stores the vertical position of the open/save dialog.
     */
    public static int osdPositionY = -1; // dialog Y position

    /**
     * Stores the horizontal size (width) of the open/save dialog.
     */
    public static int osdSizeX = -1;     // dialog Frame Width

    /**
     * Stores the vertical size (height) of the open/save dialog.
     */
    public static int osdSizeY = -1;     // dialog Frame Height

    /**
     * Stores the width of the FileName column (open/save dialog).
     */
    public static int osdSizeColN = -1;   // FileName column width

    /**
     * Stores the width of the FileSize column (open/save dialog).
     */
    public static int osdSizeColS = -1;   // FileSize column width

    /**
     * Stores the width of the FileDate column (open/save dialog).
     */
    public static int osdSizeColD = -1;   // FileDate column width

    /**
     * Stores the file sorting column number (open/save dialog).
     */
    public static int osdSortCol = -1;    // Column to sort the file list

    /**
     * Stores the file sorting order (open/save dialog).
     * 1 - ascending order, 0 - descending.
     *
     * @see lib.lOpenSaveDialog.OpenSavePanel#getFileSortAscending()
     */
    public static int osdSortOrder = -1;  // sort order (1 - ascending, 0 - descending)

    /**
     * Stores the current folder for the open/save dialog.
     */
    public static String osdCurrentDir = ""; // An absolute path to the current folder

// --------- palette dialog -------------
    /**
     * Stores the horizontal position of the palette dialog.
     */
    public static int pdPositionX = -1;

    /**
     * Stores the vertical position of the palette dialog.
     */
    public static int pdPositionY = -1;

    /**
     * Stores the Show Changes checkbox state of the palette dialog.
     */
    public static boolean pdShowChanges = true;

// --------- options dialog -------------
    /**
     * Stores the horizontal position of the options' dialog.
     */
    public static int odPositionX = -1;

    /**
     * Stores the vertical position of the options' dialog.
     */
    public static int odPositionY = -1;


// --------- Options routines  -------------

    /**
     * Saves application settings.
     */
    public static void saveOptions() {
        Properties sProps = new Properties();
        // --------- language -------------
        sProps.setProperty("Language", langCode);
        // --------- save options -------------
        sProps.setProperty("SaveGameAfterFill", (saveGameAfterFill) ? "1" : "0");
        sProps.setProperty("SaveGameAfterSolve", (saveGameAfterSolve) ? "1" : "0");
        sProps.setProperty("SaveGameBeforeClose", (saveGameBeforeClose) ? "1" : "0");
        // --------- main frame position -------------
        sProps.setProperty("MainMaximized", (mainMaximized) ? "1" : "0");
        sProps.setProperty("MainPosX", Integer.toString(mainPositionX));
        sProps.setProperty("MainPosY", Integer.toString(mainPositionY));
        sProps.setProperty("MainSizeX", Integer.toString(mainSizeX));
        sProps.setProperty("MainSizeY", Integer.toString(mainSizeY));
        // --------- main frame: palette panel -------------
        sProps.setProperty("PaletteLines", Integer.toString(palLines));
        sProps.setProperty("PaletteDockedTo", Integer.toString(palDockedTo));
        // --------- main frame: tubes panel -------------
        sProps.setProperty("BoardLines", Integer.toString(boardLines));
        sProps.setProperty("BoardDockedTo", Integer.toString(boardDockedTo));
        // --------- main frame: tools panel -------------
        sProps.setProperty("MenuAlign", Integer.toString(menuAlignment));
        sProps.setProperty("MenuDockedTo", Integer.toString(menuDockedTo));
        // --------- create new dialog -------------
        sProps.setProperty("CreateNewFilled", Integer.toString(cndFilledTubes));
        sProps.setProperty("CreateNewEmpty", Integer.toString(cndEmptyTubes));
        // --------- color picker dialog -------------
        sProps.setProperty("ColorDialogPosX", Integer.toString(ccdPositionX));
        sProps.setProperty("ColorDialogPosY", Integer.toString(ccdPositionY));
        sProps.setProperty("ColorDialogMode", Integer.toString(ccdDialogMode));
        // --------- open/save dialog -------------
        sProps.setProperty("OpenSaveDialogPosX", Integer.toString(osdPositionX));
        sProps.setProperty("OpenSaveDialogPosY", Integer.toString(osdPositionY));
        sProps.setProperty("OpenSaveDialogSizeX", Integer.toString(osdSizeX));
        sProps.setProperty("OpenSaveDialogSizeY", Integer.toString(osdSizeY));
        sProps.setProperty("OpenSaveDialogColN", Integer.toString(osdSizeColN));
        sProps.setProperty("OpenSaveDialogColS", Integer.toString(osdSizeColS));
        sProps.setProperty("OpenSaveDialogColD", Integer.toString(osdSizeColD));
        sProps.setProperty("OpenSaveDialogDir", osdCurrentDir);
        sProps.setProperty("OpenSaveDialogSortColumn", Integer.toString(osdSortCol));
        sProps.setProperty("OpenSaveDialogSortOrder", Integer.toString(osdSortOrder));
        // --------- palette dialog -------------
        sProps.setProperty("PaletteDialogPosX", Integer.toString(pdPositionX));
        sProps.setProperty("PaletteDialogPosY", Integer.toString(pdPositionY));
        sProps.setProperty("PaletteDialogShowChanges", Integer.toString((pdShowChanges) ? 1 : 0));
        // --------- options dialog -------------
        sProps.setProperty("OptionsDialogPosX", Integer.toString(odPositionX));
        sProps.setProperty("OptionsDialogPosY", Integer.toString(odPositionY));

        TubesIO.saveOptions(sProps);
        sProps.clear();
    }

    /**
     * Loads application settings.
     */
    public static void loadOptions() {
        Properties sProps = new Properties();
        if (TubesIO.loadOptions(sProps)) {

            // --------- language -------------
            langCode = sProps.getProperty("Language", Locale.getDefault().getISO3Language());
            URL u = Options.class.getResource("/lang/" + langCode + ".properties");
            if (u == null) {
                langCode = Locale.getDefault().getISO3Language();
                u = Options.class.getResource("/lang/" + langCode + ".properties");
                if (u == null) {
                    langCode = "eng";
                }
            }
            // --------- auto save options -------------
            saveGameAfterFill = Integer.parseInt(sProps.getProperty("SaveGameAfterFill", "1")) == 1;
            saveGameAfterSolve = Integer.parseInt(sProps.getProperty("SaveGameAfterSolve", "0")) == 1;
            saveGameBeforeClose = Integer.parseInt(sProps.getProperty("SaveGameBeforeClose", "0")) == 1;
            // --------- main frame position -------------
            mainMaximized = Integer.parseInt(sProps.getProperty("MainMaximized", "-1")) == 1;
            mainPositionX = Integer.parseInt(sProps.getProperty("MainPosX", "-1"));
            mainPositionY = Integer.parseInt(sProps.getProperty("MainPosY", "-1"));
            mainSizeX = Integer.parseInt(sProps.getProperty("MainSizeX", "-1"));
            mainSizeY = Integer.parseInt(sProps.getProperty("MainSizeY", "-1"));
            // --------- main frame: palette panel -------------
            palLines = Integer.parseInt(sProps.getProperty("PaletteLines", "0"));
            palDockedTo = Integer.parseInt(sProps.getProperty("PaletteDockedTo", "-1"));
            // --------- main frame: tubes panel -------------
            boardLines = Integer.parseInt(sProps.getProperty("BoardLines", "0"));
            boardDockedTo = Integer.parseInt(sProps.getProperty("BoardDockedTo", "-1"));
            // --------- main frame: tools panel -------------
            menuAlignment = Integer.parseInt(sProps.getProperty("MenuAlign", "-1"));
            menuDockedTo = Integer.parseInt(sProps.getProperty("MenuDockedTo", "-1"));
            // --------- create new dialog -------------
            cndFilledTubes = Integer.parseInt(sProps.getProperty("CreateNewFilled", "12"));
            cndEmptyTubes = Integer.parseInt(sProps.getProperty("CreateNewEmpty", "2"));
            // --------- color picker dialog -------------
            ccdPositionX = Integer.parseInt(sProps.getProperty("ColorDialogPosX", "-1"));
            ccdPositionY = Integer.parseInt(sProps.getProperty("ColorDialogPosY", "-1"));
            ccdDialogMode = Integer.parseInt(sProps.getProperty("ColorDialogMode", "0"));
            // --------- open/save dialog -------------
            osdPositionX = Integer.parseInt(sProps.getProperty("OpenSaveDialogPosX", "-1"));
            osdPositionY = Integer.parseInt(sProps.getProperty("OpenSaveDialogPosY", "-1"));
            osdSizeX = Integer.parseInt(sProps.getProperty("OpenSaveDialogSizeX", "-1"));
            osdSizeY = Integer.parseInt(sProps.getProperty("OpenSaveDialogSizeY", "-1"));
            osdSizeColS = Integer.parseInt(sProps.getProperty("OpenSaveDialogColN", "-1"));
            osdSizeColS = Integer.parseInt(sProps.getProperty("OpenSaveDialogColS", "-1"));
            osdSizeColD = Integer.parseInt(sProps.getProperty("OpenSaveDialogColD", "-1"));
            osdCurrentDir = sProps.getProperty("OpenSaveDialogDir", "");
            osdSortCol = Integer.parseInt(sProps.getProperty("OpenSaveDialogSortColumn", "-1"));
            osdSortOrder = Integer.parseInt(sProps.getProperty("OpenSaveDialogSortOrder", "-1"));
            // --------- palette dialog -------------
            pdPositionX = Integer.parseInt(sProps.getProperty("PaletteDialogPosX", "-1"));
            pdPositionY = Integer.parseInt(sProps.getProperty("PaletteDialogPosY", "-1"));
            pdShowChanges
                    = Integer.parseInt(sProps.getProperty("PaletteDialogShowChanges", "1")) == 1;
            // --------- options dialog -------------
            odPositionX = Integer.parseInt(sProps.getProperty("OptionsDialogPosX", "-1"));
            odPositionY = Integer.parseInt(sProps.getProperty("OptionsDialogPosY", "-1"));
        }
    }

    /**
     * An icon showing the selected state for any CheckBoxes and Menus at all parts of the application.
     */
    public static final ImageIcon cbIconSelected
            = createImageIcon("checkbutton_icon_selected.png");     // Icon_Selected for menus 

    /**
     * An icon showing the non_selected state for any CheckBoxes and Menus at all parts of the application.
     */
    public static final ImageIcon cbIconStandard
            = createImageIcon("checkbutton_icon_standard.png");     // Icon_Selected for menus 

    /**
     * Enumeration of possible operating systems.
     */
    public enum OS {
        WINDOWS, LINUX, MAC, SOLARIS
    } // Operating systems.

    /**
     * Stores the current operating system.
     */
    private static OS os = null;

    /**
     * Gets the current operating system.
     */
    private static OS getOS() {
        if (os == null) {
            String opSys = System.getProperty("os.name").toLowerCase();
            if (opSys.contains("win")) {
                os = OS.WINDOWS;
            } else if (opSys.contains("nix") || opSys.contains("nux")
                    || opSys.contains("aix")) {
                os = OS.LINUX;
            } else if (opSys.contains("mac")) {
                os = OS.MAC;
            } else if (opSys.contains("sunos")) {
                os = OS.SOLARIS;
            }
        }
        return os;
    }

    /**
     * Gets the main frame icon depends on the current OS;
     *
     * @return application icon image
     */
    public static Image getAppIcon() {
        switch (getOS()) {
            case MAC:
            case LINUX:
                return createImage("appicon_48.png");
            default:
                return createImage("appicon_32.png");
        }
    }

    /**
     * Loads Icon from the resources.
     *
     * @param fName file name at /resources/img/ folder. fName is usually a PNG picture.
     * @return Icon or null if resource has not found.
     */
    public static ImageIcon createImageIcon(String fName) {
        return createImageIcon(TubesIO.getImageResourceURL(fName));
    }

    /**
     * Loads Icon from the resources.
     *
     * @param imgURL the link to the file at /resources/img/ folder.
     * @return Icon or null if resource has not found.
     */
    public static ImageIcon createImageIcon(java.net.URL imgURL) {
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("core.Options.createImageIcon: Couldn't find file.");
            return null;
        }
    }

    /**
     * Loads Image from the resources.
     *
     * @param fName file name at /resources/img/ folder. fName is usually a PNG picture.
     * @return Image or null if resource has not found.
     */
    public static Image createImage(String fName) {
        return createImage(TubesIO.getImageResourceURL(fName));
    }

    /**
     * Loads Image from the resources.
     *
     * @param imgURL the link to the file at /resources/img/ folder.
     * @return Image or null if resource has not found.
     */
    public static Image createImage(java.net.URL imgURL) {
        return createBufImage(imgURL);
    }

    /**
     * Loads BufferedImage from the resources.
     *
     * @param fName file name at /resources/img/ folder. fName is usually a PNG picture.
     * @return BufferedImage or null if resource has not found.
     */
    public static BufferedImage createBufImage(String fName) {
        return createBufImage(TubesIO.getImageResourceURL(fName));
    }

    /**
     * Loads BufferedImage from the resources.
     *
     * @param imgURL the link to the file at /resources/img/ folder.
     * @return BufferedImage or null if resource has not found.
     */
    public static BufferedImage createBufImage(java.net.URL imgURL) {
        BufferedImage img = null;
        if (imgURL != null) {
            try {
                img = ImageIO.read(imgURL);
            } catch (IOException ex) {
                System.err.println("core.Options.createBufImage: Error while loading file: " + imgURL);
            }
        } else {
            System.err.println("core.Options.createBufImage: Couldn't find file.");
        }
        return img;
    }

    /**
     * Converts a color from a hexadecimal color notation to the color used by the application.
     * The hexadecimal string of color notation usually looks like 0xRRGGBB or 0xAARRGGBB
     * (where A - Alpha, R - Red, G - Green, B - Blue). Also, the string can start with the '#' sign.
     *
     * @param Hex the hexadecimal string of color notation.
     * @return Color
     */
    public static Color colorFromHex(String Hex) {
        int index = 0;
        int radix = 0;
        int result;
        if (Hex.startsWith("0x", index) || Hex.startsWith("0X", index)) {
            index += 2;
            radix = 16;
        } else if (Hex.startsWith("#", index)) {
            index++;
            radix = 16;
        }
        Hex = Hex.substring(index);
        if (Hex.length() > 6) {
            Hex = Hex.substring(Hex.length() - 6);
        }
        result = Integer.parseInt(Hex, radix);
        return new Color(result, false);
    }

    /**
     * Adds leading '0' signs to the string if it is less than the required length.
     *
     * @param str            the original string
     * @param requiredLength as is
     * @return new string with leading '0' signs
     */
    public static String leadZero(String str, int requiredLength) {
        StringBuilder result = new StringBuilder(str);
        for (int i = result.length(); i < requiredLength; i++) {
            result.insert(0, "0");
        }
        return result.toString();
    }

    /**
     * Makes the string with the current date and time: YYYY-MM-DD HH-MM-SS.
     *
     * @return new string with the date-time stamp.
     */
    public static String getDateTimeStr() {
        LocalDateTime dt = LocalDateTime.now();
        return dt.getYear() + "-"
                + leadZero(Integer.toString(dt.getMonthValue()), 2) + "-"
                + leadZero(Integer.toString(dt.getDayOfMonth()), 2) + " "
                + leadZero(Integer.toString(dt.getHour()), 2) + "-"
                + leadZero(Integer.toString(dt.getMinute()), 2) + "-"
                + leadZero(Integer.toString(dt.getSecond()), 2);
    }


}
