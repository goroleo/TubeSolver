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
import lib.lOpenSaveDialog.LOpenSaveDialog;
import run.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


@SuppressWarnings("unused")
public class MainFrame extends JFrame {

    private int width = 1000;
    private int height = 760;
    public static Palette pal;

    public static PalettePanel palPan;
    public static BoardPanel tubesPan;

    /**
     * The toolbar with action buttons.
     */
    public static ToolPanel toolPan;

    /**
     * The background layer of the Main frame.
     */
    private static PatternLayer pattern;

    private final static CongratsPanel congPan = new CongratsPanel();

    private static SolvePanel solvePan;

    public static GameMoves gameMoves = new GameMoves();
    public static int movesDone;

    /**
     * Mode of the game.
     */
    public static int gameMode;
    public static int prevMode; // previous mode
    public final static int END_GAME = 0;
    public final static int FILL_MODE = 100;
    public final static int PLAY_MODE = 200;
    public final static int ASSIST_MODE = 300;
    public final static int BUZY_MODE = 400;

    private static int filledTubes;
    private static int emptyTubes;

    private boolean saveTempOnExit = false;
    private String fileNameSuffix;


//////////////////////////////////////////////////////////////////////////////
//                  
//                  *  main frame routines *
//                  
//////////////////////////////////////////////////////////////////////////////

