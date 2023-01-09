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

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import static java.lang.System.err;
import static java.lang.System.getProperty;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;
import static javax.imageio.ImageIO.read;
import javax.swing.ImageIcon;

public class Options {


    /*
        saving options
     */
    public static boolean saveGameAfterFill = true;
    public static boolean saveGameAfterSolve = true;
    public static boolean saveGameAfterWin = true;

    /*
        language
     */
    public static String langCode = "eng";

    /*
        MainFrame options 
     */
    public static boolean mainMaximized = false;
    public static int mainPositionX = -1;
    public static int mainPositionY = -1;
    public static int mainSizeX = -1;
    public static int mainSizeY = -1;

    /*
        CreateNewDialog options 
     */
    public static int cndFilledTubes = 0;
    public static int cndEmptyTubes = 0;

    /*
        ColorChange dialog options 
     */
    public static int ccdPositionX = -1;
    public static int ccdPositionY = -1;

    /*
        OpenSave dialog options 
     */
    public static int osdPositionX = -1;
    public static int osdPositionY = -1;
    public static int osdSizeX = -1;
    public static int osdSizeY = -1;
    public static int osdSizeColN = -1;
    public static int osdSizeColS = -1;
    public static int osdSizeColD = -1;
    public static int osdSortCol = -1;
    public static int osdSortOrder = -1;
    public static String osdCurrentDir = "";

    /*
        Palette dialog options 
     */
    public static int pdPositionX = -1;
    public static int pdPositionY = -1;
    public static boolean pdShowChanges = false;

    /*
        Palette options 
     */
    public static int palLines = 0;
    public static int palDockedTo = -1;
    /*
        TubesBoard options 
     */
    public static int boardLines = 0;
    public static int boardDockedTo = -1;
    /*
        mainMenu options 
     */
    public static int menuAlign = -1;
    public static int menuDockedTo = -1;

    public static void saveOptions() {
        Properties sProps = new Properties();
        sProps.setProperty("Language", langCode);
        sProps.setProperty("SaveGameAfterFill", (saveGameAfterFill) ? "1" : "0");
        sProps.setProperty("SaveGameAfterSolve", (saveGameAfterSolve) ? "1" : "0");
        sProps.setProperty("SaveGameAfterWin", (saveGameAfterWin) ? "1" : "0");

        sProps.setProperty("MainMaximized", (mainMaximized) ? "1" : "0");
        sProps.setProperty("MainPosX", Integer.toString(mainPositionX));
        sProps.setProperty("MainPosY", Integer.toString(mainPositionY));
        sProps.setProperty("MainSizeX", Integer.toString(mainSizeX));
        sProps.setProperty("MainSizeY", Integer.toString(mainSizeY));
        
        sProps.setProperty("PaletteLines", Integer.toString(palLines));
        sProps.setProperty("PaletteDockedTo", Integer.toString(palDockedTo));
        sProps.setProperty("BoardLines", Integer.toString(boardLines));
        sProps.setProperty("BoardDockedTo", Integer.toString(boardDockedTo));

        sProps.setProperty("MenuAlign", Integer.toString(menuAlign));
        sProps.setProperty("MenuDockedTo", Integer.toString(menuDockedTo));

        sProps.setProperty("CreateNewFilled", Integer.toString(cndFilledTubes));
        sProps.setProperty("CreateNewEmpty", Integer.toString(cndEmptyTubes));

        sProps.setProperty("ColorDialogPosX", Integer.toString(ccdPositionX));
        sProps.setProperty("ColorDialogPosY", Integer.toString(ccdPositionY));

        sProps.setProperty("OpenSaveDialogPosX", Integer.toString(osdPositionX));
        sProps.setProperty("OpenSaveDialogPosY", Integer.toString(osdPositionY));
        sProps.setProperty("OpenSaveDialogSizeX", Integer.toString(osdSizeX));
        sProps.setProperty("OpenSaveDialogSizeY", Integer.toString(osdSizeY));
        sProps.setProperty("OpenSaveDialogColN", Integer.toString(osdSizeColN));
        sProps.setProperty("OpenSaveDialogColS", Integer.toString(osdSizeColS));
        sProps.setProperty("OpenSaveDialogColD", Integer.toString(osdSizeColD));
        sProps.setProperty("OpenSaveDialogDir", osdCurrentDir);
        sProps.setProperty("OpenSaveDialogSortColumn",Integer.toString(osdSortCol));
        sProps.setProperty("OpenSaveDialogSortOrder",Integer.toString(osdSortOrder));

        sProps.setProperty("PaletteDialogPosX", Integer.toString(pdPositionX));
        sProps.setProperty("PaletteDialogPosY", Integer.toString(pdPositionY));
        sProps.setProperty("PaletteDialogShowChanges", Integer.toString((pdShowChanges) ? 1 : 0));

        TubesIO.saveOptions(sProps);
        sProps.clear();
    }

