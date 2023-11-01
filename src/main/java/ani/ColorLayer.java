/*
 * Copyright (c) 2022 legoru / goroleo <legoru@me.com>
 *
 * This software is distributed under the <b>MIT License.</b>
 * The full text of the License you can read here:
 * https://choosealicense.com/licenses/mit/
 *
 * Use this as you want! ))
 */
package ani;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static gui.MainFrame.palette;

/**
 * ColorLayer is a layer of ColorTube to draw animation of fill and erase color cells.
 *
 * @see gui.ColorTube
 */
public class ColorLayer extends JComponent implements Runnable {

// -----------------------------------------------------
//     Animation settings
//

    /**
     * How many lines will fill (or erase) in the every next frame.
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final int deltaY = 1;

    /**
     * Delay (in milliseconds) before start to calculate and draw next frame.
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final int delay = 5;

    /**
     * Enables and disables filling animation. If <b>true</b> then animation is enabled.
     */
    public Boolean useAnimation = true;

// -----------------------------------------------------
//     Variables
//

    /**
     * Transparent color used to clear color cells.
     */
    private final Color transparent = new Color(0, 0, 0, 0);

    /**
     * All painting tasks of the tube are put to stack and run serially. And this is the stack of these tasks.
     *
     * @see #addFillTask
     * @see #getFillTask
     */
    private final ArrayList<Integer> fillTasks = new ArrayList<>();

    /**
     * Image for every single frame.
     */
    private final BufferedImage imgFrame;

    /**
     * Count of filled color cells in the tube. From 0 to 4.
     */
    private int count = 0;

    /**
     * If <b>true</b> then the thread is working, and we don't need to start it again.
     */
    private boolean working = false;

    /**
     * Current color for the painting thread.
     */
    private Color paintColor;

    /**
     * Current painting rect for the thread. Only Y coordinate will change in this rect, depends on the number of the cell.
     *
     * @see #updateColorRect
     */
    private final Rectangle paintRect
            = new Rectangle(13, 0, 50, 35);

    /**
     * First line (Y coordinate) of the current painting.
     */
    private int startY;

    /**
     * End line (Y coordinate) of the current painting.
     */
    private int endY;

    /**
     * The coordinate when we have to stop paint the current frame.
     */
    private int frameY;

    /**
     * Painting direction: up (filling) and down (erasing).
     */
    private boolean down = false;


    /**
     * @param width  width of the parent image
     * @param height height of the parent image
     */
    public ColorLayer(int width, int height) {
        imgFrame = new BufferedImage(width, height, 2);
        for (int i = 0; i < 4; i++) {
            eraseColor(i + 1);
        }
        setSize(width, height);
    }

    /**
     * Starts a new animation thread if it doesn't work yet and if the tasks stack is not empty.
     */
    public void start() {
        if (!working && !fillTasks.isEmpty()) {
            working = true;
            Thread t = new Thread(this);
            t.start();
        }
    }

    /**
     * Erases all cells of the tube.
     */
    public void clearColors() {
        while (count > 0) {
            removeColor();
        }
    }

    /**
     * Adds one color to the color tube. The color will add into the next empty cell.
     *
     * @param colorNum Color number from Palette (0 is a transparent color)
     */
    public void addColor(int colorNum) {
        if (count < 4) {
            count++;
            if (useAnimation) {
                addFillTask(count, colorNum, true);
                start();
            } else {
                drawColor(count, colorNum);
                repaint();
            }
        }
    }

    /**
     * Erases one (top) color from the color tube.
     */
    public void removeColor() {
        if (count > 0) {
            if (useAnimation) {
                addFillTask(count, 0, false);
                start();
            } else {
                eraseColor(count);
                repaint();
            }
            count--;
        }
    }

    /**
     * Repaints one cell of the tube. Please note, this class doesn't know what color
     * the cell is filled with. So you must specify this color manually.
     *
     * @param cellIdx   cell index
     * @param colorNum  color number from palette (0 is a transparent color)
     * @param doRepaint true if the layer has to repaint its state immediately
     */
    public void repaintColor(int cellIdx, int colorNum, boolean doRepaint) {
        drawColor(cellIdx, colorNum);
        if (doRepaint) repaint();
    }

