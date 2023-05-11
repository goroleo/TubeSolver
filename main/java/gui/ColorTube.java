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

import ani.*;
import core.TubeModel;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Color tube is a GUI component a visualization of the TubeModel.
 * Color tube has 4 cells of colors. The tube can be as filled by different or
 * same colors, as it can be empty. Colors can be <b>extracted</b> from the tube,
 * and <b>put</b> into again.
 *
 * @see TubeModel
 */
public class ColorTube extends JComponent {

// --- TUBE MODEL ---
    /**
     * The logical model of the tube without any visualization.
     */
    private final TubeModel model = new TubeModel();

// --- Images: ---
    private static BufferedImage imgBottle = null;
    private static BufferedImage imgCork = null;
    private static BufferedImage imgShadeGreen = null;
    private static BufferedImage imgShadeRed = null;
    private static BufferedImage imgShadeBlue = null;
    private static BufferedImage imgShadeYellow = null;
    private static BufferedImage imgShadeGray = null;
    private static BufferedImage imgArrowYellow = null;
    private static BufferedImage imgArrowGreen = null;

// --- Layers: ---
    /**
     * The layer with color cells.
     * @see ColorLayer
     */
    private final ColorLayer colors;

    /**
     * The layer with the gray frame around the tube. The layer shows when the mouse cursor is on the tube.
     */
    private final ShadeLayer shade;

    /**
     * The layer with the color frame around the tube. The layer shows the mode of the tube.
     */
    private final ShadeLayer frame;

    /**
     * The layer with the cork. The layer shows the closed tube.
     */
    private final SlideLayer cork;

    /**
     * The layer with the arrow above the tube. The layer shows tube FROM and tube TO.
     */
    private final WaveLayer arrow;

// --- Color frames around the tube: ---
    /**
     * The current frame number.
     */
    private int frameNum = 0;

    /**
     * No frame about the tube.
     */
    public final static int FRAME_NO_COLOR = 0;

    /**
     * There is a RED frame about the tube.
     */
    public final static int FRAME_RED = 1;

    /**
     * There is a GREEN frame about the tube.
     */
    public final static int FRAME_GREEN = 2;

    /**
     * There is a YELLOW frame about the tube.
     */
    public final static int FRAME_YELLOW = 3;

    /**
     * There is a BLUE frame about the tube.
     */
    public final static int FRAME_BLUE = 4;

// --- Arrows above the tube: ---
    /**
     * The current arrow number.
     */
    private int arrowNum = 0;

    /**
     * There is a GREEN arrow above the tube. (Shows the direction FROM tube).
     */
    public final static int ARROW_GREEN = 1;

    /**
     * There is a YELLOW arrow above the tube. (Shows the direction INTO tube).
     */
    public final static int ARROW_YELLOW = 2;

// --- Tube states: ---
    private boolean active = true;

    /**
     * The tube can be closed in 2 cases. In the game mode, a closed tube is a tube, all cells of
     * which are filled with the same color. In fill mode, a closed tube is a tube that cannot be
     * filled and must remain empty.
     */
    private boolean closed = false;

///////////////////////////////////////////////////////////////////////////
//
//               * main routines *
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Creates the color tube.
     */
    public ColorTube() {
        loadImages();

        int w = imgBottle.getWidth();
        int h = imgBottle.getHeight() + 20;

        arrow = new WaveLayer(imgArrowGreen);
        arrow.setLocation(0, 0);
        this.add(arrow);

        cork = new SlideLayer(null);
        cork.setBounds(0, 10, imgCork.getWidth(), imgCork.getHeight());
        cork.setExpDegrees(-0.5d, 0.5d);
        this.add(cork);

        frame = new ShadeLayer(imgShadeGray);
        frame.setLocation(0, 20);
        this.add(frame);

        ImageLayer bottle = new ImageLayer(imgBottle);
        bottle.setLocation(0, 20);
        this.add(bottle);

        shade = new ShadeLayer(imgShadeGray);
        shade.setLocation(0, 20);
        this.add(shade);

        colors = new ColorLayer(w, h - 20);
        colors.setLocation(0, 20);
        this.add(colors);
        colors.useAnimation = true;

        setSize(w, h);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (active && e.getButton() == 1) {
                    doClick();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (active) {
                    shade.doShow();
                    if (canShowArrow()) {
                        arrow.startUnlimited();
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                shade.doHide();
                if (canHideArrow()) {
                    arrow.stop();
                }
            }
        });
    }

    /**
     * Gets the Tube model.
     *
     * @return Tube's logical model.
     */
    public TubeModel getModel() {
        return model;
    }

    /**
     * Handles mouse click on the color tube. The routine to override it.
     */
    public void doClick() {
        // method to override
    }

///////////////////////////////////////////////////////////////////////////
//
//               * tube state routines *
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Is this tube empty?
     *
     * @return true if the tube hasn't any colors.
     */
    public boolean isEmpty() {
        return model.isEmpty();
    }

