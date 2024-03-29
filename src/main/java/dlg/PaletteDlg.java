/*
 * Copyright (c) 2022 legoru / goroleo <legoru@me.com>
 *
 * This software is distributed under the <b>MIT License.</b>
 * The full text of the License you can read here:
 * https://choosealicense.com/licenses/mit/
 *
 * Use this as you want! ))
 */
package dlg;

import core.Options;
import core.ResStrings;
import gui.ColorButton;
import gui.MainFrame;
import gui.Palette;
import gui.PalettePanel;
import lib.lButtons.LPictureButton;
import lib.lColorDialog.LColorDialog;
import run.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static gui.MainFrame.palette;

/**
 * Dialog to change palette colors.
 */
public class PaletteDlg extends JDialog {

    /**
     * Palette panel with color buttons
     */
    private final PalettePanel palPan;

    /**
     * The parent frame to locate this dialog relative to parent.
     */
    private final JFrame parent;

    /**
     * A CheckBox "Show colors change".
     */
    private final JCheckBox cbShowChanges;

    /**
     * Width of control buttons used for calculating dialog size.
     */
    private int btnWidth;

    /**
     * Height of control buttons, used for calculating dialog size.
     */
    private int btnHeight;

    /**
     * Old palette colors, to restore the palette if the Cancel button will be pressed/clicked.
     */
    private static final Color[] oldPalette = new Color[palette.size() - 1];

