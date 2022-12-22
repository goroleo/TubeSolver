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
import java.awt.event.MouseWheelEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;
import static lib.lOpenSaveDialog.LOpenSaveDialog.osPan;

public class FoldersPanel extends JComponent {

    private final FolderList folderlist;
    private final ScrollBar sbVert;

    private final JComponent viewport = new JComponent() {
    };

    private final Border border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.gray),
            BorderFactory.createEmptyBorder(1, 1, 1, 1));
    private final Border focusedBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xb8cfe5)),
            BorderFactory.createEmptyBorder(1, 1, 1, 1));

    public FoldersPanel(File folder) {

        setForeground(null);
        setFocusable(true);
        setBorder(border);

        folderlist = new FolderList(this, folder);
        viewport.add(folderlist);
        add(viewport);

        sbVert = new ScrollBar(ScrollBar.VERTICAL) {
            @Override
            public void onChangePosition() {
                folderlist.setLocation(0, -sbVert.getPosition());
            }
        };
        sbVert.setSize(11, 200);
        add(sbVert);

        setSize(200, 200);

        addMouseWheelListener((MouseWheelEvent e) -> {
            if (sbVert.isVisible()) {
                if (e.getWheelRotation() > 0) {
                    sbVert.setPosition(sbVert.getPosition() + folderlist.getItemHeight(), true);
                } else {
                    sbVert.setPosition(sbVert.getPosition() - folderlist.getItemHeight(), true);
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_KP_UP:
                        folderlist.setPreviousItem();
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_KP_DOWN:
                        folderlist.setNextItem();
                        break;
                    case KeyEvent.VK_PAGE_UP:
                        if (e.isControlDown()) {
                            folderlist.setFirstItem();
                        } else {
                            folderlist.setPreviousPageItem(viewport.getHeight());
                        }
                        break;
                    case KeyEvent.VK_PAGE_DOWN:
                        if (e.isControlDown()) {
                            folderlist.setLastItem();
                        } else {
                            folderlist.setNextPageItem(viewport.getHeight());
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        chooseFolder(folderlist.getCurrentItem());
                        break;
                }
                sbVert.scrollToComponent(folderlist.getCurrentItem());
            }
        });

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorder(focusedBorder);
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBorder(border);
            }
        }); // addFocusListener
        repaint();
    }

    public void setFolder(File folder) {
        folderlist.setFolder(folder);
        sbVert.setValues(0, folderlist.getHeight(), folderlist.getItemHeight(), viewport.getHeight());
        sbVert.onChangePosition();
    }

    @Override
    public void setSize(int w, int h) {

        super.setSize(w, h);

        w = w - 4;
        h = h - 4;

        sbVert.setValues(0, folderlist.getHeight(), folderlist.getItemHeight(), h);

        int sbV_width = (sbVert.isVisible()) ? 11 : 0;

        sbVert.setLocation(w - sbV_width + 2, 2);
        viewport.setLocation(2, 2);

        sbVert.setSize(sbV_width, h);
        viewport.setSize(w - sbV_width, h);
        folderlist.setSize(viewport.getWidth(), h);

        repaint();
    }

    public int getItemHeight() {
        return folderlist.getItemHeight();
    }

    public void chooseFolder(FileItem item) {
        if (osPan != null) {
            osPan.setCurrentFolder(item.getFile());
            OpenSavePanel.folderName.doClick();
        }
    }

    public FileItem getCurrentItem() {
        return folderlist.getCurrentItem();
    }

    public void setCurrentItem(FileItem folder) {
        folderlist.setCurrentItem(folder);
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            folderlist.setCurrentItem(folderlist.findFolder(osPan.getCurrentFolder()));
            if (sbVert.isVisible()) {
                sbVert.setPosition(folderlist.getCurrentItem().getY());
            }
        }
        super.setVisible(b);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
    }

}
