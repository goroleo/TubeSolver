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

import gui.BoardPanel;
import gui.ColorTube;
import gui.MainFrame;

import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 * All load and save routines for the application. And description of the file format when saves/loads the game.
 */
@SuppressWarnings("unused")
public class TubesIO {

///////////////////////////////////////////////////////////////////////////
//
//             * JCTL file format fields *
//         for saving and loading the game data
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Identifier of the file with the saved game. id = <b>JCTL</b>
     */
    private static final int FILE_ID = 0x6a63746c;

    /**
     * Old DOS's END_OF_FILE sign.
     */
    private static final int FILE_EOF = 0x1a;

    /**
     * Version of the file. Currently, ver.1 and ver.2 are available.<br>
     * New versions can be added if the application will get any new features.
     */
    private static int fileVer;

    /**
     * Size of the file in bytes.
     *
     * @see #getJCTLsize
     */
    private static int fileSize;

    /**
     * Number of the game level. Not used in version 1 and 2. Reserved for the
     * future use.
     */
    private static int level;

    /**
     * Game mode - mode of the saved game. Can be FILL_MODE, PLAY_MODE,
     * ASSIST_MODE.
     *
     * @see MainFrame#gameMode
     */
    private static int gMode;

    /**
     * Number of the tubes.
     */
    private static int tubesCount = 0;

    /**
     * Number of the empty tubes. Uses at FILL_MODE to know tubes that should
     * remain empty during the initial filling.
     */
    private static int emptyCount = 0;

    /**
     * The number of the moves (made or calculated) stored in the file. At
     * ASSIST_MODE <i>movesCount</i> is the count of all calculated moves.
     */
    private static int movesCount = 0;

    /**
     * The number of the <b>done</b> moves.<br>
     * At PLAY_MODE movesDone is equal to movesCount.<br>
     * At ASSIST_MODE movesDone may be less than movesCount.
     */
    private static int movesDone = 0;

    /**
     * Data of the stored tubes.
     *
     * @see TubeModel
     */
    private static int[] storedTubes;

    /**
     * Data of the stored moves.
     *
     * @see GameMoves
     */
    private static int[] storedMoves;

    /**
     * CRC of the file
     */
    private static int fileCRC;

///////////////////////////////////////////////////////////////////////////
//
//             * Files and Folders routines * 
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Get the folder to read and write application settings, temp files etc.
     *
     * @return the path string
     */
    public static String getAppDir() {
        String appDir = System.getProperty("user.home") + File.separator + ".tubesolver" + File.separator;
        File F = new File(appDir);
        //noinspection ResultOfMethodCallIgnored
        F.mkdirs();
        return appDir;
    }

    /**
     * Get the file name in application temporary folder.
     *
     * @param fileName name of the file
     * @return the path included folder and file name
     */
    public static String getAppDirFile(String fileName) {
        return getAppDir() + fileName;
    }

    /**
     * Gets the URL for access to an image stored in the application resources.
     *
     * @param fileName name of the image file
     * @return URL path
     */
    public static URL getImageResourceURL(String fileName) {
        return TubesIO.class.getResource("/img/" + fileName);
    }

    /**
     * Checks if the file is existing.
     *
     * @param fileName name of the file
     * @return true or false
     */
    public static boolean fileExists(String fileName) {
        File f = new File(fileName);
        return f.isFile() && f.canRead();
    }

    /**
     * Used to delete a temporary files.
     *
     * @param fileName name of the file
     * @return true or false
     */
    public static boolean fileDelete(String fileName) {
        boolean result = false;
        File f = new File(fileName);
        if (f.isFile() && f.canRead()) {
            result = f.delete();
        }
        return result;
    }

    /**
     * Temporary file name.
     */
    public static final String tempFileName
            = getAppDirFile(".$notsolved.jctl");


///////////////////////////////////////////////////////////////////////////
//
//             * Byte Array Stream's extension * 
//   writing and reading Integer (4 bytes) and Word (2 bytes) values
//                   to/from the Byte Array
//
///////////////////////////////////////////////////////////////////////////
    private static class myBAOS extends ByteArrayOutputStream {