    public MainFrame() {
        super(ResStrings.getString("strColorTubes"));

        pal = new Palette();

        createFrame();
        initElements();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                pattern.setBounds(getColorsArea());
                toolPan.resize();
                updatePanelsPos();
                if (congPan.isVisible()) {
                    congPan.updateSizeAndPos();
                }
            }
        });

    }

    private void createFrame() {

        Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();

        if (Options.mainSizeX >= 300 && Options.mainSizeY >= 200
                && Options.mainPositionX >= 0
                && Options.mainPositionY >= 0
                && Options.mainPositionX + Options.mainSizeX <= sSize.width
                && Options.mainPositionY + Options.mainSizeY <= sSize.height) {
            setSize(Options.mainSizeX, Options.mainSizeY);
            width = Options.mainSizeX;
            height = Options.mainSizeY;
            setBounds(Options.mainPositionX, Options.mainPositionY, width, height);
        } else {
            width = Math.min(width, sSize.width - 40);
            height = Math.min(height, sSize.height - 40);
            setBounds((sSize.width - width) / 2, (sSize.height - height) / 2, width, height);
        }

        if (Options.mainMaximized) {
            setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);
        }

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        Image img = Options.getAppIcon();
        setIconImage(img);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeFrame();
            }
        });

    }

    public void showFrame() {
        EventQueue.invokeLater(() -> setVisible(true));
        StartDlg startFrame = new StartDlg(this);
        EventQueue.invokeLater(() -> startFrame.setVisible(true));
    }

    public void closeFrame() {
        if (solvePan.isVisible()) {
            solvePan.stopSolver(1);
            return;
        }

        saveOptions();
        pal.savePalette();
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

    private void initElements() {
        getContentPane().setLayout(null);
        getContentPane().setBackground(Palette.backgroundColor);

        solvePan = new SolvePanel();
        getContentPane().add(solvePan);

        congPan.setVisible(false);
        getContentPane().add(congPan);

        toolPan = new ToolPanel();
        getContentPane().add(toolPan);

        pattern = new PatternLayer(core.Options.createBufImage("imgPattern.png"));
        getContentPane().add(pattern);
    }

    public void setGameMode(int aMode) {
        if (aMode != gameMode) {
            prevMode = gameMode;
            gameMode = aMode;
            toolPan.updateButtons(aMode);
        }
    }

    public void updateLanguage() {
        setTitle(ResStrings.getString("strColorTubes"));
        if (toolPan != null)
            toolPan.updateLanguage();
        if (solvePan != null)
            solvePan.updateLanguage();
    }

//////////////////////////////////////////////////////////////////////////////
//                  
//                  *  load and save routines  *
//                  
//////////////////////////////////////////////////////////////////////////////

    public boolean loadGame(String fileName) {
        clearBoard();
        boolean result = TubesIO.loadFromFile(fileName);
        if (result) {
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

                for (int i = filledTubes; i < tubesPan.getTubesCount(); i++) {
                    tubesPan.getTube(i).setClosed(true);
                    tubesPan.getTube(i).setActive(false);
                }
            } else {
                addTubesPanel(0, 0);
                TubesIO.restoreTubes(tubesPan);
                movesDone = TubesIO.restoreMoves(gameMoves);
            }
        }
        return result;
    }

    public void saveTempGame() {
        saveGame(TubesIO.tempFileName);
    }

    public void saveGame(String fileName) {
        if (tubesPan != null) {
            if (gameMode != BUZY_MODE)
                TubesIO.storeGameMode(gameMode);
            else
                TubesIO.storeGameMode(prevMode);

            TubesIO.storeTubes(tubesPan,
                    (gameMode != FILL_MODE) ? 0 : emptyTubes);
            TubesIO.storeMoves(gameMoves, movesDone);
            TubesIO.saveToFile(fileName, 2);
        }
    }

    public void saveGameAs() {
        saveGameAs("");
    }

    public void saveGameAs(String suffix) {
        LOpenSaveDialog os;

        if (!"".equals(suffix)) {
            os = new LOpenSaveDialog(this,
                    LOpenSaveDialog.SAVE_MODE,
                    Options.getDateTimeStr() + " " + suffix);
        } else {
            os = new LOpenSaveDialog(this, LOpenSaveDialog.SAVE_MODE);
        }

        String fileName = os.showSaveDialog();
        if (!"".equals(fileName)) {
            saveGame(fileName);
        }

    }

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
//                  *  modes *
//
//////////////////////////////////////////////////////////////////////////////
    public void startManualFillMode(int aFilled, int aEmpty) {
        setGameMode(FILL_MODE);
        clearBoard();
        addColorsPanel();
        addTubesPanel(aFilled, aEmpty);
        filledTubes = aFilled;
        emptyTubes = aEmpty;
        saveTempOnExit = true;
        fileNameSuffix = ResStrings.getString("strSaveIDManualFill");
        tubesPan.paintImmediately(tubesPan.getBounds());
        startFindTubesTo();
        nextTubeTo(0);
    }

    public void startAutoFillMode(int aFilled, int aEmpty) {
        fileNameSuffix = ResStrings.getString("strSaveIDAutoFill");
        setGameMode(FILL_MODE);
        clearBoard();
        addTubesPanel(aFilled, aEmpty);
        filledTubes = aFilled;
        emptyTubes = aEmpty;
        tubesPan.paintImmediately(tubesPan.getBounds());
        autoFillTheRest();
    }

    public void resumeManualFillMode() {
        setGameMode(FILL_MODE);
        fileNameSuffix = ResStrings.getString("strSaveIDManualFill");
        tubesPan.paintImmediately(tubesPan.getBounds());
        startFindTubesTo();
        nextTubeTo(0);
    }

    public void endFillMode() {
        // hiding palette
        if (palPan != null) {
            palPan.setVisible(false);
            redockTubes();
        }

        setGameMode(PLAY_MODE);
        // preparing tubes for the play mode
        for (int i = 0; i < tubesPan.getTubesCount(); i++) {
            ColorTube tube = tubesPan.getTube(i);
            tube.setClosed(tube.getModel().state == 3);
            tube.setActive(!tube.isClosed());
        }
        setTubeFrom(null);

        // switching to play mode
        gameMoves.clear();
        movesDone = 0;
        saveTempOnExit = true;

        // saving
        saveTempGame();
        if (Options.saveGameAfterFill) {
            saveGameAs(fileNameSuffix);
        }
    }

    public void startPlayMode() {
        setGameMode(PLAY_MODE);

        for (int i = 0; i < tubesPan.getTubesCount(); i++) {
            if (!tubesPan.getTube(i).isClosed()) {
                tubesPan.getTube(i).setActive(true);
            }
        }
        saveTempOnExit = true;
        setTubeFrom(null);
    }

    public void startAssistMode() {
        setGameMode(ASSIST_MODE);
        setTubeFrom(null);
        saveTempOnExit = true;
        hideMove();
        showMove();
    }

    public void endAssistMode() {
        setGameMode(PLAY_MODE);
        saveTempOnExit = true;
        if (gameMoves.size() > movesDone) {
            tubesPan.getTube(gameMoves.getTubeFrom(movesDone)).hideArrow();
            tubesPan.getTube(gameMoves.getTubeFrom(movesDone)).hideFrame();
            tubesPan.getTube(gameMoves.getTubeTo(movesDone)).hideArrow();
            tubesPan.getTube(gameMoves.getTubeTo(movesDone)).hideFrame();
        }
        while (gameMoves.size() > movesDone) {
            gameMoves.remove(gameMoves.size() - 1);
        }
        setTubeFrom(null);
        setTubeTo(null);
    }

    public void startSolve() {

        setGameMode(BUZY_MODE);

        MessageDlg msgDlg = new MessageDlg(Main.frame,
                ResStrings.getString("strFindSolution"),
                MessageDlg.BTN_YES_NO);
        msgDlg.setButtonsLayout(MessageDlg.BTN_LAYOUT_RIGHT);
        msgDlg.setVisible(true);


        if (msgDlg.result > 0) {
            setGameMode(BUZY_MODE);
            setResizable(false);
            solvePan.startSolve(tubesPan.getModel());
        } else {
            setGameMode(prevMode);
        }

    }

    public void endSolve(int reason) {

        MessageDlg msgDlg;

        setResizable(true);

        switch (reason) {
            case 0: // working
                break;
            case 1: // escape-cancel pressed
                msgDlg = new MessageDlg(this,
                        ResStrings.getString("strCancelSolution"),
                        MessageDlg.BTN_OK);
                msgDlg.setButtonsLayout(MessageDlg.BTN_LAYOUT_CENTER);
                msgDlg.setVisible(true);
                break;
            case 2: // not solved
                msgDlg = new MessageDlg(this,
                        ResStrings.getString("strNotSolved"),
                        MessageDlg.BTN_OK);
                msgDlg.setButtonsLayout(MessageDlg.BTN_LAYOUT_RIGHT);
                msgDlg.setVisible(true);
                break;
            case 3: // solved!
                msgDlg = new MessageDlg(this,
                        ResStrings.getString("strSolutionSuccess"),
                        MessageDlg.BTN_YES_NO);
                msgDlg.setButtonsLayout(MessageDlg.BTN_LAYOUT_RIGHT);
                msgDlg.setVisible(true);
                if (msgDlg.result == 0) reason = 0;
        }

        if (reason == 3) {
            startAssistMode();
            if (Options.saveGameAfterSolve) {
                saveGameAs(ResStrings.getString("strSaveIDSolved"));
            }
        } else {
            endAssistMode();
            startPlayMode();
        }
    }


    public void endGame() {
        saveTempOnExit = false;
        TubesIO.fileDelete(TubesIO.tempFileName);

        for (int i = 0; i < tubesPan.getTubesCount(); i++) {
            tubesPan.getTube(i).setActive(false);
            tubesPan.getTube(i).setClosed(true);
            tubesPan.getTube(i).setFrame(4);
            tubesPan.getTube(i).showFrame();
        }

        setGameMode(END_GAME);
        gameMoves.clear();
        movesDone = 0;

        congPan.setVisible(true);
    }

    public void clearBoard() {
        congPan.setVisible(false);
        if (tubesPan != null) {
            tubesPan.saveOptions();
            remove(tubesPan);
            tubesPan.clearBoard();
            tubesPan = null;
        }
        if (palPan != null) {
            palPan.saveOptions();
            remove(palPan);
            palPan.removeAll();
            palPan = null;
        }
        gameMoves.clear();
        movesDone = 0;
        TubesIO.clearMoves();
        TubesIO.clearTubes();
        Palette.usedColors.clearColorCounts();
        toolPan.updateButtons();
        repaint();
    }


