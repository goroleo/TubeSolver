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

import ani.PatternLayer;
import core.GameMoves;
import core.Options;
import core.ResStrings;
import core.TubesIO;
import dlg.MessageDlg;
import dlg.StartDlg;
import run.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import lib.lOpenSaveDialog.LOpenSaveDialog;


/**
 * The main frame of the application
 */
@SuppressWarnings("unused")
public class MainFrame extends JFrame {

//////////////////////////////////////////////////////////////////////////////
//
//                  *  Game modes  *
//
//////////////////////////////////////////////////////////////////////////////
    /**
     * The current game mode. The mode can be as follows:
     *
     * @see #PLAY_MODE
     * @see #ASSIST_MODE
     * @see #FILL_MODE
     * @see #BUSY_MODE
     * @see #END_GAME
     */
    public static int gameMode;

    /**
     * The previous mode of the game.
     *
     * @see #gameMode
     */
    public static int prevMode; // previous mode

    /**
     * The End of the game mode. It also can be named as "Game is over". At this mode the user can start the new game,
     * change some options or close the application.
     */
    public final static int END_GAME = 0;

    /**
     * Manual Fill mode. At this mode the user manually fill all the tubes
     * with the specified colors choosing them from the palette.
     */
    public final static int FILL_MODE = 100;

    /**
     * Regular Game mode. The user himself/herself shifts the colored cells
     * from one tube to another, trying to fill the tubes with one color.
     */
    public final static int PLAY_MODE = 200;

    /**
     * Assistant game mode. The application will show the user the next move, and will
     * wait for this move from the user. If the user decides to make another move,
     * this mode will end, the game will return to Regular mode.<br>
     * Assistant mode is offered to the user after a successful search for a solution
     * to the game.
     */
    public final static int ASSIST_MODE = 300;

    /**
     * Busy mode. This mode is activated when the application is busy. For example,
     * when an application is waiting for the user to answer a question, when calling
     * dialogs, when searching for a solution, etc. In this mode, the user must first
     * eliminate the cause of the busyness, and only then continue to play.
     */
    public final static int BUSY_MODE = 400;

//////////////////////////////////////////////////////////////////////////////
//
//                  *  Game moves  *
//
//////////////////////////////////////////////////////////////////////////////

    /**
     * Game moves array, passed and prepared
     */
    public static final GameMoves gameMoves = new GameMoves();

    /**
     * How much game moves has passed already
     *
     * @see #gameMoves
     */
    public static int movesDone;

//////////////////////////////////////////////////////////////////////////////
//
//                  *  Frame controls and layers *
//
//////////////////////////////////////////////////////////////////////////////

    /**
     * Colors palette for tube cells
     *
     * @see Palette
     */
    public static final Palette palette = new Palette();

    /**
     * The background layer of the Main frame.
     */
    private static final PatternLayer pattern =
            new PatternLayer(core.Options.createBufImage("imgPattern.png"));

    /**
     * Application control panels: The panel of color buttons for manual fill mode
     */
    public static PalettePanel palPan;

    /**
     * Application control panels: The Tubes Board panel with color tubes
     */
    public static BoardPanel tubesPan;

    /**
     * The toolbar with action buttons.
     */
    public static final ToolPanel toolPan = new ToolPanel();

    /**
     * Application panels: The panel with congratulations that showing when the game is done
     */
    private final static CongratsPanel congPan = new CongratsPanel();

    /**
     * Application panels: The panel shows when application seeks for the solution
     */
    private final static SolvePanel solvePan = new SolvePanel();

//////////////////////////////////////////////////////////////////////////////
//
//                  *  Game variables *
//
//////////////////////////////////////////////////////////////////////////////

    /**
     * Number of filled tubes at start of the game.
     */
    private static int filledTubes;

    /**
     * Number of уьзен tubes at start of the game.
     */
    private static int emptyTubes;

    /**
     * If <i>true</i>, the current game combination will save to the temporary file when
     * application is closed, to loads automatically when the application starts again.
     */
    private boolean saveTempOnExit = false;

    /**
     * This is a preset string for filename ending for auto save the game. It depends on the current game mode.
     */
    private String fileNameEnding;

//////////////////////////////////////////////////////////////////////////////
//                  
//                  * Main frame routines *
//                  
//////////////////////////////////////////////////////////////////////////////

