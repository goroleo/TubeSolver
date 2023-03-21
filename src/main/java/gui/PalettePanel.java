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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static gui.MainFrame.pal;
import static run.Main.frame;

/**
 * A Panel consists of color buttons, one button per one palette color. So the count of Color Buttons is the
 * count of the palette colors. The main actions of the buttons are to choose a color to put to the tube
 * and to change the color of the palette.
 *
 * @see ColorButton
 */
public class PalettePanel extends JComponent {

///////////////////////////////////////////////////////////////////////////
//
//               * Fields / variables *
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Color Buttons array.
     */
    private final ArrayList<ColorButton> colors = new ArrayList<>();

    /**
     * The space between buttons by horizontal direction.
     */
    private int spaceX = 15;

    /**
     * The space between buttons by vertical direction.
     */
    private int spaceY = 12;

    /**
     * Number of rows to display color buttons.
     */
    private int rows;

    /**
     * Number of columns to display color buttons.
     */
    private int cols;

    /**
     * Which edge of the client area the panel will be docked to: <ul>
     * <li>0 - top;
     * <li>1 - bottom;
     * <li>2 - left;
     * <li>3 - right.</ul>
     */
    private int docked = 0;

    /**
     * The popup menu with Color Buttons actions.
     */
    private static final PaletteMenu menu = new PaletteMenu();

///////////////////////////////////////////////////////////////////////////
//
//               * Routines *
//
///////////////////////////////////////////////////////////////////////////

    /**
     * Constructor of the Palette Panel. It creates all color buttons and positioned itself to the
     * proper place of the Main frame.
     */
    public PalettePanel() {

        for (int i = 0; i < pal.size() - 1; i++) {
            ColorButton cb = new ColorButton(i);
            cb.setCount(4);
            cb.addActionListener((ActionEvent e) -> clickButton(cb));
            colors.add(cb);
            this.add(cb);
        }

        if (Options.palDockedTo >= 0
                && Options.palDockedTo < 4
                && Options.palLines > 0
                && Options.palLines < 4) {
            docked = Options.palDockedTo;
            if (docked < 2) { // horizontal
                setRows(Options.palLines);
            } else { // vertical
                setColumns(Options.palLines);
            }
        } else {
            setRows(2);
        }
    }

    /**
     * The routine to add Popup Menu for the Panel and all Color Buttons. We don't need popups at the Palette
     * Dialog, so adding popups is the separate routine.
     */
    public void addPopups() {
        addPopupMenu(this);
        for (ColorButton cb : colors) {
            addPopupMenu(cb);
        }
    }