        public myBAOS(int size) {
            super(size);
        }

        public void writeInt(int value) {
            write((value >> 24) & 0xff);
            write((value >> 16) & 0xff);
            write((value >> 8) & 0xff);
            write(value & 0xff);
        }

        public void writeWord(int value) {
            write((value >> 8) & 0xff);
            write(value & 0xff);
        }
    }

    private static class myBAIS extends ByteArrayInputStream {

        public myBAIS(byte[] buf) {
            super(buf);
        }

        public int readInt() {
            int res = 0, b;
            for (int i = 0; i < 4; i++) {
                b = read();
                if (b == -1) {
                    res = -1;
                    break;
                }
                res = (res << 8) + (b & 0xff);
            }
            return res;
        }

        public int readWord() {
            int res = 0, b;
            for (int i = 0; i < 2; i++) {
                b = read();
                if (b == -1) {
                    res = -1;
                    break;
                }
                res = (res << 8) + (b & 0xff);
            }
            return res;
        }
    }

///////////////////////////////////////////////////////////////////////////
//
//                        * JCTL format routines * 
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Computes the size of an output stream to put all the game information.
     * Size of the file depends on format version.
     *
     * @param fileVer file format version
     * @return size in bytes
     */
    public static int getJCTLsize(int fileVer) {
        switch (fileVer) {
            case 1:
                return (6 + tubesCount) * 4;
            case 2:
                return (8 + tubesCount + movesCount) * 4 + 2;
            default:
                return -1;
        }
    }

    /**
     * This is the very first CRC function used for JCTL format version 1. It
     * uses variables set or loaded before.
     *
     * @return CRC 4 bytes
     */
    public static int getCRCver1() {
        int result;
        int dw; // double word
        result = 0x6a + 0x63 + 0x74 + 0x6c + 0x1a;
        result = result + fileVer + fileSize + tubesCount;
        for (int i = 0; i < storedTubes.length; i++) {
            dw = storedTubes[i];
            for (int j = 0; j < 4; j++) {
                result = result + ((byte) dw) * j * i;
                dw = dw >> 8;
            }
        }
        return result;
    }

    /**
     * Calculates CRC for JCTL format version 2 (and above?) using ModBus16
     * algorithm. This routine counts whole the buffer.
     *
     * @param buffer array of bytes
     * @return CRC in 2 bytes
     */
    public static int getCRCver2(byte[] buffer) {
        return getCRCver2(buffer, buffer.length);
    }

    /**
     * Calculates CRC for JCTL format version 2 (and above?) using ModBus16
     * algorithm. This routine counts only part of the buffer.
     *
     * @param buffer array of bytes
     * @param length which part of buffer will be used to count the CRC
     * @return CRC in 2 bytes
     */
    public static int getCRCver2(byte[] buffer, int length) {

        int crc = 0xffff;                        // assign start value 0xFFFF
        int pos = 0;                             // current buffer's position
        boolean doXOR;                           // see below

        while (pos < length) {

            // java has no unsigned data types, so we have to avoid negative byte values for binary operations.
            // let's convert byte to integer and then get the last byte of that integer.
            //   int b[pos] = ((int) buffer[pos]) & 0xff;
            crc ^= ((int) buffer[pos]) & 0xff;   // crc = crc XOR buffer[i] 

            for (int b = 0; b < 8; b++) {        // for every bit...  
                doXOR = ((crc & 1) != 0);        //   doXOR = (last bit of the crc) != 0
                crc >>= 1;                       //   crc = crc >> 1
                if (doXOR) {                     //   if doXOR than 
                    crc ^= 0xa001;               //     crc = crc XOR polynomial 0xA001
                }
            }
            pos++;                               // next buffer position
        }

        // Now you can get LOW and HIGH bytes of the CRC.
        //   Low byte:    crcLo = crc & 0xff;
        //   High byte:   crcHi = (crc >> 8) & 0xff;
        return crc;
    }

///////////////////////////////////////////////////////////////////////////
//
//               * Store and restore game routines * 
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Stores the current game mode.
     * @param gm game mode.
     */
    public static void storeGameMode(int gm) {
        gMode = gm;
    }

