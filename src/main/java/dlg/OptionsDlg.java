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
import gui.MainFrame;
import gui.Palette;
import lib.lButtons.LPictureButton;
import run.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The dialog box to displaying and changing application settings.
 */
public class OptionsDlg extends JDialog {

    private final JFrame parent;
    private final JPanel languagePan = newPanel();
    private final JPanel savePan = newPanel();
    private final int dimY = 40;
    private final int dimX = 40;
    private int w = 400; // width
    private int oldLangNum;
    private final JCheckBox cbSaveAfterFill = newCheckBox();
    private final JCheckBox cbSaveAfterSolve = newCheckBox();
    private final JCheckBox cbSaveBeforeClose = newCheckBox();

    private final LPictureButton btnOk;
    private final LPictureButton btnCancel;

    /**
     * Creates the Options dialog.
     *
     * @param owner parent frame
     */
    public OptionsDlg(JFrame owner) {
        super(owner, ResStrings.getString("strOptions"), true);
        this.parent = owner;
        getContentPane().setBackground(Palette.dialogColor);
        getContentPane().setForeground(Color.white);
        getContentPane().setLayout(null);
        setResizable(false);

        // add languages
        for (int i = 0; i < ResStrings.getLangsCount(); i++) {
            JCheckBox cb = newCheckBox();
            cb.setText(ResStrings.getLangName(i));
            int langNum = i;
            cb.addActionListener((ActionEvent e) -> setLangNum(langNum));
            languagePan.add(cb);
        }
        getContentPane().add(languagePan);

        // add saving options
        cbSaveAfterFill.setLocation(dimX, dimY);
        savePan.add(cbSaveAfterFill);
        cbSaveAfterSolve.setLocation(dimX, dimY + 30);
        savePan.add(cbSaveAfterSolve);
        cbSaveBeforeClose.setLocation(dimX, dimY + 60);
        savePan.add(cbSaveBeforeClose);
        getContentPane().add(savePan);

        btnOk = new LPictureButton(this, "btnDialog");
        btnOk.setText(ResStrings.getString("strOk"));
        btnOk.setBackground(null);
        btnOk.setForeground(null);
        btnOk.setFocusable(true);
        btnOk.addActionListener((ActionEvent e) -> confirmAndClose());
        add(btnOk);

        btnCancel = new LPictureButton(this, "btnDialog");
        btnCancel.setText(ResStrings.getString("strCancel"));
        btnCancel.setBackground(null);
        btnCancel.setForeground(null);
        btnCancel.setFocusable(true);
        btnCancel.addActionListener((ActionEvent e) -> refuseAndClose());
        add(btnCancel);

        // listeners
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                refuseAndClose();
            }
        });

        // ESCAPE pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> refuseAndClose(),
                KeyStroke.getKeyStroke(0x1B, 0), // VK_ESCAPE
                JComponent.WHEN_IN_FOCUSED_WINDOW); // WHEN_IN_FOCUSED_WINDOW

        // CTRL+ENTER pressed
        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> confirmAndClose(),
                KeyStroke.getKeyStroke('\n', InputEvent.CTRL_DOWN_MASK), // VK_ENTER + MASK_CTRL
                JComponent.WHEN_IN_FOCUSED_WINDOW); // WHEN_IN_FOCUSED_WINDOW
    }

    /**
     * Calls when Escape / Cancel pressed.
     */
    private void refuseAndClose() {
        setLangNum(oldLangNum);
        saveOptions();
        Main.frame.setGameMode(MainFrame.prevMode);
        EventQueue.invokeLater(this::dispose);
    }

    /**
     * Calls when OK button pressed.
     */
    private void confirmAndClose() {
        Options.saveGameAfterFill = cbSaveAfterFill.isSelected();
        Options.saveGameAfterSolve = cbSaveAfterSolve.isSelected();
        Options.saveGameBeforeClose = cbSaveBeforeClose.isSelected();
        saveOptions();
        Main.frame.setGameMode(MainFrame.prevMode);
        EventQueue.invokeLater(this::dispose);
    }

    /**
     * Resize dialog window due to language change.
     */
    private void resize() {
        cbSaveAfterFill.setSize(w - dimX * 2, 24);
        cbSaveAfterSolve.setSize(w - dimX * 2, 24);
        cbSaveBeforeClose.setSize(w - dimX * 2, 24);

        int rows = ResStrings.getLangsCount() / 2;
        if (ResStrings.getLangsCount() > rows * 2) rows++;
        languagePan.setSize(w, dimY + rows * 30);
        languagePan.setLocation(0, 0);
        savePan.setSize(w, dimY + 3 * 30 + 15);
        savePan.setLocation(0, languagePan.getHeight());

        for (int i = 0; i < languagePan.getComponentCount(); i++) {
            JCheckBox cb = (JCheckBox) languagePan.getComponent(i);
            cb.setLocation((i % 2) == 0 ? dimX : w / 2, dimY + (i / 2) * 30);
            cb.setSize(w / 2 - dimX, 24);
        }

        btnCancel.setLocation(
                w - btnCancel.getWidth() - dimX,
                languagePan.getHeight() + savePan.getHeight());
        btnOk.setLocation(
                btnCancel.getLocation().x - btnOk.getWidth() - 15,
                btnCancel.getLocation().y);

        Dimension dim = new Dimension();
        dim.width = w;
        dim.height = languagePan.getHeight() + savePan.getHeight() + dimY / 2 + btnCancel.getHeight();
        setPreferredSize(dim);
        pack();

        int dx = getWidth() - getContentPane().getWidth();
        int dy = getHeight() - getContentPane().getHeight();
        dim.width += dx;
        dim.height += dy;
        pack();
        setPreferredSize(dim);
        setSize(dim);
    }

    private void updatePos() {
        Rectangle r = getGraphicsConfiguration().getBounds();
        if (Options.odPositionX >= 0 && Options.odPositionY >= 0
                && Options.odPositionX + getWidth() <= r.width
                && Options.odPositionY + getHeight() <= r.height) {
            setLocation(Options.odPositionX, Options.odPositionY);
        } else if (parent != null) {
            setLocationRelativeTo(parent);
        } else {
            r.x = r.x + (r.width - getWidth()) / 2;
            r.y = r.y + (r.height - getHeight()) / 2;
            setLocation(r.x, r.y);
        }
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            oldLangNum = ResStrings.getLangNumber(Options.langCode);
            setLangNum(oldLangNum);
            cbSaveAfterFill.setSelected(Options.saveGameAfterFill);
            cbSaveAfterSolve.setSelected(Options.saveGameAfterSolve);
            cbSaveBeforeClose.setSelected(Options.saveGameBeforeClose);
            Main.frame.setGameMode(MainFrame.BUSY_MODE);
            updatePos();
        }
        super.setVisible(b);
    }

    /**
     * Creates and sets up the new checkbox.
     *
     * @return checkbox created
     */
    private JCheckBox newCheckBox() {
        JCheckBox cb = new JCheckBox();
        cb.setBackground(null);
        cb.setForeground(null);
        cb.setFont(cb.getFont().deriveFont(Font.PLAIN));
        cb.setIcon(Options.cbIconStandard);
        cb.setSelectedIcon(Options.cbIconSelected);
        cb.setBorderPainted(false);
        cb.setFocusPainted(false);
        return cb;
    }

    /**
     * Creates and sets up the new panel.
     *
     * @return panel created
     */
    private JPanel newPanel() {
        JPanel pan = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                drawPanelCaption(g, getName());
            }

            @Override
            public void setName(String s) {
                super.setName(s);
                repaint();
            }
        };

        pan.setBackground(null);
        pan.setForeground(null);
        pan.setLayout(null);
        return pan;
    }

    /**
     * Handles the language checkbox click / change.
     *
     * @param num number of the checkbox.
     */
    private void setLangNum(int num) {
        for (int i = 0; i < languagePan.getComponentCount(); i++) {
            if (languagePan.getComponent(i) instanceof JCheckBox) {
                ((JCheckBox) languagePan.getComponent(i)).setSelected(i == num);
            }
        }
        Options.langCode = ResStrings.getLangCode(num);
        ResStrings.setBundle(Options.langCode);
        Main.frame.updateLanguage();

        setTitle(ResStrings.getString("strOptions"));
        btnOk.setText(ResStrings.getString("strOk"));
        btnCancel.setText(ResStrings.getString("strCancel"));

        languagePan.setName(ResStrings.getString("strLanguage"));
        savePan.setName(ResStrings.getString("strSaveOptions"));

        cbSaveAfterFill.setText(ResStrings.getString("strSaveAfterFill"));
        int maxCheckBoxWidth = cbSaveAfterFill.getPreferredSize().width;
        cbSaveAfterSolve.setText(ResStrings.getString("strSaveAfterSolve"));
        if (cbSaveAfterSolve.getPreferredSize().width > maxCheckBoxWidth)
            maxCheckBoxWidth = cbSaveAfterSolve.getPreferredSize().width;
        cbSaveBeforeClose.setText(ResStrings.getString("strSaveBeforeClose"));
        if (cbSaveBeforeClose.getPreferredSize().width > maxCheckBoxWidth)
            maxCheckBoxWidth = cbSaveBeforeClose.getPreferredSize().width;
        w = Math.max(w, maxCheckBoxWidth + dimX * 2);

        resize();
    }

    /**
     * Draws the caption of the panel.
     *
     * @param g panel's graphics instance to draw.
     * @param s panel caption.
     */
    private void drawPanelCaption(Graphics g, String s) {
        g.setColor(Palette.dialogColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        Font f = g.getFont();
        FontMetrics fm = g.getFontMetrics(f);

        // text height
        int th = (int) (72.0 * f.getSize() / Toolkit.getDefaultToolkit().getScreenResolution());
        // text width
        int tw = (s != null) ? fm.stringWidth(s) : 0;

        if (s != null) {
            g.setColor(Color.white);
            g.drawString(s, dimX / 2, 16 + th);
        }
        g.setColor(Color.darkGray);
        g.drawLine(dimX + tw, 16 + th / 2, w - dimX / 2, 16 + th / 2);
    }

    /**
     * Saves this dialog position.
     */
    private void saveOptions() {
        Options.odPositionX = getX();
        Options.odPositionY = getY();
    }

}