    /**
     * The routine to add Palette Popup to the specified component.
     *
     * @param comp a component to add the popup menu.
     */
    public void addPopupMenu(JComponent comp) {
        comp.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    menu.show(e.getComponent(),
                            e.getX(), e.getY());
                }
            }
        });
    }

    /**
     * @return number of rows of the palette buttons.
     */
    public int getRows() {
        return rows;
    }

    /**
     * @return number of columns of the palette buttons.
     */
    public int getCols() {
        return cols;
    }

    /**
     * Set palette buttons rows.
     *
     * @param newRows new rows value
     */
    public void setRows(int newRows) {
        rows = newRows;

        // calculating columns
        cols = colors.size() / rows;
        if (cols * rows < colors.size()) {
            cols++;
        }

        updateSize();
        updateAllButtonsPos();
        reDock();
    }

    /**
     * Set palette buttons columns.
     *
     * @param newCols new columns value
     */
    public void setColumns(int newCols) {
        cols = newCols;

        // calculating rows
        rows = colors.size() / cols;
        if (cols * rows < colors.size()) {
            rows++;
        }

        updateSize();
        updateAllButtonsPos();
        reDock();
    }

    /**
     * Set location of the specified button used current values of rows and columns.
     *
     * @param number button's number
     */
    private void locateButton(int number) {
        int col = number % cols;
        int row = number / cols;
        int x = spaceX + col * (getButton(number).getWidth() + spaceX);
        int y = spaceY + row * (getButton(number).getHeight() + spaceY);
        if (row == rows - 1) {
            int lastRowCols = colors.size() - (cols * (rows - 1));
            if (lastRowCols != cols) {
                x = x + (cols - lastRowCols) * (getButton(number).getWidth() + spaceX) / 2;
            }
        }
        getButton(number).setLocation(x, y);
    }

    /**
     * Update all buttons' location.
     */
    private void updateAllButtonsPos() {
        for (int i = 0; i < colors.size(); i++) {
            locateButton(i);
        }
    }

    /**
     * Calculates and sets the component size. The size depends on current value of rows and columns.
     */
    public final void updateSize() {
        setSize(spaceX + cols * (spaceX + colors.get(0).getWidth()),
                spaceY + rows * (spaceY + colors.get(0).getHeight()));
    }

    /**
     * Gets the edge of the MainFrame the panel is docked to: <ul>
     * <li>0 - top;
     * <li>1 - bottom;
     * <li>2 - left;
     * <li>3 - right.</ul>
     *
     * @return current dockedTo value
     * @see #docked
     */
    public int getDockedTo() {
        return docked;
    }

    /**
     * Sets the edge of the MainFrame the toolbar is docked to.
     *
     * @param newDocked the new docked value.
     * @see #docked
     */
    public void setDockedTo(int newDocked) {
        docked = newDocked;
        if (docked < 2) {
            setRows(Math.min(rows, cols));
        } else {
            setColumns(Math.min(rows, cols));
        }
        reDock();
    }

    /**
     * Relocates the panel and then relocates the tubes panel too.
     */
    public void reDock() {
        Rectangle r = frame.getColorsArea();
        switch (docked) {
            case 0: // top
                r.x += (r.width - getWidth()) / 2;
                break;
            case 1: // bottom
                r.x += (r.width - getWidth()) / 2;
                r.y += r.height - getHeight();
                break;
            case 2: // left
                r.y += (r.height - getHeight()) / 2;
                break;
            case 3: // right
                r.x += r.width - getWidth();
                r.y += (r.height - getHeight()) / 2;
                break;
        }
        setLocation(r.x, r.y);
        frame.redockTubes();
    }

    /**
     * Sets new spaces values between color buttons.
     *
     * @param spaceX horizontal space
     * @param spaceY vertical space
     */
    public void setSpaces(int spaceX, int spaceY) {
        this.spaceX = spaceX;
        this.spaceY = spaceY;
        updateSize();
        updateAllButtonsPos();
        reDock();
    }

    /**
     * Gets the button by its number.
     *
     * @param number button number
     * @return Color button
     */
    public ColorButton getButton(int number) {
        return colors.get(number);
    }

    /**
     * Gets the button by color number at palette.
     *
     * @param colorNum number of color
     * @return Color button
     */
    public ColorButton getButtonByColor(int colorNum) {
        return colors.get(colorNum - 1);
    }

    /**
     * Gets the palette's color number from the specified button
     *
     * @param number button number
     * @return number of color at the palette
     */
    public int getButtonColorNum(int number) {
        return colors.get(number).getColorNumber();
    }

    /**
     * Gets the color of the specified button.
     *
     * @param number button number
     * @return Color
     */
    public Color getColor(int number) {
        return colors.get(number).getColor();
    }

    /**
     * Sets the default palette colors to all color buttons
     */
    public void setDefaultPalette() {
        pal.setDefaultPalette();
        updateColors();
    }

    /**
     * This routine updates and repaints all the color buttons
     */
    public void updateColors() {
        for (int i = 0; i < pal.size() - 1; i++) {
            getButton(i).repaintColor();
        }
    }

    /**
     * @return the count of color buttons.
     */
    public int getColorsCount() {
        return colors.size();
    }

    /**
     * Saves this panel options, docked and number of lines.
     */
    public void saveOptions() {
        if (docked < 2) { // horizontal
            Options.palLines = rows;
        } else { // vertical
            Options.palLines = cols;
        }
        Options.palDockedTo = docked;
    }

    /**
     * Calls the color picked dialog of the specified Color Button.
     *
     * @param cb specified color button
     */
    public void changeColor(ColorButton cb) {
        cb.changeColor();
    }

    /**
     * Handles the mouse click or Enter/Space press on the specified button.
     *
     * @param cb specified color button
     */
    public void clickButton(ColorButton cb) {
        // the routine to override it
    }

}