    /**
     * The constructor. Creating a frame of Dialog and adding its components.
     *
     * @param owner the parent frame to center the dialog.
     */
    public PaletteDlg(JFrame owner) {
        super(owner, ResStrings.getString("strPalette"), true);

        this.parent = owner;
        getContentPane().setBackground(Palette.dialogColor);
        getContentPane().setForeground(Color.white);
        getContentPane().setLayout(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                clickButton(-1);
            }
        });

        // ESCAPE pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> clickButton(0),
                KeyStroke.getKeyStroke(0x1B, 0), // VK_ESCAPE
                JComponent.WHEN_IN_FOCUSED_WINDOW); // WHEN_IN_FOCUSED_WINDOW

        // CTRL+ENTER pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> clickButton(1),
                KeyStroke.getKeyStroke('\n', InputEvent.CTRL_DOWN_MASK), // VK_ENTER + MASK_CTRL
                JComponent.WHEN_IN_FOCUSED_WINDOW); // WHEN_IN_FOCUSED_WINDOW

        // adding Palette panel
        palPan = new PalettePanel() {
            @Override
            public void clickButton(ColorButton cb) {
                PaletteDlg.this.changeColor(cb);
            }

            @Override
            public void reDock() {
                // do nothing
            }
        };

        for (int i = 0; i < palette.size() - 1; i++) {
            palPan.getButton(i).setCount(0);
        }
        palPan.setSpaces(5, 5);
        palPan.setRows(3);
        palPan.setLocation(10, 10);
        getContentPane().add(palPan);

        // adding CheckBox "Show changes"
        cbShowChanges = new JCheckBox();
        cbShowChanges.setBackground(null);
        cbShowChanges.setForeground(null);
        cbShowChanges.setText(ResStrings.getString("strShowChanges"));
        cbShowChanges.setFont(cbShowChanges.getFont().deriveFont(Font.PLAIN));
        cbShowChanges.setIcon(Options.cbIconStandard);
        cbShowChanges.setSelectedIcon(Options.cbIconSelected);
        cbShowChanges.setBorderPainted(false);
        cbShowChanges.setFocusPainted(false);
        cbShowChanges.setSelected(Options.pdShowChanges);
        cbShowChanges.setLocation(17, 10 + palPan.getHeight());
        cbShowChanges.setSize(palPan.getWidth(), 24);
        cbShowChanges.addActionListener((ActionEvent e) -> {
            if (cbShowChanges.isSelected()) {
                updateColors();
            }
        });
        getContentPane().add(cbShowChanges);

        // add control buttons: Apply, Cancel and Set default palette
        addButton(1, ResStrings.getString("strApply"), 17);
        addButton(0, ResStrings.getString("strCancel"), 27 + btnHeight);
        addButton(4, ResStrings.getString("strStandard"), palPan.getHeight() - btnHeight);

        calculateSize();
        calculatePos();
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            storeOldPalette();
            Main.frame.setGameMode(MainFrame.BUSY_MODE);
        }
        super.setVisible(b);
    }

    /**
     * Adds a control button to the dialog.
     *
     * @param number   number of the button that means the dialog modal result.
     * @param aCaption caption (text) on the button.
     * @param cooY     the Y coordinate.
     */
    private void addButton(int number, String aCaption, int cooY) {
        LPictureButton btn = new LPictureButton(this, "btnDialog");
        btn.setText(aCaption);
        btn.setBackground(null);
        btn.setForeground(null);
        btn.setFocusable(true);
        btn.setLocation(palPan.getWidth() + 30, cooY);
        btn.addActionListener((ActionEvent e) -> clickButton(number));
        btnHeight = btn.getHeight();
        btnWidth = btn.getWidth();

        getContentPane().add(btn);
    }

    /**
     * Calculates and sets the dialog size.
     */
    private void calculateSize() {
        setResizable(true);
        Dimension dim = new Dimension();
        dim.width = palPan.getWidth() + btnWidth + 50;
        dim.height = palPan.getHeight() + 54;
        setPreferredSize(dim);
        pack();

        int dx = (getWidth() - getContentPane().getWidth());
        int dy = (getHeight() - getContentPane().getHeight());
        dim.width += dx;
        dim.height += dy;
        setPreferredSize(dim);
        pack();
        setResizable(false);
    }

    /**
     * Calculates and sets the dialog location.
     */
    private void calculatePos() {
        Rectangle r = getGraphicsConfiguration().getBounds();
        if (Options.pdPositionX >= 0 && Options.pdPositionY >= 0
                && Options.pdPositionX + getWidth() <= r.width
                && Options.pdPositionY + getHeight() <= r.height) {
            setLocation(Options.pdPositionX, Options.pdPositionY);
        } else if (parent != null) {
            setLocationRelativeTo(parent);
        } else {
            r.x = r.x + (r.width - getWidth()) / 2;
            r.y = r.y + (r.height - getHeight()) / 2;
            setLocation(r.x, r.y);
        }
    }

    /**
     * Handles the control button click.
     *
     * @param btnNum number of the button clicked
     */
    private void clickButton(int btnNum) {
        switch (btnNum) {
            case -1: // pressed 'close window' button
            case 0: // pressed 'cancel' button
                restoreOldPalette();
                saveOptions();
                Main.frame.setGameMode(MainFrame.prevMode);
                EventQueue.invokeLater(this::dispose);
                break;
            case 1: // pressed 'Apply / OK' button
                palette.savePalette();
                saveOptions();
                Main.frame.setGameMode(MainFrame.prevMode);
                EventQueue.invokeLater(this::dispose);
                break;
            case 4: // pressed 'default palette' button
                palette.setDefaultPalette();
                for (int i = 0; i < palette.size() - 1; i++) {
                    palPan.getButton(i).repaintColor();
                }
                if (cbShowChanges.isSelected()) {
                    updateColors();
                }
                break;
        }
    }

    /**
     * Saves palette colors before show this dialog to make a possibility to restore them.
     */
    private void storeOldPalette() {
        for (int i = 0; i < palette.size() - 1; i++) {
            oldPalette[i] = palette.get(i + 1);
        }
    }

    /**
     * Restores palette colors if Cancel button has been clicked.
     */
    private void restoreOldPalette() {
        for (int i = 0; i < palette.size() - 1; i++) {
            palette.set(i + 1, oldPalette[i]);
        }
        updateColors();
    }

    /**
     * Repaints one color on all panels of the MainFrame.
     *
     * @param colorNum A number of the Color in the Palette.
     */
    private void updateColor(int colorNum) {
        if (MainFrame.palettePanel != null) {
            MainFrame.palettePanel.getButtonByColor(colorNum).repaintColor();
        }
        if (MainFrame.tubesPanel != null) {
            MainFrame.tubesPanel.updateColor(colorNum);
        }
    }

    /**
     * Repaints all colors on all panels of the MainFrame.
     */
    private void updateColors() {
        if (MainFrame.palettePanel != null) {
            MainFrame.palettePanel.updateColors();
        }
        if (MainFrame.tubesPanel != null) {
            MainFrame.tubesPanel.updateColors();
        }
    }

    /**
     * Change the color by clicked on Color Button.
     *
     * @param cb Specified Color Button
     */
    private void changeColor(ColorButton cb) {

        LColorDialog lcd = new LColorDialog(Main.frame, palette.getColor(cb.getColorNumber()));
        lcd.setBackground(Palette.dialogColor);

        lcd.addColorListener(() -> {
            palette.set(cb.getColorNumber(), lcd.getColor());
            cb.repaintColor();
            if (cbShowChanges.isSelected()) {
                updateColor(cb.getColorNumber());
            }
        });

        if (Options.ccdPositionX != -1 && Options.ccdPositionY != -1) {
            lcd.setLocation(Options.ccdPositionX, Options.ccdPositionY);
        }
        if (Options.ccdDialogMode < 0 || Options.ccdDialogMode > 5) {
            Options.ccdDialogMode = 0;
        }
        lcd.setDialogMode(Options.ccdDialogMode);

        Color newColor = lcd.chooseColor();

        // save options
        Options.ccdPositionX = lcd.getX();
        Options.ccdPositionY = lcd.getY();
        Options.ccdDialogMode = lcd.getDialogMode();

        palette.set(cb.getColorNumber(), newColor);
        cb.repaintColor();
    }

    /**
     * Saves dialog options: X position, Y position, CheckBox state.
     */
    private void saveOptions() {
        Options.pdPositionX = getX();
        Options.pdPositionY = getY();
        Options.pdShowChanges = cbShowChanges.isSelected();
    }

}