    /**
     * Gets stored game mode.
     * @return game mode
     */
    public static int getGameMode() {
        return gMode;
    }

    /**
     * Stores tubes set from Tubes Board.
     * @param bp Board Panel
     * @param emptyTubes how much empty tubes are at the board (used in GAME_FILL mode).
     */
    public static void storeTubes(BoardPanel bp, int emptyTubes) {
        tubesCount = bp.getTubesCount();
        emptyCount = emptyTubes;
        storedTubes = new int[tubesCount];
        for (int i = 0; i < tubesCount; i++) {
            storedTubes[i] = bp.getTube(i).getModel().storeColors();
        }
    }
    /**
     * Fills stored tubes to the specified Board Panel
     * @param bp Board Panel to restore tubes
     */
    public static void restoreTubes(BoardPanel bp) {
        restoreTubes(bp, true);
    }

    /**
     * Fills stored tubes to the specified Board Panel
     * @param bp Board Panel to restore tubes
     * @param hideFillAnimation if true, tubes will be filled without an animation.
     */
    public static void restoreTubes(BoardPanel bp, boolean hideFillAnimation) {
        if (bp.getTubesCount() > 0) {
            bp.clearTubes();
        }
        for (int i = 0; i < tubesCount; i++) {
            ColorTube tube = bp.addNewTube();
            if (hideFillAnimation)
                tube.setColorsAnimation(false);
            tube.restoreColors(storedTubes[i]);
            if (gMode != MainFrame.FILL_MODE)
                tube.setActive(!tube.isClosed());
            tube.setColorsAnimation(true);
        }
        bp.restoreLocation();
    }

    /**
     * Gets count of empty tubes.
     * @return number of empty tubes
     */
    public static int getEmptyTubes() {
        return emptyCount;
    }

    /**
     * Gets count of filled tubes.
     * @return number of filled tubes
     */
    public static int getFilledTubes() {
        return tubesCount - emptyCount;
    }

    /**
     * Clears the stored Tubes array
     */
    public static void clearTubes() {
        emptyCount = 0;
        tubesCount = 0;
        storedTubes = null;
    }

    /**
     * Stores the moves list from the current game.
     * @param gm GameMoves array
     * @param moves how much moves has been passed.
     */
    public static void storeMoves(GameMoves gm, int moves) {
        if (gm.size() >= moves) {
            movesCount = gm.size();
            movesDone = moves;
            if (movesCount > 0) {
                storedMoves = new int[movesCount];
                for (int i = 0; i < movesCount; i++) {
                    storedMoves[i] = gm.getStoredMove(i);
                }
            }
        }
    }

    /**
     * Restores GameMoves array / list
     * @param gm GameMoves array to restore move items
     * @return how much moves has been passed.
     */
    public static int restoreMoves(GameMoves gm) {
        gm.clear();
        for (int i = 0; i < movesCount; i++) {
            gm.add(storedMoves[i]);
        }
        return movesDone;
    }

    /**
     * Clears the stored Moves array
     */
    public static void clearMoves() {
        movesCount = 0;
        movesDone = 0;
        storedMoves = null;
    }

///////////////////////////////////////////////////////////////////////////
//
//             * Read and write JTCL format to/from stream * 
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Saving game data to an abstract output stream. Uses format version 2
     *
     * @param S stream to save JCTL format
     * @return true if success, false in other case
     */
    public static boolean saveToStream(OutputStream S) {
        return saveToStream(S, 2);
    }

