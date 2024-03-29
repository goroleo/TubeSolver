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
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

import static lib.lOpenSaveDialog.LOpenSaveDialog.osPanel;
import static lib.lOpenSaveDialog.OpenSavePanel.current;

/**
 * The drop-down panel with the list of folders.
 */
public class FoldersPanel extends JComponent implements FolderListener {

    /**
     * The Folder List component.
     */
    private final FoldersList foldersList;

    /**
     * The scroll bar of the Folders List.
     */
    private final ScrollBar scrollbar;

    /**
     * A viewable area of the Folders List.
     */
    private final JComponent viewport = new JComponent() { };

    private final Border border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.gray),
            BorderFactory.createEmptyBorder(1, 1, 1, 1));
    private final Border focusedBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xb8cfe5)),
            BorderFactory.createEmptyBorder(1, 1, 1, 1));

    /**
     * Creates the folders panel and all components inside it.
     */
    public FoldersPanel() {

        setForeground(null);
        setFocusable(true);
        setBorder(border);

        foldersList = new FoldersList(this);
        viewport.add(foldersList);
        add(viewport);

        scrollbar = new ScrollBar() {
            @Override
            public void onChangePosition() {
                foldersList.setLocation(0, -scrollbar.getPosition());
            }
        };
        add(scrollbar);

        addMouseWheelListener((MouseWheelEvent e) -> {
            if (scrollbar.isVisible() && scrollbar.isActive()) {
                if (e.getWheelRotation() > 0) {
                    scrollbar.setPosition(scrollbar.getPosition() + foldersList.getItemHeight());
                } else {
                    scrollbar.setPosition(scrollbar.getPosition() - foldersList.getItemHeight());
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_KP_UP:
                        foldersList.setPreviousItem();
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_KP_DOWN:
                        foldersList.setNextItem();
                        break;
                    case KeyEvent.VK_PAGE_UP:
                        if (e.isControlDown()) {
                            foldersList.setFirstItem();
                        } else {
                            foldersList.setPreviousPageItem(viewport.getHeight());
                        }
                        break;
                    case KeyEvent.VK_PAGE_DOWN:
                        if (e.isControlDown()) {
                            foldersList.setLastItem();
                        } else {
                            foldersList.setNextPageItem(viewport.getHeight());
                        }
                        break;
                    case KeyEvent.VK_ENTER:
                        chooseFolder(foldersList.getCurrentItem());
                        break;
                }
                scrollbar.scrollToComponent(foldersList.getCurrentItem());
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
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        h = foldersList.getItemHeight() * 8 + 4;
        super.setBounds(x, y, w, h);
        updateComponents();
    }

    /**
     * Updates components size and location when the panel resized.
     */
    private void updateComponents() {
        int w = getWidth() - 4;
        int h = getHeight() - 4;

        scrollbar.setValues(0, foldersList.getHeight(), h);

        int sbV_width = (scrollbar.isVisible()) ? 11 : 0;

        scrollbar.setLocation(w - sbV_width + 2, 2);
        viewport.setLocation(2, 2);

        scrollbar.setSize(sbV_width, h);
        viewport.setSize(w - sbV_width, h);
        foldersList.setSize(viewport.getWidth(), h);
        scrollbar.scrollToComponent(foldersList.getCurrentItem());
        repaint();
    }

    /**
     * This routine is called when some folder is selected in the list.
     *
     * @param item folder selected.
     */
    public void chooseFolder(FileItem item) {
        osPanel.showFoldersPanel(false);
        current.setFolder(item.getFile());
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            // restore current folder selection
            foldersList.setCurrentItem(foldersList.getItemByFile(current.getFolder()));
            scrollbar.scrollToComponent(foldersList.getCurrentItem());
        }
        super.setVisible(b);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
    }

    @Override
    public void updateFolder() {
        foldersList.setFolder(current.getFolder());
        updateComponents();
    }
}