    /**
     * Creates the main frame. Sets initial values and restores saved options of the frame.
     */
    public MainFrame() {
        super(ResStrings.getString("strColorTubes"));

        getContentPane().setLayout(null);
        getContentPane().setBackground(Palette.backgroundColor);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        Image img = Options.getAppIcon();
        setIconImage(img);

        int width = 1000; // default frame width
        int height = 760; // default frame height

        // Restoring saved position of the frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (Options.mainSizeX >= 300 && Options.mainSizeY >= 200
                && Options.mainPositionX >= 0
                && Options.mainPositionY >= 0
                && Options.mainPositionX + Options.mainSizeX <= screenSize.width
                && Options.mainPositionY + Options.mainSizeY <= screenSize.height) {
            setSize(Options.mainSizeX, Options.mainSizeY);
            width = Options.mainSizeX;
            height = Options.mainSizeY;
            setBounds(Options.mainPositionX, Options.mainPositionY, width, height);
        } else {
            width = Math.min(width, screenSize.width - 40);
            height = Math.min(height, screenSize.height - 40);
            setBounds((screenSize.width - width) / 2, (screenSize.height - height) / 2, width, height);
        }
        if (Options.mainMaximized) {
            setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);
        }

        // Creating and adding panels except Palette & Tubes panels - they will be added in depend on the game mode.
        solvePan.setVisible(false);
        getLayeredPane().add(solvePan, JLayeredPane.MODAL_LAYER);

        congPan.setVisible(false);
        getLayeredPane().add(congPan, JLayeredPane.MODAL_LAYER);

        toolPan.setVisible(true);
        getLayeredPane().add(toolPan, JLayeredPane.PALETTE_LAYER);

        // Adding the background layer
        getLayeredPane().add(pattern, JLayeredPane.DEFAULT_LAYER);

        // Adding listeners
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeFrame();
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeFrame();
            }
        });
    }

    /**
     * Shows the application frame.
     */
    public void showFrame() {
        EventQueue.invokeLater(() -> setVisible(true));
        StartDlg startDlg = new StartDlg(this);

        // center the Start dialog at the first start
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (Options.mainSizeX >= 300 && Options.mainSizeY >= 200
                && Options.mainPositionX >= 0
                && Options.mainPositionY >= 0
                && Options.mainPositionX + Options.mainSizeX <= screenSize.width
                && Options.mainPositionY + Options.mainSizeY <= screenSize.height) {
            startDlg.setLocation(Options.mainPositionX + (Options.mainSizeX - startDlg.getWidth()) / 2,
                    Options.mainPositionY + (Options.mainSizeY - startDlg.getHeight()) / 2);
        }

        EventQueue.invokeLater(() -> startDlg.setVisible(true));
    }

    /**
     * Closes the application frame and saves its options.
     */
    public void closeFrame() {
        if (solvePan.isVisible()) {
            solvePan.stopSolver(1);
            return;
        }

        saveOptions();
        palette.savePalette();
        if (toolPan != null) {
            toolPan.saveOptions();
        }
        if (palPan != null) {
            palPan.saveOptions();
        }
        if (tubesPan != null) {
            tubesPan.saveOptions();
        }

        Options.saveOptions();
        if (saveTempOnExit) {
            saveTempGame();
        }
        if (Options.saveGameBeforeClose) {
            saveGameAs(ResStrings.getString("strSaveIDClosed"));
        }
        System.exit(0);
    }

    /**
     * Handles the frame resizing events.
     */
    public void resizeFrame() {
        pattern.setBounds(getColorsArea());
        toolPan.resize();
        updatePanelsPos();
        if (congPan.isVisible()) {
            congPan.updateSizeAndPos();
        }
        if (solvePan.isVisible()) {
            solvePan.updateSizeAndPos();
        }
    }

    /**
     * Updates a language of the application and all its panels.
     */
    public void updateLanguage() {
        setTitle(ResStrings.getString("strColorTubes"));
        if (toolPan != null)
            toolPan.updateLanguage();
        if (solvePan != null)
            solvePan.updateLanguage();
    }

