/*
 * Copyright (c) 2022 legoru / goroleo <legoru@me.com>
 * 
 * This software is distributed under the <b>MIT License.</b>
 * The full text of the License you can read here: 
 * https://choosealicense.com/licenses/mit/
 * 
 * Use this as you want! ))
 */
package lib.lOpenSaveDialog;

import gui.Palette;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

import core.ResStrings;
import lib.lButtons.LToolButton;
import static lib.lOpenSaveDialog.LOpenSaveDialog.osPan;

public class ToolButtonsPanel extends JComponent {

    private final LToolButton btnUp;
    private final LToolButton btnRefresh;
    private final LToolButton btnFolder;
    private final int sep = 7;
    private int curBtnFocus = 0;

    public ToolButtonsPanel() {
        btnUp = addButton(0, "up", ResStrings.getString("strFolderUp"));
        btnUp.addActionListener((ActionEvent e) -> buttonClick(1));
        addKeyListener(btnUp);

        btnRefresh = addButton(1, "refresh", ResStrings.getString("strRefresh"));
        btnRefresh.addActionListener((ActionEvent e) -> buttonClick(2));
        addKeyListener(btnRefresh);

        btnFolder = addButton(2, "newfolder", ResStrings.getString("strNewFolder"));
        btnFolder.addActionListener((ActionEvent e) -> buttonClick(3));
        addKeyListener(btnFolder);

        setSize(btnUp.getWidth() * 3 + sep * 4, btnUp.getHeight());
        addKeyListener(this);

    }

    public LToolButton addButton(int number, String imgFName, String hintText) {
        LToolButton btn = new LToolButton(this, "btnTool22", imgFName);
        btn.setColorEnabled(new Color(184, 207, 229));
        btn.setColorHover(Color.WHITE);
        btn.setColorPressed(new Color(0xaaaaaa));
        btn.setFocusable(true);
        btn.setLocation(number * (sep + btn.getWidth()) + sep, 0);

        btn.setToolTipBackground(Palette.dialogColor);
        btn.setToolTipForeground(Color.WHITE);
        btn.setToolTipBorder(new Color(0xb8cfe5));

        btn.setToolTipText(hintText);

        add(btn);
        return btn;
    }

    @Override
    public void setFocusable(boolean b) {
        if (b) {
            drawFocus();
        } else {
            btnUp.setFocusable(false);
            btnRefresh.setFocusable(false);
            btnFolder.setFocusable(false);
        }
    }

    public void buttonClick(int btnNumber) {
        if (osPan.isFoldersPanelVisible()) {
            osPan.showFoldersPanel(false);
        } else {
            switch (btnNumber) {
                case 1:
                    osPan.upFolder();
                    break;
                case 2:
                    osPan.refreshFolder();
                    break;
                case 3:
                    osPan.createNewFolder();
            }
        }
    }

    private void addKeyListener(Component c) {
        c.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_KP_UP:
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_KP_LEFT:
                        focusPrev();
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_KP_DOWN:
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_KP_RIGHT:
                        focusNext();
                        break;
                    case KeyEvent.VK_ENTER:
                        if (c instanceof JButton)
                            ((JButton) c).doClick();
                        break;
                }
            }
        });

    }

    private void focusNext() {
        if (curBtnFocus == 2) {
            curBtnFocus = 0;
        } else curBtnFocus ++;
        drawFocus();
    }

    private void focusPrev() {
        if (curBtnFocus == 0) {
            curBtnFocus = 2;
        } else curBtnFocus --;
        drawFocus();
    }

    private void drawFocus() {
        switch (curBtnFocus) {
            case 0:
                btnUp.setFocusable(true);
                btnUp.requestFocus();
                btnRefresh.setFocusable(false);
                btnFolder.setFocusable(false);
                break;
            case 1:
                btnRefresh.setFocusable(true);
                btnRefresh.requestFocus();
                btnFolder.setFocusable(false);
                btnUp.setFocusable(false);
                break;
            case 2:
                btnFolder.setFocusable(true);
                btnFolder.requestFocus();
                btnUp.setFocusable(false);
                btnRefresh.setFocusable(false);
                break;
        }
    }

}