    /**
     * Saving game data to an abstract output stream
     *
     * @param S   stream to save JCTL.
     * @param ver JCTL format version
     * @return true if success, false otherwise
     */
    public static boolean saveToStream(OutputStream S, int ver) {

        if (ver != 1 && ver != 2) {
            return false;
        }

        fileVer = ver;
        fileSize = getJCTLsize(ver);

        try (myBAOS baos = new myBAOS(fileSize)) {

            baos.writeInt(FILE_ID);
            baos.writeInt(FILE_EOF);
            baos.writeInt(fileVer);
            baos.writeInt(fileSize);

            if (ver == 2) {
                baos.writeInt(level);
                baos.writeInt(gMode);
            }

            if (ver == 2 && gMode == MainFrame.FILL_MODE) {
                baos.writeWord(emptyCount);
            } else {
                baos.writeWord(0);
            }

            baos.writeWord(tubesCount);

            if (ver == 2) {
                if (movesDone != movesCount) {
                    baos.writeWord(movesDone);
                } else {
                    baos.writeWord(0);
                }
                baos.writeWord(movesCount);
            }

            for (int i = 0; i < tubesCount; i++) {
                baos.writeInt(storedTubes[i]);
            }

            if (ver == 2) {
                for (int i = 0; i < movesCount; i++) {
                    baos.writeInt(storedMoves[i]);
                }
            }

            if (ver == 1) {
                fileCRC = getCRCver1();
                baos.writeInt(fileCRC);
            } else { // if (ver == 2)
                fileCRC = getCRCver2(baos.toByteArray());
                baos.writeWord(fileCRC);
            }

            baos.writeTo(S);

        } catch (IOException ex) {
            return false;
        }

        return true;
    }

    /**
     * Loading game data from an abstract input stream.
     *
     * @param S Input stream
     * @return true if success, false otherwise
     */
    public static boolean loadFromStream(InputStream S) {
        int dw; // double word (int) to read from the stream
        boolean result;

        byte[] buf;
        myBAIS bais;

        try {
            buf = S.readAllBytes();
            bais = new myBAIS(buf);

            dw = bais.readInt();                    // read ID
            result = (dw == FILE_ID);

            if (result) {
                dw = bais.readInt();                // read EoF
                result = (dw == FILE_EOF);
            }
            if (result) {
                fileVer = bais.readInt();           // read Ver
                result = (fileVer == 1 || fileVer == 2);
            }

            if (result) {
                fileSize = bais.readInt();          // read FileSize
                result = (fileSize == buf.length);
            }

            if (result) {
                if (fileVer > 1) {
                    level = bais.readInt();         // read level 
                    gMode = bais.readInt();         // read gameMode
                    if (gMode == 0 || gMode == MainFrame.SOLVE_MODE) {
                        gMode = MainFrame.PLAY_MODE;
                    }
                } else {
                    level = 0;
                    gMode = MainFrame.PLAY_MODE;
                }

                if (gMode == MainFrame.FILL_MODE) {
                    emptyCount = bais.readWord();        // read empty tubes count 
                    tubesCount = bais.readWord();        // read all tubes count 
                } else {
                    emptyCount = 0;                      // empty tubes count is null 
                    tubesCount = bais.readInt();         // read tubes count 
                }
                if (fileVer > 1) {
                    movesDone = bais.readWord();
                    movesCount = bais.readWord();

                    if (movesDone == 0 && gMode != MainFrame.ASSIST_MODE) {
                        movesDone = movesCount;
                    }
                    if (movesDone > movesCount) {
                        movesDone = 0;
                    }

                } else { // 
                    movesDone = 0;
                    movesCount = 0;
                    storedMoves = null;
                }

                result = fileSize == getJCTLsize(fileVer);
            }

            if (result) {
                storedTubes = new int[tubesCount];
                for (int i = 0; i < tubesCount; i++) {
                    storedTubes[i] = bais.readInt();
                }
            }

            if (result && fileVer > 1 && movesCount > 0) {
                storedMoves = new int[movesCount];
                for (int i = 0; i < movesCount; i++) {
                    dw = bais.readInt();
                    storedMoves[i] = dw;

                    result = result
                            && ((dw >> 24) & 0xff) < tubesCount // check tubeFrom
                            && ((dw >> 16) & 0xff) < tubesCount // check tubeTo
                            && ((dw >> 8) & 0xff) > 0; // checks count
                }
            }

            if (result) {
                if (fileVer == 1) {
                    fileCRC = bais.readInt();     // read CRC ver 1
                    result = (fileCRC == getCRCver1());

                } else if (fileVer == 2) {
                    fileCRC = bais.readWord();    // read CRC ver 2
                    result = (fileCRC == getCRCver2(buf, fileSize - 2));
                }
            }

            bais.close();

        } catch (IOException ex) {
            result = false;
        }
        return result;
    }

///////////////////////////////////////////////////////////////////////////
//
//               * Read and write JTCL to/from file * 
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Loads JCTL format from file.
     *
     * @param fileName file name ))
     * @return true if success, false if any error
     */
    public static boolean loadFromFile(String fileName) {
        boolean result;
        try (FileInputStream S = new FileInputStream(fileName)) {
            result = loadFromStream(S);
        } catch (FileNotFoundException ex) {
            System.err.println("TubesIO.loadFromFile: Could not find file " + fileName);
            result = false;
        } catch (IOException ex) {
            System.err.println("TubesIO.loadFromFile: Error while loading file " + fileName);
            result = false;
        }
        return result;
    }

    /**
     * Saves JCTL format to specified file. Using format version 2 by default.
     *
     * @param fileName file name ))
     */
    public static void saveToFile(String fileName) {
        saveToFile(fileName, 2);
    }

    /**
     * Saves JCTL format to specified file.
     *
     * @param fileName file name ))
     * @param fileVer  JCTL format version
     */
    public static void saveToFile(String fileName, int fileVer) {
        try (FileOutputStream S = new FileOutputStream(fileName)) {
            saveToStream(S, fileVer);
        } catch (FileNotFoundException ex) {
            System.err.println("TubesIO.saveToFile: Could not find file " + fileName);
        } catch (IOException ex) {
            System.err.println("TubesIO.saveToFile: Error while saving file " + fileName);
        }
    }

