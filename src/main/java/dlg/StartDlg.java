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

import core.TubesIO;
import gui.MainFrame;
import gui.Palette;
import java.awt.Color;
import core.ResStrings;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import lib.lButtons.LPictureButton;
import run.Main;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import lib.lOpenSaveDialog.LOpenSaveDialog;

public class StartDlg extends JDialog {

    private final JFrame parent;

    private LPictureButton btnResume;
    private final JPanel pan = new JPanel();

    int startX = 40, sizeX = 90, spaceX = 20;
    int btnY = 40;

    public StartDlg(JFrame owner) {
        super(owner, ResStrings.getString("strStartGame"), true);
        this.parent = owner;
        setResizable(false);
        getContentPane().setBackground(Palette.dialogColor);
        getContentPane().setForeground(Color.white);
        setLayout(null);

        pan.setBackground(null);
        pan.setForeground(null);
        pan.setLayout(null);
        add(pan);

        calculateSize();
        calculatePos();
        addButtons();
        addLabels();

        if (TubesIO.fileExists(TubesIO.tempFileName)) {
            Main.frame.loadGame(TubesIO.tempFileName);
            btnResume.setEnabled(true);
        } else {
            btnResume.setEnabled(false);
        }

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (btnResume.isEnabled()) {
                    buttonClick(0);
                } else {
                    dispose();
                    Main.frame.closeFrame();
                }
            }
        });

    }

    private void calculateSize() {
        Dimension dim = new Dimension();
        dim.width = startX * 2 + sizeX * 4 + spaceX * 3;
        dim.height = btnY * 2 + sizeX + btnY / 2;
        pan.setSize(dim);
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
        pan.add(lnBtn);
        return lnBtn;
    }

    private void buttonClick(int number) {
        CreateNewDlg newFrame;
        switch (number) {
            case 0: // resume
                switch (MainFrame.gameMode) {
                    case MainFrame.PLAY_MODE:
                        Main.frame.resumePlayMode();
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
                    Main.frame.startAutoFillMode(newFrame.tubesFilled, newFrame.tubesEmpty);
                }
                break;
            case 2: // manual
                newFrame = new CreateNewDlg(parent);
                newFrame.setVisible(true);
                if (newFrame.ok) {
                    dispose();
                    Main.frame.startFillMode(newFrame.tubesFilled, newFrame.tubesEmpty);
                }
                break;
            case 3: // load
                LOpenSaveDialog os = new LOpenSaveDialog(Main.frame);
                String fileName = os.showOpenDialog();
                if (!"".equals(fileName)) {
                    if (Main.frame.loadGame(fileName)) {
                        switch (MainFrame.gameMode) {
                            case MainFrame.PLAY_MODE:
                                Main.frame.resumePlayMode();
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
                                "Cannot load the game. Please, try again",
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
            pan.add(out);
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