    /**
     * Updates Y coordinate of the cell specified by its number (index).
     *
     * @param idx index (number) of the cell, from 1 to 4
     * @see #paintRect
     */
    private void updateColorRect(int idx) {
        paintRect.y = 178 - (paintRect.height * (idx) + 1);
    }

    /**
     * Get color from Palette by its number.
     *
     * @param colorNum color number from Palette
     * @return color value
     * @see gui.Palette
     */
    private Color getPaintColor(int colorNum) {
        if (colorNum == 0) {
            return transparent;
        } else {
            return palette.getColor(colorNum);
        }
    }

    /**
     * Adds a new task to the painting stack.
     *
     * @param idx    index (number) of the cell, from 1 to 4.
     * @param clr    color number from Palette.
     * @param doFill true to fill the cell, false to erase.
     */
    private void addFillTask(int idx, int clr, boolean doFill) {
        int temp = (doFill ? (1 << 12) : 0)
                | ((idx & 0xf) << 8)
                | (clr & 0xff);
        fillTasks.add(temp);
    }

    /**
     * This routine reads the next paint task, sets initial values to color, coordinates,
     * etc. and then removes this task from the stack.
     *
     * @return true if the stack was not empty
     */
    private boolean getFillTask() {
        if (fillTasks.isEmpty()) {
            return false;
        }
        int temp = fillTasks.get(0);
        updateColorRect((temp >> 8) & 0xf);
        down = ((temp >> 12) & 0xf) == 0;
        if (down) {
            startY = paintRect.y;
            endY = paintRect.y + paintRect.height - 1;
            paintColor = transparent;
        } else {
            startY = paintRect.y + paintRect.height - 1;
            endY = paintRect.y;
            paintColor = getPaintColor(temp & 0xff);
        }
        fillTasks.remove(0);
        return true;
    }

    /**
     * Fills one cell without an animation.
     *
     * @param cellIdx  index of the cell
     * @param colorNum number of color from Palette
     */
    private void drawColor(int cellIdx, int colorNum) {
        updateColorRect(cellIdx);
        int clr = getPaintColor(colorNum).getRGB();
        for (int y = 0; y < paintRect.height; y++) {
            for (int x = 0; x < paintRect.width; x++) {
                imgFrame.setRGB(paintRect.x + x, paintRect.y + y, clr);
            }
        }
    }

    /**
     * Erases one cell without an animation.
     *
     * @param cellIdx index of the cell.
     */
    private void eraseColor(int cellIdx) {
        updateColorRect(cellIdx);
        int clr = transparent.getRGB();
        for (int y = 0; y < paintRect.height; y++) {
            for (int x = 0; x < paintRect.width; x++) {
                imgFrame.setRGB(paintRect.x + x, paintRect.y + y, clr);
            }
        }
    }

    /**
     * Draws the frame of animation using current values.
     */
    private void updateCurrentFrame() {
        int y = startY;
        int clr = paintColor.getRGB();
        boolean doDraw = true;
        while (doDraw) {
            for (int x = 0; x < paintRect.width; x++) {
                imgFrame.setRGB(paintRect.x + x, y, clr);
            }
            if (down) {
                y++;
                doDraw = (y <= frameY);
            } else {
                y--;
                doDraw = (y >= frameY);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(imgFrame, 0, 0, null);
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {

        // The thread will process every painting tasks,
        // including those tasks which will added during the work time.
        while (getFillTask()) {

            // initial value for the first frame
            frameY = startY;

            while (frameY != endY) {

                // start value for every frame
                startY = frameY;

                // stop value for every frame
                if (down) {
                    frameY = startY + deltaY;
                    if (frameY > endY) {
                        frameY = endY;
                    }
                } else {
                    frameY = startY - deltaY;
                    if (frameY < endY) {
                        frameY = endY;
                    }
                }

                // draw frame image
                updateCurrentFrame();

                // repaint component
                repaint();

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    // do nothing
                }
            }
        }
        working = false;
    }

}