//////////////////////////////////////////////////////////////////////////////
//                  
//                  *  Load and Save routines  *
//                  
//////////////////////////////////////////////////////////////////////////////

    /**
     * Loads the saved game.
     *
     * @param fileName name of the file
     * @return true if the game loaded successfully, false otherwise
     */
    public boolean loadGame(String fileName) {
        boolean result = TubesIO.loadFromFile(fileName);
        if (result) {
            clearBoard();
            setGameMode(TubesIO.getGameMode());
            if (gameMode == FILL_MODE) {
                addColorsPanel();
                addTubesPanel(0, 0);
                TubesIO.restoreTubes(tubesPan);
                filledTubes = TubesIO.getFilledTubes();
                emptyTubes = TubesIO.getEmptyTubes();

                for (int i = 0; i < filledTubes; i++) {
                    ColorTube tube = tubesPan.getTube(i);
                    for (int c = 0; c < tube.getColorsCount(); c++) {
                        int clr = tube.getColor(c);
                        Palette.usedColors.incColorCount((byte) clr);
                        palPan.getButtonByColor(clr).decCount();
                        if (Palette.usedColors.getColorCount((byte) clr) == 4) {
                            palPan.getButtonByColor(clr).decCount();
                        }
                    }
                }
                if (Palette.usedColors.getAllUsedColors() >= filledTubes) {
                    disableUnusedColors();
                }
                for (int i = 0; i < emptyTubes; i++) {
                    tubesPan.getTube(filledTubes + i).setClosed(true);
                }
            } else {
                addTubesPanel(0, 0);
                TubesIO.restoreTubes(tubesPan);
                movesDone = TubesIO.restoreMoves(gameMoves);
            }
        }
        return result;
    }

    /**
     * Saves the game to the temporary file.
     */
    public void saveTempGame() {
        saveGame(TubesIO.tempFileName);
    }

    /**
     * Saves the game to the specified file.
     *
     * @param fileName name of the file.
     */
    public void saveGame(String fileName) {
        if (tubesPan != null) {
            if (gameMode != BUSY_MODE)
                TubesIO.storeGameMode(gameMode);
            else
                TubesIO.storeGameMode(prevMode);

            TubesIO.storeTubes(tubesPan,
                    (gameMode != FILL_MODE) ? 0 : emptyTubes);
            TubesIO.storeMoves(gameMoves, movesDone);
            TubesIO.saveToFile(fileName, 2);
        }
    }

    /**
     * Saves the game with the Save File Dialog
     */
    public void saveGameAs() {
        saveGameAs("");
    }

    /**
     * Saves the game with the Save File Dialog
     *
     * @param ending is a preset string for filename ending
     */
    public void saveGameAs(String ending) {
        setGameMode(MainFrame.BUSY_MODE);

        LOpenSaveDialog os = new LOpenSaveDialog(this, LOpenSaveDialog.SAVE_MODE);

        if (!"".equals(ending)) {
            os.setFileName(Options.getDateTimeStr() + " " + ending);
        } else {
            os.setFileName(Options.getDateTimeStr());
        }

        String fileName = os.showSaveDialog();
        if (!"".equals(fileName)) {
            saveGame(fileName);
        }
        setGameMode(prevMode);
    }

    /**
     * Saves the frame options
     */
    private void saveOptions() {
        Options.mainMaximized = (getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
        if (!Options.mainMaximized) {
            Options.mainPositionX = getX();
            Options.mainPositionY = getY();
            Options.mainSizeX = getWidth();
            Options.mainSizeY = getHeight();
        }
    }

//////////////////////////////////////////////////////////////////////////////
//
//                  *  Game modes routines *
//
//////////////////////////////////////////////////////////////////////////////

    /**
     * Sets a new game mode
     *
     * @param aMode new game mode
     */
    public void setGameMode(int aMode) {
        if (aMode != gameMode) {
            prevMode = gameMode;
            gameMode = aMode;
            toolPan.updateButtons(aMode);
        }
    }

    /**
     * Clears the game board and prepares it to the new game.
     */
    public void clearBoard() {
        congPan.setVisible(false);
        solvePan.setVisible(false);
        if (tubesPan != null) {
            tubesPan.saveOptions();
            getLayeredPane().remove(tubesPan);
            tubesPan.emptyBoard();
            tubesPan = null;
        }
        if (palPan != null) {
            palPan.saveOptions();
            getLayeredPane().remove(palPan);
            palPan.removeAll();
            palPan = null;
        }
        gameMoves.clear();
        movesDone = 0;
        Palette.usedColors.clearColorCounts();
        toolPan.updateButtons();
        repaint();
    }

    /**
     * Clears stored tubes and moves.
     */
    public void clearStored() {
        TubesIO.clearMoves();
        TubesIO.clearTubes();
    }

    /**
     * Starts the manual fill game mode.
     *
     * @param aFilled number of filled tubes
     * @param aEmpty  number of empty tubes
     */
    public void startFillMode(int aFilled, int aEmpty) {
        setGameMode(FILL_MODE);
        clearBoard();
        clearStored();
        addColorsPanel();
        addTubesPanel(aFilled, aEmpty);
        startFindTubesTo();
        filledTubes = aFilled;
        emptyTubes = aEmpty;
        saveTempOnExit = true;
        fileNameEnding = ResStrings.getString("strSaveIDManualFill");
        nextTubeTo(0);
    }

    /**
     * Resumes the manual fill game mode (for example after autoload the previous game).
     */
    public void resumeFillMode() {
        setGameMode(FILL_MODE);
        fileNameEnding = ResStrings.getString("strSaveIDManualFill");
        startFindTubesTo();
        nextTubeTo(0);
    }

    /**
     * Starts the autofill game mode.
     *
     * @param aFilled number of filled tubes
     * @param aEmpty  number of empty tubes
     */
    public void startAutoFillMode(int aFilled, int aEmpty) {
        fileNameEnding = ResStrings.getString("strSaveIDAutoFill");
        setGameMode(FILL_MODE);
        clearBoard();
        clearStored();
        addTubesPanel(aFilled, aEmpty);
        filledTubes = aFilled;
        emptyTubes = aEmpty;
//        tubesPan.paintImmediately(tubesPan.getBounds());
        autoFillTheRest();
    }

    /**
     * Ends the fill mode and starts the game.
     */
    public void endFillMode() {
        // hiding palette
        if (palPan != null) {
            palPan.setVisible(false);
            updateTubesPos();
        }
        //
        setTubeTo(null);

        // starting PLAY_MODE
        gameMoves.clear();
        movesDone = 0;
        startPlayMode();

        // saving
        if (Options.saveGameAfterFill) {
            saveGameAs(fileNameEnding);
        }
    }

    /**
     * Starts the regular play mode.
     */
    public void startPlayMode() {
        setGameMode(PLAY_MODE);

        for (int i = 0; i < tubesPan.getTubesCount(); i++) {
            tubesPan.getTube(i).setClosed(tubesPan.getTube(i).getModel().getState() == 3);
        }

        if (tubesPan.isSolved())
            setGameMode(END_GAME);
        else {
            saveTempGame();
            saveTempOnExit = true;
            setTubeFrom(null);
        }
    }

    /**
     * Starts the Assistant play mode.
     */
    public void startAssistMode() {
        if (tubesPan.isSolved())
            setGameMode(END_GAME);
        else {
            setGameMode(ASSIST_MODE);
            setTubeFrom(null);
            saveTempOnExit = true;
            hideMove();
            showMove();
        }
    }

    /**
     * Ends the Assistant mode and return to the regular play mode.
     */
    public void endAssistMode() {

        hideMove();
        while (gameMoves.size() > movesDone) {
            gameMoves.remove(gameMoves.size() - 1);
        }

        setGameMode(PLAY_MODE);
        saveTempOnExit = true;

        setTubeTo(null);
        setTubeFrom(null);
    }

    /**
     * Starts to find the solution of the game
     */
    public void startSolve() {

        setGameMode(BUSY_MODE);

        MessageDlg msgDlg = new MessageDlg(Main.frame,
                ResStrings.getString("strFindSolution"),
                MessageDlg.BTN_YES_NO);
        msgDlg.setButtonsLayout(MessageDlg.BTN_LAYOUT_RIGHT);
        msgDlg.setVisible(true);

        if (msgDlg.result > 0) {
            setGameMode(BUSY_MODE);
            Options.numSolverRun++;
            solvePan.startSolve(tubesPan.getModel());
        } else {
            setGameMode(prevMode);
        }
    }

    /**
     * Ends of the search for a solution to the game.
     *
     * @param result the reason to end of the search for a solution.
     * @see SolvePanel#solveResult
     */
    public void endSolve(int result) {

        MessageDlg msgDlg;

        switch (result) {
            case 0: // working
                break;
            case 1: // escape-cancel pressed
                Options.numSolverCancel ++;
                msgDlg = new MessageDlg(this,
                        ResStrings.getString("strCancelSolution"),
                        MessageDlg.BTN_OK);
                msgDlg.setButtonsLayout(MessageDlg.BTN_LAYOUT_CENTER);
                msgDlg.setVisible(true);
                break;
            case 2: // not solved
                Options.numSolverNotSolved ++;
                msgDlg = new MessageDlg(this,
                        ResStrings.getString("strNotSolved"),
                        MessageDlg.BTN_OK);
                msgDlg.setButtonsLayout(MessageDlg.BTN_LAYOUT_RIGHT);
                msgDlg.setVisible(true);
                break;
            case 3: // solved!
                Options.numSolverSuccess ++;
                msgDlg = new MessageDlg(this,
                        ResStrings.getString("strSolutionSuccess"),
                        MessageDlg.BTN_YES_NO);
                msgDlg.setButtonsLayout(MessageDlg.BTN_LAYOUT_RIGHT);
                msgDlg.setVisible(true);
                if (msgDlg.result == 0) result = 0;
        }

        if (result == 3) {
            startAssistMode();
            if (Options.saveGameAfterSolve) {
                saveGameAs(ResStrings.getString("strSaveIDSolved"));
            }
        } else {
            endAssistMode();
        }
    }

    /**
     * Shows congratulations after successful end of the game.
     */
    public void endGame() {
        saveTempOnExit = false;
        TubesIO.fileDelete(TubesIO.tempFileName);

        for (int i = 0; i < tubesPan.getTubesCount(); i++) {
            tubesPan.getTube(i).setClosed(true);
        }

        setGameMode(END_GAME);
        gameMoves.clear();
        movesDone = 0;

        congPan.setVisible(true);
    }

//////////////////////////////////////////////////////////////////////////////
//                  
//                  *  Create game controls panels *
//                  
//////////////////////////////////////////////////////////////////////////////

    /**
     * Adds the Palette panel with Color Buttons in manual fill mode.
     */
    public void addColorsPanel() {
        palPan = new PalettePanel() {
            @Override
            public void clickButton(ColorButton cb) {
                clickColorButton(cb);
            }

            @Override
            public void changeColor(ColorButton cb) {
                int colorNumber = cb.getColorNumber();
                Color oldColor = palette.getColor(colorNumber);
                super.changeColor(cb);
                Color newColor = palette.getColor(colorNumber);
                if (newColor != oldColor) {
                    tubesPan.updateColor(colorNumber);
                }
            }

            @Override
            public void setDefaultPalette() {
                super.setDefaultPalette();
                tubesPan.updateColors();
            }
        };
        for (int i = 0; i < palette.size() - 1; i++) {
            palPan.getButton(i).setCount(4);
        }
        palPan.addPopups();
        palPan.reDock();
        palPan.setVisible(true);
        getLayeredPane().add(palPan, JLayeredPane.PALETTE_LAYER);
    }

    /**
     * Adds the Tubes Board panel with Color Tubes.
     *
     * @param aFilled number of filled tubes
     * @param aEmpty  number of empty tubes
     */
    public void addTubesPanel(int aFilled, int aEmpty) {

        tubesPan = new BoardPanel() {
            @Override
            public void clickTube(ColorTube tube) {
                MainFrame.this.clickTube(tube);
            }

            @Override
            public boolean canShowArrow(ColorTube tube) {
                return MainFrame.this.canShowArrow(tube);
            }

            @Override
            public boolean canHideArrow(ColorTube tube) {
                return MainFrame.this.canHideArrow(tube);
            }

            @Override
            public void clearTube(ColorTube tube) {
                MainFrame.this.clearTube(tube);
            }

            @Override
            public void clearTubes() {
                clearAllTubes();
            }
        };

        if (aFilled + aEmpty > 0) {
            filledTubes = aFilled;
            emptyTubes = aEmpty;
            tubesPan.addNewTubes(aFilled, aEmpty);
        }

        tubesPan.setDockedTo(0);
        tubesPan.restoreLocation();

        getLayeredPane().add(tubesPan, JLayeredPane.PALETTE_LAYER);
        tubesPan.setVisible(true);
    }

//////////////////////////////////////////////////////////////////////////////
//
//                  *  ALL MODES routines *
//
//////////////////////////////////////////////////////////////////////////////

    /**
     * Determines if this tube's arrow can be shown.
     *
     * @param tube specified color tube
     * @return true if this tube's arrow can be shown, false otherwise
     */
    public boolean canShowArrow(ColorTube tube) {
        switch (gameMode) {
            case FILL_MODE:
                tube.setArrow(ColorTube.ARROW_YELLOW);
                return tube.canPutColor(0) && tube != getTubeTo();
            case PLAY_MODE:
                if (getTubeFrom() == null) {
                    return tubesPan.canGetColor(tube);
                } else {
                    return tube.canPutColor(getTubeFrom().getCurrentColor())
                            && tube != getTubeFrom();
                }
            case ASSIST_MODE:
                return movesDone < gameMoves.size()
                        && ((tubesPan.getTubeNumber(tube) == gameMoves.getTubeTo(movesDone)
                        || tubesPan.getTubeNumber(tube) == gameMoves.getTubeFrom(movesDone))
                        && (tube != getTubeFrom()));
            default:
                return false;
        }
    }

    /**
     * Returns the Color Tube from which the color will be taken (Donor).
     *
     * @return Donor color tube
     */
    public ColorTube getTubeFrom() {
        return tubesPan.getTubeFrom();
    }

    /**
     * Sets the Color Tube from which the color will be taken (Donor).
     *
     * @param tube Donor color tube
     */
    public void setTubeFrom(ColorTube tube) {
        tubesPan.setTubeFrom(tube);

        if (gameMode == PLAY_MODE) {
            if (tube == null) {
                startFindTubesFrom();
            } else {
                tube.hideArrow();
                startFindTubesTo();
            }

        } else if (gameMode == ASSIST_MODE) {
            if (tube == null) {
                if (!tubesPan.isSolved()) {
                    setTubeTo(null);
                    showMove();
                }
            } else {
                tube.hideArrow();
                ColorTube tubeTo = tubesPan.getTube(gameMoves.getTubeTo(movesDone));
                tubeTo.setArrow(ColorTube.ARROW_YELLOW);
                tubeTo.setFrame(3);

                tubeTo.showArrow();
                tubeTo.pulseFrame();

            }
        }
    }

    /**
     * Returns the Color Tube in which the color will be placed (Recipient).
     *
     * @return Recipient color tube
     */
    public ColorTube getTubeTo() {
        return tubesPan.getTubeTo();
    }

    /**
     * Sets the Color Tube in which the color will be placed (Recipient).
     *
     * @param tube Recipient color tube
     */
    public void setTubeTo(ColorTube tube) {

        int howMuch;
        if (gameMode == FILL_MODE) {

            tubesPan.setTubeTo(tube);
            if (tube != null && getTubeTo() != null) {
                getTubeTo().hideArrow();
            }

        } else if (gameMode == PLAY_MODE) {
            if (tube != null && getTubeFrom() != null) {
                howMuch = tubesPan.moveColor(getTubeFrom(), tube);
                if (howMuch > 0) {
                    gameMoves.addMove(
                            tubesPan.getTubeNumber(getTubeFrom()),
                            tubesPan.getTubeNumber(tube),
                            howMuch,
                            tube.getCurrentColor());
                    movesDone++;
                    toolPan.updateButtons();
                }
                setTubeTo(null);
                setTubeFrom(null);
                if (tubesPan.isSolved()) {
                    // do congratulations!
                    endGame();
                }
            }
        } else if (gameMode == ASSIST_MODE) {

            if (tube != null) {
                if (tubesPan.getTubeNumber(tube) == gameMoves.getTubeTo(movesDone)) {
                    howMuch = tubesPan.moveColor(getTubeFrom(), tube);
                    if (howMuch == gameMoves.getMoveCount(movesDone)) {
                        movesDone++;
                        toolPan.updateButtons();
                    }
                    tube.hideArrow();
                    if (!tube.isClosed()) {
                        tube.hideFrame();
                    }
                    setTubeTo(null);

                    getTubeFrom().hideArrow();
                    setTubeFrom(null);
                    if (tubesPan.isSolved()) {
                        // do congratulations!
                        endGame();
                    } else {
                        showMove();
                    }
                }
            }
        }
    }

///////////////////////////////////////////////////////////////////////////////
//
//                  *  PLAY MODE routines *
//
//////////////////////////////////////////////////////////////////////////////

    /**
     * Starts to find the Donor tube. Sets all arrows to green.
     */
    public void startFindTubesFrom() {
        for (int i = 0; i < tubesPan.getTubesCount(); i++) {
            if (!tubesPan.getTube(i).isClosed()) {
                tubesPan.getTube(i).setArrowWhenHide(ColorTube.ARROW_GREEN);
            } else {
                tubesPan.getTube(i).setArrowWhenHide(ColorTube.ARROW_NO_COLOR);
            }
        }
    }

    /**
     * Starts to find the Recipient tube. Sets all arrows to yellow.
     */
    public void startFindTubesTo() {
        for (int i = 0; i < tubesPan.getTubesCount(); i++) {
            if (!tubesPan.getTube(i).isClosed()) {
                tubesPan.getTube(i).setArrowWhenHide(ColorTube.ARROW_YELLOW);
            } else {
                tubesPan.getTube(i).setArrowWhenHide(ColorTube.ARROW_NO_COLOR);
            }
        }
    }

///////////////////////////////////////////////////////////////////////////////
//                  
//                  *  FILL MODE routines *
//                  
//////////////////////////////////////////////////////////////////////////////

    /**
     * Clears the specified tube from all its colors.
     *
     * @param tube color tube
     */
    public void clearTube(ColorTube tube) {
        if (tube != null) {
            byte clrNum;
            if (!tube.isClosed()) {

                for (int i = tube.getColorsCount() - 1; i >= 0; i--) {
                    clrNum = tube.getCurrentColor();
                    tube.extractColor();

                    ColorButton pb = palPan.getButtonByColor(clrNum);
                    if (pb.getCount() <= 0) {
                        pb.setCount(1);
                    } else {
                        pb.incCount();
                    }
                    Palette.usedColors.decColorCount(clrNum);
                }
            }
            toolPan.updateButtons();
            setTubeTo(tube);
        }
    }

    /**
     * Clears all tubes from all their colors. Starts the manual fill mode again.
     */
    public void clearAllTubes() {
        // clear all tubes
        for (int i = 0; i < tubesPan.getTubesCount(); i++) {
            clearTube(tubesPan.getTube(i));
        }

        // clear all buttons counts 
        for (int i = 0; i < palPan.getColorsCount(); i++) {
            palPan.getButton(i).setCount(4);
        }

        Palette.usedColors.clearColorCounts();
        toolPan.updateButtons();
        nextTubeTo(0);
    }

    /**
     * Disables Color Buttons if those colors cannot be used aon the board.<br>
     * N.B. Let the board is configured with 9 filled and 2 empty tubes. Then we
     * can't use more than 9 colors at the board. All the rest color buttons with
     * unused colors should be disabled.
     */
    public void disableUnusedColors() {
        for (int i = 1; i < palette.size(); i++) {
            if (Palette.usedColors.getColorCount((byte) i) == 0) {
                Palette.usedColors.setColorCount((byte) i, 4);
                if (palPan != null)
                    palPan.getButton(i - 1).setCount(-1);
            }
        }
    }

    /**
     * Automatically fills the rest of tubes with random colors. Used also as the Automatic Fill mode.
     */
    public void autoFillTheRest() {
        fileNameEnding = ResStrings.getString("strSaveIDAutoFill");
        for (int t = 0; t < filledTubes; t++) {
            for (int i = tubesPan.getTube(t).getColorsCount(); i < 4; i++) {
                int clr = Palette.usedColors.getRandomColor();
                tubesPan.getTube(t).putColor(clr);
                Palette.usedColors.incColorCount((byte) clr);
                if (Palette.usedColors.getAllUsedColors() >= filledTubes) {
                    disableUnusedColors();
                }
            }
        }
        Options.numRandomFill ++;
        tubesPan.paintImmediately(tubesPan.getBounds());
        endFillMode();
    }

    /**
     * Finds and selects the next not filled tube on the board.
     *
     * @param startNumber number of the tube from which the search starts.
     */
    public void nextTubeTo(int startNumber) {
        int num;
        boolean done = false;
        boolean over = false;

        if (getTubeTo() != null) {
            setTubeTo(null);
        }

        ColorTube tube;
        if (startNumber == tubesPan.getTubesCount()) {
            startNumber -= tubesPan.getTubesCount();
        }
        num = startNumber;
        do {
            tube = tubesPan.getTube(num);
            if (tube.canPutColor(0)) {
                setTubeTo(tube);
                done = true;
            }
            num++;
            if (num == tubesPan.getTubesCount()) {
                num -= tubesPan.getTubesCount();
                over = true;
            }
            if (num == startNumber && over) {
                done = true;
            }
        } while (!done);
    }

//////////////////////////////////////////////////////////////////////////////
//
//                  *  ASSIST MODE routines *
//
//////////////////////////////////////////////////////////////////////////////

    /**
     * Shows the current move. TubeFrom (Donor) will display with the Green arrow & green frame, TubeTo (Recipient) - with the Yellow Arrow.
     */
    public void showMove() {
        if (gameMode == ASSIST_MODE) {
            if (getTubeFrom() != null) {
                getTubeFrom().hideFrame();
                getTubeFrom().hideArrow();
            }
            if (getTubeTo() != null) {
                getTubeTo().hideFrame();
                getTubeTo().hideArrow();
            }
            if (gameMoves.size() > movesDone) {
                ColorTube tFrom = tubesPan.getTube(gameMoves.getTubeFrom(movesDone));
                ColorTube tTo = tubesPan.getTube(gameMoves.getTubeTo(movesDone));

                tFrom.setArrow(ColorTube.ARROW_GREEN);
                tFrom.showArrow();
                tFrom.setFrame(2);
                tFrom.pulseFrame();

                tTo.setArrow(ColorTube.ARROW_YELLOW);
                tTo.showArrow();
                tTo.hideFrame();
            }
        }
    }

    /**
     * Hides the display of the current move.
     */
    public void hideMove() {
        if (gameMode == ASSIST_MODE) {
            for (int i = 0; i < tubesPan.getTubesCount(); i++) {
                tubesPan.getTube(i).hideArrow();
                if (!tubesPan.getTube(i).isClosed()) {
                    tubesPan.getTube(i).hideFrame();
                }
            }
        }
    }

    /**
     * Checks if the Arrow above the Tube may be hidden.
     *
     * @param tube specified color tube
     * @return true if the arrow is allowed to be hidden, false otherwise.
     */
    public boolean canHideArrow(ColorTube tube) {
        if (gameMode == ASSIST_MODE) {
            return movesDone < gameMoves.size()
                    && (tubesPan.getTubeNumber(tube) != gameMoves.getTubeFrom(movesDone))
                    && (tubesPan.getTubeNumber(tube) != gameMoves.getTubeTo(movesDone));
        }
        return true;
    }


//////////////////////////////////////////////////////////////////////////////
//                  
//                  *  Mouse Click routines *
//                  
//////////////////////////////////////////////////////////////////////////////

    /**
     * Handles the click event on the Color Button at the Palette.
     *
     * @param cb specified Color Button
     */
    public void clickColorButton(ColorButton cb) {

        if (gameMode == FILL_MODE) {

            if (cb.getCount() > 0) {
                ColorTube tubeTo = getTubeTo();
                if (tubeTo != null) {
                    if (tubeTo.getColorsCount() < 4) {
                        tubeTo.putColor(cb.getColorNumber());
                        Palette.usedColors.incColorCount((byte) cb.getColorNumber());
                        toolPan.updateButtons();
                        cb.decCount();
                        if (cb.getCount() == 0) {
                            cb.setCount(-1);
                        }
                    }
                    if (tubeTo.getColorsCount() == 4) {
                        nextTubeTo(tubesPan.getTubeNumber(tubeTo) + 1);
                    }
                    if (Palette.usedColors.getAllUsedColors() >= filledTubes) {
                        disableUnusedColors();
                    }
                    if (Palette.usedColors.getAllFilledColors() == palette.size() - 1) {
                        Options.numManualFill ++;
                        endFillMode();
                    }
                }
            }
        }
    }

    /**
     * Handles the click event on the Color Tube at the Board.
     *
     * @param tube specified Color Tube
     */
    public void clickTube(ColorTube tube) {
        switch (gameMode) {
            case FILL_MODE:
                if (tube != null && canShowArrow(tube)) {
                    setTubeTo(tube);
                }
                break;
            case PLAY_MODE:
                if (getTubeFrom() == null && canShowArrow(tube)) {
                    setTubeFrom(tube);
                } else if (getTubeFrom() == tube) {
                    setTubeFrom(null);
                } else if (canShowArrow(tube)) {
                    setTubeTo(tube);
                } else if (tubesPan.canGetColor(tube)) {
                    setTubeFrom(tube);
                }
                break;
            case ASSIST_MODE:
                if (getTubeFrom() == null) {
                    if (!gameMoves.isEmpty() && tubesPan.getTubeNumber(tube) == gameMoves.getTubeFrom(movesDone)) {
                        setTubeFrom(tube);
                    } else {
                        if (!tube.isClosed() && !tube.isEmpty()) {
                            // exit from the assist mode
                            setGameMode(BUSY_MODE);
                            MessageDlg msgFrame = new MessageDlg(this,
                                    ResStrings.getString("strExitAssistMode"),
                                    MessageDlg.BTN_YES_NO);
                            msgFrame.setButtonsLayout(MessageDlg.BTN_LAYOUT_RIGHT);
                            msgFrame.setVisible(true);
                            setGameMode(prevMode);

                            if (msgFrame.result > 0) {
                                endAssistMode();
                            }
                        }
                    }
                } else if (getTubeFrom() == tube) {
                    setTubeFrom(null);
                } else if (tubesPan.getTubeNumber(tube) == gameMoves.getTubeTo(movesDone)) {
                    setTubeTo(tube);
                } else {
                    if (!tube.isClosed()) {
                        // exit from the assist mode
                        setGameMode(BUSY_MODE);
                        MessageDlg msgFrame = new MessageDlg(this,
                                ResStrings.getString("strExitAssistMode"),
                                MessageDlg.BTN_YES_NO);
                        msgFrame.setButtonsLayout(MessageDlg.BTN_LAYOUT_RIGHT);
                        msgFrame.setVisible(true);
                        setGameMode(prevMode);

                        if (msgFrame.result > 0) {
                            endAssistMode();
                        }
                    }
                }

                break;
            default:
                break;
        }
    }

//////////////////////////////////////////////////////////////////////////////
//                  
//                  *  Position & resize routines *
//                  
//////////////////////////////////////////////////////////////////////////////

    /**
     * Gets the client / content area of the Main Frame. Used to place the Tools Panel.
     *
     * @return client area as the rectangle
     */
    public Rectangle getClientArea() {
        return getContentPane().getBounds();
    }

    /**
     * Gets the colors area of the Main Frame. Used to place the Palette Panel. Colors area
     * is the client area of the MainFrame besides the Tools panel area.
     *
     * @return colors area as the rectangle
     */
    public Rectangle getColorsArea() {
        Rectangle area = getClientArea();
        if (toolPan != null) {
            switch (toolPan.getDockedTo()) {
                case 0: // top
                    area.y += toolPan.getHeight();
                    area.height -= toolPan.getHeight();
                    break;
                case 1: // bottom
                    area.height -= toolPan.getHeight();
                    break;
                case 2: // left
                    area.x += toolPan.getWidth();
                    area.width -= toolPan.getWidth();
                    break;
                case 3: // right
                    area.width -= toolPan.getWidth();
                    break;
            }
        }
        return area;
    }

    /**
     * Gets the colors area of the Main Frame. Used to place the Tubes Board Panel. Tubes area
     * is the client area of the MainFrame besides the Tools panel and the Palette panel areas.
     *
     * @return Tubes area as the rectangle
     */
    public Rectangle getTubesArea() {
        Rectangle area = getColorsArea();
        if (palPan != null && palPan.isVisible()) {
            switch (palPan.getDockedTo()) {
                case 0: // top
                    area.y += palPan.getHeight();
                    area.height -= palPan.getHeight();
                    break;
                case 1: // bottom
                    area.height -= palPan.getHeight();
                    break;
                case 2: // left
                    area.x += palPan.getWidth();
                    area.width -= palPan.getWidth();
                    break;
                case 3: // right
                    area.width -= palPan.getWidth();
                    break;
            }
        }
        return area;
    }

    /**
     * Re-docks and/or rearranges all panels after resizing the frame or re-docking.
     */
    public void updatePanelsPos() {
        if (palPan != null) {
            palPan.reDock();
        }
        updateTubesPos();
        updateMinSize();
    }

    /**
     * Re-docks and/or rearranges the Tubes Board panel only.
     */
    public void updateTubesPos() {
        if (tubesPan != null) {
            tubesPan.reDock();
        }
    }

    /**
     * Updates Minimum size of the Frame after re-docking.
     */
    public void updateMinSize() {
        Dimension dim = new Dimension(100, 100);

        if (tubesPan != null) {
            dim.width = tubesPan.getWidth();
            dim.height = tubesPan.getHeight();

            if (palPan != null) {
                if (palPan.getDockedTo() > 1) { // left, right
                    dim.width += palPan.getWidth();
                    dim.height = Math.max(dim.height, palPan.getHeight());
                } else { // top, bottom
                    dim.width = Math.max(dim.width, palPan.getWidth());
                    dim.height += palPan.getHeight();
                }
            }
            if (toolPan != null) {
                if (toolPan.getDockedTo() > 1) { // left, right
                    dim.width += toolPan.getToolbarY();
                    dim.height = Math.max(dim.height, toolPan.getButtonsLength());
                } else { // top, bottom
                    dim.width = Math.max(dim.width, toolPan.getButtonsLength());
                    dim.height += toolPan.getToolbarY();
                }
            }

            dim.width += getWidth() - getContentPane().getWidth();
            dim.height += getHeight() - getContentPane().getHeight();
        }

        setMinimumSize(dim);
    }
}
