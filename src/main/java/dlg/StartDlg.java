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
import core.TubesIO;
import gui.MainFrame;
import gui.Palette;
import run.Main;

import lib.lButtons.LPictureButton;
import lib.lOpenSaveDialog.LOpenSaveDialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.event.KeyEvent;

/**
 * The Start Game dialog. Chooses the game mode.
 */
public class StartDlg extends JDialog {

    private final JFrame parent;

    private LPictureButton btnResume;

    final int startX = 40;
    final int sizeX = 90;
    final int spaceX = 20;
    final int btnY = 40;

    private int loadedMode;

    /**
     * Creates the Start Game dialog.
     *
     * @param owner the parent frame.
     */
    public StartDlg(JFrame owner) {
        super(owner, ResStrings.getString("strStartGame"), true);
        this.parent = owner;
        setResizable(false);
        getContentPane().setBackground(Palette.dialogColor);
        getContentPane().setForeground(Color.white);
        setLayout(null);

        calculateSize();
        calculatePos();
        addButtons();
        addLabels();

        if (TubesIO.fileExists(TubesIO.tempFileName)) {
            Main.frame.loadGame(TubesIO.tempFileName);
            btnResume.setEnabled(true);
            loadedMode = MainFrame.gameMode;
        } else {
            btnResume.setEnabled(false);
        }

        Main.frame.setGameMode(MainFrame.BUSY_MODE);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeFrame();
            }
        });

        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> closeFrame(),
                KeyStroke.getKeyStroke(0x1B, 0), // VK_ESCAPE
                2); // WHEN_IN_FOCUSED_WINDOW

        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> buttonClick(0),
                KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), // Button 0 - F5
                2); // WHEN_IN_FOCUSED_WINDOW

        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> buttonClick(1),
                KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), // Button 1 - F6
                2); // WHEN_IN_FOCUSED_WINDOW

        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> buttonClick(2),
                KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), // Button 2 - F7
                2); // WHEN_IN_FOCUSED_WINDOW

        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> buttonClick(3),
                KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0), // Button 3 - F8
                2); // WHEN_IN_FOCUSED_WINDOW
    }

    private void closeFrame() {
        if (btnResume.isEnabled()) {
            buttonClick(0);
        } else {
            Main.frame.setGameMode(MainFrame.END_GAME);
            dispose();
        }
    }

    private void calculateSize() {
        Dimension dim = new Dimension();
        dim.width = startX * 2 + sizeX * 4 + spaceX * 3;
        dim.height = btnY * 2 + sizeX + btnY / 2;
        setPreferredSize(dim);
        pack();

        int dx = (getWidth() - getContentPane().getWidth());
        int dy = (getHeight() - getContentPane().getHeight());
        dim.width += dx;
        dim.height += dy;
        setPreferredSize(dim);
        pack();

    }

    private void calculatePos() {
        if (parent != null) {
            setLocationRelativeTo(parent);
        } else {
            Rectangle r = getGraphicsConfiguration().getBounds();
            r.x = r.x + (r.width - getWidth()) / 2;
            r.y = r.y + (r.height - getHeight()) / 2;
            setLocation(r.x, r.y);
        }
    }

    private LPictureButton addButton(int number, String name) {
        LPictureButton lnBtn = new LPictureButton(this, name);
        lnBtn.setLocation(startX + (sizeX + spaceX) * number, btnY);
        lnBtn.addActionListener((ActionEvent e) -> buttonClick(number));
        getContentPane().add(lnBtn);
        return lnBtn;
    }

    private void buttonClick(int number) {
        CreateNewDlg newFrame;
        switch (number) {
            case 0: // resume
                switch (loadedMode) {
                    case MainFrame.PLAY_MODE:
                        Main.frame.startPlayMode();
                        break;
                    case MainFrame.ASSIST_MODE:
                        Main.frame.startAssistMode();
                        break;
                    case MainFrame.FILL_MODE:
                        Main.frame.resumeFillMode();
                        break;
                    default:
                        break;
                }
                dispose();
                break;

            case 1: // random
                newFrame = new CreateNewDlg(parent);
                newFrame.setVisible(true);
                if (newFrame.ok) {
                    dispose();
                    Main.frame.setGameMode(MainFrame.PLAY_MODE);
                    Main.frame.startAutoFillMode(Options.cndFilledTubes, Options.cndEmptyTubes);
                }
                break;
            case 2: // manual
                newFrame = new CreateNewDlg(parent);
                newFrame.setVisible(true);
                if (newFrame.ok) {
                    dispose();
                    Main.frame.startFillMode(Options.cndFilledTubes, Options.cndEmptyTubes);
                }
                break;
            case 3: // load
                LOpenSaveDialog os = new LOpenSaveDialog(Main.frame);
                String fileName = os.showOpenDialog();
                if (!"".equals(fileName)) {
                    if (Main.frame.loadGame(fileName)) {
                        switch (MainFrame.gameMode) {
                            case MainFrame.PLAY_MODE:
                                Main.frame.startPlayMode();
                                break;
                            case MainFrame.ASSIST_MODE:
                                Main.frame.startAssistMode();
                                break;
                            case MainFrame.FILL_MODE:
                                Main.frame.resumeFillMode();
                                break;
                            default:
                                break;
                        }
                        dispose();
                    } else {
                        MessageDlg msgDlg = new MessageDlg(Main.frame,
                                ResStrings.getString("strCannotLoad"),
                                MessageDlg.BTN_OK);
                        msgDlg.setButtonsLayout(MessageDlg.BTN_LAYOUT_RIGHT);
                        msgDlg.setVisible(true);
                    }
                }
                break;
        }
    }

    private void addButtons() {
        btnResume = addButton(0, "btnStart_resume");
        addButton(1, "btnStart_random");
        addButton(2, "btnStart_manual");
        addButton(3, "btnStart_load");
    }

    private void addLabel(int number, String caption) {

        int labelWidth, xPos, yPos;
        FontMetrics fm;
        JLabel out;

        String[] subStr = caption.split("\n");
        int i = subStr.length;
        do {
            out = new JLabel(subStr[i - 1]);
            fm = out.getFontMetrics(out.getFont());
            labelWidth = fm.stringWidth(subStr[i - 1]);
            xPos = startX + (sizeX + spaceX) * number + (sizeX - labelWidth) / 2;
            yPos = btnY + sizeX + 5 + fm.getHeight() * (i - 1);
            out.setBounds(xPos, yPos, labelWidth, fm.getHeight());
            out.setBackground(null);
            out.setForeground(null);
            getContentPane().add(out);
            i--;
        } while (i > 0);
    }

    private void addLabels() {
        addLabel(0, ResStrings.getString("strResume"));
        addLabel(1, ResStrings.getString("strRandomFill"));
        addLabel(2, ResStrings.getString("strManualFill"));
        addLabel(3, ResStrings.getString("strFromFile"));
    }

}
