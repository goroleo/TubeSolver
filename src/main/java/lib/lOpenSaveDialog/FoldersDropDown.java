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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JComponent;
import static lib.lOpenSaveDialog.LOpenSaveDialog.osPan;

public class FoldersDropDown extends JComponent implements FolderListener {

    private final FileItem folderInfo;
    private final BufferedImage imgFrame;

    private boolean rollover = false;
    private boolean selected = false;
    private boolean focused = false;

    private final Color selectedBackground = new Color(0xb8cfe5);
    private final Color selectedForeground = Color.BLACK;

    public FoldersDropDown() {

        this.setFocusable(true);

        imgFrame = new BufferedImage(OpenSavePanel.imgBtnDown.getWidth(), OpenSavePanel.imgBtnDown.getHeight(), 2);

        folderInfo = new FileItem(null, false, 0);
        for (MouseListener ml : folderInfo.getMouseListeners()) {
            folderInfo.removeMouseListener(ml);
        }
        add(folderInfo);

//        setSize(400, 27);

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
                rollover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                rollover = false;
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
    public void setSize(int w, int h) {
        folderInfo.setLocation(2, 2);
        folderInfo.setSize(w - imgFrame.getWidth(), h - 4);
        super.setSize(w, h);
    }

    @Override
    public void paintComponent(Graphics g) {
        updateBtn();

        if (selected) {
            g.setColor(selectedBackground);
        } else {
            g.setColor(getBackground());
        }
        g.fillRect(2, 2, getWidth() - 4, getHeight() - 4);

        if (rollover) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(2, 2, getWidth() - 5, getHeight() - 5);
        }

        if (focused) {
            g.setColor(selectedBackground);
        } else {
            g.setColor(Color.GRAY);
        }
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        g.drawImage(imgFrame,
                getWidth() - imgFrame.getWidth(),
                (getHeight() - imgFrame.getHeight()) / 2,
                null);
    }

    public void updateBtn() {
        int alpha, clr;
        BufferedImage imgSource;

        if (selected) {
            clr = selectedForeground.getRGB();
        } else {
            clr = Color.LIGHT_GRAY.getRGB();
        }

        if (osPan.isFoldersPanelVisible()) {
            imgSource = OpenSavePanel.imgBtnUp;
        } else {
            imgSource = OpenSavePanel.imgBtnDown;
        }

        for (int i = 0; i < imgSource.getWidth(); i++) {
            for (int j = 0; j < imgSource.getHeight(); j++) {
                alpha = (imgSource.getRGB(i, j) >> 24) & 0xff;
                imgFrame.setRGB(i, j, (clr & 0xffffff) | (alpha << 24));
            }
        }
    }

    public void doClick() {
        if (osPan != null) {
            osPan.showFoldersPanel(this, !osPan.isFoldersPanelVisible());
        }
        repaint();
    }

    @Override
    public void updateFolder(File folder) {
        folderInfo.setFile(folder);
    }
}