//////////////////////////////////////////////////////////////////////////////
//                  
//                  *  create panels *
//                  
//////////////////////////////////////////////////////////////////////////////

    public void addColorsPanel() {
        palPan = new PalettePanel() {
            @Override
            public void clickButton(ColorButton cb) {
                clickColorButton(cb);
            }

            @Override
            public void changeColor(ColorButton cb) {
                int colorNumber = cb.getColorNumber();
                Color oldColor = pal.getColor(colorNumber);
                super.changeColor(cb);
                Color newColor = pal.getColor(colorNumber);
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
        for (int i = 0; i < pal.size() - 1; i++) {
            palPan.getButton(i).setCount(4);
        }
        palPan.addPopups();
        palPan.reDock();
        palPan.setVisible(true);
        getContentPane().add(palPan);

        if (getContentPane().getComponentZOrder(palPan) > getContentPane().getComponentZOrder(pattern)) {
            getContentPane().setComponentZOrder(pattern, getContentPane().getComponentZOrder(palPan));
        }
    }

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

        getContentPane().add(tubesPan);
        tubesPan.setDockedTo(0);
        tubesPan.restoreLocation();

        tubesPan.setVisible(true);

        if (getContentPane().getComponentZOrder(tubesPan) > getContentPane().getComponentZOrder(pattern)) {
            getContentPane().setComponentZOrder(pattern, getContentPane().getComponentZOrder(tubesPan));
        }

    }

//////////////////////////////////////////////////////////////////////////////
//
//                  *  ALL MODES routines *
//
//////////////////////////////////////////////////////////////////////////////
    public boolean canShowArrow(ColorTube tube) {
        switch (gameMode) {
            case FILL_MODE:
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

    public ColorTube getTubeFrom() {
        return tubesPan.getTubeFrom();
    }

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

    public ColorTube getTubeTo() {
        return tubesPan.getTubeTo();
    }

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

    public void startFindTubesFrom() {
        for (int i = 0; i < tubesPan.getTubesCount(); i++) {
            if (tubesPan.getTube(i).isActive()) {
                tubesPan.getTube(i).setArrowWhenHide(ColorTube.ARROW_GREEN);
            }
        }
    }

    public void startFindTubesTo() {
        for (int i = 0; i < tubesPan.getTubesCount(); i++) {
            if (tubesPan.getTube(i).isActive()) {
                tubesPan.getTube(i).setArrowWhenHide(ColorTube.ARROW_YELLOW);
            }
        }
    }

//////////////////////////////////////////////////////////////////////////////
//                  
//                  *  FILL MODE routines *
//                  
//////////////////////////////////////////////////////////////////////////////
    public void clearTube(ColorTube tube) {
        if (tube != null) {
            byte clrNum;
            if (tube.isActive()) {

                for (int i = tube.getColorsCount() - 1; i >= 0; i--) {
                    clrNum = tube.getCurrentColor();
                    tube.extractColor();

                    ColorButton pb = palPan.getButtonByColor(clrNum);
                    if (pb.getCount() < 0) {
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

    public void clearAllTubes() {
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

    public void disableUnusedColors() {
        for (int i = 0; i < palPan.getColorsCount(); i++) {
            if (Palette.usedColors.getColorCount((byte) palPan.getButtonColorNum(i)) == 0) {
                palPan.getButton(i).setCount(-1);
            }
        }
    }

    public void fillUnusedColors() {
        for (int i = 1; i < pal.size(); i++) {
            if (Palette.usedColors.getColorCount((byte) i) == 0) {
                Palette.usedColors.setColorCount((byte) i, 4);
            }
        }
    }

    public void autoFillTheRest() {
        fileNameSuffix = ResStrings.getString("strSaveIDAutoFill");
        for (int t = 0; t < filledTubes; t++) {
            for (int i = tubesPan.getTube(t).getColorsCount(); i < 4; i++) {
                int clr = Palette.usedColors.getRandomColor();
                tubesPan.getTube(t).putColor(clr);
                Palette.usedColors.incColorCount((byte) clr);
                if (Palette.usedColors.getAllUsedColors() >= filledTubes) {
                    fillUnusedColors();
                }
            }
        }
        tubesPan.paintImmediately(tubesPan.getBounds());
        endFillMode();
    }

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
//                  *  click routines *
//                  
//////////////////////////////////////////////////////////////////////////////

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
                    if (Palette.usedColors.getAllFilledColors() >= filledTubes) {
                        endFillMode();
                    }
                }
            }
        }
    }

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
                            setGameMode(BUZY_MODE);
                            MessageDlg msgFrame = new MessageDlg(this,
                                    ResStrings.getString("strExitAssistMode"),
                                    MessageDlg.BTN_YES_NO);
                            msgFrame.setButtonsLayout(MessageDlg.BTN_LAYOUT_RIGHT);
                            msgFrame.setVisible(true);
                            if (msgFrame.result > 0) {
                                endAssistMode();
                            } else
                                setGameMode(prevMode);
                        }
                    }
                } else if (getTubeFrom() == tube) {
                    setTubeFrom(null);
                } else if (tubesPan.getTubeNumber(tube) == gameMoves.getTubeTo(movesDone)) {
                    setTubeTo(tube);
                } else {
                    if (!tube.isClosed()) {
                        // exit from the assist mode
                        setGameMode(BUZY_MODE);
                        MessageDlg msgFrame = new MessageDlg(this,
                                ResStrings.getString("strExitAssistMode"),
                                MessageDlg.BTN_YES_NO);
                        msgFrame.setButtonsLayout(MessageDlg.BTN_LAYOUT_RIGHT);
                        msgFrame.setVisible(true);
                        if (msgFrame.result > 0) {
                            endAssistMode();
                        } else
                            setGameMode(prevMode);
                    }
                }

                break;
            default:
                break;
        }
    }

//////////////////////////////////////////////////////////////////////////////
//                  
//                  *  position & resize routines *
//                  
//////////////////////////////////////////////////////////////////////////////

    public Rectangle getClientArea() {
        return getContentPane().getBounds();
    }

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

    public void redockTubes() {
        if (tubesPan != null) {
            tubesPan.reDock();
        }
    }

    public void updatePanelsPos() {
        if (palPan != null) {
            palPan.reDock();
        }
        redockTubes();
        updateMinSize();
    }

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