    /**
     * Is this tube active?
     *
     * @return true if the tube is still in the game.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the tube's activity.
     *
     * @param b true if this tube becomes active, false otherwise.
     */
    public void setActive(boolean b) {
        if (!b) {
            arrow.stop();
            shade.doHide();
        }
        active = b;
    }

    /**
     * The tube can be closed in 2 cases. In the game mode, a closed tube is a tube, all cells of
     * which are filled with the same color. In fill mode, a closed tube is a tube that cannot be
     * filled and must remain empty.
     *
     * @return true if this tube is closed or false otherwise.
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Sets the closed tube.<br>
     * The tube can be closed in 2 cases. In the game mode, a closed tube is a tube, all cells of
     * which are filled with the same color. In fill mode, a closed tube is a tube that cannot be
     * filled and must remain empty.
     *
     * @param b true if this tube becomes closed, false otherwise.
     */
    public void setClosed(boolean b) {
        if (b != closed) {
            closed = b;
            cork.setSecondImageToFirst();
            if (closed) {
                cork.setSecondImage(imgCork);
                cork.setStartPosOfSecondImg(0, -imgCork.getHeight() * 2);
                cork.setEndPosOfFirstImg(0, 0);
            } else {
                cork.setSecondImage(null);
                cork.setStartPosOfSecondImg(0, 0);
                cork.setEndPosOfFirstImg(0, -imgCork.getHeight() / 2);
                setFrame(0);
            }
            cork.start();
        }
        if (closed) {
            if (MainFrame.gameMode == MainFrame.FILL_MODE) {
                this.setFrame(1);
            } else {
                this.setFrame(4);
            }
            showFrame();
        }
    }

///////////////////////////////////////////////////////////////////////////
//
//               * tube colors routines *
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Gets the number of filled color cells in this tube.
     *
     * @return colors count
     */
    public int getColorsCount() {
        return model.count;
    }

    /**
     * Gets the current available color of this tube. Current color is always the top color of the tube.
     *
     * @return current tube color.
     */
    public byte getCurrentColor() {
        return model.currentColor;
    }

    /**
     * Gets the color at the specified color cell.
     *
     * @param number number of color cell.
     * @return color number
     */
    public int getColor(int number) {
        if (number >= 0 && number < 4) {
            return model.colors[number];
        } else {
            return 0;
        }
    }

    /**
     * Is it possible to put the specified color in the tube?
     *
     * @param colorNum color number from the palette.
     * @return true if this color can be placed in the tube, false otherwise.
     */
    public boolean canPutColor(int colorNum) {
        switch (MainFrame.gameMode) {
            case 100: // FILL_MODE
                return active && getColorsCount() < 4;
            case 200: // GAME MODE
                return model.canPutColor((byte) colorNum);
            default:
                return false;
        }
    }

    /**
     * Puts the color into the tube. Please note this routine don't check the possibility of the operation
     * that's why it can be also used for initial filling the tube.
     *
     * @param colorNum color number
     */
    public void putColor(int colorNum) {
        if (model.putColor((byte) colorNum)) {
            colors.addColor(colorNum);
            setClosed(model.state == 3);
        }
    }

    /**
     * Extracts one color from the tube. Only the top color can be extracted.
     * Please note this routine don't check the possibility of the operation.
     */
    public void extractColor() {
        if (model.extractColor() != 0) {
            colors.removeColor();
            setClosed(model.state == 3);
        }
    }

    /**
     * Sets the animation of colors put and extract.
     *
     * @param b true if you want to animate the colors change, false otherwise.
     */
    public void setColorsAnimation(boolean b) {
        colors.useAnimation = b;
    }

    /**
     * Repaint all colors in the tube.
     */
    public void repaintColors() {
        for (int i = 0; i < 4; i++) {
            if (i < model.count) {
                colors.repaintColor(i + 1, getColor(i), false);
            } else {
                colors.repaintColor(i + 1, 0, false);
            }
        }
        colors.repaint();
    }

    /**
     * Fills this tube color cells from the stored integer variable.
     *
     * @param storedColors integer value of the stored colors.
     */
    public void restoreColors(int storedColors) {
        for (int i = 0; i < 4; i++) {
            putColor((byte) (storedColors & 0xff));
            storedColors = storedColors >> 8;
        }
    }

    /**
     * Clears the tube.
     */
    public void clear() {
        boolean oldUseAnimation = colors.useAnimation;
        colors.useAnimation = false;
        model.clear();
        colors.clearColors();
        colors.useAnimation = oldUseAnimation;
        setClosed(false);
    }

///////////////////////////////////////////////////////////////////////////
//
//               * tube frame routines *
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Gets a number of the frame around the tube.
     * (FRAME_NO_COLOR, FRAME_RED, FRAME_GREEN, FRAME_YELLOW, FRAME_BLUE)
     *
     * @return frame number
     */
    public int getFrame() {
        return frameNum;
    }