    public static void loadOptions() {
        Properties sProps = new Properties();
        if (TubesIO.loadOptions(sProps)) {
            langCode = sProps.getProperty("Language", Locale.getDefault().getISO3Language());
            URL u = Options.class.getResource("/lang/" + langCode + ".properties");
            if (u==null) {
                langCode = "eng";
            }

            saveGameAfterFill = Integer.parseInt(sProps.getProperty("SaveGameAfterFill", "1")) == 1;
            saveGameAfterSolve = Integer.parseInt(sProps.getProperty("SaveGameAfterSolve", "1")) == 1;
            saveGameAfterWin = Integer.parseInt(sProps.getProperty("SaveGameAfterWin", "1")) == 1;
            mainMaximized = Integer.parseInt(sProps.getProperty("MainMaximized", "-1")) == 1;
            mainPositionX = Integer.parseInt(sProps.getProperty("MainPosX", "-1"));
            mainPositionY = Integer.parseInt(sProps.getProperty("MainPosY", "-1"));
            mainSizeX = Integer.parseInt(sProps.getProperty("MainSizeX", "-1"));
            mainSizeY = Integer.parseInt(sProps.getProperty("MainSizeY", "-1"));

            menuAlign = Integer.parseInt(sProps.getProperty("MenuAlign", "-1"));
            menuDockedTo = Integer.parseInt(sProps.getProperty("MenuDockedTo", "-1"));
            palLines = Integer.parseInt(sProps.getProperty("PaletteLines", "0"));
            palDockedTo = Integer.parseInt(sProps.getProperty("PaletteDockedTo", "-1"));
            boardLines = Integer.parseInt(sProps.getProperty("BoardLines", "0"));
            boardDockedTo = Integer.parseInt(sProps.getProperty("BoardDockedTo", "-1"));
            cndFilledTubes = Integer.parseInt(sProps.getProperty("CreateNewFilled", "12"));
            cndEmptyTubes = Integer.parseInt(sProps.getProperty("CreateNewEmpty", "2"));

            ccdPositionX = Integer.parseInt(sProps.getProperty("ColorDialogPosX", "-1"));
            ccdPositionY = Integer.parseInt(sProps.getProperty("ColorDialogPosY", "-1"));

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

            pdPositionX = Integer.parseInt(sProps.getProperty("PaletteDialogPosX", "-1"));
            pdPositionY = Integer.parseInt(sProps.getProperty("PaletteDialogPosY", "-1"));

            pdShowChanges
                    = Integer.parseInt(sProps.getProperty("PaletteDialogShowChanges", "-1")) == 1;

        }

    }

    public static ImageIcon cbIconSelected
            = createImageIcon("checkbutton_icon_selected.png");     // Icon_Selected for menus 
    public static ImageIcon cbIconStandard
            = createImageIcon("checkbutton_icon_standard.png");     // Icon_Selected for menus 

    public enum OS {
        WINDOWS, LINUX, MAC, SOLARIS
    } // Operating systems.

    private static OS os = null;

    public static OS getOS() {
        if (os == null) {
            String operSys = getProperty("os.name").toLowerCase();
            if (operSys.contains("win")) {
                os = OS.WINDOWS;
            } else if (operSys.contains("nix") || operSys.contains("nux")
                    || operSys.contains("aix")) {
                os = OS.LINUX;
            } else if (operSys.contains("mac")) {
                os = OS.MAC;
            } else if (operSys.contains("sunos")) {
                os = OS.SOLARIS;
            }
        }
        return os;
    }

    public static ImageIcon createImageIcon(String fName) {
        return createImageIcon(TubesIO.getImageResourceURL(fName));
    }

    public static ImageIcon createImageIcon(java.net.URL imgURL) {
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            err.println("createImageIcon: Couldn't find file: " + imgURL);
            return null;
        }
    }

    public static Image createImage(String fName) {
        return createImage(TubesIO.getImageResourceURL(fName));
    }

    public static Image createImage(java.net.URL imgURL) {
        return createBufImage(imgURL);
    }

    public static BufferedImage createBufImage(String fName) {
        return createBufImage(TubesIO.getImageResourceURL(fName));
    }

    public static BufferedImage createBufImage(java.net.URL imgURL) {
        BufferedImage img = null;
        if (imgURL != null) {
            try {
                img = read(imgURL);
            } catch (IOException ex) {
                err.println("createBufImage: Error while loading file: " + imgURL);
            }
        } else {
            err.println("createBufImage: Couldn't find file: " + imgURL);
        }
        return img;
    }

    public static Color colorFromHex(String Hex) {
//        int Value = Integer.decode(Hex);
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
}
