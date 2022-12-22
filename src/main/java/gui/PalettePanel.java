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
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JComponent;
import static gui.MainFrame.pal;
import static run.Main.frame;
import java.awt.event.ActionEvent;

public class PalettePanel extends JComponent {

    private final ArrayList<ColorButton> colors = new ArrayList<>();
    private int spaceX = 15;
    private int spaceY = 12;
    private int rows;
    private int cols;
    private int docked = 0;

    private static final PaletteMenu menu = new PaletteMenu();

    public PalettePanel() {

        for (int i = 0; i < pal.size() - 1; i++) {
            ColorButton cb = new ColorButton(i);
            cb.setCount(4);
            colors.add(cb);
            this.add(cb);

            cb.addActionListener((ActionEvent e) -> clickButton(cb));
        }

        if (Options.palDockedTo >= 0
                && Options.palDockedTo < 4
                && Options.palLines > 0
                && Options.palLines < 4) {
            docked = Options.palDockedTo;
            switch (docked) {
                case 0: // top
                case 1: // bottom
                    rows = Options.palLines;
                    calculateColumns();
                    break;
                case 2: // left
                case 3: // right
                    cols = Options.palLines;
                    calculateRows();
                    break;
            }
        } else {
            rows = 2;
            calculateColumns();
        }

        calculateSize();
        updateButtonsPos();
    }

    public void addPopups() {
        addPopupMenu(this);
        for (int i = 0; i < pal.size() - 1; i++) {
            addPopupMenu(getButton(i));
        }
    }

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

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public void setRows(int newRows) {
        rows = newRows;
        calculateColumns();
        calculateSize();
        updateButtonsPos();
        reDock();
    }

    public void setColumns(int newCols) {
        cols = newCols;
        calculateRows();
        calculateSize();
        updateButtonsPos();
        reDock();
    }

    private void calculateColumns() {
        cols = colors.size() / rows;
        if (cols * rows < colors.size()) {
            cols++;
        }
    }

    private void calculateRows() {
        rows = colors.size() / cols;
        if (cols * rows < colors.size()) {
            rows++;
        }
    }

    private void setBtnPos(int number) {
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

    private void updateButtonsPos() {
        for (int i = 0; i < colors.size(); i++) {
            setBtnPos(i);
        }
    }

    public final void calculateSize() {
        setSize(spaceX + cols * (spaceX + colors.get(0).getWidth()),
                spaceY + rows * (spaceY + colors.get(0).getHeight()));
    }

    public int getDockedTo() {
        return docked;
    }

    public void setDockedTo(int number) {
        docked = number;
        if (docked < 2) {
            setRows(Math.min(rows, cols));
        } else {
            setColumns(Math.min(rows, cols));
        }
        reDock();
    }

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

    public void setSpaces(int spaceX, int spaceY) {
        this.spaceX = spaceX;
        this.spaceY = spaceY;
        reDock();
    }

    public ColorButton getButton(int number) {
        return colors.get(number);
    }

    public ColorButton getButtonByColor(int colorNum) {
        return colors.get(colorNum - 1);
    }

    public int getButtonColorNum(int number) {
        return colors.get(number).getColorNumber();
    }

    public Color getColor(int number) {
        return colors.get(number).getColor();
    }

    public void setDefaultPalette() {
        pal.defaultPalette();
        updateColors();
    }

    public void updateColors() {
        for (int i = 0; i < pal.size() - 1; i++) {
            getButton(i).repaintColor();
        }
    }

    public int getColorsCount() {
        return colors.size();
    }

    public void saveOptions() {
        switch (docked) {
            case 0: // top
            case 1: // bottom
                Options.palLines = rows;
                break;
            case 2: // left
            case 3: // right
                Options.palLines = cols;
                break;
        }
        Options.palDockedTo = docked;
    }

    public void changeColor(ColorButton cb) {
        cb.colorChange();
    }

    public void clickButton(ColorButton cb) {

    }

}
