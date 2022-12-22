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
import gui.ColorButton;
import gui.MainFrame;
import static gui.MainFrame.pal;
import gui.Palette;
import gui.PalettePanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import core.ResStrings;
import lib.lButtons.LPictureButton;
import lib.lColorDialog.LColorDialog;
import run.Main;

public class PaletteDlg extends JDialog {

    private final JFrame parent;
//    private final int sY = 15;
    private final int sX = 15;

    private int btnHeight;
    private int btnWidth;

    private final JPanel panel;
    private final PalettePanel palPan;
    public int modalResult = 3;

    private ArrayList<Color> oldPalette;
    private final JCheckBox cbShowChanges;

    public PaletteDlg(JFrame owner) {
        super(owner, ResStrings.getString("strPalette"), true);
        saveOldPalette();

        this.parent = owner;
        getContentPane().setBackground(Palette.dialogColor);
        getContentPane().setForeground(Color.white);
        getContentPane().setLayout(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                btnClick(3);
            }
        });

        // ESCAPE pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> btnClick(0),
                KeyStroke.getKeyStroke(0x1B, 0), // VK_ESCAPE
                2); // WHEN_IN_FOCUSED_WINDOW

        // CTRL+ENTER pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> btnClick(1),
                KeyStroke.getKeyStroke('\n', 2), // VK_ENTER + MASK_CTRL
                2); // WHEN_IN_FOCUSED_WINDOW

        palPan = new PalettePanel() {
            @Override
            public void clickButton(ColorButton cb) {
                PaletteDlg.this.changeColor(cb);
            }

            @Override
            public void reDock() {
                super.reDock();
                setLocation(0, 0);
            }
        };

        for (int i = 0; i < pal.size() - 1; i++) {
            palPan.getButton(i).setCount(0);
        }
        palPan.setSpaces(5, 5);
        palPan.setRows(3);
        palPan.setLocation(10, 10);

        panel = new JPanel();
        panel.setBackground(null);
        panel.setForeground(null);
        panel.setLayout(null);
        panel.add(palPan);

        cbShowChanges = new JCheckBox();
        cbShowChanges.setBackground(null);
        cbShowChanges.setForeground(null);
        cbShowChanges.setText(ResStrings.getString("strShowChanges"));
        cbShowChanges.setFont(cbShowChanges.getFont().deriveFont(0));
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

        panel.add(cbShowChanges);

        addButton(1, ResStrings.getString("strApply"), 17);
        addButton(0, ResStrings.getString("strCancel"), 17 + 10 + btnHeight);
        addButton(4, ResStrings.getString("strStandard"), palPan.getHeight() - btnHeight);

        panel.setSize(palPan.getWidth() + btnWidth + sX * 2 + 20,
                palPan.getHeight() + 20 + 10 + 24);

        getContentPane().add(panel);

        calculateSize();
        calculatePos();
    }

    private LPictureButton addButton(int number, String aCaption, int cooY) {
        LPictureButton btn = new LPictureButton(this, "btnDialog");
        btn.setText(aCaption);
        btn.setBackground(null);
        btn.setForeground(null);
        btn.setFocusable(true);
        btn.setLocation(palPan.getWidth() + 30, cooY);
        btn.addActionListener((ActionEvent e) -> btnClick(number));
        btnHeight = btn.getHeight();
        btnWidth = btn.getWidth();

        panel.add(btn);
        return btn;
    }

    private void calculateSize() {
        setResizable(true);
        Dimension dim = new Dimension();
        dim.width = panel.getWidth();
        dim.height = panel.getHeight();
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

    private void btnClick(int btnNum) {
        modalResult = btnNum;
        switch (btnNum) {
            case -1: // pressed 'close window' button
            case 0: // pressed 'cancel' button
                restoreOldPalette();
                saveOptions();
                EventQueue.invokeLater(this::dispose);
                break;
            case 1: // pressed 'Apply / OK' button
                pal.savePalette();
                saveOptions();
                EventQueue.invokeLater(this::dispose);
                break;
            case 4: // pressed 'default palelle' button
                pal.defaultPalette();
                for (int i = 0; i < pal.size() - 1; i++) {
                    palPan.getButton(i).repaintColor();
                }
                if (cbShowChanges.isSelected()) {
                    updateColors();
                }
                break;
        }
    }

    private void saveOldPalette() {
        oldPalette = new ArrayList<>();
        for (int i = 1; i < pal.size(); i++) {
            oldPalette.add(pal.get(i));
        }
    }

    private void restoreOldPalette() {
        for (int i = 1; i < pal.size(); i++) {
            pal.set(i, oldPalette.get(i - 1));
        }
        updateColors();
    }

    private void updateColor(int colorNum) {
        if (MainFrame.palPan != null) {
            MainFrame.palPan.getButtonByColor(colorNum).repaintColor();
        }
        if (MainFrame.tubesPan != null) {
            MainFrame.tubesPan.updateColor(colorNum);
        }
    }

    private void updateColors() {
        if (MainFrame.palPan != null) {
            MainFrame.palPan.updateColors();
        }
        if (MainFrame.tubesPan != null) {
            MainFrame.tubesPan.updateColors();
        }
    }

    private void changeColor(ColorButton cb) {
        Color oldColor = pal.getColor(cb.getColorNumber());

        LColorDialog lcd = new LColorDialog(Main.frame, oldColor);
        lcd.setBackground(Palette.dialogColor);
        lcd.addColorListener((int rgb) -> {

            pal.set(cb.getColorNumber(), new Color(rgb));
            cb.repaintColor();
            if (cbShowChanges.isSelected()) {
                updateColor(cb.getColorNumber());
            }
        });

        if (Options.ccdPositionX != -1 && Options.ccdPositionY != -1) {
            lcd.setLocation(Options.ccdPositionX, Options.ccdPositionY);
        }
        Color newColor = lcd.chooseColor();

        Point pos = lcd.getLocation();
        Options.ccdPositionX = pos.x;
        Options.ccdPositionY = pos.y;

        pal.set(cb.getColorNumber(), newColor);
        cb.repaintColor();
    }

    private void saveOptions() {
        Options.pdPositionX = getX();
        Options.pdPositionY = getY();
        Options.pdShowChanges = cbShowChanges.isSelected();
    }

}