    /**
     * Sets a frame around the tube by its number.
     * (FRAME_NO_COLOR, FRAME_RED, FRAME_GREEN, FRAME_YELLOW, FRAME_BLUE)
     *
     * @param newFrameNum a new number of the frame.
     */
    public void setFrame(int newFrameNum) {
        if (frameNum != newFrameNum) {
            switch (newFrameNum) {
                case FRAME_RED:
                    frame.setImage(imgShadeRed);
                    break;
                case FRAME_GREEN:
                    frame.setImage(imgShadeGreen);
                    break;
                case FRAME_YELLOW:
                    frame.setImage(imgShadeYellow);
                    break;
                case FRAME_BLUE:
                    frame.setImage(imgShadeBlue);
                    break;
                default: // FRAME_NO_COLOR
                    hideFrame();
                    break;
            }
            frameNum = newFrameNum;
        }
    }

    /**
     * Shows a frame around the tube. Frame color and/or frame number must be set before.
     */
    public void showFrame() {
        frame.useAnimation = true;
        frame.doShow();
    }

    /**
     * Hides a frame around the tube.
     */
    public void hideFrame() {
        frame.useAnimation = true;
        frame.doHide();
    }

    /**
     * Pulses a frame around the tube, i.e. shows and hides it without stopping.
     */
    public void pulseFrame() {
        frame.useAnimation = true;
        frame.doPulse();
    }

///////////////////////////////////////////////////////////////////////////
//
//               * tube arrow routines *
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Sets an arrow above the tube by its number.
     * (ARROW_GREEN, ARROW_YELLOW, ARROW_NO COLOR)
     *
     * @param newArrowNum a new number of the arrow.
     */
    public void setArrow(int newArrowNum) {
        switch (newArrowNum) {
            case ARROW_GREEN:
                arrow.setImage(imgArrowGreen);
                break;
            case ARROW_YELLOW:
                arrow.setImage(imgArrowYellow);
                break;
            default: // ARROW_NO_COLOR
                hideArrow();
        }
        arrowNum = newArrowNum;
    }

    /**
     * Sets a new arrow above the tube, not right now, but when the previous arrow will disappear.
     * Delayed replacement.(ARROW_GREEN, ARROW_YELLOW, ARROW_NO COLOR)
     *
     * @param newArrowNum a new number of the arrow.
     */
    public void setArrowWhenHide(int newArrowNum) {
        if (arrowNum != newArrowNum) {
            arrow.stop();
            switch (newArrowNum) {
                case ARROW_GREEN:
                    arrow.setImageWhenHide(imgArrowGreen);
                    break;
                case ARROW_YELLOW:
                    arrow.setImageWhenHide(imgArrowYellow);
                    break;
                default: // ARROW_NO_COLOR
                    break;
            }
            arrowNum = newArrowNum;
        }
    }

    /**
     * Shows an arrow above the tube.
     */
    public void showArrow() {
        if (arrowNum != 0) {
            arrow.startUnlimited();
        }
    }

    /**
     * Hides an arrow above the tube.
     */
    public void hideArrow() {
        arrow.stop();
    }

    /**
     * The routine for introducing additional conditions for showing an arrow above the tube.
     * You can override it.
     * @return all what you want (now true)
     */
    public boolean canShowArrow() {
        // routine to override
        return true;
    }

    /**
     * The routine for introducing additional conditions for hiding an arrow above the tube.
     * You can override it.
     * @return all what you want (now true)
     */
    public boolean canHideArrow() {
        // method to override
        return true;
    }

///////////////////////////////////////////////////////////////////////////
//
//               * images *
//
///////////////////////////////////////////////////////////////////////////

    private void loadImages() {
        if (imgBottle == null) {
            imgBottle = core.Options.createBufImage("imgTube_bottle.png");
        }
        if (imgCork == null) {
            imgCork = core.Options.createBufImage("imgTube_cork.png");
        }
        if (imgShadeGreen == null) {
            imgShadeGreen = core.Options.createBufImage("imgTube_shade_green.png");
        }
        if (imgShadeYellow == null) {
            imgShadeYellow = core.Options.createBufImage("imgTube_shade_yellow.png");
        }
        if (imgShadeBlue == null) {
            imgShadeBlue = core.Options.createBufImage("imgTube_shade_blue.png");
        }
        if (imgShadeRed == null) {
            imgShadeRed = core.Options.createBufImage("imgTube_shade_red.png");
        }
        if (imgShadeGray == null) {
            imgShadeGray = core.Options.createBufImage("imgTube_shade_gray.png");
        }
        if (imgArrowYellow == null) {
            imgArrowYellow = core.Options.createBufImage("imgTube_arrow_in_yellow.png");
        }
        if (imgArrowGreen == null) {
            imgArrowGreen = core.Options.createBufImage("imgTube_arrow_out_green.png");
        }
    }

}