///////////////////////////////////////////////////////////////////////////
//
//                    * Load and save palette * 
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Load colors stored in the properties file
     *
     * @param palProps properties of the palette
     * @return true if success, false if IO error
     */
    public static boolean loadPalette(Properties palProps) {
        try (FileInputStream in
                     = new FileInputStream(getAppDirFile("tubesolvergui.palette"))) {
            palProps.load(in);
        } catch (IOException ignore) {
            return false;
        }
        return true;
    }

    /**
     * Save colors at the properties file
     *
     * @param palProps properties of the palette
     * @return true if success, false if IO error
     */
    public static boolean savePalette(Properties palProps) {
        try (FileOutputStream out
                     = new FileOutputStream(getAppDirFile("tubesolvergui.palette"))) {
            palProps.store(out, "");
        } catch (IOException ignore) {
            return false;
        }
        return true;
    }

///////////////////////////////////////////////////////////////////////////
//
//          * Load and save the application options *
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Load options stored in the properties file
     *
     * @param props properties of the Options
     * @return true if success, false if IO error
     */
    public static boolean loadOptions(Properties props) {
        try (FileInputStream in
                     = new FileInputStream(getAppDirFile("tubesolvergui.properties"))) {
            props.load(in);
        } catch (IOException ignore) {
            return false;
        }
        return true;
    }

    /**
     * Save options to the properties file
     *
     * @param props properties of the Options
     * @return true if success, false if IO error
     */
    public static boolean saveOptions(Properties props) {
        try (FileOutputStream out
                     = new FileOutputStream(getAppDirFile("tubesolvergui.properties"))) {
            props.store(out, "");
        } catch (IOException ignore) {
            return false;
        }
        return true;
    }

}
