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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static lib.lOpenSaveDialog.LOpenSaveDialog.osPan;

/**
 * A Folder Name Edit component. Displays the current folder in a combo box.
 */
public class FolderComboBox extends JComponent implements FolderListener {

    /**
     * A FileItem displayed the current folder.
     */
    private final FileItem folderItem;

    /**
     * An image that displays arrow Up and arrow Down in depends on FoldersPanel visibility.
     */
    private final BufferedImage imgUpDown;

    /**
     * True if the mouse cursor is over this component.
     */
    private boolean mouseOver = false;

    /**
     * True if this component is focused.
     */
    private boolean focused = false;

    /**
     * A color for selected background item.
     */
    private final Color selectedBackground = new Color(0xb8cfe5);

    /**
     * Creates a Folder Drop Down component.
     */
    public FolderComboBox() {

        this.setFocusable(true);

        imgUpDown = new BufferedImage(OpenSavePanel.imgBtnDown.getWidth(), OpenSavePanel.imgBtnDown.getHeight(), 2);

        folderItem = new FileItem(null, false, 0);
        for (MouseListener ml : folderItem.getMouseListeners()) {
            folderItem.removeMouseListener(ml);
        }
        add(folderItem);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_KP_UP:
                    case KeyEvent.VK_PAGE_UP:
                        if (osPan.isFoldersPanelVisible()) {
                            doClick();
                        }
                        break;
                    case KeyEvent.VK_PAGE_DOWN:
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_KP_DOWN:
                        if (!osPan.isFoldersPanelVisible()) {
                            doClick();
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_SPACE:
                        doClick();

                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                doClick();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                requestFocus();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                mouseOver = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseOver = false;
                repaint();
            }
        });

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                focused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                focused = false;
                repaint();
            }
        }); // addFocusListener

    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        folderItem.setLocation(2, 2);
        folderItem.setSize(w - imgUpDown.getWidth(), h - 4);
        super.setBounds(x, y, w, h);
    }

    @Override
    public void paintComponent(Graphics g) {
        updateBtn();

        g.setColor(getBackground());
        g.fillRect(2, 2, getWidth() - 4, getHeight() - 4);

        if (mouseOver) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(2, 2, getWidth() - 5, getHeight() - 5);
        }

        if (focused) {
            g.setColor(selectedBackground);
        } else {
            g.setColor(Color.GRAY);
        }
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        g.drawImage(imgUpDown,
                getWidth() - imgUpDown.getWidth(),
                (getHeight() - imgUpDown.getHeight()) / 2,
                null);
    }

    /**
     * Updates UpDown image in depends on FoldersPanel visibility.
     */
    public void updateBtn() {

        BufferedImage imgSource;
        if (osPan.isFoldersPanelVisible()) {
            imgSource = OpenSavePanel.imgBtnUp;
        } else {
            imgSource = OpenSavePanel.imgBtnDown;
        }

        int clr = Color.LIGHT_GRAY.getRGB() & 0xffffff;
        int alpha;

        for (int i = 0; i < imgSource.getWidth(); i++) {
            for (int j = 0; j < imgSource.getHeight(); j++) {
                alpha = (imgSource.getRGB(i, j) >> 24) & 0xff;
                imgUpDown.setRGB(i, j, (clr | (alpha << 24)));
            }
        }
    }

    /**
     * Handles the mouse click event.
     */
    public void doClick() {
        if (osPan != null) {
            osPan.showFoldersPanel(this, !osPan.isFoldersPanelVisible());
        }
        repaint();
    }

    @Override
    public void updateFolder(File folder) {
        folderItem.setFile(folder);
    }
}
