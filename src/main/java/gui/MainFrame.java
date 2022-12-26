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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

    private int width = 1000;
    private int height = 760;
    public static Palette pal;

    public static PalettePanel palPan;
    public static boolean colorsVisible;
    public static BoardPanel tubesPan;
    public static boolean tubesVisible;
    public static ToolPanel toolPan;

    public static PatternLayer pattern;

    CongratsPanel congPan = new CongratsPanel();

    public static GameMoves gameMoves = new GameMoves();
    public static int movesDone;

    /**
     * Mode of the game.
     */
    public static int gameMode;
    public final static int END_GAME = 0;
    public final static int FILL_MODE = 100;
    public final static int PLAY_MODE = 200;
    public final static int ASSIST_MODE = 300;
    public final static int VIEW_MODE = 400; // reserved for future use

    public static int filledTubes;
    public static int emptyTubes;

    private boolean saveOnExit = false;

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
                    congPan.updatePos();
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
        Image img;
        switch (core.Options.getOS()) {
            case MAC:
            case LINUX:
                img = Options.createImage("appicon_48.png");
                break;
            default:
                img = Options.createImage("appicon_32.png");
                break;
        }
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
        if (saveOnExit) {
            saveGame();
        }
        System.exit(0);
    }

    private void initElements() {
        getContentPane().setLayout(null);
        getContentPane().setBackground(Palette.backgroundColor);

        congPan.setVisible(false);
        getContentPane().add(congPan);

        toolPan = new ToolPanel();
        getContentPane().add(toolPan);

        pattern = new PatternLayer(core.Options.createBufImage("imgPattern.png"));
        getContentPane().add(pattern);
    }

    public void setGameMode(int aMode) {
        gameMode = aMode;
        toolPan.updateButtons(aMode);
    }

    public void updateLanguage() {
        setTitle(ResStrings.getString("strColorTubes"));
        if (toolPan != null)
            toolPan.updateLanguage();
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

    public void saveGame() {
        saveGame(TubesIO.tempFileName);
    }

    public void saveGame(String fileName) {
        if (tubesPan != null) {
            TubesIO.storeGameMode(gameMode);
            TubesIO.storeTubes(tubesPan.getModel(),
                    (gameMode != FILL_MODE) ? 0 : emptyTubes);
            TubesIO.storeMoves(gameMoves, movesDone);
            TubesIO.saveToFile(fileName, 2);
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
    public void startFillMode(int aFilled, int aEmpty) {
        setGameMode(FILL_MODE);
        clearBoard();
        addColorsPanel();
        addTubesPanel(aFilled, aEmpty);
        filledTubes = aFilled;
        emptyTubes = aEmpty;
        saveOnExit = true;
        startFindTubesTo();
        nextTubeTo(0);
    }

    public void startAutoFillMode(int aFilled, int aEmpty) {
        setGameMode(FILL_MODE);
        clearBoard();
        addTubesPanel(aFilled, aEmpty);
        filledTubes = aFilled;
        emptyTubes = aEmpty;
        autoFillTheRest();
    }

    public void resumeFillMode() {
        startFindTubesTo();
        nextTubeTo(0);
    }

    public void endFillMode() {
        gameMode = PLAY_MODE;
        gameMoves.clear();
        movesDone = 0;
        saveGame(TubesIO.tempFileName);

        if (palPan != null) {
            palPan.setVisible(false);
            colorsVisible = false;
            redockTubes();
        }
        startPlayMode();
    }

    public void startPlayMode() {
        congPan.setVisible(false);
        setGameMode(PLAY_MODE);

        for (int i = 0; i < tubesPan.getTubesCount(); i++) {
            ColorTube tube = tubesPan.getTube(i);
            tube.updateState();
            tube.setActive(!tube.isClosed());
            tube.setShade((tube.isClosed()) ? 4 : 0);
        }
        saveOnExit = true;
        setTubeFrom(null);
        movesDone = 0;
    }

    public void resumePlayMode() {
        setGameMode(PLAY_MODE);

        for (int i = 0; i < tubesPan.getTubesCount(); i++) {
            if (!tubesPan.getTube(i).isClosed()) {
                tubesPan.getTube(i).setActive(true);
            }
        }
        saveOnExit = true;
        setTubeFrom(null);
    }

    public void startAssistMode() {
        setGameMode(ASSIST_MODE);
        setTubeFrom(null);
        saveOnExit = true;
        hideMove();
        showMove();
    }

    public void endAssistMode() {
        setGameMode(PLAY_MODE);
        saveOnExit = true;
        if (gameMoves.size() > movesDone) {
            tubesPan.getTube(gameMoves.getTubeFrom(movesDone)).hideArrow();
            tubesPan.getTube(gameMoves.getTubeFrom(movesDone)).hideShade();
            tubesPan.getTube(gameMoves.getTubeTo(movesDone)).hideArrow();
            tubesPan.getTube(gameMoves.getTubeTo(movesDone)).hideShade();
            tubesPan.getTube(gameMoves.getTubeTo(movesDone)).setArrow(1);
        }
        while (gameMoves.size() > movesDone) {
            gameMoves.remove(gameMoves.size() - 1);
        }
        setTubeFrom(null);
    }

    public void endGame() {
        saveOnExit = false;
        setGameMode(END_GAME);
        TubesIO.fileDelete(TubesIO.tempFileName);

        for (int i = 0; i < tubesPan.getTubesCount(); i++) {
            tubesPan.getTube(i).setActive(false);
            tubesPan.getTube(i).setClosed(true);
            tubesPan.getTube(i).setShade(4);
            tubesPan.getTube(i).showShade();
        }

        gameMoves.clear();
        movesDone = 0;
        congPan.setVisible(true);
    }

    public void clearBoard() {
        congPan.setVisible(false);
        if (tubesPan != null) {
            tubesPan.saveOptions();
            tubesVisible = false;
            remove(tubesPan);
            tubesPan.clear();
            tubesPan = null;
        }
        if (palPan != null) {
            palPan.saveOptions();
            colorsVisible = false;
            remove(palPan);
            palPan.removeAll();
            palPan = null;
        }
        gameMoves.clear();
        movesDone = 0;
        TubesIO.clearMoves();
        TubesIO.clearTubes();
        Palette.usedColors.clearAllColorCounts();
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
        palPan.addPopups();
        palPan.reDock();
        palPan.setVisible(true);
        colorsVisible = true;
        getContentPane().add(palPan);

        if (getContentPane().getComponentZOrder(palPan) > getContentPane().getComponentZOrder(pattern)) {
            getContentPane().setComponentZOrder(pattern, getContentPane().getComponentZOrder(palPan));
        }
    }

    public void addTubesPanel(int aFilled, int aEmpty) {

        tubesPan = new BoardPanel() {
            @Override
            public void clickTube(ColorTube tube) {
                clickColorTube(tube);
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
                clearColorTube(tube);
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
        tubesPan.updateLocation();

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

    public ColorTube getTubeTo() {
        return tubesPan.getTubeTo();
    }

    public void setTubeTo(ColorTube tube) {

        int howMuch;
        tubesPan.setTubeTo(tube);
        if (tube != null && getTubeTo() != null) {
            getTubeTo().hideArrow();
        }

        if (gameMode == PLAY_MODE) {
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
                    if (howMuch == gameMoves.getMoveCount(movesDone)) //                    if (movesDone < gameMoves.size())
                    {
                        movesDone++;
                        toolPan.updateButtons();
                    }
                    tube.hideArrow();
                    if (!tube.isClosed()) {
                        tube.hideShade();
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
                tubeTo.setArrow(2);
                tubeTo.setShade(3);

                tubeTo.showArrow();
                tubeTo.pulseShade();

            }
        }
    }

    public void startFindTubesFrom() {
        for (int i = 0; i < tubesPan.getTubesCount(); i++) {
            if (tubesPan.getTube(i).isActive()) {
                tubesPan.getTube(i).setArrowWhenHide(1);
            }
        }
    }

    public void startFindTubesTo() {
        for (int i = 0; i < tubesPan.getTubesCount(); i++) {
            if (tubesPan.getTube(i).isActive()) {
                tubesPan.getTube(i).setArrowWhenHide(2);
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////
//                  
//                  *  FILL MODE routines *
//                  
//////////////////////////////////////////////////////////////////////////////
    public void clearColorTube(ColorTube tube) {
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
            MainFrame.toolPan.updateButtons();
            setTubeTo(tube);
        }
    }

    public void clearAllTubes() {
        for (int i = 0; i < tubesPan.getTubesCount(); i++) {
            clearColorTube(tubesPan.getTube(i));
        }

        // clear all buttons counts 
        for (int i = 0; i < palPan.getColorsCount(); i++) {
            palPan.getButton(i).setCount(4);
        }

        Palette.usedColors.clearAllColorCounts();
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
                getTubeFrom().hideShade();
                getTubeFrom().hideArrow();
            }
            if (getTubeTo() != null) {
                getTubeTo().hideShade();
                getTubeTo().hideArrow();
            }
            if (gameMoves.size() > movesDone) {
                ColorTube tubeFrom = tubesPan.getTube(gameMoves.getTubeFrom(movesDone));
                ColorTube tubeTo = tubesPan.getTube(gameMoves.getTubeTo(movesDone));

                tubeFrom.setArrow(1);
                tubeFrom.showArrow();
                tubeFrom.setShade(2);
                tubeFrom.pulseShade();

                tubeTo.setArrow(2);
                tubeTo.showArrow();
                tubeTo.hideShade();
            }
        }
    }

    public void hideMove() {
        if (gameMode == ASSIST_MODE) {
            for (int i = 0; i < tubesPan.getTubesCount(); i++) {
                tubesPan.getTube(i).hideArrow();
                if (!tubesPan.getTube(i).isClosed()) {
                    tubesPan.getTube(i).hideShade();
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

    public void clickColorTube(ColorTube tube) {
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
                            // выход из режима помощника
                            MessageDlg msgFrame = new MessageDlg(this,
                                    ResStrings.getString("strExitAssistMode"),
                                    MessageDlg.BTN_YES_NO);
                            msgFrame.setButtonsLayout(MessageDlg.BTN_LAYOUT_RIGHT);
                            msgFrame.setVisible(true);
                            if (msgFrame.modalResult > 0) {
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
                        // выход из режима помощника
                        MessageDlg msgFrame = new MessageDlg(this,
                                ResStrings.getString("strExitAssistMode"),
                                MessageDlg.BTN_YES_NO);
                        msgFrame.setButtonsLayout(MessageDlg.BTN_LAYOUT_RIGHT);
                        msgFrame.setVisible(true);
                        if (msgFrame.modalResult > 0) {
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
        if (palPan != null && colorsVisible) {
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
        if (palPan != null && colorsVisible) {
            palPan.reDock();
        }
        redockTubes();
        calculateMinSize();
    }

    public Dimension calculateMinSize() {
        Dimension dim = new Dimension(100, 100);

        if (tubesPan != null) {
            dim.width = tubesPan.getWidth();
            dim.height = tubesPan.getHeight();

            if (palPan != null && colorsVisible) {
                if (palPan.getDockedTo() > 1) { // left, right
                    dim.width = dim.width + palPan.getWidth();
                    dim.height = Math.max(dim.height, palPan.getHeight());
                } else {
                    dim.width = Math.max(dim.width, palPan.getWidth());
                    dim.height = dim.height + palPan.getHeight();
                }
            }
            if (toolPan != null) {
                if (toolPan.getDockedTo() > 1) { // left, right
                    dim.width = dim.width + toolPan.getButtonsHeight();
                    dim.height = Math.max(dim.height, toolPan.getButtonsWidth());
                } else {
                    dim.width = Math.max(dim.width, toolPan.getButtonsWidth());
                    dim.height = dim.height + toolPan.getButtonsHeight();
                }

            }

            dim.width += getWidth() - getContentPane().getWidth();
            dim.height += getHeight() - getContentPane().getHeight();

        }

        setMinimumSize(dim);
        return dim;
    }

}
