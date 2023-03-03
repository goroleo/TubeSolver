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

import core.ResStrings;
import gui.Palette;
import lib.lButtons.LToolButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static lib.lOpenSaveDialog.LOpenSaveDialog.osPan;
import static lib.lOpenSaveDialog.OpenSavePanel.current;

/**
 * A small panel with Tool Buttons: FolderUP, FolderRefresh, CreateNewFolder.
 */
public class ToolsPanel extends JComponent {

    /**
     * An array of tool buttons. Change it's size if necessary.
     */
    private final LToolButton[] buttons = new LToolButton[3];

    /**
     * The count of tool buttons.
     */
    private int buttonsCount;

    /**
     * A separator space between buttons (in pixels).
     */
    private final int sep = 7;

    /**
     * A number of currently focused button.
     */
    private int curBtnFocus = 0;

    /**
     * A tool panel constructor. It creates tool buttons, adds them to the panel and
     * sets the panel size.
     */
    @SuppressWarnings("SpellCheckingInspection")
    public ToolsPanel() {
        // add button Up Folder
        addButton("up", ResStrings.getString("strFolderUp"));
        // add button Refresh Folder
        addButton("refresh", ResStrings.getString("strRefresh"));
        // add button Create New Folder
        addButton("newfolder", ResStrings.getString("strNewFolder"));

        setFocusable(true);
        // update panel's size
        setSize(buttonsCount * (sep + buttons[0].getWidth()) + sep, buttons[0].getHeight());
    }

    /**
     * This routine creates and adds the next ToolButton.
     * @param imgFName file suffix to get the image from resources
     * @param hintText hint text to show when the mouse is over this button
     */
    public void addButton(String imgFName, String hintText) {
        int buttonNumber = buttonsCount;

        // create button & load icon image
        LToolButton btn = new LToolButton(this, "btnTool22", imgFName);
        btn.setLocation(buttonNumber * (sep + btn.getWidth()) + sep, 0);

        // setColors
        btn.setColorEnabled(new Color(184, 207, 229));
        btn.setColorHover(Color.WHITE);
        btn.setColorPressed(new Color(0xaaaaaa));

        // setToolTip
        btn.setToolTipBackground(Palette.dialogColor);
        btn.setToolTipForeground(Color.WHITE);
        btn.setToolTipBorder(new Color(0xb8cfe5));
        btn.setToolTipText(hintText);

        // add listeners
        btn.addActionListener((ActionEvent e) -> buttonClick(buttonNumber));
        addKeyListener(btn);

        // add button to panel & to array
        add(btn);
        buttons[buttonNumber] = btn;
        buttonsCount++;
    }

    /**
     * This routine catches the click of tool button and does the necessary.
     *
     * @param btnNumber number of clicked button
     */
    public void buttonClick(int btnNumber) {
        if (osPan.isFoldersPanelVisible()) {
            curBtnFocus = btnNumber;
            osPan.showFoldersPanel(false);
            drawFocus();
        } else {
            switch (btnNumber) {
                case 0:
                    current.upFolder();
                    break;
                case 1:
                    osPan.refreshFolder();
                    break;
                case 2:
                    osPan.createNewFolder();
            }
        }
    }

    /**
     * This routine adds a keyboard listener to the button
     *
     * @param btn specified button
     */
    private void addKeyListener(LToolButton btn) {
        btn.addKeyListener(new KeyAdapter() {
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
                        btn.doClick();
                        break;
                }
            }
        });
    }

    @Override
    public void setFocusable(boolean b) {
        if (b) {
            drawFocus();
        } else {
            for (int i = 0; i < buttonsCount; i++) {
                buttons[i].setFocusable(false);
            }
        }
        // the panel is not focusable anyway
        super.setFocusable(false);
    }

    /** Changes the focus to the next button.  */
    private void focusNext() {
        curBtnFocus++;
        if (curBtnFocus == buttonsCount)
            curBtnFocus = 0;
        drawFocus();
    }

    /** Changes the focus to the previous button.  */
    private void focusPrev() {
        curBtnFocus--;
        if (curBtnFocus < 0)
            curBtnFocus = buttonsCount - 1;
        drawFocus();
    }

    /** Sets the focus to the current button  */
    private void drawFocus() {
        LToolButton focusedButton = buttons[curBtnFocus];
        if (focusedButton != null) {
            focusedButton.setFocusable(true);
            focusedButton.requestFocus();
        }
        for (int i = 0; i < buttonsCount; i++) {
            if (buttons[i] != focusedButton)
                buttons[i].setFocusable(false);
        }
    }

}
